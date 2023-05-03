package com.xxx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.reggie.pojo.ShoppingCart;

/**
 * @author yang_
 * @description 针对表【shopping_cart(购物车)】的数据库操作Service
 * @createDate 2023-05-01 20:09:11
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 添加菜品（套餐）到购物车
     *
     * @param shoppingCart json封装了金额、口味（String）、菜品（套餐）id、菜品（套餐）名称
     * @return
     */
    ShoppingCart add(ShoppingCart shoppingCart);

    /**
     * 从购物车删除菜品（套餐）数量
     * @param shoppingCart json封装了 菜品（套餐）id
     * @return
     */
    ShoppingCart sub(ShoppingCart shoppingCart);
}
