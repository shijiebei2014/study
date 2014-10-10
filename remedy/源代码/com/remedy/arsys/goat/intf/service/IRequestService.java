package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.UrlHelper;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract interface IRequestService
{
  public abstract void requestDispatch(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, UrlHelper paramUrlHelper)
    throws GoatException, IOException;

  public abstract Map<String, String> getRequestParams();

  public abstract String getRequestParamsString();
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IRequestService
 * JD-Core Version:    0.6.1
 */