package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;

public class DisplayField extends ViewField
{
  private static final long serialVersionUID = -7214347493348621619L;
  private static final String CURRENT_SERVER = "@";
  private static final String SERVER_PARAM = "server=";
  private static final String HEIGHT_PARAM = "height=";
  private static final String WIDTH_PARAM = "width=";
  private static final String PLUGIN_NAME_PARAM = "plugin=";
  private static final String PLUGIN_KEY_PARAM = "name=";
  private static final int DEFAULT_HEIGHT = 200;
  private static final int DEFAULT_WIDTH = 200;
  private String mARServer;
  private String mFBNameParam;
  private String mPlugin;
  private String mPluginKey;

  public DisplayField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    if (getMDefaultValue() == null)
      setMDefaultValue("");
    if (getMARServer() == null)
      setMARServer("server=@");
    if (getMDefaultValue().length() > 0)
      setMDefaultValue(getMDefaultValue() + ',');
    setMDefaultValue(getMDefaultValue() + getMARServer());
    if (getMFBNameParam() != null)
    {
      if (getMDefaultValue().length() > 0)
        setMDefaultValue(getMDefaultValue() + ',');
      setMDefaultValue(getMDefaultValue() + getMFBNameParam());
    }
    if (getMPlugin() == null)
      setMPlugin("Flashboard");
    if (getMDefaultValue().length() > 0)
      setMDefaultValue(getMDefaultValue() + ',');
    setMDefaultValue(getMDefaultValue() + "plugin=" + getMPlugin());
    if (getMPluginKey() == null)
      setMPluginKey("");
    else if (getMFBNameParam() == null)
      setMDefaultValue(getMDefaultValue() + ",flashboard=" + getMPluginKey());
    setMDefaultValue(getMDefaultValue() + ",name=" + getMPluginKey());
    setMDefaultValue(getMDefaultValue() + ',');
    if (getMARBox() != null)
    {
      Box localBox = getMARBox().toBox();
      setMDefaultValue(getMDefaultValue() + "height=" + localBox.mH + ',' + "width=" + localBox.mW);
    }
    else
    {
      setMDefaultValue(getMDefaultValue() + "height=200,width=200");
    }
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    String str;
    switch (paramInt)
    {
    case 258:
      str = propToString(paramValue);
      setMARServer("server=" + str);
      break;
    case 241:
      str = propToString(paramValue);
      setMFBNameParam(str);
      setMPluginKey("");
      if (str != null)
      {
        int i = str.indexOf("=");
        if (i != -1)
          setMPluginKey(str.substring(i + 2, str.length() - 1));
      }
      break;
    case 269:
      str = propToString(paramValue);
      setMPlugin(str);
      break;
    default:
      super.handleProperty(paramInt, paramValue);
    }
  }

  private void setMARServer(String paramString)
  {
    this.mARServer = paramString;
  }

  private String getMARServer()
  {
    return this.mARServer;
  }

  private void setMFBNameParam(String paramString)
  {
    this.mFBNameParam = paramString;
  }

  private String getMFBNameParam()
  {
    return this.mFBNameParam;
  }

  private void setMPlugin(String paramString)
  {
    this.mPlugin = paramString;
  }

  private String getMPlugin()
  {
    return this.mPlugin;
  }

  private void setMPluginKey(String paramString)
  {
    this.mPluginKey = paramString;
  }

  private String getMPluginKey()
  {
    return this.mPluginKey;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.DisplayField
 * JD-Core Version:    0.6.1
 */