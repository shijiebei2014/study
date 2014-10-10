package com.remedy.arsys.session;

public abstract interface HttpSessionKeys
{
  public static final String REDO_LOGIN = "arsystem.redo_login";
  public static final String USER_CREDENTIALS = "usercredentials";
  public static final String USER_AUTHENTICATED = "arsystem.user_authenticated";
  public static final String USER_TIMEZONE = "usertimezone";
  public static final String SERVER_MESSAGES = "arsystem.authentication.messages";
  public static final String SERVER_CACHE_IDS = "servercacheids";
  public static final String RENDERING_ENGINE = "rendering_engine";
  public static final String CUSTOMLOGOUT_INCLUDE = "arsys.customlogout.pathelements";
  public static final String LOGIN_NAME = "arsys_login_name";
  public static final String LOGIN_MSG = "arsys_login_msg";
  public static final String LOGIN_USER_ERROR = "arsys_login_user_error";
  public static final String PASSWORD_ERROR = "arsys_login_password_error";
  public static final String TARGET_URL = "returnBack";
  public static final String MULTI_IP_OVERRIDE_MSG = "arsys_multi_ip_override_msg";
  public static final String WAIT_TARGET_URL = "form_wait_returnBack";
  public static final String WAIT_TARGET_URL_PARAMS = "form_wait_returnBack_params";
  public static final String WAIT_TARGET_URL_CACHEID = "cacheid";
  public static final String WAIT_TARGET_URL_WAIT = "wait";
  public static final String ORG_REQ_MAP_TAG = "arsystem.vfservlet.orgreqmap";
  public static final String CACHE_CONTROL_MAP = "arsystem_cache_control";
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.session.HttpSessionKeys
 * JD-Core Version:    0.6.1
 */