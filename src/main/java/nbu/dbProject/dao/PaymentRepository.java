package nbu.dbProject.dao;

import nbu.dbProject.configuration.SessionFactoryUtil;
import nbu.dbProject.entity.Payment;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentRepository {

    public Payment save(Payment payment) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(payment);
            transaction.commit();
            return payment;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error saving payment", e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Payment payment = session.get(Payment.class, id);
            if (payment != null) session.remove(payment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting payment", e);
        }
    }

    public Optional<Payment> findById(Long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(Payment.class, id));
        }
    }

    public List<Payment> findAll() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Payment", Payment.class).list();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Payment> findByFeeId(Long feeId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Query<Payment> query = session.createQuery(
                    "FROM Payment p WHERE p.fee.id = :feeId", Payment.class);
            query.setParameter("feeId", feeId);
            return query.list();
        }
    }

    public double getTotalAmountByCompany(Long companyId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT SUM(p.amount) FROM Payment p WHERE p.fee.building.company.id = :companyId";
            Double total = session.createQuery(hql, Double.class)
                    .setParameter("companyId", companyId).uniqueResult();
            return total != null ? total : 0.0;
        }
    }

    public double getTotalAmountByBuilding(Long buildingId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT SUM(p.amount) FROM Payment p WHERE p.fee.building.id = :buildingId";
            Double total = session.createQuery(hql, Double.class)
                    .setParameter("buildingId", buildingId).uniqueResult();
            return total != null ? total : 0.0;
        }
    }

    public double getTotalAmountByEmployee(Long employeeId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT SUM(p.amount) FROM Payment p WHERE p.fee.building.employee.id = :employeeId";
            Double total = session.createQuery(hql, Double.class)
                    .setParameter("employeeId", employeeId).uniqueResult();
            return total != null ? total : 0.0;
        }
    }
}
