package com.example.mall.coupon.controller;

import java.time.LocalDateTime;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.coupon.model.po.SeckillPromotion;
import com.example.mall.coupon.service.SeckillPromotionService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 秒杀活动
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:46:19
 */
@RestController
@RequestMapping("/coupon/seckillpromotion")
@RequiredArgsConstructor
public class SeckillPromotionController {
    private final SeckillPromotionService seckillPromotionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params) {
        PageResult<SeckillPromotion> result = PageResultUtils.getPage(params, seckillPromotionService.getBaseMapper(), SeckillPromotion.class);
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        SeckillPromotion seckillPromotion = seckillPromotionService.getById(id);

        return Result.ok(seckillPromotion);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody SeckillPromotion seckillPromotion) {
        seckillPromotion.setCreateTime(LocalDateTime.now());
        seckillPromotionService.save(seckillPromotion);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody SeckillPromotion seckillPromotion) {
        seckillPromotionService.updateById(seckillPromotion);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        seckillPromotionService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
