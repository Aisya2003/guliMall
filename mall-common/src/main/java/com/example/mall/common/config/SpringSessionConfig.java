package com.example.mall.common.config;

import com.example.mall.common.model.constant.SystemConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 60 * 3)
public class SpringSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setDomainName(SystemConstant.SESSION_DOMAIN);
        defaultCookieSerializer.setCookieName(SystemConstant.COOKIE_NAME);
        defaultCookieSerializer.setCookieMaxAge(SystemConstant.COOKIE_DEFAULT_EXPIRE_SECOND);
        return defaultCookieSerializer;
    }

}
