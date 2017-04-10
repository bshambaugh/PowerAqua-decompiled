package poweraqua.query;

import java.io.PrintStream;

public class QueryAnalyzer
{
  String separator;
  
  public QueryAnalyzer()
  {
    this.separator = new String();
  }
  
  public QueryElementsBean analyzeQuery(String query)
  {
    QueryElementsBean queryElements = new QueryElementsBean();
    
    String[] elements = query.split(" AND ");
    if (elements.length <= 1)
    {
      elements = query.split(" OR ");
      if (elements.length <= 1)
      {
        queryElements.add(query);
        return queryElements;
      }
      this.separator = " OR ";
    }
    else
    {
      this.separator = " AND ";
    }
    for (int i = 0; i < elements.length; i++) {
      if ((!elements[i].equals(" AND ")) && (!elements[i].equals(" OR "))) {
        queryElements.add(elements[i].trim());
      }
    }
    return queryElements;
  }
  
  public boolean isAndQuery()
  {
    if (this.separator.equals("AND")) {
      return true;
    }
    return false;
  }
  
  public boolean isOrQuery()
  {
    if (this.separator.equals("OR")) {
      return true;
    }
    return false;
  }
  
  public static void main(String[] args)
  {
    QueryAnalyzer qa = new QueryAnalyzer();
    System.out.println(qa.analyzeQuery("vanesa OR miriam"));
  }
}

