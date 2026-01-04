package nbu.dbProject.entity;

import jakarta.persistence.*;

@Entity
public class Pet {

    @Id
    @GeneratedValue
    private Long id;

    private String type;
    private boolean usesCommonAreas;

    @ManyToOne
    private Apartment apartment;
}