package com.remedy.arsys.goat.field;

import com.bmc.arsys.api.CurrencyDetail;
import com.bmc.arsys.api.CurrencyFieldLimit;
import com.bmc.arsys.api.CurrencyValue;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.DisplayPropertyMappers.BadDisplayPropertyException;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CurrencyField extends DataField
{
  private static final long serialVersionUID = -1238937092743043253L;
  private int mPrecision = 2;
  private boolean mHaveDefault;
  private BigDecimal mDefault;
  private String mDefaultCode;
  private String mDefaultCurrency;
  private CurrencyDetail[] mAllowable = new CurrencyDetail[0];
  private CurrencyDetail[] mFunctional = new CurrencyDetail[0];
  private BigDecimal mMax = new BigDecimal("99999999999999999999999999.99");
  private BigDecimal mMin = new BigDecimal("-99999999999999999999999999.99");
  private String mInitialCode;
  private static GoatImageButton mExpandButton = new GoatImageButton("currency");

  public GoatImageButton getExpandButton()
  {
    return mExpandButton;
  }

  public CurrencyDetail[] getAllowableCurrDetails()
  {
    return getMAllowable();
  }

  public String getExpandBoxAltText()
  {
    if (getMAccess() == 3L)
      return getLocalizedDescriptionStringForWidget("Functional currency list for {0},unavailable");
    return getLocalizedDescriptionStringForWidget("Functional currency list for {0}");
  }

  protected String getTitleForFieldWithMenuAndExpandBox()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with menu and functional currency");
  }

  protected String getTitleForFieldWithExpandBox()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    return MessageTranslation.getLocalizedText(SessionData.get().getLocale(), "{0} with functional currency");
  }

  protected String getTitleCodeForFieldWithMenuAndExpandBox()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    return "MFC";
  }

  protected String getTitleCodeForFieldWithExpandBox()
  {
    assert (FormContext.get().IsVoiceAccessibleUser());
    return "FC";
  }

  public CurrencyField(Form paramForm, Field paramField, int paramInt)
  {
    super(paramForm, paramField, paramInt);
    CurrencyFieldLimit localCurrencyFieldLimit = (CurrencyFieldLimit)paramField.getFieldLimit();
    if (localCurrencyFieldLimit != null)
    {
      setMPrecision(localCurrencyFieldLimit.getPrecision());
      if (getMPrecision() < 0)
        setMPrecision(2);
      setMFunctional((CurrencyDetail[])localCurrencyFieldLimit.getFunctional().toArray(new CurrencyDetail[0]));
      localObject = (CurrencyDetail[])localCurrencyFieldLimit.getAllowable().toArray(new CurrencyDetail[0]);
      setMAllowable(new CurrencyDetail[localObject.length]);
      for (int i = 0; i < localObject.length; i++)
        getMAllowable()[i] = localObject[i];
      if (getMAllowable().length > 0)
        setMDefaultCurrency(getMAllowable()[0].getCurrencyCode());
      Arrays.sort(getMAllowable(), new Comparator()
      {
        public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
        {
          CurrencyDetail localCurrencyDetail1 = (CurrencyDetail)paramAnonymousObject1;
          CurrencyDetail localCurrencyDetail2 = (CurrencyDetail)paramAnonymousObject2;
          return localCurrencyDetail1.getCurrencyCode().compareTo(localCurrencyDetail2.getCurrencyCode());
        }
      });
      setMMin(localCurrencyFieldLimit.getLowRange());
      setMMax(localCurrencyFieldLimit.getHighRange());
    }
    Object localObject = paramField.getDefaultValue();
    assert (localObject != null);
    if (((Value)localObject).getDataType().equals(DataType.CURRENCY))
    {
      CurrencyValue localCurrencyValue = (CurrencyValue)((Value)localObject).getValue();
      setMDefault(localCurrencyValue.getValue());
      setMDefaultCode(localCurrencyValue.getCurrencyCode());
      setMHaveDefault(true);
    }
  }

  protected void handleProperty(int paramInt, Value paramValue)
    throws DisplayPropertyMappers.BadDisplayPropertyException
  {
    if (paramInt == 245)
    {
      setMInitialCode(propToString(paramValue));
      MLog.fine(getMLForm().getFormName() + getMFieldID() + " AR_DPROP_INITIAL_CURRENCY_TYPE: " + getMInitialCode());
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

  public Object getSearchBarValue()
    throws GoatException
  {
    String str = getSearchBarLabel().replaceAll("'", "''");
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openList();
    localJSWriter.openObj();
    localJSWriter.property("l", "FIELD");
    localJSWriter.property("v", "'" + str + "'");
    localJSWriter.closeObj();
    localJSWriter.listSep();
    localJSWriter.openObj();
    localJSWriter.property("l", "VALUE");
    localJSWriter.property("v", "'" + str + ".VALUE'");
    localJSWriter.closeObj();
    localJSWriter.listSep();
    localJSWriter.openObj();
    localJSWriter.property("l", "TYPE");
    localJSWriter.property("v", "'" + str + ".TYPE'");
    localJSWriter.closeObj();
    localJSWriter.listSep();
    localJSWriter.openObj();
    localJSWriter.property("l", "DATE");
    localJSWriter.property("v", "'" + str + ".DATE'");
    localJSWriter.closeObj();
    for (int i = 0; i < getMFunctional().length; i++)
    {
      localJSWriter.listSep();
      localJSWriter.openObj();
      localJSWriter.property("l", getMFunctional()[i].getCurrencyCode());
      localJSWriter.property("v", "'" + str + "." + getMFunctional()[i].getCurrencyCode() + "'");
      localJSWriter.closeObj();
    }
    localJSWriter.closeList();
    return localJSWriter;
  }

  public int getAllowPrecision(String paramString)
  {
    for (int i = 0; i < getMAllowable().length; i++)
      if (getMAllowable()[i].getCurrencyCode().equals(paramString))
        return getMAllowable()[i].getPrecision();
    return 2;
  }

  void setMPrecision(int paramInt)
  {
    this.mPrecision = paramInt;
  }

  public int getMPrecision()
  {
    return this.mPrecision;
  }

  void setMHaveDefault(boolean paramBoolean)
  {
    this.mHaveDefault = paramBoolean;
  }

  public boolean isMHaveDefault()
  {
    return this.mHaveDefault;
  }

  void setMDefault(BigDecimal paramBigDecimal)
  {
    this.mDefault = paramBigDecimal;
  }

  public BigDecimal getMDefault()
  {
    return this.mDefault;
  }

  void setMDefaultCode(String paramString)
  {
    this.mDefaultCode = paramString;
  }

  public String getMDefaultCode()
  {
    return this.mDefaultCode;
  }

  void setMDefaultCurrency(String paramString)
  {
    this.mDefaultCurrency = paramString;
  }

  public String getMDefaultCurrency()
  {
    return this.mDefaultCurrency;
  }

  void setMAllowable(CurrencyDetail[] paramArrayOfCurrencyDetail)
  {
    this.mAllowable = paramArrayOfCurrencyDetail;
  }

  public CurrencyDetail[] getMAllowable()
  {
    return this.mAllowable;
  }

  void setMFunctional(CurrencyDetail[] paramArrayOfCurrencyDetail)
  {
    this.mFunctional = paramArrayOfCurrencyDetail;
  }

  public CurrencyDetail[] getMFunctional()
  {
    return this.mFunctional;
  }

  void setMMin(BigDecimal paramBigDecimal)
  {
    this.mMin = paramBigDecimal;
  }

  public BigDecimal getMMin()
  {
    return this.mMin;
  }

  void setMMax(BigDecimal paramBigDecimal)
  {
    this.mMax = paramBigDecimal;
  }

  public BigDecimal getMMax()
  {
    return this.mMax;
  }

  void setMInitialCode(String paramString)
  {
    this.mInitialCode = paramString;
  }

  public String getMInitialCode()
  {
    return this.mInitialCode;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.CurrencyField
 * JD-Core Version:    0.6.1
 */