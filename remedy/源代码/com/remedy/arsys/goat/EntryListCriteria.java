package com.remedy.arsys.goat;

import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.Timestamp;
import java.io.Serializable;
import java.util.List;

public class EntryListCriteria
  implements Serializable
{
  private static final long serialVersionUID = -7978356797121780543L;
  private String _schemaID;
  private QualifierInfo _qualifier;
  private int _firstRetrieve;
  private int _maxLimit;
  private List<SortInfo> _sortInfos;
  private String[] _entriesToRetrieve;

  public EntryListCriteria()
  {
  }

  public EntryListCriteria(String paramString, QualifierInfo paramQualifierInfo, int paramInt, Timestamp paramTimestamp, List<SortInfo> paramList, String[] paramArrayOfString)
  {
    this._schemaID = paramString;
    this._qualifier = paramQualifierInfo;
    this._maxLimit = paramInt;
    this._sortInfos = paramList;
    this._entriesToRetrieve = paramArrayOfString;
  }

  public EntryListCriteria(String paramString, QualifierInfo paramQualifierInfo, int paramInt1, int paramInt2, Timestamp paramTimestamp, List<SortInfo> paramList, String[] paramArrayOfString)
  {
    this._schemaID = paramString;
    this._qualifier = paramQualifierInfo;
    this._firstRetrieve = paramInt1;
    this._maxLimit = paramInt2;
    this._sortInfos = paramList;
    this._entriesToRetrieve = paramArrayOfString;
  }

  public String getSchemaID()
  {
    return this._schemaID;
  }

  public void setSchemaID(String paramString)
  {
    this._schemaID = paramString;
  }

  public QualifierInfo getQualifier()
  {
    return this._qualifier;
  }

  public void setQualifier(QualifierInfo paramQualifierInfo)
  {
    this._qualifier = paramQualifierInfo;
  }

  public int getFirstRetrieve()
  {
    return this._firstRetrieve;
  }

  public void setFirstRetrieve(int paramInt)
  {
    this._firstRetrieve = paramInt;
  }

  public int getMaxLimit()
  {
    return this._maxLimit;
  }

  public void setMaxLimit(int paramInt)
  {
    this._maxLimit = paramInt;
  }

  public List<SortInfo> getSortInfos()
  {
    return this._sortInfos;
  }

  public void setSortInfos(List<SortInfo> paramList)
  {
    this._sortInfos = paramList;
  }

  public String[] getEntriesToRetrieve()
  {
    return this._entriesToRetrieve;
  }

  public void setEntriesToRetrieve(String[] paramArrayOfString)
  {
    this._entriesToRetrieve = paramArrayOfString;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.EntryListCriteria
 * JD-Core Version:    0.6.1
 */