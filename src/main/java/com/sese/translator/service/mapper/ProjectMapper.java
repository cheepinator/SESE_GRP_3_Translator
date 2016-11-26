package com.sese.translator.service.mapper;

import com.sese.translator.domain.*;
import com.sese.translator.service.dto.ProjectDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Project and its DTO ProjectDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, })
public interface ProjectMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    ProjectDTO projectToProjectDTO(Project project);

    List<ProjectDTO> projectsToProjectDTOs(List<Project> projects);

    @Mapping(target = "releases", ignore = true)
    @Mapping(source = "ownerId", target = "owner")
    Project projectDTOToProject(ProjectDTO projectDTO);

    List<Project> projectDTOsToProjects(List<ProjectDTO> projectDTOs);
}
