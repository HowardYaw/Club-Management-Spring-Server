package com.thirdcc.webapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.thirdcc.webapp.domain.YearSession} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.YearSessionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /year-sessions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class YearSessionCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private StringFilter value;

  public YearSessionCriteria() {}

  public YearSessionCriteria(YearSessionCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.value = other.value == null ? null : other.value.copy();
  }

  @Override
  public YearSessionCriteria copy() {
    return new YearSessionCriteria(this);
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

  public StringFilter getValue() {
    return value;
  }

  public StringFilter value() {
    if (value == null) {
      value = new StringFilter();
    }
    return value;
  }

  public void setValue(StringFilter value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final YearSessionCriteria that = (YearSessionCriteria) o;
    return Objects.equals(id, that.id) && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, value);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "YearSessionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (value != null ? "value=" + value + ", " : "") +
            "}";
    }
}
