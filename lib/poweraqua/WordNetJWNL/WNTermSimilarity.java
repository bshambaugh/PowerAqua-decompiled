package poweraqua.WordNetJWNL;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.Relationship;

public class WNTermSimilarity
{
  private ArrayList<String> source_lemmas;
  private ArrayList<String> target_lemmas;
  private boolean isWNSource = true;
  private boolean isWNTarget = true;
  private WordNetSemantics WNSemantics;
  private WNSynsetSetBean sourceSynsetBean;
  private WNSynsetSetBean targetSynsetBean;
  
  public WNTermSimilarity(String path)
    throws Exception
  {
    this.source_lemmas = new ArrayList();
    this.target_lemmas = new ArrayList();
    this.isWNSource = true;
    this.isWNTarget = true;
    this.WNSemantics = new WordNetSemantics(path);
  }
  
  public WNTermSimilarity()
    throws Exception
  {
    this.source_lemmas = new ArrayList();
    this.target_lemmas = new ArrayList();
    this.isWNSource = true;
    this.isWNTarget = true;
    this.WNSemantics = new WordNetSemantics();
  }
  
  public void CalculateTermSimilarity(ArrayList<String> source_names, ArrayList<String> target_names)
    throws Exception
  {
    this.source_lemmas = getWNLemmas(source_names);
    this.target_lemmas = getWNLemmas(target_names);
    if (this.source_lemmas.isEmpty()) {
      this.isWNSource = false;
    } else {
      this.isWNSource = true;
    }
    if (this.target_lemmas.isEmpty()) {
      this.isWNTarget = false;
    } else {
      this.isWNTarget = true;
    }
    ArrayList<WNSynsetSimilarity> synsetSimilarities = new ArrayList();
    if ((this.isWNSource) && (this.isWNTarget)) {
      synsetSimilarities = getSynsetSimilarityForLemmas(this.source_lemmas, this.target_lemmas);
    }
    this.sourceSynsetBean = calculateSourceSynsetBean(synsetSimilarities);
    this.targetSynsetBean = calculateTargetSynsetBean(synsetSimilarities);
  }
  
  public void CalculateTermSimilarity(String source, String target)
    throws Exception
  {
    this.source_lemmas = getWNLemma(source);
    this.target_lemmas = getWNLemma(target);
    if (this.source_lemmas.isEmpty()) {
      this.isWNSource = false;
    } else {
      this.isWNSource = true;
    }
    if (this.target_lemmas.isEmpty()) {
      this.isWNTarget = false;
    } else {
      this.isWNTarget = true;
    }
    ArrayList<WNSynsetSimilarity> synsetSimilarities = new ArrayList();
    if ((this.isWNSource) && (this.isWNTarget)) {
      synsetSimilarities = getSynsetSimilarityForLemmas(this.source_lemmas, this.target_lemmas);
    }
    this.sourceSynsetBean = calculateSourceSynsetBean(synsetSimilarities);
    this.targetSynsetBean = calculateTargetSynsetBean(synsetSimilarities);
  }
  
  public ArrayList<WNSynsetSimilarity> calculateSynsetSimilarity(ArrayList<String> source_names, ArrayList<String> target_names)
    throws Exception
  {
    this.source_lemmas = getWNLemmas(source_names);
    this.target_lemmas = getWNLemmas(target_names);
    if (this.source_lemmas.isEmpty()) {
      this.isWNSource = false;
    } else {
      this.isWNSource = true;
    }
    if (this.target_lemmas.isEmpty()) {
      this.isWNTarget = false;
    } else {
      this.isWNTarget = true;
    }
    ArrayList<WNSynsetSimilarity> synsetSimilarities = new ArrayList();
    if ((this.isWNSource) && (this.isWNTarget)) {
      return synsetSimilarities = getSynsetSimilarityForLemmas(this.source_lemmas, this.target_lemmas);
    }
    return synsetSimilarities;
  }
  
  public ArrayList<WNSynsetSimilarity> calculateSynsetSimilarity(String source, String target)
    throws Exception
  {
    this.source_lemmas = getWNSemantics().isWordNetInput(source.toLowerCase());
    if (this.source_lemmas.isEmpty()) {
      this.isWNSource = false;
    } else {
      this.isWNSource = true;
    }
    this.target_lemmas = getWNSemantics().isWordNetInput(source.toLowerCase());
    if (this.target_lemmas.isEmpty()) {
      this.isWNTarget = false;
    } else {
      this.isWNTarget = true;
    }
    if ((!this.isWNSource) || (!this.isWNTarget)) {
      return new ArrayList();
    }
    return getSynsetSimilarityForLemmas(this.source_lemmas, this.target_lemmas);
  }
  
  private WNSynsetSetBean calculateSourceSynsetBean(ArrayList<WNSynsetSimilarity> synsetSimilarities)
    throws Exception
  {
    if ((synsetSimilarities.isEmpty()) && (!this.isWNSource)) {
      return new WNSynsetSetBean();
    }
    if ((synsetSimilarities.isEmpty()) && (this.isWNSource) && (!this.isWNTarget))
    {
      getAllSynsetsForLemmas(this.source_lemmas);
    }
    else
    {
      if (synsetSimilarities.isEmpty()) {
        return new WNSynsetSetBean();
      }
      WNSynsetSetBean wnSynsetSetBean = new WNSynsetSetBean();
      for (Iterator i$ = synsetSimilarities.iterator(); i$.hasNext();)
      {
        wnsynsetSim = (WNSynsetSimilarity)i$.next();
        ArrayList<Relationship> bestRelationShips = wnsynsetSim.getBestRelationships();
        for (Relationship relationship : bestRelationShips)
        {
          WNSynsetSimilarity.SimilaritySem sim_aux = (WNSynsetSimilarity.SimilaritySem)wnsynsetSim.getRelationshipList().get(relationship);
          Synset source = wnsynsetSim.getSourceSynset(relationship);
          WNSynsetBean wnsynsetBean = new WNSynsetBean(sim_aux.getSimilarity(), sim_aux.isGloss_similarity(), source);
          wnSynsetSetBean.addSynsetBean(wnsynsetBean);
          wnSynsetSetBean.addSynsetSimilarity(wnsynsetSim);
        }
      }
      WNSynsetSimilarity wnsynsetSim;
      return wnSynsetSetBean;
    }
    return new WNSynsetSetBean();
  }
  
  private WNSynsetSetBean calculateTargetSynsetBean(ArrayList<WNSynsetSimilarity> synsetSimilarities)
    throws Exception
  {
    if ((synsetSimilarities.isEmpty()) && (!this.isWNTarget)) {
      return new WNSynsetSetBean();
    }
    if ((synsetSimilarities.isEmpty()) && (this.isWNTarget) && (!this.isWNSource))
    {
      getAllSynsetsForLemmas(this.target_lemmas);
    }
    else
    {
      if (synsetSimilarities.isEmpty()) {
        return new WNSynsetSetBean();
      }
      WNSynsetSetBean wnSynsetSetBean = new WNSynsetSetBean();
      for (Iterator i$ = synsetSimilarities.iterator(); i$.hasNext();)
      {
        wnsynsetSim = (WNSynsetSimilarity)i$.next();
        ArrayList<Relationship> bestRelationShips = wnsynsetSim.getBestRelationships();
        for (Relationship relationship : bestRelationShips)
        {
          WNSynsetSimilarity.SimilaritySem sim_aux = (WNSynsetSimilarity.SimilaritySem)wnsynsetSim.getRelationshipList().get(relationship);
          Synset target = wnsynsetSim.getTargetSynset(relationship);
          WNSynsetBean wnsynsetBean = new WNSynsetBean(sim_aux.getSimilarity(), sim_aux.isGloss_similarity(), target);
          wnSynsetSetBean.addSynsetBean(wnsynsetBean);
          wnSynsetSetBean.addSynsetSimilarity(wnsynsetSim);
        }
      }
      WNSynsetSimilarity wnsynsetSim;
      return wnSynsetSetBean;
    }
    return new WNSynsetSetBean();
  }
  
  private WNSynsetSetBean getAllSynsetsForLemmas(ArrayList<String> lemmas)
    throws Exception
  {
    WNSynsetSetBean wnSynsetSetBean = new WNSynsetSetBean();
    for (String lemma : lemmas)
    {
      getWNSemantics().getWN().Initialize(lemma);
      List<Synset> synsets = this.WNSemantics.getWN().getSynset();
      for (Synset source : synsets)
      {
        WNSynsetBean wnsynsetBean = new WNSynsetBean(source);
        wnSynsetSetBean.addSynsetBean(wnsynsetBean);
      }
    }
    return wnSynsetSetBean;
  }
  
  public WNSynsetSetBean getAllSynsets(ArrayList<String> source)
    throws Exception
  {
    ArrayList<String> source_lemmas = getWNLemmas(source);
    if (source_lemmas.isEmpty()) {
      this.isWNSource = false;
    } else {
      this.isWNSource = true;
    }
    return getAllSynsetsForLemmas(source_lemmas);
  }
  
  private ArrayList<String> getWNLemmas(ArrayList<String> source_names)
    throws Exception
  {
    ArrayList<String> source_lemmas = new ArrayList();
    for (String source_name : source_names)
    {
      ArrayList<String> source_lemmas_aux = getWNSemantics().isWordNetInput(source_name.toLowerCase());
      for (String aux : source_lemmas_aux) {
        if (!source_lemmas.contains(aux)) {
          if (aux.length() > 2) {
            source_lemmas.add(aux);
          }
        }
      }
    }
    return source_lemmas;
  }
  
  private ArrayList<String> getWNLemma(String source_name)
    throws Exception
  {
    ArrayList<String> source_lemmas = new ArrayList();
    ArrayList<String> source_lemmas_aux = getWNSemantics().isWordNetInput(source_name.toLowerCase());
    for (String aux : source_lemmas_aux) {
      if (!source_lemmas.contains(aux)) {
        source_lemmas.add(aux);
      }
    }
    return source_lemmas;
  }
  
  private ArrayList<WNSynsetSimilarity> getSynsetSimilarityForLemmas(ArrayList<String> source_lemmas, ArrayList<String> target_lemmas)
    throws Exception
  {
    ArrayList<WNSynsetSimilarity> matchSimilaritySynsets = new ArrayList();
    for (Iterator i$ = source_lemmas.iterator(); i$.hasNext();)
    {
      source_lemma = (String)i$.next();
      for (String target_lemma : target_lemmas)
      {
        getWNSemantics().getWN().Initialize(source_lemma);
        IndexWord indexSource = getWNSemantics().getWN().termIndex;
        getWNSemantics().getWN().Initialize(target_lemma);
        IndexWord indexTarget = getWNSemantics().getWN().termIndex;
        WNSynsetSimilarity aux = getWNSemantics().getWNTaxonomySynsets(indexSource, indexTarget);
        if (!matchSimilaritySynsets.contains(aux)) {
          matchSimilaritySynsets.add(aux);
        }
      }
    }
    String source_lemma;
    return matchSimilaritySynsets;
  }
  
  public boolean isIsWNSource()
  {
    return this.isWNSource;
  }
  
  public boolean isIsWNTarget()
  {
    return this.isWNTarget;
  }
  
  public WordNetSemantics getWNSemantics()
  {
    return this.WNSemantics;
  }
  
  public WNSynsetSetBean getSourceSynsetBean()
  {
    return this.sourceSynsetBean;
  }
  
  public WNSynsetSetBean getTargetSynsetBean()
  {
    return this.targetSynsetBean;
  }
  
  public static void main(String[] args)
  {
    try
    {
      String term1 = "cities";
      String term2 = "city";
      WNTermSimilarity wnTermSim = new WNTermSimilarity();
      wnTermSim.CalculateTermSimilarity(term1, term2);
      for (Synset syn : wnTermSim.sourceSynsetBean.getSynsetList()) {
        System.out.println("Syn " + syn);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}

