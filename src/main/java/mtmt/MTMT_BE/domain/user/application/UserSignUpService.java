package mtmt.MTMT_BE.domain.user.application;

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
