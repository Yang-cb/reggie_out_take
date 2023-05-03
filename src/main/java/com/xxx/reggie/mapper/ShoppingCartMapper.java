package com.xxx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxx.reggie.pojo.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yang_
 * @description 针对表【shopping_cart(购物车)】的数据库操作Mapper
 * @createDate 2023-05-01 20:09:11
 * @Entity com.xxx.reggie.pojo.ShoppingCart
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}




