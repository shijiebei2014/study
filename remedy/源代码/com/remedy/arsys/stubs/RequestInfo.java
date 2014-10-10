package com.remedy.arsys.stubs;

public class RequestInfo
{
  private String mServer;
  private String mUsername;
  private String mPassword;
  private String mAppName;
  private String mFormName;
  private String mViewName;
  private String mFieldId;
  private String mAuthentication;
  private String mWindowID;

  public RequestInfo(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9)
  {
    this.mServer = paramString1;
    this.mUsername = paramString2;
    this.mPassword = paramString3;
    this.mAuthentication = paramString4;
    this.mAppName = paramString5;
    this.mFieldId = paramString6;
    this.mWindowID = paramString7;
    this.mFormName = paramString8;
    this.mViewName = paramString9;
  }

  public String getServer()
  {
    return this.mServer;
  }

  public String getUser()
  {
    return this.mUsername;
  }

  public String getPassword()
  {
    return this.mPassword;
  }

  public String getAuthentication()
  {
    return this.mAuthentication;
  }

  public String getAppname()
  {
    return this.mAppName;
  }

  public String getFieldId()
  {
    return this.mFieldId;
  }

  public String getWindowID()
  {
    return this.mWindowID;
  }

  public void setWindowID(String paramString)
  {
    this.mWindowID = paramString;
  }

  public String getFormName()
  {
    return this.mFormName;
  }

  public String getViewName()
  {
    return this.mViewName;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.RequestInfo
 * JD-Core Version:    0.6.1
 */