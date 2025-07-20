// User SignUp Service를 auth 도메인 하위에 두는 이유?: user 도메인은 조금 더 user와 관련된 책임(마이페이지 조회 등을 담당)
// auth 도메인은 회원가입 부터 로그인, 탈퇴 등의 계정과 관련된 책임을 담당
package mtmt.MTMT_BE.domain.auth.application.service;

import lombok.RequiredArgsConstructor;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.MenteeSignUpRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.MentorSignUpRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.SignUpRequest;
import mtmt.MTMT_BE.domain.auth.application.dto.signup.SignUpResponse;
import mtmt.MTMT_BE.domain.mentee.domain.entity.Mentee;
import mtmt.MTMT_BE.domain.mentee.domain.repository.MenteeRepository;
import mtmt.MTMT_BE.domain.mentor.domain.entity.Mentor;
import mtmt.MTMT_BE.domain.mentor.domain.repository.MentorRepository;
import mtmt.MTMT_BE.domain.user.domain.entity.User;
import mtmt.MTMT_BE.domain.user.domain.repository.UserRepository;
import mtmt.MTMT_BE.domain.user.domain.type.Category;
import mtmt.MTMT_BE.domain.user.domain.type.Gender;
import mtmt.MTMT_BE.domain.user.domain.type.Role;
import mtmt.MTMT_BE.global.exception.domain.user.EmailAlreadyExistsException;
import mtmt.MTMT_BE.global.exception.domain.user.InvalidRoleException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service // Bean 등록 및 서비스 클래스로써의 정의를 위한 @Service 어노테이션
@RequiredArgsConstructor // 클래스의 final 필드나 @Nonnull 필드만을 파라미터로 받는 생성자를 자동 생성
public class UserSignUpService {

    // UserRepository 의존성 주입: userRepository 이용하여 DB에 쿼리 생성 및 실행
    private final UserRepository userRepository;

    private final MentorRepository mentorRepository;

    private final MenteeRepository menteeRepository;

    private final PasswordEncoder passwordEncoder;

    // @Transactional 어노테이션: 데이터베이스 I/O 가 있는 작업일 때, 트랜잭션이 수행됨
    // 트랜잭션이란?: ACID 특성을 보유하는 데이터에베이스의 작업 단위
    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest, String role) {
        // Role에 따라 회원가입 로직 구분
        switch (role) {
            case "mentor" -> {
                // SignUpRequest 타입의 signUpRequest를 MentorSignUpRequest 타입으로 다운캐스팅
                MentorSignUpRequest mentorSignUpRequest= (MentorSignUpRequest) signUpRequest;

                if (userRepository.existsByEmail(mentorSignUpRequest.email())) throw new EmailAlreadyExistsException("Email already exists");

                // String 타입의 데이터인 birthDate를 formatter를 이용해 LocalDate 타입으로 변경
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate birthDate = LocalDate.parse(mentorSignUpRequest.birthDate(), formatter);

                // 빌더 패턴 사용
                User user = User.builder()
                        .email(mentorSignUpRequest.email())
                        .password(passwordEncoder.encode(signUpRequest.password())) // password를 encoding 해서 저장
                        .name(mentorSignUpRequest.name())
                        .role(Role.MENTOR)
                        .thumbnail(null)
                        .location(null)
                        .birthDate(birthDate)
                        .gender(Gender.valueOf(mentorSignUpRequest.gender()))
                        .age(User.calculateAgeFromBirthDate(birthDate))
                        .build();

                userRepository.save(user);

                final Integer RATING = 200;

                Mentor mentor = Mentor.builder()
                        .userId(user) // 외래키로 user 참조
                        .bio(null)
                        .major(Category.valueOf(mentorSignUpRequest.major()))
                        .rating(RATING)
                        .ratingSection(Mentor.calculateRatingSectionFromRating(RATING))
                        .build();

                mentorRepository.save(mentor);

                return new SignUpResponse(user);
            }

            case "mentee" -> {
                MenteeSignUpRequest menteeSignUpRequest = (MenteeSignUpRequest) signUpRequest;

                if (userRepository.existsByEmail(menteeSignUpRequest.email())) throw new EmailAlreadyExistsException("Email already exists");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate birthDate = LocalDate.parse(menteeSignUpRequest.birthDate(), formatter);

                User user = User.builder()
                        .email(menteeSignUpRequest.email())
                        .password(passwordEncoder.encode(signUpRequest.password())) // password를 encoding 해서 저장
                        .name(menteeSignUpRequest.name())
                        .role(Role.MENTEE)
                        .thumbnail(null)
                        .location(null)
                        .birthDate(birthDate)
                        .gender(Gender.valueOf(menteeSignUpRequest.gender()))
                        .age(User.calculateAgeFromBirthDate(birthDate))
                        .build();

                userRepository.save(user);

                final Integer EXPERIENCE_POINT = 0;

                Mentee mentee = Mentee.builder()
                        .userId(user)
                        .exp(EXPERIENCE_POINT)
                        .level(Mentee.calculateLevelFromExp(EXPERIENCE_POINT))
                        .interestFirst(Category.valueOf(menteeSignUpRequest.interestFirst()))
                        .interestSecond(Category.valueOf(menteeSignUpRequest.interestSecond()))
                        .interestThird(Category.valueOf(menteeSignUpRequest.interestThird()))
                        .build();

                menteeRepository.save(mentee);

                return new SignUpResponse(user);
            }
            default -> throw new InvalidRoleException("Role must be 'mentor' or 'mentee'");
        }
    }
}
