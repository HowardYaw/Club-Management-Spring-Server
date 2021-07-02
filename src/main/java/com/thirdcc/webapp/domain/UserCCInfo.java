package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

import com.thirdcc.webapp.domain.enumeration.ClubFamilyRole;

/**
 * A UserCCInfo.
 */
@Entity
@Table(name = "user_cc_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserCCInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "club_family_id")
    private Long clubFamilyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "family_role")
    private ClubFamilyRole familyRole;

    @Column(name = "year_session")
    private String yearSession;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

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

    public UserCCInfo userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getClubFamilyId() {
        return clubFamilyId;
    }

    public UserCCInfo clubFamilyId(Long clubFamilyId) {
        this.clubFamilyId = clubFamilyId;
        return this;
    }

    public void setClubFamilyId(Long clubFamilyId) {
        this.clubFamilyId = clubFamilyId;
    }

    public ClubFamilyRole getFamilyRole() {
        return familyRole;
    }

    public UserCCInfo familyRole(ClubFamilyRole familyRole) {
        this.familyRole = familyRole;
        return this;
    }

    public void setFamilyRole(ClubFamilyRole familyRole) {
        this.familyRole = familyRole;
    }

    public String getYearSession() {
        return yearSession;
    }

    public UserCCInfo yearSession(String yearSession) {
        this.yearSession = yearSession;
        return this;
    }

    public void setYearSession(String yearSession) {
        this.yearSession = yearSession;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserCCInfo)) {
            return false;
        }
        return id != null && id.equals(((UserCCInfo) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "UserCCInfo{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", clubFamilyId=" + getClubFamilyId() +
            ", familyRole='" + getFamilyRole() + "'" +
            ", yearSession='" + getYearSession() + "'" +
            "}";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
