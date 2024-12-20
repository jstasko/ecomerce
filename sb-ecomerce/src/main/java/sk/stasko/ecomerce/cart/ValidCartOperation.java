package sk.stasko.ecomerce.cart;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CartOperationValidator.class)
public @interface ValidCartOperation {
    String message() default "Invalid cart operation. Valid operations are ADD or DELETE.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
