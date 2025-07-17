package mtmt.MTMT_BE.global.security.config;

import lombok.RequiredArgsConstructor;
import mtmt.MTMT_BE.global.jwt.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 해당 클래스가 Spring 설정 파일임을 명시 (Bean 등록 포함)
@RequiredArgsConstructor
public class SpringSecurityConfig {

    // Jwt를 추출하는 커스텀 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // security 관련 filter chain을 설정하기 위한 메서드
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // UsernamePasswordAuthenticationFilter가 실행되기 전에 JWT 필터를 먼저 실행하도록 필터 순서 설정
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // Security 설정을 기반으로 SecurityFilterChain 객체를 반환
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // 인증 처리를 위한 AuthenticationManager를 Bean 등록
        // 주로 로그인 시도 시 사용자 인증 로직에 사용됨
        return config.getAuthenticationManager();
    }

}
