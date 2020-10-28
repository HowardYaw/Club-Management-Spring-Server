package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

import com.thirdcc.webapp.domain.enumeration.AdministratorRole;

import com.thirdcc.webapp.domain.enumeration.AdministratorStatus;

/**
 * A Administrator.
 */
@Entity
@Table(name = "administrator")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Administrator implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "year_session")
    private String yearSession;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private AdministratorRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AdministratorStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public Administrator userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getYearSession() {
        return yearSession;
    }

    public Administrator yearSession(String yearSession) {
        this.yearSession = yearSession;
        return this;
    }

    public void setYearSession(String yearSession) {
        this.yearSession = yearSession;
    }

    public AdministratorRole getRole() {
        return role;
    }

    public Administrator role(AdministratorRole role) {
        this.role = role;
        return this;
    }

    public void setRole(AdministratorRole role) {
        this.role = role;
    }

    public AdministratorStatus getStatus() {
        return status;
    }

    public Administrator status(AdministratorStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(AdministratorStatus status) {
        this.status = status;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Administrator)) {
            return false;
        }
        return id != null && id.equals(((Administrator) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Administrator{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", yearSession='" + getYearSession() + "'" +
            ", role='" + getRole() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
