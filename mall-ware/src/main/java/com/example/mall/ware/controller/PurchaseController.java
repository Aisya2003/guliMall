package com.example.mall.ware.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.example.mall.ware.model.dto.FinishPurchaseDto;
import com.example.mall.ware.model.dto.MergeDto;
import org.springframework.web.bind.annotation.*;

import com.example.mall.ware.model.po.Purchase;
import com.example.mall.ware.service.PurchaseService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * 采购信息
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:47:15
 */
@RestController
@RequestMapping("/ware/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping("/unreceive/list")
    public Result unReceiveList(PageRequestParams params) {
        return Result.ok(PageResultUtils.getPage(params
                , purchaseService.getBaseMapper()
                , Purchase.class
                , purchaseService.getUnReceivePurchaseListWrapper()));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params) {
        return Result.ok(PageResultUtils.getPage(params
                , purchaseService.getBaseMapper()
                , Purchase.class));
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        Purchase purchase = purchaseService.getById(id);
        return Result.ok(purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody Purchase purchase) {
        purchase.setCreateTime(LocalDateTime.now());
        purchase.setUpdateTime(LocalDateTime.now());
        purchaseService.save(purchase);
        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Purchase purchase) {
        purchase.setUpdateTime(LocalDateTime.now());
        purchaseService.updateById(purchase);
        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));
        return Result.ok();
    }

    @PostMapping("/merge")
    public Result mergePurchase(@RequestBody MergeDto dto) {
        purchaseService.mergePurchaseList(dto);
        return Result.ok();
    }
    /**
     * 领取采购单
     */
    @PostMapping("/received")
    public Result receivePurchase(@RequestBody List<Long> purchaseIds){
        purchaseService.receivePurchase(purchaseIds);
        return Result.ok();
    }
    /**
     * 完成采购单
     */
    @PostMapping("/done")
    public Result finishPurchase(@RequestBody FinishPurchaseDto dto){
        purchaseService.finishPurchase(dto);
        return Result.ok();
    }
}
