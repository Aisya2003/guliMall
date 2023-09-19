package com.example.mall.coupon.service;

import com.example.mall.common.model.to.SeckillSessionTo;
import com.example.mall.coupon.model.po.SeckillSession;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 秒杀活动场次 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface SeckillSessionService extends IService<SeckillSession> {

    List<SeckillSessionTo> latest3DaySkuList();
}
