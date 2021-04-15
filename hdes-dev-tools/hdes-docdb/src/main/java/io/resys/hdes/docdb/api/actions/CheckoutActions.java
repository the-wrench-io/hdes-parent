package io.resys.hdes.docdb.api.actions;

import java.time.LocalDateTime;

import org.immutables.value.Value;

import io.smallrye.mutiny.Multi;

public interface CheckoutActions {
  CommitCheckout commit();
  TagCheckout tag();
  HeadCheckout head();
  
  interface CommitCheckout {
    CommitCheckout repo(String repoId, String commitId);
    CommitCheckout gid(String commitGid);
    Multi<CheckoutResult> build();
  }
  
  interface HeadCheckout {
    HeadCheckout repo(String repoId, String headName);
    HeadCheckout gid(String headGid);
    Multi<CheckoutResult> build();
  }
  
  interface TagCheckout {
    TagCheckout repo(String repoId, String tagName);
    TagCheckout gid(String tagGid);
    Multi<CheckoutResult> build();
  }
  
  @Value.Immutable
  interface CheckoutResult {
    String getGid();
    String getRepo();
    String getCommit();
    LocalDateTime getModified();
    String getName();
    String getValue();
  }
}
