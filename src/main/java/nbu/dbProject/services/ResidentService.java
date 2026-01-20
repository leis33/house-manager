package nbu.dbProject.services;

import nbu.dbProject.entity.Apartment;
import nbu.dbProject.entity.Resident;
import nbu.dbProject.dao.ApartmentRepository;
import nbu.dbProject.dao.ResidentRepository;
import nbu.dbProject.utils.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ResidentService {
    private static final Logger logger = LogManager.getLogger(ResidentService.class);
    private final ResidentRepository residentRepository;
    private final ApartmentRepository apartmentRepository;

    public ResidentService() {
        this.residentRepository = new ResidentRepository();
        this.apartmentRepository = new ApartmentRepository();
    }

    public Resident createResident(Resident resident, Long apartmentId) {
        logger.info("Creating resident: {}", resident.getFullName());

        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new IllegalArgumentException("Apartment not found"));

        apartment.addResident(resident);

        ValidationUtil.validate(resident);
        return residentRepository.save(resident);
    }

    public Resident updateResident(Resident resident) {
        logger.info("Updating resident: {}", resident.getId());
        ValidationUtil.validate(resident);
        return residentRepository.update(resident);
    }

    public void deleteResident(Long id) {
        logger.info("Deleting resident: {}", id);

        Resident resident = residentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resident not found"));

        if (resident.getApartment() != null) {
            resident.getApartment().removeResident(resident);
        }

        residentRepository.delete(id);
    }

    public Resident getResidentById(Long id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resident not found"));
    }

    public List<Resident> getAllResidents() {
        return residentRepository.findAll();
    }

    public List<Resident> getResidentsByBuilding(Long buildingId) {
        return residentRepository.findByBuildingId(buildingId);
    }
}
