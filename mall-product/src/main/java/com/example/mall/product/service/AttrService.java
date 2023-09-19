package com.example.mall.product.service;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.product.model.dto.AttrRequestDto;
import com.example.mall.product.model.dto.AttrResponseDto;
import com.example.mall.product.model.po.Attr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品属性 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface AttrService extends IService<Attr> {

    void saveRelation(AttrRequestDto attrRequestDto);

    /**
     * 分页查询
     *
     * @param catlogId
     * @param params
     * @param attrType
     * @return
     */
    PageResult<AttrResponseDto> selectList(Long catlogId, PageRequestParams params, String attrType);

    /**
     * 携带分组路径
     * @param attrId
     * @return
     */
    AttrResponseDto selectWithPath(Long attrId);

    void updateRelation(AttrRequestDto attrRequestDto);

    void removeBatchCascadeRelation(Long[] ids);

    List<Long> getSearchableAttrIdList(List<Long> attrIds);
}
