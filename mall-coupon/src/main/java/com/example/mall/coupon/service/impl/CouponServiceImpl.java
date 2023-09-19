package com.example.mall.coupon.service.impl;

import com.example.mall.coupon.model.po.Coupon;
import com.example.mall.coupon.mapper.CouponMapper;
import com.example.mall.coupon.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

}
