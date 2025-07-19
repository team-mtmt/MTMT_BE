package mtmt.MTMT_BE.domain.auth.application.dto.signup;

// sealed: Java 17 부터 도입된 기능으로, 특정 클래스 혹은 인터페이스를 상속하거나 구현할 수 있는 타입을 제한한다
// 해당 클래스에서는 MenteeSignUpRequest, MentorSignUpRequest 만이 해당 인터페이스를 구현할 수 있도록 한다
// 멘티가 회원가입하고, 멘토가 회원가입할때 client로 부터 요청 받아야하는 필드 값이 다르기 때문에, 이렇게 공통적인 필드만 모아놓아, OOP의 장점을 살린 것
public sealed interface SignUpRequest permits MenteeSignUpRequest, MentorSignUpRequest {
    String email();
    String password();
    String name();
    String gender();
    String birthDate();
}
