package com.example.mall.order.mqlistener;

import com.example.mall.common.model.constant.RabbitMQConstant;
import com.example.mall.common.model.to.OrderTo;
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

import static com.example.mall.common.model.constant.RabbitMQConstant.Order.RELEASE_QUEUE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = RELEASE_QUEUE_NAME)
public class OrderEventOrderListener {
    private final OrderService orderService;
    private final MqMessageService mqMessageService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitHandler
    public void handleCancelOrder(Order order, Message message, Channel channel) {
        log.info("收到取消订单消息[{}]", message);
        try {
            orderService.cancelOrder(order);
            orderService.closePayTrade(order.getOrderSn());
            //发送解锁库存消息
            this.sendUnlockStockMessage(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("成功取消订单[{}]", message);
        } catch (Exception e) {
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ex) {
                log.info("取消订单失败[{}]", message);
                throw new RuntimeException("消息确认失败:" + message);
            }
        }
    }

    @RabbitHandler
    public void handleCancelSeckillOrder(SeckillOrder seckillOrder, Message message, Channel channel) {
        try {
            orderService.cancelSeckillOrder(seckillOrder);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (Exception ex) {
                log.info("取消秒杀订单失败[{}]", message);
                throw new RuntimeException(ex);
            }
        }
    }

    private void sendUnlockStockMessage(Order order) {
        if (order == null) {
            throw new RuntimeException("取消的订单不存在");
        }
        OrderTo orderTo = new OrderTo();
        BeanUtils.copyProperties(order, orderTo);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstant.Order.EVENT_EXCHANGE_NAME,
                    RabbitMQConstant.Order.RELEASE_STOCK_ROUTING_KEY,
                    orderTo,
                    new CorrelationData(RabbitMQConstant.Order.CORRELATION_ID_RELEASE_STOCK_PREFIX + order.getId())
            );
        } catch (Exception e) {
            log.error("消息发送失败[{}]", e.getMessage());
            mqMessageService.saveSendFailMessage(order,
                    RabbitMQConstant.Order.EVENT_EXCHANGE_NAME,
                    RabbitMQConstant.Order.CREATE_ORDER_ROUTING_KEY);
        }
        log.info("订单取消成功，发送解锁库存消息成功[{}]", RabbitMQConstant.Order.CORRELATION_ID_RELEASE_STOCK_PREFIX + order.getId());
    }
}
