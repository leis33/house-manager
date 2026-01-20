package nbu.dbProject.dao;

import nbu.dbProject.configuration.SessionFactoryUtil;
import nbu.dbProject.entity.Building;
import nbu.dbProject.entity.Employee;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepository {

    private static final Logger logger = LogManager.getLogger(EmployeeRepository.class);

    public Employee save(Employee employee) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(employee);
            transaction.commit();
            logger.info("Employee saved: {}", employee);
            return employee;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving employee", e);
            throw new RuntimeException("Error saving employee", e);
        }
    }

    public Employee update(Employee employee) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Employee updated = session.merge(employee);
            transaction.commit();
            logger.info("Employee updated: {}", updated);
            return updated;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating employee", e);
            throw new RuntimeException("Error updating employee", e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Employee employee = session.get(Employee.class, id);
            if (employee != null) {
                session.remove(employee);
                logger.info("Employee deleted: {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error deleting employee", e);
            throw new RuntimeException("Error deleting employee", e);
        }
    }

    public Optional<Employee> findById(Long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT e FROM Employee e " +
                    "LEFT JOIN FETCH e.buildings " +
                    "WHERE e.id = :id";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("id", id);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding employee by id", e);
            return Optional.empty();
        }
    }

    public List<Employee> findAll() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Employee", Employee.class).list();
        } catch (Exception e) {
            logger.error("Error finding all employees", e);
            return new ArrayList<>();
        }
    }

    public List<Employee> findByCompanyId(Long companyId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT e FROM Employee e " +
                    "LEFT JOIN FETCH e.buildings " +
                    "WHERE e.company.id = :companyId AND e.active = true";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("companyId", companyId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error finding employees by company", e);
            return new ArrayList<>();
        }
    }

    public Optional<Employee> findEmployeeWithFewestBuildings(Long companyId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT e FROM Employee e " +
                    "LEFT JOIN FETCH e.buildings " +
                    "WHERE e.company.id = :companyId AND e.active = true " +
                    "ORDER BY SIZE(e.buildings) ASC";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("companyId", companyId);
            query.setMaxResults(1);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            logger.error("Error finding employee with fewest buildings", e);
            return Optional.empty();
        }
    }

    public List<Employee> findAllSortedByNameAndBuildings() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT e FROM Employee e " +
                    "LEFT JOIN FETCH e.buildings " +
                    "ORDER BY e.lastName, e.firstName, SIZE(e.buildings) DESC";
            return session.createQuery(hql, Employee.class).list();
        } catch (Exception e) {
            logger.error("Error finding employees sorted", e);
            return new ArrayList<>();
        }
    }

    public List<Building> getBuildingsForEmployee(Long employeeId) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT b FROM Building b WHERE b.employee.id = :employeeId";
            Query<Building> query = session.createQuery(hql, Building.class);
            query.setParameter("employeeId", employeeId);
            return query.list();
        } catch (Exception e) {
            logger.error("Error getting buildings for employee", e);
            return new ArrayList<>();
        }
    }
}
