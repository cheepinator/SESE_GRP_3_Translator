package com.sese.translator.service.dto.protocol;

import com.sese.translator.domain.enumeration.Projectrole;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Projectassignment entity.
 */
public class ProjectassignmentProtocolDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    @NotNull
    private Projectrole role;

    @NotNull
    private Long assignedUserId;

    @NotNull
    private Long assignedProjectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Projectrole getRole() {
        return role;
    }

    public void setRole(Projectrole role) {
        this.role = role;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long userId) {
        this.assignedUserId = userId;
    }

    public Long getAssignedProjectId() {
        return assignedProjectId;
    }

    public void setAssignedProjectId(Long projectId) {
        this.assignedProjectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProjectassignmentProtocolDTO projectassignmentDTO = (ProjectassignmentProtocolDTO) o;

        if ( ! Objects.equals(id, projectassignmentDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProjectassignmentDTO{" +
            "id=" + id +
            ", role='" + role + "'" +
            '}';
    }
}
