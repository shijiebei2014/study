package com.remedy.arsys.goat;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.StatusInfo;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class GoatException extends Exception
{
  private static Log mLog = Log.get(11);
  static final int ERROR_NUMBER_NOT_FOUND = -1;
  static final String MSG_NOT_FOUND_STRING = "Message not found";
  int mErrorCode = -1;
  boolean mConvertIdToLabel = true;
  Object[] mMsgObjs = new Object[2];

  public GoatException()
  {
  }

  public GoatException(String paramString)
  {
    super(paramString);
    assert (paramString != null);
    mLog.fine("Throw Internal Exception - " + getMessage());
  }

  public GoatException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    assert (paramString != null);
    mLog.fine("Throw Internal Exception - " + getMessage());
    if (paramThrowable != null)
      mLog.log(Level.FINE, "Caused due to ", paramThrowable);
  }

  public GoatException(ARException paramARException)
  {
    super("Throw ARException - " + paramARException.toString(), paramARException);
    mLog.log(Level.FINE, getMessage(), paramARException);
  }

  public GoatException(ARException paramARException, boolean paramBoolean)
  {
    super(paramARException);
    this.mConvertIdToLabel = paramBoolean;
  }

  public GoatException(int paramInt)
  {
    super("Throw Error - " + paramInt);
    mLog.fine(getMessage());
    this.mErrorCode = paramInt;
  }

  public boolean amInternalError()
  {
    return (getCause() == null) && (this.mErrorCode == -1);
  }

  public GoatException(int paramInt, Object paramObject)
  {
    this(paramInt);
    this.mMsgObjs[0] = paramObject;
  }

  public GoatException(int paramInt, Object paramObject1, Object paramObject2)
  {
    this(paramInt);
    this.mMsgObjs[0] = paramObject1;
    this.mMsgObjs[1] = paramObject2;
  }

  public GoatException(int paramInt, Object[] paramArrayOfObject)
  {
    this(paramInt);
    this.mMsgObjs = paramArrayOfObject;
  }

  public GoatException(int paramInt, Throwable paramThrowable, Object[] paramArrayOfObject)
  {
    this(paramInt, paramThrowable);
    this.mMsgObjs = paramArrayOfObject;
    if (paramThrowable != null)
      mLog.log(Level.FINE, "Caused due to ", paramThrowable);
  }

  public List<StatusInfo> getStatusInfo(String paramString)
  {
    if ((getCause() != null) && ((getCause() instanceof ARException)))
    {
      assert ((getCause() instanceof ARException));
      List localList1 = ((ARException)getCause()).getLastStatus();
      if (localList1.size() > 0)
        return localList1;
    }
    int i = this.mErrorCode;
    if (i == -1)
    {
      assert (getMessage() != null);
      this.mErrorCode = 9215;
    }
    String str;
    if (i != -1)
    {
      str = MessageTranslation.getLocalizedErrorMessage(paramString, i, this.mMsgObjs);
    }
    else
    {
      if ((getCause() != null) && ((getCause() instanceof ARException)))
      {
        assert ((getCause() instanceof ARException));
        List localList2 = ((ARException)getCause()).getLastStatus();
        return localList2;
      }
      str = "Message not found";
    }
    int j = (i >= 9500) && (i <= 9599) ? 1 : 2;
    ArrayList localArrayList = new ArrayList(1);
    localArrayList.add(new StatusInfo(j, i, str, ""));
    return localArrayList;
  }

  public String getLocalizedMessage()
  {
    return toString();
  }

  public String toString(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    List localList = getStatusInfo(paramString);
    int i = 0;
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      StatusInfo localStatusInfo = (StatusInfo)localIterator.next();
      if (i != 0)
        localStringBuilder.append("\n");
      int j = localStatusInfo.getMessageType();
      if (j == 2)
        localStringBuilder.append("ARERR [");
      else if (j == 1)
        localStringBuilder.append("ARWARN [");
      else if (j == 0)
        localStringBuilder.append("ARNOTE [");
      localStringBuilder.append(localStatusInfo.getMessageNum()).append("] ").append(localStatusInfo.getMessageText());
      String str = localStatusInfo.getAppendedText();
      if ((str != null) && (str.length() != 0))
        localStringBuilder.append(" : ").append(str);
      i = 1;
    }
    return localStringBuilder.toString();
  }

  public String toString()
  {
    SessionData localSessionData = SessionData.tryGet();
    String str;
    if (localSessionData == null)
      str = Locale.getDefault().toString();
    else
      str = localSessionData.getLocale();
    return toString(str);
  }

  public List<StatusInfo> getStatusInfo()
  {
    return getStatusInfo(SessionData.get().getLocale());
  }

  public List<StatusInfo> getStatusInfoAssumingFromServer()
  {
    assert ((getCause() instanceof ARException));
    if ((getCause() != null) && ((getCause() instanceof ARException)))
      return ((ARException)getCause()).getLastStatus();
    return null;
  }

  public boolean canConvertIdToLabel()
  {
    return this.mConvertIdToLabel;
  }

  public void setConvertIdToLabel(boolean paramBoolean)
  {
    this.mConvertIdToLabel = paramBoolean;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.GoatException
 * JD-Core Version:    0.6.1
 */