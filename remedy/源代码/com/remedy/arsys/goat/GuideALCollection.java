package com.remedy.arsys.goat;

import com.remedy.arsys.share.JSWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.LinkedList;

public class GuideALCollection extends ActiveLinkCollection
{
  private static final long serialVersionUID = -3040023221108258500L;

  public GuideALCollection(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    super(paramString1);
    this.mServer = paramString2;
    this.mForm = paramString3;
    this.mView = paramString4;
  }

  protected void constructCollection()
  {
    this.mActiveLinks = new LinkedList();
  }

  public String getJSFunctionName()
  {
    return JSWriter.makeJSFunctionName("AREVTGuide", this.mName, "");
  }

  protected String getRunFunction(boolean paramBoolean)
  {
    return "ARRunGuideX";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    super.emitJS(paramEmitter);
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
 * Qualified Name:     com.remedy.arsys.goat.GuideALCollection
 * JD-Core Version:    0.6.1
 */