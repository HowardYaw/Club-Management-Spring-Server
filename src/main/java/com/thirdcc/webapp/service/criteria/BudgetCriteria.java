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
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.thirdcc.webapp.domain.Budget} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.BudgetResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /budgets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BudgetCriteria implements Serializable, Criteria {

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

  private BigDecimalFilter amount;

  private TransactionTypeFilter type;

  private StringFilter name;

  public BudgetCriteria() {}

  public BudgetCriteria(BudgetCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.eventId = other.eventId == null ? null : other.eventId.copy();
    this.amount = other.amount == null ? null : other.amount.copy();
    this.type = other.type == null ? null : other.type.copy();
    this.name = other.name == null ? null : other.name.copy();
  }

  @Override
  public BudgetCriteria copy() {
    return new BudgetCriteria(this);
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
    final BudgetCriteria that = (BudgetCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(eventId, that.eventId) &&
      Objects.equals(amount, that.amount) &&
      Objects.equals(type, that.type) &&
      Objects.equals(name, that.name)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, eventId, amount, type, name);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "BudgetCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            "}";
    }
}
