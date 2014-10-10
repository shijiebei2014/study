package com.remedy.arsys.goat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.LinkedList;

public class EPGuideALCollection extends GuideALCollection
{
  private static final long serialVersionUID = -2921941338649728741L;
  private static final String EPSUFF = "EntryPoint";

  public EPGuideALCollection(GuideALCollection paramGuideALCollection, String paramString1, String paramString2, String paramString3)
  {
    super(getEPGuideNameFromOriginal(paramGuideALCollection.mName), paramString1, paramString2, paramString3);
    this.mActiveLinks.addAll(paramGuideALCollection.mActiveLinks);
  }

  public static String getEPGuideNameFromOriginal(String paramString)
  {
    return paramString + "EntryPoint";
  }

  public static String getOriginalGuideNameFromEPGuideName(String paramString)
  {
    if (paramString.endsWith("EntryPoint"))
      return paramString.substring(0, paramString.length() - "EntryPoint".length());
    return null;
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    if (i > 0)
    {
      this.mActiveLinks = new LinkedList();
      for (int j = 0; j < i; j++)
      {
        String str1 = (String)paramObjectInputStream.readObject();
        String str2 = (String)paramObjectInputStream.readObject();
        try
        {
          ActiveLink localActiveLink = ActiveLink.get(str2, str1, "NOT_VALID_FORM", "NOT_VALID_VIEW");
          this.mActiveLinks.add(localActiveLink);
        }
        catch (GoatException localGoatException)
        {
        }
      }
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.EPGuideALCollection
 * JD-Core Version:    0.6.1
 */