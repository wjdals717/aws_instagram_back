package com.toyproject.instagram.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization");
        String jwtToken = jwtTokenProvider.convertToken(token);     //convertToken는 "Bearer " 떼는 역할
        String uri = httpServletRequest.getRequestURI();

        // if를 거치지 않으면 유효하지 않은 토큰
        if(!uri.startsWith("/api/v1/auth") && jwtTokenProvider.validateToken(jwtToken)) {   //~auth로 시작하면 if 실행하지 않고 dofilter로 넘어감
            Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //security 인증 상태에 Authentication 객체가 존재하면 인증된 상태임을 의미함.
        }

        chain.doFilter(request, response);
    }
}
