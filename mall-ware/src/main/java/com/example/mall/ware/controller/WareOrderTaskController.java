package com.example.mall.ware.controller;

import java.util.Arrays;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.ware.model.po.WareOrderTask;
import com.example.mall.ware.service.WareOrderTaskService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 库存工作单
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:47:15
 */
@RestController
@RequestMapping("/ware/wareordertask")
public class WareOrderTaskController {

    private final WareOrderTaskService wareOrderTaskService;

    public WareOrderTaskController(WareOrderTaskService wareOrderTaskService) {
        this.wareOrderTaskService = wareOrderTaskService;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params){
        return Result.ok(PageResultUtils.getPage(params, wareOrderTaskService.getBaseMapper(), WareOrderTask.class));
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
		WareOrderTask wareOrderTask = wareOrderTaskService.getById(id);

        return Result.ok(wareOrderTask);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody WareOrderTask wareOrderTask){
		wareOrderTaskService.save(wareOrderTask);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody WareOrderTask wareOrderTask){
		wareOrderTaskService.updateById(wareOrderTask);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
		wareOrderTaskService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
