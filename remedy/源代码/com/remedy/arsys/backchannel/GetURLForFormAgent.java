package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.RSAAuthenticator;
import com.remedy.arsys.support.InetAddressUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GetURLForFormAgent extends NDXGetURLForForm
{
  public static final String REDIRTOFEDSRVSERVLET_BASE_URI = "servlet/RedirToFedSrvServlet?";
  public static final String HTTP_PROTOCOL = "http";
  public static final String HTTPS_PROTOCOL = "https";

  public GetURLForFormAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    FormContext localFormContext = FormContext.get();
    List localList1 = Configuration.getInstance().getServers();
    List localList2 = Configuration.getInstance().getShortServers();
    int i = 0;
    String str1 = null;
    this.mServer = this.mServer.trim().toLowerCase();
    if ((!localList1.contains(this.mServer)) && (!localList2.contains(this.mServer)))
    {
      i = 1;
      this.mIsinline = false;
    }
    if (this.mIsinline)
    {
      append("this.result=").openObj();
      getURLInfoforInline(localFormContext, this.mServer, this.mForm, this.mAppName, this.mView);
      closeObj().append(";");
    }
    else
    {
      int j = 0;
      String str2 = null;
      RSAAuthenticator localRSAAuthenticator = null;
      Entry localEntry = null;
      Object localObject1;
      Object localObject2;
      if (i != 0)
      {
        localObject1 = Configuration.getInstance().getPreferenceServers();
        if ((localObject1 == null) || (((List)localObject1).isEmpty()))
          throw new GoatException(9287);
        str1 = this.mServer;
        localObject2 = ((List)localObject1).iterator();
        while ((((Iterator)localObject2).hasNext()) && (j == 0))
        {
          str2 = (String)((Iterator)localObject2).next();
          Object localObject3;
          if ((!localList1.contains(str2)) && (!localList2.contains(str2)))
          {
            localObject3 = new GoatException(9280, str2);
            ((GoatException)localObject3).setConvertIdToLabel(false);
            throw ((Throwable)localObject3);
          }
          localRSAAuthenticator = new RSAAuthenticator(str2);
          try
          {
            localEntry = localRSAAuthenticator.getPublicKeyEntry(str1);
            if (localEntry != null)
            {
              if (localEntry == null)
                throw new GoatException(9282, str1);
              if (!InetAddressUtil.isHostReachable(str1))
                throw new GoatException(9290, str1);
              if ((localObject3 = (Value)localEntry.get(Integer.valueOf(RSAAuthenticator.SERVER_KEY_MAP_SCHEMA_KEY_IDS[0]))) == null)
                throw new GoatException(9283, str1);
              String str3 = (String)((Value)localObject3).getValue();
              if ((str3 == null) || (str3.length() == 0))
                throw new GoatException(9283, str1);
              Value localValue;
              if ((localValue = (Value)localEntry.get(Integer.valueOf(RSAAuthenticator.SERVER_KEY_MAP_SCHEMA_KEY_IDS[2]))) == null)
                throw new GoatException(9284, str1);
              String str4 = (String)localValue.getValue();
              if ((str4 == null) || (str4.length() == 0))
                throw new GoatException(9284, str1);
              try
              {
                URL localURL = new URL(str4);
                String str5 = localURL.getProtocol();
                if ((!str5.equals("http")) && (!str5.equals("https")))
                  throw new GoatException(9289, str1);
              }
              catch (MalformedURLException localMalformedURLException)
              {
                throw new GoatException(9288, str1);
              }
              j = 1;
            }
          }
          catch (GoatException localGoatException)
          {
            localGoatException.printStackTrace();
            throw localGoatException;
          }
          catch (Exception localException)
          {
            localException.printStackTrace();
            throw new GoatException(9282, str1);
          }
        }
      }
      if ((this.mAppName != null) && (this.mForm.length() == 0))
      {
        localFormContext.setApplication(this.mAppName);
        if (this.mGetParts)
        {
          localObject1 = new JSWriter();
          ((JSWriter)localObject1).openObj().property("server", this.mServer).property("app", this.mAppName).property("form", "").property("view", "").closeObj();
          append("this.result=").openObj().append("url:" + ((JSWriter)localObject1).toString()).closeObj().append(";");
        }
        else
        {
          append("this.result=").openObj().property("url", localFormContext.getContextURL() + localFormContext.getFormURL(this.mServer)).closeObj().append(";");
        }
        return;
      }
      if (this.mGetParts)
      {
        localObject1 = getURLParts(localFormContext, this.mServer, this.mForm, this.mAppName, this.mView, this.mWinName, this.mWinArg);
        localObject2 = new JSWriter();
        ((JSWriter)localObject2).openObj().property("server", (String)((Map)localObject1).get("server")).property("app", (String)((Map)localObject1).get("app")).property("form", (String)((Map)localObject1).get("form")).property("view", (String)((Map)localObject1).get("view")).property("cacheid", (String)((Map)localObject1).get("cacheid")).closeObj();
        append("this.result=").openObj().append("url:" + ((JSWriter)localObject2).toString());
      }
      else if (j != 0)
      {
        localObject1 = new StringBuilder(localFormContext.getContextURL());
        if (((StringBuilder)localObject1).charAt(((StringBuilder)localObject1).length() - 1) != '/')
          ((StringBuilder)localObject1).append('/');
        ((StringBuilder)localObject1).append("servlet/RedirToFedSrvServlet?");
        if ((str2 != null) && (str2.length() > 0))
          ((StringBuilder)localObject1).append("server=" + str2);
        if ((str1 != null) && (str1.length() > 0))
          ((StringBuilder)localObject1).append("&fedsrv=" + str1);
        if ((this.mForm != null) && (this.mForm.length() > 0))
          ((StringBuilder)localObject1).append("&form=" + this.mForm);
        if ((this.mView != null) && (this.mView.length() > 0))
          ((StringBuilder)localObject1).append("&view=" + this.mView);
        if ((this.mAppName != null) && (this.mAppName.length() > 0))
          ((StringBuilder)localObject1).append("&app=" + this.mAppName);
        append("this.result=").openObj().property("url", ((StringBuilder)localObject1).toString());
      }
      else
      {
        append("this.result=").openObj().property("url", getURL(localFormContext, this.mServer, this.mForm, this.mAppName, this.mView, this.mWinName, this.mWinArg));
      }
      if (this.mFetchDimensions)
      {
        localObject1 = this.mViewInfo.getDetailBox();
        if (localObject1 == null)
          property("dims");
        else
          property("dims", ((Box)localObject1).mW + "," + ((Box)localObject1).mH);
      }
      closeObj().append(";");
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetURLForFormAgent
 * JD-Core Version:    0.6.1
 */