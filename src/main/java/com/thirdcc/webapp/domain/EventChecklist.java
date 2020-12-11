package com.thirdcc.webapp.domain;
import com.thirdcc.webapp.domain.enumeration.EventChecklistStatus;
import com.thirdcc.webapp.domain.enumeration.EventChecklistType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A Checklist.
 */
@Entity
@Table(name = "event_checklist")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventChecklist implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventChecklistStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private EventChecklistType type;

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

    public EventChecklist eventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public EventChecklist name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public EventChecklist description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventChecklistStatus getStatus() {
        return status;
    }

    public EventChecklist status(EventChecklistStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(EventChecklistStatus status) {
        this.status = status;
    }

    public EventChecklistType getType() {
        return type;
    }

    public EventChecklist type(EventChecklistType type) {
        this.type = type;
        return this;
    }

    public void setType(EventChecklistType type) {
        this.type = type;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventChecklist)) {
            return false;
        }
        return id != null && id.equals(((EventChecklist) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Checklist{" +
            "id=" + getId() +
            ", eventId=" + getEventId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
