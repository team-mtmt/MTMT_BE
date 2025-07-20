package mtmt.MTMT_BE.domain.mentor.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import mtmt.MTMT_BE.domain.mentor.domain.type.type.RatingSection;
import mtmt.MTMT_BE.domain.user.domain.entity.User;
import mtmt.MTMT_BE.domain.user.domain.type.Category;

@Entity
@Table(name = "mentor_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // OneToOne 연관관계 설정, 하나의 user에 대해 하나의 mentor 엔티티만 존재함
    // OneToOne 공부 키워드: 양방향 일대일 관계에서 Lazy Loading 적용 여부
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User userId;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "major", nullable = false, columnDefinition = "VARCHAR(255)")
    @Enumerated(EnumType.STRING)
    private Category major;

    @Column(name = "rating", nullable = false, columnDefinition = "INT")
    private Integer rating = 0;

    @Column(name = "rating_section", nullable = false, columnDefinition = "VARCHAR(30)")
    private RatingSection ratingSection;

    public static RatingSection calculateRatingSectionFromRating(Integer rating) {
        if (0 <= rating && rating <= 400) return RatingSection.PENTAGON;
        else if (401 <= rating && rating <= 700) return RatingSection.SQUARE;
        else if (701 <= rating && rating <= 900) return RatingSection.TRIANGLE;
        else if (901 <= rating && rating <= 1000) return RatingSection.CIRCLE;
        else throw new IllegalArgumentException("Invalid rating");
    }
}
