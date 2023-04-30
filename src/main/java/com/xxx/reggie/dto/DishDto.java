package com.xxx.reggie.dto;

import com.xxx.reggie.pojo.Dish;
import com.xxx.reggie.pojo.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * reggie_take_out.com.xxx.reggie.dto
 *
 * @author yang_
 * @description 描述
 * @date 2023/4/21 21:14
 */
@Data
public class DishDto extends Dish {
    //口味
    private List<DishFlavor> flavors = new ArrayList<>();

    //菜品分类
    private String categoryName;

    private Integer copies;
}
