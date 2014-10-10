package com.remedy.arsys.support;

public class FontTable
{
  public static final String DETAIL = "Detail";
  public static final String EDITOR = "Editor";
  public static final String HEADER1 = "Header1";
  public static final String HEADER2 = "Header2";
  public static final String HEADER3 = "Header3";
  public static final String NOTE = "Note";
  public static final String OPTIONAL = "Optional";
  public static final String PUSHBUTTON = "PushButton";
  public static final String RADIOBUTTON = "RadioButton";
  public static final String REQUIRED = "Required";
  public static final String SYSTEM = "System";
  private String mFont;
  private int mIndex;
  private static FontTable[] MMap = { new FontTable("Detail", 0), new FontTable("Editor", 1), new FontTable("Header1", 2), new FontTable("Header2", 3), new FontTable("Header3", 4), new FontTable("Note", 5), new FontTable("Optional", 6), new FontTable("PushButton", 7), new FontTable("RadioButton", 8), new FontTable("Required", 9), new FontTable("System", 10) };

  private FontTable(String paramString, int paramInt)
  {
    this.mFont = paramString;
    this.mIndex = paramInt;
  }

  public static String mapFontToClassName(String paramString)
  {
    if (paramString == null)
      return "";
    for (int i = 0; i < MMap.length; i++)
      if (MMap[i].mFont.equals(paramString))
        return "f" + MMap[i].mIndex;
    return "f1";
  }

  public static String mapClassNameToFont(String paramString)
  {
    if ((paramString == null) || (paramString.length() <= 1))
      return "";
    int i = -1;
    try
    {
      i = Integer.parseInt(paramString.substring(1));
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    if ((i < 0) || (i >= MMap.length))
      return "";
    return MMap[i].mFont;
  }

  public static int mapFontToIndex(String paramString)
  {
    if (paramString == null)
      return 1;
    for (int i = 0; i < MMap.length; i++)
      if (MMap[i].mFont.equals(paramString))
        return MMap[i].mIndex;
    return 1;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.support.FontTable
 * JD-Core Version:    0.6.1
 */