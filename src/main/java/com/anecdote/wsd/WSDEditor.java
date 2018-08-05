package com.anecdote.wsd;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.FakeVirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.EventDispatcher;
import org.intellij.images.editor.ImageEditor;
import org.intellij.images.editor.ImageFileEditor;
import org.intellij.images.editor.impl.ImageEditorImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Nathan Created : 04/08/2018
 */
public class WSDEditor extends UserDataHolderBase implements ImageFileEditor, PropertyChangeListener
{
  private static final String NAME = "ImageFileEditor";

  private final ImageEditor imageEditor;
  private final EventDispatcher<PropertyChangeListener> myDispatcher =
    EventDispatcher.create(PropertyChangeListener.class);

  WSDEditor(@NotNull Project project, @NotNull VirtualFile file)
  {
    imageEditor = new ImageEditorImpl(project, new LightVirtualFile("fake.wsd")); // don't pass real file as don't
                                                                                        // want image editor to register
                                                                                        // with it
    Disposer.register(this, imageEditor);
    imageEditor.setGridVisible(false);
    imageEditor.setTransparencyChessboardVisible(false);
  }

  @NotNull
  public JComponent getComponent()
  {
    return imageEditor.getComponent();
  }

  public JComponent getPreferredFocusedComponent()
  {
    return imageEditor.getContentComponent();
  }

  @NotNull
  public String getName()
  {
    return NAME;
  }

  @Override
  public void setState(@NotNull FileEditorState state)
  {
  }

  public boolean isModified()
  {
    return false;
  }

  public boolean isValid()
  {
    return true;
  }

  public void selectNotify()
  {
  }

  public void deselectNotify()
  {
  }

  public void addPropertyChangeListener(@NotNull PropertyChangeListener listener)
  {
    myDispatcher.addListener(listener);
  }

  public void removePropertyChangeListener(@NotNull PropertyChangeListener listener)
  {
    myDispatcher.removeListener(listener);
  }

  @Override
  public void propertyChange(@NotNull PropertyChangeEvent event)
  {
    PropertyChangeEvent editorEvent =
      new PropertyChangeEvent(this, event.getPropertyName(), event.getOldValue(), event.getNewValue());
    myDispatcher.getMulticaster().propertyChange(editorEvent);
  }

  public BackgroundEditorHighlighter getBackgroundHighlighter()
  {
    return null;
  }

  public FileEditorLocation getCurrentLocation()
  {
    return null;
  }

  public StructureViewBuilder getStructureViewBuilder()
  {
    return null;
  }

  public void dispose()
  {
  }

  @NotNull
  public ImageEditor getImageEditor()
  {
    return imageEditor;
  }
}
