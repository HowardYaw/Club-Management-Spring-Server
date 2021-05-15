package com.thirdcc.webapp.service.criteria;

import com.thirdcc.webapp.domain.enumeration.UserUniStatus;
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
 * Criteria class for the {@link com.thirdcc.webapp.domain.UserUniInfo} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.UserUniInfoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-uni-infos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UserUniInfoCriteria implements Serializable, Criteria {

  /**
   * Class for filtering UserUniStatus
   */
  public static class UserUniStatusFilter extends Filter<UserUniStatus> {

    public UserUniStatusFilter() {}

    public UserUniStatusFilter(UserUniStatusFilter filter) {
      super(filter);
    }

    @Override
    public UserUniStatusFilter copy() {
      return new UserUniStatusFilter(this);
    }
  }

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private LongFilter userId;

  private StringFilter faculty;

  private StringFilter program;

  private StringFilter yearSession;

  private IntegerFilter intakeSemester;

  private BigDecimalFilter yearOfStudy;

  private StringFilter stayIn;

  private UserUniStatusFilter status;

  public UserUniInfoCriteria() {}

  public UserUniInfoCriteria(UserUniInfoCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.userId = other.userId == null ? null : other.userId.copy();
    this.faculty = other.faculty == null ? null : other.faculty.copy();
    this.program = other.program == null ? null : other.program.copy();
    this.yearSession = other.yearSession == null ? null : other.yearSession.copy();
    this.intakeSemester = other.intakeSemester == null ? null : other.intakeSemester.copy();
    this.yearOfStudy = other.yearOfStudy == null ? null : other.yearOfStudy.copy();
    this.stayIn = other.stayIn == null ? null : other.stayIn.copy();
    this.status = other.status == null ? null : other.status.copy();
  }

  @Override
  public UserUniInfoCriteria copy() {
    return new UserUniInfoCriteria(this);
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

  public StringFilter getFaculty() {
    return faculty;
  }

  public StringFilter faculty() {
    if (faculty == null) {
      faculty = new StringFilter();
    }
    return faculty;
  }

  public void setFaculty(StringFilter faculty) {
    this.faculty = faculty;
  }

  public StringFilter getProgram() {
    return program;
  }

  public StringFilter program() {
    if (program == null) {
      program = new StringFilter();
    }
    return program;
  }

  public void setProgram(StringFilter program) {
    this.program = program;
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

  public IntegerFilter getIntakeSemester() {
    return intakeSemester;
  }

  public IntegerFilter intakeSemester() {
    if (intakeSemester == null) {
      intakeSemester = new IntegerFilter();
    }
    return intakeSemester;
  }

  public void setIntakeSemester(IntegerFilter intakeSemester) {
    this.intakeSemester = intakeSemester;
  }

  public BigDecimalFilter getYearOfStudy() {
    return yearOfStudy;
  }

  public BigDecimalFilter yearOfStudy() {
    if (yearOfStudy == null) {
      yearOfStudy = new BigDecimalFilter();
    }
    return yearOfStudy;
  }

  public void setYearOfStudy(BigDecimalFilter yearOfStudy) {
    this.yearOfStudy = yearOfStudy;
  }

  public StringFilter getStayIn() {
    return stayIn;
  }

  public StringFilter stayIn() {
    if (stayIn == null) {
      stayIn = new StringFilter();
    }
    return stayIn;
  }

  public void setStayIn(StringFilter stayIn) {
    this.stayIn = stayIn;
  }

  public UserUniStatusFilter getStatus() {
    return status;
  }

  public UserUniStatusFilter status() {
    if (status == null) {
      status = new UserUniStatusFilter();
    }
    return status;
  }

  public void setStatus(UserUniStatusFilter status) {
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
    final UserUniInfoCriteria that = (UserUniInfoCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(userId, that.userId) &&
      Objects.equals(faculty, that.faculty) &&
      Objects.equals(program, that.program) &&
      Objects.equals(yearSession, that.yearSession) &&
      Objects.equals(intakeSemester, that.intakeSemester) &&
      Objects.equals(yearOfStudy, that.yearOfStudy) &&
      Objects.equals(stayIn, that.stayIn) &&
      Objects.equals(status, that.status)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, faculty, program, yearSession, intakeSemester, yearOfStudy, stayIn, status);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "UserUniInfoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (faculty != null ? "faculty=" + faculty + ", " : "") +
            (program != null ? "program=" + program + ", " : "") +
            (yearSession != null ? "yearSession=" + yearSession + ", " : "") +
            (intakeSemester != null ? "intakeSemester=" + intakeSemester + ", " : "") +
            (yearOfStudy != null ? "yearOfStudy=" + yearOfStudy + ", " : "") +
            (stayIn != null ? "stayIn=" + stayIn + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            "}";
    }
}
