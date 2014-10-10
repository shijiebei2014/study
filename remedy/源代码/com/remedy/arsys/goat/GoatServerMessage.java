package com.remedy.arsys.goat;

import com.bmc.arsys.api.StatusInfo;
import com.remedy.arsys.share.MessageTranslation;

public class GoatServerMessage
{
  private StatusInfo mStatusInfo;
  private String mLocale;

  public GoatServerMessage(StatusInfo paramStatusInfo, String paramString)
  {
    this.mStatusInfo = paramStatusInfo;
    this.mLocale = paramString;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.mStatusInfo == null)
      return null;
    int i = this.mStatusInfo.getMessageType();
    if (i == 2)
    {
      localStringBuilder.append("ARERR [");
    }
    else if (i == 1)
    {
      localStringBuilder.append("ARWARN [");
    }
    else if (i == 0)
    {
      if (this.mStatusInfo.getMessageNum() == 8914L)
        return "";
      localStringBuilder.append("ARNOTE [");
    }
    localStringBuilder.append(this.mStatusInfo.getMessageNum()).append("] ").append(translate(this.mStatusInfo.getMessageText()));
    String str = translate(this.mStatusInfo.getAppendedText());
    if ((str != null) && (str.length() != 0))
      localStringBuilder.append(" : ").append(str);
    localStringBuilder.append("\\n");
    return localStringBuilder.toString();
  }

  private String translate(String paramString)
  {
    return MessageTranslation.getLocalizedText(this.mLocale, paramString);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.GoatServerMessage
 * JD-Core Version:    0.6.1
 */