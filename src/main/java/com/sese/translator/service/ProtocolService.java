package com.sese.translator.service;

import com.sese.translator.service.dto.ProtocolDTO;

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


}
