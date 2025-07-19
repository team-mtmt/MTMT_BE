package mtmt.MTMT_BE.global.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

@ControllerAdvice // @ControllerAdvice 어노테이션: 애플리케이션의 컨트롤러에서 발생하는 작업에 전역적으로 설정하기 위한 클래스로 지정
// ResponseBodyAdvice<Object>: 해당 인터페이스를 구현함으로써, HTTP Response가 클라이언트에게 도달하기 전에 응답 본문을 가로챔
public class ApiResponseWrapper implements ResponseBodyAdvice<Object> {

    // ObjectMapper 클래스는 Java 객체를 JSON 문자열로 변환하거나 JSON 문자열을 ObjectMapper 클래스로 변환하기 위한 클래스
    // 해당 클래스에서는, String Type 객체를 JSON 문자열로 변환하기 위해서 사용한다,
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ResponseBodyAdvice 적용여부를 boolean 으로 반환하는 메서드 즉, 모든 HTTP Response Body의 ApiResponseWrapper가 적용됨
    @Override
    public boolean supports(@NonNull MethodParameter returnType, // @NonNull 어노테이션: 오버라이딩한 메서드의 부모 파라미터가 NonNull을 보장한다는 의미
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // ResponseBodyAdvice 적용 여부: True
    }

    // HTTP Response Body를 작성하기 직전에 호출됨
    // 헤딩 메서드 파라미터들에 대한 설명은 IDE 통해서 직접확인할 것
    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {

        // instanceof 연산자: instanceof 예약어 왼쪽에 명시된 객체가 오른쪽 클래스의 인스턴스인지 확인하는 연산자
        if (body instanceof ApiResponse) {
            return body; // 이미 HTTP Body가 ApiResponse의 객체라면 그 상태 그대로 반환
        }

        // spring Boot 응답 형식인지 확인하고, 맞다면 해당 조건 실행
        if (SpringBootErrorDetector.isErrorResponse(body)) {

            // SpringBootErrorConverter를 이용해 ApiResponse 형태로 응답 형식을 변경
            return SpringBootErrorConverter.convertToApiResponse(body);
        }

        // body가 String 객체의 인스턴스 즉, "Hello, World!"와 같은 문자열 형식이라면 해당 조건문 실행
        if (body instanceof String) {
            try { // objectMapper를 이용해서 ApiResponse.success 메서드를 통해 ApiResponse 형식으로 감싸진 응답객체를 JSON 문자열로 반환
                // String 객체는 StringHttpMessageConverter를 호출하기 때문에, JSON 문자열로 반환하는 것임
                return objectMapper.writeValueAsString(ApiResponse.success(body));
            } catch (Exception e) { // 예외 발생한다면, 런타임 에러 발생
                throw new RuntimeException("Failed to wrap String response", e);
            }
        }

        // 이미 ApiResponse의 객체가 아니고, String 문자열도 아닌 일반적인 Java 객체라면 ApiResponse로 감싸서 반환
        return ApiResponse.success(body);
    }

    // Spring Boot의 기본 Error 응답을 체크하기 위한 Inner 클래스
    // Inner Class란?: class 안에 내장된 또 다른 클래스
    private static class SpringBootErrorDetector {

        // 아래 4개의 상수들은 스프링 부트 기본 응답 형식이 가지는 항목들
        private static final String TIMESTAMP_KEY = "timestamp";
        private static final String STATUS_KEY = "status";
        private static final String ERROR_KEY = "error";
        private static final String PATH_KEY = "path";

        // body 객체가 스프링 부트의 기본 오류 응답 형식인지 확인
        static boolean isErrorResponse(Object body) {
            // Spring Boot 응답은 Map 의 인스턴스 타입이므로 값 체크
            if (!(body instanceof Map<?, ?> bodyMap)) {
                return false; // Body가 Map 타입이 아니면 false를 반환
            }

            // Map 의 인스턴스 이면, 스프링 부트 응답이 가지는 항목들을 가지는지 체크
            return hasRequiredErrorFields(bodyMap);
        }

        // 스프링 부트 응답이 가지는 항목을 모두 가지는지 확인해서 true 또는 false로 반환
        private static boolean hasRequiredErrorFields(Map<?, ?> bodyMap) {
            return bodyMap.containsKey(TIMESTAMP_KEY) &&
                    bodyMap.containsKey(STATUS_KEY) &&
                    bodyMap.containsKey(ERROR_KEY) &&
                    bodyMap.containsKey(PATH_KEY);
        }
    }

    // Spring Boot 응답을 ApiResponse 형태로 변환하여 반환하는 클래스
    private static class SpringBootErrorConverter {
        private static final int DEFAULT_ERROR_STATUS = 500; // Status Code가 없을때 사용할 기본 값 500
        private static final String DEFAULT_ERROR_MESSAGE = "Unknown error"; // Message가 없을때 사용할 기본값 Unknown error

        // Spring Boot 응답을 ApiResponse로 convert 하기 위한 메서드
        static ApiResponse<Object> convertToApiResponse(Object errorBody) {
            Map<?, ?> errorMap = (Map<?, ?>) errorBody;

            int statusCode = extractStatusCode(errorMap); // status 코드 설정
            String errorMessage = extractErrorMessage(errorMap); // message 설정

            return ApiResponse.error(statusCode, errorMessage); // statusCode, message를 이용해 ApiResponse 생성
        }

        // StatusCode를 추출하는 메서드
        private static int extractStatusCode(Map<?, ?> errorMap) {
            Object status = errorMap.get("status"); // status Code를 추출해 status 변수에 할당
            return (status instanceof Integer) ? (Integer) status : DEFAULT_ERROR_STATUS; // status가 Integer의 인스턴스라면 그대로 반환, 아니라면 기본값으로 반환
        }

        // Message를 추출하는 메서드
        private static String extractErrorMessage(Map<?, ?> errorMap) {
            Object error = errorMap.get("error"); // message를 담고있는 필드인 error 를 추출해 error 변수에 할당
            return (error instanceof String && !((String) error).isEmpty()) // error가 String의 인스턴스이고, 빈 값이 아니라면 그대로 반환, 아니라면 기본값으로 반환
                    ? (String) error
                    : DEFAULT_ERROR_MESSAGE;
        }
    }
}
