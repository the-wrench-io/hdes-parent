package io.resys.hdes.spring.composer.controllers.util;

public class VersionEntity {

  String version;
  String timestamp;

  public VersionEntity(String version, String timestamp) {
    this.version = version;
    this.timestamp = timestamp;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}
