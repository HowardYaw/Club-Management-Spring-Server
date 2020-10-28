package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

import com.thirdcc.webapp.domain.enumeration.ChecklistStatus;

import com.thirdcc.webapp.domain.enumeration.ChecklistType;

/**
 * A Checklist.
 */
@Entity
@Table(name = "checklist")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Checklist implements Serializable {

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
    private ChecklistStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ChecklistType type;

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

    public Checklist eventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public Checklist name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Checklist description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ChecklistStatus getStatus() {
        return status;
    }

    public Checklist status(ChecklistStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ChecklistStatus status) {
        this.status = status;
    }

    public ChecklistType getType() {
        return type;
    }

    public Checklist type(ChecklistType type) {
        this.type = type;
        return this;
    }

    public void setType(ChecklistType type) {
        this.type = type;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Checklist)) {
            return false;
        }
        return id != null && id.equals(((Checklist) o).id);
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
