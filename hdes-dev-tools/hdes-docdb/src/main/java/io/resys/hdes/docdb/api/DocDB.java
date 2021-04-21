package io.resys.hdes.docdb.api;

import io.resys.hdes.docdb.api.actions.CheckoutActions;
import io.resys.hdes.docdb.api.actions.CommitActions;
import io.resys.hdes.docdb.api.actions.DiffActions;
import io.resys.hdes.docdb.api.actions.HistoryActions;
import io.resys.hdes.docdb.api.actions.ObjectsActions;
import io.resys.hdes.docdb.api.actions.RepoActions;
import io.resys.hdes.docdb.api.actions.TagActions;

public interface DocDB {
  RepoActions repo();
  CommitActions commit();
  TagActions tag();
  DiffActions diff();
  CheckoutActions checkout();
  HistoryActions history();
  ObjectsActions objects();
}
