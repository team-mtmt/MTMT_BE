package mtmt.MTMT_BE.global.response;

import lombok.Getter;
import lombok.Setter;

// 백엔드 서버는 일관적이면서, 의미있는 HTTP Response를 제공해야합니다.
// 따라서 이렇게 ApiResponse와 같은 클래스를 이용해, 모든 응답이 해당 클래스를 거쳐가도록 하면, 일관적이고 의미있는 HTTP Response를 반환할 수 있습니다.
@Getter // 필드를 수정 및 접근 하기 위한 세터와 게터
@Setter
public class ApiResponse<T> {

    // HTTP status code를 담기 위한 프로퍼티
    private int status;

    // 보다 자세한 HTTP Response를 제공하기 위한 message
    private String message;

    // 실제 Response에 필요한 data 들을 가지고 있는 프로퍼티
    private T data;

    public ApiResponse() {}

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // 성공한 응답을 생성하기 위한 메서드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    // 오류가 발생한 응답을 생성하기 위한 메서드
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
