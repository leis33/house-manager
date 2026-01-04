package nbu.dbProject.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Building extends BaseEntity {
    private String address;
    private int floors;

    @ManyToOne
    private Employee employee;

    @OneToMany(mappedBy = "building")
    private List<Apartment> apartments;
}
