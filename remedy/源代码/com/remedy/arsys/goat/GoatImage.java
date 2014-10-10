package com.remedy.arsys.goat;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Image;
import com.bmc.arsys.api.ImageData;
import com.remedy.arsys.config.ConfigProperties;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.Cache.Item;
import com.remedy.arsys.share.Cachetable;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.imageio.ImageIO;

public final class GoatImage
  implements Serializable
{
  private static final long serialVersionUID = 5430293912891169317L;
  public static final int GOATIMAGE_EXPIRY_INTERVAL = 31536000;
  private final String mContentType;
  private int mW;
  private int mH;
  private final String mDigest;
  private final boolean mOverridden;
  private final Fetcher mFetcher;
  private static Cache MDataCache;
  private static Map MGoatImageMap;
  private static ConfigProperties MOverrides;
  private static transient Log mLog;

  private static final int UB(byte paramByte)
  {
    return paramByte & 0xFF;
  }

  private GoatImage(String paramString1, byte[] paramArrayOfByte, String paramString2, boolean paramBoolean, Fetcher paramFetcher)
    throws GoatImage.GoatImageException
  {
    assert ((paramArrayOfByte != null) && (paramArrayOfByte.length > 0) && (paramFetcher != null) && (paramString2 != null));
    this.mContentType = paramString2;
    this.mFetcher = paramFetcher;
    this.mDigest = paramString1;
    this.mOverridden = paramBoolean;
    try
    {
      BufferedImage localBufferedImage = ImageIO.read(new ByteArrayInputStream(paramArrayOfByte));
      if (localBufferedImage != null)
      {
        this.mW = localBufferedImage.getWidth();
        this.mH = localBufferedImage.getHeight();
      }
      else if ((paramArrayOfByte[0] == 66) && (paramArrayOfByte[1] == 77) && (paramArrayOfByte.length >= 55))
      {
        int i = 18;
        this.mW = (UB(paramArrayOfByte[i]) | UB(paramArrayOfByte[(i + 1)]) << 8 | UB(paramArrayOfByte[(i + 2)]) << 16 | UB(paramArrayOfByte[(i + 3)]) << 24);
        int j = i + 4;
        this.mH = (UB(paramArrayOfByte[j]) | UB(paramArrayOfByte[(j + 1)]) << 8 | UB(paramArrayOfByte[(j + 2)]) << 16 | UB(paramArrayOfByte[(j + 3)]) << 24);
        assert ((this.mW >= 0) && (this.mH >= 0));
      }
      else
      {
        throw new GoatImageException("Missing image decoder/corrupt image data");
      }
    }
    catch (IOException localIOException)
    {
      throw new GoatImageException("Broken image or Java image code", localIOException);
    }
    assert (MDataCache.get(paramString1, DataWad.class) == null);
    try
    {
      MDataCache.put(paramString1, new DataWad(paramArrayOfByte, this.mContentType, new Date().getTime()));
    }
    catch (GoatException localGoatException)
    {
      throw new GoatImageException("Cannot create Globule for image", localGoatException);
    }
    assert (!MGoatImageMap.containsKey(paramString1));
    MGoatImageMap.put(paramString1, this);
  }

  private synchronized DataWad getData()
  {
    DataWad localDataWad = (DataWad)MDataCache.get(this.mDigest, DataWad.class);
    if (localDataWad == null)
    {
      byte[] arrayOfByte = this.mFetcher.reFetchImageData();
      if (arrayOfByte == null)
        return null;
      try
      {
        localDataWad = new DataWad(arrayOfByte, this.mContentType, new Date().getTime());
      }
      catch (GoatException localGoatException)
      {
        return null;
      }
      MDataCache.put(this.mDigest, localDataWad);
    }
    return localDataWad;
  }

  public void emitAsBackgroundImage(HTMLWriter paramHTMLWriter, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    emitAsBackgroundImage(paramHTMLWriter, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, true, null);
  }

  public void emitAsBackgroundImageWithoutCloseTag(HTMLWriter paramHTMLWriter, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString)
  {
    emitAsBackgroundImage(paramHTMLWriter, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, false, paramString);
  }

  private void emitAsBackgroundImage(HTMLWriter paramHTMLWriter, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean, String paramString)
  {
    if (!FormContext.get().IsLowVisionUser())
    {
      if (paramString == null)
        paramHTMLWriter.openTag("div").attr("class", "FormContainerBackground");
      else
        paramHTMLWriter.openTag("div").attr("class", "FormContainerBackground " + paramString);
      if (paramInt5 != -1)
        paramHTMLWriter.attr("arbkimgx", paramInt5);
      if (paramInt6 != -1)
        paramHTMLWriter.attr("arbkimgy", paramInt6);
      if ((paramInt1 == 3) || (paramInt2 == 3))
      {
        if (paramInt1 != 3)
          paramHTMLWriter.attr("a", paramInt1);
        if (paramInt2 != 3)
          paramHTMLWriter.attr("j", paramInt2);
        int i = 0;
        int j = 0;
        int k = this.mH;
        int m = this.mW;
        int n = 1;
        int i1 = 1;
        if (paramInt1 == 2)
          j = paramInt3 / 2 - this.mH / 2;
        else if (paramInt1 == 4)
          j = paramInt3 - this.mH;
        else if ((paramInt1 == 5) && (this.mH < paramInt3))
          i1 = paramInt3 / this.mH + 1;
        else if (paramInt1 == 3)
          k = paramInt3;
        if (paramInt2 == 2)
          i = paramInt4 / 2 - this.mW / 2;
        else if (paramInt2 == 4)
          i = paramInt4 - this.mW;
        else if ((paramInt2 == 5) && (this.mW < paramInt4))
          n = paramInt4 / this.mW + 1;
        else if (paramInt2 == 3)
          m = paramInt4;
        paramHTMLWriter.endTag(true);
        StringBuilder localStringBuilder2 = null;
        for (int i2 = 0; i2 < i1; i2++)
        {
          int i3 = j + i2 * k;
          for (int i4 = 0; i4 < n; i4++)
          {
            int i5 = i + i4 * m;
            paramHTMLWriter.openTag("img");
            paramHTMLWriter.attr("class", "FormContainerImage");
            paramHTMLWriter.attr("galleryimg", "no");
            localStringBuilder2 = new StringBuilder();
            if (paramInt1 == 4)
              localStringBuilder2.append("bottom:0px");
            else
              localStringBuilder2.append("top:").append(i3).append("px");
            if (paramInt2 == 4)
              localStringBuilder2.append("; right:0px");
            else
              localStringBuilder2.append("; left:").append(i5).append("px");
            if (paramInt2 == 3)
              localStringBuilder2.append("; width:100%");
            else
              localStringBuilder2.append("; width:").append(m).append("px");
            if (paramInt1 == 3)
              localStringBuilder2.append("; height:100%");
            else
              localStringBuilder2.append("; height:").append(k).append("px");
            localStringBuilder2.append(";");
            paramHTMLWriter.attr("style", localStringBuilder2.toString());
            paramHTMLWriter.attr("src", getUrl());
            paramHTMLWriter.attr("alt", "");
            paramHTMLWriter.closeOpenTag(false);
          }
        }
      }
      else
      {
        StringBuilder localStringBuilder1 = getBckgrdImageStr(paramInt1, paramInt2, false);
        paramHTMLWriter.attr("style", localStringBuilder1.toString());
        paramHTMLWriter.endTag();
      }
      if (paramBoolean)
        paramHTMLWriter.closeTag("div", true);
    }
  }

  public StringBuilder getBckgrdImageStr(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (!paramBoolean)
      localStringBuilder.append("width:100%;height:100%;");
    localStringBuilder.append("background-image:url(").append(getUrl()).append(");");
    localStringBuilder.append("background-position:");
    if (paramInt1 == 2)
      localStringBuilder.append("center");
    else if (paramInt1 == 4)
      localStringBuilder.append("bottom");
    else
      localStringBuilder.append("top");
    if (paramInt2 == 2)
      localStringBuilder.append(" center;");
    else if (paramInt2 == 4)
      localStringBuilder.append(" right;");
    else
      localStringBuilder.append(" left;");
    if (paramInt2 != 5)
    {
      if (paramInt1 != 5)
        localStringBuilder.append("background-repeat:no-repeat;");
      else if (paramInt1 == 5)
        localStringBuilder.append("background-repeat:repeat-y;");
    }
    else if (paramInt1 != 5)
      localStringBuilder.append("background-repeat:repeat-x;");
    return localStringBuilder;
  }

  public static Globule getImageGlobule(String paramString)
  {
    GoatImage localGoatImage = (GoatImage)MGoatImageMap.get(paramString);
    if (localGoatImage == null)
      return null;
    return localGoatImage.getData();
  }

  public static synchronized GoatImage put(byte[] paramArrayOfByte, String paramString, Fetcher paramFetcher)
    throws GoatImage.GoatImageException
  {
    assert ((paramArrayOfByte != null) && (paramArrayOfByte.length > 0));
    byte[] arrayOfByte1;
    try
    {
      arrayOfByte1 = MessageDigest.getInstance("MD5").digest(paramArrayOfByte);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      if (!$assertionsDisabled)
        throw new AssertionError();
      throw new GoatImageException("Unsupported MD-5 in Java, should never happen", localNoSuchAlgorithmException);
    }
    StringBuilder localStringBuilder = new StringBuilder(arrayOfByte1.length * 2);
    for (int i = 0; i < arrayOfByte1.length; i++)
      localStringBuilder.append(Integer.toHexString(arrayOfByte1[i]));
    String str1 = localStringBuilder.toString();
    if (MGoatImageMap.containsKey(str1))
    {
      localObject = (DataWad)MDataCache.get(str1, DataWad.class);
      if (localObject != null)
        return (GoatImage)MGoatImageMap.get(str1);
      MGoatImageMap.remove(str1);
    }
    Object localObject = MOverrides.get(str1);
    if (localObject != null)
    {
      byte[] arrayOfByte2 = loadOverride((String)localObject);
      if (arrayOfByte2 != null)
      {
        String str2 = URLConnection.getFileNameMap().getContentTypeFor((String)localObject);
        return new GoatImage(str1, arrayOfByte2, str2, true, new Fetcher()
        {
          public byte[] reFetchImageData()
          {
            return GoatImage.loadOverride(this.val$filename);
          }
        });
      }
    }
    return new GoatImage(str1, paramArrayOfByte, paramString, false, paramFetcher);
  }

  public static synchronized GoatImage getImageReference(Fetcher paramFetcher, String paramString1, String paramString2)
    throws GoatImage.GoatImageException
  {
    String str = paramString1 + "_" + paramString2;
    if (MGoatImageMap.containsKey(str))
    {
      DataWad localDataWad = (DataWad)MDataCache.get(str, DataWad.class);
      if (localDataWad != null)
        return (GoatImage)MGoatImageMap.get(str);
      MGoatImageMap.remove(str);
      return retrieveImageFromServer(paramString1, paramString2, paramFetcher);
    }
    return retrieveImageFromServer(paramString1, paramString2, paramFetcher);
  }

  public static synchronized byte[] getImageReferenceData(String paramString1, String paramString2)
  {
    String str = paramString1 + "_" + paramString2;
    DataWad localDataWad = (DataWad)MDataCache.get(str, DataWad.class);
    byte[] arrayOfByte = null;
    if (localDataWad != null)
      arrayOfByte = localDataWad.data();
    else
      arrayOfByte = retrieveImageReferenceData(paramString1, paramString2);
    return arrayOfByte;
  }

  public static synchronized byte[] retrieveImageReferenceData(String paramString1, String paramString2)
  {
    byte[] arrayOfByte = null;
    try
    {
      ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString2);
      Image localImage = retrieveImageReferenceFromServerInternal(paramString1, paramString2);
      if (localImage != null)
        arrayOfByte = localImage.getImageData().getValue();
    }
    catch (GoatException localGoatException)
    {
      mLog.fine("retrieveImageReferenceData could not retrieve image " + paramString1 + " " + localGoatException.getMessage());
    }
    return arrayOfByte;
  }

  private static synchronized Image retrieveImageReferenceFromServerInternal(String paramString1, String paramString2)
  {
    Image localImage = null;
    try
    {
      ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString2);
      localImage = localServerLogin.getImage(paramString1);
    }
    catch (ARException localARException)
    {
      mLog.fine("retrieveImageReferenceFromServer could not retrieve image " + paramString1 + " " + localARException.getMessage());
    }
    catch (GoatException localGoatException)
    {
      mLog.fine("retrieveImageReferenceFromServer could not retrieve image " + paramString1 + " " + localGoatException.getMessage());
    }
    return localImage;
  }

  private static GoatImage retrieveImageFromServer(String paramString1, String paramString2, Fetcher paramFetcher)
    throws GoatImage.GoatImageException
  {
    String str1 = paramString1 + "_" + paramString2;
    Image localImage = retrieveImageReferenceFromServerInternal(paramString1, paramString2);
    byte[] arrayOfByte = null;
    if (localImage != null)
      arrayOfByte = localImage.getImageData().getValue();
    if (arrayOfByte == null)
      throw new GoatImageException("Image reference data could not be retrieved " + paramString1);
    String str2 = getImageType(localImage.getType());
    return new GoatImage(str1, arrayOfByte, str2, false, paramFetcher);
  }

  private static String getImageType(String paramString)
  {
    String str = "image/gif";
    if (paramString.equals("bmp"))
      str = "image/bmp";
    else if (paramString.equals("tiff"))
      str = "image/tiff";
    else if (paramString.equals("targa"))
      str = "image/targa";
    else if (paramString.equals("gif"))
      str = "image/gif";
    else if (paramString.equals("png"))
      str = "image/png";
    else if (paramString.equals("jpg"))
      str = "image/jpg";
    else if (paramString.equals("jpeg"))
      str = "image/jpeg";
    return str;
  }

  public static synchronized void writeToDisc()
  {
    FormContext localFormContext = FormContext.get();
    Set localSet = MGoatImageMap.keySet();
    if (localSet == null)
      return;
    new File(localFormContext.getImagepoolPath()).mkdir();
    String[] arrayOfString = (String[])localSet.toArray(JSWriter.EmptyString);
    for (int i = 0; i < arrayOfString.length; i++)
    {
      FileOutputStream localFileOutputStream = null;
      try
      {
        localFileOutputStream = new FileOutputStream(localFormContext.getImagepoolPath() + arrayOfString[i]);
        Globule localGlobule = getImageGlobule(arrayOfString[i]);
        if (localGlobule != null)
          localGlobule.write(localFileOutputStream);
      }
      catch (IOException localIOException1)
      {
      }
      finally
      {
        try
        {
          if (localFileOutputStream != null)
            localFileOutputStream.close();
        }
        catch (IOException localIOException2)
        {
        }
      }
    }
  }

  public final String getContentType()
  {
    return this.mContentType;
  }

  public final String getDigest()
  {
    return this.mDigest;
  }

  public final String getUrl()
  {
    FormContext localFormContext = FormContext.get();
    String str = "";
    try
    {
      str = URLEncoder.encode(this.mDigest, "UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }
    return localFormContext.getImagepoolURL() + str;
  }

  public final int getW()
  {
    return this.mW;
  }

  public final int getH()
  {
    return this.mH;
  }

  public final boolean isOverridden()
  {
    return this.mOverridden;
  }

  private static byte[] loadOverride(String paramString)
  {
    FileInputStream localFileInputStream = null;
    try
    {
      File localFile = new File(paramString);
      if (!localFile.isAbsolute())
      {
        localObject1 = FormContext.get();
        localFile = new File(((FormContext)localObject1).getContextPath() + paramString);
      }
      Object localObject1 = new byte[(int)localFile.length()];
      localFileInputStream = new FileInputStream(localFile);
      localFileInputStream.read((byte[])localObject1);
      Object localObject2 = localObject1;
      return localObject2;
    }
    catch (IOException localIOException1)
    {
    }
    finally
    {
      if (localFileInputStream != null)
        try
        {
          localFileInputStream.close();
        }
        catch (IOException localIOException2)
        {
        }
    }
    return null;
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    MOverrides = new ConfigProperties(Configuration.getInstance().getFilename("imagereplacement.properties"), "Image overrides");
    try
    {
      MOverrides.load(true);
    }
    catch (IOException localIOException)
    {
    }
    MGoatImageMap.put(this.mDigest, this);
  }

  static
  {
    MDataCache = new Cachetable("Form images", 4, 1);
    MGoatImageMap = new HashMap();
    MOverrides = null;
    mLog = Log.get(11);
    MOverrides = new ConfigProperties(Configuration.getInstance().getFilename("imagereplacement.properties"), "Image overrides");
    try
    {
      MOverrides.load(true);
    }
    catch (IOException localIOException)
    {
    }
  }

  private static class DataWad extends Globule
    implements Cache.Item
  {
    private static final long serialVersionUID = -8016590943471198142L;

    DataWad(byte[] paramArrayOfByte, String paramString, long paramLong)
      throws GoatException
    {
      super(paramString, paramLong, 4);
      assert (!this.mCompressed);
    }

    public int getSize()
    {
      return this.mData.length;
    }

    public String getServer()
    {
      return null;
    }
  }

  public static class GoatImageException extends Exception
  {
    public GoatImageException(String paramString)
    {
      super();
    }

    public GoatImageException(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
      assert (paramString != null);
      GoatImage.mLog.fine("Throw Internal Exception - " + getMessage());
      if (paramThrowable != null)
        GoatImage.mLog.log(Level.FINE, "Caused due to ", paramThrowable);
    }
  }

  public static abstract interface Fetcher extends Serializable
  {
    public abstract byte[] reFetchImageData();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.GoatImage
 * JD-Core Version:    0.6.1
 */