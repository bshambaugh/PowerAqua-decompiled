package poweraqua.indexingService.manager.virtuoso.virtuosohelpers;

import java.util.ArrayList;
import java.util.LinkedList;

public class ServerConfigVirtuoso
{
  private String URL;
  private String port;
  private String login;
  private String password;
  private LinkedList<Graph> graphs;
  
  public ServerConfigVirtuoso(String uRL, String port, String login, String password, LinkedList<Graph> graphs)
  {
    this.URL = uRL;
    this.port = port;
    this.login = login;
    this.password = password;
    this.graphs = graphs;
  }
  
  public String getURL()
  {
    return this.URL;
  }
  
  public void setURL(String uRL)
  {
    this.URL = uRL;
  }
  
  public String getPort()
  {
    return this.port;
  }
  
  public void setPort(String port)
  {
    this.port = port;
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
  
  public LinkedList<Graph> getGraphs()
  {
    return this.graphs;
  }
  
  public void setGraphs(LinkedList<Graph> graphs)
  {
    this.graphs = graphs;
  }
  
  public String toString()
  {
    String string = "ServerConfigVirtuoso:\n";
    string = string + "\tURI " + getURL() + "\n";
    string = string + "\tPort " + getPort() + "\n";
    string = string + "\tLogin " + getLogin() + "\n";
    string = string + "\tPasword " + getPassword() + "\n";
    string = string + "\tGraphs:\n";
    for (Graph graph : this.graphs) {
      string = string + "\t\t" + graph;
    }
    return string;
  }
  
  public ArrayList<GraphVirtuoso> getGraphVirtuosoList()
  {
    ArrayList<GraphVirtuoso> list = new ArrayList();
    for (Graph graph : this.graphs)
    {
      GraphVirtuoso graphVirtuoso = new GraphVirtuoso(this.URL, this.port, this.login, this.password, graph.getIRI(), graph.getType());
      list.add(graphVirtuoso);
    }
    return list;
  }
}

