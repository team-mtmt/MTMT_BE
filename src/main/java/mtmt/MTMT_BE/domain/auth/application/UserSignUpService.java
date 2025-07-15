// User SignUp Service를 auth 도메인 하위에 두는 이유?: user 도메인은 조금 더 user와 관련된 책임(마이페이지 조회 등을 담당)
// auth 도메인은 회원가입 부터 로그인, 탈퇴 등의 계정과 관련된 책임을 담당
package mtmt.MTMT_BE.domain.auth.application;

import lombok.RequiredArgsConstructor;
import mtmt.MTMT_BE.domain.user.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service // Bean 등록 및 서비스 클래스로써의 정의를 위한 @Service 어노테이션
@RequiredArgsConstructor // 클래스의 final 필드나 @Nonnull 필드만을 파라미터로 받는 생성자를 자동 생성
public class UserSignUpService {

    // UserRepository 의존성 주입: userRepository 이용하여 DB에 쿼리 생성 및 실행
    private final UserRepository userRepository;

    public void signup() {

    }

}
