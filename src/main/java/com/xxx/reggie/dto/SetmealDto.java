package com.xxx.reggie.dto;

import com.xxx.reggie.pojo.Setmeal;
import com.xxx.reggie.pojo.SetmealDish;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * reggie_take_out.com.xxx.reggie.dto
 *
 * @author yang_
 * @description 描述
 * @date 2023/4/29 16:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SetmealDto extends Setmeal {
    //套餐分类
    private String categoryName;

    //套餐中的菜品集合
    private List<SetmealDish> setmealDishes;
}
