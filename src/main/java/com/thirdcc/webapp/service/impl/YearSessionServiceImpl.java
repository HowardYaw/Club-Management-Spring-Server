package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.exception.BadRequestException;
import com.thirdcc.webapp.exception.InternalServerErrorException;
import com.thirdcc.webapp.service.YearSessionService;
import com.thirdcc.webapp.domain.YearSession;
import com.thirdcc.webapp.repository.YearSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link YearSession}.
 */
@Service
@Transactional
public class YearSessionServiceImpl implements YearSessionService {

    private final Logger log = LoggerFactory.getLogger(YearSessionServiceImpl.class);

    private final YearSessionRepository yearSessionRepository;

    public YearSessionServiceImpl(YearSessionRepository yearSessionRepository) {
        this.yearSessionRepository = yearSessionRepository;
    }

    /**
     * Save a yearSession.
     *
     * @param yearSession the entity to save.
     * @return the persisted entity.
     */
    @Override
    public YearSession save(YearSession yearSession) {
        log.debug("Request to save YearSession : {}", yearSession);
        return yearSessionRepository.save(yearSession);
    }

    /**
     * Get all the yearSessions.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<YearSession> findAll() {
        log.debug("Request to get all YearSessions");
        return yearSessionRepository.findAll();
    }


    /**
     * Get one yearSession by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<YearSession> findOne(Long id) {
        log.debug("Request to get YearSession : {}", id);
        return yearSessionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public String getDefaultYearSessionString() {
        List<YearSession> yearSessionList = yearSessionRepository.findAll();
        if (yearSessionList.isEmpty()) {
            throw new RuntimeException("Not a single YearSession is found in DB");
        }
        YearSession defaultYearSession = yearSessionList.get(yearSessionList.size() - 1);
        return defaultYearSession.getValue();
    }

    @Override
    @Transactional(readOnly = true)
    public String getYearSessionStringById(Long id) {
        return yearSessionRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestException("YearSession is not found"))
            .getValue();
    }

    @Override
    public YearSession getCurrentYearSession() {
        return yearSessionRepository.findFirstByOrderByIdDesc()
            .orElseThrow(() -> new InternalServerErrorException("Year Session not found"));
    }

    /**
     * Delete the yearSession by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete YearSession : {}", id);
        yearSessionRepository.deleteById(id);
    }
}
