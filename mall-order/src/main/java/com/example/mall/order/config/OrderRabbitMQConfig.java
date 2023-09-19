package com.example.mall.order.config;

import com.example.mall.common.model.constant.RabbitMQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.example.mall.common.model.constant.RabbitMQConstant.*;
import static com.example.mall.common.model.constant.RabbitMQConstant.Order.*;

@Slf4j
@Configuration
public class OrderRabbitMQConfig {

    /**
     * 订单死信队列
     *
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> argument = new HashMap<>();
        argument.put(DEAD_EXCHANGE_ARGUMENT, EVENT_EXCHANGE_NAME);
        argument.put(DEAD_EXCHANGE_ROUTING_KEY_ARGUMENT, RELEASE_ORDER_ROUTING_KEY);
        argument.put(MESSAGE_TTL_ARGUMENT, MESSAGE_TTL_MS);
        return new Queue(DELAY_QUEUE_NAME, true, false, false, argument);
    }

    /**
     * 死信通往队列
     *
     * @return
     */
    @Bean
    public Queue orderReleaseQueue() {
        return new Queue(RELEASE_QUEUE_NAME, true, false, false);
    }

    /**
     * 交换机
     *
     * @return
     */
    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange(EVENT_EXCHANGE_NAME, true, false);
    }

    /**
     * 绑定延迟队列，订单创建时发送延时消息
     *
     * @return
     */
    @Bean
    public Binding orderCreateOrderBinding() {
        return new Binding(
                DELAY_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                EVENT_EXCHANGE_NAME,
                CREATE_ORDER_ROUTING_KEY,
                null
        );
    }

    /**
     * 绑定死信队列，发送处理未支付订单的消息
     *
     * @return
     */
    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding(
                RELEASE_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                EVENT_EXCHANGE_NAME,
                RELEASE_ORDER_ROUTING_KEY,
                null
        );
    }

        /**
     * 订单取消，通知解锁库存
     * @return
     */
    @Bean
    public Binding orderReleaseStockBinding() {
        return new Binding(
                Ware.RELEASE_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                EVENT_EXCHANGE_NAME,
                RELEASE_STOCK_ROUTING_KEY,
                null
        );
    }
}
