package com.example.mall.auth.serivce.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.mall.auth.feign.MemberFeignClient;
import com.example.mall.common.model.vo.RegisterVo;
import com.example.mall.auth.serivce.RegisterService;
import com.example.mall.common.model.constant.AuthConstant;
import com.example.mall.common.model.constant.RedisConstant;
import com.example.mall.common.model.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    private final StringRedisTemplate stringRedisTemplate;
    private final JavaMailSender javaMailSender;
    private final MemberFeignClient memberFeignClient;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String username;

    @Override
    public Result sendCheckCode(String mailToAddress) {
        //验证
        if (StringUtils.isEmpty(mailToAddress)) {
            return Result.fail("邮箱地址不能为空！");
        }
        boolean match = Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", mailToAddress);
        if (!match) {
            return Result.fail("邮箱格式错误！");
        }
        //防止多次请求
        long passedTime = this.isSended(mailToAddress);
        if (passedTime <= RedisConstant.Auth.SEND_CODE_EXPIRE_MILLIS) {
            return Result.fail("请等待" + (RedisConstant.Auth.SEND_CODE_EXPIRE_MILLIS - passedTime) / 1000 + "秒后再次尝试");
        }

        String checkCode = this.generateCheckCode(AuthConstant.USE_COMPLEX_CHECK_CODE, AuthConstant.CHECK_CODE_LENGTH);

        String newCheckCode = checkCode + (RedisConstant.Auth.CODE_TIME_SPLITER + System.currentTimeMillis());

        //保存验证码
        String key = RedisConstant.Auth.CHECK_CODE_KEY_PREFIX + mailToAddress;
        stringRedisTemplate.opsForValue().set(key, newCheckCode, Duration.ofMinutes(RedisConstant.Auth.CHECK_CODE_EXPIRE_MINUTE));

        String text = "您正在通过邮箱注册电子商城，验证码为：" + checkCode + "\n密码有效期为五分钟，请尽快完成验证！";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(username);
        simpleMailMessage.setTo(mailToAddress);
        simpleMailMessage.setSubject("验证码服务");
        //邮件内容
        simpleMailMessage.setText(text);
        //邮件发送时间
        simpleMailMessage.setSentDate(new Date());

        javaMailSender.send(simpleMailMessage);

        return Result.ok();
    }

    @Override
    public boolean checkCode(String mail, String checkCode) {
        String code = stringRedisTemplate.opsForValue().get(RedisConstant.Auth.CHECK_CODE_KEY_PREFIX + mail);
        if (StringUtils.isBlank(code)) {
            return false;
        } else {
            return code.split(RedisConstant.Auth.CODE_TIME_SPLITER)[0].equalsIgnoreCase(checkCode);
        }
    }

    @Override
    public Result register(RegisterVo registerVo) {
        //加密密码
        registerVo.setRepassword(passwordEncoder.encode(registerVo.getPassword()));
        registerVo.setPassword(passwordEncoder.encode(registerVo.getPassword()));
        Result result = memberFeignClient.register(registerVo);
        if (result.isSuccess()) {
            this.removeCheckCode(registerVo.getMail());
        }
        return result;
    }

    @Override
    public void removeCheckCode(String mail) {
        stringRedisTemplate.delete(RedisConstant.Auth.CHECK_CODE_KEY_PREFIX + mail);
    }


    private long isSended(String mailToAddress) {
        String code = stringRedisTemplate.opsForValue()
                .get(RedisConstant.Auth.CHECK_CODE_KEY_PREFIX + mailToAddress);
        if (StringUtils.isEmpty(code))
            return System.currentTimeMillis();
        long passedTime = Long.parseLong(code.substring(code.lastIndexOf(RedisConstant.Auth.CODE_TIME_SPLITER) + 1));
        return System.currentTimeMillis() - passedTime;
    }

    private String generateCheckCode(Boolean useComplexCheckCode, Integer checkCodeLength) {
        StringBuilder checkCode = new StringBuilder();
        if (!useComplexCheckCode) {
            for (int i = 0; i < checkCodeLength; i++) {
                checkCode.append((int) (Math.random() * 10));
            }
            return checkCode.toString();
        } else {
            return UUID.randomUUID().toString().substring(0, checkCodeLength);
        }
    }
}
