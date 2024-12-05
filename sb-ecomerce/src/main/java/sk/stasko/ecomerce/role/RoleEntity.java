package sk.stasko.ecomerce.role;

import jakarta.persistence.*;
import lombok.*;
import sk.stasko.ecomerce.common.entity.BaseEntity;

@Entity()
@Table(name = "roles")
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class RoleEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", length = 20)
    private AppRole roleName;

    public RoleEntity(AppRole roleName) {
        this.roleName = roleName;
    }
}
