package sk.stasko.ecomerce.adress;

import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;

import java.util.List;

public interface AddressService {
    AddressDto save(AddressDto addressDto);
    AddressEntity findById(Long id);
    PaginationDto<AddressDto> findAll(PaginationRequest paginationRequest);
    AddressDto findDtoById(Long id);
    List<AddressDto> findAddressByUser();
    boolean update(Long addressId, AddressDto entity);
    boolean deleteById(Long addressId);
}
