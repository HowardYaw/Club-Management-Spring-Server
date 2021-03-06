package com.thirdcc.webapp.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.EventAttendee} entity.
 */
public class EventAttendeeDTO implements Serializable {

    private Long id;

    private Long userId;

    private Long eventId;

    private Boolean provideTransport;
    
    private String userName;
    
    private String yearSession;
    
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

    public Boolean isProvideTransport() {
        return provideTransport;
    }

    public void setProvideTransport(Boolean provideTransport) {
        this.provideTransport = provideTransport;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getYearSession() {
        return yearSession;
    }

    public void setYearSession(String yearSession) {
        this.yearSession = yearSession;
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

        EventAttendeeDTO eventAttendeeDTO = (EventAttendeeDTO) o;
        if (eventAttendeeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), eventAttendeeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EventAttendeeDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", eventId=" + getEventId() +
            ", provideTransport='" + isProvideTransport() + "'" +
            "}";
    }
}
