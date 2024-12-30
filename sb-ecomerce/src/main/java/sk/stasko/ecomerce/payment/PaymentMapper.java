package sk.stasko.ecomerce.payment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "paymentMethod", expression = "java(entity.getPaymentMethod().getDescription() + \"\")")
    PaymentDto toDto(PaymentEntity entity);
}
