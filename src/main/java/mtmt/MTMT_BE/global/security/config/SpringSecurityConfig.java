package mtmt.MTMT_BE.global.security.config;

import lombok.RequiredArgsConstructor;
import mtmt.MTMT_BE.global.jwt.filter.JwtAuthenticationFilter;
import mtmt.MTMT_BE.global.security.handler.CustomAccessDeniedHandler;
import mtmt.MTMT_BE.global.security.handler.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 해당 클래스가 Spring 설정 파일임을 명시 (Bean 등록 포함)
@RequiredArgsConstructor
public class SpringSecurityConfig {

    // Jwt를 추출하는 커스텀 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // security 관련 filter chain을 설정하기 위한 메서드
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint authenticationEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화

                // Spring Security 관련 exception 들을 처리할 Handler 클래스 목록을 등록
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

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

    // PasswordEncoder로 BCryptPasswordEncoder 를 지정, 매번 무작위 salt 값을 사용함
    // 무작위 salt 값을 사용하는 일방향 해시 함수이므로, 복호화가 불가능함
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
