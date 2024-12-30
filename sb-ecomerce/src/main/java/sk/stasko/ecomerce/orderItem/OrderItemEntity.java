package sk.stasko.ecomerce.orderItem;

import jakarta.persistence.*;
import lombok.*;
import sk.stasko.ecomerce.order.OrderEntity;
import sk.stasko.ecomerce.product.ProductEntity;

import java.math.BigDecimal;

@Entity @Table(name = "oder_items")
@Getter @ToString @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oder_item_id")
    private Long id;

    private BigDecimal discount;
    private BigDecimal orderedProductPrice;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    public boolean isEmpty() {
        return quantity == 0;
    }

    public BigDecimal getCalculatedPrice() {
        return orderedProductPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
}
