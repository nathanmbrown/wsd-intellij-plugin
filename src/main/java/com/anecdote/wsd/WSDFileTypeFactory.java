package com.anecdote.wsd;

import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class WSDFileTypeFactory extends FileTypeFactory
{
  @Override
  public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer)
  {
    fileTypeConsumer.consume(WSDFileType.INSTANCE);
  }
}