package com.remedy.arsys.sso;

import com.remedy.arsys.log.Log;
import com.remedy.arsys.session.UserCredentials;
import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BMCWAMAuthenticator extends AbstractAuthenticator
{
  private static final String SSO_REQUEST_HEADER_KEY = "BMC_WAM_AUTHENTICATED_USER";

  public void destroy()
  {
  }

  public UserCredentials getAuthenticatedCredentials(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
    String str = paramHttpServletRequest.getHeader("BMC_WAM_AUTHENTICATED_USER");
    if ((str != null) && (str.length() > 0))
      return new UserCredentials(str, null, encrypt(str));
    arLog.log(Level.SEVERE, "BMCWAMAuthenticator: Received unauthenticated request.");
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.sso.BMCWAMAuthenticator
 * JD-Core Version:    0.6.1
 */