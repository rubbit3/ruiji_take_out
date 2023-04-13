package com.wxy.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxy.reggie.common.R;
import com.wxy.reggie.entity.Employee;
import com.wxy.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;




@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1将密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());//加密
        //2查询数据库，根据用户名
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);//用户名唯一索引查询
        //3，看看是否查到数据
        if (emp == null) {
            return R.error("登录失败");
        }
        //4进行密码比对
        if (!password.equals(emp.getPassword())) {
            return R.error("登录失败");
        }
        //5，密码比对成功，看看员工状态
        if (emp.getStatus() == 0) {
            return R.error("账户已经被禁用");
        }
        //6，登录成功，将员工id放入session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);

    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //1，退出，清除session存储的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {//接受JSON格式，转为实体对象
        log.info("新增员工:{}", employee.toString());
        //设置初试密码为123456，进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //创建时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功!");
    }
    //配合页面数据，总数，展示记录数，总条数等等

    /**
     * 员工信息进行分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("分页查询进来了啊page={},pageSize={},name={}", page, pageSize, name);
        //基于mp查询

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器,查询名字
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        if (name != null) {
            queryWrapper.like(Employee::getName, name);
        }
        //添加排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询操作
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("update successful");
        //employee.setUpdateTime(LocalDateTime.now());
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查到对应的员工信息");
    }

}
