package nbu.dbProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import nbu.dbProject.validator.InvalidNames;

import java.util.List;

@Entity
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@Builder
public class Company extends BaseEntity {

    @Column(name = "name")
    @InvalidNames(message = "The name is not valid!")
    @NotBlank(message = "Company name cannot be blank!")
    @Size(max = 20, message = "Company name has to be with up to 20 characters!")
    @Pattern(regexp = "^([A-Z]).*", message = "Company name has to start with capital letter!")
    private String name;

    private double revenue;

    @OneToMany(mappedBy = "company")
    private List<Employee> employees;
}
