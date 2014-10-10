package com.remedy.arsys.sso;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.session.Authenticator;
import com.remedy.arsys.session.UserCredentials;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractAuthenticator
  implements Authenticator
{
  private static final String CIPHER_SCHEME = "DES";
  private static final String CIPHER_TRANSFORMATION = "DES/ECB/PKCS5Padding";
  private static final String CIPHER_PROVIDER = "SunJCE";
  private static final String SSO_ENCRYPTION_KEY = "arsystem.authenticator.sso.enckey";
  private static final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  private Key secretKey;
  private Cipher cipher;
  protected static final Log arLog = Log.get(3);

  public void init(Map paramMap)
  {
    try
    {
      initCipher();
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: Initializing Cipher with invalid key.", localInvalidKeyException);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: No such ciphering algorithm.", localNoSuchAlgorithmException);
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: Bad key specification.", localInvalidKeySpecException);
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: Can't find the cipher provider.", localNoSuchProviderException);
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: The config ciphering doesn't support padding scheme.", localNoSuchPaddingException);
    }
    catch (IllegalStateException localIllegalStateException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: The Cipher is in an illegal state.", localIllegalStateException);
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: Ciphering an illegal block size.", localIllegalBlockSizeException);
    }
    catch (BadPaddingException localBadPaddingException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: Bad padding.", localBadPaddingException);
    }
  }

  public abstract void destroy();

  public abstract UserCredentials getAuthenticatedCredentials(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException;

  private void initCipher()
    throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException, IllegalStateException, IllegalBlockSizeException, BadPaddingException
  {
    byte[] arrayOfByte1 = { 115, 56, 50, 76, 49, 53, 101, 66 };
    DESKeySpec localDESKeySpec = new DESKeySpec(arrayOfByte1);
    SecretKeyFactory localSecretKeyFactory = SecretKeyFactory.getInstance("DES");
    SecretKey localSecretKey = localSecretKeyFactory.generateSecret(localDESKeySpec);
    this.cipher = Cipher.getInstance("DES/ECB/PKCS5Padding", "SunJCE");
    this.cipher.init(2, localSecretKey);
    String str1 = Configuration.getInstance().getProperty("arsystem.authenticator.sso.enckey", "105269288E76C311410B6595D6E52791");
    int i = str1.length() / 2;
    byte[] arrayOfByte2 = new byte[i];
    for (int j = 0; j < i; j++)
      arrayOfByte2[j] = byteVal(str1.charAt(2 * j), str1.charAt(2 * j + 1));
    String str2 = new String(this.cipher.doFinal(arrayOfByte2));
    localDESKeySpec = new DESKeySpec(str2.getBytes());
    this.secretKey = localSecretKeyFactory.generateSecret(localDESKeySpec);
    this.cipher.init(1, this.secretKey);
  }

  protected String encrypt(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = this.cipher.doFinal(paramString.getBytes());
      for (int i = 0; i < arrayOfByte.length; i++)
        localStringBuilder.append(hexDigit(arrayOfByte[i]));
    }
    catch (IllegalStateException localIllegalStateException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: Cipher is in illegal state.", localIllegalStateException);
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: Encoding illegal block size.", localIllegalBlockSizeException);
    }
    catch (BadPaddingException localBadPaddingException)
    {
      arLog.log(Level.SEVERE, "AbstractAuthenticator: Bad Padding.", localBadPaddingException);
    }
    return localStringBuilder.toString();
  }

  private static String hexDigit(byte paramByte)
  {
    StringBuilder localStringBuilder = new StringBuilder(2);
    localStringBuilder.append(hex[(paramByte >> 4 & 0xF)]);
    localStringBuilder.append(hex[(paramByte & 0xF)]);
    return localStringBuilder.toString();
  }

  private static byte byteVal(char paramChar1, char paramChar2)
  {
    return (byte)((lookup(paramChar1) << 4) + lookup(paramChar2));
  }

  private static byte lookup(char paramChar)
  {
    switch (paramChar)
    {
    case '0':
      return 0;
    case '1':
      return 1;
    case '2':
      return 2;
    case '3':
      return 3;
    case '4':
      return 4;
    case '5':
      return 5;
    case '6':
      return 6;
    case '7':
      return 7;
    case '8':
      return 8;
    case '9':
      return 9;
    case 'A':
      return 10;
    case 'B':
      return 11;
    case 'C':
      return 12;
    case 'D':
      return 13;
    case 'E':
      return 14;
    case 'F':
      return 15;
    case ':':
    case ';':
    case '<':
    case '=':
    case '>':
    case '?':
    case '@':
    }
    return 0;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.sso.AbstractAuthenticator
 * JD-Core Version:    0.6.1
 */