package poweraqua.indexingService.manager;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import poweraqua.core.utils.StringUtils;
import poweraqua.indexingService.creator.MultiIndexServiceConfiguration;
import poweraqua.indexingService.manager.virtuoso.IndexManagerVirtuoso;
import poweraqua.indexingService.manager.virtuoso.VirtuosoServiceConfiguration;
import poweraqua.indexingService.manager.virtuoso.virtuosohelpers.ServerConfigVirtuoso;
import poweraqua.indexingService.manager.watson.IndexManagerWatson;
import poweraqua.powermap.elementPhase.EntityMappingTable;
import poweraquaDB.DataBaseManager;
import poweraquaDB.DatabaseConector;

public class MultiIndexManager
{
  private static ArrayList<IndexManager> indexList;
  private DataBaseManager dbManager;
  private IndexManagerWatson indexManagerWatson;
  private IndexManagerVirtuoso indexManagerVirtuoso;
  private boolean useWatson;
  private boolean usePowerMap;
  private boolean useVirtuoso;
  private boolean remoteSPARQLVirtuoso;
  private String indexPath = "";
  
  public MultiIndexManager()
    throws Exception
  {
    indexList = new ArrayList();
    IndexServiceConfiguration indexServiceConfiguration = new IndexServiceConfiguration();
    indexServiceConfiguration.readConfigurationFile();
    MultiIndexServiceConfiguration multiIndexServiceConfiguration = new MultiIndexServiceConfiguration();
    this.useWatson = multiIndexServiceConfiguration.isWatson();
    this.usePowerMap = multiIndexServiceConfiguration.isPowermap();
    this.useVirtuoso = multiIndexServiceConfiguration.isVirtuoso();
    this.remoteSPARQLVirtuoso = multiIndexServiceConfiguration.isRemoteSPARQLVirtuoso();
    System.out.println("(1): usePowerMap " + this.usePowerMap + " useVirtuoso " + this.useVirtuoso);
    if (this.usePowerMap)
    {
      this.dbManager = DatabaseConector.openDbConexion(multiIndexServiceConfiguration.getDb(), multiIndexServiceConfiguration.getLogin(), multiIndexServiceConfiguration.getPassword());
      
      System.out.println("OPENING THE INDEXES ... ");
      for (IndexBean indexBean : indexServiceConfiguration.getIndexList())
      {
        IndexManagerLucene indexManager = new IndexManagerLucene(indexBean, multiIndexServiceConfiguration.getIndex_global_path());
        indexManager.openMetadataIndexForQueries();
        indexManager.openSpellCheckerFromIndex();
        indexList.add(indexManager);
      }
      IndexManagerLucene.initializeGlobalSearchers();
    }
    else
    {
      System.out.println("PowerMap not in use (1)");
    }
    if (this.useWatson) {
      this.indexManagerWatson = new IndexManagerWatson();
    }
    if (this.useVirtuoso)
    {
      System.out.println("calling virtuoso index manager (1)");
      
      VirtuosoServiceConfiguration virtuosoConfiguration = new VirtuosoServiceConfiguration();
      for (ServerConfigVirtuoso serverConfigVirtuoso : virtuosoConfiguration.getServerConfigVirtuosoList()) {
        this.indexManagerVirtuoso = new IndexManagerVirtuoso(serverConfigVirtuoso.getGraphVirtuosoList(), this.remoteSPARQLVirtuoso);
      }
    }
  }
  
  public MultiIndexManager(String indexPath)
    throws Exception
  {
    this.indexPath = indexPath;
    IndexServiceConfiguration indexServiceConfiguration = new IndexServiceConfiguration();
    indexServiceConfiguration.readConfigurationFile(indexPath);
    MultiIndexServiceConfiguration multiIndexServiceConfiguration = new MultiIndexServiceConfiguration(indexPath);
    this.useWatson = multiIndexServiceConfiguration.isWatson();
    this.usePowerMap = multiIndexServiceConfiguration.isPowermap();
    this.useVirtuoso = multiIndexServiceConfiguration.isVirtuoso();
    this.remoteSPARQLVirtuoso = multiIndexServiceConfiguration.isRemoteSPARQLVirtuoso();
    System.out.println("(2): usePowerMap " + this.usePowerMap + " useVirtuoso " + this.useVirtuoso);
    if (this.usePowerMap)
    {
      this.dbManager = DatabaseConector.openDbConexion(multiIndexServiceConfiguration.getDb(), multiIndexServiceConfiguration.getLogin(), multiIndexServiceConfiguration.getPassword());
      
      indexList = new ArrayList();
      for (IndexBean indexBean : indexServiceConfiguration.getIndexList())
      {
        IndexManagerLucene indexManager = new IndexManagerLucene(indexBean, multiIndexServiceConfiguration.getIndex_global_path());
        indexManager.openMetadataIndexForQueries();
        indexManager.openSpellCheckerFromIndex();
        indexList.add(indexManager);
      }
      IndexManagerLucene.initializeGlobalSearchers();
    }
    else
    {
      System.out.println("PowerMap not in use (2)");
    }
    if (this.useWatson) {
      this.indexManagerWatson = new IndexManagerWatson();
    }
    if (this.useVirtuoso)
    {
      System.out.println("calling virtuoso index manager (2)");
      VirtuosoServiceConfiguration virtuosoConfiguration = new VirtuosoServiceConfiguration(indexPath);
      for (ServerConfigVirtuoso serverConfigVirtuoso : virtuosoConfiguration.getServerConfigVirtuosoList()) {
        this.indexManagerVirtuoso = new IndexManagerVirtuoso(serverConfigVirtuoso.getGraphVirtuosoList(), this.remoteSPARQLVirtuoso);
      }
    }
  }
  
  public MultiIndexManager(String indexPath, boolean useWatson)
    throws Exception
  {
    this.indexPath = indexPath;
    IndexServiceConfiguration indexServiceConfiguration = new IndexServiceConfiguration();
    indexServiceConfiguration.readConfigurationFile(indexPath);
    MultiIndexServiceConfiguration multiIndexServiceConfiguration = new MultiIndexServiceConfiguration(indexPath);
    this.usePowerMap = multiIndexServiceConfiguration.isPowermap();
    this.useVirtuoso = multiIndexServiceConfiguration.isVirtuoso();
    this.remoteSPARQLVirtuoso = multiIndexServiceConfiguration.isRemoteSPARQLVirtuoso();
    System.out.println("(3): usePowerMap " + this.usePowerMap + " useVirtuoso " + this.useVirtuoso);
    if (this.usePowerMap)
    {
      this.dbManager = DatabaseConector.openDbConexion(multiIndexServiceConfiguration.getDb(), multiIndexServiceConfiguration.getLogin(), multiIndexServiceConfiguration.getPassword());
      
      indexList = new ArrayList();
      for (IndexBean indexBean : indexServiceConfiguration.getIndexList())
      {
        IndexManagerLucene indexManager = new IndexManagerLucene(indexBean, multiIndexServiceConfiguration.getIndex_global_path());
        indexManager.openMetadataIndexForQueries();
        indexManager.openSpellCheckerFromIndex();
        indexList.add(indexManager);
      }
      IndexManagerLucene.initializeGlobalSearchers();
    }
    else
    {
      System.out.println("PowerMap not in use (0)");
    }
    this.useWatson = useWatson;
    if (this.useWatson) {
      this.indexManagerWatson = new IndexManagerWatson();
    }
    if (this.useVirtuoso)
    {
      System.out.println("calling virtuoso index manager (0)");
      
      VirtuosoServiceConfiguration virtuosoConfiguration = new VirtuosoServiceConfiguration(indexPath);
      for (ServerConfigVirtuoso serverConfigVirtuoso : virtuosoConfiguration.getServerConfigVirtuosoList()) {
        this.indexManagerVirtuoso = new IndexManagerVirtuoso(serverConfigVirtuoso.getGraphVirtuosoList(), this.remoteSPARQLVirtuoso);
      }
    }
  }
  
  public void setWatson(boolean useWatson)
    throws Exception
  {
    if ((useWatson) && (isUseWatson()))
    {
      System.out.println("Watson already launched");
    }
    else if ((useWatson) && (!this.useWatson))
    {
      System.out.println("Initializing Watson ...");
      this.useWatson = true;
      this.indexManagerWatson = new IndexManagerWatson();
    }
    else if ((!useWatson) && (isUseWatson()))
    {
      System.out.println("Unloading Watson");
      this.useWatson = false;
    }
  }
  
  public EntityMappingTable searchEntityMappings(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    EntityMappingTable table = new EntityMappingTable(keyword);
    if (isUsePowerMap()) {
      table = IndexManagerLucene.multiSearchEntityMappings(keyword, semanticRelation, thresh, searchType);
    }
    if (isUseWatson()) {
      if (searchType == 2) {
        if ((semanticRelation.equals("equivalentMatching")) || (semanticRelation.equals("synonym")))
        {
          table.merge(getIndexManagerWatson().searchRankEntityMappingsOnOntology(keyword, semanticRelation, thresh, searchType));
          table.merge(getIndexManagerWatson().searchRankEntityMappingsOnKnowledgeBase(keyword, semanticRelation, thresh, searchType));
        }
        else
        {
          table.merge(getIndexManagerWatson().searchRankEntityMappingsOnOntology(keyword, semanticRelation, thresh, searchType));
        }
      }
    }
    if ((this.useVirtuoso) && (searchType == 2)) {
      System.out.println("calling virtuoso index manager (3)");
    }
    return table;
  }
  
  public EntityMappingTable searchEntityMappingsonKnowledgeBase(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    EntityMappingTable table = new EntityMappingTable(keyword);
    if (isUsePowerMap()) {
      table = IndexManagerLucene.multiSearchEntityMappingsOnKnowledgeBase(keyword, semanticRelation, thresh, searchType);
    }
    if (this.useWatson) {
      table.merge(getIndexManagerWatson().searchRankEntityMappingsOnKnowledgeBase(keyword, semanticRelation, thresh, searchType));
    }
    if ((this.useVirtuoso) && (this.indexManagerVirtuoso.getGraphList().size() > 1))
    {
      System.out.println("calling virtuoso index manager (7)");
      table.merge(this.indexManagerVirtuoso.searchInstancesMappingsOnKnowledgeBase(keyword, semanticRelation, IndexManagerVirtuoso.LIMIT_FUSION));
    }
    return table;
  }
  
  public EntityMappingTable searchEntityMappings(String keyword, String semanticRelation, float thresh_onto, float thresh_kb, int searchType, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    if (searchType == 8) {
      throw new IllegalArgumentException("SPELL SEARCH type not avaliable with two different thresholds");
    }
    EntityMappingTable table = new EntityMappingTable(keyword);
    if (isUsePowerMap()) {
      table = IndexManagerLucene.multiSearchEntityMappings(keyword, semanticRelation, thresh_onto, thresh_kb, searchType);
    }
    if (isUseWatson()) {
      if (searchType == 2) {
        if ((semanticRelation.equals("equivalentMatching")) || (semanticRelation.equals("synonym")))
        {
          if (restrictedKeywords.isEmpty())
          {
            table.merge(getIndexManagerWatson().searchRankEntityMappingsOnOntology(keyword, semanticRelation, thresh_onto, searchType));
            table.merge(getIndexManagerWatson().searchRankEntityMappingsOnKnowledgeBase(keyword, semanticRelation, thresh_kb, searchType));
          }
          else
          {
            table.merge(getIndexManagerWatson().searchEntityMappingsRestrictedByCoverage(keyword, semanticRelation, restrictedKeywords, this.indexPath));
          }
        }
        else {
          System.out.println("type of search not performed in Watson for (only exact or synonyms) " + keyword);
        }
      }
    }
    if ((this.useVirtuoso) && (searchType == 2))
    {
      System.out.println("calling virtuoso index manager (4)");
      
      table.merge(this.indexManagerVirtuoso.multiSearchEntityMappings(keyword, semanticRelation));
    }
    return table;
  }
  
  public EntityMappingTable searchEntityMappings(String keyword, ArrayList<String> synonyms, String semanticRelation, float thresh, int searchType, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    for (String synonym : synonyms)
    {
      if (isUsePowerMap())
      {
        EntityMappingTable aux = IndexManagerLucene.multiSearchEntityMappings(synonym, semanticRelation, thresh, searchType);
        if (!aux.isEmpty())
        {
          aux.filterExactStringsByOntology(synonym);
          entityMappingTable.merge(aux);
        }
      }
      if ((isUseWatson()) && (searchType == 2) && 
        (semanticRelation.equals("synonym"))) {
        if (restrictedKeywords.isEmpty())
        {
          entityMappingTable.merge(getIndexManagerWatson().searchRankEntityMappingsOnOntology(synonym, semanticRelation, thresh, searchType));
          entityMappingTable.merge(getIndexManagerWatson().searchRankEntityMappingsOnKnowledgeBase(synonym, semanticRelation, thresh, searchType));
        }
        else
        {
          entityMappingTable.merge(getIndexManagerWatson().searchEntityMappingsRestrictedByCoverage(synonym, semanticRelation, restrictedKeywords, this.indexPath));
        }
      }
      if ((this.useVirtuoso) && (searchType == 2))
      {
        System.out.println("calling virtuoso index manager (5) for " + synonym);
        
        EntityMappingTable aux = this.indexManagerVirtuoso.multiSearchEntityMappings(synonym, semanticRelation);
        if (!aux.isEmpty())
        {
          aux.filterExactStringsByOntology(synonym);
          entityMappingTable.merge(aux);
        }
      }
    }
    return entityMappingTable;
  }
  
  public EntityMappingTable searchEntityMappings(String keyword, ArrayList<String> synonyms, String semanticRelation, float thresh, int searchType, boolean isqueryterm, boolean WNet, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    for (String synonym : synonyms)
    {
      if (isUsePowerMap())
      {
        EntityMappingTable aux = IndexManagerLucene.multiSearchEntityMappings(synonym, semanticRelation, thresh, searchType, isqueryterm);
        if (!aux.isEmpty())
        {
          if (!isqueryterm) {
            aux.filterExactStringsByOntology(synonym, WNet);
          } else {
            aux.filterExactStringsByOntology(synonym);
          }
          entityMappingTable.merge(aux);
        }
      }
      if ((isUseWatson()) && (searchType == 2) && 
        (semanticRelation.equals("synonym"))) {
        if (restrictedKeywords.isEmpty())
        {
          entityMappingTable.merge(getIndexManagerWatson().searchRankEntityMappingsOnOntology(synonym, semanticRelation, thresh, searchType, WNet));
          entityMappingTable.merge(getIndexManagerWatson().searchRankEntityMappingsOnKnowledgeBase(synonym, semanticRelation, thresh, searchType));
        }
        else
        {
          entityMappingTable.merge(getIndexManagerWatson().searchEntityMappingsRestrictedByCoverage(synonym, semanticRelation, restrictedKeywords, this.indexPath));
        }
      }
      if ((this.useVirtuoso) && (searchType == 2))
      {
        System.out.println("calling virtuoso index manager (6) for " + synonym);
        
        EntityMappingTable aux = this.indexManagerVirtuoso.multiSearchEntityMappings(synonym, semanticRelation, isqueryterm);
        if (!aux.isEmpty())
        {
          aux.filterExactStringsByOntology(synonym);
          entityMappingTable.merge(aux);
        }
      }
    }
    return entityMappingTable;
  }
  
  private long getIndexId(String ontologyId)
  {
    String query = "SELECT indexManagerId from ontologyindextable where ontologyId = " + StringUtils.generateStringId(ontologyId) + ";";
    
    ResultSet set = this.dbManager.executeSelect(query);
    try
    {
      if (set.next()) {
        return set.getLong("indexManagerId");
      }
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
    return 0L;
  }
  
  public IndexManager findOntologIndex(String ontologyId)
  {
    long indexId = getIndexId(ontologyId);
    if (indexId == 0L) {
      return null;
    }
    for (IndexManager im : indexList) {
      if (indexId == StringUtils.generateStringId(im.getId())) {
        return im;
      }
    }
    return null;
  }
  
  public void filterIndex(IndexManager im)
  {
    System.out.println("Selecting only the index manager " + im.getId() + " from a total of " + indexList.size() + " indexes");
    ArrayList<IndexManager> new_indexList = new ArrayList();
    new_indexList.add(im);
    indexList = new_indexList;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    try
    {
      MultiIndexManager mIM = new MultiIndexManager();
      mIM.searchEntityMappings("person", "equivalentMatching", new Float(0.7D).floatValue(), new Float(0.7D).floatValue(), 2, new ArrayList());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public IndexManagerWatson getIndexManagerWatson()
  {
    return this.indexManagerWatson;
  }
  
  public IndexManagerVirtuoso getIndexManagerVirtuoso()
  {
    return this.indexManagerVirtuoso;
  }
  
  public boolean isUseWatson()
  {
    return this.useWatson;
  }
  
  public boolean isUsePowerMap()
  {
    return this.usePowerMap;
  }
  
  public boolean isUseVirtuoso()
  {
    return this.useVirtuoso;
  }
  
  public void setUseVirtuoso(boolean useVirtuoso)
  {
    this.useVirtuoso = useVirtuoso;
  }
}

