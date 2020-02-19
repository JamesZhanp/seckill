package com.james.secondkill.config;

import com.james.secondkill.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 自定义web配置
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 14:51
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Autowired
//    UserArgu

    @Autowired
    AccessInterceptor accessInterceptor;
}
