package nbu.dbProject.dao;

import nbu.dbProject.configuration.SessionFactoryUtil;
import nbu.dbProject.entity.Fee;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FeeRepository {

    public Fee save(Fee fee) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(fee);
            transaction.commit();
            return fee;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error saving fee", e);
        }
    }

    public Fee update(Fee fee) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Fee updated = session.merge(fee);
            transaction.commit();
            return updated;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error updating fee", e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Fee fee = session.get(Fee.class, id);
            if (fee != null) session.remove(fee);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting fee", e);
        }
    }

    public Optional<Fee> findById(Long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT f FROM Fee f " +
                    "LEFT JOIN FETCH f.building b " +
                    "LEFT JOIN FETCH b.employee " +
                    "LEFT JOIN FETCH b.company " +
                    "LEFT JOIN FETCH f.apartment " +
                    "WHERE f.id = :id";
            Query<Fee> query = session.createQuery(hql, Fee.class);
            query.setParameter("id", id);
            return query.uniqueResultOptional();
        }
    }

    public List<Fee> findAll() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT f FROM Fee f " +
                    "LEFT JOIN FETCH f.building b " +
                    "LEFT JOIN FETCH b.employee " +
                    "LEFT JOIN FETCH b.company " +
                    "LEFT JOIN FETCH f.apartment";
            return session.createQuery(hql, Fee.class).list();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Fee> findByBuildingId(Long buildingId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT f FROM Fee f " +
                    "LEFT JOIN FETCH f.building b " +
                    "LEFT JOIN FETCH b.employee " +
                    "LEFT JOIN FETCH b.company " +
                    "LEFT JOIN FETCH f.apartment " +
                    "WHERE f.building.id = :buildingId";
            Query<Fee> query = session.createQuery(hql, Fee.class);
            query.setParameter("buildingId", buildingId);
            return query.list();
        }
    }

    public List<Fee> findByApartmentId(Long apartmentId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT f FROM Fee f " +
                    "LEFT JOIN FETCH f.building b " +
                    "LEFT JOIN FETCH b.employee " +
                    "LEFT JOIN FETCH b.company " +
                    "LEFT JOIN FETCH f.apartment " +
                    "WHERE f.apartment.id = :apartmentId";
            Query<Fee> query = session.createQuery(hql, Fee.class);
            query.setParameter("apartmentId", apartmentId);
            return query.list();
        }
    }

    public List<Fee> findUnpaidFees() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT f FROM Fee f " +
                    "LEFT JOIN FETCH f.building b " +
                    "LEFT JOIN FETCH b.employee " +
                    "LEFT JOIN FETCH b.company " +
                    "LEFT JOIN FETCH f.apartment " +
                    "WHERE f.paid = false";
            return session.createQuery(hql, Fee.class).list();
        }
    }

    public double getTotalFeesByCompany(Long companyId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT SUM(f.totalAmount) FROM Fee f WHERE f.building.company.id = :companyId";
            Double total = session.createQuery(hql, Double.class)
                    .setParameter("companyId", companyId).uniqueResult();
            return total != null ? total : 0.0;
        }
    }

    public double getTotalFeesByBuilding(Long buildingId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT SUM(f.totalAmount) FROM Fee f WHERE f.building.id = :buildingId";
            Double total = session.createQuery(hql, Double.class)
                    .setParameter("buildingId", buildingId).uniqueResult();
            return total != null ? total : 0.0;
        }
    }

    public double getTotalFeesByEmployee(Long employeeId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT SUM(f.totalAmount) FROM Fee f WHERE f.building.employee.id = :employeeId";
            Double total = session.createQuery(hql, Double.class)
                    .setParameter("employeeId", employeeId).uniqueResult();
            return total != null ? total : 0.0;
        }
    }
}
