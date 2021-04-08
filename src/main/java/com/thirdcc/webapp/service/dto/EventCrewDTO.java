package com.thirdcc.webapp.service.dto;
import java.io.Serializable;
import java.util.Objects;
import com.thirdcc.webapp.domain.enumeration.EventCrewRole;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.EventCrew} entity.
 */
public class EventCrewDTO implements Serializable {

    private Long id;

    private Long userId;

    private Long eventId;

    private EventCrewRole role;

    private String userName;

    private String eventName;

    private String contactNumber;

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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public EventCrewRole getRole() {
        return role;
    }

    public void setRole(EventCrewRole role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventCrewDTO eventCrewDTO = (EventCrewDTO) o;
        if (eventCrewDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), eventCrewDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EventCrewDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", eventId=" + getEventId() +
            ", role='" + getRole() + "'" +
            "}";
    }
}
