package poweraqua.indexingService.manager;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spell.SpellChecker;
import poweraqua.WordNetJWNL.WNSynsetSetBean;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.utils.StringUtils;
import poweraqua.indexingService.LuceneService;
import poweraqua.indexingService.MyHitCollector;
import poweraqua.indexingService.MyHitCollector.LimitExceeded;
import poweraqua.powermap.elementPhase.EntityMappingTable;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.elementPhase.SyntacticComponent;

public class IndexManagerLucene
  implements IndexManager
{
  private static ArrayList<IndexManagerLucene> luceneIndexList = new ArrayList();
  private static int LIMIT_PER_ONTO = 17;
  private static int ABSOLUTE_LIMIT_PER_ONTO = 35;
  private static int LIMIT_HITS = 1000;
  private static int LIMIT_SORTED_HITS = 600;
  private IndexBean indexBean;
  private String indexGlobal_path;
  private IndexWriter indexWritter;
  private IndexWriter indexWritterInstances;
  private SpellChecker spellChecker;
  private SpellChecker spellCheckerInstances;
  private MetadataIndexManager metadataIndexManager;
  private IndexSearcher ontologySearcher;
  private IndexSearcher kbSearcher;
  private MultiSearcher multiSearcher;
  private static MultiSearcher ontologyMultiSearcher;
  private static MultiSearcher kbMultiSearcher;
  private static MultiSearcher globalMultiSearcher;
  private static ArrayList<String> NOT_RDF_PROPERTIES = new ArrayList();
  
  public IndexManagerLucene(IndexBean indexBean, String indexGlobal_path)
    throws IOException
  {
    this.indexBean = indexBean;
    luceneIndexList.add(this);
    this.indexGlobal_path = indexGlobal_path;
    
    NOT_RDF_PROPERTIES.add("http://www.w3.org/2000/01/rdf-schema#seeAlso");
    NOT_RDF_PROPERTIES.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");
    NOT_RDF_PROPERTIES.add("http://www.w3.org/2000/01/rdf-schema#isDefinedBy");
    NOT_RDF_PROPERTIES.add("http://www.w3.org/2000/01/rdf-schema#member");
    try
    {
      System.out.println("Opening Index : " + this.indexBean.getIndex_dir());
      this.kbSearcher = new IndexSearcher(getIndexGlobal_path() + this.indexBean.getInstances_index_dir());
      this.ontologySearcher = new IndexSearcher(getIndexGlobal_path() + this.indexBean.getIndex_dir());
      Searcher[] searchers = new Searcher[2];
      searchers[0] = this.ontologySearcher;
      searchers[1] = this.kbSearcher;
      this.multiSearcher = new MultiSearcher(searchers);
    }
    catch (Exception e)
    {
      System.out.println("This shoul dnot be here, it is affecting the creation of indexes. And it is not Watson compatible");
      e.printStackTrace();
    }
  }
  
  public static void initializeGlobalSearchers()
  {
    Searcher[] ontoSearchers = new Searcher[luceneIndexList.size()];
    Searcher[] kbSearchers = new Searcher[luceneIndexList.size()];
    Searcher[] allSearchers = new Searcher[luceneIndexList.size() * 2];
    
    int i = 0;
    for (IndexManagerLucene indexManager : luceneIndexList)
    {
      ontoSearchers[i] = indexManager.getSearcherClassesAndProperties();
      kbSearchers[i] = indexManager.getSearcherInstances();
      allSearchers[(i * 2)] = indexManager.getSearcherClassesAndProperties();
      allSearchers[(i * 2 + 1)] = indexManager.getSearcherInstances();
      i++;
    }
    try
    {
      if (kbMultiSearcher == null) {
        kbMultiSearcher = new MultiSearcher(kbSearchers);
      }
      if (ontologyMultiSearcher == null) {
        ontologyMultiSearcher = new MultiSearcher(ontoSearchers);
      }
      if (globalMultiSearcher == null) {
        globalMultiSearcher = new MultiSearcher(allSearchers);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public void openMetadataIndexForQueries()
  {
    this.metadataIndexManager = new MetadataIndexManager(this.indexBean.getMetadata_index_db(), this.indexBean.getMetadata_index_db_login(), this.indexBean.getMetadata_index_db_password(), this.indexBean.getMetadata_index_db_table(), false);
  }
  
  public void openIndexForCreation(boolean synsetToIndex)
  {
    try
    {
      this.indexWritter = LuceneService.createStandardIndex(getIndexGlobal_path() + this.indexBean.getIndex_dir());
      this.indexWritterInstances = LuceneService.createStandardIndex(getIndexGlobal_path() + this.indexBean.getInstances_index_dir());
      this.metadataIndexManager = new MetadataIndexManager(this.indexBean.getMetadata_index_db(), this.indexBean.getMetadata_index_db_login(), this.indexBean.getMetadata_index_db_password(), this.indexBean.getMetadata_index_db_table(), true, synsetToIndex);
    }
    catch (IOException ex)
    {
      System.out.println("The index " + getIndexGlobal_path() + this.indexBean.getIndex_dir() + " could not be created ");
      ex.printStackTrace();
    }
  }
  
  public void openIndexForUpload(boolean synsetToIndex)
  {
    try
    {
      this.indexWritter = LuceneService.openStandardIndex(getIndexGlobal_path() + this.indexBean.getIndex_dir());
      this.indexWritterInstances = LuceneService.openStandardIndex(getIndexGlobal_path() + this.indexBean.getInstances_index_dir());
      this.metadataIndexManager = new MetadataIndexManager(this.indexBean.getMetadata_index_db(), this.indexBean.getMetadata_index_db_login(), this.indexBean.getMetadata_index_db_password(), this.indexBean.getMetadata_index_db_table(), false, synsetToIndex);
    }
    catch (IOException ex)
    {
      System.out.println("The index " + getIndexGlobal_path() + this.indexBean.getIndex_dir() + " could not be opened ");
      ex.printStackTrace();
    }
  }
  
  public void createSpellCheckerFromIndex()
  {
    try
    {
      this.spellChecker = LuceneService.createSpellCheckerFromIndex(getIndexGlobal_path() + this.indexBean.getIndex_dir(), "indexField", getIndexGlobal_path() + this.indexBean.getSpell_index_dir());
      
      this.spellCheckerInstances = LuceneService.createSpellCheckerFromIndex(getIndexGlobal_path() + this.indexBean.getInstances_index_dir(), "indexField", getIndexGlobal_path() + this.indexBean.getInstances_spell_index_dir());
    }
    catch (IOException ex)
    {
      System.out.println("The index " + getIndexGlobal_path() + this.indexBean.getSpell_index_dir() + " could not be created ");
      ex.printStackTrace();
    }
  }
  
  public void openSpellCheckerFromIndex()
  {
    try
    {
      this.spellChecker = LuceneService.openSpellChecker(getIndexGlobal_path() + this.indexBean.getSpell_index_dir());
      this.spellCheckerInstances = LuceneService.openSpellChecker(getIndexGlobal_path() + this.indexBean.getInstances_spell_index_dir());
    }
    catch (IOException ex)
    {
      System.out.println("The indexes " + getIndexGlobal_path() + this.indexBean.getSpell_index_dir() + " or " + getIndexGlobal_path() + this.indexBean.getInstances_spell_index_dir() + " could not be opened ");
      
      ex.printStackTrace();
    }
  }
  
  public int getIndexType()
  {
    return 2;
  }
  
  public String getId()
  {
    return this.indexBean.getIndex_dir();
  }
  
  public String getPath()
  {
    return getIndexGlobal_path() + this.indexBean.getIndex_dir();
  }
  
  public void addRDFEntitiesToOntologyIndex(RDFEntityList entityList)
    throws IOException
  {
    for (RDFEntity entity : entityList.getAllRDFEntities())
    {
      Document doc = getDocumentFormSemanticEntity(entity);
      if (doc != null) {
        this.indexWritter.addDocument(doc);
      }
    }
    System.out.println(entityList.size() + " entities added to the ontology index");
  }
  
  public void addRDFEntitiesToKnowledgeBaseIndex(RDFEntityList entityList)
    throws IOException
  {
    for (RDFEntity entity : entityList.getAllRDFEntities())
    {
      Document doc = getDocumentFormSemanticEntity(entity);
      if (doc != null) {
        this.indexWritterInstances.addDocument(doc);
      }
    }
    System.out.println(entityList.size() + " entities added to the knowledge base index");
  }
  
  public void addLiteralsToKnowledgeBaseIndex(RDFEntity relatedInstance, RDFEntityList literals)
    throws IOException
  {
    for (RDFEntity literal : literals.getAllRDFEntities()) {
      if (!literal.getLabel().equals(relatedInstance.getLabel()))
      {
        Document doc = getDocumentFromSemanticLiteral(relatedInstance, literal);
        if (doc != null) {
          this.indexWritterInstances.addDocument(doc);
        }
      }
    }
    if (literals.size() > 1) {
      System.out.println(literals.size() + " literals indexed for " + relatedInstance.getURI());
    }
  }
  
  public void addSuperClassesToIndex(RDFEntity entity, RDFEntityList superClasses)
  {
    this.metadataIndexManager.addSuperClassesToIndex(entity, superClasses);
  }
  
  public void addSubClassesToIndex(RDFEntity entity, RDFEntityList subClasses)
  {
    this.metadataIndexManager.addSubClassesToIndex(entity, subClasses);
  }
  
  public void addDirectSuperClassesToIndex(RDFEntity entity, RDFEntityList superClasses)
  {
    this.metadataIndexManager.addDirectSuperClassesToIndex(entity, superClasses);
  }
  
  public void addDirectSubClassesToIndex(RDFEntity entity, RDFEntityList subClasses)
  {
    this.metadataIndexManager.addDirectSubClassesToIndex(entity, subClasses);
  }
  
  public void addDirectClassesToIndex(RDFEntity entity, RDFEntityList subClasses)
  {
    this.metadataIndexManager.addDirectClassesToIndex(entity, subClasses);
  }
  
  public void addEquivalentEntitiesToIndex(RDFEntity entity, RDFEntityList classes)
  {
    this.metadataIndexManager.addEquivalentEntitiesToIndex(entity, classes);
  }
  
  public void addWNSynsetsToIndex(RDFEntity entity, WNSynsetSetBean wnSynsetSetBean)
  {
    this.metadataIndexManager.addWNSynsetsToIndex(entity, wnSynsetSetBean);
  }
  
  public void addOntologyToIndex(String ontologyID)
  {
    this.metadataIndexManager.addOntologyToIndex(ontologyID, this.indexBean.getMetadata_index_db());
  }
  
  public static EntityMappingTable multiSearchEntityMappingsOnOntology(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    EntityMappingTable res = multiSearch(getOntologyMultiSearcher(), keyword, semanticRelation, thresh, searchType);
    return res;
  }
  
  public static EntityMappingTable multiSearchEntityMappingsOnKnowledgeBase(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    EntityMappingTable res = multiSearch(getKbMultiSearcher(), keyword, semanticRelation, thresh, searchType);
    return res;
  }
  
  public static EntityMappingTable multiSearchEntityMappings(String keyword, String semanticRelation, float thresh_ont, float tresh_kb, int searchType)
    throws Exception
  {
    EntityMappingTable table = multiSearchEntityMappingsOnOntology(keyword, semanticRelation, thresh_ont, searchType);
    if (semanticRelation.equals("equivalentMatching"))
    {
      EntityMappingTable tableaux = multiSearchEntityMappingsOnKnowledgeBase(keyword, semanticRelation, tresh_kb, searchType);
      
      table.merge(tableaux);
    }
    return table;
  }
  
  public static EntityMappingTable multiSearchEntityMappings(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    MultiSearcher searcher = null;
    if (semanticRelation.equals("equivalentMatching"))
    {
      EntityMappingTable res = multiSearch(globalMultiSearcher, keyword, semanticRelation, thresh, searchType);
      return res;
    }
    return multiSearchEntityMappingsOnOntology(keyword, semanticRelation, thresh, searchType);
  }
  
  public static EntityMappingTable multiSearchEntityMappings(String keyword, String semanticRelation, float thresh, int searchType, boolean queryOntoOnly)
    throws Exception
  {
    if (semanticRelation.equals("equivalentMatching"))
    {
      EntityMappingTable res = multiSearch(globalMultiSearcher, keyword, semanticRelation, thresh, searchType);
      return res;
    }
    if (!queryOntoOnly)
    {
      EntityMappingTable res = multiSearchEntityMappingsOnOntology(keyword, semanticRelation, thresh, searchType);
      if (thresh < SyntacticComponent.LEXICALY_RELATED_THRESH_SYN) {
        res.merge(multiSearchEntityMappingsOnKnowledgeBase(keyword, semanticRelation, SyntacticComponent.LEXICALY_RELATED_THRESH_SYN, searchType));
      } else {
        res.merge(multiSearchEntityMappingsOnKnowledgeBase(keyword, semanticRelation, thresh, searchType));
      }
      return res;
    }
    return multiSearchEntityMappingsOnOntology(keyword, semanticRelation, thresh, searchType);
  }
  
  private static EntityMappingTable multiSearch(Searcher searcher, String keyword, String semanticRelation, float thresh, int searchType)
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    try
    {
      switch (searchType)
      {
      case 2: 
        entityMappingTable.addMappingList(search(keyword, semanticRelation, thresh, 2, searcher));
        
        break;
      case 4: 
        entityMappingTable.addMappingList(search(keyword, semanticRelation, thresh, 4, searcher));
        
        break;
      case 8: 
        for (IndexManagerLucene index : luceneIndexList) {
          entityMappingTable.addMappingList(index.spellSearchSemanticEntity(keyword, semanticRelation, thresh));
        }
        break;
      default: 
        System.out.println("This type of search is not alowed " + searchType);
        System.out.println("Allowed types STANDARD 2 FUZZY 4 SPELL CHECKER 8");
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return entityMappingTable;
  }
  
  public EntityMappingTable searchEntityMappingsOnOntology(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    return searchEntityMappings(this.ontologySearcher, keyword, semanticRelation, thresh, searchType);
  }
  
  public EntityMappingTable searchEntityMappingsOnKnowledgeBase(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    return searchEntityMappings(this.kbSearcher, keyword, semanticRelation, thresh, searchType);
  }
  
  private EntityMappingTable searchEntityMappings(Searcher searcher, String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    switch (searchType)
    {
    case 2: 
      entityMappingTable.addMappingList(search(keyword, semanticRelation, thresh, 2, searcher));
      
      break;
    case 4: 
      entityMappingTable.addMappingList(search(keyword, semanticRelation, thresh, 4, searcher));
      
      break;
    case 8: 
      entityMappingTable.addMappingList(spellSearchSemanticEntity(keyword, semanticRelation, thresh));
      
      break;
    default: 
      System.out.println("This type of search is not allowed " + searchType);
      System.out.println("Allowed types STANDARD 2 FUZZY 4 SPELL CHECKER 8");
    }
    return entityMappingTable;
  }
  
  public ArrayList<SearchSemanticResult> spellSearchSemanticEntity(String keyword, String semanticRelation, float thresh)
    throws Exception
  {
    if (this.spellChecker == null)
    {
      System.out.println("Please set up the spell checker");
      return null;
    }
    ArrayList<String> suggestedWords = suggestSimilar(keyword, 1);
    ArrayList<SearchSemanticResult> suggestedResults = new ArrayList();
    for (String word : suggestedWords)
    {
      suggestedResults.addAll(search(word, semanticRelation, thresh, 2, this.multiSearcher));
      System.out.println(suggestedResults.size() + " total matching documents for suggested word by the spell checker " + word);
    }
    return suggestedResults;
  }
  
  public RDFEntityList searchAllSuperClasses(RDFEntity entity)
  {
    return this.metadataIndexManager.getAllSuperClasses(entity);
  }
  
  public RDFEntityList searchAllSubClasses(RDFEntity entity)
  {
    return this.metadataIndexManager.getAllSubClasses(entity);
  }
  
  public RDFEntityList searchDirectSuperClasses(RDFEntity entity)
  {
    return this.metadataIndexManager.getDirectSuperClasses(entity);
  }
  
  public RDFEntityList searchDirectSubClasses(RDFEntity entity)
  {
    return this.metadataIndexManager.getDirectSubClasses(entity);
  }
  
  public RDFEntityList searchDirectClassOfInstance(RDFEntity entity)
  {
    return this.metadataIndexManager.getDirectClassOfInstance(entity);
  }
  
  public RDFEntityList searchEquivalentEntities(RDFEntity entity)
  {
    return this.metadataIndexManager.getEquivalentEntities(entity);
  }
  
  public boolean isSynsetIndex()
  {
    return this.metadataIndexManager.isIsSynsetIndexed();
  }
  
  public WNSynsetSetBean searchSynsets(RDFEntity entity)
  {
    return this.metadataIndexManager.getAllSynsets(entity);
  }
  
  public Searcher getSearcherClassesAndProperties()
  {
    return this.ontologySearcher;
  }
  
  public Searcher getSearcherInstances()
  {
    return this.kbSearcher;
  }
  
  public void closeIndex()
    throws Exception
  {
    LuceneService.closeIndex(this.indexWritter);
    LuceneService.closeIndex(this.indexWritterInstances);
    this.metadataIndexManager.close();
  }
  
  private Searcher getOntologySearcher()
    throws Exception
  {
    return this.ontologySearcher;
  }
  
  private Searcher getKnowledgeBaseSearcher()
    throws Exception
  {
    return this.kbSearcher;
  }
  
  public static ArrayList<SearchSemanticResult> search(String keyword, String semanticRelation, float thresh, int searchType, Searcher searcher)
    throws Exception
  {
    String pos_keyword = keyword.toLowerCase();
    if ((searchType == 2) && (StringUtils.isCompound(keyword)))
    {
      if (keyword.indexOf("\"") < 0) {
        pos_keyword = "\"" + pos_keyword + "\"";
      }
    }
    else if ((!StringUtils.isCompound(keyword)) && (keyword.startsWith("'"))) {
      pos_keyword = pos_keyword.replaceAll("'", "");
    } else if ((!StringUtils.isCompound(keyword)) && (keyword.startsWith("\""))) {
      pos_keyword = pos_keyword.replaceAll("\"", "");
    }
    long startTime = System.currentTimeMillis();
    
    MyHitCollector collector = new MyHitCollector(LIMIT_HITS, thresh);
    try
    {
      switch (searchType)
      {
      case 2: 
        getSemanticEntityHits(pos_keyword, searcher, collector); break;
      case 4: 
        getfuzzySemanticEntityHits(pos_keyword, searcher, collector); break;
      default: 
        System.out.println("The type of search is not allowed v2");
      }
    }
    catch (MyHitCollector.LimitExceeded ex)
    {
      System.out.println("Limit exceed " + LIMIT_HITS);
    }
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    
    System.out.println(collector.getTotalHits() + " matching documents for " + keyword + " in " + duration);
    
    return getResultsFromCollector(searcher, collector, semanticRelation, keyword);
  }
  
  private Document getDocumentFormSemanticEntity(RDFEntity SemEntity)
    throws IOException
  {
    if (SemEntity == null) {
      return null;
    }
    if (SemEntity.getURI() == null) {
      return null;
    }
    if (!SemEntity.isIndexable()) {
      return null;
    }
    Document doc = new Document();
    
    doc.add(new Field("URI", SemEntity.getURI(), Field.Store.YES, Field.Index.NO));
    doc.add(new Field("entityType", SemEntity.getType(), Field.Store.YES, Field.Index.NO));
    doc.add(new Field("entityLabel", SemEntity.getLabel(), Field.Store.YES, Field.Index.NO));
    doc.add(new Field("pluginID", SemEntity.getIdPlugin(), Field.Store.YES, Field.Index.NO));
    doc.add(new Field("indexField", SemEntity.getIndexLabels(), Field.Store.NO, Field.Index.TOKENIZED));
    return doc;
  }
  
  private Document getDocumentFromSemanticLiteral(RDFEntity relatedInstance, RDFEntity literal)
    throws IOException
  {
    if ((relatedInstance == null) || (literal == null)) {
      return null;
    }
    if (relatedInstance.getURI() == null) {
      return null;
    }
    if ((!relatedInstance.isIndexable()) || (!literal.isIndexable())) {
      return null;
    }
    Document doc = new Document();
    doc.add(new Field("URI", relatedInstance.getURI(), Field.Store.YES, Field.Index.NO));
    doc.add(new Field("entityType", literal.getType(), Field.Store.YES, Field.Index.NO));
    doc.add(new Field("entityLabel", literal.getLabel(), Field.Store.YES, Field.Index.NO));
    doc.add(new Field("pluginID", literal.getIdPlugin(), Field.Store.YES, Field.Index.NO));
    doc.add(new Field("indexField", literal.getIndexLabels(), Field.Store.NO, Field.Index.TOKENIZED));
    return doc;
  }
  
  private static void getSemanticEntityHits(String keyword, Searcher searcher, MyHitCollector collector)
    throws Exception
  {
    QueryParser queryParser = new QueryParser("indexField", new StandardAnalyzer());
    Query query = queryParser.parse(keyword.trim());
    
    searcher.search(query, collector);
  }
  
  private static void getfuzzySemanticEntityHits(String keyword, Searcher searcher, MyHitCollector collector)
    throws Exception
  {
    FuzzyQuery fuzzyQuery = fuzzyQuery("indexField", keyword.trim());
    searcher.search(fuzzyQuery, collector);
  }
  
  private ArrayList<String> suggestSimilar(String keyword, int maxSuggestions)
    throws Exception
  {
    if ((this.spellChecker == null) && (this.spellCheckerInstances == null))
    {
      System.out.println("Please set up the spell checker");
      return null;
    }
    ArrayList<String> suggestedWords = new ArrayList();
    this.spellChecker.setAccuracy(new Float(1.0F).floatValue());
    String[] wordsOntology = this.spellChecker.suggestSimilar(keyword, maxSuggestions);
    String[] wordsKnowledgeBase = this.spellCheckerInstances.suggestSimilar(keyword, maxSuggestions);
    for (int i = 0; i < wordsOntology.length; i++)
    {
      System.out.println("SUGGESTED WORDS BY ONTOLOGY " + wordsOntology[i]);
      suggestedWords.add(wordsOntology[i]);
    }
    for (int i = 0; i < wordsKnowledgeBase.length; i++)
    {
      System.out.println("SUGGESTED WORDS BY KB " + wordsKnowledgeBase[i]);
      suggestedWords.add(wordsKnowledgeBase[i]);
    }
    return suggestedWords;
  }
  
  private static FuzzyQuery fuzzyQuery(String field, String key)
  {
    FuzzyQuery result = new FuzzyQuery(new Term(field, key), 0.7F, 3);
    
    result.setBoost(0.5F);
    return result;
  }
  
  private static ArrayList<SearchSemanticResult> getResultsFromCollector(Searcher searcher, MyHitCollector collector, String semanticRelation, String keyword)
    throws Exception
  {
    ArrayList<SearchSemanticResult> semanticResults = new ArrayList();
    
    Hashtable<String, Integer> NumHitsPerPlugin = new Hashtable();
    Hashtable<String, Float> ScPerPlugin = new Hashtable();
    Hashtable<String, Integer> eliminatedPerPlugin = new Hashtable();
    
    ArrayList<String> semanticResults_uri = new ArrayList();
    int index = 0;
    try
    {
      ScoreDoc[] hits = collector.topDocs().scoreDocs;
      for (ScoreDoc hit : hits)
      {
        int i = hit.doc;
        Document doc = searcher.doc(i);
        float score = hit.score;
        if (index >= LIMIT_SORTED_HITS)
        {
          System.out.println("Truncating the number of ordered hits to " + LIMIT_SORTED_HITS);
          break;
        }
        String uri = doc.get("URI");
        String label = doc.get("entityLabel");
        String type = doc.get("entityType");
        String idPlugin = doc.get("pluginID");
        
        Integer eliminated = (Integer)eliminatedPerPlugin.get(idPlugin);
        boolean add;
        boolean add;
        if (eliminated != null)
        {
          eliminatedPerPlugin.put(idPlugin, Integer.valueOf(eliminated.intValue() + 1));
          add = false;
        }
        else
        {
          boolean add;
          if (!NumHitsPerPlugin.containsKey(idPlugin))
          {
            NumHitsPerPlugin.put(idPlugin, Integer.valueOf(1));
            ScPerPlugin.put(idPlugin, Float.valueOf(score));
            add = true;
          }
          else
          {
            Float max_sc_plugin = (Float)ScPerPlugin.get(idPlugin);
            Integer num_plugin = (Integer)NumHitsPerPlugin.get(idPlugin);
            if (((score < max_sc_plugin.floatValue()) && (num_plugin.intValue() > LIMIT_PER_ONTO)) || (num_plugin.intValue() > ABSOLUTE_LIMIT_PER_ONTO))
            {
              System.out.println("LIMIT REACH FOR " + idPlugin + " with hits " + num_plugin);
              boolean add = false;
              eliminatedPerPlugin.put(idPlugin, Integer.valueOf(1));
            }
            else
            {
              add = true;
              NumHitsPerPlugin.put(idPlugin, Integer.valueOf(num_plugin.intValue() + 1));
            }
          }
        }
        index += 1;
        if ((add) && (!semanticResults_uri.contains(idPlugin + uri + type)) && (!NOT_RDF_PROPERTIES.contains(uri)))
        {
          semanticResults_uri.add(idPlugin + uri + type);
          SearchSemanticResult result;
          SearchSemanticResult result;
          if (semanticRelation.equals("equivalentMatching")) {
            result = new SearchSemanticResult(new RDFEntity(type, uri, label, idPlugin), score, semanticRelation);
          } else {
            result = new SearchSemanticResult(new RDFEntity(type, uri, label, idPlugin), score, semanticRelation, keyword);
          }
          semanticResults.add(result);
        }
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    for (String idPlug : eliminatedPerPlugin.keySet()) {
      System.out.println("eliminated: " + idPlug + " : " + eliminatedPerPlugin.get(idPlug));
    }
    return semanticResults;
  }
  
  public String getIndexGlobal_path()
  {
    return this.indexGlobal_path;
  }
  
  public static MultiSearcher getOntologyMultiSearcher()
  {
    return ontologyMultiSearcher;
  }
  
  public static MultiSearcher getKbMultiSearcher()
  {
    return kbMultiSearcher;
  }
  
  public static void main(String[] args)
    throws Exception
  {}
}

