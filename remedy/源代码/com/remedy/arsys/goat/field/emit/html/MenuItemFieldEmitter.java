package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.MenuItemField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.JSWriter;

public class MenuItemFieldEmitter extends GoatFieldEmitter
{
  public MenuItemFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
  }

  protected void emitScriptProperties(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitScriptProperties(paramNode, paramJSWriter);
    paramJSWriter.property("pnt", getMParentFieldID());
    paramJSWriter.property("lbl", getMLabel() == null ? "" : getMLabel());
    paramJSWriter.property("m", getMMode());
    paramJSWriter.property("mp", getMMenuParent());
    paramJSWriter.property("p", getMPosition());
    paramJSWriter.property("v", getGf().isMVisible() ? 1 : 0);
    paramJSWriter.property("s", getMAccess());
  }

  public int getMAccess()
  {
    return ((MenuItemField)getGf()).getMAccess();
  }

  public int getMMode()
  {
    return ((MenuItemField)getGf()).getMMode();
  }

  public long getMMenuParent()
  {
    return ((MenuItemField)getGf()).getMMenuParent();
  }

  public int getMPosition()
  {
    return ((MenuItemField)getGf()).getMPosition();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.MenuItemFieldEmitter
 * JD-Core Version:    0.6.1
 */