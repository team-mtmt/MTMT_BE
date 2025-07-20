package mtmt.MTMT_BE.domain.auth.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mtmt.MTMT_BE.domain.auth.application.dto.login.LoginRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.login.LoginResponse;
import mtmt.MTMT_BE.domain.auth.domain.entity.RefreshToken;
import mtmt.MTMT_BE.domain.auth.domain.repository.RefreshTokenRepository;
import mtmt.MTMT_BE.global.jwt.JwtTokenProvider;
import mtmt.MTMT_BE.global.jwt.JwtTokens;
import mtmt.MTMT_BE.global.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        // Authentication 객체: 사용자의 인증 정보를 담고있는 객체
        // authenticationManger의 메서드인 authenticate에 매개변수로 UsernamePasswordAuthenticationToken을 전달하여, 인증에 성공하면 Authentication 객체를 반환받는다.
        // UsernamePasswordAuthenticationToken: 사용자의 이메일과 패스워드를 기반으로 Authentication 객체를 생성
        // authenticationManger는 이메일과 패스워드를 기반으로 생성된 Authentication 객체가 유효하면 authentication을 그대로 반환한다.
        // 만약 이메일이 다르거나, 패스워드가 다르면 authenticationManger는 AuthenticationException을 발생시킨다. -> 이후 GlobalExceptionHandler 에서 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        // authentication을 통해 현재 user 정보를 userDetails에 담음
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // jwtToken을 authentication 객체를 통해 생성
        JwtTokens jwtTokens = jwtTokenProvider.generateTokens(authentication);

        // refresh Token 변수에 따로 refresh Toke을 담음
        String refreshToken = jwtTokens.getRefreshToken();

        // jwtTokens의 refreshToken 유효기간을 초로 변환하여 Long 타입 ttl 변수에 할당함
        Long ttl = ChronoUnit.SECONDS.between(LocalDateTime.now(), jwtTokens.getRefreshTokenExpiresAt());

        // RefreshToken 객체(Redis 엔티티)를 생성하여, redis에 삽입하고 save 저장
        refreshTokenRepository.save(new RefreshToken(userDetails.getEmail(), refreshToken, ttl));

        // LoginResponse 형식으로 jwtToken, 유저 정보 반환
        return new LoginResponse(jwtTokens, userDetails);
    }
}
