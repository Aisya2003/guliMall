package com.example.mall.order.service;

import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.OrderAndOrderItemTo;
import com.example.mall.common.model.to.OrderTo;
import com.example.mall.common.model.to.rabbitmq.SeckillNotifyTo;
import com.example.mall.order.model.po.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.order.model.to.SeckillOrder;
import com.example.mall.order.model.vo.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author zhuwenjie
 * @since 2023-06-07
 */
public interface OrderService extends IService<Order> {
    Result<OrderTo> getOrderByOrderSn(String orderSn);

    void cancelOrder(Order order);

    Result<String> generateCode(String orderSn);

    Result<List<OrderAndOrderItemTo>> getMemberOrder(int pageNum, int pageSize, String key);

    String handleSuccessOrder(PayAsyncVo payAsyncVo);

    void closePayTrade(String orderSn);

    SeckillOrder createSeckillOrder(SeckillNotifyTo to);

    Result<BigDecimal> getPayAmountByOrderSn(String orderSn);

    void cancelSeckillOrder(SeckillOrder seckillOrder);

    interface Web {
        ConfirmVo confirm();

        SubmitResultVo submit(SubmitVo submitVo);

        String payOrder(String orderSn);
    }
}
