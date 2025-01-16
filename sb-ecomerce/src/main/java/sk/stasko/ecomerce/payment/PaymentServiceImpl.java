package sk.stasko.ecomerce.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sk.stasko.ecomerce.order.OrderEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentEntity save(String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage, OrderEntity order) {
        var payment = new PaymentEntity(PaymentMethod.valueOf(paymentMethod), pgName, pgResponseMessage, pgStatus, pgPaymentId);
        payment.setOrder(order);
        return paymentRepository.save(payment);
    }
}
