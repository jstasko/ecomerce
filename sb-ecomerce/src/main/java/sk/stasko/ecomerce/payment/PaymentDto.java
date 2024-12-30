package sk.stasko.ecomerce.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class PaymentDto {
    private Long id;
    private String paymentMethod;
}
