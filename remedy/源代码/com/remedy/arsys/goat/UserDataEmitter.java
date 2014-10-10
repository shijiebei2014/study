package com.remedy.arsys.goat;

import com.bmc.arsys.api.CurrencyDetail;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.menu.GroupMenu;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.goat.menu.Menu.MKey;
import com.remedy.arsys.goat.preferences.ARUserPreferences;
import com.remedy.arsys.goat.quickreports.ARUserQuickReports;
import com.remedy.arsys.goat.savesearches.ARUserSearches;
import com.remedy.arsys.goat.service.DHTMLRequestService.CHPContent;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.ServerInfo;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDataEmitter
{
  public static int WAIT_CURSOR_MODE = Configuration.getInstance().getWaitCursorMode();
  private String userDataString;

  private void writeSystemMessageDestination(JSWriter paramJSWriter, String paramString)
  {
    Integer localInteger = Integer.valueOf(2);
    try
    {
      ServerInfo localServerInfo = ServerInfo.get(paramString);
      localInteger = Integer.valueOf(localServerInfo.getSystemPromptBar());
    }
    catch (GoatException localGoatException)
    {
    }
    paramJSWriter.statement("this.sysMsgPromptType=" + localInteger.toString());
  }

  public UserDataEmitter(String paramString1, String paramString2, String paramString3, PrintWriter paramPrintWriter, int paramInt, Form.ViewInfo paramViewInfo)
    throws GoatException
  {
    SessionData localSessionData = SessionData.get();
    String str = localSessionData.getUserName();
    ServerLogin localServerLogin = localSessionData.getServerLogin(paramString1);
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.startFunction("uddinit", "");
    localJSWriter.startInterruptibleBlock();
    Form localForm = Form.get(paramString1, paramString2);
    localJSWriter.callFunction(false, "ARKWSetup", new Object[] { "windowID", "[2", JSWriter.escapeString(str), "41", JSWriter.escapeString(localServerLogin.getUserGroupIDsKeyword()), "9", JSWriter.escapeString(localServerLogin.getUserGroupsKeyword()), "31", JSWriter.escapeString(localServerLogin.getUserRolesKeyword(localForm.getAppName())) + "]" });
    localJSWriter.endInterruptibleBlock();
    ARUserPreferences localARUserPreferences = localSessionData.getPreferences();
    assert (localARUserPreferences != null);
    localARUserPreferences.emitTablePrefs(localJSWriter, paramString1, paramString2, paramInt);
    if ((paramString3 != null) && (!paramString3.equals("")))
    {
      localObject = localSessionData.getWindowArg(paramString3);
      if (localObject != null)
        localJSWriter.append("window.openingArgs=").append((String)localObject).append(";");
    }
    localJSWriter.endFunction(false);
    if ((paramString3 != null) && (!paramString3.equals("")))
    {
      localObject = localSessionData.getWindowQBE(paramString3);
      if (localObject != null)
        localJSWriter.append("window.qbe=").append((String)localObject).append(";");
    }
    localJSWriter.startFunction("UserSearches_Init", "");
    Object localObject = ARUserSearches.getUserSearches(str, paramString2, paramString1, true);
    if (localObject != null)
    {
      localSessionData.setUserSearches((ARUserSearches)localObject);
      int i = localARUserPreferences.getRecentUsedListSize();
      localJSWriter.statement("userSearches = new SavedSearches(" + i + ")");
      localJSWriter.startStatement("userSearches.Saved=");
      localJSWriter.openObj();
      ((ARUserSearches)localObject).emitSavedJS(localJSWriter);
      localJSWriter.closeObj();
      localJSWriter.endStatement();
      localJSWriter.startStatement("userSearches.Recent=");
      localJSWriter.openList();
      ((ARUserSearches)localObject).emitRecentJS(localJSWriter, i);
      localJSWriter.closeList();
      localJSWriter.endStatement();
    }
    localJSWriter.endFunction(false);
    ARUserQuickReports localARUserQuickReports = new ARUserQuickReports(str, paramString2, paramString1);
    boolean bool = localARUserQuickReports.loadQuickReports();
    if (!bool)
      localJSWriter.statement("var quickReportsExisting=null");
    else
      localARUserQuickReports.emitSavedJS(localJSWriter);
    writeSystemMessageDestination(localJSWriter, paramString1);
    localJSWriter.statement("var userDataOk=true");
    if (paramPrintWriter != null)
      paramPrintWriter.print(localJSWriter);
    else
      this.userDataString = localJSWriter.toString();
  }

  public UserDataEmitter(String paramString1, Form paramForm, String paramString2, PrintWriter paramPrintWriter, int paramInt, Form.ViewInfo paramViewInfo, OutputNotes paramOutputNotes)
    throws GoatException
  {
    SessionData localSessionData = SessionData.get();
    String str1 = localSessionData.getUserName();
    ServerLogin localServerLogin = localSessionData.getServerLogin(paramString1);
    String str2 = paramForm.getName();
    JSWriter localJSWriter1 = new JSWriter();
    String str3 = paramString1 + "_" + str2 + "_" + paramViewInfo.mLabel;
    localJSWriter1.startFunction(JSWriter.makeJSFunctionName("", "uddjsinit", str3), "");
    localJSWriter1.append("setUDDHolder('" + paramString1 + "','" + str2 + "','" + paramViewInfo.mLabel + "', ");
    localJSWriter1.startStatement(" new function()");
    localJSWriter1.openObj();
    localJSWriter1.append("\n");
    localJSWriter1.startThisFunction("uddinit", "windowID");
    localJSWriter1.startInterruptibleBlock();
    localJSWriter1.callFunction(false, "ARKWSetup", new Object[] { "windowID", "[2", JSWriter.escapeString(str1), "41", JSWriter.escapeString(localServerLogin.getUserGroupIDsKeyword()), "9", JSWriter.escapeString(localServerLogin.getUserGroupsKeyword()), "31", JSWriter.escapeString(localServerLogin.getUserRolesKeyword(paramForm.getAppName())) + "]" });
    localJSWriter1.endInterruptibleBlock();
    localJSWriter1.endFunction();
    ARUserPreferences localARUserPreferences = localSessionData.getPreferences();
    assert (localARUserPreferences != null);
    localARUserPreferences.emitTablePrefs(localJSWriter1, paramString1, str2, paramInt);
    if ((paramString2 != null) && (!paramString2.equals("")))
    {
      localObject = localSessionData.getWindowArg(paramString2);
      if (localObject != null)
        localJSWriter1.append("this.openingArgs=").append((String)localObject).append(";");
    }
    if ((paramString2 != null) && (!paramString2.equals("")))
    {
      localObject = localSessionData.getWindowQBE(paramString2);
      if (localObject != null)
        localJSWriter1.append("this.qbe=").append((String)localObject).append(";");
    }
    Object localObject = ARUserSearches.getUserSearches(str1, str2, paramString1, true);
    if (localObject != null)
    {
      localSessionData.setUserSearches((ARUserSearches)localObject);
      int i = localARUserPreferences.getRecentUsedListSize();
      localJSWriter1.statement("this.userSearches=new SavedSearches(" + i + ")");
      localJSWriter1.startStatement("this.userSearches.Saved=");
      localJSWriter1.openObj();
      ((ARUserSearches)localObject).emitSavedJS(localJSWriter1);
      localJSWriter1.closeObj();
      localJSWriter1.endStatement();
      localJSWriter1.startStatement("this.userSearches.Recent=");
      localJSWriter1.openList();
      ((ARUserSearches)localObject).emitRecentJS(localJSWriter1, i);
      localJSWriter1.closeList();
      localJSWriter1.endStatement();
    }
    ARUserQuickReports localARUserQuickReports = new ARUserQuickReports(str1, str2, paramString1);
    boolean bool = localARUserQuickReports.loadQuickReports();
    if (!bool)
    {
      localJSWriter1.statement("this.quickReportsExisting=null");
    }
    else
    {
      localARUserQuickReports.emitSavedJS(localJSWriter1);
      localJSWriter1.endStatement();
    }
    if (paramForm.getName().equals("AR System Customizable Home Page"))
    {
      localJSWriter1.statement("this.vf=new Object()");
      Map localMap = localSessionData.getCHPContentOBJMap();
      if ((localMap != null) && (!localMap.isEmpty()))
      {
        Iterator localIterator = localMap.keySet().iterator();
        while (localIterator.hasNext())
        {
          Integer localInteger = (Integer)localIterator.next();
          DHTMLRequestService.CHPContent localCHPContent = (DHTMLRequestService.CHPContent)localMap.get(localInteger);
          if (localCHPContent != null)
          {
            JSWriter localJSWriter2 = processViewFields(localCHPContent.getServer(), localCHPContent.getForm(), localCHPContent.getViewLabel());
            localJSWriter1.startStatement("this.vf[\"" + localInteger.intValue() + "\"]={");
            localJSWriter1.append(localJSWriter2);
            localJSWriter1.append(",server:\"" + localCHPContent.getServer() + "\",form:\"" + localCHPContent.getForm() + "\",view:\"" + localCHPContent.getViewLabel() + "\"}");
            localJSWriter1.endStatement();
          }
        }
      }
    }
    writeSystemMessageDestination(localJSWriter1, paramString1);
    if (paramOutputNotes != null)
      genMenus(localJSWriter1, paramOutputNotes, paramForm, paramViewInfo.mLabel);
    localJSWriter1.closeObj().endStatement();
    localJSWriter1.append(");\n");
    localJSWriter1.endFunction();
    localJSWriter1.statement("var " + JSWriter.makeJSFunctionName("uddjs_", str3, "_loaded") + " = true");
    if (paramPrintWriter != null)
      paramPrintWriter.print(localJSWriter1);
    else
      this.userDataString = localJSWriter1.toString();
  }

  private JSWriter processViewFields(String paramString1, String paramString2, String paramString3)
    throws GoatException
  {
    Form localForm = Form.get(paramString1, paramString2);
    Form.ViewInfo localViewInfo = localForm.getViewInfoByInference(paramString3, false, false);
    JSWriter localJSWriter = new JSWriter(new StringBuilder(102400));
    localJSWriter.append("cacheid:\"" + FormContext.get().getFieldGraphURLParam(localForm.getServerLogin().getPermissionsKey(), localViewInfo.getID(), localForm) + "\"");
    return localJSWriter;
  }

  public UserDataEmitter(String paramString)
    throws GoatException
  {
    SessionData localSessionData = SessionData.get();
    String str1 = localSessionData.getUserName();
    ServerLogin localServerLogin = localSessionData.getServerLogin(paramString);
    FormContext localFormContext = FormContext.get();
    JSWriter localJSWriter1 = new JSWriter();
    StringBuilder localStringBuilder = new StringBuilder("var prefTimeZone=");
    String str2 = null;
    ARUserPreferences localARUserPreferences1 = localSessionData.getPreferences();
    if (localARUserPreferences1 == null)
      localARUserPreferences1 = ARUserPreferences.getUserPreferences();
    if (localARUserPreferences1 != null)
      str2 = localARUserPreferences1.getTimeZone();
    if ((str2 == null) || (str2.length() == 0))
    {
      if (SessionData.get().isUseServer)
        localJSWriter1.statement(localStringBuilder.append("true"));
      else
        localJSWriter1.statement(localStringBuilder.append("false"));
    }
    else
      localJSWriter1.statement(localStringBuilder.append("true"));
    localJSWriter1.append(LocaleUtil.getTimezoneConstantsScript(localFormContext.getJSPath(), localSessionData.getLocale()));
    localJSWriter1.startFunction("UserData_Init", "");
    ARUserPreferences localARUserPreferences2 = localSessionData.getPreferences();
    assert (localARUserPreferences2 != null);
    localARUserPreferences2.emitJS(localJSWriter1);
    emitDateTimeFormat(localJSWriter1);
    localJSWriter1.startStatement("currencyCodes=");
    localJSWriter1.openObj();
    CurrencyDetail[] arrayOfCurrencyDetail = CurrencyCodes.GetAllCurrencyCodes(localServerLogin);
    for (int i = 0; i < arrayOfCurrencyDetail.length; i++)
    {
      if (i > 0)
        localJSWriter1.comma();
      localJSWriter1.append("\"" + arrayOfCurrencyDetail[i].getCurrencyCode() + "\":");
      localJSWriter1.openObj().property("l", CurrencyCodes.GetCurrencyLabel(localServerLogin, localSessionData.getLocale(), arrayOfCurrencyDetail[i].getCurrencyCode()));
      localJSWriter1.property("p", arrayOfCurrencyDetail[i].getPrecision());
      localJSWriter1.closeObj();
    }
    localJSWriter1.closeObj();
    localJSWriter1.endStatement();
    localJSWriter1.startStatement("currencyMenu=");
    localJSWriter1.openList();
    for (i = 0; i < arrayOfCurrencyDetail.length; i++)
    {
      localJSWriter1.listSep().openObj();
      localJSWriter1.propertyDestinedForHTML("l", arrayOfCurrencyDetail[i].getCurrencyCode() + " - " + CurrencyCodes.GetCurrencyLabel(localServerLogin, localSessionData.getLocale(), arrayOfCurrencyDetail[i].getCurrencyCode()));
      localJSWriter1.propertyDestinedForHTML("v", arrayOfCurrencyDetail[i].getCurrencyCode());
      localJSWriter1.closeObj();
    }
    localJSWriter1.closeList();
    localJSWriter1.endStatement();
    List localList = Configuration.getServerListFromString(localARUserPreferences2.getAlertServers());
    Configuration localConfiguration = Configuration.getInstance();
    if (localList.size() == 0)
      localList = localConfiguration.getPreferenceServers();
    localJSWriter1.startStatement("AlertInfo=").openObj();
    JSWriter localJSWriter2 = new JSWriter();
    JSWriter localJSWriter3 = new JSWriter();
    localJSWriter2.append("servers:").openList();
    localJSWriter3.append("schemas:").openList();
    for (int j = 0; j < localList.size(); j++)
    {
      String str4 = (String)localList.get(j);
      try
      {
        localJSWriter3.listSep().appendqs(ServerInfo.get(str4, true).getAlertSchema());
        localJSWriter2.listSep().appendqs(str4);
      }
      catch (GoatException localGoatException)
      {
      }
    }
    localJSWriter2.closeList();
    localJSWriter3.closeList();
    localJSWriter1.append(localJSWriter2.toString()).listSep().append(localJSWriter3.toString()).closeObj().endStatement();
    localJSWriter1.endFunction(false);
    localJSWriter1.statement("var showWaitCursor=" + WAIT_CURSOR_MODE + ",hoverWaitTime=" + localConfiguration.getHoverWaitTime() + ",enableAnimate=" + localConfiguration.getAnimationEffects() + ",enableFlashUIRendering=" + localConfiguration.getFlashUIRendering() + ",enableAttachApplet=" + localConfiguration.getAttachmentApplet() + ",xmlHttpGET=" + localConfiguration.isXMLHTTPGET() + ",iUniqueid=\"" + localFormContext.getInlineFormUniqueId(localServerLogin.getPermissionsKey()) + "\"");
    String str3 = localConfiguration.getProperty("arsystem.waiting_cursor_innerhtml", "").trim();
    if (str3.length() > 0)
      localJSWriter1.statement("var WaitCursorInnerHTML=\"" + JSWriter.escape(str3) + "\"");
    this.userDataString = localJSWriter1.toString();
  }

  public String getUserDataString()
  {
    return this.userDataString;
  }

  private void emitDateTimeFormat(JSWriter paramJSWriter)
  {
    SessionData localSessionData = SessionData.get();
    String str1 = localSessionData.getTimeFormatPattern();
    if (ARUserPreferences.CUSTOM_TIME_FORMAT.equals(localSessionData.getPreferences().getDisplayTimeFormat()))
    {
      String[] arrayOfString1 = localSessionData.getTimeParsePatterns();
      String str2 = "";
      String str3 = "";
      if (arrayOfString1 != null)
      {
        paramJSWriter.startStatement("ARDate.ARCustomTimeSetup(\"");
        str2 = arrayOfString1[0];
        str2 = str2.replaceAll("\"", "\\\"");
        str3 = arrayOfString1[1];
        paramJSWriter.append("^" + str2 + "$");
        paramJSWriter.append("\",\"");
        paramJSWriter.append(str3);
        paramJSWriter.append("\");");
        paramJSWriter.endStatement();
      }
      String[] arrayOfString2 = localSessionData.getDateParsePatterns();
      if (arrayOfString2 != null)
      {
        paramJSWriter.startStatement("ARDate.ARCustomDateSetup(\"");
        String str4 = arrayOfString2[0];
        str4 = str4.replaceAll("\"", "\\\"");
        String str5 = arrayOfString2[1];
        paramJSWriter.append("^" + str4);
        paramJSWriter.append("\",\"");
        paramJSWriter.append(str5);
        paramJSWriter.append("\",\"");
        if ((str2 == null) || (str2.length() == 0))
        {
          arrayOfString1 = DateTimeParser.getParsePattern(str1);
          str2 = arrayOfString1[0];
          str3 = arrayOfString1[1];
        }
        paramJSWriter.append("^" + str4 + " " + str2 + "$");
        paramJSWriter.append("\",\"");
        paramJSWriter.append(str5 + str3);
        paramJSWriter.append("\");");
        paramJSWriter.endStatement();
      }
    }
    paramJSWriter.startStatement("ARDate.ARSetDateTimeFormat(\"");
    paramJSWriter.append(getFormatPattern(localSessionData.getDateFormatPattern()));
    paramJSWriter.append("\",\"");
    paramJSWriter.append(getFormatPattern(str1));
    paramJSWriter.append("\")");
    paramJSWriter.endStatement();
  }

  private String getFormatPattern(String paramString)
  {
    if (paramString.length() == 0)
      return "";
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder(20);
    int j = 0;
    for (int k = 0; k < paramString.length(); k++)
    {
      char c = paramString.charAt(k);
      if ((j == 0) && ("GyMdkHmsSEDFwWahKzYeugAZ".indexOf(c) != -1))
      {
        i = 1;
        while ((k + 1 < paramString.length()) && (paramString.charAt(k + 1) == c))
        {
          i++;
          k++;
        }
        localStringBuilder.append(getFormatforChar(c, i));
      }
      else if (c == '\'')
      {
        localStringBuilder.append(c);
        if ((k + 1 < paramString.length()) && (paramString.charAt(k + 1) == '\''))
        {
          localStringBuilder.append(c);
          k++;
        }
        else
        {
          j = j == 0 ? 1 : 0;
        }
      }
      else if (c == '"')
      {
        localStringBuilder.append("\\\"");
      }
      else
      {
        localStringBuilder.append(c);
      }
    }
    return localStringBuilder.toString();
  }

  private char getFormatforChar(char paramChar, int paramInt)
  {
    char c = ' ';
    switch (paramChar)
    {
    case 'Y':
    case 'y':
      if (paramInt < 3)
        c = 'y';
      else
        c = 'Y';
      break;
    case 'M':
      if (paramInt == 1)
        c = 'n';
      else if (paramInt == 2)
        c = 'N';
      else if (paramInt == 3)
        c = 'm';
      else
        c = 'M';
      break;
    case 'D':
    case 'd':
      if (paramInt == 1)
        c = 'd';
      else
        c = 'D';
      break;
    case 'E':
      if (paramInt < 4)
        c = 'e';
      else
        c = 'E';
      break;
    case 'h':
      if (paramInt == 1)
        c = 'h';
      else
        c = 'H';
      break;
    case 'H':
      if (paramInt == 1)
        c = 'i';
      else
        c = 'I';
      break;
    case 'm':
      if (paramInt == 1)
        c = 'u';
      else
        c = 'U';
      break;
    case 's':
      if (paramInt == 1)
        c = 's';
      else
        c = 'S';
      break;
    default:
      c = paramChar;
    }
    return c;
  }

  private void genMenus(JSWriter paramJSWriter, OutputNotes paramOutputNotes, Form paramForm, String paramString)
    throws GoatException
  {
    String str = paramForm.getServerName();
    boolean bool = paramOutputNotes.hasGroupMenuMapping();
    paramJSWriter.append("this.ARMenus=getFormDataHolder('" + str + "','" + paramForm.getName() + "','" + paramString + "').ARMenus;");
    paramJSWriter.append("var ARMenus=this.ARMenus;");
    if (bool)
      try
      {
        Menu.get(new Menu.MKey(str, SessionData.get().getLocale(), "GLMENU", paramForm.getAppName()));
      }
      catch (GoatException localGoatException)
      {
        localGoatException.printStackTrace();
      }
    if ((paramOutputNotes.isGroupFieldPresent()) || (bool))
      GroupMenu.emitGroupInfo(paramJSWriter, str, paramOutputNotes.getGroupFieldMenuSet(), paramForm.getAppName());
  }

  static
  {
    WAIT_CURSOR_MODE = 2;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.UserDataEmitter
 * JD-Core Version:    0.6.1
 */