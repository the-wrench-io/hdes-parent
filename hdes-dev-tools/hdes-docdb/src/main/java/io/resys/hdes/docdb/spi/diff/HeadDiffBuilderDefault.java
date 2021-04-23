package io.resys.hdes.docdb.spi.diff;

import java.util.Arrays;

import io.resys.hdes.docdb.api.actions.DiffActions.DiffResult;
import io.resys.hdes.docdb.api.actions.DiffActions.DiffStatus;
import io.resys.hdes.docdb.api.actions.DiffActions.HeadDiffBuilder;
import io.resys.hdes.docdb.api.actions.ImmutableDiffResult;
import io.resys.hdes.docdb.api.actions.ObjectsActions;
import io.resys.hdes.docdb.api.actions.ObjectsActions.CommitObjects;
import io.resys.hdes.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.resys.hdes.docdb.api.models.Diff;
import io.resys.hdes.docdb.api.models.Objects;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.resys.hdes.docdb.spi.support.RepoAssert;
import io.smallrye.mutiny.Uni;

public class HeadDiffBuilderDefault implements HeadDiffBuilder {
  private final DocDBClientState state;
  private final ObjectsActions objects;
  
  private String repoIdOrName;
  private String leftHeadOrCommitOrTag;
  private String rightHeadOrCommitOrTag;
  
  public HeadDiffBuilderDefault(DocDBClientState state, ObjectsActions objects) {
    super();
    this.state = state;
    this.objects = objects;
  }

  @Override
  public HeadDiffBuilder repo(String repoIdOrName) {
    this.repoIdOrName = repoIdOrName;
    return this;
  }
  @Override
  public HeadDiffBuilder left(String headOrCommitOrTag) {
    this.leftHeadOrCommitOrTag = headOrCommitOrTag;
    return this;
  }
  @Override
  public HeadDiffBuilder right(String headOrCommitOrTag) {
    this.rightHeadOrCommitOrTag = headOrCommitOrTag;
    return this;
  }
  @Override
  public Uni<DiffResult<Diff>> build() {
    RepoAssert.notEmpty(repoIdOrName, () -> "repoIdOrName is not defined!");
    RepoAssert.notEmpty(leftHeadOrCommitOrTag, () -> "leftHeadOrCommitOrTag is not defined!");
    RepoAssert.notEmpty(rightHeadOrCommitOrTag, () -> "rightHeadOrCommitOrTag is not defined!");
    
    return Uni.combine().all().unis(
        objects.repoState().repo(repoIdOrName).build(),
        objects.commitState().anyId(leftHeadOrCommitOrTag).repo(repoIdOrName).build(), 
        objects.commitState().anyId(rightHeadOrCommitOrTag).repo(repoIdOrName).build())

      .asTuple().onItem().transform(tuple -> {
        final var objects = tuple.getItem1();
        final var left = tuple.getItem2();
        final var right = tuple.getItem3();
        
        if(left.getStatus() != ObjectsStatus.OK || right.getStatus() != ObjectsStatus.OK) {
          return ImmutableDiffResult.<Diff>builder()
              .addAllMessages(left.getMessages())
              .addAllMessages(right.getMessages())
              .status(DiffStatus.ERROR)
              .build();
        }
        return createDiff(objects.getObjects(), left.getObjects(), right.getObjects());
      });
  }
  private DiffResult<Diff> createDiff(Objects objects, CommitObjects left, CommitObjects right) {
    final var diff = new DiffVisitor().visit(objects, left, Arrays.asList(right));
    return ImmutableDiffResult.<Diff>builder()
        .repo(left.getRepo())
        .objects(diff)
        .status(DiffStatus.OK)
        .build();
  }
}
