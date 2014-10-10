package com.remedy.arsys.goat;

import com.bmc.arsys.api.ByteListValue;
import com.bmc.arsys.api.Container;
import com.bmc.arsys.api.ContainerType;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.ExternalReference;
import com.bmc.arsys.api.Reference;
import com.bmc.arsys.api.ReferenceType;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.support.BrowserType;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GoatApplicationContainer extends GoatContainer
{
  private static final long serialVersionUID = 5725018048926749649L;
  private Map mAppResources = new HashMap();
  private String mPrimaryForm;
  private String mPrimaryView;
  private Set mSchemas = new HashSet();
  private Map mSchemaViews = new HashMap();
  private Map mMergedAppResources = new HashMap();

  protected GoatApplicationContainer(Container paramContainer, String paramString)
  {
    super(paramContainer, paramString);
    Reference[] arrayOfReference = (Reference[])this.mContainer.getReferences().toArray(new Reference[0]);
    if (arrayOfReference != null)
    {
      int i = 0;
      int j = 0;
      int k = 0;
      String str1 = null;
      for (int m = 0; m < arrayOfReference.length; m++)
      {
        assert (arrayOfReference[m] != null);
        if (arrayOfReference[m].getReferenceType() != null)
        {
          assert (arrayOfReference[m].getReferenceType() != null);
          if (arrayOfReference[m].getReferenceType().toInt() == 32771)
          {
            i = 1;
          }
          else
          {
            Object localObject;
            if (((i != 0) || (k != 0)) && (!(arrayOfReference[m] instanceof ExternalReference)) && (arrayOfReference[m].getReferenceType().toInt() == 2))
            {
              localObject = arrayOfReference[m];
              if (k != 0)
                this.mPrimaryForm = ((Reference)localObject).getName().toString();
              str1 = ((Reference)localObject).getName().toString();
              this.mSchemas.add(str1);
              j = 1;
              i = 0;
            }
            else if ((j != 0) && ((arrayOfReference[m] instanceof ExternalReference)) && (arrayOfReference[m].getReferenceType().toInt() == 32778))
            {
              localObject = (ExternalReference)arrayOfReference[m];
              if (DataType.CHAR.equals(((ExternalReference)localObject).getValue().getDataType()))
              {
                if (k != 0)
                {
                  this.mPrimaryView = ((ExternalReference)localObject).getValue().toString();
                  k = 0;
                }
                if (str1 != null)
                  this.mSchemaViews.put(str1, ((ExternalReference)localObject).getValue().toString());
              }
              str1 = null;
              j = 0;
            }
            else if (((arrayOfReference[m] instanceof ExternalReference)) && (arrayOfReference[m].getReferenceType().toInt() == 32789))
            {
              localObject = (ExternalReference)arrayOfReference[m];
              String str2 = new String(((ExternalReference)localObject).getDescription());
              str2 = str2.replaceAll("/\\\\|\\\\", "/");
              Value localValue = ((ExternalReference)localObject).getValue();
              if (localValue.getDataType().equals(DataType.BYTES))
              {
                ByteListValue localByteListValue = (ByteListValue)localValue.getValue();
                try
                {
                  Globule localGlobule = new Globule((byte[])localByteListValue.getValue(), Configuration.getInstance().getMimeType(str2), 0L);
                  this.mAppResources.put(str2, localGlobule);
                }
                catch (GoatException localGoatException)
                {
                }
              }
            }
            else if (arrayOfReference[m].getReferenceType().toInt() == 32777)
            {
              k = 1;
            }
          }
        }
      }
      this.mContainer.setReferences(null);
    }
  }

  public static GoatApplicationContainer get(ServerLogin paramServerLogin, String paramString)
    throws GoatException
  {
    GoatContainer localGoatContainer = GoatContainer.getContainer(paramServerLogin, paramString);
    if (!(localGoatContainer instanceof GoatApplicationContainer))
      throw new GoatException(9358, paramString);
    return (GoatApplicationContainer)localGoatContainer;
  }

  public static String[] getContainerList(ServerLogin paramServerLogin)
    throws GoatException
  {
    return GoatContainer.ContainerList.get(paramServerLogin, ContainerType.APPLICATION);
  }

  private final Globule getMerged(String paramString1, String paramString2)
  {
    Globule localGlobule1 = (Globule)this.mMergedAppResources.get(paramString1);
    if (localGlobule1 == null)
    {
      Globule localGlobule2 = (Globule)this.mAppResources.get(paramString1);
      Globule localGlobule3 = (Globule)this.mAppResources.get(paramString2);
      if ((localGlobule2 == null) && (localGlobule3 == null))
        return null;
      if (localGlobule2 == null)
      {
        this.mMergedAppResources.put(paramString1, localGlobule3);
        return localGlobule3;
      }
      if (localGlobule3 == null)
      {
        this.mMergedAppResources.put(paramString1, localGlobule2);
        return localGlobule2;
      }
      try
      {
        byte[] arrayOfByte1 = localGlobule3.decompressedData();
        byte[] arrayOfByte2 = localGlobule2.decompressedData();
        byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
        System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
        localGlobule1 = new Globule(arrayOfByte3, localGlobule2.contentType(), 0L);
        this.mMergedAppResources.put(paramString1, localGlobule1);
      }
      catch (IOException localIOException)
      {
      }
      catch (GoatException localGoatException)
      {
      }
    }
    return localGlobule1;
  }

  public final Globule getResourceFile(BrowserType paramBrowserType, String paramString)
  {
    Globule localGlobule = null;
    String str1 = ("standard/" + paramString).intern();
    if (paramBrowserType != null)
    {
      String str2 = (paramBrowserType.getAbbrev() + "/" + paramString).intern();
      if (paramString.startsWith("stylesheets"))
        localGlobule = getMerged(str2, str1);
      else
        localGlobule = (Globule)this.mAppResources.get(str2);
    }
    if (localGlobule == null)
      localGlobule = (Globule)this.mAppResources.get(str1);
    if (localGlobule == null)
      localGlobule = (Globule)this.mAppResources.get(paramString);
    return localGlobule;
  }

  public final String getPrimaryForm()
  {
    return this.mPrimaryForm;
  }

  public final String getPrimaryView()
  {
    return this.mPrimaryView;
  }

  public final Set getFormSet()
  {
    return this.mSchemas;
  }

  public final Map getViewSet()
  {
    return this.mSchemaViews;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.GoatApplicationContainer
 * JD-Core Version:    0.6.1
 */