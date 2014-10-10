package com.remedy.arsys.ws.services.security;

import com.remedy.arsys.log.Log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.axis.Handler;
import org.w3c.dom.Document;

class WSPolicyProcessor
{
  private static Log mLog = Log.get(5);
  static final String SECURITY_ACTION_SIGN_ENCRYPT = "Signature Encrypt";

  static Handler getWSSender(PolicyDetails paramPolicyDetails)
    throws IOException
  {
    String str1 = "";
    String str2 = paramPolicyDetails.getResponseSecurityAction();
    WSSender localWSSender = new WSSender(paramPolicyDetails);
    if (str2.indexOf("Timestamp") > -1)
      str1 = "Timestamp";
    byte[] arrayOfByte;
    String str3;
    Properties localProperties;
    ByteArrayInputStream localByteArrayInputStream;
    String str4;
    if (str2.indexOf("Signature Encrypt") > -1)
    {
      localWSSender.setOption("signatureKeyIdentifier", paramPolicyDetails.getSignKeyID());
      arrayOfByte = paramPolicyDetails.getPropBytes();
      str3 = new String(arrayOfByte);
      localProperties = new Properties();
      localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      localProperties.load(localByteArrayInputStream);
      localWSSender.setOption("SignaturePropRefId", localProperties);
      localWSSender.setOption("encryptionPropRefId", localProperties);
      localWSSender.setOption("user", paramPolicyDetails.getUserAlias());
      localWSSender.setOption("passwordCallbackRef", getPasswordCB(paramPolicyDetails, str2));
      localWSSender.setOption("encryptionKeyIdentifier", paramPolicyDetails.getEncKeyID());
      localWSSender.setOption("encryptionUser", paramPolicyDetails.getEncryptionUserAlias());
      str4 = paramPolicyDetails.getEncSymAlg();
      if (str4 != null)
      {
        if (str4.equals("AES_192"))
          localWSSender.setOption("encryptionSymAlgorithm", "http://www.w3.org/2001/04/xmlenc#aes192-cbc");
        if (str4.equals("AES_256"))
          localWSSender.setOption("encryptionSymAlgorithm", "http://www.w3.org/2001/04/xmlenc#aes256-cbc");
        if (str4.equals("Triple DES"))
          localWSSender.setOption("encryptionSymAlgorithm", "http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
      }
      if (str1.length() > 0)
        str1 = str1 + ' ' + "Signature Encrypt";
      else
        str1 = "Signature Encrypt";
    }
    else if (str2.indexOf("Signature") > -1)
    {
      localWSSender.setOption("signatureKeyIdentifier", paramPolicyDetails.getSignKeyID());
      arrayOfByte = paramPolicyDetails.getPropBytes();
      str3 = new String(arrayOfByte);
      localProperties = new Properties();
      localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      localProperties.load(localByteArrayInputStream);
      localWSSender.setOption("SignaturePropRefId", localProperties);
      localWSSender.setOption("user", paramPolicyDetails.getUserAlias());
      localWSSender.setOption("passwordCallbackRef", getPasswordCB(paramPolicyDetails, str2));
      if (str1.length() > 0)
        str1 = str1 + ' ' + "Signature";
      else
        str1 = "Signature";
    }
    else if (str2.indexOf("Encrypt") > -1)
    {
      localWSSender.setOption("encryptionKeyIdentifier", paramPolicyDetails.getEncKeyID());
      arrayOfByte = paramPolicyDetails.getPropBytes();
      str3 = new String(arrayOfByte);
      localProperties = new Properties();
      localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      localProperties.load(localByteArrayInputStream);
      localWSSender.setOption("encryptionPropRefId", localProperties);
      localWSSender.setOption("encryptionUser", paramPolicyDetails.getEncryptionUserAlias());
      localWSSender.setOption("passwordCallbackRef", getPasswordCB(paramPolicyDetails, str2));
      str4 = paramPolicyDetails.getEncSymAlg();
      if (str4 != null)
      {
        if (str4.equals("AES_192"))
          localWSSender.setOption("encryptionSymAlgorithm", "http://www.w3.org/2001/04/xmlenc#aes192-cbc");
        if (str4.equals("AES_256"))
          localWSSender.setOption("encryptionSymAlgorithm", "http://www.w3.org/2001/04/xmlenc#aes256-cbc");
        if (str4.equals("Triple DES"))
          localWSSender.setOption("encryptionSymAlgorithm", "http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
      }
      if (str1.length() > 0)
        str1 = str1 + ' ' + "Encrypt";
      else
        str1 = "Encrypt";
    }
    else
    {
      mLog.warning("Unknown Response Security Action:" + paramPolicyDetails.getResponseSecurityAction());
    }
    localWSSender.setOption("action", str1);
    localWSSender.setOption("mustUnderstand", "false");
    if (paramPolicyDetails.getSignConfirm() == true)
      localWSSender.setOption("enableSignatureConfirmation", "false");
    else
      localWSSender.setOption("enableSignatureConfirmation", "true");
    return localWSSender;
  }

  static Handler getWSReceiver(PolicyDetails paramPolicyDetails)
    throws IOException
  {
    String str1 = "";
    String str2 = paramPolicyDetails.getRequestSecurityAction();
    WSReceiver localWSReceiver = new WSReceiver(paramPolicyDetails);
    if (str2.indexOf("Timestamp") > -1)
      str1 = "Timestamp";
    byte[] arrayOfByte;
    String str3;
    Properties localProperties;
    ByteArrayInputStream localByteArrayInputStream;
    if (str2.indexOf("Signature Encrypt") > -1)
    {
      if (str1.length() > 0)
        str1 = str1 + ' ' + "Signature Encrypt";
      else
        str1 = "Signature Encrypt";
      arrayOfByte = paramPolicyDetails.getPropBytes();
      str3 = new String(arrayOfByte);
      localProperties = new Properties();
      localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      localProperties.load(localByteArrayInputStream);
      localWSReceiver.setOption("SignaturePropRefId", localProperties);
      localWSReceiver.setOption("decryptionPropRefId", localProperties);
      localWSReceiver.setOption("passwordCallbackRef", getPasswordCB(paramPolicyDetails, str2));
      if (paramPolicyDetails.isHeaderEncFlagSet())
        localWSReceiver.setOption("encryptionParts", "{Element}{urn:" + paramPolicyDetails.getWebSvcNmSpace() + "}AuthenticationInfo;{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body");
    }
    else if (str2.indexOf("Signature") > -1)
    {
      if (str1.length() > 0)
        str1 = str1 + ' ' + "Signature";
      else
        str1 = "Signature";
      arrayOfByte = paramPolicyDetails.getPropBytes();
      str3 = new String(arrayOfByte);
      localProperties = new Properties();
      localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      localProperties.load(localByteArrayInputStream);
      localWSReceiver.setOption("SignaturePropRefId", localProperties);
      localWSReceiver.setOption("passwordCallbackRef", getPasswordCB(paramPolicyDetails, str2));
    }
    else if (str2.indexOf("Encrypt") > -1)
    {
      if (str1.length() > 0)
        str1 = str1 + ' ' + "Encrypt";
      else
        str1 = "Encrypt";
      arrayOfByte = paramPolicyDetails.getPropBytes();
      str3 = new String(arrayOfByte);
      localProperties = new Properties();
      localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      localProperties.load(localByteArrayInputStream);
      localWSReceiver.setOption("decryptionPropRefId", localProperties);
      localWSReceiver.setOption("passwordCallbackRef", getPasswordCB(paramPolicyDetails, str2));
      if (paramPolicyDetails.isHeaderEncFlagSet())
        localWSReceiver.setOption("encryptionParts", "{Element}{urn:" + paramPolicyDetails.getWebSvcNmSpace() + "}AuthenticationInfo;{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body");
    }
    else
    {
      mLog.warning("Unknown Request Security Action:" + paramPolicyDetails.getRequestSecurityAction());
    }
    localWSReceiver.setOption("action", str1);
    localWSReceiver.setOption("mustUnderstand", "false");
    return localWSReceiver;
  }

  private static CallbackHandler getPasswordCB(PolicyDetails paramPolicyDetails, String paramString)
  {
    String str = null;
    if (paramString.indexOf("Signature") > -1)
      str = paramPolicyDetails.getUserAliasPassword();
    else if (paramString.indexOf("Encrypt") > -1)
      str = paramPolicyDetails.getEncryptionUserAliasPassword();
    return new PWCallback(str);
  }

  static String getStringFromDocument(Document paramDocument)
  {
    try
    {
      DOMSource localDOMSource = new DOMSource(paramDocument);
      StringWriter localStringWriter = new StringWriter();
      StreamResult localStreamResult = new StreamResult(localStringWriter);
      TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
      Transformer localTransformer = localTransformerFactory.newTransformer();
      localTransformer.transform(localDOMSource, localStreamResult);
      return localStringWriter.toString();
    }
    catch (TransformerException localTransformerException)
    {
      mLog.warning("Exception in converting Document to String:" + localTransformerException);
    }
    return null;
  }

  static enum Action
  {
    TIMESTAMP, SIGN, ENCRYPT, SIGN_ENCRYPT, NONE;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.security.WSPolicyProcessor
 * JD-Core Version:    0.6.1
 */