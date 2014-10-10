/*   */ package com.remedy.arsys.stubs;
/*   */ 
/*   */ import com.bmc.arsys.api.ARException;
/*   */ import com.bmc.arsys.api.ARServerUser;
/*   */ import com.bmc.arsys.api.GroupInfo;
/*   */ import com.bmc.arsys.api.LoggingInfo;
/*   */ import com.bmc.arsys.api.RoleInfo;
/*   */ import com.bmc.arsys.api.StatusInfo;
/*   */ import com.bmc.arsys.api.Value;
/*   */ import com.ibm.icu.util.TimeZone;
/*   */ import com.remedy.arsys.config.Configuration;
/*   */ import com.remedy.arsys.config.Configuration.ServerInformation;
/*   */ import com.remedy.arsys.goat.GoatException;
/*   */ import com.remedy.arsys.log.ARServerAdminLog;
/*   */ import com.remedy.arsys.log.ARServerLog;
/*   */ import com.remedy.arsys.plugincontainer.impl.ARConversionHelper;
/*   */ import java.io.PrintStream;
/*   */ import java.util.Arrays;
/*   */ import java.util.Collections;
/*   */ import java.util.Comparator;
/*   */ import java.util.Date;
/*   */ import java.util.HashMap;
/*   */ import java.util.Iterator;
/*   */ import java.util.List;
/*   */ import java.util.Map;
/*   */ 
/*   */ public class ServerLogin extends ARServerUser
/*   */ {
/* 1 */   private static Comparator GROUP_ID_COMP = new Comparator() { public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2) { GroupInfo localGroupInfo1 = (GroupInfo)paramAnonymousObject1;
/*   */       GroupInfo localGroupInfo2 = (GroupInfo)paramAnonymousObject2;
/*   */       return localGroupInfo2.getId() - localGroupInfo1.getId(); } 
/* 1 */     public boolean equals(Object paramAnonymousObject1, Object paramAnonymousObject2) { GroupInfo localGroupInfo1 = (GroupInfo)paramAnonymousObject1;
/*   */       GroupInfo localGroupInfo2 = (GroupInfo)paramAnonymousObject2;
/*   */       return localGroupInfo2.getId() == localGroupInfo1.getId(); }  } ; private static Comparator GROUP_NAME_COMP = new Comparator() { public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2) { GroupInfo localGroupInfo1 = (GroupInfo)paramAnonymousObject1;
/*   */       GroupInfo localGroupInfo2 = (GroupInfo)paramAnonymousObject2;
/*   */       String str1 = localGroupInfo1.getName();
/*   */       String str2 = localGroupInfo2.getName();
/*   */       return str1.compareTo(str2); } 
/* 1 */     public boolean equals(Object paramAnonymousObject1, Object paramAnonymousObject2) { GroupInfo localGroupInfo1 = (GroupInfo)paramAnonymousObject1;
/*   */       GroupInfo localGroupInfo2 = (GroupInfo)paramAnonymousObject2;
/*   */       String str1 = localGroupInfo1.getName();
/*   */       String str2 = localGroupInfo2.getName();
/*   */       return str1.equals(str2); }  } ;
/*   */   private Map mGroupMaps;
/*   */   private Map mGroupIdMaps;
/*   */   private Map mRoleMaps;
/*   */   private GroupInfo[] mGroups;
/*   */   private String mPermKey;
/*   */   long mLastUsedTime;
/*   */   private ServerLoginHost mHost;
/*   */   private boolean mAmLoggedOut;
/* 1 */   private static ServerLoginThreadManager MThreadManager = new ServerLoginThreadManager(); private static Map MAdminHostMap = new HashMap(); static boolean MDebug = false;
/*   */   static final String MADMIN_NO_LOCALE = "no_locale";
/*   */ 
/* 1 */   public static final ServerLogin get(String arg0, ServerLoginHost arg1, long arg2) throws GoatException { assert (arg0 != null); assert (arg1 != null); arg0 = arg0.toLowerCase(); ServerLogin localServerLogin = MThreadManager.get(arg0, arg1); if (localServerLogin != null) { localServerLogin.setPort(arg1.getPort(arg0)); if ((arg1 instanceof AdminHost)) localServerLogin.setPasswd(arg1.getPassword(arg0)); return localServerLogin; } if (MDebug) System.out.println("SL  > Req login to " + arg0 + " for user " + arg1.getUserName(arg0) + " GUID " + arg1.getGUID(arg0) + "..."); localServerLogin = arg1.get(arg0); if (localServerLogin != null) { localServerLogin.setPort(arg1.getPort(arg0)); if ((arg1 instanceof AdminHost)) localServerLogin.setPasswd(arg1.getPassword(arg0)); MThreadManager.put(localServerLogin); if (MDebug) System.out.println("SL  > ...returned id " + localServerLogin.hashCode() + " from host " + arg1.hashCode() + " cache"); return localServerLogin; } localServerLogin = new ServerLogin(arg0, arg1, arg2); if (MDebug) System.out.println("SL  > ...returned id " + localServerLogin.hashCode() + " newly created for host " + arg1.hashCode()); return localServerLogin; } 
/* 1 */   public static Iterator grabAll(ServerLoginHost arg0) { return MThreadManager.grabAll(arg0); } 
/* 1 */   public static ServerLogin getAdmin(String arg0) throws GoatException { return getAdmin(arg0, null); } 
/* 1 */   public static ServerLogin getAdmin(String arg0, String arg1) throws GoatException { if (arg1 == null) arg1 = "no_locale"; Object localObject1 = null; synchronized (MAdminHostMap) { localObject1 = (ServerLoginHost)MAdminHostMap.get(arg1); if (localObject1 == null) localObject1 = new AdminHost(arg1);  } assert (localObject1 != null); if (MDebug) { ??? = Thread.currentThread().getStackTrace(); for (int i = 0; i < ???.length; i++) System.out.println(???[i].toString());  } ??? = get(arg0, (ServerLoginHost)localObject1, 0L); if (MDebug) { System.out.println("AL  > Req amdin login to " + arg0 + " for user " + ((ServerLoginHost)localObject1).getUserName(arg0) + " GUID " + ((ServerLoginHost)localObject1).getGUID(arg0) + "..."); System.out.println("AL  > Returned admin object hashcode=" + ???.hashCode() + "password=" + ???.getPasswd() + "\n>>>>>>>>>>>>>>\n"); } return ???; } 
/* 1 */   public static void threadDepartingGoatSpace() { MThreadManager.threadDepartingGoatSpace(false); } 
/* 1 */   public static void threadDepartingGoatSpace(boolean arg0) { MThreadManager.threadDepartingGoatSpace(arg0); } 
/* 1 */   private ServerLogin(String paramString, ServerLoginHost paramServerLoginHost, long paramLong) throws GoatException { this.mGroupMaps = Collections.synchronizedMap(new HashMap()); this.mGroupIdMaps = Collections.synchronizedMap(new HashMap()); this.mRoleMaps = Collections.synchronizedMap(new HashMap()); assert (paramString != null); paramString = paramString.toLowerCase(); String str1 = Configuration.getInstance().getLongName(paramString); this.mAmLoggedOut = false; assert (paramServerLoginHost.getUserName(str1) != null); String str2 = paramServerLoginHost.getUserName(str1); String str3 = paramServerLoginHost.getPassword(str1); setUser(str2); setPassword(str3); this.mHost = paramServerLoginHost; if (this.mHost.getAuthentication(str1) != null) setAuthentication(this.mHost.getAuthentication(str1)); setServer(str1); String str4 = this.mHost.getLocale(); if (str4 != null) setLocale(str4); String str5 = this.mHost.getTimezone(); if (str5 != null) setTimeZone(str5); String str6 = this.mHost.getDateFormat(); if (str6 != null) setCustomDateFormat(str6); String str7 = this.mHost.getTimeFormat(); if (str7 != null) setCustomTimeFormat(str7); LoggingInfo localLoggingInfo = this.mHost.getLoggingInfo();
/*   */     try { if (localLoggingInfo != null) setLogging(localLoggingInfo); setLogInterface(this.mHost.getServerLog()); this.mHost = paramServerLoginHost; setClientType(((Integer)this.mHost.getClientType().getValue()).intValue()); int i = Configuration.getInstance().getPreferNativeViews() ? 1 : 3; setVUIType(i); setPort(this.mHost.getPort(str1)); usePrivateRpcQueue(this.mHost.getRPC(str1)); if (this.mHost.getGUID(str1) != null) { setReservedParam1(this.mHost.getGUID(str1)); setOverridePrevIP(this.mHost.getOverride() == 1); } if (paramLong != 0L) setCacheId(paramLong); verifyUser(); if (!str2.equals("MidTier Service")) this.mGroups = ((GroupInfo[])getListGroup(str2, str3).toArray(new GroupInfo[0]));  } catch (ARException localARException) { throw new GoatException(localARException); } setPermKey(this.mGroups); updateLastUsedTime(); MThreadManager.put(this); } 
/* 1 */   public void setPermKey(GroupInfo[] arg0) { this.mGroups = arg0; if (this.mGroups == null) this.mGroups = new GroupInfo[0]; StringBuilder localStringBuilder = new StringBuilder(); int i = 0; Arrays.sort(this.mGroups, GROUP_ID_COMP); for (int j = 0; j < this.mGroups.length; j++) if (((this.mGroups[j].getCategory() == 0) || (this.mGroups[j].getCategory() == 2)) && (this.mGroups[j].getGroupType() != 0)) { if (localStringBuilder.length() > 0) localStringBuilder.append(","); localStringBuilder.append(this.mGroups[j].getId()); if ((i == 0) && (this.mGroups[j].getId() == 1)) i = 1;  } 
/* 1 */     this.mPermKey = (i != 0 ? "Admin" : localStringBuilder.toString()); } 
/* 1 */   void updateLastUsedTime() { this.mLastUsedTime = new Date().getTime(); } 
/* 1 */   public boolean getAdminFlag() { return "Admin".equals(this.mPermKey); } 
/* 1 */   public void clear() { logout(); } 
/* 1 */   public void logout() { ARConversionHelper.cleanContext(this); synchronized (this) { if (this.mAmLoggedOut) return; this.mAmLoggedOut = true; this.mHost = null; super.logout(); }  } 
/* 1 */   void threadDeparting() { updateLastUsedTime(); if (this.mHost != null) this.mHost.threadDeparting(this);  } 
/* 1 */   synchronized void moveToGrimReaper() { this.mHost = null; ServerLoginGrimReaper.addToReaper(this); } 
/* 1 */   public String getPermissionsKey() { return this.mPermKey; } 
/* 1 */   public GroupInfo[] getGroupInfo() { return this.mGroups; } 
/* 1 */   public ServerLoginHost getHost() { return this.mHost; } 
/* 1 */   public String getUserGroupIDsKeyword() { String str1 = "SessionData.UserGroupsIds: " + getServer().toLowerCase(); synchronized (str1.intern()) { String str2 = (String)this.mGroupIdMaps.get(str1); if (str2 == null) { StringBuilder localStringBuilder = new StringBuilder(); for (int i = 0; i < this.mGroups.length; i++) localStringBuilder.append(";").append(this.mGroups[i].getId()); localStringBuilder.append(";"); str2 = localStringBuilder.toString(); this.mGroupIdMaps.put(str1, str2); } return str2; }  } 
/* 1 */   public String getUserGroupsKeyword() { String str1 = "SessionData.UserGroups: " + getServer(); synchronized (str1.intern()) { String str2 = (String)this.mGroupMaps.get(str1); if (str2 == null) { GroupInfo[] arrayOfGroupInfo = (GroupInfo[])this.mGroups.clone(); Arrays.sort(arrayOfGroupInfo, GROUP_NAME_COMP); StringBuilder localStringBuilder = new StringBuilder(); for (int i = 0; i < arrayOfGroupInfo.length; i++) { if (i > 0) localStringBuilder.append(" "); localStringBuilder.append(arrayOfGroupInfo[i].getName()); } str2 = localStringBuilder.toString(); this.mGroupMaps.put(str1, str2); } return str2; }  } 
/* 1 */   public String getUserRolesKeyword(String arg0) { if (arg0 == null) return ""; String str1 = "SessionData.UserRoles: " + getServer() + "/" + arg0; synchronized (str1.intern()) { String str2 = (String)this.mRoleMaps.get(str1); if (str2 == null) { StringBuilder localStringBuilder = new StringBuilder();
/*   */         try { RoleInfo[] arrayOfRoleInfo = (RoleInfo[])getListRole(arg0, getUser(), getPassword()).toArray(new RoleInfo[0]); assert (arrayOfRoleInfo != null); for (int i = 0; i < arrayOfRoleInfo.length; i++) { if (i > 0) localStringBuilder.append(" "); localStringBuilder.append(arrayOfRoleInfo[i].getName()); }  } catch (ARException localARException1) { return ""; } str2 = localStringBuilder.toString(); this.mRoleMaps.put(str1, str2); } return str2; }  } 
/* 1 */   public String getPasswd() { return getPassword(); } 
/* 1 */   public void setPasswd(String arg0) { setPassword(arg0); } 
/* 1 */   public String getUserName() { return getUser(); } 
/* 1 */   public void setLastStatus(List<StatusInfo> arg0) { super.setLastStatus(arg0); } 
/* 1 */   public static Configuration.ServerInformation getServerInformation(String arg0) throws GoatException { Configuration.ServerInformation localServerInformation = Configuration.getInstance().getServerInformation(arg0); if (localServerInformation == null) { GoatException localGoatException = new GoatException(9280, arg0); localGoatException.setConvertIdToLabel(false); throw localGoatException; } return localServerInformation;
/*   */   }
/*   */ 
/*   */   public static class AdminHost extends ServerLoginHost
/*   */   {
/*   */     String mLocale;
/*   */     ARServerLog mServerLog = new ARServerAdminLog();
/*   */     public static final String MAdminUser = "MidTier Service";
/*   */     static final Value AR_MIDTIER_CTYPE = new Value(9);
/*   */ 
/*   */     AdminHost(String paramString)
/*   */     {
/*   */       this.mLocale = paramString;
/*   */       ServerLogin.MAdminHostMap.put(paramString, this);
/*   */     }
/*   */ 
/*   */     public String getUserName(String paramString)
/*   */       throws GoatException
/*   */     {
/*   */       return "MidTier Service";
/*   */     }
/*   */ 
/*   */     public String getPassword(String paramString)
/*   */       throws GoatException
/*   */     {
/*   */       return Configuration.getInstance().getARServerPassword(paramString);
/*   */     }
/*   */ 
/*   */     public String getAuthentication(String paramString)
/*   */       throws GoatException
/*   */     {
/*   */       return null;
/*   */     }
/*   */ 
/*   */     public int getPort(String paramString)
/*   */       throws GoatException
/*   */     {
/*   */       return ServerLogin.getServerInformation(paramString).getPort();
/*   */     }
/*   */ 
/*   */     public int getRPC(String paramString)
/*   */       throws GoatException
/*   */     {
/*   */       return ServerLogin.getServerInformation(paramString).getRPC();
/*   */     }
/*   */ 
/*   */     public String getGUID(String paramString)
/*   */     {
/*   */       return null;
/*   */     }
/*   */ 
/*   */     public int getOverride()
/*   */     {
/*   */       return -1;
/*   */     }
/*   */ 
/*   */     public String getLocale()
/*   */     {
/*   */       if ("no_locale".equals(this.mLocale))
/*   */         return null;
/*   */       return this.mLocale;
/*   */     }
/*   */ 
/*   */     public String getTimezone()
/*   */     {
/*   */       return TimeZone.getDefault().getID();
/*   */     }
/*   */ 
/*   */     public String getDateFormat()
/*   */     {
/*   */       return null;
/*   */     }
/*   */ 
/*   */     public String getTimeFormat()
/*   */     {
/*   */       return null;
/*   */     }
/*   */ 
/*   */     public Value getClientType()
/*   */     {
/*   */       return AR_MIDTIER_CTYPE;
/*   */     }
/*   */ 
/*   */     public LoggingInfo getLoggingInfo()
/*   */     {
/*   */       return null;
/*   */     }
/*   */ 
/*   */     public ARServerLog getServerLog()
/*   */     {
/*   */       return this.mServerLog;
/*   */     }
/*   */   }
/*   */ }

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ServerLogin
 * JD-Core Version:    0.6.1
 */