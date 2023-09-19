package com.example.mall.product.controller;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.result.Result;
import com.example.mall.product.model.dto.AttrGroupRelationDto;
import com.example.mall.product.model.dto.AttrGroupWithAttrDto;
import com.example.mall.product.model.po.Attr;
import com.example.mall.product.model.po.AttrGroup;
import com.example.mall.product.service.AttrGroupService;
import com.example.mall.product.service.CategoryService;
import com.example.mall.product.service.ProductAttrValueService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/product/attrGroup")
public class AttrGroupController {
    private final AttrGroupService attrGroupService;

    public AttrGroupController(AttrGroupService attrGroupService, CategoryService categoryService, ProductAttrValueService productAttrValueService) {
        this.attrGroupService = attrGroupService;
    }

    /**
     * 获取属性和属性组信息
     *
     * @param catelogId
     * @return
     */
    @GetMapping("/{catalogId}/withattr")
    public Result getAttrAndAttrGroup(@PathVariable("catalogId") Long catalogId) {
        List<AttrGroupWithAttrDto> dtoList = attrGroupService. getAttrAndAttrGroupByCateId(catalogId);
        return Result.ok(dtoList);
    }


    @PostMapping("/attr/relation")
    public Result saveRelations(@RequestBody AttrGroupRelationDto[] dto) {
        attrGroupService.saveGroupRelations(dto);
        return Result.ok();
    }

    /**
     * 获取当前分类下的本分组未关联的属性
     *
     * @param attrGroupId
     * @param params
     * @return
     */
    @GetMapping("/{attrGroupId}/noattr/relation")
    public Result availableRelations(@PathVariable("attrGroupId") Long attrGroupId,
                                     PageRequestParams params
    ) {
        PageResult<Attr> pageResult = attrGroupService.getNoRelation(attrGroupId, params);
        return Result.ok(pageResult);
    }

    /**
     * 查询分组下的所有属性
     *
     * @param attrGroupId
     * @return
     */
    @GetMapping("/{attrGroupId}/attr/relation")
    public Result attrGroupRelation(@PathVariable("attrGroupId") Long attrGroupId) {
        List<Attr> relation = attrGroupService.getAttrGroupRelation(attrGroupId);
        return Result.ok(relation);
    }

    /**
     * 删除关联的属性
     *
     * @param dtos
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public Result removeRelations(@RequestBody AttrGroupRelationDto[] dtos) {
        attrGroupService.removeRelations(dtos);
        return Result.ok();
    }

    /**
     * 分页查询
     *
     * @return
     */
    @GetMapping("/list")
    public Result attrGroupList(PageRequestParams params) {
        return attrGroupService.listByPage(params);
    }

    @GetMapping("/list/{catId}")
    public Result attrGroupListById(PageRequestParams params, @PathVariable("catId") Long catId) {
        return attrGroupService.listByPageAndCatId(params, catId);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public Result deleteAttrGroups(@RequestBody Long[] ids) {
        attrGroupService.removeBatchByIds(Arrays.asList(ids));
        return Result.ok();
    }

    /**
     * 保存或者更新信息
     */
    @PostMapping("/appendOrUpdate")
    public Result appendOrUpdate(@RequestBody AttrGroup attrGroup) {
        attrGroupService.saveOrUpdate(attrGroup);
        return Result.ok();
    }

    /**
     * 单个信息
     */
    @GetMapping("/{id}")
    public Result attrGroupInfo(@PathVariable("id") Long id) {
        return Result.ok(attrGroupService.getInfoWithCatePath(id));
    }

}
