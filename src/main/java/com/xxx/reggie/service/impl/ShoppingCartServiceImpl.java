package com.xxx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.reggie.common.R;
import com.xxx.reggie.common.ThreadContextId;
import com.xxx.reggie.mapper.ShoppingCartMapper;
import com.xxx.reggie.pojo.ShoppingCart;
import com.xxx.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author yang_
 * @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
 * @createDate 2023-05-01 20:09:11
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {

    /**
     * 添加菜品（套餐）到购物车
     *
     * @param shoppingCart json封装了金额、口味（String）、菜品（套餐）id、菜品（套餐）名称
     * @return
     */
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {

        //1，获取点餐人id
        Long userId = ThreadContextId.getContextId();
        if (shoppingCart == null || userId == null) {
            return null;
        }
        shoppingCart.setUserId(userId);

        //2，查询该套餐/菜品是否已经在数据库中存在
        Long setmealId = shoppingCart.getSetmealId();
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //点餐人id
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        if (setmealId != null) {
            //套餐id
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        } else if (dishId != null) {
            //菜品id及口味信息
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            //口味不同，认为是两个菜
            //queryWrapper.eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        } else {
            R.error("加入购物车失败");
        }

        ShoppingCart addOne = this.getOne(queryWrapper);

        if (addOne != null) {
            //2.1，存在该套餐/菜品：数量加1
            addOne.setNumber(addOne.getNumber() + 1);
            //更新修改时间
            addOne.setCreateTime(LocalDateTime.now());
            this.updateById(addOne);
        } else {
            //2.2，不存在该套餐/菜品：添加到数据库
            shoppingCart.setNumber(1);
            //更新修改时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            addOne = shoppingCart;
        }
        return addOne;
    }

    /**
     * 从购物车删除菜品（套餐）数量
     *
     * @param shoppingCart json封装了 菜品（套餐）id
     * @return
     */
    @Override
    public ShoppingCart sub(ShoppingCart shoppingCart) {
        //1，获取用户id
        Long userId = ThreadContextId.getContextId();
        if (shoppingCart == null || userId == null) {
            return null;
        }
        //2，查询套餐/菜品的数量
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        Long setmealId = shoppingCart.getSetmealId();
        Long dishId = shoppingCart.getDishId();
        if (setmealId != null) {
            //减少购物车中套餐数量
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        } else if (dishId != null) {
            //减少购物车中菜品数量
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            return null;
        }
        ShoppingCart subOne = this.getOne(queryWrapper);
        subOne.setCreateTime(LocalDateTime.now());
        //3，根据当前数量判断是减少还是删除
        Integer number = subOne.getNumber();
        if (number > 1) {
            //减少
            subOne.setNumber(number - 1);
            this.updateById(subOne);
        } else {
            //删除
            this.removeById(subOne);
            subOne.setNumber(0);
        }
        return subOne;
    }
}
