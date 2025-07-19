package mtmt.MTMT_BE.global.exception.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import mtmt.MTMT_BE.global.exception.utils.CustomException;
import mtmt.MTMT_BE.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Bean 등록
// GlobalExceptionFilter: Spring Security FilterChain에 등록된 필터중, 가장 먼저 실행되어 FilterChain 을 감시하다가, 발생하는 오류를 catch 하고 처리하기 위한 클래스
public class GlobalExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response); // 다음 필터 실행
        } catch (CustomException ex) { // 다음 필터 chain 에서 발생하는 오류중에 Custom Exception이 발생하면 catch
            sendErrorResponse(response, ex.getHttpStatus(), ex.getMessage()); // sendErrorResponse 메서드를 이용해서 response 생성
        } catch (Exception ex) { // 예외 처리 되지 않은 오류가 발생했을때 catch
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error"); // sendErrorResponse 메서드를 이용해서 response 생성
            // 메세지를 Unexpected server error 라고 명시하여 예외처리 되지 않은 오류라는 것을 알림
        }
    }

    // ApiResponse Class를 이용해 발생한 오류를 Http Body로 보내기 위한 메서드
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value()); // status를 int 값인 status.value()로 설정
        response.setContentType("application/json"); // Content type을 json 형식으로 설정

        ApiResponse<?> errorBody = ApiResponse.error(status.value(), message); // error 작성
        String json = objectMapper.writeValueAsString(errorBody); // errorBody를 json 문자열로 변환

        response.getWriter().write(json); // response에 write
    }
}
