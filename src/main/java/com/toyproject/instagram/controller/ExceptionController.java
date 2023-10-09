package com.toyproject.instagram.controller;

import com.toyproject.instagram.exception.JwtException;
import com.toyproject.instagram.exception.SignupException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice       //어디에서 예외가 일어나도 상관없이 예외를 캐치해서 여기서 응답이 일어나도록 함
public class ExceptionController {

    @ExceptionHandler(SignupException.class)        //예외가 터지는 순간 @RestControllerAdvice가 낚아채서 예외를 처리함,
    // service에서 처리하다가 예외를 처리해야 하는 경우, controller까지 가지않고 바로 예외처리가 가능해짐
    public ResponseEntity<?> signupExceptionHandle(SignupException signupException) {
        return ResponseEntity.badRequest().body(signupException.getErrorMap());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> jwtExceptionHandle(JwtException jwtException) {
        return ResponseEntity.badRequest().body(jwtException.getMessage());
    }
}
