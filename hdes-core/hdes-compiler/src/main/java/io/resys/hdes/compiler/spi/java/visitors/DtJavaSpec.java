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

import org.immutables.value.Value;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public interface DtJavaSpec {
  @Value.Immutable
  public interface DtMethodSpec extends DtJavaSpec {
    MethodSpec getValue();
  }
  @Value.Immutable
  public interface DtMethodsSpec extends DtJavaSpec {
    List<MethodSpec> getValue();
  }

  @Value.Immutable
  public interface DtTypesSpec extends DtJavaSpec {
    List<TypeSpec> getValues();
  }
  @Value.Immutable
  public interface DtCodeSpecPair extends DtJavaSpec {
    CodeBlock getKey();
    CodeBlock getValue();
  }
  @Value.Immutable
  public interface DtCodeSpec extends DtJavaSpec {
    CodeBlock getValue();
  }
}
