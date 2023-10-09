package com.toyproject.instagram.controller;

import com.toyproject.instagram.dto.SigninReqDto;
import com.toyproject.instagram.dto.SignupReqDto;
import com.toyproject.instagram.service.UserService;
import com.toyproject.instagram.exception.SignupException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController                          //@Component가 IoC에 등록해 줌
@RequestMapping("/api/v1/auth")          //모든 매핑 주소에 @RequestMapping의 주소가 앞에 붙음, 필수는 아님(헷갈림 방지)
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;      //객체 생성, 상수 -> 초기화 필수, 강제성을 부여함, 알아서 AC와 연결해줌

    @PostMapping("/user")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReqDto signupReqDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){  //오류가 있을 때만 동작
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {   //
                errorMap.put(error.getField(), error.getDefaultMessage());
            });
//            return ResponseEntity.badRequest().body(errorMap);
            throw new SignupException(errorMap);
        }

        userService.signupUser(signupReqDto);
        return  ResponseEntity.ok(null);
    }

    @PostMapping("/login")
    public ResponseEntity<?> signin(@RequestBody SigninReqDto signinReqDto) {
        String accessToken = userService.signinUser(signinReqDto); //signin이 정상적으로 이루어지면 accessToken이 200번 OK로 리턴됨
        return ResponseEntity.ok(accessToken);
    }

    @GetMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(userService.authenticate(token));
    }
}
