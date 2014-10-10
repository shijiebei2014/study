package com.remedy.arsys.goat.sharedresource;

import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.Globule;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.skins.SkinDefinitionMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultResourceObject extends Globule
  implements IResourceObject
{
  private final byte[] mData;

  public DefaultResourceObject(byte[] paramArrayOfByte, String paramString)
    throws GoatException
  {
    super(paramArrayOfByte, paramString, System.currentTimeMillis(), 3);
    this.mData = paramArrayOfByte;
  }

  public void emitJS(Emitter paramEmitter, boolean paramBoolean, SkinDefinitionMap paramSkinDefinitionMap)
    throws GoatException
  {
  }

  public void transmit(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
  {
    super.transmit(paramHttpServletRequest, paramHttpServletResponse);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.sharedresource.DefaultResourceObject
 * JD-Core Version:    0.6.1
 */