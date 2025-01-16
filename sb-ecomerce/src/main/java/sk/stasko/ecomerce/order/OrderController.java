package sk.stasko.ecomerce.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.stasko.ecomerce.user.UserService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDto> orderProducts(
            @PathVariable String paymentMethod,
            @RequestBody @Valid OrderRequestDto orderRequestDto
    ) {
        var loggedUser = userService.fetchLoggedUser();
        OrderDto order = orderService.placeOrder(
                loggedUser.getEmail(),
                orderRequestDto.getAddressId(),
                paymentMethod,
                orderRequestDto.getPgName(),
                orderRequestDto.getPgPaymentId(),
                orderRequestDto.getPgStatus(),
                orderRequestDto.getPgResponseMessage()
        );

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
