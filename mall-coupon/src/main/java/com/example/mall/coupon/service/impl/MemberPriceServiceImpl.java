package com.example.mall.coupon.service.impl;

import com.example.mall.coupon.model.po.MemberPrice;
import com.example.mall.coupon.mapper.MemberPriceMapper;
import com.example.mall.coupon.service.MemberPriceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class MemberPriceServiceImpl extends ServiceImpl<MemberPriceMapper, MemberPrice> implements MemberPriceService {

}
