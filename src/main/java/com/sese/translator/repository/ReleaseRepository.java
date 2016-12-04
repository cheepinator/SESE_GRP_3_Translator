package com.sese.translator.repository;

import com.sese.translator.domain.Release;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Spring Data JPA repository for the Release entity.
 */
@SuppressWarnings("unused")
public interface ReleaseRepository extends JpaRepository<Release,Long> {

    @Query("select distinct release from Release release left join fetch release.languages")
    List<Release> findAllWithEagerRelationships();

    @Query("select release from Release release left join fetch release.languages where release.id =:id")
    Release findOneWithEagerRelationships(@Param("id") Long id);

//    @Query("SELECT release FROM Release release where release.project_id = :id")
//    Release findCurrentByProjectId(@Param("id") Long id);

    @Query("select count(*) from Translation translation where translation.definition.release.id = :id ")
    Integer countByReleaseId(@Param("id") Long id);

    @Query("select release from Release release where release.versionTag = :#{T(com.sese.translator.domain.Release).DEFAULT_TAG} and release.project.id = :id")
    Release findDefaultForProject(@Param("id") Long id);

}
