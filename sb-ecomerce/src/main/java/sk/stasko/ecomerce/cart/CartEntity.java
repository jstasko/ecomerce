package sk.stasko.ecomerce.cart;

import jakarta.persistence.*;
import lombok.*;
import sk.stasko.ecomerce.cartItem.CartItemEntity;
import sk.stasko.ecomerce.common.entity.BaseEntity;
import sk.stasko.ecomerce.user.UserEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@ToString
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    private BigDecimal totalPrice = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserEntity user;

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true)
    @ToString.Exclude
    private List<CartItemEntity> cartItems = new ArrayList<>();

    public void recalculateTotalPrice() {
        this.totalPrice = cartItems.stream()
                .map(CartItemEntity::getCalculatedPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addCartItem(CartItemEntity cartItem) {
        this.cartItems.add(cartItem);
        cartItem.setCart(this);
        recalculateTotalPrice();
    }

    public void removeCartItem(CartItemEntity cartItem) {
        this.cartItems.remove(cartItem);
        cartItem.setCart(null);
        recalculateTotalPrice();
    }

    public void setTotalPrice(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.totalPrice = this.totalPrice.add(value);
    }
}
