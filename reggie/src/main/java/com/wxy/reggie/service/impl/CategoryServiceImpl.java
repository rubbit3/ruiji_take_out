package com.wxy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wxy.reggie.common.CustomException;
import com.wxy.reggie.entity.Category;

import com.wxy.reggie.entity.Dish;
import com.wxy.reggie.entity.Setmeal;
import com.wxy.reggie.mapper.CategoryMapper;

import com.wxy.reggie.service.CategoryService;
import com.wxy.reggie.service.DishService;
import com.wxy.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        // TODO Auto-generated

        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        long count1 = dishService.count(dishQueryWrapper);
        //查询当前分类是否关联菜品，如果已经关联，抛出一个删除异常
        if (count1 > 0) {
            //已经关联菜品，抛出业务异常
            throw new CustomException("当前分类关联菜品，无法删除");
        }

        //查询当前分类是否关联套餐，如果已经关联，抛出一个删除异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        long count2 = setmealService.count(setmealLambdaQueryWrapper);

        if (count2 > 0) {
            //已经关联套餐，抛出业务异常
            throw new CustomException("当前分类关联套餐，无法删除");

        }
        //正常删除分类
        super.removeById(id);
    }
}
