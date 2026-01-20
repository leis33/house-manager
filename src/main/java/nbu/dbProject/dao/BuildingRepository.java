package nbu.dbProject.dao;

import nbu.dbProject.configuration.SessionFactoryUtil;
import nbu.dbProject.entity.Building;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuildingRepository {

    private static final Logger logger = LogManager.getLogger(BuildingRepository.class);

    public Building save(Building building) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(building);
            transaction.commit();
            logger.info("Building saved: {}", building);

            return findById(building.getId()).orElse(building);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving building", e);
            throw new RuntimeException("Error saving building", e);
        }
    }

    public Building update(Building building) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Building updated = session.merge(building);
            transaction.commit();
            logger.info("Building updated: {}", updated);

            return findById(updated.getId()).orElse(updated);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating building", e);
            throw new RuntimeException("Error updating building", e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Building building = session.get(Building.class, id);
            if (building != null) {
                session.remove(building);
                logger.info("Building deleted: {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error deleting building", e);
            throw new RuntimeException("Error deleting building", e);
        }
    }

    public Optional<Building> findById(Long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT b FROM Building b " +
                    "LEFT JOIN FETCH b.employee " +
                    "LEFT JOIN FETCH b.company " +
                    "WHERE b.id = :id";
            Query<Building> query = session.createQuery(hql, Building.class);
            query.setParameter("id", id);
            Building building = query.uniqueResult();

            if (building != null) {
                String hql2 = "SELECT b FROM Building b " +
                        "LEFT JOIN FETCH b.apartments " +
                        "WHERE b.id = :id";
                Query<Building> query2 = session.createQuery(hql2, Building.class);
                query2.setParameter("id", id);
                query2.uniqueResult();
            }

            return Optional.ofNullable(building);
        } catch (Exception e) {
            logger.error("Error finding building by id", e);
            return Optional.empty();
        }
    }

    public List<Building> findAll() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT b FROM Building b " +
                    "LEFT JOIN FETCH b.employee " +
                    "LEFT JOIN FETCH b.company";
            return session.createQuery(hql, Building.class).list();
        } catch (Exception e) {
            logger.error("Error finding all buildings", e);
            return new ArrayList<>();
        }
    }

    public List<Building> findByEmployeeId(Long employeeId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT b FROM Building b " +
                    "LEFT JOIN FETCH b.apartments " +
                    "LEFT JOIN FETCH b.company " +
                    "WHERE b.employee.id = :employeeId";
            Query<Building> query = session.createQuery(hql, Building.class);
            query.setParameter("employeeId", employeeId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding buildings by employee", e);
            return new ArrayList<>();
        }
    }

    public List<Building> findByCompanyId(Long companyId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT b FROM Building b " +
                    "LEFT JOIN FETCH b.employee " +
                    "LEFT JOIN FETCH b.company " +
                    "WHERE b.company.id = :companyId";
            Query<Building> query = session.createQuery(hql, Building.class);
            query.setParameter("companyId", companyId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding buildings by company", e);
            return new ArrayList<>();
        }
    }
}
