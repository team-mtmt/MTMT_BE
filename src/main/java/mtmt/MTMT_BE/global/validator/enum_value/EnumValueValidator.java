package mtmt.MTMT_BE.global.validator.enum_value;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mtmt.MTMT_BE.global.validator.enum_value.annotation.EnumValue;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

// @EnumValue 어노테이션을 위한 validator 클래스
// ConstraintValidator 제네릭을 통해, EnumValue 어노테이션이, String 타입의 값을 검증한다고 선언
public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {
    private Set<String> valueSet; // enum 클래스의 상수를 set type 으로 해당 변수에 할당
    private boolean ignoreCase; // 대소문자 구분 여부

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        // 모든 Enum 클래스의 상수 목록을 가져옴
        valueSet = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(e -> constraintAnnotation.ignoreCase() ? e.name().toUpperCase() : e.name())
                .collect(Collectors.toSet());
        ignoreCase = constraintAnnotation.ignoreCase();
    }

    // 실제 검증을 하는 클래스
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return valueSet.contains(ignoreCase ? value.toUpperCase() : value); // valueSet에 value가 존재하는지 확인. ignoreCase가 True 이면, 케이스는 무시하고 확인
    }
}

