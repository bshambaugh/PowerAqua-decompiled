package poweraqua.fusion;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import poweraqua.WordNetJWNL.WordNet;
import poweraqua.core.model.myocmlmodel.OcmlInstance;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.core.tripleModel.linguisticTriple.QueryTriple;
import poweraqua.core.tripleModel.ontologyTriple.OntoTriple;
import poweraqua.core.utils.LabelSplitter;
import poweraqua.core.utils.StringUtils;
import poweraqua.indexingService.manager.IndexManagerLucene;
import poweraqua.powermap.elementPhase.EntityMappingTable;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.elementPhase.SyntacticComponent;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.powermap.stringMetrics.stringMetricsComparator;
import poweraqua.powermap.triplePhase.OntoTripleBean;
import poweraqua.powermap.triplePhase.TripleMappingTable;
import poweraqua.powermap.triplePhase.TripleSimilarityService;
import poweraqua.serviceConfig.MultiOntologyManager;

public class FusionServiceUnoptimized
  implements IFusionService
{
  protected FusedAnswerBean finalAnswerBean;
  protected Map<QueryTriple, FusedAnswerBean> answerBeans;
  protected SyntacticComponent syntacticComponent;
  protected MappingSession session;
  protected stringMetricsComparator stringMetricsComparator;
  protected TripleSimilarityService tripleSimilarityService;
  protected Map<String, EntityMappingTable> entityMappingTablesByKeyword;
  public static float APPROXIMATE_STANDARD_THRESH_KB = new Float(0.416D).floatValue();
  private WordNet wordNet = null;
  private static char[] specialChars = { '|', '{', '}', '(', ')', '_', '[', ']', '.', '-', '+', ',', ':', '?', '\\', '/', '"', '@' };
  public static int MAX_NUM_WNSYNONYMS = 5;
  protected int numberOfLuceneSearchCalls;
  protected int numberOfComparisons;
  
  public FusionServiceUnoptimized(TripleSimilarityService tripleSimilarityService)
  {
    this.tripleSimilarityService = tripleSimilarityService;
    this.session = tripleSimilarityService.getMapSession();
    this.syntacticComponent = new SyntacticComponent(this.session);
    this.stringMetricsComparator = new stringMetricsComparator();
    this.finalAnswerBean = new FusedAnswerBean(this);
    this.answerBeans = new HashMap();
    this.entityMappingTablesByKeyword = new HashMap();
    try
    {
      Calendar calendar = GregorianCalendar.getInstance();
      int d = calendar.get(5);
      int m = calendar.get(2);
      y = calendar.get(1);
    }
    catch (Exception e)
    {
      int y;
      e.printStackTrace();
    }
  }
  
  public TripleSimilarityService getTripleSimilarityService()
  {
    return this.tripleSimilarityService;
  }
  
  public int getNumberOfComparisons()
  {
    return this.numberOfComparisons;
  }
  
  public int getNumberOfLuceneSearchCalls()
  {
    return this.numberOfLuceneSearchCalls;
  }
  
  public void mergeByQueryTriples()
  {
    if (this.answerBeans.size() == 1)
    {
      this.finalAnswerBean = ((FusedAnswerBean)this.answerBeans.get(this.tripleSimilarityService.getQueryTriples().get(0)));
      
      return;
    }
    try
    {
      if (this.wordNet == null) {
        this.wordNet = new WordNet(this.session.getRealpath());
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    if (this.tripleSimilarityService.getQueryTriples().size() > 2) {
      return;
    }
    System.out.println("Start merging by query triples");
    QueryTriple mainQueryTriple = (QueryTriple)this.tripleSimilarityService.getQueryTriples().get(0);
    QueryTriple auxQueryTriple = (QueryTriple)this.tripleSimilarityService.getQueryTriples().get(1);
    String firstTermKeyword = "";
    String secondTermKeyword = "";
    TripleMappingTable firstTable = (TripleMappingTable)this.tripleSimilarityService.getOntoKBTripleMappings().get(mainQueryTriple);
    
    Iterator i$ = firstTable.getMappingTable().keySet().iterator();
    if (i$.hasNext())
    {
      String ontoUri = (String)i$.next();
      List<OntoTripleBean> beanList = (List)firstTable.getMappingTable().get(ontoUri);
      firstTermKeyword = ((OntoTriple)((OntoTripleBean)beanList.get(0)).getOntoTripleBean().get(0)).getFirstTerm().getEmt_keyword();
      if (firstTermKeyword.equals("")) {
        firstTermKeyword = ((OntoTriple)((OntoTripleBean)beanList.get(0)).getOntoTripleBean().get(0)).getRelation().getEmt_keyword();
      }
    }
    String firstTermKeywordAux = "";
    
    TripleMappingTable secondTable = (TripleMappingTable)this.tripleSimilarityService.getOntoKBTripleMappings().get(auxQueryTriple);
    
    Iterator i$ = secondTable.getMappingTable().keySet().iterator();
    if (i$.hasNext())
    {
      String ontoUri = (String)i$.next();
      List<OntoTripleBean> beanList = (List)secondTable.getMappingTable().get(ontoUri);
      firstTermKeywordAux = ((OntoTriple)((OntoTripleBean)beanList.get(0)).getOntoTripleBean().get(0)).getFirstTerm().getEmt_keyword();
      if (firstTermKeywordAux.equals("")) {
        firstTermKeywordAux = ((OntoTriple)((OntoTripleBean)beanList.get(0)).getOntoTripleBean().get(0)).getRelation().getEmt_keyword();
      }
    }
    System.out.println("are the subjects similar? " + firstTermKeyword + " vs " + firstTermKeywordAux);
    if ((mainQueryTriple.getQueryTerm().contains(firstTermKeyword)) && (mainQueryTriple.getQueryTerm().contains(firstTermKeywordAux)))
    {
      System.out.println("INTERSECTION");
      this.finalAnswerBean = fuseSimilarClustersIntersection(mainQueryTriple, auxQueryTriple);
    }
    else
    {
      System.out.println("CONDITION");
      FusedAnswerBean answerBean1 = (FusedAnswerBean)this.answerBeans.get(mainQueryTriple);
      System.out.println("Main triple");
      for (RDFEntityCluster cluster : answerBean1.getAnswers()) {
        for (RDFEntityEntry entry : cluster.getEntries())
        {
          System.out.println(entry.getValue().getLocalName() + " : " + entry.getValue().getLabel());
          for (RDFEntity refTo : entry.getRefersToValues()) {
            System.out.println("\t" + refTo.getLocalName() + " : " + refTo.getLabel());
          }
        }
      }
      System.out.println("auxiliary triple");
      answerBean1 = (FusedAnswerBean)this.answerBeans.get(auxQueryTriple);
      for (RDFEntityCluster cluster : answerBean1.getAnswers()) {
        for (RDFEntityEntry entry : cluster.getEntries()) {
          System.out.println(entry.getValue().getLocalName() + " : " + entry.getValue().getLabel());
        }
      }
      this.finalAnswerBean = fuseSimilarClustersConditional(mainQueryTriple, auxQueryTriple);
    }
    if (this.wordNet != null)
    {
      this.wordNet.closeDictionary();
      this.wordNet = null;
    }
  }
  
  public void formRDFEntityEntries(QueryTriple queryTriple)
  {
    Calendar calendarBefore = new GregorianCalendar();
    this.numberOfComparisons = 0;
    this.numberOfLuceneSearchCalls = 0;
    try
    {
      if (this.wordNet == null) {
        this.wordNet = new WordNet(this.session.getRealpath());
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    Map<String, RDFEntityEntry> entryMap = new HashMap();
    List<RDFEntityEntry> entryList = new ArrayList();
    
    TripleMappingTable currentTripleMappingTable = (TripleMappingTable)this.tripleSimilarityService.getOntoKBTripleMappings().get(queryTriple);
    for (Iterator i$ = currentTripleMappingTable.getMappingTable().keySet().iterator(); i$.hasNext();)
    {
      ontology = (String)i$.next();
      List<OntoTripleBean> ontoTripleBeans = (List)currentTripleMappingTable.getMappingTable().get(ontology);
      for (i$ = ontoTripleBeans.iterator(); i$.hasNext();)
      {
        ontoTripleBean = (OntoTripleBean)i$.next();
        RDFEntityList answerList = ontoTripleBean.getAnswer_instances();
        for (RDFEntity rdfEntity : answerList.getAllRDFEntities())
        {
          RDFEntityEntry entry = (RDFEntityEntry)entryMap.get(rdfEntity.getURI());
          if (entry == null)
          {
            entry = new RDFEntityEntry(ontoTripleBean);
            entry.setValue(rdfEntity);
            
            entryMap.put(rdfEntity.getURI(), entry);
            entryList.add(entry);
            entry.setOntologyId(ontology);
          }
          else
          {
            if (entry.getOntoTripleBean() != ontoTripleBean) {
              entry.getOntoTripleBean().addBeans(ontoTripleBean);
            }
            if (rdfEntity.getRefers_to() != null) {
              entry.addRefersToValue(rdfEntity.getRefers_to());
            }
          }
        }
      }
    }
    String ontology;
    Iterator i$;
    OntoTripleBean ontoTripleBean;
    Collections.sort(entryList, new RDFEntityEntryComparator());
    Calendar calendarAfter = new GregorianCalendar();
    System.out.println("Before fusing similar");
    System.out.println("Time cost: " + (calendarAfter.getTimeInMillis() - calendarBefore.getTimeInMillis()));
    
    FusedAnswerBean answerBean = fuseSimilarClustersUnion(entryMap, entryList);
    this.answerBeans.put(queryTriple, answerBean);
    
    System.out.println("Number of clusters " + answerBean.getAnswers().size());
    
    calendarAfter = new GregorianCalendar();
    
    System.out.println("Time cost: " + (calendarAfter.getTimeInMillis() - calendarBefore.getTimeInMillis()));
  }
  
  private FusedAnswerBean fuseSimilarClustersIntersection(QueryTriple firstQueryTriple, QueryTriple secondQueryTriple)
  {
    String keyword = "";
    
    Map<RDFEntityEntry, RDFEntityCluster> clusteredEntryMap = new HashMap();
    
    List<RDFEntityEntry> similarEntries = new ArrayList();
    List<RDFEntityEntry> maybeSimilarEntries = new ArrayList();
    
    List<EntityMappingTable> tablesSynonym = new ArrayList();
    
    List<String> syns = new ArrayList();
    
    FusedAnswerBean answerBean = new FusedAnswerBean(this);
    
    FusedAnswerBean firstAnswerBean = (FusedAnswerBean)this.answerBeans.get(firstQueryTriple);
    FusedAnswerBean secondAnswerBean = (FusedAnswerBean)this.answerBeans.get(secondQueryTriple);
    
    Map<String, RDFEntityCluster> secondClusterMap = new HashMap();
    
    Set<RDFEntityCluster> clustersToMergeInto = new HashSet();
    try
    {
      ontologyIdSet = new HashSet();
      searchResults = new ArrayList();
      for (Iterator i$ = secondAnswerBean.getAnswers().iterator(); i$.hasNext();)
      {
        currentCluster = (RDFEntityCluster)i$.next();
        for (RDFEntityEntry entry : currentCluster.getEntries()) {
          secondClusterMap.put(entry.getValue().getURI(), currentCluster);
        }
      }
      RDFEntityCluster currentCluster;
      for (RDFEntityCluster currentCluster : firstAnswerBean.getAnswers())
      {
        clustersToMergeInto.clear();
        for (Iterator i$ = currentCluster.getEntries().iterator(); i$.hasNext();)
        {
          entry = (RDFEntityEntry)i$.next();
          
          OcmlInstance currentInstance = null;
          OcmlInstance foundInstance = null;
          OcmlInstance topInstance = null;
          RDFEntityCluster cluster = null;
          ontologyIdSet.clear();
          similarEntries.clear();
          tablesSynonym.clear();
          
          keyword = cleanString(entry.getValue().getLocalName());
          uri = entry.getValue().getURI();
          System.out.println("Query: " + keyword);
          EntityMappingTable tableByLocalName;
          if (this.entityMappingTablesByKeyword.containsKey(keyword))
          {
            tableByLocalName = (EntityMappingTable)this.entityMappingTablesByKeyword.get(keyword);
          }
          else
          {
            if (!StringUtils.isCompound(keyword))
            {
              EntityMappingTable tableByLocalName = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", APPROXIMATE_STANDARD_THRESH_KB, 2);
              
              this.numberOfLuceneSearchCalls += 1;
            }
            else
            {
              tableByLocalName = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", SyntacticComponent.STANDARD_THRESH_KB, 2);
              
              this.numberOfLuceneSearchCalls += 1;
            }
            this.entityMappingTablesByKeyword.put(keyword, tableByLocalName);
          }
          System.out.println("Answers: " + tableByLocalName.getOntologyIDMappings().size());
          
          keyword = cleanString(entry.getValue().getLabel());
          EntityMappingTable tableByLabel;
          if (this.entityMappingTablesByKeyword.containsKey(keyword))
          {
            tableByLabel = (EntityMappingTable)this.entityMappingTablesByKeyword.get(keyword);
          }
          else
          {
            if (!StringUtils.isCompound(keyword))
            {
              EntityMappingTable tableByLabel = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", APPROXIMATE_STANDARD_THRESH_KB, 2);
              
              this.numberOfLuceneSearchCalls += 1;
            }
            else
            {
              tableByLabel = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", SyntacticComponent.STANDARD_THRESH_KB, 2);
              
              this.numberOfLuceneSearchCalls += 1;
            }
            this.entityMappingTablesByKeyword.put(keyword, tableByLabel);
          }
          ontologyIdSet.addAll(tableByLocalName.getOntologyIDMappings());
          ontologyIdSet.addAll(tableByLabel.getOntologyIDMappings());
          syns.clear();
          try
          {
            boolean isWN = this.wordNet.Initialize(keyword.toLowerCase());
            if ((isWN) && (this.wordNet.isIs_wordnetCompound())) {
              syns = this.wordNet.getSynonyms(MAX_NUM_WNSYNONYMS);
            }
          }
          catch (Exception e)
          {
            System.err.println("Keyword " + keyword + " caused a WordNet error");
            
            e.printStackTrace();
          }
          for (String syn : syns)
          {
            EntityMappingTable tableSynonym;
            EntityMappingTable tableSynonym;
            if (this.entityMappingTablesByKeyword.containsKey(syn))
            {
              tableSynonym = (EntityMappingTable)this.entityMappingTablesByKeyword.get(syn);
            }
            else
            {
              if (!StringUtils.isCompound(syn))
              {
                EntityMappingTable tableSynonym = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(syn, "equivalentMatching", APPROXIMATE_STANDARD_THRESH_KB, 2);
                
                this.numberOfLuceneSearchCalls += 1;
              }
              else
              {
                tableSynonym = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(syn, "equivalentMatching", SyntacticComponent.STANDARD_THRESH_KB, 2);
                
                this.numberOfLuceneSearchCalls += 1;
              }
              this.entityMappingTablesByKeyword.put(syn, tableSynonym);
            }
            tablesSynonym.add(tableSynonym);
            ontologyIdSet.addAll(tableSynonym.getOntologyIDMappings());
          }
          for (String id : ontologyIdSet)
          {
            searchResults.clear();
            if (tableByLocalName.getOntologyMappings(id) != null) {
              searchResults.addAll(tableByLocalName.getOntologyMappings(id));
            }
            if (tableByLabel.getOntologyMappings(id) != null) {
              searchResults.addAll(tableByLabel.getOntologyMappings(id));
            }
            for (EntityMappingTable tsyn : tablesSynonym) {
              if (tsyn.getOntologyMappings(id) != null) {
                searchResults.addAll(tsyn.getOntologyMappings(id));
              }
            }
            double maxscore = -1.0D;
            RDFEntityEntry closestEntry = null;
            for (SearchSemanticResult searchResult : searchResults)
            {
              String foundUri = searchResult.getEntity().getURI();
              
              foundCluster = (RDFEntityCluster)secondClusterMap.get(foundUri);
              if (foundCluster != null)
              {
                if (uri.equals(foundUri))
                {
                  clustersToMergeInto.add(foundCluster);
                  
                  break;
                }
                for (RDFEntityEntry secondEntry : foundCluster.getEntries())
                {
                  boolean areSimilar = areSimilar(entry.getValue(), secondEntry.getValue());
                  
                  System.out.println("keyword: " + keyword + " local name: " + secondEntry.getValue().getLocalName() + " label: " + secondEntry.getValue().getLabel() + " similarity: " + this.stringMetricsComparator.getSimilar1());
                  if (areSimilar)
                  {
                    clustersToMergeInto.add(foundCluster);
                    
                    break;
                  }
                }
              }
            }
          }
        }
        RDFEntityEntry entry;
        String uri;
        EntityMappingTable tableByLocalName;
        EntityMappingTable tableByLabel;
        RDFEntityCluster foundCluster;
        if (clustersToMergeInto.size() > 0)
        {
          RDFEntityCluster mergedCluster = currentCluster;
          for (RDFEntityCluster toMerge : clustersToMergeInto) {
            mergedCluster = mergedCluster.merge(toMerge);
          }
          answerBean.addAnswer(mergedCluster);
        }
      }
    }
    catch (Exception e)
    {
      Set<String> ontologyIdSet;
      ArrayList<SearchSemanticResult> searchResults;
      e.printStackTrace();
    }
    return answerBean;
  }
  
  private FusedAnswerBean fuseSimilarClustersConditional(QueryTriple mainQueryTriple, QueryTriple conditionQueryTriple)
  {
    String keyword = "";
    
    List<EntityMappingTable> tablesSynonym = new ArrayList();
    List<String> syns = new ArrayList();
    
    Map<RDFEntityEntry, RDFEntityCluster> clusteredEntryMap = new HashMap();
    
    List<RDFEntityEntry> similarEntries = new ArrayList();
    List<RDFEntityEntry> maybeSimilarEntries = new ArrayList();
    
    FusedAnswerBean answerBean = new FusedAnswerBean(this);
    
    FusedAnswerBean mainAnswerBean = (FusedAnswerBean)this.answerBeans.get(mainQueryTriple);
    FusedAnswerBean conditionAnswerBean = (FusedAnswerBean)this.answerBeans.get(conditionQueryTriple);
    
    Map<String, RDFEntityCluster> secondClusterMap = new HashMap();
    List<RDFEntityCluster> clustersToMergeInto = new ArrayList();
    Set<RDFEntity> alreadyLookedAndNotFound = new HashSet();
    Map<RDFEntity, RDFEntityCluster> alreadyLookedAndFound = new HashMap();
    try
    {
      ontologyIdSet = new HashSet();
      searchResults = new ArrayList();
      for (Iterator i$ = conditionAnswerBean.getAnswers().iterator(); i$.hasNext();)
      {
        currentCluster = (RDFEntityCluster)i$.next();
        for (RDFEntityEntry entry : currentCluster.getEntries()) {
          secondClusterMap.put(entry.getValue().getURI(), currentCluster);
        }
      }
      RDFEntityCluster currentCluster;
      for (RDFEntityCluster currentCluster : mainAnswerBean.getAnswers())
      {
        clustersToMergeInto.clear();
        for (Iterator i$ = currentCluster.getEntries().iterator(); i$.hasNext();)
        {
          entry = (RDFEntityEntry)i$.next();
          tablesSynonym.clear();
          OcmlInstance currentInstance = null;
          OcmlInstance foundInstance = null;
          OcmlInstance topInstance = null;
          RDFEntityCluster cluster = null;
          ontologyIdSet.clear();
          similarEntries.clear();
          if (!entry.getRefersToValues().isEmpty()) {
            for (RDFEntity refTo : entry.getRefersToValues()) {
              if (!alreadyLookedAndNotFound.contains(refTo))
              {
                if (alreadyLookedAndFound.containsKey(refTo))
                {
                  boolean lookedAndFound = true;
                  clustersToMergeInto.add(alreadyLookedAndFound.get(refTo));
                  
                  break;
                }
                keyword = null;
                EntityMappingTable tableByLocalName = null;
                EntityMappingTable tableByLabel = null;
                try
                {
                  keyword = cleanString(refTo.getLocalName());
                }
                catch (NullPointerException e)
                {
                  System.err.println(entry.getValue().getLabel());
                  e.printStackTrace();
                }
                String uri = entry.getValue().getURI();
                if (keyword != null)
                {
                  System.out.println("Query: " + keyword);
                  if (this.entityMappingTablesByKeyword.containsKey(keyword))
                  {
                    tableByLocalName = (EntityMappingTable)this.entityMappingTablesByKeyword.get(keyword);
                  }
                  else
                  {
                    if (!StringUtils.isCompound(keyword))
                    {
                      tableByLocalName = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", APPROXIMATE_STANDARD_THRESH_KB, 2);
                      
                      this.numberOfLuceneSearchCalls += 1;
                    }
                    else
                    {
                      tableByLocalName = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", SyntacticComponent.STANDARD_THRESH_KB, 2);
                      
                      this.numberOfLuceneSearchCalls += 1;
                    }
                    this.entityMappingTablesByKeyword.put(keyword, tableByLocalName);
                  }
                  ontologyIdSet.addAll(tableByLocalName.getOntologyIDMappings());
                  System.out.println("Answers: " + tableByLocalName.getOntologyIDMappings().size());
                }
                keyword = null;
                try
                {
                  keyword = cleanString(refTo.getLabel());
                }
                catch (NullPointerException e)
                {
                  System.err.println(entry.getValue().getLabel());
                  e.printStackTrace();
                }
                if (keyword != null)
                {
                  System.out.println("Query: " + keyword);
                  if (this.entityMappingTablesByKeyword.containsKey(keyword))
                  {
                    tableByLabel = (EntityMappingTable)this.entityMappingTablesByKeyword.get(keyword);
                  }
                  else
                  {
                    if (!StringUtils.isCompound(keyword))
                    {
                      tableByLabel = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", APPROXIMATE_STANDARD_THRESH_KB, 2);
                      
                      this.numberOfLuceneSearchCalls += 1;
                    }
                    else
                    {
                      tableByLabel = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", SyntacticComponent.STANDARD_THRESH_KB, 2);
                      
                      this.numberOfLuceneSearchCalls += 1;
                    }
                    this.entityMappingTablesByKeyword.put(keyword, tableByLabel);
                  }
                  ontologyIdSet.addAll(tableByLabel.getOntologyIDMappings());
                  syns.clear();
                  try
                  {
                    System.out.println("Looking for synonyms");
                    boolean isWN = this.wordNet.Initialize(keyword.toLowerCase());
                    if ((isWN) && (this.wordNet.isIs_wordnetCompound())) {
                      syns = this.wordNet.getSynonyms(MAX_NUM_WNSYNONYMS);
                    }
                  }
                  catch (Exception e)
                  {
                    System.err.println("Keyword " + keyword + " caused a WordNet error");
                    
                    e.printStackTrace();
                  }
                  for (String syn : syns)
                  {
                    System.out.println("\tsynonym query: " + syn);
                    EntityMappingTable tableSynonym;
                    EntityMappingTable tableSynonym;
                    if (this.entityMappingTablesByKeyword.containsKey(syn))
                    {
                      tableSynonym = (EntityMappingTable)this.entityMappingTablesByKeyword.get(syn);
                    }
                    else
                    {
                      if (!StringUtils.isCompound(syn))
                      {
                        EntityMappingTable tableSynonym = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(syn, "equivalentMatching", APPROXIMATE_STANDARD_THRESH_KB, 2);
                        
                        this.numberOfLuceneSearchCalls += 1;
                      }
                      else
                      {
                        tableSynonym = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(syn, "equivalentMatching", SyntacticComponent.STANDARD_THRESH_KB, 2);
                        
                        this.numberOfLuceneSearchCalls += 1;
                      }
                      this.entityMappingTablesByKeyword.put(syn, tableSynonym);
                    }
                    tablesSynonym.add(tableSynonym);
                    ontologyIdSet.addAll(tableSynonym.getOntologyIDMappings());
                  }
                }
                boolean lookedAndFound = false;
                if (ontologyIdSet.size() == 0)
                {
                  alreadyLookedAndNotFound.add(refTo);
                  System.out.println(refTo.getLabel() + " added to the NotFound table");
                }
                for (String id : ontologyIdSet)
                {
                  searchResults.clear();
                  if ((tableByLocalName != null) && 
                    (tableByLocalName.getOntologyMappings(id) != null)) {
                    searchResults.addAll(tableByLocalName.getOntologyMappings(id));
                  }
                  if ((tableByLabel != null) && 
                    (tableByLabel.getOntologyMappings(id) != null)) {
                    searchResults.addAll(tableByLabel.getOntologyMappings(id));
                  }
                  for (EntityMappingTable tsyn : tablesSynonym) {
                    if (tsyn.getOntologyMappings(id) != null) {
                      searchResults.addAll(tsyn.getOntologyMappings(id));
                    }
                  }
                  double maxscore = -1.0D;
                  RDFEntityEntry closestEntry = null;
                  for (SearchSemanticResult searchResult : searchResults)
                  {
                    String foundUri = searchResult.getEntity().getURI();
                    foundCluster = (RDFEntityCluster)secondClusterMap.get(foundUri);
                    if (foundCluster != null)
                    {
                      lookedAndFound = true;
                      if (uri.equals(foundUri))
                      {
                        clustersToMergeInto.add(foundCluster);
                        break;
                      }
                      for (RDFEntityEntry secondEntry : foundCluster.getEntries())
                      {
                        boolean areSimilar = areSimilar(refTo, secondEntry.getValue());
                        
                        System.out.println("keyword: " + keyword + " local name: " + secondEntry.getValue().getLocalName() + " label: " + secondEntry.getValue().getLabel() + " similarity: " + this.stringMetricsComparator.getSimilar1());
                        if (areSimilar)
                        {
                          lookedAndFound = true;
                          clustersToMergeInto.add(foundCluster);
                          break;
                        }
                      }
                    }
                  }
                }
                RDFEntityCluster foundCluster;
                if (!lookedAndFound)
                {
                  System.out.println(refTo.getLabel() + " added to the NotFound table");
                  
                  alreadyLookedAndNotFound.add(refTo);
                }
                else
                {
                  System.out.println(refTo.getLabel() + " added to the AlreadyFound table");
                  if (clustersToMergeInto.size() > 0) {
                    alreadyLookedAndFound.put(refTo, clustersToMergeInto.get(0));
                  }
                }
              }
            }
          }
        }
        RDFEntityEntry entry;
        if (clustersToMergeInto.size() > 0)
        {
          for (RDFEntityCluster clusterToMergeInto : clustersToMergeInto) {
            currentCluster.getOntoTripleBean().addBeans(clusterToMergeInto.getOntoTripleBean());
          }
          answerBean.addAnswer(currentCluster);
        }
      }
    }
    catch (Exception e)
    {
      Set<String> ontologyIdSet;
      ArrayList<SearchSemanticResult> searchResults;
      e.printStackTrace();
    }
    return answerBean;
  }
  
  private FusedAnswerBean fuseSimilarClustersUnion(Map<String, RDFEntityEntry> entryMap, List<RDFEntityEntry> entryList)
  {
    String keyword = "";
    
    Map<String, EntityMappingTable> tablesSynonym = new HashMap();
    List<String> syns = new ArrayList();
    
    Map<RDFEntityEntry, RDFEntityCluster> clusteredEntryMap = new HashMap();
    
    List<RDFEntityEntry> similarEntries = new ArrayList();
    
    FusedAnswerBean answerBean = new FusedAnswerBean(this);
    try
    {
      Set<String> ontologyIdSet = new HashSet();
      ArrayList<SearchSemanticResult> searchResults = new ArrayList();
      for (RDFEntityEntry entry : entryList)
      {
        OcmlInstance currentInstance = null;
        OcmlInstance foundInstance = null;
        OcmlInstance topInstance = null;
        cluster = null;
        
        ontologyIdSet.clear();
        similarEntries.clear();
        tablesSynonym.clear();
        
        keyword = cleanString(entry.getValue().getLocalName());
        
        String uri = entry.getValue().getURI();
        
        System.out.println("Query: " + keyword);
        EntityMappingTable tableByLocalName;
        EntityMappingTable tableByLocalName;
        if (this.entityMappingTablesByKeyword.containsKey(keyword))
        {
          tableByLocalName = (EntityMappingTable)this.entityMappingTablesByKeyword.get(keyword);
        }
        else
        {
          if (!StringUtils.isCompound(keyword))
          {
            EntityMappingTable tableByLocalName = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", APPROXIMATE_STANDARD_THRESH_KB, 2);
            
            this.numberOfLuceneSearchCalls += 1;
          }
          else
          {
            tableByLocalName = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", SyntacticComponent.STANDARD_THRESH_KB, 2);
            
            this.numberOfLuceneSearchCalls += 1;
          }
          this.entityMappingTablesByKeyword.put(keyword, tableByLocalName);
        }
        System.out.println("Answers: " + tableByLocalName.getOntologyIDMappings().size());
        
        keyword = cleanString(entry.getValue().getLabel());
        EntityMappingTable tableByLabel;
        EntityMappingTable tableByLabel;
        if (this.entityMappingTablesByKeyword.containsKey(keyword))
        {
          tableByLabel = (EntityMappingTable)this.entityMappingTablesByKeyword.get(keyword);
        }
        else
        {
          if (!StringUtils.isCompound(keyword))
          {
            EntityMappingTable tableByLabel = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", APPROXIMATE_STANDARD_THRESH_KB, 2);
            
            this.numberOfLuceneSearchCalls += 1;
          }
          else
          {
            tableByLabel = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", SyntacticComponent.STANDARD_THRESH_KB, 2);
            
            this.numberOfLuceneSearchCalls += 1;
          }
          this.entityMappingTablesByKeyword.put(keyword, tableByLabel);
        }
        ontologyIdSet.addAll(tableByLocalName.getOntologyIDMappings());
        ontologyIdSet.addAll(tableByLabel.getOntologyIDMappings());
        syns.clear();
        try
        {
          if (keyword.split(" ").length < 5)
          {
            boolean isWN = this.wordNet.Initialize(keyword.toLowerCase());
            if ((isWN) && (this.wordNet.isIs_wordnetCompound())) {
              syns = this.wordNet.getSynonyms(MAX_NUM_WNSYNONYMS);
            }
          }
        }
        catch (Exception e)
        {
          System.err.println("Keyword " + keyword + " caused a WordNet error");
          
          e.printStackTrace();
        }
        for (String syn : syns)
        {
          EntityMappingTable tableSynonym;
          EntityMappingTable tableSynonym;
          if (this.entityMappingTablesByKeyword.containsKey(syn))
          {
            tableSynonym = (EntityMappingTable)this.entityMappingTablesByKeyword.get(syn);
          }
          else
          {
            if (!StringUtils.isCompound(syn))
            {
              EntityMappingTable tableSynonym = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(syn, "equivalentMatching", APPROXIMATE_STANDARD_THRESH_KB, 2);
              
              this.numberOfLuceneSearchCalls += 1;
            }
            else
            {
              tableSynonym = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(syn, "equivalentMatching", SyntacticComponent.STANDARD_THRESH_KB, 2);
              
              this.numberOfLuceneSearchCalls += 1;
            }
            this.entityMappingTablesByKeyword.put(syn, tableSynonym);
          }
          tablesSynonym.put(syn, tableSynonym);
          ontologyIdSet.addAll(tableSynonym.getOntologyIDMappings());
        }
        for (String id : ontologyIdSet) {
          if (!id.equals(entry.getOntologyId()))
          {
            searchResults.clear();
            if (tableByLocalName.getOntologyMappings(id) != null) {
              searchResults.addAll(tableByLocalName.getOntologyMappings(id));
            }
            if (tableByLabel.getOntologyMappings(id) != null) {
              searchResults.addAll(tableByLabel.getOntologyMappings(id));
            }
            for (Iterator i$ = tablesSynonym.keySet().iterator(); i$.hasNext();)
            {
              syn = (String)i$.next();
              EntityMappingTable tsyn = (EntityMappingTable)tablesSynonym.get(syn);
              if (tsyn.getOntologyMappings(id) != null)
              {
                searchResults.addAll(tsyn.getOntologyMappings(id));
                
                System.out.println("Found by synonyms");
                for (SearchSemanticResult searchResult : tsyn.getOntologyMappings(id)) {
                  System.out.println("  " + keyword + "   " + syn + "   " + searchResult.getEntity().getLabel());
                }
              }
            }
            String syn;
            double maxscore = -1.0D;
            RDFEntityEntry closestEntry = null;
            for (SearchSemanticResult searchResult : searchResults)
            {
              String foundUri = searchResult.getEntity().getURI();
              if (!uri.equals(foundUri))
              {
                RDFEntityEntry foundEntry = (RDFEntityEntry)entryMap.get(foundUri);
                if (foundEntry != null)
                {
                  boolean areSimilar = areSimilar(entry.getValue(), foundEntry.getValue());
                  if (areSimilar) {
                    if (this.stringMetricsComparator.getSimilar1() > maxscore)
                    {
                      maxscore = this.stringMetricsComparator.getSimilar1();
                      closestEntry = foundEntry;
                    }
                    else if (this.stringMetricsComparator.getSimilar1() == maxscore)
                    {
                      if (!closestEntry.getValue().getURI().equals(foundEntry.getValue().getURI()))
                      {
                        System.out.println("Compare using all properties");
                        if (currentInstance == null) {
                          currentInstance = getOcmlInstance(entry);
                        }
                        if (topInstance == null) {
                          topInstance = getOcmlInstance(closestEntry);
                        }
                        if (foundInstance == null) {
                          foundInstance = getOcmlInstance(foundEntry);
                        }
                        if (BagOfWordsEntityComparator.getSimilarity(currentInstance, foundInstance) > BagOfWordsEntityComparator.getSimilarity(currentInstance, topInstance))
                        {
                          topInstance = foundInstance;
                          closestEntry = foundEntry;
                        }
                      }
                    }
                  }
                }
              }
            }
            if (closestEntry != null)
            {
              similarEntries.add(closestEntry);
              if (cluster == null) {
                cluster = (RDFEntityCluster)clusteredEntryMap.get(closestEntry);
              }
              System.out.println("Approved: keyword: " + keyword + " local name: " + closestEntry.getValue().getLocalName() + " label: " + closestEntry.getValue().getLabel());
            }
          }
        }
        if (similarEntries.size() > 0)
        {
          System.out.println("Merge them");
          if (cluster == null)
          {
            cluster = new RDFEntityCluster(this);
            answerBean.addAnswer(cluster);
          }
          if (cluster.addEntry(entry)) {
            clusteredEntryMap.put(entry, cluster);
          }
          for (RDFEntityEntry similarEntry : similarEntries) {
            if (cluster.addEntry(similarEntry)) {
              clusteredEntryMap.put(similarEntry, cluster);
            }
          }
        }
      }
      RDFEntityCluster cluster;
      System.out.println("Entries: " + entryList.size());
      entryList.removeAll(clusteredEntryMap.keySet());
      for (RDFEntityEntry entry : entryList)
      {
        RDFEntityCluster cluster = new RDFEntityCluster(this);
        cluster.addEntry(entry);
        answerBean.addAnswer(cluster);
      }
      System.out.println("Clusters: " + answerBean.getAnswers().size());
      answerBean.sortAnswers();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return answerBean;
  }
  
  public FusedAnswerBean getFinalAnswerBeanSortedBy(int criterion)
  {
    FusedAnswerBean bean = new FusedAnswerBean(this);
    for (RDFEntityCluster cluster : this.finalAnswerBean.getAnswers()) {
      bean.addAnswer(cluster);
    }
    bean.sortAnswers(criterion);
    return bean;
  }
  
  public FusedAnswerBean getFinalAnswerBean()
  {
    return this.finalAnswerBean;
  }
  
  public Map<QueryTriple, FusedAnswerBean> getAnswerBeanMap()
  {
    return this.answerBeans;
  }
  
  public void setTripleSimilarityService(TripleSimilarityService service)
  {
    this.tripleSimilarityService = service;
  }
  
  private boolean areSimilar(RDFEntity entity1, RDFEntity entity2)
  {
    this.numberOfComparisons += 1;
    if (this.stringMetricsComparator.stringSimilarity(entity1.getLocalName(), entity2.getLabel())) {
      return true;
    }
    if (this.stringMetricsComparator.stringSimilarity(entity1.getLabel(), entity2.getLocalName())) {
      return true;
    }
    if (this.stringMetricsComparator.stringSimilarity(entity1.getLabel(), entity2.getLabel())) {
      return true;
    }
    try
    {
      boolean isWN = this.wordNet.Initialize(cleanString(entity1.getLabel().toLowerCase()));
      if (isWN) {
        for (String syn : this.wordNet.getSynonyms()) {
          if (this.stringMetricsComparator.stringSimilarityLight(syn, entity2.getLabel())) {
            return true;
          }
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return false;
  }
  
  private OcmlInstance getOcmlInstance(RDFEntityEntry entry)
  {
    MultiOntologyManager ontoManager = this.session.getMultiOntologyManager();
    try
    {
      OntologyPlugin plugin = ontoManager.getPlugin(entry.getOntologyId());
      return plugin.getInstanceInfo(entry.getValue().getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  private static String getQueryTripleTitle(QueryTriple triple)
  {
    String res = "";
    
    List<String> terms = triple.getQueryTerm();
    res = "(";
    int i = 0;
    for (String term : terms)
    {
      res = res + term;
      if (i < terms.size() - 1) {
        res = res + ",";
      }
      i++;
    }
    res = res + ")";
    res = res + "-";
    if (triple.getRelation() != null) {
      res = res + triple.getRelation();
    }
    res = res + "-";
    if (triple.getSecondTerm() != null) {
      res = res + triple.getSecondTerm();
    }
    res = res + "-";
    if (triple.getThirdTerm() != null) {
      res = res + triple.getThirdTerm();
    }
    return res;
  }
  
  private static String cleanString(String val)
  {
    String res = val;
    if (res.indexOf("^^") != -1) {
      res = res.substring(0, res.indexOf("^^"));
    }
    for (int i = 0; i < specialChars.length; i++) {
      while (res.indexOf(specialChars[i]) != -1) {
        res = res.substring(0, res.indexOf(specialChars[i])) + " " + res.substring(res.indexOf(specialChars[i]) + 1);
      }
    }
    if (!res.contains(" ")) {
      res = LabelSplitter.splitOnCaps(res);
    }
    return res;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    System.out.println(cleanString("|NEWORLEANS@fig|"));
    
    MappingSession session = new MappingSession();
    
    String keyword = "French Republic";
    
    String[] keywords = { "Wyoming" };
    EntityMappingTable table;
    for (int i = 0; i < keywords.length; i++)
    {
      keyword = keywords[i];
      table = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, "equivalentMatching", new Float(0.1D).floatValue(), 2);
      for (String id : table.getOntologyIDMappings())
      {
        ArrayList<SearchSemanticResult> searchResults = table.getOntologyMappings(id);
        for (SearchSemanticResult searchResult : searchResults)
        {
          String foundUri = searchResult.getEntity().getURI();
          System.out.println("keyword: " + keyword + " score: " + searchResult.getScore() + " local name: " + searchResult.getEntity().getLocalName() + " label: " + searchResult.getEntity().getLabel());
        }
      }
    }
  }
}

