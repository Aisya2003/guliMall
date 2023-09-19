package com.example.mall.order.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.constant.SystemConstant;
import com.example.mall.order.model.constant.CreatOrderFailEnum;
import com.example.mall.order.model.po.Order;
import com.example.mall.order.model.vo.ConfirmVo;
import com.example.mall.order.model.vo.SubmitResultVo;
import com.example.mall.order.model.vo.SubmitVo;
import com.example.mall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class OrderControllerWeb {
    private final OrderService.Web orderServiceWeb;
    private final OrderService orderService;

    @RequestMapping("/order/order/payOrder/{orderSn}")
    public void payOrder(@PathVariable("orderSn") String orderSn, HttpServletResponse httpServletResponse) throws IOException {
        String result = orderServiceWeb.payOrder(orderSn);
        if (result == null) {
            return;
        }
        httpServletResponse.setContentType("text/html;charset=utf-8");
        httpServletResponse.getWriter().write(result);
        httpServletResponse.getWriter().flush();
    }

    @GetMapping("/confirm.html")
    public String confirm(Model model) {
        ConfirmVo confirmVo = orderServiceWeb.confirm();
        model.addAttribute("confirm", confirmVo);
        return "confirm";
    }

    @PostMapping("/submit")
    public String submit(SubmitVo submitVo, Model model, RedirectAttributes redirectAttributes) {
        SubmitResultVo submitResultVo = orderServiceWeb.submit(submitVo);
        String prompt = "下单异常";
        if (submitResultVo == null || submitResultVo.getResponseCode() != 0) {//失败
            switch (submitResultVo.getResponseCode()) {
                case -1:
                    prompt = CreatOrderFailEnum.ARGUMENT_FAULT.getDesc();
                    break;
                case 1:
                    prompt = CreatOrderFailEnum.CHECK_PRICE_FAULT.getDesc();
                    break;
                case 2:
                    prompt = CreatOrderFailEnum.LOCK_STOCK_FAULT.getDesc();
                    break;
                default:
                    break;
            }
            redirectAttributes.addFlashAttribute("prompt", prompt);
            return "redirect:" + SystemConstant.MALL_ORDER_HOST + "confirm.html";
        }
        model.addAttribute("result", submitResultVo);
        return "pay";
    }

    @GetMapping("/pay")
    public String payPage(@RequestParam("orderSn") String orderSn,Model model) {
        SubmitResultVo submitResultVo = new SubmitResultVo();
        submitResultVo.setOrder(orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderSn,orderSn)));
        model.addAttribute("result", submitResultVo);
        return "pay";
    }

}
