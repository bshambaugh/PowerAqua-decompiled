package virtuosoPlugin;

import java.io.PrintStream;

public class Prefixes
{
  public static final String RDF = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
  public static final String RDFS = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
  public static final String XSD = "prefix xsd: <http://www.w3.org/2001/XMLSchema#> ";
  public static final String FN = "prefix fn: <http://www.w3.org/2005/xpath-functions#> ";
  public static final String OWL = "prefix owl: <http://www.w3.org/2002/07/owl#> ";
  
  public static String getPrefix(String prefix, String url)
  {
    while ((url.endsWith("/")) || (url.endsWith("#")))
    {
      System.out.println(url);
      url = url.substring(0, url.length() - 1);
      System.out.println(url);
    }
    return "PREFIX " + prefix + ": <" + url + "#> ";
  }
  
  public static void main(String[] args)
  {
    System.out.println(getPrefix("xxx", "http://www.w3.org/2005/xpath-functions#///"));
  }
}

