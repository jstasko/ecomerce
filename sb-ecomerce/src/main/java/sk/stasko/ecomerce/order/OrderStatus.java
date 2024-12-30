package sk.stasko.ecomerce.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    C("Cancelled"),
    P("Pending"),
    S("Successful");

    private final String orderStatus;
}
