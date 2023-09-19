package com.example.mall.order.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ConfirmVo implements Serializable {
    //收获地址
    private List<ReceiveAddress> address;
    //准备支付的商品
    private List<OrderItem> orderItem;

    //发票信息

    //会员积分
    private Integer integration;
    //总价
    private BigDecimal total;
    //实付
    private BigDecimal payPrice;

    //提交令牌
    private String orderToken;

    //总重量
    public BigDecimal getTotalWeight(){
        BigDecimal result = new BigDecimal(0);
        if (this.orderItem == null || this.orderItem.isEmpty()){
            return result;
        }
        for (OrderItem item : this.orderItem) {
            result = result.add(item.getWeight());
        }
        return result;
    }
    public Integer totalItem() {
        Integer result = 0;
        if (this.orderItem == null || this.orderItem.isEmpty()) {
            return result;
        }
        for (OrderItem item : this.orderItem) {
            result += item.getCount();
        }
        return result;
    }

    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        if (orderItem == null || orderItem.isEmpty()) {
            return total;
        }
        for (OrderItem item : orderItem) {
            total = total.add(item.getTotalPrice());
        }
        return total;
    }

    public BigDecimal getPayPrice() {
        BigDecimal reduce = new BigDecimal(0);
        return getTotal().subtract(reduce);
    }

    @Data
    public static class ReceiveAddress implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * id
         */
        @TableId(value = "id", type = IdType.AUTO)
        private Long id;

        /**
         * member_id
         */
        private Long memberId;

        /**
         * 收货人姓名
         */
        private String name;

        /**
         * 电话
         */
        private String phone;

        /**
         * 邮政编码
         */
        private String postCode;

        /**
         * 省份/直辖市
         */
        private String province;

        /**
         * 城市
         */
        private String city;

        /**
         * 区
         */
        private String region;

        /**
         * 详细地址(街道)
         */
        private String detailAddress;

        /**
         * 省市区代码
         */
        private String areacode;

        /**
         * 是否默认
         */
        private Boolean defaultStatus;
    }

    @Data
    public static class OrderItem implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long skuId;
        private String title;
        private String image;
        private List<String> skuAttr;
        private BigDecimal price;
        private Integer count;
        private BigDecimal totalPrice;

        private BigDecimal weight;
        private Boolean hasStock = false;

        public BigDecimal getTotalPrice() {
            return this.price.multiply(BigDecimal.valueOf(this.count));
        }
    }
}
