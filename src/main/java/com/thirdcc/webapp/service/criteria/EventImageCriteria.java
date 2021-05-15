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
 * Criteria class for the {@link com.thirdcc.webapp.domain.EventImage} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.EventImageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /event-images?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EventImageCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private LongFilter eventId;

  private LongFilter imageStorageId;

  public EventImageCriteria() {}

  public EventImageCriteria(EventImageCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.eventId = other.eventId == null ? null : other.eventId.copy();
    this.imageStorageId = other.imageStorageId == null ? null : other.imageStorageId.copy();
  }

  @Override
  public EventImageCriteria copy() {
    return new EventImageCriteria(this);
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

  public LongFilter getImageStorageId() {
    return imageStorageId;
  }

  public LongFilter imageStorageId() {
    if (imageStorageId == null) {
      imageStorageId = new LongFilter();
    }
    return imageStorageId;
  }

  public void setImageStorageId(LongFilter imageStorageId) {
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
    final EventImageCriteria that = (EventImageCriteria) o;
    return Objects.equals(id, that.id) && Objects.equals(eventId, that.eventId) && Objects.equals(imageStorageId, that.imageStorageId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, eventId, imageStorageId);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "EventImageCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (imageStorageId != null ? "imageStorageId=" + imageStorageId + ", " : "") +
            "}";
    }
}
