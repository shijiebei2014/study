package com.remedy.arsys.stubs;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.Value;
import com.bmc.arsys.util.ARUtilEgcp;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.support.SchemaKeyFactory;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAAuthenticator
{
  private static final String CIPHER_ALGORITHM = "RSA";
  private static final String CIPHER_TRANSFORMATION = "RSA";
  protected static final Log mlog = Log.get(3);
  public static final int[] SERVER_KEY_MAP_SCHEMA_KEY_IDS = { 99002, 99003, 99006 };
  private static final int[] KEY_STORE_SCHEMA_KEY_IDS = { 99000, 99001 };
  private ARServerUser mServer = null;
  private static final String CIPHER_TOKEN_KEY = "ARSYSTEM_DVF_PRIVATE_KEY";

  public RSAAuthenticator(String paramString)
  {
    if (this.mServer == null)
      try
      {
        createServerLogin(paramString);
      }
      catch (ARException localARException)
      {
      }
      catch (GoatException localGoatException)
      {
      }
  }

  public String encrypt(String paramString1, String paramString2)
  {
    try
    {
      Entry localEntry = getPublicKeyEntry(paramString2);
      if ((localEntry == null) || (localEntry.get(Integer.valueOf(SERVER_KEY_MAP_SCHEMA_KEY_IDS[0])) == null))
        return null;
      String str = (String)((Value)localEntry.get(Integer.valueOf(SERVER_KEY_MAP_SCHEMA_KEY_IDS[0]))).getValue();
      str = str.replaceAll("\r", "");
      String[] arrayOfString = str.split("\\n");
      KeyFactory localKeyFactory = KeyFactory.getInstance("RSA");
      RSAPublicKeySpec localRSAPublicKeySpec = new RSAPublicKeySpec(new BigInteger(arrayOfString[0]), new BigInteger(arrayOfString[1]));
      PublicKey localPublicKey = localKeyFactory.generatePublic(localRSAPublicKeySpec);
      Cipher localCipher = Cipher.getInstance("RSA");
      localCipher.init(1, localPublicKey);
      byte[] arrayOfByte = localCipher.doFinal(paramString1.getBytes());
      return asHex(arrayOfByte);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: encrypt()" + localNoSuchAlgorithmException.getMessage());
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: encrypt()" + localInvalidKeySpecException.getMessage());
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: encrypt()" + localNoSuchPaddingException.getMessage());
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: encrypt()" + localInvalidKeyException.getMessage());
    }
    catch (BadPaddingException localBadPaddingException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: encrypt()" + localBadPaddingException.getMessage());
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: encrypt()" + localIllegalBlockSizeException.getMessage());
    }
    return null;
  }

  public String decrypt(String paramString1, String paramString2)
  {
    try
    {
      Entry localEntry = getPrivateKeyEntry(paramString2);
      if ((localEntry == null) || (localEntry.get(Integer.valueOf(KEY_STORE_SCHEMA_KEY_IDS[1])) == null))
        return null;
      String str1 = (String)((Value)localEntry.get(Integer.valueOf(KEY_STORE_SCHEMA_KEY_IDS[1]))).getValue();
      String str2 = new ARUtilEgcp().funcDUtil(str1, "ARSYSTEM_DVF_PRIVATE_KEY");
      String[] arrayOfString = str2.split("\\n");
      RSAPrivateKeySpec localRSAPrivateKeySpec = new RSAPrivateKeySpec(new BigInteger(arrayOfString[0]), new BigInteger(arrayOfString[1]));
      KeyFactory localKeyFactory = KeyFactory.getInstance("RSA");
      PrivateKey localPrivateKey = localKeyFactory.generatePrivate(localRSAPrivateKeySpec);
      Cipher localCipher = Cipher.getInstance("RSA");
      localCipher.init(2, localPrivateKey);
      byte[] arrayOfByte = localCipher.doFinal(asByte(paramString1));
      return new String(arrayOfByte, "UTF-8");
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: decrypt()" + localInvalidKeySpecException.getMessage());
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: decrypt()" + localNoSuchAlgorithmException.getMessage());
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: decrypt()" + localNoSuchPaddingException.getMessage());
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: decrypt()" + localInvalidKeyException.getMessage());
    }
    catch (BadPaddingException localBadPaddingException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: decrypt()" + localBadPaddingException.getMessage());
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: decrypt()" + localIllegalBlockSizeException.getMessage());
    }
    catch (Exception localException)
    {
      mlog.log(Level.SEVERE, "exception, Class: RSAAuthentication, Method: decrypt()" + localException.getMessage());
    }
    return null;
  }

  public static void main(String[] paramArrayOfString)
  {
    String str1 = "thunder";
    RSAAuthenticator localRSAAuthenticator = new RSAAuthenticator(str1);
    try
    {
      localRSAAuthenticator.createServerLogin(str1);
    }
    catch (GoatException localGoatException)
    {
    }
    catch (ARException localARException)
    {
    }
    String str2 = localRSAAuthenticator.encrypt("Hello world", str1);
    String str3 = localRSAAuthenticator.decrypt(str2, str1);
    System.out.println("plain text after decrypt = " + new String(str3));
  }

  public Entry getPublicKeyEntry(String paramString)
  {
    try
    {
      ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand1 = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 99003);
      ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand2 = new ArithmeticOrRelationalOperand(new Value(paramString));
      QualifierInfo localQualifierInfo = new QualifierInfo(new RelationalOperationInfo(1, localArithmeticOrRelationalOperand1, localArithmeticOrRelationalOperand2));
      String str = SchemaKeyFactory.getInstance().getSchemaKey(this.mServer, SERVER_KEY_MAP_SCHEMA_KEY_IDS);
      List localList = this.mServer.getListEntryObjects(str, localQualifierInfo, 0, 0, null, SERVER_KEY_MAP_SCHEMA_KEY_IDS, false, null);
      Entry[] arrayOfEntry = (Entry[])localList.toArray(new Entry[0]);
      return arrayOfEntry[0];
    }
    catch (ARException localARException)
    {
    }
    return null;
  }

  private Entry getPrivateKeyEntry(String paramString)
  {
    try
    {
      ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand1 = new ArithmeticOrRelationalOperand(new Value("1", DataType.ENUM));
      ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand2 = new ArithmeticOrRelationalOperand(new Value("1", DataType.ENUM));
      RelationalOperationInfo localRelationalOperationInfo = new RelationalOperationInfo(1, localArithmeticOrRelationalOperand1, localArithmeticOrRelationalOperand2);
      QualifierInfo localQualifierInfo = new QualifierInfo(localRelationalOperationInfo);
      String str = SchemaKeyFactory.getInstance().getSchemaKey(this.mServer, KEY_STORE_SCHEMA_KEY_IDS);
      List localList = this.mServer.getListEntryObjects(str, localQualifierInfo, 0, 0, null, KEY_STORE_SCHEMA_KEY_IDS, false, null);
      Entry[] arrayOfEntry = (Entry[])localList.toArray(new Entry[0]);
      return arrayOfEntry[0];
    }
    catch (ARException localARException)
    {
    }
    return null;
  }

  public void createServerLogin(String paramString)
    throws ARException, GoatException
  {
    try
    {
      this.mServer = ServerLogin.getAdmin(paramString);
      this.mServer.login();
    }
    catch (GoatException localGoatException)
    {
      this.mServer = null;
      throw new GoatException(localGoatException.getMessage());
    }
    catch (ARException localARException)
    {
      throw new ARException();
    }
  }

  private String asHex(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length * 2);
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      if ((paramArrayOfByte[i] & 0xFF) < 16)
        localStringBuffer.append("0");
      localStringBuffer.append(Long.toString(paramArrayOfByte[i] & 0xFF, 16));
    }
    return localStringBuffer.toString();
  }

  private byte[] asByte(String paramString)
  {
    int i = paramString.length();
    byte[] arrayOfByte = new byte[i / 2];
    for (int j = 0; j < i; j += 2)
      arrayOfByte[(j / 2)] = (byte)((Character.digit(paramString.charAt(j), 16) << 4) + Character.digit(paramString.charAt(j + 1), 16));
    return arrayOfByte;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.RSAAuthenticator
 * JD-Core Version:    0.6.1
 */