package sk.stasko.ecomerce.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EntityAlreadyExists extends RuntimeException {
    public EntityAlreadyExists(String message) {
        super(message);
    }
}
