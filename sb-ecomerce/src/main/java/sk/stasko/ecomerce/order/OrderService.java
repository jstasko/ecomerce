package sk.stasko.ecomerce.order;

import jakarta.transaction.Transactional;

public interface OrderService {

    @Transactional
    OrderDto placeOrder(
            String email,
            Long addressId,
            String paymentMethod,
            String pgName,
            String pgPaymentId,
            String pgStatus,
            String pgResponseMessage);
}
