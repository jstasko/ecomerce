package sk.stasko.ecomerce.adress;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.stasko.ecomerce.common.constants.CommonConstants;
import sk.stasko.ecomerce.common.dto.PaginationDto;
import sk.stasko.ecomerce.common.dto.PaginationRequest;
import sk.stasko.ecomerce.common.dto.ResponseDto;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService service;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> createNewAddress(@Valid @RequestBody AddressDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dto));
    }

    @GetMapping("/addresses")
    public ResponseEntity<PaginationDto<AddressDto>> findAllAddresses(
            @RequestParam(defaultValue = AddressConstants.PAGE_NUMBER, required = false) int page,
            @RequestParam(defaultValue = AddressConstants.PAGE_LIMIT, required = false) int limit,
            @RequestParam(defaultValue = AddressConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AddressConstants.SORT_ORDER, required = false) String sortOrder
    ) {
        PaginationRequest paginationRequest = new PaginationRequest(page, limit, sortBy, sortOrder);
        return ResponseEntity.ok(this.service.findAll(paginationRequest));
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDto> getAddressById(@RequestParam Long addressId) {
        return ResponseEntity.ok(this.service.findDtoById(addressId));
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDto>> getAddressOfLoggedUser() {
        return ResponseEntity.ok(this.service.findAddressByUser());
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<ResponseDto> updateAddressById(@PathVariable Long addressId, @Valid @RequestBody AddressDto dto) {
        var updated = this.service.update(addressId, dto);
        if (updated) {
            return ResponseEntity.ok(new ResponseDto(CommonConstants.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseDto(CommonConstants.MESSAGE_417_UPDATE));
        }
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<ResponseDto> deleteAddressById(@PathVariable Long addressId) {
        var deleted = service.deleteById(addressId);
        if (deleted) {
            return ResponseEntity.ok(new ResponseDto(CommonConstants.MESSAGE_200));
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseDto(CommonConstants.MESSAGE_417_DELETE));
        }
    }
}
