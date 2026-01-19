package nbu.dbProject.services;

import nbu.dbProject.dao.ApartmentRepository;
import nbu.dbProject.dao.BuildingRepository;
import nbu.dbProject.dao.FeeRepository;
import nbu.dbProject.entity.Apartment;
import nbu.dbProject.entity.Building;
import nbu.dbProject.entity.Fee;
import nbu.dbProject.utils.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;

public class FeeService {

    private static final Logger logger = LogManager.getLogger(FeeService.class);
    private final FeeRepository feeRepository;
    private final BuildingRepository buildingRepository;
    private final ApartmentRepository apartmentRepository;

    // Fee calculation constants
    private static final double BASE_RATE_PER_SQM = 2.0;
    private static final double RESIDENT_OVER_7_CHARGE = 10.0;
    private static final double PET_COMMON_AREA_CHARGE = 15.0;

    public FeeService() {
        this.feeRepository = new FeeRepository();
        this.buildingRepository = new BuildingRepository();
        this.apartmentRepository = new ApartmentRepository();
    }

    public Fee createFee(Long buildingId, Long apartmentId, LocalDate feeDate) {
        logger.info("Creating fee for building {} apartment {}", buildingId, apartmentId);

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("Building not found"));

        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new IllegalArgumentException("Apartment not found"));

        if (!(apartment.getBuilding().getId() == buildingId)) {
            throw new IllegalArgumentException("Apartment does not belong to this building");
        }

        // Calculate fee components
        double baseAmount = calculateBaseAmount(apartment);
        double residentCharge = calculateResidentCharge(apartment, building);
        double petCharge = calculatePetCharge(apartment);

        Fee fee = new Fee(building, apartment, feeDate, baseAmount, residentCharge, petCharge);

        ValidationUtil.validate(fee);
        return feeRepository.save(fee);
    }

    public Fee generateMonthlyFee(Long apartmentId, LocalDate month) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new IllegalArgumentException("Apartment not found"));

        Building building = apartment.getBuilding();

        return createFee(building.getId(), apartmentId, month);
    }

    public List<Fee> generateMonthlyFeesForBuilding(Long buildingId, LocalDate month) {
        logger.info("Generating monthly fees for building {} for month {}", buildingId, month);

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("Building not found"));

        return building.getApartments().stream()
                .map(apartment -> createFee(buildingId, apartment.getId(), month))
                .toList();
    }

    private double calculateBaseAmount(Apartment apartment) {
        return apartment.getArea() * BASE_RATE_PER_SQM;
    }

    private double calculateResidentCharge(Apartment apartment, Building building) {
        if (!building.isHasElevator()) {
            return 0.0;
        }

        int residentsOver7 = apartment.getResidentsOver7Count();
        return residentsOver7 * RESIDENT_OVER_7_CHARGE;
    }

    private double calculatePetCharge(Apartment apartment) {
        int petsUsingCommonAreas = apartment.getPetsUsingCommonAreasCount();
        return petsUsingCommonAreas * PET_COMMON_AREA_CHARGE;
    }

    public Fee getFeeById(Long id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fee not found"));
    }

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public List<Fee> getFeesByBuilding(Long buildingId) {
        return feeRepository.findByBuildingId(buildingId);
    }

    public List<Fee> getFeesByApartment(Long apartmentId) {
        return feeRepository.findByApartmentId(apartmentId);
    }

    public List<Fee> getUnpaidFees() {
        return feeRepository.findUnpaidFees();
    }

    public void deleteFee(Long id) {
        feeRepository.delete(id);
        logger.info("Fee deleted: {}", id);
    }
}
