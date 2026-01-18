package nbu.dbProject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment extends BaseEntity {

    @NotNull(message = "Fee is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_id", nullable = false)
    private Fee fee;

    @Min(value = 0, message = "Amount cannot be negative")
    @Column(nullable = false)
    private double amount;

    @NotNull(message = "Payment date is required")
    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(length = 500)
    private String notes;

    public Payment() {}

    public Payment(Fee fee, double amount, LocalDateTime paymentDate, String notes) {
        this.fee = fee;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Payment{id=" + id + ", amount=" + amount +
                ", date=" + paymentDate + "}";
    }
}
