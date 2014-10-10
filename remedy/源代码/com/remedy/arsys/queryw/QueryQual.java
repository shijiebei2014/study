package com.remedy.arsys.queryw;

import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.type.DateType;
import com.remedy.arsys.goat.field.type.EnumType;
import com.remedy.arsys.goat.field.type.TODType;
import com.remedy.arsys.goat.field.type.TimeType;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;
import java.util.ArrayList;
import java.util.List;

public class QueryQual
{
  private final int NO_OP = -2;
  private final int ONE_OP = -1;
  private int mAllOP = -2;
  private final List<OPRec> mOps = new ArrayList();
  private final List<String> msgList = new ArrayList();

  boolean setOP(int paramInt)
  {
    if ((paramInt == 1) || (paramInt == 2))
      if (this.mAllOP == -2)
      {
        this.mAllOP = paramInt;
      }
      else
      {
        if (this.mAllOP != paramInt)
        {
          String str = "QueryConfig:qualifier: operators not the same, using first one";
          pushMessage(str);
          return false;
        }
        return true;
      }
    return false;
  }

  void pushMessage(String paramString)
  {
    this.msgList.add(paramString);
  }

  void addtoQual(int paramInt1, DataType paramDataType, int paramInt2, String paramString, Value paramValue, FieldGraph.Node paramNode, boolean paramBoolean)
  {
    this.mOps.add(new OPRec(paramInt1, paramDataType, paramInt2, paramString, paramValue, paramNode, paramBoolean));
  }

  public List<OPRec> getOps()
  {
    return this.mOps;
  }

  public int getMatch()
  {
    return this.mAllOP;
  }

  public class OPRec
  {
    private final int mRelOP;
    private final DataType mdataType;
    private final int mlFID;
    private final String mlLabel;
    private final Value mValue;
    private final FieldGraph.Node mNode;
    private final boolean mShowMenu;

    OPRec(int paramDataType, DataType paramInt1, int paramString, String paramValue, Value paramNode, FieldGraph.Node paramBoolean, boolean arg8)
    {
      this.mRelOP = paramDataType;
      this.mdataType = paramInt1;
      this.mlFID = paramString;
      this.mlLabel = paramValue;
      this.mValue = paramNode;
      this.mNode = paramBoolean;
      boolean bool;
      this.mShowMenu = bool;
    }

    public String getLabel()
    {
      return this.mlLabel;
    }

    public boolean getShowMenu()
    {
      return this.mShowMenu;
    }

    public String getOperation()
    {
      String str1 = SessionData.get().getLocale();
      switch (this.mRelOP)
      {
      case 1:
        if (this.mValue.getDataType().toInt() == 0)
          return MessageTranslation.getLocalizedErrorMessage(str1, 6600, null);
        if (getDataType() == 7)
          return MessageTranslation.getLocalizedErrorMessage(str1, 6611, null);
        return MessageTranslation.getLocalizedErrorMessage(str1, 6602, null);
      case 2:
        if (getDataType() == 7)
          return MessageTranslation.getLocalizedErrorMessage(str1, 6614, null);
        return MessageTranslation.getLocalizedErrorMessage(str1, 6607, null);
      case 4:
        if (getDataType() == 7)
          return MessageTranslation.getLocalizedErrorMessage(str1, 6613, null);
        return MessageTranslation.getLocalizedErrorMessage(str1, 6609, null);
      case 3:
        if (DataType.TIME.equals(Integer.valueOf(this.mRelOP)))
          return MessageTranslation.getLocalizedErrorMessage(str1, 6616, null);
        return MessageTranslation.getLocalizedErrorMessage(str1, 6608, null);
      case 5:
        if (getDataType() == 7)
          return MessageTranslation.getLocalizedErrorMessage(str1, 6613, null);
        return MessageTranslation.getLocalizedErrorMessage(str1, 6610, null);
      case 6:
        if (this.mValue.getDataType().toInt() == 0)
          return MessageTranslation.getLocalizedErrorMessage(str1, 6601, null);
        if (getDataType() == 7)
          return MessageTranslation.getLocalizedErrorMessage(str1, 6612, null);
        return MessageTranslation.getLocalizedErrorMessage(str1, 6603, null);
      case 7:
        String str2 = this.mValue.toString();
        if (str2.charAt(0) == '%')
        {
          if (str2.endsWith("%"))
            return MessageTranslation.getLocalizedErrorMessage(str1, 6604, null);
          return MessageTranslation.getLocalizedErrorMessage(str1, 6606, null);
        }
        if (str2.endsWith("%"))
          return MessageTranslation.getLocalizedErrorMessage(str1, 6605, null);
        break;
      }
      return "";
    }

    public String getValue()
      throws GoatException
    {
      if (this.mValue.getDataType().toInt() == 0)
        return "";
      Object localObject;
      if (getDataType() == 7)
      {
        localObject = new TimeType(this.mValue, this.mlFID);
        return ((TimeType)localObject).forHTML();
      }
      if (getDataType() == 13)
      {
        localObject = new DateType(this.mValue, this.mlFID);
        return ((DateType)localObject).forHTML();
      }
      if (getDataType() == 14)
      {
        localObject = new TODType(this.mValue, this.mlFID);
        return ((TODType)localObject).forHTML();
      }
      if (getDataType() == 6)
      {
        localObject = new EnumType(this.mValue, this.mlFID, this.mNode);
        return ((EnumType)localObject).forHTML();
      }
      if (this.mRelOP == 7)
      {
        localObject = this.mValue.toString();
        if (((String)localObject).charAt(0) == '%')
          localObject = ((String)localObject).substring(1);
        int i = ((String)localObject).length();
        if (((String)localObject).charAt(i - 1) == '%')
          localObject = ((String)localObject).substring(0, i - 1);
        return localObject;
      }
      return this.mValue.toString();
    }

    public int getDataType()
    {
      return this.mdataType.toInt();
    }
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.queryw.QueryQual
 * JD-Core Version:    0.6.1
 */