package io.resys.hdes.object.repo.spi.commands;

import io.resys.hdes.object.repo.api.ImmutableTag;
import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.Commit;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Tag;
import io.resys.hdes.object.repo.api.ObjectRepository.TagBuilder;
import io.resys.hdes.object.repo.api.exceptions.TagException;
import io.resys.hdes.object.repo.spi.file.RepoAssert;

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
      throw new TagException(TagException.builder().duplicateTag(name));
    }
    
    if(objects.getHeads().containsKey(name)) {
      throw new TagException(TagException.builder().headNameMatch(name));
    }
    
    // tags can be created only from master
    Commit commit = CommitQuery.builder(objects).commit(this.commit).head(ObjectRepository.MASTER).get();
    return ImmutableTag.builder().name(name).commit(commit.getId()).build();
  }  
}
