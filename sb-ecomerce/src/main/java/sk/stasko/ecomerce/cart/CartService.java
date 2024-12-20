package sk.stasko.ecomerce.cart;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.stasko.ecomerce.cartItem.CartItemEntity;
import sk.stasko.ecomerce.cartItem.CartItemRepository;
import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;
import sk.stasko.ecomerce.common.exception.EntityAlreadyExists;
import sk.stasko.ecomerce.common.exception.ResourceNotFoundException;
import sk.stasko.ecomerce.product.ProductEntity;
import sk.stasko.ecomerce.product.ProductService;
import sk.stasko.ecomerce.user.UserService;

import java.math.BigDecimal;
import java.util.List;

import static sk.stasko.ecomerce.common.util.DbUtil.getSortForPagination;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartItemRepository iCartItemRepository;
    private final CartRepository iCartRepository;

    private final UserService iUserService;
    private final ProductService iProductService;

    private final CartMapper cartMapper;

    public CartEntity findById(Long id) {
        return iCartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CartEntity", "id", id.toString()));
    }

    public CartItemEntity findByProductIdAndCartId(Long productId, Long cartId) {
        return iCartItemRepository.findByProductIdAndCartId(productId, cartId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId,cartId", productId + "," + cartId));
    }

    @Transactional
    public CartDto addProductAndQuantityToTheCart(Long productId, Integer quantity) {
        ProductEntity productEntity = iProductService.retrieveCorrectProduct(productId, quantity);
        CartEntity cart = createCartEntity();
        createCartItemEntity(productEntity, cart, quantity);

        var contractedBigDecimalPrice = productEntity.getSpecialPrice().multiply(BigDecimal.valueOf(quantity));
        cart.setTotalPrice(contractedBigDecimalPrice);

        var savedCart = iCartRepository.save(cart);
        log.info("Saved cart {}", savedCart);
        return cartMapper.toDto(savedCart);
    }

    public PaginationDto<CartDto> listOfAllCarts(PaginationRequest paginationRequest) {
        Pageable pageDetails = PageRequest.of(paginationRequest.page(), paginationRequest.limit(), getSortForPagination(paginationRequest));
        log.info("Fetching carts from db ... ");
        Page<CartEntity> entities = iCartRepository.findAll(pageDetails);

        List<CartDto> responseContent = entities.stream()
                .map(cartMapper::toDto)
                .toList();

        return new PaginationDto<>(
                responseContent,
                entities.getNumber(),
                entities.getSize(),
                entities.getTotalElements(),
                entities.getTotalPages(),
                entities.isLast()
        );
    }

    public CartDto getUserCart() {
        var loggedUser = iUserService.fetchLoggedUser();
        var cartEntity = iCartRepository.findByUsername(loggedUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "username", loggedUser.getUsername()));

        log.info("Fetching users cart from db ... ");
        return cartMapper.toDto(cartEntity);
    }

    @Transactional
    public CartDto updateQuantityOfProductInCart(Long productId, Integer quantity) {
        var userCart = getUserCart();
        CartEntity cartEntity = this.findById(userCart.getId());
        ProductEntity productEntity = iProductService.retrieveCorrectProduct(productId, quantity);
        CartItemEntity cartItemEntity = findByProductIdAndCartId(productId, cartEntity.getId());

        cartItemEntity.setProductPrice(productEntity.getSpecialPrice());
        cartItemEntity.setQuantity(cartItemEntity.getQuantity() + quantity);
        cartItemEntity.setDiscount(productEntity.getDiscount());
        var operationPrice = cartItemEntity.getProductPrice().multiply(BigDecimal.valueOf(quantity));
        cartEntity.setTotalPrice(cartEntity.getTotalPrice().add(operationPrice));

        iCartRepository.save(cartEntity);
        CartItemEntity updatedCartItemEntity = iCartItemRepository.save(cartItemEntity);
        if (updatedCartItemEntity.isEmpty()) {
            iCartItemRepository.deleteById(updatedCartItemEntity.getCartProductId());
        }

        return cartMapper.toDto(cartEntity);
    }

    @Transactional
    public void removeSpecificProductFromCart(Long productId, Long cartId) {
        var cartEntity = this.findById(cartId);
        var cartItemEntity = findByProductIdAndCartId(productId, cartId);

        var valueToSubtract = cartItemEntity.getProductPrice().multiply(BigDecimal.valueOf(cartItemEntity.getQuantity()));
        cartEntity.setTotalPrice(cartEntity.getTotalPrice().subtract(valueToSubtract));

        iCartItemRepository.deleteCartItemEntityByProductIdAndCartId(productId, cartId);
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

    private CartEntity createCartEntity() {
        var loggedUser = iUserService.fetchLoggedUser();
        var cartEntity =  iCartRepository.findCartByEmail(loggedUser.getEmail())
                .orElseGet(() -> {
                    CartEntity cart = new CartEntity();
                    cart.setTotalPrice(BigDecimal.ZERO);
                    cart.setUser(loggedUser);
                    return iCartRepository.save(cart);
                });

        log.info("Created new cart for user loggedUser = {}", loggedUser.getEmail());
        return cartEntity;
    }

    @Transactional
    public void updateProductInCarts(Long productId, BigDecimal productPrice) {
        List<CartEntity> cartEntities = this.iCartRepository.findCartsByProductId(productId);
        for (CartEntity cart : cartEntities) {
            cart.getCartItems().stream()
                    .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                    .forEach(cartItem -> cartItem.setProductPrice(productPrice));
            cart.recalculateTotalPrice();
        }

        iCartRepository.saveAll(cartEntities);
        log.info("Updated carts total ...");

    }
}
