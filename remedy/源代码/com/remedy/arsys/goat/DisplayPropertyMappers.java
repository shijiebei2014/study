package com.remedy.arsys.goat;

import com.bmc.arsys.api.ByteListValue;
import com.bmc.arsys.api.CoordinateInfo;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.LinkedHashMap;
import java.util.List;

public class DisplayPropertyMappers
{
  private static final String LENGTH_PAT = "\\\\[0-9]*\\\\";

  protected static String propToString(Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if ((paramValue == null) || (paramValue.getValue() == null))
      throw new BadDisplayPropertyException();
    return paramValue.getValue().toString();
  }

  protected static int propToInt(Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    try
    {
      return Integer.parseInt(propToString(paramValue));
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    throw new BadDisplayPropertyException();
  }

  protected static long propToLong(Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    try
    {
      return Long.parseLong(propToString(paramValue));
    }
    catch (NumberFormatException localNumberFormatException)
    {
    }
    throw new BadDisplayPropertyException();
  }

  protected static boolean propToBool(Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    return propToInt(paramValue) == 1;
  }

  protected static int propToFieldID(Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    return (int)Long.parseLong(propToString(paramValue));
  }

  protected static byte[] propToByteArray(Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if ((!paramValue.getDataType().equals(DataType.BYTES)) || (paramValue.getValue() == null))
      throw new BadDisplayPropertyException();
    return ((ByteListValue)paramValue.getValue()).getValue();
  }

  protected static ARBox propToBox(Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    List localList = (List)paramValue.getValue();
    if (localList.size() < 2)
    {
      if (localList.size() == 0)
        throw new BadDisplayPropertyException();
      return new ARBox(((CoordinateInfo)localList.get(0)).getXCoordinate(), ((CoordinateInfo)localList.get(0)).getYCoordinate(), ((CoordinateInfo)localList.get(0)).getXCoordinate(), ((CoordinateInfo)localList.get(0)).getYCoordinate());
    }
    int i = 1;
    if (localList.size() == 4)
      i = 2;
    return new ARBox(((CoordinateInfo)localList.get(0)).getXCoordinate(), ((CoordinateInfo)localList.get(0)).getYCoordinate(), ((CoordinateInfo)localList.get(i)).getXCoordinate(), ((CoordinateInfo)localList.get(i)).getYCoordinate());
  }

  protected static String propToHTMLColour(Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    String str = propToString(paramValue);
    if (str.startsWith("0x"))
      try
      {
        int i = Integer.parseInt(str.substring(2), 16);
        i = (i & 0xFF) << 16 | i & 0xFF00 | i >> 16 & 0xFF;
        str = "000000" + Integer.toHexString(i);
        str = "#" + str.substring(str.length() - 6);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new BadDisplayPropertyException();
      }
    else if (!str.startsWith("#"))
      str = "#" + str;
    return str;
  }

  protected static long propToMenuAccessHiddenMask(Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    String str = propToString(paramValue);
    String[] arrayOfString = str.split("\\\\");
    if (arrayOfString.length < 2)
      return 0L;
    int i = (arrayOfString.length - 1) / 2;
    if (i == 0)
      return 0L;
    int j = 0;
    int k = 1;
    for (int m = 0; m < i; m++)
    {
      int n = Integer.parseInt(arrayOfString[k]);
      k++;
      int i1 = Integer.parseInt(arrayOfString[k]);
      k++;
      if (i1 == 2)
        j |= 1 << (int)(n - 400L);
    }
    return j;
  }

  protected static GoatImage propToGoatImage(Value paramValue, GoatImage.Fetcher paramFetcher, String paramString)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    byte[] arrayOfByte = null;
    GoatImage localGoatImage = null;
    if (paramValue.getDataType().equals(DataType.CHAR))
    {
      try
      {
        String str2 = paramValue.getValue().toString();
        localGoatImage = GoatImage.getImageReference(paramFetcher, str2, paramString);
      }
      catch (GoatImage.GoatImageException localGoatImageException1)
      {
        throw new BadDisplayPropertyException();
      }
    }
    else
    {
      arrayOfByte = propToByteArray(paramValue);
      if (arrayOfByte == null)
        throw new BadDisplayPropertyException();
      int i = ((ByteListValue)paramValue.getValue()).getType();
      String str1;
      if (i == 1)
        str1 = "image/bmp";
      else if (i == 3)
        str1 = "image/tiff";
      else if (i == 4)
        str1 = "image/targa";
      else if (i == 9)
        str1 = "image/gif";
      else if (i == 10)
        str1 = "image/png";
      else
        str1 = "image/jpeg";
      try
      {
        localGoatImage = GoatImage.put(arrayOfByte, str1, paramFetcher);
      }
      catch (GoatImage.GoatImageException localGoatImageException2)
      {
        throw new BadDisplayPropertyException();
      }
    }
    return localGoatImage;
  }

  protected static LinkedHashMap propToNamedSearchesMap(Value paramValue, String paramString)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    String str1 = propToString(paramValue);
    if (str1.length() == 0)
      throw new BadDisplayPropertyException();
    int i = 0;
    int j = str1.indexOf('\\');
    if (j < 0)
      throw new BadDisplayPropertyException();
    int k = Integer.parseInt(str1.substring(0, j));
    LinkedHashMap localLinkedHashMap = new LinkedHashMap(k);
    i = j;
    for (int m = 0; (m < k) && (i < str1.length()); m++)
    {
      String str2 = str1.substring(i);
      String[] arrayOfString = str2.split("\\\\[0-9]*\\\\");
      if (arrayOfString.length > 3)
      {
        String str3 = arrayOfString[1];
        String str4 = arrayOfString[2];
        int n = str2.indexOf("\\" + str4 + "\\", str3.length());
        i = i + n + str4.length() + 2;
        try
        {
          ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString);
          QualifierInfo localQualifierInfo = localServerLogin.decodeARQualifierStruct(str1.substring(i));
          String str5 = localServerLogin.encodeARQualifierStruct(localQualifierInfo);
          if (str4.length() < 1)
            str4 = str3;
          localLinkedHashMap.put(str4, str5);
          int i1 = str5.length();
          i += i1 - 1;
        }
        catch (Exception localException)
        {
          return null;
        }
      }
    }
    return localLinkedHashMap;
  }

  protected static class BadDisplayPropertyException extends Exception
  {
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.DisplayPropertyMappers
 * JD-Core Version:    0.6.1
 */