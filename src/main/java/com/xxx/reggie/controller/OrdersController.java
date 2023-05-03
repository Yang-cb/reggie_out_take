package com.xxx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.reggie.common.CustomException;
import com.xxx.reggie.common.R;
import com.xxx.reggie.common.ThreadContextId;
import com.xxx.reggie.pojo.Orders;
import com.xxx.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * reggie_take_out.com.xxx.reggie.controller
 *
 * @author yang_
 * @description 订单
 * @date 2023/4/30 11:15
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 支付 功能
     *
     * @param orders json封装了备注、支付方式、地址id
     * @return msg
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("去结算");
    }


    /**
     * 查询用户订单信息（移动端）
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(Integer page, Integer pageSize) {
        Long userId = ThreadContextId.getContextId();
        if (userId == null) {
            return R.error("用户登录状态异常");
        }
        //1，创建分页器
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        //2，构建条件
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        qw.eq(Orders::getUserId, userId);
        //3，查表
        ordersService.page(ordersPage, qw);

        return R.success(ordersPage);
    }


    /**
     * [根据条件]分页查询订单数据（服务端）
     *
     * @param page      第几页
     * @param pageSize  每页多少条
     * @param number    订单号
     * @param beginTime 下单时间范围：开始时间
     * @param endTime   下单时间范围：结束时间
     * @return page
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(Integer page, Integer pageSize, String number,
                                @DateTimeFormat String beginTime, @DateTimeFormat String endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        //订单号
        qw.like(number != null, Orders::getNumber, number);
        //大于开始时间
        qw.gt(beginTime != null, Orders::getOrderTime, beginTime);
        //小于结束时间
        qw.lt(endTime != null, Orders::getOrderTime, endTime);

        ordersService.page(ordersPage, qw);
        return R.success(ordersPage);
    }


    /**
     * 更改订单状态（服务端）
     *
     * @param orders json 封装了订单id、修改后的状态
     * @return msg
     */
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders) {
        //1，查看当前订单状态是否与要修改的状态一样
        Integer newStatus = orders.getStatus();
        Orders byIdOrders = ordersService.getById(orders.getId());
        Integer oldStatus = byIdOrders.getStatus();
        if (Objects.equals(oldStatus, newStatus)) {
            throw new CustomException("订单已经是该状态");
        }
        //2，修改状态
        ordersService.updateById(orders);
        return R.success("修改成功");
    }
}
