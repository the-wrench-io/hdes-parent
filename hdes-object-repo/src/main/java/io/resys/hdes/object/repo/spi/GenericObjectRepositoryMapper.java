package io.resys.hdes.object.repo.spi;

import java.util.function.Function;

import io.resys.hdes.object.repo.api.ObjectRepository.Objects;

public class GenericObjectRepositoryMapper implements ObjectRepositoryMapper {
   
  private final static IdSupplier ID_SUPPLIER = new Sha1IdSupplier();
  private final Function<Objects, Writer> writer;
  private final Function<Objects, Delete> delete;
  private final Serializer serializer;
  private final Deserializer deserializer;

  public GenericObjectRepositoryMapper(
      Serializer serializer,
      Deserializer deserializer,
      Function<Objects, Writer> writer,
      Function<Objects, Delete> delete) {
    super();
    this.writer = writer;
    this.delete = delete;
    this.serializer = serializer;
    this.deserializer = deserializer;
  }

  @Override
  public Serializer serializer() {
    return serializer;
  }

  @Override
  public Deserializer deserializer() {
    return deserializer;
  }

  @Override
  public Writer writer(Objects objects) {
    return writer.apply(objects);
  }
  
  @Override
  public IdSupplier id() {
    return ID_SUPPLIER;
  }

  @Override
  public Delete delete(Objects from) {
    return delete.apply(from);
  }
}
