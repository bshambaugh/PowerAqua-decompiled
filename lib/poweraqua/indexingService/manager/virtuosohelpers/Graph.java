package poweraqua.indexingService.manager.virtuoso.virtuosohelpers;

public class Graph
{
  private String IRI;
  private String type;
  
  public Graph(String iRI, String type)
  {
    this.IRI = iRI;
    this.type = type;
  }
  
  public String getIRI()
  {
    return this.IRI;
  }
  
  public void setIRI(String IRI)
  {
    this.IRI = IRI;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  public String toString()
  {
    String string = "Graph:\n";
    string = string + "\t\t\tIRI " + this.IRI + "\n";
    string = string + "\t\t\tType " + this.type + "\n";
    return string;
  }
}

