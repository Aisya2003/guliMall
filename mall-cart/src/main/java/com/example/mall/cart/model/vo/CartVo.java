package com.example.mall.cart.model.vo;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVo {
    private List<CartItem> items;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount;
    private BigDecimal reduce = new BigDecimal(0);

    public Integer getCountNum() {
        Integer count = 0;
        if (items != null && items.size() != 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        if (items == null) {
            return 0;
        } else {
            return items.size();
        }
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal(0);
        if (items != null && items.size() != 0) {
            for (CartItem item : items) {
                if (item.check) {
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount = amount.add(totalPrice);
                }
            }
        }
        return amount.subtract(reduce);
    }

    @Data
    public static class CartItem {
        private Long skuId;
        private Boolean check = true;
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
