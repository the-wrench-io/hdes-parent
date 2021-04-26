package io.resys.hdes.docdb.api.models;

import org.immutables.value.Value;

@Value.Immutable
public interface Message {

  String getText();
}
