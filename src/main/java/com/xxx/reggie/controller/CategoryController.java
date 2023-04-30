package com.xxx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.reggie.common.R;
import com.xxx.reggie.pojo.Category;
import com.xxx.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * reggie_take_out.com.xxx.reggie.controller
 *
 * @author yang_
 * @description 分类
 * @date 2023/4/19 20:33
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询
     *
     * @param page     第几页
     * @param pageSize 每页多少条
     * @return page
     */
    @GetMapping("/page")
    public R<Page<Category>> page(Integer page, Integer pageSize) {
        // 创建分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        // 按顺序排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getType, Category::getSort);

        // 分页查询
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }


    /**
     * 添加数据
     *
     * @param category 前端键入的值，封装为json
     * @return msg
     */
    @PostMapping
    public R<String> add(@RequestBody Category category) {
//        log.info("category ==> {}", category);
        categoryService.save(category);
        return R.success("保存成功");
    }

    /**
     * 修改数据
     *
     * @param category json
     * @return msg
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根据id删除
     *
     * @param ids ?ids
     * @return msg
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        // 自定义的删除方法
        categoryService.removeClassify(ids);
        return R.success("删除成功");
    }

    /**
     * 查询分类的数据
     *
     * @param category 主要是获取type：用于区分 菜品分类，套餐分类
     * @return Category集合
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //如果type不为空，就按type查询
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //排序
        queryWrapper.orderByAsc(Category::getSort);
        List<Category> list = categoryService.list(queryWrapper);
//        log.info("list => {}", list);
        return R.success(list);
    }
}
