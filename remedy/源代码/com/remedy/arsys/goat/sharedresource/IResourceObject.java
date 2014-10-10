package com.remedy.arsys.goat.sharedresource;

import com.remedy.arsys.goat.Emitter;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.skins.SkinDefinitionMap;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract interface IResourceObject
{
  public abstract void emitJS(Emitter paramEmitter, boolean paramBoolean, SkinDefinitionMap paramSkinDefinitionMap)
    throws GoatException;

  public abstract void transmit(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.sharedresource.IResourceObject
 * JD-Core Version:    0.6.1
 */