package com.remedy.arsys.backchannel;

import com.remedy.arsys.goat.FormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.queryw.QueryConfig;
import com.remedy.arsys.queryw.QueryQual.OPRec;
import com.remedy.arsys.share.HTMLWriter;
import com.remedy.arsys.share.JSWriter;
import com.remedy.arsys.share.MessageTranslation;
import com.remedy.arsys.stubs.SessionData;
import java.util.Iterator;
import java.util.List;

public class LoadQueryWidgetAgent extends NDXLoadQueryWidget
{
  protected HTMLWriter mWidgetHTML;
  protected JSWriter mWidgetJS;
  protected String mImagePath;
  private int tabIndex;

  public LoadQueryWidgetAgent(String paramString)
  {
    super(paramString);
  }

  protected void process()
    throws GoatException
  {
    QueryConfig localQueryConfig = new QueryConfig(this.mGuid, this.mServer);
    if (!localQueryConfig.isInitialized())
    {
      append("this.result=-1;");
      return;
    }
    localQueryConfig.init();
    if (!localQueryConfig.isInitialized())
    {
      append("this.result=0;");
      return;
    }
    String str1 = localQueryConfig.getJSMenu();
    String str2 = localQueryConfig.getJSFieldInfo();
    boolean bool = localQueryConfig.parseQual(this.mQual);
    this.tabIndex = 100;
    this.mWidgetHTML = new HTMLWriter();
    this.mWidgetJS = new JSWriter();
    this.mImagePath = FormContext.get().getRelativeContextURL();
    String str3 = "QB";
    this.mWidgetHTML.openTag("div");
    this.mWidgetHTML.attr("id", str3);
    this.mWidgetHTML.attr("class", "queryBuilder");
    this.mWidgetHTML.endTag();
    String str4 = SessionData.get().getLocale();
    String str5 = MessageTranslation.getLocalizedText(str4, "Match");
    this.mWidgetHTML.openTag("div").attr("class", "queryAndOr").endTag();
    this.mWidgetHTML.openTag("label").endTag().cdata(str5).closeTag("label");
    String str6 = MessageTranslation.getLocalizedText(str4, "All");
    if ((bool) && (localQueryConfig.getMatch() == 2))
      str6 = MessageTranslation.getLocalizedText(str4, "Any");
    createInput(str6, true, 4, "");
    this.mWidgetHTML.closeTag("div");
    if ((bool) && (localQueryConfig.getOperations().size() > 0))
    {
      List localList = localQueryConfig.getOperations();
      int i = localList.size() - 1;
      int j = 0;
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        QueryQual.OPRec localOPRec = (QueryQual.OPRec)localIterator.next();
        addRow(localOPRec, i == j++);
      }
    }
    else
    {
      addRow("", "", "", 4, false, true);
    }
    this.mWidgetHTML.closeTag("div");
    append("this.result=").openObj().property("h", this.mWidgetHTML.toString()).append(",d:").append(str2).property("m", str1).property("i", this.tabIndex).closeObj().append(";");
  }

  protected void addRow(QueryQual.OPRec paramOPRec, boolean paramBoolean)
    throws GoatException
  {
    addRow(paramOPRec.getLabel(), paramOPRec.getOperation(), paramOPRec.getValue(), paramOPRec.getDataType(), paramOPRec.getShowMenu(), paramBoolean);
  }

  protected void addRow(String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    String str = "queryRow";
    if (paramBoolean2)
      str = str + " queryRowLast";
    if (paramString3 == null)
      paramString3 = "";
    this.mWidgetHTML.openTag("div").attr("class", str);
    if (paramString1.length() > 0)
      this.mWidgetHTML.attr("fld", paramString1);
    this.mWidgetHTML.endTag();
    this.mWidgetHTML.openTag("div").attr("class", "queryAddDel").endTag();
    this.mWidgetHTML.openTag("a").attr("class", "btnflt toolbarBtn").attr("href", "javascript:");
    if (!paramBoolean2)
      this.mWidgetHTML.attr("style", "visibility:hidden");
    this.mWidgetHTML.attr("tabIndex", this.tabIndex + 8);
    this.mWidgetHTML.endTag();
    this.mWidgetHTML.openTag("div").attr("class", "queryAddBtn");
    this.mWidgetHTML.endTag();
    this.mWidgetHTML.append("<img alt='add' src='" + this.mImagePath + "resources/images/add.png'>");
    this.mWidgetHTML.closeTag("div");
    this.mWidgetHTML.closeTag("a");
    this.mWidgetHTML.openTag("a").attr("class", "btnflt toolbarBtn queryDelBtn").attr("href", "javascript:").attr("tabIndex", this.tabIndex + 9).endTag();
    this.mWidgetHTML.openTag("div").attr("class", "queryDelBtn").endTag();
    this.mWidgetHTML.append("<img alt='remove' src='" + this.mImagePath + "resources/images/delete.png'>");
    this.mWidgetHTML.closeTag("div");
    this.mWidgetHTML.closeTag("a");
    this.mWidgetHTML.closeTag("div");
    this.mWidgetHTML.openTag("div").attr("class", "queryField").endTag();
    createInput(paramString1, true, paramInt, "Field Name");
    this.mWidgetHTML.closeTag("div");
    this.mWidgetHTML.openTag("div").attr("class", "queryOperator");
    this.mWidgetHTML.attr("fld", paramString1);
    this.mWidgetHTML.endTag();
    createInput(paramString2, true, paramInt, "Operator");
    this.mWidgetHTML.closeTag("div");
    this.mWidgetHTML.openTag("div");
    if ((paramString2.length() > 0) && (paramString3.length() == 0))
      this.mWidgetHTML.attr("class", "queryValue dfro");
    else
      this.mWidgetHTML.attr("class", "queryValue");
    this.mWidgetHTML.endTag();
    createInput(paramString3, paramBoolean1, paramInt, "Field Value");
    this.mWidgetHTML.closeTag("div");
    this.mWidgetHTML.closeTag("div");
    this.tabIndex += 2;
  }

  protected void createInput(String paramString1, boolean paramBoolean, int paramInt, String paramString2)
  {
    this.mWidgetHTML.openTag("div").attr("class", "queryFieldInner").endTag();
    this.mWidgetHTML.append("<input tabIndex=" + this.tabIndex++);
    this.mWidgetHTML.append(" alt='" + paramString2);
    this.mWidgetHTML.append("' value='" + paramString1);
    String str = "text queryFieldName";
    if (paramBoolean)
      str = str + " dropdown";
    this.mWidgetHTML.append("' class='" + str + "' type='text'>");
    this.mWidgetHTML.append("<a tabIndex=" + this.tabIndex++);
    if ((!paramBoolean) && (paramInt != 6))
      this.mWidgetHTML.append(" style='visibility:hidden'");
    this.mWidgetHTML.append(" class='btn btn3d'><img class='btnimg dropdown' src='" + this.mImagePath + "resources/images/field_menu.gif' alt='menu'></a>");
    if (!paramBoolean)
    {
      this.mWidgetHTML.append("<a class='btn btn3d' tabIndex=" + this.tabIndex++);
      if ((paramInt != 13) && (paramInt != 7))
        this.mWidgetHTML.append(" style='visibility:hidden'");
      this.mWidgetHTML.append("><img class='btnimg calendar' src='" + this.mImagePath + "resources/images/field_calendar.gif' alt='date picker'></a>");
      this.mWidgetHTML.append("<a class='btn btn3d' tabIndex=" + this.tabIndex++);
      if (paramInt != 14)
        this.mWidgetHTML.append(" style='visibility:hidden'");
      this.mWidgetHTML.append("><img class='btnimg calendar' src='" + this.mImagePath + "resources/images/field_time.gif' alt='time picker'></a>");
    }
    this.mWidgetHTML.closeTag("div");
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.LoadQueryWidgetAgent
 * JD-Core Version:    0.6.1
 */