package com.remedy.arsys.goat.fieldgraph.visitor.html;

import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.intf.service.IFieldGraphVisitor;
import com.remedy.arsys.share.HTMLWriter;

public abstract class BaseHTMLFieldGraphVisitor
  implements IFieldGraphVisitor
{
  protected HTMLWriter hw;
  protected IEmitterFactory emitterFactory;

  public BaseHTMLFieldGraphVisitor(HTMLWriter paramHTMLWriter, IEmitterFactory paramIEmitterFactory)
  {
    this.hw = paramHTMLWriter;
    this.emitterFactory = paramIEmitterFactory;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.fieldgraph.visitor.html.BaseHTMLFieldGraphVisitor
 * JD-Core Version:    0.6.1
 */