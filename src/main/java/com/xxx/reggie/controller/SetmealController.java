package com.xxx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.reggie.common.R;
import com.xxx.reggie.dto.SetmealDto;
import com.xxx.reggie.pojo.Category;
import com.xxx.reggie.pojo.Setmeal;
import com.xxx.reggie.pojo.SetmealDish;
import com.xxx.reggie.service.CategoryService;
import com.xxx.reggie.service.DishService;
import com.xxx.reggie.service.SetmealDishService;
import com.xxx.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    DishService dishService;


    /**
     * [按名称]分页查询套餐数据
     *
     * @param page     第几页
     * @param pageSize 每页多少条
     * @param name     [套餐名]
     * @return page对象
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
                    //拷贝套餐基本信息
                    BeanUtils.copyProperties(setmeal, setmealDto);

                    //获取套餐分类名称
                    Category category = categoryService.getById(setmeal.getCategoryId());
                    if (category != null) {
                        String categoryName = category.getName();
                        setmealDto.setCategoryName(categoryName);
                    }

                    return setmealDto;
                })
                .collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoRecords);

        return R.success(setmealDtoPage);
    }

    /**
     * 新增套餐
     *
     * @param setmealDto json数据封装setmealDto对象
     * @return msg
     */
    //新增套餐的同时将已有缓存全部删除
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveSad(setmealDto);
        return R.success("保存成功");
    }

    /**
     * 根据id查询数据（修改回显）
     *
     * @param id id
     * @return setmealDto对象
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getSad(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐信息
     *
     * @param setmealDto json封装对象
     * @return
     */
    //修改套餐的同时将已有缓存全部删除
    @PutMapping
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSad(setmealDto);
        return R.success("修改成功");
    }

    /**
     * [批量]修改套餐状态：停售/起售
     *
     * @param status 希望变成的状态
     * @param ids    需要修改状态的套餐的id[s]
     * @return msg
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, Long... ids) {
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
     * @return msg
     */
    //删除套餐，同时将已有缓存全部删除
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.deleteList(ids);
        return R.success("删除成功");
    }

    /**
     * 查询具体套餐的数据
     *
     * @param setmeal 封装了 菜品分类id：categoryId 和 状态status
     * @return 查到的套餐集合
     */
    //将返回结果加入redis缓存
    @Cacheable(cacheNames = "setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        //1，获取套餐分类id，套餐的状态
        Long categoryId = setmeal.getCategoryId();
        Integer setmealStatus = setmeal.getStatus();
        //2，根据套餐分类id查询套餐
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //查询该分类下的起售的套餐
        queryWrapper.eq(categoryId != null, Setmeal::getCategoryId, categoryId)
                .eq(setmealStatus != null, Setmeal::getStatus, setmealStatus)
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 查看套餐（菜品）的详情（移动端）
     *
     * @param id 套餐（菜品）id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<SetmealDish> getDish(@PathVariable Long id) {
        // ...
        return null;
    }
}
