package sk.stasko.ecomerce.orderItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stasko.ecomerce.order.OrderEntity;
import sk.stasko.ecomerce.product.ProductEntity;

import java.math.BigDecimal;

@Data
@NoArgsConstructor @AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private BigDecimal discount;
    private BigDecimal orderedProductPrice;
    private Integer quantity;
    private OrderEntity orderEntities;
    private ProductEntity productEntity;
}
