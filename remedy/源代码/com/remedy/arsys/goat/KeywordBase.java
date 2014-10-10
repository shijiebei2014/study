package com.remedy.arsys.goat;

public class KeywordBase
{
  protected static final String[] MKeywordNames = { "NULL", "DEFAULT", "USER", "TIMESTAMP", "TIME", "DATE", "SCHEMA", "SERVER", "WEEKDAY", "GROUPS", "OPERATION", "HARDWARE", "OS", "DATABASE", "LASTID", "LASTCOUNT", "VERSION", "VUI", "GUIDETEXT", "FIELDHELP", "GUIDE", "APPLICATION", "LOCALE", "CLIENT-TYPE", "SCHEMA-ALIAS", "ROWSELECTED", "ROWCHANGED", "BROWSER", "VUI-TYPE", "TCPPORT", "HOMEURL", "ROLES", "EVENTTYPE", "EVENTSRCWINID", "CURRENTWINID", "LASTOPENEDWINID", "INBULKTRANSACTION", "FIELDID", "FIELDNAME", "FIELDLABEL", "SERVERTIMESTAMP", "GROUPIDS", "EVENTDATA", "ERRNOFILTER", "ERRMSGFILTER", "ERRAPPMSGFILTER", "INCLNTMANTRANS", "DRAGSRCFIELDID", "DROPTGTFIELDID", "SHIFT_KEY", "ALT_KEY", "CTRL_KEY", "AUTHSTRING", "ROWVISIBLE" };
  protected static final int[] MKeywordTypes = { 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 2, 4, 4, 4, 4, 4, 4, 4, 2, 4, 2, 2, 4, 4, 2, 4, 4, 4, 4, 4, 4, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 2, 2, 2, 4, 2 };
  protected static final int[] MKeywordFlags = { 0, 104, 0, 60, 60, 60, 0, 0, 60, 0, 2, 0, 40, 0, 6, 7, 0, 0, 0, 64, 40, 0, 0, 1, 0, 45, 41, 40, 0, 1, 2, 0, 2, 2, 2, 2, 1, 40, 40, 40, 60, 0, 2, 40, 40, 40, 40, 40, 40, 3, 3, 3, 40, 45 };
  public static final int IS_INTEGER = 1;
  public static final int IS_CONSTANT = 2;
  public static final int IS_TEMPORAL = 4;
  public static final int IS_TIME_RELATED = 16;
  public static final int IS_FUNCTION = 32;
  public static final int NEEDS_FIELDID = 64;
  public static final int IDX_NULL = 0;
  public static final int IDX_DEFAULT = 1;
  public static final int IDX_USER = 2;
  public static final int IDX_TIMESTAMP = 3;
  public static final int IDX_TIME = 4;
  public static final int IDX_DATE = 5;
  public static final int IDX_SCHEMA = 6;
  public static final int IDX_SERVER = 7;
  public static final int IDX_WEEKDAY = 8;
  public static final int IDX_GROUPS = 9;
  public static final int IDX_OPERATION = 10;
  public static final int IDX_HARDWARE = 11;
  public static final int IDX_OS = 12;
  public static final int IDX_DATABASE = 13;
  public static final int IDX_LASTID = 14;
  public static final int IDX_LASTCOUNT = 15;
  public static final int IDX_VERSION = 16;
  public static final int IDX_VUI = 17;
  public static final int IDX_GUIDETEXT = 18;
  public static final int IDX_FIELDHELP = 19;
  public static final int IDX_GUIDE = 20;
  public static final int IDX_APPLICATION = 21;
  public static final int IDX_LOCALE = 22;
  public static final int IDX_CLIENT_TYPE = 23;
  public static final int IDX_SCHEMA_ALIAS = 24;
  public static final int IDX_ROWSELECTED = 25;
  public static final int IDX_ROWCHANGED = 26;
  public static final int IDX_BROWSER = 27;
  public static final int IDX_VUI_TYPE = 28;
  public static final int IDX_TCPPORT = 29;
  public static final int IDX_HOMEURL = 30;
  public static final int IDX_ROLES = 31;
  public static final int IDX_EVENTTYPE = 32;
  public static final int IDX_EVENTSRCWINID = 33;
  public static final int IDX_CURRENTWINID = 34;
  public static final int IDX_LASTOPENEDWINID = 35;
  public static final int IDX_INBULKTRANSACTION = 36;
  public static final int IDX_FIELDID = 37;
  public static final int IDX_FIELDNAME = 38;
  public static final int IDX_FIELDLABEL = 39;
  public static final int IDX_SERVERTIMESTAMP = 40;
  public static final int IDX_GROUPIDS = 41;
  public static final int IDX_EVENTDATA = 42;
  public static final int IDX_ERRNOFILTER = 43;
  public static final int IDX_ERRMSGFILTER = 44;
  public static final int IDX_ERRAPPMSGFILTER = 45;
  public static final int IDX_INCLNTMANTRANS = 46;
  public static final int IDX_DRAGSRCFIELDID = 47;
  public static final int IDX_DROPTGTFIELDID = 48;
  public static final int IDX_SHIFT_KEY = 49;
  public static final int IDX_ALT_KEY = 50;
  public static final int IDX_CTRL_KEY = 51;
  public static final int IDX_AUTHSTRING = 52;
  public static final int IDX_ROWVISIBLE = 53;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.KeywordBase
 * JD-Core Version:    0.6.1
 */