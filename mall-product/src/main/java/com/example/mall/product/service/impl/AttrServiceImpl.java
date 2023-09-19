package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.mall.common.model.constant.ProductConstant;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.product.model.dto.AttrRequestDto;
import com.example.mall.product.model.dto.AttrResponseDto;
import com.example.mall.product.model.po.Attr;
import com.example.mall.product.mapper.AttrMapper;
import com.example.mall.product.model.po.AttrAttrgroupRelation;
import com.example.mall.product.model.po.AttrGroup;
import com.example.mall.product.model.po.Category;
import com.example.mall.product.service.AttrAttrgroupRelationService;
import com.example.mall.product.service.AttrGroupService;
import com.example.mall.product.service.AttrService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {
    private final AttrAttrgroupRelationService attrAttrgroupRelationService;
    private final AttrGroupService attrGroupService;
    private final CategoryService categoryService;


    @Lazy
    public AttrServiceImpl(AttrAttrgroupRelationService attrAttrgroupRelationService, AttrGroupService attrGroupService, CategoryService categoryService) {
        this.attrAttrgroupRelationService = attrAttrgroupRelationService;
        this.attrGroupService = attrGroupService;
        this.categoryService = categoryService;
    }

    @Override
    @Transactional
    public void saveRelation(AttrRequestDto attrRequestDto) {


        Attr attr = new Attr();
        BeanUtils.copyProperties(attrRequestDto, attr);
        //保存基本信息
        this.save(attr);
        //保存关联信息
        if (attrRequestDto.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode()
                || attrRequestDto.getAttrGroupId() == null
        ) {//销售参数不需要设置关联
            return;
        }
        AttrAttrgroupRelation relation = new AttrAttrgroupRelation();
        relation.setAttrGroupId(attrRequestDto.getAttrGroupId());
        relation.setAttrId(attr.getAttrId());

        attrAttrgroupRelationService.save(relation);
    }

    @Override
    public PageResult<AttrResponseDto> selectList(Long catlogId, PageRequestParams params, String attrType) {

        QueryWrapper<Attr> queryWrapper = new QueryWrapper<Attr>()
                .eq(ProductConstant.AttrTableNameEnum.ATTR_TYPE.getTableName(), attrType.equalsIgnoreCase("base")
                        ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                        : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

        if (catlogId != null && catlogId != 0) {
            queryWrapper.eq(ProductConstant.AttrTableNameEnum.CATEGORY_ID.getTableName(), catlogId);
        }
        PageResult<Attr> result = PageResultUtils.getPage(params, baseMapper, Attr.class, queryWrapper);
        //补充返回信息
        return new PageResult<>(mapDto(result, attrType), result.getTotalCount());
    }

    @Override
    public AttrResponseDto selectWithPath(Long attrId) {
        Attr attr = this.getById(attrId);
        AttrResponseDto dto = new AttrResponseDto();
        BeanUtils.copyProperties(attr, dto);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelation relation = attrAttrgroupRelationService.getOne(new LambdaQueryWrapper<AttrAttrgroupRelation>()
                    .eq(AttrAttrgroupRelation::getAttrId, attrId));
            if (relation != null) {//组装分组信息
                Long attrGroupId = relation.getAttrGroupId();
                dto.setAttrGroupId(attrGroupId);
            }
        }
        //组装分类信息
        Long catelogId = dto.getCatelogId();
        dto.setCatelogPath(categoryService.parentIdPath(catelogId));
        return dto;
    }

    @Transactional
    @Override
    public void updateRelation(AttrRequestDto attrRequestDto) {
        if (attrRequestDto.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode()) {//销售参数不需要设置关联
            return;
        }
        Attr attr = new Attr();
        BeanUtils.copyProperties(attrRequestDto, attr);
        this.updateById(attr);

        //修改关联的表信息
        AttrAttrgroupRelation relation = new AttrAttrgroupRelation();
        Long attrId = attr.getAttrId();
        relation.setAttrId(attrId);
        relation.setAttrGroupId(attrRequestDto.getAttrGroupId());

        long count = attrAttrgroupRelationService.count(new LambdaQueryWrapper<AttrAttrgroupRelation>()
                .eq(AttrAttrgroupRelation::getAttrId, attrId));
        if (count > 0) {//更新
            attrAttrgroupRelationService.update(relation, new LambdaUpdateWrapper<AttrAttrgroupRelation>()
                    .eq(AttrAttrgroupRelation::getAttrId, attrId));
        } else {//新增
            attrAttrgroupRelationService.save(relation);
        }


    }

    @Override
    public void removeBatchCascadeRelation(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        List<Long> relationIds = attrAttrgroupRelationService.list(new LambdaQueryWrapper<AttrAttrgroupRelation>()
                        .in(AttrAttrgroupRelation::getAttrId, idList))
                .stream()
                .map(AttrAttrgroupRelation::getId)
                .collect(Collectors.toList());
        removeBatchByIds(idList);
        if (relationIds.size() > 0) {
            attrAttrgroupRelationService.removeBatchByIds(relationIds);
        }
    }

    @Override
    public List<Long> getSearchableAttrIdList(List<Long> attrIds) {
        return this.list(new LambdaQueryWrapper<Attr>()
                        .in(Attr::getAttrId, attrIds)
                        .eq(Attr::getSearchType, ProductConstant.AttrEnum.ATTR_SEARCHABLE.getCode()))
                .stream()
                .map(Attr::getAttrId)
                .collect(Collectors.toList());
    }


    private List<AttrResponseDto> mapDto(PageResult<Attr> result, String attrType) {
        return result.getList()
                .stream()
                .map(attr -> {
                    AttrResponseDto attrResponseDto = new AttrResponseDto();
                    BeanUtils.copyProperties(attr, attrResponseDto);

                    //设置分类和组名
                    //1.组名，从属性和属性组关系表中查询到组ID
                    if (attrType.equalsIgnoreCase("base")) {//存在分组信息
                        AttrAttrgroupRelation relation = attrAttrgroupRelationService.getOne(
                                new LambdaQueryWrapper<AttrAttrgroupRelation>()
                                        .eq(AttrAttrgroupRelation::getAttrId, attr.getAttrId())
                        );

                        //从属性组中查询组id
                        if (relation != null && relation.getAttrGroupId() != null) {
                            AttrGroup attrGroup = attrGroupService.getById(relation.getAttrGroupId());
                            if (attrGroup != null) {
                                attrResponseDto.setGroupName(attrGroup.getAttrGroupName());
                            }
                        }
                    }
                    //2.分类
                    Category category = categoryService.getById(attr.getCatelogId());
                    if (category != null) {
                        attrResponseDto.setCatelogName(category.getName());
                    }
                    attr = null;
                    return attrResponseDto;
                }).collect(Collectors.toList());
    }
}
