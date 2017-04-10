package poweraqua.indexingService.creator;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MultiIndexServiceConfiguration
{
  private static final String MULTI_INDEX_CONFIGURATION_FILE = "multi_index_properties.xml";
  private String db;
  private String login;
  private String password;
  private String index_global_path;
  private boolean watson = false;
  private boolean virtuoso = false;
  private boolean powermap = true;
  private boolean remoteSPARQLVirtuoso = false;
  private ArrayList<MultiOntologyIndexBean> indexList;
  
  public MultiIndexServiceConfiguration()
  {
    readConfigurationFile("multi_index_properties.xml");
  }
  
  public MultiIndexServiceConfiguration(String realPath)
  {
    readConfigurationFile(realPath + "multi_index_properties.xml");
  }
  
  private void readConfigurationFile(String path)
  {
    DOMParser parser = new DOMParser();
    try
    {
      parser.parse(path);
    }
    catch (SAXException ex)
    {
      ex.printStackTrace();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    Document doc = parser.getDocument();
    Element configuration = doc.getDocumentElement();
    
    NodeList list = configuration.getElementsByTagName("WATSON");
    try
    {
      String w = list.item(0).getFirstChild().getNodeValue();
      setWatson(Boolean.valueOf(w).booleanValue());
    }
    catch (DOMException ex)
    {
      ex.printStackTrace();
    }
    list = configuration.getElementsByTagName("POWERMAP");
    setPowermap(Boolean.valueOf(list.item(0).getFirstChild().getNodeValue()).booleanValue());
    
    list = configuration.getElementsByTagName("VIRTUOSO");
    setVirtuoso(Boolean.valueOf(list.item(0).getFirstChild().getNodeValue()).booleanValue());
    
    list = configuration.getElementsByTagName("REMOTESPARQLVIRTUOSO");
    setRemoteSPARQLVirtuoso(Boolean.valueOf(list.item(0).getFirstChild().getNodeValue()).booleanValue());
    
    list = configuration.getElementsByTagName("ONTOLOGY_INDEX_DB");
    setDb(list.item(0).getFirstChild().getNodeValue());
    
    list = configuration.getElementsByTagName("ONTOLOGY_INDEX_DB_LOGIN");
    setLogin(list.item(0).getFirstChild() != null ? list.item(0).getFirstChild().getNodeValue() : new String());
    
    list = configuration.getElementsByTagName("ONTOLOGY_INDEX_DB_PASSWORD");
    setPassword(list.item(0).getFirstChild() != null ? list.item(0).getFirstChild().getNodeValue() : new String());
    
    list = configuration.getElementsByTagName("INDEX_GLOBAL_PATH");
    setIndex_global_path(list.item(0).getFirstChild() != null ? list.item(0).getFirstChild().getNodeValue() : new String());
    
    NodeList indexes = configuration.getElementsByTagName("INDEX_INFO_FOLDER");
    System.out.println("Number of indexes is " + indexes.getLength());
    this.indexList = new ArrayList();
    for (int i = 0; i < indexes.getLength(); i++)
    {
      String indexFolder = indexes.item(i).getFirstChild() != null ? indexes.item(i).getFirstChild().getNodeValue() : null;
      MultiOntologyIndexBean bean = new MultiOntologyIndexBean(indexFolder);
      getIndexList().add(bean);
    }
  }
  
  public ArrayList<MultiOntologyIndexBean> getIndexList()
  {
    return this.indexList;
  }
  
  public static void main(String[] args)
  {
    MultiIndexServiceConfiguration isc = new MultiIndexServiceConfiguration();
    for (MultiOntologyIndexBean ib : isc.getIndexList()) {
      System.out.println(ib);
    }
  }
  
  public String getDb()
  {
    return this.db;
  }
  
  public void setDb(String db)
  {
    this.db = db;
  }
  
  public String getLogin()
  {
    return this.login;
  }
  
  public void setLogin(String login)
  {
    this.login = login;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public String getIndex_global_path()
  {
    return this.index_global_path;
  }
  
  public void setIndex_global_path(String index_global_path)
  {
    this.index_global_path = index_global_path;
  }
  
  public boolean isWatson()
  {
    return this.watson;
  }
  
  public void setWatson(boolean watson)
  {
    this.watson = watson;
  }
  
  public boolean isPowermap()
  {
    return this.powermap;
  }
  
  public void setPowermap(boolean powermap)
  {
    this.powermap = powermap;
  }
  
  public boolean isVirtuoso()
  {
    return this.virtuoso;
  }
  
  public void setVirtuoso(boolean virtuoso)
  {
    this.virtuoso = virtuoso;
  }
  
  public boolean isRemoteSPARQLVirtuoso()
  {
    return this.remoteSPARQLVirtuoso;
  }
  
  public void setRemoteSPARQLVirtuoso(boolean remoteSPARQLVirtuoso)
  {
    this.remoteSPARQLVirtuoso = remoteSPARQLVirtuoso;
  }
}

