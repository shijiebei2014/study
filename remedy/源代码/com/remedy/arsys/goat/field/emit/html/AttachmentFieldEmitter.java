package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.field.AttachmentField;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.JSWriter;

public class AttachmentFieldEmitter extends GoatFieldEmitter
{
  private AttachmentField attachmentField;

  public AttachmentFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setAttachmentField((AttachmentField)paramGoatField);
  }

  private void setAttachmentField(AttachmentField paramAttachmentField)
  {
    this.attachmentField = paramAttachmentField;
  }

  private AttachmentField getAttachmentField()
  {
    return this.attachmentField;
  }

  protected void emitScriptProperties(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitScriptProperties(paramNode, paramJSWriter);
    if (!isMVisible())
      paramJSWriter.property("hdn", 1);
    paramJSWriter.property("lbl", getMLabel() == null ? "" : getMLabel());
    paramJSWriter.property("pnt", getMParentFieldID());
    if (getMSizeLimit() > 0L)
      paramJSWriter.property("msz", getMSizeLimit());
    if ((isMDisableChange()) || (!isMInView()))
      paramJSWriter.property("dcf", 1);
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
    super.emitDefaults(paramNode, paramJSWriter);
    paramJSWriter.property("v", isMVisible());
    paramJSWriter.property("l", getMLabel() == null ? "" : getMLabel());
  }

  protected long getMSizeLimit()
  {
    return getAttachmentField().getMSizeLimit();
  }

  protected boolean isMDisableChange()
  {
    return getAttachmentField().isMDisableChange();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.AttachmentFieldEmitter
 * JD-Core Version:    0.6.1
 */