package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A EventRegistrationClosingCriteria.
 */
@Entity
@Table(name = "event_registration_closing_criteria")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventRegistrationClosingCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "max_attendees")
    private Integer maxAttendees;

    @Column(name = "closing_date")
    private Instant closingDate;

    @Column(name = "force_close")
    private Boolean forceClose;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public EventRegistrationClosingCriteria eventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    public EventRegistrationClosingCriteria maxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
        return this;
    }

    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Instant getClosingDate() {
        return closingDate;
    }

    public EventRegistrationClosingCriteria closingDate(Instant closingDate) {
        this.closingDate = closingDate;
        return this;
    }

    public void setClosingDate(Instant closingDate) {
        this.closingDate = closingDate;
    }

    public Boolean isForceClose() {
        return forceClose;
    }

    public EventRegistrationClosingCriteria forceClose(Boolean forceClose) {
        this.forceClose = forceClose;
        return this;
    }

    public void setForceClose(Boolean forceClose) {
        this.forceClose = forceClose;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventRegistrationClosingCriteria)) {
            return false;
        }
        return id != null && id.equals(((EventRegistrationClosingCriteria) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "EventRegistrationClosingCriteria{" +
            "id=" + getId() +
            ", eventId=" + getEventId() +
            ", maxAttendees=" + getMaxAttendees() +
            ", closingDate='" + getClosingDate() + "'" +
            ", forceClose='" + isForceClose() + "'" +
            "}";
    }
}
