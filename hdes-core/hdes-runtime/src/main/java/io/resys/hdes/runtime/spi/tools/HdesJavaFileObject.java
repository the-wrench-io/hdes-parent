package io.resys.hdes.runtime.spi.tools;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

public abstract class HdesJavaFileObject extends SimpleJavaFileObject {
  public HdesJavaFileObject(URI uri, JavaFileObject.Kind kind) {
    super(uri, kind);
  }
  
  public abstract byte[] getBytes();

  public static HdesJavaFileObject create(String className, CharSequence content) {
    final URI uri = URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension);
    final JavaFileObject.Kind kind = JavaFileObject.Kind.SOURCE;
    return new HdesJavaFileObject(uri, kind) {
      @Override
      public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return content;
      }
      @Override
      public byte[] getBytes() {
        return content.toString().getBytes(StandardCharsets.UTF_8);
      }
    };
  }

  public static HdesJavaFileObject create(String name, JavaFileObject.Kind kind) {
    final URI uri = URI.create("string:///" + name.replace('.', '/') + kind.extension);
    return new HdesJavaFileObject(uri, kind) {
      private ByteArrayOutputStream content = new ByteArrayOutputStream();
      private byte[] bytes;

      @Override
      public OutputStream openOutputStream() {
        return content;
      }

      @Override
      public byte[] getBytes() {
        if (bytes == null) {
          bytes = content.toByteArray();
          content = null;
        }
        return bytes;
      }

      @Override
      public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return new String(content.toByteArray(), StandardCharsets.UTF_8);
      }
    };
  }
}