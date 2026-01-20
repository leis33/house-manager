package nbu.dbProject.services;

import nbu.dbProject.entity.Apartment;
import nbu.dbProject.entity.Pet;
import nbu.dbProject.dao.ApartmentRepository;
import nbu.dbProject.dao.PetRepository;
import nbu.dbProject.utils.ValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PetService {
    private static final Logger logger = LogManager.getLogger(PetService.class);
    private final PetRepository petRepository;
    private final ApartmentRepository apartmentRepository;

    public PetService() {
        this.petRepository = new PetRepository();
        this.apartmentRepository = new ApartmentRepository();
    }

    public Pet createPet(Pet pet, Long apartmentId) {
        logger.info("Creating pet: {}", pet.getName());

        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new IllegalArgumentException("Apartment not found"));

        apartment.addPet(pet);

        ValidationUtil.validate(pet);
        return petRepository.save(pet);
    }

    public Pet updatePet(Pet pet) {
        logger.info("Updating pet: {}", pet.getId());
        ValidationUtil.validate(pet);
        return petRepository.update(pet);
    }

    public void deletePet(Long id) {
        logger.info("Deleting pet: {}", id);

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found"));

        if (pet.getApartment() != null) {
            pet.getApartment().removePet(pet);
        }

        petRepository.delete(id);
    }

    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found"));
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }
}
