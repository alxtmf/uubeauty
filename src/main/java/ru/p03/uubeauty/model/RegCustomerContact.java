/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.p03.uubeauty.model;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author timofeevan
 */
@Entity
@Table(name = "REG_CUSTOMER_CONTACT", catalog = "BEA", schema = "BEA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegCustomerContact.findAll", query = "SELECT r FROM RegCustomerContact r")})
public class RegCustomerContact implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ID_CONTACT_TYPE")
    private BigInteger idContactType;
    @Column(name = "IS_DELETED")
    private Integer isDeleted;
    @Size(max = 255)
    @Column(name = "TEXTVALUE")
    private String textvalue;
    @JoinColumn(name = "ID_CUSTOMER", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private ClsCustomer idCustomer;

    public RegCustomerContact() {
    }

    public RegCustomerContact(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getIdContactType() {
        return idContactType;
    }

    public void setIdContactType(BigInteger idContactType) {
        this.idContactType = idContactType;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getTextvalue() {
        return textvalue;
    }

    public void setTextvalue(String textvalue) {
        this.textvalue = textvalue;
    }

    public ClsCustomer getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(ClsCustomer idCustomer) {
        this.idCustomer = idCustomer;
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
        if (!(object instanceof RegCustomerContact)) {
            return false;
        }
        RegCustomerContact other = (RegCustomerContact) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.p03.uubeauty.model.RegCustomerContact[ id=" + id + " ]";
    }

}
