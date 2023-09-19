package com.example.mall.ware.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.LockStockTo;
import com.example.mall.common.model.to.SkuHasStockPrefetchTo;
import com.example.mall.common.model.to.SkuHasStockTo;
import com.example.mall.ware.model.po.WareSku;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 商品库存 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface WareSkuService extends IService<WareSku> {

    QueryWrapper<WareSku> getQueryWrapper(String skuId, String wareId);

    /**
     * 入库
     *
     * @param skuId  商品id
     * @param skuNum 商品成功数量
     * @param wareId 仓库id
     */
    void appendStock(Long skuId, Integer skuNum, Long wareId);

    List<SkuHasStockTo> SkuHasStock(List<Long> skuIds);

    List<SkuHasStockTo> skuHasStockPrefetch(List<SkuHasStockPrefetchTo> skuHasStockPrefetchTos);

    Result lockStock(LockStockTo lockStockTo);

    List<Long> wareHasStock(Long skuId);

    void unLockStock(Long taskId, Long skuId, Integer skuNum, Long wareId);
}
