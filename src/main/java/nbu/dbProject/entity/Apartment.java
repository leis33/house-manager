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
@Table(name = "apartments")
public class Apartment extends BaseEntity {

    @NotBlank(message = "Apartment number is required")
    @Column(nullable = false)
    private String apartmentNumber;

    @Min(value = 1, message = "Floor must be at least 1")
    @Column(nullable = false)
    private int floor;

    @Min(value = 1, message = "Area must be positive")
    @Column(nullable = false)
    private double area;

    @NotNull(message = "Building is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resident> residents = new ArrayList<>();

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fee> fees = new ArrayList<>();

    public Apartment() {}

    public Apartment(String apartmentNumber, int floor, double area) {
        this.apartmentNumber = apartmentNumber;
        this.floor = floor;
        this.area = area;
    }

    public void addResident(Resident resident) {
        residents.add(resident);
        resident.setApartment(this);
    }

    public void removeResident(Resident resident) {
        residents.remove(resident);
        resident.setApartment(null);
    }

    public void addPet(Pet pet) {
        pets.add(pet);
        pet.setApartment(this);
    }

    public void removePet(Pet pet) {
        pets.remove(pet);
        pet.setApartment(null);
    }

    public int getResidentsOver7Count() {
        return (int) residents.stream()
                .filter(r -> r.getAge() > 7)
                .count();
    }

    public int getPetsUsingCommonAreasCount() {
        return (int) pets.stream()
                .filter(Pet::isUsesCommonAreas)
                .count();
    }

    @Override
    public String toString() {
        return "Apartment{id=" + id + ", number='" + apartmentNumber +
                "', floor=" + floor + ", area=" + area + "}";
    }
}