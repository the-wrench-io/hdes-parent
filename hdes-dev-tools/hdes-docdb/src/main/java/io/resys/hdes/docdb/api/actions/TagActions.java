package io.resys.hdes.docdb.api.actions;

import java.util.List;

import org.immutables.value.Value;

import io.resys.hdes.docdb.api.models.Message;
import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface TagActions {

  TagBuilder create();
  TagQuery query();
  
  interface TagQuery {
    TagQuery repo(String repoId);
    TagQuery tagName(String tagName);
    Multi<Tag> find();
    Uni<Tag> get();
    Uni<Tag> delete();
  }
  
  interface TagBuilder {
    TagBuilder name(String name);
    TagBuilder repo(String repoId, String commitId);
    TagBuilder head(String headGid);
    
    TagBuilder author(String author);
    TagBuilder message(String message);    
    Uni<TagResult> build();
  }

  enum TagStatus {
    OK, CONFLICT
  }

  @Value.Immutable
  interface TagResult {    
    Tag getTag();
    TagStatus getStatus();
    List<Message> getMessages();
  }
}
