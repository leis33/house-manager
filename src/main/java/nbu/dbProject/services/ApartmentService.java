package nbu.dbProject.services;

import nbu.dbProject.entity.Apartment;
import nbu.dbProject.entity.Building;
import nbu.dbProject.dao.ApartmentRepository;
import nbu.dbProject.dao.BuildingRepository;
import nbu.dbProject.utils.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ApartmentService {
    private static final Logger logger = LogManager.getLogger(ApartmentService.class);
    private final ApartmentRepository apartmentRepository;
    private final BuildingRepository buildingRepository;

    public ApartmentService() {
        this.apartmentRepository = new ApartmentRepository();
        this.buildingRepository = new BuildingRepository();
    }

    public Apartment createApartment(Apartment apartment, Long buildingId) {
        logger.info("Creating apartment: {}", apartment.getApartmentNumber());

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("Building not found"));

        apartment.setBuilding(building);
        ValidationUtil.validate(apartment);
        return apartmentRepository.save(apartment);
    }

    public Apartment updateApartment(Apartment apartment) {
        logger.info("Updating apartment: {}", apartment.getId());
        ValidationUtil.validate(apartment);
        return apartmentRepository.update(apartment);
    }

    public void deleteApartment(Long id) {
        logger.info("Deleting apartment: {}", id);
        apartmentRepository.delete(id);
    }

    public Apartment getApartmentById(Long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Apartment not found"));
    }

    public List<Apartment> getAllApartments() {
        return apartmentRepository.findAll();
    }

    public List<Apartment> getApartmentsByBuilding(Long buildingId) {
        return apartmentRepository.findByBuildingId(buildingId);
    }
}
