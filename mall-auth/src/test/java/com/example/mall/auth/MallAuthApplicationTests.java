package com.example.mall.auth;

import com.example.mall.auth.feign.MemberFeignClient;
import com.example.mall.common.model.vo.RegisterVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallAuthApplicationTests {
@Autowired
private MemberFeignClient feignClient;
    @Test
    void contextLoads() {
        RegisterVo registerVo = new RegisterVo();
        registerVo.setPassword("123456");
        registerVo.setMail("1842929189@qq.com");
        registerVo.setUsername("test1");
        registerVo.setRepassword("123456");
        feignClient.register(registerVo);
    }

}
