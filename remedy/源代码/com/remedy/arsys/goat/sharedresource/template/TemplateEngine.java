package com.remedy.arsys.goat.sharedresource.template;

import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.log.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEngine
{
  public static final Pattern PARAM_PATTERN = Pattern.compile("(\\$\\{([^\\}]+)\\})");
  public static final Pattern FIELDID_PATTERN = Pattern.compile("(\\$([0-9]+)\\$)");
  public static final Pattern SHAREDRES_PATTERN = Pattern.compile("(sharedresources/[^,\\\"']+)");
  protected static final transient Log MLog = Log.get(7);

  private static String getContents(String paramString)
  {
    File localFile = new File(paramString);
    StringBuilder localStringBuilder = new StringBuilder();
    if ((localFile.exists()) && (localFile.isFile()))
    {
      FileReader localFileReader = null;
      try
      {
        localFileReader = new FileReader(localFile);
        BufferedReader localBufferedReader = new BufferedReader(localFileReader);
        String str;
        while ((str = localBufferedReader.readLine()) != null)
          localStringBuilder.append(str);
      }
      catch (IOException localIOException1)
      {
        MLog.severe("TemplateEngine: - Could not get html information. Error - " + localIOException1.getMessage());
      }
      finally
      {
        try
        {
          if (localFileReader != null)
            localFileReader.close();
        }
        catch (IOException localIOException2)
        {
        }
      }
    }
    return localStringBuilder.toString();
  }

  public static TemplateObject processFromFile(String paramString1, String paramString2, String paramString3)
  {
    String str = getContents(paramString3);
    return processFromString(paramString1, paramString2, str, null);
  }

  public static TemplateObject processFromString(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    Matcher localMatcher = SHAREDRES_PATTERN.matcher(paramString3);
    StringBuffer localStringBuffer = new StringBuffer("");
    String str1 = paramString4;
    if ((str1 == null) || (str1.length() == 0))
      str1 = FormContext.get().getRelativeContextURL();
    String str2 = "server=" + paramString1;
    while (localMatcher.find())
    {
      localObject1 = localMatcher.group(1);
      localObject2 = str1 + (String)localObject1;
      if (localObject1 != null)
        localObject2 = (String)localObject2 + (((String)localObject1).indexOf('?') != -1 ? "&" : "?");
      else
        localObject2 = (String)localObject2 + "?";
      localObject2 = (String)localObject2 + str2;
      localObject2 = ((String)localObject2).replace("$", "\\$").replace("{", "\\{").replace("}", "\\}");
      localMatcher.appendReplacement(localStringBuffer, (String)localObject2);
    }
    localMatcher.appendTail(localStringBuffer);
    paramString3 = localStringBuffer.toString();
    Object localObject1 = new TemplateObject(paramString2, paramString3);
    Object localObject2 = PARAM_PATTERN.matcher(paramString3);
    while (((Matcher)localObject2).find())
    {
      localObject3 = ((Matcher)localObject2).group(2);
      ((TemplateObject)localObject1).addParameter((String)localObject3);
    }
    Object localObject3 = FIELDID_PATTERN.matcher(paramString3);
    while (((Matcher)localObject3).find())
    {
      String str3 = ((Matcher)localObject3).group(2);
      ((TemplateObject)localObject1).addFieldId(str3);
    }
    return localObject1;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.sharedresource.template.TemplateEngine
 * JD-Core Version:    0.6.1
 */