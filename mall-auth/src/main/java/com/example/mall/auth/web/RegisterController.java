package com.example.mall.auth.web;

import com.example.mall.common.model.vo.RegisterVo;
import com.example.mall.auth.serivce.RegisterService;
import com.example.mall.common.model.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/auth/register")
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;

    @ResponseBody
    @GetMapping("/sendCheckCode")
    public Result checkCode(String mailToAddress) {
        return registerService.sendCheckCode(mailToAddress);
    }

    @PostMapping("/register")
    public String register(@Validated RegisterVo registerVo,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.mall.com/register.html";
        }
        if (registerService.checkCode(registerVo.getMail(), registerVo.getCheckCode())) {
            //保存注册信息
            Result register = registerService.register(registerVo);
            if (!register.isSuccess()) {
                String key = register.getMsg().split("-")[0];
                String value = register.getMsg().split("-")[1];
                HashMap<String, String> errors = new HashMap<>(1);
                errors.put(key, value);
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.mall.com/register.html";
            }
        } else {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("checkCode", "验证码错误!");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.mall.com/register.html";
        }
        return "login";
    }
}
