package mtmt.MTMT_BE.global.jwt;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JwtTokens {
    private final String accessToken; // 엑세스토큰 Jwt가 담기는 프로퍼티
    private final String refreshToken; // 리프레시토큰 Jwt가 담기는 프로퍼티
    private final LocalDateTime accessTokenExpiresAt; // 엑세스 토큰 만료 DateTime이 담기는 프로퍼티
    private final LocalDateTime refreshTokenExpiresAt; // 리프레시 토큰 만료 DateTime이 담기는 프로퍼티
}
