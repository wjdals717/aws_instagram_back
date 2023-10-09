package com.toyproject.instagram.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration      //component(IoC에 컨테이너에 설정 관련 객체 생성)
public class MvcConfig implements WebMvcConfigurer {        //@Crossorign 대신 mvcConfig

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")          //모든 요청 엔드포인트
                .allowedMethods("*")        //모든 요청 메소드
                .allowedOrigins("*");       //모든 요청 서버  https://naver.com (네이버에서 들어오는 요청 허용) 현재는 localhost:3000
    }
}
