package com.sese.translator.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Language.
 */
@Entity
@Table(name = "language")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Language implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_LANGUAGE = "De";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    @ManyToMany(mappedBy = "languages")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Release> releases = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public Language code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<Release> getReleases() {
        return releases;
    }

    public Language releases(Set<Release> releases) {
        this.releases = releases;
        return this;
    }

    public Language addReleases(Release release) {
        releases.add(release);
        release.getLanguages().add(this);
        return this;
    }

    public Language removeReleases(Release release) {
        releases.remove(release);
        release.getLanguages().remove(this);
        return this;
    }

    public void setReleases(Set<Release> releases) {
        this.releases = releases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Language language = (Language) o;
        if(language.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, language.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Language{" +
            "id=" + id +
            ", code='" + code + "'" +
            '}';
    }
}
