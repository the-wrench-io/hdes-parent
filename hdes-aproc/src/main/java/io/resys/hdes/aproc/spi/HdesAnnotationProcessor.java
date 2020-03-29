package io.resys.hdes.aproc.spi;

/*-
 * #%L
 * hdes-aproc
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import io.resys.hdes.aproc.spi.generator.FileGenerator;
import io.resys.hdes.aproc.spi.generator.MapperGenerator;
import io.resys.hdes.aproc.spi.generator.TagGenerator;
import io.resys.hdes.aproc.spi.generator.TagsGenerator;
import io.resys.hdes.aproc.spi.storage.StorageServiceFactory;
import io.resys.hdes.aproc.spi.tag.TagFactory;
import io.resys.hdes.aproc.spi.tag.TagFactory.TaggedChanges;
import io.resys.hdes.datatype.api.DataTypeService;
import io.resys.hdes.storage.api.StorageService;

@AutoService(Processor.class)
public class HdesAnnotationProcessor extends AbstractProcessor {
  private Types typeUtils;
  private Elements elementUtils;
  private Filer filer;
  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    typeUtils = processingEnv.getTypeUtils();
    elementUtils = processingEnv.getElementUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotataions = new LinkedHashSet<String>();
    annotataions.add(DataTypeService.DataTypeFactory.class.getCanonicalName());
    return annotataions;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement element : annotations) {
      final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(element);
      
      final Path path = getProjectPath();
      
      for (Element annotatedElement : annotatedElements) {
        StorageService storageService = StorageServiceFactory.builder()
            .path(path)
            .annotatedElement(annotatedElement).build();
        
        // master tag
        TagFactory.Builder tagBuilder = TagFactory.builder();
        storageService.changes().query().get().forEach(tagBuilder::add);
        
        // other tags
        final DataTypeService.DataTypeFactory annotation = annotatedElement.getAnnotation(DataTypeService.DataTypeFactory.class);
        for (String tag : annotation.tags()) {
          storageService.changes().query().tag(tag).get().forEach(c -> tagBuilder.add(tag, c));
        }
        final List<TaggedChanges> changes = tagBuilder.build();
        final Consumer<JavaFile> fileGenerator = FileGenerator.builder().filer(filer).messager(messager).build();
        
        // generate
        TagsGenerator.Builder tagsGenerator = TagsGenerator.builder(fileGenerator);
        changes.forEach(c -> {
          
          // create tag
          JavaFile tag = TagGenerator.builder(fileGenerator)
              .id(c.getTagId())
              .name(c.getTagName())
              .values(c.getValues())
              .build();
          
          // add tag
          tagsGenerator.add(tag);
          
        });
        
        
        tagsGenerator.build();
        
        // static mapper definitions
        MapperGenerator.builder(fileGenerator).build();
        
      }
    }
    return true;
  }

  private Path getProjectPath() {
    try {
      Filer filer = processingEnv.getFiler();
      FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "tmp-path-test", (Element[]) null);
      URI uri = resource.toUri();
      if(uri.toString().startsWith("mem:///")) {
        return Paths.get(new File("").getAbsolutePath());
      }
      
      Path projectPath = Paths.get(uri).getParent().getParent();
      resource.delete();
      return projectPath;
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
