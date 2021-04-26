package io.resys.hdes.docdb.spi.tags;

import java.time.LocalDateTime;

import io.resys.hdes.docdb.api.actions.ImmutableTagResult;
import io.resys.hdes.docdb.api.actions.TagActions.TagBuilder;
import io.resys.hdes.docdb.api.actions.TagActions.TagResult;
import io.resys.hdes.docdb.api.actions.TagActions.TagStatus;
import io.resys.hdes.docdb.api.models.ImmutableMessage;
import io.resys.hdes.docdb.api.models.ImmutableTag;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tag;
import io.resys.hdes.docdb.spi.ClientState;
import io.resys.hdes.docdb.spi.ClientState.ClientRepoState;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Uni;

public class CreateTagBuilder implements TagBuilder {
  private final ClientState state;
  
  private String repoId;
  private String commitIdOrHead;
  private String tagName;
  private String author;
  private String message;
  
  public CreateTagBuilder(ClientState state) {
    super();
    this.state = state;
  }
  @Override
  public TagBuilder tagName(String tagName) {
    this.tagName = tagName;
    return this;
  }
  @Override
  public TagBuilder repo(String repoId, String commitIdOrHead) {
    this.repoId = repoId;
    this.commitIdOrHead = commitIdOrHead;
    return this;
  }
  @Override
  public TagBuilder author(String author) {
    this.author = author;
    return this;
  }
  @Override
  public TagBuilder message(String message) {
    this.message = message;
    return this;
  }
  @Override
  public Uni<TagResult> build() {
    RepoAssert.notEmpty(author, () -> "author can't be empty!");
    RepoAssert.notEmpty(message, () -> "message can't be empty!");
    RepoAssert.notEmpty(repoId, () -> "repoId can't be empty!");
    RepoAssert.notEmpty(commitIdOrHead, () -> "commitIdOrHead can't be empty!");
    RepoAssert.notEmpty(tagName, () -> "tagName can't be empty!");
    
    return state.getRepo(repoId).onItem()
        .transformToUni(repo -> {
          if(repo == null) {
            return Uni.createFrom().item(ImmutableTagResult.builder()
                .status(TagStatus.ERROR)
                .addMessages(ImmutableMessage.builder()
                    .text(new StringBuilder()
                        .append("Can't create tag: '").append(tagName).append("'")
                        .append(" because there is no repository with id or name: '").append(repoId).append("'!")
                        .toString())
                    .build())
                .build());
          }
          
          final var ctx = this.state.withRepo(repo);
          return findRef(ctx, commitIdOrHead).onItem()
            .transformToUni(ref -> findCommit(ctx, ref == null ? commitIdOrHead : ref.getCommit())).onItem()
            .transformToUni(commit -> {
              
              if(commit == null) {
                return Uni.createFrom().item((TagResult) ImmutableTagResult.builder()
                    .status(TagStatus.ERROR)
                    .addMessages(ImmutableMessage.builder()
                        .text(new StringBuilder()
                            .append("Can't create tag: '").append(tagName).append("'")
                            .append(" because there is no commit or head: '").append(commitIdOrHead).append("'!")
                            .toString())
                        .build())
                    .build());
              }
              
              return findTag(ctx, tagName).onItem()
                .transformToUni(existingTag -> {
                  if(existingTag != null) {
                    return Uni.createFrom().item((TagResult) ImmutableTagResult.builder()
                        .status(TagStatus.ERROR)
                        .addMessages(ImmutableMessage.builder()
                            .text(new StringBuilder()
                                .append("Can't create tag: '").append(tagName).append("'")
                                .append(" because there is already tag with the same name with commit: '").append(existingTag.getCommit()).append("'!")
                                .toString())
                            .build())
                        .build());
                  }
                  return createTag(ctx, commit.getId());  
                });
            });
        });
  }

  private Uni<Tag> findTag(ClientRepoState state, String tagName) {
    return state.query().tags().name(tagName).get();
  }

  private Uni<Ref> findRef(ClientRepoState state, String refNameOrCommit) {
    return state.query().refs().nameOrCommit(refNameOrCommit);
  }
  
  private Uni<Commit> findCommit(ClientRepoState state, String commit) {
    return state.query().commits().id(commit);
  }
  
  private Uni<TagResult> createTag(ClientRepoState state, String commit) {
    final var tag = ImmutableTag.builder()
        .commit(commit)
        .name(tagName)
        .message(message)
        .author(author)
        .dateTime(LocalDateTime.now())
        .build();
    return state.insert()
        .tag(tag)
        .onItem().transform(inserted -> {
          if(inserted.getDuplicate()) {
            return (TagResult) ImmutableTagResult.builder()
                .status(TagStatus.ERROR)
                .tag(tag)
                .addMessages(ImmutableMessage.builder()
                    .text(new StringBuilder()
                        .append("Tag with name:")
                        .append(" '").append(tagName).append("'")
                        .append(" is already created.")
                        .toString())
                    .build())
                .build();
          } 
          return (TagResult) ImmutableTagResult.builder()
              .status(TagStatus.OK).tag(tag)
              .build();
        });
  }
}
