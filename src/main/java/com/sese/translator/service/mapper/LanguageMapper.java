package com.sese.translator.service.mapper;

import com.sese.translator.domain.*;
import com.sese.translator.service.dto.LanguageDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Language and its DTO LanguageDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LanguageMapper {

    LanguageDTO languageToLanguageDTO(Language language);

    List<LanguageDTO> languagesToLanguageDTOs(List<Language> languages);

    @Mapping(target = "projects", ignore = true)
    Language languageDTOToLanguage(LanguageDTO languageDTO);

    List<Language> languageDTOsToLanguages(List<LanguageDTO> languageDTOs);
}
