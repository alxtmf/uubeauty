/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.p03.uubeauty.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import ru.p03.classifier.model.Classifier;

/**
 *
 * @author timofeevan
 */
@Entity
@Table(name = "CLS_CUSTOMER", catalog = "BEA", schema = "BEA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ClsCustomer.findAll", query = "SELECT c FROM ClsCustomer c")})
public class ClsCustomer extends Classifier implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "IS_DELETED")
    private Integer isDeleted;
    @Size(max = 255)
    @Column(name = "FAM")
    private String fam;
    @Size(max = 255)
    @Column(name = "IM")
    private String im;
    @Size(max = 255)
    @Column(name = "OTC")
    private String otc;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCustomer")
    private Collection<RegCustomerContact> regCustomerContactCollection;

    public ClsCustomer() {
    }

    public ClsCustomer(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getFam() {
        return fam;
    }

    public void setFam(String fam) {
        this.fam = fam;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getOtc() {
        return otc;
    }

    public void setOtc(String otc) {
        this.otc = otc;
    }

    @XmlTransient
    public Collection<RegCustomerContact> getRegCustomerContactCollection() {
        return regCustomerContactCollection;
    }

    public void setRegCustomerContactCollection(Collection<RegCustomerContact> regCustomerContactCollection) {
        this.regCustomerContactCollection = regCustomerContactCollection;
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
        if (!(object instanceof ClsCustomer)) {
            return false;
        }
        ClsCustomer other = (ClsCustomer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.p03.uubeauty.model.ClsCustomer[ id=" + id + " ]";
    }

}
