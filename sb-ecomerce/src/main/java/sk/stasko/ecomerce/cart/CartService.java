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
    private final CartRepository iCartRepository;
    private final UserService iUserService;
    private final CartMapper cartMapper;

    public CartEntity findById(Long id) {
        return iCartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CartEntity", "id", id.toString()));
    }

    public CartDto saveCartEntity(CartEntity cartEntity) {
        var savedEntity = iCartRepository.save(cartEntity);
        log.info("Saved cart {}", savedEntity);
        return cartMapper.toDto(savedEntity);
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

    public CartEntity createCartEntity() {
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
}
