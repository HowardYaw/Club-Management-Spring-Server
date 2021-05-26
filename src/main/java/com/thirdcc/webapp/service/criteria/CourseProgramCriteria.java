package com.thirdcc.webapp.service.criteria;

import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

public class CourseProgramCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter facultyId;

    private StringFilter name;

    private IntegerFilter numOfSem;

    public CourseProgramCriteria() {
    }

    public CourseProgramCriteria(CourseProgramCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.facultyId = other.facultyId == null ? null : other.facultyId.copy();
        this.name = other.name == null ? null : other.name.copy();
    }

    @Override
    public CourseProgramCriteria copy() {
        return new CourseProgramCriteria(this);
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

    public LongFilter getFacultyId() {
        return facultyId;
    }

    public LongFilter facultyId() {
        if (facultyId == null) {
            facultyId = new LongFilter();
        }
        return facultyId;
    }

    public void setFacultyId(LongFilter facultyId) {
        this.facultyId = facultyId;
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

    public IntegerFilter getNumOfSem() {
        return numOfSem;
    }

    public IntegerFilter numOfSem() {
        if (numOfSem == null) {
            numOfSem = new IntegerFilter();
        }
        return numOfSem;
    }

    public void setNumOfSem(IntegerFilter numOfSem) {
        this.numOfSem = numOfSem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseProgramCriteria that = (CourseProgramCriteria) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(facultyId, that.facultyId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(numOfSem, that.numOfSem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, facultyId, name, numOfSem);
    }

    // prettier-ignore

    @Override
    public String toString() {
        return "CourseProgramCriteria{" +
            "id=" + id +
            ", facultyId=" + facultyId +
            ", name=" + name +
            ", numOfSem=" + numOfSem +
            '}';
    }
}
