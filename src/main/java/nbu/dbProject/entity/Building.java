package nbu.dbProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "buildings")
public class Building extends BaseEntity {

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    @Min(value = 1, message = "Number of floors must be at least 1")
    @Column(nullable = false)
    private int numberOfFloors;

    @Min(value = 1, message = "Number of apartments must be at least 1")
    @Column(nullable = false)
    private int numberOfApartments;

    @Min(value = 1, message = "Built-up area must be positive")
    @Column(nullable = false)
    private double builtUpArea;

    @Min(value = 0, message = "Common area cannot be negative")
    @Column(nullable = false)
    private double commonArea;

    @Column(nullable = false)
    private boolean hasElevator;

    @NotNull(message = "Company is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Apartment> apartments = new ArrayList<>();

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fee> fees = new ArrayList<>();

    public Building() {}

    public Building(String address, int numberOfFloors, int numberOfApartments,
                    double builtUpArea, double commonArea, boolean hasElevator) {
        this.address = address;
        this.numberOfFloors = numberOfFloors;
        this.numberOfApartments = numberOfApartments;
        this.builtUpArea = builtUpArea;
        this.commonArea = commonArea;
        this.hasElevator = hasElevator;
    }

    // Helper methods
    public void addApartment(Apartment apartment) {
        apartments.add(apartment);
        apartment.setBuilding(this);
    }

    public void removeApartment(Apartment apartment) {
        apartments.remove(apartment);
        apartment.setBuilding(null);
    }

    @Override
    public String toString() {
        return "Building{id=" + id + ", address='" + address +
                "', floors=" + numberOfFloors + ", apartments=" + numberOfApartments + "}";
    }
}