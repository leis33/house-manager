package nbu.dbProject.entity;

import jakarta.persistence.*;

@Entity
public class Resident extends BaseEntity {
    private String name;
    private int age;
    private boolean usesElevator;

    @ManyToOne
    private Apartment apartment;
}