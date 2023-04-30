package com.xxx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.reggie.dto.DishDto;
import com.xxx.reggie.pojo.Dish;

import java.util.List;

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
     * @param dishDto json对象
     */
    void saveDaf(DishDto dishDto);

    /**
     * 根据id查询 菜品基本信息 及 菜品的口味信息，需要两张表
     *
     * @param id id
     * @return dishDto
     */
    DishDto getDaf(Long id);

    /**
     * 修改信息
     *
     * @param dishDto json
     */
    void updateDaf(DishDto dishDto);

    /**
     * [批量]删除菜品
     *
     * @param ids 需要删除的id
     */
    void deleteList(List<Long> ids);

    /**
     * [批量]修改套餐状态：停售/起售
     *
     * @param status 希望变成的状态
     * @param ids    需要修改状态的套餐的id[s]
     */
    void updateStatus(Integer status, List<Long> ids);

}
