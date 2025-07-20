package mtmt.MTMT_BE.domain.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import mtmt.MTMT_BE.domain.auth.application.dto.login.LoginRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.login.LoginResponse;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.MenteeSignUpRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.MentorSignUpRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.SignUpRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.SignUpResponse;
import mtmt.MTMT_BE.domain.auth.application.service.UserLoginService;
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
@Tag(name = "Auth API", description = "계정 관련 API") // 스웨거 문서화
public class AuthController {

    private final UserSignUpService userSignUpService;

    private final UserLoginService userLoginService;

    // 값 검증을 위해 Validator 의존성 주입
    private final Validator validator;

    private final ObjectMapper objectMapper;

    @PostMapping("/signup") // "baseurl/auth/signup" 해당 url을 가지는 http request를 해당 메서드로 매핑
    @Operation(
            summary = "사용자 회원가입 API",
            description = "사용자 정보와 멘토/멘티 여부를 통해 사용자 회원가입을 진행합니다.",
            parameters = {
                    @Parameter(
                            name = "role",
                            in = ParameterIn.QUERY,
                            description = "회원가입 역할 여부 (mentor 또는 mentee)",
                            required = true,
                            schema = @Schema(type = "string", allowableValues = {"mentor", "mentee"})
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 사용자 회원가입 진행됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                    name = "회원가입 성공",
                                    summary = "회원가입 성공시 응답 예시",
                                    value = """
                                            {
                                                "status": 200,
                                                "message": "success",
                                                "data": {
                                                    "email": "hello@dsm.hs.kr",
                                                    "name": "손희찬",
                                                    "role": "MENTOR",
                                                    "thumbnail": null,
                                                    "location": null,
                                                    "birthDate": "2000-01-02",
                                                    "gender": "MALE",
                                                    "age": 25
                                                }
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않음",
                    content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject(
                                name = "유효성 검사 실패",
                                summary = "Validation 실패 예시",
                                value = """
                                {
                                  "status": 400,
                                  "message": "Validation failed",
                                  "data": {
                                    "major": "Invalid major type"
                                  }
                                }
                                """
                        )
                    )),
                    @ApiResponse(responseCode = "409", description = "이메일이 이미 존재함",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "중복된 이메일",
                                    summary = "회원가입 이메일 중복시 예시",
                                    value = """
                                            {
                                                "status": 409,
                                                "message": "Email already exists",
                                                "data": null
                                            }
                                            """
                            )
                    )),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    summary = "서버 오류 응답 예시",
                                    value = """
                                            {
                                                "status": 500,
                                                "message": "Internal Server Error",
                                                "data": null
                                            }
                                            """
                            )
                    ))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "mode 파라미터 값에 따라 Body JSON이 다름",
                    required = true,
                    content = @Content(
                            schema = @Schema(type = "object"),
                            examples = {
                                    @ExampleObject(
                                            name = "멘토 예시",
                                            summary = "멘토 회원가입 요청 예시",
                                            value = """
                            {
                              "email": "mentor@example.com",
                              "password": "StrongPass123",
                              "name": "홍길동",
                              "gender": "MALE",
                              "birthDate": "1990-01-01",
                              "major": "ART_DRAWING"
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "멘티 예시",
                                            summary = "멘티 회원가입 요청 예시",
                                            value = """
                            {
                              "email": "mentee@example.com",
                              "password": "SecurePass456",
                              "name": "김영희",
                              "gender": "FEMALE",
                              "birthDate": "2002-05-20",
                              "interestFirst": "SPORT_BASKETBALL",
                              "interestSecond": "SPORT_BASEBALL",
                              "interestThird": "ACADEMIC_MIDTERM"
                            }
                            """
                                    )
                            }
                    )
            )
    )
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



    @PostMapping("/login")
    @Operation(
            summary = "사용자 로그인 API",
            description = "사용자의 이메일과 비밀번호를 통해 사용자 로그인을 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 사용자 로그인 진행됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "로그인 성공",
                                            summary = "로그인 성공시 응답 예시",
                                            value = """
                                                {
                                                    "status": 200,
                                                    "message": "success",
                                                    "data": {
                                                        "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJoZWxsb0...",
                                                        "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJoZ...",
                                                        "userInfo": {
                                                            "name": "손희찬",
                                                            "email": "hello@dsm.hs.kr",
                                                            "role": "MENTOR"
                                                        },
                                                        "loginAt": "2025-07-20T16:49:36.260956"
                                                    }
                                                }
                                            """
                                    ))),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않음",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "유효성 검사 실패",
                                            summary = "Validation 실패 예시",
                                            value = """
                                {
                                  "status": 400,
                                  "message": "Validation failed",
                                  "data": {
                                    "major": "Invalid email type"
                                  }
                                }
                                """
                                    )
                            )),
                    @ApiResponse(responseCode = "401", description = "인증이 거부됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "잘못된 요청(권한 없음)",
                                            summary = "이메일이나 비밀번호가 옳바르지 않음",
                                            value = """
                                                {
                                                    "status": 401,
                                                    "message": "UnAuthenticationException user : Bad credentials",
                                                    "data": null
                                                }
                                            """
                                    )
                            )),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "서버 오류",
                                            summary = "서버 오류 응답 예시",
                                            value = """
                                            {
                                                "status": 500,
                                                "message": "Internal Server Error",
                                                "data": null
                                            }
                                            """
                                    )
                            ))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "mode 파라미터 값에 따라 Body JSON이 다름",
                    required = true,
                    content = @Content(
                            schema = @Schema(type = "object"),
                            examples = {
                                    @ExampleObject(
                                            name = "로그인 요청 본문",
                                            summary = "로그인 요청 본문 예시",
                                            value = """
                                                    {
                                                      "email": "mentor@example.com",
                                                      "password": "StrongPass123"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return userLoginService.login(loginRequest);
    }
}
