package com.example.mall.product.controller;

import com.example.mall.common.model.result.Result;
import com.example.mall.product.model.dto.CategoryDto;
import com.example.mall.product.model.po.Category;
import com.example.mall.product.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/product/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 返回分类信息
     */
    @GetMapping("/list/tree")
    public Result allCategory() {
        List<CategoryDto> categoryList = categoryService.listTree();
        return Result.ok(categoryList);
    }

    /**
     * 批量删除分类
     */
    @PostMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody Long[] ids) {
        categoryService.deleteBatch(Arrays.asList(ids));
        return Result.ok();
    }

    /**
     * 新增分类
     *
     * @param category
     * @return
     */
    @PostMapping("/append")
    public Result appendCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.ok();
    }

    /**
     * 查询单挑分类信息
     * @param id
     * @return
     */
    @GetMapping("/{categoryId}")
    public Result getCategoryInfo(@PathVariable("categoryId") Long id) {
        return Result.ok(categoryService.getById(id));
    }

    /**
     * 更新分类
     * @param dto
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Category category){
        categoryService.updateCascade(category);
        return Result.ok();
    }
    @PostMapping("/updateBatch")
    public Result updateBatch(@RequestBody Category[] lists){
        categoryService.updateBatchById(Arrays.asList(lists));
        return Result.ok();
    }

}
