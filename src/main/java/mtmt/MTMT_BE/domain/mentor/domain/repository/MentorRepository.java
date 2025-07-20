package mtmt.MTMT_BE.domain.mentor.domain.repository;

import mtmt.MTMT_BE.domain.mentor.domain.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {
}
