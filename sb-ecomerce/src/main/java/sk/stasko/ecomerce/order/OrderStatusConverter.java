package sk.stasko.ecomerce.order;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter()
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatus) {
        return orderStatus == null ? null : orderStatus.name();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : OrderStatus.valueOf(dbData);
    }
}
