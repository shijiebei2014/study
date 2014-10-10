package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;

public class GetOpenWindowQueryVFAgent extends NDXRequest
{
  protected String mServer;
  protected String mSchema;
  protected String mAppName;
  protected String mQualification;
  protected int[] mQualFieldIds;
  protected String[] mQualFieldValues;
  protected int[] mQualFieldTypes;
  protected String mView;
  protected String mWinName;
  protected long[] mSortOrder;
  protected Form.ViewInfo mViewInfo;

  public GetOpenWindowQueryVFAgent(String paramString)
  {
    super(paramString);
  }

  protected void mapProperties(ArrayList paramArrayList)
    throws GoatException
  {
    MLog.fine("--> GetOpenWindowQueryVF");
    if (paramArrayList.size() != 10)
      throw new GoatException("Wrong argument length, spoofed");
    int i = 0;
    this.mServer = ((String)paramArrayList.get(i++));
    MLog.fine("mServer=" + this.mServer);
    this.mSchema = ((String)paramArrayList.get(i++));
    MLog.fine("mSchema=" + this.mSchema);
    this.mAppName = ((String)paramArrayList.get(i++));
    MLog.fine("mAppName=" + this.mAppName);
    this.mQualification = ((String)paramArrayList.get(i++));
    MLog.fine("mQualification=" + this.mQualification);
    this.mQualFieldIds = argToIntArray((String)paramArrayList.get(i++));
    StringBuilder localStringBuilder = new StringBuilder("mQualFieldIds=");
    for (int j = 0; j < this.mQualFieldIds.length; j++)
    {
      if (j > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldIds[j]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldValues = argToStringArray((String)paramArrayList.get(i++));
    localStringBuilder = new StringBuilder("mQualFieldValues=");
    for (j = 0; j < this.mQualFieldValues.length; j++)
    {
      if (j > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldValues[j]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mQualFieldTypes = argToIntArray((String)paramArrayList.get(i++));
    localStringBuilder = new StringBuilder("mQualFieldTypes=");
    for (j = 0; j < this.mQualFieldTypes.length; j++)
    {
      if (j > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mQualFieldTypes[j]);
    }
    MLog.fine(localStringBuilder.toString());
    this.mView = ((String)paramArrayList.get(i++));
    MLog.fine("mView=" + this.mView);
    this.mWinName = ((String)paramArrayList.get(i++));
    MLog.fine("mWinName=" + this.mWinName);
    this.mSortOrder = argToLongArray((String)paramArrayList.get(i++));
    localStringBuilder = new StringBuilder("mSortOrder=");
    for (j = 0; j < this.mSortOrder.length; j++)
    {
      if (j > 0)
        localStringBuilder.append(",");
      localStringBuilder.append(this.mSortOrder[j]);
    }
    MLog.fine(localStringBuilder.toString());
  }

  protected void process()
    throws GoatException
  {
    if ((this.mQualFieldIds.length != this.mQualFieldValues.length) || (this.mQualFieldIds.length != this.mQualFieldTypes.length))
      throw new GoatException("Badly formatted backchannel request (field arrays)");
    Form localForm = Form.get(this.mServer, this.mSchema);
    this.mViewInfo = localForm.getViewInfoByInference(this.mView, false, false);
    final SessionData localSessionData = SessionData.get();
    final FormContext localFormContext = FormContext.get();
    new Thread()
    {
      public void run()
      {
        SessionData.set(localSessionData);
        FormContext.set(localFormContext);
        GetQBETableEntryListAgent localGetQBETableEntryListAgent = new GetQBETableEntryListAgent(GetOpenWindowQueryVFAgent.this.mServer, GetOpenWindowQueryVFAgent.this.mSchema, GetOpenWindowQueryVFAgent.this.mAppName, GetOpenWindowQueryVFAgent.this.mQualification, GetOpenWindowQueryVFAgent.this.mQualFieldIds, GetOpenWindowQueryVFAgent.this.mQualFieldValues, GetOpenWindowQueryVFAgent.this.mQualFieldTypes, GetOpenWindowQueryVFAgent.this.mViewInfo, GetOpenWindowQueryVFAgent.this.mSortOrder);
        try
        {
          localGetQBETableEntryListAgent.process();
          SessionData.get().putWindowQBE(GetOpenWindowQueryVFAgent.this.mWinName, localGetQBETableEntryListAgent.toString());
        }
        catch (GoatException localGoatException)
        {
          localGoatException.printStackTrace();
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
        finally
        {
          SessionData.set(null);
          FormContext.set(null);
        }
      }
    }
    .start();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetOpenWindowQueryVFAgent
 * JD-Core Version:    0.6.1
 */