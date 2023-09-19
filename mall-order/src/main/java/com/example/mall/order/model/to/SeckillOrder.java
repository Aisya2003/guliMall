package com.example.mall.order.model.to;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.mall.common.model.to.rabbitmq.SeckillNotifyTo;
import com.example.mall.order.model.po.Order;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillOrder implements Serializable {
    private Order order;
    private SeckillNotifyTo to;
}
