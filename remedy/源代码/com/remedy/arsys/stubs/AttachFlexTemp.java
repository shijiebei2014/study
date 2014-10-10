package com.remedy.arsys.stubs;

import com.remedy.arsys.goat.AttachmentData;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AttachFlexTemp
{
  private static Map<String, Object[]> uploadedAttach = new HashMap();

  public static void uploadToField(String paramString1, String paramString2, InputStream paramInputStream, String paramString3)
  {
    File localFile = AttachmentData.createTemporary(paramInputStream);
    Object[] arrayOfObject = { localFile, paramString3 };
    Collections.synchronizedMap(uploadedAttach).put(paramString1 + "/" + paramString2, arrayOfObject);
    localFile.delete();
  }

  public static Object[] getUploaded(String paramString1, String paramString2)
  {
    return (Object[])Collections.synchronizedMap(uploadedAttach).remove(paramString1 + "/" + paramString2);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.AttachFlexTemp
 * JD-Core Version:    0.6.1
 */