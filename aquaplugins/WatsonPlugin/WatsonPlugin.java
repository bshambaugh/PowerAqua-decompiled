package WatsonPlugin;

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import poweraqua.core.model.myocmlmodel.OcmlClass;
import poweraqua.core.model.myocmlmodel.OcmlInstance;
import poweraqua.core.model.myocmlmodel.OcmlProperty;
import poweraqua.core.model.myrdfmodel.MyURI;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.model.myrdfmodel.RDFPath;
import poweraqua.core.model.myrdfmodel.RDFProperty;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.serviceConfig.Repository;
import uk.ac.open.kmi.watson.clientapi.EntitySearch;
import uk.ac.open.kmi.watson.clientapi.EntitySearchServiceLocator;
import uk.ac.open.kmi.watson.clientapi.SemanticContentSearch;
import uk.ac.open.kmi.watson.clientapi.SemanticContentSearchServiceLocator;

public class WatsonPlugin
  implements OntologyPlugin
{
  private Repository repository;
  private static EntitySearch entitySearch = null;
  private String ontologyURI;
  private final String rdfs_type = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
  private static SemanticContentSearch semanticSearch;
  
  public WatsonPlugin()
  {
    try
    {
      SemanticContentSearchServiceLocator locator = new SemanticContentSearchServiceLocator();
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      semanticSearch = locator.getUrnSemanticContentSearch();
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      
      EntitySearchServiceLocator entityLocator = new EntitySearchServiceLocator();
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      entitySearch = entityLocator.getUrnEntitySearch();
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.out.println(e.getCause());
    }
  }
  
  public String getName()
  {
    return "watson";
  }
  
  public String getRepositoryType()
    throws Exception
  {
    return this.repository.getRepositoryType();
  }
  
  public String getPluginID()
  {
    return this.ontologyURI;
  }
  
  public void loadPlugin(Repository repository)
    throws Exception
  {
    this.repository = repository;
    
    this.ontologyURI = repository.getRepositoryName();
    try
    {
      String repositoryType = getLanguage(repository.getRepositoryName());
      this.repository.setRepositoryType(repositoryType);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.out.println(e.getCause());
    }
  }
  
  public static String getLanguage(String ontology_name)
    throws RemoteException
  {
    String[] res = semanticSearch.getSemanticContentLanguages(ontology_name);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (res != null) {
      for (String s : res)
      {
        if (s.equalsIgnoreCase("owl")) {
          return "OWL";
        }
        if (s.equalsIgnoreCase("daml")) {
          return "DAML";
        }
        if (s.equalsIgnoreCase("daml+oil")) {
          return "DAML";
        }
      }
    }
    return "RDF";
  }
  
  public void closePlugin() {}
  
  public String getLabelOfEntity(String entity_uri)
    throws Exception
  {
    String[] labels = entitySearch.getLabels(this.ontologyURI, entity_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if ((labels == null) || (labels.length < 1)) {
      return null;
    }
    return labels[0];
  }
  
  public RDFEntityList getAllClasses()
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] res = semanticSearch.listClasses(this.ontologyURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (res != null) {
      for (String s : res)
      {
        RDFEntity entity = new RDFEntity("class", s, getLabelOfEntity(s), getPluginID());
        entityList.addRDFEntity(entity);
      }
    }
    return entityList;
  }
  
  public RDFEntityList getAllClassesPeriodically(int offset, int limit)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] res = semanticSearch.listClasses(this.ontologyURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (res != null) {
      for (int i = offset; (i < offset + limit) && (i < res.length); i++)
      {
        String s = res[i];
        RDFEntity entity = new RDFEntity("class", s, getLabelOfEntity(s), getPluginID());
        entityList.addRDFEntity(entity);
      }
    }
    return entityList;
  }
  
  public RDFEntityList getAllProperties()
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] res = semanticSearch.listProperties(this.ontologyURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (res != null) {
      for (String s : res)
      {
        RDFEntity entity = new RDFProperty(s, getLabelOfEntity(s), getPluginID());
        entityList.addRDFEntity(entity);
      }
    }
    return entityList;
  }
  
  public RDFEntityList getAllPropertiesPeriodically(int offset, int limit)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] res = semanticSearch.listProperties(this.ontologyURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (res != null) {
      for (int i = offset; (i < offset + limit) && (i < res.length); i++)
      {
        String s = res[i];
        RDFEntity entity = new RDFProperty(s, getLabelOfEntity(s), getPluginID());
        entityList.addRDFEntity(entity);
      }
    }
    return entityList;
  }
  
  public RDFEntityList getAllInstances()
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] res = semanticSearch.listIndividuals(this.ontologyURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (res != null) {
      for (String s : res)
      {
        RDFEntity entity = new RDFEntity("instance", s, getLabelOfEntity(s), getPluginID());
        entityList.addRDFEntity(entity);
      }
    }
    return entityList;
  }
  
  public RDFEntityList getAllInstancesPeriodically(int offset, int limit)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] res = semanticSearch.listIndividuals(this.ontologyURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (res != null) {
      for (int i = offset; (i < offset + limit) && (i < res.length); i++)
      {
        String s = res[i];
        RDFEntity entity = new RDFEntity("instance", s, getLabelOfEntity(s), getPluginID());
        entityList.addRDFEntity(entity);
      }
    }
    return entityList;
  }
  
  public RDFEntityList getLiteralValuesOfInstance(String instance_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[][] literals = entitySearch.getLiteralsFor(this.ontologyURI, instance_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (literals != null) {
      for (int i = 0; i < literals.length; i++)
      {
        String literal = literals[i][2];
        RDFEntity entity = new RDFEntity("literal", instance_uri, literal, getPluginID());
        entityList.addRDFEntity(entity);
      }
    }
    return entityList;
  }
  
  public RDFEntityList getAllSubClasses(String class_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] res = entitySearch.getAllSubClasses(this.ontologyURI, class_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (res != null) {
      for (String s : res) {
        entityList.addRDFEntity(new RDFEntity("class", s, getLabelOfEntity(s), getPluginID()));
      }
    }
    return entityList;
  }
  
  public RDFEntityList getDirectSubClasses(String class_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    try
    {
      String[] subClasses = entitySearch.getSubClasses(this.ontologyURI, class_uri);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      if (subClasses != null) {
        for (String subClass : subClasses)
        {
          RDFEntity entity = new RDFEntity("class", subClass, getLabelOfEntity(class_uri), getPluginID());
          entityList.addRDFEntity(entity);
        }
      }
    }
    catch (RemoteException ex)
    {
      ex.printStackTrace();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return entityList;
  }
  
  public RDFEntityList getAllSuperClasses(String class_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] res = entitySearch.getAllSuperClasses(this.ontologyURI, class_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (res != null) {
      for (String s : res) {
        entityList.addRDFEntity(new RDFEntity("class", s, getLabelOfEntity(s), getPluginID()));
      }
    }
    return entityList;
  }
  
  public RDFEntityList getDirectSuperClasses(String class_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    try
    {
      String[] subClasses = entitySearch.getSuperClasses(this.ontologyURI, class_uri);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      if (subClasses != null) {
        for (String subClass : subClasses)
        {
          RDFEntity entity = new RDFEntity("class", subClass, getLabelOfEntity(class_uri), getPluginID());
          entityList.addRDFEntity(entity);
        }
      }
    }
    catch (RemoteException ex)
    {
      ex.printStackTrace();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return entityList;
  }
  
  public RDFEntityList getAllPropertiesOfClass(String class_uri)
    throws Exception
  {
    RDFEntityList properties = new RDFEntityList();
    properties = getDirectPropertiesOfClass(class_uri);
    
    RDFEntityList superClasses = getAllSuperClasses(class_uri);
    for (RDFEntity entity : superClasses.getAllRDFEntities()) {
      properties.addAllRDFEntity(getDirectPropertiesOfClass(entity.getURI()));
    }
    return properties;
  }
  
  public RDFEntityList getAllPropertiesOfInstance(String instance_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    String[][] relations = entitySearch.getRelationsFrom(this.ontologyURI, instance_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (relations != null) {
      for (String[] relation : relations) {
        if (!relation[0].equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
          entityList.addRDFEntity(new RDFProperty(relation[0], getLabelOfEntity(relation[0]), getPluginID()));
        }
      }
    }
    relations = entitySearch.getRelationsTo(this.ontologyURI, instance_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (relations != null) {
      for (String[] relation : relations) {
        entityList.addRDFEntity(new RDFProperty(relation[0], getLabelOfEntity(relation[0]), getPluginID()));
      }
    }
    return entityList;
  }
  
  public RDFEntityList getDirectPropertiesOfClass(String class_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] properties = entitySearch.getDomainOf(this.ontologyURI, class_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (properties != null) {
      for (String property : properties)
      {
        RDFEntity entity = new RDFProperty(property, getLabelOfEntity(property), getPluginID());
        entityList.addRDFEntity(entity);
      }
    }
    return entityList;
  }
  
  public RDFEntityList getAllPropertiesBetweenClass_Literal(String classURI, String slot_value)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[][] literals = entitySearch.getLiteralsFor(this.ontologyURI, classURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (literals != null) {
      for (int i = 0; i < literals.length; i++) {
        if (literals[i][2].equals(slot_value))
        {
          String property = literals[i][1];
          RDFEntity entity = new RDFProperty(property, getLabelOfEntity(property), getPluginID());
          entityList.addRDFEntity(entity);
        }
      }
    }
    return entityList;
  }
  
  public RDFEntityList getPropertiesBetweenClasses(String class1_uri, String class2_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    HashSet domainClass1 = new HashSet();
    HashSet rangeClass1 = new HashSet();
    
    RDFEntityList superClasses = getAllSuperClasses(class1_uri);
    superClasses.addRDFEntity(new RDFEntity("class", class1_uri, getLabelOfEntity(class1_uri), this.ontologyURI));
    for (RDFEntity c : superClasses.getAllRDFEntities())
    {
      String[] propertiesDomainClass1 = entitySearch.getDomainOf(this.ontologyURI, class1_uri);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      String[] propertiesRangeClass1 = entitySearch.getRangeOf(this.ontologyURI, class1_uri);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String p : propertiesDomainClass1) {
        domainClass1.add(p);
      }
      for (String p : propertiesRangeClass1) {
        rangeClass1.add(p);
      }
    }
    superClasses = getAllSuperClasses(class2_uri);
    superClasses.addRDFEntity(new RDFEntity("class", class2_uri, getLabelOfEntity(class2_uri), this.ontologyURI));
    for (RDFEntity c : superClasses.getAllRDFEntities())
    {
      String[] propertiesDomainClass2 = entitySearch.getDomainOf(this.ontologyURI, c.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      String[] propertiesRangeClass2 = entitySearch.getRangeOf(this.ontologyURI, c.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String p : propertiesDomainClass2) {
        if (rangeClass1.contains(p)) {
          entityList.addRDFEntity(new RDFProperty(p, getLabelOfEntity(p), getPluginID()));
        }
      }
      for (String p : propertiesRangeClass2) {
        if (domainClass1.contains(p)) {
          entityList.addRDFEntity(new RDFProperty(p, getLabelOfEntity(p), getPluginID()));
        }
      }
    }
    return entityList;
  }
  
  public RDFEntityList getAllClassesOfInstance(String instance_uri)
    throws Exception
  {
    String[] instanceTypes = entitySearch.getAllClasses(this.ontologyURI, instance_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    
    RDFEntityList entityList = new RDFEntityList();
    if (instanceTypes != null) {
      for (String instanceType : instanceTypes) {
        entityList.addRDFEntity(new RDFEntity("class", instanceType, getLabelOfEntity(instanceType), getPluginID()));
      }
    }
    return entityList;
  }
  
  public RDFEntityList getDirectClassOfInstance(String instance_uri)
    throws Exception
  {
    String[] instanceTypes = entitySearch.getClasses(this.ontologyURI, instance_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    RDFEntityList entityList = new RDFEntityList();
    if (instanceTypes != null) {
      for (String instanceType : instanceTypes) {
        entityList.addRDFEntity(new RDFEntity("class", instanceType, getLabelOfEntity(instanceType), getPluginID()));
      }
    }
    return entityList;
  }
  
  public RDFEntityList getAllInstancesOfClass(String class_uri, int limit)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] instances = entitySearch.getAllInstances(this.ontologyURI, class_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    int count = 0;
    if (instances != null) {
      for (String instance : instances)
      {
        if (count > limit) {
          break;
        }
        count++;
        entityList.addRDFEntity(new RDFEntity("instance", instance, getLabelOfEntity(instance), getPluginID()));
      }
    }
    return entityList;
  }
  
  public boolean isInstanceOf(String instance_uri, String class_uri)
    throws Exception
  {
    String[] instances = entitySearch.getInstances(this.ontologyURI, class_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (instances != null) {
      for (String instance : instances) {
        if (instance.equals(instance_uri)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean isNameOfInstance(String instance_uri, String literal)
    throws Exception
  {
    return false;
  }
  
  public boolean isSubClassOf(String class1, String class2)
    throws Exception
  {
    RDFEntityList subclasses = getAllSubClasses(class2);
    for (RDFEntity subclass : subclasses.getAllRDFEntities()) {
      if (subclass.getURI().equals(class1)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isURIExisted(String uri)
    throws Exception
  {
    String[] ontologies = entitySearch.getBelongsTo(uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (ontologies != null) {
      for (String ontology : ontologies) {
        if (ontology.equals(this.ontologyURI)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public RDFEntityList getSlotValue(String instance_uri, String property_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[][] relations = entitySearch.getRelationsFrom(this.ontologyURI, instance_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (relations != null) {
      for (String[] relation : relations) {
        if (relation[0].equals(property_uri)) {
          entityList.addRDFEntity(new RDFEntity("instance", relation[2], getLabelOfEntity(relation[2]), getPluginID()));
        }
      }
    }
    return entityList;
  }
  
  public RDFEntityList getInstancesWithSlotValue(String property_uri, String slot_value, boolean isValueLiteral)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    if (isValueLiteral) {
      throw new UnsupportedOperationException("the operation is not implemented for literals");
    }
    String[][] relations = entitySearch.getRelationsTo(this.ontologyURI, slot_value);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (relations != null) {
      for (String[] relation : relations) {
        if (relation[0].equals(property_uri)) {
          entityList.addRDFEntity(new RDFEntity("instance", relation[2], getLabelOfEntity(relation[2]), getPluginID()));
        }
      }
    }
    return entityList;
  }
  
  public OcmlClass getClassInfo(String class_uri)
    throws Exception
  {
    RDFEntity entityClass = new RDFEntity("class", class_uri, getLabelOfEntity(class_uri), getPluginID());
    OcmlClass c = new OcmlClass(entityClass);
    
    c.setDirectSuperClasses(getDirectSuperClasses(class_uri));
    c.setDirectSubclasses(getDirectSubClasses(class_uri));
    c.setSuperClasses(getAllSuperClasses(class_uri));
    c.setSubClasses(getAllSubClasses(class_uri));
    
    c.setProperties(getAllPropertiesOfClass(class_uri));
    c.setDirectProperties(getDirectPropertiesOfClass(class_uri));
    return c;
  }
  
  public OcmlInstance getInstanceInfo(String instance_uri)
    throws Exception
  {
    RDFEntity entityInstance = new RDFEntity("instance", instance_uri, getLabelOfEntity(instance_uri), getPluginID());
    OcmlInstance ocmlInstance = new OcmlInstance(entityInstance);
    
    RDFEntityList directSuperClasses = getDirectClassOfInstance(instance_uri);
    RDFEntityList allSuperClasses = getAllSuperClasses(instance_uri);
    
    ocmlInstance.addDirectSuperClasses(directSuperClasses);
    ocmlInstance.addSuperClasses(allSuperClasses);
    
    ocmlInstance.setEquivalentInstances(getEquivalentEntitiesForInstance(instance_uri));
    
    Hashtable<RDFEntity, RDFEntityList> propertiesTable = new Hashtable();
    String[][] relations = entitySearch.getRelationsFrom(this.ontologyURI, instance_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    for (String[] r : relations)
    {
      RDFEntity property = new RDFProperty(r[0], getLabelOfEntity(r[0]), getPluginID());
      RDFEntity value = null;
      if (MyURI.isURIValid(r[2])) {
        value = new RDFEntity("instance", r[2], getLabelOfEntity(r[2]), getPluginID());
      } else {
        value = new RDFEntity("literal", r[2], r[2], getPluginID());
      }
      if (propertiesTable.containsKey(property))
      {
        ((RDFEntityList)propertiesTable.get(property)).addRDFEntity(value);
      }
      else
      {
        RDFEntityList list = new RDFEntityList();
        list.addRDFEntity(value);
        propertiesTable.put(property, list);
      }
    }
    ocmlInstance.setProperties(propertiesTable);
    return ocmlInstance;
  }
  
  public OcmlProperty getPropertyInfo(String property_uri)
    throws Exception
  {
    RDFEntity entityClass = new RDFProperty(property_uri, getLabelOfEntity(property_uri), getPluginID());
    
    OcmlProperty ocmlProperty = new OcmlProperty(entityClass);
    ocmlProperty.setDirectSuperProperties(getDirectSuperClasses(property_uri));
    ocmlProperty.setSuperProperties(getAllSuperClasses(property_uri));
    ocmlProperty.setDirectSubProperties(getDirectSubClasses(property_uri));
    ocmlProperty.setSubProperties(getAllSubClasses(property_uri));
    
    ocmlProperty.setDomain(getDomainOfProperty(property_uri));
    ocmlProperty.setRange(getRangeOfProperty(property_uri));
    
    return ocmlProperty;
  }
  
  public RDFProperty getRDFProperty(String property_uri)
    throws Exception
  {
    if ((property_uri != null) && (MyURI.isURIValid(property_uri)))
    {
      RDFProperty p = new RDFProperty(property_uri, getLabelOfEntity(property_uri), getPluginID());
      RDFEntityList domain = getDomainOfProperty(property_uri);
      if (domain.size() > 0) {
        p.setDomain(domain);
      }
      RDFEntityList range = getRangeOfProperty(property_uri);
      if (range.size() > 0) {
        p.setRange(range);
      }
      return p;
    }
    return null;
  }
  
  public RDFEntityList getSchemaPropertiesForGenericClass(String classGeneric_uri, String class2_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    try
    {
      domainC = new HashSet();
      rangeC = new HashSet();
      
      RDFEntityList genericClasses = getAllSuperClasses(classGeneric_uri);
      genericClasses.addAllRDFEntity(getAllSubClasses(classGeneric_uri));
      genericClasses.addRDFEntity(new RDFEntity("class", classGeneric_uri, getLabelOfEntity(classGeneric_uri), this.ontologyURI));
      for (RDFEntity c : genericClasses.getAllRDFEntities())
      {
        String[] propertiesDomainC = entitySearch.getDomainOf(this.ontologyURI, c.getURI());
        MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
        String[] propertiesRangeC = entitySearch.getRangeOf(this.ontologyURI, c.getURI());
        MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
        for (String p : propertiesDomainC) {
          domainC.add(p);
        }
        for (String p : propertiesRangeC) {
          rangeC.add(p);
        }
      }
      RDFEntityList superClasses = getAllSuperClasses(class2_uri);
      superClasses.addRDFEntity(new RDFEntity("class", class2_uri, getLabelOfEntity(class2_uri), this.ontologyURI));
      for (RDFEntity c : superClasses.getAllRDFEntities())
      {
        String[] propertiesDomainClass2 = entitySearch.getDomainOf(this.ontologyURI, c.getURI());
        MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
        String[] propertiesRangeClass2 = entitySearch.getRangeOf(this.ontologyURI, c.getURI());
        MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
        for (String p : propertiesDomainClass2) {
          if (rangeC.contains(p)) {
            entityList.addRDFEntity(new RDFProperty(p, getLabelOfEntity(p), getPluginID()));
          }
        }
        for (String p : propertiesRangeClass2) {
          if (domainC.contains(p)) {
            entityList.addRDFEntity(new RDFProperty(p, getLabelOfEntity(p), getPluginID()));
          }
        }
      }
    }
    catch (Exception ex)
    {
      HashSet domainC;
      HashSet rangeC;
      ex.printStackTrace();
    }
    return entityList;
  }
  
  public RDFEntityList getKBPropertiesForGenericClass(String classGeneric_uri, String instance2_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[][] relations = entitySearch.getRelationsTo(this.ontologyURI, instance2_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    
    String[] subclasses = entitySearch.getAllSubClasses(this.ontologyURI, classGeneric_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    ArrayList<String> instanceList = new ArrayList();
    String[] instances = entitySearch.getInstances(this.ontologyURI, classGeneric_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    for (String instance : instances) {
      instanceList.add(instance);
    }
    for (String subclass : subclasses)
    {
      instances = entitySearch.getInstances(this.ontologyURI, subclass);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String instance : instances) {
        instanceList.add(instance);
      }
    }
    String[] r;
    for (r : relations) {
      for (String instance : instanceList) {
        if (instance.equals(r[2])) {
          entityList.addRDFEntity(new RDFProperty(r[0], getLabelOfEntity(r[0]), getPluginID()));
        }
      }
    }
    relations = entitySearch.getRelationsFrom(this.ontologyURI, instance2_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    String[] r;
    for (r : relations) {
      for (String instance : instanceList) {
        if (instance.equals(r[2])) {
          entityList.addRDFEntity(new RDFProperty(r[0], getLabelOfEntity(r[0]), getPluginID()));
        }
      }
    }
    return entityList;
  }
  
  public RDFEntityList getInstanceProperties(String instance1_uri, String instance2_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[][] relations = entitySearch.getRelationsTo(this.ontologyURI, instance2_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    for (String[] r : relations) {
      if (r[2].equals(instance1_uri)) {
        entityList.addRDFEntity(new RDFProperty(r[0], getLabelOfEntity(r[0]), getPluginID()));
      }
    }
    return entityList;
  }
  
  public RDFEntityList getPropertiesForGenericClass(String class_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    HashSet<String> properties = new HashSet();
    
    RDFEntityList classes = getAllSuperClasses(class_uri);
    classes.addAllRDFEntity(getAllSubClasses(class_uri));
    classes.addRDFEntity(new RDFEntity("class", class_uri, getLabelOfEntity(class_uri), this.ontologyURI));
    for (RDFEntity c : classes.getAllRDFEntities())
    {
      String[] propertiesDomainC = entitySearch.getDomainOf(this.ontologyURI, c.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      String[] propertiesRangeC = entitySearch.getRangeOf(this.ontologyURI, c.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String p : propertiesDomainC)
      {
        RDFProperty prop = new RDFProperty(p, getLabelOfEntity(p), getPluginID());
        prop.setDomain(c);
        prop.setRange(getRangeOfProperty(p));
        entityList.addRDFEntity(prop);
      }
      for (String p : propertiesRangeC)
      {
        RDFProperty prop = new RDFProperty(p, getLabelOfEntity(p), getPluginID());
        prop.setRange(c);
        prop.setDomain(getDomainOfProperty(p));
        entityList.addRDFEntity(prop);
      }
    }
    return entityList;
  }
  
  public RDFEntityList getKBPropertiesBetweenClasses(String class1_uri, String class2_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] instancesClass1 = entitySearch.getInstances(this.ontologyURI, class1_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    String[] instancesClass2 = entitySearch.getInstances(this.ontologyURI, class2_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    
    HashSet instanceClass1Set = new HashSet(instancesClass1.length);
    for (String c : instancesClass1) {
      instanceClass1Set.add(c);
    }
    HashSet instanceClass2Set = new HashSet(instancesClass2.length);
    for (String c : instancesClass2) {
      instanceClass2Set.add(c);
    }
    for (String ins1 : instancesClass1)
    {
      String[][] propertiesIns1 = entitySearch.getRelationsFrom(this.ontologyURI, ins1);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String[] p : propertiesIns1) {
        if (instanceClass2Set.contains(p[2])) {
          entityList.addRDFEntity(new RDFProperty(p[0], getLabelOfEntity(p[0]), getPluginID()));
        }
      }
    }
    for (String ins2 : instancesClass2)
    {
      String[][] propertiesIns2 = entitySearch.getRelationsFrom(this.ontologyURI, ins2);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String[] p : propertiesIns2) {
        if (instanceClass1Set.contains(p[2])) {
          entityList.addRDFEntity(new RDFProperty(p[0], getLabelOfEntity(p[0]), getPluginID()));
        }
      }
    }
    return entityList;
  }
  
  public boolean isKBTripleClassClass(String sourceURI, String relation, String targetURI)
    throws Exception
  {
    String[] instancesClass1 = entitySearch.getInstances(this.ontologyURI, sourceURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    String[] instancesClass2 = entitySearch.getInstances(this.ontologyURI, targetURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    
    HashSet instanceClass2Set = new HashSet(instancesClass2.length);
    for (String c : instancesClass2) {
      instanceClass2Set.add(c);
    }
    HashSet instanceClass1Set = new HashSet(instancesClass1.length);
    for (String c : instancesClass1) {
      instanceClass1Set.add(c);
    }
    for (String ins1 : instancesClass1)
    {
      String[][] propertiesIns1 = entitySearch.getRelationsFrom(this.ontologyURI, ins1);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String[] p : propertiesIns1) {
        if ((relation.equals(p[0])) && (instanceClass2Set.contains(p[2]))) {
          return true;
        }
      }
    }
    for (String ins2 : instancesClass2)
    {
      String[][] propertiesIns2 = entitySearch.getRelationsFrom(this.ontologyURI, ins2);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String[] p : propertiesIns2) {
        if ((relation.equals(p[0])) && (instanceClass1Set.contains(p[2]))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean isKBTripleClassInstance(String sourceURI, String relation, String targetURI)
    throws Exception
  {
    String[] instancesClass1 = entitySearch.getInstances(this.ontologyURI, sourceURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    for (String ins1 : instancesClass1)
    {
      String[][] propertiesIns1 = entitySearch.getRelationsFrom(this.ontologyURI, ins1);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String[] p : propertiesIns1) {
        if ((relation.equals(p[0])) && (targetURI.equals(p[2]))) {
          return true;
        }
      }
      propertiesIns1 = entitySearch.getRelationsTo(this.ontologyURI, ins1);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String[] p : propertiesIns1) {
        if ((relation.equals(p[0])) && (targetURI.equals(p[2]))) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean isKBTripleInstanceInstance(String sourceURI, String relation, String targetURI)
    throws Exception
  {
    String[][] propertiesIns1 = entitySearch.getRelationsFrom(this.ontologyURI, sourceURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    for (String[] p : propertiesIns1) {
      if ((relation.equals(p[0])) && (targetURI.equals(p[2]))) {
        return true;
      }
    }
    String[][] propertiesIns2 = entitySearch.getRelationsFrom(this.ontologyURI, targetURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    for (String[] p : propertiesIns2) {
      if ((relation.equals(p[0])) && (targetURI.equals(p[2]))) {
        return true;
      }
    }
    return false;
  }
  
  public ArrayList<RDFPath> getSchemaIndirectRelations(String sourceURI, String targetURI)
    throws Exception
  {
    ArrayList<RDFPath> rdfPathList = new ArrayList();
    
    HashSet<String> instances = new HashSet();
    RDFEntityList subclasses = getAllSubClasses(targetURI);
    subclasses.addRDFEntity(new RDFEntity("class", targetURI, getLabelOfEntity(targetURI), this.ontologyURI));
    for (RDFEntity e : subclasses.getAllRDFEntities())
    {
      String[] ins = entitySearch.getInstances(this.ontologyURI, e.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String i : ins) {
        instances.add(i);
      }
    }
    for (String instance_targetURI : instances) {
      rdfPathList.addAll(getKBIndirectRelations(sourceURI, instance_targetURI));
    }
    return rdfPathList;
  }
  
  public ArrayList<RDFPath> getKBIndirectRelations(String class_sourceURI, String instance_targetURI)
    throws Exception
  {
    Hashtable<String, ArrayList<String>> iPointedByTarget = new Hashtable();
    Hashtable<String, ArrayList<String>> iWhoPointedTarget = new Hashtable();
    ArrayList<RDFPath> rdfPathList = new ArrayList();
    
    String[][] prop = entitySearch.getRelationsFrom(this.ontologyURI, instance_targetURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    addPropertiesToTable(prop, iPointedByTarget);
    prop = entitySearch.getRelationsTo(this.ontologyURI, instance_targetURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    addPropertiesToTable(prop, iWhoPointedTarget);
    if ((iPointedByTarget.isEmpty()) && (iWhoPointedTarget.isEmpty())) {
      return rdfPathList;
    }
    HashSet<String> instances = new HashSet();
    RDFEntityList subclasses = getAllSubClasses(class_sourceURI);
    subclasses.addRDFEntity(new RDFEntity("class", class_sourceURI, getLabelOfEntity(class_sourceURI), this.ontologyURI));
    for (RDFEntity e : subclasses.getAllRDFEntities())
    {
      String[] ins = entitySearch.getInstances(this.ontologyURI, e.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String i : ins) {
        instances.add(i);
      }
    }
    for (String instance_sourceURI : instances)
    {
      Hashtable<String, ArrayList<String>> iPointedBySource = new Hashtable();
      Hashtable<String, ArrayList<String>> iWhoPointedSource = new Hashtable();
      prop = entitySearch.getRelationsFrom(this.ontologyURI, instance_sourceURI);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      addPropertiesToTable(prop, iPointedBySource);
      prop = entitySearch.getRelationsTo(this.ontologyURI, instance_sourceURI);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      addPropertiesToTable(prop, iWhoPointedSource);
      
      ArrayList<RDFPath> pathAuxList = getPathListBetweenInstanceSets(iPointedBySource, iWhoPointedTarget);
      rdfPathList = RDFPath.mergePathLists(rdfPathList, ConvertKBPathToOntoPath(instance_sourceURI, pathAuxList));
      
      pathAuxList = getPathListBetweenInstanceSets(iPointedBySource, iPointedByTarget);
      rdfPathList = RDFPath.mergePathLists(rdfPathList, ConvertKBPathToOntoPath(instance_sourceURI, pathAuxList));
      
      pathAuxList = getPathListBetweenInstanceSets(iWhoPointedSource, iWhoPointedTarget);
      rdfPathList = RDFPath.mergePathLists(rdfPathList, ConvertKBPathToOntoPath(instance_sourceURI, pathAuxList));
      
      pathAuxList = getPathListBetweenInstanceSets(iWhoPointedSource, iPointedByTarget);
      rdfPathList = RDFPath.mergePathLists(rdfPathList, ConvertKBPathToOntoPath(instance_sourceURI, pathAuxList));
    }
    return rdfPathList;
  }
  
  private ArrayList<RDFPath> ConvertKBPathToOntoPath(String instance_source, ArrayList<RDFPath> KbPathList)
    throws Exception
  {
    ArrayList<RDFPath> ontoPathList = new ArrayList();
    if (KbPathList.isEmpty()) {
      return ontoPathList;
    }
    RDFEntity instance_answer = new RDFEntity("instance", instance_source, getLabelOfEntity(instance_source), getPluginID());
    for (RDFPath pathAux : KbPathList)
    {
      RDFEntity middle_instance = pathAux.getRDFEntityReference();
      
      RDFEntityList middle_classes = getDirectClassOfInstance(middle_instance.getURI());
      RDFPath newpath;
      RDFPath newpath;
      if (middle_classes.isEmpty())
      {
        newpath = new RDFPath(pathAux.getRDFProperty1(), middle_instance, pathAux.getRDFProperty2());
      }
      else
      {
        RDFEntity middle_class = (RDFEntity)middle_classes.getAllRDFEntities().get(0);
        newpath = new RDFPath(pathAux.getRDFProperty1(), middle_class, pathAux.getRDFProperty2());
      }
      instance_answer.setRefers_to(middle_instance);
      newpath.setKBAnswers(instance_answer);
      ontoPathList.add(newpath);
    }
    return ontoPathList;
  }
  
  private void addPropertiesToTable(String[][] prop, Hashtable<String, ArrayList<String>> table)
  {
    for (String[] p : prop) {
      if (table.contains(p[2]))
      {
        ((ArrayList)table.get(p[2])).add(p[0]);
      }
      else
      {
        ArrayList<String> pList = new ArrayList();
        pList.add(p[0]);
        table.put(p[2], pList);
      }
    }
  }
  
  private ArrayList<RDFPath> getPathListBetweenInstanceSets(Hashtable<String, ArrayList<String>> set1, Hashtable<String, ArrayList<String>> set2)
    throws Exception
  {
    ArrayList<RDFPath> pathList = new ArrayList();
    for (Iterator i$ = set1.keySet().iterator(); i$.hasNext();)
    {
      i = (String)i$.next();
      if (set2.containsKey(i))
      {
        ArrayList<String> properties1 = (ArrayList)set1.get(i);
        properties2 = (ArrayList)set2.get(i);
        for (i$ = properties1.iterator(); i$.hasNext();)
        {
          p1 = (String)i$.next();
          for (String p2 : properties2)
          {
            RDFProperty prop1 = new RDFProperty(new RDFEntity("property", p1, getLabelOfEntity(p1), this.ontologyURI));
            RDFProperty prop2 = new RDFProperty(new RDFEntity("property", p2, getLabelOfEntity(p2), this.ontologyURI));
            if ((!prop1.getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) || (!prop2.getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")))
            {
              RDFEntity middle = new RDFEntity("instance", i, getLabelOfEntity(i), this.ontologyURI);
              RDFPath path = new RDFPath(prop1, middle, prop2);
              pathList.add(path);
            }
          }
        }
      }
    }
    String i;
    ArrayList<String> properties2;
    Iterator i$;
    String p1;
    return pathList;
  }
  
  public ArrayList<RDFPath> getInstanceIndirectRelations(String instance_sourceURI, String instance_targetURI)
    throws Exception
  {
    ArrayList<RDFPath> rdfPathList = new ArrayList();
    
    Hashtable<String, ArrayList<String>> iPointedBySource = new Hashtable();
    Hashtable<String, ArrayList<String>> iWhoPointedSource = new Hashtable();
    Hashtable<String, ArrayList<String>> iPointedByTarget = new Hashtable();
    Hashtable<String, ArrayList<String>> iWhoPointedTarget = new Hashtable();
    
    String[][] prop = entitySearch.getRelationsFrom(this.ontologyURI, instance_sourceURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    addPropertiesToTable(prop, iPointedBySource);
    
    prop = entitySearch.getRelationsTo(this.ontologyURI, instance_sourceURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    addPropertiesToTable(prop, iWhoPointedSource);
    
    prop = entitySearch.getRelationsFrom(this.ontologyURI, instance_targetURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    addPropertiesToTable(prop, iPointedByTarget);
    
    prop = entitySearch.getRelationsTo(this.ontologyURI, instance_targetURI);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    addPropertiesToTable(prop, iWhoPointedTarget);
    
    rdfPathList.addAll(getPathListBetweenInstanceSets(iPointedBySource, iWhoPointedTarget));
    
    rdfPathList.addAll(getPathListBetweenInstanceSets(iWhoPointedSource, iWhoPointedTarget));
    
    rdfPathList.addAll(getPathListBetweenInstanceSets(iWhoPointedSource, iPointedByTarget));
    
    return rdfPathList;
  }
  
  public RDFEntityList getGenericInstances(String genericClass, String slot, String instance)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    RDFEntityList entityListInverse = new RDFEntityList();
    
    RDFEntityList subClasses = getAllSubClasses(genericClass);
    subClasses.addRDFEntity(new RDFEntity("class", genericClass, getLabelOfEntity(genericClass), this.ontologyURI));
    
    String[][] propertiesInstance = entitySearch.getRelationsFrom(this.ontologyURI, instance);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    for (RDFEntity c : subClasses.getAllRDFEntities())
    {
      String[] instancesGenericClass = instancesGenericClass = entitySearch.getInstances(this.ontologyURI, c.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String ins : instancesGenericClass)
      {
        String[][] propertiesIns = entitySearch.getRelationsFrom(this.ontologyURI, ins);
        MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
        for (String[] p : propertiesIns) {
          if ((slot.equals(p[0])) && (instance.equals(p[2]))) {
            entityList.addRDFEntity(new RDFEntity("instance", ins, getLabelOfEntity(ins), getPluginID()));
          }
        }
        for (String[] p : propertiesInstance) {
          if ((slot.equals(p[0])) && (ins.equals(p[2]))) {
            entityListInverse.addRDFEntity(new RDFEntity("instance", ins, getLabelOfEntity(ins), getPluginID()));
          }
        }
      }
    }
    if (entityList.isEmpty()) {
      return entityListInverse;
    }
    return entityList;
  }
  
  public RDFEntityList getTripleInstances(String class1_uri, String slot, String class2_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    RDFEntityList subClassesClass1 = getAllSubClasses(class1_uri);
    subClassesClass1.addRDFEntity(new RDFEntity("class", class1_uri, getLabelOfEntity(class1_uri), this.ontologyURI));
    
    RDFEntityList subClassesClass2 = getAllSubClasses(class2_uri);
    subClassesClass2.addRDFEntity(new RDFEntity("class", class2_uri, getLabelOfEntity(class2_uri), this.ontologyURI));
    
    HashSet<String> instancesClass1 = new HashSet();
    for (RDFEntity entity : subClassesClass1.getAllRDFEntities())
    {
      String[] instances = entitySearch.getInstances(this.ontologyURI, entity.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String i : instances) {
        instancesClass1.add(i);
      }
    }
    HashSet<String> instancesClass2 = new HashSet();
    for (RDFEntity entity : subClassesClass2.getAllRDFEntities())
    {
      String[] instances = entitySearch.getInstances(this.ontologyURI, entity.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String i : instances) {
        instancesClass2.add(i);
      }
    }
    for (String ins1 : instancesClass1)
    {
      String[][] propertiesIns1 = entitySearch.getRelationsFrom(this.ontologyURI, ins1);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String[] p : propertiesIns1) {
        if ((slot.equals(p[0])) && (instancesClass2.contains(p[2])))
        {
          RDFEntity rdfEntity = new RDFEntity("instance", ins1, getLabelOfEntity(ins1), getPluginID());
          RDFEntity rdfEntityRef = new RDFEntity("instance", p[2], getLabelOfEntity(p[2]), getPluginID());
          rdfEntity.setRefers_to(rdfEntityRef);
          entityList.addRDFEntity(rdfEntity);
        }
      }
    }
    if (entityList.size() <= 0) {
      for (String ins2 : instancesClass2)
      {
        String[][] propertiesIns2 = entitySearch.getRelationsFrom(this.ontologyURI, ins2);
        MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
        for (String[] p : propertiesIns2) {
          if ((slot.equals(p[0])) && (instancesClass1.contains(p[2])))
          {
            RDFEntity rdfEntity = new RDFEntity("instance", ins2, getLabelOfEntity(ins2), getPluginID());
            RDFEntity rdfEntityRef = new RDFEntity("instance", p[2], getLabelOfEntity(p[2]), getPluginID());
            rdfEntity.setRefers_to(rdfEntityRef);
            entityList.addRDFEntity(rdfEntity);
          }
        }
      }
    }
    return entityList;
  }
  
  public RDFEntityList getTripleInstances(String query_class, String prop1, String ref_class, String prop2, String instance)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    RDFEntityList list1 = getTripleInstances(query_class, prop1, ref_class);
    RDFEntityList list2 = getGenericInstances(ref_class, prop2, instance);
    for (RDFEntity e : list1.getAllRDFEntities()) {
      if (list2.isRDFEntityContained(e.getRefers_to().getURI())) {
        entityList.addRDFEntity(e);
      }
    }
    return entityList;
  }
  
  public RDFEntityList getTripleInstancesFromClasses(String query_class, String prop1, String ref_class, String prop2, String class2)
    throws Exception
  {
    System.out.println("TO DO *************************************************************************");
    return new RDFEntityList();
  }
  
  public RDFEntityList getTripleInstancesFromLiteral(String query_class, String prop1, String ref_class, String prop2, String class2)
    throws Exception
  {
    System.out.println("TO DO *************************************************************************");
    return new RDFEntityList();
  }
  
  public RDFEntityList getGenericInstancesForLiteral(String class_uri, String slot, String value)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    RDFEntityList subClasses = getAllSubClasses(class_uri);
    subClasses.addRDFEntity(new RDFEntity("class", class_uri, getLabelOfEntity(class_uri), this.ontologyURI));
    for (RDFEntity c : subClasses.getAllRDFEntities())
    {
      String[] instances = entitySearch.getInstances(this.ontologyURI, c.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (String i : instances)
      {
        String[][] propertiesInstance = entitySearch.getLiteralsFor(this.ontologyURI, i);
        MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
        for (String[] p : propertiesInstance) {
          if ((p[0].equals(slot)) && (p[2].equals(value))) {
            entityList.addRDFEntity(new RDFEntity("instance", i, getLabelOfEntity(i), getPluginID()));
          }
        }
      }
    }
    return entityList;
  }
  
  public RDFEntityList getEquivalentEntitiesForClass(String entity_uri)
    throws Exception
  {
    return new RDFEntityList();
  }
  
  public RDFEntityList getEquivalentEntitiesForProperty(String entity_uri)
    throws Exception
  {
    return new RDFEntityList();
  }
  
  public RDFEntityList getEquivalentEntitiesForInstance(String entity_uri)
    throws Exception
  {
    return new RDFEntityList();
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForClasses()
    throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForProperties()
    throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForInstances()
    throws Exception
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ArrayList<RDFPath> getKBIndirectRelationsWithLiterals(String class_URI, String literal)
    throws Exception
  {
    return new ArrayList();
  }
  
  public RDFEntityList getDomainOfProperty(String property_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] domainClasses = entitySearch.getDomain(this.ontologyURI, property_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    for (String domainClass : domainClasses) {
      entityList.addRDFEntity(new RDFEntity("class", domainClass, getLabelOfEntity(domainClass), getPluginID()));
    }
    return entityList;
  }
  
  public RDFEntityList getRangeOfProperty(String property_uri)
    throws Exception
  {
    RDFEntityList entityList = new RDFEntityList();
    
    String[] rangeClasses = entitySearch.getRange(this.ontologyURI, property_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    for (String rangeClass : rangeClasses) {
      entityList.addRDFEntity(new RDFEntity("class", rangeClass, getLabelOfEntity(rangeClass), getPluginID()));
    }
    return entityList;
  }
  
  public boolean existTripleForProperty(String entity)
  {
    int num = numberOfTriplesWithPropertyAsPredicate(this.ontologyURI, entity);
    if (num > 0) {
      return true;
    }
    return false;
  }
  
  public boolean existTripleForInstance(String entity)
  {
    return true;
  }
  
  public int numberOfAllTriples(String onto)
  {
    try
    {
      return (int)semanticSearch.getNumberOfStatement(onto);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return -1;
  }
  
  public String entityType(String onto, String e)
  {
    try
    {
      return entitySearch.getType(onto, e);
    }
    catch (RemoteException e1)
    {
      e1.printStackTrace();
    }
    return null;
  }
  
  public Set<String> getClassesOf(String onto, String i)
  {
    Set<String> res = new HashSet();
    try
    {
      String[] cls = entitySearch.getAllClasses(onto, i);
      if (cls != null)
      {
        for (String cl : cls) {
          res.add(cl);
        }
        return res;
      }
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return res;
  }
  
  public Set<String> getInstancesOf(String onto, String c)
  {
    Set<String> res = new HashSet();
    try
    {
      String[] ins = entitySearch.getAllInstances(onto, c);
      if (ins != null)
      {
        for (String in : ins) {
          res.add(in);
        }
        return res;
      }
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return res;
  }
  
  public Set<String> getSubPropertyOf(String onto, String p)
  {
    Set<String> res = new HashSet();
    try
    {
      String[] prs = entitySearch.getAllSubProperties(onto, p);
      if (prs != null)
      {
        for (String pr : prs) {
          res.add(pr);
        }
        return res;
      }
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return res;
  }
  
  public int numberOfTripleWithClassAsObjectAndDomainProperty(String onto, String c)
  {
    try
    {
      String[] dom = entitySearch.getDomainOf(onto, c);
      if (dom != null) {
        return dom.length;
      }
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return -1;
  }
  
  public int numberOfTriplesWithClassAsObjectAndTypeProperty(String onto, String c)
  {
    try
    {
      String[] type = entitySearch.getInstances(onto, c);
      if (type != null) {
        return type.length;
      }
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return -1;
  }
  
  public int numberOfTriplesWithClassAsSubject(String onto, String c)
  {
    try
    {
      String[][] rel = entitySearch.getRelationsFrom(onto, c);
      String[][] lit = entitySearch.getLiteralsFor(onto, c);
      if ((rel != null) && (lit != null)) {
        return rel.length + lit.length;
      }
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return -1;
  }
  
  public int numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(String onto, String c)
  {
    try
    {
      String[] sup = entitySearch.getSuperClasses(onto, c);
      if (sup != null) {
        return sup.length;
      }
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return -1;
  }
  
  public int numberOfTriplesWithInstanceAsSubject(String onto, String i)
  {
    try
    {
      String[][] rel = entitySearch.getRelationsFrom(onto, i);
      String[][] lit = entitySearch.getLiteralsFor(onto, i);
      if ((rel != null) && (lit != null)) {
        return rel.length + lit.length;
      }
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return -1;
  }
  
  public int numberOfTriplesWithPropertyAsPredicate(String onto, String p)
  {
    try
    {
      String[][] pred = entitySearch.getRelatedBy(onto, p);
      if (pred != null) {
        return pred.length;
      }
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return -1;
  }
  
  public int numberOfTriplesWithPropertyAsSubject(String onto, String p)
  {
    try
    {
      String[][] rel = entitySearch.getRelationsFrom(onto, p);
      String[][] lit = entitySearch.getLiteralsFor(onto, p);
      if ((rel != null) && (lit != null)) {
        return rel.length + lit.length;
      }
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
    return -1;
  }
  
  public static void main(String[] args)
  {
    try
    {
      WatsonPlugin p = new WatsonPlugin();
      Repository rep = new Repository();
      rep.setRepositoryName("http://social.semantic-web.at/wiki/index.php/Spezial:ExportRDF/Tassilo_Pellegrini?xmlmime=rdf");
      p.loadPlugin(rep);
      
      instanceUri = "http://sun.semantic-2Dweb.at/wiki/index.php/_Denny_Vrandecic";
    }
    catch (Exception ex)
    {
      String instanceUri;
      ex.printStackTrace();
    }
  }
}

