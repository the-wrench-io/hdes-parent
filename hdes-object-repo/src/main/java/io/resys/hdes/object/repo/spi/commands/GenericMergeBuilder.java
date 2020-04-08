package io.resys.hdes.object.repo.spi.commands;

import java.util.Optional;
import java.util.function.Supplier;

import io.resys.hdes.object.repo.api.ObjectRepository.ChangeAction;
import io.resys.hdes.object.repo.api.ObjectRepository.Changes;
import io.resys.hdes.object.repo.api.ObjectRepository.CommitBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.HeadStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.MergeBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.Status;
import io.resys.hdes.object.repo.api.exceptions.CommitException;

public abstract class GenericMergeBuilder implements MergeBuilder {
  private final Objects objects;
  private final Supplier<CommitBuilder> commitBuilder;
  private String head;

  public GenericMergeBuilder(Objects objects, Supplier<CommitBuilder> commitBuilder) {
    super();
    this.objects = objects;
    this.commitBuilder = commitBuilder;
  }

  @Override
  public MergeBuilder head(String name) {
    this.head = name;
    return this;
  }

  @Override
  public Objects build() {
    Status status = new GenericStatusBuilder(objects).head(head).build();
    if(status.getEntries().isEmpty()) {
      throw new CommitException(CommitException.builder().nothingToMerge(head));
    }
    
    HeadStatus entry = status.getEntries().get(0);
    Optional<Changes> conflicts = entry.getChanges().stream().filter(c -> c.getAction() == ChangeAction.CONFLICT).findFirst();
    if(conflicts.isPresent()) {
      throw new CommitException(CommitException.builder().conflicts(head));  
    }
    CommitBuilder commitBuilder = this.commitBuilder.get().comment("merge: " + head);
    for(Changes changes : entry.getChanges()) {
      switch (changes.getAction()) {
      case CREATED:
      case MODIFIED: commitBuilder.add(changes.getName(), changes.getNewValue().get()); break;
      case DELETED: commitBuilder.delete(changes.getName()); break;
      default: throw new CommitException(CommitException.builder().conflicts(head, changes));  
      }
    }
    
    return delete(commitBuilder.build(), entry);
  }
  
  protected abstract Objects delete(Objects objects, HeadStatus head);
}
