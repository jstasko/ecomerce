package sk.stasko.ecomerce.common.exception;

public class ProductNotAvailableException extends RuntimeException{
    public ProductNotAvailableException(String message) {
        super(message);
    }
}
