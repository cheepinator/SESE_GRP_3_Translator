package com.sese.translator.service.dto.protocol;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;


/**
 * A DTO for the Release entity.
 */
public class ReleaseProtocolDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    private String description;

    @NotNull
    private String versionTag;

    private ZonedDateTime dueDate;

    @NotNull
    private Long projectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getVersionTag() {
        return versionTag;
    }

    public void setVersionTag(String versionTag) {
        this.versionTag = versionTag;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReleaseProtocolDTO releaseDTO = (ReleaseProtocolDTO) o;

        if ( ! Objects.equals(id, releaseDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Release: " +
            "id=" + id +
            ", description='" + description + "'" +
            ", versionTag='" + versionTag + "'" +
            ", dueDate='" + dueDate + "'" ;
    }
}
