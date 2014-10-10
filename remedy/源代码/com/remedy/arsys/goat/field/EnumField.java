package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.EnumItem;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.SelectionFieldLimit;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;
import java.util.List;

public class EnumField extends DataField
{
  private static final long serialVersionUID = 1566060168565094485L;
  public static final int DROPDOWN = 0;
  public static final int RADIO = 1;
  public static final int CHECKBOX = 2;
  private int mEnumType;
  private String[] mEnumLabels;
  private String[] mEnumValues;
  private SelectionFieldLimit mEnumLimitInfo;
  private long[] mEnumIds;
  private boolean mIsCustom;
  private int mDefaultValue;
  private String mRadioString;
  private String mLabelString;
  public static final GoatImageButton mSelectionButton = new GoatImageButton("menu");

  public EnumField(com.remedy.arsys.goat.Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    setMEnumLimitInfo((SelectionFieldLimit)paramField.getFieldLimit());
    assert (getMEnumLimitInfo() != null);
    int i = getMEnumLimitInfo().getListStyle();
    setMIsCustom(i == 2);
    EnumItem[] arrayOfEnumItem = (EnumItem[])getMEnumLimitInfo().getValues().toArray(new EnumItem[0]);
    setMEnumValues(new String[arrayOfEnumItem.length]);
    setMEnumLabels(new String[arrayOfEnumItem.length]);
    setMEnumIds(new long[arrayOfEnumItem.length]);
    for (int j = 0; j < arrayOfEnumItem.length; j++)
    {
      String str = arrayOfEnumItem[j].getEnumItemName();
      String tmp148_146 = str;
      getMEnumLabels()[j] = tmp148_146;
      getMEnumValues()[j] = tmp148_146;
      getMEnumIds()[j] = arrayOfEnumItem[j].getEnumItemNumber();
    }
    if (getMLabelString() != null)
    {
      localObject = getMLabelString().split("\\\\");
      if ((localObject.length != getMEnumValues().length * 2 + 1) || (localObject.length < 1))
        MLog.fine("Enum label string possibly wrong length. The number of enum labels does not match the values length, so taking only those that are present:" + getMLabelString() + " != " + getMEnumValues().length + ": Schema: " + paramForm.getSchema().getName() + " Field: " + paramField.getFieldID());
      for (int k = 1; k < localObject.length; k += 2)
        try
        {
          long l = Long.parseLong(localObject[k]);
          int m = idToIndex(l);
          if ((m != 2147483647) && (m >= 0) && (m < getMEnumLabels().length) && (k + 1 < localObject.length))
            getMEnumLabels()[m] = localObject[(k + 1)];
        }
        catch (NumberFormatException localNumberFormatException)
        {
          MLog.warning("Corrupted enum label string: " + localObject[k] + " --- using values instead: " + getMLabelString() + ": Schema: " + paramForm.getSchema() + " Field: " + paramField.getFieldID());
        }
      setMLabelString(null);
    }
    setMEnumType(0);
    if (getMRadioString() != null)
    {
      if (getMRadioString().equals("1"))
        setMEnumType(1);
      else if (getMRadioString().equals("2"))
        setMEnumType(2);
      setMRadioString(null);
    }
    setMDefaultValue(-1);
    Object localObject = paramField.getDefaultValue();
    assert (localObject != null);
    if (((Value)localObject).getDataType().equals(DataType.ENUM))
      setMDefaultValue(Integer.parseInt(((Value)localObject).getValue().toString()));
    if (getMRows() > getMEnumValues().length)
      setMRows(getMEnumValues().length);
    else if ((getMRows() == 0L) || (getMEnumType() == 2))
      setMRows(1L);
  }

  public int idToIndex(long paramLong)
  {
    assert ((getMEnumValues() != null) || (getMEnumLabels() != null) || (getMEnumIds() != null));
    for (int i = 0; i < getMEnumIds().length; i++)
      if (getMEnumIds()[i] == paramLong)
        return i;
    return 2147483647;
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 64)
    {
      setMRadioString(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_RADIO: " + getMRadioString());
    }
    else if (paramInt == 230)
    {
      setMLabelString(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_ENUM_LABELS: " + getMLabelString());
    }
    else
    {
      super.handleProperty(paramInt, paramValue);
    }
  }

  public String getForCodePrefix()
  {
    return "x-arid";
  }

  public String getDisplayTitleForField()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (getMEnumType() == 0)
      return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "ReadOnly {0} with value selectable menu");
    return null;
  }

  public String getDisplayTitleCodeForField()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (getMEnumType() == 0)
      return "MR";
    return "";
  }

  public String getARType()
  {
    if (getMEnumType() == 0)
      return getMDataTypeString() + "Sel";
    return getMDataTypeString();
  }

  public String getSearchBarLabel()
  {
    if ((getMFieldID() == 7) && (getMLabel() == null))
      return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "Status");
    return super.getSearchBarLabel();
  }

  public long enumIdFromLabel(String paramString)
  {
    if (getMEnumLabels() != null)
      for (int i = 0; i < getMEnumLabels().length; i++)
        if (getMEnumLabels()[i].equals(paramString))
          return getMEnumIds()[i];
    return -1L;
  }

  public long enumIdFromValues(String paramString)
  {
    if (getMEnumLabels() != null)
      for (int i = 0; i < getMEnumLabels().length; i++)
        if (getMEnumLabels()[i].equals(paramString))
          return getMEnumIds()[i];
    return -1L;
  }

  private int indexFromEnumId(int paramInt)
  {
    if (getMEnumIds() != null)
      for (int i = 0; i < getMEnumIds().length; i++)
        if (getMEnumIds()[i] == paramInt)
          return i;
    return -1;
  }

  public String valueFromEnumId(Value paramValue)
  {
    Integer localInteger = (Integer)paramValue.getValue();
    int i = indexFromEnumId(localInteger.intValue());
    if ((getMEnumValues() != null) && (i != -1))
      return getMEnumValues()[i];
    return "";
  }

  public String labelFromEnumId(int paramInt)
  {
    int i = indexFromEnumId(paramInt);
    if ((getMEnumLabels() != null) && (i != -1))
      return getMEnumLabels()[i];
    return "";
  }

  public String labelFromEnumId(Value paramValue)
  {
    Integer localInteger = (Integer)paramValue.getValue();
    int i = indexFromEnumId(localInteger.intValue());
    if ((getMEnumLabels() != null) && (i != -1))
      return getMEnumLabels()[i];
    return "";
  }

  private void setMEnumType(int paramInt)
  {
    this.mEnumType = paramInt;
  }

  public int getMEnumType()
  {
    return this.mEnumType;
  }

  public void setMEnumLabels(String[] paramArrayOfString)
  {
    this.mEnumLabels = paramArrayOfString;
  }

  public String[] getMEnumLabels()
  {
    return this.mEnumLabels;
  }

  public void setMEnumValues(String[] paramArrayOfString)
  {
    this.mEnumValues = paramArrayOfString;
  }

  public String[] getMEnumValues()
  {
    return this.mEnumValues;
  }

  public void setMEnumLimitInfo(SelectionFieldLimit paramSelectionFieldLimit)
  {
    this.mEnumLimitInfo = paramSelectionFieldLimit;
  }

  public SelectionFieldLimit getMEnumLimitInfo()
  {
    return this.mEnumLimitInfo;
  }

  private void setMEnumIds(long[] paramArrayOfLong)
  {
    this.mEnumIds = paramArrayOfLong;
  }

  public long[] getMEnumIds()
  {
    return this.mEnumIds;
  }

  private void setMIsCustom(boolean paramBoolean)
  {
    this.mIsCustom = paramBoolean;
  }

  public boolean isMIsCustom()
  {
    return this.mIsCustom;
  }

  protected void setMDefaultValue(int paramInt)
  {
    this.mDefaultValue = paramInt;
  }

  public int getMDefaultValue()
  {
    return this.mDefaultValue;
  }

  private void setMRadioString(String paramString)
  {
    this.mRadioString = paramString;
  }

  private String getMRadioString()
  {
    return this.mRadioString;
  }

  private void setMLabelString(String paramString)
  {
    this.mLabelString = paramString;
  }

  private String getMLabelString()
  {
    return this.mLabelString;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.EnumField
 * JD-Core Version:    0.6.1
 */