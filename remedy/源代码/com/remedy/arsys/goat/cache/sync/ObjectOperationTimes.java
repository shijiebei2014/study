package com.remedy.arsys.goat.cache.sync;

import com.bmc.arsys.api.Timestamp;
import java.io.Serializable;

public class ObjectOperationTimes
  implements Serializable
{
  private static final long serialVersionUID = 2162969591518755539L;
  private long changeTimestamp;
  private long createTimestamp;
  private long deleteTimestamp;

  protected ObjectOperationTimes()
  {
  }

  public ObjectOperationTimes(com.bmc.arsys.api.ObjectOperationTimes paramObjectOperationTimes)
  {
    setChangeTimestamp(paramObjectOperationTimes.getChangeTime().getValue());
    setCreateTimestamp(paramObjectOperationTimes.getCreateTime().getValue());
    setDeleteTimestamp(paramObjectOperationTimes.getDeleteTime().getValue());
  }

  public ObjectOperationTimes(long[] paramArrayOfLong)
  {
    setChangeTimestamp(paramArrayOfLong[0]);
    setCreateTimestamp(paramArrayOfLong[1]);
    setDeleteTimestamp(paramArrayOfLong[2]);
  }

  public long getChangeTimestamp()
  {
    return this.changeTimestamp;
  }

  public void setChangeTimestamp(long paramLong)
  {
    this.changeTimestamp = paramLong;
  }

  public long getCreateTimestamp()
  {
    return this.createTimestamp;
  }

  public void setCreateTimestamp(long paramLong)
  {
    this.createTimestamp = paramLong;
  }

  public long getDeleteTimestamp()
  {
    return this.deleteTimestamp;
  }

  public void setDeleteTimestamp(long paramLong)
  {
    this.deleteTimestamp = paramLong;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.cache.sync.ObjectOperationTimes
 * JD-Core Version:    0.6.1
 */