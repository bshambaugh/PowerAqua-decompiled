package poweraqua.indexingService.manager.virtuoso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
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
import poweraqua.indexingService.manager.virtuoso.virtuosohelpers.Graph;
import poweraqua.indexingService.manager.virtuoso.virtuosohelpers.GraphVirtuoso;
import poweraqua.indexingService.manager.virtuoso.virtuosohelpers.ServerConfigVirtuoso;

public class VirtuosoServiceConfiguration
  implements Serializable
{
  private static final long serialVersionUID = 4291834248104406289L;
  private static final String VIRTUOSO_CONFIGURATION_FILE = "virtuoso_properties.xml";
  private static String labelsFile = "labels.txt";
  private static String[] labels;
  private String pluginsDirectory;
  private ArrayList<ServerConfigVirtuoso> serverConfigVirtuosoList;
  private static final String CONFIGURATION = "CONFIGURATION";
  private static final String SERVER = "SERVER";
  private static final String URL = "URL";
  private static final String PORT = "PORT";
  private static final String GRAPHS = "GRAPHS";
  private static final String GRAPH = "GRAPH";
  private static final String IRI = "IRI";
  private static final String TYPE = "TYPE";
  private static final String LOGIN = "LOGIN";
  private static final String PASSWORD = "PASSWORD";
  
  public VirtuosoServiceConfiguration()
    throws Exception
  {
    this.serverConfigVirtuosoList = new ArrayList();
    setPluginsDirectory(new String());
    readConfiguration("virtuoso_properties.xml");
    try
    {
      labels = readLabelsFromFile();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public VirtuosoServiceConfiguration(String path)
  {
    this.serverConfigVirtuosoList = new ArrayList();
    setPluginsDirectory(new String());
    readConfiguration(path + "virtuoso_properties.xml");
    try
    {
      labels = readLabelsFromFile(path);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private static String[] readLabelsFromFile()
    throws IOException
  {
    String file = getContents(new File(labelsFile));
    
    String[] labels = file.split(";");
    
    return labels;
  }
  
  private static String[] readLabelsFromFile(String path)
    throws IOException
  {
    String file = getContents(new File(path + labelsFile));
    
    String[] labels = file.split(";");
    
    return labels;
  }
  
  public static String getContents(File aFile)
    throws IOException
  {
    StringBuilder contents = new StringBuilder();
    
    BufferedReader input = new BufferedReader(new FileReader(aFile));
    try
    {
      String line = null;
      while ((line = input.readLine()) != null)
      {
        contents.append(line);
        contents.append(System.getProperty("line.separator"));
      }
    }
    finally
    {
      input.close();
    }
    return contents.toString();
  }
  
  public void writeConfigurationFile()
  {
    createConfigurationFile("virtuoso_properties.xml");
  }
  
  public void writeConfigurationFile(String path)
  {
    createConfigurationFile(path + "virtuoso_properties.xml");
  }
  
  public static String[] getLabels()
  {
    return labels;
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
    
    NodeList server = configuration.getElementsByTagName("SERVER");
    System.out.println("Number of server in xml file is " + server.getLength());
    ServerConfigVirtuoso serverConfig = null;
    for (int i = 0; i < server.getLength(); i++)
    {
      Element serverInfo = (Element)server.item(i);
      String sURL = "";String sPort = "";String sLogin = "";String sPassword = "";
      try
      {
        NodeList url = serverInfo.getElementsByTagName("URL");
        sURL = url.item(0).getFirstChild().getNodeValue();
        
        NodeList port = serverInfo.getElementsByTagName("PORT");
        sPort = port.item(0).getFirstChild() != null ? port.item(0).getFirstChild().getNodeValue() : null;
        
        NodeList login = serverInfo.getElementsByTagName("LOGIN");
        sLogin = login.item(0).getFirstChild() != null ? login.item(0).getFirstChild().getNodeValue() : null;
        
        NodeList password = serverInfo.getElementsByTagName("PASSWORD");
        sPassword = password.item(0).getFirstChild() != null ? password.item(0).getFirstChild().getNodeValue() : null;
        
        NodeList nodeGraphs = serverInfo.getElementsByTagName("GRAPH");
        
        LinkedList<Graph> graphs = new LinkedList();
        System.out.println("Number of graphs on server " + (i + 1) + " in xml file is " + nodeGraphs.getLength());
        for (int j = 0; j < nodeGraphs.getLength(); j++)
        {
          String sIRI = "";String sType = "";
          Element graphInfo = (Element)nodeGraphs.item(j);
          
          NodeList iri = graphInfo.getElementsByTagName("IRI");
          sIRI = iri.item(0).getFirstChild().getNodeValue();
          
          NodeList type = graphInfo.getElementsByTagName("TYPE");
          sType = type.item(0).getFirstChild().getNodeValue();
          
          graphs.add(new Graph(sIRI, sType));
        }
        serverConfig = new ServerConfigVirtuoso(sURL, sPort, sLogin, sPassword, graphs);
      }
      catch (DOMException ex)
      {
        System.out.println("server number " + i + " not valid");
        continue;
      }
      if (serverConfig != null) {
        this.serverConfigVirtuosoList.add(serverConfig);
      }
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
      for (ServerConfigVirtuoso serverConfigVirtuoso : getServerConfigVirtuosoList())
      {
        Element eServerConfigVirtuoso = newDoc.createElement("SERVER");
        
        Element field = newDoc.createElement("URL");
        field.appendChild(newDoc.createTextNode(serverConfigVirtuoso.getURL()));
        eServerConfigVirtuoso.appendChild(field);
        
        field = newDoc.createElement("PORT");
        field.appendChild(newDoc.createTextNode(serverConfigVirtuoso.getPort() == null ? "" : serverConfigVirtuoso.getPort()));
        eServerConfigVirtuoso.appendChild(field);
        
        field = newDoc.createElement("LOGIN");
        field.appendChild(newDoc.createTextNode(serverConfigVirtuoso.getLogin() == null ? "" : serverConfigVirtuoso.getLogin()));
        eServerConfigVirtuoso.appendChild(field);
        
        field = newDoc.createElement("PASSWORD");
        field.appendChild(newDoc.createTextNode(serverConfigVirtuoso.getPassword() == null ? "" : serverConfigVirtuoso.getPassword()));
        eServerConfigVirtuoso.appendChild(field);
        
        Element egraphs = newDoc.createElement("GRAPHS");
        for (Graph graph : serverConfigVirtuoso.getGraphs())
        {
          Element egraph = newDoc.createElement("GRAPH");
          
          Element eIRI = newDoc.createElement("IRI");
          eIRI.appendChild(newDoc.createTextNode(graph.getIRI()));
          egraph.appendChild(eIRI);
          
          Element eType = newDoc.createElement("TYPE");
          eType.appendChild(newDoc.createTextNode(graph.getType()));
          egraph.appendChild(eType);
          
          egraphs.appendChild(egraph);
        }
        eServerConfigVirtuoso.appendChild(egraphs);
        
        rootElement.appendChild(eServerConfigVirtuoso);
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
  
  public void addServerConfigVirtuoso(ServerConfigVirtuoso serverConfigVirtuoso)
  {
    if (!this.serverConfigVirtuosoList.contains(serverConfigVirtuoso)) {
      this.serverConfigVirtuosoList.add(serverConfigVirtuoso);
    }
  }
  
  public void addServerConfigVirtuosoList(ArrayList<ServerConfigVirtuoso> serverConfigVirtuosoList)
  {
    for (ServerConfigVirtuoso serverConfigVirtuoso : serverConfigVirtuosoList) {
      addServerConfigVirtuoso(serverConfigVirtuoso);
    }
  }
  
  public String getPluginsDirectory()
  {
    return this.pluginsDirectory;
  }
  
  public ArrayList<ServerConfigVirtuoso> getServerConfigVirtuosoList()
  {
    return this.serverConfigVirtuosoList;
  }
  
  public ArrayList<GraphVirtuoso> getGraphVirtuosoList()
  {
    ArrayList<GraphVirtuoso> list = new ArrayList();
    for (ServerConfigVirtuoso serverConfigVirtuoso : getServerConfigVirtuosoList()) {
      list.addAll(serverConfigVirtuoso.getGraphVirtuosoList());
    }
    return list;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    VirtuosoServiceConfiguration context = new VirtuosoServiceConfiguration();
    System.out.println("Plugins directory " + context.getPluginsDirectory() + "\n");
    for (ServerConfigVirtuoso serverConfigVirtuoso : context.getServerConfigVirtuosoList()) {
      System.out.println(serverConfigVirtuoso);
    }
    context.createConfigurationFile("test.xml");
  }
  
  public void setPluginsDirectory(String pluginsDirectory)
  {
    this.pluginsDirectory = pluginsDirectory;
  }
}

