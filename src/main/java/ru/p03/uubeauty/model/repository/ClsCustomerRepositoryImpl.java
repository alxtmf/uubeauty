/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.p03.uubeauty.model.repository;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ru.p03.uubeauty.model.RegCustomerContact;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import ru.p03.uubeauty.model.ClsCustomer;
import ru.p03.uubeauty.model.repository.exceptions.IllegalOrphanException;
import ru.p03.uubeauty.model.repository.exceptions.NonexistentEntityException;

/**
 *
 * @author timofeevan
 */
public class ClsCustomerRepositoryImpl implements Serializable {

    public ClsCustomerRepositoryImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ClsCustomer clsCustomer) {
        if (clsCustomer.getRegCustomerContactCollection() == null) {
            clsCustomer.setRegCustomerContactCollection(new ArrayList<RegCustomerContact>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<RegCustomerContact> attachedRegCustomerContactCollection = new ArrayList<RegCustomerContact>();
            for (RegCustomerContact regCustomerContactCollectionRegCustomerContactToAttach : clsCustomer.getRegCustomerContactCollection()) {
                regCustomerContactCollectionRegCustomerContactToAttach = em.getReference(regCustomerContactCollectionRegCustomerContactToAttach.getClass(), regCustomerContactCollectionRegCustomerContactToAttach.getId());
                attachedRegCustomerContactCollection.add(regCustomerContactCollectionRegCustomerContactToAttach);
            }
            clsCustomer.setRegCustomerContactCollection(attachedRegCustomerContactCollection);
            em.persist(clsCustomer);
            for (RegCustomerContact regCustomerContactCollectionRegCustomerContact : clsCustomer.getRegCustomerContactCollection()) {
                ClsCustomer oldIdCustomerOfRegCustomerContactCollectionRegCustomerContact = regCustomerContactCollectionRegCustomerContact.getIdCustomer();
                regCustomerContactCollectionRegCustomerContact.setIdCustomer(clsCustomer);
                regCustomerContactCollectionRegCustomerContact = em.merge(regCustomerContactCollectionRegCustomerContact);
                if (oldIdCustomerOfRegCustomerContactCollectionRegCustomerContact != null) {
                    oldIdCustomerOfRegCustomerContactCollectionRegCustomerContact.getRegCustomerContactCollection().remove(regCustomerContactCollectionRegCustomerContact);
                    oldIdCustomerOfRegCustomerContactCollectionRegCustomerContact = em.merge(oldIdCustomerOfRegCustomerContactCollectionRegCustomerContact);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ClsCustomer clsCustomer) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ClsCustomer persistentClsCustomer = em.find(ClsCustomer.class, clsCustomer.getId());
            Collection<RegCustomerContact> regCustomerContactCollectionOld = persistentClsCustomer.getRegCustomerContactCollection();
            Collection<RegCustomerContact> regCustomerContactCollectionNew = clsCustomer.getRegCustomerContactCollection();
            List<String> illegalOrphanMessages = null;
            for (RegCustomerContact regCustomerContactCollectionOldRegCustomerContact : regCustomerContactCollectionOld) {
                if (!regCustomerContactCollectionNew.contains(regCustomerContactCollectionOldRegCustomerContact)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RegCustomerContact " + regCustomerContactCollectionOldRegCustomerContact + " since its idCustomer field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<RegCustomerContact> attachedRegCustomerContactCollectionNew = new ArrayList<RegCustomerContact>();
            for (RegCustomerContact regCustomerContactCollectionNewRegCustomerContactToAttach : regCustomerContactCollectionNew) {
                regCustomerContactCollectionNewRegCustomerContactToAttach = em.getReference(regCustomerContactCollectionNewRegCustomerContactToAttach.getClass(), regCustomerContactCollectionNewRegCustomerContactToAttach.getId());
                attachedRegCustomerContactCollectionNew.add(regCustomerContactCollectionNewRegCustomerContactToAttach);
            }
            regCustomerContactCollectionNew = attachedRegCustomerContactCollectionNew;
            clsCustomer.setRegCustomerContactCollection(regCustomerContactCollectionNew);
            clsCustomer = em.merge(clsCustomer);
            for (RegCustomerContact regCustomerContactCollectionNewRegCustomerContact : regCustomerContactCollectionNew) {
                if (!regCustomerContactCollectionOld.contains(regCustomerContactCollectionNewRegCustomerContact)) {
                    ClsCustomer oldIdCustomerOfRegCustomerContactCollectionNewRegCustomerContact = regCustomerContactCollectionNewRegCustomerContact.getIdCustomer();
                    regCustomerContactCollectionNewRegCustomerContact.setIdCustomer(clsCustomer);
                    regCustomerContactCollectionNewRegCustomerContact = em.merge(regCustomerContactCollectionNewRegCustomerContact);
                    if (oldIdCustomerOfRegCustomerContactCollectionNewRegCustomerContact != null && !oldIdCustomerOfRegCustomerContactCollectionNewRegCustomerContact.equals(clsCustomer)) {
                        oldIdCustomerOfRegCustomerContactCollectionNewRegCustomerContact.getRegCustomerContactCollection().remove(regCustomerContactCollectionNewRegCustomerContact);
                        oldIdCustomerOfRegCustomerContactCollectionNewRegCustomerContact = em.merge(oldIdCustomerOfRegCustomerContactCollectionNewRegCustomerContact);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = clsCustomer.getId();
                if (findClsCustomer(id) == null) {
                    throw new NonexistentEntityException("The clsCustomer with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ClsCustomer clsCustomer;
            try {
                clsCustomer = em.getReference(ClsCustomer.class, id);
                clsCustomer.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The clsCustomer with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<RegCustomerContact> regCustomerContactCollectionOrphanCheck = clsCustomer.getRegCustomerContactCollection();
            for (RegCustomerContact regCustomerContactCollectionOrphanCheckRegCustomerContact : regCustomerContactCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ClsCustomer (" + clsCustomer + ") cannot be destroyed since the RegCustomerContact " + regCustomerContactCollectionOrphanCheckRegCustomerContact + " in its regCustomerContactCollection field has a non-nullable idCustomer field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(clsCustomer);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ClsCustomer> findClsCustomerEntities() {
        return findClsCustomerEntities(true, -1, -1);
    }

    public List<ClsCustomer> findClsCustomerEntities(int maxResults, int firstResult) {
        return findClsCustomerEntities(false, maxResults, firstResult);
    }

    private List<ClsCustomer> findClsCustomerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ClsCustomer.class));
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

    public ClsCustomer findClsCustomer(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ClsCustomer.class, id);
        } finally {
            em.close();
        }
    }

    public int getClsCustomerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ClsCustomer> rt = cq.from(ClsCustomer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
