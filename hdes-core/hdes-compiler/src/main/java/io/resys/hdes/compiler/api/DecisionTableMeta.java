package io.resys.hdes.compiler.api;

import java.io.Serializable;
import java.util.Map;

import org.immutables.value.Value;

import io.resys.hdes.compiler.api.HdesExecutable.MetaToken;


@Value.Immutable
public interface DecisionTableMeta extends HdesExecutable.Meta {
  
  long getTime();
  
  Map<Integer, DecisionTableMetaEntry> getValues();

  @Value.Immutable
  interface DecisionTableMetaEntry extends Serializable {
    int getId();
    int getIndex();
    MetaToken getToken();
  }
}
