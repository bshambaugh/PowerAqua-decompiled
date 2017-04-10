package poweraqua.lexicon;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Lexicon
{
  private Hashtable<String, ArrayList<String>> lexicons = new Hashtable();
  private String inputfile = "lexicon.xml";
  
  public Lexicon()
  {
    System.out.println("Reading the lexicon file ");
    readInputFile(this.inputfile);
  }
  
  public Lexicon(String realpath)
  {
    System.out.println("Reading the lexicon file ");
    readInputFile(realpath + this.inputfile);
  }
  
  public void readInputFile(String inputfile)
  {
    DOMParser parser = new DOMParser();
    try
    {
      parser.parse(inputfile);
    }
    catch (SAXException ex)
    {
      ex.printStackTrace();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    Document doc = parser.getDocument();
    Element configuration = doc.getDocumentElement();
    NodeList indexes = configuration.getElementsByTagName("lexicon");
    System.out.println("Number of lexicons is " + indexes.getLength());
    for (int i = 0; i < indexes.getLength(); i++)
    {
      Element questionInfo = (Element)indexes.item(i);
      
      NodeList question = questionInfo.getElementsByTagName("word");
      String id = question.item(0).getFirstChild().getNodeValue();
      
      ArrayList<String> synonymList = new ArrayList();
      NodeList synonyms = questionInfo.getElementsByTagName("synonym");
      for (int j = 0; j < synonyms.getLength(); j++)
      {
        String synonym = synonyms.item(j).getFirstChild().getNodeValue();
        synonymList.add(synonym);
      }
      getLexicons().put(id, synonymList);
    }
  }
  
  public void print()
  {
    for (String id : this.lexicons.keySet())
    {
      ArrayList<String> syns = (ArrayList)this.lexicons.get(id);
      System.out.println("Word: " + id);
      System.out.println("Synonyms: " + syns.toString());
    }
  }
  
  public ArrayList<String> getSynonyms(String word)
  {
    if (this.lexicons.containsKey(word)) {
      return (ArrayList)this.lexicons.get(word);
    }
    return new ArrayList();
  }
  
  public Hashtable<String, ArrayList<String>> getLexicons()
  {
    return this.lexicons;
  }
  
  public void setLexicons(Hashtable<String, ArrayList<String>> lexicons)
  {
    this.lexicons = lexicons;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    String fileInput = "./tests/dbpedia-train.xml";
    String fileOutput = "./tests/dbpedia-poweraqua-output.xml";
    Lexicon lex = new Lexicon();
    lex.print();
  }
}

