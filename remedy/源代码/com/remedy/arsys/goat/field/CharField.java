package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.CharacterFieldLimit;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.FieldMapping;
import com.bmc.arsys.api.JoinFieldMapping;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.OutputNotes;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.goat.menu.Menu.MKey;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;

public class CharField extends DataField
{
  private static final long serialVersionUID = 5256127814425847377L;
  private int mMaxLength = -1;
  private int mMenuStyle = 1;
  private int mAutoCompleteStyle;
  private boolean mEnableClear;
  private int mAutoCompleteMatchBy;
  private int mAutoCompleteAfterKeyStrokes;
  private int mExternalURLFieldID;
  private int mQBEMatch = -1;
  private String mCharMenu;
  private String mCharPattern;
  private int mFullTextOption = -1;
  private String mDefaultValue;
  private int mDisplayType;
  private boolean mShowURL;
  private int mAttachmentFieldID;
  private int mJoinIndex;
  private int mAutoSize;
  private static final GoatImageButton MDefaultRTFButton = new GoatImageButton("rtf");

  public CharField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    CharacterFieldLimit localCharacterFieldLimit = (CharacterFieldLimit)paramField.getFieldLimit();
    if (localCharacterFieldLimit != null)
    {
      setMMaxLength(localCharacterFieldLimit.getMaxLength());
      setMMenuStyle(localCharacterFieldLimit.getMenuStyle());
      setMQBEMatch(localCharacterFieldLimit.getQBEMatch());
      setMCharMenu(localCharacterFieldLimit.getCharMenu().toString());
      if ((getMCharMenu() != null) && (getMCharMenu().trim().length() == 0))
        setMCharMenu(null);
      setMCharPattern(localCharacterFieldLimit.getPattern());
      setMFullTextOption(localCharacterFieldLimit.getFullTextOption());
    }
    Value localValue = paramField.getDefaultValue();
    assert (localValue != null);
    if (!DataType.NULL.equals(localValue.getDataType()))
      setMDefaultValue(localValue.toString());
    if (getMDisplayType() == 1)
    {
      setMMenuStyle(2);
      if (getMMenuARBox() != null)
        getMMenuARBox().setW(100);
    }
    if ((getMFieldID() == 102) || (getMFieldID() == 123) || (getMDisplayType() == 2))
    {
      setMDisplayType(2);
      setMExpandARBox(null);
    }
    if (getMRows() <= 0L)
      setMRows(1L);
    if ((paramField != null) && (paramField.getFieldMap() != null) && (paramField.getFieldMap().getMappingType() == 2))
      this.mJoinIndex = ((JoinFieldMapping)paramField.getFieldMap()).getIndex();
    else
      this.mJoinIndex = -1;
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 67)
    {
      setMDisplayType(propToInt(paramValue));
    }
    else if (paramInt == 66)
    {
      setMExpandARBox(propToBox(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_DATA_EXPAND_BBOX: " + getMExpandARBox());
    }
    else if (paramInt == 69)
    {
      setMAutoCompleteMatchBy(propToInt(paramValue));
    }
    else if (paramInt == 68)
    {
      setMAutoCompleteStyle(propToInt(paramValue));
    }
    else if (paramInt == 70)
    {
      setMEnableClear(propToBool(paramValue));
    }
    else if (paramInt == 5116)
    {
      setMShowURL(propToBool(paramValue));
    }
    else if (paramInt == 5201)
    {
      setMAttachmendFieldID(propToInt(paramValue));
    }
    else if (paramInt == 323)
    {
      setMAutoSize(propToInt(paramValue));
    }
    else if (paramInt == 328)
    {
      setMAutoCompleteAfterKeyStrokes(propToInt(paramValue));
    }
    else if (paramInt == 336)
    {
      setMExternalURLFieldID(propToInt(paramValue));
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
    if (FormContext.get().IsVoiceAccessibleUser())
    {
      if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMCharMenu() != null) && (!getMCharMenu().equals("$NULL$")) && (getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton() != null))
        return getTitleForFieldWithMenuAndExpandBox();
      if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMCharMenu() != null) && (!getMCharMenu().equals("$NULL$")))
        return getTitleForFieldWithMenu();
      if ((getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton() != null))
      {
        if (getMDisplayType() == 3)
          return getTitleForFileInputField();
        if ((getMDisplayType() == 4) || (getMDisplayType() == 5))
          return getTitleForRTFInputField();
        return getTitleForFieldWithExpandBox();
      }
    }
    return null;
  }

  public String getDisplayTitleCodeForField()
  {
    if (FormContext.get().IsVoiceAccessibleUser())
    {
      if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMCharMenu() != null) && (!getMCharMenu().equals("$NULL$")) && (getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton() != null))
        return getTitleCodeForFieldWithMenuAndExpandBox();
      if ((getMMenuARBox() != null) && (getMMenuARBox().getW() > 0) && (getMCharMenu() != null) && (!getMCharMenu().equals("$NULL$")))
        return getTitleCodeForFieldWithMenu();
      if ((getMExpandARBox() != null) && (getMExpandARBox().getW() > 0) && (getExpandButton() != null))
      {
        if (getMDisplayType() == 3)
          return getTitleCodeForFileInputField();
        return getTitleCodeForFieldWithExpandBox();
      }
    }
    return "";
  }

  protected String getTitleForFieldWithMenuAndExpandBox()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (getMDisplayType() == 1)
      return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "ReadOnly {0} with value selectable menu and expand");
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with menu and expand");
  }

  protected String getTitleForFieldWithMenu()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (getMDisplayType() == 1)
      return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "ReadOnly {0} with value selectable menu");
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with menu");
  }

  protected String getTitleForFileInputField()
  {
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} File Path");
  }

  protected String getTitleForRTFInputField()
  {
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} Rich Text with expand");
  }

  protected String getTitleCodeForFileInputField()
  {
    return "FP";
  }

  protected String getTitleCodeForFieldWithMenuAndExpandBox()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (getMDisplayType() == 1)
      return "MER";
    return "ME";
  }

  protected String getTitleCodeForFieldWithMenu()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    if (getMDisplayType() == 1)
      return "MR";
    return "M";
  }

  protected void addMenuToOutputNotes(OutputNotes paramOutputNotes)
  {
    paramOutputNotes.addFieldMenus(getMCharMenu());
  }

  public void addToOutputNotes(OutputNotes paramOutputNotes, int paramInt)
  {
    super.addToOutputNotes(paramOutputNotes, paramInt);
    if ((paramInt == 0) && (getMCharMenu() != null))
      addMenuToOutputNotes(paramOutputNotes);
  }

  public void setMMaxLength(int paramInt)
  {
    this.mMaxLength = paramInt;
  }

  public int getMMaxLength()
  {
    return this.mMaxLength;
  }

  protected void setMMenuStyle(int paramInt)
  {
    this.mMenuStyle = paramInt;
  }

  public int getMMenuStyle()
  {
    return this.mMenuStyle;
  }

  protected void setMAutoCompleteStyle(int paramInt)
  {
    this.mAutoCompleteStyle = paramInt;
  }

  public int getMAutoCompleteStyle()
  {
    return this.mAutoCompleteStyle;
  }

  void setMEnableClear(boolean paramBoolean)
  {
    this.mEnableClear = paramBoolean;
  }

  public boolean isMEnableClear()
  {
    return this.mEnableClear;
  }

  protected void setMAutoCompleteMatchBy(int paramInt)
  {
    this.mAutoCompleteMatchBy = paramInt;
  }

  public int getMAutoCompleteMatchBy()
  {
    return this.mAutoCompleteMatchBy;
  }

  public int getMAutoCompleteAfterKeyStrokes()
  {
    return this.mAutoCompleteAfterKeyStrokes;
  }

  public void setMAutoCompleteAfterKeyStrokes(int paramInt)
  {
    this.mAutoCompleteAfterKeyStrokes = paramInt;
  }

  protected void setMQBEMatch(int paramInt)
  {
    this.mQBEMatch = paramInt;
  }

  public int getMQBEMatch()
  {
    return this.mQBEMatch;
  }

  protected void setMCharMenu(String paramString)
  {
    this.mCharMenu = paramString;
  }

  public String getMCharMenu()
  {
    return this.mCharMenu;
  }

  protected void setMCharPattern(String paramString)
  {
    this.mCharPattern = paramString;
  }

  public String getMCharPattern()
  {
    return this.mCharPattern;
  }

  protected void setMFullTextOption(int paramInt)
  {
    this.mFullTextOption = paramInt;
  }

  public int getMFullTextOption()
  {
    return this.mFullTextOption;
  }

  protected void setMDefaultValue(String paramString)
  {
    this.mDefaultValue = paramString;
  }

  public String getMDefaultValue()
  {
    return this.mDefaultValue;
  }

  protected void setMDisplayType(int paramInt)
  {
    this.mDisplayType = paramInt;
  }

  public int getMDisplayType()
  {
    return this.mDisplayType;
  }

  public boolean isMShowURL()
  {
    return this.mShowURL;
  }

  public boolean isMRichTextField()
  {
    return (getMDisplayType() == 4) || (getMDisplayType() == 5);
  }

  public void setMShowURL(boolean paramBoolean)
  {
    this.mShowURL = paramBoolean;
  }

  public int getMAttachmentFieldID()
  {
    return this.mAttachmentFieldID;
  }

  public void setMAttachmendFieldID(int paramInt)
  {
    this.mAttachmentFieldID = paramInt;
  }

  void setMAutoSize(int paramInt)
  {
    this.mAutoSize = paramInt;
  }

  public int getMAutoSize()
  {
    return this.mAutoSize;
  }

  public GoatImageButton getRichTextEditorButton()
  {
    return MDefaultRTFButton;
  }

  public String getRichTextEditorClassString()
  {
    return "richtext";
  }

  public int getMJoinIndex()
  {
    return this.mJoinIndex;
  }

  public Menu getMenu()
    throws GoatException
  {
    return Menu.get(new Menu.MKey(getServer(), SessionData.get().getLocale(), getMCharMenu()));
  }

  public int getMExternalURLFieldID()
  {
    return this.mExternalURLFieldID;
  }

  protected void setMExternalURLFieldID(int paramInt)
  {
    this.mExternalURLFieldID = paramInt;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.CharField
 * JD-Core Version:    0.6.1
 */