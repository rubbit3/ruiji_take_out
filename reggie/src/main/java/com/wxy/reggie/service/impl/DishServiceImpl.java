package com.wxy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;
import com.wxy.reggie.dto.DishDto;
import com.wxy.reggie.entity.Dish;
import com.wxy.reggie.entity.DishFlavor;
import com.wxy.reggie.mapper.DishMapper;
import com.wxy.reggie.service.DishFlavorService;
import com.wxy.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存口味数据
     *
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {


        //保存基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味到dish——flavor数据表中
        dishFlavorService.saveBatch(dishDto.getFlavors());


    }

    /**
     * 根据id查询菜品信息跟口味数据
     *
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //查询当前菜品对应的口味信息，从dish_flavor中查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish基本表
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        //清理当前菜品口味数据  -- delete the  dish_flavor
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加新的 菜品口味数据  -- dish_flavor

        List<DishFlavor> flavors = dishDto.getFlavors();
        //dishId adds the flavor
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }
}
