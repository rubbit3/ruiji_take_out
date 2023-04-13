package com.wxy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wxy.reggie.common.R;
import com.wxy.reggie.config.BaseContext;
import com.wxy.reggie.entity.ShoppingCart;
import com.wxy.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("Adding shopping cart" + shoppingCart.toString());

        //设置用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();

        //添加查询条件，根据当前用户id查询对应的购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if (dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //查询当前菜品或者套餐是否在购物车中
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (shoppingCartServiceOne != null) {
            //如果已经存在购物车中，就在原来的基础上加1
            Integer number = shoppingCartServiceOne.getNumber();
            shoppingCartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        } else {
            //如果不存在，则添加到购物车，数量默认就是1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne = shoppingCart;

        }
        return R.success(shoppingCartServiceOne);

    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {


        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //查看当前用户id对应下的购物车
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("购物车清空成功!");
    }

}
