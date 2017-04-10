package poweraqua.query;

import java.util.ArrayList;

public class QueryElementsBean
{
  private ArrayList<String> queryElements;
  
  public QueryElementsBean()
  {
    this.queryElements = new ArrayList();
  }
  
  public void add(String element)
  {
    getQueryElements().add(element);
  }
  
  public int getNumElements()
  {
    return getQueryElements().size();
  }
  
  public String toString()
  {
    StringBuffer buf = new StringBuffer();
    for (String element : this.queryElements) {
      buf.append(element + "-");
    }
    return buf.toString();
  }
  
  public ArrayList<String> getQueryElements()
  {
    return this.queryElements;
  }
}

