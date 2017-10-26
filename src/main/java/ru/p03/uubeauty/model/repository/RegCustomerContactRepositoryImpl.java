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
import ru.p03.uubeauty.model.ClsCustomer;
import ru.p03.uubeauty.model.RegCustomerContact;
import ru.p03.uubeauty.model.repository.exceptions.NonexistentEntityException;

/**
 *
 * @author timofeevan
 */
public class RegCustomerContactRepositoryImpl implements Serializable {

    public RegCustomerContactRepositoryImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RegCustomerContact regCustomerContact) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ClsCustomer idCustomer = regCustomerContact.getIdCustomer();
            if (idCustomer != null) {
                idCustomer = em.getReference(idCustomer.getClass(), idCustomer.getId());
                regCustomerContact.setIdCustomer(idCustomer);
            }
            em.persist(regCustomerContact);
            if (idCustomer != null) {
                idCustomer.getRegCustomerContactCollection().add(regCustomerContact);
                idCustomer = em.merge(idCustomer);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RegCustomerContact regCustomerContact) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RegCustomerContact persistentRegCustomerContact = em.find(RegCustomerContact.class, regCustomerContact.getId());
            ClsCustomer idCustomerOld = persistentRegCustomerContact.getIdCustomer();
            ClsCustomer idCustomerNew = regCustomerContact.getIdCustomer();
            if (idCustomerNew != null) {
                idCustomerNew = em.getReference(idCustomerNew.getClass(), idCustomerNew.getId());
                regCustomerContact.setIdCustomer(idCustomerNew);
            }
            regCustomerContact = em.merge(regCustomerContact);
            if (idCustomerOld != null && !idCustomerOld.equals(idCustomerNew)) {
                idCustomerOld.getRegCustomerContactCollection().remove(regCustomerContact);
                idCustomerOld = em.merge(idCustomerOld);
            }
            if (idCustomerNew != null && !idCustomerNew.equals(idCustomerOld)) {
                idCustomerNew.getRegCustomerContactCollection().add(regCustomerContact);
                idCustomerNew = em.merge(idCustomerNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = regCustomerContact.getId();
                if (findRegCustomerContact(id) == null) {
                    throw new NonexistentEntityException("The regCustomerContact with id " + id + " no longer exists.");
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
            RegCustomerContact regCustomerContact;
            try {
                regCustomerContact = em.getReference(RegCustomerContact.class, id);
                regCustomerContact.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The regCustomerContact with id " + id + " no longer exists.", enfe);
            }
            ClsCustomer idCustomer = regCustomerContact.getIdCustomer();
            if (idCustomer != null) {
                idCustomer.getRegCustomerContactCollection().remove(regCustomerContact);
                idCustomer = em.merge(idCustomer);
            }
            em.remove(regCustomerContact);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RegCustomerContact> findRegCustomerContactEntities() {
        return findRegCustomerContactEntities(true, -1, -1);
    }

    public List<RegCustomerContact> findRegCustomerContactEntities(int maxResults, int firstResult) {
        return findRegCustomerContactEntities(false, maxResults, firstResult);
    }

    private List<RegCustomerContact> findRegCustomerContactEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RegCustomerContact.class));
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

    public RegCustomerContact findRegCustomerContact(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RegCustomerContact.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegCustomerContactCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RegCustomerContact> rt = cq.from(RegCustomerContact.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
