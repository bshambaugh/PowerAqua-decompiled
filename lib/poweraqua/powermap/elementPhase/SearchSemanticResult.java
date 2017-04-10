package poweraqua.powermap.elementPhase;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import net.sf.extjwnl.data.Synset;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import poweraqua.WordNetJWNL.WNSynsetBean;
import poweraqua.WordNetJWNL.WNSynsetSetBean;
import poweraqua.WordNetJWNL.WNSynsetSimilarity;
import poweraqua.WordNetJWNL.WNSynsetSimilarity.SimilaritySem;
import poweraqua.WordNetJWNL.WordNetSemantics;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.model.myrdfmodel.RDFProperty;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.indexingService.manager.IndexManager;
import poweraqua.powermap.mappingModel.MappingSession;

public class SearchSemanticResult
  implements Serializable, Comparator
{
  private RDFEntity entity;
  private OntologyPlugin osPlugin;
  private String emt_keyword;
  private float score;
  private boolean exact;
  private boolean exactLexicalWord;
  private String semanticRelation;
  private String lexicalRelatedWord = "";
  private RDFEntityList background_history;
  private WNSynsetSetBean taxonomySimilaritySynsets;
  private WNSynsetSetBean matchSimilaritySynsets;
  private ArrayList<Synset> validSynset;
  private boolean watson = false;
  private boolean virtuoso = false;
  private boolean synsetIndexed = false;
  private MetadataBean metadataBean;
  
  public SearchSemanticResult(RDFEntity entity, float score, String semanticRelation)
    throws Exception
  {
    setEntity(entity);
    this.score = score;
    this.semanticRelation = semanticRelation;
    this.metadataBean = new MetadataBean();
    setTaxonomySimilaritySynsets(new WNSynsetSetBean());
    setMatchSimilaritySynsets(new WNSynsetSetBean());
    setValidSynset(new ArrayList());
    this.exact = false;
    this.exactLexicalWord = false;
    setBackground_history(new RDFEntityList());
  }
  
  public SearchSemanticResult(RDFEntity entity, float score, String semanticRelation, String lexicalRelatedWord)
    throws Exception
  {
    setEntity(entity);
    this.score = score;
    this.semanticRelation = semanticRelation;
    this.lexicalRelatedWord = lexicalRelatedWord;
    this.metadataBean = new MetadataBean();
    this.exactLexicalWord = false;
    setTaxonomySimilaritySynsets(new WNSynsetSetBean());
    setMatchSimilaritySynsets(new WNSynsetSetBean());
    setValidSynset(new ArrayList());
    this.exact = false;
    setBackground_history(new RDFEntityList());
  }
  
  public SearchSemanticResult(RDFEntity entity, String semanticRelation, String lexicalRelatedWord)
    throws Exception
  {
    setEntity(entity);
    this.score = 0.0F;
    this.semanticRelation = semanticRelation;
    this.lexicalRelatedWord = lexicalRelatedWord;
    this.metadataBean = new MetadataBean();
    this.exactLexicalWord = false;
    setTaxonomySimilaritySynsets(new WNSynsetSetBean());
    setMatchSimilaritySynsets(new WNSynsetSetBean());
    setValidSynset(new ArrayList());
    this.exact = false;
    setBackground_history(new RDFEntityList());
  }
  
  public SearchSemanticResult(RDFEntity entity, OntologyPlugin osPlugin)
    throws Exception
  {
    this.semanticRelation = "ontology_ad_hoc";
    setEntity(entity);
    this.osPlugin = osPlugin;
    this.metadataBean = new MetadataBean();
    setTaxonomySimilaritySynsets(new WNSynsetSetBean());
    setMatchSimilaritySynsets(new WNSynsetSetBean());
    setValidSynset(new ArrayList());
    setBackground_history(new RDFEntityList());
    this.exact = false;
    this.exactLexicalWord = false;
  }
  
  public ArrayList<String> getClassOfEntity()
  {
    ArrayList<String> classesOfEntity = new ArrayList();
    if (this.entity.isClass()) {
      classesOfEntity.add(this.entity.getURI());
    }
    if (this.entity.isInstance()) {
      classesOfEntity = getMetadataBean().getDirectClasses().getUris();
    }
    return classesOfEntity;
  }
  
  public RDFEntity getEntity()
  {
    return this.entity;
  }
  
  public float getScore()
  {
    return this.score;
  }
  
  public String getSemanticRelation()
  {
    return this.semanticRelation;
  }
  
  public String getIdPlugin()
  {
    return getEntity().getIdPlugin();
  }
  
  public boolean isHypernym()
  {
    if (this.semanticRelation.equals("hypernym")) {
      return true;
    }
    return false;
  }
  
  public boolean isHyperHypoNym()
  {
    if (this.semanticRelation.equals("hypernym")) {
      return true;
    }
    if (this.semanticRelation.equals("hyponym")) {
      return true;
    }
    return false;
  }
  
  public void setSynsetIndexed(boolean value)
  {
    this.synsetIndexed = value;
  }
  
  public boolean isSynsetIndexed()
  {
    return this.synsetIndexed;
  }
  
  public String toString()
  {
    return new String("SSR: " + this.entity.toString() + " score " + this.score + " SemanticR " + this.semanticRelation + " direct superclass " + getDirectParents());
  }
  
  public static ArrayList<SearchSemanticResult> FilterClasses(ArrayList<SearchSemanticResult> RDFQueryTerms)
  {
    ArrayList<SearchSemanticResult> res = new ArrayList();
    for (SearchSemanticResult SSR : RDFQueryTerms) {
      if (SSR.getEntity().isClass()) {
        res.add(SSR);
      }
    }
    return res;
  }
  
  public int compare(Object se1, Object se2)
  {
    SearchSemanticResult ssr1 = (SearchSemanticResult)se1;
    SearchSemanticResult ssr2 = (SearchSemanticResult)se2;
    if (ssr1.getScore() < ssr2.getScore()) {
      return -1;
    }
    if (ssr1.getScore() > ssr2.getScore()) {
      return 1;
    }
    return 0;
  }
  
  public int hashCode()
  {
    String id = getEntity().getURI() + getEntity().getLabel() + getEntity().getType() + getIdPlugin();
    byte[] bytes = id.getBytes();
    Checksum checksumEngine = new CRC32();
    checksumEngine.update(bytes, 0, bytes.length);
    
    return new Long(checksumEngine.getValue()).intValue();
  }
  
  public boolean equals(Object se)
  {
    SearchSemanticResult ssr = (SearchSemanticResult)se;
    if (getEntity().equals(ssr.getEntity())) {
      return true;
    }
    return false;
  }
  
  public void setOntologyPlugin(OntologyPlugin osPlugin)
    throws Exception
  {
    this.osPlugin = osPlugin;
  }
  
  public void addMetadataInfo(IndexManager indexManager)
    throws Exception
  {
    Logger log_poweraqua = Logger.getLogger("poweraqua");
    
    RDFEntity entity = this.entity;
    if (entity.getType().equals("instance")) {
      this.metadataBean = new MetadataBean(indexManager, entity);
    }
    if (entity.getType().equalsIgnoreCase("class"))
    {
      this.metadataBean = new MetadataBean(indexManager, entity);
      
      this.synsetIndexed = indexManager.isSynsetIndex();
      if (isSynsetIndexed())
      {
        log_poweraqua.log(Level.INFO, "Adding the taxonomical synset from the index for " + entity);
        setTaxonomySimilaritySynsets(indexManager.searchSynsets(entity));
      }
    }
  }
  
  public void setEntityToProperty(RDFEntityList domain, RDFEntityList range)
  {
    RDFProperty propEnt = new RDFProperty(this.entity.getURI(), this.entity.getLabel(), this.entity.getIdPlugin());
    propEnt.setDomain(domain);
    propEnt.setRange(range);
    setEntity(propEnt);
  }
  
  public RDFEntityList getEquivalentEntities()
  {
    try
    {
      if ((getOsPlugin().getRepositoryType().equals("OWL")) && (!getEntity().getType().equals("literal"))) {
        return getMetadataBean().getEquivalentEntity();
      }
    }
    catch (Exception e)
    {
      System.out.println("can not get equivalent entities for " + getEntity().getURI());
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  public RDFEntityList getSuperclasses()
  {
    return getMetadataBean().getSuperclasses();
  }
  
  public RDFEntityList getDirectSuperclasses()
  {
    return getMetadataBean().getDirectSuperclasses();
  }
  
  public RDFEntityList getDirectClasses()
  {
    return getMetadataBean().getDirectClasses();
  }
  
  public RDFEntityList getDirectSubclasses()
  {
    return getMetadataBean().getDirectSubclasses();
  }
  
  public RDFEntityList getSubclasses()
  {
    return getMetadataBean().getSubclasses();
  }
  
  public RDFEntityList getDirectParents()
  {
    if (getEntity().isClass()) {
      return getMetadataBean().getDirectSuperclasses();
    }
    return getMetadataBean().getDirectClasses();
  }
  
  public String getLexicalRelatedWord()
  {
    return this.lexicalRelatedWord;
  }
  
  public WNSynsetSetBean getTaxonomySimilaritySynsets()
  {
    return this.taxonomySimilaritySynsets;
  }
  
  public boolean isEmptyTaxonomySimilaritySynsets()
  {
    ArrayList<Synset> aux = getTaxonomySimilaritySynsets().getSynsetList();
    if (aux.isEmpty() == true) {
      return true;
    }
    return false;
  }
  
  public boolean isEmptyMatchSimilaritySynsets()
  {
    ArrayList<Synset> aux = getMatchSimilaritySynsets().getSynsetList();
    if (aux.isEmpty() == true) {
      return true;
    }
    return false;
  }
  
  public boolean isEmptyValidSynsets()
  {
    ArrayList<Synset> aux = getValidSynset();
    if (aux.isEmpty() == true) {
      return true;
    }
    return false;
  }
  
  public WNSynsetSetBean getMatchSimilaritySynsets()
  {
    return this.matchSimilaritySynsets;
  }
  
  public void setTaxonomySimilaritySynsets(WNSynsetSetBean similaritySynsets)
  {
    this.taxonomySimilaritySynsets = similaritySynsets;
  }
  
  public void setMatchSimilaritySynsets(WNSynsetSetBean similaritySynsets)
  {
    this.matchSimilaritySynsets = similaritySynsets;
  }
  
  public void setValidSynsets(boolean isWN_keyword, boolean isWN_SSR)
  {
    ArrayList<Synset> valid = new ArrayList();
    ArrayList<Synset> aux = new ArrayList();
    if ((isWN_keyword) && (isEmptyMatchSimilaritySynsets())) {
      return;
    }
    if ((isWN_keyword) && (isEmptyTaxonomySimilaritySynsets()))
    {
      setValidSynset(getMatchSimilaritySynsets().getSynsetList());
      return;
    }
    if ((!isWN_keyword) && (!isEmptyTaxonomySimilaritySynsets()))
    {
      setValidSynset(getTaxonomySimilaritySynsets().getSynsetList());
      return;
    }
    aux.addAll(getTaxonomySimilaritySynsets().getSynsetList());
    for (Synset syn : getMatchSimilaritySynsets().getSynsetList()) {
      if ((aux.contains(syn)) && (!valid.contains(syn))) {
        valid.add(syn);
      }
    }
    Iterator i$;
    if (valid.isEmpty()) {
      for (i$ = aux.iterator(); i$.hasNext();)
      {
        synTax = (Synset)i$.next();
        for (Synset synMatch : getMatchSimilaritySynsets().getSynsetList()) {
          try
          {
            WNSynsetSimilarity similarSynsets = WordNetSemantics.getWNMappingSynsets(synMatch, synTax);
            if (!similarSynsets.getBestRelationships().isEmpty())
            {
              MappingSession.getLog_poweraqua().log(Level.INFO, "Taxonomy and match synsets are not the same bus similar enough ...");
              for (Synset simsyn : similarSynsets.getBestSourceSynsets()) {
                if (!valid.contains(simsyn)) {
                  valid.add(simsyn);
                }
              }
            }
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        }
      }
    }
    Synset synTax;
    setValidSynset(valid);
  }
  
  public ArrayList<Synset> getValidSynsets()
  {
    return getValidSynset();
  }
  
  public void print()
  {
    System.out.println("URI " + getEntity().getURI());
    System.out.println("(" + getEntity().getLabel() + ")");
    System.out.println("Semantic Relation " + getSemanticRelation());
    
    System.out.println("(" + getEntity().getType() + ")");
    System.out.println("Exact map?" + isExact());
    System.out.println("Score " + getScore());
    System.out.println("DIRECT PARENTS " + getDirectParents());
    for (WNSynsetSimilarity synsetSim : getTaxonomySimilaritySynsets().getSynsetSimilarityList())
    {
      System.out.println("The best taxonomy synsets from " + synsetSim.getSource_lemma() + " to " + synsetSim.getTarget_lemma());
      for (Synset syn : synsetSim.getBestSourceSynsets()) {
        System.out.println(syn);
      }
      WNSynsetSimilarity.SimilaritySem sim = synsetSim.getBestSimilarity();
      if (sim != null)
      {
        System.out.println("Wu and Palmer taxonomy similarity " + sim.getSimilarity());
        System.out.println("Gloss similarity? " + sim.isGloss_similarity());
      }
    }
    for (Synset syn : getMatchSimilaritySynsets().getSynsetList()) {
      System.out.println(syn);
    }
    for (WNSynsetBean wnsynsetBean : getMatchSimilaritySynsets().getAllSynsetBean())
    {
      System.out.println("Wu and Palmer match similarity " + wnsynsetBean.getSimilarity());
      System.out.println("Gloss similarity? " + wnsynsetBean.isGloss_similarity());
    }
    ArrayList<Synset> valid = getValidSynsets();
    for (Synset syn : valid) {
      System.out.println("valid SYNSETS " + syn);
    }
  }
  
  public String printString()
  {
    String res = new String("URI " + getEntity().getURI());
    res = res.concat("Label " + getEntity().getLabel());
    res = res.concat("Type " + getEntity().getType());
    res = res.concat("Direct superclasses " + getDirectParents());
    
    return res;
  }
  
  public void printTest()
  {
    System.out.println("Ontology:" + getEntity().getIdPlugin() + " URI " + getEntity().getLocalName() + " ( " + getEntity().getLabel() + " ) : " + getScore());
  }
  
  public void printShort()
  {
    System.out.println("URI " + getEntity().getURI());
    System.out.println("Label " + getEntity().getLabel());
    System.out.println(this.semanticRelation + getScore());
  }
  
  public static void main(String[] args)
  {
    try
    {
      ArrayList<SearchSemanticResult> results = new ArrayList();
      SearchSemanticResult ssr = new SearchSemanticResult(new RDFEntity("class", "http://www.loa-cnr.it/ontologies/Plans.owl#task-postcondition", "task-postcondition", "http://pckm143.open.ac.uk:8080/sesame/DOLCE-db"), 1.0F, "s");
      
      SearchSemanticResult ssr2 = new SearchSemanticResult(new RDFEntity("class", "http://www.loa-cnr.it/ontologies/Plans.owl#task-postcondition", "task-postcondition", "http://pckm143.open.ac.uk:8080/sesame/DOLCE-db"), 1.0F, "c");
      SearchSemanticResult ssr3 = new SearchSemanticResult(new RDFEntity("class", "http://www.loa-cnr.it/ontologies/Plans.owl#task-postcondition", "task-postcondition-pepe", "http://pckm143.open.ac.uk:8080/sesame/DOLCE-db"), 1.0F, "c");
      results.add(ssr3);
      results.add(ssr);
      if (results.contains(ssr2)) {
        System.out.println("true");
      } else {
        System.out.println("false");
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public boolean isExact()
  {
    return this.exact;
  }
  
  public void setExact(boolean exact)
  {
    this.exact = exact;
  }
  
  public RDFEntityList getBackground_history()
  {
    if (this.background_history == null) {
      return new RDFEntityList();
    }
    return this.background_history;
  }
  
  public void setBackground_history(RDFEntityList background_history)
  {
    this.background_history = background_history;
  }
  
  public MetadataBean getMetadataBean()
  {
    return this.metadataBean;
  }
  
  public void setEntity(RDFEntity entity)
  {
    this.entity = entity;
  }
  
  public OntologyPlugin getOsPlugin()
  {
    return this.osPlugin;
  }
  
  public boolean isWatson()
  {
    return this.watson;
  }
  
  public void setWatson(boolean watson)
  {
    this.watson = watson;
  }
  
  public ArrayList<Synset> getValidSynset()
  {
    return this.validSynset;
  }
  
  public void setValidSynset(ArrayList<Synset> validSynset)
  {
    this.validSynset = validSynset;
  }
  
  public String getEmt_keyword()
  {
    if (this.emt_keyword == null) {
      return "";
    }
    return this.emt_keyword;
  }
  
  public void setEmt_keyword(String emt_keyword)
  {
    this.emt_keyword = emt_keyword;
  }
  
  public boolean isExactLexicalWord()
  {
    return this.exactLexicalWord;
  }
  
  public void setExactLexicalWord(boolean exactLexicalWord)
  {
    this.exactLexicalWord = exactLexicalWord;
  }
  
  public boolean isVirtuoso()
  {
    return this.virtuoso;
  }
  
  public void setVirtuoso(boolean virtuoso)
  {
    this.virtuoso = virtuoso;
  }
}

