package io.resys.hdes.object.repo.spi.file;

import io.resys.hdes.object.repo.api.ObjectRepository.Commands;
import io.resys.hdes.object.repo.api.ObjectRepository.CommitBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.HistoryBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.PullBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.SnapshotBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.StatusBuilder;
import io.resys.hdes.object.repo.api.ObjectRepository.TagBuilder;

public class FileCommands implements Commands {

  @Override
  public PullBuilder pull() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public StatusBuilder status() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CommitBuilder commit() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SnapshotBuilder snapshot() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HistoryBuilder history() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TagBuilder tag() {
    // TODO Auto-generated method stub
    return null;
  }
}
