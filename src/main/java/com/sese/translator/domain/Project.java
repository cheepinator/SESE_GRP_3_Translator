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
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Project.
 */
@Entity
@Table(name = "project")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Cascade(CascadeType.DELETE)
    private Set<Release> releases = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "project_languages",
        joinColumns = @JoinColumn(name = "projects_id", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "languages_id", referencedColumnName = "ID"))
    private Set<Language> languages = new HashSet<>();

    @ManyToOne
    private User owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Project name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Release> getReleases() {
        return releases;
    }

    public Project releases(Set<Release> releases) {
        this.releases = releases;
        return this;
    }

    public Project addReleases(Release release) {
        releases.add(release);
        release.setProject(this);
        return this;
    }

    public Project removeReleases(Release release) {
        releases.remove(release);
        release.setProject(null);
        return this;
    }

    public void setReleases(Set<Release> releases) {
        this.releases = releases;
    }

    public User getOwner() {
        return owner;
    }

    public Project owner(User user) {
        this.owner = user;
        return this;
    }

    public void setOwner(User user) {
        this.owner = user;
    }


    public Set<Language> getLanguages() {
        return languages;
    }

    public Project languages(Set<Language> languages) {
        this.languages = languages;
        return this;
    }

    public Project addLanguages(Language language) {
        languages.add(language);
        language.getProjects().add(this);
        return this;
    }

    public Project removeLanguages(Language language) {
        languages.remove(language);
        language.getProjects().remove(this);
        return this;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Project project = (Project) o;
        if(project.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + id +
            ", name='" + name + "'" +
            '}';
    }
}
