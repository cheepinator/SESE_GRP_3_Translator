package com.sese.translator.service.dto;

import com.sese.translator.service.dto.protocol.TranslationProtocolDTO;

import java.io.Serializable;
import java.util.List;


/**
 * A DTO for the Protocol entity.
 */
public class ProtocolDTO implements Serializable {

    private Long ProjectId;

    private List<TranslationProtocolDTO> translations;

    public Long getProjectId() {
        return ProjectId;
    }

    public void setProjectId(Long projectId) {
        ProjectId = projectId;
    }

    public List<TranslationProtocolDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(List<TranslationProtocolDTO> translations) {
        this.translations = translations;
    }
}
