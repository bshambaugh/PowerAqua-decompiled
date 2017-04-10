package poweraqua.fusion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
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
import poweraqua.indexingService.manager.MultiIndexManager;
import poweraqua.powermap.elementPhase.EntityMappingTable;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.elementPhase.SyntacticComponent;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.powermap.stringMetrics.stringMetricsComparator;
import poweraqua.powermap.triplePhase.OntoTripleBean;
import poweraqua.powermap.triplePhase.TripleMappingTable;
import poweraqua.powermap.triplePhase.TripleSimilarityService;
import poweraqua.serviceConfig.MultiOntologyManager;

public class FusionService
  implements IFusionService
{
  protected FusedAnswerBean finalAnswerBean;
  protected Map<QueryTriple, FusedAnswerBean> answerBeans;
  protected SyntacticComponent syntacticComponent;
  protected MappingSession session;
  protected stringMetricsComparator stringMetricsComparator;
  protected TripleSimilarityService tripleSimilarityService;
  protected Map<String, EntityMappingTable> entityMappingTablesByKeyword;
  
  public static float getAPPROXIMATE_STANDARD_THRESH_KB()
  {
    return APPROXIMATE_STANDARD_THRESH_KB;
  }
  
  public static void setAPPROXIMATE_STANDARD_THRESH_KB(float aAPPROXIMATE_STANDARD_THRESH_KB)
  {
    APPROXIMATE_STANDARD_THRESH_KB = aAPPROXIMATE_STANDARD_THRESH_KB;
  }
  
  private static float APPROXIMATE_STANDARD_THRESH_KB = new Float(0.416D).floatValue();
  private WordNet wordNet = null;
  private static char[] specialChars = { '|', '{', '}', '(', ')', '_', '[', ']', '.', '-', '+', ',', ':', '?', '\\', '/', '"', '@' };
  public static int MAX_NUM_WNSYNONYMS = 5;
  protected int numberOfLuceneSearchCalls;
  protected int numberOfComparisons;
  Set<String> ignoredOntologies;
  Set<String> ignoredOntologiesSynonyms;
  
  public FusionService(TripleSimilarityService tripleSimilarityService)
  {
    this.tripleSimilarityService = tripleSimilarityService;
    this.session = tripleSimilarityService.getMapSession();
    this.syntacticComponent = new SyntacticComponent(this.session);
    this.stringMetricsComparator = new stringMetricsComparator();
    this.finalAnswerBean = new FusedAnswerBean(this);
    this.answerBeans = new HashMap();
    this.entityMappingTablesByKeyword = new HashMap();
    this.ignoredOntologies = new HashSet();
    this.ignoredOntologiesSynonyms = new HashSet();
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
      System.out.println("ToDo: More than two triples for fusion");
    }
    System.out.println("Start merging by query triples");
    QueryTriple mainQueryTriple = (QueryTriple)this.tripleSimilarityService.getQueryTriples().get(0);
    QueryTriple auxQueryTriple = (QueryTriple)this.tripleSimilarityService.getQueryTriples().get(1);
    ArrayList<String> firstTermKeyword = new ArrayList();
    ArrayList<String> secondTermKeyword = new ArrayList();
    TripleMappingTable firstTable = (TripleMappingTable)this.tripleSimilarityService.getOntoKBTripleMappings().get(mainQueryTriple);
    for (String ontoUri : firstTable.getMappingTable().keySet())
    {
      List<OntoTripleBean> beanList = (List)firstTable.getMappingTable().get(ontoUri);
      for (OntoTripleBean bean : beanList) {
        if (!((OntoTriple)bean.getOntoTripleBean().get(0)).isIS_A_RELATION())
        {
          String firstTermKeyword_otb = ((OntoTriple)bean.getOntoTripleBean().get(0)).getFirstTerm().getEmt_keyword();
          
          String secondTermKeyword_otb = ((OntoTriple)bean.getOntoTripleBean().get(bean.getOntoTripleBean().size() - 1)).getSecondTerm().getEmt_keyword();
          if (!secondTermKeyword.contains(secondTermKeyword_otb)) {
            secondTermKeyword.add(secondTermKeyword_otb);
          }
          if (firstTermKeyword_otb.equals("")) {
            firstTermKeyword_otb = ((OntoTriple)bean.getOntoTripleBean().get(0)).getRelation().getEmt_keyword();
          }
          if (!firstTermKeyword.contains(firstTermKeyword_otb)) {
            firstTermKeyword.add(firstTermKeyword_otb);
          }
        }
      }
    }
    ArrayList<String> firstTermKeywordAux = new ArrayList();
    TripleMappingTable secondTable = (TripleMappingTable)this.tripleSimilarityService.getOntoKBTripleMappings().get(auxQueryTriple);
    for (String ontoUri : secondTable.getMappingTable().keySet())
    {
      List<OntoTripleBean> beanList = (List)secondTable.getMappingTable().get(ontoUri);
      for (OntoTripleBean bean : beanList)
      {
        String firstTermKeywordAux_otb = ((OntoTriple)bean.getOntoTripleBean().get(0)).getFirstTerm().getEmt_keyword();
        if (firstTermKeywordAux_otb.equals("")) {
          firstTermKeywordAux_otb = ((OntoTriple)bean.getOntoTripleBean().get(0)).getRelation().getEmt_keyword();
        }
        if (!firstTermKeywordAux.contains(firstTermKeywordAux_otb)) {
          firstTermKeywordAux.add(firstTermKeywordAux_otb);
        }
      }
    }
    System.out.println("are the subjects similar? " + firstTermKeyword.toString() + " vs " + firstTermKeywordAux.toString());
    System.out.println("are the objects and subjects similar? " + secondTermKeyword.toString() + " vs " + firstTermKeywordAux.toString());
    
    boolean intersection = false;
    boolean condition = false;
    for (Iterator i$ = firstTermKeyword.iterator(); i$.hasNext();)
    {
      firstTerm = (String)i$.next();
      for (String secondTerm : firstTermKeywordAux) {
        if (((mainQueryTriple.getQueryTerm().contains(firstTerm)) && (mainQueryTriple.getQueryTerm().contains(secondTerm))) || (firstTerm.equals(secondTerm)))
        {
          intersection = true;
          break;
        }
      }
    }
    String firstTerm;
    for (Iterator i$ = secondTermKeyword.iterator(); i$.hasNext();)
    {
      firstTerm = (String)i$.next();
      for (String secondTerm : firstTermKeywordAux) {
        if (firstTerm.equals(secondTerm))
        {
          condition = true;
          break;
        }
      }
    }
    String firstTerm;
    if ((intersection) && (!condition))
    {
      if (mainQueryTriple.getTypeQuestion() == 19)
      {
        System.out.println("UNION ACROSS QUERY TRIPLES");
        this.finalAnswerBean = fuseSimilarClustersIntersection(mainQueryTriple, auxQueryTriple, true);
      }
      else
      {
        System.out.println("INTERSECTION");
        this.finalAnswerBean = fuseSimilarClustersIntersection(mainQueryTriple, auxQueryTriple, false);
      }
    }
    else
    {
      System.out.println("CONDITION");
      
      this.finalAnswerBean = fuseSimilarClustersConditional(mainQueryTriple, auxQueryTriple);
    }
    if (this.wordNet != null)
    {
      this.wordNet.closeDictionary();
      this.wordNet = null;
    }
  }
  
  private void createLog(FusedAnswerBean finalAnswerBean)
  {
    System.out.println("Creating a log of answers for seals evaluation");
    ArrayList<String> answers = new ArrayList();
    if ((finalAnswerBean.getAnswers() != null) && (!finalAnswerBean.getAnswers().isEmpty()))
    {
      for (RDFEntityCluster cluster : finalAnswerBean.getAnswers()) {
        for (RDFEntityEntry entry : cluster.getEntries()) {
          answers.add(entry.getValue().getURI());
        }
      }
    }
    else
    {
      RDFEntityList allanswers = new RDFEntityList();
      for (QueryTriple qt : this.tripleSimilarityService.getQueryTriples())
      {
        TripleMappingTable tripleMappingTable = (TripleMappingTable)this.tripleSimilarityService.getOntoKBTripleMappings().get(qt);
        allanswers.addAllRDFEntity(tripleMappingTable.getAllAnswersNoRepetitions());
      }
      answers.addAll(allanswers.getUris());
    }
    String path;
    String path;
    if (this.session.getRealpath().equals("")) {
      path = "./logs/sealsanswer.log";
    } else {
      path = this.session.getRealpath() + "/WEB-INF/logs/sealsanswer.log";
    }
    try
    {
      Writer log = null;
      File file_log = new File(path);
      log = new BufferedWriter(new FileWriter(file_log));
      log.write(System.currentTimeMillis() + "\n");
      for (String answer : answers) {
        log.write(answer + "\n");
      }
      if (answers.isEmpty()) {
        log.write("null \n");
      }
      log.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void formRDFEntityEntries(QueryTriple queryTriple)
  {
    Calendar calendarBefore = new GregorianCalendar();
    this.numberOfComparisons = 0;
    this.numberOfLuceneSearchCalls = 0;
    Map<String, RDFEntityCluster> currentMapByLabel = new HashMap();
    Map<String, RDFEntityCluster> currentMapByLocalName = new HashMap();
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
    
    int maxAnswerSetSize = 0;
    String ontologyWithMaxAnswerSet = "";
    
    this.ignoredOntologies = new HashSet();
    this.ignoredOntologiesSynonyms = new HashSet();
    for (Iterator i$ = currentTripleMappingTable.getMappingTable().keySet().iterator(); i$.hasNext();)
    {
      ontology = (String)i$.next();
      List<OntoTripleBean> ontoTripleBeans = (List)currentTripleMappingTable.getMappingTable().get(ontology);
      for (i$ = ontoTripleBeans.iterator(); i$.hasNext();)
      {
        ontoTripleBean = (OntoTripleBean)i$.next();
        RDFEntityList answerList = ontoTripleBean.getAnswer_instances();
        if (answerList.size() > maxAnswerSetSize)
        {
          maxAnswerSetSize = answerList.size();
          ontologyWithMaxAnswerSet = ontology;
        }
        if (answerList.size() > 500) {
          this.ignoredOntologies.add(ontology);
        }
        if (answerList.size() > 200) {
          this.ignoredOntologiesSynonyms.add(ontology);
        }
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
    if (maxAnswerSetSize > 100) {
      this.ignoredOntologies.add(ontologyWithMaxAnswerSet);
    }
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
  
  private FusedAnswerBean fuseSimilarClustersIntersection(QueryTriple firstQueryTriple, QueryTriple secondQueryTriple, boolean isOrQuery)
  {
    String keyword = "";
    
    Map<RDFEntityEntry, RDFEntityCluster> clusteredEntryMap = new HashMap();
    
    List<RDFEntityEntry> similarEntries = new ArrayList();
    List<RDFEntityEntry> maybeSimilarEntries = new ArrayList();
    
    List<EntityMappingTable> tablesSynonym = new ArrayList();
    
    List<String> syns = new ArrayList();
    
    Set<RDFEntityCluster> unmergedClusters = new HashSet();
    
    FusedAnswerBean answerBean = new FusedAnswerBean(this);
    FusedAnswerBean secondAnswerBean;
    FusedAnswerBean firstAnswerBean;
    FusedAnswerBean secondAnswerBean;
    if (((FusedAnswerBean)this.answerBeans.get(firstQueryTriple)).getAnswers().size() <= ((FusedAnswerBean)this.answerBeans.get(secondQueryTriple)).getAnswers().size())
    {
      FusedAnswerBean firstAnswerBean = (FusedAnswerBean)this.answerBeans.get(firstQueryTriple);
      secondAnswerBean = (FusedAnswerBean)this.answerBeans.get(secondQueryTriple);
    }
    else
    {
      firstAnswerBean = (FusedAnswerBean)this.answerBeans.get(secondQueryTriple);
      secondAnswerBean = (FusedAnswerBean)this.answerBeans.get(firstQueryTriple);
    }
    unmergedClusters.addAll(firstAnswerBean.getAnswers());
    unmergedClusters.addAll(secondAnswerBean.getAnswers());
    
    Map<String, RDFEntityCluster> secondClusterMap = new HashMap();
    
    Set<RDFEntityCluster> clustersToMergeInto = new HashSet();
    
    Set<String> ontologyIdsMentioned = new HashSet();
    for (RDFEntityCluster currentCluster : firstAnswerBean.getAnswers()) {
      for (RDFEntityEntry entry : currentCluster.getEntries()) {
        ontologyIdsMentioned.add(entry.getOntologyId());
      }
    }
    try
    {
      Set<String> ontologyIdSet = new HashSet();
      ArrayList<SearchSemanticResult> searchResults = new ArrayList();
      for (Iterator i$ = secondAnswerBean.getAnswers().iterator(); i$.hasNext();)
      {
        currentCluster = (RDFEntityCluster)i$.next();
        for (RDFEntityEntry entry : currentCluster.getEntries())
        {
          secondClusterMap.put(entry.getValue().getURI(), currentCluster);
          ontologyIdsMentioned.add(entry.getOntologyId());
        }
      }
      RDFEntityCluster currentCluster;
      for (RDFEntityCluster currentCluster : firstAnswerBean.getAnswers())
      {
        clustersToMergeInto.clear();
        for (Iterator i$ = currentCluster.getEntries().iterator(); i$.hasNext();)
        {
          entry = (RDFEntityEntry)i$.next();
          
          RDFEntityCluster cluster = null;
          ontologyIdSet.clear();
          similarEntries.clear();
          tablesSynonym.clear();
          
          keyword = cleanString(entry.getValue().getLocalName());
          uri = entry.getValue().getURI();
          if (secondClusterMap.containsKey(uri)) {
            clustersToMergeInto.add(secondClusterMap.get(uri));
          }
          if (ontologyIdsMentioned.size() > 1)
          {
            System.out.println("Query: " + keyword);
            tableByLocalName = getEntityMappingTableForKeyword(keyword);
            
            System.out.println("Answers: " + tableByLocalName.getOntologyIDMappings().size());
            
            keyword = cleanString(entry.getValue().getLabel());
            
            tableByLabel = getEntityMappingTableForKeyword(keyword);
            
            ontologyIdSet.addAll(tableByLocalName.getOntologyIDMappings());
            ontologyIdSet.addAll(tableByLabel.getOntologyIDMappings());
            syns.clear();
            if (!this.ignoredOntologiesSynonyms.contains(entry.getOntologyId()))
            {
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
              if (keyword.toLowerCase().contains("mustang")) {
                System.out.println("here");
              }
              for (String syn : syns)
              {
                EntityMappingTable tableSynonym = getEntityMappingTableForKeyword(syn);
                
                tablesSynonym.add(tableSynonym);
                ontologyIdSet.addAll(tableSynonym.getOntologyIDMappings());
              }
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
                  boolean toCompare = true;
                  for (RDFEntityEntry secondEntry : foundCluster.getEntries()) {
                    if (secondEntry.getOntologyId().equals(entry.getOntologyId()))
                    {
                      toCompare = false;
                      break;
                    }
                  }
                  if (toCompare) {
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
          unmergedClusters.remove(currentCluster);
          for (RDFEntityCluster toMerge : clustersToMergeInto)
          {
            mergedCluster = mergedCluster.merge(toMerge);
            unmergedClusters.remove(toMerge);
          }
          answerBean.addAnswer(mergedCluster);
        }
      }
      if (isOrQuery) {
        for (RDFEntityCluster unmergedCluster : unmergedClusters) {
          answerBean.addAnswer(unmergedCluster);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    answerBean.consolidate();
    return answerBean;
  }
  
  private void putToClusterSetByUriMap(Map<String, Set<RDFEntityCluster>> map, String key, RDFEntityCluster cluster)
  {
    Set<RDFEntityCluster> clusterSet;
    Set<RDFEntityCluster> clusterSet;
    if (map.containsKey(key))
    {
      clusterSet = (Set)map.get(key);
    }
    else
    {
      clusterSet = new HashSet();
      map.put(key, clusterSet);
    }
    clusterSet.add(cluster);
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
    
    Map<String, Set<RDFEntityCluster>> secondClusterMap = new HashMap();
    List<RDFEntityCluster> clustersToMergeInto = new ArrayList();
    Set<RDFEntity> alreadyLookedAndNotFound = new HashSet();
    Map<RDFEntity, Set<RDFEntityCluster>> alreadyLookedAndFound = new HashMap();
    try
    {
      ontologyIdSet = new HashSet();
      searchResults = new ArrayList();
      
      ontologyIdsMentioned = new HashSet();
      
      useMainBean = true;
      FusedAnswerBean auxiliaryAnswerBean;
      FusedAnswerBean primaryAnswerBean;
      FusedAnswerBean auxiliaryAnswerBean;
      if (mainAnswerBean.getAnswers().size() <= conditionAnswerBean.getAnswers().size())
      {
        FusedAnswerBean primaryAnswerBean = mainAnswerBean;
        auxiliaryAnswerBean = conditionAnswerBean;
      }
      else
      {
        primaryAnswerBean = conditionAnswerBean;
        auxiliaryAnswerBean = mainAnswerBean;
        useMainBean = false;
      }
      for (RDFEntityCluster currentCluster : mainAnswerBean.getAnswers()) {
        for (RDFEntityEntry entry : currentCluster.getEntries()) {
          ontologyIdsMentioned.add(entry.getOntologyId());
        }
      }
      for (Iterator i$ = auxiliaryAnswerBean.getAnswers().iterator(); i$.hasNext();)
      {
        currentCluster = (RDFEntityCluster)i$.next();
        for (RDFEntityEntry entry : currentCluster.getEntries())
        {
          ontologyIdsMentioned.add(entry.getOntologyId());
          if (useMainBean) {
            putToClusterSetByUriMap(secondClusterMap, entry.getValue().getURI(), currentCluster);
          } else {
            for (RDFEntity refTo : entry.getRefersToValues()) {
              putToClusterSetByUriMap(secondClusterMap, refTo.getURI(), currentCluster);
            }
          }
        }
      }
      RDFEntityCluster currentCluster;
      for (i$ = primaryAnswerBean.getAnswers().iterator(); i$.hasNext();)
      {
        currentCluster = (RDFEntityCluster)i$.next();
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
          if ((!useMainBean) || (!entry.getRefersToValues().isEmpty()))
          {
            List<RDFEntity> entitiesToCheck;
            List<RDFEntity> entitiesToCheck;
            if (useMainBean)
            {
              entitiesToCheck = entry.getRefersToValues();
            }
            else
            {
              entitiesToCheck = new ArrayList();
              entitiesToCheck.add(entry.getValue());
            }
            for (RDFEntity checkedEntity : entitiesToCheck)
            {
              String uri = checkedEntity.getURI();
              if (!alreadyLookedAndNotFound.contains(checkedEntity))
              {
                if (alreadyLookedAndFound.containsKey(checkedEntity))
                {
                  boolean lookedAndFound = true;
                  for (RDFEntityCluster tmpCluster : (Set)alreadyLookedAndFound.get(checkedEntity)) {
                    if (!clustersToMergeInto.contains(tmpCluster)) {
                      clustersToMergeInto.add(tmpCluster);
                    }
                  }
                }
                if (secondClusterMap.containsKey(uri)) {
                  for (RDFEntityCluster tmpCluster : (Set)secondClusterMap.get(uri)) {
                    if (!clustersToMergeInto.contains(tmpCluster)) {
                      clustersToMergeInto.add(tmpCluster);
                    }
                  }
                }
                if (ontologyIdsMentioned.size() > 1)
                {
                  keyword = null;
                  EntityMappingTable tableByLocalName = null;
                  EntityMappingTable tableByLabel = null;
                  try
                  {
                    keyword = cleanString(checkedEntity.getLocalName());
                  }
                  catch (NullPointerException e)
                  {
                    System.err.println(entry.getValue().getLabel());
                    e.printStackTrace();
                  }
                  if (keyword != null)
                  {
                    System.out.println("Query: " + keyword);
                    
                    tableByLocalName = getEntityMappingTableForKeyword(keyword);
                    
                    ontologyIdSet.addAll(tableByLocalName.getOntologyIDMappings());
                    System.out.println("Answers: " + tableByLocalName.getOntologyIDMappings().size());
                  }
                  keyword = null;
                  try
                  {
                    keyword = cleanString(checkedEntity.getLabel());
                  }
                  catch (NullPointerException e)
                  {
                    System.err.println(entry.getValue().getLabel());
                    e.printStackTrace();
                  }
                  if (keyword != null)
                  {
                    System.out.println("Query: " + keyword);
                    
                    tableByLabel = getEntityMappingTableForKeyword(keyword);
                    
                    ontologyIdSet.addAll(tableByLabel.getOntologyIDMappings());
                    syns.clear();
                    if (!this.ignoredOntologiesSynonyms.contains(entry.getOntologyId()))
                    {
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
                        
                        EntityMappingTable tableSynonym = getEntityMappingTableForKeyword(syn);
                        
                        tablesSynonym.add(tableSynonym);
                        ontologyIdSet.addAll(tableSynonym.getOntologyIDMappings());
                      }
                    }
                  }
                  boolean lookedAndFound = false;
                  if (ontologyIdSet.size() == 0) {
                    alreadyLookedAndNotFound.add(checkedEntity);
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
                      foundUri = searchResult.getEntity().getURI();
                      
                      Set<RDFEntityCluster> foundClusters = (Set)secondClusterMap.get(foundUri);
                      if ((foundClusters != null) && 
                      
                        (!foundClusters.isEmpty())) {
                        for (i$ = foundClusters.iterator(); i$.hasNext();)
                        {
                          currentFoundCluster = (RDFEntityCluster)i$.next();
                          lookedAndFound = true;
                          if (uri.equals(foundUri))
                          {
                            if (clustersToMergeInto.contains(currentFoundCluster)) {
                              break;
                            }
                            clustersToMergeInto.add(currentFoundCluster); break;
                          }
                          boolean toCompare = true;
                          for (RDFEntityEntry secondEntry : currentFoundCluster.getEntries()) {
                            if (secondEntry.getOntologyId().equals(entry.getOntologyId()))
                            {
                              toCompare = false;
                              break;
                            }
                          }
                          if (toCompare) {
                            for (RDFEntityEntry secondEntry : currentFoundCluster.getEntries())
                            {
                              boolean areSimilar;
                              if (useMainBean)
                              {
                                boolean areSimilar = areSimilar(checkedEntity, secondEntry.getValue());
                                
                                System.out.println("keyword: " + keyword + " local name: " + secondEntry.getValue().getLocalName() + " label: " + secondEntry.getValue().getLabel() + " similarity: " + this.stringMetricsComparator.getSimilar1());
                              }
                              else
                              {
                                areSimilar = false;
                                for (RDFEntity refTo : secondEntry.getRefersToValues())
                                {
                                  areSimilar |= areSimilar(checkedEntity, refTo);
                                  
                                  System.out.println("keyword: " + keyword + " local name: " + secondEntry.getValue().getLocalName() + " label: " + secondEntry.getValue().getLabel() + " similarity: " + this.stringMetricsComparator.getSimilar1());
                                  if (areSimilar) {
                                    break;
                                  }
                                }
                              }
                              if (areSimilar)
                              {
                                lookedAndFound = true;
                                if (clustersToMergeInto.contains(currentFoundCluster)) {
                                  break;
                                }
                                clustersToMergeInto.add(currentFoundCluster); break;
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                  String foundUri;
                  Iterator i$;
                  RDFEntityCluster currentFoundCluster;
                  if (!lookedAndFound)
                  {
                    alreadyLookedAndNotFound.add(checkedEntity);
                  }
                  else
                  {
                    System.out.println(checkedEntity.getLabel() + " added to the AlreadyFound table");
                    if (clustersToMergeInto.size() > 0) {
                      if (alreadyLookedAndFound.containsKey(checkedEntity))
                      {
                        Set<RDFEntityCluster> tmpCluster = (Set)alreadyLookedAndFound.get(checkedEntity);
                        tmpCluster.addAll(clustersToMergeInto);
                      }
                      else
                      {
                        Set<RDFEntityCluster> tmpCluster = new HashSet(clustersToMergeInto);
                        alreadyLookedAndFound.put(checkedEntity, tmpCluster);
                      }
                    }
                  }
                }
              }
            }
          }
        }
        RDFEntityEntry entry;
        if (clustersToMergeInto.size() > 0) {
          if (useMainBean)
          {
            for (RDFEntityCluster clusterToMergeInto : clustersToMergeInto) {
              currentCluster.getOntoTripleBean().addBeans(clusterToMergeInto.getOntoTripleBean());
            }
            answerBean.addAnswer(currentCluster);
          }
          else
          {
            for (RDFEntityCluster clusterToMergeInto : clustersToMergeInto)
            {
              clusterToMergeInto.getOntoTripleBean().addBeans(currentCluster.getOntoTripleBean());
              
              answerBean.addAnswer(clusterToMergeInto);
            }
          }
        }
      }
    }
    catch (Exception e)
    {
      Set<String> ontologyIdSet;
      ArrayList<SearchSemanticResult> searchResults;
      Set<String> ontologyIdsMentioned;
      boolean useMainBean;
      Iterator i$;
      RDFEntityCluster currentCluster;
      e.printStackTrace();
    }
    return answerBean;
  }
  
  private EntityMappingTable getEntityMappingTableForKeyword(String keyword)
    throws Exception
  {
    EntityMappingTable resultTable = null;
    if (this.entityMappingTablesByKeyword.containsKey(keyword))
    {
      resultTable = (EntityMappingTable)this.entityMappingTablesByKeyword.get(keyword);
    }
    else
    {
      if (!StringUtils.isCompound(keyword))
      {
        resultTable = this.session.getMultiIndexManager().searchEntityMappingsonKnowledgeBase(keyword, "equivalentMatching", getAPPROXIMATE_STANDARD_THRESH_KB(), 2);
        
        this.numberOfLuceneSearchCalls += 1;
      }
      else
      {
        resultTable = this.session.getMultiIndexManager().searchEntityMappingsonKnowledgeBase(keyword, "equivalentMatching", SyntacticComponent.STANDARD_THRESH_KB, 2);
        
        this.numberOfLuceneSearchCalls += 1;
      }
      this.entityMappingTablesByKeyword.put(keyword, resultTable);
    }
    return resultTable;
  }
  
  private FusedAnswerBean fuseSimilarClustersUnion(Map<String, RDFEntityEntry> entryMap, List<RDFEntityEntry> entryList)
  {
    String keyword = "";
    
    Map<String, EntityMappingTable> tablesSynonym = new HashMap();
    List<String> syns = new ArrayList();
    
    Map<RDFEntityEntry, RDFEntityCluster> clusteredEntryMap = new HashMap();
    
    List<RDFEntityEntry> similarEntries = new ArrayList();
    
    FusedAnswerBean answerBean = new FusedAnswerBean(this);
    Map<String, Set<RDFEntityEntry>> mapByLabel = new HashMap();
    Map<String, Set<RDFEntityEntry>> mapByLocalName = new HashMap();
    try
    {
      Set<String> ontologyIdSet = new HashSet();
      ArrayList<SearchSemanticResult> searchResults = new ArrayList();
      
      Set<String> ontologyIdsMentioned = new HashSet();
      for (RDFEntityEntry entry : entryList) {
        ontologyIdsMentioned.add(entry.getOntologyId());
      }
      if (ontologyIdsMentioned.size() > 1) {
        for (RDFEntityEntry entry : entryList)
        {
          OcmlInstance currentInstance = null;
          OcmlInstance foundInstance = null;
          OcmlInstance topInstance = null;
          cluster = null;
          if (!this.ignoredOntologies.contains(entry.getOntologyId()))
          {
            ontologyIdSet.clear();
            similarEntries.clear();
            tablesSynonym.clear();
            
            keyword = cleanString(entry.getValue().getLocalName());
            
            String uri = entry.getValue().getURI();
            
            System.out.println("Query: " + keyword);
            
            EntityMappingTable tableByLocalName = getEntityMappingTableForKeyword(keyword);
            
            System.out.println("Answers: " + tableByLocalName.getOntologyIDMappings().size());
            
            keyword = cleanString(entry.getValue().getLabel());
            
            EntityMappingTable tableByLabel = getEntityMappingTableForKeyword(keyword);
            
            ontologyIdSet.addAll(tableByLocalName.getOntologyIDMappings());
            ontologyIdSet.addAll(tableByLabel.getOntologyIDMappings());
            syns.clear();
            if (!this.ignoredOntologiesSynonyms.contains(entry.getOntologyId()))
            {
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
                EntityMappingTable tableSynonym = getEntityMappingTableForKeyword(syn);
                
                tablesSynonym.put(syn, tableSynonym);
                ontologyIdSet.addAll(tableSynonym.getOntologyIDMappings());
              }
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
                for (String syn : tablesSynonym.keySet())
                {
                  EntityMappingTable tsyn = (EntityMappingTable)tablesSynonym.get(syn);
                  if (tsyn.getOntologyMappings(id) != null) {
                    searchResults.addAll(tsyn.getOntologyMappings(id));
                  }
                }
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
      table = session.getMultiIndexManager().searchEntityMappingsonKnowledgeBase(keyword, "equivalentMatching", new Float(0.1D).floatValue(), 2);
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

