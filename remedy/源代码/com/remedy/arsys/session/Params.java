package com.remedy.arsys.session;

import com.remedy.arsys.support.Validator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class Params
{
  public static final String FORM = "form";
  public static final String VIEW = "view";
  public static final String MODE = "mode";
  public static final String EID = "eid";
  public static final String QUAL = "qual";
  public static final String APP = "app";
  public static final String USERNAME = "username";
  public static final String USERNAME_LENGTH = "254";
  public static final String PASSWORD = "pwd";
  public static final String PASSWORD_LENGTH = "61";
  public static final String TIMEZONE = "timezone";
  public static final String AUTHENTICATION_STRING = "auth";
  public static final String AUTHENTICATION_STRING_LENGTH = "2048";
  public static final String PASSWORD_ENCRYPTED = "encpwd";
  public static final String ENCRYPTED_CRED = "enccred";
  public static final String GOTO_URL = "goto";
  public static final String SERVER = "server";
  public static final String FEDSERVER = "fedsrv";
  public static final String GUESTWARN = "gw";
  public static final String ERROR_MSGS = "siMap";
  private static final int MAX_URL_LENGTH = 2048;
  public static final String IP_OVERRIDE_URI = "overrideURI";
  public static final String IP_OVERRIDE = "ipoverride";
  public static final String RENDER_SEL = "render_sel";
  public static final String RENDER_SEL_FLEX = "flex";
  public static final String RENDER_SEL_DHTML = "html";
  public static final String TIMEZONE_INDICATOR = "tzind";
  private HttpServletRequest mReq;
  private Map mMap;

  public Params(HttpServletRequest paramHttpServletRequest)
  {
    this.mReq = paramHttpServletRequest;
    this.mMap = null;
  }

  public void setMap(Map paramMap)
  {
    this.mMap = paramMap;
  }

  public String get(String paramString)
  {
    String str = null;
    if (this.mMap != null)
    {
      assert (this.mMap.size() > 0);
      str = (String)this.mMap.get(paramString);
    }
    if (str == null)
    {
      assert (this.mReq != null);
      if (Validator.isStringLengthValid(this.mReq.getRequestURL() + this.mReq.getQueryString(), 2048))
      {
        str = this.mReq.getParameter(paramString);
        if (str != null)
          str = str.trim();
        if ((this.mMap != null) && (str != null))
          this.mMap.put(paramString, str);
      }
    }
    return Validator.sanitizeCRandLF(str);
  }

  public Map getMap()
  {
    return this.mMap;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.session.Params
 * JD-Core Version:    0.6.1
 */