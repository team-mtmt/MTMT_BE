package mtmt.MTMT_BE.global.response;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import mtmt.MTMT_BE.global.exception.utils.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;
import java.util.Map;
import java.util.stream.Collectors;

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

    // HttpMessageNotReadableException: HTTP Body가 잘못된 형식일때 발생하는 예외
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Invalid request body. Please check your JSON format and required fields.";

        // 더 구체적인 에러 메시지 제공
        if (ex.getMessage().contains("Required request body is missing")) {
            message = "Request body is required for this endpoint.";
        } else if (ex.getMessage().contains("JSON parse error")) {
            message = "Invalid JSON format in request body.";
        }

        ApiResponse<String> response = ApiResponse.error(400, message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ConstraintViolationException을 AuthController의 validation 과정에서 발행중임. 따라서 처리하기 위한 메서드 등록
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> fieldErrors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing // 중복 키 처리
                ));

        ApiResponse<Map<String, String>> response = ApiResponse.error(400, "Validation failed");
        response.setData(fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Spring Validation 에서 발생한 오류는 해당 메서드에서 처리
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(Exception ex) {
        String message = "Validation failed. Please check your input: " + ex.getMessage();
        ApiResponse<String> response = ApiResponse.error(400, message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
