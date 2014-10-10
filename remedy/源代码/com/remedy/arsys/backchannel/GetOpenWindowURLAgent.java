package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryListFieldInfo;
import com.bmc.arsys.api.QualifierInfo;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOpenWindowURLAgent extends NDXGetOpenWindowURL
{
  GetOpenWindowURLAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    ServerLogin localServerLogin = SessionData.get().getServerLogin(this.mServer);
    if ((this.mQualFieldIds.length != this.mQualFieldValues.length) || (this.mQualFieldIds.length != this.mQualFieldTypes.length))
      throw new GoatException("Badly formatted backchannel request (field arrays)");
    Object localObject1 = new ArrayList();
    Object localObject2;
    Object localObject3;
    Object localObject4;
    if ((this.mSuppressEmpty) || (this.mGetCount))
    {
      localObject2 = EntryListBase.buildEntryItems(this.mServer, this.mQualFieldIds, this.mQualFieldValues, this.mQualFieldTypes);
      localObject3 = null;
      if (this.mQualification.length() > 0)
      {
        localObject3 = Qualifier.ARDecodeARQualifierStruct(localServerLogin, this.mQualification);
        localObject4 = new ARQualifier((QualifierInfo)localObject3, (Entry)localObject2);
        localObject3 = ((ARQualifier)localObject4).getQualInfo();
      }
      localObject4 = Arrays.asList(new EntryListFieldInfo[] { new EntryListFieldInfo(1) });
      try
      {
        localObject1 = localServerLogin.getListEntry(this.mSchema, (QualifierInfo)localObject3, 0, 0, new ArrayList(), (List)localObject4, false, null);
      }
      catch (ARException localARException)
      {
        throw new GoatException(localARException);
      }
    }
    if (this.mGetParts)
    {
      localObject2 = new HashMap();
      ((Map)localObject2).put("server", "");
      ((Map)localObject2).put("app", "");
      ((Map)localObject2).put("form", "");
      ((Map)localObject2).put("view", "");
      ((Map)localObject2).put("cacheid", "");
      if ((((List)localObject1).size() > 0) || (!this.mSuppressEmpty))
        localObject2 = getURLParts(FormContext.get(), this.mServer, this.mSchema, this.mAppName, this.mView, this.mWinName, this.mWinArg);
      localObject3 = SessionData.get();
      localObject4 = FormContext.get();
      new Thread()
      {
        public void run()
        {
          SessionData.set(this.val$oSess);
          FormContext.set(this.val$fctx);
          GetQBETableEntryListAgent localGetQBETableEntryListAgent = new GetQBETableEntryListAgent(GetOpenWindowURLAgent.this.mServer, GetOpenWindowURLAgent.this.mSchema, GetOpenWindowURLAgent.this.mAppName, GetOpenWindowURLAgent.this.mQualification, GetOpenWindowURLAgent.this.mQualFieldIds, GetOpenWindowURLAgent.this.mQualFieldValues, GetOpenWindowURLAgent.this.mQualFieldTypes, GetOpenWindowURLAgent.this.mViewInfo, GetOpenWindowURLAgent.this.mSortOrder);
          try
          {
            localGetQBETableEntryListAgent.process();
            SessionData.get().putWindowQBE(GetOpenWindowURLAgent.this.mWinName, localGetQBETableEntryListAgent.toString());
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
      JSWriter localJSWriter = new JSWriter();
      localJSWriter.openObj().property("server", (String)((Map)localObject2).get("server")).property("app", (String)((Map)localObject2).get("app")).property("form", (String)((Map)localObject2).get("form")).property("view", (String)((Map)localObject2).get("view")).property("cacheid", (String)((Map)localObject2).get("cacheid")).closeObj();
      append("this.result=").openObj().property("n", ((List)localObject1).size()).append(",url:" + localJSWriter.toString());
    }
    else
    {
      localObject2 = "";
      if ((((List)localObject1).size() > 0) || (!this.mSuppressEmpty))
        localObject2 = getURL(FormContext.get(), this.mServer, this.mSchema, this.mAppName, this.mView, this.mWinName, this.mWinArg);
      localObject3 = SessionData.get();
      localObject4 = FormContext.get();
      new Thread()
      {
        public void run()
        {
          SessionData.set(this.val$oSess);
          FormContext.set(this.val$fctx);
          GetQBETableEntryListAgent localGetQBETableEntryListAgent = new GetQBETableEntryListAgent(GetOpenWindowURLAgent.this.mServer, GetOpenWindowURLAgent.this.mSchema, GetOpenWindowURLAgent.this.mAppName, GetOpenWindowURLAgent.this.mQualification, GetOpenWindowURLAgent.this.mQualFieldIds, GetOpenWindowURLAgent.this.mQualFieldValues, GetOpenWindowURLAgent.this.mQualFieldTypes, GetOpenWindowURLAgent.this.mViewInfo, GetOpenWindowURLAgent.this.mSortOrder);
          try
          {
            localGetQBETableEntryListAgent.process();
            SessionData.get().putWindowQBE(GetOpenWindowURLAgent.this.mWinName, localGetQBETableEntryListAgent.toString());
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
      append("this.result=").openObj().property("n", ((List)localObject1).size()).property("url", (String)localObject2);
    }
    closeObj().append(";");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetOpenWindowURLAgent
 * JD-Core Version:    0.6.1
 */