package com.example.mall.seckill.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.mall.common.model.constant.SystemConstant;
import com.example.mall.seckill.model.vo.SeckillOrderVo;
import com.example.mall.seckill.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/seckill")
public class SeckillControllerWeb {
    private final SeckillService.Web seckillServiceWeb;

    @GetMapping("/seckill")
    public String seckill(@RequestParam("seckillId") String seckillId,
                          @RequestParam("count") Integer count,
                          @RequestParam("token") String token,
                          RedirectAttributes model) {
        SeckillOrderVo result = seckillServiceWeb.seckillSku(seckillId, count, token);
        model.addAttribute("result", result);
        return "pay";
    }
}
