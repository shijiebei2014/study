package com.remedy.arsys.share;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.StringTokenizer;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Commandline.Argument;

public class VirusChecker
{
  private String workDir;
  private String executable;
  private String fileToScan;
  private String outputFile;
  private static final String DELIMITER = "--------------";
  private static final String FIELD_DELIMITER = ":";
  private boolean cleanInfectedFiles = false;
  public final int TOTAL_ANALYZED_INDEX = 0;
  public final int TOTAL_INFECTED_INDEX = 1;
  public final int PUP_FOUND_INDEX = 2;
  public final int INFECTIONS_REPAIRED_INDEX = 3;
  public final int PUP_REPAIRED_INDEX = 4;
  public final int WARNINGS_INDEX = 5;
  public int[] results = new int[6];
  public String[] names = new String[6];
  public static final int ITEM_COUNT = 6;

  private void scan()
  {
    ExecTask localExecTask = new ExecTask();
    localExecTask.setDir(new File(this.workDir));
    localExecTask.setExecutable(this.executable);
    localExecTask.createArg().setLine("/SCAN=" + this.fileToScan);
    localExecTask.createArg().setLine("/REPORT=" + this.outputFile);
    if (this.cleanInfectedFiles)
      localExecTask.createArg().setLine("/CLEAN");
    localExecTask.setFailIfExecutionFails(true);
    localExecTask.execute();
  }

  private void deleteOutputFile()
  {
    try
    {
      Delete localDelete = new Delete();
      localDelete.setFile(new File(this.outputFile));
      localDelete.execute();
    }
    catch (Exception localException)
    {
      System.err.println("Cannot delete output file '" + this.outputFile + "'.  See details...\n" + localException.getLocalizedMessage());
    }
  }

  private void parseOutput()
  {
    try
    {
      BufferedReader localBufferedReader = new BufferedReader(new FileReader(this.outputFile));
      String str1 = null;
      for (int i = 0; (str1 = localBufferedReader.readLine()) != null; i++)
      {
        System.out.println("Line " + i + ": " + str1);
        if (str1.startsWith("--------------"))
        {
          str1 = localBufferedReader.readLine();
          for (int j = 0; (!str1.startsWith("--------------")) && (str1 != null) && (j < 6); j++)
          {
            StringTokenizer localStringTokenizer = new StringTokenizer(str1, ":");
            for (int k = 0; localStringTokenizer.hasMoreTokens(); k++)
            {
              String str2 = localStringTokenizer.nextToken();
              switch (k)
              {
              case 0:
                this.names[j] = new String(str2.trim());
                break;
              case 1:
                this.results[j] = Integer.parseInt(str2.trim());
              }
            }
            str1 = localBufferedReader.readLine();
          }
        }
      }
      localBufferedReader.close();
    }
    catch (Exception localException)
    {
      System.out.println("Cannot parse output file '" + this.outputFile + "'.  More info " + localException.getLocalizedMessage());
    }
  }

  private boolean checkResult()
  {
    for (int i = 0; i < 6; i++)
      System.out.println(this.names[i] + " : " + this.results[i]);
    if (this.results[1] > 0)
    {
      System.err.println("--------------\n\nVIRUS DETECTED !!!!!!\n\n--------------");
      return true;
    }
    return false;
  }

  public boolean run()
  {
    scan();
    parseOutput();
    boolean bool = checkResult();
    deleteOutputFile();
    return bool;
  }

  public static void main(String[] paramArrayOfString)
  {
    VirusChecker localVirusChecker = new VirusChecker();
    localVirusChecker.setExecutable("C:/Program Files (x86)/AVG/AVG9/avgscanx.exe");
    localVirusChecker.setWorkDir("c:/temp");
    localVirusChecker.setFileToScan("C:/temp/virus.com");
    localVirusChecker.setOutputFile("C:/temp/virusCheckOutput.txt");
    localVirusChecker.run();
  }

  public String getWorkDir()
  {
    return this.workDir;
  }

  public void setWorkDir(String paramString)
  {
    this.workDir = paramString;
  }

  public String getExecutable()
  {
    return this.executable;
  }

  public void setExecutable(String paramString)
  {
    this.executable = paramString;
  }

  public String getFileToScan()
  {
    return this.fileToScan;
  }

  public void setFileToScan(String paramString)
  {
    this.fileToScan = paramString;
  }

  public String getOutputFile()
  {
    return this.outputFile;
  }

  public void setOutputFile(String paramString)
  {
    this.outputFile = paramString;
  }

  public void setCleanInfectedFiles(boolean paramBoolean)
  {
    this.cleanInfectedFiles = true;
  }

  public boolean getCleanInfectedFiles()
  {
    return this.cleanInfectedFiles;
  }
}

/* Location:           D:\temp\原来桌面的\webapps\midtier_hpia32\WEB-INF\lib\MidTier.jar
 * Qualified Name:     com.remedy.arsys.share.VirusChecker
 * JD-Core Version:    0.6.1
 */