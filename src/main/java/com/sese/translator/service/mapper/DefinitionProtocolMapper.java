package com.sese.translator.service.mapper;

import com.sese.translator.domain.Definition;
import com.sese.translator.domain.Release;
import com.sese.translator.service.dto.protocol.DefinitionProtocolDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for the entity Definition and its DTO DefinitionProtocolDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DefinitionProtocolMapper {

    @Mapping(source = "release.id", target = "releaseId")
    DefinitionProtocolDTO definitionToDefinitionProtocolDTO(Definition definition);

    List<DefinitionProtocolDTO> definitionsToDefinitionProtocolDTOs(List<Definition> definitions);

    @Mapping(target = "translations", ignore = true)
    @Mapping(source = "releaseId", target = "release")
    Definition definitionDTOToDefinition(DefinitionProtocolDTO definitionDTO);

    List<Definition> definitionDTOsToDefinitions(List<DefinitionProtocolDTO> definitionDTOs);

    default Release releaseFromId(Long id) {
        if (id == null) {
            return null;
        }
        Release release = new Release();
        release.setId(id);
        return release;
    }
}
