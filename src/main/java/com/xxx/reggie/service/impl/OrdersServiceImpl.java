package com.xxx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.reggie.mapper.OrdersMapper;
import com.xxx.reggie.pojo.Orders;
import com.xxx.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

/**
 * reggie_take_out.com.xxx.reggie.service.impl
 *
 * @author yang_
 * @description 订单Service实现
 * @date 2023/4/30 11:14
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}
