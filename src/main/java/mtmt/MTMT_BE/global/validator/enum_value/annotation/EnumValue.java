package mtmt.MTMT_BE.global.validator.enum_value.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import mtmt.MTMT_BE.global.validator.enum_value.EnumValueValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// 해당 클래스는 커스텀 어노테이션을 만든 클래스이다.
// 많은 Framework과 Library 에서는 개발의 편의성을 위해 어노테이션을 제공한다.
// 하지만 우리가 원하는 모든 어노테이션이 제공되는 것은 아니다. 따라서 우리가 필요에 따라서 직접 개발해야할 필요도 있다.
// Spring Validation 라이브러리는 값 검증과 관련된 다양한 어노테이션을 제공하나, Enum 의 값을 Enum 에 명시된 값인지 체크하는 어노테이션이나 기능은 제공하지않는다.
// 따라서 해당 어노테이션은 Enum 필드의 값이 Enum 에 등록된 값인지 체크하는 어노테이션이다.
@Target({ ElementType.FIELD, ElementType.PARAMETER }) // @Target 어노테이션: 어노테이션이 적용될 범위를 지정, 해당 어노테이션의 지정 범위는 필드 또는 매개변수
@Retention(RetentionPolicy.RUNTIME) // 어노테이션이 어느 시점에 동작할것인지 지정. 해당 어노테이션은 RUNTIME에 동작한다(Http Request는 application Runtime에 등록되기 때문)
@Constraint(validatedBy = EnumValueValidator.class) // @Constraint: 해당 어노테이션이 유효성 검사(validation) 어노테이션임을 알림. validatedBy 매개변수를 통해 validation 로직을 가진 클래스를 지정
public @interface EnumValue {
    Class<? extends Enum<?>> enumClass(); // 검증할 Enum class
    String message() default "Wrong enum value"; // 값 검증 실패시 전달할 message
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean ignoreCase() default false; // 대소문자 구분 여부
}