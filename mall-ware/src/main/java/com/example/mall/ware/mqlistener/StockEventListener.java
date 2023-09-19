package com.example.mall.ware.mqlistener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.constant.OrderConstant;
import com.example.mall.common.model.constant.RabbitMQConstant;
import com.example.mall.common.model.constant.WareConstant;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.OrderTo;
import com.example.mall.common.model.to.rabbitmq.LockedNotifyTo;
import com.example.mall.ware.feign.OrderFeignClient;
import com.example.mall.ware.model.po.WareOrderTask;
import com.example.mall.ware.model.po.WareOrderTaskDetail;
import com.example.mall.ware.service.WareOrderTaskDetailService;
import com.example.mall.ware.service.WareOrderTaskService;
import com.example.mall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitMQConstant.Ware.RELEASE_QUEUE_NAME)
public class StockEventListener {
    private final WareOrderTaskService wareOrderTaskService;
    private final WareSkuService wareSkuService;
    private final WareOrderTaskDetailService wareOrderTaskDetailService;
    private final OrderFeignClient orderFeignClient;

    @RabbitHandler
    public void releaseLockedStock(LockedNotifyTo lockedNotifyTo, Message message, Channel channel) {
        log.info("收到解锁库存的消息[{}]", message);
        //检查是否存在库存锁定信息
        Long detailId = lockedNotifyTo.getDetail().getId();
        WareOrderTaskDetail wareOrderTaskDetail = wareOrderTaskDetailService.getById(detailId);
        try {
            //锁定库存执行异常，无需执行
            if (wareOrderTaskDetail == null) {
                log.info("锁定服务执行库存异常，无需执行[{}]", message);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            //库存未被锁定
            if (wareOrderTaskDetail.getLockStatus() != WareConstant.WareOrderTaskDetailStatusEnum.LOCKED.getCode()) {
                log.info("库存已被解锁，无需执行[{}]", message);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            Long taskId = wareOrderTaskDetail.getTaskId();
            WareOrderTask wareOrderTask = wareOrderTaskService.getById(taskId);
            //不存在任务信息或者任务处理完成
            if (wareOrderTask == null || wareOrderTask.getTaskStatus() == WareConstant.WareOrderTaskStatusEnum.SUCCESS.getCode()) {
                log.info("不存在订单任务信息或者任务处理完成[{}]", message);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            Result<OrderTo> order = orderFeignClient.getOrderStatus(wareOrderTask.getOrderSn());
            if (order == null || !order.isSuccess()) {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
                throw new RuntimeException("远程服务调用错误！");
            }
            OrderTo orderTo = order.castData(OrderTo.class);
            //订单处于未取消状态，消息需要重新消费
            if (orderTo != null && orderTo.getStatus().intValue() != OrderConstant.OrderStatusEnum.CANCELLED.getCode().intValue()) {
                log.info("订单处于未取消状态，消息需要重新消费[{}]", message);
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
                return;
            }
            //解锁
            wareSkuService.unLockStock(taskId, wareOrderTaskDetail.getSkuId(), wareOrderTaskDetail.getSkuNum(), wareOrderTaskDetail.getWareId());
            //成功执行
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("库存解锁成功[{}]", message);
        } catch (IOException e) {
            throw new RuntimeException("消息确认失败:" + message);
        }
    }

    @RabbitHandler
    public void orderReleaseStock(OrderTo orderTo, Message message, Channel channel) {
        log.info("收到订单服务发送的解锁库存消息[{}]", message);
        try {
            //防止无法预测因素导致解锁库存消息先一步被消费至无法消费状态
            //订单取消成功后会再次发送解锁库存消息
            //查询最新状态
            Result<OrderTo> order = orderFeignClient.getOrderStatus(orderTo.getOrderSn());
            if (order == null || !order.isSuccess()) {
                throw new RuntimeException("远程调用异常");
            }
            orderTo = order.castData(OrderTo.class);
            WareOrderTask wareOrderTask = wareOrderTaskService.getOne(new LambdaQueryWrapper<WareOrderTask>()
                    .eq(WareOrderTask::getOrderSn, orderTo.getOrderSn()));
            if (wareOrderTask == null) {
                log.info("订单服务发送的解锁库存任务不存在，订单号[{}]", orderTo.getOrderSn());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            Long taskId = wareOrderTask.getId();
            List<WareOrderTaskDetail> unLockList = wareOrderTaskDetailService.list(new LambdaQueryWrapper<WareOrderTaskDetail>()
                    .eq(WareOrderTaskDetail::getTaskId, taskId)
                    .eq(WareOrderTaskDetail::getLockStatus, WareConstant.WareOrderTaskDetailStatusEnum.LOCKED.getCode()));
            //解锁库存
            for (WareOrderTaskDetail unLockTask : unLockList) {
                wareSkuService.unLockStock(unLockTask.getTaskId(),
                        unLockTask.getSkuId(),
                        unLockTask.getSkuNum(),
                        unLockTask.getWareId());
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("订单服务请求解锁库存成功[{}]", message);

        } catch (Exception e) {
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ex) {
                log.info("订单服务请求的解锁库存失败[{}]", message);
                throw new RuntimeException("消息确认失败:" + message);
            }

        }
    }
}
