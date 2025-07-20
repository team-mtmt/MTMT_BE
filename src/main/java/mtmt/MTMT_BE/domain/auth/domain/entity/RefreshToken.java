package mtmt.MTMT_BE.domain.auth.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@RedisHash // RedisHash: 해당 어노테이션이 등록된 클래스를 Redis Entity로 등록 시킴
public class RefreshToken {

    @Id // email을 키(key)값으로 설정
    private String email;

    @Indexed // 해당 필드는 redis 에서 인덱싱함 -> 검색가능
    private String token;

    @TimeToLive // TimeToLive: 특정 시간이 지나면 해당 객체가 만료되도록 함
    private Long ttl;

}
