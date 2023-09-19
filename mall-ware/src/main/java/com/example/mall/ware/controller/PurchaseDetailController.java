package com.example.mall.ware.controller;

import java.util.Arrays;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mall.ware.model.po.PurchaseDetail;
import com.example.mall.ware.service.PurchaseDetailService;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResultUtils;
import com.example.mall.common.model.page.PageResult;


/**
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-06-14 09:47:15
 */
@RestController
@RequestMapping("/ware/purchasedetail")
public class PurchaseDetailController {

    private final PurchaseDetailService purchaseDetailService;

    public PurchaseDetailController(PurchaseDetailService purchaseDetailService) {
        this.purchaseDetailService = purchaseDetailService;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(PageRequestParams params, String wareId, String status) {

        return Result.ok(PageResultUtils.getPage(params
                , purchaseDetailService.getBaseMapper()
                , PurchaseDetail.class
                , purchaseDetailService.getQueryWrapper(wareId, status)));
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        PurchaseDetail purchaseDetail = purchaseDetailService.getById(id);

        return Result.ok(purchaseDetail);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody PurchaseDetail purchaseDetail) {
        purchaseDetailService.save(purchaseDetail);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody PurchaseDetail purchaseDetail) {
        purchaseDetailService.updateById(purchaseDetail);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        purchaseDetailService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
