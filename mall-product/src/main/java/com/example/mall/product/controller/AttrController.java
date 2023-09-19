package com.example.mall.product.controller;

import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.result.Result;
import com.example.mall.product.model.dto.AttrRequestDto;
import com.example.mall.product.model.dto.AttrResponseDto;
import com.example.mall.product.model.po.ProductAttrValue;
import com.example.mall.product.service.AttrService;
import com.example.mall.product.service.ProductAttrValueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product/attr")
public class AttrController {
    private final AttrService attrService;
    private final ProductAttrValueService productAttrValueService;

    public AttrController(AttrService attrService, ProductAttrValueService productAttrValueService) {
        this.attrService = attrService;
        this.productAttrValueService = productAttrValueService;
    }

    /**
     * 保存关联信息
     *
     * @param attrRequestDto
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody AttrRequestDto attrRequestDto) {
        attrService.saveRelation(attrRequestDto);
        return Result.ok();
    }

    /**
     * 查询分类下的所有属性
     *
     * @param attrType
     * @param params
     * @param catlogId
     * @return
     */
    @GetMapping("/{attrType}/list/{catlogId}")
    public Result attrList(@PathVariable("attrType") String attrType,
                           PageRequestParams params,
                           @PathVariable("catlogId") Long catlogId) {
        PageResult<AttrResponseDto> result = attrService.selectList(catlogId, params, attrType);
        return Result.ok(result);
    }

    /**
     * 查询单个属性的信息
     *
     * @param attrId
     * @return
     */
    @GetMapping("/info/{attrId}")
    public Result attrInfo(@PathVariable("attrId") Long attrId) {
        AttrResponseDto dto = attrService.selectWithPath(attrId);
        return Result.ok(dto);
    }

    /**
     * 获取属性名
     * @param attrId
     * @return
     */
    @GetMapping("/inner/info/{attrId}")
    public String attrName(@PathVariable("attrId") Long attrId) {
        return attrService.getById(attrId).getAttrName();
    }

    /**
     * 更新属性
     *
     * @param attrRequestDto
     * @return
     */
    @PostMapping("/update")
    public Result updateInfo(@RequestBody AttrRequestDto attrRequestDto) {
        attrService.updateRelation(attrRequestDto);
        return Result.ok();
    }

    @PostMapping("/update/{spuId}")
    public Result updateInfoById(@PathVariable("spuId") Long spuId,
                                 @RequestBody List<ProductAttrValue> attrValueList) {
        productAttrValueService.updateList(spuId, attrValueList);
        return Result.ok();
    }

    @PostMapping("/delete")
    public Result removeAttr(@RequestBody Long[] ids) {
        attrService.removeBatchCascadeRelation(ids);
        return Result.ok();
    }

    @GetMapping("/base/listforspu/{spuId}")
    public Result getBaseSpuList(@PathVariable("spuId") Long spuId) {
        return Result.ok(productAttrValueService.getBaseSpuList(spuId));
    }
}
