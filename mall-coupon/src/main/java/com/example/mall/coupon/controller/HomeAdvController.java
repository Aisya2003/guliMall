package com.example.mall.coupon.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.coupon.model.po.HomeAdv;
import com.example.mall.coupon.service.HomeAdvService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 首页轮播广告
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:46:19
 */
@RestController
@RequestMapping("/coupon/homeadv")
public class HomeAdvController {
    @Autowired
    private HomeAdvService homeAdvService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params){
        PageResult<HomeAdv> result = PageResultUtils.getPage(params, homeAdvService.getBaseMapper(), HomeAdv.class);
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
		HomeAdv homeAdv = homeAdvService.getById(id);

        return Result.ok(homeAdv);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody HomeAdv homeAdv){
		homeAdvService.save(homeAdv);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody HomeAdv homeAdv){
		homeAdvService.updateById(homeAdv);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
		homeAdvService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
