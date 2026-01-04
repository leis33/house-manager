package nbu.dbProject.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Building {

    @Id
    @GeneratedValue
    private Long id;

    private String address;
    private int floors;

    @ManyToOne
    private Employee employee;

    @OneToMany(mappedBy = "building")
    private List<Apartment> apartments;
}
