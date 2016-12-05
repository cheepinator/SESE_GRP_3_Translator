package com.sese.translator.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;


/**
 * A DTO for the Translation entity.
 */
public class TranslationDTO implements Serializable {

    private Long id;

    @Lob
    private String translatedText;

    @NotNull
    private Boolean updateNeeded;


    private Long translatorId;
    
    private Long languageId;
    
    private Long definitionId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
    public Boolean getUpdateNeeded() {
        return updateNeeded;
    }

    public void setUpdateNeeded(Boolean updateNeeded) {
        this.updateNeeded = updateNeeded;
    }

    public Long getTranslatorId() {
        return translatorId;
    }

    public void setTranslatorId(Long userId) {
        this.translatorId = userId;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public Long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TranslationDTO translationDTO = (TranslationDTO) o;

        if ( ! Objects.equals(id, translationDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TranslationDTO{" +
            "id=" + id +
            ", translatedText='" + translatedText + "'" +
            ", updateNeeded='" + updateNeeded + "'" +
            '}';
    }
}
