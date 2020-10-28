package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.FacultyService;
import com.thirdcc.webapp.domain.Faculty;
import com.thirdcc.webapp.repository.FacultyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Faculty}.
 */
@Service
@Transactional
public class FacultyServiceImpl implements FacultyService {

    private final Logger log = LoggerFactory.getLogger(FacultyServiceImpl.class);

    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    /**
     * Save a faculty.
     *
     * @param faculty the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Faculty save(Faculty faculty) {
        log.debug("Request to save Faculty : {}", faculty);
        return facultyRepository.save(faculty);
    }

    /**
     * Get all the faculties.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Faculty> findAll() {
        log.debug("Request to get all Faculties");
        return facultyRepository.findAll();
    }


    /**
     * Get one faculty by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Faculty> findOne(Long id) {
        log.debug("Request to get Faculty : {}", id);
        return facultyRepository.findById(id);
    }

    /**
     * Delete the faculty by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Faculty : {}", id);
        facultyRepository.deleteById(id);
    }
}
