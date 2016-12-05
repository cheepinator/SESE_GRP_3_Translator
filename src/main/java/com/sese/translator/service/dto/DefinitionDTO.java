package com.sese.translator.service.dto;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the Definition entity.
 */
public class DefinitionDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    @Lob
    private String originalText;

    private Long releaseId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public Long getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(Long releaseId) {
        this.releaseId = releaseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefinitionDTO definitionDTO = (DefinitionDTO) o;

        if ( ! Objects.equals(id, definitionDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DefinitionDTO{" +
            "id=" + id +
            ", code='" + code + "'" +
            ", originalText='" + originalText + "'" +
            '}';
    }
}
