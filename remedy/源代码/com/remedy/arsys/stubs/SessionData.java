package com.remedy.arsys.stubs;

import com.bmc.arsys.api.LoggingInfo;
import com.bmc.arsys.api.Value;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.config.Configuration.ServerInformation;
import com.remedy.arsys.goat.DateTimePattern;
import com.remedy.arsys.goat.Globule;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.LocaleUtil;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.goat.quickreports.QuickReportCache;
import com.remedy.arsys.goat.savesearches.ARUserSearches;
import com.remedy.arsys.goat.service.DHTMLRequestService.CHPContent;
import com.remedy.arsys.log.ARServerLog;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.session.UserCredentials;
import com.remedy.arsys.share.ActorViewCache;
import com.remedy.arsys.share.CacheDirectiveController.TimeHashMap;
import com.remedy.arsys.share.UserActorCache;
import com.remedy.arsys.support.PwdUtility;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionData
  implements Serializable
{
  public boolean isUseServer = false;
  private transient Calendar mCalendar;
  private transient Calendar mLocalisedCalendar;
  private transient SimpleDateFormat mLocalisedSimpleDateFormat;
  private transient SimpleDateFormat mTimeOnlySimpleDateFormat;
  private transient SimpleDateFormat mDateOnlyShortSimpleDateFormat;
  private final Set mEventListeners = new HashSet();
  private String mUser;
  private transient HashMap mServerCacheIds;
  private transient int mFormRequestCount = 0;
  private Globule staticUserDataJS = null;
  private transient boolean isModified = false;
  private Map<String, String> searchesSrvPrefFormMap;
  private transient Map<Integer, String> chpContentURLMap;
  private transient Map<Integer, DHTMLRequestService.CHPContent> chpContentOBJMap;
  private transient String mPassword;
  private String mAuthentication;
  private final Map<String, UserActorCache> userActorMap = new HashMap(10);
  private final Map<String, ActorViewCache> actorViewMap = new HashMap(10);
  private transient ServerLoginHost mLoginHost;
  private static final Value AR_MIDTIER_CTYPE = new Value(9);
  private Value mClientType = null;
  private transient ARUserPreferences mPreferences;
  private String mLocale;
  private String mTimeZone;
  private String cTimeZone;
  private static String mDefaultTimeZone = null;
  private transient LoggingInfo mLoggingInfo;
  private transient ARServerLog mServerLog;
  private final Map<String, String> mWOArgumentsMap = Collections.synchronizedMap(new HashMap());
  private final Map<String, String> mQBEMap = Collections.synchronizedMap(new HashMap());
  private String mID;
  private String mGUID;
  private int mOverride;
  private transient ARUserSearches mSearches;
  private transient CacheDirectiveController.TimeHashMap<String, HashMap<String, QuickReportCache>> mQuickReports;
  public static final String SESSION_DATA = "com.remedy.arsys.stubs.sessionData";
  private static Log mSessionLog = Log.get(2);
  private static final Map allSessionData = new HashMap();
  private boolean mLoggedOut = false;
  private String mPasswordChangeTargetURL;
  private boolean mIsPasswordChangeRequired = false;
  private boolean mWebTimings = false;
  private boolean mEnableFlex = false;
  private boolean mGroupModified = false;
  private static ThreadLocal MSessionLocal = new ThreadLocal()
  {
    protected synchronized Object initialValue()
    {
      return null;
    }
  };
  private transient DateTimePattern mDateTimePatternObj;

  public static void set(SessionData paramSessionData)
  {
    MSessionLocal.set(paramSessionData);
  }

  public static SessionData get()
  {
    SessionData localSessionData = (SessionData)MSessionLocal.get();
    assert (localSessionData != null);
    return localSessionData;
  }

  public static SessionData tryGet()
  {
    SessionData localSessionData = (SessionData)MSessionLocal.get();
    return localSessionData;
  }

  public SessionData()
    throws GoatException
  {
    this(Configuration.getInstance().getProperty("ardev.login.user", "Demo"), Configuration.getInstance().getProperty("ardev.login.password", ""), Configuration.getInstance().getProperty("ardev.login.authentication", ""), null, null, "GUID", "DebugDummySession");
    System.out.println("Warning: Using dummy debug-mode session data");
  }

  public SessionData(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, Value paramValue)
    throws GoatException
  {
    this(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramValue, false);
  }

  public SessionData(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, Value paramValue, boolean paramBoolean)
    throws GoatException
  {
    assert (paramString1 != null);
    assert (paramString7 != null);
    SessionData localSessionData = null;
    try
    {
      this.mDateTimePatternObj = new DateTimePattern(ARUserPreferences.SHORT_TIME_FORMAT);
      localSessionData = tryGet();
      set(this);
      this.mUser = paramString1;
      this.mPassword = paramString2;
      this.mIsPasswordChangeRequired = (paramBoolean ? false : Login.isPasswordChangeRequired(new UserCredentials(paramString1, paramString2, null)));
      this.mAuthentication = paramString3;
      this.mClientType = paramValue;
      assert (this.mClientType != null);
      this.mGUID = paramString6;
      this.mOverride = -1;
      this.mID = paramString7;
      this.mLoginHost = new SessionDataHost();
      this.mPreferences = ARUserPreferences.getUserPreferences();
      this.mLoginHost.setHostLoggingOut();
      assert (this.mPreferences != null);
      String str = this.mPreferences.getUserLocale();
      if ((str == null) || (str.trim().equals("")))
      {
        str = paramString4;
        if ((str == null) || (str.trim().equals("")))
          str = Locale.getDefault().toString();
      }
      assert (str != null);
      this.mLocale = (str.length() > 2 ? str : LocaleUtil.mapToLocaleWithCountryCode(str));
      this.mLocale = LocaleUtil.getNormalizedLocale(this.mLocale);
      if ((this.mLocale == null) || (this.mLocale.trim().equals("")))
        this.mLocale = LocaleUtil.getNormalizedLocale(Locale.getDefault().toString());
      initDateTimeFormatPattern();
      initTimezone(paramString5);
      boolean bool1 = this.mPreferences.getLogApi().equals(ARUserPreferences.YES);
      boolean bool2 = this.mPreferences.getLogDatabase().equals(ARUserPreferences.YES);
      boolean bool3 = this.mPreferences.getLogFilter().equals(ARUserPreferences.YES);
      boolean bool4 = (bool1) || (bool2) || (bool3);
      this.mWebTimings = this.mPreferences.getLogWebTimings().equals(ARUserPreferences.YES);
      if ((bool1) || (bool2) || (bool3) || (this.mWebTimings))
        this.mServerLog = new ARServerLog();
      this.mLoggingInfo = new LoggingInfo(bool4, (bool1 ? 16 : 0) | (bool2 ? 1 : 0) | (bool3 ? 2 : 0), 2L, null);
      this.mLoginHost = new SessionDataHost();
    }
    finally
    {
      set(localSessionData);
    }
    synchronized (allSessionData)
    {
      Set localSet = getSessionDataSetFromCentral(paramString1);
      localSet.add(this);
    }
  }

  private static Set getSessionDataSetFromCentral(String paramString)
  {
    Object localObject = (Set)allSessionData.get(paramString);
    if (localObject == null)
    {
      localObject = new LinkedHashSet();
      allSessionData.put(paramString, localObject);
    }
    return localObject;
  }

  private static void removeSessionDataSetFromCentral(String paramString)
  {
    allSessionData.remove(paramString);
  }

  public SessionData(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
    throws GoatException
  {
    this(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, AR_MIDTIER_CTYPE);
  }

  public SessionData(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, boolean paramBoolean)
    throws GoatException
  {
    this(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, AR_MIDTIER_CTYPE, paramBoolean);
  }

  private void initTimezone(String paramString)
  {
    this.cTimeZone = paramString;
    if (paramString != null)
    {
      if (paramString.equals("use_server"))
        this.isUseServer = true;
      if (("".equals(paramString)) || (paramString.equals("use_server")))
        paramString = null;
    }
    this.mTimeZone = this.mPreferences.getTimeZone();
    if ((this.mTimeZone != null) && (!LocaleUtil.isValidTimezone(this.mTimeZone)))
    {
      mSessionLog.log(Level.WARNING, "Invalid user preference time zone " + this.mTimeZone + " specified for user " + this.mUser);
      this.mTimeZone = null;
    }
    if (this.mTimeZone == null)
    {
      this.mTimeZone = paramString;
      if (this.mTimeZone == null)
        if (mDefaultTimeZone == null)
          this.mTimeZone = TimeZone.getDefault().getID();
        else
          this.mTimeZone = mDefaultTimeZone;
    }
    mSessionLog.log(Level.FINE, "InitTimeZone = " + this.mTimeZone);
    assert (this.mTimeZone != null);
  }

  public ServerLogin getServerLogin(String paramString)
    throws GoatException
  {
    String str = Configuration.getInstance().getLongName(paramString);
    Long localLong = getServerCacheId(str);
    ServerLogin localServerLogin = ServerLogin.get(str, this.mLoginHost, localLong.longValue());
    if ((localLong.longValue() == 0L) && (localServerLogin != null))
    {
      localLong = Long.valueOf(localServerLogin.getCacheId());
      if (this.mServerCacheIds == null)
        this.mServerCacheIds = new HashMap();
      this.mServerCacheIds.put(str, localLong);
    }
    return localServerLogin;
  }

  public String getLocale()
  {
    return this.mLocale;
  }

  public String getUserName()
  {
    return this.mUser;
  }

  public String getPassword()
  {
    return this.mPassword;
  }

  public String getAuthentication()
  {
    return this.mAuthentication;
  }

  public String getTimeZone()
  {
    return this.mTimeZone;
  }

  public String getcTimeZone()
  {
    return this.cTimeZone;
  }

  public ARUserPreferences getPreferences()
  {
    return this.mPreferences;
  }

  public String getCustomDateTimeFormat()
  {
    return this.mPreferences.getCustomTimeFormatStr();
  }

  public Long getDateTimeStyle()
  {
    return this.mPreferences.getDisplayTimeFormat();
  }

  public ARServerLog getServerLog()
  {
    return this.mServerLog;
  }

  public boolean isLoggedOut()
  {
    return this.mLoggedOut;
  }

  public void putWindowArg(String paramString1, String paramString2)
  {
    String str = paramString1;
    this.mWOArgumentsMap.put(str, paramString2);
  }

  public String getWindowArg(String paramString)
  {
    String str1 = paramString;
    String str2 = (String)this.mWOArgumentsMap.get(str1);
    if (str2 != null)
      this.mWOArgumentsMap.remove(str1);
    return str2;
  }

  public void putWindowQBE(String paramString1, String paramString2)
  {
    String str = paramString1;
    this.mQBEMap.put(str, paramString2);
  }

  public String getWindowQBE(String paramString)
  {
    String str1 = paramString;
    String str2 = (String)this.mQBEMap.get(str1);
    if (str2 != null)
      this.mQBEMap.remove(str1);
    return str2;
  }

  public void dispose(boolean paramBoolean)
  {
    this.mDateTimePatternObj = null;
    this.mPreferences = null;
    resetFormRequests();
    SessionData localSessionData1 = (SessionData)MSessionLocal.get();
    if ((localSessionData1 != null) && (this.mID.equals(localSessionData1.getID())))
      MSessionLocal.set(null);
    if (paramBoolean)
      this.mLoginHost.setHostLoggingOut();
    else
      this.mLoginHost.setHostDeparting();
    Object localObject1;
    Object localObject2;
    synchronized (allSessionData)
    {
      localObject1 = (Set)allSessionData.get(this.mUser);
      if (localObject1 != null)
      {
        localObject2 = ((Set)localObject1).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          SessionData localSessionData2 = (SessionData)((Iterator)localObject2).next();
          if ((localSessionData2 != null) && (this.mID.equals(localSessionData2.getID())))
            ((Iterator)localObject2).remove();
        }
        if (((Set)localObject1).size() == 0)
          allSessionData.remove(this.mUser);
      }
    }
    if (paramBoolean)
    {
      this.mLoggedOut = true;
      synchronized (this.mEventListeners)
      {
        localObject1 = this.mEventListeners.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (SessionDataEventListener)((Iterator)localObject1).next();
          ((SessionDataEventListener)localObject2).disposing(this);
        }
      }
    }
    if (this.userActorMap.size() > 0)
    {
      ??? = this.userActorMap.values().iterator();
      while (((Iterator)???).hasNext())
      {
        localObject1 = (UserActorCache)((Iterator)???).next();
        ((UserActorCache)localObject1).release();
      }
      this.userActorMap.clear();
    }
    this.actorViewMap.clear();
  }

  public static void dispose(String paramString1, String paramString2, String paramString3)
  {
    LinkedHashSet localLinkedHashSet = new LinkedHashSet();
    Object localObject1;
    synchronized (allSessionData)
    {
      localObject1 = getSessionDataSetFromCentral(paramString1);
      if (localObject1 != null)
      {
        Iterator localIterator = ((Set)localObject1).iterator();
        while (localIterator.hasNext())
        {
          SessionData localSessionData = (SessionData)localIterator.next();
          if ((equalWithNull(localSessionData.getPassword(), paramString2)) && (equalWithNull(localSessionData.getAuthentication(), paramString3)))
          {
            localIterator.remove();
            localLinkedHashSet.add(localSessionData);
          }
        }
        if (((Set)localObject1).size() == 0)
          removeSessionDataSetFromCentral(paramString1);
      }
    }
    ??? = localLinkedHashSet.iterator();
    while (((Iterator)???).hasNext())
    {
      localObject1 = (SessionData)((Iterator)???).next();
      ((SessionData)localObject1).dispose(true);
    }
  }

  private static boolean equalWithNull(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0))
      return (paramString2 == null) || (paramString2.length() == 0);
    return paramString1.equals(paramString2);
  }

  protected void finalize()
    throws Throwable
  {
    try
    {
      dispose(false);
    }
    finally
    {
      super.finalize();
    }
  }

  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    String str = PwdUtility.encryptPswdUsingBlowfish(this.mPassword);
    if (str.length() > 120)
    {
      assert (str.length() <= 120);
      paramObjectOutputStream.writeInt(0);
    }
    else
    {
      byte[] arrayOfByte = str.getBytes();
      paramObjectOutputStream.writeInt(arrayOfByte.length);
      paramObjectOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
    }
    synchronized (this.mEventListeners)
    {
      Iterator localIterator = this.mEventListeners.iterator();
      while (localIterator.hasNext())
      {
        SessionDataEventListener localSessionDataEventListener = (SessionDataEventListener)localIterator.next();
        localSessionDataEventListener.passivating(this, paramObjectOutputStream);
      }
    }
  }

  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    if (i > 120)
    {
      assert (i <= 120);
      this.mPassword = PwdUtility.decryptPswdUsingBlowfish(new String());
    }
    else
    {
      byte[] arrayOfByte = new byte[i];
      int j = paramObjectInputStream.read(arrayOfByte, 0, i);
      this.mPassword = PwdUtility.decryptPswdUsingBlowfish(new String(arrayOfByte));
    }
    this.mLoginHost = new SessionDataHost();
    this.mServerLog = new ARServerLog();
    assert (this.mEventListeners != null);
    Object localObject1;
    synchronized (this.mEventListeners)
    {
      Iterator localIterator = this.mEventListeners.iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (SessionDataEventListener)localIterator.next();
        ((SessionDataEventListener)localObject1).activating(this, paramObjectInputStream);
      }
    }
    ??? = null;
    try
    {
      ??? = tryGet();
      set(this);
      this.mPreferences = ARUserPreferences.getUserPreferences();
      if ((!$assertionsDisabled) && (this.mPreferences == null))
        throw new AssertionError();
    }
    finally
    {
      set((SessionData)???);
    }
    synchronized (allSessionData)
    {
      localObject1 = getSessionDataSetFromCentral(this.mUser);
      ((Set)localObject1).add(this);
    }
  }

  public static String getLocale(HttpServletRequest paramHttpServletRequest)
  {
    SessionData localSessionData = (SessionData)MSessionLocal.get();
    if (localSessionData != null)
      return localSessionData.getLocale();
    return paramHttpServletRequest.getLocale().toString();
  }

  public synchronized Calendar getICUCalendarInstance()
  {
    if (this.mCalendar != null)
      return this.mCalendar;
    this.mCalendar = Calendar.getInstance();
    return this.mCalendar;
  }

  public synchronized Calendar getICULocalisedCalendarInstance()
  {
    if (this.mLocalisedCalendar != null)
      return this.mLocalisedCalendar;
    this.mLocalisedCalendar = Calendar.getInstance(TimeZone.getTimeZone(getTimeZone()), new Locale(getLocale()));
    return this.mLocalisedCalendar;
  }

  public synchronized SimpleDateFormat getICULocalisedSimpleDateFormatInstance()
  {
    if (this.mLocalisedSimpleDateFormat != null)
      return this.mLocalisedSimpleDateFormat;
    this.mLocalisedSimpleDateFormat = ((SimpleDateFormat)DateFormat.getInstance(getICULocalisedCalendarInstance(), new Locale(getLocale())));
    return this.mLocalisedSimpleDateFormat;
  }

  public synchronized SimpleDateFormat getICUTimeOnlySimpleDateFormatInstance()
  {
    if (this.mTimeOnlySimpleDateFormat != null)
      return this.mTimeOnlySimpleDateFormat;
    this.mTimeOnlySimpleDateFormat = ((SimpleDateFormat)DateFormat.getTimeInstance(3, new Locale(getLocale())));
    return this.mTimeOnlySimpleDateFormat;
  }

  public synchronized SimpleDateFormat getICUDateOnlyShortSimpleDateFormatInstance()
  {
    if (this.mDateOnlyShortSimpleDateFormat != null)
      return this.mDateOnlyShortSimpleDateFormat;
    this.mDateOnlyShortSimpleDateFormat = ((SimpleDateFormat)DateFormat.getDateInstance(3, new Locale(getLocale())));
    return this.mDateOnlyShortSimpleDateFormat;
  }

  public String getID()
  {
    assert (this.mID != null);
    return this.mID;
  }

  public synchronized void setOverride(int paramInt)
  {
    this.mOverride = paramInt;
    if (paramInt == 0)
      return;
    Iterator localIterator = ServerLogin.grabAll(this.mLoginHost);
    if (localIterator != null)
      while (localIterator.hasNext())
        ((ServerLogin)localIterator.next()).setOverridePrevIP(paramInt == 1);
    localIterator = this.mLoginHost.grabAll();
    if (localIterator != null)
      while (localIterator.hasNext())
        ((ServerLogin)localIterator.next()).setOverridePrevIP(paramInt == 1);
  }

  public int getOverride()
  {
    return this.mOverride;
  }

  public void changePassword(String paramString)
  {
    synchronized (allSessionData)
    {
      Set localSet = getSessionDataSetFromCentral(this.mUser);
      SessionData localSessionData = null;
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        localSessionData = (SessionData)localIterator.next();
        localSessionData.changeSessionDataPassword(paramString);
      }
    }
  }

  private void changeSessionDataPassword(String paramString)
  {
    this.mPassword = paramString;
    Iterator localIterator = ServerLogin.grabAll(this.mLoginHost);
    if (localIterator != null)
      while (localIterator.hasNext())
        ((ServerLogin)localIterator.next()).setPasswd(paramString);
    localIterator = this.mLoginHost.grabAll();
    if (localIterator != null)
      while (localIterator.hasNext())
        ((ServerLogin)localIterator.next()).setPasswd(paramString);
  }

  public void setPasswordChangeTargetURL(String paramString)
  {
    this.mPasswordChangeTargetURL = paramString;
  }

  public String getPasswordChangeTargetURL()
  {
    return this.mPasswordChangeTargetURL;
  }

  public boolean isPasswordChangeRequired()
  {
    return this.mIsPasswordChangeRequired;
  }

  public void setPasswordChangeRequired(boolean paramBoolean)
  {
    this.mIsPasswordChangeRequired = paramBoolean;
  }

  public boolean isWebTimingsEnabled()
  {
    return this.mWebTimings;
  }

  public void SetEnableFlex(boolean paramBoolean)
  {
    this.mEnableFlex = paramBoolean;
  }

  public boolean isFlexEnabled()
  {
    return this.mEnableFlex;
  }

  public void setGroupModified(boolean paramBoolean)
  {
    this.mGroupModified = paramBoolean;
  }

  public boolean isGroupModified()
  {
    return this.mGroupModified;
  }

  public void addEventListener(SessionDataEventListener paramSessionDataEventListener)
  {
    assert (paramSessionDataEventListener != null);
    synchronized (this.mEventListeners)
    {
      this.mEventListeners.add(paramSessionDataEventListener);
    }
  }

  public void removeEventListener(SessionDataEventListener paramSessionDataEventListener)
  {
    assert (paramSessionDataEventListener != null);
    synchronized (this.mEventListeners)
    {
      this.mEventListeners.remove(paramSessionDataEventListener);
    }
  }

  public void setUserSearches(ARUserSearches paramARUserSearches)
  {
    this.mSearches = paramARUserSearches;
  }

  public ARUserSearches getUserSearches()
  {
    return this.mSearches;
  }

  public static void setDefaultTimeZone(String paramString)
  {
    mDefaultTimeZone = paramString;
  }

  public void setServerCacheIds(HashMap paramHashMap)
  {
    this.mServerCacheIds = paramHashMap;
  }

  public Long getServerCacheId(String paramString)
  {
    if ((this.mServerCacheIds != null) && (this.mServerCacheIds.containsKey(paramString)))
      return (Long)this.mServerCacheIds.get(paramString);
    return new Long(0L);
  }

  public final synchronized void incrFormRequestCount()
  {
    this.mFormRequestCount += 1;
  }

  public final synchronized void decrFormRequestCount()
  {
    this.mFormRequestCount -= 1;
    if (this.mFormRequestCount < 0)
      this.mFormRequestCount = 0;
  }

  public final synchronized boolean hasFormRequests()
  {
    return this.mFormRequestCount > 0;
  }

  public final synchronized void resetFormRequests()
  {
    this.mFormRequestCount = 0;
  }

  public Globule getStaticUserDataJS()
  {
    return this.staticUserDataJS;
  }

  public void setStaticUserDataJS(Globule paramGlobule)
  {
    assert (paramGlobule != null);
    this.staticUserDataJS = paramGlobule;
    this.isModified = true;
  }

  public String getSearchesPrefFormForServer(String paramString)
  {
    return this.searchesSrvPrefFormMap == null ? null : (String)this.searchesSrvPrefFormMap.get(paramString);
  }

  public void setSearchesPrefFormForServer(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null))
    {
      if (this.searchesSrvPrefFormMap == null)
        this.searchesSrvPrefFormMap = new HashMap();
      this.searchesSrvPrefFormMap.put(paramString1, paramString2);
      this.isModified = true;
    }
  }

  public boolean getIsModified()
  {
    return this.isModified;
  }

  public void updateSessionDataIfModified(HttpServletRequest paramHttpServletRequest)
  {
    if (this.isModified)
    {
      assert (paramHttpServletRequest != null);
      HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
      if (localHttpSession != null)
      {
        localHttpSession.setAttribute("com.remedy.arsys.stubs.sessionData", this);
        this.isModified = false;
      }
    }
  }

  public void setCHPContentOBJMap(Map<Integer, DHTMLRequestService.CHPContent> paramMap)
  {
    this.chpContentOBJMap = paramMap;
  }

  public Map<Integer, DHTMLRequestService.CHPContent> getCHPContentOBJMap()
  {
    return this.chpContentOBJMap;
  }

  public synchronized UserActorCache getUserActorCache(String paramString)
  {
    UserActorCache localUserActorCache = null;
    if (!this.userActorMap.containsKey(paramString))
    {
      localUserActorCache = UserActorCache.getInstance(this, paramString);
      this.userActorMap.put(paramString, localUserActorCache);
    }
    else
    {
      localUserActorCache = (UserActorCache)this.userActorMap.get(paramString);
    }
    return localUserActorCache;
  }

  public ActorViewCache getActorViewCache(String paramString)
  {
    ActorViewCache localActorViewCache = null;
    if (!this.actorViewMap.containsKey(paramString))
    {
      localActorViewCache = ActorViewCache.getInstance(paramString);
      if (localActorViewCache != null)
        this.actorViewMap.put(paramString, localActorViewCache);
    }
    else
    {
      localActorViewCache = (ActorViewCache)this.actorViewMap.get(paramString);
    }
    return localActorViewCache;
  }

  public CacheDirectiveController.TimeHashMap<String, HashMap<String, QuickReportCache>> getMQuickReports()
  {
    if (this.mQuickReports == null)
      this.mQuickReports = new CacheDirectiveController.TimeHashMap(5);
    return this.mQuickReports;
  }

  public void setMQuickReports(CacheDirectiveController.TimeHashMap<String, HashMap<String, QuickReportCache>> paramTimeHashMap)
  {
    this.mQuickReports = paramTimeHashMap;
  }

  public void setTimeZone(String paramString)
  {
    this.mTimeZone = paramString;
  }

  public DateTimePattern getDateTimePattern()
  {
    return this.mDateTimePatternObj;
  }

  public String[] getDateParsePatterns()
  {
    return this.mDateTimePatternObj.getDateParsePatterns();
  }

  public String getDateFormatPattern()
  {
    return this.mDateTimePatternObj.getDateFormatPattern();
  }

  public String[] getTimeParsePatterns()
  {
    return this.mDateTimePatternObj.getTimeParsePatterns();
  }

  public String getTimeFormatPattern()
  {
    return this.mDateTimePatternObj.getTimeFormatPattern();
  }

  public void initDateTimeFormatPattern()
  {
    long l = this.mPreferences.getDisplayTimeFormat().longValue();
    this.mDateTimePatternObj.setDisplayTimeFormat(Long.valueOf(l));
    if (ARUserPreferences.CUSTOM_TIME_FORMAT.equals(Long.valueOf(l)))
    {
      String str = this.mPreferences.getCustomDateFormatStr();
      if (str != null)
        this.mDateTimePatternObj.setDatePattern(str);
      str = this.mPreferences.getCustomTimeFormatStr();
      if (str != null)
        this.mDateTimePatternObj.setTimePattern(str);
    }
    this.mDateTimePatternObj.initDateTimeFormatPattern();
  }

  class SessionDataHost extends ServerLoginHost
  {
    SessionDataHost()
    {
    }

    public String getUserName(String paramString)
      throws GoatException
    {
      assert (SessionData.this.mUser != null);
      return SessionData.this.mUser;
    }

    public String getPassword(String paramString)
      throws GoatException
    {
      return SessionData.this.mPassword;
    }

    public String getAuthentication(String paramString)
      throws GoatException
    {
      return SessionData.this.mAuthentication;
    }

    public int getPort(String paramString)
      throws GoatException
    {
      return ServerLogin.getServerInformation(paramString).getPort();
    }

    public int getRPC(String paramString)
      throws GoatException
    {
      return ServerLogin.getServerInformation(paramString).getRPC();
    }

    public String getGUID(String paramString)
    {
      return SessionData.this.mGUID;
    }

    public int getOverride()
    {
      return SessionData.this.mOverride;
    }

    public String getLocale()
    {
      return SessionData.this.mLocale;
    }

    public LoggingInfo getLoggingInfo()
    {
      return SessionData.this.mLoggingInfo;
    }

    public ARServerLog getServerLog()
    {
      return SessionData.this.mServerLog;
    }

    public String getTimezone()
    {
      return SessionData.this.mTimeZone;
    }

    public String getDateFormat()
    {
      return SessionData.this.mDateTimePatternObj == null ? null : SessionData.this.mDateTimePatternObj.getDateFormatPattern();
    }

    public String getTimeFormat()
    {
      return SessionData.this.mDateTimePatternObj == null ? null : SessionData.this.mDateTimePatternObj.getTimeFormatPattern();
    }

    public Value getClientType()
    {
      return SessionData.this.mClientType;
    }
  }

  public static abstract interface SessionDataEventListener
  {
    public abstract void activating(SessionData paramSessionData, ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException;

    public abstract void passivating(SessionData paramSessionData, ObjectOutputStream paramObjectOutputStream)
      throws IOException;

    public abstract void disposing(SessionData paramSessionData);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.SessionData
 * JD-Core Version:    0.6.1
 */