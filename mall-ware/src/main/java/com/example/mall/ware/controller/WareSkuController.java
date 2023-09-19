package com.example.mall.ware.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.example.mall.common.model.to.LockStockTo;
import com.example.mall.common.model.to.SkuHasStockPrefetchTo;
import com.example.mall.common.model.to.SkuHasStockTo;
import org.springframework.web.bind.annotation.*;

import com.example.mall.ware.model.po.WareSku;
import com.example.mall.ware.service.WareSkuService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;


/**
 * 商品库存
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:47:15
 */
@RestController
@RequestMapping("/ware/waresku")
public class WareSkuController {

    private final WareSkuService wareSkuService;

    public WareSkuController(WareSkuService wareSkuService) {
        this.wareSkuService = wareSkuService;
    }

    /**
     * 订单提交锁定库存
     * @param lockStockTo
     * @return
     */
    @PostMapping("/order/lock")
    public Result lockStock(@RequestBody LockStockTo lockStockTo) {
        Result result = null;
        try {
            result = wareSkuService.lockStock(lockStockTo);
        } catch (Exception e) {
            return Result.fail();
        }
        return result;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params, String skuId, String wareId) {
        return Result.ok(PageResultUtils.getPage(params
                , wareSkuService.getBaseMapper()
                , WareSku.class
                , wareSkuService.getQueryWrapper(skuId, wareId)));
    }

    @PostMapping("/inner/hasStock")
    public List<SkuHasStockTo> hasStock(@RequestBody List<Long> skuIds) {
        return wareSkuService.SkuHasStock(skuIds);
    }

    @PostMapping("/inner/hasStock/prefetch")
    public List<SkuHasStockTo> hasStockWithPrefetch(@RequestBody List<SkuHasStockPrefetchTo> skuHasStockPrefetchTos) {
        return wareSkuService.skuHasStockPrefetch(skuHasStockPrefetchTos);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        WareSku wareSku = wareSkuService.getById(id);
        return Result.ok(wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody WareSku wareSku) {
        wareSkuService.save(wareSku);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody WareSku wareSku) {
        wareSkuService.updateById(wareSku);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
