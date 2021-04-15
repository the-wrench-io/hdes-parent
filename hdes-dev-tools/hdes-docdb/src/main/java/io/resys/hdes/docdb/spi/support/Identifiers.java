package io.resys.hdes.docdb.spi.support;

public class Identifiers {

  public static String toRepoHeadGid(String repoId, String headName) {
    return repoId + ":" + headName;
  }
}
