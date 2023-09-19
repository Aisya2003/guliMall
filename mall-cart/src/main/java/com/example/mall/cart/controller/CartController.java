package com.example.mall.cart.controller;

import com.example.mall.cart.model.vo.CartVo;
import com.example.mall.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    @GetMapping("/inner/getcart")
    public List<CartVo.CartItem> getCartItemList(){
        return cartService.getCartItemList();
    }
}
