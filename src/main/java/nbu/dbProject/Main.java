package nbu.dbProject;

import nbu.dbProject.configuration.SessionFactoryUtil;
import nbu.dbProject.entity.*;
import nbu.dbProject.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);

    private static CompanyService companyService = new CompanyService();
    private static EmployeeService employeeService = new EmployeeService();
    private static BuildingService buildingService = new BuildingService();
    private static ApartmentService apartmentService = new ApartmentService();
    private static OwnerService ownerService = new OwnerService();
    private static ResidentService residentService = new ResidentService();
    private static PetService petService = new PetService();
    private static FeeService feeService = new FeeService();
    private static PaymentService paymentService = new PaymentService();
    private static ReportService reportService = new ReportService();

    public static void main(String[] args) {
        logger.info("Starting Electronic Home Manager Application");

        try {
//            demonstrateApplication();
            mainMenu();

        } catch (Exception e) {
            logger.error("Application error", e);
            e.printStackTrace();
        } finally {
            SessionFactoryUtil.shutdown();
            scanner.close();
            logger.info("Application shutdown complete");
        }
    }

    private static void demonstrateApplication() {
        logger.info("Creating sample data...");

        try {
            // company
            Company company = new Company(
                    "Elite Property Management",
                    "123456738",
                    "Sofia, Bulgaria",
                    "+359888123456"
            );
            company = companyService.createCompany(company);
            System.out.println("Created company: " + company.getName());

            // building
            Building building = new Building(
                    "123 Main Street, Sofia",
                    5,
                    10,
                    1000.0,
                    200.0,
                    true
            );
            building = buildingService.createBuilding(building, company.getId());
            System.out.println("Created building: " + building.getAddress());
            System.out.println("Assigned to employee: " + building.getEmployee().getFullName());

            // employees
            Employee emp1 = new Employee("Ivan", "Petrov", "ivan@example.com", "+359888111111");
            emp1 = employeeService.createEmployee(emp1, company.getId());

            Employee emp2 = new Employee("Maria", "Ivanova", "maria@example.com", "+359888222222");
            emp2 = employeeService.createEmployee(emp2, company.getId());

            System.out.println("Created employees: " + emp1.getFullName() + ", " + emp2.getFullName());

            // owner
            Owner owner = new Owner("Georgi", "Dimitrov", 45, "+359888333333", "georgi@example.com");
            owner = ownerService.createOwner(owner);

            // apartment
            Apartment apartment = new Apartment("101", 1, 75.0);
            apartment = apartmentService.createApartment(apartment, building.getId());
            apartment.setOwner(owner);
            apartment = apartmentService.updateApartment(apartment);
            System.out.println("Created apartment: " + apartment.getApartmentNumber());

            // residents
            Resident resident1 = new Resident("Elena", "Dimitrova", 8, "+359888444444", null);
            resident1 = residentService.createResident(resident1, apartment.getId());

            Resident resident2 = new Resident("Nikola", "Dimitrov", 12, "+359888555555", null);
            resident2 = residentService.createResident(resident2, apartment.getId());

            System.out.println("Added residents: " + resident1.getFullName() + ", " + resident2.getFullName());

            // pet
            Pet pet = new Pet("Max", "Dog", true);
            pet = petService.createPet(pet, apartment.getId());
            System.out.println("Added pet: " + pet.getName());

            // monthly fee
            Fee fee = feeService.generateMonthlyFee(apartment.getId(), LocalDate.now());
            System.out.println("\nGenerated monthly fee:");
            System.out.println("  Base amount: " + fee.getBaseAmount());
            System.out.println("  Resident charge: " + fee.getResidentCharge());
            System.out.println("  Pet charge: " + fee.getPetCharge());
            System.out.println("  Total: " + fee.getTotalAmount());

            // payment
            Payment payment = paymentService.createPayment(
                    fee.getId(),
                    fee.getTotalAmount(),
                    "Full payment"
            );
            System.out.println("\nPayment recorded: " + payment.getAmount());
            System.out.println("Fee marked as paid: " + feeService.getFeeById(fee.getId()).isPaid());

            System.out.println("\n=== Sample data created successfully ===\n");

        } catch (Exception e) {
            logger.error("Error creating sample data", e);
            System.err.println("Error creating sample data: " + e.getMessage());
        }
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n====== ELECTRONIC HOME MANAGER ======");
            System.out.println("1. Company Management");
            System.out.println("2. Employee Management");
            System.out.println("3. Building Management");
            System.out.println("4. Apartment Management");
            System.out.println("5. Owner & Resident Management");
            System.out.println("6. Fee Management");
            System.out.println("7. Payment Management");
            System.out.println("8. Reports");
            System.out.println("0. Exit");
            System.out.print("Select option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> companyMenu();
                case 2 -> employeeMenu();
                case 3 -> buildingMenu();
                case 4 -> apartmentMenu();
                case 5 -> ownerResidentMenu();
                case 6 -> feeMenu();
                case 7 -> paymentMenu();
                case 8 -> reportsMenu();
                case 0 -> {
                    System.out.println("Exiting application...");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private static void companyMenu() {
        System.out.println("\n--- Company Management ---");
        System.out.println("1. List all companies");
        System.out.println("2. Create company");
        System.out.println("3. Update company");
        System.out.println("4. Delete company");
        System.out.println("0. Back");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> listCompanies();
            case 2 -> createCompany();
            case 3 -> updateCompany();
            case 4 -> deleteCompany();
        }
    }

    private static void listCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        System.out.println("\n=== Companies ===");
        for (Company company : companies) {
            System.out.println("ID: " + company.getId() + " | " + company.getName() +
                    " | " + company.getAddress());
        }
    }

    private static void createCompany() {
        scanner.nextLine();
        System.out.print("Company name: ");
        String name = scanner.nextLine();
        System.out.print("Registration number: ");
        String regNum = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        try {
            Company company = new Company(name, regNum, address, phone);
            company = companyService.createCompany(company);
            System.out.println("Company created with ID: " + company.getId());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void updateCompany() {
        System.out.print("Enter company ID: ");
        Long id = getLongInput();

        try {
            Company company = companyService.getCompanyById(id);
            scanner.nextLine();

            System.out.print("New name (current: " + company.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) company.setName(name);

            System.out.print("New address (current: " + company.getAddress() + "): ");
            String address = scanner.nextLine();
            if (!address.isEmpty()) company.setAddress(address);

            companyService.updateCompany(company);
            System.out.println("Company updated successfully");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void deleteCompany() {
        System.out.print("Enter company ID to delete: ");
        Long id = getLongInput();

        try {
            companyService.deleteCompany(id);
            System.out.println("Company deleted successfully");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void employeeMenu() {
        System.out.println("\n--- Employee Management ---");
        System.out.println("1. List all employees");
        System.out.println("2. Create employee");
        System.out.println("3. Delete employee (redistribute buildings)");
        System.out.println("0. Back");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> listEmployees();
            case 2 -> createEmployee();
            case 3 -> deleteEmployee();
        }
    }

    private static void listEmployees() {
        List<Employee> employees = employeeService.getEmployeesSortedByNameAndBuildings();
        System.out.println("\n=== Employees ===");
        for (Employee employee : employees) {
            System.out.println("ID: " + employee.getId() + " | " + employee.getFullName() +
                    " | Buildings: " + employee.getBuildingCount() +
                    " | Active: " + employee.isActive());
        }
    }

    private static void createEmployee() {
        scanner.nextLine();
        System.out.print("First name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Company ID: ");
        Long companyId = getLongInput();

        try {
            Employee employee = new Employee(firstName, lastName, email, phone);
            employee = employeeService.createEmployee(employee, companyId);
            System.out.println("Employee created with ID: " + employee.getId());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void deleteEmployee() {
        System.out.print("Enter employee ID to delete: ");
        Long id = getLongInput();

        try {
            employeeService.deleteEmployee(id);
            System.out.println("Employee deactivated and buildings redistributed");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void buildingMenu() {
        System.out.println("\n--- Building Management ---");
        System.out.println("1. List all buildings");
        System.out.println("2. Create building");
        System.out.println("0. Back");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> listBuildings();
            case 2 -> createBuilding();
        }
    }

    private static void listBuildings() {
        List<Building> buildings = buildingService.getAllBuildings();
        System.out.println("\n=== Buildings ===");
        for (Building building : buildings) {
            System.out.println("ID: " + building.getId() + " | " + building.getAddress() +
                    " | Employee: " + (building.getEmployee() != null ?
                    building.getEmployee().getFullName() : "N/A"));
        }
    }

    private static void createBuilding() {
        scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Number of floors: ");
        int floors = getIntInput();
        System.out.print("Number of apartments: ");
        int apartments = getIntInput();
        System.out.print("Built-up area: ");
        double builtUpArea = getDoubleInput();
        System.out.print("Common area: ");
        double commonArea = getDoubleInput();
        System.out.print("Has elevator (true/false): ");
        boolean hasElevator = scanner.nextBoolean();
        System.out.print("Company ID: ");
        Long companyId = getLongInput();

        try {
            Building building = new Building(address, floors, apartments, builtUpArea, commonArea, hasElevator);
            building = buildingService.createBuilding(building, companyId);
            System.out.println("Building created with ID: " + building.getId());
            System.out.println("Assigned to employee: " + building.getEmployee().getFullName());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void apartmentMenu() {
        System.out.println("\n--- Apartment Management ---");
        System.out.println("1. List apartments in building");
        System.out.println("2. Create apartment");
        System.out.println("0. Back");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> listApartments();
            case 2 -> createApartment();
        }
    }

    private static void listApartments() {
        System.out.print("Enter building ID: ");
        Long buildingId = getLongInput();

        List<Apartment> apartments = apartmentService.getApartmentsByBuilding(buildingId);
        System.out.println("\n=== Apartments ===");
        for (Apartment apt : apartments) {
            System.out.println("ID: " + apt.getId() + " | Number: " + apt.getApartmentNumber() +
                    " | Floor: " + apt.getFloor() + " | Area: " + apt.getArea() +
                    " | Residents: " + apt.getResidents().size());
        }
    }

    private static void createApartment() {
        scanner.nextLine();
        System.out.print("Apartment number: ");
        String number = scanner.nextLine();
        System.out.print("Floor: ");
        int floor = getIntInput();
        System.out.print("Area (sq.m): ");
        double area = getDoubleInput();
        System.out.print("Building ID: ");
        Long buildingId = getLongInput();

        try {
            Apartment apartment = new Apartment(number, floor, area);
            apartment = apartmentService.createApartment(apartment, buildingId);
            System.out.println("Apartment created with ID: " + apartment.getId());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void ownerResidentMenu() {
        System.out.println("\n--- Owner & Resident Management ---");
        System.out.println("1. Create owner");
        System.out.println("2. Create resident");
        System.out.println("3. Create pet");
        System.out.println("0. Back");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> createOwner();
            case 2 -> createResident();
            case 3 -> createPet();
        }
    }

    private static void createOwner() {
        scanner.nextLine();
        System.out.print("First name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Age: ");
        int age = getIntInput();
        scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        try {
            Owner owner = new Owner(firstName, lastName, age, phone, email);
            owner = ownerService.createOwner(owner);
            System.out.println("Owner created with ID: " + owner.getId());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void createResident() {
        scanner.nextLine();
        System.out.print("First name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Age: ");
        int age = getIntInput();
        scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Apartment ID: ");
        Long apartmentId = getLongInput();

        try {
            Resident resident = new Resident(firstName, lastName, age, phone, null);
            resident = residentService.createResident(resident, apartmentId);
            System.out.println("Resident created with ID: " + resident.getId());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void createPet() {
        scanner.nextLine();
        System.out.print("Pet name: ");
        String name = scanner.nextLine();
        System.out.print("Pet type (Dog/Cat/etc): ");
        String type = scanner.nextLine();
        System.out.print("Uses common areas (true/false): ");
        boolean usesCommonAreas = scanner.nextBoolean();
        System.out.print("Apartment ID: ");
        Long apartmentId = getLongInput();

        try {
            Pet pet = new Pet(name, type, usesCommonAreas);
            pet = petService.createPet(pet, apartmentId);
            System.out.println("Pet created with ID: " + pet.getId());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void feeMenu() {
        System.out.println("\n--- Fee Management ---");
        System.out.println("1. Generate monthly fees for building");
        System.out.println("2. List unpaid fees");
        System.out.println("0. Back");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> generateMonthlyFees();
            case 2 -> listUnpaidFees();
        }
    }

    private static void generateMonthlyFees() {
        System.out.print("Building ID: ");
        Long buildingId = getLongInput();

        try {
            List<Fee> fees = feeService.generateMonthlyFeesForBuilding(buildingId, LocalDate.now());
            System.out.println("Generated " + fees.size() + " monthly fees");
            for (Fee fee : fees) {
                System.out.println("  Apartment " + fee.getApartment().getApartmentNumber() +
                        ": " + fee.getTotalAmount());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void listUnpaidFees() {
        List<Fee> fees = feeService.getUnpaidFees();
        System.out.println("\n=== Unpaid Fees ===");
        for (Fee fee : fees) {
            System.out.println("ID: " + fee.getId() + " | Apartment: " +
                    fee.getApartment().getApartmentNumber() + " | Amount: " + fee.getTotalAmount() +
                    " | Date: " + fee.getFeeDate());
        }
    }

    private static void paymentMenu() {
        System.out.println("\n--- Payment Management ---");
        System.out.println("1. Record payment");
        System.out.println("2. List all payments");
        System.out.println("0. Back");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> recordPayment();
            case 2 -> listPayments();
        }
    }

    private static void recordPayment() {
        System.out.print("Fee ID: ");
        Long feeId = getLongInput();
        System.out.print("Amount: ");
        double amount = getDoubleInput();
        scanner.nextLine();
        System.out.print("Notes: ");
        String notes = scanner.nextLine();

        try {
            Payment payment = paymentService.createPayment(feeId, amount, notes);
            System.out.println("Payment recorded with ID: " + payment.getId());
            System.out.println("Payment written to file: payments/payments.txt");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void listPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        System.out.println("\n=== Payments ===");
        for (Payment payment : payments) {
            System.out.println("ID: " + payment.getId() + " | Amount: " + payment.getAmount() +
                    " | Date: " + payment.getPaymentDate());
        }
    }

    private static void reportsMenu() {
        System.out.println("\n--- Reports ---");
        System.out.println("1. Buildings by employee");
        System.out.println("2. Apartments in building");
        System.out.println("3. Residents in building");
        System.out.println("4. Payment amounts by company");
        System.out.println("5. Payment amounts by building");
        System.out.println("6. Payment amounts by employee");
        System.out.println("0. Back");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> {
                System.out.print("Company ID: ");
                Long companyId = getLongInput();
                System.out.println(reportService.buildingsByEmployeeReport(companyId));
            }
            case 2 -> {
                System.out.print("Building ID: ");
                Long buildingId = getLongInput();
                System.out.println(reportService.apartmentsInBuildingReport(buildingId));
            }
            case 3 -> {
                System.out.print("Building ID: ");
                Long buildingId = getLongInput();
                System.out.println(reportService.residentsInBuildingReport(buildingId));
            }
            case 4 -> System.out.println(reportService.paymentAmountsByCompanyReport());
            case 5 -> {
                System.out.print("Company ID: ");
                Long companyId = getLongInput();
                System.out.println(reportService.paymentAmountsByBuildingReport(companyId));
            }
            case 6 -> {
                System.out.print("Company ID: ");
                Long companyId = getLongInput();
                System.out.println(reportService.paymentAmountsByEmployeeReport(companyId));
            }
        }
    }

    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static long getLongInput() {
        while (!scanner.hasNextLong()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        return scanner.nextLong();
    }

    private static double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        return scanner.nextDouble();
    }
}