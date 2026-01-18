package nbu.dbProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "fees")
public class Fee extends BaseEntity {

    @NotNull(message = "Building is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @NotNull(message = "Apartment is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @NotNull(message = "Fee date is required")
    @Column(nullable = false)
    private LocalDate feeDate;

    @Min(value = 0, message = "Base amount cannot be negative")
    @Column(nullable = false)
    private double baseAmount;

    @Min(value = 0, message = "Resident charge cannot be negative")
    @Column(nullable = false)
    private double residentCharge;

    @Min(value = 0, message = "Pet charge cannot be negative")
    @Column(nullable = false)
    private double petCharge;

    @Min(value = 0, message = "Total amount cannot be negative")
    @Column(nullable = false)
    private double totalAmount;

    @Column(nullable = false)
    private boolean paid = false;

    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public Fee() {}

    public Fee(Building building, Apartment apartment, LocalDate feeDate,
               double baseAmount, double residentCharge, double petCharge) {
        this.building = building;
        this.apartment = apartment;
        this.feeDate = feeDate;
        this.baseAmount = baseAmount;
        this.residentCharge = residentCharge;
        this.petCharge = petCharge;
        this.totalAmount = baseAmount + residentCharge + petCharge;
    }

    public void calculateTotalAmount() {
        this.totalAmount = baseAmount + residentCharge + petCharge;
    }

    @Override
    public String toString() {
        return "Fee{id=" + id + ", apartment=" + (apartment != null ? apartment.getApartmentNumber() : "null") +
                ", date=" + feeDate + ", total=" + totalAmount + ", paid=" + paid + "}";
    }
}
