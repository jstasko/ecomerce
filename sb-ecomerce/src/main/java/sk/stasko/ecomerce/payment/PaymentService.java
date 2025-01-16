package sk.stasko.ecomerce.payment;

import sk.stasko.ecomerce.order.OrderEntity;

public interface PaymentService {
    PaymentEntity save(String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage, OrderEntity order);
}
