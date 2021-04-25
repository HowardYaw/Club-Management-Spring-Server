package com.thirdcc.webapp.service;

import com.thirdcc.webapp.domain.CourseProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseProgramService {
    Page<CourseProgram> findAllByFacultyId(Long facultyId, Pageable pageable);
}
