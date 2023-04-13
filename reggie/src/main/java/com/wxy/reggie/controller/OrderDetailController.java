package com.wxy.reggie.controller;

import com.wxy.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/ordersDetails")
public class OrderDetailController {
    @Autowired
    private OrdersService ordersService;

}
