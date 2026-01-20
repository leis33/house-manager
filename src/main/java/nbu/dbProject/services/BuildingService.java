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

public class BuildingService {

    private static final Logger logger = LogManager.getLogger(BuildingService.class);
    private final BuildingRepository buildingRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    public BuildingService() {
        this.buildingRepository = new BuildingRepository();
        this.companyRepository = new CompanyRepository();
        this.employeeRepository = new EmployeeRepository();
    }

    public Building createBuilding(Building building, Long companyId) {
        logger.info("Creating building: {}", building.getAddress());

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        building.setCompany(company);

        Employee employee = employeeRepository.findEmployeeWithFewestBuildings(companyId)
                .orElseThrow(() -> new IllegalStateException(
                        "No active employees available to assign building"));

        building.setEmployee(employee);

        ValidationUtil.validate(building);
        Building saved = buildingRepository.save(building);

        logger.info("Building assigned to employee: {}", employee.getFullName());
        return saved;
    }

    public Building updateBuilding(Building building) {
        logger.info("Updating building: {}", building.getId());

        if (!buildingRepository.findById(building.getId()).isPresent()) {
            throw new IllegalArgumentException("Building not found");
        }

        ValidationUtil.validate(building);
        return buildingRepository.update(building);
    }

    public void deleteBuilding(Long id) {
        logger.info("Deleting building: {}", id);
        buildingRepository.delete(id);
    }

    public Building getBuildingById(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Building not found"));
    }

    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    public List<Building> getBuildingsByEmployee(Long employeeId) {
        return buildingRepository.findByEmployeeId(employeeId);
    }

    public List<Building> getBuildingsByCompany(Long companyId) {
        return buildingRepository.findByCompanyId(companyId);
    }

    public void assignBuildingToEmployee(Long buildingId, Long employeeId) {
        Building building = getBuildingById(buildingId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (!employee.isActive()) {
            throw new IllegalArgumentException("Cannot assign building to inactive employee");
        }

        if (!(building.getCompany().getId() == employee.getCompany().getId())) {
            throw new IllegalArgumentException(
                    "Employee and building must belong to the same company");
        }

        building.setEmployee(employee);
        buildingRepository.update(building);

        logger.info("Building {} assigned to employee {}", buildingId, employeeId);
    }
}