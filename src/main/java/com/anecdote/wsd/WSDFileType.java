package com.anecdote.wsd;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Nathan Created : 04/08/2018
 */
public class WSDFileType extends LanguageFileType
{
  public static final WSDFileType INSTANCE = new WSDFileType();

  private WSDFileType()
  {
    super(WSDLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName()
  {
    return "WSD file";
  }

  @NotNull
  @Override
  public String getDescription()
  {
    return "WSD language file";
  }

  @NotNull
  @Override
  public String getDefaultExtension()
  {
    return "WSD";
  }

  @Nullable
  @Override
  public Icon getIcon()
  {
    return PlatformIcons.CUSTOM_FILE_ICON;
  }
}