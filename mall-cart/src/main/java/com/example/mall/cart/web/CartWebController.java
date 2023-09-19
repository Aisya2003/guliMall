package com.example.mall.cart.web;

import com.example.mall.cart.model.vo.CartVo;
import com.example.mall.cart.service.CartService;
import com.example.mall.common.model.constant.SystemConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CartWebController {
    private final CartService cartService;

    @GetMapping("/removeitem")
    public String removeItem(@RequestParam("skuId") Long skuId) {
        cartService.removeItem(skuId);
        return "redirect:" + SystemConstant.MALL_CART_HOST + "cart.html";
    }

    @GetMapping("/changeitemcount")
    public String changeItemCount(@RequestParam("skuId") Long skuId,
                                  @RequestParam("itemCount") Integer itemCount) {
        cartService.changeItemCount(skuId, itemCount);
        return "redirect:" + SystemConstant.MALL_CART_HOST + "cart.html";
    }

    @GetMapping("/changeitem")
    public String changeItemStatus(@RequestParam("skuId") Long skuId,
                                   @RequestParam("hasChecked") Boolean hasChecked) {
        cartService.changeItemStatus(skuId, hasChecked);
        return "redirect:" + SystemConstant.MALL_CART_HOST + "cart.html";
    }

    @GetMapping("/cart.html")
    public String index(Model model) {
        CartVo cartVo = cartService.getCartInfo();
        model.addAttribute("cart", cartVo);
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("skuNum") Integer skuNum,
                            RedirectAttributes redirectAttributes) {
        cartService.addToCart(skuId, skuNum);
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:" + SystemConstant.MALL_CART_HOST + "success.html";
    }

    @GetMapping("/success.html")
    public String addSuccess(@RequestParam(value = "skuId", required = false) Long skuId,
                             Model model) {
        if (skuId == null) {
            return "success";
        }
        CartVo.CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }
}
