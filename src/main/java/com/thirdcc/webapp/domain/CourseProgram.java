package com.thirdcc.webapp.domain;

import com.thirdcc.webapp.domain.enumeration.UserUniStatus;
import com.thirdcc.webapp.service.dto.UserUniInfoDTO;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A CourseProgram.
 */
@Entity
@Table(name = "course_program")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CourseProgram implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faculty_id")
    private Long facultyId;

    @Column(name = "name")
    private String name;

    @Column(name = "num_of_sem")
    private Integer numOfSem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumOfSem() {
        return numOfSem;
    }

    public void setNumOfSem(Integer numOfSem) {
        this.numOfSem = numOfSem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserUniStatus)) {
            return false;
        }
        return id != null && id.equals(((CourseProgram) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "CourseProgram{" +
            "id=" + id +
            ", facultyId=" + facultyId +
            ", name='" + name + '\'' +
            ", numOfSem=" + numOfSem +
            '}';
    }
}
