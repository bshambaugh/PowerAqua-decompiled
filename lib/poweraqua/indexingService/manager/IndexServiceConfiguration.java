package poweraqua.indexingService.manager;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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

public class IndexServiceConfiguration
{
  private static final String INDEX_CONFIGURATION_FILE = "index_properties.xml";
  private ArrayList<IndexBean> indexList;
  
  public IndexServiceConfiguration()
  {
    this.indexList = new ArrayList();
  }
  
  public void readConfigurationFile(String realPath)
  {
    readConfiguration(realPath + "index_properties.xml");
  }
  
  public void readConfigurationFile()
  {
    readConfiguration("index_properties.xml");
  }
  
  public void writeConfigurationFile(String realPath)
  {
    createConfigurationFile(realPath + "index_properties.xml");
  }
  
  public void writeConfigurationFile()
  {
    createConfigurationFile("index_properties.xml");
  }
  
  private void readConfiguration(String path)
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
    NodeList indexes = configuration.getElementsByTagName("INDEX");
    
    System.out.println("Number of indexes is " + indexes.getLength());
    IndexBean indexBean = null;
    for (int i = 0; i < indexes.getLength(); i++)
    {
      Element index = (Element)indexes.item(i);
      
      NodeList indexDir = index.getElementsByTagName("INDEX_DIRECTORY");
      String sIndexDir = indexDir.item(0).getFirstChild().getNodeValue();
      
      NodeList spellIndexDir = index.getElementsByTagName("SPELL_INDEX_DIRECTORY");
      String sSpellIndexDir = spellIndexDir.item(0).getFirstChild().getNodeValue();
      
      NodeList metadataIndexDB = index.getElementsByTagName("METADATA_INDEX_DB");
      String sMetadataIndexDB = metadataIndexDB.item(0).getFirstChild().getNodeValue();
      
      NodeList metadataIndexDB_login = index.getElementsByTagName("METADATA_INDEX_DB_LOGIN");
      String sMetadataIndexDB_login = metadataIndexDB_login.item(0).getFirstChild().getNodeValue();
      
      NodeList metadataIndexDB_password = index.getElementsByTagName("METADATA_INDEX_DB_PASSWORD");
      String sMetadataIndexDB_password = metadataIndexDB_password.item(0).getFirstChild() != null ? metadataIndexDB_password.item(0).getFirstChild().getNodeValue() : null;
      
      NodeList metadataIndexDB_table = index.getElementsByTagName("METADATA_INDEX_TABLE");
      String sMetadataIndexDB_table = metadataIndexDB_table.item(0).getFirstChild().getNodeValue();
      
      indexBean = new IndexBean(sIndexDir, sSpellIndexDir, sMetadataIndexDB, sMetadataIndexDB_login, sMetadataIndexDB_password, sMetadataIndexDB_table);
      
      getIndexList().add(indexBean);
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
      for (IndexBean indexBean : getIndexList())
      {
        Element indexElement = newDoc.createElement("INDEX");
        
        Element field = newDoc.createElement("INDEX_DIRECTORY");
        field.appendChild(newDoc.createTextNode(indexBean.getIndex_dir()));
        indexElement.appendChild(field);
        
        field = newDoc.createElement("SPELL_INDEX_DIRECTORY");
        field.appendChild(newDoc.createTextNode(indexBean.getSpell_index_dir()));
        indexElement.appendChild(field);
        
        field = newDoc.createElement("METADATA_INDEX_DB");
        field.appendChild(newDoc.createTextNode(indexBean.getMetadata_index_db()));
        indexElement.appendChild(field);
        
        field = newDoc.createElement("METADATA_INDEX_DB_LOGIN");
        field.appendChild(newDoc.createTextNode(indexBean.getMetadata_index_db_login()));
        indexElement.appendChild(field);
        
        field = newDoc.createElement("METADATA_INDEX_DB_PASSWORD");
        field.appendChild(newDoc.createTextNode(indexBean.getMetadata_index_db_password() == null ? "" : indexBean.getMetadata_index_db_password()));
        indexElement.appendChild(field);
        
        field = newDoc.createElement("METADATA_INDEX_TABLE");
        field.appendChild(newDoc.createTextNode(indexBean.getMetadata_index_db_table()));
        indexElement.appendChild(field);
        
        rootElement.appendChild(indexElement);
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
  
  public void addIndexBean(IndexBean indexBean)
  {
    if (!this.indexList.contains(indexBean)) {
      this.indexList.add(indexBean);
    }
  }
  
  public ArrayList<IndexBean> getIndexList()
  {
    return this.indexList;
  }
  
  public static void main(String[] args)
  {
    IndexServiceConfiguration isc = new IndexServiceConfiguration();
    isc.readConfigurationFile();
    for (IndexBean ib : isc.getIndexList()) {
      System.out.println(ib.getIndex_dir() + " " + ib.getSpell_index_dir());
    }
    isc.createConfigurationFile("c:/file.xml");
  }
}

