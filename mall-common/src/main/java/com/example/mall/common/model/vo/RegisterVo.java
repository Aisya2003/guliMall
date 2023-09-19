package com.example.mall.common.model.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class RegisterVo {
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 4, max = 20, message = "用户名长度必须在4-20位之间")
    private String username;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    @NotEmpty(message = "两次密码不一致")
    @Length(min = 6, max = 20, message = "两次密码不一致")
    private String repassword;

    @NotEmpty(message = "邮件不能为空")
    @Email(message = "邮件格式错误")
    private String mail;

    @NotEmpty(message = "验证码格式不能为空")
    private String checkCode;
}
