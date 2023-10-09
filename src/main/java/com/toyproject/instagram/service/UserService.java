package com.toyproject.instagram.service;

import com.toyproject.instagram.dto.SigninReqDto;
import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.entity.User;
import com.toyproject.instagram.exception.JwtException;
import com.toyproject.instagram.exception.SignupException;
import com.toyproject.instagram.repository.UserMapper;
import com.toyproject.instagram.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service        //IoC 공간에 UserService가 등록됨, 따로 주소를 달아주지 않으면 ("userService")이렇게 등록됨, 지금은 하나 밖에 없기 때문에 자동으로 연결
@RequiredArgsConstructor
public class UserService {
    //controller -> service -> repository

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;        //IoC에 등록된 이름 사용
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signupUser(SignupReqDto signupReqDto) {
        User user = signupReqDto.toUserEntity(passwordEncoder);
        String emailPattern = "^[a-zA-Z0-9]+@[0-9a-zA-Z]+\\.[a-z]*$";
        String phonePattern = "^+|[0-9]{11}+$";

        Pattern emailRegex = Pattern.compile(emailPattern);
        Pattern phoneRegex = Pattern.compile(phonePattern);

        Matcher emailMatcher = emailRegex.matcher(signupReqDto.getPhoneOrEmail());
        Matcher phoneMatcher = phoneRegex.matcher(signupReqDto.getPhoneOrEmail());

        if(emailMatcher.matches()) {
            user.setEmail(signupReqDto.getPhoneOrEmail());
        }
        if(phoneMatcher.matches()) {
            user.setPhone(signupReqDto.getPhoneOrEmail());
        }

        checkDuplicated(user);      //중복체크
        userMapper.saveUser(user);  //중복체크를 통과했으면 회원가입 저장

//        Integer executeCount = userMapper.saveUser(signupReqDto.toUserEntity(passwordEncoder));       //Dto를 Entity로 변환
//        System.out.println(executeCount);
    }

    private void checkDuplicated(User user) {       //회원가입 중복체크
        if(StringUtils.hasText(user.getPhone())) {
            if(userMapper.findUserByPhone(user.getPhone()) != null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("phone", "이미 사용중인 연락처입니다.");
                throw new SignupException(errorMap);
            }
        }
        if(StringUtils.hasText(user.getEmail())) {
            if(userMapper.findUserByEmail(user.getEmail()) != null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("email", "이미 사용중인 이메일입니다.");
                throw new SignupException(errorMap);
            }
        }
        if(StringUtils.hasText(user.getUsername())) {
            if(userMapper.findUserByUsername(user.getUsername()) != null) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("username", "이미 사용중인 사용자 이름입니다.");
                throw new SignupException(errorMap);
            }
        }
    }

    public String signinUser(SigninReqDto signinReqDto){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signinReqDto.getPhoneOrEmailOrUsername(), signinReqDto.getLoginPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken); //Builder에게 인증(authenticate)해달라고 요청

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);  //JWT Token 생성
        return accessToken;
    }

    public Boolean authenticate(String token) {
        String accessToken = jwtTokenProvider.convertToken(token);
        if(!jwtTokenProvider.validateToken(accessToken)){        //조건문 결과는 true/false
            //못 쓰는 토큰일 경우 -> 예외를 강제로 발생시킴(JwtException)
            throw new JwtException("사용자 정보가 만료되었습니다. 다시 로그인하세요.");
        }
        return true;
    }

}
