package com.remedy.arsys.goat;

import java.io.File;

public class FileFormContext extends FormContext
{
  public FileFormContext(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    super(paramString1, paramString2, paramString3, paramString4);
  }

  public String getResourceURL()
  {
    return "resources/";
  }

  public String getResourcePath()
  {
    return "resources" + File.separator;
  }

  public String getFormURL(String paramString1, String paramString2, String paramString3)
  {
    return "";
  }

  public String getFormURL()
  {
    return "";
  }

  public final String getImagepoolURL()
  {
    return "imagepool/";
  }

  public final String getImagepoolPath()
  {
    return "imagepool" + File.separator;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.FileFormContext
 * JD-Core Version:    0.6.1
 */