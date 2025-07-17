package mtmt.MTMT_BE.global.jwt;

import mtmt.MTMT_BE.domain.auth.domain.entity.RefreshToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import mtmt.MTMT_BE.global.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Component // Bean 객체로 등록
@Slf4j // 로깅을 위한 어노테이션 Slf4j(Simple Logging Facade for Java):
// JwtTokenProvider.class: Jwt 토큰의 생성과 인증, 권한 관리, claims 추출 등 Jwt를 제공하고 사용하기 위한 기능들을 수행하는 클래스
public class JwtTokenProvider {

    private final SecretKey key; // Signature key
    private final long accessTokenExpiration; // access Token 유효기간 (보통 짧음)
    private final long refreshTokenExpiration; // refresh Token 유효기간 (보통 김)

    // 생성자 함수 선언
    public JwtTokenProvider(
            // @Value 어노테이션: application 설정파일(.yml, .properties 등)에 정의 된 값을 필드에 주입하기 위한 어노테이션
            @Value("${jwt.secret}") String secretKey, // application 파일의 secret을 해당 클래스의 변수 secretKey에 할당
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration, // application 파일의 secret을 해당 클래스의 변수 key에 할당
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes()); // 문자열 Secret을 바이트로 변환하여 jwt 서명행성
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // Access Token(jwt)과 Refresh Token(jwt) 쌍을 가진 객체인 JwtTokens 객체를 생성하는 메서드
    // Authentication에 담긴 정보를 기반으로 jwt를 생성함
    public JwtTokens generateTokens(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal(); // authentication 기반으로 CustomUserDetails 객체 생성
        LocalDateTime now = LocalDateTime.now(); // 현재 시간을 now 변수에 할당

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")); // 사용자가 가지고 있는 권한 목록을 콤마로 연결된 하나의 문자열로 변경 (예. ROLE_MENTOR,ROLE_MENTEE)

        // AccessToken 만료 시간 계산 이후 Date 타입으로 변환해서 변수에 할당
        Date accessTokenExpiresAt = Date.from(now.plusSeconds(accessTokenExpiration / 1000)
                .atZone(ZoneId.systemDefault()).toInstant());
        // RefreshToken 만료 시간 계산 이후 Date 타입으로 변환해서 변수에 할당
        Date refreshTokenExpiresAt = Date.from(now.plusSeconds(refreshTokenExpiration / 1000)
                .atZone(ZoneId.systemDefault()).toInstant());

        // Jwts 빌더패턴을 통해 accessToken 생성
        String accessToken = Jwts.builder()
                .subject(userDetails.getUsername()) // subject: 사용자의 식별자(email)를 설정
                .claim("userId", userDetails.getId()) // userId: 사용자의 아이디를 토큰에 포함
                .claim("authorities", authorities) // authorities: 사용자의 권한을 토큰에 포함
                .claim("tokenType", "ACCESS") // tokenType: JWT 토큰이 어떤 유형인지(access, refresh) 설명하는 claim
                .issuedAt(new Date()) // 토큰이 발급된 시점을 현재로 설정
                .expiration(accessTokenExpiresAt) // 토큰 만료시간 설정
                .signWith(key, Jwts.SIG.HS512) // 비밀 키(key)를 알고리즘을 이용해 서명
                .compact(); // 토큰 문자열 생성

        // Jwts 빌더패턴을 통해 refreshToken 생성
        String refreshToken = Jwts.builder()
                .subject(userDetails.getUsername()) // subject: 사용자의 식별자(email)를 설정
                .claim("userId", userDetails.getId()) // userId: 사용자의 아이디를 토큰에 포함
                .claim("tokenType", "REFRESH") // tokenType: JWT 토큰이 어떤 유형인지(access, refresh) 설명하는 claim
                .issuedAt(new Date()) // 토큰이 발급된 시점을 현재로 설정
                .expiration(refreshTokenExpiresAt) // 토큰 만료시간 설정
                .signWith(key, Jwts.SIG.HS512) // 비밀 키(key)를 알고리즘을 이용해 서명
                .compact(); // 토큰 문자열 생성

        // JwtToken 객체에 생성된 access_token 과 refresh_token을 담아 반환
        return JwtTokens.builder()
                .accessToken(accessToken) // access_token을 할당
                .refreshToken(refreshToken) // refresh_token을 할당
                // JwtTokens 객체에는 access 토큰과 refresh 토큰의 만료시간을 timezone을 고려한 시간을 기준으로 넘김: 클라이언트, 외부에 더욱 정확한 정보를 제공하기 위해
                .accessTokenExpiresAt(LocalDateTime.ofInstant(accessTokenExpiresAt.toInstant(), ZoneId.systemDefault()))
                .refreshTokenExpiresAt(LocalDateTime.ofInstant(refreshTokenExpiresAt.toInstant(), ZoneId.systemDefault()))
                .build();
    }

    // jwt 에서 username을 추출하는 메서드
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject(); // username은 subject에 담겨있음
    }

    // jwt 에서 user id을 추출하는 메서드
    public Long getUserIdFromToken(String token) {
        return getClaims(token).get("userId", Long.class); // token의 userId 값을 리턴
    }

    // 토큰 검증하는 메서드(이 토큰이 유효한지)
    public boolean validateToken(String token) {
        try {
            getClaims(token); // 토큰이 만료돠었거나 위조되었으면 exception 발생
            return true; // 아무런 exception 케이스 없이 통과한다면 ture(인증됨)을 반환
        } catch (JwtException | IllegalArgumentException e) { // 예외 발생시
            log.error("Invalid JWT token: {}", e.getMessage()); // 옳바르지 않은 jwt 이므로 logging
            return false; // false(인증되지 않음) 반환
        }
    }

    // 토큰 만료되었는지 확인하는 메서드
    public boolean isTokenExpired(String token) {
        // 만료날자가 오늘 날짜보다 이전인지 확인 만료날짜가 오늘보다 이전이면 true(만료됨) 반환
        return getClaims(token).getExpiration().before(new Date());
    }

    // 토큰 타입을 반환하는 메서드
    public String getTokenType(String token) {
        return getClaims(token).get("tokenType", String.class); // 토큰이 access 인지 refresh 인지 반환
    }

    // claims를 파싱후 추출하는 메서드
    private Claims getClaims(String token) {
        return Jwts.parser() // 문자열을 읽고 검증하기 위한 빌더
                .verifyWith(key) // key를 지정
                .build() // 파서 빌드 완료
                .parseSignedClaims(token) // 파서를 서명 검증과 함께 파싱: 서명이 유효하지 않거나 위조되었으면 예외 발생
                .getPayload(); // 파싱 결과에서 claim 추출
    }

    // RefreshToken 객체만을 생성하는 메서드
    public RefreshToken createRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // refresh Token이 자동으로 만료되어 Redis 에서 사라질 시점을 설정한다. ttl: Time To Live
        long ttlSeconds = refreshTokenExpiration / 1000; // 밀리초 → 초 변환
        LocalDateTime now = LocalDateTime.now();

        // Refresh Token 생성
        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", userDetails.getId())
                .claim("tokenType", "REFRESH")
                .issuedAt(new Date())
                .expiration(Date.from(now.plusSeconds(ttlSeconds)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key, Jwts.SIG.HS512)
                .compact();

        // RefreshToken 객체(Redis 엔티티) 타입으로 반환
        return RefreshToken.builder()
                .email(userDetails.getUsername())
                .token(token)
                .ttl(ttlSeconds)
                .build();
    }

}
