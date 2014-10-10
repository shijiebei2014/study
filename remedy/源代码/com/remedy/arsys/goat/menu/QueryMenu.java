package com.remedy.arsys.goat.menu;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.QualifierInfo;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.ARQualifier;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.field.CharField;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.List;

public class QueryMenu extends Menu
{
  private static final long serialVersionUID = 4432354265411129556L;
  private List<Integer> mLFields;
  private int mVField;
  private boolean mSortOnLabel;

  protected QueryMenu(Menu.MKey paramMKey, com.bmc.arsys.api.Menu paramMenu)
    throws GoatException
  {
    super(paramMKey, paramMenu);
  }

  public int getValueField()
  {
    return this.mVField;
  }

  public int getLeafLabelField()
  {
    if ((this.mLFields == null) || (this.mLFields.size() == 0) || (((Integer)this.mLFields.get(0)).intValue() == 0))
      return -1;
    int i = this.mLFields.size();
    for (int j = i - 1; j >= 0; j--)
    {
      Integer localInteger = (Integer)this.mLFields.get(j);
      if (localInteger.intValue() != 0)
        return localInteger.intValue();
    }
    return -1;
  }

  protected String getJS()
    throws GoatException
  {
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openObj();
    Object localObject1;
    String str;
    Object localObject2;
    synchronized (this.mAPIMenu)
    {
      com.bmc.arsys.api.QueryMenu localQueryMenu = (com.bmc.arsys.api.QueryMenu)this.mAPIMenu;
      if (localQueryMenu != null)
      {
        localObject1 = new Qualifier(localQueryMenu.getQualification(), this.mServer);
        str = localQueryMenu.getServer();
        localObject2 = localQueryMenu.getForm();
        this.mLFields = localQueryMenu.getLabelField();
        this.mVField = localQueryMenu.getValueField();
        this.mSortOnLabel = localQueryMenu.isSortOnLabel();
        localJSWriter.property("q", ((Qualifier)localObject1).emitEncodedAsString()).property("s", str);
        localJSWriter.property("f", (String)localObject2).property("r", this.mAPIMenu.getRefreshCode());
      }
      localJSWriter.property("t", 4);
    }
    try
    {
      int i = getLeafLabelField();
      int j = getValueField();
      localObject1 = getForm();
      str = getServer();
      if (str.equals("@"))
        str = this.mServer;
      localObject2 = FieldGraph.get(str, (String)localObject1, "");
      GoatField localGoatField = ((FieldGraph)localObject2).getField(i);
      if (!(localGoatField instanceof CharField))
        localJSWriter.property("l", 1);
      localGoatField = ((FieldGraph)localObject2).getField(j);
      if (!(localGoatField instanceof CharField))
        localJSWriter.property("v", 1);
    }
    catch (GoatException localGoatException)
    {
      localGoatException.printStackTrace();
      localJSWriter.property("l", 1);
      localJSWriter.property("v", 1);
    }
    localJSWriter.closeObj();
    return localJSWriter.toString();
  }

  public void emitJS(JSWriter paramJSWriter, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Entry paramEntry1, Entry paramEntry2, Entry paramEntry3)
    throws GoatException
  {
    ServerLogin localServerLogin = SessionData.get().getServerLogin(paramString1);
    com.bmc.arsys.api.QueryMenu localQueryMenu = (com.bmc.arsys.api.QueryMenu)this.mAPIMenu;
    QualifierInfo localQualifierInfo = null;
    if ((paramString5 != null) && (paramString5.length() > 0))
    {
      localQualifierInfo = Qualifier.ARDecodeARQualifierStruct(localServerLogin, paramString5);
      localObject = new ARQualifier(localQualifierInfo, paramEntry1);
      localQualifierInfo = ((ARQualifier)localObject).getQualInfo();
    }
    if (localQualifierInfo == null)
      localQualifierInfo = localQueryMenu.getQualification();
    Object localObject = new com.bmc.arsys.api.QueryMenu(paramString2, paramString1, localQualifierInfo, this.mLFields, this.mVField, this.mSortOnLabel, localQueryMenu.getSampleForm(), localQueryMenu.getSampleServer());
    ((com.bmc.arsys.api.QueryMenu)localObject).setForm(paramString4);
    List localList = Configuration.getInstance().getServers();
    paramString3 = paramString3.trim().toLowerCase();
    if ((localList != null) && (localList.contains(paramString3)))
      ((com.bmc.arsys.api.QueryMenu)localObject).setServer(paramString3);
    else
      throw new GoatException(9280, paramString3);
    recurEmitJS(paramJSWriter, expandMenu(localServerLogin, (com.bmc.arsys.api.Menu)localObject, paramEntry2, paramEntry1), 4);
  }

  public String getForm()
  {
    return ((com.bmc.arsys.api.QueryMenu)this.mAPIMenu).getForm();
  }

  public String getServer()
  {
    return ((com.bmc.arsys.api.QueryMenu)this.mAPIMenu).getServer();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.menu.QueryMenu
 * JD-Core Version:    0.6.1
 */