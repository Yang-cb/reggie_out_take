package com.xxx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxx.reggie.common.CustomException;
import com.xxx.reggie.dto.SetmealDto;
import com.xxx.reggie.mapper.SetmealMapper;
import com.xxx.reggie.pojo.Dish;
import com.xxx.reggie.pojo.Setmeal;
import com.xxx.reggie.pojo.SetmealDish;
import com.xxx.reggie.service.SetmealDishService;
import com.xxx.reggie.service.SetmealService;
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
 * @date 2023/4/19 22:26
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 保存套餐到setmeal表，保存套餐中的菜品到setmeal_dish，共操作两张表
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveSAD(SetmealDto setmealDto) {
        //1，保存套餐到setmeal表
        this.save(setmealDto);
        //2，清除原有的套餐菜品数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //3，添加新的套餐菜品数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream()
                .map(setmealDish -> {
                    setmealDish.setSetmealId(setmealDto.getId());
                    return setmealDish;
                })
                .collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public SetmealDto getSAD(Long id) {
        //1，查询套餐基础信息
        Setmeal setmeal = this.getById(id);

        //2，查询套餐中的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDish = setmealDishService.list(queryWrapper);

        //3，组装数据
        SetmealDto setmealDto = new SetmealDto();
        //拷贝套餐基础信息
        BeanUtils.copyProperties(setmeal, setmealDto);
        //赋值菜品信息
        setmealDto.setSetmealDishes(setmealDish);
        return setmealDto;
    }

    /**
     * 修改
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateSAD(SetmealDto setmealDto) {
        //1，修改套餐基本信息
        this.updateById(setmealDto);
        //2，删除套餐菜品对应表中的旧数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //3，添加到套餐菜品对应表新数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream()
                .map(setmealDish -> {
                    Long setmealId = setmealDto.getId();
                    setmealDish.setSetmealId(setmealId);
                    return setmealDish;
                })
                .collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * [批量]删除套餐
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteList(List<Long> ids) {
        //1，如果要删除的套餐还在售卖中，抛出异常
        //   select count(*) from setmeal where id in (ids) and status = 1;
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId, ids)
                .eq(Setmeal::getStatus, 1);

        int count = this.count(qw);
        if (count > 0) {
            throw new CustomException("套餐正在售卖，无法删除");
        }

        //2，删除套餐对应的菜品
        // delete from setmeal_dish where setmeal_id in (ids)
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper);

        //3，删除套餐
        this.removeByIds(ids);
    }
}
