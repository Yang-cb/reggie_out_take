package com.xxx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.reggie.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yang_
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2023-05-02 23:13:01
* @Entity com/xxx/reggie.pojo.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}




