package com.example.mall.common.model.to.rabbitmq;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LockedNotifyTo {
    private Long taskId;
    private LockDetailTo detail;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LockDetailTo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * id
         */
        private Long id;

        /**
         * sku_id
         */
        private Long skuId;

        /**
         * sku_name
         */
        private String skuName;

        /**
         * 购买个数
         */
        private Integer skuNum;

        /**
         * 工作单id
         */
        private Long taskId;

        /**
         * 仓库id
         */
        private Long wareId;

        /**
         * 1-已锁定  2-已解锁  3-扣减
         */
        private Integer lockStatus;


    }

}
