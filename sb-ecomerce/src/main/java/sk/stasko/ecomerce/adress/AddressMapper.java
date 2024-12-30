package sk.stasko.ecomerce.adress;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDto toDto(AddressEntity addressEntity);
}
