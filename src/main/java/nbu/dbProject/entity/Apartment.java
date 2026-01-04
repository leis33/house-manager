package nbu.dbProject.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Apartment {

    @Id
    @GeneratedValue
    private Long id;

    private int number;
    private double area;

    @ManyToOne
    private Building building;

    @OneToMany(mappedBy = "apartment")
    private List<Resident> residents;

    @OneToMany(mappedBy = "apartment")
    private List<Pet> pets;
}