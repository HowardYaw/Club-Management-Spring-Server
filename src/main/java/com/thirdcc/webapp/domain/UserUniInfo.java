package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

import com.thirdcc.webapp.domain.enumeration.UserUniStatus;

/**
 * A UserUniInfo.
 */
@Entity
@Table(name = "user_uni_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserUniInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "program")
    private String program;

    @Column(name = "year_session")
    private String yearSession;

    @Column(name = "intake_semester")
    private Integer intakeSemester;

    @Column(name = "year_of_study", precision = 21, scale = 2)
    private BigDecimal yearOfStudy;

    @Column(name = "stay_in")
    private String stayIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserUniStatus status;

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

    public UserUniInfo userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFaculty() {
        return faculty;
    }

    public UserUniInfo faculty(String faculty) {
        this.faculty = faculty;
        return this;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getProgram() {
        return program;
    }

    public UserUniInfo program(String program) {
        this.program = program;
        return this;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getYearSession() {
        return yearSession;
    }

    public UserUniInfo yearSession(String yearSession) {
        this.yearSession = yearSession;
        return this;
    }

    public void setYearSession(String yearSession) {
        this.yearSession = yearSession;
    }

    public Integer getIntakeSemester() {
        return intakeSemester;
    }

    public UserUniInfo intakeSemester(Integer intakeSemester) {
        this.intakeSemester = intakeSemester;
        return this;
    }

    public void setIntakeSemester(Integer intakeSemester) {
        this.intakeSemester = intakeSemester;
    }

    public BigDecimal getYearOfStudy() {
        return yearOfStudy;
    }

    public UserUniInfo yearOfStudy(BigDecimal yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
        return this;
    }

    public void setYearOfStudy(BigDecimal yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getStayIn() {
        return stayIn;
    }

    public UserUniInfo stayIn(String stayIn) {
        this.stayIn = stayIn;
        return this;
    }

    public void setStayIn(String stayIn) {
        this.stayIn = stayIn;
    }

    public UserUniStatus getStatus() {
        return status;
    }

    public UserUniInfo status(UserUniStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(UserUniStatus status) {
        this.status = status;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserUniInfo)) {
            return false;
        }
        return id != null && id.equals(((UserUniInfo) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "UserUniInfo{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", faculty='" + getFaculty() + "'" +
            ", program='" + getProgram() + "'" +
            ", yearSession='" + getYearSession() + "'" +
            ", intakeSemester=" + getIntakeSemester() +
            ", yearOfStudy=" + getYearOfStudy() +
            ", stayIn='" + getStayIn() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
