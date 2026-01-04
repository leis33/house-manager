package nbu.dbProject.dao;

import nbu.dbProject.configuration.SessionFactoryUtil;
import nbu.dbProject.entity.*;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CompanyDao {
    public static void createCompany(Company company) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(company);
            transaction.commit();
        }
    }

    public static Company getCompany(long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return session.find(Company.class, id);
        }
    }

    public static List<Company> getCompanies() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT c FROM Company c", Company.class).getResultList();
        }
    }

    public static void deleteCompany(long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Company company = session.find(Company.class, id);
            session.remove(company);
            transaction.commit();
        }
    }
}
