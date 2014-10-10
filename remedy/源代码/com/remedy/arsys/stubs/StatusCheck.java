package com.remedy.arsys.stubs;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.Form;
import com.bmc.arsys.api.FormCriteria;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.prefetch.PrefetchTask;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.session.Params;
import com.remedy.arsys.session.UserCredentials;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StatusCheck
{
  private static final int MAX_LOGIN_LENGTH = 254;
  private static final int MAX_PW_LENGTH = 30;
  private static final String FORM_NAME = "User";
  private static final FormCriteria MINIMAL_SCHEMA_PROPS = new FormCriteria();

  public static int checkServerStatus(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, boolean paramBoolean)
    throws IOException, ServletException, GoatException
  {
    int i = 200;
    paramHttpServletResponse.setHeader("Cache-Control", "no-cache");
    paramHttpServletResponse.setHeader("Pragma", "no-cache");
    paramHttpServletResponse.setDateHeader("Last-Modified", new Date().getTime());
    if ((paramBoolean) && (!PrefetchTask.prefetchTaskDone()))
      return 500;
    Params localParams = new Params(paramHttpServletRequest);
    HttpSession localHttpSession = paramHttpServletRequest.getSession();
    if (localHttpSession != null)
    {
      localObject1 = (Map)localHttpSession.getAttribute("arsystem.vfservlet.orgreqmap");
      localParams.setMap((Map)localObject1);
    }
    Object localObject1 = localParams.get("username");
    String str = localParams.get("pwd");
    if (str == null)
      str = "";
    List localList = Configuration.getInstance().getServers();
    if (localList.size() == 0)
    {
      i = 501;
    }
    else
    {
      Object localObject2;
      if ((localObject1 == null) || (((String)localObject1).length() == 0) || (((String)localObject1).length() > 254) || (str.length() > 30))
      {
        ServerLogin localServerLogin = null;
        for (int j = 0; j < localList.size(); j++)
        {
          localObject2 = (String)localList.get(j);
          try
          {
            localServerLogin = ServerLogin.getAdmin((String)localObject2);
            Form localForm = localServerLogin.getForm("User", MINIMAL_SCHEMA_PROPS);
          }
          catch (ARException localARException)
          {
            i = 501;
            break;
          }
          catch (GoatException localGoatException)
          {
            i = 500;
            break;
          }
        }
      }
      else
      {
        localObject2 = Login.establishSession(paramHttpServletRequest, paramHttpServletResponse, new UserCredentials((String)localObject1, str, null), true);
        if (localObject2 == null)
          i = 501;
      }
    }
    return i;
  }

  static
  {
    MINIMAL_SCHEMA_PROPS.setPropertiesToRetrieve(FormCriteria.LAST_CHANGED);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.StatusCheck
 * JD-Core Version:    0.6.1
 */