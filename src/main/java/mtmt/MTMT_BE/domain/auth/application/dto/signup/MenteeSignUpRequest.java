package mtmt.MTMT_BE.domain.auth.application.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import mtmt.MTMT_BE.domain.user.domain.type.Category;
import mtmt.MTMT_BE.domain.user.domain.type.Gender;
import mtmt.MTMT_BE.global.validator.enum_value.annotation.EnumValue;

// record: Java 14부터 사용 가능한 불변 데이터 객체를 만들 수 있는 클래스 타입
// 데이터가 한번 할당되면 변경되지 않기에 DTO로 많이 사용됨
// 생성자, getter, equals, hashCode, toString 등을 자동으로 구현해줌
public record MenteeSignUpRequest(

        @NotBlank(message = "Email is required.")
        @Email(message = "Please provide a valid email address.")
        String email,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters.")
        String password,

        @NotBlank(message = "Name is required.")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
        String name,

        @NotBlank
        @EnumValue(enumClass = Gender.class, message = "Invalid gender type.") // 커스텀 어노테이션을 이용한 예외처리
        String gender,

        @NotBlank(message = "Birth date is required. Format: yyyy-MM-dd")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Birth date must be in yyyy-MM-dd format") // 정규식으로 String 문자열 형식 체크
        String birthDate,

        @NotBlank(message = "First interest is required.")
        @EnumValue(enumClass = Category.class, message = "Invalid first interest type.") // 커스텀 어노테이션을 이용한 예외처리
        String interestFirst,

        @NotBlank(message = "Second interest is required.")
        @EnumValue(enumClass = Category.class, message = "Invalid second interest type.") // 커스텀 어노테이션을 이용한 예외처리
        String interestSecond,

        @NotBlank(message = "Third interest is required.")
        @EnumValue(enumClass = Category.class, message = "Invalid third interest type.") // 커스텀 어노테이션을 이용한 예외처리
        String interestThird

) implements SignUpRequest { }
