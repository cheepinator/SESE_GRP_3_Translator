package com.sese.translator.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Translation.
 */
@Entity
@Table(name = "translation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Translation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Column(name = "translated_text")
    private String translatedText;

    @NotNull
    @Column(name = "update_needed", nullable = false)
    private Boolean updateNeeded;

    @ManyToOne
    private User translator;

    @ManyToOne
    private Language language;

    @ManyToOne
    private Definition definition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public Translation translatedText(String translatedText) {
        this.translatedText = translatedText;
        return this;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public Boolean isUpdateNeeded() {
        return updateNeeded;
    }

    public Translation updateNeeded(Boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
        return this;
    }

    public void setUpdateNeeded(Boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    public User getTranslator() {
        return translator;
    }

    public Translation translator(User user) {
        this.translator = user;
        return this;
    }

    public void setTranslator(User user) {
        this.translator = user;
    }

    public Language getLanguage() {
        return language;
    }

    public Translation language(Language language) {
        this.language = language;
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Definition getDefinition() {
        return definition;
    }

    public Translation definition(Definition definition) {
        this.definition = definition;
        return this;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Translation translation = (Translation) o;
        if(translation.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, translation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Translation{" +
            "id=" + id +
            ", translatedText='" + translatedText + "'" +
            ", updateNeeded='" + updateNeeded + "'" +
            '}';
    }
}
