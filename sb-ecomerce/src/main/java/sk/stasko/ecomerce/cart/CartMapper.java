package sk.stasko.ecomerce.cart;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import sk.stasko.ecomerce.cartItem.CartItemEntity;
import sk.stasko.ecomerce.product.ProductDto;
import sk.stasko.ecomerce.product.ProductMapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartMapper {

    @Mapping(target = "products", source = "cartItems", qualifiedByName = "mapCartItemsToProducts")
    CartDto toDto(CartEntity cartEntity);

    @Named("mapCartItemsToProducts")
    default List<ProductDto> mapCartItemsToProducts(List<CartItemEntity> cartItems) {
        if (cartItems == null) return new ArrayList<>();
        return cartItems.stream()
                .map(cartItem -> {
                    var dto = ProductMapper.INSTANCE.toDto(cartItem.getProduct());
                    dto.setQuantity(cartItem.getQuantity());
                    return dto;
                }).toList();
    }

}
