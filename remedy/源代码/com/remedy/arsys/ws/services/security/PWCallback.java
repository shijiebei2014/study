package com.remedy.arsys.ws.services.security;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.ws.security.WSPasswordCallback;

class PWCallback
  implements CallbackHandler
{
  private String passwd = "security";

  public PWCallback(String paramString)
  {
    this.passwd = paramString;
  }

  public void handle(Callback[] paramArrayOfCallback)
    throws IOException, UnsupportedCallbackException
  {
    for (int i = 0; i < paramArrayOfCallback.length; i++)
      if ((paramArrayOfCallback[i] instanceof WSPasswordCallback))
      {
        WSPasswordCallback localWSPasswordCallback = (WSPasswordCallback)paramArrayOfCallback[i];
        localWSPasswordCallback.setPassword(this.passwd);
      }
      else
      {
        throw new UnsupportedCallbackException(paramArrayOfCallback[i], "Unrecognized Callback");
      }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.security.PWCallback
 * JD-Core Version:    0.6.1
 */