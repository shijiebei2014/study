package com.remedy.arsys.sso;

import com.bmc.atrium.sso.common.AtriumSSOException;
import com.bmc.atrium.sso.sdk.SSOToken;
import com.remedy.arsys.session.Params;
import com.remedy.arsys.session.UserCredentials;
import com.remedy.arsys.support.Validator;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AtriumSSOAuthenticator extends AbstractAuthenticator
{
  private Class filterClass;
  private final String getTokenMethodName = "getToken";
  private String timezone;

  public void init(Map paramMap)
  {
    super.init(paramMap);
    try
    {
      this.filterClass = Class.forName("com.bmc.atrium.sso.agents.web.SSOFilter");
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      localClassNotFoundException.printStackTrace();
    }
  }

  public UserCredentials getAuthenticatedCredentials(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException
  {
    Params localParams = new Params(paramHttpServletRequest);
    String str = localParams.get("timezone");
    Object localObject3;
    if (str == null)
    {
      localObject1 = paramHttpServletRequest.getRequestURI();
      localObject2 = paramHttpServletRequest.getQueryString();
      localObject3 = (String)localObject1 + (localObject2 != null ? "?" + (String)localObject2 : "");
      paramHttpServletRequest.setAttribute("target_url", Validator.URLParamsEscape(Validator.StripOffScriptTag((String)localObject3)));
      RequestDispatcher localRequestDispatcher = paramHttpServletRequest.getRequestDispatcher("/shared/customTimezone.jsp");
      if (localRequestDispatcher != null)
        try
        {
          localRequestDispatcher.forward(paramHttpServletRequest, paramHttpServletResponse);
        }
        catch (ServletException localServletException)
        {
          localServletException.printStackTrace();
          throw new IOException(localServletException.getMessage());
        }
      return null;
    }
    this.timezone = str;
    Object localObject1 = null;
    Object localObject2 = extractATSSOToken(paramHttpServletRequest);
    if (localObject2 != null)
    {
      try
      {
        localObject3 = ((SSOToken)localObject2).getPrincipal();
      }
      catch (AtriumSSOException localAtriumSSOException)
      {
        throw new IOException(localAtriumSSOException.getMessage());
      }
      if (localObject3 == null)
        throw new IOException("Couldnt get user principal. Please check your configuration");
      localObject1 = new UserCredentials(((Principal)localObject3).getName(), null, ((SSOToken)localObject2).getTokenID());
      if (this.timezone != null)
        ((UserCredentials)localObject1).setTimezone(this.timezone);
      paramHttpServletRequest.setAttribute("ISSSOENABLE", new Boolean(true));
    }
    else
    {
      throw new IOException("Null token received from SSO service. Please check your configuration.");
    }
    return localObject1;
  }

  private SSOToken extractATSSOToken(HttpServletRequest paramHttpServletRequest)
    throws IOException
  {
    SSOToken localSSOToken = null;
    if (this.filterClass == null)
      throw new IOException("Could not load required SSOFilter class. Cannot extract token. Please check your configuration");
    for (Method localMethod : this.filterClass.getMethods())
      if (localMethod.getName().equals("getToken"))
        try
        {
          localSSOToken = (SSOToken)localMethod.invoke(null, new Object[] { paramHttpServletRequest });
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          throw new IOException(localIllegalArgumentException.getMessage());
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new IOException(localIllegalAccessException.getMessage());
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          throw new IOException(localInvocationTargetException.getMessage());
        }
    return localSSOToken;
  }

  public void destroy()
  {
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.sso.AtriumSSOAuthenticator
 * JD-Core Version:    0.6.1
 */