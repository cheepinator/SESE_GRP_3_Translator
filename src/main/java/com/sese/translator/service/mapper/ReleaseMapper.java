package com.sese.translator.service.mapper;

import com.sese.translator.domain.*;
import com.sese.translator.service.dto.ReleaseDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Release and its DTO ReleaseDTO.
 */
@Mapper(componentModel = "spring", uses = { })
public interface ReleaseMapper {

    @Mapping(source = "project.id", target = "projectId")
    ReleaseDTO releaseToReleaseDTO(Release release);

    List<ReleaseDTO> releasesToReleaseDTOs(List<Release> releases);

    @Mapping(target = "definitions", ignore = true)
    @Mapping(source = "projectId", target = "project")
    Release releaseDTOToRelease(ReleaseDTO releaseDTO);

    List<Release> releaseDTOsToReleases(List<ReleaseDTO> releaseDTOs);

    default Project projectFromId(Long id) {
        if (id == null) {
            return null;
        }
        Project project = new Project();
        project.setId(id);
        return project;
    }
}
