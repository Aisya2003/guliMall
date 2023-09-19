package com.example.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.common.model.constant.RabbitMQConstant;
import com.example.mall.common.model.constant.WareConstant;
import com.example.mall.common.model.exception.LockStockException;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.LockStockTo;
import com.example.mall.common.model.to.SkuHasStockPrefetchTo;
import com.example.mall.common.model.to.SkuHasStockTo;
import com.example.mall.common.model.to.rabbitmq.LockedNotifyTo;
import com.example.mall.ware.feign.ProductFeignClient;
import com.example.mall.ware.model.po.WareOrderTask;
import com.example.mall.ware.model.po.WareOrderTaskDetail;
import com.example.mall.ware.model.po.WareSku;
import com.example.mall.ware.mapper.WareSkuMapper;
import com.example.mall.ware.model.to.WareHasStockTo;
import com.example.mall.ware.service.MqMessageService;
import com.example.mall.ware.service.WareOrderTaskDetailService;
import com.example.mall.ware.service.WareOrderTaskService;
import com.example.mall.ware.service.WareSkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSku> implements WareSkuService {
    private final ProductFeignClient productFeignClient;
    private final RabbitTemplate rabbitTemplate;
    private final WareOrderTaskService wareOrderTaskService;
    private final MqMessageService mqMessageService;
    private final WareOrderTaskDetailService wareOrderTaskDetailService;


    @Override
    public QueryWrapper<WareSku> getQueryWrapper(String skuId, String wareId) {
        Long sId = null;
        Long wId = null;
        if (StringUtils.isBlank(skuId) && StringUtils.isBlank(wareId)) {
            return new QueryWrapper<>();
        }
        try {
            if (StringUtils.isBlank(skuId)) {
                wId = Long.valueOf(wareId);
            } else if (StringUtils.isBlank(wareId)) {
                sId = Long.valueOf(skuId);
            } else {
                wId = Long.valueOf(wareId);
                sId = Long.valueOf(skuId);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("查询参数错误skuId：" + skuId + "wareId:" + wareId);
        }
        return new QueryWrapper<WareSku>()
                .eq(wId != null, WareConstant.WareSkuTableNameEnum.WARE_ID.getTableName(), wId)
                .eq(sId != null, WareConstant.WareSkuTableNameEnum.SKU_ID.getTableName(), sId);
    }

    @Override
    @Transactional
    public void appendStock(Long skuId, Integer skuNum, Long wareId) {
        //查询仓库中是否存在商品
        WareSku wareSku = this.getOne(new LambdaQueryWrapper<WareSku>()
                .eq(WareSku::getSkuId, skuId)
                .eq(WareSku::getWareId, wareId));
        if (wareSku == null) {
            //新增
            WareSku insert = new WareSku();
            insert.setStock(skuNum);
            insert.setSkuId(skuId);
            insert.setWareId(wareId);
            insert.setStockLocked(0);
            Object result = productFeignClient.getInfo(skuId).getData();
            if (result != null) {
                insert.setSkuName(result.toString());
            } else {
                log.info("调用远程服务获取skuName发生异常,skuId[{}]", insert.getId());
            }
            this.save(insert);
        } else {
            //更新
            Integer stock = wareSku.getStock();
            stock += skuNum;
            WareSku update = new WareSku();
            update.setStock(stock);
            this.update(update, new LambdaUpdateWrapper<WareSku>()
                    .eq(WareSku::getSkuId, skuId)
                    .eq(WareSku::getWareId, wareId));
        }
    }

    @Override
    public List<SkuHasStockTo> SkuHasStock(List<Long> skuIds) {
        return skuIds.stream()
                .map(skuId -> {
                    SkuHasStockTo hasStockTo = new SkuHasStockTo();
                    Long totalStock = baseMapper.getStock(skuId);
                    hasStockTo.setSkuId(skuId);
                    hasStockTo.setHasStock(totalStock != null && totalStock > 0);
                    return hasStockTo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SkuHasStockTo> skuHasStockPrefetch(List<SkuHasStockPrefetchTo> skuHasStockPrefetchTos) {
        return skuHasStockPrefetchTos.stream()
                .map(skuHasStockPrefetchTo -> {
                    SkuHasStockTo skuHasStockTo = new SkuHasStockTo();
                    Long skuId = skuHasStockPrefetchTo.getSkuId();
                    skuHasStockTo.setSkuId(skuId);
                    Long totalStock = baseMapper.getStock(skuId);
                    skuHasStockTo.setHasStock(totalStock != null && totalStock >= skuHasStockPrefetchTo.getPrefetch());
                    return skuHasStockTo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = LockStockException.class)
    public Result lockStock(LockStockTo lockStockTo) {
        //记录任务
        WareOrderTask wareOrderTask = saveWareOrderTask(lockStockTo);
        //查询仓库中是否存在库存
        List<LockStockTo.LockInfo> lockList = lockStockTo.getLockList();
        if (lockList == null || lockList.isEmpty()) {
            return Result.fail();
        }
        List<WareHasStockTo> hasStockList = lockList.stream()
                .map(lockInfo -> {
                    WareHasStockTo wareHasStockTo = new WareHasStockTo();
                    wareHasStockTo.setSkuId(lockInfo.getSkuId());
                    wareHasStockTo.setCount(lockInfo.getCount());
                    //查询库存
                    wareHasStockTo.setWareIdList(this.wareHasStock(lockInfo.getSkuId()));
                    return wareHasStockTo;
                })
                .collect(Collectors.toList());
        if (hasStockList.isEmpty()) {
            return Result.fail();
        }
        //锁定库存
        boolean success = false;
        for (WareHasStockTo wareHasStockTo : hasStockList) {
            List<Long> wareIdList = wareHasStockTo.getWareIdList();
            if (wareIdList == null || wareIdList.isEmpty()) {
                return Result.fail();
            }
            for (Long wareId : wareIdList) {
                //尝试锁定
                Long skuId = wareHasStockTo.getSkuId();
                Integer count = wareHasStockTo.getCount();
                success = this.lockStockWithCount(
                        wareId,
                        skuId,
                        count);
                if (success) {
                    //保存任务详情
                    WareOrderTaskDetail wareOrderTaskDetail = WareOrderTaskDetail.builder()
                            .id(null)
                            .skuId(skuId)
                            .wareId(wareId)
                            .skuName(null)
                            .skuNum(count)
                            .taskId(wareOrderTask.getId())
                            .lockStatus(WareConstant.WareOrderTaskDetailStatusEnum.LOCKED.getCode())
                            .build();
                    wareOrderTaskDetailService.save(wareOrderTaskDetail);
                    //发送成功锁定库存的消息
                    LockedNotifyTo.LockDetailTo detail = new LockedNotifyTo.LockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetail, detail);
                    LockedNotifyTo notifyTo = LockedNotifyTo.builder()
                            .taskId(wareOrderTask.getId())
                            .detail(detail)
                            .build();
                    try {

                        rabbitTemplate.convertAndSend(
                                RabbitMQConstant.Ware.EVENT_EXCHANGE_NAME,
                                RabbitMQConstant.Ware.LOCKED_ROUTING_KEY,
                                notifyTo,
                                new CorrelationData(RabbitMQConstant.Ware.CORRELATION_ID_LOCKED_PREFIX + detail.getId())
                        );
                    } catch (Exception e) {
                        log.error("消息发送失败[{}]", e.getMessage());
                        mqMessageService.saveSendFailMessage(notifyTo,
                                RabbitMQConstant.Order.EVENT_EXCHANGE_NAME,
                                RabbitMQConstant.Order.CREATE_ORDER_ROUTING_KEY);
                    }
                    log.info("发送锁定库存成功消息[{}]", RabbitMQConstant.Ware.CORRELATION_ID_LOCKED_PREFIX + detail.getId());
                    break;
                }
            }
            //所有仓库锁定失败
            if (!success) {
                LockStockException.error("锁定库存失败");
            }
        }
        return Result.ok();
    }

    private WareOrderTask saveWareOrderTask(LockStockTo lockStockTo) {
        //保存任务
        WareOrderTask wareOrderTask = new WareOrderTask();
        wareOrderTask.setOrderSn(lockStockTo.getOrderSn());
        wareOrderTask.setCreateTime(LocalDateTime.now());
        wareOrderTask.setTaskStatus(WareConstant.WareOrderTaskStatusEnum.CREATE.getCode());
        wareOrderTaskService.save(wareOrderTask);
        return wareOrderTask;
    }

    /**
     * 判断库存是否充足
     *
     * @param wareId
     * @param skuId
     * @param count
     * @return
     */
    private Boolean lockStockWithCount(Long wareId, Long skuId, Integer count) {
        WareSku wareSku = this.getOne(new LambdaQueryWrapper<WareSku>()
                .eq(WareSku::getSkuId, skuId)
                .eq(WareSku::getWareId, wareId));
        if (wareSku != null) {
            int canLockCount = wareSku.getStock() - wareSku.getStockLocked();
            if (canLockCount < count) {
                return false;
            }
            WareSku updateWareSku = new WareSku();
            updateWareSku.setId(wareSku.getId());
            updateWareSku.setStockLocked(wareSku.getStockLocked() + count);
            return this.updateById(updateWareSku);
        }
        return false;
    }

    @Override
    public List<Long> wareHasStock(Long skuId) {
        List<WareSku> wareSkus = this.list(new LambdaQueryWrapper<WareSku>()
                .eq(WareSku::getSkuId, skuId));
        return wareSkus.stream()
                .filter(wareSku -> wareSku.getStock() - wareSku.getStockLocked() > 0)
                .map(WareSku::getWareId)
                .collect(Collectors.toList());
    }

    @Override
    public void unLockStock(Long taskId, Long skuId, Integer skuNum, Long wareId) {
        //解锁
        this.baseMapper.unlockStock(skuId, skuNum, wareId);
        //更新状态
        wareOrderTaskDetailService.updateStatusBySkuIdAndTaskId(taskId, skuId);
    }

}
