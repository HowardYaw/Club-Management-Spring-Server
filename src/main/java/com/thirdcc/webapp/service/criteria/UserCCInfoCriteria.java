package com.thirdcc.webapp.service.criteria;

import com.thirdcc.webapp.domain.enumeration.ClubFamilyRole;
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
 * Criteria class for the {@link com.thirdcc.webapp.domain.UserCCInfo} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.UserCCInfoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-cc-infos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UserCCInfoCriteria implements Serializable, Criteria {

  /**
   * Class for filtering ClubFamilyRole
   */
  public static class ClubFamilyRoleFilter extends Filter<ClubFamilyRole> {

    public ClubFamilyRoleFilter() {}

    public ClubFamilyRoleFilter(ClubFamilyRoleFilter filter) {
      super(filter);
    }

    @Override
    public ClubFamilyRoleFilter copy() {
      return new ClubFamilyRoleFilter(this);
    }
  }

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private LongFilter userId;

  private LongFilter clubFamilyId;

  private ClubFamilyRoleFilter familyRole;

  private StringFilter yearSession;

  private StringFilter intakeYearSession;

  private StringFilter userFirstName;

  private StringFilter userLastName;

  private LongFilter courseProgramId;

  public UserCCInfoCriteria() {}

  public UserCCInfoCriteria(UserCCInfoCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.userId = other.userId == null ? null : other.userId.copy();
    this.clubFamilyId = other.clubFamilyId == null ? null : other.clubFamilyId.copy();
    this.familyRole = other.familyRole == null ? null : other.familyRole.copy();
    this.yearSession = other.yearSession == null ? null : other.yearSession.copy();
    this.intakeYearSession = other.intakeYearSession == null ? null : other.intakeYearSession.copy();
    this.userFirstName = other.userFirstName == null ? null : other.userFirstName.copy();
    this.userLastName = other.userLastName == null ? null : other.userLastName.copy();
    this.courseProgramId = other.courseProgramId == null ? null : other.courseProgramId.copy();
  }

  @Override
  public UserCCInfoCriteria copy() {
    return new UserCCInfoCriteria(this);
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

  public LongFilter getClubFamilyId() {
    return clubFamilyId;
  }

  public LongFilter clubFamilyId() {
    if (clubFamilyId == null) {
      clubFamilyId = new LongFilter();
    }
    return clubFamilyId;
  }

  public void setClubFamilyId(LongFilter clubFamilyId) {
    this.clubFamilyId = clubFamilyId;
  }

  public ClubFamilyRoleFilter getFamilyRole() {
    return familyRole;
  }

  public ClubFamilyRoleFilter familyRole() {
    if (familyRole == null) {
      familyRole = new ClubFamilyRoleFilter();
    }
    return familyRole;
  }

  public void setFamilyRole(ClubFamilyRoleFilter familyRole) {
    this.familyRole = familyRole;
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

    public StringFilter getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(StringFilter userFirstName) {
        this.userFirstName = userFirstName;
    }

    public StringFilter getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(StringFilter userLastName) {
        this.userLastName = userLastName;
    }

    public StringFilter getIntakeYearSession() {
        return intakeYearSession;
    }

    public void setIntakeYearSession(StringFilter intakeYearSession) {
        this.intakeYearSession = intakeYearSession;
    }

    public LongFilter getCourseProgramId() {
        return courseProgramId;
    }

    public void setCourseProgramId(LongFilter courseProgramId) {
        this.courseProgramId = courseProgramId;
    }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final UserCCInfoCriteria that = (UserCCInfoCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(userId, that.userId) &&
      Objects.equals(clubFamilyId, that.clubFamilyId) &&
      Objects.equals(familyRole, that.familyRole) &&
      Objects.equals(yearSession, that.yearSession)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, clubFamilyId, familyRole, yearSession);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "UserCCInfoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (clubFamilyId != null ? "clubFamilyId=" + clubFamilyId + ", " : "") +
            (familyRole != null ? "familyRole=" + familyRole + ", " : "") +
            (yearSession != null ? "yearSession=" + yearSession + ", " : "") +
            "}";
    }
}
