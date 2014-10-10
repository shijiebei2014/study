package com.remedy.arsys.ws.services.security;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.FormType;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.Value;
import com.bmc.arsys.arencrypt.PaddedPasswordEncryption;
import java.util.List;
import java.util.StringTokenizer;

class ARPolicyLoader
{
  static final int FIELD_POLICY_ID = 179;
  static final int FIELD_REQ_SEC_ACTION = 9003;
  static final int FIELD_RES_SEC_ACTION = 9004;
  static final int FIELD_SEC_TOK_REF = 9000;
  static final int FIELD_SIGN_ALIAS = 9005;
  static final int FIELD_SIGN_ALIAS_PASS = 9011;
  static final int FIELD_ENC_ALIAS = 9006;
  static final int FIELD_ENC_ALIAS_PASS = 9009;
  static final int FIELD_PROP_FILE = 9008;
  static final int FIELD_KEYSTORE_FILE = 9007;
  static final int FIELD_HEADER_ENC_FLG = 9019;
  static final int FIELD_ENC_SYM_ALG = 9024;
  static final int FIELD_DISABLE_SIGN_CONFIRM = 9023;
  private static String ENC_PASS_KEY = "arwebservicepluginkey";
  static final int[] fieldIds = { 179, 9003, 9004, 9000, 9005, 9011, 9006, 9009, 9008, 9007, 9019, 9024, 9023 };

  static PolicyDetails loadPolicyDetails(ARServerUser paramARServerUser, String paramString1, String paramString2)
    throws Exception
  {
    List localList1 = paramARServerUser.getListForm(0L, FormType.REGULAR.toInt(), null, fieldIds);
    if ((localList1 == null) || (localList1.isEmpty()))
      throw new ARException(2, 9130, "Web Service security configuration form not found");
    String str1 = (String)localList1.get(0);
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand1 = new ArithmeticOrRelationalOperand(179);
    ArithmeticOrRelationalOperand localArithmeticOrRelationalOperand2 = new ArithmeticOrRelationalOperand(new Value(paramString1));
    RelationalOperationInfo localRelationalOperationInfo = new RelationalOperationInfo(1, localArithmeticOrRelationalOperand1, localArithmeticOrRelationalOperand2);
    QualifierInfo localQualifierInfo = new QualifierInfo(localRelationalOperationInfo);
    List localList2 = paramARServerUser.getListEntryObjects(str1, localQualifierInfo, 0, 2, null, fieldIds, false, null);
    if ((localList2 == null) || (localList2.size() == 0))
      throw new Exception("Webservices: Security configuration not found for configuration id:" + paramString1);
    Entry localEntry = (Entry)localList2.get(0);
    PolicyDetails localPolicyDetails = new PolicyDetails();
    localPolicyDetails.setPolicyId(paramString1);
    if (((Value)localEntry.get(Integer.valueOf(9003))).getValue() != null)
    {
      str2 = getSecurityAction((String)((Value)localEntry.get(Integer.valueOf(9003))).getValue());
      localPolicyDetails.setRequestSecurityAction(str2);
    }
    if (((Value)localEntry.get(Integer.valueOf(9004))).getValue() != null)
    {
      str2 = getSecurityAction((String)((Value)localEntry.get(Integer.valueOf(9004))).getValue());
      localPolicyDetails.setResponseSecurityAction(str2);
    }
    String str2 = getKeyIdentifier(((Integer)((Value)localEntry.get(Integer.valueOf(9000))).getValue()).intValue());
    localPolicyDetails.setSignKeyID(str2);
    localPolicyDetails.setEncKeyID(str2);
    if (((Value)localEntry.get(Integer.valueOf(9005))).getValue() != null)
      localPolicyDetails.setUserAlias((String)((Value)localEntry.get(Integer.valueOf(9005))).getValue());
    if (((Value)localEntry.get(Integer.valueOf(9006))).getValue() != null)
      localPolicyDetails.setEncryptionUserAlias((String)((Value)localEntry.get(Integer.valueOf(9006))).getValue());
    PaddedPasswordEncryption localPaddedPasswordEncryption = new PaddedPasswordEncryption(true, true);
    localPaddedPasswordEncryption.setPasswordKeyForEncryption(ENC_PASS_KEY.getBytes());
    byte[] arrayOfByte;
    if (((Value)localEntry.get(Integer.valueOf(9011))).getValue() != null)
    {
      localObject = (String)((Value)localEntry.get(Integer.valueOf(9011))).getValue();
      arrayOfByte = localPaddedPasswordEncryption.decryptCharData(((String)localObject).getBytes());
      localPolicyDetails.setUserAliasPassword(new String(arrayOfByte));
    }
    if (((Value)localEntry.get(Integer.valueOf(9009))).getValue() != null)
    {
      localObject = (String)((Value)localEntry.get(Integer.valueOf(9009))).getValue();
      arrayOfByte = localPaddedPasswordEncryption.decryptCharData(((String)localObject).getBytes());
      localPolicyDetails.setEncryptionUserAliasPassword(new String(arrayOfByte));
    }
    if (((Value)localEntry.get(Integer.valueOf(9019))).getValue() != null)
      localPolicyDetails.setHeaderEncFlag(true);
    if (((Value)localEntry.get(Integer.valueOf(9024))).getValue() != null)
    {
      localObject = (Integer)((Value)localEntry.get(Integer.valueOf(9024))).getValue();
      if (((Integer)localObject).intValue() > 0)
      {
        if (((Integer)localObject).intValue() == 1)
          localPolicyDetails.setEncSymAlg("AES_192");
        if (((Integer)localObject).intValue() == 2)
          localPolicyDetails.setEncSymAlg("AES_256");
        if (((Integer)localObject).intValue() == 3)
          localPolicyDetails.setEncSymAlg("Triple DES");
      }
    }
    Object localObject = storePolicyFiles(paramARServerUser, localEntry, 9008, str1);
    localPolicyDetails.setPropBytes((byte[])localObject);
    localObject = storePolicyFiles(paramARServerUser, localEntry, 9007, str1);
    localPolicyDetails.setKsBytes((byte[])localObject);
    if (((Value)localEntry.get(Integer.valueOf(9023))).getValue() != null)
      localPolicyDetails.setSignConfirm(true);
    return localPolicyDetails;
  }

  private static String getSecurityAction(String paramString)
    throws ARException
  {
    Object localObject = "";
    String str1 = null;
    String str2 = null;
    String str3 = null;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
    while (localStringTokenizer.hasMoreTokens())
    {
      localObject = localStringTokenizer.nextToken();
      if (((String)localObject).equals("Signature"))
        str1 = "Signature";
      if (((String)localObject).equals("Encrypt"))
        str2 = "Encrypt";
      if (((String)localObject).equals("Timestamp"))
        str3 = "Timestamp";
    }
    localObject = null;
    if (str3 != null)
      localObject = str3;
    if (str1 != null)
      if (localObject == null)
        localObject = str1;
      else
        localObject = (String)localObject + " " + str1;
    if (str2 != null)
      if (localObject == null)
        localObject = str2;
      else
        localObject = (String)localObject + " " + str2;
    return localObject;
  }

  private static String getKeyIdentifier(int paramInt)
    throws ARException
  {
    switch (paramInt)
    {
    case 0:
      return "DirectReference";
    case 1:
      return "IssuerSerial";
    }
    return "IssuerSerial";
  }

  private static byte[] storePolicyFiles(ARServerUser paramARServerUser, Entry paramEntry, int paramInt, String paramString)
    throws ARException
  {
    byte[] arrayOfByte = paramARServerUser.getEntryBlob(paramString, paramEntry.getEntryId(), paramInt);
    return arrayOfByte;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.security.ARPolicyLoader
 * JD-Core Version:    0.6.1
 */