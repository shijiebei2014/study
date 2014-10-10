package com.remedy.arsys.goat.field.type;

import com.bmc.arsys.api.Value;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.permissions.Group;
import com.remedy.arsys.stubs.ServerLogin;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class GroupType extends CharType
{
  private String mServer = null;

  public GroupType(Value paramValue, int paramInt, ServerLogin paramServerLogin)
    throws GoatException
  {
    super(paramValue, paramInt);
    if (paramServerLogin != null)
      this.mServer = paramServerLogin.getServer();
  }

  public GroupType(Value paramValue, int paramInt)
    throws GoatException
  {
    this(paramValue, paramInt, null);
  }

  public GroupType(Value paramValue, int paramInt, String paramString, ServerLogin paramServerLogin, FieldGraph.Node paramNode)
    throws GoatException
  {
    super(paramValue, paramInt, paramString, paramServerLogin, paramNode);
    this.mServer = paramServerLogin.getServer();
  }

  public GroupType(String paramString, int paramInt)
    throws GoatException
  {
    super(paramString, paramInt);
  }

  public String forHTML()
  {
    if (this.mServer != null)
      try
      {
        Map localMap = Group.getAllInstances(this.mServer);
        if (localMap != null)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          String[] arrayOfString = this.mValue.split(";");
          for (int i = 0; i < arrayOfString.length; i++)
            try
            {
              Long localLong = new Long(arrayOfString[i]);
              if (localLong.longValue() < 0L)
              {
                if (i != 0)
                  localStringBuilder.append(" ");
                localStringBuilder.append(arrayOfString[i]);
              }
              else
              {
                Group localGroup = (Group)localMap.get(localLong);
                if (localGroup != null)
                {
                  ArrayList localArrayList = localGroup.getGroupNameList();
                  Iterator localIterator = localArrayList.iterator();
                  while (localIterator.hasNext())
                  {
                    if (i != 0)
                      localStringBuilder.append(" ");
                    localStringBuilder.append(localIterator.next());
                  }
                }
              }
            }
            catch (NumberFormatException localNumberFormatException)
            {
              if (i != 0)
                localStringBuilder.append(" ");
              localStringBuilder.append(arrayOfString[i]);
            }
          return localStringBuilder.toString();
        }
      }
      catch (GoatException localGoatException)
      {
        localGoatException.printStackTrace();
      }
    return this.mValue;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.type.GroupType
 * JD-Core Version:    0.6.1
 */