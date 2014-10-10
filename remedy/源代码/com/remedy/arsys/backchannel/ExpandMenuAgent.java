package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.QualifierInfo;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Form;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.menu.Menu;
import com.remedy.arsys.goat.menu.Menu.MKey;
import com.remedy.arsys.goat.menu.QueryMenu;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;

public class ExpandMenuAgent extends NDXExpandMenu
{
  public ExpandMenuAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    Menu.MKey localMKey = new Menu.MKey(this.mServer, SessionData.get().getLocale(), this.mName);
    Menu localMenu;
    try
    {
      localMenu = Menu.get(localMKey);
    }
    catch (GoatException localGoatException)
    {
      throw new GoatException(9372, localGoatException);
    }
    Entry localEntry1 = buildEntryItems(this.mServer, this.mQualFieldIds, this.mQualFieldValues, this.mQualFieldTypes);
    Entry localEntry2 = buildEntryItems(this.mServer, this.mKeywordIds, this.mKeywordVals, this.mKeywordTypes);
    Entry localEntry3 = buildEntryItems(this.mServer, this.mFieldIds, this.mFieldValues, this.mFieldTypes);
    if (localMenu != null)
    {
      applyFilter(localMenu);
      append("this.result=");
      localMenu.emitJS(this, this.mServer, this.mSchema, this.mRServer, this.mRSchema, this.mQualification, localEntry1, localEntry2, localEntry3);
    }
    else
    {
      throw new GoatException(9372);
    }
  }

  private void applyFilter(Menu paramMenu)
    throws GoatException
  {
    boolean bool = this.mFilter.equals("");
    paramMenu.setReturnFullQuery(bool);
    if (bool)
      return;
    String str1;
    if ((this.mArautocmb.trim().length() > 0) && (this.mArautocmb.equals("1")))
      str1 = "" + ((QueryMenu)paramMenu).getLeafLabelField();
    else
      str1 = "" + ((QueryMenu)paramMenu).getValueField();
    String str2 = "";
    CachedFieldMap localCachedFieldMap1 = Form.get(this.mRServer, this.mRSchema).getCachedFieldMap(true);
    CachedFieldMap localCachedFieldMap2 = Form.get(this.mServer, this.mSchema).getCachedFieldMap();
    ServerLogin localServerLogin = SessionData.get().getServerLogin(this.mServer);
    QualifierInfo localQualifierInfo = null;
    if (this.mQualification.length() > 0)
      localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(localServerLogin, this.mQualification);
    ARQualifier localARQualifier;
    if (this.mAcstyle.equals("1"))
    {
      str2 = "'" + str1 + "' like \"" + this.mFilter + "%\"";
      localARQualifier = new ARQualifier(localServerLogin, str2, localCachedFieldMap1, localCachedFieldMap2, 1);
      if (localQualifierInfo != null)
        this.mQualification = Qualifier.AREncodeARQualifierStruct(localServerLogin, new QualifierInfo(1, localQualifierInfo, localARQualifier.getQualInfo()));
      else
        this.mQualification = Qualifier.AREncodeARQualifierStruct(localServerLogin, localARQualifier.getQualInfo());
    }
    else
    {
      str2 = "'" + str1 + "' like \"%" + this.mFilter + "%\"";
      localARQualifier = new ARQualifier(localServerLogin, str2, localCachedFieldMap1, localCachedFieldMap2, 1);
      if (localQualifierInfo != null)
        this.mQualification = Qualifier.AREncodeARQualifierStruct(localServerLogin, new QualifierInfo(1, localQualifierInfo, localARQualifier.getQualInfo()));
      else
        this.mQualification = Qualifier.AREncodeARQualifierStruct(localServerLogin, localARQualifier.getQualInfo());
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.ExpandMenuAgent
 * JD-Core Version:    0.6.1
 */