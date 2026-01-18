package nbu.dbProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    @NotBlank(message = "Company name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[0-9]{9,13}$", message = "Registration number must be 9-13 digits")
    @Column(nullable = false, unique = true)
    private String registrationNumber;

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String phoneNumber;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Building> buildings = new ArrayList<>();

    public Company() {}

    public Company(String name, String registrationNumber, String address, String phoneNumber) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setCompany(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setCompany(null);
    }

    public void addBuilding(Building building) {
        buildings.add(building);
        building.setCompany(this);
    }

    public void removeBuilding(Building building) {
        buildings.remove(building);
        building.setCompany(null);
    }

    @Override
    public String toString() {
        return "Company{id=" + id + ", name='" + name + "', registrationNumber='" +
                registrationNumber + "'}";
    }
}