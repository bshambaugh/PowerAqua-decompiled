package poweraqua.indexingService.manager.virtuoso.virtuosohelpers;

public class GraphVirtuoso
{
  private String URL;
  private String port;
  private String login;
  private String password;
  private String graphIRI;
  private String OntologyType;
  
  public GraphVirtuoso(String uRL, String port, String login, String password, String graphIRI, String OntologyType)
  {
    this.URL = uRL;
    this.port = port;
    this.login = login;
    this.password = password;
    this.graphIRI = graphIRI;
    this.OntologyType = OntologyType;
  }
  
  public String getOntologyType()
  {
    return this.OntologyType;
  }
  
  public void setOntologyType(String ontologyType)
  {
    this.OntologyType = ontologyType;
  }
  
  public void setURL(String uRL)
  {
    this.URL = uRL;
  }
  
  public void setPort(String port)
  {
    this.port = port;
  }
  
  public void setLogin(String login)
  {
    this.login = login;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public void setGraphIRI(String graphIRI)
  {
    this.graphIRI = graphIRI;
  }
  
  public String getURL()
  {
    return this.URL;
  }
  
  public String getPort()
  {
    return this.port;
  }
  
  public String getLogin()
  {
    return this.login;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public String getGraphIRI()
  {
    return this.graphIRI;
  }
}

