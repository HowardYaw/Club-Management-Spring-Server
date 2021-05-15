package com.thirdcc.webapp.service.criteria;

import com.thirdcc.webapp.domain.enumeration.TransactionType;
import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BigDecimalFilter;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.thirdcc.webapp.domain.Transaction} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TransactionCriteria implements Serializable, Criteria {

  /**
   * Class for filtering TransactionType
   */
  public static class TransactionTypeFilter extends Filter<TransactionType> {

    public TransactionTypeFilter() {}

    public TransactionTypeFilter(TransactionTypeFilter filter) {
      super(filter);
    }

    @Override
    public TransactionTypeFilter copy() {
      return new TransactionTypeFilter(this);
    }
  }

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private LongFilter eventId;

  private LongFilter receiptId;

  private TransactionTypeFilter type;

  private BigDecimalFilter amount;

  private StringFilter details;

  private StringFilter createdBy;

  private InstantFilter createdDate;

  private StringFilter lastModifiedBy;

  private InstantFilter lastModifiedDate;

  public TransactionCriteria() {}

  public TransactionCriteria(TransactionCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.eventId = other.eventId == null ? null : other.eventId.copy();
    this.receiptId = other.receiptId == null ? null : other.receiptId.copy();
    this.type = other.type == null ? null : other.type.copy();
    this.amount = other.amount == null ? null : other.amount.copy();
    this.details = other.details == null ? null : other.details.copy();
    this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
    this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
    this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
    this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
  }

  @Override
  public TransactionCriteria copy() {
    return new TransactionCriteria(this);
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

  public LongFilter getReceiptId() {
    return receiptId;
  }

  public LongFilter receiptId() {
    if (receiptId == null) {
      receiptId = new LongFilter();
    }
    return receiptId;
  }

  public void setReceiptId(LongFilter receiptId) {
    this.receiptId = receiptId;
  }

  public TransactionTypeFilter getType() {
    return type;
  }

  public TransactionTypeFilter type() {
    if (type == null) {
      type = new TransactionTypeFilter();
    }
    return type;
  }

  public void setType(TransactionTypeFilter type) {
    this.type = type;
  }

  public BigDecimalFilter getAmount() {
    return amount;
  }

  public BigDecimalFilter amount() {
    if (amount == null) {
      amount = new BigDecimalFilter();
    }
    return amount;
  }

  public void setAmount(BigDecimalFilter amount) {
    this.amount = amount;
  }

  public StringFilter getDetails() {
    return details;
  }

  public StringFilter details() {
    if (details == null) {
      details = new StringFilter();
    }
    return details;
  }

  public void setDetails(StringFilter details) {
    this.details = details;
  }

  public StringFilter getCreatedBy() {
    return createdBy;
  }

  public StringFilter createdBy() {
    if (createdBy == null) {
      createdBy = new StringFilter();
    }
    return createdBy;
  }

  public void setCreatedBy(StringFilter createdBy) {
    this.createdBy = createdBy;
  }

  public InstantFilter getCreatedDate() {
    return createdDate;
  }

  public InstantFilter createdDate() {
    if (createdDate == null) {
      createdDate = new InstantFilter();
    }
    return createdDate;
  }

  public void setCreatedDate(InstantFilter createdDate) {
    this.createdDate = createdDate;
  }

  public StringFilter getLastModifiedBy() {
    return lastModifiedBy;
  }

  public StringFilter lastModifiedBy() {
    if (lastModifiedBy == null) {
      lastModifiedBy = new StringFilter();
    }
    return lastModifiedBy;
  }

  public void setLastModifiedBy(StringFilter lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public InstantFilter getLastModifiedDate() {
    return lastModifiedDate;
  }

  public InstantFilter lastModifiedDate() {
    if (lastModifiedDate == null) {
      lastModifiedDate = new InstantFilter();
    }
    return lastModifiedDate;
  }

  public void setLastModifiedDate(InstantFilter lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final TransactionCriteria that = (TransactionCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(eventId, that.eventId) &&
      Objects.equals(receiptId, that.receiptId) &&
      Objects.equals(type, that.type) &&
      Objects.equals(amount, that.amount) &&
      Objects.equals(details, that.details) &&
      Objects.equals(createdBy, that.createdBy) &&
      Objects.equals(createdDate, that.createdDate) &&
      Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
      Objects.equals(lastModifiedDate, that.lastModifiedDate)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, eventId, receiptId, type, amount, details, createdBy, createdDate, lastModifiedBy, lastModifiedDate);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (receiptId != null ? "receiptId=" + receiptId + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (details != null ? "details=" + details + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            "}";
    }
}
