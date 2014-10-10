package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.Form.ViewInfo;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Guide;
import com.remedy.arsys.goat.Guide.Key;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;

public class GetGuideServerAndFormAgent extends NDXGetGuideServerAndForm
{
  public GetGuideServerAndFormAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    append("this.result=").openObj();
    Guide localGuide = Guide.get(new Guide.Key(SessionData.get().getServerLogin(this.mGuideServer), this.mGuideName));
    property("server", localGuide.getServer());
    property("form", localGuide.getSchema());
    property("view", Form.get(localGuide.getServer(), localGuide.getSchema()).getViewInfoByInference(null, false, false).getLabel());
    property("guideName", localGuide.getGuideName());
    closeObj().append(";");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetGuideServerAndFormAgent
 * JD-Core Version:    0.6.1
 */