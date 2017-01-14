package com.sese.translator.service.impl;

import com.sese.translator.domain.*;
import com.sese.translator.repository.*;
import com.sese.translator.service.ProtocolService;
import com.sese.translator.service.UserService;
import com.sese.translator.service.dto.ProtocolDTO;
import com.sese.translator.service.dto.protocol.ProtocolEntryDTO;
import com.sese.translator.service.mapper.*;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

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
    private DefinitionRepository definitionRepository;


    @Inject
    private LanguageRepository languageRepository;


    @Inject
    private ProjectassignmentRepository projectassignmentRepository;

    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private EntityManager entityManager;

    @Inject
    private TranslationProtocolMapper translationProtocolMapper;

    @Inject
    private DefinitionProtocolMapper definitionProtocolMapper;

    @Inject
    private LanguageProtocolMapper languageProtocolMapper;

    @Inject
    private ProjectassignmentProtocolMapper projectassignmentProtocolMapper;

    @Inject
    private ReleaseProtocolMapper releaseProtocolMapper;

    @Inject
    private UserProtocolMapper  userProtocolMapper;



    /**
     * Get a Protocol for everything related to a single Project
     *  Can be used, if we decide to show each Type of entry in a single Table
     *
     * @param id the id of the entity
     * @return the list of entities
     */
    @Override
    public ProtocolDTO findAllOfProject(Long id) {
        AuditReader reader = AuditReaderFactory.get(entityManager);



        //--- Translations -----------
        List<Translation> relevantTranslations = translationRepository.translationsOfProject(id);
        List<Number> relevantTranslationsIDs = new ArrayList<>();

        relevantTranslations.forEach(translation -> relevantTranslationsIDs.add(translation.getId()));

        List<Object[]> revListTranslation = (List<Object[]>)reader.createQuery().forRevisionsOfEntity(Translation.class, false, true)
            .add(AuditEntity.id().in(relevantTranslationsIDs)).getResultList();
        List<Translation> resultTranslationList = new ArrayList<Translation>();


        revListTranslation.forEach(objects -> resultTranslationList.add((Translation) objects[0]));

        ProtocolDTO result = new ProtocolDTO();
        result.setTranslations(translationProtocolMapper.translationsToTranslationDTOs(resultTranslationList));


        //--- Definitions -----------
        List<Definition> relevantDefinitions = definitionRepository.definitionsOfProject(id);
        List<Number> relevantDefinitionsIDs = new ArrayList<>();

        relevantDefinitions.forEach(definition -> relevantDefinitionsIDs.add(definition.getId()));

        List<Object[]> revListDefinition = (List<Object[]>)reader.createQuery().forRevisionsOfEntity(Definition.class, false, true)
            .add(AuditEntity.id().in(relevantDefinitionsIDs)).getResultList();
        List<Definition> resultListDefinition = new ArrayList<Definition>();

        revListDefinition.forEach(objects -> resultListDefinition.add((Definition) objects[0]));
        result.setDefinitions(definitionProtocolMapper.definitionsToDefinitionProtocolDTOs(resultListDefinition));



        //--- Languages -----------
        List<Language> relevantLanguages = languageRepository.findByProjectId(id);
        List<Number> relevantLanguagesIDs = new ArrayList<>();

        relevantLanguages.forEach(language -> relevantLanguagesIDs.add(language.getId()));

        List<Object[]> revListLanguage = (List<Object[]>)reader.createQuery().forRevisionsOfEntity(Language.class, false, true)
            .add(AuditEntity.id().in(relevantLanguagesIDs)).getResultList();
        List<Language> resultListLanguage = new ArrayList<Language>();

        revListLanguage.forEach(objects -> resultListLanguage.add((Language) objects[0]));
        result.setLanguages(languageProtocolMapper.languagesToLanguageProtocolDTOs(resultListLanguage));


// Auskommentiert wg. Usern, die nicht vom initial schema aus Audited sind
//        //--- Projectassignments -----------
//        List<Projectassignment> relevantProjectassignments = projectassignmentRepository.findByAssignedProjectId(id);
//        List<Number> relevantProjectassignmentsIDs = new ArrayList<>();
//
//        relevantProjectassignments.forEach(projectassignment -> relevantProjectassignmentsIDs.add(projectassignment.getId()));
//
//        List<Object[]> revListProjectassignment = (List<Object[]>)reader.createQuery().forRevisionsOfEntity(Projectassignment.class, false, true)
//            .add(AuditEntity.id().in(relevantProjectassignmentsIDs)).getResultList();
//        List<Projectassignment> resultListProjectassignment = new ArrayList<Projectassignment>();
//
//        revListProjectassignment.forEach(objects -> resultListProjectassignment.add((Projectassignment) objects[0]));
//        result.setProjectassignments(projectassignmentProtocolMapper.projectassignmentsToProjectassignmentProtocolDTOs(resultListProjectassignment));



        //--- Releases -----------
        List<Release> relevantReleases = releaseRepository.findByProjectIdWithEagerRelationships(id);
        List<Number> relevantReleasesIDs = new ArrayList<>();

        relevantReleases.forEach(release -> relevantReleasesIDs.add(release.getId()));

        List<Object[]> revListRelease = (List<Object[]>)reader.createQuery().forRevisionsOfEntity(Release.class, false, true)
            .add(AuditEntity.id().in(relevantReleasesIDs)).getResultList();
        List<Release> resultListRelease = new ArrayList<Release>();

        revListRelease.forEach(objects -> resultListRelease.add((Release) objects[0]));
        result.setReleases(releaseProtocolMapper.releasesToReleaseProtocolDTOs(resultListRelease));



        result.setProjectId(id);

        return result;
    }

    /**
     * Get a Protocol for everything related to a single Project
     *
     * @param id the id of the entity
     * @return the list of entities
     */
    @Override
    public List<ProtocolEntryDTO> findAllOfProjectAsList(Long id) {
        List<ProtocolEntryDTO> result= new ArrayList<>();

        ProtocolDTO protocolDTO = this.findAllOfProject(id);

        protocolDTO.getTranslations().forEach(translationProtocolDTO -> result.add(new ProtocolEntryDTO(translationProtocolDTO.toString(),translationProtocolDTO.getCreatedBy(),translationProtocolDTO.getCreatedDate(),translationProtocolDTO.getLastModifiedBy(),translationProtocolDTO.getLastModifiedDate())));
        protocolDTO.getDefinitions().forEach(definitionProtocolDTO -> result.add(new ProtocolEntryDTO(definitionProtocolDTO.toString(),definitionProtocolDTO.getCreatedBy(),definitionProtocolDTO.getCreatedDate(),definitionProtocolDTO.getLastModifiedBy(),definitionProtocolDTO.getLastModifiedDate())));
        protocolDTO.getLanguages().forEach(languageProtocolDTO -> result.add(new ProtocolEntryDTO(languageProtocolDTO.toString(),languageProtocolDTO.getCreatedBy(),languageProtocolDTO.getCreatedDate(),languageProtocolDTO.getLastModifiedBy(),languageProtocolDTO.getLastModifiedDate())));
        //protocolDTO.getProjectassignments().forEach(projectassignmentProtocolDTO -> result.add(new ProtocolEntryDTO(projectassignmentProtocolDTO.toString(),projectassignmentProtocolDTO.getCreatedBy(),projectassignmentProtocolDTO.getCreatedDate(),projectassignmentProtocolDTO.getLastModifiedBy(),projectassignmentProtocolDTO.getLastModifiedDate())));
        protocolDTO.getReleases().forEach(releaseProtocolDTO -> result.add(new ProtocolEntryDTO(releaseProtocolDTO.toString(),releaseProtocolDTO.getCreatedBy(),releaseProtocolDTO.getCreatedDate(),releaseProtocolDTO.getLastModifiedBy(),releaseProtocolDTO.getLastModifiedDate())));


        return result;
    }
}
