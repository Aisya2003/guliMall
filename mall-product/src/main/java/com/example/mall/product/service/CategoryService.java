package com.example.mall.product.service;

import com.example.mall.product.model.dto.CategoryDto;
import com.example.mall.product.model.po.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.product.model.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品三级分类 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface CategoryService extends IService<Category> {

    /**
     * 查询所有分类，返回树形结构
     *
     * @return
     */
    List<CategoryDto> listTree();

    /**
     * 根据id批量删除（逻辑删除）
     *
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 带分类路径查询
     *
     * @param id
     * @return
     */
    Long[] parentIdPath(Long id);

    /**
     * 更新关联的所有表数据
     *
     * @param category
     */
    void updateCascade(Category category);

    interface Web {
        /**
         * 获取根分类
         * @return
         */
        List<Category> getRootCategories();

        Map<String, List<Catelog2Vo>> getAllCategoriesJson();
    }
}
