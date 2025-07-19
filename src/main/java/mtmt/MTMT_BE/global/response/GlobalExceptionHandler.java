package mtmt.MTMT_BE.global.response;

import mtmt.MTMT_BE.global.exception.utils.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // Application Controller에 전역 설정
public class GlobalExceptionHandler {

    // @ExceptionHandler 어노테이션: 파라미터로 전달받은 예외 class가 발생할때 해당 메서드를 실행하도록 설정
    // Exception.class: Spring의 예외를 담당하는 최상위 레벨 클래스
    // 해당 GlobalExceptionHandler 클래스에 ExceptionHandler로 지정되지 않은 특별한 예외는 여기서 처리됨
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        ApiResponse<String> response = ApiResponse.error(500, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // IllegalArgumentException.class: 메서드에 유효하지 않은 인자가 전달되면 해당 예외가 발생함
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponse<String> response = ApiResponse.error(400, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomException(CustomException ex) {
        ApiResponse<String> response = ApiResponse.error(ex.getStatus(), ex.getMessage());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }
}
