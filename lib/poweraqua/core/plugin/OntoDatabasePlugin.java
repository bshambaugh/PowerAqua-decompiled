package poweraqua.core.plugin;

public abstract class OntoDatabasePlugin
  implements OntologyPlugin
{
  private String name;
  private String subType;
  private String serverURL;
  private String repositoryName;
  
  public void setUrlID(String url)
  {
    this.serverURL = url;
  }
  
  public String getUrlID()
  {
    return this.serverURL;
  }
  
  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }
  
  public String getRepositoryName()
  {
    return this.repositoryName;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setSubType(String subType)
  {
    this.subType = this.name;
  }
  
  public String getSubType()
  {
    return this.subType;
  }
}

