package com.example.mall.order.service.impl;

import com.example.mall.order.model.po.OrderOperateHistory;
import com.example.mall.order.mapper.OrderOperateHistoryMapper;
import com.example.mall.order.service.OrderOperateHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class OrderOperateHistoryServiceImpl extends ServiceImpl<OrderOperateHistoryMapper, OrderOperateHistory> implements OrderOperateHistoryService {

}
