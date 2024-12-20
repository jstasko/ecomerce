package sk.stasko.ecomerce.cartItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stasko.ecomerce.cart.CartDto;
import sk.stasko.ecomerce.product.ProductDto;

import java.math.BigDecimal;

@Data
@NoArgsConstructor @AllArgsConstructor
public class CardItemDto {
    private Long cartItemId;
    private CartDto cart;
    private ProductDto product;
    private Integer quantity;
    private BigDecimal discount;
    private BigDecimal productPrice;
}
