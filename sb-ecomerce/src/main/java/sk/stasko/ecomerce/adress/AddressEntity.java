package sk.stasko.ecomerce.adress;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import sk.stasko.ecomerce.common.entity.BaseEntity;
import sk.stasko.ecomerce.user.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Entity()
@Table(name = "addresses")
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class AddressEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @NotBlank
    @Size(min = 5, message = "Street name must be at least 5 characters")
    private String street;

    @NotBlank
    @Size(min = 5, message = "Building name must be at least 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 4, message = "City name must be at least 4 characters")
    private String city;

    @NotBlank
    @Size(min = 2, message = "State name must be at least 2 characters")
    private String state;

    @NotBlank
    @Size(min = 2, message = "Country name must be at least 2 characters")
    private String country;

    @NotBlank
    @Size(min = 6, message = "Pincode must be at least 2 characters")
    private String pincode;

    @ManyToMany(mappedBy = "addresses")
    @ToString.Exclude
    private List<UserEntity> users = new ArrayList<>();
}
