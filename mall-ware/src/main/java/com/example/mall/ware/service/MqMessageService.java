package com.example.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.common.model.to.rabbitmq.LockedNotifyTo;
import com.example.mall.ware.model.po.MqMessage;


/**
 * 
 *
 * @author zhuwenjie
 * @email 1842929189@qq.com
 * @date 2023-08-01 08:41:39
 */
public interface MqMessageService extends IService<MqMessage> {
    void saveSendFailMessage(LockedNotifyTo order, String exchangeName, String routingKey);

}

