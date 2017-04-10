package poweraqua.indexingService.manager.virtuoso;

import RemoteSPARQLPlugin.RemoteSPARQLPlugin;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.engineHTTP.QueryEngineHTTP;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import poweraqua.WordNetJWNL.WNSynsetSetBean;
import poweraqua.core.model.myrdfmodel.MyURI;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.indexingService.manager.IndexManager;
import poweraqua.indexingService.manager.virtuoso.virtuosohelpers.GraphVirtuoso;
import poweraqua.powermap.elementPhase.EntityMappingTable;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.serviceConfig.RepositoryVirtuoso;
import virtuosoPlugin.ToLongException;
import virtuosoPlugin.VirtuosoPlugin;
import virtuosoPlugin.virtuosoHelpers.ObjectType;
import virtuosoPlugin.virtuosoHelpers.ObjectType.Connector;
import virtuosoPlugin.virtuosoHelpers.ObjectType.Literal;
import virtuosoPlugin.virtuosoHelpers.ObjectType.Relation;

public class IndexManagerVirtuoso
  implements IndexManager
{
  private static int LIMIT_LITERALS = 5;
  private static int LIMIT_LEXICAL_WORDS = 10;
  private static int LIMIT_PROPERTIES = 2;
  private static int LIMIT_HITS = 21;
  public static int LIMIT_FUSION = 16;
  public Connection con;
  ArrayList<String> datesList = new ArrayList(Arrays.asList(new String[] { "xsd:date", "xsd:gYear" }));
  private ArrayList<GraphVirtuoso> graphList;
  private GraphVirtuoso graphVirtuoso;
  private java.sql.ResultSet result;
  private String SPARQL_PREFIX = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
  private com.hp.hpl.jena.query.ResultSet resultsRemote;
  private boolean remoteSPARQLVirtuoso;
  private long time;
  private ArrayList<String> listClasses = new ArrayList(Arrays.asList(new String[] { "http://www.w3.org/2002/07/owl#Class", "http://www.w3.org/2000/01/rdf-schema#Class" }));
  private ArrayList<String> listProperties = new ArrayList(Arrays.asList(new String[] { "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property", "http://www.w3.org/2002/07/owl#Property", "http://www.w3.org/2002/07/owl#DatatypeProperty", "http://www.w3.org/2002/07/owl#ObjectProperty" }));
  
  public IndexManagerVirtuoso(ArrayList<GraphVirtuoso> graphList)
    throws IOException
  {
    this.graphList = graphList;
  }
  
  public IndexManagerVirtuoso(ArrayList<GraphVirtuoso> graphList, boolean remoteSPARQLVirtuoso)
    throws IOException
  {
    this.graphList = graphList;
    this.remoteSPARQLVirtuoso = remoteSPARQLVirtuoso;
  }
  
  public void openIndexForCreation(boolean addSynsetsToIndex) {}
  
  public void openIndexForUpload(boolean addSynsetsToIndex) {}
  
  public int getIndexType()
  {
    return 6;
  }
  
  public String getId()
  {
    return "virtuoso";
  }
  
  public void closeIndex()
    throws Exception
  {}
  
  public void addRDFEntitiesToOntologyIndex(RDFEntityList entityList)
    throws IOException
  {}
  
  public void addRDFEntitiesToKnowledgeBaseIndex(RDFEntityList entityList)
    throws IOException
  {}
  
  public void addLiteralsToKnowledgeBaseIndex(RDFEntity relatedInstance, RDFEntityList literals)
    throws IOException
  {}
  
  public void addSuperClassesToIndex(RDFEntity entity, RDFEntityList superClasses) {}
  
  public void addSubClassesToIndex(RDFEntity entity, RDFEntityList subClasses) {}
  
  public void addDirectSuperClassesToIndex(RDFEntity entity, RDFEntityList superClasses) {}
  
  public void addDirectSubClassesToIndex(RDFEntity entity, RDFEntityList subClasses) {}
  
  public void addWNSynsetsToIndex(RDFEntity entity, WNSynsetSetBean directclasses) {}
  
  public boolean isSynsetIndex()
  {
    return false;
  }
  
  public WNSynsetSetBean searchSynsets(RDFEntity entity)
  {
    return new WNSynsetSetBean();
  }
  
  public void addDirectClassesToIndex(RDFEntity entity, RDFEntityList subClasses) {}
  
  public void addEquivalentEntitiesToIndex(RDFEntity entity, RDFEntityList classes) {}
  
  public void addOntologyToIndex(String ontologyID) {}
  
  private RDFEntityList searchAllSuperClassesRemote(RDFEntity entity)
  {
    try
    {
      RemoteSPARQLPlugin remoteSPARQLPlugin = startRemoteSPARQL();
      return remoteSPARQLPlugin.getAllSuperClasses(entity.getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  public RDFEntityList searchAllSuperClasses(RDFEntity entity)
  {
    if (this.remoteSPARQLVirtuoso) {
      return searchAllSuperClassesRemote(entity);
    }
    try
    {
      VirtuosoPlugin virtuosoPlugin = startVirtuoso();
      return virtuosoPlugin.getAllSuperClasses(entity.getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  private RDFEntityList searchAllSubClassesRemote(RDFEntity entity)
  {
    try
    {
      RemoteSPARQLPlugin remoteSPARQLPlugin = startRemoteSPARQL();
      return remoteSPARQLPlugin.getAllSubClasses(entity.getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  public RDFEntityList searchAllSubClasses(RDFEntity entity)
  {
    if (this.remoteSPARQLVirtuoso) {
      return searchAllSubClassesRemote(entity);
    }
    try
    {
      VirtuosoPlugin virtuosoPlugin = startVirtuoso();
      return virtuosoPlugin.getAllSubClasses(entity.getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  private RDFEntityList searchDirectSuperClassesRemote(RDFEntity entity)
  {
    try
    {
      RemoteSPARQLPlugin remoteSPARQLPlugin = startRemoteSPARQL();
      return remoteSPARQLPlugin.getDirectSuperClasses(entity.getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  public RDFEntityList searchDirectSuperClasses(RDFEntity entity)
  {
    if (this.remoteSPARQLVirtuoso) {
      return searchDirectSuperClassesRemote(entity);
    }
    try
    {
      VirtuosoPlugin virtuosoPlugin = startVirtuoso();
      return virtuosoPlugin.getDirectSuperClasses(entity.getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  private RDFEntityList searchDirectSubClassesRemote(RDFEntity entity)
  {
    try
    {
      RemoteSPARQLPlugin remoteSPARQLPlugin = startRemoteSPARQL();
      return remoteSPARQLPlugin.getDirectSubClasses(entity.getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  public RDFEntityList searchDirectSubClasses(RDFEntity entity)
  {
    if (this.remoteSPARQLVirtuoso) {
      return searchDirectSubClassesRemote(entity);
    }
    try
    {
      VirtuosoPlugin virtuosoPlugin = startVirtuoso();
      return virtuosoPlugin.getDirectSubClasses(entity.getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  private RDFEntityList searchDirectClassesOfInstanceRemote(RDFEntity entity)
  {
    try
    {
      RemoteSPARQLPlugin remoteSPARQLPlugin = startRemoteSPARQL();
      return remoteSPARQLPlugin.getDirectClassOfInstance(entity.getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  public RDFEntityList searchDirectClassOfInstance(RDFEntity entity)
  {
    if (this.remoteSPARQLVirtuoso) {
      return searchDirectClassesOfInstanceRemote(entity);
    }
    try
    {
      VirtuosoPlugin virtuosoPlugin = startVirtuoso();
      return virtuosoPlugin.getDirectClassOfInstance(entity.getURI());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  private RDFEntityList searchEquivalentEntitiesRemote(RDFEntity entity)
  {
    try
    {
      RemoteSPARQLPlugin remoteSPARQLPlugin = startRemoteSPARQL();
      if (entity.getType().contains("class")) {
        return remoteSPARQLPlugin.getEquivalentEntitiesForClass(entity.getURI());
      }
      if (entity.getType().contains("property")) {
        return remoteSPARQLPlugin.getEquivalentEntitiesForProperty(entity.getURI());
      }
      if (entity.getType().contains("instance")) {
        return remoteSPARQLPlugin.getEquivalentEntitiesForInstance(entity.getURI());
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  public RDFEntityList searchEquivalentEntities(RDFEntity entity)
  {
    if (this.remoteSPARQLVirtuoso) {
      return searchEquivalentEntitiesRemote(entity);
    }
    try
    {
      VirtuosoPlugin virtuosoPlugin = startVirtuoso();
      if (entity.getType().contains("class")) {
        return virtuosoPlugin.getEquivalentEntitiesForClass(entity.getURI());
      }
      if (entity.getType().contains("property")) {
        return virtuosoPlugin.getEquivalentEntitiesForProperty(entity.getURI());
      }
      if (entity.getType().contains("instance")) {
        return virtuosoPlugin.getEquivalentEntitiesForInstance(entity.getURI());
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new RDFEntityList();
  }
  
  public EntityMappingTable searchEntityMappingsOnOntology(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    return new EntityMappingTable(keyword);
  }
  
  public EntityMappingTable searchEntityMappingsOnKnowledgeBase(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    return new EntityMappingTable(keyword);
  }
  
  private EntityMappingTable multiSearchEntityMappings(String keyword, String semanticRelation, int LIMIT)
    throws Exception
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    for (GraphVirtuoso gV : getGraphList())
    {
      this.graphVirtuoso = gV;
      
      entityMappingTable.merge(searchClassesMappingsOnOntology(keyword, semanticRelation, LIMIT));
      
      entityMappingTable.merge(searchPropertiesMappingsOnOntology(keyword, semanticRelation, LIMIT));
      if ((semanticRelation.equals("equivalentMatching")) || (semanticRelation.equals("synonym"))) {
        entityMappingTable.merge(searchInstancesMappingsOnKnowledgeBase(keyword, semanticRelation, LIMIT));
      }
    }
    return entityMappingTable;
  }
  
  public EntityMappingTable multiSearchEntityMappings(String keyword, String semanticRelation)
    throws Exception
  {
    return multiSearchEntityMappings(keyword, semanticRelation, false);
  }
  
  public EntityMappingTable multiSearchEntityMappings(String keyword, String semanticRelation, boolean isqueryTerm)
    throws Exception
  {
    while (keyword.contains("  ")) {
      keyword = keyword.replace("  ", " ");
    }
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    int limit;
    int limit;
    if (semanticRelation.equals("equivalentMatching")) {
      limit = LIMIT_HITS;
    } else {
      limit = LIMIT_LEXICAL_WORDS;
    }
    for (GraphVirtuoso gV : getGraphList())
    {
      this.graphVirtuoso = gV;
      if (!checkIfNumber(keyword))
      {
        entityMappingTable.merge(searchAnyEntities(keyword, semanticRelation, limit));
        if ((semanticRelation.equals("equivalentMatching")) && (!this.datesList.contains(keyword))) {
          entityMappingTable.merge(searchClassesMappingsOnOntology(keyword, semanticRelation, LIMIT_PROPERTIES));
        }
      }
      if ((!this.datesList.contains(keyword)) && (
        (semanticRelation.equals("equivalentMatching")) || ((semanticRelation.equals("synonym")) && (!isqueryTerm)))) {
        entityMappingTable.merge(searchInstancesMappingsOnKnowledgeBase(keyword, semanticRelation, limit));
      }
    }
    return entityMappingTable;
  }
  
  public EntityMappingTable searchInstancesMappingsOnKnowledgeBase(String keyword, String semanticRelation, int LIMIT)
    throws Exception
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    ArrayList<SearchSemanticResult> list = searchInstances(keyword, semanticRelation, LIMIT);
    
    entityMappingTable.addMappingList(list);
    if (semanticRelation.equals("equivalentMatching"))
    {
      list = searchLiterals(keyword, semanticRelation, LIMIT_LITERALS);
      entityMappingTable.addMappingList(list);
    }
    return entityMappingTable;
  }
  
  private EntityMappingTable searchEntitiesMappingsOnOntology(String keyword, String semanticRelation, int LIMIT)
    throws Exception
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    entityMappingTable.addMappingList(searchClasses(keyword, semanticRelation, LIMIT));
    
    entityMappingTable.addMappingList(searchProperties(keyword, semanticRelation, LIMIT));
    
    return entityMappingTable;
  }
  
  private EntityMappingTable searchClassesMappingsOnOntology(String keyword, String semanticRelation, int LIMIT)
    throws Exception
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    
    ArrayList<SearchSemanticResult> list = searchClasses(keyword, semanticRelation, LIMIT);
    
    entityMappingTable.addMappingList(list);
    return entityMappingTable;
  }
  
  private EntityMappingTable searchPropertiesMappingsOnOntology(String keyword, String semanticRelation, int LIMIT)
    throws Exception
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    
    ArrayList<SearchSemanticResult> list = searchProperties(keyword, semanticRelation, LIMIT);
    
    entityMappingTable.addMappingList(list);
    return entityMappingTable;
  }
  
  private ArrayList<SearchSemanticResult> searchClasses(String keyword, String semanticRelation, int LIMIT)
    throws Exception
  {
    return search(keyword, semanticRelation, ObjectType.CLASS, LIMIT);
  }
  
  private ArrayList<SearchSemanticResult> searchProperties(String keyword, String semanticRelation, int LIMIT)
    throws Exception
  {
    return search(keyword, semanticRelation, ObjectType.PROPERTY, LIMIT);
  }
  
  private ArrayList<SearchSemanticResult> searchInstances(String keyword, String semanticRelation, int LIMIT)
    throws Exception
  {
    return search(keyword, semanticRelation, ObjectType.INSTANCE, LIMIT);
  }
  
  private ArrayList<SearchSemanticResult> searchLiterals(String keyword, String semanticRelation, int LIMIT)
    throws Exception
  {
    ArrayList<SearchSemanticResult> listOld = search(keyword, semanticRelation, ObjectType.LITERAL, LIMIT);
    ArrayList<SearchSemanticResult> list = new ArrayList();
    for (SearchSemanticResult s : listOld) {
      if (s.getEntity().getLabel().toString().length() <= 40) {
        list.add(s);
      }
    }
    return list;
  }
  
  private EntityMappingTable searchAnyEntities(String keyword, String semanticRelation, int LIMIT)
    throws Exception
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    if (this.datesList.contains(keyword)) {
      entityMappingTable.addMappingList(searchDates(keyword, semanticRelation, ObjectType.ALL, LIMIT));
    } else if (keyword.startsWith("xsd:")) {
      System.out.println("do nothing for " + keyword);
    } else {
      entityMappingTable.addMappingList(search(keyword, semanticRelation, ObjectType.ALL, LIMIT));
    }
    return entityMappingTable;
  }
  
  private ArrayList<SearchSemanticResult> searchDates(String keyword, String semanticRelation, ObjectType objectType, int LIMIT)
    throws Exception
  {
    String sparql = "SELECT DISTINCT  ?p ?l ?type FROM  <" + this.graphVirtuoso.getGraphIRI() + "> WHERE {?p rdfs:range " + keyword + ". ?p a ?type. OPTIONAL {?p rdfs:label ?l}}";
    
    execute(sparql, -1);
    if (objectType.getRelation() == ObjectType.Relation.NO_SPECIFIC_TYPE) {
      return resultToSearchSemanticResultList(semanticRelation, keyword);
    }
    return resultToSearchSemanticResultList(objectType.getObjectType(), semanticRelation, keyword);
  }
  
  private ArrayList<SearchSemanticResult> search(String keyword, String semanticRelation, ObjectType objectType, int LIMIT)
    throws Exception
  {
    if ((objectType.getLiteral() == ObjectType.Literal.IS_LITERAL) && (!checkIfNumber(keyword)) && (!keyword.contains(" ")))
    {
      String sparql = getSPARQLExactLiteral(keyword);
      execute(sparql, -1);
      ArrayList<SearchSemanticResult> exactLiterals = resultToSearchSemanticResultList(objectType.getObjectType(), semanticRelation, keyword);
      if (!exactLiterals.isEmpty()) {
        return exactLiterals;
      }
    }
    keyword = keyword.replace(".", "");
    String sparql;
    String sparql;
    if (keyword.contains("\"")) {
      sparql = getSPARQL(keyword, ObjectType.Connector.ONE_STRING, objectType, LIMIT);
    } else {
      sparql = getSPARQL(keyword, ObjectType.Connector.AND, objectType, LIMIT);
    }
    execute(sparql, -1);
    if (objectType.getRelation() == ObjectType.Relation.NO_SPECIFIC_TYPE) {
      return resultToSearchSemanticResultList(semanticRelation, keyword);
    }
    return resultToSearchSemanticResultList(objectType.getObjectType(), semanticRelation, keyword);
  }
  
  private ArrayList<SearchSemanticResult> resultToSearchSemanticResultListRemote(String type, String semanticRelation, String keyword)
    throws Exception
  {
    ArrayList<SearchSemanticResult> entities = new ArrayList();
    if (this.resultsRemote == null) {
      return entities;
    }
    while (this.resultsRemote.hasNext())
    {
      QuerySolution soln = this.resultsRemote.nextSolution();
      Iterator it = soln.varNames();
      String objectName = (String)it.next();
      String value = soln.get(objectName).toString();
      objectName = (String)it.next();
      String label = soln.get(objectName).toString();
      if (value != null)
      {
        String valueString = value.toString().trim();
        if ((!valueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#List")) && 
        
          (MyURI.isURIValid(value.toString())))
        {
          String uri = value.toString();
          RDFEntity entity = new RDFEntity(type, uri, label, getPluginID());
          
          SearchSemanticResult ssr = new SearchSemanticResult(entity, semanticRelation, keyword);
          
          ssr.setVirtuoso(true);
          entities.add(ssr);
        }
      }
    }
    return entities;
  }
  
  private ArrayList<SearchSemanticResult> resultToSearchSemanticResultList(String type, String semanticRelation, String keyword)
    throws Exception
  {
    if (this.remoteSPARQLVirtuoso) {
      return resultToSearchSemanticResultListRemote(type, semanticRelation, keyword);
    }
    ArrayList<SearchSemanticResult> entities = new ArrayList();
    if (this.result == null) {
      return entities;
    }
    while (this.result.next())
    {
      String value = this.result.getString(1);
      if (value != null)
      {
        String valueString = value.toString().trim();
        if ((!valueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#List")) && 
        
          (MyURI.isURIValid(value.toString())))
        {
          String uri = value.toString();
          String label = this.result.getString(2);
          RDFEntity entity = new RDFEntity(type, uri, label, getPluginID());
          
          SearchSemanticResult ssr = new SearchSemanticResult(entity, semanticRelation, keyword);
          
          ssr.setVirtuoso(true);
          entities.add(ssr);
        }
      }
    }
    try
    {
      if (this.con != null) {
        this.con.close();
      }
    }
    catch (Exception e)
    {
      System.out.println("Unable to close the connection");
      e.printStackTrace();
    }
    return entities;
  }
  
  private ArrayList<SearchSemanticResult> resultToSearchSemanticResultListRemote(String semanticRelation, String keyword)
    throws Exception
  {
    ArrayList<SearchSemanticResult> entities = new ArrayList();
    if (this.resultsRemote == null) {
      return entities;
    }
    while (this.resultsRemote.hasNext())
    {
      QuerySolution soln = this.resultsRemote.nextSolution();
      Iterator it = soln.varNames();
      String objectName = (String)it.next();
      String uri = soln.get(objectName).toString();
      objectName = (String)it.next();
      String label = soln.get(objectName).toString();
      objectName = (String)it.next();
      String typeEnt = soln.get(objectName).toString();
      if (this.listClasses.contains(typeEnt))
      {
        RDFEntity entity = new RDFEntity("class", uri, label, getPluginID());
        SearchSemanticResult ssr = new SearchSemanticResult(entity, semanticRelation, keyword);
        
        ssr.setVirtuoso(true);
        entities.add(ssr);
      }
      else if (this.listProperties.contains(typeEnt))
      {
        RDFEntity entity = new RDFEntity("property", uri, label, getPluginID());
        SearchSemanticResult ssr = new SearchSemanticResult(entity, semanticRelation, keyword);
        
        ssr.setVirtuoso(true);
        entities.add(ssr);
      }
    }
    return entities;
  }
  
  private ArrayList<SearchSemanticResult> resultToSearchSemanticResultList(String semanticRelation, String keyword)
    throws Exception
  {
    if (this.remoteSPARQLVirtuoso) {
      return resultToSearchSemanticResultListRemote(semanticRelation, keyword);
    }
    ArrayList<SearchSemanticResult> entities = new ArrayList();
    if (this.result == null) {
      return entities;
    }
    while (this.result.next())
    {
      String uri = this.result.getString(1);
      String label = this.result.getString(2);
      String typeEnt = this.result.getString(3);
      if ((typeEnt == null) || (this.listClasses.contains(typeEnt)))
      {
        RDFEntity entity = new RDFEntity("class", uri, label, getPluginID());
        SearchSemanticResult ssr = new SearchSemanticResult(entity, semanticRelation, keyword);
        
        ssr.setVirtuoso(true);
        entities.add(ssr);
      }
      else if (this.listProperties.contains(typeEnt))
      {
        RDFEntity entity = new RDFEntity("property", uri, label, getPluginID());
        SearchSemanticResult ssr = new SearchSemanticResult(entity, semanticRelation, keyword);
        
        ssr.setVirtuoso(true);
        entities.add(ssr);
      }
    }
    try
    {
      if (this.con != null) {
        this.con.close();
      }
    }
    catch (Exception e)
    {
      System.out.println("Unable to close the connection");
      e.printStackTrace();
    }
    return entities;
  }
  
  public String getPluginID()
  {
    if (this.graphVirtuoso.getPort() != null) {
      return this.graphVirtuoso.getURL() + ":" + this.graphVirtuoso.getPort() + "#" + this.graphVirtuoso.getGraphIRI();
    }
    return this.graphVirtuoso.getURL() + "#" + this.graphVirtuoso.getGraphIRI();
  }
  
  private String getSPARQLExactLiteral(String keyword)
  {
    String sparql = "SELECT DISTINCT ?s ?o FROM <" + this.graphVirtuoso.getGraphIRI() + "> " + " WHERE {?s ?p ?o .FILTER(<bif:contains>(?o, \"" + keyword + "\" ) && isLiteral(?o)  && ( str(?p) != rdfs:label  && str(?p) !=  foaf:name) && " + " (str(?o) = \"" + keyword + "\"))} LIMIT 1";
    
    return sparql;
  }
  
  private String getSPARQLold(String[] words, boolean and_or, ObjectType objectType, int limit)
  {
    String connector;
    String connector;
    if (and_or) {
      connector = " and ";
    } else {
      connector = " or ";
    }
    String sparql = "";
    sparql = sparql + "SELECT DISTINCT ";
    sparql = sparql + "?s ?o ";
    sparql = sparql + "FROM ";
    
    sparql = sparql + "<" + this.graphVirtuoso.getGraphIRI() + "> ";
    sparql = sparql + "WHERE ";
    sparql = sparql + "{ ?s ?p ?o .";
    switch (objectType.getRelation())
    {
    case CLASS: 
      sparql = sparql + "[] a ?s .";
      break;
    case PROPERTY: 
      sparql = sparql + "[] ?s [].";
      break;
    case INSTANCE: 
      sparql = sparql + "?s a ?concept . ?concept a [] .";
      break;
    case TYPE: 
      if (objectType.hasTypes())
      {
        boolean first = true;
        for (int i = 0; i < objectType.getTypes().length; i++)
        {
          if (first) {
            first = false;
          } else {
            sparql = sparql + " UNION ";
          }
          sparql = sparql + " {?s rdf:type " + objectType.getTypes()[i] + ".}";
        }
      }
      break;
    case TYPE_OF_SOMETHING_THATS_TYPE: 
      if (objectType.hasTypes())
      {
        sparql = sparql + " {?s rdf:type ?something.}";
        boolean first = true;
        for (int i = 0; i < objectType.getTypes().length; i++)
        {
          if (first) {
            first = false;
          } else {
            sparql = sparql + " UNION ";
          }
          sparql = sparql + " {?something rdf:type " + objectType.getTypes()[i] + ".}";
        }
      }
      break;
    case SUBCLASS: 
      if (objectType.hasTypes())
      {
        boolean first = true;
        for (int i = 0; i < objectType.getTypes().length; i++)
        {
          if (first) {
            first = false;
          } else {
            sparql = sparql + " UNION ";
          }
          sparql = sparql + " {?s rdfs:subClassOf " + objectType.getTypes()[i] + ".}";
        }
      }
      break;
    }
    sparql = sparql + "FILTER( <bif:contains>(?o, \"";
    boolean first = true;
    for (String word : words)
    {
      if (first) {
        first = false;
      } else {
        sparql = sparql + connector;
      }
      sparql = sparql + word;
    }
    sparql = sparql + "\" ) ";
    if (objectType.getLiteral() != ObjectType.Literal.NO_RESTRICTION) {
      if (objectType.getLiteral() == ObjectType.Literal.IS_NO_LITERAL) {
        sparql = sparql + "&& ! isLiteral(?s)";
      } else if (objectType.getLiteral() == ObjectType.Literal.IS_LITERAL) {
        sparql = sparql + "&& isLiteral(?o)";
      }
    }
    String relationalOperator;
    String relationalOperator;
    if (objectType.isNotProperty()) {
      relationalOperator = " != ";
    } else {
      relationalOperator = " = ";
    }
    boolean firstProp = true;
    for (String property : objectType.getProperties())
    {
      if (firstProp)
      {
        firstProp = false;
        sparql = sparql + " && ( ";
      }
      else
      {
        sparql = sparql + " || ";
      }
      sparql = sparql + "str(?p)" + relationalOperator + property + " ";
    }
    sparql = sparql + ")";
    sparql = sparql + ")";
    sparql = sparql + "}";
    sparql = sparql + "LIMIT " + limit;
    return sparql;
  }
  
  private String getSPARQL(String words, ObjectType.Connector connector, ObjectType objectType, int limit)
  {
    String sparql = "";
    sparql = sparql + "SELECT DISTINCT ";
    sparql = sparql + "?s ?o ";
    if (objectType.getRelation() == ObjectType.Relation.NO_SPECIFIC_TYPE) {
      sparql = sparql + "?type ";
    }
    sparql = sparql + "FROM ";
    
    sparql = sparql + "<" + this.graphVirtuoso.getGraphIRI() + "> ";
    sparql = sparql + "WHERE {";
    if (!objectType.isNotProperty())
    {
      boolean firstProp = true;
      sparql = sparql + "{";
      for (String property : objectType.getProperties())
      {
        if (!firstProp) {
          sparql = sparql + " } UNION { ";
        }
        sparql = sparql + getSPARQLHelp(words, connector, objectType, property);
        firstProp = false;
      }
      sparql = sparql + "}";
    }
    else
    {
      sparql = sparql + getSPARQLHelp(words, connector, objectType, "?p");
    }
    sparql = sparql + "}";
    sparql = sparql + "LIMIT " + limit;
    
    return sparql;
  }
  
  private String getSPARQLHelp(String words, ObjectType.Connector connector, ObjectType objectType, String property)
  {
    switch (connector)
    {
    case AND: 
      words = words.replace(" ", " and ");
      break;
    case OR: 
      words = words.replace(" ", " or ");
      break;
    case ONE_STRING: 
      words = words.replace(" ", " and ");
      words = words.replace("\"", "");
    }
    String sparql = "";
    sparql = sparql + "?s " + property + " ?o.";
    switch (objectType.getRelation())
    {
    case NO_SPECIFIC_TYPE: 
      sparql = sparql + "?s a  ?type .";
      break;
    case CLASS: 
      sparql = sparql + "[] a ?s .";
      break;
    case PROPERTY: 
      sparql = sparql + "[] ?s [].";
      break;
    case INSTANCE: 
      sparql = sparql + "?s a ?concept . ?concept ?b ?c .";
      break;
    case TYPE: 
      if (objectType.hasTypes())
      {
        boolean first = true;
        for (int i = 0; i < objectType.getTypes().length; i++)
        {
          if (first) {
            first = false;
          } else {
            sparql = sparql + " UNION ";
          }
          sparql = sparql + " {?s rdf:type " + objectType.getTypes()[i] + ".}";
        }
      }
      break;
    case TYPE_OF_SOMETHING_THATS_TYPE: 
      if (objectType.hasTypes())
      {
        sparql = sparql + " {?s rdf:type ?something.}";
        boolean first = true;
        for (int i = 0; i < objectType.getTypes().length; i++)
        {
          if (first) {
            first = false;
          } else {
            sparql = sparql + " UNION ";
          }
          sparql = sparql + " {?something rdf:type " + objectType.getTypes()[i] + ".}";
        }
      }
      break;
    case SUBCLASS: 
      if (objectType.hasTypes())
      {
        boolean first = true;
        for (int i = 0; i < objectType.getTypes().length; i++)
        {
          if (first) {
            first = false;
          } else {
            sparql = sparql + " UNION ";
          }
          sparql = sparql + " {?s rdfs:subClassOf " + objectType.getTypes()[i] + ".}";
        }
      }
      break;
    }
    if (!checkIfNumber(words))
    {
      sparql = sparql + "FILTER( <bif:contains>(?o, \"";
      sparql = sparql + words;
      sparql = sparql + "\" ) ";
    }
    else
    {
      sparql = sparql + "FILTER( <bif:contains>(?o, '\"";
      sparql = sparql + words;
      sparql = sparql + "*\"') ";
    }
    if (objectType.getLiteral() != ObjectType.Literal.NO_RESTRICTION) {
      if (objectType.getLiteral() == ObjectType.Literal.IS_NO_LITERAL) {
        sparql = sparql + "&& ! isLiteral(?s)";
      } else if (objectType.getLiteral() == ObjectType.Literal.IS_LITERAL) {
        sparql = sparql + "&& isLiteral(?o)";
      }
    }
    if (objectType.isNotProperty())
    {
      String relationalOperator = " != ";
      
      boolean firstProp2 = true;
      for (String property2 : objectType.getProperties())
      {
        if (firstProp2)
        {
          firstProp2 = false;
          sparql = sparql + " && ( ";
        }
        else
        {
          sparql = sparql + " || ";
        }
        sparql = sparql + "str(?p)" + relationalOperator + property2 + " ";
      }
      sparql = sparql + ")";
    }
    sparql = sparql + ")";
    return sparql;
  }
  
  private void executeRemote(String sparql, int timeout_sec)
  {
    long start = System.currentTimeMillis();
    long time = 0L;
    try
    {
      String query = this.SPARQL_PREFIX + sparql;
      QueryEngineHTTP qexec = new QueryEngineHTTP(this.graphVirtuoso.getURL(), query);
      
      this.resultsRemote = qexec.execSelect();
      time = System.currentTimeMillis() - start;
      if (time > 400L) {
        throw new ToLongException();
      }
    }
    catch (ToLongException e)
    {
      System.out.println("Too long (plugin) : " + time + "ms\t" + sparql + "\t");
      boolean done = false;
      for (StackTraceElement trace : e.getStackTrace())
      {
        if (done)
        {
          System.out.println(trace + "\n");
          break;
        }
        done = true;
      }
    }
    catch (Exception e)
    {
      System.out.println("Fail to execute: " + sparql);
      e.printStackTrace();
    }
  }
  
  private void execute(String sparql, int timeout_sec)
  {
    if (this.remoteSPARQLVirtuoso)
    {
      executeRemote(sparql, timeout_sec);
    }
    else
    {
      long start = System.currentTimeMillis();
      try
      {
        Class.forName("virtuoso.jdbc3.Driver");
      }
      catch (ClassNotFoundException e)
      {
        System.err.println("Keine Treiber-Klasse");
        return;
      }
      this.con = null;
      try
      {
        try
        {
          if (timeout_sec > 0) {
            this.con = DriverManager.getConnection("jdbc:virtuoso://" + this.graphVirtuoso.getURL() + "/UID=" + this.graphVirtuoso.getLogin() + "/PWD=" + this.graphVirtuoso.getPassword() + "/TIMEOUT=" + timeout_sec);
          } else {
            this.con = DriverManager.getConnection("jdbc:virtuoso://" + this.graphVirtuoso.getURL() + "/UID=" + this.graphVirtuoso.getLogin() + "/PWD=" + this.graphVirtuoso.getPassword());
          }
          Statement stmt = this.con.createStatement();
          this.result = stmt.executeQuery("SPARQL " + sparql);
          this.time = (System.currentTimeMillis() - start);
          if (this.time > 300L) {
            throw new ToLongException();
          }
        }
        finally {}
      }
      catch (ToLongException e)
      {
        e = 
        
          e;System.out.println("too long (index) : " + this.time + ")\t" + sparql + "\t");boolean done = false;
        for (StackTraceElement trace : e.getStackTrace())
        {
          if (done)
          {
            System.out.println(trace + "\n");
            
            break;
          }
          done = true;
        }
      }
      catch (Exception e)
      {
        e = 
        
          e;System.out.println("Fail to execute: " + sparql);e.printStackTrace();
      }
      finally {}
    }
  }
  
  private RemoteSPARQLPlugin startRemoteSPARQL()
    throws Exception
  {
    RemoteSPARQLPlugin remoteSPARQLPlugin = new RemoteSPARQLPlugin();
    remoteSPARQLPlugin.loadPlugin(new RepositoryVirtuoso(this.graphVirtuoso.getURL(), "", this.graphVirtuoso.getPort(), this.graphVirtuoso.getLogin(), this.graphVirtuoso.getPassword(), "virtuoso", this.graphVirtuoso.getURL() + ":" + this.graphVirtuoso.getPort() + "#" + this.graphVirtuoso.getGraphIRI(), ""));
    
    return remoteSPARQLPlugin;
  }
  
  private VirtuosoPlugin startVirtuoso()
    throws Exception
  {
    VirtuosoPlugin virtuosoPlugin = new VirtuosoPlugin();
    virtuosoPlugin.loadPlugin(new RepositoryVirtuoso(this.graphVirtuoso.getURL(), "", this.graphVirtuoso.getPort(), this.graphVirtuoso.getLogin(), this.graphVirtuoso.getPassword(), "virtuoso", this.graphVirtuoso.getURL() + ":" + this.graphVirtuoso.getPort() + "#" + this.graphVirtuoso.getGraphIRI(), ""));
    
    return virtuosoPlugin;
  }
  
  private boolean checkIfNumber(String in)
  {
    try
    {
      if (Character.isDigit(in.charAt(0))) {
        return true;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return false;
    }
    return false;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    ArrayList<GraphVirtuoso> graphList = new ArrayList();
    
    int[] limits = { 25, 20, 15, 10, 30 };
    
    graphList.add(new GraphVirtuoso("kmi-web03.open.ac.uk", "8890", "dba", "dba", "http://dbpedia.org", "OWL"));
    
    IndexManagerVirtuoso indexManagerVirtuoso = new IndexManagerVirtuoso(graphList, true);
    indexManagerVirtuoso.graphVirtuoso = ((GraphVirtuoso)graphList.get(0));
    indexManagerVirtuoso.execute(indexManagerVirtuoso.getSPARQL("film", ObjectType.Connector.AND, ObjectType.CLASS, 15), -1);
    indexManagerVirtuoso.execute(indexManagerVirtuoso.getSPARQL("star", ObjectType.Connector.AND, ObjectType.PROPERTY, 15), -1);
    indexManagerVirtuoso.execute(indexManagerVirtuoso.getSPARQL("Brad Pitt", ObjectType.Connector.AND, ObjectType.INSTANCE, 15), -1);
    indexManagerVirtuoso.execute(indexManagerVirtuoso.getSPARQL("Angelina Jolie", ObjectType.Connector.AND, ObjectType.LITERAL, 15), -1);
    indexManagerVirtuoso.execute(indexManagerVirtuoso.getSPARQL("Angelina Jolie", ObjectType.Connector.ONE_STRING, ObjectType.ALL, 15), -1);
    
    System.out.println("done");
    
    indexManagerVirtuoso = new IndexManagerVirtuoso(graphList);
  }
  
  public void setremoteSPARQLVirtuoso(boolean remoteSPARQLVirtuoso)
  {
    this.remoteSPARQLVirtuoso = remoteSPARQLVirtuoso;
  }
  
  public boolean isremoteSPARQLVirtuoso()
  {
    return this.remoteSPARQLVirtuoso;
  }
  
  public ArrayList<GraphVirtuoso> getGraphList()
  {
    return this.graphList;
  }
}

