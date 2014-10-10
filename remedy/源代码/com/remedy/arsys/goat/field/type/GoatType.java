package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.AttachmentData.AttachmentDataKey;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import java.io.Serializable;

public abstract class GoatType
  implements Serializable
{
  public abstract int getOperandType();

  public abstract boolean isConstant();

  public abstract void emitJS(Emitter paramEmitter);

  public abstract String emitAR();

  public abstract int getDataType();

  public abstract Value toValue();

  public AttachmentData.AttachmentDataKey toAttachmentDataKey()
  {
    return null;
  }

  public abstract String toPrimitive();

  public String toJFieldPrimitive()
  {
    return toPrimitive();
  }

  public String forHTML()
  {
    return toPrimitive();
  }

  public String toString()
  {
    String str = getClass().getName();
    str = str.substring(str.lastIndexOf(".") + 1);
    return str + " " + getDataType() + " " + toPrimitive();
  }

  public void emitPrimitive(JSWriter paramJSWriter)
  {
    paramJSWriter.property("t", getDataType());
    paramJSWriter.property("v", toPrimitive());
  }

  public static String escapeQuotedString(String paramString)
  {
    assert (paramString != null);
    paramString = paramString.replaceAll("\\\\", "\\\\\\\\");
    paramString = paramString.replaceAll("\"", "\\\\\"");
    paramString = paramString.replaceAll("\n", "\\\\n");
    return paramString;
  }

  public GoatType bind(ServerLogin paramServerLogin, Field paramField)
    throws GoatException
  {
    return this;
  }

  public Integer toInteger()
  {
    return null;
  }

  public String toChar()
  {
    return null;
  }

  public Long toLong()
  {
    return null;
  }

  public boolean isNull()
  {
    return false;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.GoatType
 * JD-Core Version:    0.6.1
 */