package com.remedy.arsys.goat;

import com.remedy.arsys.share.JSWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class EventALCollection extends ActiveLinkCollection
{
  private static final long serialVersionUID = 2701055426371107075L;
  private static final ALComparator mALComparator = new ALComparator(null);

  public EventALCollection(String paramString1, int paramInt1, int paramInt2, String paramString2, String paramString3, String paramString4)
  {
    super(paramString1);
    this.mServer = paramString2;
    this.mForm = paramString3;
    this.mView = paramString4;
    this.mAREvtID = paramInt1;
    this.mEvtID = paramInt2;
  }

  protected void constructCollection()
  {
    this.mActiveLinks = new TreeSet(mALComparator);
  }

  public String getJSFunctionName()
  {
    return JSWriter.makeJSFunctionName("AREVT", this.mName, "");
  }

  protected String getRunFunction(boolean paramBoolean)
  {
    if (paramBoolean)
      return "ARRunGotoX";
    return "ARRunX";
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    if (this.mActiveLinks.size() > 0)
      super.emitJS(paramEmitter);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    if (i > 0)
    {
      this.mActiveLinks = new TreeSet(mALComparator);
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

  private static final class ALComparator
    implements Comparator
  {
    public int compare(Object paramObject1, Object paramObject2)
    {
      ActiveLink localActiveLink1 = (ActiveLink)paramObject1;
      ActiveLink localActiveLink2 = (ActiveLink)paramObject2;
      long l1 = localActiveLink1.getExecutionOrder();
      long l2 = localActiveLink2.getExecutionOrder();
      if (l1 != l2)
        return (int)(l1 - l2);
      return localActiveLink1.getName().compareTo(localActiveLink2.getName());
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.EventALCollection
 * JD-Core Version:    0.6.1
 */