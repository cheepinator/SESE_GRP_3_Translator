package com.sese.translator.service.dto;

import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Release entity.
 */
public class ReleaseDTO implements Serializable {

    private Long id;

    private String description;

    @NotNull
    private String versionTag;

    @NotNull
    private Boolean isCurrentRelease;

    private ZonedDateTime dueDate;


    private Set<LanguageDTO> languages = new HashSet<>();

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
    public Boolean getIsCurrentRelease() {
        return isCurrentRelease;
    }

    public void setIsCurrentRelease(Boolean isCurrentRelease) {
        this.isCurrentRelease = isCurrentRelease;
    }
    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Set<LanguageDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<LanguageDTO> languages) {
        this.languages = languages;
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

        ReleaseDTO releaseDTO = (ReleaseDTO) o;

        if ( ! Objects.equals(id, releaseDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ReleaseDTO{" +
            "id=" + id +
            ", description='" + description + "'" +
            ", versionTag='" + versionTag + "'" +
            ", isCurrentRelease='" + isCurrentRelease + "'" +
            ", dueDate='" + dueDate + "'" +
            '}';
    }
}
