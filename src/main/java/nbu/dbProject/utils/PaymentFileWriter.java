package nbu.dbProject.utils;

import nbu.dbProject.entity.Payment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class PaymentFileWriter {

    private static final Logger logger = LogManager.getLogger(PaymentFileWriter.class);
    private static final String PAYMENT_FILE_PATH = "payments/payments.txt";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        try {
            Path directory = Paths.get("payments");
            if (!Files.exists(directory)) {
                Files.createDirectory(directory);
                logger.info("Created payments directory");
            }
        } catch (IOException e) {
            logger.error("Error creating payments directory", e);
        }
    }

    public static void writePayment(Payment payment) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(PAYMENT_FILE_PATH, true))) {

            String companyName = payment.getFee().getBuilding().getCompany().getName();
            String employeeName = payment.getFee().getBuilding().getEmployee() != null
                    ? payment.getFee().getBuilding().getEmployee().getFullName()
                    : "N/A";
            String buildingAddress = payment.getFee().getBuilding().getAddress();
            String apartmentNumber = payment.getFee().getApartment().getApartmentNumber();
            double amount = payment.getAmount();
            String date = payment.getPaymentDate().format(DATE_FORMATTER);

            String line = String.format(
                    "Company: %s | Employee: %s | Building: %s | Apartment: %s | Amount: %.2f | Date: %s",
                    companyName, employeeName, buildingAddress, apartmentNumber, amount, date
            );

            writer.write(line);
            writer.newLine();

            logger.info("Payment written to file: {}", payment.getId());
        } catch (IOException e) {
            logger.error("Error writing payment to file", e);
            throw new RuntimeException("Error writing payment to file", e);
        }
    }
}
