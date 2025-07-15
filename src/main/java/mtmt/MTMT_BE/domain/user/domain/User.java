// 패키지 선언
package mtmt.MTMT_BE.domain.user.domain;

// 임포트
import jakarta.persistence.*;
import lombok.*;
import mtmt.MTMT_BE.domain.user.domain.type.Gender;
import mtmt.MTMT_BE.domain.user.domain.type.Location;
import mtmt.MTMT_BE.domain.user.domain.type.Role;

@Entity // 데이터 베이스에 테이블과 1대1 매핑되는 엔티티로써 클래스를 지정하기 위한 어노테이션
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 선언  JPA 엔티티는 기본생성자를 필수로 함 (프록시 + 리플렉션 위해서)
// AccessLevel.PROTECTED는, 기본 생성자의 접근 제어자를 protected로 설정함. 불필요한 객체 생성 방지
@AllArgsConstructor // @Builder 사용을 위한 모든 필드를 인자로 받는 생성자 생성
@Builder // Builder 패턴 코드 자동 생성
@Getter // Get 메서드 자동생성 (But, 상황에 따라서 지'양' 해야함)
public class User {

    @Id // 해당 엔티티의 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK를 자동생성하기 위한 어노테이션 @GeneratedValue
    // strategy 매개변수는 PK 생성 전략을 지정, GenerationType.IDENTITY는 DB의 AUTO_INCREMENT를 활성화 시킴
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50)") // null 허용 x, 유니크 키 활성화, 가변타입 50자 컬럼으로 지정
    private String email;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String password; // password는 DB에 평문이 아닌 암호화 된 문자열이 저장됨.

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING) // @Enumerated을 지정해주지 않으면 JPA 는 Enum의 인덱스를 컬럼에 저장함(0, 1, 둥)
    // 따라서 EnumType을 지정해줌으로써, Enum 상수를 DB에 그대로 저장함
    private Role role;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String thumbnail;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private Location location;

    @Column(nullable = false, columnDefinition = "DATE")
    private String birthDate;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private Gender gender;
}