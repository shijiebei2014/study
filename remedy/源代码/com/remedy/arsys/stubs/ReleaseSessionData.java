package com.remedy.arsys.stubs;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

public class ReleaseSessionData
  implements HttpSessionAttributeListener
{
  public void attributeAdded(HttpSessionBindingEvent paramHttpSessionBindingEvent)
  {
  }

  public void attributeRemoved(HttpSessionBindingEvent paramHttpSessionBindingEvent)
  {
    String str = paramHttpSessionBindingEvent.getName();
    if (str.equals("com.remedy.arsys.stubs.sessionData"))
    {
      SessionData localSessionData = (SessionData)paramHttpSessionBindingEvent.getValue();
      if (localSessionData != null)
        localSessionData.dispose(true);
    }
  }

  public void attributeReplaced(HttpSessionBindingEvent paramHttpSessionBindingEvent)
  {
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.ReleaseSessionData
 * JD-Core Version:    0.6.1
 */