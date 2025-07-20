package mtmt.MTMT_BE.global.exception.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// CustomException.class: 스프링에서 발생하지 않고, 우리 애플리케이션만의 예외 (예. 멘티를 찾을수 없습니다 등) 처리를 위한 클래스
// 모든 애플리케이션 예외가 해당 클래스를 상속받는 구조로 설계 하기 위해 추상 클래스로 선언
public abstract class CustomException extends RuntimeException {

    @Getter
    // HttpStatus: 다양한 HttpStatus를 담고있는 Enum
    private final HttpStatus httpStatus;

    @Getter
    // Http Status code를 저장할 필드
    private final int status;

    // 클라이언트에게 보다 정확한 메시지를 전달하기 위한 필드
    private final String message;

    // 생성자는 protected, 상속관계 클래스만 호출하도록 설정
    protected CustomException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.status = httpStatus.value(); // HttpStatus Enum은 value 라는 필드로 int형 status code를 가짐
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
