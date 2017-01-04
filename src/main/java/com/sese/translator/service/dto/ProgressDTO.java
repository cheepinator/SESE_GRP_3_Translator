package com.sese.translator.service.dto;

import java.io.Serializable;

/**
 *
 */
public class ProgressDTO implements Serializable {

    private LanguageDTO language;

    private Long max;

    private Long current;

    public ProgressDTO(LanguageDTO language, Long max, Long current) {
        this.language = language;
        this.max = max;
        this.current = current;
    }

    public LanguageDTO getLanguage() {
        return language;
    }

    public void setLanguage(LanguageDTO language) {
        this.language = language;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgressDTO)) return false;

        ProgressDTO that = (ProgressDTO) o;

        if (language != null ? !language.equals(that.language) : that.language != null) return false;
        if (max != null ? !max.equals(that.max) : that.max != null) return false;
        return current != null ? current.equals(that.current) : that.current == null;
    }

    @Override
    public int hashCode() {
        int result = language != null ? language.hashCode() : 0;
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (current != null ? current.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProgressDTO{" +
            "language=" + language +
            ", max=" + max +
            ", current=" + current +
            '}';
    }
}
