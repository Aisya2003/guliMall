package com.example.mall.order.mqlistener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.constant.OrderConstant;
import com.example.mall.common.model.constant.RabbitMQConstant;
import com.example.mall.common.model.to.OrderTo;
import com.example.mall.common.model.to.rabbitmq.SeckillNotifyTo;
import com.example.mall.order.model.po.Order;
import com.example.mall.order.model.to.SeckillOrder;
import com.example.mall.order.service.MqMessageService;
import com.example.mall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitMQConstant.Seckill.SECKILL_ORDER_QUEUE_NAME)
public class OrderEventSeckillListener {
    private final OrderService orderService;
    private final RabbitTemplate rabbitTemplate;
    private final MqMessageService mqMessageService;

    @RabbitHandler
    public void handleSeckillOrder(SeckillNotifyTo to, Message message, Channel channel) {
        log.info("收到秒杀订单的消息");
        try {
            SeckillOrder seckillOrder = orderService.createSeckillOrder(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            this.sendCreatSeckillOrderMessage(seckillOrder);
            log.info("发送秒杀订单创建消息");
        } catch (IOException e) {
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ex) {
                log.info("取消订单失败[{}]", message);
                throw new RuntimeException("消息确认失败:" + message);
            }
        }
    }

    private void sendCreatSeckillOrderMessage(SeckillOrder seckillOrder) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstant.Order.EVENT_EXCHANGE_NAME,
                    RabbitMQConstant.Order.CREATE_ORDER_ROUTING_KEY,
                    seckillOrder,
                    new CorrelationData(RabbitMQConstant.Order.CORRELATION_ID_CREATE_PREFIX + "seckill-" +  + seckillOrder.getOrder().getId())
            );
        } catch (Exception e) {
            log.error("消息发送失败[{}]", e.getMessage());
            mqMessageService.saveSendFailMessage(seckillOrder.getOrder(),
                    RabbitMQConstant.Order.EVENT_EXCHANGE_NAME,
                    RabbitMQConstant.Order.CREATE_ORDER_ROUTING_KEY);
        }
    }
}
