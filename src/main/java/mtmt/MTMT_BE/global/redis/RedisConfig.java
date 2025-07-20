package mtmt.MTMT_BE.global.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

// Redis 관련 설정들을 위한 클래스
@Configuration
public class RedisConfig {

    // Redis가 실행되는 host 이름
    @Value("${spring.data.redis.host}")
    private String host;

    // Redis가 실행되는 포트번호
    @Value("${spring.data.redis.port}")
    private int port;

    // 호스트, 포트를 기반으로 Redis Connection 설정
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    // RedisTemplate 객체: 최종적으로 모든 Redis 설정들을 포함하여 스프링 빈으로 등록되는 객체
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
