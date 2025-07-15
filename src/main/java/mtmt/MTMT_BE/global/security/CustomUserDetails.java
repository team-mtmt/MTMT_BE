package mtmt.MTMT_BE.global.security;

import lombok.RequiredArgsConstructor;
import mtmt.MTMT_BE.domain.user.domain.User;
import mtmt.MTMT_BE.domain.user.domain.type.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
// 사용자의 정보를 담는 Spring Security의 객체인 UserDetails를 상속받아, 우리 서비스의 User 버전으로 재구성한 클래스
public class CustomUserDetails implements UserDetails {

    private final User user;

    // 사용자 권한 목록을 'ROLE_권한' 형태로 반환하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        ); // List<GrantedAuthority> 타입의 불변 리스트
    }

    // 비밀번호를 반환하는 메서드 (DB에 저장된 값이므로 해싱되어있음)
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 사용자의 아이디(username)를 반환하는 메서드 해당 서버의 아이디는 이메일이므로, user email을 반환
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // 계정 만료 여부, 미사용이므로 true반환
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부: 현재 정책에서 사용하지 않음
    }

    // 계정 잠금 여부, 미사용이므로 true반환
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 인증 정보 만료 여부, 사용하지 않음
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부, 사용하지 않음
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 추가 정보 접근용 커스텀 메서드
    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public Role getRole() {
        return user.getRole();
    }
}
