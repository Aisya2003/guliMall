package com.example.mall.seckill.config;

import com.example.mall.common.model.constant.RabbitMQConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.mall.common.model.constant.RabbitMQConstant.Seckill.*;

@Configuration
public class SeckillMQConfig {
    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange(EVENT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue orderSeckillOrderQueue() {
        return new Queue(SECKILL_ORDER_QUEUE_NAME, true, false, false);
    }

    @Bean
    public Binding seckillOrderBinding() {
        return new Binding(
                SECKILL_ORDER_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                EVENT_EXCHANGE_NAME,
                SECKILL_ORDER_QUEUE_ROUTING_KEY,
                null
        );
    }
}
