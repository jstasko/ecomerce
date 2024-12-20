package sk.stasko.ecomerce.cart;

import java.math.BigDecimal;

public interface CartItemProjection {
    Long getCartId();
    BigDecimal getProductPrice();
}
