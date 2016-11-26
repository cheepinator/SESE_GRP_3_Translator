package com.sese.translator.service.mapper;

import com.sese.translator.domain.*;
import com.sese.translator.service.dto.ProjectassignmentDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Projectassignment and its DTO ProjectassignmentDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, })
public interface ProjectassignmentMapper {

    @Mapping(source = "assignedUser.id", target = "assignedUserId")
    @Mapping(source = "assignedProject.id", target = "assignedProjectId")
    ProjectassignmentDTO projectassignmentToProjectassignmentDTO(Projectassignment projectassignment);

    List<ProjectassignmentDTO> projectassignmentsToProjectassignmentDTOs(List<Projectassignment> projectassignments);

    @Mapping(source = "assignedUserId", target = "assignedUser")
    @Mapping(source = "assignedProjectId", target = "assignedProject")
    Projectassignment projectassignmentDTOToProjectassignment(ProjectassignmentDTO projectassignmentDTO);

    List<Projectassignment> projectassignmentDTOsToProjectassignments(List<ProjectassignmentDTO> projectassignmentDTOs);

    default Project projectFromId(Long id) {
        if (id == null) {
            return null;
        }
        Project project = new Project();
        project.setId(id);
        return project;
    }
}
