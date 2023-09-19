package com.example.mall.ware.controller;

import java.util.Arrays;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.ware.model.po.UndoLog;
import com.example.mall.ware.service.UndoLogService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:47:15
 */
@RestController
@RequestMapping("/ware/undolog")
public class UndoLogController {

    private final UndoLogService undoLogService;

    public UndoLogController(UndoLogService undoLogService) {
        this.undoLogService = undoLogService;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params){

        return Result.ok( PageResultUtils.getPage(params, undoLogService.getBaseMapper(), UndoLog.class));
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
		UndoLog undoLog = undoLogService.getById(id);

        return Result.ok(undoLog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody UndoLog undoLog){
		undoLogService.save(undoLog);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody UndoLog undoLog){
		undoLogService.updateById(undoLog);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
		undoLogService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
