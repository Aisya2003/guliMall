package com.example.mall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.example.mall.common.model.constant.RabbitMQConstant.*;
import static com.example.mall.common.model.constant.RabbitMQConstant.Ware.*;

@Configuration
public class WareRabbitMQConfig {
    /**
     * 交换机
     */
    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange(
                EVENT_EXCHANGE_NAME, true, false);
    }

    /**
     * 库存死信通往队列
     */
    @Bean
    public Queue stockReleaseQueue() {
        return new Queue(RELEASE_QUEUE_NAME, true, false, false);
    }

    /**
     * 死信绑定队列
     */
    @Bean
    public Queue stockDelayQueue() {
        Map<String, Object> argument = new HashMap<>();
        argument.put(DEAD_EXCHANGE_ARGUMENT, EVENT_EXCHANGE_NAME);
        argument.put(MESSAGE_TTL_ARGUMENT, MESSAGE_TTL_MS);
        argument.put(DEAD_EXCHANGE_ROUTING_KEY_ARGUMENT, DEAD_EXCHANGE_ROUTING_KEY);
        return new Queue(DELAY_QUEUE_NAME, true, false, false, argument);
    }

    /**
     * 库存解锁队列
     *
     * @return
     */
    @Bean
    public Binding stockReleaseBinding() {
        return new Binding(
                RELEASE_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                EVENT_EXCHANGE_NAME,
                RELEASE_ROUTING_KEY,
                null
        );
    }

    /**
     * 库存成功锁定队列
     *
     * @return
     */
    @Bean
    public Binding stockLockedBinding() {
        return new Binding(
                DELAY_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                EVENT_EXCHANGE_NAME,
                LOCKED_BINDING_ROUTING_KEY,
                null
        );
    }

    /**
     * 来自订单服务的解锁库存绑定
     */
    @Bean
    public Binding orderUnlockStockBinding() {
        return new Binding(
                RELEASE_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                EVENT_EXCHANGE_NAME,
                Order.RELEASE_STOCK_ROUTING_KEY,
                null
        );
    }
}
