package com.sese.translator.service.mapper;

import com.sese.translator.domain.*;
import com.sese.translator.service.dto.DefinitionDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Definition and its DTO DefinitionDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DefinitionMapper {

    @Mapping(source = "release.id", target = "releaseId")
    DefinitionDTO definitionToDefinitionDTO(Definition definition);

    List<DefinitionDTO> definitionsToDefinitionDTOs(List<Definition> definitions);

    @Mapping(target = "translations", ignore = true)
    @Mapping(source = "releaseId", target = "release")
    Definition definitionDTOToDefinition(DefinitionDTO definitionDTO);

    List<Definition> definitionDTOsToDefinitions(List<DefinitionDTO> definitionDTOs);

    default Release releaseFromId(Long id) {
        if (id == null) {
            return null;
        }
        Release release = new Release();
        release.setId(id);
        return release;
    }
}
