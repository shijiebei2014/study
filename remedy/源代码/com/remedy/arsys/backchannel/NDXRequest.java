package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.StatusInfo;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.goat.ForcedLoginException;
import com.remedy.arsys.goat.Globule;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.log.ARServerLog;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.session.Login;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;
import com.remedy.arsys.support.Validator;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public abstract class NDXRequest extends JSWriter
{
  protected static final Log MLog = Log.get(10);
  protected static final Log MServerLog = Log.get(12);
  boolean outputStreamed = false;
  protected List<StatusInfo> mStatus = new ArrayList();
  protected boolean mConvertIdToLabel = false;
  protected Globule mGlobule = null;

  public NDXRequest()
  {
  }

  public NDXRequest(String paramString)
  {
    processRequest(paramString);
  }

  public NDXRequest(String paramString, OutputStream paramOutputStream)
  {
    super(paramOutputStream);
    processRequest(paramString);
  }

  public boolean isOutputStreamed()
  {
    return this.outputStreamed;
  }

  private void processRequest(String paramString)
  {
    int i = 1;
    try
    {
      ArrayList localArrayList = new ArrayList();
      localObject1 = new Parser(paramString);
      String str1;
      while ((str1 = ((Parser)localObject1).next()) != null)
        localArrayList.add(str1);
      mapProperties(localArrayList);
      process();
    }
    catch (GoatException localGoatException)
    {
      Object localObject1;
      if ((localGoatException instanceof ForcedLoginException))
      {
        append(Login.handleForcedLoginException((ForcedLoginException)localGoatException));
        i = 0;
        return;
      }
      if (localGoatException.amInternalError())
      {
        localObject1 = new ArrayList(1);
        int j = 9350;
        String str2 = MessageTranslation.getLocalizedErrorMessage(SessionData.get().getLocale(), j, null);
        ((List)localObject1).add(new StatusInfo(2, j, str2, localGoatException.getMessage()));
        emitStatus((List)localObject1, localGoatException.canConvertIdToLabel());
      }
      else
      {
        emitStatus(localGoatException.getStatusInfo(), localGoatException.canConvertIdToLabel());
      }
      MLog.log(Level.SEVERE, "GoatException during NDXRequest:= > ", localGoatException);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    finally
    {
      SessionData localSessionData = SessionData.tryGet();
      if ((localSessionData != null) && (localSessionData.isWebTimingsEnabled()))
      {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss.SSS");
        localSessionData.getServerLog().log("<MTT > /* " + localSimpleDateFormat.format(Long.valueOf(System.currentTimeMillis())) + " */" + "-MidTier\n");
      }
      if (i != 0)
        emitStatus(this.mStatus, this.mConvertIdToLabel);
    }
  }

  protected final void emitStatus(GoatException paramGoatException)
  {
    if (paramGoatException.getStatusInfo().size() == 0)
      MLog.log(Level.SEVERE, "GoatException with no status", paramGoatException);
    assert (paramGoatException.getStatusInfo().size() > 0);
    emitStatus(paramGoatException.getStatusInfo(), paramGoatException.canConvertIdToLabel());
  }

  protected final void emitStatus(List<StatusInfo> paramList, boolean paramBoolean)
  {
    emitStatus(this, paramList, paramBoolean);
  }

  private static final void emitStatus(JSWriter paramJSWriter, List<StatusInfo> paramList, boolean paramBoolean)
  {
    int i = 0;
    int j = 0;
    Iterator localIterator;
    StatusInfo localStatusInfo;
    int k;
    if (paramList != null)
    {
      localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        localStatusInfo = (StatusInfo)localIterator.next();
        k = localStatusInfo.getMessageType();
        if ((k == 2) || (k == 3))
          i = 1;
        if (localStatusInfo.getMessageNum() == 9093L)
          j = 1;
      }
    }
    if ((i != 0) || (j != 0))
      paramJSWriter.clear();
    if (j != 0)
      paramJSWriter.append("retry=this.Override(").openList();
    else
      paramJSWriter.append(";if(getCurWFC_NS(this.windowID)!=null) getCurWFC_NS(this.windowID).status(").openList();
    if (paramList != null)
    {
      localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        localStatusInfo = (StatusInfo)localIterator.next();
        k = localStatusInfo.getMessageType();
        long l = localStatusInfo.getMessageNum();
        if ((i == 0) || ((l != 8914L) && ((k != 0) || (l > 9999L))))
        {
          String str1 = localStatusInfo.getMessageText();
          assert (str1 != null);
          String str2 = localStatusInfo.getAppendedText();
          if (str2 == null)
            str2 = "";
          paramJSWriter.listSep().openObj();
          if ((paramBoolean) && (l < 9999L))
            paramJSWriter.property("cId", 1);
          paramJSWriter.property("t", k).property("m", str1).property("n", l).property("a", str2).closeObj();
        }
      }
    }
    emitServerLog(paramJSWriter);
    paramJSWriter.closeList().append(");");
  }

  public static void emitServerLog(JSWriter paramJSWriter)
  {
    SessionData localSessionData = SessionData.tryGet();
    if ((localSessionData != null) && (localSessionData.getServerLog() != null))
    {
      ARServerLog localARServerLog = localSessionData.getServerLog();
      String str1 = localARServerLog.getLogMessage();
      if ((str1 != null) && (str1.length() > 0))
      {
        String str2 = Configuration.getInstance().getProperty("arsystem.log_category");
        if ((str2 != null) && (str2.indexOf(Log.MCategories[12]) != -1))
          MServerLog.fine("ARServer Log (API/FILTER/DATABASE)\n" + str1);
        paramJSWriter.listSep();
        paramJSWriter.openObj();
        paramJSWriter.property("t", 0);
        paramJSWriter.property("m", Validator.escape(str1));
        paramJSWriter.property("n", 8914);
        paramJSWriter.property("a", "");
        paramJSWriter.closeObj();
        localARServerLog.clearMessage();
      }
    }
  }

  protected static final int argToInt(String paramString)
    throws GoatException
  {
    try
    {
      return Integer.parseInt(paramString);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new GoatException("Wrong backchannel argument type (int)!", localNumberFormatException);
    }
  }

  protected static final long argToLong(String paramString)
    throws GoatException
  {
    try
    {
      return Long.parseLong(paramString);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new GoatException("Wrong backchannel argument type (long)!", localNumberFormatException);
    }
  }

  protected static final boolean argToBoolean(String paramString)
    throws GoatException
  {
    if (paramString.length() != 1)
      throw new GoatException("Wrong backchannel argument type! (bool-length)");
    if (paramString.charAt(0) == '0')
      return false;
    if (paramString.charAt(0) == '1')
      return true;
    throw new GoatException("Wrong backchannel argument type! (bool-char)");
  }

  protected static final double argToDouble(String paramString)
    throws GoatException
  {
    try
    {
      return Double.parseDouble(paramString);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new GoatException("Wrong backchannel argument type (double)!", localNumberFormatException);
    }
  }

  protected static final int getInitialArrayCount(String paramString)
    throws GoatException
  {
    int i = paramString.indexOf('/');
    if (i == -1)
      throw new GoatException("Backchannel array type: Bad formatting!");
    String str = paramString.substring(0, i);
    int j;
    try
    {
      j = Integer.parseInt(str);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new GoatException("Backchannel array type: Bad item count!", localNumberFormatException);
    }
    return j;
  }

  protected static final String[] argToStringArray(String paramString)
    throws GoatException
  {
    int i = getInitialArrayCount(paramString);
    String[] arrayOfString = new String[i];
    Parser localParser = new Parser(paramString, paramString.indexOf('/') + 1);
    for (int j = 0; j < i; j++)
      arrayOfString[j] = localParser.next();
    return arrayOfString;
  }

  protected static final int[] argToIntArray(String paramString)
    throws GoatException
  {
    int i = getInitialArrayCount(paramString);
    int[] arrayOfInt = new int[i];
    Parser localParser = new Parser(paramString, paramString.indexOf('/') + 1);
    try
    {
      for (int j = 0; j < i; j++)
        arrayOfInt[j] = Integer.parseInt(localParser.next());
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new GoatException("Integer array: Bad integer format!", localNumberFormatException);
    }
    return arrayOfInt;
  }

  protected static final long[] argToLongArray(String paramString)
    throws GoatException
  {
    int i = getInitialArrayCount(paramString);
    long[] arrayOfLong = new long[i];
    Parser localParser = new Parser(paramString, paramString.indexOf('/') + 1);
    try
    {
      for (int j = 0; j < i; j++)
        arrayOfLong[j] = Long.parseLong(localParser.next());
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new GoatException("Long array: Bad long format!", localNumberFormatException);
    }
    return arrayOfLong;
  }

  protected static final boolean[] argToBooleanArray(String paramString)
    throws GoatException
  {
    int i = getInitialArrayCount(paramString);
    boolean[] arrayOfBoolean = new boolean[i];
    Parser localParser = new Parser(paramString, paramString.indexOf('/') + 1);
    try
    {
      for (int j = 0; j < i; j++)
      {
        int k = Integer.parseInt(localParser.next());
        if (k == 0)
          arrayOfBoolean[j] = false;
        else if (k == 1)
          arrayOfBoolean[j] = true;
        else
          throw new GoatException("Boolean array: Bad boolean format 2!");
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new GoatException("Boolean array: Bad boolean format!", localNumberFormatException);
    }
    return arrayOfBoolean;
  }

  protected static final double[] argToDoubleArray(String paramString)
    throws GoatException
  {
    int i = getInitialArrayCount(paramString);
    double[] arrayOfDouble = new double[i];
    Parser localParser = new Parser(paramString, paramString.indexOf('/') + 1);
    try
    {
      for (int j = 0; j < i; j++)
        arrayOfDouble[j] = Double.parseDouble(localParser.next());
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new GoatException("Double array: Bad double format!", localNumberFormatException);
    }
    return arrayOfDouble;
  }

  public static final String returnError(String paramString, GoatException paramGoatException)
  {
    List localList = paramGoatException.getStatusInfo(paramString);
    JSWriter localJSWriter = new JSWriter();
    emitStatus(localJSWriter, localList, paramGoatException.canConvertIdToLabel());
    return localJSWriter.toString();
  }

  protected abstract void mapProperties(ArrayList paramArrayList)
    throws GoatException;

  protected abstract void process()
    throws GoatException, ARException;

  public Globule getGlobuleObject()
  {
    return this.mGlobule;
  }

  public void setGlobuleObject(Globule paramGlobule)
  {
    this.mGlobule = paramGlobule;
  }

  public static class Parser
  {
    private final String mArgStr;
    private int mPrev;

    public Parser(String paramString)
    {
      assert (paramString != null);
      this.mArgStr = paramString;
      this.mPrev = 0;
    }

    public Parser(String paramString, int paramInt)
    {
      assert (paramString != null);
      assert (paramInt > 0);
      this.mPrev = paramInt;
      this.mArgStr = paramString;
    }

    public String next()
      throws GoatException
    {
      int i = this.mArgStr.indexOf('/', this.mPrev);
      if (i == -1)
        return null;
      int j;
      try
      {
        j = Integer.parseInt(this.mArgStr.substring(this.mPrev, i));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new GoatException("Bad backchannel parameter - length scrambled", localNumberFormatException);
      }
      i++;
      if ((j < 0) || (i + j > this.mArgStr.length()))
        throw new GoatException("Bad backchannel parameter - length bad");
      this.mPrev = (i + j);
      return this.mArgStr.substring(i, i + j);
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.NDXRequest
 * JD-Core Version:    0.6.1
 */