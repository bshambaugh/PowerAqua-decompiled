package poweraqua.powermap.mappingModel;

import TrustEngine.userSession.UserDB;
import java.io.PrintStream;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import poweraqua.WordNetJWNL.WordNet;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.indexingService.manager.MultiIndexManager;
import poweraqua.lexicon.Lexicon;
import poweraqua.serviceConfig.MultiOntologyManager;

public class MappingSession
{
  private MultiIndexManager multiIndexManager;
  private static MultiOntologyManager multiOntologyManager;
  private String realpath = "";
  private UserDB userDB;
  private String userID = "";
  public static int watsonCalls = 0;
  public static int serqlCalls = 0;
  private static int virtuosoCalls = 0;
  private double sesame_threshold = 0.0D;
  private boolean useSynonyms = true;
  private Lexicon lexicon;
  
  public MappingSession()
    throws Exception
  {
    System.out.println("Creating mapping session");
    this.multiIndexManager = new MultiIndexManager();
    this.userDB = new UserDB();
    watsonCalls = 0;
    serqlCalls = 0;
    virtuosoCalls = 0;
    System.out.println("Java Version = " + System.getProperty("java.version"));
    WordNet.OpenDictionary();
    this.lexicon = new Lexicon();
    if ((!this.multiIndexManager.isUsePowerMap()) && (!this.multiIndexManager.isUseVirtuoso()))
    {
      ArrayList<OntologyPlugin> plugins = new ArrayList();
      multiOntologyManager = new MultiOntologyManager(plugins);
    }
    else
    {
      multiOntologyManager = new MultiOntologyManager();
    }
    PropertyConfigurator.configure("log4j.properties");
  }
  
  public MappingSession(String realPath)
    throws Exception
  {
    System.out.println("Creating mapping session with real path " + realPath);
    this.realpath = realPath;
    this.userDB = new UserDB(realPath);
    watsonCalls = 0;
    serqlCalls = 0;
    virtuosoCalls = 0;
    System.out.println("Java Version = " + System.getProperty("java.version"));
    WordNet.OpenDictionary(realPath);
    this.lexicon = new Lexicon(realPath);
    this.multiIndexManager = new MultiIndexManager(getRealpath());
    if (multiOntologyManager == null) {
      if ((!this.multiIndexManager.isUsePowerMap()) && (!this.multiIndexManager.isUseVirtuoso()))
      {
        ArrayList<OntologyPlugin> plugins = new ArrayList();
        multiOntologyManager = new MultiOntologyManager(realPath, plugins);
      }
      else
      {
        multiOntologyManager = new MultiOntologyManager(getRealpath());
      }
    }
    PropertyConfigurator.configure(realPath + "log4j.properties");
  }
  
  public MappingSession(String realPath, boolean useWatson)
    throws Exception
  {
    System.out.println("Creating mapping session with real path " + realPath);
    this.realpath = realPath;
    this.userDB = new UserDB(realPath);
    watsonCalls = 0;
    serqlCalls = 0;
    virtuosoCalls = 0;
    this.multiIndexManager = new MultiIndexManager(getRealpath(), useWatson);
    System.out.println("Java Version = " + System.getProperty("java.version"));
    WordNet.OpenDictionary(realPath);
    if (multiOntologyManager == null) {
      if ((!this.multiIndexManager.isUsePowerMap()) && (!this.multiIndexManager.isUseVirtuoso()))
      {
        ArrayList<OntologyPlugin> plugins = new ArrayList();
        multiOntologyManager = new MultiOntologyManager(realPath, plugins);
      }
      else
      {
        multiOntologyManager = new MultiOntologyManager(getRealpath());
      }
    }
    PropertyConfigurator.configure(realPath + "log4j.properties");
  }
  
  public MultiIndexManager getMultiIndexManager()
  {
    return this.multiIndexManager;
  }
  
  public MultiOntologyManager getMultiOntologyManager()
  {
    return multiOntologyManager;
  }
  
  public String getRealpath()
  {
    return this.realpath;
  }
  
  public static Logger getLog_poweraqua()
  {
    return Logger.getLogger("poweraqua");
  }
  
  public UserDB getUserSession()
  {
    return this.userDB;
  }
  
  public void setUserSession(UserDB userDB)
  {
    this.userDB = userDB;
  }
  
  public String getUserID()
  {
    return this.userID;
  }
  
  public void setUserID(String userID)
  {
    this.userID = userID;
  }
  
  public static int getWatsonCalls()
  {
    return watsonCalls;
  }
  
  public static int getSerqlCalls()
  {
    return serqlCalls;
  }
  
  public static int getVirtuosoCalls()
  {
    return virtuosoCalls;
  }
  
  public static void setVirtuosoCalls(int virtuosoCalls)
  {
    virtuosoCalls = virtuosoCalls;
  }
  
  public static void increaseVirtuosoCalls()
  {
    virtuosoCalls += 1;
  }
  
  public static void setWatsonCalls(int aWatsonCalls)
  {
    watsonCalls = aWatsonCalls;
  }
  
  public static void setSerqlCalls(int aSerqlCalls)
  {
    serqlCalls = aSerqlCalls;
  }
  
  public double getSesame_threshold()
  {
    return this.sesame_threshold;
  }
  
  public void setSesame_threshold(double aSesame_threshold)
  {
    this.sesame_threshold = aSesame_threshold;
  }
  
  public boolean isUseSynonyms()
  {
    return this.useSynonyms;
  }
  
  public void setUseSynonyms(boolean useSynonyms)
  {
    this.useSynonyms = useSynonyms;
  }
  
  public Lexicon getLexicon()
  {
    return this.lexicon;
  }
}

