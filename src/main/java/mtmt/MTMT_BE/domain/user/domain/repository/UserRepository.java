package mtmt.MTMT_BE.domain.user.domain.repository;

import mtmt.MTMT_BE.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Bean 등록 및 레포지토리 클래스로써의 정의를 위한 @Repository 어노테이션
// JPA 레포지토리를 상속받음
// Crud Repository vs JPA repository: CRUD 기능만 제공 vs CRUD 레포지토리를 확장하여 JPA 특화 기능까지 제공
public interface UserRepository extends JpaRepository<User, Long> {

    // Optional 이란?: 특정 객체가 Null이 될 수 있음을 허용(null-safe)
    Optional<User> findByEmail(String email);
}
