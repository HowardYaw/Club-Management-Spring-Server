package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

import com.thirdcc.webapp.domain.enumeration.EventCrewRole;

/**
 * A EventCrew.
 */
@Entity
@Table(name = "event_crew")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventCrew implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "event_id")
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private EventCrewRole role;

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

    public EventCrew userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public EventCrew eventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public EventCrewRole getRole() {
        return role;
    }

    public EventCrew role(EventCrewRole role) {
        this.role = role;
        return this;
    }

    public void setRole(EventCrewRole role) {
        this.role = role;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventCrew)) {
            return false;
        }
        return id != null && id.equals(((EventCrew) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "EventCrew{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", eventId=" + getEventId() +
            ", role='" + getRole() + "'" +
            "}";
    }
}
