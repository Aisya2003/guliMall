package com.example.mall.order.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.OrderAndOrderItemTo;
import com.example.mall.common.model.to.OrderTo;
import com.example.mall.order.constant.AlipayProperties;
import com.example.mall.order.model.vo.PayAsyncVo;
import com.example.mall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/order/order")
@EnableConfigurationProperties(AlipayProperties.class)
public class OrderController {
    private final OrderService orderService;
    private final AlipayProperties alipayProperties;

    /**
     * 支付宝回调地址
     *
     * @return
     */
    @RequestMapping("/paysuccess")
    public String paySuccess(PayAsyncVo payAsyncVo, HttpServletRequest request) {
        System.out.println(alipayProperties.getNotify_url());
        //验签
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        boolean verify_result;
        try {
            verify_result = AlipaySignature.rsaCheckV1(params,
                    alipayProperties.getAlipay_public_key(),
                    alipayProperties.getCharset(),
                    alipayProperties.getSign_type());
        } catch (AlipayApiException e) {
            throw new RuntimeException("校验支付签名异常");
        }
        if (!verify_result) {
            return null;
        }
        return orderService.handleSuccessOrder(payAsyncVo);
    }

    @GetMapping("/inner/payamount/{orderSn}")
    public Result<BigDecimal> payAmount(@PathVariable("orderSn")String orderSn){
        return orderService.getPayAmountByOrderSn(orderSn);
    }
    @PostMapping("/inner/getmemberorder")
    public Result<List<OrderAndOrderItemTo>> getMemberOrder(@RequestBody PageRequestParams params) {
        return orderService.getMemberOrder(params.getPage(), params.getLimit(),params.getKey());
    }

    @GetMapping("/{orderSn}/order")
    public Result<OrderTo> getOrderStatus(@PathVariable("orderSn") String orderSn) {
        return orderService.getOrderByOrderSn(orderSn);
    }

    @GetMapping("/{orderSn}/code")
    public Result<String> generateCode(@PathVariable("orderSn") String orderSn) {
        return orderService.generateCode(orderSn);
    }
}
