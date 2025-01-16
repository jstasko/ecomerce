package sk.stasko.ecomerce.order;

import lombok.Data;

@Data
public class OrderRequestDto {
    private Long addressId;
    private String pgName;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
}
