package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.GoatException;

public class GetBulkTableEntryListAgent extends NDXGetBulkTableEntryList
{
  private StringBuilder mStatusBuffer;

  public GetBulkTableEntryListAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    if ((this.mTableList == null) || (this.mTableList.length < 1))
      throw new GoatException(9350);
    int i = this.mTableList.length;
    String str1 = null;
    String str2 = null;
    int j = 0;
    NDXRequest localNDXRequest = null;
    this.mStatusBuffer = new StringBuilder();
    append("this.result={");
    for (int k = 0; k < i; k++)
    {
      str1 = this.mTableList[k];
      j = str1.indexOf('/');
      if (j == -1)
        throw new GoatException(9350);
      str1 = str1.substring(j + 1);
      str2 = getTableID(str1);
      localNDXRequest = NDXFactory.handleRequest(str1);
      if (localNDXRequest == null)
        throw new GoatException(9350);
      str1 = getResultObject(localNDXRequest.toString());
      if ((str1 != null) && (str1.length() > 0))
      {
        if (k > 0)
          append(",");
        append(str2);
        append(":");
        append(str1);
      }
    }
    append("}");
    append(this.mStatusBuffer);
  }

  private String getResultObject(String paramString)
  {
    int i = paramString.indexOf('=');
    int j = paramString.indexOf(";if(getCurWFC_NS(this.windowID)!=null) getCurWFC_NS(this.windowID)");
    if (j != -1)
      this.mStatusBuffer.append(paramString.substring(j));
    String str = paramString.substring(i + 1);
    if (paramString.indexOf("this.result=") == -1)
      return "";
    return paramString.substring(0, j == -1 ? str.length() - 1 : j - 1);
  }

  private static String getTableID(String paramString)
    throws GoatException
  {
    NDXRequest.Parser localParser = new NDXRequest.Parser(paramString.substring(paramString.indexOf('/') + 1));
    for (int i = 0; i < 3; i++)
      localParser.next();
    return localParser.next();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetBulkTableEntryListAgent
 * JD-Core Version:    0.6.1
 */