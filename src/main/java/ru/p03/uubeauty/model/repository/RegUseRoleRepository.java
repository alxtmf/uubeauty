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
import ru.p03.classifier.model.Classifier;
import ru.p03.common.util.QueriesEngine;
import ru.p03.uubeauty.model.ClsEmployee;
import ru.p03.uubeauty.model.RegUseRole;
import ru.p03.uubeauty.model.repository.exceptions.NonexistentEntityException;

/**
 *
 * @author timofeevan
 */
public class RegUseRoleRepository implements Serializable {

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

    public void create(RegUseRole regUseRole) {
        EntityManager em = null;
        try {
            em = getDAO().getEntityManager();
            em.getTransaction().begin();
            em.persist(regUseRole);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RegUseRole regUseRole) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getDAO().getEntityManager();
            em.getTransaction().begin();
            regUseRole = em.merge(regUseRole);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = regUseRole.getId();
                if (findRegUseRole(id) == null) {
                    throw new NonexistentEntityException("The regUseRole with id " + id + " no longer exists.");
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
            em = getDAO().getEntityManager();
            em.getTransaction().begin();
            RegUseRole regUseRole;
            try {
                regUseRole = em.getReference(RegUseRole.class, id);
                regUseRole.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The regUseRole with id " + id + " no longer exists.", enfe);
            }
            em.remove(regUseRole);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RegUseRole> findRegUseRoleEntities() {
        return findRegUseRoleEntities(true, -1, -1);
    }

    public List<RegUseRole> findRegUseRoleEntities(int maxResults, int firstResult) {
        return findRegUseRoleEntities(false, maxResults, firstResult);
    }

    private List<RegUseRole> findRegUseRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getDAO().getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RegUseRole.class));
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

    public RegUseRole findRegUseRole(Long id) {
        EntityManager em = getDAO().getEntityManager();
        try {
            return em.find(RegUseRole.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegUseRoleCount() {
        EntityManager em = getDAO().getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RegUseRole> rt = cq.from(RegUseRole.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public boolean hasRole(ClsEmployee employee, String roleCode){
        String text =  "SELECT * FROM(\n" +
                        "    SELECT RUR.*, R.CODE AS ROLE_CODE FROM(\n" +
                        "        SELECT * FROM BEA.REG_USE_ROLE \n" +
                        "        WHERE NAME IN(\n" +
                        "            SELECT LOGIN FROM BEA.CLS_USER AS U\n" +
                        "            WHERE U.ID_EMPLOYEE = ?\n" +
                        "        )\n" +
                        "    ) AS RUR\n" +
                        "    LEFT JOIN BEA.CLS_ROLE AS R ON RUR.ID_ROLE = R.ID\n" +
                        ") AS RES\n" +
                        "WHERE RES.ROLE_CODE = ?";
        List<Object> roles = getDAO().getListNativeQuery(text, 
                getDAO().pair(1, employee.getId()),
                getDAO().pair(2, roleCode));
        return ! roles.isEmpty();
    }

}
