package com.example.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.order.model.po.MqMessage;
import com.example.mall.order.model.po.Order;


/**
 * 
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-08-01 08:41:39
 */
public interface MqMessageService extends IService<MqMessage> {


    void saveSendFailMessage(Order order, String exchangeName, String routingKey);
}

