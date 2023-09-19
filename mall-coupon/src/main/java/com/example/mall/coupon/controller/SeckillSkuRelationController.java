package com.example.mall.coupon.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.common.model.constant.CouponConstant;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.result.Result;
import com.example.mall.coupon.model.po.SeckillSession;
import com.example.mall.coupon.model.po.SeckillSkuRelation;
import com.example.mall.coupon.service.SeckillSkuRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon/seckillskurelation")
public class SeckillSkuRelationController {
    private final SeckillSkuRelationService seckillSkuRelationService;
    @RequestMapping("/list")
    public Result list(PageRequestParams params, @RequestParam("promotionSessionId") String promotionSessionId){
        PageResult<SeckillSkuRelation> result = PageResultUtils.getPage(params,
                seckillSkuRelationService.getBaseMapper(),
                SeckillSkuRelation.class,
                new QueryWrapper<SeckillSkuRelation>()
                        .eq(StringUtils.isNotBlank(promotionSessionId), CouponConstant.SeckillSkuRelationTableField.PROMOTION_SESSION_ID,promotionSessionId));
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        SeckillSkuRelation seckillSession = seckillSkuRelationService.getById(id);

        return Result.ok(seckillSession);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody SeckillSkuRelation seckillSession){
        seckillSkuRelationService.save(seckillSession);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody SeckillSkuRelation seckillSession){
        seckillSkuRelationService.updateById(seckillSession);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        seckillSkuRelationService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }
}
