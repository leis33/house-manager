package nbu.dbProject.services;


import nbu.dbProject.entity.Fee;
import nbu.dbProject.entity.Payment;
import nbu.dbProject.dao.FeeRepository;
import nbu.dbProject.dao.PaymentRepository;
import nbu.dbProject.utils.PaymentFileWriter;
import nbu.dbProject.utils.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentService {

    private static final Logger logger = LogManager.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final FeeRepository feeRepository;

    public PaymentService() {
        this.paymentRepository = new PaymentRepository();
        this.feeRepository = new FeeRepository();
    }

    public Payment createPayment(Long feeId, double amount, String notes) {
        logger.info("Creating payment for fee {}", feeId);

        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new IllegalArgumentException("Fee not found"));

        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        if (amount > fee.getTotalAmount()) {
            throw new IllegalArgumentException("Payment amount exceeds fee total");
        }

        Payment payment = new Payment(fee, amount, LocalDateTime.now(), notes);

        ValidationUtil.validate(payment);
        Payment savedPayment = paymentRepository.save(payment);

        // Check if fee is fully paid
        double totalPaid = getTotalPaidForFee(feeId);
        if (totalPaid >= fee.getTotalAmount()) {
            fee.setPaid(true);
            feeRepository.update(fee);
            logger.info("Fee {} marked as fully paid", feeId);
        }

        // Write payment to file
        PaymentFileWriter.writePayment(savedPayment);

        return savedPayment;
    }

    public double getTotalPaidForFee(Long feeId) {
        return paymentRepository.findByFeeId(feeId).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByFee(Long feeId) {
        return paymentRepository.findByFeeId(feeId);
    }

    public double getTotalRevenueByCompany(Long companyId) {
        return paymentRepository.getTotalAmountByCompany(companyId);
    }

    public double getTotalRevenueByBuilding(Long buildingId) {
        return paymentRepository.getTotalAmountByBuilding(buildingId);
    }

    public double getTotalRevenueByEmployee(Long employeeId) {
        return paymentRepository.getTotalAmountByEmployee(employeeId);
    }

    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);
        Fee fee = payment.getFee();

        paymentRepository.delete(id);

        // Recalculate if fee is still fully paid
        double totalPaid = getTotalPaidForFee(fee.getId());
        if (totalPaid < fee.getTotalAmount()) {
            fee.setPaid(false);
            feeRepository.update(fee);
        }

        logger.info("Payment deleted: {}", id);
    }
}
