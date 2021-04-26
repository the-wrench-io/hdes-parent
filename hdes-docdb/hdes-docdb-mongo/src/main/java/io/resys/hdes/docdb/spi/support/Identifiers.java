package io.resys.hdes.docdb.spi.support;

import java.util.UUID;

public class Identifiers {
  
  public static String toRepoHeadGid(String repoId, String headName) {
    return repoId + ":" + headName;
  }
  
  // 0 - repo id, 1 - head name
  public static String[] fromRepoHeadGid(String headGid) {
    String[] path = headGid.split(":");
    RepoAssert.isTrue(path.length == 2, () -> "Invalid headGid: '" + headGid + "'!");
    return path;
  }
  
  public static String uuid() {
    return UUID.randomUUID().toString();
  }
}
