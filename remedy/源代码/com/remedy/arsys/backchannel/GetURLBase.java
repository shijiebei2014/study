package com.remedy.arsys.backchannel;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatApplicationContainer;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.HashMap;
import java.util.Map;

public abstract class GetURLBase extends NDXRequest
{
  protected Form.ViewInfo mViewInfo;

  GetURLBase(String paramString)
  {
    super(paramString);
  }

  protected String getURL(FormContext paramFormContext, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
    throws GoatException
  {
    Object localObject;
    if (paramString3.length() == 0)
    {
      paramString3 = null;
    }
    else
    {
      localObject = null;
      try
      {
        localObject = GoatApplicationContainer.get(SessionData.get().getServerLogin(paramString1), paramString3);
      }
      catch (GoatException localGoatException1)
      {
      }
      if (localObject == null)
        paramString3 = null;
    }
    paramFormContext.setApplication(paramString3);
    String str1 = Configuration.getInstance().getLongName(paramString1);
    try
    {
      localObject = Form.get(str1, paramString2);
    }
    catch (GoatException localGoatException2)
    {
      if ((localGoatException2.getCause() instanceof GoatException))
        throw ((GoatException)localGoatException2.getCause());
      throw localGoatException2;
    }
    this.mViewInfo = ((Form)localObject).getViewInfoByInference(paramString4, true, false);
    String str2 = this.mViewInfo.getLabel();
    assert ((paramString5 != null) && (paramString6 != null));
    if ((!paramString5.equals("")) && (!paramString6.equals("")))
      SessionData.get().putWindowArg(paramString5, paramString6);
    String str3 = FormContext.get().getFieldGraphURLParam(((Form)localObject).getServerLogin().getPermissionsKey(), this.mViewInfo.getID(), (Form)localObject);
    String str4 = paramFormContext.getContextURL() + paramFormContext.getFormURL(paramString1, paramString2, str2) + "?cacheid=" + str3;
    if (SessionData.get().isFlexEnabled())
      str4 = str4 + "&" + "format" + "=" + "flex";
    return str4;
  }

  protected Map<String, String> getURLParts(FormContext paramFormContext, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
    throws GoatException
  {
    Object localObject;
    if (paramString3.length() == 0)
    {
      paramString3 = null;
    }
    else
    {
      localObject = null;
      try
      {
        localObject = GoatApplicationContainer.get(SessionData.get().getServerLogin(paramString1), paramString3);
      }
      catch (GoatException localGoatException1)
      {
      }
      if (localObject == null)
        paramString3 = null;
    }
    paramFormContext.setApplication(paramString3);
    try
    {
      localObject = Form.get(paramString1, paramString2);
    }
    catch (GoatException localGoatException2)
    {
      if ((localGoatException2.getCause() instanceof GoatException))
        throw ((GoatException)localGoatException2.getCause());
      throw localGoatException2;
    }
    assert ((paramString5 != null) && (paramString6 != null));
    if ((!paramString5.equals("")) && (!paramString6.equals("")))
      SessionData.get().putWindowArg(paramString5, paramString6);
    this.mViewInfo = ((Form)localObject).getViewInfoByInference(paramString4, true, false);
    String str1 = this.mViewInfo.getLabel();
    String str2 = FormContext.get().getFieldGraphURLParam(((Form)localObject).getServerLogin().getPermissionsKey(), this.mViewInfo.getID(), (Form)localObject);
    HashMap localHashMap = new HashMap();
    localHashMap.put("server", paramString1);
    String str3 = paramFormContext.getApplication();
    localHashMap.put("app", str3 == null ? "" : str3);
    localHashMap.put("form", paramString2);
    localHashMap.put("view", str1);
    localHashMap.put("cacheid", str2);
    return localHashMap;
  }

  protected void getURLInfoforInline(FormContext paramFormContext, String paramString1, String paramString2, String paramString3, String paramString4)
    throws GoatException
  {
    Object localObject;
    if (paramString3.length() == 0)
    {
      paramString3 = null;
    }
    else
    {
      localObject = null;
      try
      {
        localObject = GoatApplicationContainer.get(SessionData.get().getServerLogin(paramString1), paramString3);
      }
      catch (GoatException localGoatException1)
      {
      }
      if (localObject == null)
        paramString3 = null;
    }
    paramFormContext.setApplication(paramString3);
    try
    {
      localObject = Form.get(paramString1, paramString2);
    }
    catch (GoatException localGoatException2)
    {
      if ((localGoatException2.getCause() instanceof GoatException))
        throw ((GoatException)localGoatException2.getCause());
      throw localGoatException2;
    }
    this.mViewInfo = ((Form)localObject).getViewInfoByInference(paramString4, true, false);
    String str1 = this.mViewInfo.getLabel();
    String str2 = FormContext.get().getFieldGraphURLParam(((Form)localObject).getServerLogin().getPermissionsKey(), this.mViewInfo.getID(), (Form)localObject);
    property("view", str1).property("cacheid", str2);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetURLBase
 * JD-Core Version:    0.6.1
 */