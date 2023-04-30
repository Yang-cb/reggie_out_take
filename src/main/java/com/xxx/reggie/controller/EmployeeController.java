package com.xxx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.reggie.common.R;
import com.xxx.reggie.pojo.Employee;
import com.xxx.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

// 日志
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param employee 前端传递的json数据
     * @param request  获取session，保存登录者id
     * @return R数据
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        //1，将传递的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2,查询用户名是否存在
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee employee1 = employeeService.getOne(queryWrapper);
        //3,如果不存在，error：用户名不存在
        if (employee1 == null) {
            return R.error("用户名不存在");
        }
        //4,如果存在，继续判断密码是否正确
        if (!employee1.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        //5,判断用户状态是否已被禁用
        if (employee1.getStatus() == 0) {
            return R.error("用户已被封禁");
        }
        //6,登陆成功，将用户id存入session，并返回登陆成功
        HttpSession session = request.getSession();
        session.setAttribute("employee", employee1.getId());
        return R.success(employee1);
    }

    /**
     * 员工退出
     *
     * @param request 用于清除session中保存的数据
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //1,清除session中保存的数据
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    /**
     * 添加员工
     *
     * @param employee 前端传递的json数据封装成对象
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody Employee employee) {
//        log.info("添加的对象：{}", employee);
        //1，设置创建时间，更新时间，创建者，更新者 ：在MetaObjectHandle通过线程获取

        //3，为账户创建初始密码 123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //4，执行方法
        employeeService.save(employee);
        return R.success("添加成功");
    }

    /**
     * 员工信息分页查询 ：打开首页默认会执行一次
     *
     * @param page     第几页
     * @param pageSize 每页几条数据
     * @param name     具体查哪个人
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page, Integer pageSize, String name) {
//        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);
        //1，添加分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        //2，条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件：如果name值不为空，就按name查询
        queryWrapper.like(StringUtils.hasLength(name), Employee::getName, name);
        //排序条件：按照最后修改时间排序
        queryWrapper.orderBy(true, false, Employee::getUpdateTime);

        //3，设置分页构造器
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 更新数据
     *
     * @param employee 需要更新的数据
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee) {
//        log.info("传递对象：{}", employee);
        //设置更新时间，更新人 在MetaObjectHandle通过线程获取

        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    /**
     * 根据id查询用户（数据回显）
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
//        log.info("id:" + id);
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
