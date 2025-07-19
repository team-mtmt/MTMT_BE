package mtmt.MTMT_BE.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mtmt.MTMT_BE.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// AccessDeniedHandler는 AccessDeniedException을 처리하기 위한 객체이다.
// AccessDeniedException 이란, Spring Security Library의 예외 객체로, Spring Security로 인해 접근이 거부될때 발생하는 예외이다.
// AccessDeniedException은 GlobalExceptionFilter 에서 처리 되지 않기에, 이렇게 CustomAccessDeniedHandler를 통해 ApiResponse 형태로 감싸야한다.
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        // response 정보들 설정
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Response를 ApiResponse로 감싸서 전달: 일관된 예외처리 가능.
        // 이렇게 Spring Security 에서 발행하는 예외도 우리의 응답 구조를 따르게 되었다.
        ApiResponse<String> errorResponse = ApiResponse.error(
                HttpStatus.FORBIDDEN.value(),
                "Access denied"
        );

        // response 전달
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
