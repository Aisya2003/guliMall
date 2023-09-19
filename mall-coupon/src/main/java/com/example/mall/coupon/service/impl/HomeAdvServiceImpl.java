package com.example.mall.coupon.service.impl;

import com.example.mall.coupon.model.po.HomeAdv;
import com.example.mall.coupon.mapper.HomeAdvMapper;
import com.example.mall.coupon.service.HomeAdvService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class HomeAdvServiceImpl extends ServiceImpl<HomeAdvMapper, HomeAdv> implements HomeAdvService {

}
