package sk.stasko.ecomerce.cart;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CartOperationValidator implements ConstraintValidator<ValidCartOperation, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && CartOperation.isValid(value);
    }

    @Override
    public void initialize(ValidCartOperation constraintAnnotation) {
        // Initialization if needed
    }
}
