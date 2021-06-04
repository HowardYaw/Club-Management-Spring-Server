package com.thirdcc.webapp.service.criteria;

import com.thirdcc.webapp.domain.enumeration.EventStatus;
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
 * Criteria class for the {@link com.thirdcc.webapp.domain.Event} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.EventResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /events?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EventCriteria implements Serializable, Criteria {

  /**
   * Class for filtering EventStatus
   */
  public static class EventStatusFilter extends Filter<EventStatus> {

    public EventStatusFilter() {}

    public EventStatusFilter(EventStatusFilter filter) {
      super(filter);
    }

    @Override
    public EventStatusFilter copy() {
      return new EventStatusFilter(this);
    }
  }

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private StringFilter name;

  private StringFilter remarks;

  private StringFilter venue;

  private InstantFilter startDate;

  private InstantFilter endDate;

  private BigDecimalFilter fee;

  private BooleanFilter requiredTransport;

  private EventStatusFilter status;

  public EventCriteria() {}

  public EventCriteria(EventCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.name = other.name == null ? null : other.name.copy();
    this.remarks = other.remarks == null ? null : other.remarks.copy();
    this.venue = other.venue == null ? null : other.venue.copy();
    this.startDate = other.startDate == null ? null : other.startDate.copy();
    this.endDate = other.endDate == null ? null : other.endDate.copy();
    this.fee = other.fee == null ? null : other.fee.copy();
    this.requiredTransport = other.requiredTransport == null ? null : other.requiredTransport.copy();
    this.status = other.status == null ? null : other.status.copy();
  }

  @Override
  public EventCriteria copy() {
    return new EventCriteria(this);
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

  public StringFilter getRemarks() {
    return remarks;
  }

  public StringFilter remarks() {
    if (remarks == null) {
      remarks = new StringFilter();
    }
    return remarks;
  }

  public void setRemarks(StringFilter remarks) {
    this.remarks = remarks;
  }

  public StringFilter getVenue() {
    return venue;
  }

  public StringFilter venue() {
    if (venue == null) {
      venue = new StringFilter();
    }
    return venue;
  }

  public void setVenue(StringFilter venue) {
    this.venue = venue;
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

  public InstantFilter getEndDate() {
    return endDate;
  }

  public InstantFilter endDate() {
    if (endDate == null) {
      endDate = new InstantFilter();
    }
    return endDate;
  }

  public void setEndDate(InstantFilter endDate) {
    this.endDate = endDate;
  }

  public BigDecimalFilter getFee() {
    return fee;
  }

  public BigDecimalFilter fee() {
    if (fee == null) {
      fee = new BigDecimalFilter();
    }
    return fee;
  }

  public void setFee(BigDecimalFilter fee) {
    this.fee = fee;
  }

  public BooleanFilter getRequiredTransport() {
    return requiredTransport;
  }

  public BooleanFilter requiredTransport() {
    if (requiredTransport == null) {
      requiredTransport = new BooleanFilter();
    }
    return requiredTransport;
  }

  public void setRequiredTransport(BooleanFilter requiredTransport) {
    this.requiredTransport = requiredTransport;
  }

  public EventStatusFilter getStatus() {
    return status;
  }

  public EventStatusFilter status() {
    if (status == null) {
      status = new EventStatusFilter();
    }
    return status;
  }

  public void setStatus(EventStatusFilter status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final EventCriteria that = (EventCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(name, that.name) &&
      Objects.equals(remarks, that.remarks) &&
      Objects.equals(venue, that.venue) &&
      Objects.equals(startDate, that.startDate) &&
      Objects.equals(endDate, that.endDate) &&
      Objects.equals(fee, that.fee) &&
      Objects.equals(requiredTransport, that.requiredTransport) &&
      Objects.equals(status, that.status)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, remarks, venue, startDate, endDate, fee, requiredTransport, status);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "EventCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (remarks != null ? "remarks=" + remarks + ", " : "") +
            (venue != null ? "venue=" + venue + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (fee != null ? "fee=" + fee + ", " : "") +
            (requiredTransport != null ? "requiredTransport=" + requiredTransport + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            "}";
    }
}
