package com.sese.translator.service.dto.protocol;

import java.util.Objects;


/**
 * A DTO for the Translation entity with Protocols.
 */
public class TranslationProtocolDTO extends AbstractAuditingDTO{

    private Long id;


    private String translatedText;

    private Boolean updateNeeded;

    private Long translatorId;

    private Long languageId;

    private String languageCode;

    private Long definitionId;

    private String definitionCode;


    private String originalText;


    private String release;


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

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
    }

    public String getDefinitionCode() {
        return definitionCode;
    }

    public void setDefinitionCode(String definitionCode) {
        this.definitionCode = definitionCode;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
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

        TranslationProtocolDTO translationDTO = (TranslationProtocolDTO) o;

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
