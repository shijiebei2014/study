package com.remedy.arsys.sso;

import com.remedy.arsys.log.Log;
import com.remedy.arsys.session.UserCredentials;
import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OracleAuthenticator extends AbstractAuthenticator
{
  private static final String GUID = "Osso-User-Guid";
  private static final int AUTH_REQUEST_CODE = 499;
  private static final String AUTH_REQUEST_STRING = "Oracle SSO";
  private static final String FORCED_AUTH_STRING = "Osso-Paranoid";

  public void destroy()
  {
  }

  public UserCredentials getAuthenticatedCredentials(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
    String str1 = paramHttpServletRequest.getRemoteUser();
    String str2 = paramHttpServletRequest.getHeader("Osso-User-Guid");
    if ((str1 != null) && (str1.length() > 0) && (str2 != null) && (str2.length() > 0))
      return new UserCredentials(str1.toLowerCase(), null, encrypt(str2));
    arLog.log(Level.SEVERE, "OracleAuthenticator: Received unauthenticated request.");
    paramHttpServletResponse.setHeader("Osso-Paranoid", "true");
    paramHttpServletResponse.sendError(499, "Oracle SSO");
    return null;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.sso.OracleAuthenticator
 * JD-Core Version:    0.6.1
 */