package com.wxy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxy.reggie.common.R;
import com.wxy.reggie.dto.DishDto;
import com.wxy.reggie.dto.SetmealDto;
import com.wxy.reggie.entity.Category;
import com.wxy.reggie.entity.Dish;
import com.wxy.reggie.entity.Setmeal;
import com.wxy.reggie.entity.SetmealDish;
import com.wxy.reggie.service.CategoryService;
import com.wxy.reggie.service.SetmealDishService;
import com.wxy.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.soap.Addressing;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {

        System.out.println(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("Saved the setmealDto successfully");
    }

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件,根据name进行模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件 根据name进行like模糊查询
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto); //对象拷贝
            Long categoryId = item.getCategoryId();//分类id
            //根据分类id进行查询
            Category category = categoryService.getById(categoryId);
            if (category != null) {

                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        System.out.println(ids.toString());
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功！");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);


        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);

    }

}
