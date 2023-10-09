package com.toyproject.instagram.security;

import com.toyproject.instagram.entity.User;
import com.toyproject.instagram.repository.UserMapper;
import com.toyproject.instagram.service.PrincipalDetailsService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.security.Key;
import java.util.Date;

// JWT 토큰을 관리해주는 로직
@Component
public class JwtTokenProvider {
    private final Key key;
    private final PrincipalDetailsService principalDetailsService;
    private final UserMapper userMapper;

//    @Autowired는 IoC 컨테이너에서 객체를 자동 주입
//    @Value는 appliacation.yml에서 변수 데이터를 자동 주입
    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Autowired PrincipalDetailsService principalDetailsService, @Autowired UserMapper userMapper) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.principalDetailsService = principalDetailsService;
        this.userMapper = userMapper;
    }

    // JWT 토큰을 생성
    public String generateAccessToken(Authentication authentication) {
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        Date tokenExpiresDate = new Date(new Date().getTime() + (1000 * 60 * 60 * 24));   //24시간 //1000 * 60 * 60 -> 1시간 //만료기간의 매개변수

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject("Access Token")                       //토큰의 제목
                .setExpiration(tokenExpiresDate)                  //만료기간(매개변수 -> Date Type)
                .signWith(key, SignatureAlgorithm.HS256);         //JWT를 서명할 때 사용

        User user = userMapper.findUserByPhone(principalUser.getUsername());
        if(user != null) {
            return jwtBuilder.claim("username", user.getUsername()).compact();  //토큰 안에 username을 넣어둠, 매번 로그인을 하지 않아도 token에서  username을 꺼내 쓸 수 있음
        }
        user = userMapper.findUserByEmail(principalUser.getUsername());
        if(user != null) {
            return jwtBuilder.claim("username", user.getUsername()).compact();
        }
        user = userMapper.findUserByPhone(principalUser.getUsername());
        if(user != null) {
            return jwtBuilder.claim("username", user.getUsername()).compact();
        }

        return jwtBuilder.claim("username", user.getUsername()).compact();
    }

    public Boolean validateToken(String token) {    // token이 전달될 때 온전한 토큰이 필요, "Barear "을 제거해야 함
        try {
            //쓸 수 있는 토큰인지 확인해주는 과정
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String convertToken(String bearerToken){ //"Barear "을 제거
        String type = "Bearer ";   //제거해야 하는 문구

        // hasText() : null인지 확인, 공백인지 확인
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(type)) {      //StringUtils import 주의!!
            return bearerToken.substring(type.length());        //type.length() -> 0-6번, substring으로 7번부터 자르게 함
        }
        return "";
    }

    public Authentication getAuthentication(String accessToken) {
        Authentication authentication = null;
        String username = Jwts
                .parserBuilder()
                .setSigningKey(key)             //암호화를 품
                .build()
                .parseClaimsJws(accessToken)
                .getBody()                      //claim 등의 payload의 정보를 가져옴
                .get("username")                //claim 중 username을 가져옴
                .toString();
        PrincipalUser principalUser = (PrincipalUser) principalDetailsService.loadUserByUsername(username);

        authentication = new UsernamePasswordAuthenticationToken(principalUser, null, principalUser.getAuthorities());
        return authentication;
    }
}
