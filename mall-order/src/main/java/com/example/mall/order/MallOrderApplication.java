package com.example.mall.order;

import com.example.mall.common.config.*;
import com.example.mall.order.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootApplication
@MapperScan("com.example.mall.order.mapper")
@EnableFeignClients(basePackages = "com.example.mall.order.feign")
@Import({
        SpringSessionConfig.class,
        RabbitMQConfig.class,
        CustomThreadPoolConfiguration.class,
        LocalDateTimeConfig.class,
        FeignRequestInterceptorConfig.class,
        RedissonConfig.class,
        SentinelBlockUrlConfig.class
})
public class MallOrderApplication implements ApplicationContextAware {

    public static void main(String[] args) {
        SpringApplication.run(MallOrderApplication.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean success, String cause) {
                if (correlationData == null) {
                    log.info("收到未封装的消息");
                    return;
                }
                if (success) {
                    log.info("消息[{}]成功发送到Exchange中", correlationData.getId());
                } else {
                    log.info("消息[{}]投递到Exchange失败,原因：[{}]", correlationData.getId(), cause);
                }
            }
        });
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                Message message = returnedMessage.getMessage();
                int replyCode = returnedMessage.getReplyCode();
                String exchange = returnedMessage.getExchange();
                String replyText = returnedMessage.getReplyText();
                String routingKey = returnedMessage.getRoutingKey();
                log.error("消息被退回：replyCode[{}],退回原因[{}],目标交换机[{}],routingKey[{}],Message[{}]", replyCode, replyText, exchange, routingKey, message);
            }
        });
    }
}
