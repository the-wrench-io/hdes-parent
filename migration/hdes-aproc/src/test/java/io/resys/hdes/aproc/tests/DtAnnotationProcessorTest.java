package io.resys.hdes.aproc.tests;

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

import static com.google.testing.compile.Compiler.javac;

import java.util.List;

import javax.tools.JavaFileObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

import io.resys.hdes.aproc.spi.HdesAnnotationProcessor;


public class DtAnnotationProcessorTest {
  @Test
  public void generateDT() {
    Compilation compilation = javac()
        .withProcessors(new HdesAnnotationProcessor())
        .compile(JavaFileObjects.forResource("DtResourceForTestingAProc.java"));
    Assertions.assertTrue(compilation.errors().isEmpty());
    
    List<JavaFileObject> generatedFiles = compilation.generatedFiles();
    generatedFiles.forEach(System.out::println);
  }
}
