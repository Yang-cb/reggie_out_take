package com.xxx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.reggie.pojo.Orders;

/**
* @author yang_
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2023-05-02 23:13:01
*/
public interface OrdersService extends IService<Orders> {

    /**
     * 支付 功能
     *
     * @param orders json封装了备注、支付方式、地址id
     */
    void submit(Orders orders);
}
