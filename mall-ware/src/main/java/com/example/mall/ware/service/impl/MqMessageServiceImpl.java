package com.example.mall.ware.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.constant.RabbitMQConstant;
import com.example.mall.common.model.to.rabbitmq.LockedNotifyTo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.mall.ware.mapper.MqMessageMapper;
import com.example.mall.ware.model.po.MqMessage;
import com.example.mall.ware.service.MqMessageService;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {
    private final ScheduledExecutorService scheduledExecutorService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void saveSendFailMessage(LockedNotifyTo lockedNotifyTo, String exchangeName, String routingKey) {
        saveMessage(lockedNotifyTo, exchangeName, routingKey, RabbitMQConstant.MqMessageStatusEnum.CREATE.getCode());
    }

    private void saveMessage(LockedNotifyTo lockedNotifyTo, String exchangeName, String routingKey, Integer errorCode) {
        MqMessage mqMessage = new MqMessage();
        mqMessage.setClassType(lockedNotifyTo.getClass().toString());
        mqMessage.setCreatTime(LocalDateTime.now());
        mqMessage.setUpdateTime(LocalDateTime.now());
        mqMessage.setContent(JSON.toJSONString(lockedNotifyTo));
        mqMessage.setRoutingKey(routingKey);
        mqMessage.setToExchange(exchangeName);
        mqMessage.setMessageStatus(errorCode);
        this.save(mqMessage);
        log.info("消息成功保存到数据库中[{}]", mqMessage);

        scheduledExecutorService.schedule(resendMessage(),
                RabbitMQConstant.SEND_FAIL_DELAY_SECOND,
                TimeUnit.SECONDS
        );
    }

    private Runnable resendMessage() {
        AtomicInteger failCount = new AtomicInteger();
        return () -> {
            while (true) {
                if (failCount.get() >= 3) {
                    //等待下次发送失败执行
                    return;
                }
                List<MqMessage> messageList = this.list(new LambdaQueryWrapper<MqMessage>()
                        .ne(MqMessage::getMessageStatus, RabbitMQConstant.MqMessageStatusEnum.ARRIVE.getCode()));
                if (messageList == null) {
                    log.info("发送失败的消息已全部处理完成");
                    return;
                }
                for (MqMessage message : messageList) {
                    try {
                        Class<?> messageClass = Class.forName(message.getClassType(), false, null);
                        Object o = JSON.parseObject(message.getContent(), messageClass);
                        String id = RabbitMQConstant.RESEND_CORRELATION_ID_PREFIX + UUID.randomUUID();
                        try {
                            rabbitTemplate.convertAndSend(
                                    message.getToExchange(),
                                    message.getRoutingKey(),
                                    o,
                                    new CorrelationData(id)
                            );
                            log.info("失败的消息成功发送[{}]", id);
                            break;
                        } catch (Exception e) {
                            failCount.addAndGet(1);
                            this.wait(1000);
                        }
                    } catch (ClassNotFoundException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
    }


}