/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.p03.uubeauty.model.repository;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ru.p03.uubeauty.model.RegSchedule;
import ru.p03.uubeauty.model.repository.exceptions.NonexistentEntityException;

/**
 *
 * @author timofeevan
 */
public class RegScheduleRepositoryImpl implements Serializable {

    public RegScheduleRepositoryImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RegSchedule regSchedule) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(regSchedule);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RegSchedule regSchedule) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            regSchedule = em.merge(regSchedule);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = regSchedule.getId();
                if (findRegSchedule(id) == null) {
                    throw new NonexistentEntityException("The regSchedule with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RegSchedule regSchedule;
            try {
                regSchedule = em.getReference(RegSchedule.class, id);
                regSchedule.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The regSchedule with id " + id + " no longer exists.", enfe);
            }
            em.remove(regSchedule);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RegSchedule> findRegScheduleEntities() {
        return findRegScheduleEntities(true, -1, -1);
    }

    public List<RegSchedule> findRegScheduleEntities(int maxResults, int firstResult) {
        return findRegScheduleEntities(false, maxResults, firstResult);
    }

    private List<RegSchedule> findRegScheduleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RegSchedule.class));
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

    public RegSchedule findRegSchedule(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RegSchedule.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegScheduleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RegSchedule> rt = cq.from(RegSchedule.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
