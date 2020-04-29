package io.resys.hdes.compiler.spi.java.visitors;

/*-
 * #%L
 * hdes-compiler
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

import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public interface FlJavaSpec {
  
  @Value.Immutable
  public interface FlHeaderSpec extends FlJavaSpec {
    MethodSpec getValue();
    List<TypeSpec> getChildren();
  }
  @Value.Immutable
  public interface FlTaskSpec extends FlJavaSpec {
    List<TypeSpec> getChildren();
  }
  @Value.Immutable
  public interface FlTypesSpec extends FlJavaSpec {
    List<TypeSpec> getValues();
  }
  
  @Value.Immutable
  public interface FlTaskVisitSpec extends FlJavaSpec {
    CodeBlock getValue();
    List<MethodSpec> getValues();
  }
  @Value.Immutable
  public interface FlWhenThenSpec extends FlJavaSpec {
    Optional<CodeBlock> getWhen();
    FlTaskVisitSpec getThen();
  }
  public interface FlMethodSpec extends FlJavaSpec {
    MethodSpec getValue();
  }
  @Value.Immutable
  public interface FlCodeSpecPair extends FlJavaSpec {
    CodeBlock getKey();
    CodeBlock getValue();
  }
  @Value.Immutable
  public interface FlCodeSpec extends FlJavaSpec {
    CodeBlock getValue();
  }
}
