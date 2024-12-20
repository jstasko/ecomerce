package sk.stasko.ecomerce.adress;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;
import sk.stasko.ecomerce.common.exception.EntityAlreadyExists;
import sk.stasko.ecomerce.common.exception.ResourceNotFoundException;
import sk.stasko.ecomerce.common.util.DbUtil;
import sk.stasko.ecomerce.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository iAddressRepository;
    private final AddressMapper iAddressMapper;
    private final UserService iUserService;

    public AddressDto save(AddressDto addressDto) {
        boolean existed = iAddressRepository.existsByBuildingNameAndCityAndCountryAndStreetAndStateAndPincode(
                addressDto.getBuildingName(), addressDto.getCity(), addressDto.getCountry(), addressDto.getStreet(), addressDto.getState(), addressDto.getPincode()
        );

        if (existed) {
            throw new EntityAlreadyExists("Address already registered with given parameters ");
        }

        log.info("Saving new address: {}", addressDto);

        var saved = iAddressRepository.save(createNewAddress(addressDto));
        return iAddressMapper.toDto(saved);
    }

    private AddressEntity createNewAddress(AddressDto addressDto) {
        AddressEntity newAddress = new AddressEntity();
        newAddress.setBuildingName(addressDto.getBuildingName());
        newAddress.setCity(addressDto.getCity());
        newAddress.setCountry(addressDto.getCountry());
        newAddress.setStreet(addressDto.getStreet());
        newAddress.setState(addressDto.getState());
        newAddress.setPincode(addressDto.getPincode());
        newAddress.setUsers(new ArrayList<>());

        return newAddress;
    }

    public PaginationDto<AddressDto> findAll(PaginationRequest paginationRequest) {
        Sort sortAndOrderBy = DbUtil.getSortForPagination(paginationRequest);
        Pageable pageDetails = PageRequest.of(paginationRequest.page(), paginationRequest.limit(), sortAndOrderBy);

        log.info("Fetching all address ... ");
        Page<AddressEntity> addressEntities = iAddressRepository.findAll(pageDetails);
        List<AddressDto> responseCategoryList = addressEntities.getContent()
                .stream()
                .map(iAddressMapper::toDto)
                .toList();

        return new PaginationDto<>(
                responseCategoryList,
                addressEntities.getNumber(),
                addressEntities.getSize(),
                addressEntities.getTotalElements(),
                addressEntities.getTotalPages(),
                addressEntities.isLast()
        );
    }

    protected AddressEntity findById(Long id) {
        log.info("Fetching address : {}", id);

        return iAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id.toString()));
    }

    public AddressDto findDtoById(Long id) {
        var address = this.findById(id);
        return iAddressMapper.toDto(address);
    }

    public List<AddressDto> findAddressByUser() {
        var loggedUser = iUserService.fetchLoggedUser();

        return loggedUser.getAddresses()
                .stream()
                .map(iAddressMapper::toDto)
                .toList();
    }

    public boolean update(Long addressId, AddressDto entity) {
        log.info("Updating address with id: {}", addressId);
        var addressEntity  = this.findById(addressId);

        addressEntity.setBuildingName(entity.getBuildingName());
        addressEntity.setCity(entity.getCity());
        addressEntity.setCountry(entity.getCountry());
        addressEntity.setStreet(entity.getStreet());
        addressEntity.setState(entity.getState());
        addressEntity.setPincode(entity.getPincode());
        addressEntity.setUsers(entity.getUsers());

        iAddressRepository.save(addressEntity);
        return true;
    }

    public boolean deleteById(Long addressId) {
        log.info("Deleting address with id: {}", addressId);
        AddressEntity category = this.findById(addressId);
        iAddressRepository.delete(category);
        return true;
    }
}
