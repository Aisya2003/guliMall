package com.example.mall.member;

import com.example.mall.common.model.vo.RegisterVo;
import com.example.mall.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MallMemberApplicationTestsVo {
    @Autowired
    private MemberService memberService;

    @Test
    void testInset() {
        RegisterVo registerVo = new RegisterVo();
        registerVo.setPassword("123456");
        registerVo.setMail("1842929189@qq.com");
        registerVo.setUsername("test1");
        registerVo.setRepassword("123456");

    }
}
