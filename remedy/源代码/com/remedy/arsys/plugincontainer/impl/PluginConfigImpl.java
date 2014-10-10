package com.remedy.arsys.plugincontainer.impl;

import com.remedy.arsys.plugincontainer.PluginConfig;
import java.util.Properties;
import javax.servlet.ServletContext;

public class PluginConfigImpl
  implements PluginConfig
{
  private String mServletName;
  private String mPluginName;
  private ServletContext mServletContext;
  private String mVersion;
  private Properties mProps;

  PluginConfigImpl(String paramString1, ServletContext paramServletContext, String paramString2, String paramString3, Properties paramProperties)
  {
    this.mServletName = paramString1;
    this.mPluginName = paramString2;
    this.mServletContext = paramServletContext;
    this.mVersion = paramString3;
    this.mProps = paramProperties;
  }

  public Properties getConfigurationProperties()
  {
    return this.mProps;
  }

  public String getPluginName()
  {
    return this.mPluginName;
  }

  public ServletContext getServletContext()
  {
    return this.mServletContext;
  }

  public String getServletName()
  {
    return this.mServletName;
  }

  public String getVersion()
  {
    return this.mVersion;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.PluginConfigImpl
 * JD-Core Version:    0.6.1
 */