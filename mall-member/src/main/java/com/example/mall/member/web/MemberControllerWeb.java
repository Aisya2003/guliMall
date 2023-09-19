package com.example.mall.member.web;

import com.alibaba.fastjson2.JSON;
import com.example.mall.common.model.page.PageRequestParams;
import com.example.mall.common.model.page.PageResult;
import com.example.mall.common.model.result.Result;
import com.example.mall.common.model.to.OrderAndOrderItemTo;
import com.example.mall.member.feign.OrderFeignClient;
import com.example.mall.member.model.vo.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberControllerWeb {
    private final OrderFeignClient orderFeignClient;

    @GetMapping("/memberorder.html")
    public String orderList(@RequestParam(value = "pageNum", defaultValue = "1", required = false) int pageNum,
                            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize,
                            @RequestParam(value = "key",required = false) String key,
                            Model model) {
        PageRequestParams pageRequestParams = new PageRequestParams();
        pageRequestParams.setKey(key);
        pageRequestParams.setPage(pageNum);
        pageRequestParams.setLimit(pageSize);
        Result<List<OrderAndOrderItemTo>> memberOrder = orderFeignClient.getMemberOrder(pageRequestParams);
        if (!memberOrder.isSuccess()) {
            return null;
        }
        List<OrderAndOrderItemTo> result = JSON.parseArray(JSON.toJSONString(memberOrder.getData()), OrderAndOrderItemTo.class);

        model.addAttribute("result", result);
        PageInfo pageResult = new PageInfo();
        pageResult.setPageNum(pageNum);
        pageResult.setTotal(Integer.parseInt(memberOrder.getMsg()));
        model.addAttribute("page", pageResult);

        return "orderList";
    }
}
