package com.sese.translator.service.mapper;

import com.sese.translator.domain.Language;
import com.sese.translator.service.dto.protocol.LanguageProtocolDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for the entity Language and its DTO LanguageProtocolDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LanguageProtocolMapper {

    LanguageProtocolDTO languageToLanguageProtocolDTO(Language language);

    List<LanguageProtocolDTO> languagesToLanguageProtocolDTOs(List<Language> languages);

    @Mapping(target = "projects", ignore = true)
    Language languageDTOToLanguage(LanguageProtocolDTO languageDTO);

    List<Language> languageDTOsToLanguages(List<LanguageProtocolDTO> languageDTOs);
}
