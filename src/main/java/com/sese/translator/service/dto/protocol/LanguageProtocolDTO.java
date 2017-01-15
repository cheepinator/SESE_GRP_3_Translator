package com.sese.translator.service.dto.protocol;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the Language entity.
 */
public class LanguageProtocolDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LanguageProtocolDTO languageDTO = (LanguageProtocolDTO) o;

        if ( ! Objects.equals(id, languageDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Language: " +
            "id=" + id +
            ", code='" + code + "'";
    }
}
