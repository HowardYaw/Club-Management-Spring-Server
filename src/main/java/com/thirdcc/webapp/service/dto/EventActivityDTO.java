package com.thirdcc.webapp.service.dto;
import java.time.Instant;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.EventActivity} entity.
 */
public class EventActivityDTO implements Serializable {

    private Long id;

    private Long eventId;

    private Instant startDate;

    private BigDecimal durationInDay;

    private String name;

    @Lob
    private String description;


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

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getDurationInDay() {
        return durationInDay;
    }

    public void setDurationInDay(BigDecimal durationInDay) {
        this.durationInDay = durationInDay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventActivityDTO eventActivityDTO = (EventActivityDTO) o;
        if (eventActivityDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), eventActivityDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EventActivityDTO{" +
            "id=" + getId() +
            ", eventId=" + getEventId() +
            ", startDate='" + getStartDate() + "'" +
            ", durationInDay=" + getDurationInDay() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
