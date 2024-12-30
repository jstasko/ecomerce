package sk.stasko.ecomerce.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.stasko.ecomerce.cartItem.CartItemService;
import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;
import sk.stasko.ecomerce.common.dto.ResponseDto;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;
    private final CartItemService cartItemService;

    @GetMapping("/carts")
    public ResponseEntity<PaginationDto<CartDto>> getAllCarts(
            @RequestParam(defaultValue = CartConstants.PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = CartConstants.PAGE_LIMIT, required = false) int limit,
            @RequestParam(defaultValue = CartConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = CartConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        var request = new PaginationRequest(page, limit, sortBy, sortOrder);
        return ResponseEntity.ok(this.cartService.listOfAllCarts(request));
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDto> retrieveUserCart() {
        return ResponseEntity.ok(this.cartService.getUserCart());
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDto> addProductAndQuantityToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity
    ) {
        var updatedCart = this.cartItemService.addProductAndQuantityToTheCart(productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }

    @PutMapping("/carts/products/{productId}/operation/{operation}")
    public ResponseEntity<CartDto> updateProductAndQuantityToCart(
            @PathVariable Long productId,
            @ValidCartOperation @PathVariable String operation
    ) {
        log.info("Updating cart with productId: {} and operation: {}", productId, operation);

        var quantity = CartOperation.getQuantityChangeFromString(operation);

        var updatedCart = this.cartItemService.updateQuantityOfProductInCart(productId, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCart);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<ResponseDto> deleteSpecificProductFromCart(
            @PathVariable Long cartId,
            @PathVariable Long productId
    ) {
        this.cartItemService.removeSpecificProductFromCart(productId, cartId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("Product was deleted from cart"));
    }
}
