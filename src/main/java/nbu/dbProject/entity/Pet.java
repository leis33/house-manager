package nbu.dbProject.entity;

import jakarta.persistence.*;

@Entity
public class Pet extends BaseEntity {
    private String type;
    private boolean usesCommonAreas;

    @ManyToOne
    private Apartment apartment;
}