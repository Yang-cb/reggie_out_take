package com.xxx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.reggie.common.R;
import com.xxx.reggie.dto.SetmealDto;
import com.xxx.reggie.pojo.Category;
import com.xxx.reggie.pojo.Setmeal;
import com.xxx.reggie.service.CategoryService;
import com.xxx.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * reggie_take_out.com.xxx.reggie.controller
 *
 * @author yang_
 * @description 套餐管理controller
 * @date 2023/4/29 16:39
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @Autowired
    CategoryService categoryService;


    /**
     * [按名称]分页查询套餐数据
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        //1，创建setmeal的分页器
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        //2，创建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);
        //3，分页查询
        setmealService.page(setmealPage, queryWrapper);
        //4，创建setmealDto的分页器
        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        //5，获取setmeal分页器的数据集合，
        // 添加分类名称
        // 赋值给setmealDto分页器的数据集合
        List<Setmeal> setmealRecords = setmealPage.getRecords();
        List<SetmealDto> setmealDtoRecords = setmealRecords.stream()
                .map(setmeal -> {
                    SetmealDto setmealDto = new SetmealDto();
                    BeanUtils.copyProperties(setmeal, setmealDto);
                    Category category = categoryService.getById(setmeal.getCategoryId());
                    String categoryName = category.getName();
                    setmealDto.setCategoryName(categoryName);
                    return setmealDto;
                })
                .collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoRecords);

        return R.success(setmealDtoPage);
    }

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveSAD(setmealDto);
        return R.success("保存成功");
    }

    /**
     * 根据id查询数据（修改回显）
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getSAD(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSAD(setmealDto);

        return R.success("修改成功");
    }

    /**
     * [批量]修改套餐状态：停售/起售
     *
     * @param status 希望变成的状态
     * @param ids    需要修改状态的套餐的id[s]
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, Long... ids) {
        /**
         * 套餐中菜品停售时，对应套餐也要停售。且不能起售
         */

        SetmealDto setmealDto = new SetmealDto();
        for (Long id : ids) {
            setmealDto.setStatus(status);
            setmealDto.setId(id);
            setmealService.updateById(setmealDto);
        }
        return R.success("修改成功");
    }

    /**
     * [批量]删除套餐
     *
     * @param ids 需要删除的套餐的id[s]
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.deleteList(ids);
        return R.success("删除成功");
    }
}
