package com.example.mall.ware.controller;

import java.util.Arrays;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.ware.model.po.WareInfo;
import com.example.mall.ware.service.WareInfoService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 仓库信息
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:47:15
 */
@RestController
@RequestMapping("/ware/wareinfo")
public class WareInfoController {
    private final WareInfoService wareInfoService;

    public WareInfoController(WareInfoService wareInfoService) {
        this.wareInfoService = wareInfoService;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params){

        return Result.ok(PageResultUtils.getPage(params, wareInfoService.getBaseMapper(), WareInfo.class));
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
		WareInfo wareInfo = wareInfoService.getById(id);

        return Result.ok(wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody WareInfo wareInfo){
		wareInfoService.save(wareInfo);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody WareInfo wareInfo){
		wareInfoService.updateById(wareInfo);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
		wareInfoService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
