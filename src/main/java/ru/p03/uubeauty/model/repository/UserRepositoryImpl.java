/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.p03.uubeauty.model.repository;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ru.p03.classifier.model.Classifier;
import ru.p03.common.util.QueriesEngine;
import ru.p03.uubeauty.model.repository.exceptions.NonexistentEntityException;
import ru.p03.uubeauty.model.ClsUser;

/**
 *
 * @author timofeevan
 */
public class UserRepositoryImpl implements Serializable, UserRepository {

    /**
     * @return the DAO
     */
    public QueriesEngine getDAO() {
        return DAO;
    }

    /**
     * @param DAO the DAO to set
     */
    public void setDAO(QueriesEngine DAO) {
        this.DAO = DAO;
    }

    private QueriesEngine DAO;

    @Override
    public<T extends Classifier> void delete(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getDAO().getEntityManager();
            em.getTransaction().begin();
            ClsUser cls;
            try {
                cls = em.getReference(ClsUser.class, id);
                cls.getId();
                cls.setIsDeleted(1);
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The clssifier" + ClsUser.class + " with id " + id + " no longer exists.", enfe);
            }
            em.merge(cls);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public <T extends Classifier> void create(T object) {
        EntityManager em = null;
        try {
            em = getDAO().getEntityManager();
            em.getTransaction().begin();
            em.persist(object);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public <T extends Classifier> void edit(T object) throws NonexistentEntityException, Exception {
        if (object.getId() == null){
            create(object);
            return;
        }
        EntityManager em = null;
        try {
            em = getDAO().getEntityManager(); 
            em.getTransaction().begin();
            object = em.merge(object);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = object.getId();
                if (em.find(object.getClass(), id) == null) {
                    throw new NonexistentEntityException("The " + object.getClass() + " with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    public List<ClsUser> find() {
        return findClsUserEntities(true, -1, -1);
    }

    public List<ClsUser> find(int maxResults, int firstResult) {
        return findClsUserEntities(false, maxResults, firstResult);
    }

    private List<ClsUser> findClsUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getDAO().getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ClsUser.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public ClsUser find(Long id) {
        EntityManager em = getDAO().getEntityManager();
        try {
            return em.find(ClsUser.class, id);
        } finally {
            em.close();
        }
    }

    public int getClsUserCount() {
        EntityManager em = getDAO().getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ClsUser> rt = cq.from(ClsUser.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    @Override
    public <T extends Classifier> List<T> find(boolean isDeleted) {
        String text = " SELECT c FROM " + ClsUser.class.getSimpleName() + " c  WHERE c.isDeleted = :isDeleted";
        List<T> list = DAO.<T>getListTextQuery(ClsUser.class, text, DAO.pair("isDeleted", isDeleted ? 1 : 0));
        return list;
    }
    
    public <T extends Classifier> T find(String login, boolean isDeleted) {
        String text = " SELECT c FROM ClsUser c  WHERE c.isDeleted = :isDeleted AND c.login = :name";
        T cu = DAO.single(DAO.<T>getListTextQuery(ClsUser.class, text, 
                DAO.pair("isDeleted", isDeleted ? 1 : 0),
                DAO.pair("name", login)));
        return cu;
    }

}
