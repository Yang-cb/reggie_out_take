package com.xxx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.reggie.pojo.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yang_
* @description 针对表【order_detail(订单明细表)】的数据库操作Mapper
* @createDate 2023-05-02 23:26:35
* @Entity com/xxx/reggie.pojo.OrderDetail
*/
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}




