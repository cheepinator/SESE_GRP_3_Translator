package com.sese.translator.service.mapper;

import com.sese.translator.domain.Definition;
import com.sese.translator.domain.Language;
import com.sese.translator.domain.Translation;
import com.sese.translator.service.dto.protocol.TranslationProtocolDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for the entity Translation and its DTO TranslationDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, })
public interface TranslationProtocolMapper {

    @Mapping(source = "translator.id", target = "translatorId")
    @Mapping(source = "language.id", target = "languageId")
    @Mapping(source = "language.code", target = "languageCode")
    @Mapping(source = "definition.id", target = "definitionId")
    @Mapping(source = "definition.originalText", target = "originalText")
    @Mapping(source = "definition.code", target = "definitionCode")
    @Mapping(source = "definition.release.versionTag", target = "release")
    TranslationProtocolDTO translationToTranslationDTO(Translation translation);

    List<TranslationProtocolDTO> translationsToTranslationDTOs(List<Translation> translations);


    default Language languageFromId(Long id) {
        if (id == null) {
            return null;
        }
        Language language = new Language();
        language.setId(id);
        return language;
    }

    default Definition definitionFromId(Long id) {
        if (id == null) {
            return null;
        }
        Definition definition = new Definition();
        definition.setId(id);
        return definition;
    }
}
