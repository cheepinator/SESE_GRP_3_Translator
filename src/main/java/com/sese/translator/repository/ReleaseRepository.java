package com.sese.translator.repository;

import com.sese.translator.domain.Project;
import com.sese.translator.domain.Release;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Release entity.
 */
@SuppressWarnings("unused")
public interface ReleaseRepository extends JpaRepository<Release, Long> {

    @Query("select release from Release release")
    List<Release> findAllWithEagerRelationships();

    @Query("select release from Release release where release.id =:id")
    Release findOneWithEagerRelationships(@Param("id") Long id);

    @Query("SELECT release FROM Release release where release.project = project")
    List<Release> findReleasesByProject(@Param("project") Project project);

    @Query("select count(*) from Translation translation where translation.definition.release.id = :id ")
    Integer countByReleaseId(@Param("id") Long id);

    @Query("select release from Release release where release.versionTag = :#{T(com.sese.translator.domain.Release).DEFAULT_TAG} and release.project.id = :id")
    Release findDefaultForProject(@Param("id") Long id);

    @Query("select distinct release from Release release where release.project.id = :projectId")
    List<Release> findByProjectIdWithEagerRelationships(@Param("projectId") Long projectId);

//    @Query("select release from Release release join release.project where project.owner.login = ?#{principal.username}")
//    List<Release> findByOwnerIsCurrentUser();

    @Query("select release from Release release where release.project.owner.login = ?#{principal.username} ")
    List<Release> findByOwnerIsCurrentUser();

}
