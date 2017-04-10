package poweraqua.yahoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.tripleModel.linguisticTriple.QueryTriple;
import poweraqua.core.tripleModel.ontologyTriple.OntoTriple;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.triplePhase.OntoTripleBean;

public class WebSearch
{
  private ArrayList<String> splitQuery(String query)
  {
    ArrayList<String> queryWords = new ArrayList();
    String word = "";
    int charIndex = 0;
    while (charIndex < query.length())
    {
      Character character = Character.valueOf(query.charAt(charIndex));
      if (Character.isLetterOrDigit(character.charValue()))
      {
        word = word + character;
      }
      else if (!word.equals(""))
      {
        queryWords.add(word);
        word = "";
      }
      charIndex++;
    }
    return queryWords;
  }
  
  private String createURL(ArrayList<String> triple, ArrayList<String> semanticCleanData)
  {
    ArrayList<String> query = (ArrayList)triple.clone();
    
    String url = "http://search.yahooapis.com/WebSearchService/V1/webSearch?appid=PowerAquaSearch&query=";
    
    url = url.concat((String)query.remove(0));
    while (!query.isEmpty())
    {
      url = url.concat("+");
      url = url.concat((String)query.remove(0));
    }
    if (!semanticCleanData.isEmpty())
    {
      url = url.concat("+");
      url = url.concat((String)semanticCleanData.remove(0));
      while (!semanticCleanData.isEmpty())
      {
        url = url.concat("+");
        url = url.concat((String)semanticCleanData.remove(0));
      }
    }
    return url;
  }
  
  private InputStream webSearch(ArrayList<String> query, ArrayList<String> semanticData)
  {
    String proxyHost = "wwwcache.open.ac.uk";
    String proxyPort = "80";
    System.getProperties().setProperty("http.proxyHost", proxyHost);
    System.getProperties().setProperty("http.proxyPort", proxyPort);
    ArrayList<String> semanticCleanData = SplitLabels.splitLabels(semanticData);
    System.out.println("Searching in yahoo for the query: " + query.toString() + " and the (clean) semantic data " + semanticCleanData.toString());
    
    String urlString = createURL(query, semanticCleanData);
    System.out.println("Yahoo URL: " + urlString);
    try
    {
      URL url = new URL(urlString);
      return url.openStream();
    }
    catch (MalformedURLException malURL)
    {
      System.err.println("MalformedURLException: " + malURL.getMessage());
    }
    catch (IOException ioe)
    {
      System.err.println("IOException: " + ioe.getMessage());
    }
    return null;
  }
  
  private Hashtable<Integer, ArrayList<String>> yahooResults(ArrayList<String> query, ArrayList<String> semanticData)
  {
    Hashtable<Integer, ArrayList<String>> output = new Hashtable();
    
    InputStream inStream = webSearch(query, semanticData);
    if (inStream != null)
    {
      BufferedReader input = new BufferedReader(new InputStreamReader(inStream));
      
      int key = 1;
      try
      {
        String inputLine;
        while ((inputLine = input.readLine()) != null) {
          if (inputLine.startsWith("<Result>"))
          {
            ArrayList<String> result = new ArrayList();
            
            int tStart = inputLine.indexOf("<Title>");
            int tEnd = inputLine.indexOf("</Title>");
            String title = (String)inputLine.subSequence(tStart + 7, tEnd);
            result.add(title);
            if (inputLine.contains("<Summary>"))
            {
              int sStart = inputLine.indexOf("<Summary>");
              int sEnd = inputLine.indexOf("</Summary>");
              String summary = (String)inputLine.subSequence(sStart + 9, sEnd);
              result.add(summary);
            }
            else
            {
              result.add("No summary available.");
            }
            int uStart = inputLine.indexOf("<ClickUrl>");
            int uEnd = inputLine.indexOf("</ClickUrl>");
            String webAdd = (String)inputLine.subSequence(uStart + 10, uEnd);
            result.add(webAdd);
            
            output.put(Integer.valueOf(key), result);
            key++;
          }
        }
        input.close();
      }
      catch (IOException ioe)
      {
        System.err.println("IOException: " + ioe.getMessage());
      }
    }
    return output;
  }
  
  private Hashtable<Integer, ArrayList<String>> noResults()
  {
    Hashtable<Integer, ArrayList<String>> noResults = new Hashtable();
    ArrayList<String> string = new ArrayList();
    string.add("No pages could be found for this query.");
    noResults.put(Integer.valueOf(1), string);
    return noResults;
  }
  
  public Hashtable<Integer, ArrayList<String>> webPages(String queryString, ArrayList<String> semanticData)
  {
    ArrayList<String> query = splitQuery(queryString);
    Hashtable<Integer, ArrayList<String>> output = yahooResults(query, semanticData);
    if ((output.isEmpty()) && (!semanticData.isEmpty()))
    {
      semanticData.clear();
      output = yahooResults(query, semanticData);
    }
    else if (output.isEmpty())
    {
      output = noResults();
    }
    return output;
  }
  
  public Hashtable<Integer, ArrayList<String>> webPages(QueryTriple queryTriple, ArrayList<String> semanticData)
  {
    ArrayList<String> query = queryTriple.getQueryTerm();
    if (query.contains("what_is")) {
      query.remove("what_is");
    }
    query.add(queryTriple.getRelation());
    query.add(queryTriple.getSecondTerm());
    query.add(queryTriple.getThirdTerm());
    while (query.contains(null)) {
      query.remove(null);
    }
    query = SplitLabels.splitLabels(query);
    
    Hashtable<Integer, ArrayList<String>> output = yahooResults(query, semanticData);
    if ((output.isEmpty()) && (!semanticData.isEmpty()))
    {
      semanticData.clear();
      
      output = yahooResults(query, semanticData);
    }
    if (output.isEmpty()) {
      output = noResults();
    }
    return output;
  }
  
  public Hashtable<Integer, ArrayList<String>> webPages(ArrayList<QueryTriple> queryTriples, ArrayList<String> semanticData)
  {
    ArrayList<String> query = new ArrayList();
    for (QueryTriple queryTriple : queryTriples)
    {
      query = queryTriple.getQueryTerm();
      if (query.contains("what_is")) {
        query.remove("what_is");
      }
      if (!query.contains(queryTriple.getRelation())) {
        query.add(queryTriple.getRelation());
      }
      if (!query.contains(queryTriple.getSecondTerm())) {
        query.add(queryTriple.getSecondTerm());
      }
      if (!query.contains(queryTriple.getThirdTerm())) {
        query.add(queryTriple.getThirdTerm());
      }
    }
    while (query.contains(null)) {
      query.remove(null);
    }
    query = SplitLabels.splitLabels(query);
    System.out.println("query: " + query.toString());
    Hashtable<Integer, ArrayList<String>> output = yahooResults(query, semanticData);
    if ((output.isEmpty()) && (!semanticData.isEmpty()))
    {
      System.out.println("Graceful degradation (re-sending the query withouth the semantic answers)..");
      semanticData.clear();
      output = yahooResults(query, semanticData);
    }
    if (output.isEmpty()) {
      output = noResults();
    }
    return output;
  }
  
  public Hashtable<Integer, ArrayList<String>> webPages(OntoTripleBean ontoTripleBean, ArrayList<String> semanticData)
  {
    ArrayList<OntoTriple> tripleBean = ontoTripleBean.getOntoTripleBean();
    ArrayList<String> query = new ArrayList();
    for (int beanIndex = 0; beanIndex < tripleBean.size(); beanIndex++)
    {
      OntoTriple ontoTriple = (OntoTriple)tripleBean.get(beanIndex);
      query.add(ontoTriple.getFirstTerm().getEntity().getLabel());
      query.add(ontoTriple.getRelation().getEntity().getLabel());
      query.add(ontoTriple.getSecondTerm().getEntity().getLabel());
    }
    query = SplitLabels.splitLabels(query);
    
    Hashtable<Integer, ArrayList<String>> output = yahooResults(query, semanticData);
    if ((output.isEmpty()) && (!semanticData.isEmpty()))
    {
      semanticData.clear();
      output = yahooResults(query, semanticData);
    }
    if (output.isEmpty()) {
      output = noResults();
    }
    return output;
  }
}

