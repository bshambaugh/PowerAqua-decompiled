package poweraqua.powermap.mappingModel;

import java.io.PrintStream;
import java.util.ArrayList;
import poweraqua.WordNetJWNL.WordNet;

public class WordNetBean
{
  private String keyword;
  private String realpath;
  private boolean is_wordnet;
  private ArrayList<String> synonyms;
  private ArrayList<String> derived;
  private ArrayList<String> hypernyms;
  private ArrayList<String> hypernymsLemma;
  private ArrayList<String> meronyms;
  private String WN_lemma;
  private int POS;
  private String singular;
  private String plural;
  
  public WordNetBean(String keyword, String realpath)
  {
    try
    {
      this.keyword = keyword;
      this.realpath = realpath;
      WordNet WN = new WordNet(realpath);
      this.is_wordnet = WN.Initialize(keyword);
      System.out.println("WordNet initialized");
      this.singular = WN.getSingularNoun(keyword, this.is_wordnet);
      this.plural = WN.getPluralNoun(keyword, this.is_wordnet);
      if (this.is_wordnet)
      {
        this.POS = WN.pos;
        this.synonyms = WN.getSynonyms();
        this.hypernyms = WN.getHypernyms();
        this.hypernymsLemma = WN.getHypernymsLemma();
        this.derived = WN.getDerived();
        this.meronyms = WN.getFirstMeronyms();
        this.WN_lemma = WN.getUniqueLemma();
      }
      else
      {
        this.synonyms = new ArrayList();
        this.hypernyms = new ArrayList();
        this.hypernymsLemma = new ArrayList();
        this.derived = new ArrayList();
        this.meronyms = new ArrayList();
      }
      WN.closeDictionary();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public WordNetBean(String keyword)
  {
    try
    {
      this.keyword = keyword;
      WordNet WN = new WordNet();
      this.is_wordnet = WN.Initialize(keyword);
      this.singular = WN.getSingularNoun(keyword);
      this.plural = WN.getPluralNoun(keyword);
      if (this.is_wordnet)
      {
        this.POS = WN.pos;
        this.synonyms = WN.getSynonyms();
        this.hypernyms = WN.getHypernyms();
        this.hypernymsLemma = WN.getHypernymsLemma();
        this.derived = WN.getDerived();
        this.meronyms = WN.getFirstMeronyms();
        this.WN_lemma = WN.getUniqueLemma();
      }
      else
      {
        this.synonyms = new ArrayList();
        this.hypernyms = new ArrayList();
        this.hypernymsLemma = new ArrayList();
        this.derived = new ArrayList();
        this.meronyms = new ArrayList();
      }
      WN.closeDictionary();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public String getKeyword()
  {
    return this.keyword;
  }
  
  public void setKeyword(String keyword)
  {
    this.keyword = keyword;
  }
  
  public ArrayList<String> getSynonyms()
  {
    return this.synonyms;
  }
  
  public void setSynonyms(ArrayList<String> synonyms)
  {
    this.synonyms = synonyms;
  }
  
  public ArrayList<String> getHypernyms()
  {
    return this.hypernyms;
  }
  
  public ArrayList<String> getHypernyms(int max_number)
  {
    ArrayList<String> hypernyms_max = new ArrayList();
    int i = 0;
    while ((i < max_number) && (i < this.hypernyms.size()))
    {
      hypernyms_max.add(this.hypernyms.get(i));
      i++;
    }
    return hypernyms_max;
  }
  
  public ArrayList<String> getSynonyms(int max_number)
  {
    ArrayList<String> synonyms_max = new ArrayList();
    int i = 0;
    while ((i < max_number) && (i < this.synonyms.size()))
    {
      if (!synonyms_max.contains(this.synonyms.get(i))) {
        synonyms_max.add(this.synonyms.get(i));
      }
      i++;
    }
    return synonyms_max;
  }
  
  public ArrayList<String> getDerived(int max_number)
  {
    ArrayList<String> synonyms_max = new ArrayList();
    int i = 0;
    while ((i < max_number) && (i < this.derived.size()))
    {
      if (!synonyms_max.contains(this.derived.get(i))) {
        synonyms_max.add(this.derived.get(i));
      }
      i++;
    }
    return synonyms_max;
  }
  
  public ArrayList<String> getSynonymsAndDerived(int max_number_syn, int max_number_der)
  {
    ArrayList<String> syns = getSynonyms(max_number_syn);
    ArrayList<String> der = getDerived(max_number_der);
    for (String d : der) {
      if (!syns.contains(d)) {
        syns.add(d);
      }
    }
    return syns;
  }
  
  public void setHypernyms(ArrayList<String> hypernyms)
  {
    this.hypernyms = hypernyms;
  }
  
  public String getWN_lemma()
  {
    return this.WN_lemma;
  }
  
  public void setWN_lemma(String WN_lemma)
  {
    this.WN_lemma = WN_lemma;
  }
  
  public String getSingular()
  {
    return this.singular;
  }
  
  public void setSingular(String singular)
  {
    this.singular = singular;
  }
  
  public String getPlural()
  {
    return this.plural;
  }
  
  public void setPlural(String plural)
  {
    this.plural = plural;
  }
  
  public boolean isIs_wordnet()
  {
    return this.is_wordnet;
  }
  
  public void setIs_wordnet(boolean is_wordnet)
  {
    this.is_wordnet = is_wordnet;
  }
  
  public String getRealpath()
  {
    return this.realpath;
  }
  
  public void setRealpath(String realpath)
  {
    this.realpath = realpath;
  }
  
  public int getPOS()
  {
    return this.POS;
  }
  
  public ArrayList<String> getDerived()
  {
    return this.derived;
  }
  
  public ArrayList<String> getMeronyms()
  {
    return this.meronyms;
  }
  
  public ArrayList<String> getHypernymsLemma()
  {
    return this.hypernymsLemma;
  }
}

