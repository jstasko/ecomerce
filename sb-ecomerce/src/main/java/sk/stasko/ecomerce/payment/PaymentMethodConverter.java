package sk.stasko.ecomerce.payment;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter()
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {
    @Override
    public String convertToDatabaseColumn(PaymentMethod paymentMethod) {
        return paymentMethod != null ? paymentMethod.name() : null;
    }

    @Override
    public PaymentMethod convertToEntityAttribute(String dbData) {
        return dbData != null ? PaymentMethod.valueOf(dbData) : null;
    }
}
