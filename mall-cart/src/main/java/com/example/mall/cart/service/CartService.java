package com.example.mall.cart.service;

import com.example.mall.cart.model.vo.CartVo;

import java.util.List;

public interface CartService {
    void addToCart(Long skuId, Integer skuNum);

    CartVo.CartItem getCartItem(Long skuId);

    CartVo getCartInfo();

    void changeItemStatus(Long skuId, Boolean hasChecked);

    void changeItemCount(Long skuId, Integer itemCount);

    void removeItem(Long skuId);

    List<CartVo.CartItem> getCartItemList();
}
