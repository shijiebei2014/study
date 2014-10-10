package com.remedy.arsys.goat.preferences;

import com.bmc.arsys.api.CoreFieldId;
import com.bmc.arsys.api.EntryListFieldInfo;
import java.util.HashMap;

public abstract class AbstractARUserPreferences extends Preferences
{
  public static final ARUserPreferencesKey ON_NEW_SUBMIT;
  public static final ARUserPreferencesKey ON_NEW_QUERY;
  public static final ARUserPreferencesKey LIMIT_QUERY_ITEMS;
  public static final ARUserPreferencesKey MAX_QUERY_ITEMS;
  public static final ARUserPreferencesKey DIARY_SHOW_RECENT_FIRST;
  public static final ARUserPreferencesKey SHOW_ADVANCED_SEARCH_BAR;
  public static final ARUserPreferencesKey RECENT_USED_LIST_SIZE;
  public static final ARUserPreferencesKey MAX_WINDOW_ON_OPEN;
  public static final ARUserPreferencesKey CONFIRM_NEW_REQUEST;
  public static final ARUserPreferencesKey LOG_ACTIVE_LINKS;
  public static final ARUserPreferencesKey LOG_API;
  public static final ARUserPreferencesKey LOG_DATABASE;
  public static final ARUserPreferencesKey LOG_FILTER;
  public static final ARUserPreferencesKey LOG_WEB_TIMINGS;
  public static final ARUserPreferencesKey DEFAULT_FORM_VIEW;
  public static final ARUserPreferencesKey SHOW_HIDDEN_SCHEMAS;
  public static final ARUserPreferencesKey OPEN_WINDOW_VIEW_EXT;
  public static final ARUserPreferencesKey PANE_LAYOUT;
  public static final ARUserPreferencesKey REFRESH_TABLE_FIELDS;
  public static final ARUserPreferencesKey PREFERRED_REPORT_VIEWER;
  public static final ARUserPreferencesKey REPORT_SERVER;
  public static final ARUserPreferencesKey ALERT_REFRESH_INTERVAL;
  public static final ARUserPreferencesKey ALERT_SERVERS;
  public static final ARUserPreferencesKey USER_LOCALE;
  public static final ARUserPreferencesKey TIME_ZONE;
  public static final ARUserPreferencesKey DISPLAY_TIME_FORMAT;
  public static final ARUserPreferencesKey CUSTOM_DATE_FORMAT_STR;
  public static final ARUserPreferencesKey INITIAL_CURRENCY_CODE;
  public static final ARUserPreferencesKey CUSTOM_TIME_FORMAT_STR;
  public static final ARUserPreferencesKey WEB_ACCESSIBLE_MODE;
  public static final ARUserPreferencesKey ACCESSIBLE_MESSAGE;
  public static final ARUserPreferencesKey SESSION_TIMEOUT_IN_MINUTES;
  public static final ARUserPreferencesKey HOME_PAGE_SERVER;
  public static final ARUserPreferencesKey HOME_PAGE_FORM;
  public static final ARUserPreferencesKey TABLE_COLUMN_WIDTH;
  public static final ARUserPreferencesKey TABLE_COLUMN_ORDER;
  public static final ARUserPreferencesKey TABLE_COLUMN_SORT;
  public static final ARUserPreferencesKey TABLE_REFRESH_INTERVAL;
  public static final ARUserPreferencesKey ANIMATION_EFFECTS;
  public static final ARUserPreferencesKey HOVER_WAIT_TIME;
  public static final ARUserPreferencesKey OPEN_AFTER_SUBMIT;
  public static final ARUserPreferencesKey MENU_TYPE;
  public static final ARUserPreferencesKey SMARTMENU_ITEM_THRESHOLD;
  public static final ARUserPreferencesKey SMARTMENU_LEVEL_THRESHOLD;
  private static final ARUserPreferencesKey LAST_UPDATED_TIME;
  public static final Long PANE_TOP;
  public static final Long DIARY_YES;
  public static final Long HTML_FRAMES;
  public static final Long ACCESIBLE_MODE_VISUAL;
  public static final Long JAVA_JVM;
  public static final Long ACCESSIBLE_MODE_VOICE;
  public static final Long ACCESSIBLE_MODE_NONE;
  public static final Long NETSCAPE_PLUGIN;
  public static final Integer DEFAULT_SESSION_TIMEOUT;
  public static final Long PANE_RIGHT;
  public static final Long DIARY_DEFAULT;
  public static final Long DIARY_NO;
  public static final Long ACCESSIBLE_MSG_ALL_ACTIONS;
  public static final Long ACCESSIBLE_MSG_NONE;
  public static final Integer DEFAULT_ALERT_REFRESH_INTERVAL;
  public static final Long LONG_TIME_FORMAT;
  public static final Long MENU_TYPE_LISTBOX;
  public static final Integer DEFAULT_MAX_QUERY_ITEMS;
  public static final Long CLEAR_ALL;
  public static final Long JAVA_PLUGIN;
  public static final Long PANE_DEFAULT;
  public static final Long ACCESSIBLE_MSG_ACTION;
  public static final Long NO;
  public static final Long YES;
  public static final Long PANE_BOTTOM;
  public static final Integer DEFAULT_RECENT_USED_LIST_SIZE;
  public static final Long SET_DEFAULTS;
  public static final Long MENU_TYPE_POPUP;
  public static final Long PANE_LEFT;
  public static final Long HTML_NOFRAMES;
  public static final Long MENU_TYPE_SMART;
  public static final Long KEEP_PREVIOUS;
  public static final Long SHORT_TIME_FORMAT;
  public static final Long ACTIVEX;
  public static final Long CUSTOM_TIME_FORMAT;
  public static final Long MENU_TYPE_DEFAULT;
  protected static final ARVersionInfo[] FIELDS_TO_RETRIEVE_ARR;
  protected static final ARUserPreferencesKey[] BROWSER_PREFS;
  protected static final HashMap DEFAULT_PREFS = localHashMap;

  protected ARVersionInfo getInfoForServerVersion(String paramString)
  {
    for (int i = 0; i < FIELDS_TO_RETRIEVE_ARR.length; i++)
      if (isVersionGreater(paramString, FIELDS_TO_RETRIEVE_ARR[i].ver))
        return FIELDS_TO_RETRIEVE_ARR[i];
    if (!$assertionsDisabled)
      throw new AssertionError();
    return FIELDS_TO_RETRIEVE_ARR[(FIELDS_TO_RETRIEVE_ARR.length - 1)];
  }

  protected boolean isVersionGreater(String paramString1, String paramString2)
  {
    String[] arrayOfString1 = paramString1.split(" ");
    String[] arrayOfString2 = arrayOfString1[0].split("\\.");
    String[] arrayOfString3 = paramString2.split("\\.");
    int i = arrayOfString2.length > arrayOfString3.length ? arrayOfString3.length : arrayOfString2.length;
    for (int j = 0; j < i; j++)
    {
      int k = Integer.parseInt(arrayOfString2[j]);
      int m = Integer.parseInt(arrayOfString3[j]);
      if (k > m)
        return true;
      if (k < m)
        return false;
      if ((k == m) && (j == i - 1))
        return true;
    }
    return false;
  }

  private String getPrefVal(ARUserPreferencesKey paramARUserPreferencesKey)
  {
    String str = get(paramARUserPreferencesKey);
    if (str == null)
      str = (String)DEFAULT_PREFS.get(paramARUserPreferencesKey);
    return str;
  }

  private Long getEnumPref(ARUserPreferencesKey paramARUserPreferencesKey)
  {
    return new Long(getPrefVal(paramARUserPreferencesKey));
  }

  private void setEnumPref(ARUserPreferencesKey paramARUserPreferencesKey, Long paramLong)
  {
    assert (paramLong != null);
    put(paramARUserPreferencesKey, paramLong.toString());
  }

  private int getIntegerPref(ARUserPreferencesKey paramARUserPreferencesKey)
  {
    return Integer.parseInt(getPrefVal(paramARUserPreferencesKey));
  }

  private void setIntegerPref(ARUserPreferencesKey paramARUserPreferencesKey, int paramInt)
  {
    put(paramARUserPreferencesKey, paramInt + "");
  }

  public Long getOnNewSubmit()
  {
    return getEnumPref(ON_NEW_SUBMIT);
  }

  public void setOnNewSubmit(Long paramLong)
  {
    setEnumPref(ON_NEW_SUBMIT, paramLong);
  }

  public Long getOnNewQuery()
  {
    return getEnumPref(ON_NEW_QUERY);
  }

  public void setOnNewQuery(Long paramLong)
  {
    setEnumPref(ON_NEW_QUERY, paramLong);
  }

  public Long getLimitQueryItems()
  {
    return getEnumPref(LIMIT_QUERY_ITEMS);
  }

  public void setLimitQueryItems(Long paramLong)
  {
    setEnumPref(LIMIT_QUERY_ITEMS, paramLong);
  }

  public int getMaxQueryItems()
  {
    return getIntegerPref(MAX_QUERY_ITEMS);
  }

  public void setMaxQueryItems(int paramInt)
  {
    setIntegerPref(MAX_QUERY_ITEMS, paramInt);
  }

  public Long getDiaryShowRecentFirst()
  {
    return getEnumPref(DIARY_SHOW_RECENT_FIRST);
  }

  public void setDiaryShowRecentFirst(Long paramLong)
  {
    setEnumPref(DIARY_SHOW_RECENT_FIRST, paramLong);
  }

  public Long getShowAdvancedSearchBar()
  {
    return getEnumPref(SHOW_ADVANCED_SEARCH_BAR);
  }

  public void setShowAdvancedSearchBar(Long paramLong)
  {
    setEnumPref(SHOW_ADVANCED_SEARCH_BAR, paramLong);
  }

  public int getRecentUsedListSize()
  {
    return getIntegerPref(RECENT_USED_LIST_SIZE);
  }

  public void setRecentUsedListSize(int paramInt)
  {
    setIntegerPref(RECENT_USED_LIST_SIZE, paramInt);
  }

  public Long getMaxWindowOnOpen()
  {
    return getEnumPref(MAX_WINDOW_ON_OPEN);
  }

  public void setMaxWindowOnOpen(Long paramLong)
  {
    setEnumPref(MAX_WINDOW_ON_OPEN, paramLong);
  }

  public Long getConfirmNewRequest()
  {
    return getEnumPref(CONFIRM_NEW_REQUEST);
  }

  public void setConfirmNewRequest(Long paramLong)
  {
    setEnumPref(CONFIRM_NEW_REQUEST, paramLong);
  }

  public Long getLogActiveLinks()
  {
    return getEnumPref(LOG_ACTIVE_LINKS);
  }

  public void setLogActiveLinks(Long paramLong)
  {
    setEnumPref(LOG_ACTIVE_LINKS, paramLong);
  }

  public Long getLogApi()
  {
    return getEnumPref(LOG_API);
  }

  public void setLogApi(Long paramLong)
  {
    setEnumPref(LOG_API, paramLong);
  }

  public Long getLogDatabase()
  {
    return getEnumPref(LOG_DATABASE);
  }

  public void setLogDatabase(Long paramLong)
  {
    setEnumPref(LOG_DATABASE, paramLong);
  }

  public Long getLogFilter()
  {
    return getEnumPref(LOG_FILTER);
  }

  public void setLogFilter(Long paramLong)
  {
    setEnumPref(LOG_FILTER, paramLong);
  }

  public Long getLogWebTimings()
  {
    return getEnumPref(LOG_WEB_TIMINGS);
  }

  public void setLogWebTimings(Long paramLong)
  {
    setEnumPref(LOG_WEB_TIMINGS, paramLong);
  }

  public String getDefaultFormView()
  {
    return get(DEFAULT_FORM_VIEW);
  }

  public void setDefaultFormView(String paramString)
  {
    put(DEFAULT_FORM_VIEW, paramString);
  }

  public Long getShowHiddenSchemas()
  {
    return getEnumPref(SHOW_HIDDEN_SCHEMAS);
  }

  public void setShowHiddenSchemas(Long paramLong)
  {
    setEnumPref(SHOW_HIDDEN_SCHEMAS, paramLong);
  }

  public String getOpenWindowViewExt()
  {
    return get(OPEN_WINDOW_VIEW_EXT);
  }

  public void setOpenWindowViewExt(String paramString)
  {
    put(OPEN_WINDOW_VIEW_EXT, paramString);
  }

  public Long getPaneLayout()
  {
    return getEnumPref(PANE_LAYOUT);
  }

  public void setPaneLayout(Long paramLong)
  {
    setEnumPref(PANE_LAYOUT, paramLong);
  }

  public Long getRefreshTableFields()
  {
    return getEnumPref(REFRESH_TABLE_FIELDS);
  }

  public void setRefreshTableFields(Long paramLong)
  {
    setEnumPref(REFRESH_TABLE_FIELDS, paramLong);
  }

  public Long getPreferredReportViewer()
  {
    return getEnumPref(PREFERRED_REPORT_VIEWER);
  }

  public void setPreferredReportViewer(Long paramLong)
  {
    setEnumPref(PREFERRED_REPORT_VIEWER, paramLong);
  }

  public String getReportServer()
  {
    return get(REPORT_SERVER);
  }

  public void setReportServer(String paramString)
  {
    put(REPORT_SERVER, paramString);
  }

  public int getAlertRefreshInterval()
  {
    return getIntegerPref(ALERT_REFRESH_INTERVAL);
  }

  public void setAlertRefreshInterval(int paramInt)
  {
    setIntegerPref(ALERT_REFRESH_INTERVAL, paramInt);
  }

  public String getAlertServers()
  {
    return get(ALERT_SERVERS);
  }

  public void setAlertServers(String paramString)
  {
    put(ALERT_SERVERS, paramString);
  }

  public String getUserLocale()
  {
    return get(USER_LOCALE);
  }

  public void setUserLocale(String paramString)
  {
    put(USER_LOCALE, paramString);
  }

  public String getTimeZone()
  {
    return get(TIME_ZONE);
  }

  public void setTimeZone(String paramString)
  {
    put(TIME_ZONE, paramString);
  }

  public Long getDisplayTimeFormat()
  {
    return getEnumPref(DISPLAY_TIME_FORMAT);
  }

  public void setDisplayTimeFormat(Long paramLong)
  {
    setEnumPref(DISPLAY_TIME_FORMAT, paramLong);
  }

  public String getCustomDateFormatStr()
  {
    return get(CUSTOM_DATE_FORMAT_STR);
  }

  public void setCustomDateFormatStr(String paramString)
  {
    put(CUSTOM_DATE_FORMAT_STR, paramString);
  }

  public String getInitialCurrencyCode()
  {
    return get(INITIAL_CURRENCY_CODE);
  }

  public void setInitialCurrencyCode(String paramString)
  {
    put(INITIAL_CURRENCY_CODE, paramString);
  }

  public String getCustomTimeFormatStr()
  {
    return get(CUSTOM_TIME_FORMAT_STR);
  }

  public void setCustomTimeFormatStr(String paramString)
  {
    put(CUSTOM_TIME_FORMAT_STR, paramString);
  }

  public Long getWebAccessibleMode()
  {
    return getEnumPref(WEB_ACCESSIBLE_MODE);
  }

  public void setWebAccessibleMode(Long paramLong)
  {
    setEnumPref(WEB_ACCESSIBLE_MODE, paramLong);
  }

  public Long getAccessibleMessage()
  {
    return getEnumPref(ACCESSIBLE_MESSAGE);
  }

  public void setAccessibleMessage(Long paramLong)
  {
    setEnumPref(ACCESSIBLE_MESSAGE, paramLong);
  }

  public int getSessionTimeoutInMinutes()
  {
    return getIntegerPref(SESSION_TIMEOUT_IN_MINUTES);
  }

  public void setSessionTimeoutInMinutes(int paramInt)
  {
    setIntegerPref(SESSION_TIMEOUT_IN_MINUTES, paramInt);
  }

  public String getHomePageServer()
  {
    return get(HOME_PAGE_SERVER);
  }

  public void setHomePageServer(String paramString)
  {
    put(HOME_PAGE_SERVER, paramString);
  }

  public String getHomePageForm()
  {
    return get(HOME_PAGE_FORM);
  }

  public void setHomePageForm(String paramString)
  {
    put(HOME_PAGE_FORM, paramString);
  }

  public String getTableColumnWidth()
  {
    return get(TABLE_COLUMN_WIDTH);
  }

  public void setTableColumnWidth(String paramString)
  {
    put(TABLE_COLUMN_WIDTH, paramString);
  }

  public String getTableColumnOrder()
  {
    return get(TABLE_COLUMN_ORDER);
  }

  public void setTableColumnOrder(String paramString)
  {
    put(TABLE_COLUMN_ORDER, paramString);
  }

  public String getTableColumnSort()
  {
    return get(TABLE_COLUMN_SORT);
  }

  public void setTableColumnSort(String paramString)
  {
    put(TABLE_COLUMN_SORT, paramString);
  }

  public String getTableRefreshInterval()
  {
    return get(TABLE_REFRESH_INTERVAL);
  }

  public void setTableRefreshInterval(String paramString)
  {
    put(TABLE_REFRESH_INTERVAL, paramString);
  }

  public Long getAnimationEffects()
  {
    return getEnumPref(ANIMATION_EFFECTS);
  }

  public void setAnimationEffects(Long paramLong)
  {
    setEnumPref(ANIMATION_EFFECTS, paramLong);
  }

  public int getHoverWaitTime()
  {
    return getIntegerPref(HOVER_WAIT_TIME);
  }

  public void setHoverWaitTime(int paramInt)
  {
    setIntegerPref(HOVER_WAIT_TIME, paramInt);
  }

  public Long getOpenAfterSubmit()
  {
    return getEnumPref(OPEN_AFTER_SUBMIT);
  }

  public void setOpenAfterSubmit(Long paramLong)
  {
    setEnumPref(OPEN_AFTER_SUBMIT, paramLong);
  }

  public Long getMenuType()
  {
    return getEnumPref(MENU_TYPE);
  }

  public void setMenuType(Long paramLong)
  {
    setEnumPref(MENU_TYPE, paramLong);
  }

  public int getSmartmenuItemThreshold()
  {
    return getIntegerPref(SMARTMENU_ITEM_THRESHOLD);
  }

  public void setSmartmenuItemThreshold(int paramInt)
  {
    setIntegerPref(SMARTMENU_ITEM_THRESHOLD, paramInt);
  }

  public int getSmartmenuLevelThreshold()
  {
    return getIntegerPref(SMARTMENU_LEVEL_THRESHOLD);
  }

  public void setSmartmenuLevelThreshold(int paramInt)
  {
    setIntegerPref(SMARTMENU_LEVEL_THRESHOLD, paramInt);
  }

  static
  {
    ON_NEW_SUBMIT = new ARUserPreferencesKey(20101);
    ON_NEW_QUERY = new ARUserPreferencesKey(20102);
    LIMIT_QUERY_ITEMS = new ARUserPreferencesKey(20103);
    MAX_QUERY_ITEMS = new ARUserPreferencesKey(20104);
    DIARY_SHOW_RECENT_FIRST = new ARUserPreferencesKey(20107);
    SHOW_ADVANCED_SEARCH_BAR = new ARUserPreferencesKey(23203);
    RECENT_USED_LIST_SIZE = new ARUserPreferencesKey(23206);
    MAX_WINDOW_ON_OPEN = new ARUserPreferencesKey(23204);
    CONFIRM_NEW_REQUEST = new ARUserPreferencesKey(20109);
    LOG_ACTIVE_LINKS = new ARUserPreferencesKey(20112);
    LOG_API = new ARUserPreferencesKey(20113);
    LOG_DATABASE = new ARUserPreferencesKey(20114);
    LOG_FILTER = new ARUserPreferencesKey(20115);
    LOG_WEB_TIMINGS = new ARUserPreferencesKey(23605);
    DEFAULT_FORM_VIEW = new ARUserPreferencesKey(20117);
    SHOW_HIDDEN_SCHEMAS = new ARUserPreferencesKey(20118);
    OPEN_WINDOW_VIEW_EXT = new ARUserPreferencesKey(20120);
    PANE_LAYOUT = new ARUserPreferencesKey(23207);
    REFRESH_TABLE_FIELDS = new ARUserPreferencesKey(20119);
    PREFERRED_REPORT_VIEWER = new ARUserPreferencesKey(24000);
    REPORT_SERVER = new ARUserPreferencesKey(24002);
    ALERT_REFRESH_INTERVAL = new ARUserPreferencesKey(24001);
    ALERT_SERVERS = new ARUserPreferencesKey(24004);
    USER_LOCALE = new ARUserPreferencesKey(20121);
    TIME_ZONE = new ARUserPreferencesKey(20123);
    DISPLAY_TIME_FORMAT = new ARUserPreferencesKey(24006);
    CUSTOM_DATE_FORMAT_STR = new ARUserPreferencesKey(24003);
    INITIAL_CURRENCY_CODE = new ARUserPreferencesKey(20130);
    CUSTOM_TIME_FORMAT_STR = new ARUserPreferencesKey(24015);
    WEB_ACCESSIBLE_MODE = new ARUserPreferencesKey(24007);
    ACCESSIBLE_MESSAGE = new ARUserPreferencesKey(24008);
    SESSION_TIMEOUT_IN_MINUTES = new ARUserPreferencesKey(24009);
    HOME_PAGE_SERVER = new ARUserPreferencesKey(20140);
    HOME_PAGE_FORM = new ARUserPreferencesKey(20141);
    TABLE_COLUMN_WIDTH = new ARUserPreferencesKey(23633);
    TABLE_COLUMN_ORDER = new ARUserPreferencesKey(20131);
    TABLE_COLUMN_SORT = new ARUserPreferencesKey(23634);
    TABLE_REFRESH_INTERVAL = new ARUserPreferencesKey(23628);
    ANIMATION_EFFECTS = new ARUserPreferencesKey(28510);
    HOVER_WAIT_TIME = new ARUserPreferencesKey(28514);
    OPEN_AFTER_SUBMIT = new ARUserPreferencesKey(28515);
    MENU_TYPE = new ARUserPreferencesKey(20105);
    SMARTMENU_ITEM_THRESHOLD = new ARUserPreferencesKey(23240);
    SMARTMENU_LEVEL_THRESHOLD = new ARUserPreferencesKey(23241);
    LAST_UPDATED_TIME = new ARUserPreferencesKey(CoreFieldId.ModifiedDate.getFieldId());
    PANE_TOP = new Long(3L);
    DIARY_YES = new Long(2L);
    HTML_FRAMES = new Long(4L);
    ACCESIBLE_MODE_VISUAL = new Long(1L);
    JAVA_JVM = new Long(0L);
    ACCESSIBLE_MODE_VOICE = new Long(2L);
    ACCESSIBLE_MODE_NONE = new Long(0L);
    NETSCAPE_PLUGIN = new Long(3L);
    DEFAULT_SESSION_TIMEOUT = new Integer(-1);
    PANE_RIGHT = new Long(2L);
    DIARY_DEFAULT = new Long(0L);
    DIARY_NO = new Long(1L);
    ACCESSIBLE_MSG_ALL_ACTIONS = new Long(2L);
    ACCESSIBLE_MSG_NONE = new Long(0L);
    DEFAULT_ALERT_REFRESH_INTERVAL = new Integer(0);
    LONG_TIME_FORMAT = new Long(2L);
    MENU_TYPE_LISTBOX = new Long(2L);
    DEFAULT_MAX_QUERY_ITEMS = new Integer(0);
    CLEAR_ALL = new Long(0L);
    JAVA_PLUGIN = new Long(1L);
    PANE_DEFAULT = new Long(0L);
    ACCESSIBLE_MSG_ACTION = new Long(1L);
    NO = new Long(0L);
    YES = new Long(1L);
    PANE_BOTTOM = new Long(4L);
    DEFAULT_RECENT_USED_LIST_SIZE = new Integer(5);
    SET_DEFAULTS = new Long(1L);
    MENU_TYPE_POPUP = new Long(1L);
    PANE_LEFT = new Long(1L);
    HTML_NOFRAMES = new Long(5L);
    MENU_TYPE_SMART = new Long(3L);
    KEEP_PREVIOUS = new Long(2L);
    SHORT_TIME_FORMAT = new Long(1L);
    ACTIVEX = new Long(2L);
    CUSTOM_TIME_FORMAT = new Long(0L);
    MENU_TYPE_DEFAULT = new Long(0L);
    FIELDS_TO_RETRIEVE_ARR = new ARVersionInfo[] { new ARVersionInfo("7.05.00", new EntryListFieldInfo[] { new EntryListFieldInfo(ON_NEW_SUBMIT.getFieldID()), new EntryListFieldInfo(ON_NEW_QUERY.getFieldID()), new EntryListFieldInfo(LIMIT_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(MAX_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(DIARY_SHOW_RECENT_FIRST.getFieldID()), new EntryListFieldInfo(SHOW_ADVANCED_SEARCH_BAR.getFieldID()), new EntryListFieldInfo(RECENT_USED_LIST_SIZE.getFieldID()), new EntryListFieldInfo(MAX_WINDOW_ON_OPEN.getFieldID()), new EntryListFieldInfo(CONFIRM_NEW_REQUEST.getFieldID()), new EntryListFieldInfo(LOG_ACTIVE_LINKS.getFieldID()), new EntryListFieldInfo(LOG_API.getFieldID()), new EntryListFieldInfo(LOG_DATABASE.getFieldID()), new EntryListFieldInfo(LOG_FILTER.getFieldID()), new EntryListFieldInfo(LOG_WEB_TIMINGS.getFieldID()), new EntryListFieldInfo(DEFAULT_FORM_VIEW.getFieldID()), new EntryListFieldInfo(SHOW_HIDDEN_SCHEMAS.getFieldID()), new EntryListFieldInfo(OPEN_WINDOW_VIEW_EXT.getFieldID()), new EntryListFieldInfo(PANE_LAYOUT.getFieldID()), new EntryListFieldInfo(REFRESH_TABLE_FIELDS.getFieldID()), new EntryListFieldInfo(PREFERRED_REPORT_VIEWER.getFieldID()), new EntryListFieldInfo(REPORT_SERVER.getFieldID()), new EntryListFieldInfo(ALERT_REFRESH_INTERVAL.getFieldID()), new EntryListFieldInfo(ALERT_SERVERS.getFieldID()), new EntryListFieldInfo(USER_LOCALE.getFieldID()), new EntryListFieldInfo(TIME_ZONE.getFieldID()), new EntryListFieldInfo(DISPLAY_TIME_FORMAT.getFieldID()), new EntryListFieldInfo(CUSTOM_DATE_FORMAT_STR.getFieldID()), new EntryListFieldInfo(INITIAL_CURRENCY_CODE.getFieldID()), new EntryListFieldInfo(CUSTOM_TIME_FORMAT_STR.getFieldID()), new EntryListFieldInfo(WEB_ACCESSIBLE_MODE.getFieldID()), new EntryListFieldInfo(ACCESSIBLE_MESSAGE.getFieldID()), new EntryListFieldInfo(SESSION_TIMEOUT_IN_MINUTES.getFieldID()), new EntryListFieldInfo(HOME_PAGE_SERVER.getFieldID()), new EntryListFieldInfo(HOME_PAGE_FORM.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_WIDTH.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_ORDER.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_SORT.getFieldID()), new EntryListFieldInfo(TABLE_REFRESH_INTERVAL.getFieldID()), new EntryListFieldInfo(ANIMATION_EFFECTS.getFieldID()), new EntryListFieldInfo(HOVER_WAIT_TIME.getFieldID()), new EntryListFieldInfo(OPEN_AFTER_SUBMIT.getFieldID()), new EntryListFieldInfo(MENU_TYPE.getFieldID()), new EntryListFieldInfo(SMARTMENU_ITEM_THRESHOLD.getFieldID()), new EntryListFieldInfo(SMARTMENU_LEVEL_THRESHOLD.getFieldID()), new EntryListFieldInfo(LAST_UPDATED_TIME.getFieldID()) }, new ARUserPreferencesKey[] { ON_NEW_SUBMIT, ON_NEW_QUERY, LIMIT_QUERY_ITEMS, MAX_QUERY_ITEMS, DIARY_SHOW_RECENT_FIRST, SHOW_ADVANCED_SEARCH_BAR, RECENT_USED_LIST_SIZE, MAX_WINDOW_ON_OPEN, CONFIRM_NEW_REQUEST, LOG_ACTIVE_LINKS, LOG_API, LOG_DATABASE, LOG_FILTER, LOG_WEB_TIMINGS, DEFAULT_FORM_VIEW, SHOW_HIDDEN_SCHEMAS, OPEN_WINDOW_VIEW_EXT, PANE_LAYOUT, REFRESH_TABLE_FIELDS, PREFERRED_REPORT_VIEWER, REPORT_SERVER, ALERT_REFRESH_INTERVAL, ALERT_SERVERS, USER_LOCALE, TIME_ZONE, DISPLAY_TIME_FORMAT, CUSTOM_DATE_FORMAT_STR, INITIAL_CURRENCY_CODE, CUSTOM_TIME_FORMAT_STR, WEB_ACCESSIBLE_MODE, ACCESSIBLE_MESSAGE, SESSION_TIMEOUT_IN_MINUTES, HOME_PAGE_SERVER, HOME_PAGE_FORM, TABLE_COLUMN_WIDTH, TABLE_COLUMN_ORDER, TABLE_COLUMN_SORT, TABLE_REFRESH_INTERVAL, ANIMATION_EFFECTS, HOVER_WAIT_TIME, OPEN_AFTER_SUBMIT, MENU_TYPE, SMARTMENU_ITEM_THRESHOLD, SMARTMENU_LEVEL_THRESHOLD, LAST_UPDATED_TIME }, null), new ARVersionInfo("7.01.00", new EntryListFieldInfo[] { new EntryListFieldInfo(ON_NEW_SUBMIT.getFieldID()), new EntryListFieldInfo(ON_NEW_QUERY.getFieldID()), new EntryListFieldInfo(LIMIT_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(MAX_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(DIARY_SHOW_RECENT_FIRST.getFieldID()), new EntryListFieldInfo(SHOW_ADVANCED_SEARCH_BAR.getFieldID()), new EntryListFieldInfo(RECENT_USED_LIST_SIZE.getFieldID()), new EntryListFieldInfo(MAX_WINDOW_ON_OPEN.getFieldID()), new EntryListFieldInfo(CONFIRM_NEW_REQUEST.getFieldID()), new EntryListFieldInfo(LOG_ACTIVE_LINKS.getFieldID()), new EntryListFieldInfo(LOG_API.getFieldID()), new EntryListFieldInfo(LOG_DATABASE.getFieldID()), new EntryListFieldInfo(LOG_FILTER.getFieldID()), new EntryListFieldInfo(DEFAULT_FORM_VIEW.getFieldID()), new EntryListFieldInfo(SHOW_HIDDEN_SCHEMAS.getFieldID()), new EntryListFieldInfo(OPEN_WINDOW_VIEW_EXT.getFieldID()), new EntryListFieldInfo(PANE_LAYOUT.getFieldID()), new EntryListFieldInfo(REFRESH_TABLE_FIELDS.getFieldID()), new EntryListFieldInfo(PREFERRED_REPORT_VIEWER.getFieldID()), new EntryListFieldInfo(REPORT_SERVER.getFieldID()), new EntryListFieldInfo(ALERT_REFRESH_INTERVAL.getFieldID()), new EntryListFieldInfo(ALERT_SERVERS.getFieldID()), new EntryListFieldInfo(USER_LOCALE.getFieldID()), new EntryListFieldInfo(TIME_ZONE.getFieldID()), new EntryListFieldInfo(DISPLAY_TIME_FORMAT.getFieldID()), new EntryListFieldInfo(CUSTOM_DATE_FORMAT_STR.getFieldID()), new EntryListFieldInfo(INITIAL_CURRENCY_CODE.getFieldID()), new EntryListFieldInfo(CUSTOM_TIME_FORMAT_STR.getFieldID()), new EntryListFieldInfo(WEB_ACCESSIBLE_MODE.getFieldID()), new EntryListFieldInfo(ACCESSIBLE_MESSAGE.getFieldID()), new EntryListFieldInfo(SESSION_TIMEOUT_IN_MINUTES.getFieldID()), new EntryListFieldInfo(HOME_PAGE_SERVER.getFieldID()), new EntryListFieldInfo(HOME_PAGE_FORM.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_WIDTH.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_ORDER.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_SORT.getFieldID()), new EntryListFieldInfo(TABLE_REFRESH_INTERVAL.getFieldID()), new EntryListFieldInfo(MENU_TYPE.getFieldID()), new EntryListFieldInfo(SMARTMENU_ITEM_THRESHOLD.getFieldID()), new EntryListFieldInfo(SMARTMENU_LEVEL_THRESHOLD.getFieldID()), new EntryListFieldInfo(LAST_UPDATED_TIME.getFieldID()) }, new ARUserPreferencesKey[] { ON_NEW_SUBMIT, ON_NEW_QUERY, LIMIT_QUERY_ITEMS, MAX_QUERY_ITEMS, DIARY_SHOW_RECENT_FIRST, SHOW_ADVANCED_SEARCH_BAR, RECENT_USED_LIST_SIZE, MAX_WINDOW_ON_OPEN, CONFIRM_NEW_REQUEST, LOG_ACTIVE_LINKS, LOG_API, LOG_DATABASE, LOG_FILTER, DEFAULT_FORM_VIEW, SHOW_HIDDEN_SCHEMAS, OPEN_WINDOW_VIEW_EXT, PANE_LAYOUT, REFRESH_TABLE_FIELDS, PREFERRED_REPORT_VIEWER, REPORT_SERVER, ALERT_REFRESH_INTERVAL, ALERT_SERVERS, USER_LOCALE, TIME_ZONE, DISPLAY_TIME_FORMAT, CUSTOM_DATE_FORMAT_STR, INITIAL_CURRENCY_CODE, CUSTOM_TIME_FORMAT_STR, WEB_ACCESSIBLE_MODE, ACCESSIBLE_MESSAGE, SESSION_TIMEOUT_IN_MINUTES, HOME_PAGE_SERVER, HOME_PAGE_FORM, TABLE_COLUMN_WIDTH, TABLE_COLUMN_ORDER, TABLE_COLUMN_SORT, TABLE_REFRESH_INTERVAL, MENU_TYPE, SMARTMENU_ITEM_THRESHOLD, SMARTMENU_LEVEL_THRESHOLD, LAST_UPDATED_TIME }, null), new ARVersionInfo("7.00.00", new EntryListFieldInfo[] { new EntryListFieldInfo(ON_NEW_SUBMIT.getFieldID()), new EntryListFieldInfo(ON_NEW_QUERY.getFieldID()), new EntryListFieldInfo(LIMIT_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(MAX_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(DIARY_SHOW_RECENT_FIRST.getFieldID()), new EntryListFieldInfo(SHOW_ADVANCED_SEARCH_BAR.getFieldID()), new EntryListFieldInfo(RECENT_USED_LIST_SIZE.getFieldID()), new EntryListFieldInfo(MAX_WINDOW_ON_OPEN.getFieldID()), new EntryListFieldInfo(CONFIRM_NEW_REQUEST.getFieldID()), new EntryListFieldInfo(LOG_ACTIVE_LINKS.getFieldID()), new EntryListFieldInfo(LOG_API.getFieldID()), new EntryListFieldInfo(LOG_DATABASE.getFieldID()), new EntryListFieldInfo(LOG_FILTER.getFieldID()), new EntryListFieldInfo(DEFAULT_FORM_VIEW.getFieldID()), new EntryListFieldInfo(SHOW_HIDDEN_SCHEMAS.getFieldID()), new EntryListFieldInfo(OPEN_WINDOW_VIEW_EXT.getFieldID()), new EntryListFieldInfo(PANE_LAYOUT.getFieldID()), new EntryListFieldInfo(REFRESH_TABLE_FIELDS.getFieldID()), new EntryListFieldInfo(PREFERRED_REPORT_VIEWER.getFieldID()), new EntryListFieldInfo(REPORT_SERVER.getFieldID()), new EntryListFieldInfo(ALERT_REFRESH_INTERVAL.getFieldID()), new EntryListFieldInfo(ALERT_SERVERS.getFieldID()), new EntryListFieldInfo(USER_LOCALE.getFieldID()), new EntryListFieldInfo(TIME_ZONE.getFieldID()), new EntryListFieldInfo(DISPLAY_TIME_FORMAT.getFieldID()), new EntryListFieldInfo(CUSTOM_DATE_FORMAT_STR.getFieldID()), new EntryListFieldInfo(INITIAL_CURRENCY_CODE.getFieldID()), new EntryListFieldInfo(CUSTOM_TIME_FORMAT_STR.getFieldID()), new EntryListFieldInfo(WEB_ACCESSIBLE_MODE.getFieldID()), new EntryListFieldInfo(ACCESSIBLE_MESSAGE.getFieldID()), new EntryListFieldInfo(SESSION_TIMEOUT_IN_MINUTES.getFieldID()), new EntryListFieldInfo(HOME_PAGE_SERVER.getFieldID()), new EntryListFieldInfo(HOME_PAGE_FORM.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_WIDTH.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_ORDER.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_SORT.getFieldID()), new EntryListFieldInfo(MENU_TYPE.getFieldID()), new EntryListFieldInfo(SMARTMENU_ITEM_THRESHOLD.getFieldID()), new EntryListFieldInfo(SMARTMENU_LEVEL_THRESHOLD.getFieldID()), new EntryListFieldInfo(LAST_UPDATED_TIME.getFieldID()) }, new ARUserPreferencesKey[] { ON_NEW_SUBMIT, ON_NEW_QUERY, LIMIT_QUERY_ITEMS, MAX_QUERY_ITEMS, DIARY_SHOW_RECENT_FIRST, SHOW_ADVANCED_SEARCH_BAR, RECENT_USED_LIST_SIZE, MAX_WINDOW_ON_OPEN, CONFIRM_NEW_REQUEST, LOG_ACTIVE_LINKS, LOG_API, LOG_DATABASE, LOG_FILTER, DEFAULT_FORM_VIEW, SHOW_HIDDEN_SCHEMAS, OPEN_WINDOW_VIEW_EXT, PANE_LAYOUT, REFRESH_TABLE_FIELDS, PREFERRED_REPORT_VIEWER, REPORT_SERVER, ALERT_REFRESH_INTERVAL, ALERT_SERVERS, USER_LOCALE, TIME_ZONE, DISPLAY_TIME_FORMAT, CUSTOM_DATE_FORMAT_STR, INITIAL_CURRENCY_CODE, CUSTOM_TIME_FORMAT_STR, WEB_ACCESSIBLE_MODE, ACCESSIBLE_MESSAGE, SESSION_TIMEOUT_IN_MINUTES, HOME_PAGE_SERVER, HOME_PAGE_FORM, TABLE_COLUMN_WIDTH, TABLE_COLUMN_ORDER, TABLE_COLUMN_SORT, MENU_TYPE, SMARTMENU_ITEM_THRESHOLD, SMARTMENU_LEVEL_THRESHOLD, LAST_UPDATED_TIME }, null), new ARVersionInfo("6.03.00", new EntryListFieldInfo[] { new EntryListFieldInfo(ON_NEW_SUBMIT.getFieldID()), new EntryListFieldInfo(ON_NEW_QUERY.getFieldID()), new EntryListFieldInfo(LIMIT_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(MAX_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(DIARY_SHOW_RECENT_FIRST.getFieldID()), new EntryListFieldInfo(SHOW_ADVANCED_SEARCH_BAR.getFieldID()), new EntryListFieldInfo(RECENT_USED_LIST_SIZE.getFieldID()), new EntryListFieldInfo(MAX_WINDOW_ON_OPEN.getFieldID()), new EntryListFieldInfo(CONFIRM_NEW_REQUEST.getFieldID()), new EntryListFieldInfo(LOG_ACTIVE_LINKS.getFieldID()), new EntryListFieldInfo(LOG_API.getFieldID()), new EntryListFieldInfo(LOG_DATABASE.getFieldID()), new EntryListFieldInfo(LOG_FILTER.getFieldID()), new EntryListFieldInfo(DEFAULT_FORM_VIEW.getFieldID()), new EntryListFieldInfo(SHOW_HIDDEN_SCHEMAS.getFieldID()), new EntryListFieldInfo(OPEN_WINDOW_VIEW_EXT.getFieldID()), new EntryListFieldInfo(PANE_LAYOUT.getFieldID()), new EntryListFieldInfo(REFRESH_TABLE_FIELDS.getFieldID()), new EntryListFieldInfo(PREFERRED_REPORT_VIEWER.getFieldID()), new EntryListFieldInfo(REPORT_SERVER.getFieldID()), new EntryListFieldInfo(ALERT_REFRESH_INTERVAL.getFieldID()), new EntryListFieldInfo(ALERT_SERVERS.getFieldID()), new EntryListFieldInfo(USER_LOCALE.getFieldID()), new EntryListFieldInfo(TIME_ZONE.getFieldID()), new EntryListFieldInfo(DISPLAY_TIME_FORMAT.getFieldID()), new EntryListFieldInfo(CUSTOM_DATE_FORMAT_STR.getFieldID()), new EntryListFieldInfo(INITIAL_CURRENCY_CODE.getFieldID()), new EntryListFieldInfo(CUSTOM_TIME_FORMAT_STR.getFieldID()), new EntryListFieldInfo(WEB_ACCESSIBLE_MODE.getFieldID()), new EntryListFieldInfo(ACCESSIBLE_MESSAGE.getFieldID()), new EntryListFieldInfo(SESSION_TIMEOUT_IN_MINUTES.getFieldID()), new EntryListFieldInfo(HOME_PAGE_SERVER.getFieldID()), new EntryListFieldInfo(HOME_PAGE_FORM.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_WIDTH.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_SORT.getFieldID()), new EntryListFieldInfo(MENU_TYPE.getFieldID()), new EntryListFieldInfo(SMARTMENU_ITEM_THRESHOLD.getFieldID()), new EntryListFieldInfo(SMARTMENU_LEVEL_THRESHOLD.getFieldID()), new EntryListFieldInfo(LAST_UPDATED_TIME.getFieldID()) }, new ARUserPreferencesKey[] { ON_NEW_SUBMIT, ON_NEW_QUERY, LIMIT_QUERY_ITEMS, MAX_QUERY_ITEMS, DIARY_SHOW_RECENT_FIRST, SHOW_ADVANCED_SEARCH_BAR, RECENT_USED_LIST_SIZE, MAX_WINDOW_ON_OPEN, CONFIRM_NEW_REQUEST, LOG_ACTIVE_LINKS, LOG_API, LOG_DATABASE, LOG_FILTER, DEFAULT_FORM_VIEW, SHOW_HIDDEN_SCHEMAS, OPEN_WINDOW_VIEW_EXT, PANE_LAYOUT, REFRESH_TABLE_FIELDS, PREFERRED_REPORT_VIEWER, REPORT_SERVER, ALERT_REFRESH_INTERVAL, ALERT_SERVERS, USER_LOCALE, TIME_ZONE, DISPLAY_TIME_FORMAT, CUSTOM_DATE_FORMAT_STR, INITIAL_CURRENCY_CODE, CUSTOM_TIME_FORMAT_STR, WEB_ACCESSIBLE_MODE, ACCESSIBLE_MESSAGE, SESSION_TIMEOUT_IN_MINUTES, HOME_PAGE_SERVER, HOME_PAGE_FORM, TABLE_COLUMN_WIDTH, TABLE_COLUMN_SORT, MENU_TYPE, SMARTMENU_ITEM_THRESHOLD, SMARTMENU_LEVEL_THRESHOLD, LAST_UPDATED_TIME }, null), new ARVersionInfo("6.00.00", new EntryListFieldInfo[] { new EntryListFieldInfo(ON_NEW_SUBMIT.getFieldID()), new EntryListFieldInfo(ON_NEW_QUERY.getFieldID()), new EntryListFieldInfo(LIMIT_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(MAX_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(DIARY_SHOW_RECENT_FIRST.getFieldID()), new EntryListFieldInfo(SHOW_ADVANCED_SEARCH_BAR.getFieldID()), new EntryListFieldInfo(RECENT_USED_LIST_SIZE.getFieldID()), new EntryListFieldInfo(MAX_WINDOW_ON_OPEN.getFieldID()), new EntryListFieldInfo(CONFIRM_NEW_REQUEST.getFieldID()), new EntryListFieldInfo(LOG_ACTIVE_LINKS.getFieldID()), new EntryListFieldInfo(LOG_API.getFieldID()), new EntryListFieldInfo(LOG_DATABASE.getFieldID()), new EntryListFieldInfo(LOG_FILTER.getFieldID()), new EntryListFieldInfo(DEFAULT_FORM_VIEW.getFieldID()), new EntryListFieldInfo(SHOW_HIDDEN_SCHEMAS.getFieldID()), new EntryListFieldInfo(OPEN_WINDOW_VIEW_EXT.getFieldID()), new EntryListFieldInfo(PANE_LAYOUT.getFieldID()), new EntryListFieldInfo(REFRESH_TABLE_FIELDS.getFieldID()), new EntryListFieldInfo(PREFERRED_REPORT_VIEWER.getFieldID()), new EntryListFieldInfo(REPORT_SERVER.getFieldID()), new EntryListFieldInfo(ALERT_REFRESH_INTERVAL.getFieldID()), new EntryListFieldInfo(ALERT_SERVERS.getFieldID()), new EntryListFieldInfo(USER_LOCALE.getFieldID()), new EntryListFieldInfo(TIME_ZONE.getFieldID()), new EntryListFieldInfo(DISPLAY_TIME_FORMAT.getFieldID()), new EntryListFieldInfo(CUSTOM_DATE_FORMAT_STR.getFieldID()), new EntryListFieldInfo(INITIAL_CURRENCY_CODE.getFieldID()), new EntryListFieldInfo(WEB_ACCESSIBLE_MODE.getFieldID()), new EntryListFieldInfo(ACCESSIBLE_MESSAGE.getFieldID()), new EntryListFieldInfo(SESSION_TIMEOUT_IN_MINUTES.getFieldID()), new EntryListFieldInfo(HOME_PAGE_SERVER.getFieldID()), new EntryListFieldInfo(HOME_PAGE_FORM.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_WIDTH.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_SORT.getFieldID()), new EntryListFieldInfo(MENU_TYPE.getFieldID()), new EntryListFieldInfo(SMARTMENU_ITEM_THRESHOLD.getFieldID()), new EntryListFieldInfo(SMARTMENU_LEVEL_THRESHOLD.getFieldID()), new EntryListFieldInfo(LAST_UPDATED_TIME.getFieldID()) }, new ARUserPreferencesKey[] { ON_NEW_SUBMIT, ON_NEW_QUERY, LIMIT_QUERY_ITEMS, MAX_QUERY_ITEMS, DIARY_SHOW_RECENT_FIRST, SHOW_ADVANCED_SEARCH_BAR, RECENT_USED_LIST_SIZE, MAX_WINDOW_ON_OPEN, CONFIRM_NEW_REQUEST, LOG_ACTIVE_LINKS, LOG_API, LOG_DATABASE, LOG_FILTER, DEFAULT_FORM_VIEW, SHOW_HIDDEN_SCHEMAS, OPEN_WINDOW_VIEW_EXT, PANE_LAYOUT, REFRESH_TABLE_FIELDS, PREFERRED_REPORT_VIEWER, REPORT_SERVER, ALERT_REFRESH_INTERVAL, ALERT_SERVERS, USER_LOCALE, TIME_ZONE, DISPLAY_TIME_FORMAT, CUSTOM_DATE_FORMAT_STR, INITIAL_CURRENCY_CODE, WEB_ACCESSIBLE_MODE, ACCESSIBLE_MESSAGE, SESSION_TIMEOUT_IN_MINUTES, HOME_PAGE_SERVER, HOME_PAGE_FORM, TABLE_COLUMN_WIDTH, TABLE_COLUMN_SORT, MENU_TYPE, SMARTMENU_ITEM_THRESHOLD, SMARTMENU_LEVEL_THRESHOLD, LAST_UPDATED_TIME }, null), new ARVersionInfo("5.00.00", new EntryListFieldInfo[] { new EntryListFieldInfo(ON_NEW_SUBMIT.getFieldID()), new EntryListFieldInfo(ON_NEW_QUERY.getFieldID()), new EntryListFieldInfo(LIMIT_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(MAX_QUERY_ITEMS.getFieldID()), new EntryListFieldInfo(DIARY_SHOW_RECENT_FIRST.getFieldID()), new EntryListFieldInfo(SHOW_ADVANCED_SEARCH_BAR.getFieldID()), new EntryListFieldInfo(RECENT_USED_LIST_SIZE.getFieldID()), new EntryListFieldInfo(MAX_WINDOW_ON_OPEN.getFieldID()), new EntryListFieldInfo(CONFIRM_NEW_REQUEST.getFieldID()), new EntryListFieldInfo(LOG_ACTIVE_LINKS.getFieldID()), new EntryListFieldInfo(LOG_API.getFieldID()), new EntryListFieldInfo(LOG_DATABASE.getFieldID()), new EntryListFieldInfo(LOG_FILTER.getFieldID()), new EntryListFieldInfo(DEFAULT_FORM_VIEW.getFieldID()), new EntryListFieldInfo(SHOW_HIDDEN_SCHEMAS.getFieldID()), new EntryListFieldInfo(PANE_LAYOUT.getFieldID()), new EntryListFieldInfo(REFRESH_TABLE_FIELDS.getFieldID()), new EntryListFieldInfo(PREFERRED_REPORT_VIEWER.getFieldID()), new EntryListFieldInfo(REPORT_SERVER.getFieldID()), new EntryListFieldInfo(ALERT_REFRESH_INTERVAL.getFieldID()), new EntryListFieldInfo(ALERT_SERVERS.getFieldID()), new EntryListFieldInfo(USER_LOCALE.getFieldID()), new EntryListFieldInfo(TIME_ZONE.getFieldID()), new EntryListFieldInfo(DISPLAY_TIME_FORMAT.getFieldID()), new EntryListFieldInfo(CUSTOM_DATE_FORMAT_STR.getFieldID()), new EntryListFieldInfo(INITIAL_CURRENCY_CODE.getFieldID()), new EntryListFieldInfo(WEB_ACCESSIBLE_MODE.getFieldID()), new EntryListFieldInfo(ACCESSIBLE_MESSAGE.getFieldID()), new EntryListFieldInfo(SESSION_TIMEOUT_IN_MINUTES.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_WIDTH.getFieldID()), new EntryListFieldInfo(TABLE_COLUMN_SORT.getFieldID()), new EntryListFieldInfo(MENU_TYPE.getFieldID()), new EntryListFieldInfo(SMARTMENU_ITEM_THRESHOLD.getFieldID()), new EntryListFieldInfo(SMARTMENU_LEVEL_THRESHOLD.getFieldID()), new EntryListFieldInfo(LAST_UPDATED_TIME.getFieldID()) }, new ARUserPreferencesKey[] { ON_NEW_SUBMIT, ON_NEW_QUERY, LIMIT_QUERY_ITEMS, MAX_QUERY_ITEMS, DIARY_SHOW_RECENT_FIRST, SHOW_ADVANCED_SEARCH_BAR, RECENT_USED_LIST_SIZE, MAX_WINDOW_ON_OPEN, CONFIRM_NEW_REQUEST, LOG_ACTIVE_LINKS, LOG_API, LOG_DATABASE, LOG_FILTER, DEFAULT_FORM_VIEW, SHOW_HIDDEN_SCHEMAS, PANE_LAYOUT, REFRESH_TABLE_FIELDS, PREFERRED_REPORT_VIEWER, REPORT_SERVER, ALERT_REFRESH_INTERVAL, ALERT_SERVERS, USER_LOCALE, TIME_ZONE, DISPLAY_TIME_FORMAT, CUSTOM_DATE_FORMAT_STR, INITIAL_CURRENCY_CODE, WEB_ACCESSIBLE_MODE, ACCESSIBLE_MESSAGE, SESSION_TIMEOUT_IN_MINUTES, TABLE_COLUMN_WIDTH, TABLE_COLUMN_SORT, MENU_TYPE, SMARTMENU_ITEM_THRESHOLD, SMARTMENU_LEVEL_THRESHOLD, LAST_UPDATED_TIME }, null) };
    BROWSER_PREFS = new ARUserPreferencesKey[] { ON_NEW_SUBMIT, ON_NEW_QUERY, SHOW_ADVANCED_SEARCH_BAR, MAX_WINDOW_ON_OPEN, CONFIRM_NEW_REQUEST, LOG_ACTIVE_LINKS, LOG_API, LOG_DATABASE, LOG_FILTER, LOG_WEB_TIMINGS, PANE_LAYOUT, REFRESH_TABLE_FIELDS, REPORT_SERVER, ALERT_REFRESH_INTERVAL, INITIAL_CURRENCY_CODE, WEB_ACCESSIBLE_MODE, ACCESSIBLE_MESSAGE, TABLE_COLUMN_WIDTH, TABLE_COLUMN_ORDER, TABLE_COLUMN_SORT, TABLE_REFRESH_INTERVAL, ANIMATION_EFFECTS, HOVER_WAIT_TIME, OPEN_AFTER_SUBMIT, MENU_TYPE, SMARTMENU_ITEM_THRESHOLD, SMARTMENU_LEVEL_THRESHOLD };
    HashMap localHashMap = new HashMap();
    localHashMap.put(ON_NEW_SUBMIT, SET_DEFAULTS.toString());
    localHashMap.put(ON_NEW_QUERY, CLEAR_ALL.toString());
    localHashMap.put(LIMIT_QUERY_ITEMS, YES.toString());
    localHashMap.put(MAX_QUERY_ITEMS, DEFAULT_MAX_QUERY_ITEMS.toString());
    localHashMap.put(DIARY_SHOW_RECENT_FIRST, DIARY_YES.toString());
    localHashMap.put(SHOW_ADVANCED_SEARCH_BAR, NO.toString());
    localHashMap.put(RECENT_USED_LIST_SIZE, DEFAULT_RECENT_USED_LIST_SIZE.toString());
    localHashMap.put(MAX_WINDOW_ON_OPEN, NO.toString());
    localHashMap.put(CONFIRM_NEW_REQUEST, NO.toString());
    localHashMap.put(LOG_ACTIVE_LINKS, NO.toString());
    localHashMap.put(LOG_API, NO.toString());
    localHashMap.put(LOG_DATABASE, NO.toString());
    localHashMap.put(LOG_FILTER, NO.toString());
    localHashMap.put(LOG_WEB_TIMINGS, NO.toString());
    localHashMap.put(SHOW_HIDDEN_SCHEMAS, YES.toString());
    localHashMap.put(PANE_LAYOUT, PANE_DEFAULT.toString());
    localHashMap.put(REFRESH_TABLE_FIELDS, YES.toString());
    localHashMap.put(PREFERRED_REPORT_VIEWER, HTML_NOFRAMES.toString());
    localHashMap.put(ALERT_REFRESH_INTERVAL, DEFAULT_ALERT_REFRESH_INTERVAL.toString());
    localHashMap.put(DISPLAY_TIME_FORMAT, SHORT_TIME_FORMAT.toString());
    localHashMap.put(WEB_ACCESSIBLE_MODE, ACCESSIBLE_MODE_NONE.toString());
    localHashMap.put(ACCESSIBLE_MESSAGE, ACCESSIBLE_MSG_NONE.toString());
    localHashMap.put(SESSION_TIMEOUT_IN_MINUTES, DEFAULT_SESSION_TIMEOUT.toString());
    localHashMap.put(ANIMATION_EFFECTS, YES.toString());
    localHashMap.put(OPEN_AFTER_SUBMIT, NO.toString());
    localHashMap.put(MENU_TYPE, MENU_TYPE_DEFAULT.toString());
  }

  protected static class ARVersionInfo
  {
    String ver;
    EntryListFieldInfo[] ec;
    ARUserPreferencesKey[] keys;

    private ARVersionInfo(String paramString, EntryListFieldInfo[] paramArrayOfEntryListFieldInfo, ARUserPreferencesKey[] paramArrayOfARUserPreferencesKey)
    {
      this.ver = paramString;
      this.ec = paramArrayOfEntryListFieldInfo;
      this.keys = paramArrayOfARUserPreferencesKey;
    }

    public EntryListFieldInfo[] getEntryCriteria()
    {
      return this.ec;
    }

    public ARUserPreferencesKey[] getKnownKeys()
    {
      return this.keys;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.preferences.AbstractARUserPreferences
 * JD-Core Version:    0.6.1
 */