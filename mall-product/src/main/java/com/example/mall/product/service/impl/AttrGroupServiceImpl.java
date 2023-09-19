package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.mall.common.model.constant.ProductConstant;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.result.Result;
import com.example.mall.product.model.dto.AttrGroupWithAttrDto;
import com.example.mall.product.model.dto.AttrGroupWithCatePathDto;
import com.example.mall.product.model.dto.AttrGroupRelationDto;
import com.example.mall.product.model.po.Attr;
import com.example.mall.product.model.po.AttrAttrgroupRelation;
import com.example.mall.product.model.po.AttrGroup;
import com.example.mall.product.mapper.AttrGroupMapper;
import com.example.mall.product.model.vo.SkuDetailVo;
import com.example.mall.product.service.AttrAttrgroupRelationService;
import com.example.mall.product.service.AttrGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.product.service.AttrService;
import com.example.mall.product.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup> implements AttrGroupService {
    private final CategoryService categoryService;
    private final AttrAttrgroupRelationService attrAttrgroupRelationService;
    private final AttrService attrService;

    public AttrGroupServiceImpl(CategoryService categoryService, AttrAttrgroupRelationService attrAttrgroupRelationService, AttrService attrService) {
        this.categoryService = categoryService;
        this.attrAttrgroupRelationService = attrAttrgroupRelationService;
        this.attrService = attrService;
    }

    @Override
    public Result listByPage(PageRequestParams params) {
        PageResult<AttrGroup> pageResult = PageResultUtils
                .getPage(params, baseMapper, AttrGroup.class);
        return Result.ok(pageResult);
    }

    @Override
    public Result listByPageAndCatId(PageRequestParams params, Long catId) {
        if (catId == null || catId == 0)
            return listByPage(params);
        else {
            QueryWrapper<AttrGroup> wrapper = new QueryWrapper<AttrGroup>().eq(ProductConstant.AttrGroupTableNameEnum.CATEGORY_ID.getTableName(), catId);
            PageResult<AttrGroup> page = PageResultUtils.getPage(params, baseMapper, AttrGroup.class, wrapper);
            return Result.ok(page);
        }
    }

    @Override
    public AttrGroupWithCatePathDto getInfoWithCatePath(Long id) {
        AttrGroup attrGroup = this.getById(id);
        AttrGroupWithCatePathDto dto = new AttrGroupWithCatePathDto();
        dto.setCatelogIdPath(categoryService.parentIdPath(attrGroup.getCatelogId()));
        BeanUtils.copyProperties(attrGroup, dto);
        return dto;
    }

    @Override
    public List<Attr> getAttrGroupRelation(Long attrGroupId) {
        List<AttrAttrgroupRelation> relations = attrAttrgroupRelationService.list(new LambdaUpdateWrapper<AttrAttrgroupRelation>()
                .eq(AttrAttrgroupRelation::getAttrGroupId, attrGroupId));
        List<Long> attrIds = relations.stream()
                .map(AttrAttrgroupRelation::getAttrId).collect(Collectors.toList());
        if (attrIds.isEmpty()) {
            return null;
        }
        return attrService.listByIds(attrIds);
    }

    @Override
    public void removeRelations(AttrGroupRelationDto[] dtos) {
        List<AttrAttrgroupRelation> relationList = Arrays.stream(dtos).map(dto -> {
            if (dto.getAttrGroupId() != null && dto.getAttrId() != null) {
                AttrAttrgroupRelation relation = new AttrAttrgroupRelation();
                BeanUtils.copyProperties(dto, relation);
                return relation;
            } else {
                return null;
            }
        }).collect(Collectors.toList());
        attrAttrgroupRelationService.removeBatch(relationList);
    }

    @Override
    public PageResult<Attr> getNoRelation(Long attrGroupId, PageRequestParams params) {
        //查询所在分类ID
        AttrGroup attrGroup = this.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        //获取本分类下的其他分组id，用于排除分组中的属性，自己使用过的分组也需要被排除，因此不需要设置AttrGroup::getAttrGroupId, attrGroupId
        List<Long> groupIds = this.list(new LambdaQueryWrapper<AttrGroup>()
                        .eq(AttrGroup::getCatelogId, catelogId))
                .stream()
                .map(AttrGroup::getAttrGroupId)
                .collect(Collectors.toList());
        if (groupIds.size() == 0) {
            return null;
        }
        //获取这些分组下的所有属性id
        List<Long> attrIds = attrAttrgroupRelationService.list(new LambdaQueryWrapper<AttrAttrgroupRelation>()
                        .in(AttrAttrgroupRelation::getAttrGroupId, groupIds))
                .stream()
                .map(AttrAttrgroupRelation::getAttrId)
                .collect(Collectors.toList());
        //查询除了这些属性的其他属性，封装为分页查询
        QueryWrapper<Attr> queryWrapper = new QueryWrapper<Attr>()
                .eq(ProductConstant.AttrTableNameEnum.CATEGORY_ID.getTableName(), catelogId)
                .eq(ProductConstant.AttrTableNameEnum.ATTR_TYPE.getTableName(), ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds.size() > 0) {
            queryWrapper.notIn(ProductConstant.AttrTableNameEnum.ATTR_ID.getTableName(), attrIds);
        }
        return PageResultUtils.getPage(params, attrService.getBaseMapper(), Attr.class, queryWrapper);
    }

    @Override
    public void saveGroupRelations(AttrGroupRelationDto[] dto) {
        List<AttrAttrgroupRelation> relations = Arrays.stream(dto)
                .map(attrGroupRelationDto -> {
                    if (attrGroupRelationDto.getAttrGroupId() == null && attrGroupRelationDto.getAttrId() == null) {
                        return null;
                    }
                    AttrAttrgroupRelation relation = new AttrAttrgroupRelation();
                    BeanUtils.copyProperties(attrGroupRelationDto, relation);
                    return relation;
                })
                .collect(Collectors.toList());
        attrAttrgroupRelationService.saveBatch(relations);
    }

    @Override
    public List<AttrGroupWithAttrDto> getAttrAndAttrGroupByCateId(Long catalogId) {
        //查询所有分组信息
        return list(new LambdaQueryWrapper<AttrGroup>()
                //查询组信息
                .eq(catalogId != null, AttrGroup::getCatelogId, catalogId))
                .stream()
                .map(attrGroup -> {//设置attr信息
                    AttrGroupWithAttrDto dto = new AttrGroupWithAttrDto();
                    BeanUtils.copyProperties(attrGroup, dto);
                    dto.setAttrs(this.getAttrGroupRelation(dto.getAttrGroupId()));
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<SkuDetailVo.SpuDetailAttrGroupVo> getAttrAndAttrGroupBySpuId(Long spuId, Long catalogId) {
        return this.baseMapper.getAttrGroupDetailBySpuIdAndCatalogId(spuId,catalogId);
    }
}
