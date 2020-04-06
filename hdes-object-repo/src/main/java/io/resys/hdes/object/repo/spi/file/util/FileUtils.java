package io.resys.hdes.object.repo.spi.file.util;

import java.io.File;
import java.io.IOException;

import io.resys.hdes.object.repo.spi.file.exceptions.FileCantBeWrittenException;

public class FileUtils {
  public static File mkdir(File src) {
    if(src.exists()) {
      return isWritable(src);
    }
    
    if (src.mkdir()) {
      return src;
    }
    throw new FileCantBeWrittenException(src);
  }
  public static File mkFile(File src) throws IOException {
    if(src.exists()) {
      return isWritable(src);
    }
    
    if (src.createNewFile()) {
      return src;
    }
    throw new FileCantBeWrittenException(src);
  }
  public static File isWritable(File src) {
    if (src.canWrite()) {
      return src;
    }
    throw new FileCantBeWrittenException(src);
  }

  public static String getCanonicalNameOrName(File file) {
    try {
      return file.getCanonicalPath();
    } catch (Exception e) {
      return file.getName();
    }
  }
}
