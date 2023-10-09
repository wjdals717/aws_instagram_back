package com.toyproject.instagram.dto;

import com.toyproject.instagram.entity.User;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignupReqDto { //Dto는 Ioc에 등록되어 있지 않기 때문에 바로 암호화를 시킬 수 없음
    // @Pattern(regexp = "")정규식을 검사할 때 사용됩니다.
    @Pattern(regexp = "^[a-zA-Z0-9]+@[0-9a-zA-Z]+\\.[a-z]*$|^+|[0-9]{11}+$", message = "이메일 또는 전화번호를 입력하세요.")      // "\\d"="0-9"를 의미
    private String phoneOrEmail;

    @Pattern(regexp = "^[가-힣]{2,6}$", message = "이름은 한글만 입력할 수 있습니다.")
    private String name;

    @Pattern(regexp = "^(?=.*[a-z])[a-z0-9_.]*$", message = "사용할 수 없는 사용자 이름입니다. 다른 사용자 이름을 입력하세요.")
    private String username;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,25}$", message = "비밀번호는 영문, 숫자 조합으로 8자 이상 입력하세요.")
    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;

    // Dto -> Entity로 변환, 제어의 역전이 불가능 -> IoC에 저장 불가능
    public User toUserEntity(BCryptPasswordEncoder passwordEncoder) {   //Dto로 넘어올 때 암호화가 되지 않은 상태, 비밀번호를 암호화해서 전달해주기 위하여 매개변수 받음
        return User.builder()
                .name(name)
                .username(username)
                .password(passwordEncoder.encode(password))     //비밀번호 암호화, 회원가입할 때마다 매번 객체 생성을 방지
                .build();
    }
}
