package com.remedy.arsys.goat.intf.service;

import com.remedy.arsys.goat.ActiveLinkCollector;
import com.remedy.arsys.goat.Globule;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.GoatServerMessage;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.support.BrowserType;
import java.io.PrintWriter;

public abstract interface IDHTMLService extends IViewService
{
  public abstract Globule getHTMLData(FieldGraph paramFieldGraph, GoatServerMessage[] paramArrayOfGoatServerMessage)
    throws GoatException;

  public abstract Globule getHTMLData(FieldGraph paramFieldGraph)
    throws GoatException;

  public abstract Globule getJSData(FieldGraph paramFieldGraph, ActiveLinkCollector paramActiveLinkCollector)
    throws GoatException;

  public abstract void emitHelp(FieldGraph paramFieldGraph, PrintWriter paramPrintWriter)
    throws GoatException;

  public abstract Globule getVFHTMLData(FieldGraph paramFieldGraph, GoatServerMessage[] paramArrayOfGoatServerMessage)
    throws GoatException;

  public abstract Globule getVFJSData(FieldGraph paramFieldGraph, ActiveLinkCollector paramActiveLinkCollector)
    throws GoatException;

  public abstract Globule emitSystemSkins(FieldGraph paramFieldGraph, BrowserType paramBrowserType)
    throws GoatException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.intf.service.IDHTMLService
 * JD-Core Version:    0.6.1
 */