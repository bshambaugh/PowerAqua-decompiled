package poweraqua.indexingService.creator;

public class MultiOntologyIndexBean
{
  private String indexFolder;
  
  public MultiOntologyIndexBean(String indexFolder)
  {
    this.indexFolder = indexFolder;
  }
  
  public String getIndexFolder()
  {
    return this.indexFolder;
  }
  
  public String toString()
  {
    return new String("IndexInformationFolder " + this.indexFolder);
  }
}
