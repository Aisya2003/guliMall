package com.example.mall.coupon.model.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 商品阶梯价格
 * </p>
 *
 * @author zhuwenjie
 */
@Data
@TableName("sms_sku_ladder")
public class SkuLadder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * spu_id
     */
    private Long skuId;

    /**
     * 满几件
     */
    private Integer fullCount;

    /**
     * 打几折
     */
    private BigDecimal discount;

    /**
     * 折后价
     */
    private BigDecimal price;

    /**
     * 是否叠加其他优惠[0-不可叠加，1-可叠加]
     */
    private Integer addOther;


}
