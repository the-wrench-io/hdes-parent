package io.resys.wrench.assets.bundle.spi.hash;

/*-
 * #%L
 * wrench-assets-bundle
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

import org.apache.commons.codec.digest.DigestUtils;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;

public class HashBuilder {
  private final Vector<InputStream> streams = new Vector<>();

  public HashBuilder add(Service service) {
    streams.add(new ByteArrayInputStream(service.getSrc().getBytes(StandardCharsets.UTF_8)));
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
