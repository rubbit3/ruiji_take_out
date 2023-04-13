package com.wxy.reggie.controller;

import com.wxy.reggie.common.R;
import com.wxy.reggie.entity.Orders;
import com.wxy.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {


    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        System.out.println(orders);
        ordersService.submit(orders);
        return R.success("Order added successfully");
    }

}
