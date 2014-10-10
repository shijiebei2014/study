package com.remedy.arsys.goat.intf;

import com.remedy.arsys.support.Validator;

public abstract interface Constants
{
  public static final String REQUEST_SERVICE_PARAM_FORMAT = "format";
  public static final String REQUEST_SERVICE_FORMAT_HTML = "html";
  public static final String REQUEST_SERVICE_FORMAT_FLASH = "flash";
  public static final String REQUEST_SERVICE_FORMAT_FLEX = "flex";
  public static final String ACCEPT_ENCODING = "accept-encoding";
  public static final String CONTENT_ENCODING = "Content-Encoding";
  public static final String GZIP = "gzip";
  public static final String UTF8 = "UTF-8";
  public static final int compMinRespSz = 512;
  public static final String SHOCKWAVE_FLASH = "application/x-shockwave-flash";
  public static final String IMAGE = "image";
  public static final String STATIC_USERDATA_JS = "uds.js";
  public static final String DYNAMIC_USERDATA_JS = "udd.js";
  public static final String CHP_FORM_NAME = "AR System Customizable Home Page";
  public static final String HPL_FORM_NAME = "AR System Home Page Layout";
  public static final String HPD_FORM_NAME = "AR System Home Page Descriptor";
  public static final int ENTRY_ID_FID = 1;
  public static final int CHP_UL_CONTENTID = 80028;
  public static final int CHP_LL_CONTENTID = 80058;
  public static final int CHP_UR_CONTENTID = 80043;
  public static final int CHP_LR_CONTENTID = 80073;
  public static final String VF_DEFURL = Validator.escape("javascript:\"<HTML></HTML>\"");
  public static final String WINDOWID = "windowID";
  public static final String HTTPONLY = "HttpOnly";
  public static final String JSESSIONID = "JSESSIONID";
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.Constants
 * JD-Core Version:    0.6.1
 */