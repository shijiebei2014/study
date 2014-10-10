package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.NavBarItemField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.JSWriter;

public class NavBarItemFieldEmitter extends GoatFieldEmitter
{
  private NavBarItemField navBarItemField;

  public NavBarItemFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setNavBarItemField((NavBarItemField)paramGoatField);
  }

  private void setNavBarItemField(NavBarItemField paramNavBarItemField)
  {
    this.navBarItemField = paramNavBarItemField;
  }

  private NavBarItemField getNavBarItemField()
  {
    return this.navBarItemField;
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    paramJSWriter.property("v", isMVisible());
    paramJSWriter.property("l", getMLabel() == null ? "" : getMLabel());
    int i = paramNode.getEmitMode();
    if (i == 0)
      paramJSWriter.property("a", getMAccess());
  }

  protected int getMAccess()
  {
    return getNavBarItemField().getMAccess();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.NavBarItemFieldEmitter
 * JD-Core Version:    0.6.1
 */