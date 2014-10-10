package com.remedy.arsys.stubs;

import com.remedy.arsys.config.ConfigServlet;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.prefetch.PrefetchTask;
import com.remedy.arsys.share.Cache;
import com.remedy.arsys.share.GoatCacheManager;
import com.remedy.arsys.support.PwdUtility;
import com.remedy.arsys.support.Validator;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class GoatConfigServlet extends ConfigServlet
{
  private static final int MAX_PREFETCH_LEN = 512000;

  protected void updateCacheSettings(HttpServletRequest paramHttpServletRequest, HttpSession paramHttpSession, Enumeration paramEnumeration, Map paramMap)
  {
    paramHttpServletRequest.getSession().setAttribute("errorInsave", Boolean.valueOf(false));
    paramHttpServletRequest.getSession().setAttribute("prefetch_config_dirty", Boolean.valueOf(false));
    String[] arrayOfString = null;
    String str1 = null;
    String str2 = paramHttpServletRequest.getParameter("overflowToDisk");
    String str3 = null;
    Object localObject2;
    if (str2 != null)
    {
      localObject1 = cfg.getProperty("arsystem.ehcache.overflowToDisk", "false");
      str4 = cfg.getProperty("arsystem.ehcache.overflowToDisk", "false");
      try
      {
        if ((!((String)localObject1).equalsIgnoreCase(str2)) && (!str2.equals("")))
        {
          cfg.setProperty("arsystem.ehcache.overflowToDisk", str2);
          String str5 = cfg.getProperty("arsystem.ehcache.overflowToDiskTemp", "false");
          boolean bool = cfg.getOverflowToDisk();
          cfg.setProperty("arsystem.ehcache.overflowToDiskTemp", "" + (!bool));
          str3 = "The Mid-Tier requires a restart in order for temporary cache persistence configuration changes to take effect.";
          localObject2 = Cache.getCacheManager();
          GoatCacheManager.setOverflowToDisk(new Boolean(str2).booleanValue());
          str1 = ((GoatCacheManager)localObject2).update();
          if (str1 != null)
          {
            cfg.setProperty("arsystem.ehcache.overflowToDisk", (String)localObject1);
            cfg.setProperty("arsystem.ehcache.overflowToDiskTemp", str5);
          }
        }
      }
      catch (IOException localIOException1)
      {
        paramHttpSession.setAttribute("status", "Failed to modify Cache Settings.");
      }
    }
    Object localObject1 = Collections.synchronizedList(Collections.list(paramEnumeration));
    String str4 = null;
    Iterator localIterator = ((List)localObject1).iterator();
    int i = cfg.getCacheUpdateInterval();
    try
    {
      while (localIterator.hasNext())
      {
        localObject2 = (String)localIterator.next();
        if ((localObject2 != null) && (!((String)localObject2).equals("")))
        {
          localObject2 = ((String)localObject2).trim();
          localObject3 = "arsystem." + (String)localObject2;
          if (((String)localObject2).equalsIgnoreCase("prefetch_configuration"))
            paramHttpServletRequest.setCharacterEncoding("UTF-8");
          localObject4 = paramHttpServletRequest.getParameter((String)localObject2);
          if (localObject4 != null)
            localObject4 = ((String)localObject4).trim();
          if ("arsystem.cache_update_interval".equals(localObject3))
          {
            if ((localObject4 != null) && (!((String)localObject4).equals("")))
              try
              {
                Integer.parseInt((String)localObject4);
                cfg.setProperty((String)localObject3, (String)localObject4);
              }
              catch (NumberFormatException localNumberFormatException1)
              {
              }
          }
          else if ("arsystem.resource_expiry_interval".equals(localObject3))
          {
            if ((localObject4 != null) && (!((String)localObject4).equals("")))
              try
              {
                Integer.parseInt((String)localObject4);
                cfg.setProperty((String)localObject3, (String)localObject4);
              }
              catch (NumberFormatException localNumberFormatException2)
              {
              }
          }
          else if (((String)localObject2).equalsIgnoreCase("cache_flash_update_interval"))
          {
            if ((localObject4 != null) && (!((String)localObject4).equals("")))
              try
              {
                Integer.parseInt((String)localObject4);
                cfg.setProperty((String)localObject3, (String)localObject4);
              }
              catch (NumberFormatException localNumberFormatException3)
              {
              }
          }
          else if ("arsystem.cache_update_needed".equals(localObject3))
          {
            if ((localObject4 != null) && (((String)localObject4).length() > 0))
            {
              cfg.setProperty((String)localObject3, (String)localObject4);
              if (!cfg.getCacheUpdateNeeded())
                Cache.stopReaping();
              else
                Cache.initServerReapers();
            }
          }
          else if (((String)localObject2).equalsIgnoreCase("cache_flush"))
          {
            if ("Yes".equals(localObject4))
            {
              Cache.flush(false, null);
              PrefetchTask.schedule(10L);
            }
          }
          else if (((String)localObject2).equalsIgnoreCase("prefetch_configuration"))
          {
            if ((localObject4 != null) && (((String)localObject4).length() <= 512000) && (((String)localObject4).length() > 0))
            {
              String str6 = paramHttpServletRequest.getParameter("prefetch_config_dirty");
              if ((str6 != null) && (str6.equalsIgnoreCase("true")))
              {
                arrayOfString = PrefetchTask.writePreFetchConfiguration((String)localObject4);
                if ((arrayOfString[0] != null) && (arrayOfString[0].trim().length() > 0))
                {
                  str4 = "Failed to update prefetch config";
                  paramHttpServletRequest.getSession().setAttribute("errorInsave", Boolean.valueOf(true));
                  paramHttpServletRequest.getSession().setAttribute("prefetch_config_dirty", Boolean.valueOf(true));
                }
                else
                {
                  PrefetchTask.schedule(10L);
                }
              }
            }
          }
          else if ((((String)localObject2).equals("cache_action")) && ("syncServerCache".equals(localObject4)))
          {
            int k = Configuration.getInstance().getCacheUpdateInterval();
            if (k > 0)
            {
              String str7 = paramHttpServletRequest.getParameter("server");
              str7 = Validator.sanitizeCRandLF(str7);
              Cache.flush(true, str7);
              str4 = "Sync cache successfully submitted for server: " + str7 + ".";
            }
            else
            {
              str4 = "Cache Manager is operating in development-mode. Switch to production-mode to use the Sync Cache button.";
            }
          }
        }
      }
      int j = cfg.getCacheUpdateInterval();
      if ((j == 0) || (!cfg.getCacheUpdateNeeded()))
        Cache.stopReaping();
      else if (j != i)
        Cache.flush(true, null);
      if (str4 != null)
        paramHttpSession.setAttribute("status", str4);
      else
        paramHttpSession.setAttribute("status", "Successfully modified Cache Settings." + (str3 != null ? " " + str3 : ""));
      if (arrayOfString != null)
      {
        if (arrayOfString[0] != null)
          paramHttpSession.setAttribute("status1", Validator.sanitizeCRandLF(arrayOfString[0]));
        paramHttpSession.setAttribute("oldconfig", Validator.sanitizeCRandLF(arrayOfString[1]));
      }
      if (str1 != null)
      {
        paramHttpSession.setAttribute("status2", str1);
        paramHttpSession.setAttribute("status", "");
      }
      Object localObject3 = paramMap.keySet();
      Object localObject4 = (String[])((Set)localObject3).toArray(new String[0]);
      Arrays.sort((Object[])localObject4);
      paramHttpSession.setAttribute("ar_server_set", localObject4);
    }
    catch (IOException localIOException2)
    {
      paramHttpSession.setAttribute("status", "Failed to modify Cache Settings.");
    }
    super.updateCacheSettings(paramHttpServletRequest, paramHttpSession, paramEnumeration, paramMap);
  }

  protected void updateWebServiceSettings(HttpServletRequest paramHttpServletRequest, HttpSession paramHttpSession)
  {
    String str1 = paramHttpServletRequest.getParameter("wsUserName");
    String str2 = paramHttpServletRequest.getParameter("wsPwd");
    if (str1 != null)
      try
      {
        cfg.setProperty("arsystem.ws_anonymous_user", str1.trim());
        if (str2 != null)
        {
          str2 = PwdUtility.encryptPswdUsingBlowfish(str2);
          cfg.setProperty("arsystem.ws_anonymous_pwd", str2);
        }
        paramHttpSession.setAttribute("status", "Successfully modified Web Service Settings.");
      }
      catch (IOException localIOException)
      {
        paramHttpSession.setAttribute("status", "Failed to modify Web Service Settings.");
      }
    super.updateWebServiceSettings(paramHttpServletRequest, paramHttpSession);
  }

  protected void updateLogSettings(HttpServletRequest paramHttpServletRequest, HttpSession paramHttpSession, Enumeration paramEnumeration)
  {
    int i = 0;
    try
    {
      while (paramEnumeration.hasMoreElements())
      {
        String str1 = (String)paramEnumeration.nextElement();
        if ((str1 != null) && (!str1.equals("")))
        {
          str1 = str1.trim();
          String str2 = "arsystem." + str1;
          Object localObject1;
          Object localObject2;
          int j;
          if (str2.equalsIgnoreCase("arsystem.log_category"))
          {
            localObject1 = paramHttpServletRequest.getParameterValues("log_category");
            localObject2 = new StringBuilder();
            for (j = 0; (localObject1 != null) && (j < localObject1.length); j++)
            {
              if (j > 0)
                ((StringBuilder)localObject2).append(" ");
              ((StringBuilder)localObject2).append(localObject1[j]);
            }
            cfg.setProperty("arsystem.log_category", ((StringBuilder)localObject2).toString());
            paramHttpSession.setAttribute("status", "Successfully modified Log Settings.");
          }
          else if (str2.equalsIgnoreCase("arsystem.log_lastlines"))
          {
            localObject1 = paramHttpServletRequest.getParameter(str1);
            if (localObject1 != null)
            {
              localObject2 = ((String)localObject1).trim();
              paramHttpSession.setAttribute("LogLastLines", localObject2);
            }
          }
          else if (str2.equalsIgnoreCase("arsystem.log_filepath"))
          {
            localObject1 = paramHttpServletRequest.getParameter(str1);
            if (localObject1 != null)
            {
              localObject1 = ((String)localObject1).trim();
              try
              {
                localObject2 = new File((String)localObject1);
                j = 0;
                if (((File)localObject2).isDirectory())
                  j = 1;
                boolean bool;
                if (j == 0)
                  bool = ((File)localObject2).mkdirs();
                if (!bool)
                  throw new Exception("failed to create Log Directory.");
                localObject1 = ((File)localObject2).getAbsolutePath();
                cfg.setProperty(str2, (String)localObject1);
                paramHttpSession.setAttribute("status", "Successfully modified Log Settings.");
              }
              catch (Exception localException2)
              {
                paramHttpSession.setAttribute("status", "Failed to modify Log Settings, " + localException2.getMessage());
                throw new Exception("failed to create Log Directory.");
              }
            }
          }
          else if ((str2.equalsIgnoreCase("arsystem.log_level")) || (str2.equalsIgnoreCase("arsystem.log_viewer")) || (str2.equalsIgnoreCase("arsystem.log_filesize")) || (str2.equalsIgnoreCase("arsystem.log_backups")) || (str2.equalsIgnoreCase("arsystem.log_format")) || (str2.equalsIgnoreCase("arsystem.log_user")) || (str2.equalsIgnoreCase("arsystem.js_profile")))
          {
            localObject1 = paramHttpServletRequest.getParameter(str1);
            if (localObject1 != null)
            {
              localObject1 = ((String)localObject1).trim();
              cfg.setProperty(str2, (String)localObject1);
              paramHttpSession.setAttribute("status", "Successfully modified Log Settings.");
            }
          }
          else if (str1.equalsIgnoreCase("logger"))
          {
            localObject1 = paramHttpServletRequest.getParameter(str1);
            if (localObject1 != null)
            {
              localObject1 = ((String)localObject1).trim();
              if ("1".equals(localObject1))
                i = 1;
            }
          }
        }
      }
    }
    catch (IOException localIOException)
    {
      paramHttpSession.setAttribute("status", "Failed to modify Log Settings.");
    }
    catch (Exception localException1)
    {
    }
    if (i != 0)
    {
      System.out.println("Refreshing logging configuration");
      Log.RefreshConfig();
    }
    saveScrollBarCoordinates(paramHttpServletRequest, paramHttpSession);
    super.updateLogSettings(paramHttpServletRequest, paramHttpSession, paramEnumeration);
  }

  private void saveScrollBarCoordinates(HttpServletRequest paramHttpServletRequest, HttpSession paramHttpSession)
  {
    String str1 = paramHttpServletRequest.getParameter("scrollX");
    String str2 = paramHttpServletRequest.getParameter("scrollY");
    paramHttpSession.setAttribute("scrollX", parseInt(str1));
    paramHttpSession.setAttribute("scrollY", parseInt(str2));
  }

  protected void updateResources()
  {
    ResourceService.getInstance().start(Log.get(10));
  }

  private static String parseInt(String paramString)
  {
    if (paramString != null)
      try
      {
        Integer.parseInt(paramString);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        paramString = "0";
      }
    else
      paramString = "0";
    return paramString;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.stubs.GoatConfigServlet
 * JD-Core Version:    0.6.1
 */