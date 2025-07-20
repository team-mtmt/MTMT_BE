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

    // boolean 값으로 조건에 따른 행이 있는지 찾는 메서드. 실제 JPA 에서 날리는 쿼리는 COUNT(*) SELECT ~ 이런 형식임. 행이 하나라도 존재하면 True, 아니면 False
    boolean existsByEmail(String email);
}
