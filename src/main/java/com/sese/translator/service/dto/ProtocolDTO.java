package com.sese.translator.service.dto;

import com.sese.translator.service.dto.protocol.*;

import java.io.Serializable;
import java.util.List;


/**
 * A DTO for the Protocol entity.
 */
public class ProtocolDTO implements Serializable {

    private Long ProjectId;

    private List<TranslationProtocolDTO> translations;

    private List<DefinitionProtocolDTO> definitions;

    private List<LanguageProtocolDTO> languages;

    private List<ProjectassignmentProtocolDTO> projectassignments;

    private List<ReleaseProtocolDTO> releases;

    private List<UserProtocolDTO> users;

    public Long getProjectId() {
        return ProjectId;
    }

    public void setProjectId(Long projectId) {
        ProjectId = projectId;
    }

    public List<TranslationProtocolDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(List<TranslationProtocolDTO> translations) {
        this.translations = translations;
    }


    public List<DefinitionProtocolDTO> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<DefinitionProtocolDTO> definitions) {
        this.definitions = definitions;
    }

    public List<LanguageProtocolDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageProtocolDTO> languages) {
        this.languages = languages;
    }

    public List<ProjectassignmentProtocolDTO> getProjectassignments() {
        return projectassignments;
    }

    public void setProjectassignments(List<ProjectassignmentProtocolDTO> projectassignments) {
        this.projectassignments = projectassignments;
    }

    public List<ReleaseProtocolDTO> getReleases() {
        return releases;
    }

    public void setReleases(List<ReleaseProtocolDTO> releases) {
        this.releases = releases;
    }

    public List<UserProtocolDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserProtocolDTO> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProtocolDTO that = (ProtocolDTO) o;

        if (ProjectId != null ? !ProjectId.equals(that.ProjectId) : that.ProjectId != null) return false;
        if (translations != null ? !translations.equals(that.translations) : that.translations != null) return false;
        if (definitions != null ? !definitions.equals(that.definitions) : that.definitions != null) return false;
        if (languages != null ? !languages.equals(that.languages) : that.languages != null) return false;
        if (projectassignments != null ? !projectassignments.equals(that.projectassignments) : that.projectassignments != null)
            return false;
        if (releases != null ? !releases.equals(that.releases) : that.releases != null) return false;
        return users != null ? users.equals(that.users) : that.users == null;

    }

    @Override
    public int hashCode() {
        int result = ProjectId != null ? ProjectId.hashCode() : 0;
        result = 31 * result + (translations != null ? translations.hashCode() : 0);
        result = 31 * result + (definitions != null ? definitions.hashCode() : 0);
        result = 31 * result + (languages != null ? languages.hashCode() : 0);
        result = 31 * result + (projectassignments != null ? projectassignments.hashCode() : 0);
        result = 31 * result + (releases != null ? releases.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        return result;
    }
}
