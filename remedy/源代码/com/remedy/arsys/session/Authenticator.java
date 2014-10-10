package com.remedy.arsys.session;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract interface Authenticator
{
  public abstract void init(Map paramMap);

  public abstract void destroy();

  public abstract UserCredentials getAuthenticatedCredentials(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.session.Authenticator
 * JD-Core Version:    0.6.1
 */