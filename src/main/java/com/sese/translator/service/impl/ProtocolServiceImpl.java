package com.sese.translator.service.impl;

import com.sese.translator.repository.ProjectassignmentRepository;
import com.sese.translator.repository.TranslationRepository;
import com.sese.translator.service.ProtocolService;
import com.sese.translator.service.UserService;
import com.sese.translator.service.dto.ProtocolDTO;
import com.sese.translator.service.mapper.LanguageMapper;
import com.sese.translator.service.mapper.ProjectMapper;
import com.sese.translator.service.mapper.ProjectassignmentMapper;
import com.sese.translator.service.mapper.TranslationProtocolMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Service Implementation for managing Protocol.
 */
@Service
@Transactional
public class ProtocolServiceImpl implements ProtocolService {

    private final Logger log = LoggerFactory.getLogger(ProtocolServiceImpl.class);


    @Inject
    private ProjectassignmentRepository protocolassignmentRepository;

    @Inject
    private UserService userService;

    @Inject
    private ProjectMapper protocolMapper;

    @Inject
    private LanguageMapper languageMapper;

    @Inject
    private ProjectassignmentMapper protocolassignmentMapper;

    @Inject
    private TranslationRepository translationRepository;

    @Inject
    private EntityManager entityManager;

    @Inject
    private TranslationProtocolMapper translationProtocolMapper;




    /**
     * Get a Protocol for everything related to a single Project
     *
     * @param id the id of the entity
     * @return the list of entities
     */
    @Override
    public ProtocolDTO findAllOfProject(Long id) {
        //AuditReader reader = AuditReaderFactory.get(entityManager);

       // reader.createQuery().forfsda

        ProtocolDTO result = new ProtocolDTO();
        result.setTranslations(translationProtocolMapper.translationsToTranslationDTOs(translationRepository.translationProtocol(id)));
        System.out.println(result.getTranslations().get(0).getCreatedBy());
        result.setProjectId(id);

        return result;
    }
}
