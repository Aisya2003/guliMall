package com.example.mall.coupon.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.coupon.model.po.CouponSpuRelation;
import com.example.mall.coupon.service.CouponSpuRelationService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 优惠券与产品关联
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:46:19
 */
@RestController
@RequestMapping("/coupon/couponspurelation")
public class CouponSpuRelationController {
    @Autowired
    private CouponSpuRelationService couponSpuRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params){
        PageResult<CouponSpuRelation> result = PageResultUtils.getPage(params, couponSpuRelationService.getBaseMapper(), CouponSpuRelation.class);
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
		CouponSpuRelation couponSpuRelation = couponSpuRelationService.getById(id);

        return Result.ok(couponSpuRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody CouponSpuRelation couponSpuRelation){
		couponSpuRelationService.save(couponSpuRelation);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody CouponSpuRelation couponSpuRelation){
		couponSpuRelationService.updateById(couponSpuRelation);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
		couponSpuRelationService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
