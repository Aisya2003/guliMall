package com.example.mall.member.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.member.model.po.GrowthChangeHistory;
import com.example.mall.member.service.GrowthChangeHistoryService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 成长值变化历史记录
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:05:58
 */
@RestController
@RequestMapping("/member/growthchangehistory")
public class GrowthChangeHistoryController {
    @Autowired
    private GrowthChangeHistoryService growthChangeHistoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params){
        PageResult<GrowthChangeHistory> result = PageResultUtils.getPage(params, growthChangeHistoryService.getBaseMapper(), GrowthChangeHistory.class);
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
		GrowthChangeHistory growthChangeHistory = growthChangeHistoryService.getById(id);

        return Result.ok(growthChangeHistory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody GrowthChangeHistory growthChangeHistory){
		growthChangeHistoryService.save(growthChangeHistory);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody GrowthChangeHistory growthChangeHistory){
		growthChangeHistoryService.updateById(growthChangeHistory);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
		growthChangeHistoryService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
