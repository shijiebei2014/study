package com.remedy.arsys.goat.intf.field.emit;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;

public abstract interface IGoatFieldEmitter
{
  public abstract void setGf(GoatField paramGoatField);

  public abstract void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter);

  public abstract void emitCloseMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter);

  public abstract void emitFlowVertSpaceMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter, long paramLong);

  public abstract void emitOpenHelp(HTMLWriter paramHTMLWriter);

  public abstract void emitCloseHelp(HTMLWriter paramHTMLWriter);

  public abstract void emitScript(FieldGraph.Node paramNode, JSWriter paramJSWriter);

  public abstract void emitTableProperties(JSWriter paramJSWriter);

  public abstract void emitPushFieldProperties(JSWriter paramJSWriter);

  public abstract void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter);
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.field.emit.IGoatFieldEmitter
 * JD-Core Version:    0.6.1
 */