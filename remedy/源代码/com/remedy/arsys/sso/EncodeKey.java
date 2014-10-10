package com.remedy.arsys.sso;

import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class EncodeKey
{
  private static final String CIPHER_SCHEME = "DES";
  private static final String CIPHER_TRANSFORMATION = "DES/ECB/NoPadding";
  private static final String CIPHER_PROVIDER = "SunJCE";
  private static final byte[] wrapper = { 115, 56, 50, 76, 49, 53, 101, 66 };
  private static final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  private static SecretKey tmpKey;
  private static Cipher cipher;

  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    if (paramArrayOfString.length < 1)
    {
      System.out.println("Usage: java -classpath 'yourpath/Mid-Tier.jar' com.remedy.arsys.sso.EncodeKey ssokey");
      System.exit(0);
    }
    if (paramArrayOfString[0].length() != 8)
    {
      System.out.println("WARNING: The length of the input encryption key must be 8 characters/numbers.");
      System.exit(0);
    }
    init();
    cipher.init(1, tmpKey);
    byte[] arrayOfByte1 = pad(paramArrayOfString[0].getBytes());
    byte[] arrayOfByte2 = cipher.doFinal(arrayOfByte1);
    String str = bytesToHexString(arrayOfByte2);
    System.out.println(str);
  }

  private static void init()
    throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException
  {
    DESKeySpec localDESKeySpec = new DESKeySpec(wrapper);
    SecretKeyFactory localSecretKeyFactory = SecretKeyFactory.getInstance("DES");
    tmpKey = localSecretKeyFactory.generateSecret(localDESKeySpec);
    cipher = Cipher.getInstance("DES/ECB/NoPadding", "SunJCE");
  }

  private static byte[] pad(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;
    int j = i / 8;
    int k = i % 8;
    int m = 8 - k;
    int n = 0;
    n = 8 * (j + 1);
    byte[] arrayOfByte = new byte[n];
    for (int i1 = 0; i1 < i; i1++)
      arrayOfByte[i1] = paramArrayOfByte[i1];
    while (i < n)
    {
      arrayOfByte[i] = (byte)m;
      i++;
    }
    return arrayOfByte;
  }

  private static String bytesToHexString(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramArrayOfByte.length; i++)
      localStringBuilder.append(hexDigit(paramArrayOfByte[i]));
    return localStringBuilder.toString();
  }

  private static String hexDigit(byte paramByte)
  {
    StringBuilder localStringBuilder = new StringBuilder(2);
    localStringBuilder.append(hex[(paramByte >> 4 & 0xF)]);
    localStringBuilder.append(hex[(paramByte & 0xF)]);
    return localStringBuilder.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.sso.EncodeKey
 * JD-Core Version:    0.6.1
 */