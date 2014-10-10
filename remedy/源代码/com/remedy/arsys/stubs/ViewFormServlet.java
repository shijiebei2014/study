package com.remedy.arsys.stubs;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARQualifierHelper;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.QualifierInfo;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.session.LoginServlet;
import com.remedy.arsys.session.Params;
import com.remedy.arsys.session.UserCredentials;
import com.remedy.arsys.share.CacheDirectiveController;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ViewFormServlet extends GoatHttpServlet
{
  private static final int MAX_GET_URL_LEN = 2048;
  private static final int MAX_LOGIN_LENGTH = 254;
  private static final int MAX_PW_LENGTH = 30;
  private static final Field[] EMPTY_FIELD_ARR = new Field[0];

  protected final void doRequest(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException, ServletException, GoatException
  {
    Params localParams = new Params(paramHttpServletRequest);
    HttpSession localHttpSession = paramHttpServletRequest.getSession();
    if (localHttpSession != null)
    {
      localMap = (Map)localHttpSession.getAttribute("arsystem.vfservlet.orgreqmap");
      localParams.setMap(localMap);
    }
    Map localMap = localParams.getMap();
    if (localMap == null)
    {
      localMap = paramHttpServletRequest.getParameterMap();
      if (Validator.URLParamHasXSSTag(localMap))
      {
        paramHttpServletResponse.setStatus(400);
        throw new GoatException(9423);
      }
    }
    else if (Validator.URLParamHasXSSTag_String(localMap))
    {
      paramHttpServletResponse.setStatus(400);
      throw new GoatException(9423);
    }
    String str1 = localParams.get("form");
    String str2 = localParams.get("view");
    String str3 = localParams.get("mode");
    String str4 = localParams.get("eid");
    String str5 = localParams.get("qual");
    Object localObject1 = localParams.get("username");
    Object localObject2 = localParams.get("pwd");
    if (localObject2 == null)
      localObject2 = "";
    String str6 = localParams.get("server");
    String str7 = localParams.get("app");
    Object localObject3 = localParams.get("auth");
    String str8 = localParams.get("usertimezone");
    String str9 = localParams.get("enccred");
    if ((str9 != null) && (str9.equals("1")) && (str6 != null) && (str6.length() > 0))
    {
      localObject4 = new RSAAuthenticator(str6);
      String str10 = null;
      localObject5 = null;
      localObject6 = null;
      if (localObject1 != null)
        str10 = ((RSAAuthenticator)localObject4).decrypt((String)localObject1, str6);
      if (localObject2 != null)
        localObject5 = ((RSAAuthenticator)localObject4).decrypt((String)localObject2, str6);
      if (localObject3 != null)
        localObject6 = ((RSAAuthenticator)localObject4).decrypt((String)localObject3, str6);
      if (str10 != null)
        localObject1 = str10;
      if (localObject5 != null)
        localObject2 = localObject5;
      if (localObject6 != null)
        localObject3 = localObject6;
    }
    Object localObject4 = localParams.get("gw");
    int i = 0;
    if ((localObject4 != null) && (((String)localObject4).equals("1")))
      i = 1;
    Object localObject5 = new HashMap();
    if (localObject1 != null)
      ((Map)localObject5).put("username", localObject1);
    if (localObject2 != null)
      ((Map)localObject5).put("pwd", localObject2);
    if (localObject3 != null)
      ((Map)localObject5).put("auth", localObject2);
    if ((str6 == null) || (str6.length() == 0) || (str1 == null) || (str1.length() == 0))
      throw new GoatException(9359);
    if ((str6.length() > 64) || (str1.length() > 254))
      throw new GoatException(9389);
    if (((str1 == null) && (localParams.get("formalias") != null)) || ((str2 == null) && (localParams.get("viewalias") != null)) || ((str7 == null) && (localParams.get("appalias") != null)))
      throw new GoatException(9362);
    Object localObject6 = null;
    int j = 1;
    Object localObject7;
    Object localObject9;
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramHttpServletRequest.getContextPath());
      if ((str7 != null) && (str7.length() > 0))
        localStringBuilder.append("/apps/").append(URLEncoder.encode(str6, "UTF-8")).append("/").append(URLEncoder.encode(str7, "UTF-8"));
      else
        localStringBuilder.append("/forms/").append(URLEncoder.encode(str6, "UTF-8"));
      localStringBuilder.append("/").append(URLEncoder.encode(str1, "UTF-8"));
      if (str2 != null)
        localStringBuilder.append("/").append(URLEncoder.encode(str2, "UTF-8"));
      if (localMap != null)
      {
        localObject7 = localMap.keySet();
        if (localObject7 != null)
        {
          Iterator localIterator = ((Set)localObject7).iterator();
          while (localIterator.hasNext())
          {
            localObject9 = (String)localIterator.next();
            if (((((String)localObject9).charAt(0) == 'F') || ((((String)localObject9).charAt(0) == 'f') && (((String)localObject9).length() > 1))) && (((String)localObject9).charAt(1) >= '0') && (((String)localObject9).charAt(1) <= '9'))
            {
              localStringBuilder.append(j != 0 ? "?" : "&").append((String)localObject9).append("=").append(URLEncoder.encode(localParams.getMap() == null ? paramHttpServletRequest.getParameter((String)localObject9) : localParams.get((String)localObject9), "UTF-8"));
              j = 0;
            }
          }
        }
      }
      if (str4 != null)
      {
        localObject7 = URLEncoder.encode(str4, "UTF-8");
        localStringBuilder.append(j != 0 ? "?" : "&").append("eid=").append((String)localObject7);
        j = 0;
      }
      else if (str5 != null)
      {
        localObject7 = URLEncoder.encode(str5, "UTF-8");
        if (localStringBuilder.length() + ((String)localObject7).length() + 1 < 2048)
          localStringBuilder.append(j != 0 ? "?" : "&").append("qual=").append((String)localObject7);
        else
          localObject6 = new JSWriter();
        j = 0;
      }
      else if (str3 != null)
      {
        localObject7 = URLEncoder.encode(str3, "UTF-8");
        localStringBuilder.append(j != 0 ? "?" : "&").append("mode=").append((String)localObject7);
        j = 0;
      }
      ((Map)localObject5).put("goto", localStringBuilder.toString());
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    SessionData localSessionData = setupSessionData(paramHttpServletRequest);
    Object localObject8;
    if (localSessionData == null)
    {
      if ((localObject1 == null) || (((String)localObject1).length() == 0) || (((String)localObject1).length() > 254) || (((String)localObject2).length() > 30))
      {
        localObject7 = new HashMap();
        ((Map)localObject7).put("form", str1);
        ((Map)localObject7).put("view", str2);
        ((Map)localObject7).put("mode", str3);
        ((Map)localObject7).put("eid", str4);
        ((Map)localObject7).put("qual", str5);
        ((Map)localObject7).put("server", str6);
        ((Map)localObject7).put("app", str7);
        ((Map)localObject7).put("usertimezone", str8);
        localHttpSession = paramHttpServletRequest.getSession();
        localHttpSession.removeAttribute("arsystem.vfservlet.orgreqmap");
        localHttpSession.setAttribute("arsystem.vfservlet.orgreqmap", localObject7);
        localHttpSession.setAttribute("returnBack", Validator.sanitizeCRandLF((String)((Map)localObject5).get("goto")));
        boolean bool = Login.establishSession(paramHttpServletRequest, paramHttpServletResponse);
        if (!bool)
          return;
      }
      else
      {
        localObject7 = new UserCredentials((String)localObject1, (String)localObject2, (String)localObject3);
        localObject8 = Login.establishSession(paramHttpServletRequest, paramHttpServletResponse, (UserCredentials)localObject7, true);
        if (localObject8 == null)
          return;
        if (i != 0)
        {
          localObject9 = null;
          String str11 = localParams.get("ipoverride");
          localObject9 = LoginServlet.doLogin(paramHttpServletRequest, (UserCredentials)localObject7, str11, SessionData.getLocale(paramHttpServletRequest));
          if (localObject9 != null)
            ((HttpSession)localObject8).setAttribute("arsystem.authentication.messages", localObject9);
        }
      }
    }
    else if ((localObject1 != null) && (((String)localObject1).length() > 0) && (!localSessionData.getUserName().equals(localObject1)))
    {
      paramHttpServletRequest.getSession().removeAttribute("arsystem.vfservlet.orgreqmap");
      throw new GoatException(9361, localSessionData.getUserName());
    }
    paramHttpServletRequest.getSession().removeAttribute("arsystem.vfservlet.orgreqmap");
    if (localObject6 != null)
    {
      ((JSWriter)localObject6).openObj().property("qual", getJSQualObjStr(str6, str1, str2, str5)).closeObj();
      SessionData.get().putWindowArg(str6.toLowerCase() + str1, ((JSWriter)localObject6).toString());
    }
    CacheDirectiveController.forceContentUpdate(paramHttpServletRequest, paramHttpServletResponse);
    if (str8 != null)
    {
      localObject7 = (SessionData)paramHttpServletRequest.getSession().getAttribute("com.remedy.arsys.stubs.sessionData");
      localObject8 = new StringBuffer((String)((Map)localObject5).get("goto"));
      str8 = URLEncoder.encode(localObject7 == null ? str8 : ((SessionData)localObject7).getTimeZone(), "UTF-8");
      ((StringBuffer)localObject8).append(j != 0 ? "?" : "&").append("usertimezone=").append(str8);
      j = 0;
      ((Map)localObject5).put("goto", ((StringBuffer)localObject8).toString());
    }
    paramHttpServletResponse.sendRedirect((String)((Map)localObject5).get("goto"));
  }

  private JSWriter getJSQualObjStr(String paramString1, String paramString2, String paramString3, String paramString4)
    throws GoatException
  {
    CachedFieldMap localCachedFieldMap = Form.get(paramString1, paramString2).getCachedFieldMap();
    ArrayList localArrayList = new ArrayList(localCachedFieldMap.values());
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString1);
    QualifierInfo localQualifierInfo;
    try
    {
      if (ARQualifier.isEncodedQualStr(paramString4))
        localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(localServerLogin, paramString4);
      else
        localQualifierInfo = localServerLogin.parseQualification(paramString4, localArrayList, localArrayList, 0);
    }
    catch (ARException localARException1)
    {
      localObject = Form.get(paramString1, paramString2).getViewInfoByInference(paramString3, false, false);
      FieldGraph localFieldGraph = FieldGraph.get(paramString1, paramString2, paramString3);
      try
      {
        localQualifierInfo = localFieldGraph.GetQualifierHelper().parseQualification(localServerLogin.getLocale(), paramString4, localServerLogin.getTimeZone());
      }
      catch (ARException localARException2)
      {
        throw new GoatException(localARException2);
      }
    }
    String str = Qualifier.AREncodeARQualifierStruct(localServerLogin, localQualifierInfo);
    Object localObject = new JSWriter();
    ((JSWriter)localObject).openObj().property("qual", str).append(",fieldids:[],fieldvals:[],fieldtypes:[]").closeObj();
    return localObject;
  }

  private final SessionData setupSessionData(HttpServletRequest paramHttpServletRequest)
  {
    HttpSession localHttpSession = paramHttpServletRequest.getSession(false);
    if ((localHttpSession != null) && (!localHttpSession.isNew()) && (paramHttpServletRequest.isRequestedSessionIdValid()))
    {
      SessionData localSessionData = (SessionData)localHttpSession.getAttribute("com.remedy.arsys.stubs.sessionData");
      if (localSessionData != null)
      {
        MLog.fine("ViewFormServlet: SessionID=" + localSessionData.getID());
        return localSessionData;
      }
      MLog.fine("ViewFormServlet: No SessionData");
    }
    else
    {
      MLog.fine("ViewFormServlet: No session or new session");
    }
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ViewFormServlet
 * JD-Core Version:    0.6.1
 */