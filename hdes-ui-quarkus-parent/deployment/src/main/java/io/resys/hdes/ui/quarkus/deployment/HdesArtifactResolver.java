package io.resys.hdes.ui.quarkus.deployment;

import java.io.File;

/*-
 * #%L
 * hdes-ui-quarkus-deployment
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

import io.quarkus.deployment.index.ArtifactResolver;
import io.quarkus.deployment.index.ResolvedArtifact;
import io.resys.hdes.ui.quarkus.runtime.HdesUiRecorder;

public class HdesArtifactResolver implements ArtifactResolver {
  private static final String META_INF_MANIFEST_MF = "META-INF/MANIFEST.MF";
  private final List<StoredUrl> pathList = new ArrayList<>();

  public HdesArtifactResolver() {
    ClassLoader cl = HdesUiRecorder.class.getClassLoader();
    try {
      Enumeration<URL> res = cl.getResources(META_INF_MANIFEST_MF);
      while (res.hasMoreElements()) {
        URL jarUrl = res.nextElement();
        String path = jarUrl.getPath();
        if(!path.contains("hdes")) {
          continue;
        }
        
        // Local build
        
        if(path.startsWith(File.separator)) {
          Path jarPath = Path.of(path);
          Path classes = jarPath.getParent().getParent();
          if(!classes.endsWith("classes")) {
            continue;
          }
          
          Path target = classes.getParent();
          if(!target.endsWith("target")) {
            continue;
          }
          
          for(File file : target.toFile().listFiles((File dir, String name) -> name.endsWith(".jar"))) {
            path = "file:" + file.toPath().toAbsolutePath().toString();
          }
          pathList.add(new StoredUrl(Paths.get(new URI(path))));
          continue;
        }

        if (path.startsWith("file:")) {
          pathList.add(new StoredUrl(
              Paths.get(new URI(path.substring(0, path.length() - META_INF_MANIFEST_MF.length() - 2)))));
        }
      }
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ResolvedArtifact getArtifact(String groupId, String artifactId, String classifier) {
    String filePatten = artifactId + "-(\\d.*)\\.jar";
    Function<StoredUrl, ResolvedArtifact> resolver = createMavenResolver(groupId, artifactId);
    for (StoredUrl url : pathList) {
      if (url.fileName.matches(filePatten)) {
        ResolvedArtifact result = resolver.apply(url);
        if (result != null) {
          return result;
        }
      }
    }
    throw new RuntimeException("Could not resolve artifact " + groupId + ":" + artifactId + ":" + classifier);
  }

  public Function<StoredUrl, ResolvedArtifact> createMavenResolver(String groupId, String artifactId) {
    return (StoredUrl url) -> {
      String[] groupParts = groupId.split("\\.");
      if (url.path.getNameCount() < groupParts.length + 2) {
        return null;
      }
      boolean matches = true;
      
      if(!url.path.getParent().getFileName().getName(0).toString().equals("target")) {
        for (int i = 0; i < groupParts.length; ++i) {
          String up = url.path.getName(url.path.getNameCount() - groupParts.length - 3 + i).toString();
          if (!up.equals(groupParts[i])) {
            matches = false;
            break;
          }
        }
      }
      
      if (matches) {
        try {
          
          int start = url.fileName.lastIndexOf(artifactId) + artifactId.length() + 1;
          int end = url.fileName.lastIndexOf(".jar");
          String version = url.fileName.substring(start, end);
          
          return new ResolvedArtifact(groupId, artifactId, version, null, url.path);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      return null;
    };
  }

  static class StoredUrl {
    final Path path;
    final String fileName;

    private StoredUrl(Path path) {
      this.path = path;
      this.fileName = path.getFileName().toString();
    }

    @Override
    public String toString() {
      return "StoredUrl{" + "path=" + path + "}";
    }
  }
}
