package com.thirdcc.webapp.service.impl;

import com.thirdcc.webapp.service.ImageStorageService;
import com.thirdcc.webapp.domain.ImageStorage;
import com.thirdcc.webapp.repository.ImageStorageRepository;
import com.thirdcc.webapp.service.dto.ImageStorageDTO;
import com.thirdcc.webapp.service.mapper.ImageStorageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link ImageStorage}.
 */
@Service
@Transactional
public class ImageStorageServiceImpl implements ImageStorageService {

    private final Logger log = LoggerFactory.getLogger(ImageStorageServiceImpl.class);

    private final ImageStorageRepository imageStorageRepository;

    private final ImageStorageMapper imageStorageMapper;

    public ImageStorageServiceImpl(ImageStorageRepository imageStorageRepository, ImageStorageMapper imageStorageMapper) {
        this.imageStorageRepository = imageStorageRepository;
        this.imageStorageMapper = imageStorageMapper;
    }

    /**
     * Save a imageStorage.
     *
     * @param imageStorageDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ImageStorageDTO save(ImageStorageDTO imageStorageDTO) {
        log.debug("Request to save ImageStorage : {}", imageStorageDTO);
        ImageStorage imageStorage = imageStorageMapper.toEntity(imageStorageDTO);
        imageStorage = imageStorageRepository.save(imageStorage);
        return imageStorageMapper.toDto(imageStorage);
    }

    /**
     * Get all the imageStorages.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ImageStorageDTO> findAll() {
        log.debug("Request to get all ImageStorages");
        return imageStorageRepository.findAll().stream()
            .map(imageStorageMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one imageStorage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ImageStorageDTO> findOne(Long id) {
        log.debug("Request to get ImageStorage : {}", id);
        return imageStorageRepository.findById(id)
            .map(imageStorageMapper::toDto);
    }

    /**
     * Delete the imageStorage by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ImageStorage : {}", id);
        imageStorageRepository.deleteById(id);
    }
}
