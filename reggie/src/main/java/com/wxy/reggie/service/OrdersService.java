package com.wxy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxy.reggie.common.R;
import com.wxy.reggie.entity.Orders;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrdersService extends IService<Orders> {
    public R<String> submit(@RequestBody Orders orders);
}
