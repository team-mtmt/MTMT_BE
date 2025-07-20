package mtmt.MTMT_BE.global.swagger;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Swagger 관련 설정을 하기 위한 클래스
// Swagger란? RESTFul API 서비스 문서화를 편하게 해주는 프레임웍
@Configuration
public class SwaggerConfig {

    // 설정은 OpenAPI 객체 형태로 return 해야함
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MTMT Backend API Docs with Swagger") // 문서 제목 설정
                        .version("1.0.0") // 문서 버전 설정
                        .description("멘토-멘티 매칭 서비스 MTMT의 백엔드 API 문서를 Swagger를 이용해 확인할 수 있습니다.") // Swagger Docs 설명
                        .contact(new Contact() // 연락처 설정(주로 사내에서 부서가 다를때 이용)
                                .name("관리자")
                                .email("admin@example.com"))
                );
    }
}

