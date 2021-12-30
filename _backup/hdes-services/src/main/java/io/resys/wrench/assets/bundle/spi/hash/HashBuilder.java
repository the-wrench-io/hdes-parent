package io.resys.wrench.assets.bundle.spi.hash;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Vector;

import org.apache.commons.codec.digest.DigestUtils;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.AssetService;

public class HashBuilder {
  private final Vector<InputStream> streams = new Vector<>();

  public HashBuilder add(AssetService service) {
    //streams.add(new ByteArrayInputStream(service.getSrc().getBytes(StandardCharsets.UTF_8)));
    return this;
  }

  public String build() {
    SequenceInputStream seqStream = new SequenceInputStream(streams.elements());
    try {
      String md5Hash = DigestUtils.md5Hex(seqStream);
      seqStream.close();
      return md5Hash;
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
