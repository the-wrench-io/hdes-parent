package io.resys.hdes.aproc.spi.generator;

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

import java.io.IOException;
import java.util.function.Consumer;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

import com.squareup.javapoet.JavaFile;

public class FileGenerator {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    
    private Filer filer;
    private Messager messager;
    
    public Builder filer(Filer filer) {
      this.filer = filer;
      return this;
    }

    public Builder messager(Messager messager) {
      this.messager = messager;
      return this;
    }
    
    public Consumer<JavaFile> build() {
      
      Consumer<JavaFile> fileGenerator = (javaFile) -> {
        try {
          javaFile.writeTo(System.out);
          javaFile.writeTo(filer);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }; 
      
      
      return fileGenerator;
    }
  } 
}
