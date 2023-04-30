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
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto) {
        dishService.saveDAF(dishDto);
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
                .orderByDesc(Dish::getSort);
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
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getOne(@PathVariable Long id) {
        DishDto dishDto = dishService.getDAF(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateDAF(dishDto);

        return R.success("修改成功");
    }


    /**
     * 根据分类id查询该分类下的菜品信息
     *
     * @param dish 对象dish中包含id，且传过来其他字段也能使用该方法，通用性更强。
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus, 1) //只要 在售 的菜品（状态为1）
                .orderByAsc(Dish::getSort)  //排序
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);
        return R.success(dishList);
    }
}
