package poweraqua.indexingService.manager;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import net.sf.extjwnl.data.Synset;
import poweraqua.WordNetJWNL.WNSynsetBean;
import poweraqua.WordNetJWNL.WNSynsetSetBean;
import poweraqua.WordNetJWNL.WordNet;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.utils.StringUtils;
import poweraquaDB.DataBaseManager;
import poweraquaDB.SQLConnectionBean;
import poweraquaDB.SQLConnectionsPool;

public class MetadataIndexManager
{
  SQLConnectionsPool conectionsMetadataPool = new SQLConnectionsPool();
  private String metadataTableName;
  private SQLConnectionBean connectionBean;
  private boolean isSynsetIndexed = false;
  public static int max_number_rows = 7000;
  
  public MetadataIndexManager(String dbURL, String dbLogin, String dbPassword, String metadataTableName, boolean create)
  {
    try
    {
      this.connectionBean = this.conectionsMetadataPool.stablishConnexion(dbURL, dbLogin, dbPassword);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return;
    }
    this.metadataTableName = metadataTableName;
    if (create)
    {
      createTable(getTableDirectClasses());
      createTable(getTableDirectSubClasses());
      createTable(getTableDirectSuperClasses());
      createTable(getTableSubClasses());
      createTable(getTableSuperClasses());
      createTable(getTableEquivalent());
      if (this.isSynsetIndexed) {
        createSynsetTable(getTableSynsets());
      }
    }
    else
    {
      this.isSynsetIndexed = existsTable(getTableSynsets());
    }
  }
  
  public MetadataIndexManager(String dbURL, String dbLogin, String dbPassword, String metadataTableName, boolean create, boolean synsetToIndex)
  {
    try
    {
      this.connectionBean = this.conectionsMetadataPool.stablishConnexion(dbURL, dbLogin, dbPassword);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return;
    }
    this.metadataTableName = metadataTableName;
    if (create)
    {
      createTable(getTableDirectClasses());
      createTable(getTableDirectSubClasses());
      createTable(getTableDirectSuperClasses());
      createTable(getTableSubClasses());
      createTable(getTableSuperClasses());
      createTable(getTableEquivalent());
      if (synsetToIndex) {
        createSynsetTable(getTableSynsets());
      }
    }
    else
    {
      this.isSynsetIndexed = existsTable(getTableSynsets());
    }
  }
  
  private boolean existsTable(String tableName)
  {
    String exists = "Show tables like '" + tableName + "'";
    ResultSet results = this.connectionBean.getDbManager().executeSelect(exists);
    try
    {
      if (results.next()) {
        return true;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();System.out.println("exist Table: " + exists);
      return false;
    }
    return false;
  }
  
  private void createTable(String tableName)
  {
    try
    {
      String dropTable = "DROP TABLE  " + tableName;
      this.connectionBean.getDbManager().executeDropTable(dropTable);
      
      String createTable = "CREATE TABLE " + tableName + " (" + "id INT NOT NULL AUTO_INCREMENT, " + "ontologyId BIGINT, " + "entityURI  CHAR(255), " + "classURI CHAR(255), " + "classLabel CHAR(255), " + "PRIMARY KEY(id) ) ";
      
      this.connectionBean.getDbManager().executeCreateTable(createTable);
      
      String createIndex = "CREATE INDEX entity_index ON " + tableName + "(ontologyId, entityURI)";
      this.connectionBean.getDbManager().executeCreateIndex(createIndex);
      
      System.out.println("metadata tables created .. ");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  private void createSynsetTable(String tableName)
  {
    try
    {
      String dropTable = "DROP TABLE  " + tableName;
      this.connectionBean.getDbManager().executeDropTable(dropTable);
      
      String createTable = "CREATE TABLE " + tableName + " (" + "id INT NOT NULL AUTO_INCREMENT, " + "ontologyId BIGINT, " + "entityURI  CHAR(255), " + "synsetID CHAR(255), " + "similarity FLOAT, " + "isgloss BOOLEAN, " + "PRIMARY KEY(id) ) ";
      
      this.connectionBean.getDbManager().executeCreateTable(createTable);
      
      String createIndex = "CREATE INDEX entity_index ON " + tableName + "(ontologyId, entityURI)";
      this.connectionBean.getDbManager().executeCreateIndex(createIndex);
      
      System.out.println("metadata tables created .. ");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  private void addInformationToIndex(String tableName, RDFEntity entity, RDFEntityList entityList)
  {
    String query = new String();
    long idOntology = StringUtils.generateStringId(entity.getIdPlugin());
    for (RDFEntity asociatedClass : entityList.getAllRDFEntities())
    {
      String entityURI = entity.getURI();
      String asociatedClassURI = asociatedClass.getURI();
      String asociatedClassLabel = asociatedClass.getLabel();
      if (entityURI.indexOf("'") > 0) {
        entityURI = replacecharacters(entityURI);
      }
      if (asociatedClassURI.indexOf("'") > 0) {
        asociatedClassURI = replacecharacters(asociatedClassURI);
      }
      if (asociatedClassLabel.indexOf("'") > 0) {
        asociatedClassLabel = replacecharacters(asociatedClassLabel);
      }
      if (asociatedClassLabel.length() > 40) {
        asociatedClassLabel = asociatedClassLabel.substring(0, 40);
      }
      query = "insert into " + tableName + "(ontologyId, entityURI, classURI, classLabel) values (" + idOntology + ", '" + entityURI + "', '" + asociatedClassURI + "', '" + asociatedClassLabel + "')";
      
      this.connectionBean.getDbManager().executeInsert(query);
    }
  }
  
  private void addSynsetsToIndex(String tableName, RDFEntity entity, WNSynsetSetBean wnSynsetSetBean)
  {
    String query = new String();
    long idOntology = StringUtils.generateStringId(entity.getIdPlugin());
    String entityURI = entity.getURI();
    if (entityURI.indexOf("'") > 0) {
      entityURI = replacecharacters(entityURI);
    }
    for (String keyID : wnSynsetSetBean.getSynsetSetBean().keySet())
    {
      WNSynsetBean synsetBean = (WNSynsetBean)wnSynsetSetBean.getSynsetSetBean().get(keyID);
      double sim = synsetBean.getSimilarity();
      boolean isgloss = synsetBean.isGloss_similarity();
      query = "insert into " + tableName + "(ontologyId, entityURI, synsetID, similarity, isgloss) values (" + idOntology + ", '" + entityURI + "', '" + keyID + "', " + sim + ", " + isgloss + ")";
      
      this.connectionBean.getDbManager().executeInsert(query);
    }
  }
  
  private static String replacecharacters(String text)
  {
    String result = text;
    try
    {
      int i = text.indexOf("'");
      if (i > 0)
      {
        String part1 = text.substring(0, i).concat("\\'");
        String part2 = text.substring(i + 1, text.length());
        if (part2.indexOf("'") > 0) {
          part2 = replacecharacters(part2);
        }
        result = part1.concat(part2);
      }
    }
    finally {}
    return result;
  }
  
  public void addSuperClassesToIndex(RDFEntity entity, RDFEntityList superclasses)
  {
    addInformationToIndex(getTableSuperClasses(), entity, superclasses);
  }
  
  public void addSubClassesToIndex(RDFEntity entity, RDFEntityList subclasses)
  {
    addInformationToIndex(getTableSubClasses(), entity, subclasses);
  }
  
  public void addDirectSuperClassesToIndex(RDFEntity entity, RDFEntityList superclasses)
  {
    addInformationToIndex(getTableDirectSuperClasses(), entity, superclasses);
  }
  
  public void addDirectSubClassesToIndex(RDFEntity entity, RDFEntityList subclasses)
  {
    addInformationToIndex(getTableDirectSubClasses(), entity, subclasses);
  }
  
  public void addDirectClassesToIndex(RDFEntity entity, RDFEntityList classes)
  {
    addInformationToIndex(getTableDirectClasses(), entity, classes);
  }
  
  public void addEquivalentEntitiesToIndex(RDFEntity entity, RDFEntityList classes)
  {
    addInformationToIndex(getTableEquivalent(), entity, classes);
  }
  
  public void addWNSynsetsToIndex(RDFEntity entity, WNSynsetSetBean wnSynsetSetBean)
  {
    addSynsetsToIndex(getTableSynsets(), entity, wnSynsetSetBean);
  }
  
  public void addOntologyToIndex(String ontologyID, String metadata_index_db)
  {
    String query = "insert into " + metadata_index_db.toLowerCase() + "(ontologyId, tableName) values (" + StringUtils.generateStringId(ontologyID) + ", '" + getMetadataTableName() + "')";
    
    this.connectionBean.getDbManager().executeInsert(query);
  }
  
  private RDFEntityList getAllClasses(String table, RDFEntity entity)
  {
    return getAllClasses(table, entity, "class");
  }
  
  public WNSynsetSetBean getAllSynsets(RDFEntity entity)
  {
    WNSynsetSetBean wnSynsetSetBean = new WNSynsetSetBean();
    if (!isIsSynsetIndexed()) {
      return wnSynsetSetBean;
    }
    String tableName = getTableSynsets();
    String query = "SELECT synsetID, similarity, isgloss from " + tableName + " USE INDEX (entity_index) where" + " ontologyId = " + StringUtils.generateStringId(entity.getIdPlugin()) + " and entityURI = '" + entity.getURI() + "';";
    
    ResultSet results = this.connectionBean.getDbManager().executeSelect(query);
    try
    {
      while (results.next())
      {
        double similarity = results.getFloat("similarity");
        boolean isgloss = results.getBoolean("isgloss");
        String synsetID = results.getString("synsetID");
        WordNet WN = new WordNet();
        Synset synset = WN.getSynsetfromID(synsetID);
        WN.closeDictionary();
        WNSynsetBean auxBean = new WNSynsetBean(similarity, isgloss, synset);
        wnSynsetSetBean.addSynsetBean(auxBean);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return wnSynsetSetBean;
    }
    return wnSynsetSetBean;
  }
  
  private ArrayList<String> getAllClassURIs(String table, RDFEntity entity)
  {
    String type = "class";
    ArrayList<String> entityList = new ArrayList();
    
    String query = "SELECT classURI from " + table + "  USE INDEX (entity_index) where" + " ontologyId = " + StringUtils.generateStringId(entity.getIdPlugin()) + " and entityURI = '" + entity.getURI() + "';";
    
    ResultSet results = this.connectionBean.getDbManager().executeSelect(query);
    try
    {
      while (results.next()) {
        entityList.add(results.getString("classURI"));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return entityList;
    }
    return entityList;
  }
  
  private RDFEntityList getAllClasses(String table, RDFEntity entity, String type)
  {
    RDFEntityList entityList = new RDFEntityList();
    String entityURI = entity.getURI();
    if (entityURI.indexOf("'") > 0) {
      entityURI = replacecharacters(entityURI);
    }
    String query = "SELECT classURI, classLabel from " + table + "  USE INDEX (entity_index) where" + " ontologyId = " + StringUtils.generateStringId(entity.getIdPlugin()) + " and entityURI = '" + entityURI + "';";
    
    ResultSet results = this.connectionBean.getDbManager().executeSelect(query);
    try
    {
      results.last();
      int num = results.getRow();
      if (num > max_number_rows)
      {
        System.out.println("Too many results " + num + " to be processed in the metadata tables " + query);
        return entityList;
      }
      results.beforeFirst();
      while (results.next())
      {
        RDFEntity entityClass = new RDFEntity(type, results.getString("classURI"), results.getString("classLabel"), entity.getIdPlugin());
        
        entityList.addRDFEntity(entityClass);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();System.out.println("get all classes: " + query);
      return entityList;
    }
    return entityList;
  }
  
  public RDFEntityList getAllSuperClasses(RDFEntity entity)
  {
    return getAllClasses(getTableSuperClasses(), entity);
  }
  
  public RDFEntityList getAllSubClasses(RDFEntity entity)
  {
    return getAllClasses(getTableSubClasses(), entity);
  }
  
  public RDFEntityList getDirectSuperClasses(RDFEntity entity)
  {
    return getAllClasses(getTableDirectSuperClasses(), entity);
  }
  
  public RDFEntityList getDirectSubClasses(RDFEntity entity)
  {
    return getAllClasses(getTableDirectSubClasses(), entity);
  }
  
  public RDFEntityList getDirectClassOfInstance(RDFEntity entity)
  {
    return getAllClasses(getTableDirectClasses(), entity);
  }
  
  public RDFEntityList getEquivalentEntities(RDFEntity entity)
  {
    return getAllClasses(getTableEquivalent(), entity, entity.getType());
  }
  
  public String getTableSuperClasses()
  {
    return (getMetadataTableName() + "superclasses").toLowerCase();
  }
  
  public String getTableEquivalent()
  {
    return (getMetadataTableName() + "equivalent").toLowerCase();
  }
  
  public String getTableSubClasses()
  {
    return (getMetadataTableName() + "subclasses").toLowerCase();
  }
  
  public String getTableDirectSuperClasses()
  {
    return (getMetadataTableName() + "directsuperclasses").toLowerCase();
  }
  
  public String getTableDirectSubClasses()
  {
    return (getMetadataTableName() + "directsubclasses").toLowerCase();
  }
  
  public String getTableDirectClasses()
  {
    return (getMetadataTableName() + "directclasses").toLowerCase();
  }
  
  public String getTableSynsets()
  {
    return (getMetadataTableName() + "synsets").toLowerCase();
  }
  
  public void close()
  {
    this.conectionsMetadataPool.eraseConnection(this.connectionBean);
  }
  
  public void closeAllMetadataConnections()
  {
    this.conectionsMetadataPool.eraseAllConnections();
  }
  
  public String getMetadataTableName()
  {
    return this.metadataTableName;
  }
  
  public boolean isIsSynsetIndexed()
  {
    return this.isSynsetIndexed;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    String aa = "cohen's boo'k (alleluy''a)";
    System.out.println(replacecharacters(aa));
  }
  
  public void setIsSynsetIndexed(boolean isSynsetIndexed)
  {
    this.isSynsetIndexed = isSynsetIndexed;
  }
}

