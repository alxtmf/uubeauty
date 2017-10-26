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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import ru.p03.classifier.model.Classifier;

/**
 *
 * @author timofeevan
 */
@Entity
@Table(name = "CLS_USER", catalog = "BEA", schema = "BEA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ClsUser.findAll", query = "SELECT c FROM ClsUser c")})
public class ClsUser extends Classifier implements Serializable  {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ID_EMPLOYEE")
    private BigInteger idEmployee;
    @Column(name = "IS_DELETED")
    private Integer isDeleted;
    @Size(max = 255)
    @Column(name = "LOGIN")
    private String login;
    @Lob
    @Column(name = "PASSWORD")
    private Serializable password;

    public ClsUser() {
    }

    public ClsUser(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(BigInteger idEmployee) {
        this.idEmployee = idEmployee;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Serializable getPassword() {
        return password;
    }

    public void setPassword(Serializable password) {
        this.password = password;
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
        if (!(object instanceof ClsUser)) {
            return false;
        }
        ClsUser other = (ClsUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.p03.uubeauty.model.ClsUser[ id=" + id + " ]";
    }

}
