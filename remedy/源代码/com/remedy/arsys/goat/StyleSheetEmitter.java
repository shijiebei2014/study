package com.remedy.arsys.goat;

import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;
import java.util.HashSet;

public class StyleSheetEmitter
{
  private static final String DEFAULT_SUFFIX = ".css";
  private static HashSet STYLED_LANGUAGES = new HashSet();
  private final String mSheetName;
  private final boolean mLocaleSpecific;
  private final boolean mAppSpecific;
  private final String mSuffix;

  public StyleSheetEmitter(String paramString)
  {
    this(paramString, true, true);
  }

  public StyleSheetEmitter(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramString, paramBoolean1, paramBoolean2, ".css");
  }

  public StyleSheetEmitter(String paramString1, boolean paramBoolean1, boolean paramBoolean2, String paramString2)
  {
    this.mAppSpecific = paramBoolean2;
    this.mLocaleSpecific = paramBoolean1;
    this.mSheetName = paramString1;
    this.mSuffix = paramString2;
  }

  public void emit(HTMLWriter paramHTMLWriter, String paramString)
    throws GoatException
  {
    emit(paramHTMLWriter, this.mSheetName + this.mSuffix, paramString);
    if (this.mLocaleSpecific)
    {
      String str = SessionData.get().getLocale();
      if ((str != null) && (str.length() >= 2))
      {
        str = str.substring(0, 2);
        if (STYLED_LANGUAGES.contains(str))
          emit(paramHTMLWriter, this.mSheetName + "_" + str + this.mSuffix, paramString);
      }
    }
  }

  public void emit(String paramString1, HTMLWriter paramHTMLWriter, String paramString2)
    throws GoatException
  {
    emit(paramString1, paramHTMLWriter, this.mSheetName + this.mSuffix, paramString2);
    if (this.mLocaleSpecific)
    {
      String str = SessionData.get().getLocale();
      if ((str != null) && (str.length() >= 2))
      {
        str = str.substring(0, 2);
        if (STYLED_LANGUAGES.contains(str))
          emit(paramString1, paramHTMLWriter, this.mSheetName + "_" + str + this.mSuffix, paramString2);
      }
    }
  }

  private void emit(HTMLWriter paramHTMLWriter, String paramString1, String paramString2)
    throws GoatException
  {
    FormContext localFormContext = FormContext.get();
    paramHTMLWriter.openTag("script").attr("type", "text/javascript").endTag(false).append(JSWriter.genDynEmbedRsrcRef("link", false, false, new String[] { "rel", "stylesheet" }, "href", localFormContext.getStylesheetURL(), paramString1)).closeTag("script");
    if (this.mAppSpecific)
    {
      String str = localFormContext.getApplication();
      if (str != null)
      {
        GoatApplicationContainer localGoatApplicationContainer = GoatApplicationContainer.get(SessionData.get().getServerLogin(paramString2), str);
        Globule localGlobule = localGoatApplicationContainer.getResourceFile(null, localFormContext.getStylesheet(paramString1));
        if (localGlobule != null)
          paramHTMLWriter.openTag("script").attr("type", "text/javascript").endTag(false).append(JSWriter.genDynEmbedRsrcRef("link", false, false, new String[] { "rel", "stylesheet" }, "href", localFormContext.getAppStylesheetPrefixURL(paramString2), paramString1)).closeTag("script");
      }
    }
  }

  private void emit(String paramString1, HTMLWriter paramHTMLWriter, String paramString2, String paramString3)
    throws GoatException
  {
    FormContext localFormContext = FormContext.get();
    String str1 = localFormContext.getStylesheetURL() + "8.1.00 201301251157" + "/" + paramString1 + "/" + paramString2;
    paramHTMLWriter.openTag("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", str1).endTag(true);
    if (this.mAppSpecific)
    {
      String str2 = localFormContext.getApplication();
      if (str2 != null)
      {
        GoatApplicationContainer localGoatApplicationContainer = GoatApplicationContainer.get(SessionData.get().getServerLogin(paramString3), str2);
        Globule localGlobule = localGoatApplicationContainer.getResourceFile(null, localFormContext.getStylesheet(paramString2));
        if (localGlobule != null)
        {
          str1 = localFormContext.getAppStylesheetPrefixURL(paramString3) + "8.1.00 201301251157" + "/" + paramString1 + "/" + paramString2;
          paramHTMLWriter.openTag("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", str1).endTag(false).closeTag("link");
        }
      }
    }
  }

  static
  {
    STYLED_LANGUAGES.add("ja");
    STYLED_LANGUAGES.add("zh");
    STYLED_LANGUAGES.add("ko");
    STYLED_LANGUAGES.add("pl");
    STYLED_LANGUAGES.add("de");
    STYLED_LANGUAGES.add("sk");
    STYLED_LANGUAGES.add("pt");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.StyleSheetEmitter
 * JD-Core Version:    0.6.1
 */