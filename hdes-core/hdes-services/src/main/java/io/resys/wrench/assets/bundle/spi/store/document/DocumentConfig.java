package io.resys.wrench.assets.bundle.spi.store.document;

public class DocumentConfig {
  private String repoName;
  private String headName;
  
  public String getRepoName() {
    return repoName;
  }
  public DocumentConfig setRepoName(String repoName) {
    this.repoName = repoName;
    return this;
  }
  public String getHeadName() {
    return headName;
  }
  public DocumentConfig setHeadName(String headName) {
    this.headName = headName;
    return this;
  }
}
