package com.wxy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.reggie.common.CustomException;
import com.wxy.reggie.common.R;
import com.wxy.reggie.config.BaseContext;
import com.wxy.reggie.entity.*;
import com.wxy.reggie.mapper.OrdersMapper;
import com.wxy.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {


    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户完成下单
     *
     * @param orders
     * @return
     */
    @Transactional
    public R<String> submit(Orders orders) {
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();
        //查询用户购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId, userId);

        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("No shopping cart found");
        }
        //根据用户id 查询用户对象
        User user = userService.getById(userId);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("user address book not found");
        }
        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);     //总金额

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());//一份的金额
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());//金额✖份数
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        //向订单表插入数据，一条数据，
        this.save(orders);
        //向订单明细表插入数据，有可能多条数据
        orderDetailService.saveBatch(orderDetails);
        //完成下单，清空购物车
        shoppingCartService.remove(queryWrapper);
        return null;
    }
}
