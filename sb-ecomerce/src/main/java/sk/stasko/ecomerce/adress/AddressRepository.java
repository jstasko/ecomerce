package sk.stasko.ecomerce.adress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    boolean existsByBuildingNameAndCityAndCountryAndStreetAndStateAndPincode(String buildingName, String city, String country, String street, String state, String pincode);
}
