package com.anecdote.wsd;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Alarm;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.net.*;

/**
 * @author Nathan Created : 04/08/2018
 */
final class WSDEditorProvider implements FileEditorProvider, DumbAware
{
  @NonNls
  private static final String EDITOR_TYPE_ID = "wsd";

  @Override
  public boolean accept(@NotNull Project project, @NotNull VirtualFile file)
  {
    return file.getFileType() == WSDFileType.INSTANCE;
  }

  private void updatePreview(WSDEditor viewer, String documentContent)
  {
    try {
      BufferedImage bufferedImage = viewer.getImageEditor().getDocument().getValue();
      if (bufferedImage != null) {
        RescaleOp rescaleOp = new RescaleOp(1.0f, 155, null);
        rescaleOp.filter(bufferedImage,
                         bufferedImage);  // Source and destination are the same.
        viewer.getImageEditor().getDocument().setValue(bufferedImage);
      }
      BufferedImage image = getImage(documentContent);
      viewer.getImageEditor().getDocument().setValue(image);
//      String svg = getSVG(documentContent);
//      ImageDocument.ScaledImageProvider imageProvider = IfsUtil.getImageProvider(new LightVirtualFile("preview.svg",
//                                                                                                      SvgFileType.INSTANCE,
//                                                                                                      svg));
//      viewer.getImageEditor().getDocument().setValue(imageProvider);
    } catch (Exception e) {
      viewer.getImageEditor().getDocument().setValue(toBufferedImage(IconLoader.getIcon("/general/error@2x.png")));
      e.printStackTrace();
    }
  }

  private String getSVG(String documentContent) throws IOException
  {
    URL wsdurl = getWSDURL(documentContent, "default", "svg");
    InputStream inputStream = wsdurl.openStream();
    return IOUtils.toString(inputStream, "UTF-8");
  }

  private BufferedImage toBufferedImage(Icon icon)
  {
    BufferedImage bufferedImage = UIUtil.createImage(
      icon.getIconWidth(),
      icon.getIconHeight(),
      BufferedImage.TYPE_INT_RGB);
    Graphics g = bufferedImage.createGraphics();
// paint the Icon to the BufferedImage.
    icon.paintIcon(null, g, 0, 0);
    g.dispose();
    return bufferedImage;
  }

  private BufferedImage getImage(String documentContent) throws IOException, ImageReadException
  {
    //Build parameter string
    String style = "default";
    String format = "png";
    URL url = getWSDURL(documentContent, style, format);

    try (InputStream inputStream = url.openStream()) {
      return Imaging.getBufferedImage(inputStream);
    }
  }

  @NotNull
  private URL getWSDURL(String documentContent, String style, String format) throws IOException
  {
    String message = URLEncoder.encode(documentContent, "UTF-8");
    String data = String.format("style=%s&message=%s&apiVersion=1&format=%s", style, message, format);

    // Send the request
    URL url = new URL("http://www.websequencediagrams.com");
    URLConnection conn = url.openConnection();
    conn.setDoOutput(true);
    OutputStreamWriter writer = new OutputStreamWriter(
      conn.getOutputStream());

    //write parameters
    writer.write(data);
    writer.flush();

    // Get the response
    StringBuffer answer = new StringBuffer();
    BufferedReader reader = new BufferedReader(new InputStreamReader(
      conn.getInputStream()));
    String line;
    while ((line = reader.readLine()) != null) {
      answer.append(line);
    }
    writer.close();
    reader.close();

    String json = answer.toString();
    int start = json.indexOf("?" + format + "=");
    int end = json.indexOf("\"", start);

    url = new URL("http://www.websequencediagrams.com/" +
                  json.substring(start, end));
    return url;
  }

  @Override
  @NotNull
  public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file)
  {
    WSDEditor viewer = new WSDEditor(project, file);
    TextEditor editor = (TextEditor)TextEditorProvider.getInstance().createEditor(project, file);
    editor.getEditor().getDocument().addDocumentListener(new DocumentListener()
    {
      Alarm myAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, editor);

      @Override
      public void documentChanged(DocumentEvent event)
      {
        myAlarm.cancelAllRequests();
        myAlarm.addRequest(() -> updatePreview(viewer, event.getDocument().getText()), 500);
      }
    }, editor);
    updatePreview(viewer, editor.getEditor().getDocument().getText());
    return new TextEditorWithPreview(editor, viewer, "SvgEditor");
  }

  @Override
  @NotNull
  public String getEditorTypeId()
  {
    return EDITOR_TYPE_ID;
  }

  @Override
  @NotNull
  public FileEditorPolicy getPolicy()
  {
    return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
  }
}
