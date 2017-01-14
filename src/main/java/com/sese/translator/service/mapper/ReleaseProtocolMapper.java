package com.sese.translator.service.mapper;

import com.sese.translator.domain.Project;
import com.sese.translator.domain.Release;
import com.sese.translator.service.dto.protocol.ReleaseProtocolDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for the entity Release and its DTO ReleaseProtocolDTO.
 */
@Mapper(componentModel = "spring", uses = { })
public interface ReleaseProtocolMapper {

    @Mapping(source = "project.id", target = "projectId")
    ReleaseProtocolDTO releaseToReleaseProtocolDTO(Release release);

    List<ReleaseProtocolDTO> releasesToReleaseProtocolDTOs(List<Release> releases);

    @Mapping(target = "definitions", ignore = true)
    @Mapping(source = "projectId", target = "project")
    Release releaseDTOToRelease(ReleaseProtocolDTO releaseDTO);

    List<Release> releaseDTOsToReleases(List<ReleaseProtocolDTO> releaseDTOs);

    default Project projectFromId(Long id) {
        if (id == null) {
            return null;
        }
        Project project = new Project();
        project.setId(id);
        return project;
    }
}
