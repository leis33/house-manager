package nbu.dbProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class Company {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    private double revenue;

    @OneToMany(mappedBy = "company")
    private List<Employee> employees;
}
