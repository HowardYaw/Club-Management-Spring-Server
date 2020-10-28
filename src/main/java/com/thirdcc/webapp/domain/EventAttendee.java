package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A EventAttendee.
 */
@Entity
@Table(name = "event_attendee")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventAttendee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "even_id")
    private Long evenId;

    @Column(name = "provide_transport")
    private Boolean provideTransport;

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

    public EventAttendee userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEvenId() {
        return evenId;
    }

    public EventAttendee evenId(Long evenId) {
        this.evenId = evenId;
        return this;
    }

    public void setEvenId(Long evenId) {
        this.evenId = evenId;
    }

    public Boolean isProvideTransport() {
        return provideTransport;
    }

    public EventAttendee provideTransport(Boolean provideTransport) {
        this.provideTransport = provideTransport;
        return this;
    }

    public void setProvideTransport(Boolean provideTransport) {
        this.provideTransport = provideTransport;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventAttendee)) {
            return false;
        }
        return id != null && id.equals(((EventAttendee) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "EventAttendee{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", evenId=" + getEvenId() +
            ", provideTransport='" + isProvideTransport() + "'" +
            "}";
    }
}
