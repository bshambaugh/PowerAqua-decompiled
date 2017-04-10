package poweraqua.powermap.elementPhase;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.core.utils.LabelSplitter;
import poweraqua.core.utils.StringUtils;
import poweraqua.indexingService.manager.IndexManager;
import poweraqua.indexingService.manager.MultiIndexManager;
import poweraqua.indexingService.manager.virtuoso.IndexManagerVirtuoso;
import poweraqua.indexingService.manager.watson.IndexManagerWatson;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.powermap.stringMetrics.stringMetricsComparator;
import poweraqua.serviceConfig.MultiOntologyManager;

public class EntityMappingTable
{
  private String keyword;
  private String WNLemma;
  private ArrayList<String> WNDerived;
  private long searchDuration;
  private Hashtable<String, ArrayList<SearchSemanticResult>> mappingTable;
  private boolean mergedKeyword;
  private boolean is_ISACompound;
  private ArrayList<String> restrictedKeywords;
  
  public EntityMappingTable(String keyword, long searchDuration)
  {
    this.keyword = keyword;
    this.searchDuration = searchDuration;
    this.mappingTable = new Hashtable();
    this.mergedKeyword = false;
    this.WNLemma = "";
    this.is_ISACompound = false;
    this.restrictedKeywords = new ArrayList();
    this.WNDerived = new ArrayList();
  }
  
  public EntityMappingTable(String keyword)
  {
    this.keyword = keyword;
    this.searchDuration = 0L;
    this.mappingTable = new Hashtable();
    this.mergedKeyword = false;
    this.WNLemma = "";
    this.is_ISACompound = false;
    this.restrictedKeywords = new ArrayList();
    this.WNDerived = new ArrayList();
  }
  
  public void mergeKeyword(String keyword)
  {
    this.keyword = this.keyword.concat(" " + keyword);
    setMergedKeyword(true);
  }
  
  private boolean ContainsSSR(ArrayList<SearchSemanticResult> SSRs, SearchSemanticResult ent)
  {
    for (SearchSemanticResult SSR : SSRs) {
      if ((SSR.getEntity().getURI().equals(ent.getEntity().getURI())) && (SSR.getEntity().getType().equals(ent.getEntity().getType()))) {
        return true;
      }
    }
    return false;
  }
  
  public void addMapping(SearchSemanticResult searchSemanticResult)
  {
    if (searchSemanticResult.getIdPlugin() == null)
    {
      System.out.println("ERROR ontology name is null on " + searchSemanticResult.getEntity().getURI() + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    else if (this.mappingTable.containsKey(searchSemanticResult.getIdPlugin()))
    {
      ArrayList<SearchSemanticResult> RRSs = (ArrayList)this.mappingTable.get(searchSemanticResult.getIdPlugin());
      if (!ContainsSSR(RRSs, searchSemanticResult))
      {
        if (!this.mergedKeyword) {
          searchSemanticResult.setEmt_keyword(this.keyword);
        }
        ((ArrayList)this.mappingTable.get(searchSemanticResult.getIdPlugin())).add(searchSemanticResult);
      }
    }
    else
    {
      ArrayList<SearchSemanticResult> results = new ArrayList();
      if (!this.mergedKeyword) {
        searchSemanticResult.setEmt_keyword(this.keyword);
      }
      results.add(searchSemanticResult);
      this.mappingTable.put(searchSemanticResult.getIdPlugin(), results);
    }
  }
  
  public void addMappingList(ArrayList<SearchSemanticResult> searchSemanticResults)
  {
    for (SearchSemanticResult result : searchSemanticResults) {
      addMapping(result);
    }
  }
  
  public ArrayList<SearchSemanticResult> getOntologyMappings(String idPlugin)
  {
    return (ArrayList)this.mappingTable.get(idPlugin);
  }
  
  public String getKeyword()
  {
    return this.keyword;
  }
  
  public long getSearchDuration()
  {
    return this.searchDuration;
  }
  
  public void addOntologyInfo(MultiOntologyManager multiOntologyManager)
    throws Exception
  {
    Iterator<String> iter = this.mappingTable.keySet().iterator();
    OntologyPlugin osPlugin;
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(key);
      osPlugin = multiOntologyManager.getPlugin(key);
      for (SearchSemanticResult SSR : SSRList) {
        SSR.setOntologyPlugin(osPlugin);
      }
    }
  }
  
  public void addMetadataInfo(MultiIndexManager multiIndexManager)
    throws Exception
  {
    IndexManagerWatson indexManagerWatson = null;
    if (anyWatsonOntology()) {
      indexManagerWatson = multiIndexManager.getIndexManagerWatson();
    }
    IndexManagerVirtuoso indexManagerVirtuoso = null;
    if (anyVirtuosoOntology()) {
      indexManagerVirtuoso = multiIndexManager.getIndexManagerVirtuoso();
    }
    long time = System.currentTimeMillis();
    
    System.out.println("Creating the metadata access  points ");
    for (String key : this.mappingTable.keySet()) {
      if (isWatsonOntology(key))
      {
        ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(key);
        for (SearchSemanticResult SSR : SSRList) {
          SSR.addMetadataInfo(indexManagerWatson);
        }
      }
      else if (isVirtuosoOntology(key))
      {
        ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(key);
        for (SearchSemanticResult SSR : SSRList) {
          SSR.addMetadataInfo(indexManagerVirtuoso);
        }
      }
      else
      {
        ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(key);
        
        boolean have_metadata = haveMetadata(key);
        if (have_metadata)
        {
          indexM = multiIndexManager.findOntologIndex(key);
          if (indexM == null) {
            System.out.println("ONTOLOGY ERR: Impossible to add metadata " + key);
          } else {
            for (SearchSemanticResult SSR : SSRList) {
              SSR.addMetadataInfo(indexM);
            }
          }
        }
      }
    }
    IndexManager indexM;
    Long Time = Long.valueOf(System.currentTimeMillis() - time);
    System.out.println("Total TIME TO create metadata access points: " + Time + " ms");
  }
  
  public void eraseNonValidOntologyEntries(ArrayList<String> validIds)
  {
    Enumeration<String> keys = this.mappingTable.keys();
    while (keys.hasMoreElements())
    {
      String key = (String)keys.nextElement();
      if ((!isWatsonOntology(key)) && (!isVirtuosoOntology(key)) && 
        (!validIds.contains(key)))
      {
        this.mappingTable.remove(key);
        System.out.println("Removing index entry " + key + " for keyword " + this.keyword + " because the ontology it is not accesible by the current MultiOntologyManager");
      }
    }
  }
  
  public boolean isWatsonOntology(String ontology)
  {
    ArrayList<SearchSemanticResult> ssr = (ArrayList)this.mappingTable.get(ontology);
    if ((ssr.size() > 0) && 
      (((SearchSemanticResult)ssr.get(0)).isWatson())) {
      return true;
    }
    return false;
  }
  
  public boolean anyWatsonOntology()
  {
    for (String key : this.mappingTable.keySet())
    {
      ArrayList<SearchSemanticResult> ssr = (ArrayList)this.mappingTable.get(key);
      if ((ssr.size() > 0) && 
        (((SearchSemanticResult)ssr.get(0)).isWatson())) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isVirtuosoOntology(String ontology)
  {
    ArrayList<SearchSemanticResult> ssr = (ArrayList)this.mappingTable.get(ontology);
    if ((ssr.size() > 0) && 
      (((SearchSemanticResult)ssr.get(0)).isVirtuoso())) {
      return true;
    }
    return false;
  }
  
  public boolean anyVirtuosoOntology()
  {
    for (String key : this.mappingTable.keySet())
    {
      ArrayList<SearchSemanticResult> ssr = (ArrayList)this.mappingTable.get(key);
      if ((ssr.size() > 0) && 
        (((SearchSemanticResult)ssr.get(0)).isVirtuoso())) {
        return true;
      }
    }
    return false;
  }
  
  public void removeOntology(String ontology_id)
  {
    if (this.mappingTable.containsKey(ontology_id)) {
      this.mappingTable.remove(ontology_id);
    }
  }
  
  public ArrayList<String> getOntologyIDMappings()
  {
    return new ArrayList(this.mappingTable.keySet());
  }
  
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    Iterator<String> iter = this.mappingTable.keySet().iterator();
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      buffer.append("Ontology " + key + "\n");
      ArrayList<SearchSemanticResult> results = (ArrayList)this.mappingTable.get(key);
      for (SearchSemanticResult result : results) {
        buffer.append(result.toString());
      }
    }
    return buffer.toString();
  }
  
  public String toString2()
  {
    StringBuffer buffer = new StringBuffer();
    Iterator<String> iter = this.mappingTable.keySet().iterator();
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      buffer.append("Ontology " + key + "\n");
      ArrayList<SearchSemanticResult> results = (ArrayList)this.mappingTable.get(key);
      for (SearchSemanticResult result : results)
      {
        String label = "";
        for (String l : result.getEntity().getLabel().split("\n")) {
          label = label + l;
        }
        buffer.append(result.getEntity().getURI() + "\t" + label + "\t" + result.getEntity().getType() + "\n");
      }
    }
    return buffer.toString();
  }
  
  public boolean isEmpty()
  {
    if (this.mappingTable.isEmpty()) {
      return true;
    }
    if (this.mappingTable.keySet().isEmpty()) {
      return true;
    }
    return false;
  }
  
  public void merge(EntityMappingTable e2)
  {
    Iterator<String> iter = e2.mappingTable.keySet().iterator();
    this.searchDuration += e2.searchDuration;
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      ArrayList<SearchSemanticResult> results = (ArrayList)e2.mappingTable.get(key);
      addMappingList(results);
    }
  }
  
  public EntityMappingTable groupLiteralsToEntities(boolean filter_unconnectedProperties)
  {
    EntityMappingTable filtered = new EntityMappingTable(this.keyword, this.searchDuration);
    EntityMappingTable eliminated = new EntityMappingTable(this.keyword, this.searchDuration);
    
    Iterator<String> iter = this.mappingTable.keySet().iterator();
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(key);
      ArrayList<SearchSemanticResult> removethem = new ArrayList();
      for (SearchSemanticResult SSR : SSRList)
      {
        boolean add = true;
        String uri_instance;
        if (SSR.getEntity().isLiteral())
        {
          uri_instance = SSR.getEntity().getURI();
          
          add = true;
          for (SearchSemanticResult SSRAux : SSRList) {
            if ((SSRAux.getEntity().isInstance()) && (SSRAux.getEntity().getURI().equals(uri_instance))) {
              add = false;
            }
          }
        }
        String uri_instance;
        if (SSR.getEntity().isInstance())
        {
          uri_instance = SSR.getEntity().getURI();
          add = true;
          for (SearchSemanticResult SSRAux : SSRList) {
            if ((SSRAux.getEntity().isClass()) && (SSRAux.getEntity().getURI().equals(uri_instance))) {
              add = false;
            }
          }
        }
        if ((filter_unconnectedProperties) && (SSR.getEntity().isProperty()) && 
          (!SSR.getOsPlugin().existTripleForProperty(SSR.getEntity().getURI())))
        {
          add = false;
          System.out.println("Filtering property not linked " + SSR.getEntity().getURI());
        }
        if (add) {
          filtered.addMapping(SSR);
        } else {
          removethem.add(SSR);
        }
      }
      for (SearchSemanticResult removeit : removethem)
      {
        MappingSession.getLog_poweraqua().log(Level.INFO, ">>>Group per ontology taxonomy is filtering the  SSR mapping " + removeit.getIdPlugin() + " , " + removeit.getEntity().getURI() + " , " + removeit.getEntity().getType() + " , " + removeit.getSemanticRelation());
        
        eliminated.addMapping(removeit);
      }
    }
    this.mappingTable = filtered.mappingTable;
    return eliminated;
  }
  
  public EntityMappingTable filterUnconnectedEntities()
  {
    EntityMappingTable filtered = new EntityMappingTable(this.keyword, this.searchDuration);
    EntityMappingTable eliminated = new EntityMappingTable(this.keyword, this.searchDuration);
    
    Iterator<String> iter = this.mappingTable.keySet().iterator();
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(key);
      ArrayList<SearchSemanticResult> removethem = new ArrayList();
      
      int count = 0;
      for (SearchSemanticResult SSR : SSRList)
      {
        boolean add = true;
        if (SSR.getEntity().isProperty())
        {
          count++;
          if (!SSR.getOsPlugin().existTripleForProperty(SSR.getEntity().getURI()))
          {
            add = false;
            System.out.println("Filtering property not linked " + SSR.getEntity().getURI());
          }
        }
        if (add) {
          filtered.addMapping(SSR);
        } else {
          removethem.add(SSR);
        }
      }
      if (!removethem.isEmpty()) {
        MappingSession.getLog_poweraqua().log(Level.INFO, removethem.size() + " unconencted properties removed for " + key);
      }
      for (SearchSemanticResult removeit : removethem) {
        eliminated.addMapping(removeit);
      }
    }
    this.mappingTable = filtered.mappingTable;
    return eliminated;
  }
  
  public EntityMappingTable groupPerOntologyTaxonomy()
  {
    EntityMappingTable filtered = new EntityMappingTable(this.keyword, this.searchDuration);
    EntityMappingTable eliminated = new EntityMappingTable(this.keyword, this.searchDuration);
    
    Iterator<String> iter = this.mappingTable.keySet().iterator();
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(key);
      ArrayList<SearchSemanticResult> removethem = new ArrayList();
      for (SearchSemanticResult SSR : SSRList)
      {
        boolean add = true;
        if (SSR.getEntity().isClass())
        {
          RDFEntityList superclasses = SSR.getSuperclasses();
          superclasses.removeRDFEntity(SSR.getEntity());
          add = true;
          for (RDFEntity superclass : superclasses.getAllRDFEntities())
          {
            for (SearchSemanticResult SSRAux : SSRList) {
              if (removethem.contains(SSR)) {
                add = false;
              } else if (SSRAux.getEntity().getURI().equals(superclass.getURI())) {
                if ((SSR.getSemanticRelation().equals("equivalentMatching")) && (SSRAux.getSemanticRelation().equals("equivalentMatching"))) {
                  add = false;
                } else if ((!SSR.getSemanticRelation().equals("equivalentMatching")) && (SSRAux.getSemanticRelation().equals("equivalentMatching"))) {
                  add = false;
                } else if ((!SSR.getSemanticRelation().equals("equivalentMatching")) && (SSRAux.getSemanticRelation().equals("synonym"))) {
                  add = false;
                } else if ((!SSR.getSemanticRelation().equals("synonym")) && (!SSR.getSemanticRelation().equals("equivalentMatching")) && (!SSRAux.getSemanticRelation().equals("equivalentMatching"))) {
                  add = false;
                } else if (SSRAux.getSemanticRelation().equals("hypernym")) {
                  removethem.add(SSRAux);
                }
              }
            }
            if (!add) {}
          }
        }
        else
        {
          ArrayList<String> parents;
          if (SSR.getEntity().isInstance())
          {
            parents = SSR.getSuperclasses().getUris();
            for (SearchSemanticResult SSRAux : SSRList) {
              if ((SSRAux.getEntity().isClass()) && (parents.contains(SSRAux.getEntity().getURI())))
              {
                add = false;
              }
              else if (SSR.getClassOfEntity().contains("http://www.w3.org/2002/07/owl#Ontology"))
              {
                SSR.getEntity().setType("instance_ontology");
                System.out.println("INFO ** instance " + SSR.getEntity().getURI() + " is of type ONTOLOGY");
              }
            }
          }
          else if (SSR.getEntity().isLiteral())
          {
            try
            {
              if (SSR.getOsPlugin().isNameOfInstance(SSR.getEntity().getURI(), SSR.getEntity().getLabel()))
              {
                System.out.println("the literal is the alternative name of the instance " + SSR.getEntity().getURI());
                SSR.getEntity().setType("instance");
              }
            }
            catch (Exception e)
            {
              e.printStackTrace();
            }
          }
        }
        if (add) {
          filtered.addMapping(SSR);
        } else {
          removethem.add(SSR);
        }
      }
      for (SearchSemanticResult removeit : removethem)
      {
        MappingSession.getLog_poweraqua().log(Level.INFO, ">>>Group per ontology taxonomy is filtering the  SSR mapping " + removeit.getIdPlugin() + " , " + removeit.getEntity().getURI() + " , " + removeit.getEntity().getLabel() + " , " + removeit.getSemanticRelation());
        
        eliminated.addMapping(removeit);
      }
    }
    this.mappingTable = filtered.mappingTable;
    return eliminated;
  }
  
  public void removeMapping(String ontology, SearchSemanticResult SSR)
  {
    ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(ontology);
    SSRList.remove(SSR);
    if (SSRList.isEmpty()) {
      this.mappingTable.remove(ontology);
    } else {
      this.mappingTable.put(ontology, SSRList);
    }
  }
  
  public void filterExactStringsByOntology(String lexicalRelatedWord)
  {
    filterExactStringsByOntology(lexicalRelatedWord, false);
  }
  
  public void filterExactStringsByOntology(String lexicalRelatedWord, boolean WNet_onKB)
  {
    EntityMappingTable filtered = new EntityMappingTable(this.keyword, this.searchDuration);
    String lexicalword = lexicalRelatedWord.replaceAll("\"", "").trim();
    
    lexicalword = lexicalword.replaceAll("-", " ");
    lexicalword = lexicalword.replaceAll("_", " ");
    Iterator<String> iter = this.mappingTable.keySet().iterator();
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(key);
      if (SSRList.size() > 0)
      {
        boolean any_exact_mapping = false;
        ArrayList<SearchSemanticResult> filtered_SSRs = new ArrayList();
        ArrayList<SearchSemanticResult> exact_SSRs = new ArrayList();
        for (SearchSemanticResult SSR : SSRList)
        {
          String localname_raw = SSR.getEntity().getLocalName();
          String label_raw = SSR.getEntity().getLabel();
          String localname = localname_raw.replaceAll("-", " ");
          localname = localname.replaceAll("_", " ");
          String label = label_raw.replaceAll("-", " ");
          label = label.replaceAll("_", " ");
          if ((StringUtils.isSingularPluralExactMapping(lexicalword, label)) || (StringUtils.isSingularPluralExactMapping(lexicalword, localname)))
          {
            any_exact_mapping = true;
            SSR.setExactLexicalWord(true);
            exact_SSRs.add(SSR);
          }
          else
          {
            filtered_SSRs.add(SSR);
          }
        }
        if (any_exact_mapping)
        {
          if (filtered_SSRs.size() > 0) {
            System.out.println("Exact String for the lexical word " + this.keyword + " eliminating " + filtered_SSRs.size() + " in " + key);
          }
          filtered.addMappingList(exact_SSRs);
        }
        else if (WNet_onKB)
        {
          ArrayList<String> non_exact_KB_synonyms = new ArrayList();
          int count = 0;
          for (SearchSemanticResult SSR : filtered_SSRs) {
            if ((!SSR.getEntity().isInstance()) && (!SSR.getEntity().isLiteral()))
            {
              filtered.addMapping(SSR);
            }
            else
            {
              count++;
              non_exact_KB_synonyms.add(SSR.getEntity().getURI().concat("(" + SSR.getEntity().getLabel() + ")"));
            }
          }
          if (count > 0) {
            System.out.println("Eliminated " + count + " non exact KB mappings for the lexicaly related word " + this.keyword);
          }
        }
        else
        {
          filtered.addMappingList(filtered_SSRs);
        }
      }
    }
    this.mappingTable = filtered.mappingTable;
  }
  
  public boolean haveEquivalentMappings(String ontology)
  {
    ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(ontology);
    boolean any_equivalent_mapping = false;
    for (SearchSemanticResult SSR : SSRList) {
      if (SSR.getSemanticRelation().equals("equivalentMatching")) {
        return true;
      }
    }
    return false;
  }
  
  public EntityMappingTable filterExactMappings()
  {
    EntityMappingTable filtered = new EntityMappingTable(this.keyword, this.searchDuration);
    Iterator<String> iter = this.mappingTable.keySet().iterator();
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      ArrayList<SearchSemanticResult> filtered_SSRs = filterExactMappings(key);
      filtered.addMappingList(filtered_SSRs);
    }
    return filtered;
  }
  
  public ArrayList<SearchSemanticResult> filterExactMappings(String ontology)
  {
    ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(ontology);
    boolean any_exact_mapping_props = false;
    boolean any_exact_mapping_classes = false;
    boolean any_exact_mapping_kb = false;
    boolean any_exact_mapping_literals = false;
    
    ArrayList<SearchSemanticResult> filtered_SSRs = new ArrayList();
    ArrayList<SearchSemanticResult> filtered_SSRs_props = new ArrayList();
    ArrayList<SearchSemanticResult> exact_SSRs_props = new ArrayList();
    ArrayList<SearchSemanticResult> filtered_SSRs_classes = new ArrayList();
    ArrayList<SearchSemanticResult> exact_SSRs_classes = new ArrayList();
    ArrayList<SearchSemanticResult> filtered_SSRs_kb = new ArrayList();
    ArrayList<SearchSemanticResult> filtered_SSRs_literals = new ArrayList();
    ArrayList<SearchSemanticResult> exact_SSRs_kb = new ArrayList();
    ArrayList<SearchSemanticResult> exact_SSRs_literals = new ArrayList();
    ArrayList<SearchSemanticResult> results = new ArrayList();
    
    ArrayList<SearchSemanticResult> exact_lexicalWords = new ArrayList();
    for (SearchSemanticResult SSR : SSRList) {
      if ((SSR.getSemanticRelation().equals("equivalentMatching")) && (SSR.isExact()))
      {
        if (SSR.getEntity().isProperty())
        {
          any_exact_mapping_props = true;
          exact_SSRs_props.add(SSR);
        }
        else if ((SSR.getEntity().isInstance()) && (!SSR.getOsPlugin().existTripleForInstance(SSR.getEntity().getURI())))
        {
          System.out.println("Eliminating exact instance not linked to any other ontological element " + SSR.getEntity().getURI());
        }
        else if (SSR.getEntity().isClass())
        {
          any_exact_mapping_classes = true;
          exact_SSRs_classes.add(SSR);
        }
        else if (SSR.getEntity().isLiteral())
        {
          any_exact_mapping_literals = true;
          exact_SSRs_literals.add(SSR);
        }
        else
        {
          any_exact_mapping_kb = true;
          exact_SSRs_kb.add(SSR);
        }
      }
      else if ((SSR.getSemanticRelation().equals("synonym")) && (SSR.isExact()))
      {
        if (SSR.getEntity().isProperty())
        {
          any_exact_mapping_props = true;
          exact_SSRs_props.add(SSR);
        }
        else if ((SSR.getEntity().isInstance()) && (!SSR.getOsPlugin().existTripleForInstance(SSR.getEntity().getURI())))
        {
          System.out.println("Eliminating exact instance not linked to any other ontological element " + SSR.getEntity().getURI());
        }
        else if (SSR.getEntity().isClass())
        {
          any_exact_mapping_classes = true;
          exact_SSRs_classes.add(SSR);
        }
        else if (SSR.getEntity().isLiteral())
        {
          any_exact_mapping_literals = true;
          exact_SSRs_literals.add(SSR);
        }
        else
        {
          any_exact_mapping_kb = true;
          exact_SSRs_kb.add(SSR);
        }
      }
      else if (SSR.isExactLexicalWord()) {
        exact_lexicalWords.add(SSR);
      } else if (SSR.getEntity().isProperty()) {
        filtered_SSRs_props.add(SSR);
      } else if (SSR.getEntity().isClass()) {
        filtered_SSRs_classes.add(SSR);
      } else if (SSR.getEntity().isLiteral()) {
        filtered_SSRs_literals.add(SSR);
      } else {
        filtered_SSRs_kb.add(SSR);
      }
    }
    if (any_exact_mapping_classes)
    {
      if (filtered_SSRs_classes.size() > 0) {
        System.out.println("EXACT class MAPPING: eliminating " + filtered_SSRs_classes.size() + " approx classes in " + ontology);
      }
      results.addAll(exact_SSRs_classes);
      filtered_SSRs.addAll(filtered_SSRs_classes);
    }
    else
    {
      results.addAll(filtered_SSRs_classes);
    }
    if (any_exact_mapping_kb)
    {
      if (filtered_SSRs_kb.size() > 0) {
        System.out.println("EXACT instance MAPPING: eliminating " + filtered_SSRs_kb.size() + " approx in " + ontology);
      }
      results.addAll(exact_SSRs_kb);
      results.addAll(exact_SSRs_literals);
      
      filtered_SSRs.addAll(filtered_SSRs_kb);
      filtered_SSRs.addAll(filtered_SSRs_literals);
    }
    else if (!any_exact_mapping_classes)
    {
      results.addAll(filtered_SSRs_kb);
      if (any_exact_mapping_literals)
      {
        results.addAll(exact_SSRs_literals);
        System.out.println("EXACT literal MAPPING: eliminating " + filtered_SSRs_literals.size() + " approx in " + ontology);
        filtered_SSRs.addAll(filtered_SSRs_literals);
      }
      else
      {
        results.addAll(filtered_SSRs_literals);
      }
    }
    else
    {
      filtered_SSRs.addAll(filtered_SSRs_literals);
      filtered_SSRs.addAll(exact_SSRs_literals);
      filtered_SSRs.addAll(filtered_SSRs_kb);
    }
    if (any_exact_mapping_props)
    {
      if (filtered_SSRs_props.size() > 0) {
        System.out.println("EXACT property MAPPING: eliminating " + filtered_SSRs_props.size() + " approx in " + ontology);
      }
      results.addAll(exact_SSRs_props);
      filtered_SSRs.addAll(filtered_SSRs_props);
    }
    else
    {
      results.addAll(filtered_SSRs_props);
    }
    results.addAll(exact_lexicalWords);
    this.mappingTable.put(ontology, results);
    
    return filtered_SSRs;
  }
  
  public EntityMappingTable filerSimilarStringLabels()
  {
    EntityMappingTable filtered = new EntityMappingTable(this.keyword, this.searchDuration);
    EntityMappingTable eliminated = new EntityMappingTable(this.keyword, this.searchDuration);
    
    String keyword = this.keyword.replaceAll("\"", "").trim();
    
    String parse_keyword = keyword.replaceAll("-", " ");
    parse_keyword = parse_keyword.replaceAll("_", " ");
    
    Iterator<String> iter = this.mappingTable.keySet().iterator();
    stringMetricsComparator metricsComparator = new stringMetricsComparator();
    while (iter.hasNext())
    {
      String key = (String)iter.next();
      ArrayList<SearchSemanticResult> SSRList = (ArrayList)this.mappingTable.get(key);
      
      ArrayList<SearchSemanticResult> filtered_SSRs = new ArrayList();
      for (SearchSemanticResult SSR : SSRList)
      {
        String localname_raw = SSR.getEntity().getLocalName();
        String label_raw = SSR.getEntity().getLabel();
        
        String localname = localname_raw.replaceAll("-", " ");
        localname = localname.replaceAll("_", " ");
        String label = label_raw.replaceAll("-", " ");
        label = label.replaceAll("_", " ");
        
        localname = LabelSplitter.splitOnCaps(localname);
        label = LabelSplitter.splitOnCaps(label);
        keyword = LabelSplitter.splitOnCaps(keyword);
        if (SSR.getSemanticRelation().equals("equivalentMatching"))
        {
          if (keyword.startsWith("xsd:"))
          {
            SSR.setExact(true);
            filtered_SSRs.add(SSR);
          }
          else if ((StringUtils.isSingularPluralExactMapping(keyword, label)) || (StringUtils.isSingularPluralExactMapping(keyword, localname)))
          {
            SSR.setExact(true);
            filtered_SSRs.add(SSR);
          }
          else if ((getWNLemma().equalsIgnoreCase(label)) || (getWNLemma().equalsIgnoreCase(localname)))
          {
            SSR.setExact(true);
            filtered_SSRs.add(SSR);
          }
          else if (metricsComparator.stringSimilarity(keyword, label))
          {
            filtered_SSRs.add(SSR);
          }
          else if (metricsComparator.stringSimilarity(keyword, localname))
          {
            filtered_SSRs.add(SSR);
          }
          else
          {
            Logger log_poweraqua = Logger.getLogger("poweraqua");
            log_poweraqua.log(Level.INFO, " String sim.: Filtering out " + localname + " AND " + label + " (" + SSR.getEntity().getType() + ")");
            eliminated.addMapping(SSR);
          }
        }
        else
        {
          if ((SSR.getSemanticRelation().equals("synonym")) && (!getWNLemma().equals("")) && ((getWNLemma().equalsIgnoreCase(label)) || (getWNLemma().equalsIgnoreCase(localname)))) {
            SSR.setExact(true);
          }
          if ((SSR.getSemanticRelation().equals("synonym")) && ((StringUtils.isSingularPluralExactMapping(keyword, label)) || (StringUtils.isSingularPluralExactMapping(keyword, localname)))) {
            SSR.setExact(true);
          }
          if ((SSR.getSemanticRelation().equals("synonym")) && ((getWNDerived().contains(label.toLowerCase())) || (getWNDerived().contains(localname.toLowerCase())))) {
            SSR.setExact(true);
          }
          String relatedWord = SSR.getLexicalRelatedWord();
          if ((metricsComparator.stringSimilarity(relatedWord, localname)) || (metricsComparator.stringSimilarity(relatedWord, label)))
          {
            filtered_SSRs.add(SSR);
          }
          else
          {
            Logger log_poweraqua = Logger.getLogger("poweraqua");
            log_poweraqua.log(Level.INFO, " String sim.: Filtering out " + localname + " AND " + label + " (" + SSR.getEntity().getType() + ")");
            eliminated.addMapping(SSR);
          }
        }
      }
      filtered.addMappingList(filtered_SSRs);
    }
    this.mappingTable = filtered.mappingTable;
    return eliminated;
  }
  
  public boolean haveMetadata(String key)
  {
    boolean have_metadata = false;
    for (SearchSemanticResult SSR : getOntologyMappings(key)) {
      if ((SSR.getEntity().getType().equals("class")) || (SSR.getEntity().getType().equals("instance"))) {
        return true;
      }
    }
    return false;
  }
  
  public void addOntologyBackgroundHistory(Hashtable<String, RDFEntityList> ontoBackgroundEntities)
  {
    for (String key : this.mappingTable.keySet()) {
      for (SearchSemanticResult SSR : getOntologyMappings(key))
      {
        RDFEntityList entities_background = (RDFEntityList)ontoBackgroundEntities.get(SSR.getLexicalRelatedWord());
        SSR.setBackground_history(entities_background);
      }
    }
  }
  
  public void setExactMappingsToFalse()
  {
    ArrayList<String> keys = getOntologyIDMappings();
    for (String key : keys) {
      for (SearchSemanticResult SSR : getOntologyMappings(key)) {
        SSR.setExact(false);
      }
    }
  }
  
  public void printSemanticResults()
  {
    System.out.println("Showing search semantic results for " + getKeyword());
    ArrayList<String> keys = getOntologyIDMappings();
    for (String key : keys) {
      for (SearchSemanticResult SSR : getOntologyMappings(key)) {
        System.out.print(SSR);
      }
    }
  }
  
  public boolean isMergedKeyword()
  {
    return this.mergedKeyword;
  }
  
  public String getWNLemma()
  {
    return this.WNLemma;
  }
  
  public void setWNLemma(String WNLemma)
  {
    this.WNLemma = WNLemma;
  }
  
  public void setMergedKeyword(boolean mergedKeyword)
  {
    this.mergedKeyword = mergedKeyword;
  }
  
  public boolean isIs_ISACompound()
  {
    return this.is_ISACompound;
  }
  
  public void setIs_ISACompound(boolean is_ISACompound)
  {
    this.is_ISACompound = is_ISACompound;
  }
  
  public ArrayList<String> getRestrictedKeywords()
  {
    return this.restrictedKeywords;
  }
  
  public void setRestrictedKeywords(ArrayList<String> restrictedKeywords)
  {
    this.restrictedKeywords = restrictedKeywords;
  }
  
  public ArrayList<String> getWNDerived()
  {
    return this.WNDerived;
  }
  
  public void setWNDerived(ArrayList<String> WNDerived)
  {
    this.WNDerived = WNDerived;
  }
}

