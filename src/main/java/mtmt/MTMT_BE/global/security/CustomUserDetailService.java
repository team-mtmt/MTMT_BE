package mtmt.MTMT_BE.global.security;

import lombok.RequiredArgsConstructor;
import mtmt.MTMT_BE.domain.user.domain.User;
import mtmt.MTMT_BE.domain.user.domain.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;


    // Email에 대한 User를 기반으로 CustomUserDetail 객체 생성 없을시 UsernameNotFoundException(Spring Security 내장) 호출
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. email: " + email));

        // 조회된 User를 기반으로 CustomUserDetails 객체 생성
        return new CustomUserDetails(user);
    }
}
