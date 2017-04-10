package poweraqua.powermap.elementPhase;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import net.sf.extjwnl.data.Synset;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import poweraqua.WordNetJWNL.WNSynsetSetBean;
import poweraqua.WordNetJWNL.WNSynsetSimilarity;
import poweraqua.WordNetJWNL.WNSynsetSimilarity.SimilaritySem;
import poweraqua.WordNetJWNL.WNTermSimilarity;
import poweraqua.WordNetJWNL.WordNet;
import poweraqua.WordNetJWNL.WordNetSemantics;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.utils.LabelSplitter;
import poweraqua.powermap.mappingModel.MappingBean;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.powermap.mappingModel.RecyclingBean;
import poweraqua.query.QueryAnalyzer;
import poweraqua.query.QueryElementsBean;

public class SemanticComponent
{
  private ArrayList<MappingBean> mappingBeans;
  private WNTermSimilarity termSimilarity;
  
  public SemanticComponent(String realpath, ArrayList<MappingBean> mappingBeans)
    throws Exception
  {
    this.mappingBeans = mappingBeans;
    this.termSimilarity = new WNTermSimilarity(realpath);
  }
  
  public SemanticComponent(String realpath, MappingBean mappingBean)
    throws Exception
  {
    this.mappingBeans = new ArrayList();
    this.mappingBeans.add(mappingBean);
    this.termSimilarity = new WNTermSimilarity(realpath);
  }
  
  public SemanticComponent(String realpath)
    throws Exception
  {
    this.termSimilarity = new WNTermSimilarity(realpath);
  }
  
  public ArrayList<MappingBean> getMappingBeans()
  {
    return this.mappingBeans;
  }
  
  public void addSemanticInfo()
    throws Exception
  {
    System.out.println("Adding semantic info - POSTPONED");
  }
  
  public void closeOpenFileDescriptors()
  {
    this.termSimilarity.getWNSemantics().getWN().closeDictionary();
  }
  
  private EntityMappingTable addSemanticInfo(EntityMappingTable res)
    throws Exception
  {
    String keyword = res.getKeyword();
    return addSemanticInfoClasses(keyword, res);
  }
  
  private EntityMappingTable addSemanticInfoClasses(String keyword, EntityMappingTable res)
    throws Exception
  {
    EntityMappingTable eliminatedMappings = new EntityMappingTable(res.getKeyword(), res.getSearchDuration());
    for (Iterator i$ = res.getOntologyIDMappings().iterator(); i$.hasNext();)
    {
      key = (String)i$.next();
      ArrayList<SearchSemanticResult> SSRList = res.getOntologyMappings(key);
      
      ArrayList<SearchSemanticResult> toErase = new ArrayList();
      for (SearchSemanticResult SSR : SSRList) {
        if (SSR.getEntity().getType().equalsIgnoreCase("class"))
        {
          MappingSession.getLog_poweraqua().log(Level.INFO, "Adding semantic info for " + SSR.getEntity().getURI());
          if (!SSR.isSynsetIndexed())
          {
            WNSynsetSetBean TaxonomySynsetSetBean = getClassTaxonomySynsets(SSR.getEntity(), SSR.getDirectSuperclasses());
            SSR.setTaxonomySimilaritySynsets(TaxonomySynsetSetBean);
          }
          if (this.termSimilarity.isIsWNSource())
          {
            WNSynsetSetBean MatchSynsetSetBean = getClassMatchSynsets(keyword, SSR.getEntity());
            boolean isWN_keyword = this.termSimilarity.isIsWNSource();
            boolean isWN_SSR = this.termSimilarity.isIsWNTarget();
            SSR.setMatchSimilaritySynsets(MatchSynsetSetBean);
            
            SSR.setValidSynsets(isWN_keyword, isWN_SSR);
            if ((SSR.getValidSynset().isEmpty()) && (this.termSimilarity.isIsWNTarget()))
            {
              eliminatedMappings.addMapping(SSR);
              System.out.println("Eliminating mapping " + SSR.getEntity().getURI() + " in ontology " + key + " with incorrect syntactic match synset ");
              toErase.add(SSR);
            }
          }
        }
      }
      for (SearchSemanticResult erase : toErase) {
        res.removeMapping(key, erase);
      }
    }
    String key;
    return eliminatedMappings;
  }
  
  public void addSemanticInfoInstance(SearchSemanticResult SSR)
  {
    try
    {
      if ((SSR.getEntity().getType().equals("instance")) && 
        (SSR.isEmptyTaxonomySimilaritySynsets()))
      {
        if (!SSR.isSynsetIndexed())
        {
          System.out.println("get synsets for the instance " + SSR.getEntity().getURI());
          WNSynsetSetBean TaxonomySynsetSetBean = getInstanceTaxonomySynsets(SSR.getDirectClasses(), SSR.getDirectSuperclasses());
          SSR.setTaxonomySimilaritySynsets(TaxonomySynsetSetBean);
          SSR.setSynsetIndexed(true);
        }
        SSR.setValidSynset(SSR.getTaxonomySimilaritySynsets().getSynsetList());
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public ArrayList<SearchSemanticResult> addSemanticInfoClass(SearchSemanticResult SSR)
  {
    String originalKeyword = SSR.getEmt_keyword();
    
    ArrayList<SearchSemanticResult> invalidMappings = new ArrayList();
    try
    {
      if ((SSR.getEntity().getType().equalsIgnoreCase("class")) && 
        (SSR.isEmptyMatchSimilaritySynsets()) && (SSR.isEmptyTaxonomySimilaritySynsets()))
      {
        MappingSession.getLog_poweraqua().log(Level.INFO, "Adding semantic info for " + SSR.getEntity().getURI());
        if (!SSR.isSynsetIndexed())
        {
          WNSynsetSetBean TaxonomySynsetSetBean = getClassTaxonomySynsets(SSR.getEntity(), SSR.getDirectSuperclasses());
          SSR.setTaxonomySimilaritySynsets(TaxonomySynsetSetBean);
        }
        if (this.termSimilarity.isIsWNSource())
        {
          WNSynsetSetBean MatchSynsetSetBean = getClassMatchSynsets(originalKeyword, SSR.getEntity());
          boolean isWN_keyword = this.termSimilarity.isIsWNSource();
          boolean isWN_SSR = this.termSimilarity.isIsWNTarget();
          SSR.setMatchSimilaritySynsets(MatchSynsetSetBean);
          
          SSR.setValidSynsets(isWN_keyword, isWN_SSR);
          
          SSR.setSynsetIndexed(true);
          if ((SSR.getValidSynset().isEmpty()) && (this.termSimilarity.isIsWNTarget()))
          {
            System.out.println("mapping " + SSR.getEntity().getURI() + " has an incorrect match synset ");
            invalidMappings.add(SSR);
          }
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return invalidMappings;
  }
  
  public boolean isEquivalentSynsets(SearchSemanticResult SSR1, SearchSemanticResult SSR2, double sim_thresh)
  {
    if (SSR1.equals(SSR2)) {
      return true;
    }
    if (SSR1.isEmptyValidSynsets()) {
      return true;
    }
    if (SSR2.isEmptyValidSynsets()) {
      return true;
    }
    ArrayList<Synset> valid1 = SSR1.getValidSynset();
    ArrayList<Synset> valid2 = SSR2.getValidSynset();
    for (Synset syn : valid2) {
      if (valid1.contains(syn)) {
        return true;
      }
    }
    for (Iterator i$ = valid1.iterator(); i$.hasNext();)
    {
      syn1 = (Synset)i$.next();
      for (Synset syn2 : valid2) {
        try
        {
          WNSynsetSimilarity similarSynsets = WordNetSemantics.getWNMappingSynsets(syn1, syn2);
          if (!similarSynsets.getBestRelationships().isEmpty())
          {
            double similarity = similarSynsets.getBestSimilarity().getSimilarity();
            if (similarity > sim_thresh) {
              return true;
            }
            System.out.println("The synsets with not enough similarity " + SSR1.getEntity().getURI() + " and " + SSR2.getEntity().getURI() + " sim : " + similarity);
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }
    }
    Synset syn1;
    System.out.println("The synsets are not equivalent for " + SSR1.getEntity().getURI() + " and " + SSR2.getEntity().getURI());
    
    return false;
  }
  
  public boolean addSemanticInfo(String queryTerm, SearchSemanticResult SSR)
  {
    try
    {
      if (SSR.getEntity().getType().equalsIgnoreCase("class"))
      {
        if (!SSR.isSynsetIndexed())
        {
          WNSynsetSetBean TaxonomySynsetSetBean = getClassTaxonomySynsets(SSR.getEntity(), SSR.getDirectSuperclasses());
          SSR.setTaxonomySimilaritySynsets(TaxonomySynsetSetBean);
        }
        if (this.termSimilarity.isIsWNSource())
        {
          WNSynsetSetBean MatchSynsetSetBean = getClassMatchSynsets(queryTerm, SSR.getEntity());
          boolean isWN_keyword = this.termSimilarity.isIsWNSource();
          boolean isWN_SSR = this.termSimilarity.isIsWNTarget();
          SSR.setMatchSimilaritySynsets(MatchSynsetSetBean);
          
          SSR.setValidSynsets(isWN_keyword, isWN_SSR);
          if ((SSR.getValidSynset().isEmpty()) && (this.termSimilarity.isIsWNTarget())) {
            return false;
          }
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return true;
  }
  
  public WNSynsetSetBean getClassTaxonomySynsets(RDFEntity entity, RDFEntityList superclasses)
    throws Exception
  {
    if (!informativeParent(entity.getURI())) {
      return new WNSynsetSetBean();
    }
    ArrayList<String> term_names = new ArrayList();
    term_names.add(entity.getLocalName());
    if (!entity.getLabel().equalsIgnoreCase(entity.getLocalName())) {
      term_names.add(entity.getLabel());
    }
    String labelSplit = LabelSplitter.splitOnCaps(entity.getLabel());
    if (!labelSplit.equals(entity.getLabel())) {
      term_names.add(labelSplit);
    }
    ArrayList<String> parents_names = new ArrayList();
    for (RDFEntity parent : superclasses.getAllRDFEntities()) {
      if (informativeParent(parent.getURI()))
      {
        String localname = parent.getLocalName();
        String label = parent.getLabel();
        if (!parents_names.contains(localname)) {
          parents_names.add(localname);
        }
        if (!parents_names.contains(label)) {
          parents_names.add(label);
        }
      }
    }
    if (parents_names.isEmpty())
    {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Parents for term " + entity.getLocalName() + " are not informative to obtain the taxonomical synsets " + parents_names.toString());
      return this.termSimilarity.getAllSynsets(term_names);
    }
    this.termSimilarity.CalculateTermSimilarity(term_names, parents_names);
    if (!this.termSimilarity.isIsWNSource()) {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Non WordNet entrance for " + term_names.toString());
    }
    if (!this.termSimilarity.isIsWNTarget()) {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Non WordNet entrance for " + parents_names.toString());
    }
    return this.termSimilarity.getSourceSynsetBean();
  }
  
  public WNSynsetSetBean getInstanceTaxonomySynsets(RDFEntityList directClasses, RDFEntityList superclasses)
    throws Exception
  {
    ArrayList<String> term_names = new ArrayList();
    for (RDFEntity directClass : directClasses.getAllRDFEntities())
    {
      if (!informativeParent(directClass.getURI())) {
        break;
      }
      term_names.add(directClass.getLocalName());
      if (!directClass.getLabel().equalsIgnoreCase(directClass.getLocalName())) {
        term_names.add(directClass.getLabel());
      }
      String labelSplit = LabelSplitter.splitOnCaps(directClass.getLabel());
      if (!labelSplit.equals(directClass.getLabel())) {
        term_names.add(labelSplit);
      }
    }
    ArrayList<String> parents_names = new ArrayList();
    for (RDFEntity parent : superclasses.getAllRDFEntities()) {
      if (informativeParent(parent.getURI()))
      {
        String localname = parent.getLocalName();
        String label = parent.getLabel();
        if (!parents_names.contains(localname)) {
          parents_names.add(localname);
        }
        if (!parents_names.contains(label)) {
          parents_names.add(label);
        }
      }
    }
    if (parents_names.isEmpty()) {
      return this.termSimilarity.getAllSynsets(term_names);
    }
    this.termSimilarity.CalculateTermSimilarity(term_names, parents_names);
    return this.termSimilarity.getSourceSynsetBean();
  }
  
  public static WNSynsetSetBean getClassTaxonomySynsetsForIndex(RDFEntity entity, RDFEntityList superclasses)
    throws Exception
  {
    if (!informativeParent(entity.getURI())) {
      return new WNSynsetSetBean();
    }
    try
    {
      WNTermSimilarity termSimilarity = new WNTermSimilarity();
      ArrayList<String> term_names = new ArrayList();
      term_names.add(entity.getLocalName());
      if (!entity.getLabel().equalsIgnoreCase(entity.getLocalName())) {
        term_names.add(entity.getLabel());
      }
      ArrayList<String> parents_names = new ArrayList();
      for (RDFEntity parent : superclasses.getAllRDFEntities()) {
        if (informativeParent(parent.getURI()))
        {
          String localname = parent.getLocalName();
          String label = parent.getLabel();
          if (!parents_names.contains(localname)) {
            parents_names.add(localname);
          }
          if (!parents_names.contains(label)) {
            parents_names.add(label);
          }
        }
      }
      if (parents_names.isEmpty()) {
        return termSimilarity.getAllSynsets(term_names);
      }
      termSimilarity.CalculateTermSimilarity(term_names, parents_names);
      if (!termSimilarity.isIsWNSource()) {
        System.out.println("Non WordNet entrance for source" + entity.getURI());
      }
      if (!termSimilarity.isIsWNTarget()) {
        System.out.println("Non WordNet entrance for target" + parents_names.toString());
      }
      termSimilarity.getWNSemantics().getWN().closeDictionary();
      return termSimilarity.getSourceSynsetBean();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new WNSynsetSetBean();
  }
  
  public WNSynsetSetBean getClassMatchSynsets(String keyword, RDFEntity ent)
    throws Exception
  {
    ArrayList<String> term_names = new ArrayList();
    term_names.add(ent.getLocalName());
    if (!ent.getLabel().equalsIgnoreCase(ent.getLocalName())) {
      term_names.add(ent.getLabel());
    }
    ArrayList<String> keywordList = new ArrayList();
    keywordList.add(keyword);
    
    this.termSimilarity.CalculateTermSimilarity(term_names, keywordList);
    if (!this.termSimilarity.isIsWNSource()) {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Non WordNet entrance for " + term_names.toString());
    }
    if (!this.termSimilarity.isIsWNTarget()) {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Non WordNet entrance for keyword" + keyword);
    }
    return this.termSimilarity.getSourceSynsetBean();
  }
  
  public WNSynsetSetBean getInstanceMatchSynsets(String keyword, RDFEntityList parents)
    throws Exception
  {
    ArrayList<String> term_names = new ArrayList();
    for (RDFEntity ent : parents.getAllRDFEntities())
    {
      term_names.add(ent.getLocalName());
      if (!ent.getLabel().equalsIgnoreCase(ent.getLocalName())) {
        term_names.add(ent.getLabel());
      }
    }
    ArrayList<String> keywordList = new ArrayList();
    keywordList.add(keyword);
    
    this.termSimilarity.CalculateTermSimilarity(term_names, keywordList);
    return this.termSimilarity.getSourceSynsetBean();
  }
  
  public ArrayList<WNSynsetSimilarity> getClassTaxonomySynsets(SearchSemanticResult SSR)
    throws Exception
  {
    RDFEntityList terminosParents = SSR.getDirectSuperclasses();
    
    ArrayList<String> term_names = new ArrayList();
    term_names.add(SSR.getEntity().getLocalName());
    if (!SSR.getEntity().getLabel().equalsIgnoreCase(SSR.getEntity().getLocalName())) {
      term_names.add(SSR.getEntity().getLabel());
    }
    ArrayList<String> parents_names = new ArrayList();
    for (RDFEntity parent : terminosParents.getAllRDFEntities()) {
      if (informativeParent(parent.getURI()))
      {
        String localname = parent.getLocalName();
        String label = parent.getLabel();
        if (!parents_names.contains(localname)) {
          parents_names.add(localname);
        }
        if (!parents_names.contains(label)) {
          parents_names.add(label);
        }
      }
    }
    if (parents_names.isEmpty())
    {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Parents for term " + SSR.getEntity().getLocalName() + " are not informative to obtain the taxonomical synsets " + parents_names.toString());
      return new ArrayList();
    }
    ArrayList<WNSynsetSimilarity> res = this.termSimilarity.calculateSynsetSimilarity(term_names, parents_names);
    if (!this.termSimilarity.isIsWNSource()) {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Non WordNet entrance for " + term_names.toString());
    }
    if (!this.termSimilarity.isIsWNTarget()) {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Non WordNet entrance for " + parents_names.toString());
    }
    return res;
  }
  
  private boolean informativeParent(SearchSemanticResult SSR)
  {
    ArrayList<String> non_informative_parents = new ArrayList();
    non_informative_parents.add("http://www.w3.org/2000/01/rdf-schema#Resource");
    non_informative_parents.add("http://www.w3.org/2002/07/owl#Thing");
    non_informative_parents.add("http://dbpedia.org/ontology/Resource");
    ArrayList<String> Uris = SSR.getDirectSuperclasses().getUris();
    for (String uri : Uris) {
      if (!non_informative_parents.contains(uri)) {
        return true;
      }
    }
    return false;
  }
  
  private static boolean informativeParent(String parent_uri)
  {
    ArrayList<String> non_informative_parents = new ArrayList();
    non_informative_parents.add("http://www.w3.org/2000/01/rdf-schema#Resource");
    non_informative_parents.add("http://www.w3.org/2002/07/owl#Thing");
    non_informative_parents.add("http://dbpedia.org/ontology/Resource");
    if (!non_informative_parents.contains(parent_uri)) {
      return true;
    }
    return false;
  }
  
  private String replaceLemma(String lemma, String termino1_name)
  {
    String new_termino_name = termino1_name.toLowerCase().replace(lemma, "").trim();
    if (new_termino_name.startsWith("s_")) {
      new_termino_name = new_termino_name.replaceFirst("s_", "");
    } else if (new_termino_name.startsWith("es_")) {
      new_termino_name = new_termino_name.replaceFirst("es_", "");
    } else if (new_termino_name.endsWith("_s")) {
      new_termino_name = new_termino_name.substring(0, new_termino_name.length() - 2);
    } else if (new_termino_name.endsWith("_es")) {
      new_termino_name = new_termino_name.substring(0, new_termino_name.length() - 3);
    }
    if (new_termino_name.startsWith("s-")) {
      new_termino_name = new_termino_name.replaceFirst("s-", "");
    } else if (new_termino_name.startsWith("es-")) {
      new_termino_name = new_termino_name.replaceFirst("es-", "");
    } else if (new_termino_name.endsWith("-s")) {
      new_termino_name = new_termino_name.substring(0, new_termino_name.length() - 2);
    } else if (new_termino_name.endsWith("-es")) {
      new_termino_name = new_termino_name.substring(0, new_termino_name.length() - 3);
    }
    if (new_termino_name.startsWith("s ")) {
      new_termino_name = new_termino_name.replaceFirst("s ", "");
    } else if (new_termino_name.startsWith("es ")) {
      new_termino_name = new_termino_name.replaceFirst("es ", "");
    } else if (new_termino_name.endsWith(" s")) {
      new_termino_name = new_termino_name.substring(0, new_termino_name.length() - 2);
    } else if (new_termino_name.endsWith(" es")) {
      new_termino_name = new_termino_name.substring(0, new_termino_name.length() - 3);
    }
    return new_termino_name.trim();
  }
  
  public static ArrayList<Synset> CrossOntologyValidSourceSynsets(ArrayList<SearchSemanticResult> mappings_source, ArrayList<SearchSemanticResult> mappings_target)
    throws Exception
  {
    ArrayList<Synset> valid = new ArrayList();
    for (int i = 0; i < mappings_source.size(); i++)
    {
      Iterator i$;
      Synset syn1;
      for (int j = 0; j < mappings_target.size(); j++) {
        for (i$ = ((SearchSemanticResult)mappings_source.get(i)).getValidSynsets().iterator(); i$.hasNext();)
        {
          syn1 = (Synset)i$.next();
          for (Synset syn2 : ((SearchSemanticResult)mappings_target.get(j)).getValidSynsets())
          {
            WNSynsetSimilarity similarSyns = new WNSynsetSimilarity();
            try
            {
              similarSyns = WordNetSemantics.getWNMappingSynsets(syn1, syn2);
              if (!similarSyns.getBestRelationships().isEmpty()) {
                for (Synset simsyn : similarSyns.getBestSourceSynsets()) {
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
    }
    return valid;
  }
  
  public static ArrayList<Synset> CrossOntologyValidTargetSynsets(ArrayList<SearchSemanticResult> mappings_source, ArrayList<SearchSemanticResult> mappings_target)
    throws Exception
  {
    ArrayList<Synset> valid = new ArrayList();
    for (int i = 0; i < mappings_source.size(); i++)
    {
      Iterator i$;
      Synset syn1;
      for (int j = 0; j < mappings_target.size(); j++) {
        for (i$ = ((SearchSemanticResult)mappings_source.get(i)).getValidSynsets().iterator(); i$.hasNext();)
        {
          syn1 = (Synset)i$.next();
          for (Synset syn2 : ((SearchSemanticResult)mappings_target.get(j)).getValidSynsets())
          {
            WNSynsetSimilarity similarSyns = new WNSynsetSimilarity();
            try
            {
              similarSyns = WordNetSemantics.getWNMappingSynsets(syn1, syn2);
              if (!similarSyns.getBestRelationships().isEmpty()) {
                for (Synset simsyn : similarSyns.getBestTargetSynsets()) {
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
    }
    return valid;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    try
    {
      int total = 0;
      int valid = 0;
      
      MappingSession mapSession = new MappingSession();
      
      String[] pair1 = { "Seasons", "Mammals", "Asia", "Game", "Malt", "Mammals", "Mammals", "Mammals", "Mammals", "Mammals", "Mammals", "Mammals", "Storage", "Leaf", "Liquid_Fuels", "Mass_transfer", "Managers", "Membranes", "Agents", "Anemone", "Fractures", "Dehydration", "Infrastructure", "Infrastructure", "Transport", "Education", "Colloids", "Size", "Feeds", "Flight", "Fruit", "Fruit", "Infrastructure", "Infrastructure", "Training", "Translocation", "Land_transfers", "Piping", "Coal", "Environment", "Simulation", "Water_purification", "Foods", "Cats", "Adsorption", "Computers", "Industry", "Industry", "Industry", "Internet", "Condensation", "Gases", "Production", "Desalting", "Americas", "East_Asia", "North_America", "England", "Americas", "Brazil", "Europe", "USA", "Irrigation", "Hydrogenation", "Environment", "Environment", "Mass_transfer", "Nucleic_acids", "Radar", "Cryogenics", "Harvesting", "Economics", "Separation", "Technology", "Technology", "Boilers", "Convection", "Liquid_fuels", "Ion_exchange", "Technology", "Technology", "Dew", "Osmotic_pressure", "Products", "Agents", "Infrastructure", "Education", "Fishes", "Meat", "Vegetation", "Evaporation", "Digestive_disorders" };
      
      String[] pair1_all_positive = { "Grasshoppers", "Agricultural_products", "Algae", "Animal_production", "Animals", "Animals", "Animals", "Animals", "Animals", "Animals", "Animals", "Animals", "Animals", "Animals", "Animals", "Aquaculture", "Marine_animals", "Aquatic_mammals", "Atmospheric_circulation", "Atmospheric_circulation", "Bacterial_diseases", "Bread", "Bread", "Breweries", "Buildings", "Buildings", "Butter", "Caesarean_section", "Camels", "Canned_foods", "Capital", "Financial_market", "Carbohydrates", "Carbohydrates", "Cardiovascular_disorders", "Cow_pea", "Cattle", "Caviar", "Cell_division", "Cheese", "Chemicals", "Chemistry", "Chemistry", "Chlamydia_psittaci", "Chromatography", "Chrysanthemum", "Citrus_fruits", "Clostridium_perfringens", "Conifers", "Cooking_oils", "Cow_milk", "Crayfish", "Cream", "Cyclones", "Dams", "Desserts", "Dredgers", "Drinking_water", "Drinking_water", "Drugs", "Drugs", "Drugs", "Echinoderms", "Ecosystems", "Ecosystems", "Ecosystems", "Enzymes", "Epilepsy", "Equipment", "Equipment", "Appliances", "Tools", "Equipment", "Appliances", "Equipment", "Equipment", "Erosion", "Exports", "Ferns", "Filtration", "Fires", "Fish", "Fish", "Fish", "Fish", "Fish", "Fish_products", "Fishes", "Fishes", "Fishes", "Flours", "Food_allergies", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Foods", "Forest_products", "Port_wine", "Freshwater_fishes", "Frozen_foods", "Fruit", "Fruit", "Fruit", "Fruit", "Fruits", "Fruits", "Fruits", "Fruits", "Fruits", "Fungal_diseases", "Fungi", "Gases", "Goose", "Grain", "Grasslands", "Grasslands", "Grinders", "Growth_rate", "Habitats", "Health_foods", "Imports", "Industry", "Infectious_diseases", "Infectious_diseases", "Infectious_diseases", "Infectious_diseases", "Infectious_diseases", "Infrastructure", "Infrastructure", "Inland_waterways", "Koalas", "Koalas", "Land_management", "Law", "Green_vegetables", "Green_vegetables", "Legumes", "Legumes", "Lentils", "Lima_beans", "Liquids", "Luminescence", "Management", "Business_management", "Markets", "Zucchini", "Marsupials", "Marsupials", "Mass_transfer", "Mass_transfer", "Meat", "Meat", "Meningitis", "Microorganisms", "Microorganisms", "Microorganisms", "Microorganisms", "Microorganisms", "Microorganisms", "Microorganisms", "Microorganisms", "Dairy_products", "Dairy_products", "Dairy_products", "Dairy_products", "Dairy_products", "Minerals", "Monosaccharides", "Nitrates", "Oases", "Oilseeds", "Onions", "Ornamental_fishes", "Ovens", "Pasta", "Pinto_beans", "Petroleum", "Piping", "Plant_production", "Plants", "Plants", "Plants", "Pollutants", "Pork", "Pork", "Poultry", "Poultry", "Poultry", "Power_tools", "Prepared_foods", "Prepared_foods", "Prepared_foods", "Prepared_foods", "Prepared_foods", "Manufactured_products", "Manufactured_products", "Production", "Production", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Products", "Proteins", "Protozoa", "Optimization_methods", "Radiation", "Red_wines", "Rivers", "Rodents", "Root_vegetables", "Ketchups", "Sausages", "Scientists", "Seeds", "Senses", "Sex", "Shellfish", "Shrubs", "Snack_foods", "Snack_foods", "Sociology", "Berry_fruits", "Berry_fruits", "Soups", "Spices", "Spices", "Mycotoxins", "Squashes", "Squashes", "Supermarkets", "Sweeteners", "Technology", "Toxins", "Tropical_fruits", "Vegetables", "Vegetables", "Vertebrates", "Vertebrates", "Viruses", "Water", "Wheat_flour", "Yersinia_pestis", "Allergies" };
      
      String[] pair2 = { "measurement", "canine", "Iran", "sports", "Pales", "marine_mammals", "Beagle", "carnivores", "orangutans", "terriers", "Doberman_Pinscher", "hominids", "turbines", "plants", "technology", "crystallization", "industry", "osmosis", "government_agencies", "fish", "strains", "drying", "buildings", "computers", "endocytosis", "physics", "equipment", "extrusion", "technology", "infrastructure", "desserts", "prunes", "radar", "electricity", "calculi", "reforestation", "drives", "elbows", "industry", "education", "models", "water", "sauces", "bonobos", "technology", "infrastructure", "oils", "waste_water_treatment", "quality_assurance", "industry", "technology", "equipment", "infrastructure", "environment", "Washington", "Asia", "New_York", "Europe", "North_America", "Brasilia", "United_Kingdom", "Washington", "agriculture", "technology", "cells", "waste_water", "reservoirs", "alternative_splicing", "infrastructure", "separation", "agriculture", "industry", "methods", "reservoirs", "frequency", "separation", "separation", "alkylation", "environment", "particle_size_distribution", "pollution", "distillation", "osmosis", "orchids", "chemists", "transportation", "research_projects", "lobsters", "shellfish", "nutrients", "dialysis", "hernia" };
      
      String[] pair2_all_positive = { "invertebrates", "vegetable_products", "plants", "fish_production", "organisms", "crustaceans", "Greyhound", "goose", "Golden_Retriever", "marine_fish", "poultry", "fish", "marine_fish", "baboons", "terriers", "fish_production", "jellyfish", "vertebrates", "dust_storms", "typhoons", "hepatitis", "sourdough_bread", "foods", "organizations", "infrastructure", "hospitals", "foods", "surgery", "mammals", "canned_meat", "call_options", "stock_exchange", "glycoproteins", "oligosaccharides", "heart_diseases", "vegetables", "mammals", "foods", "mitosis", "American_cheese", "materials", "physical_chemistry", "education", "microorganisms", "technology", "organisms", "limes", "organisms", "junipers", "olive_oil", "beverages", "animals", "foods", "hurricanes", "infrastructure", "shellfish", "vehicles", "tap_water", "foods", "enzymes", "nucleic_acids", "clones", "animals", "prairies", "tundra", "oasis", "drugs", "neurological_disorders", "computers", "rotary_dryers", "dishwashers", "cooking_equipment", "sensors", "microwave_ovens", "compressors", "crushers", "soil_erosion", "imports", "plants", "separation", "combustion", "desserts", "mollusks", "triggerfishes", "lobsters", "zebrafish", "caviar", "triggerfishes", "animals", "freshwater_fish", "wheat_flour", "wheat_allergy", "sour_cream", "whipped_cream", "tomatoes", "Swiss_cheese", "orange_juice", "corned_beef", "tortilla_chips", "dairy_products", "frozen_foods", "doughnuts", "potatoes", "lowfat_milk", "canned_meat", "onions", "canned_vegetables", "poultry", "kale", "lumber", "alcoholic_beverages", "trout", "foods", "pasta", "shellfish", "mangoes", "olives", "tropical_fruits", "kiwi_fruit", "tomatoes", "melons", "blueberries", "ringworm", "yeasts", "solids", "livestock", "barley", "prairies", "biomes", "equipment", "measurement", "rainforests", "foods", "exports", "fertilizer_industry", "toxoplasmosis", "parasitic_diseases", "bubonic_plague", "malaria", "scabies", "schools", "harbors", "irrigation_canals", "animals", "persons", "land_use", "environmental_law", "spinach", "lima_beans", "green_beans", "soybeans", "vegetables", "foods", "materials", "fluorescence", "water_management", "risk_management", "stock_exchange", "vegetables", "persons", "animals", "leaching", "filtration", "pasta", "turkey_meat", "neurological_disorders", "bacteria", "Bacillus_cereus", "Hepatitis_C_virus", "Burkholderia_pseudomallei", "Lassa_virus", "Hepatitis_B_virus", "Clostridium_perfringens", "Sabia_virus", "foods", "Brie_cheese", "cottage_cheese", "sour_cream", "Cheddar_cheese", "asbestos", "carbohydrates", "nitrogen", "biomes", "seeds", "vegetables", "vertebrates", "convection_ovens", "seafoods", "vegetables", "oils", "equipment", "harvesting", "vines", "forage_crops", "algae", "asbestos", "meat", "bacon", "turkey_meat", "foods", "ducks", "electrical_generators", "tortillas", "cheese", "chocolate_milk", "American_cheese", "dairy_products", "pharmaceutical_products", "lumber", "apiculture", "breeding", "cooking_equipment", "nuts", "seafoods", "doughnuts", "garments", "fruit_products", "bananas", "blowers", "strawberries", "sprayers", "vegetables", "whipped_cream", "wood_products", "cloves", "vinegars", "carrots", "cheese", "pork", "corn_flour", "turnips", "glycoproteins", "microorganisms", "technology", "heat", "alcoholic_beverages", "waterways", "animals", "cassava", "foods", "Polish_sausage", "chemists", "sunflower_seed", "vision", "gender", "crayfish", "Viburnum", "tortilla_chips", "chocolate_milk", "vital_statistics", "blackberries", "foods", "foods", "mace", "nutmeg", "toxins", "vegetables", "foods", "buildings", "sugar_substitutes", "cracking", "tetrodotoxin", "pineapples", "leafy_green_vegetables", "beets", "marsupials", "reindeer", "Guanarito_virus", "potable_water", "flour", "organisms", "food_allergies" };
      
      String pair_res = new String();
      
      QueryAnalyzer qa = new QueryAnalyzer();
      MappingBean mappingBean2;
      for (int pair_i = 0; pair_i < pair1.length; pair_i++)
      {
        total += 1;
        pair_res = pair_res.concat("\n*************************** \n <PAIR " + pair1[pair_i] + " -- " + pair2[pair_i] + "> \n");
        
        String mapping_pair = new String(pair1[pair_i] + " OR " + pair2[pair_i]);
        System.out.println("Analyzing the mapping pair " + mapping_pair + "*******************************");
        System.out.println("-----------------------------------------------------------------------------");
        QueryElementsBean queryElements = qa.analyzeQuery(mapping_pair);
        boolean isqueryTerm = false;
        ArrayList<MappingBean> mappingBeans = MappingBean.getMappingBeans(queryElements, mapSession.getRealpath(), isqueryTerm);
        
        SyntacticComponent map = new SyntacticComponent(mapSession, mappingBeans);
        map.match();
        
        ArrayList<String> sortedOntologies = map.sortByCoverage();
        if (sortedOntologies == null)
        {
          System.out.println("No results"); break;
        }
        SemanticComponent mapSemantic = new SemanticComponent(mapSession.getRealpath(), mappingBeans);
        
        MappingBean mappingBean1 = (MappingBean)mappingBeans.get(0);
        String key1 = ((MappingBean)mappingBeans.get(0)).getKeyword();
        if (mappingBean1.getEntityMappingTable().getOntologyIDMappings().isEmpty())
        {
          System.out.println("No mappings for " + key1);
          pair_res = pair_res.concat("No mappings for " + key1 + "\n");
          break;
        }
        String agrovoc = "http://pckm143.open.ac.uk:8080/sesame/agrovoc_test";
        EntityMappingTable mappingAgrovocTable = new EntityMappingTable(mappingBean1.getKeyword());
        ArrayList<SearchSemanticResult> mapppings_Agrovoc = mappingBean1.getEntityMappingTable().getOntologyMappings(agrovoc);
        mappingAgrovocTable.addMappingList(mapppings_Agrovoc);
        mappingBean1.setEntityMappingTable(mappingAgrovocTable);
        
        mappingBean2 = (MappingBean)mappingBeans.get(1);
        String key2 = ((MappingBean)mappingBeans.get(1)).getKeyword();
        if (mappingBean1.getEntityMappingTable().getOntologyIDMappings().isEmpty())
        {
          System.out.println("No mappings for " + key2);
          pair_res = pair_res.concat("No mappings for " + key2 + "\n");
          break;
        }
        String nalt = "http://pckm143.open.ac.uk:8080/sesame/nalt_2006";
        EntityMappingTable mappingNaltTable = new EntityMappingTable(mappingBean2.getKeyword());
        ArrayList<SearchSemanticResult> mapppings_Nalt = mappingBean2.getEntityMappingTable().getOntologyMappings(nalt);
        mappingNaltTable.addMappingList(mapppings_Nalt);
        mappingBean2.setEntityMappingTable(mappingNaltTable);
        
        EntityMappingTable eliminatedMappings1 = mapSemantic.addSemanticInfoClasses(key2, mappingAgrovocTable);
        mappingBean1.getRecyclingBean().addSynsetRecyclingMapping(eliminatedMappings1);
        
        EntityMappingTable eliminatedMappings2 = mapSemantic.addSemanticInfoClasses(key1, mappingNaltTable);
        mappingBean2.getRecyclingBean().addSynsetRecyclingMapping(eliminatedMappings2);
        
        ArrayList<Synset> validSource = CrossOntologyValidSourceSynsets(mapppings_Nalt, mapppings_Agrovoc);
        ArrayList<Synset> validTarget = CrossOntologyValidTargetSynsets(mapppings_Nalt, mapppings_Agrovoc);
        if ((validSource.isEmpty()) && (!((SearchSemanticResult)mapppings_Nalt.get(0)).isEmptyMatchSimilaritySynsets()))
        {
          pair_res = pair_res.concat("valid is empty!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
          
          pair_res = pair_res.concat("eliminated mappings:");
          for (String key : mappingBean1.getEntityMappingTable().getOntologyIDMappings()) {
            pair_res = pair_res.concat(mappingBean1.printString(key));
          }
          pair_res = pair_res.concat(mappingBean1.getRecyclingBean().getSynsetRecyclingBean().toString());
          
          pair_res = pair_res.concat("eliminated mappings:");
          for (String key : mappingBean2.getEntityMappingTable().getOntologyIDMappings()) {
            pair_res = pair_res.concat(mappingBean2.printString(key));
          }
          pair_res = pair_res.concat(mappingBean2.getRecyclingBean().getSynsetRecyclingBean().toString());
        }
        else
        {
          valid += 1;
          
          pair_res = pair_res.concat("IS VALID!!!!!!!!!!!!!!!!!!: \n ");
          
          pair_res = pair_res.concat("Printing the valid mappings for" + key1 + " in agrovoc with respect to " + key2 + " in nalt **** \n");
          for (String key : mappingBean1.getEntityMappingTable().getOntologyIDMappings()) {
            pair_res = pair_res.concat(mappingBean1.printString(key));
          }
          pair_res = pair_res.concat("The best valid source synsets are: \n");
          pair_res = pair_res.concat(validSource.toString() + "\n");
          
          pair_res = pair_res.concat("Printing the valid mappings for" + key2 + " in nalt with respect to " + key1 + " in agrovoc **** \n ");
          for (String key : mappingBean2.getEntityMappingTable().getOntologyIDMappings()) {
            pair_res = pair_res.concat(mappingBean2.printString(key));
          }
        }
      }
      System.out.println("for a total of " + total + " there are this valid " + valid);
      
      System.out.println("******************************************************************");
      System.out.println(pair_res);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}

