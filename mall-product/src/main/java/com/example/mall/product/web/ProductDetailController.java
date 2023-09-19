package com.example.mall.product.web;

import com.example.mall.product.model.vo.SkuDetailVo;
import com.example.mall.product.service.SkuInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ProductDetailController {
    private final SkuInfoService.Web skuInfoServiceWeb;

    @GetMapping("/{skuId}.html")
    public String productDetail(@PathVariable("skuId") Long skuId, Model model) {
        SkuDetailVo skuDetailVo = skuInfoServiceWeb.getDetail(skuId);
        model.addAttribute("result", skuDetailVo);
        return "item";
    }
}
