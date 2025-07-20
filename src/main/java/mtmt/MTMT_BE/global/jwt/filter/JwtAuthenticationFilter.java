package mtmt.MTMT_BE.global.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtmt.MTMT_BE.global.exception.domain.auth.UnauthorizedException;
import mtmt.MTMT_BE.global.jwt.JwtTokenProvider;
import mtmt.MTMT_BE.global.security.CustomUserDetailService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


@Component // Spring Bean 으로 등록
@RequiredArgsConstructor
@Slf4j // 로깅 어노테이션
// OncePerRequestFilter를 상속받는 Jwt를 위한 필터를 등록하는 클래스
// OncePerRequestFilter: 해당 클래스를 상속받은 클래스가 요청 한번당 해당 필터를 한번 실행할 수 있도록 함
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider; // JwtTokenProvider Bean 주입
    private final CustomUserDetailService userDetailsService; // CustomUserDetailService Bean 주입
    private final AntPathMatcher pathMatcher = new AntPathMatcher(); // url 이나 파일 경로가 일치하는 확인하는 Matcher

    // Jwt 인증이 필요없는 api end point들(화이트 리스트)
    private static final String[] PERMITTED_PATHS = {
            "/auth/signup",
            "/auth/login",
            "/health",
    };

    // Filtering 되면 안되는 작업들을 설정하는 메서드
    // Spring Security Config 에서 authorizeHttpRequests를 설정하는 방법도 있지만, 해당 방법과 이 방법의 각각 차이점과 장단점이 존재함. 공부해보면 좋을 듯
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // OPTIONS 요청은 CORS preflight 이므로 필터링 제외
        if ("OPTIONS".equals(method)) {
            return true;
        }

        // 명시적으로 허용된 경로만 제외
        return Arrays.stream(PERMITTED_PATHS)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }


    // 수행하고 싶은 필터 작업을 수행할 수 있또록 하는 메서드
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, // HttpServletRequest.class: HTTP request를 담고 있는 클래스
                                    @NonNull HttpServletResponse response, // HttpServletRequest.class: HTTP response를 담는 클래스
                                    @NonNull FilterChain filterChain // FilterChain: Spring의 Filter 들을 체인처럼 연결해놓은 클래스
                                    ) throws ServletException, IOException { // doFilterInternal을 오버라이딩시에  ServletException, IOException를 예외처리하거나 예외를 던져야 함

        String jwt = getJwtFromRequest(request); // jwt를 요청으로부터 추출해홈

        // jwt가 validate를 통과하지 못하면 유효하지 않은 jwt 이므로, 예외 발생
        if (!tokenProvider.validateToken(jwt)) throw new UnauthorizedException("Invalid JWT token");

        String username = tokenProvider.getUsernameFromToken(jwt); // username을 jwt 로부터 추출
        String tokenType = tokenProvider.getTokenType(jwt); // tokenType(access, refresh)을 jwt로 부터 추출

        // 엑세스 토큰이라면 조건문 실행
        if ("ACCESS".equals(tokenType)) {
            // UserDetails를 username 기반으로 객체 생성
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // userDetails가 null 이면 jwt에 담긴 Username이 잘못된 것 이므로, 예외 발생
            if (userDetails == null) throw new UnauthorizedException("User not found for JWT token");

            // UsernamePasswordAuthenticationToken: UserDetails 객체를 기반으로 사용자를 인증하는 Spring Security 클래스
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // request의 IP, 세션, 사용자 정보 등을 Authentication 객체에 포함
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Spring Security에 authentication을 인증 객체로 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }


        // filterChain에 등록된 다음 필터 호출.
        filterChain.doFilter(request, response);
    }

    // Jwt를 요청으로부터 추출해오는 메서드
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // jwt가 담긴 헤더인 Authorization을 request로 부터 추출해옴

        // bearerToken이 Null 이라면 예외 발생
        if (!StringUtils.hasText(bearerToken)) {
            throw new UnauthorizedException("Authorization header is missing");
        }

        // bearerToken이 "Bearer "(prefix)로 시작하지 않는다면 예외 발생
        if (!bearerToken.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization header must start with 'Bearer '");
        }

        // "Bearer "(prefix)를 jwt 에서 제외시킨 형식으로 오직 jwt 만을 반환
        return bearerToken.substring(7);
    }
}
