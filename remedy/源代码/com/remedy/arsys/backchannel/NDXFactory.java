package com.remedy.arsys.backchannel;

import com.remedy.arsys.log.ARServerLog;
import com.remedy.arsys.stubs.SessionData;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class NDXFactory
{
  public static NDXRequest handleRequest(String paramString)
  {
    return handleRequest(paramString, null);
  }

  public static NDXRequest handleRequest(String paramString, OutputStream paramOutputStream)
  {
    int i = paramString.indexOf('/');
    if (i == -1)
      return null;
    String str1 = paramString.substring(0, i);
    i++;
    String str2;
    if (i >= paramString.length())
      str2 = "";
    else
      str2 = paramString.substring(i);
    SessionData localSessionData = SessionData.tryGet();
    if ((localSessionData != null) && (localSessionData.isWebTimingsEnabled()) && (!str1.equals("GetAPITimings")))
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss.SSS");
      localSessionData.getServerLog().log("<MTT > /* " + localSimpleDateFormat.format(Long.valueOf(System.currentTimeMillis())) + " */" + "+MidTier " + paramString + "\n");
    }
    if (str1.equals("GetEntryList"))
      return new GetEntryListAgent(str2);
    if (str1.equals("SetEntryList"))
      return new SetEntryListAgent(str2);
    if (str1.equals("GetEntry"))
      return new GetEntryAgent(str2);
    if (str1.equals("SetEntry"))
      return new SetEntryAgent(str2);
    if (str1.equals("GetSQLEntryList"))
      return new GetSQLEntryListAgent(str2);
    if (str1.equals("DeleteEntry"))
      return new DeleteEntryAgent(str2);
    if (str1.equals("CompileQualification"))
      return new CompileQualificationAgent(str2);
    if (str1.equals("CompileExternalQualification"))
      return new CompileExternalQualificationAgent(str2);
    if (str1.equals("ServerRunProcess"))
      return new ServerRunProcessAgent(str2);
    if (str1.equals("ExpandMenu"))
      return new ExpandMenuAgent(str2);
    if (str1.equals("GetMenuDefinition"))
      return new GetMenuDefinitionAgent(str2);
    if (str1.equals("GetARUserPreference"))
      return new GetARUserPreferenceAgent(str2);
    if (str1.equals("SetARUserPreference"))
      return new SetARUserPreferenceAgent(str2);
    if (str1.equals("RefreshARUserPreferences"))
      return new RefreshARUserPreferencesAgent(str2);
    if (str1.equals("GetURLForForm"))
      return new GetURLForFormAgent(str2);
    if (str1.equals("GetTableEntryList"))
    {
      if (paramOutputStream != null)
        return new GetTableEntryListAgent(str2, paramOutputStream);
      return new GetTableEntryListAgent(str2);
    }
    if (str1.equals("GetQBETableEntryList"))
    {
      if (paramOutputStream != null)
        return new GetQBETableEntryListAgent(str2, paramOutputStream);
      return new GetQBETableEntryListAgent(str2);
    }
    if (str1.equals("GetGuideServerAndForm"))
      return new GetGuideServerAndFormAgent(str2);
    if (str1.equals("GetCurrencyExchangeRates"))
      return new GetCurrencyExchangeRatesAgent(str2);
    if (str1.equals("GetPushFieldTypes"))
      return new GetPushFieldTypesAgent(str2);
    if (str1.equals("GetServerTimestamp"))
      return new GetServerTimestampAgent(str2);
    if (str1.equals("MarkAlert"))
      return new MarkAlertAgent(str2);
    if (str1.equals("ParseAndEncodeQualifier"))
      return new ParseAndEncodeQualifierAgent(str2);
    if (str1.equals("ParseAndEvaluateQualifier"))
      return new ParseAndEvaluateQualifierAgent(str2);
    if (str1.equals("GetFieldDefaults"))
      return new GetFieldDefaultsAgent(str2);
    if (str1.equals("SetOverride"))
      return new SetOverrideAgent(str2);
    if (str1.equals("SaveSearch"))
      return new SaveSearchAgent(str2);
    if (str1.equals("DeleteSearch"))
      return new DeleteSearchAgent(str2);
    if (str1.equals("SaveTableSettings"))
      return new SaveTableSettingsAgent(str2);
    if (str1.equals("ExecuteService"))
      return new ExecuteServiceAgent(str2);
    if (str1.equals("GetARTemplate"))
      return new GetARTemplateAgent(str2);
    if (str1.equals("GetOpenWindowURL"))
      return new GetOpenWindowURLAgent(str2);
    if (str1.equals("GetBulkTableEntryList"))
      return new GetBulkTableEntryListAgent(str2);
    if (str1.equals("SaveQuickReport"))
      return new SaveQuickReportAgent(str2);
    if (str1.equals("DeleteQuickReport"))
      return new DeleteQuickReportAgent(str2);
    if (str1.equals("ModifyQuickReport"))
      return new ModifyQuickReportAgent(str2);
    if (str1.equals("GetAPITimings"))
      return new GetAPITimingsAgent(str2);
    if (str1.equals("GetHTMLScriptForViewField"))
      return new GetHTMLScriptForViewFieldAgent(str2);
    if (str1.equals("GetOpenWindowQueryVF"))
      return new GetOpenWindowQueryVFAgent(str2);
    if (str1.equals("LoadQueryWidget"))
      return new LoadQueryWidgetAgent(str2);
    if (str1.equals("SpellChecker"))
      return new SpellCheckerAgent(str2);
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXFactory
 * JD-Core Version:    0.6.1
 */