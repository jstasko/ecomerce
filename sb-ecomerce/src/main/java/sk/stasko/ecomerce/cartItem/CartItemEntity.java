package sk.stasko.ecomerce.cartItem;

import jakarta.persistence.*;
import lombok.*;
import sk.stasko.ecomerce.cart.CartEntity;
import sk.stasko.ecomerce.common.entity.BaseEntity;
import sk.stasko.ecomerce.product.ProductEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class CartItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_product_id")
    private Long cartProductId;

    private BigDecimal discount;
    private BigDecimal productPrice;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    public boolean isEmpty() {
        return quantity == 0;
    }

    public BigDecimal getCalculatedPrice() {
        return productPrice.multiply(BigDecimal.valueOf(this.quantity));
    }
}
