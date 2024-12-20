package sk.stasko.ecomerce.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stasko.ecomerce.product.ProductDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
public class CartDto {
    private Long id;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private List<ProductDto> products = new ArrayList<>();
}
