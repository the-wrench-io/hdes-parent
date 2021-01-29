package io.resys.hdes.projects.quarkus.deployment;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = HdesProjectsProcessor.FEATURE_BUILD_ITEM)
public class HdesProjectsConfig {
  /**
   * projects UI path, anything except '/'
   */
  @ConfigItem(defaultValue = "/hdes/projects-ui")
  String frontendPath;

  /**
   * projects services path, anything except '/'
   */
  @ConfigItem(defaultValue = "/hdes/projects-services")
  String backendPath;
  
  /**
   * Mongo DB connection URL
   */
  @ConfigItem
  String connectionUrl;
}