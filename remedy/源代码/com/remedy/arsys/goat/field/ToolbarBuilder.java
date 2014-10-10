package com.remedy.arsys.goat.field;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.TextDirStyleContext;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;

public class ToolbarBuilder
{
  private static final String[][] MToolbarImages = new String[5][];
  static final int STRING_SEARCHSAVECHANGES = 0;
  static final int STRING_NEWSEARCH = 3;
  static final int STRING_NEWREQUEST = 4;
  static final int STRING_MODIFYALL = 5;
  static final int STRING_ADVANCEDSEARCH = 6;
  static final int STRING_CLEAR = 7;
  static final int STRING_SETTODEFAULTS = 8;
  static final int STRING_STATUSHISTORY = 9;
  static final int STRING_LOGOUT = 10;
  static final int STRING_HELP = 11;
  static final int STRING_HOME = 12;
  static final int STRING_SHOWPROFILE = 13;
  static final int STRING_CLEARPROFILE = 14;
  static final int STRING_SAVEDSEARCHES = 15;
  static final int STRING_QUICKREPORTS = 16;
  private static ToolbarItem[][] MToolbarItems;

  public ToolbarBuilder(FormContext paramFormContext, HTMLWriter paramHTMLWriter, long paramLong)
  {
    String str = SessionData.get().getLocale();
    paramHTMLWriter.openTag("div").attr("class", "TBTopBarStatus").endTag(false);
    paramHTMLWriter.cdata(MessageTranslation.getLocalizedText(str, "Current mode:") + " ");
    paramHTMLWriter.openTag("span").attr("class", "TBTopBarStatusMode").endTag(false).closeTag("span", false).closeTag("div", false);
    paramHTMLWriter.openTag("a").attr("class", "btn TBTopBarBox").attr("href", "javascript:");
    if (paramFormContext.IsVoiceAccessibleUser())
      paramHTMLWriter.attr("title", MessageTranslation.getLocalizedText(str, "Toolbar Visible"));
    paramHTMLWriter.endTag(false);
    paramHTMLWriter.openTag("img").attr("class", "tbright").attr("src", paramFormContext.getImageURL() + "mt_sprites.gif");
    paramHTMLWriter.attr("alt", MessageTranslation.getLocalizedText(str, "Show toolbar"));
    paramHTMLWriter.closeOpenTag(false);
    paramHTMLWriter.openTag("img").attr("class", "tbdown").attr("src", paramFormContext.getImageURL() + "mt_sprites.gif");
    paramHTMLWriter.attr("alt", MessageTranslation.getLocalizedText(str, "Hide toolbar"));
    paramHTMLWriter.closeOpenTag(false);
    paramHTMLWriter.closeTag("a", false);
    paramHTMLWriter.openTag("table").attr("cellpadding", 0).attr("cellspacing", 0).attr("class", "Toolbar").endTag(false);
    paramHTMLWriter.openTag("tbody").endTag(false).openTag("tr").endTag(false);
    if (paramFormContext.IsVoiceAccessibleUser())
    {
      paramHTMLWriter.openTag("td").endTag();
      paramHTMLWriter.openTag("a").attr("href", "#endToolbar").endTag();
      paramHTMLWriter.openTag("img").attr("src", paramFormContext.getImageURL() + "transparent.gif").attr("alt", "Skip toolbar link").attr("width", "1").attr("height", "1").attr("border", "0");
      paramHTMLWriter.endTag();
      paramHTMLWriter.closeTag("a");
      paramHTMLWriter.closeTag("td");
    }
    for (int i = 0; i < MToolbarItems.length; i++)
    {
      ToolbarItem[] arrayOfToolbarItem = (ToolbarItem[])MToolbarItems[i];
      int j = 0;
      for (int k = 0; (k < arrayOfToolbarItem.length) && (j == 0); k++)
        if (arrayOfToolbarItem[k].enabled(paramLong))
          j = 1;
      if (j != 0)
      {
        paramHTMLWriter.openTag("td").attr("nowrap").attr("class", "TBGroup TBGroup" + i).endTag();
        for (k = 0; k < arrayOfToolbarItem.length; k++)
        {
          ToolbarItem localToolbarItem = arrayOfToolbarItem[k];
          if (localToolbarItem.enabled(paramLong))
          {
            paramHTMLWriter.openTag("a").attr("class", localToolbarItem.mClassName + " btn btn3d tbbtn").attr("href", "javascript:");
            if (TextDirStyleContext.get().isRTL())
              paramHTMLWriter.attr("dir", "rtl");
            if (paramFormContext.IsVoiceAccessibleUser())
              paramHTMLWriter.attr("title", localToolbarItem.getLocalisedString(str));
            paramHTMLWriter.attr("style", "position:static").endTag().openTag("div").attr("id", "TB" + localToolbarItem.mClassName).endTag(false);
            if ((localToolbarItem.mImage != null) && (!paramFormContext.IsVoiceAccessibleUser()))
            {
              paramHTMLWriter.openTag("img").attr("src", paramFormContext.getImageURL() + localToolbarItem.mImage).attr("alt", localToolbarItem.getLocalisedString(str).trim()).closeOpenTag(false);
              paramHTMLWriter.closeTag("div");
              paramHTMLWriter.openTag("span").endTag();
              paramHTMLWriter.cdata(localToolbarItem.getLocalisedString(str));
              paramHTMLWriter.closeTag("span");
            }
            else
            {
              paramHTMLWriter.cdata(localToolbarItem.getLocalisedString(str));
              paramHTMLWriter.closeTag("div");
            }
            paramHTMLWriter.closeTag("a");
          }
        }
        paramHTMLWriter.closeTag("td");
      }
    }
    if (paramFormContext.IsVoiceAccessibleUser())
    {
      paramHTMLWriter.openTag("td").endTag();
      paramHTMLWriter.openTag("a").attr("name", "endToolbar").endTag().closeTag("a");
      paramHTMLWriter.closeTag("td");
    }
    paramHTMLWriter.closeTag("tr").closeTag("tbody").closeTag("table");
  }

  static
  {
    MToolbarImages[0] = { "mt_sprites.gif", "tbdown" };
    MToolbarImages[1] = { "mt_sprites.gif", "tbright" };
    MToolbarImages[2] = { "mt_sprites.gif", "modifyall" };
    MToolbarImages[3] = { "mt_sprites.gif", "newrequest" };
    MToolbarImages[4] = { "mt_sprites.gif", "newsearch" };
    boolean bool = Configuration.getInstance().getWorkflowProfiling();
    MToolbarItems = new ToolbarItem[bool ? 5 : 4][];
    MToolbarItems[0] = { new ToolbarItem(new long[] { 412L, 406L }, "searchsavechanges", 0) };
    MToolbarItems[1] = { new ToolbarItem(413L, "newsearch", 3, "mt_sprites.gif"), new ToolbarItem(414L, "newrequest", 4, "mt_sprites.gif"), new ToolbarItem(401L, "modifyall", 5, "mt_sprites.gif") };
    MToolbarItems[2] = { new ToolbarItem(420L, "savedsearches", 15), new ToolbarItem(1014L, "quickreports", 16), new ToolbarItem(418L, "advancedsearch", 6), new ToolbarItem(410L, "clear", 7), new ToolbarItem(411L, "settodefaults", 8), new ToolbarItem(415L, "statushistory", 9) };
    MToolbarItems[3] = { new ToolbarItem(419L, "logout", 10), new ToolbarItem(417L, "help", 11), new ToolbarItem(416L, "home", 12) };
    if (MToolbarItems.length == 5)
      MToolbarItems[4] = { new ToolbarItem(430L, "showprofile", 13), new ToolbarItem(430L, "clearprofile", 14) };
  }

  static class ToolbarItem
  {
    long[] mEquivalentFieldIDs;
    String mClassName;
    int mNeedsLocalisingName;
    String mImage;

    ToolbarItem(long[] paramArrayOfLong, String paramString1, int paramInt, String paramString2)
    {
      this.mEquivalentFieldIDs = paramArrayOfLong;
      this.mClassName = paramString1;
      this.mNeedsLocalisingName = paramInt;
      this.mImage = paramString2;
    }

    ToolbarItem(long[] paramArrayOfLong, String paramString, int paramInt)
    {
      this(paramArrayOfLong, paramString, paramInt, null);
    }

    ToolbarItem(long paramLong, String paramString, int paramInt)
    {
      this(new long[] { paramLong }, paramString, paramInt, null);
    }

    ToolbarItem(long paramLong, String paramString1, int paramInt, String paramString2)
    {
      this(new long[] { paramLong }, paramString1, paramInt, paramString2);
    }

    private String getLocalisedString(String paramString)
    {
      if (this.mNeedsLocalisingName == 0)
      {
        String str1 = MessageTranslation.getLocalizedText(paramString, "Save");
        String str2 = MessageTranslation.getLocalizedText(paramString, "Search");
        return str1.length() > str2.length() ? str1 : str2;
      }
      if (this.mNeedsLocalisingName == 3)
        return " " + MessageTranslation.getLocalizedText(paramString, "New search");
      if (this.mNeedsLocalisingName == 4)
        return " " + MessageTranslation.getLocalizedText(paramString, "New request");
      if (this.mNeedsLocalisingName == 5)
        return " " + MessageTranslation.getLocalizedText(paramString, "Modify all");
      if (this.mNeedsLocalisingName == 15)
        return " " + MessageTranslation.getLocalizedText(paramString, "Searches");
      if (this.mNeedsLocalisingName == 6)
        return MessageTranslation.getLocalizedText(paramString, "Advanced search");
      if (this.mNeedsLocalisingName == 7)
        return MessageTranslation.getLocalizedText(paramString, "Clear");
      if (this.mNeedsLocalisingName == 7)
        return MessageTranslation.getLocalizedText(paramString, "QuickReports");
      if (this.mNeedsLocalisingName == 8)
        return MessageTranslation.getLocalizedText(paramString, "Set to defaults");
      if (this.mNeedsLocalisingName == 9)
        return MessageTranslation.getLocalizedText(paramString, "Status history");
      if (this.mNeedsLocalisingName == 10)
        return MessageTranslation.getLocalizedText(paramString, "Logout");
      if (this.mNeedsLocalisingName == 11)
        return MessageTranslation.getLocalizedText(paramString, "Help");
      if (this.mNeedsLocalisingName == 12)
        return MessageTranslation.getLocalizedText(paramString, "Home");
      if (this.mNeedsLocalisingName == 13)
        return MessageTranslation.getLocalizedText(paramString, "Show profile");
      if (this.mNeedsLocalisingName == 14)
        return MessageTranslation.getLocalizedText(paramString, "Clear profile");
      if (this.mNeedsLocalisingName == 16)
        return MessageTranslation.getLocalizedText(paramString, "My Reports");
      if (!$assertionsDisabled)
        throw new AssertionError();
      return "Missing";
    }

    boolean enabled(long paramLong)
    {
      int i = 0;
      for (int j = 0; j < this.mEquivalentFieldIDs.length; j++)
        if ((paramLong & 1 << (int)(this.mEquivalentFieldIDs[j] - 400L)) != 0L)
          i++;
      return i != this.mEquivalentFieldIDs.length;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.ToolbarBuilder
 * JD-Core Version:    0.6.1
 */