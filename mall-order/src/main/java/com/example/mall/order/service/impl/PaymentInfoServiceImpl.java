package com.example.mall.order.service.impl;

import com.example.mall.order.model.po.PaymentInfo;
import com.example.mall.order.mapper.PaymentInfoMapper;
import com.example.mall.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

}
