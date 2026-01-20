package nbu.dbProject.dao;

import nbu.dbProject.configuration.SessionFactoryUtil;
import nbu.dbProject.entity.Owner;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OwnerRepository {

    public Owner save(Owner owner) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(owner);
            transaction.commit();
            return owner;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error saving owner", e);
        }
    }

    public Owner update(Owner owner) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Owner updated = session.merge(owner);
            transaction.commit();
            return updated;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error updating owner", e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Owner owner = session.get(Owner.class, id);
            if (owner != null) session.remove(owner);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Error deleting owner", e);
        }
    }

    public Optional<Owner> findById(Long id) {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "SELECT o FROM Owner o " +
                    "LEFT JOIN FETCH o.apartments " +
                    "WHERE o.id = :id";
            Query<Owner> query = session.createQuery(hql, Owner.class);
            query.setParameter("id", id);
            return query.uniqueResultOptional();
        }
    }

    public List<Owner> findAll() {
        try (Session session = SessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Owner", Owner.class).list();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
