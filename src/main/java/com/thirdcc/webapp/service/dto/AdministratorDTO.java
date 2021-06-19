package com.thirdcc.webapp.service.dto;
import java.io.Serializable;
import java.util.Objects;
import com.thirdcc.webapp.domain.enumeration.AdministratorRole;
import com.thirdcc.webapp.domain.enumeration.AdministratorStatus;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.Administrator} entity.
 */
public class AdministratorDTO implements Serializable {

    private Long id;

    private Long userId;

    private String yearSession;

    private AdministratorRole role;

    private AdministratorStatus status;

    private String firstName;

    private String lastName;

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

    public String getYearSession() {
        return yearSession;
    }

    public void setYearSession(String yearSession) {
        this.yearSession = yearSession;
    }

    public AdministratorRole getRole() {
        return role;
    }

    public void setRole(AdministratorRole role) {
        this.role = role;
    }

    public AdministratorStatus getStatus() {
        return status;
    }

    public void setStatus(AdministratorStatus status) {
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

        AdministratorDTO administratorDTO = (AdministratorDTO) o;
        if (administratorDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), administratorDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AdministratorDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", yearSession='" + getYearSession() + "'" +
            ", role='" + getRole() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
