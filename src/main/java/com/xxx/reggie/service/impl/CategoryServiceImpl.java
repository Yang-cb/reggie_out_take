package com.xxx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.reggie.common.CustomException;
import com.xxx.reggie.mapper.CategoryMapper;
import com.xxx.reggie.pojo.Category;
import com.xxx.reggie.pojo.Setmeal;
import com.xxx.reggie.pojo.Dish;
import com.xxx.reggie.service.CategoryService;
import com.xxx.reggie.service.DishService;
import com.xxx.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * reggie_take_out.com.xxx.reggie.service.impl
 *
 * @author yang_
 * @description 描述
 * @date 2023/4/19 20:31
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    //注入套餐service
    @Autowired
    private SetmealService setmealService;
    //注入菜品service
    @Autowired
    private DishService dishService;

    /**
     * 根据id删除分类，删除之前要先判断该分类中是否有套餐/菜品
     *
     * @param id 分类id
     */
    @Override
    public void removeClassify(Long id) {
        // 是否有套餐
        LambdaQueryWrapper<Setmeal> qw1 = new LambdaQueryWrapper<>();
        qw1.eq(Setmeal::getCategoryId, id);
        if (setmealService.count(qw1) > 0) {
            //抛出异常
            throw new CustomException("分类中存有套餐，无法删除");
        }
        // 是否有菜品
        LambdaQueryWrapper<Dish> qw2 = new LambdaQueryWrapper<>();
        qw2.eq(Dish::getCategoryId, id);
        if (dishService.count(qw2) > 0) {
            //
            throw new CustomException("分类中存有菜品，无法删除");
        }
        // 都没有 -> 删除
        super.removeById(id);
    }
}
