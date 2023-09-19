package com.example.mall.product.service;

import com.example.mall.product.model.po.AttrAttrgroupRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 属性&属性分组关联 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelation> {
    /**
     * 删除关联表中的信息
     * @param relationList
     */
    void removeBatch(List<AttrAttrgroupRelation> relationList);
}
