package com.sese.translator.repository;

import com.sese.translator.domain.Definition;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Definition entity.
 */
@SuppressWarnings("unused")
public interface DefinitionRepository extends JpaRepository<Definition,Long> {

}
