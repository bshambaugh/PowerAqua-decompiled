package poweraqua.indexingService.creator;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import poweraqua.WordNetJWNL.WNSynsetSetBean;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.core.utils.StringUtils;
import poweraqua.indexingService.manager.IndexBean;
import poweraqua.indexingService.manager.IndexManager;
import poweraqua.indexingService.manager.IndexManagerLucene;
import poweraqua.indexingService.manager.IndexServiceConfiguration;
import poweraqua.powermap.elementPhase.SemanticComponent;
import poweraqua.serviceConfig.MultiOntologyManager;
import poweraqua.serviceConfig.ServiceConfiguration;
import poweraquaDB.DataBaseManager;
import poweraquaDB.DatabaseConector;

public class IndexingCreator
{
  private static int MAX_RESULTS = 10000;
  public static final String ONTOLOGY_INDEX_TABLE = "ontologyindextable";
  private ServiceConfiguration serviceConfiguration;
  private IndexServiceConfiguration indexServiceConfiguration;
  private MultiIndexServiceConfiguration multiIndexServiceConfiguration;
  private DataBaseManager dbManager;
  private boolean addSynsetsToIndex = false;
  private boolean replaceInformation;
  
  public IndexingCreator(boolean replaceAllInformation)
    throws Exception
  {
    this.serviceConfiguration = new ServiceConfiguration();
    this.indexServiceConfiguration = new IndexServiceConfiguration();
    this.multiIndexServiceConfiguration = new MultiIndexServiceConfiguration();
    this.dbManager = DatabaseConector.openDbConexion(this.multiIndexServiceConfiguration.getDb(), this.multiIndexServiceConfiguration.getLogin(), this.multiIndexServiceConfiguration.getPassword());
    if (!replaceAllInformation)
    {
      this.serviceConfiguration.readConfigurationFile();
      this.indexServiceConfiguration.readConfigurationFile();
    }
    else
    {
      createIndexOntologiesTable();
    }
  }
  
  public void createIndex(MultiOntologyIndexBean indexInformationBean)
  {
    try
    {
      String IndexInformationFolder = indexInformationBean.getIndexFolder();
      MultiOntologyManager multiOntologyManager = new MultiOntologyManager(IndexInformationFolder);
      
      IndexServiceConfiguration index = new IndexServiceConfiguration();
      index.readConfigurationFile(IndexInformationFolder);
      IndexBean indexBean = (IndexBean)index.getIndexList().get(0);
      
      createIndex(multiOntologyManager, indexBean);
      
      this.indexServiceConfiguration.addIndexBean(indexBean);
      this.serviceConfiguration.addRepositoryList(multiOntologyManager.getContext().getRepositories());
      this.serviceConfiguration.setPluginsDirectory(multiOntologyManager.getContext().getPluginsDirectory());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  private void createIndex(MultiOntologyManager multiOntologyManager, IndexBean indexBean)
  {
    try
    {
      System.out.println("********Indexing Semantic Entities in " + indexBean.getIndex_dir());
      
      IndexManagerLucene indexManager = new IndexManagerLucene(indexBean, this.multiIndexServiceConfiguration.getIndex_global_path());
      indexManager.openIndexForCreation(this.addSynsetsToIndex);
      
      addInformationToIndexPeriodically(multiOntologyManager, indexManager);
      
      indexManager.createSpellCheckerFromIndex();
      indexManager.closeIndex();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void uploadIndex(MultiOntologyIndexBean indexInformationBean)
  {
    try
    {
      String IndexInformationFolder = indexInformationBean.getIndexFolder();
      MultiOntologyManager multiOntologyManager = new MultiOntologyManager(IndexInformationFolder);
      
      IndexServiceConfiguration index = new IndexServiceConfiguration();
      index.readConfigurationFile(IndexInformationFolder);
      IndexBean indexBean = (IndexBean)index.getIndexList().get(0);
      
      uploadIndex(multiOntologyManager, indexBean);
      
      this.indexServiceConfiguration.addIndexBean(indexBean);
      this.serviceConfiguration.addRepositoryList(multiOntologyManager.getContext().getRepositories());
      this.serviceConfiguration.setPluginsDirectory(multiOntologyManager.getContext().getPluginsDirectory());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  private void uploadIndex(MultiOntologyManager multiOntologyManager, IndexBean indexBean)
  {
    try
    {
      System.out.println("********Indexing Semantic Entities in " + indexBean.getIndex_dir());
      
      IndexManagerLucene indexManager = new IndexManagerLucene(indexBean, this.multiIndexServiceConfiguration.getIndex_global_path());
      indexManager.openIndexForUpload(this.addSynsetsToIndex);
      addInformationToIndexPeriodically(multiOntologyManager, indexManager);
      
      indexManager.closeIndex();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private void addInformationToIndexPeriodically(MultiOntologyManager multiOntologyManager, IndexManager indexManager)
  {
    int plugin_number = 0;
    for (OntologyPlugin plugin : multiOntologyManager.getAllPlugins())
    {
      plugin_number += 1;
      System.out.println("Indexing osPlugin number " + plugin_number + " " + plugin.getPluginID() + " : !!!!!!!!!!!!!!!!");
      try
      {
        int offset = 0;
        
        System.out.println("Reading all properties");
        RDFEntityList list = plugin.getAllPropertiesPeriodically(offset, MAX_RESULTS);
        if ((list == null) || (list.size() == 0)) {
          System.out.println("No properties found");
        }
        while ((list != null) && (list.size() > 0))
        {
          System.out.println("Adding " + (list.size() < MAX_RESULTS ? list.size() : MAX_RESULTS) + " more properties... " + offset);
          list.setPlugingID(plugin.getPluginID());
          indexManager.addRDFEntitiesToOntologyIndex(list);
          offset += MAX_RESULTS;
          list = plugin.getAllPropertiesPeriodically(offset, MAX_RESULTS);
        }
        equivalent = plugin.getEquivalentEntitiesForProperties();
        for (RDFEntity entity : equivalent.keySet())
        {
          System.out.println("Adding equivalent entities for property " + entity);
          indexManager.addEquivalentEntitiesToIndex(entity, (RDFEntityList)equivalent.get(entity));
        }
        offset = 0;
        System.out.println("Reading all classes");
        list = plugin.getAllClassesPeriodically(0, MAX_RESULTS);
        if ((list == null) || (list.size() == 0)) {
          System.out.println("No classes found");
        }
        while ((list != null) && (list.size() > 0))
        {
          System.out.println("Adding " + (list.size() < MAX_RESULTS ? list.size() : MAX_RESULTS) + " more classes... " + offset);
          
          indexManager.addRDFEntitiesToOntologyIndex(list);
          
          RDFEntityList directclasses = new RDFEntityList();
          RDFEntityList classes = new RDFEntityList();
          for (RDFEntity entity : list.getAllRDFEntities())
          {
            entity.setIdPlugin(plugin.getPluginID());
            
            directclasses = plugin.getDirectSuperClasses(entity.getURI());
            if ((directclasses != null) && (directclasses.size() > 0))
            {
              indexManager.addDirectSuperClassesToIndex(entity, directclasses);
              classes = plugin.getAllSuperClasses(entity.getURI());
              indexManager.addSuperClassesToIndex(entity, classes);
              System.out.println("metadata for superclasses added .. " + entity.getURI());
              if (isAddSynsetsToIndex())
              {
                WNSynsetSetBean wnSynsetSetBean = SemanticComponent.getClassTaxonomySynsetsForIndex(entity, directclasses);
                indexManager.addWNSynsetsToIndex(entity, wnSynsetSetBean);
                System.out.println("metadata for synsets added .. " + entity.getURI());
              }
            }
            classes = plugin.getDirectSubClasses(entity.getURI());
            if ((classes != null) && (classes.size() > 0))
            {
              indexManager.addDirectSubClassesToIndex(entity, classes);
              classes = plugin.getAllSubClasses(entity.getURI());
              indexManager.addSubClassesToIndex(entity, classes);
              System.out.println("metadata for subclasses added .. " + entity.getURI());
            }
          }
          offset += MAX_RESULTS;
          list = plugin.getAllClassesPeriodically(offset, MAX_RESULTS);
        }
        equivalent = plugin.getEquivalentEntitiesForClasses();
        for (RDFEntity entity : equivalent.keySet())
        {
          System.out.println("Adding equivalent entities for class " + entity);
          indexManager.addEquivalentEntitiesToIndex(entity, (RDFEntityList)equivalent.get(entity));
        }
        offset = 0;
        System.out.println("Reading all instances (offset " + offset + ")");
        
        list = plugin.getAllInstancesPeriodically(0, MAX_RESULTS);
        if ((list == null) || (list.size() == 0))
        {
          offset += MAX_RESULTS;
          list = plugin.getAllInstancesPeriodically(offset, MAX_RESULTS);
          if ((list == null) || (list.size() == 0)) {
            System.out.println("No instances found");
          }
        }
        boolean secondChance = false;
        while ((list != null) && (list.size() > 0) && (!secondChance))
        {
          secondChance = false;
          System.out.println("Adding " + (list.size() < MAX_RESULTS ? list.size() : MAX_RESULTS) + " more instances... " + offset);
          
          list.setPlugingID(plugin.getPluginID());
          indexManager.addRDFEntitiesToKnowledgeBaseIndex(list);
          
          RDFEntityList classes = new RDFEntityList();
          for (RDFEntity instance : list.getAllRDFEntities())
          {
            System.out.println("ayyyyy " + instance.getURI());
            RDFEntityList literals = plugin.getLiteralValuesOfInstance(instance.getURI());
            indexManager.addLiteralsToKnowledgeBaseIndex(instance, literals);
            classes = plugin.getDirectClassOfInstance(instance.getURI());
            System.out.println("auuuuu " + classes.toString());
            indexManager.addDirectClassesToIndex(instance, classes);
          }
          offset += MAX_RESULTS;
          list = plugin.getAllInstancesPeriodically(offset, MAX_RESULTS);
          if ((list == null) || (list.size() == 0))
          {
            secondChance = true;
            offset += MAX_RESULTS;
            System.out.println("second chance to add instances: offset " + offset);
            list = plugin.getAllInstancesPeriodically(offset, MAX_RESULTS);
          }
        }
        equivalent = plugin.getEquivalentEntitiesForInstances();
        for (RDFEntity entity : equivalent.keySet())
        {
          System.out.println("Adding equivalent entities for instance " + entity);
          indexManager.addEquivalentEntitiesToIndex(entity, (RDFEntityList)equivalent.get(entity));
        }
      }
      catch (Exception ex)
      {
        Hashtable<RDFEntity, RDFEntityList> equivalent;
        System.out.println("Imposible to add the information to the index ********************************* ");
        
        ex.printStackTrace();
      }
      plugin.closePlugin();
    }
  }
  
  public void createMultipleIndexes(ArrayList<MultiOntologyIndexBean> multiOntologyIndexBeanList)
  {
    for (MultiOntologyIndexBean multiOntologyIndexBean : multiOntologyIndexBeanList) {
      createIndex(multiOntologyIndexBean);
    }
  }
  
  public void createMultipleIndexes()
  {
    createMultipleIndexes(this.multiIndexServiceConfiguration.getIndexList());
  }
  
  public void uploadMultipleIndexes(ArrayList<MultiOntologyIndexBean> multiOntologyIndexBeanList)
  {
    for (MultiOntologyIndexBean multiOntologyIndexBean : multiOntologyIndexBeanList) {
      uploadIndex(multiOntologyIndexBean);
    }
  }
  
  public void uploadMultipleIndexes()
  {
    uploadMultipleIndexes(this.multiIndexServiceConfiguration.getIndexList());
  }
  
  public void updateconfigurationInformation()
  {
    this.indexServiceConfiguration.writeConfigurationFile();
    this.serviceConfiguration.writeConfigurationFile();
  }
  
  public void updateconfigurationInformation(String path)
  {
    this.indexServiceConfiguration.writeConfigurationFile(path);
    this.serviceConfiguration.writeConfigurationFile(path);
  }
  
  private void createIndexOntologiesTable()
  {
    try
    {
      String dropTable = "DROP TABLE  ontologyindextable";
      this.dbManager.executeDropTable(dropTable);
      
      String createTable = "CREATE TABLE ontologyindextable (id INT NOT NULL AUTO_INCREMENT, ontologyId BIGINT, indexManagerId BIGINT, PRIMARY KEY(id) ) ";
      
      this.dbManager.executeCreateTable(createTable);
      
      System.out.println("metadata tables created .. ");
    }
    catch (Exception ex) {}
  }
  
  private void addNewEntryToTable(String ontologyId, String indexManagerId)
  {
    String query = "insert into ontologyindextable(ontologyId, indexManagerId) values (" + StringUtils.generateStringId(ontologyId) + ", " + StringUtils.generateStringId(indexManagerId) + ")";
    
    this.dbManager.executeInsert(query);
  }
  
  public static void main(String[] args)
    throws Exception
  {
    if (args.length == 0)
    {
      System.out.println("Please insert one initialization argument between {CREATE_ALL, UPDATE_NEW, UPDATE_ALL} to create/initialize all the indexes, update the existing indexes by creating a new one, or update an existing index respectively ");
      
      args = new String[1];
      
      args[0] = "UPDATE_NEW";
    }
    System.out.println("Creating standard index for entities (Classes, properties, instances, literals): type-URI-label");
    if (args[0].equalsIgnoreCase("CREATE_ALL"))
    {
      IndexingCreator iCreator = new IndexingCreator(true);
      
      iCreator.setAddSynsetsToIndex(false);
      iCreator.createMultipleIndexes();
      iCreator.updateconfigurationInformation();
    }
    else if (args[0].equalsIgnoreCase("UPDATE_NEW"))
    {
      IndexingCreator iCreator = new IndexingCreator(false);
      iCreator.setAddSynsetsToIndex(false);
      iCreator.createMultipleIndexes();
      iCreator.updateconfigurationInformation();
    }
    else if (args[0].equalsIgnoreCase("UPDATE_ALL"))
    {
      IndexingCreator iCreator = new IndexingCreator(false);
      iCreator.setAddSynsetsToIndex(false);
      iCreator.uploadMultipleIndexes();
      iCreator.updateconfigurationInformation();
    }
    else
    {
      System.out.println("Please insert one initialization argument between {CREATE_ALL, UPDATE_NEW, UPDATE_ALL} to create/initialize all the indexes, update the existing indexes by creating a new one, or update an existing index respectively ");
    }
  }
  
  public boolean isAddSynsetsToIndex()
  {
    return this.addSynsetsToIndex;
  }
  
  public void setAddSynsetsToIndex(boolean addSynsetsToIndex)
  {
    this.addSynsetsToIndex = addSynsetsToIndex;
  }
}

