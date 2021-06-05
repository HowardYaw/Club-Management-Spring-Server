package com.thirdcc.webapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BigDecimalFilter;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.InstantFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.thirdcc.webapp.domain.EventActivity} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.EventActivityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /event-activities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EventActivityCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private LongFilter eventId;

  private InstantFilter startDate;

  private BigDecimalFilter durationInDay;

  private StringFilter name;

  public EventActivityCriteria() {}

  public EventActivityCriteria(EventActivityCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.eventId = other.eventId == null ? null : other.eventId.copy();
    this.startDate = other.startDate == null ? null : other.startDate.copy();
    this.durationInDay = other.durationInDay == null ? null : other.durationInDay.copy();
    this.name = other.name == null ? null : other.name.copy();
  }

  @Override
  public EventActivityCriteria copy() {
    return new EventActivityCriteria(this);
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

  public InstantFilter getStartDate() {
    return startDate;
  }

  public InstantFilter startDate() {
    if (startDate == null) {
      startDate = new InstantFilter();
    }
    return startDate;
  }

  public void setStartDate(InstantFilter startDate) {
    this.startDate = startDate;
  }

  public BigDecimalFilter getDurationInDay() {
    return durationInDay;
  }

  public BigDecimalFilter durationInDay() {
    if (durationInDay == null) {
      durationInDay = new BigDecimalFilter();
    }
    return durationInDay;
  }

  public void setDurationInDay(BigDecimalFilter durationInDay) {
    this.durationInDay = durationInDay;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final EventActivityCriteria that = (EventActivityCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(eventId, that.eventId) &&
      Objects.equals(startDate, that.startDate) &&
      Objects.equals(durationInDay, that.durationInDay) &&
      Objects.equals(name, that.name)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, eventId, startDate, durationInDay, name);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "EventActivityCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (durationInDay != null ? "durationInDay=" + durationInDay + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            "}";
    }
}
