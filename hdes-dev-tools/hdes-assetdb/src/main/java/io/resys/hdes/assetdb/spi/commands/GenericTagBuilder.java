package io.resys.hdes.assetdb.spi.commands;

import io.resys.hdes.assetdb.api.AssetClient;
import io.resys.hdes.assetdb.api.AssetClient.Commit;
import io.resys.hdes.assetdb.api.AssetClient.Objects;
import io.resys.hdes.assetdb.api.AssetClient.Tag;
import io.resys.hdes.assetdb.api.AssetClient.TagBuilder;
import io.resys.hdes.assetdb.api.ImmutableTag;
import io.resys.hdes.assetdb.api.exceptions.RefException;
import io.resys.hdes.assetdb.spi.RepoAssert;

public class GenericTagBuilder implements TagBuilder {

  private final Objects objects;
  private String name;
  private String commit;
  
  public GenericTagBuilder(Objects objects) {
    super();
    this.objects = objects;
  }  
  @Override
  public TagBuilder name(String name) {
    this.name = name;
    return this;
  }
  @Override
  public TagBuilder commit(String commit) {
    this.commit = commit;
    return this;
  }
  @Override
  public Tag build() {
    RepoAssert.notNull(name, () -> "name can't be null!");
    
    if(objects.getTags().containsKey(name)) {
      throw new RefException(RefException.builder().duplicateTag(name));
    }
    
    if(objects.getRefs().containsKey(name)) {
      throw new RefException(RefException.builder().refNameMatch(name));
    }
    
    // tags can be created only from master
    Commit commit = CommitQuery.builder(objects).commit(this.commit).ref(AssetClient.MASTER).get();
    return ImmutableTag.builder().name(name).commit(commit.getId()).build();
  }  
}
