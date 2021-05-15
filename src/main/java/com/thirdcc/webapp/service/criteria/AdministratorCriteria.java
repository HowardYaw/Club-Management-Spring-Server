package com.thirdcc.webapp.service.criteria;

import com.thirdcc.webapp.domain.enumeration.AdministratorRole;
import com.thirdcc.webapp.domain.enumeration.AdministratorStatus;
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
 * Criteria class for the {@link com.thirdcc.webapp.domain.Administrator} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.AdministratorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /administrators?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AdministratorCriteria implements Serializable, Criteria {

  /**
   * Class for filtering AdministratorRole
   */
  public static class AdministratorRoleFilter extends Filter<AdministratorRole> {

    public AdministratorRoleFilter() {}

    public AdministratorRoleFilter(AdministratorRoleFilter filter) {
      super(filter);
    }

    @Override
    public AdministratorRoleFilter copy() {
      return new AdministratorRoleFilter(this);
    }
  }

  /**
   * Class for filtering AdministratorStatus
   */
  public static class AdministratorStatusFilter extends Filter<AdministratorStatus> {

    public AdministratorStatusFilter() {}

    public AdministratorStatusFilter(AdministratorStatusFilter filter) {
      super(filter);
    }

    @Override
    public AdministratorStatusFilter copy() {
      return new AdministratorStatusFilter(this);
    }
  }

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private LongFilter userId;

  private StringFilter yearSession;

  private AdministratorRoleFilter role;

  private AdministratorStatusFilter status;

  public AdministratorCriteria() {}

  public AdministratorCriteria(AdministratorCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.userId = other.userId == null ? null : other.userId.copy();
    this.yearSession = other.yearSession == null ? null : other.yearSession.copy();
    this.role = other.role == null ? null : other.role.copy();
    this.status = other.status == null ? null : other.status.copy();
  }

  @Override
  public AdministratorCriteria copy() {
    return new AdministratorCriteria(this);
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

  public StringFilter getYearSession() {
    return yearSession;
  }

  public StringFilter yearSession() {
    if (yearSession == null) {
      yearSession = new StringFilter();
    }
    return yearSession;
  }

  public void setYearSession(StringFilter yearSession) {
    this.yearSession = yearSession;
  }

  public AdministratorRoleFilter getRole() {
    return role;
  }

  public AdministratorRoleFilter role() {
    if (role == null) {
      role = new AdministratorRoleFilter();
    }
    return role;
  }

  public void setRole(AdministratorRoleFilter role) {
    this.role = role;
  }

  public AdministratorStatusFilter getStatus() {
    return status;
  }

  public AdministratorStatusFilter status() {
    if (status == null) {
      status = new AdministratorStatusFilter();
    }
    return status;
  }

  public void setStatus(AdministratorStatusFilter status) {
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
    final AdministratorCriteria that = (AdministratorCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(userId, that.userId) &&
      Objects.equals(yearSession, that.yearSession) &&
      Objects.equals(role, that.role) &&
      Objects.equals(status, that.status)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, yearSession, role, status);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "AdministratorCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (yearSession != null ? "yearSession=" + yearSession + ", " : "") +
            (role != null ? "role=" + role + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            "}";
    }
}
