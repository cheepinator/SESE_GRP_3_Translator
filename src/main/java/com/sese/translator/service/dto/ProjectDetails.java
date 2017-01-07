package com.sese.translator.service.dto;

/**
 * Helper class for saving details about the progress and about the current release of a project.
 */
public class ProjectDetails {
    /**
     * The projectId.
     */
    private Long projectId;
    /**
     * The progress of the project (active release).
     */
    private double projectProgress;
    /**
     * The current release of the project.
     */
    private ReleaseDTO currentRelease;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(final Long projectId) {
        this.projectId = projectId;
    }

    public double getProjectProgress() {
        return projectProgress;
    }

    public void setProjectProgress(final double projectProgress) {
        this.projectProgress = projectProgress;
    }

    public ReleaseDTO getCurrentRelease() {
        return currentRelease;
    }

    public void setCurrentRelease(final ReleaseDTO currentRelease) {
        this.currentRelease = currentRelease;
    }
}
