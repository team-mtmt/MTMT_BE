package mtmt.MTMT_BE.domain.auth.domain.repository;

import mtmt.MTMT_BE.domain.auth.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
