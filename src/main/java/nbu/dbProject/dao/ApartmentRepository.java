package nbu.dbProject.dao;

import nbu.dbProject.configuration.SessionFactoryUtil;
import nbu.dbProject.entity.Apartment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApartmentRepository {
    private static final Logger logger = LogManager.getLogger(ApartmentRepository.class);

    public Apartment save(Apartment apartment) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(apartment);
            transaction.commit();
            return apartment;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error saving apartment", e);
            throw new RuntimeException("Error saving apartment", e);
        }
    }

    public Apartment update(Apartment apartment) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Apartment updated = session.merge(apartment);
            transaction.commit();
            return updated;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error updating apartment", e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Apartment apartment = session.get(Apartment.class, id);
            if (apartment != null) session.remove(apartment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting apartment", e);
        }
    }

    public Optional<Apartment> findById(Long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Apartment.class, id));
        }
    }

    public List<Apartment> findAll() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Apartment", Apartment.class).list();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Apartment> findByBuildingId(Long buildingId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Query<Apartment> query = session.createQuery(
                    "FROM Apartment a WHERE a.building.id = :buildingId", Apartment.class);
            query.setParameter("buildingId", buildingId);
            return query.list();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
