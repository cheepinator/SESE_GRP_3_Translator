package com.sese.translator.service.dto.protocol;

import java.io.Serializable;
import java.time.ZonedDateTime;


/**
 * A DTO for the ProtocolEntry entity.
 */
public class ProtocolEntryDTO extends AbstractAuditingDTO implements Serializable {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ProtocolEntryDTO(String content, String createdBy, ZonedDateTime createdDate, String lastModifiedBy, ZonedDateTime lastModifiedDate) {
        this.content = content;
        this.setCreatedBy(createdBy);
        this.setCreatedDate(createdDate);
        this.setLastModifiedBy(lastModifiedBy);
        this.setLastModifiedDate(lastModifiedDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProtocolEntryDTO that = (ProtocolEntryDTO) o;

        return content != null ? content.equals(that.content) : that.content == null;

    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }
}
