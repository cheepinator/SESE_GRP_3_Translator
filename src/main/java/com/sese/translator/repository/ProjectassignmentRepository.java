package com.sese.translator.repository;

import com.sese.translator.domain.Project;
import com.sese.translator.domain.Projectassignment;
import com.sese.translator.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Projectassignment entity.
 */
@SuppressWarnings("unused")
public interface ProjectassignmentRepository extends JpaRepository<Projectassignment, Long> {

    @Query("select projectassignment from Projectassignment projectassignment where projectassignment.assignedUser.login = ?#{principal.username}")
    List<Projectassignment> findByAssignedUserIsCurrentUser();

    @Query("SELECT pa FROM Projectassignment pa WHERE pa.assignedProject.owner.login = ?#{principal.username}")
    List<Projectassignment> findByAssignedProjectBelongToCurrentUser();

    List<Projectassignment> findByAssignedProject(Project assignedProject);

    List<Projectassignment> findByAssignedProjectId(@Param("id") Long projectId);

    List<Projectassignment> findByAssignedUser(User user);

    List<Projectassignment> findByAssignedUserAndAssignedProjectId(User user, Long projectId);

    List<Projectassignment> deleteByAssignedProjectId(@Param("id") Long projectId);
}
