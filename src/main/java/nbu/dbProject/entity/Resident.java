package nbu.dbProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "residents")
public class Resident extends Person {

    @NotNull(message = "Apartment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    public Resident() {
        super();
    }

    public Resident(String firstName, String lastName, int age, String phoneNumber, String email) {
        super(firstName, lastName, age, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "Resident{id=" + id + ", name='" + getFullName() +
                "', age=" + getAge() + "}";
    }
}
