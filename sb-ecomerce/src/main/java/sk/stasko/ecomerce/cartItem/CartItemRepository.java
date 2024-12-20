package sk.stasko.ecomerce.cartItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    @Query("SELECT COUNT(c) > 0 FROM CartItemEntity c WHERE c.product.id = :productId AND c.cart.id = :cartId")
    boolean existsByProductIdAndCartId(@Param("productId") Long productId, @Param("cartId") Long cartId);

    @Query("SELECT c FROM CartItemEntity c WHERE c.product.id = :productId AND c.cart.id = :cartId")
    Optional<CartItemEntity> findByProductIdAndCartId(@Param("productId") Long productId, @Param("cartId") Long cartId);

    @Query("DELETE FROM CartItemEntity c WHERE c.product.id = :productId AND c.cart.id = :cartId")
    @Modifying
    void deleteCartItemEntityByProductIdAndCartId(@Param("productId") Long productId, @Param("cartId") Long cartId);
}
