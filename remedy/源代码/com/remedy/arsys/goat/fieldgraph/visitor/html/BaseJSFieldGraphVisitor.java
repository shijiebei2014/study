package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.intf.service.IFieldGraphVisitor;
import com.remedy.arsys.share.JSWriter;

public abstract class BaseJSFieldGraphVisitor
  implements IFieldGraphVisitor
{
  protected JSWriter jw;
  protected IEmitterFactory emitterFactory;

  public BaseJSFieldGraphVisitor(JSWriter paramJSWriter, IEmitterFactory paramIEmitterFactory)
  {
    this.jw = paramJSWriter;
    this.emitterFactory = paramIEmitterFactory;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.BaseJSFieldGraphVisitor
 * JD-Core Version:    0.6.1
 */