package sk.stasko.ecomerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    @Query("SELECT c FROM CartEntity c WHERE c.user.email = :email")
    Optional<CartEntity> findCartByEmail(@Param("email") String email);

    @Query("Select c FROM CartEntity c WHERE c.user.username = :username")
    Optional<CartEntity> findByUsername(@Param("username") String username);

    @Query("SELECT DISTINCT c from CartEntity c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.id = :productId ")
    List<CartEntity> findCartsByProductId(@Param("productId") Long productId);
}
