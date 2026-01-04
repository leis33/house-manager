package nbu.dbProject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Payment extends BaseEntity {
    private double amount;
    private LocalDate paymentDate;

    @ManyToOne
    private Company company;

    @ManyToOne
    private Employee employee;

    @ManyToOne
    private Building building;

    @ManyToOne
    private Apartment apartment;
}
