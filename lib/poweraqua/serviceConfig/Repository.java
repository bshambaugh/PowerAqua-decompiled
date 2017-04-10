package poweraqua.serviceConfig;

public class Repository
{
  protected String serverURL;
  protected String proxy;
  protected String port;
  protected String login;
  protected String password;
  protected String pluginType;
  protected String repositoryName;
  protected String repositoryType;
  
  public Repository() {}
  
  public Repository(String serverURL, String proxy, String port, String login, String password, String pluginType, String repositoryName, String repositoryType)
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
  
  public String getServerURL()
  {
    return this.serverURL;
  }
  
  public void setServerURL(String serverURL)
  {
    this.serverURL = serverURL;
  }
  
  public String getProxy()
  {
    return this.proxy;
  }
  
  public void setProxy(String proxy)
  {
    this.proxy = proxy;
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
  
  public String getRepositoryName()
  {
    return this.repositoryName;
  }
  
  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }
  
  public String getRepositoryType()
  {
    return this.repositoryType;
  }
  
  public void setRepositoryType(String repositoryType)
  {
    this.repositoryType = repositoryType;
  }
  
  public String getPluginType()
  {
    return this.pluginType;
  }
  
  public void setPluginType(String pluginType)
  {
    this.pluginType = pluginType;
  }
  
  public String toString()
  {
    return new String("Repository:\nServerURI " + getServerURL() + "\n" + "ServerProxy " + getProxy() + "\n" + "ServerPort " + getPort() + "\n" + "ServerLogin " + getLogin() + "\n" + "ServerPasword " + getPassword() + "\n" + "PluginType " + getPluginType() + "\n" + "RepositoryName " + getRepositoryName() + "\n" + "RepositoryType " + getRepositoryType() + "\n");
  }
  
  public boolean equals(Object obj)
  {
    if (obj.getClass() != getClass()) {
      return false;
    }
    Repository repository = (Repository)obj;
    if ((repository.getServerURL().equals(getServerURL())) && (repository.getRepositoryName().equals(getRepositoryName()))) {
      return true;
    }
    return false;
  }
}

