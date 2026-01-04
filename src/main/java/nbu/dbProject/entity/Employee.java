package nbu.dbProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class Employee extends BaseEntity {
    @NotBlank
    private String name;

    @ManyToOne
    private Company company;

    @OneToMany(mappedBy = "employee")
    private List<Building> buildings;
}
