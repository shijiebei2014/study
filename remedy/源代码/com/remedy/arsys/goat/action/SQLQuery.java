package com.remedy.arsys.goat.action;

import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.FormAware;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.TargetAware;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;

public class SQLQuery
  implements FormAware, TargetAware, Cloneable, Serializable
{
  private static final long serialVersionUID = -7153721380391918763L;
  private String mActiveLinkName;
  private int mActionIndex;
  private long mValIndex;
  private long mMultiMatch;
  private long mNoMatch;
  private SQLQuery mReferTo;
  private String mVarIndex;
  private ARCommandString mCmdString;
  private long mMaxReferredToValIdx;

  public SQLQuery(String paramString1, int paramInt, long paramLong1, long paramLong2, long paramLong3, String paramString2)
  {
    this.mActiveLinkName = paramString1;
    this.mActionIndex = paramInt;
    this.mValIndex = (this.mMaxReferredToValIdx = paramLong1);
    this.mMultiMatch = paramLong2;
    this.mNoMatch = paramLong3;
    this.mCmdString = new ARCommandString(paramString2);
    this.mReferTo = null;
    this.mVarIndex = null;
  }

  protected String getVarName()
  {
    return "fq" + (this.mVarIndex == null ? "" + hashCode() : this.mVarIndex);
  }

  public String toString()
  {
    return "F(" + this.mValIndex + ").SQLQuery(" + this.mActiveLinkName + ", " + this.mActionIndex + ", " + this.mMultiMatch + "," + this.mNoMatch + ", " + this.mCmdString.getCommandString() + ")";
  }

  public void emitInterruptibleJS(Emitter paramEmitter)
    throws GoatException
  {
    if (this.mReferTo == null)
    {
      JSWriter localJSWriter = paramEmitter.js();
      localJSWriter.statement("var " + getVarName() + " = new FieldList(" + "windowID" + ")");
      Object[] arrayOfObject = { JSWriter.escapeString(this.mActiveLinkName), "" + this.mActionIndex, "" + this.mMultiMatch, "" + this.mNoMatch, this.mCmdString.getKeywordReferencesAsJSArrayString(), this.mCmdString.getFieldReferencesAsJSArrayString(), "" + this.mMaxReferredToValIdx };
      localJSWriter.callFunction(isInterruptible(), getVarName(), "SQLQuery", arrayOfObject);
    }
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    paramEmitter.js().append(getVarName());
    paramEmitter.js().append(".F(").append(this.mValIndex).append(")");
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
    this.mCmdString.bindToForm(paramCachedFieldMap);
  }

  public void bindToTarget(int paramInt1, int paramInt2)
    throws GoatException
  {
  }

  public void simplify(SimplifyState paramSimplifyState)
    throws GoatException
  {
    if (paramSimplifyState != null)
    {
      SQLQuery localSQLQuery = paramSimplifyState.crossReferenceSQLQuery(this);
      if (localSQLQuery != null)
      {
        localSQLQuery.addReferrer(this);
        this.mReferTo = localSQLQuery;
      }
    }
  }

  public boolean isInterruptible()
  {
    return this.mMultiMatch == 4L;
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    SQLQuery localSQLQuery = (SQLQuery)super.clone();
    localSQLQuery.mCmdString = ((ARCommandString)this.mCmdString.clone());
    return localSQLQuery;
  }

  public long getMultiMatch()
  {
    return this.mMultiMatch;
  }

  public long getNoMatch()
  {
    return this.mNoMatch;
  }

  public long getIndex()
  {
    return this.mValIndex;
  }

  public void addReferrer(SQLQuery paramSQLQuery)
  {
    long l = paramSQLQuery.getIndex();
    if (l > this.mMaxReferredToValIdx)
      this.mMaxReferredToValIdx = l;
  }

  public void setIndex(String paramString)
  {
    this.mVarIndex = paramString;
  }

  public int hashCode()
  {
    return this.mCmdString.hashCode();
  }

  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof SQLQuery))
    {
      SQLQuery localSQLQuery = (SQLQuery)paramObject;
      return (this.mMultiMatch == localSQLQuery.mMultiMatch) && (this.mNoMatch == localSQLQuery.mNoMatch) && (this.mCmdString.equals(localSQLQuery.mCmdString)) && (this.mActiveLinkName.equals(localSQLQuery.mActiveLinkName)) && (this.mActionIndex == localSQLQuery.mActionIndex);
    }
    return false;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.SQLQuery
 * JD-Core Version:    0.6.1
 */