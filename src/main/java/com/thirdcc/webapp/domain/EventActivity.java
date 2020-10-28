package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A EventActivity.
 */
@Entity
@Table(name = "event_activity")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "duration_in_day", precision = 21, scale = 2)
    private BigDecimal durationInDay;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

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

    public EventActivity eventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public EventActivity startDate(Instant startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getDurationInDay() {
        return durationInDay;
    }

    public EventActivity durationInDay(BigDecimal durationInDay) {
        this.durationInDay = durationInDay;
        return this;
    }

    public void setDurationInDay(BigDecimal durationInDay) {
        this.durationInDay = durationInDay;
    }

    public String getName() {
        return name;
    }

    public EventActivity name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public EventActivity description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventActivity)) {
            return false;
        }
        return id != null && id.equals(((EventActivity) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "EventActivity{" +
            "id=" + getId() +
            ", eventId=" + getEventId() +
            ", startDate='" + getStartDate() + "'" +
            ", durationInDay=" + getDurationInDay() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
