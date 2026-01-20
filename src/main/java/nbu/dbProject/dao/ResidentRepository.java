package nbu.dbProject.dao;

import nbu.dbProject.configuration.SessionFactoryUtil;
import nbu.dbProject.entity.Resident;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResidentRepository {

    public Resident save(Resident resident) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(resident);
            transaction.commit();
            return resident;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error saving resident", e);
        }
    }

    public Resident update(Resident resident) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Resident updated = session.merge(resident);
            transaction.commit();
            return updated;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error updating resident", e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Resident resident = session.get(Resident.class, id);
            if (resident != null) session.remove(resident);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting resident", e);
        }
    }

    public Optional<Resident> findById(Long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT r FROM Resident r " +
                    "LEFT JOIN FETCH r.apartment " +
                    "WHERE r.id = :id";
            Query<Resident> query = session.createQuery(hql, Resident.class);
            query.setParameter("id", id);
            return query.uniqueResultOptional();
        }
    }

    public List<Resident> findAll() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT r FROM Resident r " +
                    "LEFT JOIN FETCH r.apartment";
            return session.createQuery(hql, Resident.class).list();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Resident> findByBuildingId(Long buildingId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT r FROM Resident r " +
                    "LEFT JOIN FETCH r.apartment " +
                    "WHERE r.apartment.building.id = :buildingId";
            Query<Resident> query = session.createQuery(hql, Resident.class);
            query.setParameter("buildingId", buildingId);
            return query.list();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}