package com.sese.translator.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

import com.sese.translator.domain.enumeration.Projectrole;

/**
 * A Projectassignment.
 */
@Entity
@Table(name = "projectassignment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Projectassignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Projectrole role;

    @ManyToOne
    private User assignedUser;

    @ManyToOne
    private Project assignedProject;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Projectrole getRole() {
        return role;
    }

    public Projectassignment role(Projectrole role) {
        this.role = role;
        return this;
    }

    public void setRole(Projectrole role) {
        this.role = role;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public Projectassignment assignedUser(User user) {
        this.assignedUser = user;
        return this;
    }

    public void setAssignedUser(User user) {
        this.assignedUser = user;
    }

    public Project getAssignedProject() {
        return assignedProject;
    }

    public Projectassignment assignedProject(Project project) {
        this.assignedProject = project;
        return this;
    }

    public void setAssignedProject(Project project) {
        this.assignedProject = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Projectassignment projectassignment = (Projectassignment) o;
        if(projectassignment.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, projectassignment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Projectassignment{" +
            "id=" + id +
            ", role='" + role + "'" +
            '}';
    }
}
