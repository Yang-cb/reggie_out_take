package com.xxx.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.reggie.common.R;
import com.xxx.reggie.pojo.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;

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
    /**
     * [根据条件]分页查询订单数据
     *
     * @param page      第几页
     * @param pageSize  每页多少条
     * @param number    订单号
     * @param beginTime 时间范围：开始时间
     * @param endTime   时间范围：结束时间
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(Integer page, Integer pageSize, String number,
                                Date beginTime, Date endTime) {
        //请求 URL: /order/page?page=1&pageSize=10&number=111&beginTime=2023-04-18%2000%3A00%3A00&endTime=2023-05-12%2023%3A59%3A59
        //请求方法: GET
        log.info("");
        return null;
    }
}
