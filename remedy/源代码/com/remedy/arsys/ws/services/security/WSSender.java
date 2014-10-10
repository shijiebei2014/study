package com.remedy.arsys.ws.services.security;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.security.auth.callback.CallbackHandler;
import org.apache.axis.MessageContext;
import org.apache.ws.axis.security.WSDoAllSender;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoBase;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.handler.RequestData;

class WSSender extends WSDoAllSender
{
  private static final long serialVersionUID = -8905511215599153916L;
  private PolicyDetails pd = null;

  WSSender(PolicyDetails paramPolicyDetails)
  {
    this.pd = paramPolicyDetails;
  }

  private Crypto loadCrypto(PolicyDetails paramPolicyDetails, Properties paramProperties)
    throws WSSecurityException
  {
    String str1 = paramProperties.getProperty("org.apache.ws.security.crypto.provider");
    if (str1 == null)
      throw new WSSecurityException("WSHandler: loadKeyStore: no crypto provider in properties file");
    Crypto localCrypto = CryptoFactory.getInstance(paramProperties);
    if (localCrypto == null)
      throw new WSSecurityException("WSHandler: Signature: CryptoFactory could not create a crypto");
    try
    {
      KeyStore localKeyStore = localCrypto.getKeyStore();
      String str2 = null;
      if (localKeyStore != null)
        str2 = localKeyStore.getType();
      if (str2 == null)
        localKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      else
        localKeyStore = KeyStore.getInstance(str2);
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramPolicyDetails.getKsBytes());
      String str3 = null;
      Enumeration localEnumeration = paramProperties.keys();
      while (localEnumeration.hasMoreElements())
      {
        String str4 = (String)localEnumeration.nextElement();
        if (str4.indexOf(".keystore.password") > 0)
          str3 = paramProperties.getProperty(str4);
      }
      if (str3 == null)
        localKeyStore.load(localByteArrayInputStream, null);
      else
        localKeyStore.load(localByteArrayInputStream, str3.toCharArray());
      ((CryptoBase)localCrypto).setKeyStore(localKeyStore);
    }
    catch (KeyStoreException localKeyStoreException)
    {
      throw new WSSecurityException("unable to create keystore", localKeyStoreException);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new WSSecurityException("no such algorithm", localNoSuchAlgorithmException);
    }
    catch (CertificateException localCertificateException)
    {
      throw new WSSecurityException("certificate exception", localCertificateException);
    }
    catch (IOException localIOException)
    {
      throw new WSSecurityException("io exception", localIOException);
    }
    return localCrypto;
  }

  public Crypto loadSignatureCrypto(RequestData paramRequestData)
    throws WSSecurityException
  {
    setPasswordCallBack(paramRequestData);
    Crypto localCrypto = null;
    String str = getString("signaturePropFile", paramRequestData.getMsgContext());
    if (str != null)
    {
      localCrypto = (Crypto)cryptos.get(str);
      try
      {
        localCrypto = CryptoFactory.getInstance(str, getURlClassLoader(paramRequestData));
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new WSSecurityException("Error loading Crypto instance", localMalformedURLException);
      }
    }
    else
    {
      Properties localProperties = (Properties)getOption("SignaturePropRefId");
      if (localProperties != null)
        localCrypto = loadCrypto(this.pd, localProperties);
      else
        throw new WSSecurityException("WSHandler: Signature: no crypto properties");
    }
    return localCrypto;
  }

  protected URLClassLoader getURlClassLoader(RequestData paramRequestData)
    throws MalformedURLException
  {
    File localFile = new File(this.pd.getKeystoreFilePath());
    URL localURL = localFile.toURI().toURL();
    return new URLClassLoader(new URL[] { localURL }, getClassLoader(paramRequestData.getMsgContext()));
  }

  public Crypto loadEncryptionCrypto(RequestData paramRequestData)
    throws WSSecurityException
  {
    setPasswordCallBack(paramRequestData);
    Crypto localCrypto = null;
    String str = getString("encryptionPropFile", paramRequestData.getMsgContext());
    if (str != null)
    {
      try
      {
        localCrypto = CryptoFactory.getInstance(str, getURlClassLoader(paramRequestData));
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new WSSecurityException("Error loading Crypto instance", localMalformedURLException);
      }
    }
    else
    {
      Properties localProperties = (Properties)getOption("encryptionPropRefId");
      if (localProperties != null)
        localCrypto = loadCrypto(this.pd, localProperties);
      else
        throw new WSSecurityException("WSHandler: Encryption: no crypto properties");
    }
    return localCrypto;
  }

  public Crypto loadDecryptionCrypto(RequestData paramRequestData)
    throws WSSecurityException
  {
    setPasswordCallBack(paramRequestData);
    Crypto localCrypto = null;
    String str = getString("decryptionPropFile", paramRequestData.getMsgContext());
    if (str != null)
    {
      try
      {
        localCrypto = CryptoFactory.getInstance(str, getURlClassLoader(paramRequestData));
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new WSSecurityException("Error loading Crypto instance", localMalformedURLException);
      }
    }
    else
    {
      Properties localProperties = (Properties)getOption("decryptionPropRefId");
      if (localProperties != null)
        localCrypto = loadCrypto(this.pd, localProperties);
      else
        throw new WSSecurityException("WSHandler: Decryption: no crypto properties");
    }
    return localCrypto;
  }

  protected CallbackHandler getPasswordCB(RequestData paramRequestData)
    throws WSSecurityException
  {
    return (CallbackHandler)getOption("passwordCallbackRef");
  }

  protected void setPasswordCallBack(RequestData paramRequestData)
  {
    Object localObject = getOption("passwordCallbackRef");
    if ((localObject != null) && ((localObject instanceof CallbackHandler)))
      ((MessageContext)paramRequestData.getMsgContext()).setProperty("passwordCallbackRef", localObject);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.security.WSSender
 * JD-Core Version:    0.6.1
 */