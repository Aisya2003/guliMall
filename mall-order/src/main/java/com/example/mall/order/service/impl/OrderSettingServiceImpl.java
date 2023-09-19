package com.example.mall.order.service.impl;

import com.example.mall.order.model.po.OrderSetting;
import com.example.mall.order.mapper.OrderSettingMapper;
import com.example.mall.order.service.OrderSettingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class OrderSettingServiceImpl extends ServiceImpl<OrderSettingMapper, OrderSetting> implements OrderSettingService {

}
