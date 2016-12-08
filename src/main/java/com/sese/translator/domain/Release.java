package com.sese.translator.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Release.
 */
@Entity
@Table(name = "release")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Release implements Serializable {

    public static final String DEFAULT_TAG = "no release";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "version_tag", nullable = false)
    private String versionTag;

    @NotNull
    @Column(name = "is_current_release", nullable = false)
    private Boolean isCurrentRelease = Boolean.FALSE;

    @Column(name = "due_date")
    private ZonedDateTime dueDate;

    @OneToMany(mappedBy = "release")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Cascade(CascadeType.DELETE)
    private Set<Definition> definitions = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "release_languages",
        joinColumns = @JoinColumn(name = "releases_id", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "languages_id", referencedColumnName = "ID"))
    private Set<Language> languages = new HashSet<>();

    @NotNull
    @ManyToOne
    private Project project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public Release description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersionTag() {
        return versionTag;
    }

    public Release versionTag(String versionTag) {
        this.versionTag = versionTag;
        return this;
    }

    public void setVersionTag(String versionTag) {
        this.versionTag = versionTag;
    }

    public Boolean isIsCurrentRelease() {
        return isCurrentRelease;
    }

    public Release isCurrentRelease(Boolean isCurrentRelease) {
        this.isCurrentRelease = isCurrentRelease;
        return this;
    }

    public void setIsCurrentRelease(Boolean isCurrentRelease) {
        this.isCurrentRelease = isCurrentRelease;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public Release dueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Set<Definition> getDefinitions() {
        return definitions;
    }

    public Release definitions(Set<Definition> definitions) {
        this.definitions = definitions;
        return this;
    }

    public Release addDefinitions(Definition definition) {
        definitions.add(definition);
        definition.setRelease(this);
        return this;
    }

    public Release removeDefinitions(Definition definition) {
        definitions.remove(definition);
        definition.setRelease(null);
        return this;
    }

    public void setDefinitions(Set<Definition> definitions) {
        this.definitions = definitions;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public Release languages(Set<Language> languages) {
        this.languages = languages;
        return this;
    }

    public Release addLanguages(Language language) {
        languages.add(language);
        language.getReleases().add(this);
        return this;
    }

    public Release removeLanguages(Language language) {
        languages.remove(language);
        language.getReleases().remove(this);
        return this;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    public Project getProject() {
        return project;
    }

    public Release project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Release release = (Release) o;
        if (release.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, release.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Release{" +
            "id=" + id +
            ", description='" + description + "'" +
            ", versionTag='" + versionTag + "'" +
            ", isCurrentRelease='" + isCurrentRelease + "'" +
            ", dueDate='" + dueDate + "'" +
            '}';
    }
}
