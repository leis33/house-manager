package nbu.dbProject.services;

import nbu.dbProject.entity.Building;
import nbu.dbProject.entity.Company;
import nbu.dbProject.entity.Employee;
import nbu.dbProject.dao.BuildingRepository;
import nbu.dbProject.dao.CompanyRepository;
import nbu.dbProject.dao.EmployeeRepository;
import nbu.dbProject.utils.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

public class EmployeeService {

    private static final Logger logger = LogManager.getLogger(EmployeeService.class);
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final BuildingRepository buildingRepository;

    public EmployeeService() {
        this.employeeRepository = new EmployeeRepository();
        this.companyRepository = new CompanyRepository();
        this.buildingRepository = new BuildingRepository();
    }

    public Employee createEmployee(Employee employee, Long companyId) {
        logger.info("Creating employee: {}", employee.getFullName());

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        employee.setCompany(company);
        ValidationUtil.validate(employee);

        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Employee employee) {
        logger.info("Updating employee: {}", employee.getId());

        if (employeeRepository.findById(employee.getId()).isEmpty()) {
            throw new IllegalArgumentException("Employee not found");
        }

        ValidationUtil.validate(employee);
        return employeeRepository.update(employee);
    }

    public void deleteEmployee(Long id) {
        logger.info("Deleting employee: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (!employee.getBuildings().isEmpty()) {
            redistributeBuildings(employee);
        }

        employee.setActive(false);
        employeeRepository.update(employee);

        logger.info("Employee {} marked as inactive, buildings redistributed", id);
    }

    private void redistributeBuildings(Employee departingEmployee) {
        List<Building> buildings = departingEmployee.getBuildings();
        Long companyId = departingEmployee.getCompany().getId();

        logger.info("Redistributing {} buildings from employee {}",
                buildings.size(), departingEmployee.getId());

        List<Building> buildingsToRedistribute = new ArrayList<>(buildings);

        for (Building building : buildingsToRedistribute) {
            Employee targetEmployee = employeeRepository
                    .findEmployeeWithFewestBuildings(companyId)
                    .orElseThrow(() -> new IllegalStateException(
                            "No active employees available for building redistribution"));

            departingEmployee.removeBuilding(building);
            targetEmployee.addBuilding(building);

            buildingRepository.update(building);

            logger.info("Building {} reassigned from employee {} to employee {}",
                    building.getId(), departingEmployee.getId(), targetEmployee.getId());
        }
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployeesByCompany(Long companyId) {
        return employeeRepository.findByCompanyId(companyId);
    }

    public List<Employee> getEmployeesSortedByNameAndBuildings() {
        return employeeRepository.findAllSortedByNameAndBuildings();
    }

    public Employee getEmployeeWithFewestBuildings(Long companyId) {
        return employeeRepository.findEmployeeWithFewestBuildings(companyId)
                .orElseThrow(() -> new IllegalStateException(
                        "No active employees found for company"));
    }
}