package io.resys.hdes.docdb.spi;

import io.resys.hdes.docdb.api.models.Objects.Blob;
import io.resys.hdes.docdb.api.models.Objects.Commit;
import io.resys.hdes.docdb.api.models.Objects.Ref;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.resys.hdes.docdb.api.models.Repo;
import io.resys.hdes.docdb.spi.state.DocDBClientState;
import io.resys.hdes.docdb.spi.state.DocDBContext;

public class DocDBPrettyPrinter {
  private final DocDBClientState state;

  public DocDBPrettyPrinter(DocDBClientState state) {
    super();
    this.state = state;
  }
  
  public String print(Repo repo) {
   DocDBContext ctx = state.getContext().toRepo(repo);
    
    StringBuilder result = new StringBuilder();

    result
    .append(System.lineSeparator())
    .append("Repo").append(System.lineSeparator())
    .append("  - id: ").append(repo.getId())
    .append(", rev: ").append(repo.getRev()).append(System.lineSeparator())
    .append("    name: ").append(repo.getName())
    .append(", prefix: ").append(repo.getPrefix()).append(System.lineSeparator());
    
    result
    .append(System.lineSeparator())
    .append("Refs").append(System.lineSeparator());
    
    state.getClient().getDatabase(ctx.getDb())
    .getCollection(ctx.getRefs(), Ref.class)
    .find().onItem()
    .transform(item -> {
      result.append("  - ")
      .append(item.getCommit()).append(": ").append(item.getName())
      .append(System.lineSeparator());
      return item;
    }).collectItems().asList().await().indefinitely();
    
    result
    .append(System.lineSeparator())
    .append("Commits").append(System.lineSeparator());
    
    state.getClient().getDatabase(ctx.getDb())
    .getCollection(ctx.getCommits(), Commit.class)
    .find().onItem()
    .transform(item -> {
      result.append("  - id: ").append(item.getId())
      .append(System.lineSeparator())
      .append("    tree: ").append(item.getTree())
      .append(", dateTime: ").append(item.getDateTime())
      .append(", parent: ").append(item.getParent().orElse(""))
      .append(", message: ").append(item.getMessage())
      .append(", author: ").append(item.getAuthor())
      .append(System.lineSeparator());
      
      return item;
    }).collectItems().asList().await().indefinitely();
    
    
    result
    .append(System.lineSeparator())
    .append("Trees").append(System.lineSeparator());
    
    state.getClient().getDatabase(ctx.getDb())
    .getCollection(ctx.getTrees(), Tree.class)
    .find().onItem()
    .transform(item -> {
      result.append("  - id: ").append(item.getId()).append(System.lineSeparator());
      item.getValues().entrySet().forEach(e -> {
        result.append("    ")
          .append(e.getValue().getBlob())
          .append(": ")
          .append(e.getValue().getName())
          .append(System.lineSeparator());
      });
      
      return item;
    }).collectItems().asList().await().indefinitely();
    
    
    
    result
    .append(System.lineSeparator())
    .append("Blobs").append(System.lineSeparator());
    
    state.getClient().getDatabase(ctx.getDb())
    .getCollection(ctx.getBlobs(), Blob.class)
    .find().onItem()
    .transform(item -> {
      result.append("  - ").append(item.getId()).append(": ").append(item.getValue()).append(System.lineSeparator());
      return item;
    }).collectItems().asList().await().indefinitely();
    
    return result.toString();
  }
}
