package com.remedy.arsys.goat.sharedresource.template;

import com.remedy.arsys.goat.sharedresource.IResourceFactory;
import com.remedy.arsys.goat.sharedresource.IResourceObject;
import com.remedy.arsys.log.Log;
import java.io.IOException;

public class TemplateResourceFactory
  implements IResourceFactory
{
  private static TemplateResourceFactory instance = new TemplateResourceFactory();
  private static transient Log MLog = Log.get(7);

  public static IResourceFactory getInstance()
  {
    return instance;
  }

  public IResourceObject createFromData(String paramString1, String paramString2, int paramInt, String paramString3, byte[] paramArrayOfByte, String paramString4, String paramString5, String paramString6)
  {
    String str = "";
    TemplateObject localTemplateObject = null;
    if (paramArrayOfByte != null)
      try
      {
        str = new String(paramArrayOfByte, "UTF-8");
      }
      catch (IOException localIOException)
      {
        MLog.warning(localIOException.getMessage());
      }
    if (str == null)
      str = "";
    localTemplateObject = TemplateEngine.processFromString(paramString1, paramString2, str, paramString6);
    return localTemplateObject;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.sharedresource.template.TemplateResourceFactory
 * JD-Core Version:    0.6.1
 */