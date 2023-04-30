package com.xxx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.reggie.common.CustomException;
import com.xxx.reggie.dto.DishDto;
import com.xxx.reggie.mapper.DishMapper;
import com.xxx.reggie.pojo.Dish;
import com.xxx.reggie.pojo.DishFlavor;
import com.xxx.reggie.service.DishFlavorService;
import com.xxx.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * reggie_take_out.com.xxx.reggie.service.impl
 *
 * @author yang_
 * @description 描述
 * @date 2023/4/19 22:23
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 保存 菜品基本信息 及 菜品的口味信息，需要两张表
     *
     * @param dishDto
     */
    //操作两张表，使用事务保证都能保存上
    @Transactional
    public void saveDAF(DishDto dishDto) {
        //保存菜品到菜品表 dish
        this.save(dishDto);
        //保存口味
        //1，获取口味对应的具体菜品的id
        Long id = dishDto.getId();
        //2，获取口味集合
        List<DishFlavor> flavors = dishDto.getFlavors();

        //3，处理数据
        flavors = flavors.stream()
                //通过id，将菜品与口味关联（外键）
                .map(flavor -> {
                    flavor.setDishId(id);
                    return flavor;
                })
                .collect(Collectors.toList());

        //4，保存口味到口味表 dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询 菜品基本信息 及 菜品的口味信息，需要两张表
     *
     * @param id
     * @return
     */
    @Transactional
    public DishDto getDAF(Long id) {
        //1，根据id查询菜品基本信息，从dish表查
        Dish dish = this.getById(id);

        //2，根据菜品id（dishId）查询菜品口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);

        //3，将基本数据添加到dishDto
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //4，将口味信息添加到dishDto
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 修改信息
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateDAF(DishDto dishDto) {
        //1，将菜品基本信息保存到 dish表
        this.updateById(dishDto);
        //2，清除原有口味 dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //3，添加新增口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream()
                .map(flavor -> {
                    flavor.setDishId(dishDto.getId());
                    return flavor;
                })
                .collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * [批量]删除菜品
     *
     * @param ids 需要删除的id
     */
    @Override
    public void deleteList(List<Long> ids) {
        //1，判断菜品是否是 售卖状态
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.in(Dish::getId, ids)
                .eq(Dish::getStatus, 1); // 1 为正在售卖
        int count = this.count(qw);
        if (count > 0) {
            //有售卖的菜品
            throw new CustomException("菜品正在售卖，无法删除");
        }

        //2，删除对应的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(queryWrapper);

        //3，删除菜品
        this.removeByIds(ids);
    }

    /**
     * [批量]修改套餐状态：停售/起售
     *
     * @param status 希望变成的状态
     * @param ids    需要修改状态的套餐的id[s]
     */
    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        Dish dish = new Dish();
        dish.setStatus(status);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);

        this.update(dish, queryWrapper);
    }
}
