package sk.stasko.ecomerce.cartItem;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sk.stasko.ecomerce.cart.CartDto;
import sk.stasko.ecomerce.cart.CartEntity;
import sk.stasko.ecomerce.cart.CartService;
import sk.stasko.ecomerce.common.exception.EntityAlreadyExists;
import sk.stasko.ecomerce.common.exception.ResourceNotFoundException;
import sk.stasko.ecomerce.product.ProductEntity;
import sk.stasko.ecomerce.product.ProductService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository iCartItemRepository;
    private final ProductService iProductService;
    private final CartService cartService;

    public CartItemEntity findByProductIdAndCartId(Long productId, Long cartId) {
        return iCartItemRepository.findByProductIdAndCartId(productId, cartId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId,cartId", productId + "," + cartId));
    }

    @Override
    @Transactional
    public CartDto updateQuantityOfProductInCart(Long productId, Integer quantity) {
        var userCart = this.cartService.getUserCart();
        CartEntity cartEntity = this.cartService.findById(userCart.getId());
        ProductEntity productEntity = iProductService.retrieveCorrectProduct(productId, quantity);
        CartItemEntity cartItemEntity = findByProductIdAndCartId(productId, cartEntity.getId());

        cartItemEntity.setProductPrice(productEntity.getSpecialPrice());
        cartItemEntity.setQuantity(cartItemEntity.getQuantity() + quantity);
        cartItemEntity.setDiscount(productEntity.getDiscount());
        var operationPrice = cartItemEntity.getProductPrice().multiply(BigDecimal.valueOf(quantity));
        cartEntity.setTotalPrice(cartEntity.getTotalPrice().add(operationPrice));

        var savedCart = this.cartService.saveCartEntity(cartEntity);
        CartItemEntity updatedCartItemEntity = iCartItemRepository.save(cartItemEntity);
        if (updatedCartItemEntity.isEmpty()) {
            iCartItemRepository.deleteById(updatedCartItemEntity.getCartProductId());
        }

        return savedCart;
    }

    @Override
    @Transactional
    public void removeSpecificProductFromCart(Long productId, Long cartId) {
        var cartEntity = this.cartService.findById(cartId);
        var cartItemEntity = findByProductIdAndCartId(productId, cartId);

        var valueToSubtract = cartItemEntity.getProductPrice().multiply(BigDecimal.valueOf(cartItemEntity.getQuantity()));
        cartEntity.setTotalPrice(cartEntity.getTotalPrice().subtract(valueToSubtract));

        iCartItemRepository.deleteCartItemEntityByProductIdAndCartId(productId, cartId);
    }

    @Override
    @Transactional
    public CartDto addProductAndQuantityToTheCart(Long productId, Integer quantity) {
        ProductEntity productEntity = iProductService.retrieveCorrectProduct(productId, quantity);
        CartEntity cart = cartService.createCartEntity();
        createCartItemEntity(productEntity, cart, quantity);

        var contractedBigDecimalPrice = productEntity.getSpecialPrice().multiply(BigDecimal.valueOf(quantity));
        cart.setTotalPrice(contractedBigDecimalPrice);

        return cartService.saveCartEntity(cart);
    }

    private void createCartItemEntity(ProductEntity productEntity, CartEntity cartEntity, Integer quantity) {
        if (iCartItemRepository.existsByProductIdAndCartId(productEntity.getId(), cartEntity.getId())) {
            throw new EntityAlreadyExists("Product with productName " + productEntity.getProductName() + " already exists in cart");
        }

        CartItemEntity newCartItem = new CartItemEntity();
        newCartItem.setProduct(productEntity);
        newCartItem.setCart(cartEntity);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(productEntity.getDiscount());
        newCartItem.setProductPrice(productEntity.getSpecialPrice());
        iCartItemRepository.save(newCartItem);

        cartEntity.getCartItems().add(newCartItem);

        log.info("CartItem consist of productName {} was placed into Cart", productEntity.getProductName());
    }
}
