package com.xxx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.reggie.dto.DishDto;
import com.xxx.reggie.pojo.Dish;

/**
 * reggie_take_out.com.xxx.reggie.service
 *
 * @author yang_
 * @description 描述
 * @date 2023/4/19 22:23
 */
public interface DishService extends IService<Dish> {
    /**
     * 保存 菜品基本信息 及 菜品的口味信息，需要两张表
     *
     * @param dishDto
     */
    public void saveDAF(DishDto dishDto);

    /**
     * 根据id查询 菜品基本信息 及 菜品的口味信息，需要两张表
     *
     * @param id
     * @return
     */
    public DishDto getDAF(Long id);

    /**
     * 修改信息
     * @param dishDto
     */
    public void updateDAF(DishDto dishDto);
}
