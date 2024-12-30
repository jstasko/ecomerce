package sk.stasko.ecomerce.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    C("Credit Card"),
    D("Debit Card"),
    P("PayPal"),
    B("Bank Transfer");

    private final String description;
}
