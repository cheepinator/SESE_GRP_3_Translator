package com.sese.translator.service;

import com.sese.translator.service.dto.ProtocolDTO;
import com.sese.translator.service.dto.protocol.ProtocolEntryDTO;

import java.util.List;

/**
 * Service Interface for managing Protocol.
 */
public interface ProtocolService {


    /**
     *  Get a Protocol for everything related to a single Project
     *  @param id the id of the entity
     *
     *  @return the list of entities
     */
    ProtocolDTO findAllOfProject(Long id);


    /**
     *  Get a Protocol for everything related to a single Project
     *  @param id the id of the entity
     *
     *  @return the list of entities
     */
    List<ProtocolEntryDTO> findAllOfProjectAsList(Long id);


}
