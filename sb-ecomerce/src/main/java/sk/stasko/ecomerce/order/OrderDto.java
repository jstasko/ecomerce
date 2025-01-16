package sk.stasko.ecomerce.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stasko.ecomerce.adress.AddressDto;
import sk.stasko.ecomerce.payment.PaymentDto;
import sk.stasko.ecomerce.product.ProductDto;

import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
public class OrderDto {
    private Long id;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
    private String orderStatus;
    private Double totalAmount;
    private PaymentDto payment;
    private List<ProductDto> products;
    private AddressDto shippingAddress;
}
