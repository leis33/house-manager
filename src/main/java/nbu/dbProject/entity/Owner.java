package nbu.dbProject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "owners")
public class Owner extends Person {

    @OneToMany(mappedBy = "owner")
    private List<Apartment> apartments = new ArrayList<>();

    public Owner() {
        super();
    }

    public Owner(String firstName, String lastName, int age, String phoneNumber, String email) {
        super(firstName, lastName, age, phoneNumber, email);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addApartment(Apartment apartment) {
        apartments.add(apartment);
        apartment.setOwner(this);
    }

    public void removeApartment(Apartment apartment) {
        apartments.remove(apartment);
        apartment.setOwner(null);
    }

    @Override
    public String toString() {
        return "Owner{id=" + id + ", name='" + getFullName() +
                "', apartments=" + apartments.size() + "}";
    }
}