package sk.stasko.ecomerce.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import sk.stasko.ecomerce.adress.AddressEntity;
import sk.stasko.ecomerce.common.entity.BaseEntity;
import sk.stasko.ecomerce.orderItem.OrderItemEntity;
import sk.stasko.ecomerce.payment.PaymentEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "orders")
@Getter @ToString @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Email
    @Column(nullable = false)
    private String email;

    @Convert(converter = OrderStatusConverter.class)
    @Column(name = "order_status", nullable = false, length = 1)
    private OrderStatus orderStatus;

    private BigDecimal totalAmount;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "address_id")
    private AddressEntity address;
}
