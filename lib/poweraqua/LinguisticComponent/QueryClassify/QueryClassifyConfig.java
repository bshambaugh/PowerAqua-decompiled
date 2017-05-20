package poweraqua.LinguisticComponent.QueryClassify;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QueryClassifyConfig
{
  public String who;
  public String when;
  public String where;
  public String howlong;
  
  public QueryClassifyConfig(String path)
    throws Exception
  {
    DOMParser parser = new DOMParser();
    if (path != null) {
      parser.parse(path + "/query_properties.xml");
    } else {
      parser.parse("query_properties.xml");
    }
    Document doc = parser.getDocument();
    Element configuration = doc.getDocumentElement();
    
    NodeList whoquery = configuration.getElementsByTagName("WHO");
    this.who = whoquery.item(0).getFirstChild().getNodeValue();
    
    NodeList whenquery = configuration.getElementsByTagName("WHEN");
    this.when = whenquery.item(0).getFirstChild().getNodeValue();
    
    NodeList wherequery = configuration.getElementsByTagName("WHERE");
    this.where = wherequery.item(0).getFirstChild().getNodeValue();
    
    NodeList howlongquery = configuration.getElementsByTagName("HOWLONG");
    this.howlong = howlongquery.item(0).getFirstChild().getNodeValue();
  }
}

