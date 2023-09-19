package com.example.mall.common.model.to;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 秒杀活动场次
 * </p>
 *
 * @author zhuwenjie
 */
@Data
public class SeckillSessionTo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 场次名称
     */
    private String name;

    /**
     * 每日开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;


    /**
     * 每日结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 启用状态
     */
    private Boolean status;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private List<SeckillSkuRelationTo> skuRelationList;

    @Data
    public static class SeckillSkuRelationTo implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * id
         */
        private Long id;

        /**
         * 活动id
         */
        private Long promotionId;

        /**
         * 活动场次id
         */
        private Long promotionSessionId;

        /**
         * 商品id
         */
        private Long skuId;

        /**
         * 秒杀价格
         */
        private BigDecimal seckillPrice;

        /**
         * 秒杀总量
         */
        private BigDecimal seckillCount;

        /**
         * 每人限购数量
         */
        private Integer seckillLimit;

        /**
         * 排序
         */
        private Integer seckillSort;

        /**
         * 商品基本信息
         */
        private SkuInfoTo skuInfo;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

        private String randomCode;

    }

}
