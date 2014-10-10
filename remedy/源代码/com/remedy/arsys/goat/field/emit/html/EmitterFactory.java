package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.goat.intf.field.emit.IGoatFieldEmitter;
import java.util.HashMap;
import java.util.Map;

public final class EmitterFactory
  implements IEmitterFactory
{
  private Map<String, IGoatFieldEmitter> emitterCache = new HashMap();

  public IGoatFieldEmitter getEmitter(GoatField paramGoatField)
  {
    String str = paramGoatField.getClass().getSimpleName();
    Object localObject = (IGoatFieldEmitter)this.emitterCache.get(str);
    if (localObject != null)
    {
      ((IGoatFieldEmitter)localObject).setGf(paramGoatField);
      return localObject;
    }
    if ("AlertListField".equals(str))
      localObject = new TableFieldEmitter(paramGoatField, this);
    else if ("AppListField".equals(str))
      localObject = new AppListFieldEmitter(paramGoatField, this);
    else if ("AttachmentField".equals(str))
      localObject = new AttachmentFieldEmitter(paramGoatField, this);
    else if ("AttachmentPoolField".equals(str))
      localObject = new AttachmentPoolFieldEmitter(paramGoatField, this);
    else if ("CharField".equals(str))
      localObject = new CharFieldEmitter(paramGoatField, this);
    else if ("ColumnField".equals(str))
      localObject = new ColumnFieldEmitter(paramGoatField, this);
    else if ("ControlField".equals(str))
      localObject = new ControlFieldEmitter(paramGoatField, this);
    else if ("CurrencyField".equals(str))
      localObject = new CurrencyFieldEmitter(paramGoatField, this);
    else if ("DateField".equals(str))
      localObject = new DateFieldEmitter(paramGoatField, this);
    else if ("DecimalField".equals(str))
      localObject = new DecimalFieldEmitter(paramGoatField, this);
    else if ("DiaryField".equals(str))
      localObject = new DiaryFieldEmitter(paramGoatField, this);
    else if ("DisplayField".equals(str))
      localObject = new ViewFieldEmitter(paramGoatField, this);
    else if ("EnumField".equals(str))
      localObject = new EnumFieldEmitter(paramGoatField, this);
    else if ("GroupField".equals(str))
      localObject = new CharFieldEmitter(paramGoatField, this);
    else if ("HorzNavBarField".equals(str))
      localObject = new HorzNavBarFieldEmitter(paramGoatField, this);
    else if ("IntegerField".equals(str))
      localObject = new IntegerFieldEmitter(paramGoatField, this);
    else if ("NavBarItemField".equals(str))
      localObject = new NavBarItemFieldEmitter(paramGoatField, this);
    else if ("PageField".equals(str))
      localObject = new PageFieldEmitter(paramGoatField, this);
    else if ("PageHolderField".equals(str))
      localObject = new PageHolderFieldEmitter(paramGoatField, this);
    else if ("RealField".equals(str))
      localObject = new RealFieldEmitter(paramGoatField, this);
    else if ("SearchBarField".equals(str))
      localObject = new SearchBarFieldEmitter(paramGoatField, this);
    else if ("StatusHistoryField".equals(str))
      localObject = new StatusHistoryFieldEmitter(paramGoatField, this);
    else if ("TODField".equals(str))
      localObject = new TODFieldEmitter(paramGoatField, this);
    else if ("TableField".equals(str))
      localObject = new TableFieldEmitter(paramGoatField, this);
    else if ("TimeField".equals(str))
      localObject = new TimeFieldEmitter(paramGoatField, this);
    else if ("TrimField".equals(str))
      localObject = new TrimFieldEmitter(paramGoatField, this);
    else if ("VertNavBarField".equals(str))
      localObject = new VertNavBarFieldEmitter(paramGoatField, this);
    else if ("ViewField".equals(str))
      localObject = new ViewFieldEmitter(paramGoatField, this);
    else if ("MenuItemField".equals(str))
      localObject = new MenuItemFieldEmitter(paramGoatField, this);
    if (localObject != null)
    {
      this.emitterCache.put(str, localObject);
      return localObject;
    }
    throw new IllegalArgumentException("unknown GoatField type: " + str);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.EmitterFactory
 * JD-Core Version:    0.6.1
 */