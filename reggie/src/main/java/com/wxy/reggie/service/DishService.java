package com.wxy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxy.reggie.dto.DishDto;
import com.wxy.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增餐品，同时插入菜品对应的口味数据，需要操作两张表，
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
