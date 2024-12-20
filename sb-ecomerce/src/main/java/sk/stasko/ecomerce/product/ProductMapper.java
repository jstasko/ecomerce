package sk.stasko.ecomerce.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDto toDto(ProductEntity productEntity);

    @Mapping(target = "id", ignore = true)
    ProductEntity toEntity(ProductEntity categoryDto);
}
