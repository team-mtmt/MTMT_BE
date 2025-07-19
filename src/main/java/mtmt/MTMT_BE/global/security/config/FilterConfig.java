package mtmt.MTMT_BE.global.security.config;

import mtmt.MTMT_BE.global.exception.filter.GlobalExceptionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration // Spring Security Filter Chain 관련 설정들을 위한 클래스
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<GlobalExceptionFilter> filterRegistrationBean(GlobalExceptionFilter globalExceptionFilter) {
        FilterRegistrationBean<GlobalExceptionFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(globalExceptionFilter);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 가장 먼저 실행되는 Filter 로써 등록
        return bean;
    }
}
