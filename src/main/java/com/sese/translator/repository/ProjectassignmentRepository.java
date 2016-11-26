package com.sese.translator.repository;

import com.sese.translator.domain.Projectassignment;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Projectassignment entity.
 */
@SuppressWarnings("unused")
public interface ProjectassignmentRepository extends JpaRepository<Projectassignment,Long> {

    @Query("select projectassignment from Projectassignment projectassignment where projectassignment.assignedUser.login = ?#{principal.username}")
    List<Projectassignment> findByAssignedUserIsCurrentUser();

}
