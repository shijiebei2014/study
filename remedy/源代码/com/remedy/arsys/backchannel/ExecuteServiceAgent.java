package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.AttachmentValue;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.AttachmentData;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.type.GoatType;
import com.remedy.arsys.goat.field.type.GoatTypeFactory;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class ExecuteServiceAgent extends NDXExecuteService
{
  private ServerLogin mServerUser;

  public ExecuteServiceAgent(String paramString)
  {
    super(paramString);
  }

  private Entry buildEntry(int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2)
    throws GoatException
  {
    int i = paramArrayOfInt1.length;
    Entry localEntry = new Entry();
    for (int k = 0; k < i; k++)
    {
      int j = paramArrayOfInt2[k];
      if ((j == 11) && (paramArrayOfString[k].length() == 0))
        j = 0;
      GoatType localGoatType = GoatTypeFactory.create(paramArrayOfString[k], j, paramArrayOfInt1[k]);
      localEntry.put(Integer.valueOf(paramArrayOfInt1[k]), localGoatType.toValue());
    }
    return localEntry;
  }

  private JSWriter writeoa(Entry paramEntry)
    throws GoatException
  {
    Form localForm1 = Form.get(this.mServer, this.mForm);
    FieldGraph localFieldGraph1 = FieldGraph.get(localForm1.getViewInfoByInference(null, false, false));
    Form localForm2 = Form.get(this.mServer, this.mSourceForm);
    FieldGraph localFieldGraph2 = FieldGraph.get(localForm2.getViewInfoByInference(null, false, false));
    JSWriter localJSWriter = new JSWriter();
    String str1 = this.mArrName + ".SV(";
    String str2 = ")";
    AttachmentValue localAttachmentValue = null;
    for (int i = 0; i < this.mOutRefs.length; i++)
    {
      Value localValue = (Value)paramEntry.get(Integer.valueOf(this.mOutRefs[i]));
      localJSWriter.append(str1).append(this.mOutRefs[i]).append(",");
      localJSWriter.openObj();
      if (localValue == null)
      {
        localJSWriter.property("t", 0);
        MLog.fineAndConsole("Service: entry did not return value for field " + this.mOutRefs[i]);
      }
      else
      {
        localAttachmentValue = null;
        FieldGraph.Node localNode;
        if (localValue.getDataType() == DataType.ATTACHMENT)
        {
          localNode = localFieldGraph2.getNode(this.mOutRefs[i]);
          localAttachmentValue = (AttachmentValue)localValue.getValue();
        }
        else
        {
          localNode = localFieldGraph1.getNode(this.mOutRefs[i]);
        }
        GoatType localGoatType = GoatTypeFactory.create(localValue, this.mOutRefs[i], null, this.mServerUser, localNode);
        if (localAttachmentValue != null)
          try
          {
            AttachmentData.uploadToField(localGoatType.toAttachmentDataKey(), localAttachmentValue.getName().substring(localAttachmentValue.getName().lastIndexOf("\\") + 1), new ByteArrayInputStream(localAttachmentValue.getValue()));
          }
          catch (IOException localIOException)
          {
            MLog.fineAndConsole("IOException occured for an attachment: " + this.mOutRefs[i]);
          }
        localGoatType.emitPrimitive(localJSWriter);
      }
      localJSWriter.closeObj();
      localJSWriter.append(str2).append(";");
    }
    return localJSWriter;
  }

  private JSWriter generateResult(Entry paramEntry)
    throws GoatException
  {
    JSWriter localJSWriter1 = new JSWriter();
    JSWriter localJSWriter2 = writeoa(paramEntry);
    localJSWriter1.openObj();
    localJSWriter1.property("s", "null");
    localJSWriter1.property("oa", localJSWriter2.toString());
    localJSWriter1.closeObj();
    return localJSWriter1;
  }

  protected void process()
    throws GoatException
  {
    try
    {
      this.mServerUser = SessionData.get().getServerLogin(this.mServer);
    }
    catch (GoatException localGoatException1)
    {
      MLog.fineAndConsole("Service: goat exception getServerLogin");
      append("this.result=0");
      throw localGoatException1;
    }
    String str = this.mRequestidStr;
    if (str.length() == 0)
      str = null;
    Entry localEntry1 = null;
    try
    {
      if (this.mInIds.length > 0)
        localEntry1 = buildEntry(this.mInIds, this.mInValues, this.mInTypes);
    }
    catch (GoatException localGoatException2)
    {
      MLog.fineAndConsole("Service: goat exception building input entry");
      append("this.result=0");
      throw localGoatException2;
    }
    Entry localEntry2 = null;
    try
    {
      localEntry2 = this.mServerUser.executeService(this.mForm, str, localEntry1, this.mOutRefs.length == 0 ? null : this.mOutRefs);
    }
    catch (ARException localARException)
    {
      MLog.fineAndConsole("Service: exception calling executeservice");
      append("this.result=0");
      throw new GoatException(localARException);
    }
    List localList = this.mServerUser.getLastStatus();
    for (int i = 0; i < localList.size(); i++)
    {
      StatusInfo localStatusInfo = (StatusInfo)localList.get(i);
      if (localStatusInfo.getMessageNum() == 51L)
        localList.remove(i);
    }
    this.mStatus = localList;
    this.mConvertIdToLabel = false;
    try
    {
      JSWriter localJSWriter = generateResult(localEntry2);
      append("this.result=");
      append(localJSWriter.toString());
    }
    catch (GoatException localGoatException3)
    {
      MLog.fineAndConsole("Service: exception generating result");
      append("this.result=0");
      throw localGoatException3;
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.ExecuteServiceAgent
 * JD-Core Version:    0.6.1
 */