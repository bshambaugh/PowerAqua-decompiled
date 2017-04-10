package poweraqua.powermap.elementPhase;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.core.utils.StringUtils;
import poweraqua.indexingService.manager.IndexManagerLucene;
import poweraqua.indexingService.manager.MultiIndexManager;
import poweraqua.lexicon.Lexicon;
import poweraqua.powermap.mappingModel.MappingBean;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.powermap.mappingModel.RecyclingBean;
import poweraqua.powermap.mappingModel.WordNetBean;
import poweraqua.query.QueryAnalyzer;
import poweraqua.query.QueryElementsBean;
import poweraqua.serviceConfig.MultiOntologyManager;

public class SyntacticComponent
{
  public static float COMPOUND_THRESH = new Float(0.6D).floatValue();
  public static float STANDARD_THRESH_ONTO = new Float(0.443D).floatValue();
  public static float STANDARD_THRESH_KB = new Float(0.414D).floatValue();
  public static float MIN_STANDARD_THRESH = new Float(0.33D).floatValue();
  public int MAX_NUM_HYP = 40;
  public int MAX_NUM_HYP_PER_ONTO = 20;
  public static int MAX_NUM_WN_HYPERNMYS = 3;
  public static int MAX_NUM_WN_SYNONYMS = 5;
  public static int MAX_NUM_WN_DERIVED = 5;
  public static float FUZZY_THRESH_ONTO = new Float(0.51D).floatValue();
  public static float FUZZY_THRESH_KB = new Float(0.53D).floatValue();
  public static float LEXICALY_RELATED_THRESH_SYN = new Float(0.5D).floatValue();
  public static float LEXICALY_RELATED_THRESH_HYP = new Float(0.7D).floatValue();
  public static float QUERYTERM_THRESH_KB = new Float(0.66D).floatValue();
  public static float SPELL_THRESH = new Float(0.5D).floatValue();
  public static float SPELL_THRESH_COMPOUND = new Float(1.4D).floatValue();
  public static final String AND_CONVERAGE = "AND";
  public static final String OR_CONVERAGE = "OR";
  public static final String[] too_generic_classes = { "object", "agent", "resource", "thing", "entity", "physical body", "tangible", "tangible thing", "intangible thing", "legal agent", "generic agent", "temporal thing", "physical object", "physical", "cognitiveagent", "cognitive agent", "node", "entitys", "event", "activity", "situation", "individual" };
  private MappingSession session;
  private ArrayList<MappingBean> mappingBeans;
  
  public SyntacticComponent(MappingSession session)
  {
    this.session = session;
    if (this.session.getSesame_threshold() > 0.0D)
    {
      double thresh = this.session.getSesame_threshold();
      setThresholds(new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue());
    }
  }
  
  public SyntacticComponent(MappingSession session, ArrayList<MappingBean> mappingBeans)
  {
    this.session = session;
    if (this.session.getSesame_threshold() > 0.0D)
    {
      double thresh = this.session.getSesame_threshold();
      setThresholds(new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue(), new Float(thresh).floatValue());
    }
    setMappingBeans(mappingBeans);
  }
  
  public boolean isEmptyMapping()
  {
    for (MappingBean mb : this.mappingBeans) {
      if (!mb.getEntityMappingTable().isEmpty()) {
        return false;
      }
    }
    return true;
  }
  
  public void setThresholds(double COMPOUND_THRESH, double STANDARD_THRESH_ONTO, double STANDARD_THRESH_KB, double FUZZY_THRESH_ONTO, double FUZZY_THRESH_KB)
  {
    COMPOUND_THRESH = new Float(COMPOUND_THRESH).floatValue();
    STANDARD_THRESH_ONTO = new Float(STANDARD_THRESH_ONTO).floatValue();
    STANDARD_THRESH_KB = new Float(STANDARD_THRESH_KB).floatValue();
    FUZZY_THRESH_ONTO = new Float(FUZZY_THRESH_ONTO).floatValue();
    FUZZY_THRESH_KB = new Float(FUZZY_THRESH_KB).floatValue();
  }
  
  public void setThresholds(float STANDARD_THRESH_ONTO, float STANDARD_THRESH_KB, float FUZZY_THRESH_ONTO, float FUZZY_THRESH_KB)
  {
    STANDARD_THRESH_ONTO = STANDARD_THRESH_ONTO;
    STANDARD_THRESH_KB = STANDARD_THRESH_KB;
    FUZZY_THRESH_ONTO = FUZZY_THRESH_ONTO;
    FUZZY_THRESH_KB = FUZZY_THRESH_KB;
  }
  
  public void setThresholds(float COMPOUND_THRESH, float STANDARD_THRESH_ONTO, float STANDARD_THRESH_KB, float FUZZY_THRESH_ONTO, float FUZZY_THRESH_KB, float SPELL_THRESH, float WN_LEXICALY_RELATED_THRESH_SYN, float WN_LEXICALY_RELATED_THRESH_HYP, float WN_SYNONYMS_THRESH_KB)
  {
    COMPOUND_THRESH = COMPOUND_THRESH;
    STANDARD_THRESH_ONTO = STANDARD_THRESH_ONTO;
    STANDARD_THRESH_KB = STANDARD_THRESH_KB;
    FUZZY_THRESH_ONTO = FUZZY_THRESH_ONTO;
    FUZZY_THRESH_KB = FUZZY_THRESH_KB;
    SPELL_THRESH = SPELL_THRESH;
    LEXICALY_RELATED_THRESH_SYN = WN_LEXICALY_RELATED_THRESH_SYN;
    LEXICALY_RELATED_THRESH_HYP = WN_LEXICALY_RELATED_THRESH_HYP;
  }
  
  public EntityMappingTable standardSemanticEntitySearch(String keyword, boolean compound, ArrayList<String> restrictedKeywords, boolean is_queryTerm)
    throws Exception
  {
    System.out.println("Standard searches");
    EntityMappingTable resultsFromIndex;
    EntityMappingTable resultsFromIndex;
    if (compound)
    {
      resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, "equivalentMatching", COMPOUND_THRESH, COMPOUND_THRESH, 2, restrictedKeywords);
    }
    else if (is_queryTerm)
    {
      EntityMappingTable resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, "equivalentMatching", STANDARD_THRESH_ONTO, QUERYTERM_THRESH_KB, 2, restrictedKeywords);
      if ((this.session.getMultiIndexManager().isUsePowerMap()) && (resultsFromIndex.isEmpty()) && (MIN_STANDARD_THRESH < STANDARD_THRESH_ONTO))
      {
        System.out.println("Lowering the threshold to obtain mappings which score above " + MIN_STANDARD_THRESH);
        resultsFromIndex = IndexManagerLucene.multiSearchEntityMappings(keyword, "equivalentMatching", MIN_STANDARD_THRESH, STANDARD_THRESH_KB, 2);
      }
    }
    else
    {
      resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, "equivalentMatching", STANDARD_THRESH_ONTO, STANDARD_THRESH_KB, 2, restrictedKeywords);
      if ((this.session.getMultiIndexManager().isUsePowerMap()) && (resultsFromIndex.isEmpty()) && (MIN_STANDARD_THRESH < STANDARD_THRESH_ONTO))
      {
        System.out.println("Lowering the threshold to obtain mappings which score above " + MIN_STANDARD_THRESH);
        resultsFromIndex = IndexManagerLucene.multiSearchEntityMappings(keyword, "equivalentMatching", MIN_STANDARD_THRESH, MIN_STANDARD_THRESH, 2);
      }
    }
    return resultsFromIndex;
  }
  
  public EntityMappingTable fuzzySemanticEntitySearch(String keyword, boolean compound, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    System.out.println("Fuzzy searches for " + keyword);
    EntityMappingTable resultsFromIndex;
    EntityMappingTable resultsFromIndex;
    if (compound) {
      resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, "equivalentMatching", COMPOUND_THRESH, COMPOUND_THRESH, 4, restrictedKeywords);
    } else {
      resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, "equivalentMatching", FUZZY_THRESH_ONTO, FUZZY_THRESH_KB, 4, restrictedKeywords);
    }
    return resultsFromIndex;
  }
  
  public EntityMappingTable spellSemanticEntitySearch(String keyword)
    throws Exception
  {
    boolean isCompound = StringUtils.isCompound(keyword);
    String queryCompound;
    String queryCompound;
    if (isCompound) {
      queryCompound = "\"" + keyword + "\"";
    } else {
      queryCompound = keyword;
    }
    System.out.println("Spell searches");
    EntityMappingTable resultsFromIndex;
    EntityMappingTable resultsFromIndex;
    if (!isCompound) {
      resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(queryCompound, "equivalentMatching", SPELL_THRESH, 8);
    } else {
      resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(queryCompound, "equivalentMatching", SPELL_THRESH_COMPOUND, 8);
    }
    return resultsFromIndex;
  }
  
  public EntityMappingTable standardSemanticSynonymSearch(String keyword, ArrayList<String> synonyms, boolean isqueryterm, boolean from_wordnet, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    if (synonyms.isEmpty()) {
      return new EntityMappingTable(keyword);
    }
    System.out.println("Standard synonym searches");
    ArrayList<String> compounds_syns = new ArrayList();
    ArrayList<String> non_compounds_syns = new ArrayList();
    for (String synonym : synonyms) {
      if (StringUtils.isCompound(synonym)) {
        compounds_syns.add(synonym);
      } else {
        non_compounds_syns.add(synonym);
      }
    }
    EntityMappingTable resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, non_compounds_syns, "synonym", LEXICALY_RELATED_THRESH_SYN, 2, isqueryterm, from_wordnet, restrictedKeywords);
    
    resultsFromIndex.merge(this.session.getMultiIndexManager().searchEntityMappings(keyword, compounds_syns, "synonym", COMPOUND_THRESH, 2, isqueryterm, from_wordnet, restrictedKeywords));
    
    return resultsFromIndex;
  }
  
  public EntityMappingTable semanticSynonymSearch(String keyword, ArrayList<String> synonyms, boolean isqueryterm, boolean from_wordnet, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    ArrayList<String> compounds_syns = new ArrayList();
    ArrayList<String> non_compounds_syns = new ArrayList();
    for (String synonym : synonyms) {
      if (StringUtils.isCompound(synonym)) {
        compounds_syns.add(synonym);
      } else {
        non_compounds_syns.add(synonym);
      }
    }
    EntityMappingTable res = standardSemanticSynonymSearch(keyword, compounds_syns, isqueryterm, from_wordnet, restrictedKeywords);
    res.merge(standardSemanticSynonymSearch(keyword, non_compounds_syns, isqueryterm, from_wordnet, restrictedKeywords));
    
    res.merge(fuzzySemanticSynonymSearch(keyword, synonyms, true, restrictedKeywords));
    return res;
  }
  
  public EntityMappingTable semanticHypernymSearch(String keyword, ArrayList<String> hypernyms, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    ArrayList<String> compounds_syns = new ArrayList();
    ArrayList<String> non_compounds_syns = new ArrayList();
    for (String hypernym : hypernyms) {
      if (StringUtils.isCompound(hypernym)) {
        compounds_syns.add(hypernym);
      } else {
        non_compounds_syns.add(hypernym);
      }
    }
    EntityMappingTable res = standardSemanticHypernymSearch(keyword, compounds_syns, restrictedKeywords);
    res.merge(standardSemanticHypernymSearch(keyword, non_compounds_syns, restrictedKeywords));
    res.merge(fuzzySemanticHypernymSearch(keyword, non_compounds_syns, restrictedKeywords));
    return res;
  }
  
  public EntityMappingTable semanticHyponymSearch(String keyword, ArrayList<String> hypos, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    EntityMappingTable res = standardSemanticHyponymSearch(keyword, hypos, restrictedKeywords);
    return res;
  }
  
  public EntityMappingTable fuzzySemanticSynonymSearch(String keyword, ArrayList<String> synonyms, boolean isqueryterm, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    if (synonyms.isEmpty()) {
      return new EntityMappingTable(keyword);
    }
    System.out.println("Fuzzy synonym searches");
    
    EntityMappingTable resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, synonyms, "synonym", LEXICALY_RELATED_THRESH_SYN, 4, isqueryterm, false, restrictedKeywords);
    
    return resultsFromIndex;
  }
  
  public EntityMappingTable standardSemanticHypernymSearch(String keyword, ArrayList<String> hypernyms, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    hypernyms = removeTooGenericClasses(hypernyms);
    if (hypernyms.isEmpty()) {
      return new EntityMappingTable(keyword);
    }
    System.out.println("Standard hypernym searches");
    ArrayList<String> compounds_syns = new ArrayList();
    ArrayList<String> non_compounds_syns = new ArrayList();
    for (String synonym : hypernyms) {
      if (StringUtils.isCompound(synonym)) {
        compounds_syns.add(synonym);
      } else {
        non_compounds_syns.add(synonym);
      }
    }
    System.out.println("Standard hypernym searches");
    
    EntityMappingTable resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, non_compounds_syns, "hypernym", LEXICALY_RELATED_THRESH_HYP, 2, restrictedKeywords);
    
    resultsFromIndex.merge(this.session.getMultiIndexManager().searchEntityMappings(keyword, compounds_syns, "hypernym", COMPOUND_THRESH, 2, restrictedKeywords));
    
    return resultsFromIndex;
  }
  
  public EntityMappingTable fuzzySemanticHypernymSearch(String keyword, ArrayList<String> hypernyms, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    System.out.println("Fuzzy hypernym searches");
    hypernyms = removeTooGenericClasses(hypernyms);
    EntityMappingTable resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, hypernyms, "hypernym", LEXICALY_RELATED_THRESH_HYP, 4, restrictedKeywords);
    
    return resultsFromIndex;
  }
  
  public EntityMappingTable standardSemanticHyponymSearch(String keyword, ArrayList<String> hyponyms, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    System.out.println("Standard hyponym searches");
    EntityMappingTable resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, hyponyms, "hyponym", LEXICALY_RELATED_THRESH_HYP, 2, restrictedKeywords);
    
    return resultsFromIndex;
  }
  
  public EntityMappingTable fuzzySemanticHyponymSearch(String keyword, ArrayList<String> hyponyms, boolean is_queryterm, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    System.out.println("Fuzzy hyponym searches");
    EntityMappingTable resultsFromIndex = this.session.getMultiIndexManager().searchEntityMappings(keyword, hyponyms, "hyponym", LEXICALY_RELATED_THRESH_HYP, 4, is_queryterm, false, restrictedKeywords);
    
    return resultsFromIndex;
  }
  
  public void matchBean(MappingBean mapingBean)
    throws Exception
  {
    EntityMappingTable res = matchMappingBean(mapingBean);
    mapingBean.setEntityMappingTable(res);
    ontologyFiltering(mapingBean);
  }
  
  public MappingBean matchSplittedKeyword(MappingBean mapingBean)
    throws Exception
  {
    mapingBean.setFind_hypernyms(false);
    EntityMappingTable res = matchMappingBean(mapingBean);
    mapingBean.setEntityMappingTable(res);
    ontologyFiltering(mapingBean);
    return mapingBean;
  }
  
  public EntityMappingTable matchMappingBean(MappingBean mapingBean)
    throws Exception
  {
    System.out.println("*************Matching " + mapingBean.getKeyword());
    long time = System.currentTimeMillis();
    
    ArrayList<String> restrictedKeywords = mapingBean.getEntityMappingTable().getRestrictedKeywords();
    boolean compound = StringUtils.isCompound(mapingBean.getKeyword());
    if (compound) {
      mapingBean.setFind_hypernyms(false);
    }
    EntityMappingTable res = standardSemanticEntitySearch(mapingBean.getKeyword(), compound, restrictedKeywords, mapingBean.isIs_queryTerm());
    res.merge(fuzzySemanticEntitySearch(mapingBean.getKeyword(), compound, restrictedKeywords));
    if (mapingBean.isIs_wordnet(compound))
    {
      System.out.println("Looking for WN synonyms, lemma and derived...");
      String lemma = mapingBean.getWordNetBean().getWN_lemma();
      if ((lemma != null) && (!lemma.equalsIgnoreCase(mapingBean.getKeyword())))
      {
        res.setWNLemma(lemma);
        res.merge(standardSemanticEntitySearch(lemma, compound, restrictedKeywords, mapingBean.isIs_queryTerm()));
        res.merge(fuzzySemanticEntitySearch(mapingBean.getKeyword(), compound, restrictedKeywords));
      }
      ArrayList<String> sinonimos;
      ArrayList<String> sinonimos;
      if (compound) {
        sinonimos = mapingBean.getWordNetBean().getSynonyms(MAX_NUM_WN_SYNONYMS);
      } else {
        sinonimos = mapingBean.getWordNetBean().getSynonymsAndDerived(MAX_NUM_WN_SYNONYMS, MAX_NUM_WN_DERIVED);
      }
      if (sinonimos.contains(lemma)) {
        sinonimos.remove(lemma);
      }
      String k = mapingBean.getKeyword();
      sinonimos.remove(k.toLowerCase());
      sinonimos.remove(k);
      sinonimos.remove(k.replaceAll(" ", "_").toLowerCase());
      sinonimos.remove(k.replaceAll(" ", "-").toLowerCase());
      
      sinonimos.addAll(this.session.getLexicon().getSynonyms(mapingBean.getKeyword()));
      if ((!sinonimos.isEmpty()) && (this.session.isUseSynonyms()))
      {
        res.merge(semanticSynonymSearch(mapingBean.getKeyword(), sinonimos, mapingBean.isIs_queryTerm(), true, restrictedKeywords));
        
        res.setWNDerived(mapingBean.getWordNetBean().getDerived());
      }
      if ((mapingBean.isFind_hypernyms()) && (this.session.isUseSynonyms()))
      {
        System.out.println("Looking for WN hypernyms...");
        res.merge(semanticHypernymSearch(mapingBean.getKeyword(), mapingBean.getWordNetBean().getHypernyms(MAX_NUM_WN_HYPERNMYS), restrictedKeywords));
        
        System.out.println("Looking for WN meronyms...");
        res.merge(semanticHyponymSearch(mapingBean.getKeyword(), mapingBean.getWordNetBean().getMeronyms(), restrictedKeywords));
      }
    }
    else
    {
      System.out.println("Non lexical related words found in WN for " + mapingBean.getKeyword());
      if ((mapingBean.getKeyword().endsWith("s")) || (mapingBean.getKeyword().endsWith("s\"")))
      {
        String queryTerm = mapingBean.getWordNetBean().getSingular();
        System.out.println("Looking for the singular " + queryTerm);
        
        res.merge(standardSemanticEntitySearch(queryTerm, compound, restrictedKeywords, mapingBean.isIs_queryTerm()));
      }
    }
    if ((!mapingBean.getKeyword().endsWith("s")) && (!mapingBean.getKeyword().endsWith("s\"")))
    {
      String plural = StringUtils.SingularToPlural(mapingBean.getKeyword());
      if (!plural.equalsIgnoreCase(mapingBean.getKeyword()))
      {
        System.out.println("Looking for the plural " + plural);
        
        ArrayList<String> aux_plural = new ArrayList();
        aux_plural.add(plural);
        
        res.merge(standardSemanticSynonymSearch(mapingBean.getKeyword(), aux_plural, mapingBean.isIs_queryTerm(), false, restrictedKeywords));
      }
    }
    time = System.currentTimeMillis() - time;
    
    float secs = (float)time / 1000.0F;
    System.out.println("TIME PASSED SEARCHING INDEXES (" + time + ") :  " + secs + " secs");
    res.setRestrictedKeywords(restrictedKeywords);
    return res;
  }
  
  public void ontologyFiltering(MappingBean mapingBean)
    throws Exception
  {
    EntityMappingTable eliminatedString = ontologyStringFiltering(mapingBean.getEntityMappingTable());
    mapingBean.getRecyclingBean().addStringRecyclingMapping(eliminatedString);
    
    addOntologyAndMetadataInfo(mapingBean.getEntityMappingTable());
    boolean filter_UnconnectedProperties = true;
    EntityMappingTable eliminatedTaxonomy = mapingBean.getEntityMappingTable().groupLiteralsToEntities(filter_UnconnectedProperties);
    mapingBean.getRecyclingBean().addTaxonomyRecyclingMapping(eliminatedTaxonomy);
    
    EntityMappingTable eliminatedExact = filterExactMappings(mapingBean.getEntityMappingTable());
    mapingBean.getRecyclingBean().addStringRecyclingMapping(eliminatedExact);
    
    EntityMappingTable eliminatedTaxonomy2 = ontologyTaxonomyFiltering(mapingBean.getEntityMappingTable());
    mapingBean.getRecyclingBean().addTaxonomyRecyclingMapping(eliminatedTaxonomy2);
  }
  
  private EntityMappingTable ontologyStringFiltering(EntityMappingTable res)
  {
    res.eraseNonValidOntologyEntries(this.session.getMultiOntologyManager().listIdPlugings());
    System.out.println("**********Starting with the filtering");
    
    EntityMappingTable eliminatedString = res.filerSimilarStringLabels();
    return eliminatedString;
  }
  
  private EntityMappingTable filterExactMappings(EntityMappingTable res)
  {
    EntityMappingTable eliminatedString = res.filterExactMappings();
    return eliminatedString;
  }
  
  private void addOntologyAndMetadataInfo(EntityMappingTable res)
    throws Exception
  {
    res.addOntologyInfo(this.session.getMultiOntologyManager());
    res.addMetadataInfo(this.session.getMultiIndexManager());
  }
  
  private EntityMappingTable filterUnconnectedProperties(EntityMappingTable res)
    throws Exception
  {
    System.out.println("plugin and metadata loaded");
    EntityMappingTable eliminatedTaxonomy = res.filterUnconnectedEntities();
    return eliminatedTaxonomy;
  }
  
  private EntityMappingTable ontologyTaxonomyFiltering(EntityMappingTable res)
    throws Exception
  {
    EntityMappingTable eliminatedTaxonomy = res.groupPerOntologyTaxonomy();
    
    return eliminatedTaxonomy;
  }
  
  public void match()
    throws Exception
  {
    for (MappingBean mappinBean : this.mappingBeans) {
      matchBean(mappinBean);
    }
  }
  
  public ArrayList<String> sortByCoverage()
  {
    if (getMappingBeans().size() < 1) {
      return null;
    }
    if (getMappingBeans().size() == 1) {
      return ((MappingBean)getMappingBeans().get(0)).getEntityMappingTable().getOntologyIDMappings();
    }
    Hashtable<String, Integer> ontologiesTable = new Hashtable();
    for (MappingBean bean : getMappingBeans()) {
      for (String ontology : bean.getEntityMappingTable().getOntologyIDMappings()) {
        if (ontologiesTable.containsKey(ontology))
        {
          Integer value = new Integer(((Integer)ontologiesTable.get(ontology)).intValue() + 1);
          ontologiesTable.put(ontology, value);
        }
        else
        {
          ontologiesTable.put(ontology, Integer.valueOf(1));
        }
      }
    }
    Hashtable<Integer, ArrayList<String>> ontologiesFrecuency = new Hashtable();
    for (String ontology : ontologiesTable.keySet()) {
      if (ontologiesFrecuency.containsKey(ontologiesTable.get(ontology)))
      {
        ((ArrayList)ontologiesFrecuency.get(ontologiesTable.get(ontology))).add(ontology);
      }
      else
      {
        ArrayList<String> ontologies = new ArrayList();
        ontologies.add(ontology);
        ontologiesFrecuency.put(ontologiesTable.get(ontology), ontologies);
      }
    }
    ArrayList<String> finalOrder = new ArrayList();
    ArrayList<Integer> numericOrder = new ArrayList(ontologiesFrecuency.keySet());
    
    Collections.sort(numericOrder);
    Collections.reverse(numericOrder);
    for (Integer integer : numericOrder) {
      finalOrder.addAll((Collection)ontologiesFrecuency.get(integer));
    }
    return finalOrder;
  }
  
  public static ArrayList<String> sortByCoverage(ArrayList<MappingBean> mappingBeans)
  {
    if (mappingBeans.size() < 1) {
      return null;
    }
    if (mappingBeans.size() == 1) {
      return ((MappingBean)mappingBeans.get(0)).getEntityMappingTable().getOntologyIDMappings();
    }
    Hashtable<String, Integer> ontologiesTable = new Hashtable();
    for (MappingBean bean : mappingBeans) {
      for (String ontology : bean.getEntityMappingTable().getOntologyIDMappings()) {
        if (ontologiesTable.containsKey(ontology))
        {
          Integer value = new Integer(((Integer)ontologiesTable.get(ontology)).intValue() + 1);
          ontologiesTable.put(ontology, value);
        }
        else
        {
          ontologiesTable.put(ontology, Integer.valueOf(1));
        }
      }
    }
    Hashtable<Integer, ArrayList<String>> ontologiesFrecuency = new Hashtable();
    for (String ontology : ontologiesTable.keySet()) {
      if (ontologiesFrecuency.containsKey(ontologiesTable.get(ontology)))
      {
        ((ArrayList)ontologiesFrecuency.get(ontologiesTable.get(ontology))).add(ontology);
      }
      else
      {
        ArrayList<String> ontologies = new ArrayList();
        ontologies.add(ontology);
        ontologiesFrecuency.put(ontologiesTable.get(ontology), ontologies);
      }
    }
    ArrayList<String> finalOrder = new ArrayList();
    ArrayList<Integer> numericOrder = new ArrayList(ontologiesFrecuency.keySet());
    
    Collections.sort(numericOrder);
    Collections.reverse(numericOrder);
    for (Integer integer : numericOrder) {
      finalOrder.addAll((Collection)ontologiesFrecuency.get(integer));
    }
    return finalOrder;
  }
  
  public void matchOntologyBackground()
    throws Exception
  {
    for (MappingBean mappinBean : this.mappingBeans)
    {
      EntityMappingTable background = matchOntologyBackground(mappinBean);
      if (!background.isEmpty())
      {
        ontologyStringFiltering(background);
        addOntologyAndMetadataInfo(background);
        filterUnconnectedProperties(background);
        mappinBean.getEntityMappingTable().merge(background);
        
        boolean filterProperties = false;
        mappinBean.getEntityMappingTable().groupLiteralsToEntities(filterProperties);
        filterExactMappings(mappinBean.getEntityMappingTable());
        ontologyTaxonomyFiltering(mappinBean.getEntityMappingTable());
      }
    }
  }
  
  public EntityMappingTable matchOntologyBackground(MappingBean mappingBean)
    throws Exception
  {
    System.out.println("using the ontology background to find new matches");
    EntityMappingTable e1 = mappingBean.getEntityMappingTable();
    
    EntityMappingTable ontoRes = new EntityMappingTable(e1.getKeyword());
    ArrayList<String> restrictedKeywords = mappingBean.getEntityMappingTable().getRestrictedKeywords();
    
    Hashtable<String, RDFEntityList> ontoSynonyms = new Hashtable();
    for (String ontology : e1.getOntologyIDMappings())
    {
      is_OWL = false;
      try
      {
        if (this.session.getMultiOntologyManager().getPlugin(ontology).getRepositoryType().equals("OWL")) {
          is_OWL = true;
        }
      }
      catch (Exception ex)
      {
        System.out.println("TODO WATSON: whether the ontology is RDF or OWL");
      }
      for (i$ = e1.getOntologyMappings(ontology).iterator(); i$.hasNext();)
      {
        SSR = (SearchSemanticResult)i$.next();
        if ((SSR.isExact()) && (is_OWL) && (!SSR.getEntity().isLiteral()))
        {
          ArrayList<String> auxs = SSR.getEquivalentEntities().getLabels();
          if ((auxs != null) && (auxs.size() > 0)) {
            for (String aux : auxs) {
              if ((!Character.isDigit(aux.charAt(0))) && (!aux.startsWith("guid"))) {
                if (!ontoSynonyms.keySet().contains(aux))
                {
                  RDFEntityList auxRelatedEntities = new RDFEntityList();
                  auxRelatedEntities.addRDFEntity(SSR.getEntity());
                  ontoSynonyms.put(aux, auxRelatedEntities);
                }
                else
                {
                  RDFEntityList auxRelatedEntities = (RDFEntityList)ontoSynonyms.get(aux);
                  auxRelatedEntities.addRDFEntity(SSR.getEntity());
                  ontoSynonyms.put(aux, auxRelatedEntities);
                }
              }
            }
          }
        }
      }
    }
    boolean is_OWL;
    Iterator i$;
    SearchSemanticResult SSR;
    if (ontoSynonyms.keySet().contains(e1.getKeyword().toLowerCase())) {
      ontoSynonyms.remove(e1.getKeyword().toLowerCase());
    }
    if (mappingBean.isIs_wordnet()) {
      for (String syn : mappingBean.getWordNetBean().getSynonyms()) {
        if (ontoSynonyms.keySet().contains(syn)) {
          ontoSynonyms.remove(syn);
        }
      }
    }
    if ((ontoSynonyms != null) && (ontoSynonyms.size() > 0))
    {
      System.out.println("We found " + ontoSynonyms.size() + "synonyms using the ontology background ");
      boolean compound = StringUtils.isCompound(e1.getKeyword());
      ArrayList<String> syns = new ArrayList();
      for (String ontoSynonym : ontoSynonyms.keySet()) {
        syns.add(ontoSynonym);
      }
      System.out.println("Matching " + e1.getKeyword());
      EntityMappingTable res_syn = standardSemanticSynonymSearch(e1.getKeyword(), syns, mappingBean.isIs_queryTerm(), false, restrictedKeywords);
      
      ontoRes.addOntologyBackgroundHistory(ontoSynonyms);
    }
    return ontoRes;
  }
  
  public ArrayList<MappingBean> getMappingBeans()
  {
    return this.mappingBeans;
  }
  
  public void setMappingBeans(ArrayList<MappingBean> mappingBeans)
  {
    this.mappingBeans = mappingBeans;
  }
  
  private static ArrayList<String> removeTooGenericClasses(ArrayList<String> auxs)
  {
    ArrayList<String> res = new ArrayList();
    for (String aux : auxs)
    {
      aux.replace("-", " ");
      aux.replace("_", " ");
      res.add(aux.toLowerCase().trim());
    }
    for (int i = 0; i < too_generic_classes.length; i++)
    {
      String generic_class = too_generic_classes[i];
      if (res.contains(generic_class))
      {
        MappingSession.getLog_poweraqua().log(Level.INFO, "Eliminating generic term " + generic_class);
        res.remove(generic_class);
      }
    }
    return res;
  }
  
  public static ArrayList<String> readLine()
    throws Exception
  {
    File file = null;
    FileReader freader = null;
    LineNumberReader lnreader = null;
    ArrayList<String> results = new ArrayList();
    try
    {
      file = new File("/Users/vl474/Trabajo/NetBeans projects/subversionProjects/PowerAquaSVN/tests/powerAqua_not_classified.txt");
      freader = new FileReader(file);
      lnreader = new LineNumberReader(freader);
      String line = "";
      while ((line = lnreader.readLine()) != null)
      {
        results.add(line);
        System.out.println("Line:  " + lnreader.getLineNumber() + ": " + line);
      }
    }
    finally
    {
      freader.close();
      lnreader.close();
    }
    return results;
  }
  
  public void test()
    throws Exception
  {
    String[] keywords = { "zirconium", "dachshund", "academics", "projects", "papers", "publications", "semantic web", "knowledge media institute", "vanessa", "enrico motta", "asun gomez perez", "asuncion gomez-perez", "cat", "bengal cats", "bengal cat", "bengal cat breeders", "breeders", "cats", "beavers", "pizza", "human", "person", "group", "groups", "organization", "person", "albums", "album", "rock group", "rock", "singers", "singer", "musicians", "nirvana", "metallica", "genre", "Spain", "usa", "united states", "states", "republic of poland", "poland", "france", "french republic", "republic of france", "russia", "russiaFederation", "country", "countries", "nebraska", "capital", "city", "cities", "european nations", "east europe", "eastern europe", "europe", "european", "black sea", "river", "rivers", "russian rivers", "Turkey", "Sacramento area", "californian", "california", "Mississipi", "lakes", "lake", "holland country", "play", "types", "origin", "member", "members", "treatment", "treatments", "bordering", "borders", "peer gynt suite", "mistletoe", "calcium", "earthquake", "diseases", "disease", "hair loss", "earthquake", "wine", "dry", "smoking", "habitats", "habitat", "prizes", "prize", "award", "awards", "fasting periods", "fasting period", "1997", "chinese", "restaurants", "frog", "tornadoes", "mexican", "food", "knowledge web", "location", "state", "place", "academic", "task", "labor", "piece_of_writting", "document", "business", "business_enterprises", "feline", "adult_male", "units", "european_country", "european_nation", "land" };
    
    ArrayList<SearchSemanticResult> SSROntologyStandard = new ArrayList();
    for (String keyword : keywords)
    {
      System.out.println(">>>> KEYWORD >>>>>>>>>>>>>: " + keyword);
      System.out.println("*****************************************");
      
      ArrayList<SearchSemanticResult> SSRKBStandard = new ArrayList();
      System.out.print("standard searches on the kb ");
      SSRKBStandard = IndexManagerLucene.search(keyword, "equivalentMatching", STANDARD_THRESH_KB, 2, IndexManagerLucene.getKbMultiSearcher());
      for (SearchSemanticResult SSR : SSRKBStandard) {
        if (SSR.getScore() < 0.47D) {
          SSR.printTest();
        }
      }
    }
  }
  
  public static void main(String[] args)
    throws Exception
  {
    try
    {
      String query = "spanish";
      
      MappingSession mapSession = new MappingSession();
      
      QueryAnalyzer queryAnalyzer = new QueryAnalyzer();
      QueryElementsBean queryElements = queryAnalyzer.analyzeQuery(query);
      
      boolean queryTerm = false;
      ArrayList<MappingBean> mappingBeans = MappingBean.getMappingBeans(queryElements, mapSession.getRealpath(), queryTerm);
      
      SyntacticComponent map = new SyntacticComponent(mapSession, mappingBeans);
      
      map.setThresholds(new Float(0.0D).floatValue(), new Float(0.0D).floatValue(), new Float(0.0D).floatValue(), new Float(0.0D).floatValue(), new Float(0.0D).floatValue(), new Float(0.0D).floatValue(), new Float(0.0D).floatValue(), new Float(0.0D).floatValue(), new Float(0.0D).floatValue());
      
      map.match();
      
      map.matchOntologyBackground();
      
      ArrayList<String> sortedOntologies = map.sortByCoverage();
      
      mapSemantic = new SemanticComponent(mapSession.getRealpath(), mappingBeans);
      
      mapSemantic.addSemanticInfo();
      if (sortedOntologies == null) {
        System.out.println("No results");
      } else {
        for (i$ = sortedOntologies.iterator(); i$.hasNext();)
        {
          key = (String)i$.next();
          System.out.println("*************Element Mappings found in " + key);
          for (MappingBean mappingBean : mapSemantic.getMappingBeans()) {
            mappingBean.printShort(key);
          }
        }
      }
    }
    catch (Exception ex)
    {
      SemanticComponent mapSemantic;
      Iterator i$;
      String key;
      ex.printStackTrace();
    }
  }
}

