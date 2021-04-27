package com.thirdcc.webapp.service.dto;
import java.time.Instant;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.EventRegistrationClosingCriteria} entity.
 */
public class EventRegistrationClosingCriteriaDTO implements Serializable {

    private Long id;

    private Long eventId;

    private Integer maxAttendees;

    private Instant closingDate;

    private Boolean forceClose;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Instant getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Instant closingDate) {
        this.closingDate = closingDate;
    }

    public Boolean isForceClose() {
        return forceClose;
    }

    public void setForceClose(Boolean forceClose) {
        this.forceClose = forceClose;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventRegistrationClosingCriteriaDTO eventRegistrationClosingCriteriaDTO = (EventRegistrationClosingCriteriaDTO) o;
        if (eventRegistrationClosingCriteriaDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), eventRegistrationClosingCriteriaDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EventRegistrationClosingCriteriaDTO{" +
            "id=" + getId() +
            ", eventId=" + getEventId() +
            ", maxAttendees=" + getMaxAttendees() +
            ", closingDate='" + getClosingDate() + "'" +
            ", forceClose='" + isForceClose() + "'" +
            "}";
    }
}
