package com.example.mall.member.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.member.model.po.IntegrationChangeHistory;
import com.example.mall.member.service.IntegrationChangeHistoryService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 积分变化历史记录
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:05:58
 */
@RestController
@RequestMapping("/member/integrationchangehistory")
public class IntegrationChangeHistoryController {
    @Autowired
    private IntegrationChangeHistoryService integrationChangeHistoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params){
        PageResult<IntegrationChangeHistory> result = PageResultUtils.getPage(params, integrationChangeHistoryService.getBaseMapper(), IntegrationChangeHistory.class);
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
		IntegrationChangeHistory integrationChangeHistory = integrationChangeHistoryService.getById(id);

        return Result.ok(integrationChangeHistory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody IntegrationChangeHistory integrationChangeHistory){
		integrationChangeHistoryService.save(integrationChangeHistory);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody IntegrationChangeHistory integrationChangeHistory){
		integrationChangeHistoryService.updateById(integrationChangeHistory);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
		integrationChangeHistoryService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
