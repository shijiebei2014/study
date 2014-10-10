package com.remedy.arsys.goat.menu;

import com.bmc.arsys.api.DataDictionaryMenu;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.FieldDataDictionaryMenu;
import com.bmc.arsys.api.FormDataDictionaryMenu;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.SessionData;

public class DDMenu extends Menu
{
  private static final long serialVersionUID = -3570683715061966650L;
  private int mNameType;
  private int mFieldType;
  private int mFormType;
  private int mValFormat;
  private int mStructFormat;
  private boolean mIncludeHidden;

  protected DDMenu(Menu.MKey paramMKey, com.bmc.arsys.api.Menu paramMenu)
    throws GoatException
  {
    super(paramMKey, paramMenu);
  }

  protected String getJS()
  {
    JSWriter localJSWriter = new JSWriter();
    localJSWriter.openObj();
    synchronized (this.mAPIMenu)
    {
      DataDictionaryMenu localDataDictionaryMenu = (DataDictionaryMenu)this.mAPIMenu;
      this.mNameType = localDataDictionaryMenu.getNameType();
      this.mValFormat = localDataDictionaryMenu.getValueFormat();
      this.mStructFormat = localDataDictionaryMenu.getStructType();
      localJSWriter.property("r", this.mAPIMenu.getRefreshCode());
      localJSWriter.property("s", localDataDictionaryMenu.getServer());
      Object localObject1;
      if (this.mStructFormat == 2)
      {
        localObject1 = (FieldDataDictionaryMenu)this.mAPIMenu;
        this.mFieldType = ((FieldDataDictionaryMenu)localObject1).getFieldType();
        String str = ((FieldDataDictionaryMenu)localObject1).getForm();
        assert (str != null);
        localJSWriter.property("f", str);
      }
      else
      {
        localObject1 = (FormDataDictionaryMenu)this.mAPIMenu;
        this.mFormType = ((FormDataDictionaryMenu)localObject1).getFormType();
        this.mIncludeHidden = ((FormDataDictionaryMenu)localObject1).isIncludeHidden();
      }
      localJSWriter.property("t", 0);
    }
    localJSWriter.closeObj();
    return localJSWriter.toString();
  }

  public void emitJS(JSWriter paramJSWriter, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Entry paramEntry1, Entry paramEntry2, Entry paramEntry3)
    throws GoatException
  {
    Object localObject = null;
    if (this.mStructFormat == 2)
      localObject = new FieldDataDictionaryMenu(paramString3, this.mNameType, this.mValFormat, this.mFieldType, paramString4);
    else
      localObject = new FormDataDictionaryMenu(paramString3, this.mNameType, this.mValFormat, this.mFormType, this.mIncludeHidden);
    recurEmitJS(paramJSWriter, expandMenu(SessionData.get().getServerLogin(paramString3), (com.bmc.arsys.api.Menu)localObject), 0);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.menu.DDMenu
 * JD-Core Version:    0.6.1
 */