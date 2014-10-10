package com.remedy.arsys.share;

public class ObjectCreationCount
{
  static long[] cnt;
  static int max = 30;
  public static final int SCHEMALIST = 0;
  public static final int ACTIVE_LINKS = 1;
  public static final int FIELD_MAPS = 2;
  public static final int FORMS = 3;
  public static final int CONTAINERS = 4;
  public static final int FORM_IMAGES = 5;
  public static final int GUIDES = 6;
  public static final int COMPILED_FORMS = 7;
  public static final int DISPLAYED_FIELDS = 8;
  public static final int MENUS = 9;
  public static final int GROUPS = 10;
  public static final int ROLES = 11;
  public static final int USER_PREFERENCES = 12;
  public static final int USER_SEARCHES = 13;
  public static final int TRANSLATED_MESSAGES = 14;
  public static final int CONTAINER_LIST = 15;
  public static final int HTMLData = 16;
  public static final int JSData = 18;

  public static void inc(int paramInt)
  {
    cnt[paramInt] += 1L;
  }

  public static void print(String paramString, int paramInt)
  {
  }

  static
  {
    cnt = new long[max];
    for (int i = 0; i < max; i++)
      cnt[i] = 0L;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.ObjectCreationCount
 * JD-Core Version:    0.6.1
 */