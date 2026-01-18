package nbu.dbProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "pets")
public class Pet extends BaseEntity {

    @NotBlank(message = "Pet name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Pet type is required")
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private boolean usesCommonAreas;

    @NotNull(message = "Apartment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    public Pet() {}

    public Pet(String name, String type, boolean usesCommonAreas) {
        this.name = name;
        this.type = type;
        this.usesCommonAreas = usesCommonAreas;
    }

    @Override
    public String toString() {
        return "Pet{id=" + id + ", name='" + name + "', type='" + type +
                "', usesCommonAreas=" + usesCommonAreas + "}";
    }
}