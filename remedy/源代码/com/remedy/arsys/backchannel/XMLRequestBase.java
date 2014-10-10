package com.remedy.arsys.backchannel;

import com.bmc.arsys.api.ARException;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.service.XMLWriter;
import java.io.OutputStream;

public abstract class XMLRequestBase extends NDXRequest
{
  protected XMLWriter xmlWriter;

  public XMLRequestBase()
  {
  }

  public XMLRequestBase(String paramString)
  {
    super(paramString);
  }

  public XMLRequestBase(String paramString, OutputStream paramOutputStream)
  {
    super(paramString, paramOutputStream);
  }

  protected final void process()
    throws GoatException, ARException
  {
    this.xmlWriter = new XMLWriter();
    process(this.xmlWriter);
  }

  protected abstract void process(XMLWriter paramXMLWriter)
    throws GoatException, ARException;
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.backchannel.XMLRequestBase
 * JD-Core Version:    0.6.1
 */