package io.resys.hdes.client.api.diff;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalDateTime;

@Value.Immutable
@JsonSerialize(as = ImmutableTagDiff.class)
@JsonDeserialize(as = ImmutableTagDiff.class)
public interface TagDiff {
  String getBaseName();
  String getTargetName();
  String getBaseId();
  String getTargetId();
  LocalDateTime getCreated();
  String getBody();
}
