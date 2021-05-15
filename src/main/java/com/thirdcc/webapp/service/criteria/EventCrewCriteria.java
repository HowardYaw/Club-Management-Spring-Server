package com.thirdcc.webapp.service.criteria;

import com.thirdcc.webapp.domain.enumeration.EventCrewRole;
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
 * Criteria class for the {@link com.thirdcc.webapp.domain.EventCrew} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.EventCrewResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /event-crews?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EventCrewCriteria implements Serializable, Criteria {

  /**
   * Class for filtering EventCrewRole
   */
  public static class EventCrewRoleFilter extends Filter<EventCrewRole> {

    public EventCrewRoleFilter() {}

    public EventCrewRoleFilter(EventCrewRoleFilter filter) {
      super(filter);
    }

    @Override
    public EventCrewRoleFilter copy() {
      return new EventCrewRoleFilter(this);
    }
  }

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private LongFilter userId;

  private LongFilter eventId;

  private EventCrewRoleFilter role;

  public EventCrewCriteria() {}

  public EventCrewCriteria(EventCrewCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.userId = other.userId == null ? null : other.userId.copy();
    this.eventId = other.eventId == null ? null : other.eventId.copy();
    this.role = other.role == null ? null : other.role.copy();
  }

  @Override
  public EventCrewCriteria copy() {
    return new EventCrewCriteria(this);
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

  public EventCrewRoleFilter getRole() {
    return role;
  }

  public EventCrewRoleFilter role() {
    if (role == null) {
      role = new EventCrewRoleFilter();
    }
    return role;
  }

  public void setRole(EventCrewRoleFilter role) {
    this.role = role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final EventCrewCriteria that = (EventCrewCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(userId, that.userId) &&
      Objects.equals(eventId, that.eventId) &&
      Objects.equals(role, that.role)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, eventId, role);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "EventCrewCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (role != null ? "role=" + role + ", " : "") +
            "}";
    }
}
