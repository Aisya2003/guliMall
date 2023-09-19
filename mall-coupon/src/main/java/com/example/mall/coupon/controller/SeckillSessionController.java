package com.example.mall.coupon.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.example.mall.common.model.to.SeckillSessionTo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.mall.coupon.model.po.SeckillSession;
import com.example.mall.coupon.service.SeckillSessionService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 秒杀活动场次
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:46:19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon/seckillsession")
public class SeckillSessionController {
    private final SeckillSessionService seckillSessionService;

    @GetMapping("/inner/3DaySeckillSku")
    public Result<List<SeckillSessionTo>> latest3DaySku() {
        List<SeckillSessionTo> seckillSessionToList = seckillSessionService.latest3DaySkuList();
        return Result.ok(seckillSessionToList);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params) {
        PageResult<SeckillSession> result = PageResultUtils.getPage(params, seckillSessionService.getBaseMapper(), SeckillSession.class);
        return Result.ok(result);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        SeckillSession seckillSession = seckillSessionService.getById(id);

        return Result.ok(seckillSession);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody SeckillSession seckillSession) {
        seckillSession.setCreateTime(LocalDateTime.now());
        seckillSessionService.save(seckillSession);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody SeckillSession seckillSession) {
        seckillSessionService.updateById(seckillSession);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        seckillSessionService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
