package poweraqua.powermap.triplePhase;

import TrustEngine.PowerAquaTaxonomyProxy;
import it.essepuntato.trust.engine.ITaxonomyProxy;
import it.essepuntato.trust.engine.ITrustEngine;
import it.essepuntato.trust.engine.TrustEngine;
import it.essepuntato.trust.engine.exception.TrustEngineException;
import java.io.File;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import poweraqua.LinguisticComponent.LinguisticComponent;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.core.tripleModel.linguisticTriple.QueryTriple;
import poweraqua.core.tripleModel.ontologyTriple.OntoTriple;
import poweraqua.core.utils.StringUtils;
import poweraqua.fusion.FusedAnswerBean;
import poweraqua.fusion.FusionService;
import poweraqua.fusion.RDFEntityCluster;
import poweraqua.fusion.RDFEntityEntry;
import poweraqua.indexingService.manager.MultiIndexManager;
import poweraqua.powermap.elementPhase.EntityMappingTable;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.elementPhase.SemanticComponent;
import poweraqua.powermap.elementPhase.SyntacticComponent;
import poweraqua.powermap.mappingModel.MappingBean;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.powermap.mappingModel.RecyclingBean;
import poweraqua.powermap.mappingModel.WordNetBean;
import poweraqua.ranking.MappingRanking;
import poweraqua.ranking.SynsetClusterRanking;
import poweraqua.serviceConfig.MultiOntologyManager;

public class TripleSimilarityService
{
  private ArrayList<QueryTriple> queryTriples;
  private Hashtable<QueryTriple, TripleMappingTable> filteredOntoTripleMappings;
  private Hashtable<QueryTriple, TripleMappingTable> ontoKBTripleMappings;
  private Hashtable<String, ArrayList<MappingBean>> ontoCompoundMappings;
  private Hashtable<String, MappingBean> ontoTermMappings;
  private SynsetClusterRanking synsetClusterRanking;
  private Hashtable<QueryTriple, TripleMappingTable> ontoRecyclingMappings;
  private MappingSession mapSession;
  private ArrayList<String> sortedOntologies;
  private ArrayList<String> coverage_ontologies;
  private ArrayList<String> non_coverage_ontologies;
  
  public TripleSimilarityService(MappingSession mapSession, ArrayList<QueryTriple> queryTriples)
    throws Exception
  {
    this.queryTriples = queryTriples;
    this.mapSession = mapSession;
    initializeTripleSimilarityService();
  }
  
  public TripleSimilarityService(MappingSession mapSession)
  {
    this.mapSession = mapSession;
  }
  
  public TripleSimilarityService(ArrayList<QueryTriple> queryTriples)
    throws Exception
  {
    this.queryTriples = queryTriples;
    this.mapSession = new MappingSession();
    initializeTripleSimilarityService();
  }
  
  public TripleSimilarityService(ArrayList<QueryTriple> queryTriples, String real_path)
    throws Exception
  {
    this.queryTriples = queryTriples;
    this.mapSession = new MappingSession(real_path);
    initializeTripleSimilarityService();
  }
  
  public TripleSimilarityService(MappingSession mapSession, ArrayList<QueryTriple> queryTriples, boolean useWatson)
    throws Exception
  {
    this.queryTriples = queryTriples;
    this.mapSession = mapSession;
    this.mapSession.getMultiIndexManager().setWatson(useWatson);
    initializeTripleSimilarityService();
  }
  
  public TripleSimilarityService(ArrayList<QueryTriple> queryTriples, String real_path, boolean useWatson)
    throws Exception
  {
    this.queryTriples = queryTriples;
    this.mapSession = new MappingSession(real_path, useWatson);
    initializeTripleSimilarityService();
  }
  
  private void initializeTripleSimilarityService()
    throws Exception
  {
    this.sortedOntologies = new ArrayList();
    this.coverage_ontologies = new ArrayList();
    this.non_coverage_ontologies = new ArrayList();
    this.ontoCompoundMappings = new Hashtable();
    this.ontoRecyclingMappings = new Hashtable();
    this.ontoTermMappings = new Hashtable();
    
    this.ontoKBTripleMappings = new Hashtable();
    this.filteredOntoTripleMappings = new Hashtable();
    
    System.out.println(" CALLING COMPOUND MAPPING ");
    CompoundMapping();
    System.out.println(" CALLING TERM MAPPING ");
    TermMapping();
    this.sortedOntologies = sortByTermCoverage();
  }
  
  private void SynsetInterpretationClustering()
  {
    this.synsetClusterRanking = new SynsetClusterRanking(this.mapSession.getRealpath(), getOntoKBTripleMappings());
    
    getSynsetClusterRanking().Rank();
  }
  
  public void TermMapping()
    throws Exception
  {
    ArrayList<QueryTriple> queryTriples_compounds = new ArrayList();
    for (QueryTriple queryTriple : this.queryTriples)
    {
      int num_terms = queryTriple.getQueryTerm().size();
      for (int i = 0; i < num_terms; i++)
      {
        String queryterm = (String)queryTriple.getQueryTerm().get(i);
        if (!queryterm.equals("what_is"))
        {
          String mainQueryTerm = TermMapping(queryterm, queryTriples_compounds);
          if (!mainQueryTerm.equals(queryterm))
          {
            System.out.println("Modify the query Triple " + queryTriple.getQueryTerm());
            
            ArrayList<MappingBean> mapBeans = (ArrayList)this.ontoCompoundMappings.get(queryterm);
            if (mapBeans.size() == 2)
            {
              MappingBean secondQueryTerm = (MappingBean)mapBeans.get(mapBeans.size() - 2);
              queryTriple.getQueryTerm().add(secondQueryTerm.getKeyword());
            }
            queryTriple.getQueryTerm().set(i, mainQueryTerm);
            
            System.out.println("by splitting the compound " + queryTriple.getQueryTerm());
          }
        }
      }
      if ((queryTriple.getRelation() != null) && (!queryTriple.getRelation().isEmpty()) && (!queryTriple.getRelation().equals("IS_A_Relation")))
      {
        String relation = queryTriple.getRelation();
        String mainRelation = RelationMapping(relation);
        if (!mainRelation.equals(relation)) {
          queryTriple.setRelation(mainRelation);
        }
      }
      if (queryTriple.getSecondTerm() != null)
      {
        String secondTerm = queryTriple.getSecondTerm();
        String mainSecondTerm = TermMapping(secondTerm, queryTriples_compounds);
        if (!mainSecondTerm.equals(secondTerm))
        {
          ArrayList<MappingBean> mapBeans = (ArrayList)this.ontoCompoundMappings.get(secondTerm);
          if (queryTriple.getTypeQuestion() == 1)
          {
            System.out.println("Changing desciption query triple to a compound one...");
            queryTriple.setTypeQuestion(((QueryTriple)queryTriples_compounds.get(0)).getTypeQuestion());
            queryTriple.setQueryTerm(((QueryTriple)queryTriples_compounds.get(0)).getQueryTerm());
            queryTriple.setRelation(((QueryTriple)queryTriples_compounds.get(0)).getRelation());
            queryTriple.setSecondTerm(((QueryTriple)queryTriples_compounds.get(0)).getSecondTerm());
            queryTriples_compounds.remove(queryTriples_compounds.get(0));
          }
          else
          {
            queryTriple.setSecondTerm(mainSecondTerm);
            if (mapBeans.size() == 2)
            {
              MappingBean secondaryTerm = (MappingBean)mapBeans.get(mapBeans.size() - 2);
              MappingBean mainTermBean = (MappingBean)this.ontoTermMappings.get(mainSecondTerm);
              
              MappingBean secondTermBean = new MappingBean(mainTermBean.getKeyword(), mainTermBean.getRealpath(), false);
              for (String onto : mainTermBean.getEntityMappingTable().getOntologyIDMappings()) {
                secondTermBean.getEntityMappingTable().addMappingList(mainTermBean.getEntityMappingTable().getOntologyMappings(onto));
              }
              secondTermBean.mergeBean(secondaryTerm);
              queryTriple.setSecondTerm(secondTermBean.getKeyword());
              
              this.ontoTermMappings.put(secondTermBean.getKeyword(), secondTermBean);
            }
          }
        }
      }
      if (queryTriple.getThirdTerm() != null)
      {
        String thirdTerm = queryTriple.getThirdTerm();
        String mainThirdTerm = TermMapping(thirdTerm, queryTriples_compounds);
        if (!mainThirdTerm.equals(thirdTerm)) {
          queryTriple.setThirdTerm(mainThirdTerm);
        }
      }
    }
    this.queryTriples.addAll(queryTriples_compounds);
  }
  
  private String TermMapping(String queryTerm, ArrayList<QueryTriple> queryTriples_compounds)
    throws Exception
  {
    ArrayList<MappingBean> mapBeans = (ArrayList)this.ontoCompoundMappings.get(queryTerm);
    if (mapBeans.size() == 1)
    {
      if (!this.ontoTermMappings.contains(queryTerm)) {
        this.ontoTermMappings.put(queryTerm, mapBeans.get(0));
      }
      return queryTerm;
    }
    MappingBean mainQueryTerm = (MappingBean)mapBeans.get(mapBeans.size() - 1);
    if (!this.ontoTermMappings.contains(mainQueryTerm.getKeyword())) {
      this.ontoTermMappings.put(mainQueryTerm.getKeyword(), mainQueryTerm);
    }
    for (int i = 0; i < mapBeans.size() - 1; i++)
    {
      MappingBean mapBean = (MappingBean)mapBeans.get(i);
      if (!this.ontoTermMappings.contains(mapBean.getKeyword()))
      {
        mapBean.getEntityMappingTable().setExactMappingsToFalse();
        this.ontoTermMappings.put(mapBean.getKeyword(), mapBeans.get(i));
      }
      ArrayList<String> aux_qterm = new ArrayList();
      aux_qterm.add(mapBean.getKeyword());
      System.out.println("Creating a query triple compound <" + mapBean.getKeyword() + " -- " + mainQueryTerm.getKeyword() + ">");
      queryTriples_compounds.add(new QueryTriple(26, aux_qterm, null, mainQueryTerm.getKeyword()));
    }
    return mainQueryTerm.getKeyword();
  }
  
  private String RelationMapping(String relationTerm)
    throws Exception
  {
    ArrayList<MappingBean> mapBeans = (ArrayList)this.ontoCompoundMappings.get(relationTerm);
    if (mapBeans.size() == 1)
    {
      if (!this.ontoTermMappings.contains(relationTerm)) {
        this.ontoTermMappings.put(relationTerm, mapBeans.get(0));
      }
      return relationTerm;
    }
    if ((mapBeans.size() > 1) && 
      (!this.ontoTermMappings.contains(relationTerm))) {
      for (int i = 0; i < mapBeans.size(); i++)
      {
        MappingBean auxbean = (MappingBean)mapBeans.get(i);
        
        auxbean.getEntityMappingTable().setExactMappingsToFalse();
        if (i == 0)
        {
          this.ontoTermMappings.put(relationTerm, auxbean);
        }
        else
        {
          MappingBean allbeans = (MappingBean)this.ontoTermMappings.get(relationTerm);
          allbeans.mergeBean(auxbean);
          this.ontoTermMappings.put(relationTerm, allbeans);
        }
      }
    }
    return relationTerm;
  }
  
  public void CompoundMapping()
    throws Exception
  {
    for (QueryTriple queryTriple : this.queryTriples) {
      addMapping(queryTriple);
    }
  }
  
  public void addMapping(QueryTriple queryTriple)
    throws Exception
  {
    ArrayList<String> restrictedKeywords = new ArrayList();
    restrictedKeywords.add(queryTriple.getSecondTerm());
    if ((queryTriple.getThirdTerm() != null) && (!queryTriple.getThirdTerm().equals(""))) {
      restrictedKeywords.add(queryTriple.getThirdTerm());
    }
    for (String qterm : queryTriple.getQueryTerm()) {
      if (!qterm.equals("what_is")) {
        if ((queryTriple.getRelationFeature() != null) && (queryTriple.getRelationFeature().equalsIgnoreCase("where"))) {
          addMapping(qterm, false, true, restrictedKeywords);
        } else {
          addMapping(qterm, true, true, restrictedKeywords);
        }
      }
    }
    addMapping(queryTriple.getSecondTerm(), true, false, new ArrayList());
    if ((queryTriple.getRelation() != null) && (!queryTriple.getRelation().equals("IS_A_Relation"))) {
      addMapping(queryTriple.getRelation(), true, true, restrictedKeywords);
    }
    if (queryTriple.getThirdTerm() != null) {
      addMapping(queryTriple.getThirdTerm(), true, false, new ArrayList());
    }
  }
  
  public void addMapping(String queryTerm, boolean find_hypernyms, boolean is_queryTerm, ArrayList<String> restrictedKeywords)
    throws Exception
  {
    if ((queryTerm != null) && (!queryTerm.equals("")) && (!this.ontoCompoundMappings.containsKey(queryTerm)))
    {
      ArrayList<MappingBean> mappingBeans = new ArrayList();
      boolean isCompound = StringUtils.isCompound(queryTerm);
      MappingBean mappingBean;
      MappingBean mappingBean;
      if (restrictedKeywords == null) {
        mappingBean = new MappingBean(queryTerm, this.mapSession.getRealpath(), is_queryTerm);
      } else {
        mappingBean = new MappingBean(queryTerm, restrictedKeywords, this.mapSession.getRealpath(), is_queryTerm);
      }
      if (!find_hypernyms) {
        mappingBean.setFind_hypernyms(false);
      } else if (mappingBean.getWordNetBean().getPOS() != 1) {
        mappingBean.setFind_hypernyms(false);
      }
      mappingBeans.add(mappingBean);
      
      SyntacticComponent map = new SyntacticComponent(this.mapSession, mappingBeans);
      
      map.match();
      
      map.matchOntologyBackground();
      if ((map.isEmptyMapping()) && (isCompound))
      {
        ArrayList<MappingBean> mappingBeanSemantics = splitQueryTermCompound(queryTerm, restrictedKeywords, is_queryTerm);
        this.ontoCompoundMappings.put(queryTerm, mappingBeanSemantics);
      }
      else
      {
        SemanticComponent mapSemantic = new SemanticComponent(this.mapSession.getRealpath(), map.getMappingBeans());
        mapSemantic.addSemanticInfo();
        mapSemantic.closeOpenFileDescriptors();
        this.ontoCompoundMappings.put(queryTerm, mapSemantic.getMappingBeans());
      }
    }
  }
  
  public ArrayList<String> splitCompound(String queryTerm)
  {
    ArrayList<String> compounds = new ArrayList();
    String[] quotes = queryTerm.split("\"");
    for (String quote : quotes) {
      if (!queryTerm.contains("\"" + quote + "\""))
      {
        String[] elements = quote.split(" ");
        if (elements.length <= 1)
        {
          elements = quote.split("-");
          if (elements.length <= 1) {
            elements = quote.split("_");
          }
        }
        for (int i = 0; i < elements.length; i++) {
          compounds.add(elements[i].trim());
        }
      }
      else
      {
        compounds.add("\"" + quote + "\"");
      }
    }
    return compounds;
  }
  
  public ArrayList<String> sortByTermCoverage()
  {
    if (this.ontoTermMappings.isEmpty()) {
      return null;
    }
    Hashtable<String, Integer> ontologiesTable = new Hashtable();
    for (String queryTerm : this.ontoTermMappings.keySet())
    {
      MappingBean bean = (MappingBean)this.ontoTermMappings.get(queryTerm);
      for (String ontology : bean.getEntityMappingTable().getOntologyIDMappings()) {
        if (ontologiesTable.containsKey(ontology))
        {
          Integer value = new Integer(((Integer)ontologiesTable.get(ontology)).intValue() + 1);
          ontologiesTable.put(ontology, value);
        }
        else
        {
          ontologiesTable.put(ontology, Integer.valueOf(1));
        }
      }
    }
    Hashtable<Integer, ArrayList<String>> ontologiesFrecuency = new Hashtable();
    for (String ontology : ontologiesTable.keySet()) {
      if (ontologiesFrecuency.containsKey(ontologiesTable.get(ontology)))
      {
        ((ArrayList)ontologiesFrecuency.get(ontologiesTable.get(ontology))).add(ontology);
      }
      else
      {
        ArrayList<String> ontologies = new ArrayList();
        ontologies.add(ontology);
        ontologiesFrecuency.put(ontologiesTable.get(ontology), ontologies);
      }
    }
    ArrayList<String> finalOrder = new ArrayList();
    ArrayList<Integer> numericOrder = new ArrayList(ontologiesFrecuency.keySet());
    
    Collections.sort(numericOrder);
    Collections.reverse(numericOrder);
    for (Integer integer : numericOrder) {
      finalOrder.addAll((Collection)ontologiesFrecuency.get(integer));
    }
    return finalOrder;
  }
  
  public void TripleMapping()
    throws Exception
  {
    ArrayList<QueryTriple> aux = (ArrayList)this.queryTriples.clone();
    for (QueryTriple queryTriple : aux)
    {
      System.out.println("Triple Mapping of " + queryTriple.getQueryTerm() + "--" + queryTriple.getRelation() + "--" + queryTriple.getSecondTerm() + "--" + queryTriple.getThirdTerm());
      
      TripleMappingTable tripleMappingTable = new TripleMappingTable();
      switch (queryTriple.getTypeQuestion())
      {
      case 2: 
        System.out.println("WH_GENERICTERM");
        getWh_BasicOntoTriples(queryTriple);
        break;
      case 3: 
        System.out.println("WH_UNKNTERM");
        getWhIS_BasicOntoTriples(queryTriple);
        break;
      case 26: 
        System.out.println("COMPOUND");
        getCompound_BasicOntoTriples(queryTriple);
        break;
      case 4: 
        System.out.println("WH_UNKNREL");
        getCompound_BasicOntoTriples(queryTriple);
        break;
      case 27: 
        System.out.println("UNCLASSIFIED");
        if (queryTriple.getRelation() == null) {
          getCompound_BasicOntoTriples(queryTriple);
        } else {
          getWh_BasicOntoTriples(queryTriple);
        }
        break;
      case 1: 
        System.out.println("DESCRIPTION");
        getDesciption_BasicOntoTriples(queryTriple);
        break;
      case 5: 
        System.out.println("AFFIRMATIVE_NEGATIVE");
        getWh_BasicOntoTriples(queryTriple);
        break;
      case 6: 
        System.out.println("AFFIRM_NEG_PSEUDOREL");
        getCompound_BasicOntoTriples(queryTriple);
        break;
      case 14: 
        System.out.println("WH_3TERM");
        QueryTriple newQT = convert3TermQueryTriple(queryTriple);
        getWh_BasicOntoTriples(queryTriple);
        if (newQT.getTypeQuestion() == 29)
        {
          System.out.println("IS_A_ONLY");
          getIS_A_ONLY(newQT);
        }
        else
        {
          System.out.println("COMPOUND");
          getCompound_BasicOntoTriples(newQT);
        }
        break;
      case 11: 
        System.out.println("WH_3UNKNREL");
        QueryTriple newQTr = convert3TermQueryTriple(queryTriple);
        getCompound_BasicOntoTriples(queryTriple);
        if (newQTr.getTypeQuestion() == 29)
        {
          System.out.println("IS_A_ONLY");
          getIS_A_ONLY(newQTr);
        }
        else
        {
          System.out.println("COMPOUND");
          getCompound_BasicOntoTriples(newQTr);
        }
        break;
      case 13: 
        System.out.println("WH_UNKNTERM_2CLAUSE");
        QueryTriple newQTrip = convert3TermQueryTriple(queryTriple);
        getWhIS_BasicOntoTriples(queryTriple);
        if (newQTrip.getTypeQuestion() == 29)
        {
          System.out.println("IS_A_ONLY");
          getIS_A_ONLY(newQTrip);
        }
        else
        {
          System.out.println("COMPOUND");
          getCompound_BasicOntoTriples(newQTrip);
        }
        break;
      case 12: 
        System.out.println("WH_GENERIC_1TERMCLAUSE");
        
        System.out.println("Creating a new wh-generic query triple<" + queryTriple.getQueryTerm() + ", " + queryTriple.getRelation() + " , " + queryTriple.getThirdTerm());
        
        QueryTriple QTriple = new QueryTriple(2, queryTriple.getQueryTerm(), queryTriple.getRelation(), queryTriple.getThirdTerm());
        
        this.queryTriples.add(QTriple);
        getWh_BasicOntoTriples(QTriple);
        
        System.out.println("Modifying the query triple to <" + queryTriple.getQueryTerm().toString() + " -- " + queryTriple.getSecondTerm() + ">");
        
        queryTriple.setRelation(null);
        getCompound_BasicOntoTriples(queryTriple);
        break;
      case 18: 
        System.out.println("WH_COMB_AND");
        getWh_BasicOntoTriples(queryTriple);
        break;
      case 19: 
        System.out.println("WH_COMB_OR");
        if (queryTriple.getRelation() == null) {
          getCompound_BasicOntoTriples(queryTriple);
        } else {
          getWh_BasicOntoTriples(queryTriple);
        }
        break;
      case 20: 
        System.out.println("WH_COMB_COND");
        ArrayList<String> queryTermComp = new ArrayList();
        if (((String)queryTriple.getQueryTerm().get(0)).equals("what_is"))
        {
          getWhIS_BasicOntoTriples(queryTriple);
          queryTermComp.add(queryTriple.getRelation());
          queryTermComp.add(queryTriple.getSecondTerm());
        }
        else
        {
          getWh_BasicOntoTriples(queryTriple);
          queryTermComp.addAll(queryTriple.getQueryTerm());
          queryTermComp.add(queryTriple.getSecondTerm());
        }
        ((QueryTriple)this.queryTriples.get(1)).setQueryTerm(queryTermComp);
        ((QueryTriple)this.queryTriples.get(1)).setTypeQuestion(2);
        System.out.println("Modifying the query triple to <" + ((QueryTriple)this.queryTriples.get(1)).getQueryTerm().toString() + ", " + ((QueryTriple)this.queryTriples.get(1)).getRelation() + ", " + ((QueryTriple)this.queryTriples.get(1)).getSecondTerm() + ">");
        
        getWh_BasicOntoTriples((QueryTriple)this.queryTriples.get(1));
      }
    }
    aux = (ArrayList)this.queryTriples.clone();
    int j;
    if (aux.size() > 1)
    {
      j = 0;
      for (QueryTriple queryTriple : aux)
      {
        if (j > 0)
        {
          TripleMappingTable tmt = (TripleMappingTable)this.ontoKBTripleMappings.get(queryTriple);
          if (tmt.getMappingTable().isEmpty())
          {
            System.out.println("Eliminating query triple with no results: ");queryTriple.print();
            this.queryTriples.remove(queryTriple);
            this.ontoKBTripleMappings.remove(queryTriple);
          }
        }
        j++;
      }
    }
  }
  
  private boolean mappingsTaxonomicalyRelated(MappingBean parentMappingBeans, MappingBean sonMappingBeans)
  {
    EntityMappingTable parent_mappings = parentMappingBeans.getEntityMappingTable();
    EntityMappingTable son_mappings = sonMappingBeans.getEntityMappingTable();
    ArrayList<String> ontologies_parent = parent_mappings.getOntologyIDMappings();
    for (String ontology_son : son_mappings.getOntologyIDMappings()) {
      if (ontologies_parent.contains(ontology_son))
      {
        RSS_parents = parent_mappings.getOntologyMappings(ontology_son);
        for (SearchSemanticResult RSS_son : son_mappings.getOntologyMappings(ontology_son))
        {
          parents = RSS_son.getDirectParents();
          for (SearchSemanticResult RSS_parent : RSS_parents) {
            if (parents.isRDFEntityContained(RSS_parent.getEntity().getURI())) {
              return true;
            }
          }
        }
      }
    }
    ArrayList<SearchSemanticResult> RSS_parents;
    RDFEntityList parents;
    return false;
  }
  
  private QueryTriple convert3TermQueryTriple(QueryTriple queryTriple)
    throws Exception
  {
    MappingBean secondTermBeanrel = (MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm());
    MappingBean thirdTermBeanrel = (MappingBean)this.ontoTermMappings.get(queryTriple.getThirdTerm());
    
    ArrayList<String> queryTerms = new ArrayList();
    queryTerms.add(queryTriple.getSecondTerm());
    if (!((String)queryTriple.getQueryTerm().get(0)).equals("what_is")) {
      queryTerms.addAll(queryTriple.getQueryTerm());
    } else if ((queryTriple.getRelation() != null) && (!queryTriple.getRelation().isEmpty())) {
      queryTerms.add(queryTriple.getRelation());
    }
    System.out.println("Creating a query triple for the 3-terms <" + queryTerms.toString() + " -- " + queryTriple.getThirdTerm() + ">");
    
    QueryTriple newQT = new QueryTriple(4, queryTerms, null, queryTriple.getThirdTerm());
    
    boolean related = mappingsTaxonomicalyRelated(secondTermBeanrel, thirdTermBeanrel);
    if (related)
    {
      newQT.setTypeQuestion(29);
      newQT.setQueryTerm(queryTriple.getSecondTerm());
      queryTriple.setSecondTerm(thirdTermBeanrel.getKeyword());
    }
    this.queryTriples.add(newQT);
    return newQT;
  }
  
  public ArrayList<String> applyHypernymFilteringCriteriaOnDescription(QueryTriple queryTriple)
  {
    ArrayList<SearchSemanticResult> RDFSecondTerms = new ArrayList();
    
    ArrayList<String> ontologies = ((MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm())).getEntityMappingTable().getOntologyIDMappings();
    ArrayList<String> equivalentOntologies = new ArrayList();
    Iterator i$;
    if (queryTriple.getSecondTerm() != null) {
      for (i$ = ontologies.iterator(); i$.hasNext();)
      {
        ontologyID = (String)i$.next();
        RDFSecondTerms = ((MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm())).getEntityMappingTable().getOntologyMappings(ontologyID);
        if (RDFSecondTerms == null) {
          RDFSecondTerms = new ArrayList();
        }
        for (SearchSemanticResult RDFSecondTerm : RDFSecondTerms) {
          if ((RDFSecondTerm.getSemanticRelation().equals("equivalentMatching")) || (RDFSecondTerm.getSemanticRelation().equals("synonym")))
          {
            equivalentOntologies.add(ontologyID);
            break;
          }
        }
      }
    }
    String ontologyID;
    if (!equivalentOntologies.isEmpty()) {
      return equivalentOntologies;
    }
    return ontologies;
  }
  
  public void applyOntologyCoverageCriteria(QueryTriple queryTriple)
  {
    ArrayList<SearchSemanticResult> RDFQueryTerms = new ArrayList();
    ArrayList<SearchSemanticResult> RDFRelations = new ArrayList();
    ArrayList<SearchSemanticResult> RDFSecondTerms = new ArrayList();
    
    this.coverage_ontologies = new ArrayList();
    this.non_coverage_ontologies = new ArrayList();
    
    boolean secondTermMandatory = true;
    boolean queryTermMandatory = queryTriple.isQueryTermCoverage();
    for (String ontologyID : this.sortedOntologies)
    {
      RDFQueryTerms = new ArrayList();
      for (String queryTerm : queryTriple.getQueryTerm()) {
        if (!queryTerm.equals("what_is"))
        {
          ArrayList<SearchSemanticResult> SSRauxs = ((MappingBean)this.ontoTermMappings.get(queryTerm)).getEntityMappingTable().getOntologyMappings(ontologyID);
          if (SSRauxs != null) {
            RDFQueryTerms.addAll(SSRauxs);
          }
        }
      }
      if ((queryTriple.getRelation() != null) && (!queryTriple.getRelation().isEmpty()) && (!queryTriple.getRelation().equals("IS_A_Relation"))) {
        RDFRelations = ((MappingBean)this.ontoTermMappings.get(queryTriple.getRelation())).getEntityMappingTable().getOntologyMappings(ontologyID);
      }
      if (RDFRelations == null) {
        RDFRelations = new ArrayList();
      }
      if (queryTriple.getSecondTerm() != null) {
        RDFSecondTerms = ((MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm())).getEntityMappingTable().getOntologyMappings(ontologyID);
      }
      if (RDFSecondTerms == null) {
        RDFSecondTerms = new ArrayList();
      }
      if ((!secondTermMandatory) || (!RDFSecondTerms.isEmpty())) {
        if ((!RDFQueryTerms.isEmpty()) && (!RDFSecondTerms.isEmpty())) {
          this.coverage_ontologies.add(ontologyID);
        } else if ((!RDFSecondTerms.isEmpty()) && (!RDFRelations.isEmpty()) && ((!queryTermMandatory) || (((String)queryTriple.getQueryTerm().get(0)).equals("what_is")))) {
          this.coverage_ontologies.add(ontologyID);
        } else {
          this.non_coverage_ontologies.add(ontologyID);
        }
      }
    }
    if (secondTermMandatory)
    {
      ArrayList<String> ontologies_with_equivalent_secondterm = new ArrayList();
      ArrayList<String> ontologies_with_only_hypernym_secondterm = new ArrayList();
      for (String onto_covered : this.coverage_ontologies)
      {
        RDFSecondTerms = ((MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm())).getEntityMappingTable().getOntologyMappings(onto_covered);
        boolean equivalent = false;
        for (SearchSemanticResult secondTerm : RDFSecondTerms) {
          if (!secondTerm.isHypernym()) {
            equivalent = true;
          }
        }
        if (equivalent) {
          ontologies_with_equivalent_secondterm.add(onto_covered);
        } else {
          ontologies_with_only_hypernym_secondterm.add(onto_covered);
        }
      }
      if ((!ontologies_with_equivalent_secondterm.isEmpty()) && (!ontologies_with_only_hypernym_secondterm.isEmpty()))
      {
        System.out.println("The coverage ontologies " + ontologies_with_only_hypernym_secondterm.toString() + " has not equivalent mapping for the second term ");
        
        this.coverage_ontologies = ontologies_with_equivalent_secondterm;
        this.non_coverage_ontologies.addAll(ontologies_with_only_hypernym_secondterm);
      }
    }
    System.out.println(" Number of covering ontologies " + this.coverage_ontologies.size());
  }
  
  private boolean splitTriple(QueryTriple queryTriple)
  {
    boolean split = false;
    for (String queryTerm : queryTriple.getQueryTerm()) {
      if (!((MappingBean)this.ontoTermMappings.get(queryTerm)).getEntityMappingTable().isEmpty())
      {
        split = true;
        break;
      }
    }
    if ((split) && (!((MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm())).getEntityMappingTable().isEmpty())) {
      return true;
    }
    return false;
  }
  
  private ArrayList<OntoTripleBean> getWh_BasicOntoTriples(QueryTriple queryTriple, String ontologyID)
    throws Exception
  {
    OntologyPlugin osPlugin = this.mapSession.getMultiOntologyManager().getPlugin(ontologyID);
    
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    ArrayList<SearchSemanticResult> RDFQueryTerms = new ArrayList();
    ArrayList<SearchSemanticResult> RDFRelations = new ArrayList();
    ArrayList<SearchSemanticResult> RDFSecondTerms = new ArrayList();
    for (String queryTerm : queryTriple.getQueryTerm())
    {
      ArrayList<SearchSemanticResult> SSRs = ((MappingBean)this.ontoTermMappings.get(queryTerm)).getEntityMappingTable().getOntologyMappings(ontologyID);
      if (SSRs != null) {
        RDFQueryTerms.addAll(SSRs);
      }
    }
    if ((!queryTriple.getRelation().equals("IS_A_Relation")) && (!queryTriple.getRelation().isEmpty())) {
      RDFRelations = ((MappingBean)this.ontoTermMappings.get(queryTriple.getRelation())).getEntityMappingTable().getOntologyMappings(ontologyID);
    }
    RDFSecondTerms = ((MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm())).getEntityMappingTable().getOntologyMappings(ontologyID);
    if ((RDFSecondTerms != null) && (!RDFSecondTerms.isEmpty()))
    {
      boolean is_relation = false;
      if (queryTriple.getRelation().equals("IS_A_Relation")) {
        is_relation = true;
      }
      RelationSimilarityService RSSMainTriple;
      RelationSimilarityService RSSMainTriple;
      if (queryTriple.isCoverage_criteria())
      {
        RSSMainTriple = new RelationSimilarityService(osPlugin, queryTriple, RDFQueryTerms, RDFRelations, RDFSecondTerms, queryTriple.isCoverage_criteria(), is_relation, queryTriple.getTypeQuestion());
      }
      else
      {
        RelationSimilarityService RSSMainTriple;
        if ((RDFRelations != null) && (!RDFRelations.isEmpty())) {
          RSSMainTriple = new RelationSimilarityService(osPlugin, queryTriple, RDFQueryTerms, new ArrayList(), RDFSecondTerms, queryTriple.isCoverage_criteria(), is_relation, queryTriple.getTypeQuestion());
        } else {
          RSSMainTriple = new RelationSimilarityService(osPlugin, queryTriple, new ArrayList(), RDFRelations, RDFSecondTerms, queryTriple.isCoverage_criteria(), is_relation, queryTriple.getTypeQuestion());
        }
      }
      System.out.println("calling relation matching for " + ontologyID);
      ontoTripleBeans = RSSMainTriple.RelationMatching();
      ArrayList<OntoTripleBean> discardedTripleBeans = RSSMainTriple.getDiscardedTripleBeans();
      addOntoRecyclingTriples(queryTriple, ontologyID, discardedTripleBeans);
      if (queryTriple.isCoverage_criteria()) {
        this.non_coverage_ontologies.addAll(RSSMainTriple.getNonCoverageOntologies());
      } else {
        System.out.println("No triples found for " + ontologyID);
      }
    }
    else
    {
      System.out.println("The second term can not be mapped in " + ontologyID);
    }
    return ontoTripleBeans;
  }
  
  private void getWh_BasicOntoTriples(QueryTriple queryTriple)
    throws Exception
  {
    TripleMappingTable tripleMappingTable = new TripleMappingTable();
    
    applyOntologyCoverageCriteria(queryTriple);
    
    queryTriple.setQueryTermCoverage(false);
    if (this.coverage_ontologies.isEmpty()) {
      System.out.println(" NONE ONTOLOGY FULLFILL THE COVERAGE CRITERIA - Trying splitting compounds and partial mappings ");
    }
    for (String ontologyID : this.coverage_ontologies)
    {
      ArrayList<OntoTripleBean> ontoTripleBeans = getWh_BasicOntoTriples(queryTriple, ontologyID);
      if (!ontoTripleBeans.isEmpty()) {
        tripleMappingTable.addOntologyTriples(ontologyID, ontoTripleBeans);
      }
    }
    TripleMappingTable kbmappingtriples = filterOntoKBTripleMappings(queryTriple, tripleMappingTable);
    if (kbmappingtriples.getMappingTable().isEmpty())
    {
      if ((queryTriple.getQueryTerm().size() == 1) && (StringUtils.isCompound((String)queryTriple.getQueryTerm().get(0))) && (!((MappingBean)getOntoTermMappings().get(queryTriple.getQueryTerm().get(0))).getEntityMappingTable().isMergedKeyword()))
      {
        ArrayList<String> restrictedKeywords = ((MappingBean)getOntoTermMappings().get(queryTriple.getQueryTerm().get(0))).getEntityMappingTable().getRestrictedKeywords();
        ArrayList<QueryTriple> compoundTriples = splitQueryTripleCompound(queryTriple, true, restrictedKeywords, true);
        if (compoundTriples != null)
        {
          System.out.println(" FINDING TRIPLE MAPPINGS FOR SECOND TIME: splitting the query term ********** ");
          if (compoundTriples.isEmpty())
          {
            queryTriple.setQueryTermCoverage(true);
            getWh_BasicOntoTriples(queryTriple);
            return;
          }
          if (compoundTriples.size() == 1)
          {
            System.out.println("Triple Mapping of " + queryTriple.getQueryTerm() + "--" + queryTriple.getRelation() + "--" + queryTriple.getSecondTerm());
            getWh_BasicOntoTriples(queryTriple);
            kbmappingtriples = (TripleMappingTable)this.ontoKBTripleMappings.get(queryTriple);
            if (!kbmappingtriples.getMappingTable().isEmpty())
            {
              System.out.println("Triple Mapping of " + ((QueryTriple)compoundTriples.get(0)).getQueryTerm() + "--" + ((QueryTriple)compoundTriples.get(0)).getRelation() + "--" + ((QueryTriple)compoundTriples.get(0)).getSecondTerm());
              getCompound_BasicOntoTriples((QueryTriple)compoundTriples.get(0));
            }
            else
            {
              getQueryTriples().removeAll(compoundTriples);
            }
            return;
          }
          System.out.println("TODO for more than 1 triples compounds!!!!");
        }
      }
      if ((StringUtils.isCompound(queryTriple.getSecondTerm())) && (!((MappingBean)getOntoTermMappings().get(queryTriple.getSecondTerm())).getEntityMappingTable().isMergedKeyword()))
      {
        String original_secondTerm = queryTriple.getSecondTerm();
        
        ArrayList<QueryTriple> compoundTriples = splitQueryTripleCompound(queryTriple, false, new ArrayList(), false);
        if (compoundTriples != null)
        {
          System.out.println(" FINDING TRIPLE MAPPINGS FOR SECOND TIME: spliting the second term ********** ");
          if (compoundTriples.isEmpty())
          {
            queryTriple.setCoverage_criteria(true);
            queryTriple.setSplitting(true);
            getWh_BasicOntoTriples(queryTriple);
            kbmappingtriples = (TripleMappingTable)this.ontoKBTripleMappings.get(queryTriple);
          }
          else if (compoundTriples.size() == 1)
          {
            queryTriple.setCoverage_criteria(true);
            queryTriple.setSplitting(true);
            System.out.println("Triple Mapping of " + queryTriple.getQueryTerm() + "--" + queryTriple.getRelation() + "--" + queryTriple.getSecondTerm());
            getWh_BasicOntoTriples(queryTriple);
            kbmappingtriples = (TripleMappingTable)this.ontoKBTripleMappings.get(queryTriple);
            if (!kbmappingtriples.getMappingTable().isEmpty())
            {
              System.out.println("Triple Mapping of " + ((QueryTriple)compoundTriples.get(0)).getQueryTerm() + "--" + ((QueryTriple)compoundTriples.get(0)).getRelation() + "--" + ((QueryTriple)compoundTriples.get(0)).getSecondTerm());
              getCompound_BasicOntoTriples((QueryTriple)compoundTriples.get(0));
            }
          }
          else
          {
            System.out.println("TODO for more than one compound triple!!!!!! ");
          }
        }
        if (kbmappingtriples.getMappingTable().isEmpty())
        {
          int i = getQueryTriples().indexOf(queryTriple);
          ((QueryTriple)getQueryTriples().get(i)).setSecondTerm(original_secondTerm);
          queryTriple.setSecondTerm(original_secondTerm);
          if (compoundTriples != null) {
            getQueryTriples().removeAll(compoundTriples);
          }
          queryTriple.setCoverage_criteria(false);
          queryTriple.setSplitting(false);
        }
        else
        {
          return;
        }
      }
      else if (!((MappingBean)getOntoTermMappings().get(queryTriple.getSecondTerm())).getEntityMappingTable().isMergedKeyword())
      {
        queryTriple.setCoverage_criteria(false);
        queryTriple.setSplitting(false);
      }
      if ((kbmappingtriples.getMappingTable().isEmpty()) && (!queryTriple.isSplitting()))
      {
        queryTriple.setCoverage_criteria(false);
        System.out.println("The ontologies with better coverage did not produce any valid onto triple");
        System.out.println("Trying ontologies with partial coverage");
        System.out.println("Triple Mapping of " + queryTriple.getQueryTerm() + "--" + queryTriple.getRelation() + "--" + queryTriple.getSecondTerm());
        for (String ontologyID : this.non_coverage_ontologies)
        {
          ArrayList<OntoTripleBean> ontoTripleBeans = getWh_BasicOntoTriples(queryTriple, ontologyID);
          if (!ontoTripleBeans.isEmpty()) {
            tripleMappingTable.addOntologyTriples(ontologyID, ontoTripleBeans);
          }
        }
      }
    }
    kbmappingtriples = filterOntoKBTripleMappings(queryTriple, tripleMappingTable);
    this.ontoKBTripleMappings.put(queryTriple, kbmappingtriples);
  }
  
  private QueryTriple splitRelationCompound(QueryTriple queryTriple)
    throws Exception
  {
    if ((!queryTriple.getRelation().equals("IS_A_Relation")) && (StringUtils.isCompound(queryTriple.getRelation())))
    {
      System.out.println("Splitting the relation in its compounds");
      ArrayList<String> restrictedKeywords = ((MappingBean)this.ontoTermMappings.get(queryTriple.getRelation())).getEntityMappingTable().getRestrictedKeywords();
      
      ArrayList<String> relationCompounds = splitCompound(queryTriple.getRelation());
      
      String lastCompound = (String)relationCompounds.get(relationCompounds.size() - 1);
      if (lastCompound.equals(queryTriple.getRelation())) {
        return null;
      }
      MappingBean mainlemma = (MappingBean)this.ontoTermMappings.get(lastCompound);
      if (mainlemma == null)
      {
        SyntacticComponent sc_tmp = new SyntacticComponent(this.mapSession);
        mainlemma = sc_tmp.matchSplittedKeyword(new MappingBean(lastCompound, restrictedKeywords, this.mapSession.getRealpath(), true));
        if (mainlemma.getEntityMappingTable().isEmpty())
        {
          System.out.println("Splittong the relation produces no results");
          return null;
        }
        mainlemma.getEntityMappingTable().setExactMappingsToFalse();
        this.ontoTermMappings.put(lastCompound, mainlemma);
      }
      queryTriple.setRelation(lastCompound);
      return queryTriple;
    }
    return null;
  }
  
  private MappingBean ISACompound(EntityMappingTable entTable_compound, EntityMappingTable entTable_parents, String termC, boolean is_queryTerm)
  {
    if ((!entTable_compound.isEmpty()) || (!entTable_parents.isEmpty()))
    {
      MappingBean mapISA = new MappingBean(termC, this.mapSession.getRealpath(), is_queryTerm);
      ArrayList<String> ontologies_parent = entTable_parents.getOntologyIDMappings();
      for (Iterator i$ = entTable_compound.getOntologyIDMappings().iterator(); i$.hasNext();)
      {
        onto_comp = (String)i$.next();
        if (ontologies_parent.contains(onto_comp)) {
          for (SearchSemanticResult RSS_comp : entTable_compound.getOntologyMappings(onto_comp)) {
            if (RSS_comp.isExact())
            {
              RDFEntityList parents = RSS_comp.getDirectParents();
              parents.addAllRDFEntity(RSS_comp.getSuperclasses());
              boolean son_father = false;
              for (SearchSemanticResult RSS_parent : entTable_parents.getOntologyMappings(onto_comp)) {
                if (parents.isRDFEntityContained(RSS_parent.getEntity().getURI()))
                {
                  son_father = true;
                  break;
                }
              }
              if (son_father)
              {
                mapISA.getEntityMappingTable().addMapping(RSS_comp);
                if (RSS_comp.isExact())
                {
                  ArrayList<SearchSemanticResult> eliminatedString = mapISA.getEntityMappingTable().filterExactMappings(RSS_comp.getEntity().getIdPlugin());
                  if (!eliminatedString.isEmpty()) {
                    mapISA.getRecyclingBean().addStringRecyclingMapping(eliminatedString);
                  }
                }
              }
            }
          }
        }
      }
      String onto_comp;
      if (!mapISA.getEntityMappingTable().isEmpty())
      {
        mapISA.getEntityMappingTable().setIs_ISACompound(true);
        
        this.ontoTermMappings.put(termC, mapISA);
        return mapISA;
      }
    }
    return null;
  }
  
  private ArrayList<MappingBean> splitQueryTermCompound(String termC, ArrayList<String> restrictedKeywords, boolean is_queryTerm)
    throws Exception
  {
    ArrayList<MappingBean> mappingBeans = new ArrayList();
    
    ArrayList<String> compoundTerms = splitCompound(termC);
    
    boolean secondIteration = false;
    boolean splitbean_compound = false;
    boolean splitmain_compound = false;
    if (getOntoTermMappings().get(termC) != null) {
      secondIteration = ((MappingBean)getOntoTermMappings().get(termC)).getEntityMappingTable().isIs_ISACompound();
    }
    if (compoundTerms.size() == 2)
    {
      MappingBean bean_compound = (MappingBean)this.ontoTermMappings.get(compoundTerms.get(0));
      if ((bean_compound == null) && (!((String)compoundTerms.get(0)).equals("main")))
      {
        SyntacticComponent sc_tmp = new SyntacticComponent(this.mapSession);
        if (!restrictedKeywords.isEmpty())
        {
          restrictedKeywords.add(compoundTerms.get(1));
          restrictedKeywords.remove(compoundTerms.get(0));
        }
        bean_compound = sc_tmp.matchSplittedKeyword(new MappingBean((String)compoundTerms.get(0), restrictedKeywords, this.mapSession.getRealpath(), false));
        SemanticComponent semantic_main = new SemanticComponent(this.mapSession.getRealpath(), bean_compound);
        semantic_main.addSemanticInfo();
        
        splitbean_compound = true;
        this.ontoTermMappings.put(compoundTerms.get(0), bean_compound);
        semantic_main.closeOpenFileDescriptors();
      }
      MappingBean bean_main = (MappingBean)this.ontoTermMappings.get(compoundTerms.get(1));
      if (bean_main == null)
      {
        SyntacticComponent sc_tmp = new SyntacticComponent(this.mapSession);
        if (!restrictedKeywords.isEmpty())
        {
          restrictedKeywords.add(compoundTerms.get(0));
          restrictedKeywords.remove(compoundTerms.get(1));
        }
        bean_main = sc_tmp.matchSplittedKeyword(new MappingBean((String)compoundTerms.get(1), restrictedKeywords, this.mapSession.getRealpath(), is_queryTerm));
        SemanticComponent semantic_main = new SemanticComponent(this.mapSession.getRealpath(), bean_main);
        semantic_main.addSemanticInfo();
        
        splitmain_compound = true;
        this.ontoTermMappings.put(compoundTerms.get(1), bean_main);
        semantic_main.closeOpenFileDescriptors();
      }
      if ((!secondIteration) && (!((String)compoundTerms.get(0)).equals("main")))
      {
        EntityMappingTable entTable_compound = bean_compound.getEntityMappingTable();
        EntityMappingTable entTable_parents = bean_main.getEntityMappingTable();
        MappingBean mapISA = ISACompound(entTable_compound, entTable_parents, termC, is_queryTerm);
        if (mapISA != null)
        {
          mappingBeans.add(mapISA);
          return mappingBeans;
        }
        mapISA = ISACompound(entTable_parents, entTable_compound, termC, is_queryTerm);
        if (mapISA != null)
        {
          mappingBeans.add(mapISA);
          return mappingBeans;
        }
      }
      if (splitbean_compound) {
        bean_compound.getEntityMappingTable().setExactMappingsToFalse();
      }
      if (splitmain_compound) {
        bean_main.getEntityMappingTable().setExactMappingsToFalse();
      }
      if (!((String)compoundTerms.get(0)).equals("main")) {
        mappingBeans.add(bean_compound);
      }
      mappingBeans.add(bean_main);
    }
    else
    {
      mappingBeans = new ArrayList();
      int i = 0;
      SyntacticComponent sc_tmp = new SyntacticComponent(this.mapSession);
      while (i < compoundTerms.size())
      {
        String compound = (String)compoundTerms.get(i);
        if (i == compoundTerms.size() - 1)
        {
          MappingBean mappingBean = sc_tmp.matchSplittedKeyword(new MappingBean(compound, this.mapSession.getRealpath(), is_queryTerm));
          SemanticComponent semantic_main = new SemanticComponent(this.mapSession.getRealpath(), mappingBean);
          semantic_main.addSemanticInfo();
          mappingBean.getEntityMappingTable().setExactMappingsToFalse();
          this.ontoTermMappings.put(compound, mappingBean);
          semantic_main.closeOpenFileDescriptors();
          mappingBeans.add(mappingBean);
        }
        else
        {
          String nextCompound = (String)compoundTerms.get(i + 1);
          String mergeCompound = compound + " " + nextCompound;
          MappingBean mappingBean = sc_tmp.matchSplittedKeyword(new MappingBean(mergeCompound, this.mapSession.getRealpath(), is_queryTerm));
          if (!mappingBean.getEntityMappingTable().isEmpty())
          {
            SemanticComponent semantic_main = new SemanticComponent(this.mapSession.getRealpath(), mappingBean);
            semantic_main.addSemanticInfo();
            mappingBean.getEntityMappingTable().setExactMappingsToFalse();
            this.ontoTermMappings.put(mergeCompound, mappingBean);
            semantic_main.closeOpenFileDescriptors();
            mappingBeans.add(mappingBean);
            i++;
          }
          else
          {
            mappingBean = sc_tmp.matchSplittedKeyword(new MappingBean(compound, this.mapSession.getRealpath(), is_queryTerm));
            SemanticComponent semantic_main = new SemanticComponent(this.mapSession.getRealpath(), mappingBean);
            semantic_main.addSemanticInfo();
            mappingBean.getEntityMappingTable().setExactMappingsToFalse();
            this.ontoTermMappings.put(compound, mappingBean);
            semantic_main.closeOpenFileDescriptors();
            mappingBeans.add(mappingBean);
          }
        }
        i++;
      }
    }
    return mappingBeans;
  }
  
  private ArrayList<QueryTriple> splitQueryTripleCompound(QueryTriple queryTriple, boolean split_query_term, ArrayList<String> restrictedKeywords, boolean isqueryTerm)
    throws Exception
  {
    ArrayList<QueryTriple> queryTripleCompounds = new ArrayList();
    ArrayList<MappingBean> mappingBeans;
    ArrayList<MappingBean> mappingBeans;
    if (split_query_term)
    {
      System.out.println("Splitting the query term in its compounds");
      mappingBeans = splitQueryTermCompound((String)queryTriple.getQueryTerm().get(0), restrictedKeywords, isqueryTerm);
    }
    else
    {
      System.out.println("Splitting the second term in its compounds");
      mappingBeans = splitQueryTermCompound(queryTriple.getSecondTerm(), restrictedKeywords, isqueryTerm);
    }
    if (mappingBeans == null) {
      return null;
    }
    if (mappingBeans.size() == 1)
    {
      if (((MappingBean)mappingBeans.get(0)).getEntityMappingTable().isEmpty()) {
        return null;
      }
      if (split_query_term)
      {
        queryTriple.setQueryTerm(((MappingBean)mappingBeans.get(0)).getKeyword());
      }
      else
      {
        queryTriple.setSecondTerm(((MappingBean)mappingBeans.get(0)).getKeyword());
        return queryTripleCompounds;
      }
    }
    else if (mappingBeans.size() == 2)
    {
      MappingBean bean_main = (MappingBean)mappingBeans.get(1);
      MappingBean bean_compound = (MappingBean)mappingBeans.get(0);
      if ((bean_compound == null) || (bean_main == null))
      {
        System.out.println("splitting is not possible");
        return null;
      }
      if ((bean_compound.getEntityMappingTable().isEmpty()) || (bean_main.getEntityMappingTable().isEmpty()))
      {
        System.out.println("splitting is not possible");
        return null;
      }
      QueryTriple compoundTriple = new QueryTriple(26, bean_compound.getKeyword(), null, bean_main.getKeyword());
      
      bean_main.getEntityMappingTable().setExactMappingsToFalse();
      bean_compound.getEntityMappingTable().setExactMappingsToFalse();
      int i = getQueryTriples().indexOf(queryTriple);
      if (split_query_term)
      {
        ArrayList<String> bean_mains = new ArrayList();
        bean_mains.add(bean_main.getKeyword());
        bean_mains.add(bean_compound.getKeyword());
        ((QueryTriple)getQueryTriples().get(i)).setQueryTerm(bean_mains);
        queryTriple.setQueryTerm(bean_mains);
      }
      else
      {
        MappingBean secondTermBean = new MappingBean(bean_main.getKeyword(), bean_main.getRealpath(), isqueryTerm);
        for (String onto : bean_main.getEntityMappingTable().getOntologyIDMappings()) {
          secondTermBean.getEntityMappingTable().addMappingList(bean_main.getEntityMappingTable().getOntologyMappings(onto));
        }
        secondTermBean.mergeBean(bean_compound);
        this.ontoTermMappings.put(secondTermBean.getKeyword(), secondTermBean);
        ((QueryTriple)getQueryTriples().get(i)).setSecondTerm(secondTermBean.getKeyword());
        queryTriple.setSecondTerm(secondTermBean.getKeyword());
      }
      getQueryTriples().add(compoundTriple);
      queryTripleCompounds.add(compoundTriple);
    }
    else
    {
      System.out.println("TODOOOOOOOOOOO: MORE THAN 3 COMPOUNDS !!!!!!!!!!!!!!!!!!!!!!!!!!");
      return null;
    }
    return queryTripleCompounds;
  }
  
  private ArrayList<OntoTripleBean> getCompound_BasicOntoTriples(QueryTriple queryTriple, String ontologyID)
    throws Exception
  {
    OntologyPlugin osPlugin = this.mapSession.getMultiOntologyManager().getPlugin(ontologyID);
    
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    ArrayList<SearchSemanticResult> RDFQueryTerms = new ArrayList();
    ArrayList<SearchSemanticResult> RDFRelations = new ArrayList();
    ArrayList<SearchSemanticResult> RDFSecondTerms = new ArrayList();
    for (String queryTerm : queryTriple.getQueryTerm())
    {
      ArrayList<SearchSemanticResult> SSRs = ((MappingBean)this.ontoTermMappings.get(queryTerm)).getEntityMappingTable().getOntologyMappings(ontologyID);
      if (SSRs != null) {
        RDFQueryTerms.addAll(SSRs);
      }
    }
    RDFSecondTerms = ((MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm())).getEntityMappingTable().getOntologyMappings(ontologyID);
    RelationSimilarityService RSSMainTriple;
    RelationSimilarityService RSSMainTriple;
    if (queryTriple.getTypeQuestion() == 26)
    {
      RSSMainTriple = new RelationSimilarityService(osPlugin, queryTriple, RDFSecondTerms, RDFRelations, RDFQueryTerms, queryTriple.isCoverage_criteria(), true, queryTriple.getTypeQuestion());
    }
    else
    {
      RelationSimilarityService RSSMainTriple;
      if (queryTriple.isCoverage_criteria()) {
        RSSMainTriple = new RelationSimilarityService(osPlugin, queryTriple, RDFQueryTerms, RDFRelations, RDFSecondTerms, queryTriple.isCoverage_criteria(), false, queryTriple.getTypeQuestion());
      } else {
        RSSMainTriple = new RelationSimilarityService(osPlugin, queryTriple, new ArrayList(), RDFRelations, RDFSecondTerms, queryTriple.isCoverage_criteria(), false, queryTriple.getTypeQuestion());
      }
    }
    System.out.println("calling relation matching for " + ontologyID);
    ontoTripleBeans = RSSMainTriple.RelationMatching();
    ArrayList<OntoTripleBean> discardedTripleBeans = RSSMainTriple.getDiscardedTripleBeans();
    if (queryTriple.isCoverage_criteria()) {
      this.non_coverage_ontologies.addAll(RSSMainTriple.getNonCoverageOntologies());
    }
    addOntoRecyclingTriples(queryTriple, ontologyID, discardedTripleBeans);
    return ontoTripleBeans;
  }
  
  private void getIS_A_ONLY(QueryTriple queryTriple)
    throws Exception
  {
    TripleMappingTable tripleMappingTable = new TripleMappingTable();
    applyOntologyCoverageCriteria(queryTriple);
    for (String ontologyID : this.coverage_ontologies)
    {
      OntologyPlugin osPlugin = this.mapSession.getMultiOntologyManager().getPlugin(ontologyID);
      ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
      ArrayList<SearchSemanticResult> RDFQueryTerms = new ArrayList();
      ArrayList<SearchSemanticResult> RDFRelations = new ArrayList();
      ArrayList<SearchSemanticResult> RDFSecondTerms = new ArrayList();
      for (String queryTerm : queryTriple.getQueryTerm())
      {
        ArrayList<SearchSemanticResult> SSRs = ((MappingBean)this.ontoTermMappings.get(queryTerm)).getEntityMappingTable().getOntologyMappings(ontologyID);
        if (SSRs != null) {
          RDFQueryTerms.addAll(SSRs);
        }
      }
      RDFSecondTerms = ((MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm())).getEntityMappingTable().getOntologyMappings(ontologyID);
      
      boolean is_relation = true;
      
      RelationSimilarityService RSS = new RelationSimilarityService(osPlugin, queryTriple, RDFQueryTerms, RDFRelations, RDFSecondTerms, queryTriple.isCoverage_criteria(), is_relation, queryTriple.getTypeQuestion());
      
      System.out.println("calling relation matching for " + ontologyID);
      Iterator i$;
      if ((!RDFQueryTerms.isEmpty()) && (!RDFSecondTerms.isEmpty())) {
        for (i$ = RDFQueryTerms.iterator(); i$.hasNext();)
        {
          RDFQueryTerm = (SearchSemanticResult)i$.next();
          for (SearchSemanticResult RDFSecondTerm : RDFSecondTerms)
          {
            if (RDFQueryTerm.equals(RDFSecondTerm))
            {
              System.out.println("Query term equals second term: " + RDFQueryTerm.getEntity().getURI());
              break;
            }
            OntoTripleBean ontoTripleBean = new OntoTripleBean();
            if (!RDFQueryTerm.getEntity().isProperty())
            {
              ontoTripleBean = RSS.IS_A_RelationMatching(RDFQueryTerm, RDFSecondTerm);
              if (!ontoTripleBean.isEmpty()) {
                ontoTripleBeans.add(ontoTripleBean);
              }
            }
          }
        }
      }
      SearchSemanticResult RDFQueryTerm;
      if (!ontoTripleBeans.isEmpty()) {
        tripleMappingTable.addOntologyTriples(ontologyID, ontoTripleBeans);
      } else {
        System.out.println("No triples found for " + ontologyID);
      }
    }
    TripleMappingTable kbmappingtriples = filterOntoKBTripleMappings(queryTriple, tripleMappingTable);
    this.ontoKBTripleMappings.put(queryTriple, kbmappingtriples);
  }
  
  private void getCompound_BasicOntoTriples(QueryTriple queryTriple)
    throws Exception
  {
    TripleMappingTable tripleMappingTable = new TripleMappingTable();
    
    applyOntologyCoverageCriteria(queryTriple);
    if (this.coverage_ontologies.isEmpty()) {
      System.out.println(" No ontology covers the two terms .... ");
    }
    for (String ontologyID : this.coverage_ontologies)
    {
      ArrayList<OntoTripleBean> ontoTripleBeans = getCompound_BasicOntoTriples(queryTriple, ontologyID);
      if (!ontoTripleBeans.isEmpty()) {
        tripleMappingTable.addOntologyTriples(ontologyID, ontoTripleBeans);
      } else {
        System.out.println("No triples found for " + ontologyID);
      }
    }
    TripleMappingTable kbmappingtriples = filterOntoKBTripleMappings(queryTriple, tripleMappingTable);
    if (kbmappingtriples.getMappingTable().isEmpty())
    {
      if ((queryTriple.getQueryTerm().size() == 1) && (StringUtils.isCompound((String)queryTriple.getQueryTerm().get(0))))
      {
        ArrayList<String> restrictedKeywords = ((MappingBean)getOntoTermMappings().get(queryTriple.getQueryTerm().get(0))).getEntityMappingTable().getRestrictedKeywords();
        ArrayList<QueryTriple> compoundTriples = splitQueryTripleCompound(queryTriple, true, restrictedKeywords, true);
        if (compoundTriples != null)
        {
          System.out.println(" FINDING TRIPLE MAPPINGS FOR SECOND TIME: splitting the query term ********* ");
          if (compoundTriples.isEmpty())
          {
            getCompound_BasicOntoTriples(queryTriple);
            return;
          }
          if (compoundTriples.size() == 1)
          {
            System.out.println("Triple Mapping of " + queryTriple.getQueryTerm() + "--" + queryTriple.getRelation() + "--" + queryTriple.getSecondTerm());
            getCompound_BasicOntoTriples(queryTriple);
            System.out.println("Triple Mapping of " + ((QueryTriple)compoundTriples.get(0)).getQueryTerm() + "--" + ((QueryTriple)compoundTriples.get(0)).getRelation() + "--" + ((QueryTriple)compoundTriples.get(0)).getSecondTerm());
            getCompound_BasicOntoTriples((QueryTriple)compoundTriples.get(0));
            return;
          }
          System.out.println("TODO for more than one compound triple");
        }
      }
      System.out.println("The ontologies with better coverage did not produce any valid onto triple");
      if ((queryTriple.getTypeQuestion() != 26) || ((queryTriple.getTypeQuestion() == 26) && (this.queryTriples.size() == 1)))
      {
        queryTriple.setCoverage_criteria(false);
        for (String ontologyID : this.non_coverage_ontologies)
        {
          ArrayList<OntoTripleBean> ontoTripleBeans = getCompound_BasicOntoTriples(queryTriple, ontologyID);
          if (!ontoTripleBeans.isEmpty()) {
            tripleMappingTable.addOntologyTriples(ontologyID, ontoTripleBeans);
          } else {
            System.out.println("No triples found for " + ontologyID);
          }
        }
        kbmappingtriples = filterOntoKBTripleMappings(queryTriple, tripleMappingTable);
      }
      if (kbmappingtriples.getMappingTable().isEmpty())
      {
        String original_secondTerm = queryTriple.getSecondTerm();
        ArrayList<QueryTriple> compoundTriples = null;
        if ((StringUtils.isCompound(queryTriple.getSecondTerm())) && (!((MappingBean)getOntoTermMappings().get(queryTriple.getSecondTerm())).getEntityMappingTable().isMergedKeyword()))
        {
          compoundTriples = splitQueryTripleCompound(queryTriple, false, new ArrayList(), false);
          if (compoundTriples != null)
          {
            System.out.println(" FINDING TRIPLE MAPPINGS FOR SECOND TIME: splitting the second term ************ ");
            if (compoundTriples.isEmpty())
            {
              queryTriple.setCoverage_criteria(true);
              getCompound_BasicOntoTriples(queryTriple);
              return;
            }
            if (compoundTriples.size() == 1)
            {
              System.out.println("Triple Mapping of " + queryTriple.getQueryTerm() + "--" + queryTriple.getRelation() + "--" + queryTriple.getSecondTerm());
              getCompound_BasicOntoTriples(queryTriple);
              System.out.println("Triple Mapping of " + ((QueryTriple)compoundTriples.get(0)).getQueryTerm() + "--" + ((QueryTriple)compoundTriples.get(0)).getRelation() + "--" + ((QueryTriple)compoundTriples.get(0)).getSecondTerm());
              getCompound_BasicOntoTriples((QueryTriple)compoundTriples.get(0));
              return;
            }
            System.out.println("TODO for more than one compound triple");
          }
        }
      }
    }
    kbmappingtriples = filterOntoKBTripleMappings(queryTriple, tripleMappingTable);
    this.ontoKBTripleMappings.put(queryTriple, kbmappingtriples);
  }
  
  public boolean isMergeKeyword(String keyword)
  {
    return ((MappingBean)getOntoTermMappings().get(keyword)).getEntityMappingTable().isMergedKeyword();
  }
  
  private void getDesciption_BasicOntoTriples(QueryTriple queryTriple)
    throws Exception
  {
    TripleMappingTable tripleMappingTable = new TripleMappingTable();
    
    ArrayList<String> ontologies = applyHypernymFilteringCriteriaOnDescription(queryTriple);
    for (String ontologyID : ontologies)
    {
      OntologyPlugin osPlugin = this.mapSession.getMultiOntologyManager().getPlugin(ontologyID);
      
      ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
      
      ArrayList<SearchSemanticResult> RDFSecondTerms = ((MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm())).getEntityMappingTable().getOntologyMappings(ontologyID);
      for (SearchSemanticResult secondTerm : RDFSecondTerms)
      {
        OntoTripleBean ontoTripleBean = new OntoTripleBean(true);
        OntoTriple ontoTriple = new OntoTriple(1);
        ontoTriple.setSecondTerm(secondTerm);
        ontoTripleBean.addBean(ontoTriple);
        ontoTripleBeans.add(ontoTripleBean);
      }
      if (!ontoTripleBeans.isEmpty()) {
        tripleMappingTable.addOntologyTriples(ontologyID, ontoTripleBeans);
      } else {
        System.out.println("No triples found for " + ontologyID);
      }
    }
    this.ontoKBTripleMappings.put(queryTriple, tripleMappingTable);
  }
  
  private ArrayList<OntoTripleBean> getWhIS_BasicOntoTriples(QueryTriple queryTriple, String ontologyID)
    throws Exception
  {
    OntologyPlugin osPlugin = this.mapSession.getMultiOntologyManager().getPlugin(ontologyID);
    
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    ArrayList<SearchSemanticResult> RDFQueryTerms = new ArrayList();
    ArrayList<SearchSemanticResult> RDFRelations = new ArrayList();
    ArrayList<SearchSemanticResult> RDFSecondTerms = new ArrayList();
    
    RDFRelations = ((MappingBean)this.ontoTermMappings.get(queryTriple.getRelation())).getEntityMappingTable().getOntologyMappings(ontologyID);
    
    RDFSecondTerms = ((MappingBean)this.ontoTermMappings.get(queryTriple.getSecondTerm())).getEntityMappingTable().getOntologyMappings(ontologyID);
    if ((RDFSecondTerms != null) && (!RDFSecondTerms.isEmpty()))
    {
      boolean is_relation = false;
      if (queryTriple.getRelation().equals("IS_A_Relation")) {
        is_relation = true;
      }
      RelationSimilarityService RSSMainTriple;
      RelationSimilarityService RSSMainTriple;
      if (queryTriple.isCoverage_criteria()) {
        RSSMainTriple = new RelationSimilarityService(osPlugin, queryTriple, RDFQueryTerms, RDFRelations, RDFSecondTerms, queryTriple.isCoverage_criteria(), is_relation, queryTriple.getTypeQuestion());
      } else {
        RSSMainTriple = new RelationSimilarityService(osPlugin, queryTriple, RDFQueryTerms, new ArrayList(), RDFSecondTerms, queryTriple.isCoverage_criteria(), is_relation, queryTriple.getTypeQuestion());
      }
      System.out.println("calling relation matching for " + ontologyID);
      ontoTripleBeans = RSSMainTriple.RelationMatching();
      ArrayList<OntoTripleBean> discardedTripleBeans = RSSMainTriple.getDiscardedTripleBeans();
      addOntoRecyclingTriples(queryTriple, ontologyID, discardedTripleBeans);
      if (queryTriple.isCoverage_criteria()) {
        this.non_coverage_ontologies.addAll(RSSMainTriple.getNonCoverageOntologies());
      }
    }
    else
    {
      System.out.println("The second term can not be mapped in " + ontologyID);
    }
    return ontoTripleBeans;
  }
  
  private void getWhIS_BasicOntoTriples(QueryTriple queryTriple)
    throws Exception
  {
    TripleMappingTable tripleMappingTable = new TripleMappingTable();
    
    applyOntologyCoverageCriteria(queryTriple);
    for (String ontologyID : this.coverage_ontologies)
    {
      ArrayList<OntoTripleBean> ontoTripleBeans = getWhIS_BasicOntoTriples(queryTriple, ontologyID);
      if (!ontoTripleBeans.isEmpty()) {
        tripleMappingTable.addOntologyTriples(ontologyID, ontoTripleBeans);
      } else {
        System.out.println("No triples found for " + ontologyID);
      }
    }
    TripleMappingTable kbmappingtriples = filterOntoKBTripleMappings(queryTriple, tripleMappingTable);
    if (kbmappingtriples.getMappingTable().isEmpty())
    {
      QueryTriple qt = splitRelationCompound(queryTriple);
      if (qt != null)
      {
        System.out.println("FINDING TRIPLES FOR SECOND TIME: getting the lemma of the compound relation *****");
        System.out.println("Tripple Mapping of <what-is, " + queryTriple.getRelation() + ", " + queryTriple.getSecondTerm() + ">");
        getWhIS_BasicOntoTriples(qt);
        return;
      }
      if ((StringUtils.isCompound(queryTriple.getSecondTerm())) && (!((MappingBean)getOntoTermMappings().get(queryTriple.getSecondTerm())).getEntityMappingTable().isMergedKeyword()))
      {
        String original_secondTerm = queryTriple.getSecondTerm();
        ArrayList<QueryTriple> compoundTriples = splitQueryTripleCompound(queryTriple, false, new ArrayList(), false);
        if (compoundTriples != null)
        {
          System.out.println(" FINDING TRIPLE MAPPINGS FOR SECOND TIME: splitting the second term ******* ");
          if (compoundTriples.isEmpty())
          {
            queryTriple.setCoverage_criteria(true);
            queryTriple.setSplitting(true);
            getWhIS_BasicOntoTriples(queryTriple);
            kbmappingtriples = (TripleMappingTable)this.ontoKBTripleMappings.get(queryTriple);
          }
          else if (compoundTriples.size() == 1)
          {
            queryTriple.setCoverage_criteria(true);
            queryTriple.setSplitting(true);
            System.out.println("Triple Mapping of " + queryTriple.getRelation() + "--" + queryTriple.getSecondTerm());
            getWhIS_BasicOntoTriples(queryTriple);
            kbmappingtriples = (TripleMappingTable)this.ontoKBTripleMappings.get(queryTriple);
            if (!kbmappingtriples.getMappingTable().isEmpty())
            {
              System.out.println("Triple Mapping of " + ((QueryTriple)compoundTriples.get(0)).getRelation() + "--" + ((QueryTriple)compoundTriples.get(0)).getSecondTerm());
              getCompound_BasicOntoTriples((QueryTriple)compoundTriples.get(0));
            }
          }
          else
          {
            System.out.println("todo for more than one compound triple");
          }
        }
        if (kbmappingtriples.getMappingTable().isEmpty())
        {
          queryTriple.setCoverage_criteria(false);
          queryTriple.setSplitting(false);
          int i = getQueryTriples().indexOf(queryTriple);
          ((QueryTriple)getQueryTriples().get(i)).setSecondTerm(original_secondTerm);
          queryTriple.setSecondTerm(original_secondTerm);
          if (compoundTriples != null) {
            getQueryTriples().removeAll(compoundTriples);
          }
        }
        else
        {
          return;
        }
      }
      else if (!((MappingBean)getOntoTermMappings().get(queryTriple.getSecondTerm())).getEntityMappingTable().isMergedKeyword())
      {
        queryTriple.setCoverage_criteria(false);
        queryTriple.setSplitting(false);
      }
      if ((kbmappingtriples.getMappingTable().isEmpty()) && (!queryTriple.isSplitting()))
      {
        queryTriple.setCoverage_criteria(false);
        System.out.println("The ontologies with better coverage did not produce any valid onto triple");
        for (String ontologyID : this.non_coverage_ontologies)
        {
          ArrayList<OntoTripleBean> ontoTripleBeans = getWhIS_BasicOntoTriples(queryTriple, ontologyID);
          if (!ontoTripleBeans.isEmpty()) {
            tripleMappingTable.addOntologyTriples(ontologyID, ontoTripleBeans);
          }
        }
      }
    }
    kbmappingtriples = filterOntoKBTripleMappings(queryTriple, tripleMappingTable);
    this.ontoKBTripleMappings.put(queryTriple, kbmappingtriples);
  }
  
  public ArrayList<OntoTripleBean> addSemanticInfo(QueryTriple queryTriple, String ontologyID, ArrayList<OntoTripleBean> ontoTripleBeans)
    throws Exception
  {
    ArrayList<OntoTripleBean> res_filtered = new ArrayList();
    ArrayList<OntoTripleBean> res_eliminated = new ArrayList();
    ArrayList<SearchSemanticResult> analyzed_positive = new ArrayList();
    ArrayList<SearchSemanticResult> analyzed_negative = new ArrayList();
    for (OntoTripleBean ontoTripleBean : ontoTripleBeans)
    {
      SearchSemanticResult termSSR = ((OntoTriple)ontoTripleBean.getOntoTripleBean().get(0)).getFirstTerm();
      boolean validSynset = false;
      if (analyzed_positive.contains(termSSR)) {
        validSynset = true;
      } else if (analyzed_negative.contains(termSSR)) {
        validSynset = false;
      } else {
        for (String queryTerm : queryTriple.getQueryTerm())
        {
          SemanticComponent semComponent = new SemanticComponent(this.mapSession.getRealpath());
          validSynset = semComponent.addSemanticInfo(queryTerm, termSSR);
          semComponent.closeOpenFileDescriptors();
          if (validSynset) {
            break;
          }
        }
      }
      if (validSynset)
      {
        analyzed_positive.add(termSSR);
        
        res_filtered.add(ontoTripleBean);
      }
      else
      {
        analyzed_negative.add(termSSR);
        res_eliminated.add(ontoTripleBean);
        System.out.println("Eliminating non semantically valid triple ");
        ontoTripleBean.print();
      }
    }
    TripleMappingTable mapTable = new TripleMappingTable();
    mapTable.addOntologyTriples(ontologyID, res_eliminated);
    getOntoRecyclingMappings().put(queryTriple, mapTable);
    return res_filtered;
  }
  
  public SynsetClusterRanking getSynsetClusterRanking()
  {
    return this.synsetClusterRanking;
  }
  
  public ArrayList<QueryTriple> getQueryTriples()
  {
    return this.queryTriples;
  }
  
  public Hashtable<String, ArrayList<MappingBean>> getOntoCompoundMappings()
  {
    return this.ontoCompoundMappings;
  }
  
  public Hashtable<String, MappingBean> getOntoTermMappings()
  {
    return this.ontoTermMappings;
  }
  
  public MappingSession getMapSession()
  {
    return this.mapSession;
  }
  
  public ArrayList<String> getSortedOntologies()
  {
    return this.sortedOntologies;
  }
  
  public boolean isCoverage_criteria()
  {
    for (QueryTriple qt : this.queryTriples) {
      if (!qt.isCoverage_criteria()) {
        return false;
      }
    }
    return true;
  }
  
  public void setSortedOntologies(ArrayList<String> sortedOntologies)
  {
    this.sortedOntologies = sortedOntologies;
  }
  
  public TripleMappingTable filterOntoKBTripleMappings(QueryTriple queryTriple, TripleMappingTable tripleSchemaMappingTable)
  {
    TripleMappingTable tripleKBMappingTable = new TripleMappingTable();
    if (tripleSchemaMappingTable != null) {
      for (String ontology : tripleSchemaMappingTable.getMappingTable().keySet())
      {
        ArrayList<OntoTripleBean> ontoKBTripleBeans = new ArrayList();
        ArrayList<OntoTripleBean> ontofilteredTripleBeans = new ArrayList();
        ArrayList<OntoTripleBean> ontoSchemaTripleBeans = (ArrayList)tripleSchemaMappingTable.getMappingTable().get(ontology);
        for (OntoTripleBean ontoSchemaTripleBean : ontoSchemaTripleBeans) {
          if (ontoSchemaTripleBean.isInstance_based()) {
            ontoKBTripleBeans.add(ontoSchemaTripleBean);
          } else {
            ontofilteredTripleBeans.add(ontoSchemaTripleBean);
          }
        }
        if (!ontoKBTripleBeans.isEmpty()) {
          tripleKBMappingTable.addOntologyTriples(ontology, ontoKBTripleBeans);
        }
        if (!ontofilteredTripleBeans.isEmpty())
        {
          System.out.println("Filtering " + ontofilteredTripleBeans.size() + " triples with no answers in the KB");
          if (this.filteredOntoTripleMappings.containsKey(queryTriple))
          {
            TripleMappingTable nonfilteredTMT = (TripleMappingTable)this.filteredOntoTripleMappings.get(queryTriple);
            if (nonfilteredTMT.getMappingTable().containsKey(ontology)) {
              ((ArrayList)nonfilteredTMT.getMappingTable().get(ontology)).addAll(ontofilteredTripleBeans);
            } else {
              nonfilteredTMT.addOntologyTriples(ontology, ontofilteredTripleBeans);
            }
          }
          else
          {
            TripleMappingTable nonfilteredTMT = new TripleMappingTable();
            nonfilteredTMT.getMappingTable().put(ontology, ontofilteredTripleBeans);
            this.filteredOntoTripleMappings.put(queryTriple, nonfilteredTMT);
          }
        }
      }
    }
    return tripleKBMappingTable;
  }
  
  public Hashtable<QueryTriple, TripleMappingTable> getOntoRecyclingMappings()
  {
    return this.ontoRecyclingMappings;
  }
  
  public Hashtable<QueryTriple, TripleMappingTable> getOntoKBTripleMappings()
  {
    return this.ontoKBTripleMappings;
  }
  
  private void addOntoRecyclingTriples(QueryTriple queryTriple, String ontologyID, ArrayList<OntoTripleBean> discardedTripleBeans)
  {
    if (discardedTripleBeans == null) {
      return;
    }
    if (discardedTripleBeans.isEmpty()) {
      return;
    }
    TripleMappingTable mapTable = new TripleMappingTable();
    mapTable.addOntologyTriples(ontologyID, discardedTripleBeans);
    getOntoRecyclingMappings().put(queryTriple, mapTable);
  }
  
  public ArrayList<String> getSemanticData(QueryTriple queryTriple, String ontology)
    throws Exception
  {
    ArrayList<OntoTripleBean> mappings = (ArrayList)((TripleMappingTable)this.ontoKBTripleMappings.get(queryTriple)).getMappingTable().get(ontology);
    AnswersEngine answers = new AnswersEngine(this.mapSession, this.queryTriples, this.sortedOntologies, this.ontoKBTripleMappings);
    HashSet noRepeats = new HashSet();
    if (mappings != null) {
      for (int beanIndex = 0; beanIndex < mappings.size(); beanIndex++)
      {
        OntoTripleBean ontoTripleBean = (OntoTripleBean)mappings.get(beanIndex);
        RDFEntityList entities = answers.AnswersEngineOntoTripleBean(ontoTripleBean);
        ArrayList<String> labels = entities.getLabels();
        noRepeats.addAll(labels);
      }
    }
    ArrayList<String> semanticData = new ArrayList();
    semanticData.addAll(noRepeats);
    return semanticData;
  }
  
  public static ArrayList<String> generateKeywordsFromQueryTriple(QueryTriple queryTriple)
  {
    ArrayList<String> keywords = new ArrayList();
    ArrayList<String> queryTerms = queryTriple.getQueryTerm();
    if ((queryTerms != null) && (!((String)queryTerms.get(0)).equals("what_is"))) {
      for (String queryTerm : queryTerms)
      {
        keywords.addAll(queryTriple.getQueryTerm());
        StringTokenizer st;
        if (StringUtils.isCompound(queryTerm)) {
          for (st = new StringTokenizer(queryTerm); st.hasMoreTokens();)
          {
            String k = st.nextToken();
            keywords.add(k);
            WordNetBean wnBean = new WordNetBean(k);
            if (!keywords.contains(wnBean.getPlural())) {
              keywords.add(wnBean.getPlural());
            }
            if (!keywords.contains(wnBean.getSingular())) {
              keywords.add(wnBean.getSingular());
            }
            if ((wnBean.getWN_lemma() != null) && (!keywords.contains(wnBean.getWN_lemma()))) {
              keywords.add(wnBean.getWN_lemma());
            }
          }
        }
        WordNetBean wnBean = new WordNetBean(queryTerm);
        if (!keywords.contains(wnBean.getPlural())) {
          keywords.add(wnBean.getPlural());
        }
        if (!keywords.contains(wnBean.getSingular())) {
          keywords.add(wnBean.getSingular());
        }
        if ((wnBean.getWN_lemma() != null) && (!keywords.contains(wnBean.getWN_lemma()))) {
          keywords.add(wnBean.getWN_lemma());
        }
        keywords.addAll(wnBean.getSynonyms());
      }
    }
    if ((queryTriple.getRelation() != null) && (!queryTriple.getRelation().isEmpty()) && (!queryTriple.getRelation().equals("IS_A_Relation")))
    {
      keywords.add(queryTriple.getRelation());
      StringTokenizer st;
      if (StringUtils.isCompound(queryTriple.getRelation())) {
        for (st = new StringTokenizer(queryTriple.getRelation()); st.hasMoreTokens();)
        {
          String k = st.nextToken();
          keywords.add(k);
          WordNetBean wnBean = new WordNetBean(k);
          if (!keywords.contains(wnBean.getPlural())) {
            keywords.add(wnBean.getPlural());
          }
          if (!keywords.contains(wnBean.getSingular())) {
            keywords.add(wnBean.getSingular());
          }
          if ((wnBean.getWN_lemma() != null) && (!keywords.contains(wnBean.getWN_lemma()))) {
            keywords.add(wnBean.getWN_lemma());
          }
        }
      }
      WordNetBean wnBean = new WordNetBean(queryTriple.getRelation());
      if (!keywords.contains(wnBean.getPlural())) {
        keywords.add(wnBean.getPlural());
      }
      if (!keywords.contains(wnBean.getSingular())) {
        keywords.add(wnBean.getSingular());
      }
      if ((wnBean.getWN_lemma() != null) && (!keywords.contains(wnBean.getWN_lemma()))) {
        keywords.add(wnBean.getWN_lemma());
      }
      keywords.addAll(wnBean.getSynonyms());
    }
    keywords.add(queryTriple.getSecondTerm());
    StringTokenizer st;
    if (StringUtils.isCompound(queryTriple.getSecondTerm())) {
      for (st = new StringTokenizer(queryTriple.getSecondTerm()); st.hasMoreTokens();)
      {
        String k = st.nextToken();
        keywords.add(k);
        WordNetBean wnBean = new WordNetBean(k);
        if (!keywords.contains(wnBean.getPlural())) {
          keywords.add(wnBean.getPlural());
        }
        if (!keywords.contains(wnBean.getSingular())) {
          keywords.add(wnBean.getSingular());
        }
        if ((wnBean.getWN_lemma() != null) && (!keywords.contains(wnBean.getWN_lemma()))) {
          keywords.add(wnBean.getWN_lemma());
        }
      }
    }
    WordNetBean wnBean = new WordNetBean(queryTriple.getSecondTerm());
    if (!keywords.contains(wnBean.getPlural())) {
      keywords.add(wnBean.getPlural());
    }
    if (!keywords.contains(wnBean.getSingular())) {
      keywords.add(wnBean.getSingular());
    }
    if ((wnBean.getWN_lemma() != null) && (!keywords.contains(wnBean.getWN_lemma()))) {
      keywords.add(wnBean.getWN_lemma());
    }
    keywords.addAll(wnBean.getSynonyms());
    return keywords;
  }
  
  public void testTrust()
    throws Exception
  {
    String userID = "test";
    String real_path = this.mapSession.getRealpath();
    try
    {
      if (userID.equals(""))
      {
        System.out.println("You should login first");
      }
      else
      {
        File base = new File(real_path + "database");
        
        ITaxonomyProxy tp = new PowerAquaTaxonomyProxy(this.mapSession);
        
        ITrustEngine te = new TrustEngine(base, tp);
        
        String answer = "Analyzing answer: wrong%7C%7C%7Chttp%3A%2F%2Fkmi-web07.open.ac.uk%3A8080%2Fsesame%2Ftapfull%7C%7C%7Chttp%3A%2F%2Ftap.stanford.edu%2Fdata%2FMusicianRock%7C%7C%7Ctype_of%7C%7C%7Chttp%3A%2F%2Ftap.stanford.edu%2Fdata%2FMusician";
        
        answer = URLDecoder.decode(answer);
        answer = answer.replaceAll("XXXXX", "#");
        
        StringTokenizer st = new StringTokenizer(answer, "|||");
        if (st.hasMoreTokens())
        {
          String trustValue = st.nextToken();
          String ontologyID = st.nextToken();
          String subject = st.nextToken();
          String relation = st.nextToken();
          String object = st.nextToken();
          
          System.out.println("Updating trust value " + trustValue + " for " + ontologyID + " < " + subject + ", " + relation + ", " + object + ">");
          
          double num_stars = 0.0D;
          String predicate;
          String predicate;
          if (relation.equals("type_of"))
          {
            predicate = "type";
          }
          else
          {
            String predicate;
            if (relation.equals("subclass_of")) {
              predicate = "subClassOf";
            } else {
              predicate = relation;
            }
          }
          te.evaluate(ontologyID, subject, predicate, object, userID, num_stars);
          if (st.hasMoreTokens())
          {
            String subject2 = st.nextToken();
            String relation2 = st.nextToken();
            String object2 = st.nextToken();
            
            System.out.println("Updating trust value " + trustValue + " for " + ontologyID + " < " + object + ", " + relation2 + ", " + object2 + ">");
            if (relation2.equals("type_of")) {
              predicate = "type";
            } else if (relation2.equals("subclass_of")) {
              predicate = "subClassOf";
            } else {
              predicate = relation2;
            }
            te.evaluate(ontologyID, subject2, predicate, object2, userID, num_stars);
          }
          te.store();
          
          double value1 = te.getEvaluation("ontologyID", subject, relation, object, userID);
          System.out.println("Trust value for the triple <" + subject + "," + relation + "," + object + ">: " + value1);
          
          double value2 = te.getEntityEvaluation("ontologyID", subject, userID);
          System.out.println("Trust value for the entity " + subject + ": " + value2);
        }
      }
    }
    catch (TrustEngineException ex)
    {
      ex.printStackTrace();
    }
  }
  
  public void RanK_ontoKBTripleMappings()
  {
    SynsetInterpretationClustering();
    
    RanK_byConfidence();
  }
  
  public void RanK_byConfidence()
  {
    for (QueryTriple queryTriple : this.ontoKBTripleMappings.keySet())
    {
      System.out.println("Ranking the triple mapping table for ");queryTriple.print();
      MappingRanking mappingRanking = new MappingRanking(queryTriple, (TripleMappingTable)this.ontoKBTripleMappings.get(queryTriple));
      
      mappingRanking.RankTMT();
    }
  }
  
  public Hashtable<QueryTriple, TripleMappingTable> getOntoTripleMappings()
  {
    return this.filteredOntoTripleMappings;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    String question = "boiling point of water";
    
    long totaltime = System.currentTimeMillis();
    LinguisticComponent chunk = new LinguisticComponent();
    chunk.parseQuestion(question);
    System.out.println("THE TYPE IS " + chunk.typeQuestion);
    
    MappingSession mapSession = new MappingSession();
    
    TripleSimilarityService TSS = new TripleSimilarityService(mapSession, chunk.getQueryTriples());
    System.out.println(" CALLING TRIPLE MAPPING ");
    TSS.TripleMapping();
    
    AnswersEngine answersEngine = new AnswersEngine(TSS.getMapSession(), TSS.getQueryTriples(), TSS.getSortedOntologies(), TSS.getOntoKBTripleMappings());
    
    TSS.RanK_ontoKBTripleMappings();
    
    float timeFusion = (float)System.currentTimeMillis();
    
    FusionService fusionService = new FusionService(TSS);
    FusionService.setAPPROXIMATE_STANDARD_THRESH_KB(new Float(0.2D).floatValue());
    
    System.out.println("");System.out.println("");
    System.out.println("Printing the KB triple mapping tables (ontoTripleMappings) .................. ");
    System.out.println("------------------------------------------------------------------------");
    for (QueryTriple queryTriple : TSS.getQueryTriples())
    {
      System.out.println("Analyzing query triple ");queryTriple.print();
      TripleMappingTable tripleMappingTable = (TripleMappingTable)TSS.ontoKBTripleMappings.get(queryTriple);
      
      Hashtable<Integer, TripleMappingTable> rank_TripleMappingTables = MappingRanking.create_rankMappingTables(tripleMappingTable);
      for (int score = 1; score < rank_TripleMappingTables.keySet().size() + 1; score++)
      {
        System.out.println("Results with score " + score);
        TripleMappingTable rank_tmt = (TripleMappingTable)rank_TripleMappingTables.get(Integer.valueOf(score));
        rank_tmt.print();
      }
      fusionService.formRDFEntityEntries(queryTriple);
    }
    System.out.println("FUSION SERVICE");
    for (QueryTriple triple : fusionService.getAnswerBeanMap().keySet())
    {
      System.out.println("Fused answers for the triple: ");
      triple.print();
      bean = (FusedAnswerBean)fusionService.getAnswerBeanMap().get(triple);
    }
    FusedAnswerBean bean;
    fusionService.mergeByQueryTriples();
    
    System.out.println("Final fused answers: ");
    System.out.println("Total answers: " + fusionService.getFinalAnswerBean().getAnswers().size());
    for (RDFEntityCluster cluster : fusionService.getFinalAnswerBean().getAnswers())
    {
      System.out.println("-----------");
      for (RDFEntityEntry entry : cluster.getEntries()) {
        System.out.println(entry.getValue().getLabel() + " : " + entry.getRefersToValues().toString());
      }
      System.out.println("-----------");
    }
    System.out.println("Number of serql calls " + MappingSession.getSerqlCalls());
    System.out.println("Number of virtuoso calls " + MappingSession.getVirtuosoCalls());
    
    totaltime = System.currentTimeMillis() - totaltime;
    
    long secs = TimeUnit.MILLISECONDS.toSeconds(totaltime);
    System.out.println("Total time for fusion (msecs)" + totaltime);
    
    System.out.println("Total time for fusion (secs)" + secs);
  }
}

