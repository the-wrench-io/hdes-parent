package io.resys.hdes.docdb.tests;

import java.io.Serializable;
import java.time.Duration;

import org.immutables.value.Value;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;

import io.resys.hdes.docdb.api.models.ImmutableMessage;
import io.resys.hdes.docdb.api.models.ImmutableTree;
import io.resys.hdes.docdb.api.models.ImmutableTreeValue;
import io.resys.hdes.docdb.api.models.Objects.Tree;
import io.resys.hdes.docdb.spi.commit.ImmutableUpsertResult;
import io.resys.hdes.docdb.spi.commits.CommitSaveVisitor.UpsertResult;
import io.resys.hdes.docdb.spi.commits.CommitSaveVisitor.UpsertStatus;
import io.resys.hdes.docdb.tests.config.MongoDbConfig;


public class DbLogicTest extends MongoDbConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(DbLogicTest.class);
  
  @Value.Immutable
  public interface TestContent extends Serializable {
    String getId();
    String getName();
  }
 
  @Test
  public void randomTest() {
    final var state = createState();
    final var tree = ImmutableTree.builder()
        .id("xxx")
        .putValues("val1", ImmutableTreeValue.builder()
            .name("val1").blob("id1")
            .build())
        .build();
    
    
    UpsertResult result = state.getClient()
      .getDatabase(state.getContext().getDb())
      .getCollection(state.getContext().getTrees(), Tree.class)
      .insertOne(tree)
      .onItem()
      .transform(updateResult -> {
        final var modified = false; //updateResult.getModifiedCount() > 0;
        return ImmutableUpsertResult.builder()
          .id(tree.getId())
          .isModified(modified)
          .target(tree)
          .status(UpsertStatus.OK)
          .message(ImmutableMessage.builder()
              .text(new StringBuilder()
                  .append("Tree with id: '").append(tree.getId()).append("'")
                  
                  .toString())
              .build())
          .build();
      })
      .await().atMost(Duration.ofMinutes(1));
    System.out.println(result);
    
    result = state.getClient()
      .getDatabase(state.getContext().getDb())
      .getCollection(state.getContext().getTrees(), Tree.class)
      .insertOne(tree)
      .onItem()
      .transform(updateResult -> {
        final var modified = false; //updateResult.getModifiedCount() > 0;
        return ImmutableUpsertResult.builder()
          .id(tree.getId())
          .isModified(modified)
          .target(tree)
          .status(UpsertStatus.OK)
          .message(ImmutableMessage.builder()
              .text(new StringBuilder()
                  .append("Tree with id: '").append(tree.getId()).append("'")
                  
                  .toString())
              .build())
          .build();
      })
      .onFailure(e  -> {
        com.mongodb.MongoWriteException t = (MongoWriteException) e;
        return t.getError().getCategory() == ErrorCategory.DUPLICATE_KEY;
      })
      .recoverWithItem(e -> null)
      .await().atMost(Duration.ofMinutes(1));
    
    System.out.println(result);
  }
}
