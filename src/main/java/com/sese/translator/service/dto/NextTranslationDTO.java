package com.sese.translator.service.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * A DTO used for queriing the next definition with its translation for the translation view
 */
public class NextTranslationDTO implements Serializable {

    private Long releaseId;

    @NotNull
    private Long languageId;


    public Long getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(Long releaseId) {
        this.releaseId = releaseId;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NextTranslationDTO that = (NextTranslationDTO) o;

        if (releaseId != null ? !releaseId.equals(that.releaseId) : that.releaseId != null) return false;
        return languageId != null ? languageId.equals(that.languageId) : that.languageId == null;

    }

    @Override
    public int hashCode() {
        int result = releaseId != null ? releaseId.hashCode() : 0;
        result = 31 * result + (languageId != null ? languageId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NextTranslationDTO{" +
            "releaseId=" + releaseId +
            ", languageId=" + languageId +
            '}';
    }
}
