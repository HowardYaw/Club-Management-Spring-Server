package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

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

    @Column(name = "course_program_id")
    private Long courseProgramId;

    @Column(name = "year_session")
    private String yearSession;

    @Column(name = "intake_semester")
    private Integer intakeSemester;

    @Column(name = "stay_in")
    private String stayIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserUniStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseProgramId() {
        return courseProgramId;
    }

    public void setCourseProgramId(Long courseProgramId) {
        this.courseProgramId = courseProgramId;
    }

    public String getYearSession() {
        return yearSession;
    }

    public void setYearSession(String yearSession) {
        this.yearSession = yearSession;
    }

    public Integer getIntakeSemester() {
        return intakeSemester;
    }

    public void setIntakeSemester(Integer intakeSemester) {
        this.intakeSemester = intakeSemester;
    }

    public String getStayIn() {
        return stayIn;
    }

    public void setStayIn(String stayIn) {
        this.stayIn = stayIn;
    }

    public UserUniStatus getStatus() {
        return status;
    }

    public void setStatus(UserUniStatus status) {
        this.status = status;
    }

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
            "id=" + id +
            ", userId=" + userId +
            ", courseProgramId=" + courseProgramId +
            ", yearSession='" + yearSession + '\'' +
            ", intakeSemester=" + intakeSemester +
            ", stayIn='" + stayIn + '\'' +
            ", status=" + status +
            '}';
    }
}
