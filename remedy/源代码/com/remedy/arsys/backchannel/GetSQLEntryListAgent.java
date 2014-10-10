package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.SQLResult;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.util.Iterator;
import java.util.List;

public class GetSQLEntryListAgent extends NDXGetSQLEntryList
{
  private ServerLogin mServerUser;

  public GetSQLEntryListAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    this.mServerUser = SessionData.get().getServerLogin(this.mServer);
    List localList1 = buildListItems(this.mServer, this.mKeywordIds, this.mKeywordValues, this.mKeywordTypes);
    List localList2 = buildListItems(this.mServer, this.mFieldIds, this.mFieldValues, this.mFieldTypes);
    int i = this.mIsAction ? 8 : 2;
    SQLResult localSQLResult;
    try
    {
      localSQLResult = this.mServerUser.getListSQLForActiveLink(this.mActionName, this.mActionIdx, i, new Timestamp(0L), localList1, localList2, 0, false);
    }
    catch (ARException localARException)
    {
      throw new GoatException(localARException);
    }
    if (this.mIsAction)
    {
      append("this.result=0;");
      return;
    }
    List localList3 = null;
    int j = 0;
    if (localSQLResult != null)
    {
      localList3 = localSQLResult.getContents();
      if (localList3 != null)
        j = localList3.size();
    }
    int k = 1;
    if (j > 1)
      switch (this.mMultiMatchOpt)
      {
      case 3:
      case 7:
        j = 1;
        break;
      case 1:
      case 2:
        k = 0;
        break;
      case 4:
      case 5:
      case 6:
      default:
        break;
      }
    else if (j == 0)
      k = 0;
    if (k == 0)
    {
      append("this.result=");
      append(j);
      append(";");
      return;
    }
    assert (localList3 != null);
    append("this.result=").openObj().property("n", j).append(",l:").openObj();
    append("r:").openList();
    Iterator localIterator1 = localList3.iterator();
    while (localIterator1.hasNext())
    {
      listSep().openObj().append("d:").openList();
      List localList4 = (List)localIterator1.next();
      Iterator localIterator2 = localList4.iterator();
      while (localIterator2.hasNext())
      {
        Value localValue = (Value)localIterator2.next();
        if (localValue != null)
        {
          String str = localValue.toString();
          if (str == null)
            str = "";
          listSep().openObj().property("v", str).property("t", localValue.getDataType().toInt()).closeObj();
        }
      }
      closeList().closeObj();
      if (j == 1)
        break;
    }
    closeList().closeObj().closeObj();
    append(";");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.GetSQLEntryListAgent
 * JD-Core Version:    0.6.1
 */