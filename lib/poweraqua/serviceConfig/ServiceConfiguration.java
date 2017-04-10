package poweraqua.serviceConfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ServiceConfiguration
  implements Serializable
{
  private static final String CONFIGURATION_FILE = "service_properties.xml";
  private String pluginsDirectory;
  private ArrayList<Repository> respositories;
  
  public ServiceConfiguration()
    throws Exception
  {
    this.respositories = new ArrayList();
    setPluginsDirectory(new String());
  }
  
  public void readConfigurationFile()
  {
    readConfiguration("service_properties.xml");
  }
  
  public void readConfigurationFile(String path)
  {
    readConfiguration(path + "service_properties.xml");
  }
  
  public void writeConfigurationFile()
  {
    createConfigurationFile("service_properties.xml");
  }
  
  public void writeConfigurationFile(String path)
  {
    createConfigurationFile(path + "service_properties.xml");
  }
  
  private void readConfiguration(String path)
  {
    DOMParser parser = new DOMParser();
    try
    {
      parser.parse(path);
    }
    catch (IOException ex)
    {
      System.out.println("Imposible to read the configuration file " + ex.getMessage());
      ex.printStackTrace();
    }
    catch (SAXException ex)
    {
      System.out.println("Imposible to read the configuration file " + ex.getMessage());
      ex.printStackTrace();
    }
    Document doc = parser.getDocument();
    Element configuration = doc.getDocumentElement();
    
    NodeList pluginManager = configuration.getElementsByTagName("PLUGIN_MANAGER");
    setPluginsDirectory(pluginManager.item(0).getFirstChild().getNodeValue());
    
    NodeList repositories = configuration.getElementsByTagName("REPOSITORY");
    System.out.println("Number of repositories in xml file is " + repositories.getLength());
    for (int i = 0; i < repositories.getLength(); i++)
    {
      Element repositoryInfo = (Element)repositories.item(i);
      String Sserver = "";String Sproxy = "";String Sport = "";String Slogin = "";String Spassword = "";String SpluginType = "";String SrepositoryName = "";String Stype = "";
      try
      {
        NodeList server = repositoryInfo.getElementsByTagName("SERVER");
        Sserver = server.item(0).getFirstChild().getNodeValue();
        
        NodeList proxy = repositoryInfo.getElementsByTagName("PROXY");
        Sproxy = proxy.item(0).getFirstChild() != null ? proxy.item(0).getFirstChild().getNodeValue() : null;
        
        NodeList port = repositoryInfo.getElementsByTagName("PORT");
        Sport = port.item(0).getFirstChild() != null ? port.item(0).getFirstChild().getNodeValue() : null;
        
        NodeList login = repositoryInfo.getElementsByTagName("LOGIN");
        Slogin = login.item(0).getFirstChild() != null ? login.item(0).getFirstChild().getNodeValue() : null;
        
        NodeList password = repositoryInfo.getElementsByTagName("PASSWORD");
        Spassword = password.item(0).getFirstChild() != null ? password.item(0).getFirstChild().getNodeValue() : null;
        
        NodeList typeOfplugin = repositoryInfo.getElementsByTagName("PLUGIN_TYPE");
        SpluginType = typeOfplugin.item(0).getFirstChild().getNodeValue();
        
        NodeList repositoryName = repositoryInfo.getElementsByTagName("REPOSITORY_NAME");
        SrepositoryName = repositoryName.item(0).getFirstChild().getNodeValue();
        
        NodeList repositoryType = repositoryInfo.getElementsByTagName("TYPE");
        Stype = repositoryType.item(0).getFirstChild().getNodeValue();
      }
      catch (DOMException ex)
      {
        System.out.println("Repository number " + i + " not valid");
        continue;
      }
      Repository repository;
      Repository repository;
      if ((SpluginType.contains("virtuoso")) || (SpluginType.contains("remoteSPARQL"))) {
        repository = new RepositoryVirtuoso(Sserver, Sproxy, Sport, Slogin, Spassword, SpluginType, SrepositoryName, Stype);
      } else {
        repository = new Repository(Sserver, Sproxy, Sport, Slogin, Spassword, SpluginType, SrepositoryName, Stype);
      }
      this.respositories.add(repository);
    }
  }
  
  private void createConfigurationFile(String path)
  {
    try
    {
      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
      Document newDoc = domBuilder.newDocument();
      
      Element rootElement = newDoc.createElement("CONFIGURATION");
      newDoc.appendChild(rootElement);
      
      Element pDirectory = newDoc.createElement("PLUGIN_MANAGER");
      pDirectory.appendChild(newDoc.createTextNode(getPluginsDirectory()));
      rootElement.appendChild(pDirectory);
      for (Repository repository : getRepositories())
      {
        Element eRepository = newDoc.createElement("REPOSITORY");
        
        Element field = newDoc.createElement("SERVER");
        field.appendChild(newDoc.createTextNode(repository.getServerURL()));
        eRepository.appendChild(field);
        
        field = newDoc.createElement("PROXY");
        field.appendChild(newDoc.createTextNode(repository.getProxy() == null ? "" : repository.getProxy()));
        eRepository.appendChild(field);
        
        field = newDoc.createElement("PORT");
        field.appendChild(newDoc.createTextNode(repository.getPort() == null ? "" : repository.getPort()));
        eRepository.appendChild(field);
        
        field = newDoc.createElement("LOGIN");
        field.appendChild(newDoc.createTextNode(repository.getLogin() == null ? "" : repository.getLogin()));
        eRepository.appendChild(field);
        
        field = newDoc.createElement("PASSWORD");
        field.appendChild(newDoc.createTextNode(repository.getPassword() == null ? "" : repository.getPassword()));
        eRepository.appendChild(field);
        
        field = newDoc.createElement("PLUGIN_TYPE");
        field.appendChild(newDoc.createTextNode(repository.getPluginType()));
        eRepository.appendChild(field);
        
        field = newDoc.createElement("REPOSITORY_NAME");
        field.appendChild(newDoc.createTextNode(repository.getRepositoryName()));
        eRepository.appendChild(field);
        
        field = newDoc.createElement("TYPE");
        field.appendChild(newDoc.createTextNode(repository.getRepositoryType()));
        eRepository.appendChild(field);
        
        rootElement.appendChild(eRepository);
      }
      TransformerFactory tranFactory = TransformerFactory.newInstance();
      Transformer aTransformer = tranFactory.newTransformer();
      Source src = new DOMSource(newDoc);
      Result dest = new StreamResult(new File(path));
      aTransformer.transform(src, dest);
    }
    catch (TransformerConfigurationException ex)
    {
      ex.printStackTrace();
    }
    catch (DOMException ex)
    {
      ex.printStackTrace();
    }
    catch (ParserConfigurationException ex)
    {
      ex.printStackTrace();
    }
    catch (TransformerException ex)
    {
      ex.printStackTrace();
    }
    catch (TransformerFactoryConfigurationError ex)
    {
      ex.printStackTrace();
    }
  }
  
  public void addRepository(Repository repository)
  {
    if (!this.respositories.contains(repository)) {
      this.respositories.add(repository);
    }
  }
  
  public void addRepositoryList(ArrayList<Repository> repositoryList)
  {
    for (Repository repository : repositoryList) {
      addRepository(repository);
    }
  }
  
  public String getPluginsDirectory()
  {
    return this.pluginsDirectory;
  }
  
  public ArrayList<Repository> getRepositories()
  {
    return this.respositories;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    ServiceConfiguration context = new ServiceConfiguration();
    context.readConfigurationFile();
    System.out.println("Plugins directory " + context.getPluginsDirectory() + "\n");
    for (Repository repository : context.getRepositories()) {
      System.out.println(repository);
    }
  }
  
  public void setPluginsDirectory(String pluginsDirectory)
  {
    this.pluginsDirectory = pluginsDirectory;
  }
}

