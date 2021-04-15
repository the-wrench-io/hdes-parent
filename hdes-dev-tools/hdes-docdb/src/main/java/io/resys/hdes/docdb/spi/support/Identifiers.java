package io.resys.hdes.docdb.spi.support;

import java.util.UUID;

public class Identifiers {

  public static String toRepoHeadGid(String repoId, String headName) {
    return repoId + ":" + headName;
  }
  
  public static String uuid() {
    return UUID.randomUUID().toString();
  }
}
