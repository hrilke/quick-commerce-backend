package com.quickcommerce.orders.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignAuthConfig {

    @Bean
    public RequestInterceptor authorizationHeaderForwarder() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
                if (attrs instanceof ServletRequestAttributes servletAttrs) {
                    HttpServletRequest req = servletAttrs.getRequest();
                    String auth = req.getHeader("Authorization");
                    if (auth != null && !auth.isBlank() && !template.headers().containsKey("Authorization")) {
                        template.header("Authorization", auth);
                    }
                }
            }
        };
    }
}
