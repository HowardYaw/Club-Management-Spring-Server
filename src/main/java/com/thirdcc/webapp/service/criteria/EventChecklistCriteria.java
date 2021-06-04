package com.thirdcc.webapp.service.criteria;

import com.thirdcc.webapp.domain.enumeration.EventChecklistStatus;
import com.thirdcc.webapp.domain.enumeration.EventChecklistType;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.*;

import java.io.Serializable;
import java.util.Objects;

public class EventChecklistCriteria implements Serializable, Criteria {

    public static class EventChecklistStatusFilter extends Filter<EventChecklistStatus> {

        public EventChecklistStatusFilter() {
        }

        public EventChecklistStatusFilter(EventChecklistStatusFilter filter) {
            super(filter);
        }

        @Override
        public EventChecklistStatusFilter copy() {
            return new EventChecklistStatusFilter(this);
        }
    }

    public static class EventChecklistTypeFilter extends Filter<EventChecklistType> {

        public EventChecklistTypeFilter() {
        }

        public EventChecklistTypeFilter(EventChecklistTypeFilter filter) {
            super(filter);
        }

        @Override
        public EventChecklistTypeFilter copy() {
            return new EventChecklistTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter eventId;

    private StringFilter name;

    private EventChecklistStatusFilter status;

    private EventChecklistTypeFilter type;

    public EventChecklistCriteria() {
    }

    public EventChecklistCriteria(EventChecklistCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.eventId = other.eventId == null ? null : other.eventId.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.type = other.type == null ? null : other.type.copy();
    }

    @Override
    public EventChecklistCriteria copy() {
        return new EventChecklistCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getEventId() {
        return eventId;
    }

    public LongFilter eventId() {
        if (eventId == null) {
            eventId = new LongFilter();
        }
        return eventId;
    }

    public void setEventId(LongFilter eventId) {
        this.eventId = eventId;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public EventChecklistStatusFilter getStatus() {
        return status;
    }

    public EventChecklistStatusFilter status() {
        if (status == null) {
            status = new EventChecklistStatusFilter();
        }
        return status;
    }

    public void setStatus(EventChecklistStatusFilter status) {
        this.status = status;
    }

    public EventChecklistTypeFilter getType() {
        return type;
    }

    public EventChecklistTypeFilter type() {
        if (type == null) {
            type = new EventChecklistTypeFilter();
        }
        return type;
    }

    public void setType(EventChecklistTypeFilter type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventChecklistCriteria that = (EventChecklistCriteria) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(status, that.status) &&
            Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, name, status, type);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventChecklistCriteria{" +
            "id=" + id +
            ", eventId=" + eventId +
            ", name=" + name +
            ", status=" + status +
            ", type=" + type +
            '}';
    }
}
