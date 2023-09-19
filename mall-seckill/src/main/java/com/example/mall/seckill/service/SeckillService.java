package com.example.mall.seckill.service;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.seckill.model.vo.SeckillOrderVo;

import java.util.List;

public interface SeckillService {
    void uploadProduct();

    Result<List<SeckillSessionTo.SeckillSkuRelationTo>> startAvailableSeckill();

    Result<SeckillSessionTo.SeckillSkuRelationTo> getSkuSeckillInfo(Long skuId);

    interface Web{
        SeckillOrderVo seckillSku(String seckillId, Integer count, String token);
    }
}
