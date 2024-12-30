package sk.stasko.ecomerce.payment;

import jakarta.persistence.*;
import lombok.*;
import sk.stasko.ecomerce.common.entity.BaseEntity;

@Entity @Table(name = "payments")
@Getter @ToString @Setter
@NoArgsConstructor @AllArgsConstructor
public class PaymentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Convert(converter = PaymentMethodConverter.class)
    @Column(name = "payment_method", nullable = false, length = 1)
    private PaymentMethod paymentMethod;
}
