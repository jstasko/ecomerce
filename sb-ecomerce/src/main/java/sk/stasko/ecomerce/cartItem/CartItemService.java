package sk.stasko.ecomerce.cartItem;

import jakarta.transaction.Transactional;
import sk.stasko.ecomerce.cart.CartDto;

public interface CartItemService {
    @Transactional
    CartDto updateQuantityOfProductInCart(Long productId, Integer quantity);
    @Transactional
    void removeSpecificProductFromCart(Long productId, Long cartId);

    @Transactional
    CartDto addProductAndQuantityToTheCart(Long productId, Integer quantity);
}
