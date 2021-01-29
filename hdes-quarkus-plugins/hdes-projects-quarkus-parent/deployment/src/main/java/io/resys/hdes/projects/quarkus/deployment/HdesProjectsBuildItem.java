package io.resys.hdes.projects.quarkus.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

public final class HdesProjectsBuildItem extends SimpleBuildItem {
  private final String projectsUiFinalDestination;
  private final String projectsUiPath;

  public HdesProjectsBuildItem(String projectsUiFinalDestination, String projectsUiPath) {
    super();
    this.projectsUiFinalDestination = projectsUiFinalDestination;
    this.projectsUiPath = projectsUiPath;
  }

  public String getUiFinalDestination() {
      return projectsUiFinalDestination;
  }

  public String getUiPath() {
      return projectsUiPath;
  }

}
