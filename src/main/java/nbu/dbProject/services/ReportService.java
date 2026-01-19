package nbu.dbProject.services;

import nbu.dbProject.entity.*;
import nbu.dbProject.dao.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {

    private static final Logger logger = LogManager.getLogger(ReportService.class);
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final BuildingRepository buildingRepository;
    private final ApartmentRepository apartmentRepository;
    private final ResidentRepository residentRepository;
    private final PaymentRepository paymentRepository;
    private final FeeRepository feeRepository;

    public ReportService() {
        this.companyRepository = new CompanyRepository();
        this.employeeRepository = new EmployeeRepository();
        this.buildingRepository = new BuildingRepository();
        this.apartmentRepository = new ApartmentRepository();
        this.residentRepository = new ResidentRepository();
        this.paymentRepository = new PaymentRepository();
        this.feeRepository = new FeeRepository();
    }

    // Buildings served by each employee in a company
    public String buildingsByEmployeeReport(Long companyId) {
        logger.info("Generating buildings by employee report for company {}", companyId);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        List<Employee> employees = employeeRepository.findByCompanyId(companyId);

        StringBuilder report = new StringBuilder();
        report.append("===== BUILDINGS BY EMPLOYEE REPORT =====\n");
        report.append("Company: ").append(company.getName()).append("\n\n");

        for (Employee employee : employees) {
            List<Building> buildings = buildingRepository.findByEmployeeId(employee.getId());
            report.append("Employee: ").append(employee.getFullName()).append("\n");
            report.append("Total Buildings: ").append(buildings.size()).append("\n");

            if (!buildings.isEmpty()) {
                report.append("Buildings:\n");
                for (Building building : buildings) {
                    report.append("  - ").append(building.getAddress())
                            .append(" (Floors: ").append(building.getNumberOfFloors())
                            .append(", Apartments: ").append(building.getNumberOfApartments())
                            .append(")\n");
                }
            }
            report.append("\n");
        }

        return report.toString();
    }

    // Apartments in a building
    public String apartmentsInBuildingReport(Long buildingId) {
        logger.info("Generating apartments report for building {}", buildingId);

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("Building not found"));

        List<Apartment> apartments = apartmentRepository.findByBuildingId(buildingId);

        StringBuilder report = new StringBuilder();
        report.append("===== APARTMENTS REPORT =====\n");
        report.append("Building: ").append(building.getAddress()).append("\n");
        report.append("Total Apartments: ").append(apartments.size()).append("\n\n");

        for (Apartment apartment : apartments) {
            report.append("Apartment: ").append(apartment.getApartmentNumber()).append("\n");
            report.append("  Floor: ").append(apartment.getFloor()).append("\n");
            report.append("  Area: ").append(apartment.getArea()).append(" sq.m\n");
            report.append("  Owner: ")
                    .append(apartment.getOwner() != null ?
                            apartment.getOwner().getFullName() : "N/A")
                    .append("\n");
            report.append("  Residents: ").append(apartment.getResidents().size()).append("\n");
            report.append("  Pets: ").append(apartment.getPets().size()).append("\n\n");
        }

        return report.toString();
    }

    // Residents in a building sorted by name and age
    public String residentsInBuildingReport(Long buildingId) {
        logger.info("Generating residents report for building {}", buildingId);

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("Building not found"));

        List<Resident> residents = residentRepository.findByBuildingId(buildingId);

        // Sort by name and age
        residents.sort(Comparator.comparing(Resident::getLastName)
                .thenComparing(Resident::getFirstName)
                .thenComparing(Resident::getAge));

        StringBuilder report = new StringBuilder();
        report.append("===== RESIDENTS REPORT =====\n");
        report.append("Building: ").append(building.getAddress()).append("\n");
        report.append("Total Residents: ").append(residents.size()).append("\n\n");

        for (Resident resident : residents) {
            report.append("Name: ").append(resident.getFullName()).append("\n");
            report.append("  Age: ").append(resident.getAge()).append("\n");
            report.append("  Apartment: ")
                    .append(resident.getApartment().getApartmentNumber()).append("\n");
            report.append("  Phone: ")
                    .append(resident.getPhoneNumber() != null ?
                            resident.getPhoneNumber() : "N/A")
                    .append("\n\n");
        }

        return report.toString();
    }

    // Payment amounts by company
    public String paymentAmountsByCompanyReport() {
        logger.info("Generating payment amounts by company report");

        List<Company> companies = companyRepository.findAll();

        StringBuilder report = new StringBuilder();
        report.append("===== PAYMENT AMOUNTS BY COMPANY =====\n\n");

        for (Company company : companies) {
            double totalFees = feeRepository.getTotalFeesByCompany(company.getId());
            double totalPaid = paymentRepository.getTotalAmountByCompany(company.getId());

            report.append("Company: ").append(company.getName()).append("\n");
            report.append("  Total Fees: ").append(String.format("%.2f", totalFees)).append("\n");
            report.append("  Total Paid: ").append(String.format("%.2f", totalPaid)).append("\n");
            report.append("  Outstanding: ")
                    .append(String.format("%.2f", totalFees - totalPaid)).append("\n\n");
        }

        return report.toString();
    }

    // Payment amounts by building
    public String paymentAmountsByBuildingReport(Long companyId) {
        logger.info("Generating payment amounts by building report");

        List<Building> buildings = buildingRepository.findByCompanyId(companyId);

        StringBuilder report = new StringBuilder();
        report.append("===== PAYMENT AMOUNTS BY BUILDING =====\n\n");

        for (Building building : buildings) {
            double totalFees = feeRepository.getTotalFeesByBuilding(building.getId());
            double totalPaid = paymentRepository.getTotalAmountByBuilding(building.getId());

            report.append("Building: ").append(building.getAddress()).append("\n");
            report.append("  Total Fees: ").append(String.format("%.2f", totalFees)).append("\n");
            report.append("  Total Paid: ").append(String.format("%.2f", totalPaid)).append("\n");
            report.append("  Outstanding: ")
                    .append(String.format("%.2f", totalFees - totalPaid)).append("\n\n");
        }

        return report.toString();
    }

    // Payment amounts by employee
    public String paymentAmountsByEmployeeReport(Long companyId) {
        logger.info("Generating payment amounts by employee report");

        List<Employee> employees = employeeRepository.findByCompanyId(companyId);

        StringBuilder report = new StringBuilder();
        report.append("===== PAYMENT AMOUNTS BY EMPLOYEE =====\n\n");

        for (Employee employee : employees) {
            double totalFees = feeRepository.getTotalFeesByEmployee(employee.getId());
            double totalPaid = paymentRepository.getTotalAmountByEmployee(employee.getId());

            report.append("Employee: ").append(employee.getFullName()).append("\n");
            report.append("  Buildings Managed: ").append(employee.getBuildingCount()).append("\n");
            report.append("  Total Fees: ").append(String.format("%.2f", totalFees)).append("\n");
            report.append("  Total Collected: ").append(String.format("%.2f", totalPaid)).append("\n");
            report.append("  Collection Rate: ");
            if (totalFees > 0) {
                report.append(String.format("%.1f%%", (totalPaid / totalFees) * 100));
            } else {
                report.append("N/A");
            }
            report.append("\n\n");
        }

        return report.toString();
    }

    // Companies sorted by revenue
    public String companiesByRevenueReport() {
        logger.info("Generating companies by revenue report");

        List<Company> companies = companyRepository.findAll();

        Map<Company, Double> revenueMap = companies.stream()
                .collect(Collectors.toMap(
                        c -> c,
                        c -> paymentRepository.getTotalAmountByCompany(c.getId())
                ));

        List<Company> sortedCompanies = revenueMap.entrySet().stream()
                .sorted(Map.Entry.<Company, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        StringBuilder report = new StringBuilder();
        report.append("===== COMPANIES BY REVENUE =====\n\n");

        for (Company company : sortedCompanies) {
            double revenue = revenueMap.get(company);
            report.append("Company: ").append(company.getName()).append("\n");
            report.append("  Revenue: ").append(String.format("%.2f", revenue)).append("\n");
            report.append("  Buildings: ").append(company.getBuildings().size()).append("\n");
            report.append("  Employees: ").append(company.getEmployees().size()).append("\n\n");
        }

        return report.toString();
    }
}