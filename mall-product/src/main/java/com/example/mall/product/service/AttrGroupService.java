package com.example.mall.product.service;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.result.Result;
import com.example.mall.product.model.dto.AttrGroupWithAttrDto;
import com.example.mall.product.model.dto.AttrGroupWithCatePathDto;
import com.example.mall.product.model.dto.AttrGroupRelationDto;
import com.example.mall.product.model.po.Attr;
import com.example.mall.product.model.po.AttrGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.product.model.vo.SkuDetailVo;

import java.util.List;

/**
 * <p>
 * 属性分组 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface AttrGroupService extends IService<AttrGroup> {
    Result listByPage(PageRequestParams params);

    Result listByPageAndCatId(PageRequestParams params, Long catId);


    AttrGroupWithCatePathDto getInfoWithCatePath(Long id);

    /**
     * 查询关联的属性
     * @param attrGroupId
     * @return
     */
    List<Attr> getAttrGroupRelation(Long attrGroupId);

    /**
     * 删除属性和关联
     * @param dtos
     */
    void removeRelations(AttrGroupRelationDto[] dtos);

    /**
     * 查询可关联的属性，这些不能是已经关联分组下的属性，同时这些属性必须还是当前分类下的属性
     * @param attrGroupId
     * @param params
     * @return
     */
    PageResult<Attr> getNoRelation(Long attrGroupId, PageRequestParams params);

    /**
     * 保存关系属性
     * @param dto
     */
    void saveGroupRelations(AttrGroupRelationDto[] dto);

    List<AttrGroupWithAttrDto> getAttrAndAttrGroupByCateId(Long catalogId);

    List<SkuDetailVo.SpuDetailAttrGroupVo> getAttrAndAttrGroupBySpuId(Long spuId, Long catalogId);
}
