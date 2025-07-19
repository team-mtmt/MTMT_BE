package mtmt.MTMT_BE.domain.mentee.domain.repository;

import mtmt.MTMT_BE.domain.mentee.domain.entity.Mentee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {
}
