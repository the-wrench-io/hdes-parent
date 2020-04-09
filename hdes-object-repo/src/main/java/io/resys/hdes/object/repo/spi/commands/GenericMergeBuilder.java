package io.resys.hdes.object.repo.spi.commands;

import java.util.Optional;
import java.util.function.Supplier;

import io.resys.hdes.object.repo.api.ObjectRepository;
import io.resys.hdes.object.repo.api.ObjectRepository.ChangeAction;
import io.resys.hdes.object.repo.api.ObjectRepository.Changes;
import io.resys.hdes.object.repo.api.ObjectRepository.CommitBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.MergeBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.Objects;
import io.resys.hdes.object.repo.api.ObjectRepository.RefStatus;
import io.resys.hdes.object.repo.api.ObjectRepository.Status;
import io.resys.hdes.object.repo.api.exceptions.CommitException;

public abstract class GenericMergeBuilder implements MergeBuilder {
  private final Objects objects;
  private final Supplier<CommitBuilder> commitBuilder;
  private String ref;
  private String author;

  public GenericMergeBuilder(Objects objects, Supplier<CommitBuilder> commitBuilder) {
    super();
    this.objects = objects;
    this.commitBuilder = commitBuilder;
  }

  @Override
  public MergeBuilder ref(String name) {
    this.ref = name;
    return this;
  }
  
  @Override
  public MergeBuilder author(String author) {
    this.author = author;
    return this;
  }

  @Override
  public Objects build() {
    Status status = new GenericStatusBuilder(objects).ref(ref).build();
    if(status.getEntries().isEmpty()) {
      throw new CommitException(CommitException.builder().nothingToMerge(ref));
    }
    
    RefStatus entry = status.getEntries().get(0);
    Optional<Changes> conflicts = entry.getChanges().stream().filter(c -> c.getAction() == ChangeAction.CONFLICT).findFirst();
    if(conflicts.isPresent()) {
      throw new CommitException(CommitException.builder().conflicts(ref));  
    }
    
    StringBuilder comment = new StringBuilder()
        .append("Merged from: ").append(ref)
        .append(", authors: ");
    
    StringBuilder authors = new StringBuilder();
    entry.getCommits().stream().forEach(c -> {
      if(authors.length() > 0) {
        authors.append(", ");  
      }
      authors.append(c.getAuthor());
    });
    
    CommitBuilder commitBuilder = this.commitBuilder.get()
        .comment(comment.append(authors).toString())
        .merge(entry.getCommits().get(0).getId())
        .author(author)
        .parent(objects.getRefs().get(ObjectRepository.MASTER).getCommit());
    for(Changes changes : entry.getChanges()) {
      switch (changes.getAction()) {
      case CREATED:
      case MODIFIED: commitBuilder.add(changes.getName(), changes.getNewValue().get()); break;
      case DELETED: commitBuilder.delete(changes.getName()); break;
      default: throw new CommitException(CommitException.builder().conflicts(ref, changes));  
      }
    }
    commitBuilder.build();
    return delete(entry);
  }
  
  protected abstract Objects delete(RefStatus ref);
}
