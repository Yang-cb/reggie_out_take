package com.xxx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.reggie.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * reggie_take_out.com.xxx.reggie.mapper
 *
 * @author yang_
 * @description 订单Mapper
 * @date 2023/4/30 11:13
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
