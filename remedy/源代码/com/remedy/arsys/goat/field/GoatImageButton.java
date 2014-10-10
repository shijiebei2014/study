package com.remedy.arsys.goat.field;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.share.HTMLWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import javax.imageio.ImageIO;

public class GoatImageButton
  implements Serializable
{
  private static final long serialVersionUID = -2125233604691543730L;
  private String mName;
  private int mW;
  private int mH;
  private String mTitle;

  public String getMName()
  {
    return this.mName;
  }

  public void setMName(String paramString)
  {
    this.mName = paramString;
  }

  public int getMW()
  {
    return this.mW;
  }

  public void setMW(int paramInt)
  {
    this.mW = paramInt;
  }

  public int getMH()
  {
    return this.mH;
  }

  public void setMH(int paramInt)
  {
    this.mH = paramInt;
  }

  public void setTitle(String paramString)
  {
    this.mTitle = paramString;
  }

  public GoatImageButton(String paramString)
  {
    FormContext localFormContext = FormContext.get();
    String str1 = Configuration.getInstance().getImageFile(paramString);
    assert (str1 != null);
    if (str1 == null)
    {
      this.mName = "MissingImage";
      this.mW = 12;
      this.mH = 12;
      return;
    }
    this.mName = str1;
    this.mTitle = "";
    String str2 = localFormContext.getImagePath() + str1;
    try
    {
      BufferedImage localBufferedImage = ImageIO.read(new File(str2));
      this.mW = localBufferedImage.getWidth();
      this.mH = localBufferedImage.getHeight();
    }
    catch (IOException localIOException)
    {
      System.out.println("Image not found - " + str2);
    }
  }

  public GoatImageButton(String paramString1, String paramString2)
  {
    this(paramString1);
    this.mTitle = paramString2;
  }

  public void emitMarkup(HTMLWriter paramHTMLWriter, Box paramBox, String paramString1, String paramString2)
  {
    emitMarkup(paramHTMLWriter, paramBox, paramString1, paramString2, false, true, true, false);
  }

  public void emitMarkup(HTMLWriter paramHTMLWriter, Box paramBox, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    paramHTMLWriter.openTag("a").attr("href", "javascript:").attr("class", (paramBoolean4 ? "btn " : "btn btn3d ") + paramString1);
    paramHTMLWriter.attr("style", paramBox.toCSS() + (paramBoolean1 ? "visibility:hidden;" : "")).endTag(paramBoolean2);
    Box localBox1 = new Box(paramBox);
    localBox1.mW -= 2;
    if (localBox1.mW < 0)
      localBox1.mW = 0;
    if (localBox1.mH < 0)
      localBox1.mH = 0;
    Box localBox2 = localBox1.newCentredBox(this.mW, this.mH);
    paramHTMLWriter.openTag("img");
    FormContext localFormContext = FormContext.get();
    String str = localFormContext.getImageURL() + this.mName;
    if (!paramBoolean3)
      paramHTMLWriter.attr("class", "btnimg").attr("src", str).attr("style", localBox2.toCSS());
    else
      paramHTMLWriter.attr("class", "btnimg").attr("src", str);
    if (paramString2 != null)
    {
      paramHTMLWriter.attr("alt", paramString2);
      if ((paramString2.length() > 0) && (!FormContext.get().IsVoiceAccessibleUser()))
        paramHTMLWriter.attr("title", this.mTitle);
    }
    paramHTMLWriter.closeOpenTag(paramBoolean2).closeTag("a", paramBoolean2);
  }

  public void emitMarkup(HTMLWriter paramHTMLWriter, Box paramBox, String paramString1, String paramString2, boolean paramBoolean)
  {
    emitMarkup(paramHTMLWriter, paramBox, paramString1, paramString2, paramBoolean, true, true, false);
  }

  public void emitMarkup(HTMLWriter paramHTMLWriter, Box paramBox, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    emitMarkup(paramHTMLWriter, paramBox, paramString1, paramString2, paramBoolean1, true, paramBoolean2, false);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.GoatImageButton
 * JD-Core Version:    0.6.1
 */