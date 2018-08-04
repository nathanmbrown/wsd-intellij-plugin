package com.anecdote.wsd;

import com.intellij.lang.Language;

public class WSDLanguage extends Language
{
  public static final WSDLanguage INSTANCE = new WSDLanguage();

  public WSDLanguage()
  {
    super("WSD");
  }
}
