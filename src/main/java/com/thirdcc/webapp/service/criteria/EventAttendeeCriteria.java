package com.thirdcc.webapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.thirdcc.webapp.domain.EventAttendee} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.EventAttendeeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /event-attendees?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EventAttendeeCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private LongFilter userId;

  private LongFilter eventId;

  private BooleanFilter provideTransport;

  public EventAttendeeCriteria() {}

  public EventAttendeeCriteria(EventAttendeeCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.userId = other.userId == null ? null : other.userId.copy();
    this.eventId = other.eventId == null ? null : other.eventId.copy();
    this.provideTransport = other.provideTransport == null ? null : other.provideTransport.copy();
  }

  @Override
  public EventAttendeeCriteria copy() {
    return new EventAttendeeCriteria(this);
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

  public LongFilter getUserId() {
    return userId;
  }

  public LongFilter userId() {
    if (userId == null) {
      userId = new LongFilter();
    }
    return userId;
  }

  public void setUserId(LongFilter userId) {
    this.userId = userId;
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

  public BooleanFilter getProvideTransport() {
    return provideTransport;
  }

  public BooleanFilter provideTransport() {
    if (provideTransport == null) {
      provideTransport = new BooleanFilter();
    }
    return provideTransport;
  }

  public void setProvideTransport(BooleanFilter provideTransport) {
    this.provideTransport = provideTransport;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final EventAttendeeCriteria that = (EventAttendeeCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(userId, that.userId) &&
      Objects.equals(eventId, that.eventId) &&
      Objects.equals(provideTransport, that.provideTransport)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, eventId, provideTransport);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "EventAttendeeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (provideTransport != null ? "provideTransport=" + provideTransport + ", " : "") +
            "}";
    }
}
