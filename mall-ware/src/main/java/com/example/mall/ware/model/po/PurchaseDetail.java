package com.example.mall.ware.model.po;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author zhuwenjie
 */
@Data
@TableName("wms_purchase_detail")
public class PurchaseDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 采购单id
     */
    private Long purchaseId;

    /**
     * 采购商品id
     */
    private Long skuId;

    /**
     * 采购数量
     */
    private Integer skuNum;

    /**
     * 采购金额
     */
    private BigDecimal skuPrice;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 状态[0新建，1已分配，2正在采购，3已完成，4采购失败]
     */
    private Integer status;
    /**
     * 失败的原因
     */
    private String reason;


}
