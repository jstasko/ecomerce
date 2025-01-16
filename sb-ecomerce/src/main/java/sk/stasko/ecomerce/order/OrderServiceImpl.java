package sk.stasko.ecomerce.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sk.stasko.ecomerce.adress.AddressMapper;
import sk.stasko.ecomerce.adress.AddressService;
import sk.stasko.ecomerce.cart.CartService;
import sk.stasko.ecomerce.cartItem.CartItemService;
import sk.stasko.ecomerce.common.exception.CartIsEmptyException;
import sk.stasko.ecomerce.orderItem.OrderItemEntity;
import sk.stasko.ecomerce.orderItem.OrderItemRepository;
import sk.stasko.ecomerce.payment.PaymentService;
import sk.stasko.ecomerce.product.ProductEntity;
import sk.stasko.ecomerce.product.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final AddressService addressService;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;
    private final CartItemService cartItemService;

    @Override
    @Transactional
    public OrderDto placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        var cart = this.cartService.findCartByEmail(email);
        var address = this.addressService.findById(addressId);

        var order = new OrderEntity();
        order.setEmail(email);
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus(OrderStatus.S);
        order.setAddress(address);

        var payment = paymentService.save(paymentMethod, pgName, pgPaymentId, pgStatus, pgResponseMessage, order);
        order.setPayment(payment);

        OrderEntity savedOrderEntity = this.orderRepository.save(order);

        List<OrderItemEntity> orderItemEntityList = Optional.ofNullable(cart.getCartItems())
                .filter(cartItems -> !cartItems.isEmpty())
                .orElseThrow(() -> new CartIsEmptyException("Cart is Empty"))
                .stream()
                .map(ci -> new OrderItemEntity(ci.getProduct(), ci.getQuantity(), ci.getDiscount(), ci.getProductPrice(), savedOrderEntity))
                .toList();
        this.orderItemRepository.saveAll(orderItemEntityList);

        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            ProductEntity product = item.getProduct();

            // Reduce stock quantity
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            cartItemService.removeSpecificProductFromCart(item.getProduct().getId(), cart.getId());
        });

        OrderDto orderDto = orderMapper.toDto(savedOrderEntity);
        orderDto.setShippingAddress(addressMapper.toDto(address));
        return null;
    }
}
