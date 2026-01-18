package nbu.dbProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "employees")
public class Employee extends BaseEntity {

    @NotBlank(message = "First name is required")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(nullable = false)
    private String lastName;

    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotNull(message = "Company is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "employee")
    private List<Building> buildings = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    public Employee() {}

    public Employee(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getBuildingCount() {
        return buildings.size();
    }

    public void addBuilding(Building building) {
        buildings.add(building);
        building.setEmployee(this);
    }

    public void removeBuilding(Building building) {
        buildings.remove(building);
        building.setEmployee(null);
    }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + getFullName() +
                "', buildings=" + getBuildingCount() + "}";
    }
}