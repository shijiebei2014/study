package com.remedy.arsys.ws.services.security;

class PolicyDetails
{
  private String policyId;
  private String requestSecurityAction;
  private String responseSecurityAction;
  private String propFileName;
  private String propFilePath;
  private String keystoreFileName;
  private String keystoreFilePath;
  private String userAlias;
  private String userAliasPassword;
  private String encryptionUserAlias;
  private String encryptionUserAliasPassword;
  private byte[] propBytes;
  private byte[] ksBytes;
  private String encSymAlg = null;
  private boolean signConfirm = false;
  private String signKeyID = "DirectReference";
  private String encKeyID = "DirectReference";
  private String signAlg;
  private String encryptAlg;
  private boolean headerEncFlag = false;
  private String webSvcNmSpace;
  static final String CONST_KEY_ID_SERIAL = "IssuerSerial";
  static final String CONST_KEY_ID_DIRECT_REF = "DirectReference";

  public String getPolicyId()
  {
    return this.policyId;
  }

  public void setPolicyId(String paramString)
  {
    this.policyId = paramString;
  }

  public String getRequestSecurityAction()
  {
    return this.requestSecurityAction;
  }

  public void setRequestSecurityAction(String paramString)
  {
    this.requestSecurityAction = paramString;
  }

  public String getResponseSecurityAction()
  {
    return this.responseSecurityAction;
  }

  public void setResponseSecurityAction(String paramString)
  {
    this.responseSecurityAction = paramString;
  }

  public String getPropFilePath()
  {
    return this.propFilePath;
  }

  public void setPropFilePath(String paramString)
  {
    this.propFilePath = paramString;
  }

  public String getKeystoreFilePath()
  {
    return this.keystoreFilePath;
  }

  public void setKeystoreFilePath(String paramString)
  {
    this.keystoreFilePath = paramString;
  }

  public String getSignKeyID()
  {
    return this.signKeyID;
  }

  public void setSignKeyID(String paramString)
  {
    this.signKeyID = paramString;
  }

  public String getEncKeyID()
  {
    return this.encKeyID;
  }

  public void setEncKeyID(String paramString)
  {
    this.encKeyID = paramString;
  }

  public String getSignAlg()
  {
    return this.signAlg;
  }

  boolean isHeaderEncFlagSet()
  {
    return this.headerEncFlag;
  }

  void setHeaderEncFlag(boolean paramBoolean)
  {
    this.headerEncFlag = paramBoolean;
  }

  String getWebSvcNmSpace()
  {
    return this.webSvcNmSpace;
  }

  void setWebSvcNmSpace(String paramString)
  {
    this.webSvcNmSpace = paramString;
  }

  public void setSignAlg(String paramString)
  {
    this.signAlg = paramString;
  }

  public String getEncryptAlg()
  {
    return this.encryptAlg;
  }

  public void setEncryptAlg(String paramString)
  {
    this.encryptAlg = paramString;
  }

  public String getUserAlias()
  {
    return this.userAlias;
  }

  public void setUserAlias(String paramString)
  {
    this.userAlias = paramString;
  }

  public String getEncryptionUserAlias()
  {
    return this.encryptionUserAlias;
  }

  public void setEncryptionUserAlias(String paramString)
  {
    this.encryptionUserAlias = paramString;
  }

  public String getUserAliasPassword()
  {
    return this.userAliasPassword;
  }

  public void setUserAliasPassword(String paramString)
  {
    this.userAliasPassword = paramString;
  }

  public String getEncryptionUserAliasPassword()
  {
    return this.encryptionUserAliasPassword;
  }

  public void setEncryptionUserAliasPassword(String paramString)
  {
    this.encryptionUserAliasPassword = paramString;
  }

  public String getPropFileName()
  {
    return this.propFileName;
  }

  public void setPropFileName(String paramString)
  {
    this.propFileName = paramString;
  }

  public String getKeystoreFileName()
  {
    return this.keystoreFileName;
  }

  public void setKeystoreFileName(String paramString)
  {
    this.keystoreFileName = paramString;
  }

  void setPropBytes(byte[] paramArrayOfByte)
  {
    this.propBytes = paramArrayOfByte;
  }

  byte[] getPropBytes()
  {
    return this.propBytes;
  }

  void setKsBytes(byte[] paramArrayOfByte)
  {
    this.ksBytes = paramArrayOfByte;
  }

  byte[] getKsBytes()
  {
    return this.ksBytes;
  }

  void setEncSymAlg(String paramString)
  {
    this.encSymAlg = paramString;
  }

  String getEncSymAlg()
  {
    return this.encSymAlg;
  }

  void setSignConfirm(boolean paramBoolean)
  {
    this.signConfirm = paramBoolean;
  }

  boolean getSignConfirm()
  {
    return this.signConfirm;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Policy Details[").append("policyId:").append(this.policyId).append("\nrequestSecurityAction:").append(this.requestSecurityAction).append("\nresponseSecurityAction:").append(this.responseSecurityAction).append("\npropFileName:").append(this.propFileName).append("\npropFilePath:").append(this.propFilePath).append("\nkeystoreFileName:").append(this.keystoreFileName).append("\nkeystoreFilePath:").append(this.keystoreFilePath).append("\nheaderEncFlag:").append(this.headerEncFlag).append("\nuserAlias:").append(this.userAlias).append("\nencryptionUserAlias:").append(this.encryptionUserAlias).append("\nuserAliasPassword:");
    if (this.userAliasPassword != null)
      localStringBuilder.append("******");
    localStringBuilder.append("\nencryptionUserAliasPassword:");
    if (this.encryptionUserAliasPassword != null)
      localStringBuilder.append("******");
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.ws.services.security.PolicyDetails
 * JD-Core Version:    0.6.1
 */