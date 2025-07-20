package mtmt.MTMT_BE.domain.auth.application.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mtmt.MTMT_BE.global.jwt.JwtTokens;
import mtmt.MTMT_BE.global.security.CustomUserDetails;

import java.time.LocalDateTime;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserInfo userInfo,
        LocalDateTime loginAt
) {
    public LoginResponse(JwtTokens jwtTokens, CustomUserDetails user) {
        this(
                jwtTokens.getAccessToken(),
                jwtTokens.getRefreshToken(),
                new UserInfo(user.getName(), user.getEmail(), user.getRole().toString()),
                LocalDateTime.now()
        );
    }

    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        private String name;
        private String email;
        private String role;
    }
}
