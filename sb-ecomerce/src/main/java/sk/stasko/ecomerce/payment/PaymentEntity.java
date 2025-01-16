package sk.stasko.ecomerce.payment;

import jakarta.persistence.*;
import lombok.*;
import sk.stasko.ecomerce.common.entity.BaseEntity;
import sk.stasko.ecomerce.order.OrderEntity;

@Entity @Table(name = "payments")
@Getter @ToString @Setter
@NoArgsConstructor @AllArgsConstructor
public class PaymentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(mappedBy = "payment", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private OrderEntity order;

    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;

    public PaymentEntity(PaymentMethod paymentMethod, String pgName, String pgResponseMessage, String pgStatus, String pgPaymentId) {
        this.paymentMethod = paymentMethod;
        this.pgName = pgName;
        this.pgResponseMessage = pgResponseMessage;
        this.pgStatus = pgStatus;
        this.pgPaymentId = pgPaymentId;
    }

    @Convert(converter = PaymentMethodConverter.class)
    @Column(name = "payment_method", nullable = false, length = 1)
    private PaymentMethod paymentMethod;


}
