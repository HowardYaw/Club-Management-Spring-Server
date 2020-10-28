package com.thirdcc.webapp.service.dto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import com.thirdcc.webapp.domain.enumeration.UserUniStatus;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.UserUniInfo} entity.
 */
public class UserUniInfoDTO implements Serializable {

    private Long id;

    private Long userId;

    private String faculty;

    private String program;

    private String yearSession;

    private Integer intakeSemester;

    private BigDecimal yearOfStudy;

    private String stayIn;

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

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
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

    public BigDecimal getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(BigDecimal yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserUniInfoDTO userUniInfoDTO = (UserUniInfoDTO) o;
        if (userUniInfoDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userUniInfoDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserUniInfoDTO{" +
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
