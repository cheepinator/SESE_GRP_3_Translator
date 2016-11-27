package com.sese.translator.service.impl;

import com.sese.translator.domain.Release;
import com.sese.translator.repository.ReleaseRepository;
import com.sese.translator.service.ReleaseService;
import com.sese.translator.service.dto.ReleaseDTO;
import com.sese.translator.service.mapper.ReleaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Release.
 */
@Service
@Transactional
public class ReleaseServiceImpl implements ReleaseService{

    private final Logger log = LoggerFactory.getLogger(ReleaseServiceImpl.class);

    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private ReleaseMapper releaseMapper;

    /**
     * Save a release.
     *
     * @param releaseDTO the entity to save
     * @return the persisted entity
     */
    public ReleaseDTO save(ReleaseDTO releaseDTO) {
        log.debug("Request to save Release : {}", releaseDTO);
        Release release = releaseMapper.releaseDTOToRelease(releaseDTO);
        release = releaseRepository.save(release);
        ReleaseDTO result = releaseMapper.releaseToReleaseDTO(release);
        return result;
    }

    /**
     *  Get all the releases.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ReleaseDTO> findAll() {
        log.debug("Request to get all Releases");
        List<ReleaseDTO> result = releaseRepository.findAllWithEagerRelationships().stream()
            .map(releaseMapper::releaseToReleaseDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one release by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public ReleaseDTO findOne(Long id) {
        log.debug("Request to get Release : {}", id);
        Release release = releaseRepository.findOneWithEagerRelationships(id);
        ReleaseDTO releaseDTO = releaseMapper.releaseToReleaseDTO(release);
        return releaseDTO;
    }


//    @Transactional(readOnly = true)
//    public ReleaseDTO findCurrentByProjectId(Long id) {
//        log.debug("Request to get current Release of Project : {}", id);
//        Release release = releaseRepository.findCurrentByProjectId(id);
//        ReleaseDTO releaseDTO = releaseMapper.releaseToReleaseDTO(release);
//        return releaseDTO;
//    }




    /**
     *  Delete the  release by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Release : {}", id);
        releaseRepository.delete(id);
    }
}
