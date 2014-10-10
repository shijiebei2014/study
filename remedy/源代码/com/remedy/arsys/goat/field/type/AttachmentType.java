package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.backchannel.NDXRequest.Parser;
import com.remedy.arsys.goat.AttachmentData;
import com.remedy.arsys.goat.AttachmentData.AttachmentDataKey;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttachmentType extends GoatType
{
  protected AttachmentValue mValue;
  private AttachmentData.AttachmentDataKey mAttDataKey;
  private static final Pattern FILE_DOS_PATH_PARSER = Pattern.compile("[A-Za-z]:(\\\\([^\\\\]+))+$");

  public AttachmentType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
  {
    this.mValue = ((AttachmentValue)paramValue.getValue());
    String str1;
    if (paramServerLogin == null)
      str1 = "";
    else
      str1 = paramServerLogin.getServer();
    if (paramInt <= 0)
      paramInt = 0;
    String str2 = "";
    if (paramNode != null)
    {
      FieldGraph localFieldGraph = paramNode.getParentFieldGraph();
      str2 = localFieldGraph.getForm().getSchemaKey();
      assert (str2 != null);
    }
    this.mAttDataKey = new AttachmentData.AttachmentDataKey(str1, str2, paramString, paramInt, Long.toString(Calendar.getInstance().getTimeInMillis()), SessionData.get().getID());
  }

  public AttachmentType(Value paramValue, int paramInt)
  {
    this(paramValue, paramInt, (String)null, (ServerLogin)null, (FieldGraph.Node)null);
  }

  public AttachmentType(String paramString, int paramInt)
    throws GoatException
  {
    NDXRequest.Parser localParser = new NDXRequest.Parser(paramString);
    String str = localParser.next();
    if (str == null)
      return;
    this.mAttDataKey = new AttachmentData.AttachmentDataKey(str);
    AttachmentData localAttachmentData = AttachmentData.get(this.mAttDataKey);
    this.mValue = localAttachmentData.toAttachmentValue();
    str = localParser.next();
    long l = Long.parseLong(str);
    str = localParser.next();
    assert ((l == localAttachmentData.getSize()) && (localAttachmentData.getName().equals(str)));
  }

  public AttachmentType(AttachmentData.AttachmentDataKey paramAttachmentDataKey)
    throws GoatException
  {
    this.mAttDataKey = paramAttachmentDataKey;
    AttachmentData localAttachmentData = AttachmentData.get(this.mAttDataKey);
    this.mValue = localAttachmentData.toAttachmentValue();
  }

  public int getDataType()
  {
    return 11;
  }

  public int getOperandType()
  {
    return 1;
  }

  public boolean isConstant()
  {
    return true;
  }

  public void emitJS(Emitter paramEmitter)
  {
    paramEmitter.js().append("new AttachmentType(").appendqs(toPrimitive()).append(")");
  }

  public String emitAR()
  {
    return escapeQuotedString("\"" + this.mValue.getName() + "\"");
  }

  public Value toValue()
  {
    return new Value(this.mValue);
  }

  public AttachmentData.AttachmentDataKey toAttachmentDataKey()
  {
    return this.mAttDataKey;
  }

  public String toPrimitive()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    String str = this.mAttDataKey.toStringFormat();
    localStringBuilder.append(str.length()).append('/').append(str);
    str = this.mValue.getOriginalSize() + "";
    localStringBuilder.append(str.length()).append('/').append(str);
    str = this.mValue.getName();
    if (str == null)
      str = "";
    localStringBuilder.append(str.length()).append('/').append(str);
    return localStringBuilder.toString();
  }

  public String forHTML()
  {
    String str = this.mValue.getName();
    Matcher localMatcher = FILE_DOS_PATH_PARSER.matcher(str);
    return localMatcher.find() ? localMatcher.group(2) : str;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.AttachmentType
 * JD-Core Version:    0.6.1
 */