package com.wxy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.reggie.common.CustomException;
import com.wxy.reggie.dto.SetmealDto;
import com.wxy.reggie.entity.Setmeal;
import com.wxy.reggie.entity.SetmealDish;
import com.wxy.reggie.mapper.SetmealMapper;
import com.wxy.reggie.service.SetmealDishService;
import com.wxy.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 更新2个表
     *
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息

        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐跟菜品的关联信息表
        setmealDishService.saveBatch(setmealDishes);


    }

    /**
     * 删除套餐以及关联菜品
     *
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐的状态是否可以删除

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        long count = this.count(queryWrapper);
        if (count > 0) {
            //如果不能删除，抛出业务异常
            throw new CustomException("Query returned more than one result for 套餐正在售卖中 ");
        }

        //如果可以删除，先删除套餐中的数据  setMeal
        this.removeBatchByIds(ids);
        //删除关系表中的数据,批量删除
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(lambdaQueryWrapper);

    }
}
