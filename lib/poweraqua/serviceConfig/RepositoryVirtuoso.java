package poweraqua.serviceConfig;

import java.io.PrintStream;

public class RepositoryVirtuoso
  extends Repository
{
  private String graphIRI;
  
  public RepositoryVirtuoso() {}
  
  public RepositoryVirtuoso(String serverURL, String proxy, String port, String login, String password, String pluginType, String repositoryName, String repositoryType)
  {
    setLogin(login);
    setPassword(password);
    setPluginType(pluginType);
    setPort(port);
    setProxy(proxy);
    setRepositoryName(repositoryName);
    setRepositoryType(repositoryType);
    setServerURL(serverURL);
  }
  
  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
    
    setGraphIRI(repositoryName.split("#")[1]);
  }
  
  public void setGraphIRI(String graphIRI)
  {
    this.graphIRI = graphIRI;
  }
  
  public String getGraphIRI()
  {
    return this.graphIRI;
  }
  
  public static void main(String[] args)
  {
    RepositoryVirtuoso repository = new RepositoryVirtuoso();
    repository.setRepositoryName("jdbc:virtuoso://kmi-dev02.open.ac.uk:8890#http://geography.org");
    System.out.print(repository.getGraphIRI());
  }
}

