package com.remedy.arsys.goat.action;

import com.remedy.arsys.goat.CachedFieldMap;
import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.FormAware;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.Qualifier;
import com.remedy.arsys.goat.TargetAware;
import com.remedy.arsys.share.JSWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FieldQuery
  implements FormAware, TargetAware, Cloneable, Serializable
{
  private static final long serialVersionUID = 3123868170607073379L;
  private int mFieldId;
  private long mMultiMatch;
  private long mNoMatch;
  private Qualifier mQualifier;
  private String mServer;
  private String mSchema;
  private FieldQuery mReferTo;
  private Map mReferrers;
  private String mVarIndex;

  public FieldQuery(int paramInt, long paramLong1, long paramLong2, Qualifier paramQualifier, String paramString1, String paramString2)
  {
    assert (paramQualifier != null);
    this.mFieldId = paramInt;
    this.mMultiMatch = paramLong1;
    this.mNoMatch = paramLong2;
    this.mQualifier = paramQualifier;
    this.mServer = paramString1;
    this.mSchema = paramString2;
    this.mReferTo = null;
    this.mReferrers = new HashMap();
    this.mVarIndex = null;
  }

  public String getVarName()
  {
    return "fq" + (this.mVarIndex == null ? "" + hashCode() : this.mVarIndex);
  }

  public String toString()
  {
    String str = "";
    try
    {
      str = this.mQualifier.emitARAsString();
    }
    catch (GoatException localGoatException)
    {
    }
    return "F(" + this.mFieldId + ").Query(" + this.mMultiMatch + "," + this.mNoMatch + "," + str + "," + this.mServer + "," + this.mSchema + ")";
  }

  public void emitInterruptibleJS(Emitter paramEmitter)
    throws GoatException
  {
    if (this.mReferTo == null)
    {
      JSWriter localJSWriter = paramEmitter.js();
      localJSWriter.statement("var " + getVarName() + " = new FieldList(" + "windowID" + ")");
      addReferrer(this.mFieldId);
      Object[] arrayOfObject = new Object[6];
      arrayOfObject[0] = JSWriter.escapeString(this.mServer);
      arrayOfObject[1] = JSWriter.escapeString(this.mSchema);
      arrayOfObject[2] = this.mQualifier.emitEncodedAsString();
      arrayOfObject[3] = ("" + this.mMultiMatch);
      arrayOfObject[4] = ("" + this.mNoMatch);
      StringBuilder localStringBuilder = new StringBuilder("[");
      Iterator localIterator = this.mReferrers.keySet().iterator();
      while (localIterator.hasNext())
      {
        if (localStringBuilder.length() > 1)
          localStringBuilder.append(",");
        localStringBuilder.append((Long)localIterator.next());
      }
      localStringBuilder.append("]");
      arrayOfObject[5] = localStringBuilder.toString();
      localJSWriter.callFunction(isInterruptible(), getVarName(), "Query", arrayOfObject);
    }
  }

  public String getUniqueFieldIds()
    throws GoatException
  {
    if (this.mReferTo == null)
    {
      StringBuilder localStringBuilder = new StringBuilder("[");
      if (this.mReferrers.size() == 0)
      {
        localStringBuilder.append(this.mFieldId);
      }
      else
      {
        Iterator localIterator = this.mReferrers.keySet().iterator();
        while (localIterator.hasNext())
        {
          if (localStringBuilder.length() > 1)
            localStringBuilder.append(",");
          localStringBuilder.append((Long)localIterator.next());
        }
        if (!this.mReferrers.containsKey(new Long(this.mFieldId)))
        {
          if (localStringBuilder.length() > 1)
            localStringBuilder.append(",");
          localStringBuilder.append(this.mFieldId);
        }
      }
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
    return null;
  }

  public void emitJS(Emitter paramEmitter)
    throws GoatException
  {
    paramEmitter.js().append(getVarName());
    if (this.mFieldId != 98)
      paramEmitter.js().append(".F(").append(this.mFieldId).append(")");
  }

  public void bindToForm(CachedFieldMap paramCachedFieldMap)
    throws GoatException
  {
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
      FieldQuery localFieldQuery = paramSimplifyState.crossReferenceFieldQuery(this);
      if (localFieldQuery != null)
        localFieldQuery.addReferrer(this);
      this.mReferTo = localFieldQuery;
    }
  }

  public boolean isInterruptible()
  {
    return this.mMultiMatch == 4L;
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    FieldQuery localFieldQuery = (FieldQuery)super.clone();
    localFieldQuery.mQualifier = ((Qualifier)this.mQualifier.clone());
    return localFieldQuery;
  }

  public Qualifier getQualifier()
  {
    return this.mQualifier;
  }

  public long getMultiMatch()
  {
    return this.mMultiMatch;
  }

  public long getNoMatch()
  {
    return this.mNoMatch;
  }

  public int getFieldId()
  {
    return this.mFieldId;
  }

  public String getServer()
  {
    return this.mServer;
  }

  public String getSchema()
  {
    return this.mSchema;
  }

  public void addReferrer(FieldQuery paramFieldQuery)
  {
    this.mReferrers.put(new Long(paramFieldQuery.getFieldId()), paramFieldQuery);
  }

  public void addReferrer(long paramLong)
  {
    this.mReferrers.put(new Long(paramLong), this);
  }

  public void setIndex(String paramString)
  {
    this.mVarIndex = paramString;
  }

  public boolean compareTo(FieldQuery paramFieldQuery)
  {
    try
    {
      return (this.mMultiMatch == paramFieldQuery.mMultiMatch) && (this.mNoMatch == paramFieldQuery.mNoMatch) && (this.mServer.equals(paramFieldQuery.mServer)) && (this.mSchema.equals(paramFieldQuery.mSchema)) && (this.mQualifier.emitAR().equals(paramFieldQuery.mQualifier.emitAR())) && (this.mFieldId != 98) && (paramFieldQuery.mFieldId != 98);
    }
    catch (GoatException localGoatException)
    {
    }
    return false;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.action.FieldQuery
 * JD-Core Version:    0.6.1
 */