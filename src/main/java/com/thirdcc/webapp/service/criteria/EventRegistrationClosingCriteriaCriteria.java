package com.thirdcc.webapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.thirdcc.webapp.domain.EventRegistrationClosingCriteria} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.EventRegistrationClosingCriteriaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /event-registration-closing-criteria?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EventRegistrationClosingCriteriaCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private LongFilter eventId;

  private IntegerFilter maxAttendees;

  private InstantFilter closingDate;

  private BooleanFilter forceClose;

  public EventRegistrationClosingCriteriaCriteria() {}

  public EventRegistrationClosingCriteriaCriteria(EventRegistrationClosingCriteriaCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.eventId = other.eventId == null ? null : other.eventId.copy();
    this.maxAttendees = other.maxAttendees == null ? null : other.maxAttendees.copy();
    this.closingDate = other.closingDate == null ? null : other.closingDate.copy();
    this.forceClose = other.forceClose == null ? null : other.forceClose.copy();
  }

  @Override
  public EventRegistrationClosingCriteriaCriteria copy() {
    return new EventRegistrationClosingCriteriaCriteria(this);
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

  public IntegerFilter getMaxAttendees() {
    return maxAttendees;
  }

  public IntegerFilter maxAttendees() {
    if (maxAttendees == null) {
      maxAttendees = new IntegerFilter();
    }
    return maxAttendees;
  }

  public void setMaxAttendees(IntegerFilter maxAttendees) {
    this.maxAttendees = maxAttendees;
  }

  public InstantFilter getClosingDate() {
    return closingDate;
  }

  public InstantFilter closingDate() {
    if (closingDate == null) {
      closingDate = new InstantFilter();
    }
    return closingDate;
  }

  public void setClosingDate(InstantFilter closingDate) {
    this.closingDate = closingDate;
  }

  public BooleanFilter getForceClose() {
    return forceClose;
  }

  public BooleanFilter forceClose() {
    if (forceClose == null) {
      forceClose = new BooleanFilter();
    }
    return forceClose;
  }

  public void setForceClose(BooleanFilter forceClose) {
    this.forceClose = forceClose;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final EventRegistrationClosingCriteriaCriteria that = (EventRegistrationClosingCriteriaCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(eventId, that.eventId) &&
      Objects.equals(maxAttendees, that.maxAttendees) &&
      Objects.equals(closingDate, that.closingDate) &&
      Objects.equals(forceClose, that.forceClose)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, eventId, maxAttendees, closingDate, forceClose);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "EventRegistrationClosingCriteriaCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (maxAttendees != null ? "maxAttendees=" + maxAttendees + ", " : "") +
            (closingDate != null ? "closingDate=" + closingDate + ", " : "") +
            (forceClose != null ? "forceClose=" + forceClose + ", " : "") +
            "}";
    }
}
