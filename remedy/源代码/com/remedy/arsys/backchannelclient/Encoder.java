package com.remedy.arsys.backchannelclient;

class Encoder
{
  public static String EncodeParam(long paramLong)
  {
    return EncodeParam("" + paramLong);
  }

  public static String EncodeParam(String paramString)
  {
    return paramString.length() + "/" + paramString;
  }

  public static String EncodeParam(boolean paramBoolean)
  {
    return paramBoolean ? "1/1" : "1/0";
  }

  public static String EncodeParam(int paramInt)
  {
    return EncodeParam("" + paramInt);
  }

  public static String EncodeParam(long[] paramArrayOfLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramArrayOfLong.length; i++)
      localStringBuilder.append(EncodeParam(paramArrayOfLong[i]));
    return EncodeParam(paramArrayOfLong.length + "/" + localStringBuilder);
  }

  public static String EncodeParam(String[] paramArrayOfString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramArrayOfString.length; i++)
      localStringBuilder.append(EncodeParam(paramArrayOfString[i]));
    return EncodeParam(paramArrayOfString.length + "/" + localStringBuilder);
  }

  public static String EncodeParam(boolean[] paramArrayOfBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramArrayOfBoolean.length; i++)
      localStringBuilder.append(EncodeParam(paramArrayOfBoolean[i]));
    return EncodeParam(paramArrayOfBoolean.length + "/" + localStringBuilder);
  }

  public static String EncodeParam(int[] paramArrayOfInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramArrayOfInt.length; i++)
      localStringBuilder.append(EncodeParam(paramArrayOfInt[i]));
    return EncodeParam(paramArrayOfInt.length + "/" + localStringBuilder);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannelclient.Encoder
 * JD-Core Version:    0.6.1
 */