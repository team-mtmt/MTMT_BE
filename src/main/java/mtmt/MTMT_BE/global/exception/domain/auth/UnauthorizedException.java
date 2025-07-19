package mtmt.MTMT_BE.global.exception.domain.auth;

import mtmt.MTMT_BE.global.exception.utils.CustomException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
