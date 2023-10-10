package io.resys.hdes.client.api.ast;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableAstTagSummary.class)
@JsonDeserialize(as = ImmutableAstTagSummary.class)
public interface AstTagSummary extends Serializable {

  String getTagName();
  List<SummaryItem> getFlows();
  List<SummaryItem> getDecisions();
  List<SummaryItem> getServices();

  @Value.Immutable
  @JsonSerialize(as = ImmutableSummaryItem.class)
  @JsonDeserialize(as = ImmutableSummaryItem.class)
  interface SummaryItem {
    String getId();
    String getName();
    String getBody();
  }

}
