package mtmt.MTMT_BE.global.exception.domain.user;

import mtmt.MTMT_BE.global.exception.utils.CustomException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends CustomException {
    public EmailAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
