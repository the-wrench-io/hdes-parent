package io.resys.hdes.projdb.spi.context;

import java.util.function.Function;

@FunctionalInterface
public interface DBCommand {
  <T> T accept(Function<DBContext, T> action);
}
