package com.remedy.arsys.goat;

import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

public class WindowsDateTimeFormats
{
  private static final String WINDOWS_FORMATS_FILE = "Cultures.xml";
  private HashMap mWindowsFormats;
  private static WindowsDateTimeFormats mInstance = new WindowsDateTimeFormats();
  protected static final Log arLog = Log.get(11);

  private WindowsDateTimeFormats()
  {
    try
    {
      String str1 = Configuration.getInstance().getRootPath();
      String str2 = str1 + File.separator + "WEB-INF" + File.separator + "Cultures.xml";
      SAXParser localSAXParser = SAXParserFactory.newInstance().newSAXParser();
      LocaleContentHandler localLocaleContentHandler = new LocaleContentHandler();
      localSAXParser.parse(new FileInputStream(str2), localLocaleContentHandler);
      this.mWindowsFormats = localLocaleContentHandler.getCultures();
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      arLog.log(Level.SEVERE, localParserConfigurationException.toString());
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    catch (IOException localIOException)
    {
      arLog.log(Level.SEVERE, localIOException.toString());
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
    catch (SAXException localSAXException)
    {
      arLog.log(Level.SEVERE, localSAXException.toString());
      if (!$assertionsDisabled)
        throw new AssertionError();
    }
  }

  public static LocaleContentHandler.CultureInfo get(String paramString)
  {
    return (LocaleContentHandler.CultureInfo)mInstance.mWindowsFormats.get(paramString);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.WindowsDateTimeFormats
 * JD-Core Version:    0.6.1
 */