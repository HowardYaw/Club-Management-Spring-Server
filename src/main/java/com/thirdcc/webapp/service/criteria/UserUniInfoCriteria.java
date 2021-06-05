package com.thirdcc.webapp.service.criteria;

import com.thirdcc.webapp.domain.enumeration.UserUniStatus;

import java.io.Serializable;
import java.util.Objects;

import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BigDecimalFilter;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

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

        public UserUniStatusFilter() {
        }

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

    private LongFilter courseProgramId;

    private StringFilter yearSession;

    private IntegerFilter intakeSemester;

    private StringFilter stayIn;

    private UserUniStatusFilter status;

    public UserUniInfoCriteria() {
    }

    public UserUniInfoCriteria(UserUniInfoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.courseProgramId = other.courseProgramId == null ? null : other.courseProgramId.copy();
        this.yearSession = other.yearSession == null ? null : other.yearSession.copy();
        this.intakeSemester = other.intakeSemester == null ? null : other.intakeSemester.copy();
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

    public LongFilter getCourseProgramId() {
        return courseProgramId;
    }

    public LongFilter courseProgramId() {
        if (courseProgramId == null) {
            courseProgramId = new LongFilter();
        }
        return courseProgramId;
    }

    public void setCourseProgramId(LongFilter courseProgramId) {
        this.courseProgramId = courseProgramId;
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
                Objects.equals(yearSession, that.yearSession) &&
                Objects.equals(intakeSemester, that.intakeSemester) &&
                Objects.equals(stayIn, that.stayIn) &&
                Objects.equals(status, that.status)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, courseProgramId, yearSession, intakeSemester, stayIn, status);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserUniInfoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (yearSession != null ? "yearSession=" + yearSession + ", " : "") +
            (intakeSemester != null ? "intakeSemester=" + intakeSemester + ", " : "") +
            (stayIn != null ? "stayIn=" + stayIn + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            "}";
    }
}
