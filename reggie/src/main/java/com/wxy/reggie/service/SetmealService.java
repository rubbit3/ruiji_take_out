package com.wxy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wxy.reggie.dto.SetmealDto;
import com.wxy.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);
}
