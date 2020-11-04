package com.thirdcc.webapp.service.dto;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;

import com.thirdcc.webapp.domain.EventChecklist;
import com.thirdcc.webapp.domain.enumeration.EventChecklistStatus;
import com.thirdcc.webapp.domain.enumeration.EventChecklistType;

/**
 * A DTO for the {@link EventChecklist} entity.
 */
public class EventChecklistDTO implements Serializable {

    private Long id;

    private Long eventId;

    private String name;

    @Lob
    private String description;

    private EventChecklistStatus status;

    private EventChecklistType type;


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

    public EventChecklistStatus getStatus() {
        return status;
    }

    public void setStatus(EventChecklistStatus status) {
        this.status = status;
    }

    public EventChecklistType getType() {
        return type;
    }

    public void setType(EventChecklistType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventChecklistDTO checklistDTO = (EventChecklistDTO) o;
        if (checklistDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), checklistDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ChecklistDTO{" +
            "id=" + getId() +
            ", eventId=" + getEventId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
