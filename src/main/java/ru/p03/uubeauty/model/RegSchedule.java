/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.p03.uubeauty.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author timofeevan
 */
@Entity
@Table(name = "REG_SCHEDULE", catalog = "BEA", schema = "BEA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegSchedule.findAll", query = "SELECT r FROM RegSchedule r")})
public class RegSchedule implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ID_CUSTOMER")
    private Long idCustomer;
    @Column(name = "ID_EMPLOYEE")
    private Long idEmployee;
    @Column(name = "ID_SERVICE")
    private Long idService;
    @Column(name = "IS_DELETED")
    private Integer isDeleted;
    @Column(name = "DATE_REG")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateReg;
    @Column(name = "DATE_TIME_SERVICE_BEGIN")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeServiceBegin;
    @Column(name = "DATE_TIME_SERVICE_END")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTimeServiceEnd;   
    @Column(name = "HOUR_BEGIN")
    private Integer hourBegin;
    @Column(name = "MINUTE_BEGIN")
    private Integer minuteBegin;
    @Column(name = "HOUR_END")
    private Integer hourEnd;
    @Column(name = "MINUTE_END")
    private Integer minuteEnd;

    public RegSchedule() {
    }

    public RegSchedule(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Long idCustomer) {
        this.idCustomer = idCustomer;
    }

    public Long getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(Long idEmployee) {
        this.idEmployee = idEmployee;
    }

    public Long getIdService() {
        return idService;
    }

    public void setIdService(Long idService) {
        this.idService = idService;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getDateReg() {
        return dateReg;
    }

    public void setDateReg(Date dateReg) {
        this.dateReg = dateReg;
    }

    public Date getDateTimeServiceBegin() {
        return dateTimeServiceBegin;
    }

    public void setDateTimeServiceBegin(Date dateTimeServiceBegin) {
        this.dateTimeServiceBegin = dateTimeServiceBegin;
    }

    public Date getDateTimeServiceEnd() {
        return dateTimeServiceEnd;
    }

    public void setDateTimeServiceEnd(Date dateTimeServiceEnd) {
        this.dateTimeServiceEnd = dateTimeServiceEnd;
    }
    
    /**
     * @return the hourBegin
     */
    public Integer getHourBegin() {
        return hourBegin;
    }

    /**
     * @param hourBegin the hourBegin to set
     */
    public void setHourBegin(Integer hourBegin) {
        this.hourBegin = hourBegin;
    }

    /**
     * @return the minuteBegin
     */
    public Integer getMinuteBegin() {
        return minuteBegin;
    }

    /**
     * @param minuteBegin the minuteBegin to set
     */
    public void setMinuteBegin(Integer minuteBegin) {
        this.minuteBegin = minuteBegin;
    }

    /**
     * @return the hourEnd
     */
    public Integer getHourEnd() {
        return hourEnd;
    }

    /**
     * @param hourEnd the hourEnd to set
     */
    public void setHourEnd(Integer hourEnd) {
        this.hourEnd = hourEnd;
    }

    /**
     * @return the minuteEnd
     */
    public Integer getMinuteEnd() {
        return minuteEnd;
    }

    /**
     * @param minuteEnd the minuteEnd to set
     */
    public void setMinuteEnd(Integer minuteEnd) {
        this.minuteEnd = minuteEnd;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RegSchedule)) {
            return false;
        }
        RegSchedule other = (RegSchedule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.p03.uubeauty.model.RegSchedule[ id=" + id + " ]";
    }

}
