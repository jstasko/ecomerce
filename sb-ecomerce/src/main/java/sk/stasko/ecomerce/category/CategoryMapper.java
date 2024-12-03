package sk.stasko.ecomerce.category;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(source = "id", target = "id")
    CategoryDto toDto(CategoryEntity category);

    @Mapping(target = "id", ignore = true)
    CategoryEntity toEntity(CategoryDto categoryDto);
}
