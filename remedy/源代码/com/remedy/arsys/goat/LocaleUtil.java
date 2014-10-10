package com.remedy.arsys.goat;

import com.ibm.icu.util.TimeZone;
import com.remedy.arsys.config.ConfigProperties;
import com.remedy.arsys.config.Configuration;
import com.remedy.arsys.log.Log;
import com.remedy.arsys.stubs.SessionData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class LocaleUtil
{
  public static final ConfigProperties MLocaleMap;
  public static final Map localeList;
  public static final Set timezoneList = Collections.unmodifiableSet(localHashSet);

  public static String getDecimalSep(String paramString)
  {
    LCNumInfo localLCNumInfo = (LCNumInfo)localeList.get(paramString);
    return localLCNumInfo != null ? localLCNumInfo.dec : ".";
  }

  public static String getThousandSep(String paramString)
  {
    LCNumInfo localLCNumInfo = (LCNumInfo)localeList.get(paramString);
    return localLCNumInfo != null ? localLCNumInfo.thsn : "";
  }

  public static String FormatReal(String paramString, Double paramDouble, int paramInt)
  {
    NumberFormat localNumberFormat = NumberFormat.getNumberInstance(new Locale("en", "US"));
    String str = "";
    if ((paramDouble.doubleValue() >= 1000000000.0D) || (paramDouble.doubleValue() <= -1000000000.0D))
    {
      DecimalFormat localDecimalFormat = new DecimalFormat();
      StringBuilder localStringBuilder = new StringBuilder("0.");
      for (int i = 0; i < paramInt; i++)
        localStringBuilder.append("#");
      localStringBuilder.append("E000");
      localDecimalFormat.applyPattern(localStringBuilder.toString());
      str = localDecimalFormat.format(paramDouble.doubleValue());
      str = str.replaceAll("E", "e+");
    }
    else
    {
      localNumberFormat.setMaximumFractionDigits(paramInt);
      localNumberFormat.setMinimumFractionDigits(paramInt);
      str = localNumberFormat.format(paramDouble.doubleValue());
    }
    str = str.replaceAll(",", "");
    return str.replaceAll("\\.", getDecimalSep(paramString));
  }

  public static String getLocaleConstantsScript(String paramString)
    throws GoatException
  {
    String str1 = SessionData.get().getLocale();
    String[] arrayOfString = normalizeLocale(str1);
    String str2 = "locale/" + arrayOfString[0] + "_" + arrayOfString[1] + ".js";
    File localFile = new File(paramString + str2);
    if (!localFile.exists())
    {
      Log.get(2).fine("Locale file not found - " + localFile.getPath());
      String str3 = null;
      if (str1 != null)
        str3 = mapToLocaleWithCountryCode(str1.substring(0, 2));
      if ((str3 != null) && (!str3.equals(str1)))
      {
        arrayOfString = normalizeLocale(str3);
        str2 = "locale/" + arrayOfString[0] + "_" + arrayOfString[1] + ".js";
        localFile = new File(paramString + str2);
        if (localFile.exists())
        {
          Log.get(2).fine("Instead using the Locale file - " + localFile.getPath());
          return str2;
        }
      }
      throw new GoatException(9356, arrayOfString[0] + "_" + arrayOfString[1]);
    }
    return str2;
  }

  public static String getTimezoneConstantsScript(String paramString1, String paramString2)
    throws GoatException
  {
    Object localObject1 = SessionData.get().getTimeZone();
    assert ((localObject1 != null) && (((String)localObject1).length() != 0));
    String str1 = "timezone/" + ((String)localObject1).replace('/', '_') + ".js";
    File localFile = new File(paramString1 + str1);
    String str2;
    Object localObject3;
    TimeZone localTimeZone;
    if (!localFile.exists())
    {
      localObject2 = null;
      try
      {
        int i = TimeZone.countEquivalentIDs((String)localObject1);
        for (int j = 0; j < i; j++)
        {
          str2 = TimeZone.getEquivalentID((String)localObject1, j);
          localObject3 = "timezone/" + str2.replace('/', '_') + ".js";
          localFile = new File(paramString1 + (String)localObject3);
          if (localFile.exists())
          {
            localObject2 = str2;
            break;
          }
        }
      }
      catch (Exception localException)
      {
        localTimeZone = TimeZone.getDefault();
        str2 = "timezone/" + localTimeZone.getID().replace('/', '_') + ".js";
        localFile = new File(paramString1 + str2);
        if (!localFile.exists())
        {
          localObject3 = TimeZone.getAvailableIDs(localTimeZone.getRawOffset());
          for (int k = 0; k < localObject3.length; k++)
          {
            str2 = "timezone/" + localObject3[k].replace('/', '_') + ".js";
            localFile = new File(paramString1 + str2);
            if (localFile.exists())
            {
              localObject2 = localObject3[k];
              break;
            }
          }
        }
        else
        {
          localObject2 = localTimeZone.getID();
        }
      }
      if (localObject2 == null)
        throw new GoatException(9357, localObject1);
      localObject1 = localObject2;
      SessionData.setDefaultTimeZone((String)localObject2);
      SessionData.get().setTimeZone((String)localObject2);
    }
    Object localObject2 = new byte[(int)localFile.length()];
    FileInputStream localFileInputStream = null;
    try
    {
      localFileInputStream = new FileInputStream(localFile);
      if (localFileInputStream.read((byte[])localObject2, 0, (int)localFile.length()) != localFile.length())
        throw new GoatException("File IO Error reading file: " + str1);
      localTimeZone = TimeZone.getTimeZone((String)localObject1);
      str2 = localTimeZone.getDisplayName(false, 0, getLocale(paramString2));
      localObject3 = localTimeZone.getDisplayName(true, 0, getLocale(paramString2));
      if (((String)localObject1).equals("Australia/Perth"))
        localObject3 = "GMT+09:00";
      StringBuffer localStringBuffer = new StringBuffer(50);
      localStringBuffer.append("var tzNames = [\"").append(str2).append("\", \"").append((String)localObject3).append("\"];\r\n");
      String str3 = "// " + str1 + "\r\n" + new String((byte[])localObject2) + localStringBuffer.toString();
      return str3;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      throw new GoatException(9357, localObject1);
    }
    catch (IOException localIOException1)
    {
      throw new GoatException("File IO Error reading file: " + str1);
    }
    finally
    {
      try
      {
        localFileInputStream.close();
      }
      catch (IOException localIOException2)
      {
        throw new GoatException("File IO Error closing file: " + str1);
      }
    }
  }

  public static boolean isValidTimezone(String paramString)
  {
    return timezoneList.contains(paramString);
  }

  public static String getLocalizedMessagesScript(String paramString)
  {
    return getLocalizedFileName(paramString, "LocalizedMessages", ".js");
  }

  public static String mapToLocaleWithCountryCode(String paramString)
  {
    String str = MLocaleMap.getString(paramString);
    return str != null ? str : paramString;
  }

  public static String FormatDecimal(String paramString, BigDecimal paramBigDecimal, int paramInt)
  {
    BigDecimal localBigDecimal = paramBigDecimal.setScale(paramInt, 4);
    String str = localBigDecimal.toString();
    str = str.replaceAll("\\.", getDecimalSep(paramString));
    StringBuilder localStringBuilder = new StringBuilder();
    int i = str.length() - 1;
    int j = 0;
    int k = str.indexOf(getDecimalSep(paramString)) - 1;
    if (k < 0)
      k = i;
    for (j = 0; j <= i; j++)
    {
      char c = str.charAt(j);
      localStringBuilder.append(c);
      if ((j < k) && ((k - j) % 3 == 0) && (c != '-'))
        localStringBuilder.append(getThousandSep(paramString));
    }
    return localStringBuilder.toString();
  }

  private static String getLocalizedFileName(String paramString1, String paramString2, String paramString3)
  {
    String str1 = SessionData.get().getLocale();
    String[] arrayOfString = normalizeLocale(str1);
    String str2 = paramString2 + "_" + arrayOfString[0] + "_" + arrayOfString[1] + paramString3;
    File localFile = new File(paramString1 + str2);
    if (!localFile.exists())
    {
      str2 = paramString2 + "_" + arrayOfString[0] + paramString3;
      localFile = new File(paramString1 + str2);
      if (!localFile.exists())
        str2 = paramString2 + paramString3;
    }
    return str2;
  }

  public static String[] normalizeLocale(String paramString)
  {
    assert (paramString != null);
    String str1 = null;
    String str2 = null;
    int i = paramString.indexOf('_');
    if (i == -1)
    {
      str1 = paramString;
    }
    else
    {
      str1 = paramString.substring(0, i);
      if (i <= paramString.length())
        str2 = paramString.substring(i + 1);
    }
    String[] arrayOfString = new String[2];
    arrayOfString[0] = str1.toLowerCase();
    arrayOfString[1] = (str2 == null ? null : str2.toUpperCase());
    return arrayOfString;
  }

  public static Locale getLocale(String paramString)
  {
    String[] arrayOfString = normalizeLocale(paramString);
    if (arrayOfString[1] == null)
      return new Locale(arrayOfString[0]);
    return new Locale(arrayOfString[0], arrayOfString[1]);
  }

  public static String getNormalizedLocale(String paramString)
  {
    assert (paramString != null);
    String[] arrayOfString = normalizeLocale(paramString);
    if (arrayOfString[1] == null)
      return arrayOfString[0];
    return arrayOfString[0] + "_" + arrayOfString[1];
  }

  static
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("af_ZA", new LCNumInfo(".", ","));
    localHashMap.put("ar_AE", new LCNumInfo(".", ","));
    localHashMap.put("ar_BH", new LCNumInfo(".", ","));
    localHashMap.put("ar_DZ", new LCNumInfo(".", ","));
    localHashMap.put("ar_EG", new LCNumInfo(".", ","));
    localHashMap.put("ar_IQ", new LCNumInfo(".", ","));
    localHashMap.put("ar_JO", new LCNumInfo(".", ","));
    localHashMap.put("ar_KW", new LCNumInfo(".", ","));
    localHashMap.put("ar_LB", new LCNumInfo(".", ","));
    localHashMap.put("ar_LY", new LCNumInfo(".", ","));
    localHashMap.put("ar_MA", new LCNumInfo(".", ","));
    localHashMap.put("ar_OM", new LCNumInfo(".", ","));
    localHashMap.put("ar_QA", new LCNumInfo(".", ","));
    localHashMap.put("ar_SA", new LCNumInfo(".", ","));
    localHashMap.put("ar_SY", new LCNumInfo(".", ","));
    localHashMap.put("ar_TN", new LCNumInfo(".", ","));
    localHashMap.put("ar_YE", new LCNumInfo(".", ","));
    localHashMap.put("be_BY", new LCNumInfo(",", " "));
    localHashMap.put("bg_BG", new LCNumInfo(",", " "));
    localHashMap.put("ca_ES", new LCNumInfo(",", "."));
    localHashMap.put("cs_CZ", new LCNumInfo(",", " "));
    localHashMap.put("da_DK", new LCNumInfo(",", "."));
    localHashMap.put("de_AT", new LCNumInfo(",", "."));
    localHashMap.put("de_CH", new LCNumInfo(".", "'"));
    localHashMap.put("de_DE", new LCNumInfo(",", "."));
    localHashMap.put("de_LU", new LCNumInfo(",", "."));
    localHashMap.put("el_GR", new LCNumInfo(",", "."));
    localHashMap.put("en_AU", new LCNumInfo(".", ","));
    localHashMap.put("en_CA", new LCNumInfo(".", ","));
    localHashMap.put("en_GB", new LCNumInfo(".", ","));
    localHashMap.put("en_IE", new LCNumInfo(".", ","));
    localHashMap.put("en_NZ", new LCNumInfo(".", ","));
    localHashMap.put("en_PH", new LCNumInfo(".", ","));
    localHashMap.put("en_US", new LCNumInfo(".", ","));
    localHashMap.put("en_ZA", new LCNumInfo(".", ","));
    localHashMap.put("en_ZW", new LCNumInfo(".", ","));
    localHashMap.put("es_AR", new LCNumInfo(",", "."));
    localHashMap.put("es_BO", new LCNumInfo(",", "."));
    localHashMap.put("es_CL", new LCNumInfo(",", "."));
    localHashMap.put("es_CO", new LCNumInfo(",", "."));
    localHashMap.put("es_CR", new LCNumInfo(",", "."));
    localHashMap.put("es_DO", new LCNumInfo(".", ","));
    localHashMap.put("es_EC", new LCNumInfo(",", "."));
    localHashMap.put("es_ES", new LCNumInfo(",", "."));
    localHashMap.put("es_GT", new LCNumInfo(".", ","));
    localHashMap.put("es_HN", new LCNumInfo(".", ","));
    localHashMap.put("es_MX", new LCNumInfo(".", ","));
    localHashMap.put("es_NI", new LCNumInfo(".", ","));
    localHashMap.put("es_PA", new LCNumInfo(".", ","));
    localHashMap.put("es_PE", new LCNumInfo(".", ","));
    localHashMap.put("es_PR", new LCNumInfo(".", ","));
    localHashMap.put("es_PY", new LCNumInfo(",", "."));
    localHashMap.put("es_SV", new LCNumInfo(".", ","));
    localHashMap.put("es_UY", new LCNumInfo(",", "."));
    localHashMap.put("es_VE", new LCNumInfo(",", "."));
    localHashMap.put("et_EE", new LCNumInfo(",", " "));
    localHashMap.put("eu_ES", new LCNumInfo(",", "."));
    localHashMap.put("fa_IR", new LCNumInfo(".", ","));
    localHashMap.put("fi_FI", new LCNumInfo(",", " "));
    localHashMap.put("fo_FO", new LCNumInfo(",", "."));
    localHashMap.put("fr_BE", new LCNumInfo(",", "."));
    localHashMap.put("fr_CA", new LCNumInfo(",", " "));
    localHashMap.put("fr_CH", new LCNumInfo(".", "'"));
    localHashMap.put("fr_FR", new LCNumInfo(",", " "));
    localHashMap.put("fr_LU", new LCNumInfo(",", " "));
    localHashMap.put("he_IL", new LCNumInfo(".", ","));
    localHashMap.put("hi_IN", new LCNumInfo(".", ","));
    localHashMap.put("hr_HR", new LCNumInfo(",", "."));
    localHashMap.put("hu_HU", new LCNumInfo(",", " "));
    localHashMap.put("id_ID", new LCNumInfo(",", "."));
    localHashMap.put("is_IS", new LCNumInfo(",", "."));
    localHashMap.put("it_CH", new LCNumInfo(".", "'"));
    localHashMap.put("it_IT", new LCNumInfo(",", "."));
    localHashMap.put("iw_IL", new LCNumInfo(".", ","));
    localHashMap.put("ja_JP", new LCNumInfo(".", ","));
    localHashMap.put("ko_KR", new LCNumInfo(".", ","));
    localHashMap.put("lt_LT", new LCNumInfo(",", "."));
    localHashMap.put("lv_LV", new LCNumInfo(",", " "));
    localHashMap.put("mk_MK", new LCNumInfo(",", "."));
    localHashMap.put("mr_IN", new LCNumInfo(".", ","));
    localHashMap.put("nb_NO", new LCNumInfo(",", " "));
    localHashMap.put("nl_BE", new LCNumInfo(",", "."));
    localHashMap.put("nl_NL", new LCNumInfo(",", "."));
    localHashMap.put("nn_NO", new LCNumInfo(",", " "));
    localHashMap.put("no_NO", new LCNumInfo(",", " "));
    localHashMap.put("pl_PL", new LCNumInfo(",", " "));
    localHashMap.put("pt_BR", new LCNumInfo(",", "."));
    localHashMap.put("pt_PT", new LCNumInfo(",", "."));
    localHashMap.put("ro_RO", new LCNumInfo(",", "."));
    localHashMap.put("ru_RU", new LCNumInfo(",", " "));
    localHashMap.put("sh_YU", new LCNumInfo(",", "."));
    localHashMap.put("sk_SK", new LCNumInfo(",", " "));
    localHashMap.put("sl_SI", new LCNumInfo(",", "."));
    localHashMap.put("sq_AL", new LCNumInfo(",", "."));
    localHashMap.put("sr_YU", new LCNumInfo(",", "."));
    localHashMap.put("sv_FI", new LCNumInfo(",", " "));
    localHashMap.put("sv_SE", new LCNumInfo(",", " "));
    localHashMap.put("sw_KE", new LCNumInfo(".", ","));
    localHashMap.put("ta_IN", new LCNumInfo(".", ","));
    localHashMap.put("th_TH", new LCNumInfo(".", ","));
    localHashMap.put("tr_TR", new LCNumInfo(",", "."));
    localHashMap.put("uk_UA", new LCNumInfo(",", " "));
    localHashMap.put("vi_VN", new LCNumInfo(",", "."));
    localHashMap.put("zh_CN", new LCNumInfo(".", ","));
    localHashMap.put("zh_HK", new LCNumInfo(".", ","));
    localHashMap.put("zh_SG", new LCNumInfo(".", ","));
    localHashMap.put("zh_TW", new LCNumInfo(".", ","));
    localeList = Collections.unmodifiableMap(localHashMap);
    MLocaleMap = new ConfigProperties(Configuration.getInstance().getFilename("locale.properties"), "");
    try
    {
      MLocaleMap.load(false);
    }
    catch (Exception localException)
    {
    }
    HashSet localHashSet = new HashSet();
    String[] arrayOfString = TimeZone.getAvailableIDs();
    for (int i = 0; i < arrayOfString.length; i++)
      localHashSet.add(arrayOfString[i]);
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.goat.LocaleUtil
 * JD-Core Version:    0.6.1
 */