package com.xxx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.reggie.dto.SetmealDto;
import com.xxx.reggie.pojo.Setmeal;

import java.util.List;

/**
 * reggie_take_out.com.xxx.reggie.service
 *
 * @author yang_
 * @description 描述
 * @date 2023/4/19 22:26
 */

public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存套餐到setmeal表，保存套餐中的菜品到setmeal_dish，共操作两张表
     *
     * @param setmealDto json
     */
    void saveSad(SetmealDto setmealDto);

    /**
     * 按id查询，数据回显
     *
     * @param id /id
     * @return SetmealDto
     */
    SetmealDto getSad(Long id);

    /**
     * 修改
     *
     * @param setmealDto json
     */
    void updateSad(SetmealDto setmealDto);

    /**
     * [批量]删除套餐
     *
     * @param ids ?ids&ids
     */
    void deleteList(List<Long> ids);
}
