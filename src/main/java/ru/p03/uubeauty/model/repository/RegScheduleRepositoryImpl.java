/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.p03.uubeauty.model.repository;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.util.converter.LocalDateTimeStringConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ru.p03.common.util.QueriesEngine;
import ru.p03.uubeauty.model.ClsCustomer;
import ru.p03.uubeauty.model.ClsEmployee;
import ru.p03.uubeauty.model.RegSchedule;
import ru.p03.uubeauty.model.repository.exceptions.NonexistentEntityException;

/**
 *
 * @author timofeevan
 */
public class RegScheduleRepositoryImpl implements Serializable {

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

    public void create(RegSchedule regSchedule) {
        EntityManager em = null;
        try {
            em = getDAO().getEntityManager();
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
        
        if (regSchedule.getId() == null){
            create(regSchedule);
            return;
        }
        
        EntityManager em = null;
        try {
            em = getDAO().getEntityManager();
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
            em = getDAO().getEntityManager();
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
        EntityManager em = getDAO().getEntityManager();
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
    
    public List<RegSchedule> findFromCustomer(ClsCustomer customer) {
        String text = " SELECT c FROM RegSchedule " 
                + " c  WHERE c.isDeleted = 0 AND c.idCustomer = :idCustomer";
        List<RegSchedule> list = DAO.getListTextQuery(RegSchedule.class, text, 
                DAO.pair("idCustomer", customer.getId()));
        return list;
    }

    public RegSchedule findRegSchedule(Long id) {
        EntityManager em = getDAO().getEntityManager();
        try {
            return em.find(RegSchedule.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegScheduleCount() {
        EntityManager em = getDAO().getEntityManager();
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
    
    public void createOrder(Long employeeId, Long CustomerId, Long ServiceId, LocalDateTime dateTime){
        
    }
    
    public List<LocalDateTime> getReserved (ClsEmployee employee, LocalDate ld){
        String text = " SELECT c FROM RegSchedule " 
                + " c  WHERE c.isDeleted = 0 AND c.idEmployee = :idEmployee"
                + " AND YEAR(c.dateReg) = YEAR(:date)"
                + " AND MONTH(c.dateReg) = MONTH(:date)"
                + " AND DAY(c.dateReg) = DAY(:date)";
        List<RegSchedule> busy = DAO.getListTextQuery(ClsCustomer.class, text, 
                DAO.pair("idEmployee", employee.getId()),
                DAO.pair("date", employee.getId()));
        return busy.stream().map((RegSchedule t) -> {
            LocalDateTime ldt = LocalDateTime.ofInstant(t.getDateTimeServiceBegin().toInstant(), ZoneId.systemDefault());
            return ldt;
        }).collect(Collectors.toList());
    }
    
    public boolean isFree (ClsEmployee employee, LocalDateTime ldt){
        String text = " SELECT c FROM RegSchedule " 
                + " c  WHERE c.isDeleted = 0 AND c.idEmployee = :idEmployee"
                + " AND c.dateTimeServiceBegin <= :date"
                + " AND c.dateTimeServiceEnd >= :date";
        List<RegSchedule> busy = DAO.getListTextQuery(ClsCustomer.class, text, 
                DAO.pair("idEmployee", employee.getId()),
                DAO.pair("date", Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())));
        return busy.isEmpty();
    }
}
