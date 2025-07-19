package mtmt.MTMT_BE.domain.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.MenteeSignUpRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.MentorSignUpRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.SignUpRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.SignUpResponse;
import mtmt.MTMT_BE.domain.auth.application.service.UserSignUpService;
import mtmt.MTMT_BE.global.exception.domain.user.InvalidRoleException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController // @RestController 어노테이션?: @ResponseBody 어노테이션 + @Controller 어노테이션의 조합
// @ResponseBody 어노테이션: 해당 어노테이션이 선언된 클래스의 반환값들은 모두 Http Response Body로 파싱됨
// @Controller 어노테이션: 해당 어노테이션이 선언된 클래스는 Controller 로써 Bean 으로 등록됨
@RequestMapping("/auth") // @RequestMapping 어노테이션: 해당 어노테이션이 등록된 클래스 혹은 메서드는 요청에서 해당 경로를 가짐
// 현재는 클래스에 등록된 모든 url 에 "/auth" 해당 경로가 붙게됨
@RequiredArgsConstructor
public class AuthController {

    private final UserSignUpService userSignUpService;

    // 값 검증을 위해 Validator 의존성 주입
    private final Validator validator;

    private final ObjectMapper objectMapper;

    @PostMapping("/signup") // "baseurl/auth/signup" 해당 url을 가지는 http request를 해당 메서드로 매핑
    public SignUpResponse signUp(
            @RequestParam("role") String role, // Http 요청에서 QueryParam 값중 "role"이라는 값을 role 변수에 할당 시킴
            @RequestBody Map<String, Object> body // Http 요청 Body를 담는 변수
    ) {
        // switch-case 문을 이용해, 쿼리 파라미터 role에 따라서, 적절한 객체로 반환한다음에, SignUpRequest로 업캐스팅
        // 다운 캐스팅은 OOP 에서 중요한 개념이니, 아래 코드가 이해될때까지 다운캐스팅에 대해서 정확히 공부할 것.
        SignUpRequest request = switch (role.toLowerCase()) {
            case "mentor" -> objectMapper.convertValue(body, MentorSignUpRequest.class);
            case "mentee" -> objectMapper.convertValue(body, MenteeSignUpRequest.class);
            default -> throw new InvalidRoleException("Role must be 'mentor' or 'mentee'"); // mentor와 mentee 둘다 해당하지 않은 값을 쿼리 파람이 가지고있다면, 예외 발생 시킴
        };

        // @Valid 어노테이션을 통한 request 객체에 대한 유효성 검사는 업캐스팅-다운캐스팅 구조를 이용하는 SignUpRequest에 적용될 수 없음.
        // 따라서 request에 존재하는 필드값을 하나씩 확인하며 값 검증을 해야함
        // validator.validate 메서드를 이용해 validation 라이브러리 어노테이션이 등록된 필드들에 대한 검증을 수행
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // violations 객체가 비어있지 않다면, 값 검증에서 통과하지 못한 필드값이 존재한다는 의미
        if (!violations.isEmpty())
            throw new ConstraintViolationException(violations); // ConstraintViolationException 발생 -> 이후 GlobalExceptionHandler 에서 처리

        return userSignUpService.signUp(request, role.toLowerCase()); // service에 request 와 role을 위임하여 메서드 실행
    }
}
