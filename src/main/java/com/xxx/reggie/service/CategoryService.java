package com.xxx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xxx.reggie.pojo.Category;

/**
 * reggie_take_out.com.xxx.reggie.service
 *
 * @author yang_
 * @description 描述
 * @date 2023/4/19 20:30
 */
public interface CategoryService extends IService<Category> {
    /**
     * 根据id删除分类，删除之前要先判断该分类中是否有套餐/菜品
     *
     * @param id 分类id
     */
    public void removeClassify(Long id);
}
