package com.xxx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.reggie.common.R;
import com.xxx.reggie.dto.DishDto;
import com.xxx.reggie.pojo.Category;
import com.xxx.reggie.pojo.Dish;
import com.xxx.reggie.service.CategoryService;
import com.xxx.reggie.service.DishService;
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
 * @description 菜品
 * @date 2023/4/21 19:12
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品
     *
     * @param dishDto json
     * @return msg
     */
    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto) {
        dishService.saveDaf(dishDto);
        return R.success("保存成功");
    }


    /**
     * [根据名称]分页查询
     *
     * @param page     第几页
     * @param pageSize 每页多少条数据
     * @param name     名称
     * @return 页面需要（分类名称），而Dish里没有，需要一个“中间类”DishDto
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        //1，创建Dish的分页器
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //2，创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .like(name != null, Dish::getName, name)
                .orderByDesc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        //3，使用Dish分页器分页查询数据库
        dishService.page(dishPage, queryWrapper);
        //4，创建DishDto的分页器
        Page<DishDto> dishDtoPage = new Page<>();
        //5，将Dish分页器的属性（除数据，数据需要加工一下） 拷贝给 DishDto分页器
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //6，加工数据：
        //      将分类名称放入Dish分页器的数据中，并当作DishDto分页器的数据。
        List<Dish> dishRecords = dishPage.getRecords();
        List<DishDto> dishDtoRecords = dishRecords.stream()
                .map(dish -> {
                    DishDto dishDto = new DishDto();
                    //将dish的属性尽数拷贝给dishDto
                    BeanUtils.copyProperties(dish, dishDto);
                    //获取分类名称，赋值给dishDto
                    Category category = categoryService.getById(dish.getCategoryId());
                    if (category != null) {
                        String categoryName = category.getName();
                        dishDto.setCategoryName(categoryName);
                    }
                    return dishDto;
                })
                .collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoRecords);
        return R.success(dishDtoPage);
    }

    /**
     * （修改回显）根据id查询菜品基本信息，口味信息
     *
     * @param id /id
     * @return dishDto
     */
    @GetMapping("/{id}")
    public R<DishDto> getOne(@PathVariable Long id) {
        DishDto dishDto = dishService.getDaf(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto json
     * @return msg
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateDaf(dishDto);
        return R.success("修改成功");
    }


    /**
     * 根据分类id查询该分类下的菜品信息（新建套餐时、移动端使用）
     *
     * @param dishDto 对象dishDto中包含id，且传过来其他字段也能使用该方法，通用性更强。
     * @return Dish集合，移动端需要展示口味
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(DishDto dishDto) {
        List<DishDto> dishDtoList = dishService.listDaf(dishDto);
        return R.success(dishDtoList);
    }

    /**
     * [批量]删除菜品
     *
     * @param ids 需要删除的id
     * @return msg
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.deleteList(ids);
        return R.success("删除成功");
    }

    /**
     * [批量]修改菜品状态：停售/起售
     *
     * @param status 希望变成的状态
     * @param ids    需要修改状态的套餐的id[s]
     * @return msg
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        dishService.updateStatus(status, ids);
        return R.success("修改状态成功");
    }
}
