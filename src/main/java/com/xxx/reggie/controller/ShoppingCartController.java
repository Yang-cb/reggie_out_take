package com.xxx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xxx.reggie.common.R;
import com.xxx.reggie.common.ThreadContextId;
import com.xxx.reggie.pojo.ShoppingCart;
import com.xxx.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * reggie_take_out.com.xxx.reggie.controller
 *
 * @author yang_
 * @description 描述
 * @date 2023/5/1 20:11
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    //{amount: 78, dishFlavor: "不要葱,中辣", dishId: "1397849739276890114", name: "辣子鸡",…}

    /**
     * 添加菜品（套餐）到购物车
     *
     * @param shoppingCart json封装了金额、口味（String）、菜品（套餐）id、菜品（套餐）名称
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        ShoppingCart addOne = shoppingCartService.add(shoppingCart);
        return R.success(addOne);
    }

    /**
     * 从购物车删除菜品（套餐）数量
     *
     * @param shoppingCart json封装了 菜品（套餐）id
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        ShoppingCart subOne = shoppingCartService.sub(shoppingCart);
        return R.success(subOne);
    }

    /**
     * 查询userid对应的购物车信息
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        //1，获取用户id
        Long userId = ThreadContextId.getContextId();
        if (userId == null) {
            return R.error("查询购物车信息失败");
        }
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId)
                .orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        return R.success(shoppingCartList);
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        //1，获取用户id
        Long userId = ThreadContextId.getContextId();
        if (userId == null) {
            return R.error("购物车清空失败");
        }
        //2，清空用户id下的所有商品
        // delete from shopping_cart where userId = (?)
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(queryWrapper);
        return R.success("购物车已清空");
    }
}
