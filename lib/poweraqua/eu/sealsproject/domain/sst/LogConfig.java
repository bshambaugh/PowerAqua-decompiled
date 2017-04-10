package eu.sealsproject.domain.sst;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class LogConfig
{
  private String path;
  private final String sealsanswer = "sealsanswer.log";
  private final String sealsquery = "sealsquestion.log";
  public long millis;
  public String question = null;
  public ArrayList<String> answers = new ArrayList();
  
  public LogConfig()
  {
    readLogLocation();
  }
  
  public void readLogLocation()
  {
    String fileName = "./logs.txt";
    try
    {
      FileInputStream fstream = new FileInputStream(fileName);
      
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      this.path = br.readLine();
      in.close();
      System.out.println("path for the log file " + this.path);
    }
    catch (Exception e)
    {
      System.err.println("Error: " + e.getMessage());
    }
  }
  
  public void readLogQuestion()
  {
    String logfile = this.path.concat("sealsquestion.log");
    try
    {
      System.out.println("Reading log file " + logfile);
      FileInputStream fstream = new FileInputStream(logfile);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String line = br.readLine();
      if (line != null)
      {
        this.millis = Long.parseLong(line);
        this.question = br.readLine();
      }
      else
      {
        System.out.println("the file is null");
      }
      in.close();
    }
    catch (Exception e)
    {
      System.err.println("Error: " + e.getMessage());
    }
  }
  
  public void readLogAnswers()
  {
    String logfile = this.path.concat("sealsanswer.log");
    try
    {
      System.out.println("Reading log file " + logfile);
      FileInputStream fstream = new FileInputStream(logfile);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String line = br.readLine();
      if (line != null) {
        this.millis = Long.parseLong(line);
      }
      int i = 1;
      String res = br.readLine();
      while (res != null)
      {
        this.answers.add(res);
        res = br.readLine();
        i++;
      }
      in.close();
    }
    catch (Exception e)
    {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
