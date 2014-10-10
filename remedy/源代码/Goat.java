import com.remedy.arsys.goat.FileFormContext;
import com.remedy.arsys.goat.GoatException;
import com.remedy.arsys.goat.GoatImage;
import com.remedy.arsys.goat.UserDataEmitter;
import com.remedy.arsys.goat.field.FieldGraph;
import com.remedy.arsys.goat.service.DHTMLRequestService;
import com.remedy.arsys.log.MeasureTime;
import com.remedy.arsys.stubs.ServerLogin;
import com.remedy.arsys.stubs.SessionData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Goat
{
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      ClassPathXmlApplicationContext localClassPathXmlApplicationContext = new ClassPathXmlApplicationContext("/META-INF/applicationContext.xml");
      DHTMLRequestService localDHTMLRequestService = (DHTMLRequestService)localClassPathXmlApplicationContext.getBean("dhtmlRequestService");
      String str1 = "http://localhost/arsys/";
      SessionData.set(new SessionData());
      if ((paramArrayOfString.length == 0) || ((paramArrayOfString.length > 0) && (paramArrayOfString[0].equals("HTTPD"))))
      {
        if (!$assertionsDisabled)
          throw new AssertionError();
      }
      else if (paramArrayOfString.length > 0)
      {
        String str2 = paramArrayOfString[0];
        if (str2.charAt(0) == '+')
        {
          str2 = str2.substring(1);
          str1 = "http://" + str2 + "/arsys/";
          System.out.println("Using server with midtier " + str2);
        }
        else
        {
          System.out.println("Using midtier-less server " + str2);
        }
        new FileFormContext(str1, "", "", "." + File.separator);
        ServerLogin localServerLogin = SessionData.get().getServerLogin(str2);
        String str3 = paramArrayOfString.length > 1 ? paramArrayOfString[1] : null;
        String str4 = paramArrayOfString.length > 2 ? paramArrayOfString[2] : null;
        if ((str3 == null) || (str3.length() <= 0))
        {
          System.out.println("Form name sould not be empty");
          return;
        }
        FieldGraph localFieldGraph = FieldGraph.get(str2, str3, str4);
        PrintWriter localPrintWriter = new PrintWriter(new FileOutputStream("form.html"));
        try
        {
          localDHTMLRequestService.emitHTMLToFile(localFieldGraph, localPrintWriter);
        }
        finally
        {
          localPrintWriter.close();
        }
        localPrintWriter = new PrintWriter(new FileOutputStream("form.js"));
        try
        {
          localDHTMLRequestService.emitJSToFile(localFieldGraph, localPrintWriter);
        }
        finally
        {
          localPrintWriter.close();
        }
        localPrintWriter = new PrintWriter(new FileOutputStream("help.html"));
        try
        {
          localDHTMLRequestService.emitHelp(localFieldGraph, localPrintWriter);
        }
        finally
        {
          localPrintWriter.close();
        }
        localPrintWriter = new PrintWriter(new FileOutputStream("userdata.js"));
        try
        {
          new UserDataEmitter(str2, str3, null, localPrintWriter, localFieldGraph.getViewID(), localFieldGraph.getViewInfo());
        }
        finally
        {
          localPrintWriter.close();
        }
        GoatImage.writeToDisc();
        localServerLogin.logout();
      }
      else
      {
        System.out.println("Usage: goat [+]<server> [form] [view]");
      }
    }
    catch (GoatException localGoatException)
    {
      System.out.println("TOP LEVEL Goat Exception: " + localGoatException.toString());
    }
    catch (IOException localIOException)
    {
      System.out.println("TOP LEVEL IO Exception - probably error writing form.html");
    }
    MeasureTime.showTotals();
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     Goat
 * JD-Core Version:    0.6.1
 */