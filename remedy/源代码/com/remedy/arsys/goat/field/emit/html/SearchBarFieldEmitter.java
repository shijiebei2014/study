package com.remedy.arsys.goat.field.emit.html;

import com.remedy.arsys.goat.ARBox;
import com.remedy.arsys.goat.Box;
import com.remedy.arsys.goat.field.FieldGraph.Node;
import com.remedy.arsys.goat.field.GoatField;
import com.remedy.arsys.goat.field.SearchBarField;
import com.remedy.arsys.goat.intf.field.emit.IEmitterFactory;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;

public class SearchBarFieldEmitter extends GoatFieldEmitter
{
  private SearchBarField searchBarField;

  public SearchBarFieldEmitter(GoatField paramGoatField, IEmitterFactory paramIEmitterFactory)
  {
    super(paramGoatField, paramIEmitterFactory);
  }

  public void setGf(GoatField paramGoatField)
  {
    super.setGf(paramGoatField);
    setSearchBarField((SearchBarField)paramGoatField);
  }

  private void setSearchBarField(SearchBarField paramSearchBarField)
  {
    this.searchBarField = paramSearchBarField;
  }

  private SearchBarField getSearchBarField()
  {
    return this.searchBarField;
  }

  public void emitOpenMarkup(FieldGraph.Node paramNode, HTMLWriter paramHTMLWriter)
  {
    super.emitOpenMarkup(paramNode, paramHTMLWriter);
    StringBuilder localStringBuilder = new StringBuilder();
    paramHTMLWriter.openTag("div").attr("arid", getMFieldID()).attr("artype", "SearchBar").attr("ardbn", getMDBName());
    paramHTMLWriter.attr("ardcf", 1);
    paramHTMLWriter.attr("class", "AdvancedSearchBarField " + getSelectorClassNames());
    localStringBuilder.append(getMARBox().toBox().toCSS());
    localStringBuilder.append("visibility:hidden;");
    if (getMZOrder() != -1L)
      localStringBuilder.append("z-index:" + getMZOrder() + ";");
    paramHTMLWriter.attr("style", localStringBuilder.toString());
    paramHTMLWriter.endTag(false);
    paramHTMLWriter.openTag("label").attr("class", "hidden").attr("for", "arid" + getMFieldID()).endTag(false);
    paramHTMLWriter.cdata(getMLabel());
    paramHTMLWriter.closeTag("label");
    paramHTMLWriter.closeTag("div");
  }

  public void emitDefaults(FieldGraph.Node paramNode, JSWriter paramJSWriter)
  {
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.field.emit.html.SearchBarFieldEmitter
 * JD-Core Version:    0.6.1
 */