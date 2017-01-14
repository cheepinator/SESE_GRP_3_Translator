package com.sese.translator.service.mapper;

import com.sese.translator.domain.Project;
import com.sese.translator.domain.Projectassignment;
import com.sese.translator.service.dto.protocol.ProjectassignmentProtocolDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for the entity Projectassignment and its DTO ProjectassignmentProtocolDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, })
public interface ProjectassignmentProtocolMapper {

    @Mapping(source = "assignedUser.id", target = "assignedUserId")
    @Mapping(source = "assignedProject.id", target = "assignedProjectId")
    ProjectassignmentProtocolDTO projectassignmentToProjectassignmentProtocolDTO(Projectassignment projectassignment);

    List<ProjectassignmentProtocolDTO> projectassignmentsToProjectassignmentProtocolDTOs(List<Projectassignment> projectassignments);

    @Mapping(source = "assignedUserId", target = "assignedUser")
    @Mapping(source = "assignedProjectId", target = "assignedProject")
    Projectassignment projectassignmentDTOToProjectassignment(ProjectassignmentProtocolDTO projectassignmentDTO);

    List<Projectassignment> projectassignmentDTOsToProjectassignments(List<ProjectassignmentProtocolDTO> projectassignmentDTOs);

    default Project projectFromId(Long id) {
        if (id == null) {
            return null;
        }
        Project project = new Project();
        project.setId(id);
        return project;
    }
}
