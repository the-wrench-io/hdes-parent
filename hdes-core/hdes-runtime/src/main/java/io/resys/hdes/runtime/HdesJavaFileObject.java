package io.resys.hdes.runtime;

import java.net.URI;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

public class HdesJavaFileObject extends SimpleJavaFileObject {
  final CharSequence content;

  public HdesJavaFileObject(String className, CharSequence content) {
    super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
    this.content = content;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return content;
  }
  
  public static HdesJavaFileObject create(String className, CharSequence content) {
    return new HdesJavaFileObject(className, content);
  }
}
