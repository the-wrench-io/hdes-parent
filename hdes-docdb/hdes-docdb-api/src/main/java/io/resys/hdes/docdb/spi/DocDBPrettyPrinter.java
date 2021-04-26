package io.resys.hdes.docdb.spi;

import io.resys.hdes.docdb.api.models.Diff;
import io.resys.hdes.docdb.api.models.Repo;

public class DocDBPrettyPrinter {
  private final ClientState state;

  public DocDBPrettyPrinter(ClientState state) {
    super();
    this.state = state;
  }
  
  public String print(Diff diff) {
    final var repo = diff.getRepo();
    
    StringBuilder result = new StringBuilder();
    
    result.append(System.lineSeparator())
      .append("Diff").append(System.lineSeparator())
      .append("  - id: ").append(repo.getId())
      .append(", rev: ").append(repo.getRev())
      .append(System.lineSeparator()).append(System.lineSeparator());
    
    result.append("Divergences").append(System.lineSeparator());
    for(var divergence : diff.getDivergences()) {
      result
        .append("  - head: ").append(divergence.getHead().getCommit().getId()).append(" alias: ").append(String.join(", ", divergence.getHead().getRefs())).append(System.lineSeparator())
        .append("    main: ").append(divergence.getMain().getCommit().getId()).append(" alias: ").append(String.join(", ", divergence.getMain().getRefs())).append(System.lineSeparator())
        .append("    commits in main: ").append(divergence.getMain().getCommits()).append(System.lineSeparator())
        .append("    commits in head: ").append(divergence.getHead().getCommits()).append(System.lineSeparator());
    }
    return result.toString();
  }
  
  public String print(Repo repo) {
   final var ctx = state.withRepo(repo);
    
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
    
    ctx.query().refs()
    .find().onItem()
    .transform(item -> {
      result.append("  - ")
      .append(item.getCommit()).append(": ").append(item.getName())
      .append(System.lineSeparator());
      return item;
    }).collectItems().asList().await().indefinitely();

    
    result
    .append(System.lineSeparator())
    .append("Tags").append(System.lineSeparator());
    
    ctx.query().tags()
    .find().onItem()
    .transform(item -> {
      result.append("  - id: ").append(item.getName())
      .append(System.lineSeparator())
      .append("    commit: ").append(item.getCommit())
      .append(", dateTime: ").append(item.getDateTime())
      .append(", message: ").append(item.getMessage())
      .append(", author: ").append(item.getAuthor())
      .append(System.lineSeparator());
      
      return item;
    }).collectItems().asList().await().indefinitely();
    
    result
    .append(System.lineSeparator())
    .append("Commits").append(System.lineSeparator());
    
    ctx.query().commits()
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
    
    ctx.query().trees()
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
    
    ctx.query().blobs()
    .find().onItem()
    .transform(item -> {
      result.append("  - ").append(item.getId()).append(": ").append(item.getValue()).append(System.lineSeparator());
      return item;
    }).collectItems().asList().await().indefinitely();
    
    return result.toString();
  }
}
