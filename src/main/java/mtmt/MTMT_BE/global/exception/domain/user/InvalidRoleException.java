package mtmt.MTMT_BE.global.exception.domain.user;

import mtmt.MTMT_BE.global.exception.utils.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidRoleException extends CustomException {
    public InvalidRoleException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
