package com.toyproject.instagram.config;

import com.toyproject.instagram.exception.AuthenticateExceptionEntryPoint;
import com.toyproject.instagram.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity      //현재 우리가 만든 security 설정 정책을 따르겠다.
@Configuration          //설정 component
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticateExceptionEntryPoint authenticateExceptionEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean       //이미 존재하는 객체를 생성자를 통해 생성해서 직접 IoC에 등록해줌, 직접 생성해서 등록해주는 것
    public BCryptPasswordEncoder passwordEncoder() { //IoC에 등록된 이름 :passwordEncoder
        return new BCryptPasswordEncoder();
    }
    //BCryptPasswordEncoder은 외부에서 가져온 라이브러리이기 때문에 코드를 수정할 수 없어서 어노테이션을 달 수 없기 때문

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();            //WebMvcConfig에서 설정한 cors 정책을 따르겠다 (우리가 설정한 crossorign 동작을 따라감 -> MvcConfig 파일)
        http.csrf().disable();  //csrf 토큰 비활성화

        //로그인 인증이 되지 않아도 가능한 요청이 되어야 함
        http.authorizeRequests()
                .antMatchers("/api/v1/auth/**")     // "/api/v1/auth/"로 시작하는 모든 요청(**은 뒤에 들어오는 어떤 요청이라도 상관없음)
                .permitAll()                        // (antMatchers에서 들어온 요청에 대하여) 인증없이 요청을 허용하겠다.
                .anyRequest()                       // 나머지 요청은
                .authenticated()                    // 인증을 받아라
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticateExceptionEntryPoint);
    }
}
