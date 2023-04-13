package com.wxy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxy.reggie.common.R;
import com.wxy.reggie.config.BaseContext;
import com.wxy.reggie.entity.Category;
import com.wxy.reggie.entity.Employee;
import com.wxy.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {

        categoryService.save(category);
        log.info("Category saved");
        return R.success("分类成功");

    }


    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("分页查询进来了啊page={},pageSize={}", page, pageSize);

        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        //构造条件构造器,排序条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询操作
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteCategory(Long ids) {
        log.info("deleteCategory id: {}", ids);
        //categoryService.removeById(ids);
        categoryService.remove(ids);
        return R.success("delete category successfully");
    }

    /**
     * 根据id修改分类信息
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("update category successfully");
    }


    /**
     * 根据条件查询分类
     *
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //condition 构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //sort conditions
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }


}
