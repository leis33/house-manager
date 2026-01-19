package nbu.dbProject.services;

import nbu.dbProject.entity.Owner;
import nbu.dbProject.dao.OwnerRepository;
import nbu.dbProject.utils.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class OwnerService {
    private static final Logger logger = LogManager.getLogger(OwnerService.class);
    private final OwnerRepository ownerRepository;

    public OwnerService() {
        this.ownerRepository = new OwnerRepository();
    }

    public Owner createOwner(Owner owner) {
        logger.info("Creating owner: {}", owner.getFullName());
        ValidationUtil.validate(owner);
        return ownerRepository.save(owner);
    }

    public Owner updateOwner(Owner owner) {
        logger.info("Updating owner: {}", owner.getId());
        ValidationUtil.validate(owner);
        return ownerRepository.update(owner);
    }

    public void deleteOwner(Long id) {
        logger.info("Deleting owner: {}", id);
        ownerRepository.delete(id);
    }

    public Owner getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
    }

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }
}
