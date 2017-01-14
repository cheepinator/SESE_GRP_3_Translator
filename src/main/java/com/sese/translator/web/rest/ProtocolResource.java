package com.sese.translator.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sese.translator.service.ProtocolService;
import com.sese.translator.service.dto.ProtocolDTO;
import com.sese.translator.service.dto.protocol.ProtocolEntryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Project.
 */
@RestController
@RequestMapping("/api")
public class ProtocolResource {

    private final Logger log = LoggerFactory.getLogger(ProtocolResource.class);

    @Inject
    private ProtocolService protocolService;


    /**
     * GET  /protocol/:id : get the protocol of the "id" project.
     *
     * @param id the id of the protocolDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectDTO, or with status 404 (Not Found)
     */
    @GetMapping("/protocol/{id}")
    @Timed
    public ResponseEntity<ProtocolDTO> getProject(@PathVariable Long id) {
        log.debug("REST request to get Protocol : {}", id);
        ProtocolDTO protocolDTO = protocolService.findAllOfProject(id);

        return Optional.ofNullable(protocolDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * GET  /protocol/:id : get the protocol of the "id" project.
     *
     * @param id the id of the protocolDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectDTO, or with status 404 (Not Found)
     */
    @GetMapping("/protocollist/{id}")
    @Timed
    public ResponseEntity<List<ProtocolEntryDTO>> getProjectAsList(@PathVariable Long id) {
        log.debug("REST request to get Protocol : {}", id);
        List<ProtocolEntryDTO> result = protocolService.findAllOfProjectAsList(id);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
