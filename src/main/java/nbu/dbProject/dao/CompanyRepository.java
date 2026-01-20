package nbu.dbProject.dao;

import nbu.dbProject.configuration.SessionFactoryUtil;
import nbu.dbProject.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompanyRepository {

    private static final Logger logger = LogManager.getLogger(CompanyRepository.class);

    public Company save(Company company) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(company);
            transaction.commit();
            logger.info("Company saved: {}", company);
            return company;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving company", e);
            throw new RuntimeException("Error saving company", e);
        }
    }

    public Company update(Company company) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Company updated = session.merge(company);
            transaction.commit();
            logger.info("Company updated: {}", updated);
            return updated;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating company", e);
            throw new RuntimeException("Error updating company", e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Company company = session.get(Company.class, id);
            if (company != null) {
                session.remove(company);
                logger.info("Company deleted: {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error deleting company", e);
            throw new RuntimeException("Error deleting company", e);
        }
    }

    public Optional<Company> findById(Long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Company c " +
                    "LEFT JOIN FETCH c.employees " +
                    "WHERE c.id = :id";
            Query<Company> query = session.createQuery(hql, Company.class);
            query.setParameter("id", id);
            Company company = query.uniqueResult();

            if (company != null) {
                String hql2 = "SELECT c FROM Company c " +
                        "LEFT JOIN FETCH c.buildings " +
                        "WHERE c.id = :id";
                Query<Company> query2 = session.createQuery(hql2, Company.class);
                query2.setParameter("id", id);
                query2.uniqueResult();
            }

            return Optional.ofNullable(company);
        } catch (Exception e) {
            logger.error("Error finding company by id", e);
            return Optional.empty();
        }
    }

    public List<Company> findAll() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Company", Company.class).list();
        } catch (Exception e) {
            logger.error("Error finding all companies", e);
            return new ArrayList<>();
        }
    }

    public Optional<Company> findByRegistrationNumber(String registrationNumber) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Query<Company> query = session.createQuery(
                    "FROM Company c WHERE c.registrationNumber = :regNumber", Company.class);
            query.setParameter("regNumber", registrationNumber);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding company by registration number", e);
            return Optional.empty();
        }
    }

    public List<Company> findAllSortedByRevenue() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Company c " +
                    "LEFT JOIN c.buildings b " +
                    "LEFT JOIN b.fees f " +
                    "LEFT JOIN f.payments p " +
                    "GROUP BY c.id " +
                    "ORDER BY SUM(COALESCE(p.amount, 0)) DESC";
            return session.createQuery(hql, Company.class).list();
        } catch (Exception e) {
            logger.error("Error finding companies sorted by revenue", e);
            return new ArrayList<>();
        }
    }
}
