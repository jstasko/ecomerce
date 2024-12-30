package sk.stasko.ecomerce.order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import sk.stasko.ecomerce.orderItem.OrderItemEntity;
import sk.stasko.ecomerce.product.ProductDto;
import sk.stasko.ecomerce.product.ProductMapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderMapper {

    @Mapping(target = "products", source = "orderItems", qualifiedByName = "mapOrderItemsToProductDto")
    @Mapping(target = "orderStatus", expression = "java(orderEntity.getOrderStatus() + \"\")")
    OrderDto toDto(OrderEntity orderEntity);

    @Named("mapOrderItemsToProductDto")
    default List<ProductDto> mapOrderItemsToProductDto(List<OrderItemEntity> orderItems) {
        if (orderItems == null) return new ArrayList<>();
        return orderItems.stream()
                .map(orderItem -> {
                    var dto = ProductMapper.INSTANCE.toDto(orderItem.getProduct());
                    dto.setQuantity(orderItem.getQuantity());
                    return dto;
                }).toList();
    }
}
