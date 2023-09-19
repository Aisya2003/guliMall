package com.example.mall.order.service.impl;

import com.example.mall.order.model.po.OrderItem;
import com.example.mall.order.mapper.OrderItemMapper;
import com.example.mall.order.service.OrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

}
