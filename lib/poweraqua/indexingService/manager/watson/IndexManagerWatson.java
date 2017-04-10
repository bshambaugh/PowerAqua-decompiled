package poweraqua.indexingService.manager.watson;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import poweraqua.WordNetJWNL.WNSynsetSetBean;
import poweraqua.core.model.myrdfmodel.MyURI;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.model.myrdfmodel.RDFProperty;
import poweraqua.core.utils.StringUtils;
import poweraqua.indexingService.manager.IndexManager;
import poweraqua.powermap.elementPhase.EntityMappingTable;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.powermap.mappingModel.WordNetBean;
import uk.ac.open.kmi.watson.clientapi.EntityResult;
import uk.ac.open.kmi.watson.clientapi.EntitySearch;
import uk.ac.open.kmi.watson.clientapi.EntitySearchServiceLocator;
import uk.ac.open.kmi.watson.clientapi.SearchConf;
import uk.ac.open.kmi.watson.clientapi.SemanticContentResult;
import uk.ac.open.kmi.watson.clientapi.SemanticContentSearch;
import uk.ac.open.kmi.watson.clientapi.SemanticContentSearchServiceLocator;

public class IndexManagerWatson
  implements IndexManager
{
  private static float SEARCH_SCORE = 1.0F;
  private static int SEARCH_INCREMENT = 100;
  private static double SEARCH_THRESH = 0.219D;
  private static int SEARCH_LIMIT = 500;
  private static EntitySearch entitySearch;
  private static SemanticContentSearch semanticSearch;
  
  public IndexManagerWatson()
    throws IOException
  {
    System.out.println("INITIALIZING WATSON");
    if (entitySearch == null)
    {
      EntitySearchServiceLocator entityLocator = new EntitySearchServiceLocator();
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      try
      {
        entitySearch = entityLocator.getUrnEntitySearch();
        MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      }
      catch (Exception e)
      {
        e.printStackTrace();
        System.out.println(e.getCause());
      }
    }
    if (semanticSearch == null)
    {
      SemanticContentSearchServiceLocator locator = new SemanticContentSearchServiceLocator();
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      try
      {
        semanticSearch = locator.getUrnSemanticContentSearch();
        MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      }
      catch (Exception e)
      {
        e.printStackTrace();
        System.out.println(e.getCause());
      }
    }
  }
  
  public int listOntologies()
  {
    int count = 0;
    try
    {
      for (int i = 0; i < 30000; i += 1000)
      {
        String[] res = semanticSearch.listSemanticContents(i, i + 1000);
        for (String s : res)
        {
          count++;
          System.out.println(s);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.out.println(e.getCause());
    }
    return count;
  }
  
  public void openIndexForCreation(boolean addSynsetsToIndex) {}
  
  public void openIndexForUpload(boolean addSynsetsToIndex) {}
  
  public int getIndexType()
  {
    return 4;
  }
  
  public String getId()
  {
    return "watson";
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
  
  public EntityMappingTable searchRankEntityMappingsOnOntology(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    return searchRankEntityMappingsOnOntology(keyword, semanticRelation, thresh, searchType, true);
  }
  
  public EntityMappingTable searchRankEntityMappingsOnOntology(String keyword, String semanticRelation, float thresh, int searchType, boolean from_WN)
    throws Exception
  {
    System.out.println("Searching rank ontology mappings for " + keyword + " in watson");
    
    SearchConf conf = new SearchConf();
    int scopeModifier = 2 + 4;
    int entityTypeModifier = 1 + 2;
    conf.setScope(scopeModifier);
    conf.setEntities(entityTypeModifier);
    conf.setEntitiesInfo(1 + 2);
    int start = 0;
    
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    
    float sc = 1.0F;
    int total_mappings = 0;
    while ((sc > SEARCH_THRESH) && (start < SEARCH_LIMIT))
    {
      long time = System.currentTimeMillis();
      
      conf.setStart(start);
      conf.setInc(SEARCH_INCREMENT);
      if ((semanticRelation.equals("equivalentMatching")) || (!from_WN)) {
        conf.setMatch(1);
      } else {
        conf.setMatch(2);
      }
      EntityResult[] es = entitySearch.getAnyEntityByKeyword(keyword, conf);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      time = System.currentTimeMillis() - time;
      System.out.println("TIME PASSED: " + time + " ms");
      System.out.println("Watson is getting " + es.length + " mappings in the schema (start=" + start + ", increment=" + SEARCH_INCREMENT + ")");
      sc = 0.0F;
      if (es.length < SEARCH_INCREMENT) {
        start = SEARCH_LIMIT;
      } else {
        start += SEARCH_INCREMENT;
      }
      for (EntityResult e : es)
      {
        String entURI = e.getURI();
        String ontology = e.getSCURI();
        String type = e.getType();
        sc = new Float(e.getScore()).floatValue();
        if (sc < SEARCH_THRESH)
        {
          System.out.println("DISCARDING THE MAPPINGS WITH SCORE BELOW " + SEARCH_THRESH + "total mappings=" + total_mappings);
          
          return entityMappingTable;
        }
        String[] labels = e.getLabels();
        String label = null;
        if ((labels != null) && (labels.length > 0)) {
          label = labels[0];
        }
        if (type == null)
        {
          System.out.println("ERROR: Entity withouth type discarded ");
        }
        else
        {
          RDFEntity entity;
          RDFEntity entity;
          if (type.equalsIgnoreCase("class")) {
            entity = new RDFEntity("class", entURI, label == null ? MyURI.getLocalName(entURI) : label, ontology);
          } else {
            entity = new RDFProperty(entURI, label == null ? MyURI.getLocalName(entURI) : label, ontology);
          }
          SearchSemanticResult ssr = new SearchSemanticResult(entity, sc, semanticRelation, keyword);
          ssr.setWatson(true);
          entityMappingTable.addMapping(ssr);
          total_mappings += 1;
        }
      }
    }
    return entityMappingTable;
  }
  
  public EntityMappingTable searchRankEntityMappingsOnKnowledgeBase(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    System.out.println("Searching rank KB mappings for " + keyword + " in watson");
    
    SearchConf conf = new SearchConf();
    
    int entityTypeModifier = 4;
    
    int scopeModifier = 2 + 4;
    
    conf.setScope(scopeModifier);
    conf.setEntities(entityTypeModifier);
    conf.setEntitiesInfo(1 + 2);
    int start = 0;
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    
    float sc = 1.0F;
    int total_mappings = 0;
    while ((sc > SEARCH_THRESH) && (start < SEARCH_LIMIT))
    {
      long time = System.currentTimeMillis();
      
      conf.setStart(start);
      conf.setInc(SEARCH_INCREMENT);
      if (semanticRelation.equals("equivalentMatching")) {
        conf.setMatch(1);
      } else {
        conf.setMatch(2);
      }
      EntityResult[] es = entitySearch.getAnyEntityByKeyword(keyword, conf);
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      time = System.currentTimeMillis() - time;
      System.out.println("TIME PASSED: " + time + " ms");
      System.out.println("Watson is getting " + es.length + " mappings in the KB (start=" + start + ", increment=" + SEARCH_INCREMENT + ")");
      sc = 0.0F;
      if (es.length < SEARCH_INCREMENT) {
        start = SEARCH_LIMIT;
      } else {
        start += SEARCH_INCREMENT;
      }
      for (EntityResult e : es)
      {
        String entURI = e.getURI();
        String ontology = e.getSCURI();
        
        sc = new Float(e.getScore()).floatValue();
        if (sc < SEARCH_THRESH)
        {
          System.out.println("DISCARDING THE MAPPINGS WITH SCORE BELOW " + SEARCH_THRESH + "total mappings=" + total_mappings);
          
          return entityMappingTable;
        }
        String[] labels = e.getLabels();
        String label = null;
        if ((labels != null) && (labels.length > 0)) {
          label = labels[0];
        }
        RDFEntity entity = new RDFEntity("instance", entURI, label == null ? MyURI.getLocalName(entURI) : label, ontology);
        SearchSemanticResult ssr = new SearchSemanticResult(entity, sc, semanticRelation, keyword);
        ssr.setWatson(true);
        entityMappingTable.addMapping(ssr);
        total_mappings += 1;
      }
    }
    return entityMappingTable;
  }
  
  public EntityMappingTable searchEntityMappingsRestrictedByCoverage(String keyword, String semanticRelation, ArrayList<String> restrictedKeywords, String realPath)
    throws Exception
  {
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    
    restrictedKeywords = expandKeywords(restrictedKeywords, realPath);
    long time = System.currentTimeMillis();
    
    SearchConf conf = new SearchConf();
    int scopeModifier = 2 + 4;
    
    int entityTypeModifier = 4 + 1 + 2;
    conf.setScope(scopeModifier);
    conf.setEntities(entityTypeModifier);
    
    ArrayList<String> sc_ontologies = new ArrayList();
    String keyword2 = keyword.trim().replaceAll(" ", "-");
    for (String restrictionKeyword : restrictedKeywords)
    {
      String restrictionKeyword2 = restrictionKeyword.trim().replaceAll(" ", "-");
      String[] params = { keyword2, restrictionKeyword2 };
      if (!semanticRelation.equals("equivalentMatching")) {
        conf.setMatch(1);
      } else {
        conf.setMatch(2);
      }
      SemanticContentResult[] _auxList = semanticSearch.getSemanticContentByKeywords(params, conf);
      
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (SemanticContentResult aux : _auxList) {
        if (!sc_ontologies.contains(aux)) {
          sc_ontologies.add(aux.getURI());
        }
      }
      System.out.println(_auxList.length + " relevant ontologies found for " + keyword2 + " restricted to " + restrictionKeyword2);
    }
    int number_mappings = 0;
    System.out.println(sc_ontologies.size() + " relevant ontologies found in total");
    for (String sc_ontology : sc_ontologies)
    {
      int start = 0;int increment = 20;
      if (semanticRelation.equals("equivalentMatching")) {
        conf.setMatch(1);
      } else {
        conf.setMatch(2);
      }
      conf.setStart(start);
      conf.setInc(SEARCH_INCREMENT);
      conf.setEntitiesInfo(1 + 2);
      EntityResult[] entityResults2 = entitySearch.getEntitiesByKeyword(sc_ontology, keyword2, conf);
      ArrayList<String> avoid_repetitions = new ArrayList();
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      for (EntityResult entRes : entityResults2)
      {
        String entURI = entRes.getURI();
        String ontology = entRes.getSCURI();
        
        String type = entRes.getType();
        String[] labels = entRes.getLabels();
        String label = null;
        if ((labels != null) && (labels.length > 0)) {
          label = labels[0];
        }
        float sc = new Float(entRes.getScore()).floatValue();
        
        String is_repetition = ontology;
        if (type != null) {
          is_repetition.concat(type);
        }
        if (type == null)
        {
          System.out.println("ERROR: Entity withouth type discarded: " + entURI);
        }
        else if (!avoid_repetitions.contains(is_repetition))
        {
          avoid_repetitions.add(is_repetition);
          RDFEntity entity;
          RDFEntity entity;
          if (type.equalsIgnoreCase("class"))
          {
            entity = new RDFEntity("class", entURI, label == null ? MyURI.getLocalName(entURI) : label, ontology);
          }
          else
          {
            RDFEntity entity;
            if (type.equalsIgnoreCase("property")) {
              entity = new RDFProperty(entURI, label == null ? MyURI.getLocalName(entURI) : label, ontology);
            } else {
              entity = new RDFEntity("instance", entURI, label == null ? MyURI.getLocalName(entURI) : label, ontology);
            }
          }
          SearchSemanticResult ssr = new SearchSemanticResult(entity, sc, semanticRelation, keyword);
          ssr.setWatson(true);
          entityMappingTable.addMapping(ssr);
          number_mappings += 1;
        }
      }
    }
    time = System.currentTimeMillis() - time;
    System.out.println(number_mappings + "in TIME PASSED searching by coverage " + time);
    return entityMappingTable;
  }
  
  public ArrayList<SearchSemanticResult> searchLiteralMappingsOnKnowledgeBase(String keyword, String semanticRelation, float thresh)
    throws Exception
  {
    System.out.println("Searching for literals and labels of " + keyword);
    long time = System.currentTimeMillis();
    ArrayList<SearchSemanticResult> literals = new ArrayList();
    if (!semanticRelation.equals("equivalentMatching")) {
      return literals;
    }
    String[] params = { keyword };
    
    SearchConf conf = new SearchConf();
    int scopeModifier = 16;
    int entityTypeModifier = 4;
    conf.setScope(scopeModifier);
    conf.setEntities(entityTypeModifier);
    conf.setMatch(1);
    
    SemanticContentResult[] sc_ontologies = semanticSearch.getSemanticContentByKeywords(params, conf);
    
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    int total_mappings = 0;
    if (sc_ontologies != null) {
      for (SemanticContentResult sc_ontology : sc_ontologies)
      {
        System.out.println("Searching literals for " + sc_ontology.getDLExpressivness());
        String[][] literalsList = entitySearch.getLiteralsByKeyword(sc_ontology.getURI(), keyword);
        MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
        
        String rdf_label = "http://www.w3.org/2000/01/rdf-schema#label";
        String rdf_title = "http://purl.org/dc/elements/1.1/title";
        for (int i = 0; i < literalsList.length; i++)
        {
          String literal_label = literalsList[i][0];
          if (literal_label.length() < 40)
          {
            String instance_uri = literalsList[i][2];
            
            String property = literalsList[i][3];
            RDFEntity entity;
            RDFEntity entity;
            if ((property.equals(rdf_label)) || (property.equals(rdf_title)))
            {
              entity = new RDFEntity("instance", instance_uri, literal_label == null ? MyURI.getLocalName(instance_uri) : literal_label, sc_ontology.getURI());
            }
            else
            {
              entity = new RDFEntity("literal", instance_uri, literal_label, sc_ontology.getURI());
              entity.setRefers_to(new RDFProperty(property, getLabelOfEntity(sc_ontology.getURI(), property), sc_ontology.getURI()));
            }
            SearchSemanticResult ssr = new SearchSemanticResult(entity, new Float("1.0").floatValue(), semanticRelation, keyword);
            ssr.setWatson(true);
            literals.add(ssr);
            total_mappings += 1;
          }
        }
      }
    }
    time = System.currentTimeMillis() - time;
    float secs = (float)time / 1000.0F;
    System.out.println(total_mappings + " mappings: TIME PASSED SEARCHING LITERALS for " + keyword + " (" + time + ") :  " + secs + " secs");
    
    return literals;
  }
  
  public EntityMappingTable searchEntityMappingsOnOntology(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    System.out.println("Searching ontology mappings for " + keyword + " in watson");
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    
    String[] keywords = new String[1];
    keywords[0] = keyword;
    
    SemanticContentResult[] sc_ontologies = searchExactOntoLimit(keyword);
    System.out.println(sc_ontologies.length + " potential ontologies found in Watson");
    for (SemanticContentResult sc_ontology : sc_ontologies)
    {
      EntityResult[] entityResults = sc_ontology.getEntityResultList();
      System.out.println("Watson is getting " + entityResults.length + " mappings on " + sc_ontology.getURI());
      for (EntityResult entityResult : entityResults)
      {
        MappingSession.getLog_poweraqua().log(Level.INFO, "entity: \t" + entityResult.getType() + "::" + entityResult.getURI() + "(" + entityResult.getLabels() + ")");
        
        String ent_uri = entityResult.getURI();
        String[] ent_labels = entityResult.getLabels();
        
        String ent_label = null;
        if (ent_labels != null) {
          ent_label = ent_labels[0];
        }
        RDFEntity entity;
        RDFEntity entity;
        if (entityResult.getType().equalsIgnoreCase("class")) {
          entity = new RDFEntity("class", ent_uri, ent_label == null ? MyURI.getLocalName(ent_uri) : ent_label, sc_ontology.getURI());
        } else {
          entity = new RDFProperty(ent_uri, ent_label == null ? MyURI.getLocalName(ent_uri) : ent_label, sc_ontology.getURI());
        }
        SearchSemanticResult ssr = new SearchSemanticResult(entity, SEARCH_SCORE, semanticRelation, keyword);
        ssr.setWatson(true);
        entityMappingTable.addMapping(ssr);
      }
    }
    return entityMappingTable;
  }
  
  public EntityMappingTable searchEntityMappingsOnKnowledgeBase(String keyword, String semanticRelation, float thresh, int searchType)
    throws Exception
  {
    System.out.println("Searching instance (todo: literals) mappings for " + keyword + " in watson");
    EntityMappingTable entityMappingTable = new EntityMappingTable(keyword);
    
    String[] keywords = new String[1];
    keywords[0] = keyword;
    
    SemanticContentResult[] sc_ontologies = searchExactKBLimit(keyword);
    if (sc_ontologies == null)
    {
      System.out.println(" 0 ontologies found in Watson for " + keyword);
    }
    else
    {
      System.out.println(sc_ontologies.length + " potential ontologies found in Watson for " + keyword);
      for (SemanticContentResult sc_ontology : sc_ontologies)
      {
        String[] languages = sc_ontology.getLanguages();
        
        EntityResult[] entityResults = sc_ontology.getEntityResultList();
        System.out.println("Watson is getting " + entityResults.length + " mappings on " + sc_ontology.getURI());
        for (EntityResult entityResult : entityResults)
        {
          MappingSession.getLog_poweraqua().log(Level.INFO, "entity: \t" + entityResult.getType() + "::" + entityResult.getURI() + "(" + entityResult.getLabels() + ")");
          
          String ent_uri = entityResult.getURI();
          String[] ent_labels = entityResult.getLabels();
          String ent_label = null;
          if (ent_labels != null) {
            ent_label = ent_labels[0];
          }
          RDFEntity entity = new RDFEntity("instance", ent_uri, ent_label == null ? MyURI.getLocalName(ent_uri) : ent_label, sc_ontology.getURI());
          SearchSemanticResult ssr = new SearchSemanticResult(entity, SEARCH_SCORE, semanticRelation, keyword);
          ssr.setWatson(true);
          entityMappingTable.addMapping(ssr);
        }
      }
    }
    return entityMappingTable;
  }
  
  private ArrayList<String> expandKeywords(ArrayList<String> restrictedKeywords, String realPath)
  {
    ArrayList<String> keywords = new ArrayList();
    for (String keyword : restrictedKeywords)
    {
      keywords.add(keyword.toLowerCase());
      
      String lastToken = "";
      if (StringUtils.isCompound(keyword))
      {
        for (StringTokenizer st = new StringTokenizer(keyword); st.hasMoreTokens();) {
          lastToken = st.nextToken().toLowerCase();
        }
        if (!lastToken.equals("")) {
          keywords.add(lastToken);
        }
      }
      WordNetBean wnBean = new WordNetBean(keyword, realPath);
      if (!keywords.contains(wnBean.getPlural().toLowerCase())) {
        keywords.add(wnBean.getPlural().toLowerCase());
      }
      if (!keywords.contains(wnBean.getSingular().toLowerCase())) {
        keywords.add(wnBean.getSingular().toLowerCase());
      }
      if ((wnBean.getWN_lemma() != null) && (!keywords.contains(wnBean.getWN_lemma().toLowerCase()))) {
        keywords.add(wnBean.getWN_lemma().toLowerCase());
      }
      ArrayList<String> synonyms = wnBean.getSynonyms();
      for (String syn : synonyms)
      {
        syn = syn.replace("-", " ");
        if (!keywords.contains(syn.toLowerCase())) {
          keywords.add(syn.toLowerCase());
        }
      }
    }
    return keywords;
  }
  
  public RDFEntityList searchAllSuperClasses(RDFEntity entity)
  {
    RDFEntityList entityList = new RDFEntityList();
    try
    {
      String[] res = entitySearch.getAllSuperClasses(entity.getIdPlugin(), entity.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      if (res != null) {
        for (String s : res)
        {
          if (s.equals(entity.getURI())) {
            break;
          }
          RDFEntity c = new RDFEntity("class", s, "watsonToDo", entity.getIdPlugin());
          entityList.addRDFEntity(c);
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return entityList;
  }
  
  public RDFEntityList searchAllSubClasses(RDFEntity entity)
  {
    RDFEntityList entityList = new RDFEntityList();
    try
    {
      String[] res = entitySearch.getAllSubClasses(entity.getIdPlugin(), entity.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      if (res != null) {
        for (String s : res)
        {
          RDFEntity c = new RDFEntity("class", s, "watsonToDo", entity.getIdPlugin());
          entityList.getAllRDFEntities().add(c);
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return entityList;
  }
  
  public RDFEntityList searchDirectSuperClasses(RDFEntity entity)
  {
    RDFEntityList entityList = new RDFEntityList();
    try
    {
      String[] res = entitySearch.getSuperClasses(entity.getIdPlugin(), entity.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      if (res != null) {
        for (String s : res) {
          entityList.getAllRDFEntities().add(new RDFEntity("class", s, getLabelOfEntity(entity.getIdPlugin(), s), entity.getIdPlugin()));
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
  
  public RDFEntityList searchDirectSubClasses(RDFEntity entity)
  {
    RDFEntityList entityList = new RDFEntityList();
    try
    {
      String[] res = entitySearch.getSubClasses(entity.getIdPlugin(), entity.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      if (res != null) {
        for (String s : res) {
          entityList.addRDFEntity(new RDFEntity("class", s, getLabelOfEntity(entity.getIdPlugin(), s), entity.getIdPlugin()));
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
  
  public RDFEntityList searchDirectClassOfInstance(RDFEntity entity)
  {
    RDFEntityList entityList = new RDFEntityList();
    try
    {
      String[] instanceTypes = entitySearch.getClasses(entity.getIdPlugin(), entity.getURI());
      MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
      if (instanceTypes != null) {
        for (String instanceType : instanceTypes) {
          entityList.addRDFEntity(new RDFEntity("class", instanceType, getLabelOfEntity(entity.getIdPlugin(), instanceType), entity.getIdPlugin()));
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
  
  public RDFEntityList searchEquivalentEntities(RDFEntity entity)
  {
    return new RDFEntityList();
  }
  
  private String getLabelOfEntity(String ontologyURI, String entity_uri)
    throws Exception
  {
    String[] labels = entitySearch.getLabels(ontologyURI, entity_uri);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    if (labels == null) {
      return MyURI.getLocalName(entity_uri);
    }
    if (labels.length == 0) {
      return MyURI.getLocalName(entity_uri);
    }
    return labels[0];
  }
  
  public SemanticContentResult[] searchExactKBLimit(String keyword)
    throws Exception
  {
    String[] params = { keyword.toLowerCase() };
    
    SearchConf conf = new SearchConf();
    int scopeModifier = 2 + 4;
    int entityTypeModifier = 4;
    conf.setScope(scopeModifier);
    conf.setEntities(entityTypeModifier);
    conf.setMatch(2);
    SemanticContentResult[] _auxList = semanticSearch.getSemanticContentByKeywords(params, conf);
    
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    return _auxList;
  }
  
  public SemanticContentResult[] searchExactOntoLimit(String keyword)
    throws Exception
  {
    String[] params = { keyword.toLowerCase() };
    SearchConf conf = new SearchConf();
    int scopeModifier = 2 + 4;
    int entityTypeModifier = 1 + 2;
    conf.setScope(scopeModifier);
    conf.setEntities(entityTypeModifier);
    
    conf.setMatch(2);
    SemanticContentResult[] _auxList = semanticSearch.getSemanticContentByKeywords(params, conf);
    MappingSession.setWatsonCalls(MappingSession.getWatsonCalls() + 1);
    return _auxList;
  }
  
  public void displayResult(SemanticContentResult[] sr)
  {
    if (sr == null)
    {
      System.out.println("no results");
      return;
    }
    System.out.println("Number of results: " + sr.length);
    for (SemanticContentResult r : sr)
    {
      System.out.println("SC:: " + r.getURI());
      
      String[] languages = r.getLanguages();
      if (languages != null)
      {
        System.out.print("  Languages: ");
        for (String l : languages) {
          System.out.print(l + " ");
        }
        System.out.println();
      }
      String[] locations = r.getLocations();
      if (locations != null)
      {
        System.out.print("  Locations: ");
        for (String l : locations) {
          System.out.print(l + " ");
        }
        System.out.println();
      }
      EntityResult[] er = r.getEntityResultList();
      for (EntityResult e : er)
      {
        System.out.println("\t" + e.getType() + "::" + e.getURI() + "(" + e.getLabels() + ")");
        
        String[][] literals = e.getLiterals();
        if (literals != null) {
          for (String[] l : literals) {
            System.out.println("literals: \t\t" + l[1] + " = " + l[2]);
          }
        }
      }
    }
  }
  
  public static EntityMappingTable searchOnt(String key)
  {
    EntityMappingTable t = new EntityMappingTable(key);
    try
    {
      IndexManagerWatson imw = new IndexManagerWatson();
      t = imw.searchEntityMappingsOnOntology(key, "equivalentMatching", 1.0F, 2);
      System.out.println("AYYYYYYYYYYYY WATSON " + t);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return t;
  }
  
  public static EntityMappingTable searchKB(String key)
  {
    EntityMappingTable t = new EntityMappingTable(key);
    try
    {
      IndexManagerWatson imw = new IndexManagerWatson();
      t = imw.searchEntityMappingsOnKnowledgeBase(key, "equivalentMatching", 1.0F, 2);
      System.out.println("AYYYYYYYYYYYYYYY WATSON " + t);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return t;
  }
  
  public static void searchSuperClasses()
  {
    try
    {
      String idPlugin = "http://139.91.183.30:9090/RDF/VRP/Examples/tap.rdf";
      String entityURI = "http://kmi-web05.open.ac.uk:81/cache/e/aa9/02c9/cd6d9/318c843642/9e583ebfa89145d54#Sokoke_Cat";
      RDFEntity e = new RDFEntity("class", entityURI, null, idPlugin);
      IndexManagerWatson imw = new IndexManagerWatson();
      System.out.println("directsuperclasses + \n" + imw.searchDirectSuperClasses(e));
      System.out.println("superclasses + \n" + imw.searchAllSuperClasses(e));
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static void searchSubClasses()
  {
    try
    {
      String idPlugin = "http://139.91.183.30:9090/RDF/VRP/Examples/tap.rdf";
      String entityURI = "http://kmi-web05.open.ac.uk:81/cache/e/aa9/02c9/cd6d9/318c843642/9e583ebfa89145d54#Mammal";
      RDFEntity e = new RDFEntity("class", entityURI, null, idPlugin);
      IndexManagerWatson imw = new IndexManagerWatson();
      System.out.println("directSubClasse + \n" + imw.searchDirectSubClasses(e));
      System.out.println("subclasses + \n" + imw.searchAllSubClasses(e));
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static void searchEntityClasses()
  {
    try
    {
      String idPlugin = "http://social.semantic-web.at/wiki/index.php/Spezial:ExportRDF/Tassilo_Pellegrini?xmlmime=rdf";
      String entityURI = "http://sun.semantic-2Dweb.at/wiki/index.php/_Denny_Vrandecic";
      RDFEntity e = new RDFEntity("instance", entityURI, null, idPlugin);
      IndexManagerWatson imw = new IndexManagerWatson();
      System.out.println("directClass + \n" + imw.searchDirectClassOfInstance(e));
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static void main(String[] args)
  {
    try
    {
      IndexManagerWatson imw = new IndexManagerWatson();
      
      System.out.println("*********************************************");
      
      System.out.println("*********************************************");
      
      long time = System.currentTimeMillis();
      
      System.out.println("*********************************************");
      System.out.println("Search by exact coverage entity mappings");
      time = System.currentTimeMillis();
      ArrayList<String> restrictedKeywords = new ArrayList();
      restrictedKeywords.add("Black Sea");
      EntityMappingTable resKB = imw.searchEntityMappingsRestrictedByCoverage("river", "equivalentMatching", restrictedKeywords, "");
      
      time = System.currentTimeMillis() - time;
      System.out.println("TIME PASSED: " + time + " ms");
      
      System.out.println("*********************************************");
      
      System.out.println("*********************************************");
      
      System.out.println("*********************************************");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}

