package com.example.mall.auth.config;

import com.example.mall.auth.feign.MemberFeignClient;
import com.example.mall.common.model.vo.MemberVo;
import com.example.mall.auth.model.vo.MemberDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class JDBCUserDetailService implements UserDetailsService {
    private final MemberFeignClient memberFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberVo login = memberFeignClient.login(username);
        if (login == null) {
            throw new UsernameNotFoundException("用户名或密码错误！");
        }
        MemberDetailVo memberDetailVo = new MemberDetailVo();
        BeanUtils.copyProperties(login, memberDetailVo);
        return memberDetailVo;
    }
}
