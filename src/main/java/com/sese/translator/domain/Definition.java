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
 * A Definition.
 */
@Entity
@Table(name = "definition")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Definition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    @NotNull
    @Lob
    @Column(name = "original_text", nullable = false)
    private String originalText;

    @OneToMany(mappedBy = "definition")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Translation> translations = new HashSet<>();

    @ManyToOne
    private Release release;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public Definition code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOriginalText() {
        return originalText;
    }

    public Definition originalText(String originalText) {
        this.originalText = originalText;
        return this;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public Set<Translation> getTranslations() {
        return translations;
    }

    public Definition translations(Set<Translation> translations) {
        this.translations = translations;
        return this;
    }

    public Definition addTranslations(Translation translation) {
        translations.add(translation);
        translation.setDefinition(this);
        return this;
    }

    public Definition removeTranslations(Translation translation) {
        translations.remove(translation);
        translation.setDefinition(null);
        return this;
    }

    public void setTranslations(Set<Translation> translations) {
        this.translations = translations;
    }

    public Release getRelease() {
        return release;
    }

    public Definition release(Release release) {
        this.release = release;
        return this;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Definition definition = (Definition) o;
        if(definition.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, definition.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Definition{" +
            "id=" + id +
            ", code='" + code + "'" +
            ", originalText='" + originalText + "'" +
            '}';
    }
}
