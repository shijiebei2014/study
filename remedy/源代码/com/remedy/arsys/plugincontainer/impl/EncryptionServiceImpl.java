package com.remedy.arsys.plugincontainer.impl;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.OperandType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.plugincontainer.IEncryptionService;
import com.remedy.arsys.support.SchemaKeyFactory;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncryptionServiceImpl
  implements IEncryptionService
{
  private static final String CIPHER_ALGORITHM = "RSA";
  private static final String CIPHER_TRANSFORMATION = "RSA";
  protected static final Log mlog = Log.get(3);
  public static final int[] SERVER_KEY_MAP_SCHEMA_KEY_IDS = { 99002 };
  private ARServerUser mServer = null;

  public EncryptionServiceImpl(ARServerUser paramARServerUser)
  {
    this.mServer = paramARServerUser;
  }

  public String encrypt(String paramString)
  {
    try
    {
      String str = getPublicKey();
      if (str == null)
        throw new InvalidKeyException("No key exists for the current server in Key Map!");
      str = str.replaceAll("\r", "");
      String[] arrayOfString = str.split("\\n");
      KeyFactory localKeyFactory = KeyFactory.getInstance("RSA");
      RSAPublicKeySpec localRSAPublicKeySpec = new RSAPublicKeySpec(new BigInteger(arrayOfString[0]), new BigInteger(arrayOfString[1]));
      PublicKey localPublicKey = localKeyFactory.generatePublic(localRSAPublicKeySpec);
      Cipher localCipher = Cipher.getInstance("RSA");
      localCipher.init(1, localPublicKey);
      byte[] arrayOfByte = localCipher.doFinal(paramString.getBytes());
      return asHex(arrayOfByte);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      mlog.log(Level.SEVERE, "exception, Class: EncryptionServiceImpl, Method: encrypt()" + localNoSuchAlgorithmException.getMessage());
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
      mlog.log(Level.SEVERE, "exception, Class: EncryptionServiceImpl, Method: encrypt()" + localInvalidKeySpecException.getMessage());
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
      mlog.log(Level.SEVERE, "exception, Class: EncryptionServiceImpl, Method: encrypt()" + localNoSuchPaddingException.getMessage());
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      mlog.log(Level.SEVERE, "exception, Class: EncryptionServiceImpl, Method: encrypt()" + localInvalidKeyException.getMessage());
    }
    catch (BadPaddingException localBadPaddingException)
    {
      mlog.log(Level.SEVERE, "exception, Class: EncryptionServiceImpl, Method: encrypt()" + localBadPaddingException.getMessage());
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      mlog.log(Level.SEVERE, "exception, Class: EncryptionServiceImpl, Method: encrypt()" + localIllegalBlockSizeException.getMessage());
    }
    return null;
  }

  public String getPublicKey()
  {
    try
    {
      ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand1 = new ArithmeticOrRelationalOperand(OperandType.FIELDID, 99003);
      ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand2 = new ArithmeticOrRelationalOperand(new Value(Configuration.getInstance().getLongName(this.mServer.getServer()) + "%"));
      QualifierInfo localQualifierInfo = new QualifierInfo(new RelationalOperationInfo(7, localArithmeticOrRelationalOperand1, localArithmeticOrRelationalOperand2));
      String str = SchemaKeyFactory.getInstance().getSchemaKey(this.mServer, SERVER_KEY_MAP_SCHEMA_KEY_IDS);
      List localList = this.mServer.getListEntryObjects(str, localQualifierInfo, 0, 0, null, SERVER_KEY_MAP_SCHEMA_KEY_IDS, false, null);
      Object localObject = (localList != null) && (!localList.isEmpty()) ? (Entry)localList.get(0) : null;
      if ((localObject == null) || (localObject.get(Integer.valueOf(SERVER_KEY_MAP_SCHEMA_KEY_IDS[0])) == null))
        return null;
      return (String)((Value)localObject.get(Integer.valueOf(SERVER_KEY_MAP_SCHEMA_KEY_IDS[0]))).getValue();
    }
    catch (ARException localARException)
    {
      mlog.log(Level.SEVERE, "exception, Class: EncryptionServiceImpl, Method: getPublicKey()" + localARException.getMessage());
    }
    return null;
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
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.plugincontainer.impl.EncryptionServiceImpl
 * JD-Core Version:    0.6.1
 */