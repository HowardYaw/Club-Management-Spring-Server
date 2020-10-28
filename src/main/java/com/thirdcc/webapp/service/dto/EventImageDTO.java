package com.thirdcc.webapp.service.dto;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.EventImage} entity.
 */
public class EventImageDTO implements Serializable {

    private Long id;

    private Long eventId;

    private Long imageStorageId;


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

    public Long getImageStorageId() {
        return imageStorageId;
    }

    public void setImageStorageId(Long imageStorageId) {
        this.imageStorageId = imageStorageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventImageDTO eventImageDTO = (EventImageDTO) o;
        if (eventImageDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), eventImageDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EventImageDTO{" +
            "id=" + getId() +
            ", eventId=" + getEventId() +
            ", imageStorageId=" + getImageStorageId() +
            "}";
    }
}
