package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.AttachmentFieldLimit;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;

public class AttachmentField extends GoatField
{
  private static final long serialVersionUID = -4765696401951863594L;
  private long mSizeLimit;
  private boolean mDisableChange;

  public AttachmentField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    setMEmitViewable(setMEmitOptimised(1));
    setMParentExecDep(true);
    AttachmentFieldLimit localAttachmentFieldLimit = (AttachmentFieldLimit)paramField.getFieldLimit();
    if (localAttachmentFieldLimit != null)
      setMSizeLimit(localAttachmentFieldLimit.getMaxSize());
    if (getMLabel() == null)
      setMLabel("");
    if (getMParentFieldID() == 0)
      setMParentFieldID(-1);
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 228)
      setMDisableChange(propToBool(paramValue));
    else
      super.handleProperty(paramInt, paramValue);
  }

  public boolean hasSearchBarMenu()
  {
    return (isMInView()) && ((getMFieldID() == 178) || (getMFieldOption() != 4));
  }

  public String getSearchBarLabel()
  {
    String str = "";
    if ((getMLabel() != null) && (getMLabel().length() > 0))
      str = getMLabel();
    if (str.trim().length() == 0)
      str = "" + getMFieldID();
    return str;
  }

  public boolean isDataField()
  {
    return true;
  }

  protected void setMSizeLimit(long paramLong)
  {
    this.mSizeLimit = paramLong;
  }

  public long getMSizeLimit()
  {
    return this.mSizeLimit;
  }

  protected void setMDisableChange(boolean paramBoolean)
  {
    this.mDisableChange = paramBoolean;
  }

  public boolean isMDisableChange()
  {
    return this.mDisableChange;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.AttachmentField
 * JD-Core Version:    0.6.1
 */