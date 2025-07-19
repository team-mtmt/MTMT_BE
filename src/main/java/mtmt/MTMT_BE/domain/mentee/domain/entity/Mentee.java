package mtmt.MTMT_BE.domain.mentee.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import mtmt.MTMT_BE.domain.user.domain.entity.User;
import mtmt.MTMT_BE.domain.user.domain.type.Category;


@Entity
@Table(name = "mentee_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mentee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // OneToOne 연관관계 설정, 하나의 user에 대해 하나의 mentee 엔티티만 존재함
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User userId;

    @Column(name = "exp", nullable = false, columnDefinition = "INT")
    private Integer exp;

    @Column(name = "level", nullable = false, columnDefinition = "INT")
    private Integer level;

    @Column(name = "interest_first", nullable = false, columnDefinition = "VARCHAR(255)")
    private Category interestFirst;

    @Column(name = "interest_second", nullable = false, columnDefinition = "VARCHAR(255)")
    private Category interestSecond;

    @Column(name = "interest_third", nullable = false, columnDefinition = "VARCHAR(255)")
    private Category interestThird;

    public static Integer calculateLevelFromExp(Integer exp) {
        if (exp == null || exp < 0) {
            return 1;
        }

        return (exp / 100) + 1;
    }
}
