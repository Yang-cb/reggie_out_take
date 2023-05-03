package com.xxx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.reggie.mapper.OrderDetailMapper;
import com.xxx.reggie.pojo.OrderDetail;
import com.xxx.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
* @author yang_
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2023-05-02 23:26:35
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService {

}




