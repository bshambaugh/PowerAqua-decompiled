package poweraqua.powermap.triplePhase;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import poweraqua.core.model.myocmlmodel.OcmlInstance;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.model.myrdfmodel.RDFPath;
import poweraqua.core.model.myrdfmodel.RDFProperty;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.core.tripleModel.linguisticTriple.QueryTriple;
import poweraqua.core.tripleModel.ontologyTriple.OntoTriple;
import poweraqua.core.utils.StringUtils;
import poweraqua.powermap.elementPhase.MetadataBean;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.mappingModel.MappingSession;

public class RelationSimilarityService
{
  private OntologyPlugin osPlugin;
  private ArrayList<SearchSemanticResult> RDFQueryTerms;
  private ArrayList<SearchSemanticResult> RDFRelations;
  private ArrayList<SearchSemanticResult> RDFSecondTerms;
  private ArrayList<String> nonCoverageOntologies;
  private boolean IS_A_RELATION = false;
  private int type_query;
  private QueryTriple queryTriple;
  private boolean coverage_criteria = true;
  private ArrayList<OntoTripleBean> discardedTripleBeans = null;
  
  public RelationSimilarityService(OntologyPlugin osPlugin, QueryTriple queryTriple, ArrayList<SearchSemanticResult> RDFQueryTerms, ArrayList<SearchSemanticResult> RDFRelations, ArrayList<SearchSemanticResult> RDFSecondTerms, boolean coverage_criteria, boolean IS_A_RELATION, int type_query)
  {
    this.osPlugin = osPlugin;
    this.queryTriple = queryTriple;
    this.IS_A_RELATION = IS_A_RELATION;
    this.coverage_criteria = coverage_criteria;
    this.type_query = type_query;
    setNonCoverageOntologies(new ArrayList());
    if (RDFQueryTerms == null)
    {
      this.RDFQueryTerms = new ArrayList();
    }
    else
    {
      this.RDFQueryTerms = GroupLiterals(RDFQueryTerms);
      
      this.RDFQueryTerms = FilterInstancesByTaxonomy(this.RDFQueryTerms);
    }
    if (RDFRelations == null) {
      this.RDFRelations = new ArrayList();
    } else {
      this.RDFRelations = GroupLiterals(RDFRelations);
    }
    if (RDFSecondTerms == null) {
      this.RDFSecondTerms = new ArrayList();
    } else {
      this.RDFSecondTerms = GroupLiterals(RDFSecondTerms);
    }
    FilterExactMappings();
    System.out.println("RSS for " + osPlugin.getPluginID());
    System.out.println(this.RDFQueryTerms.size() + " query terms " + this.RDFRelations.size() + " relations and " + this.RDFSecondTerms.size() + " second terms");
  }
  
  public RelationSimilarityService(OntologyPlugin osPlugin, QueryTriple queryTriple, ArrayList<SearchSemanticResult> RDFQueryTerms, ArrayList<SearchSemanticResult> RDFSecondTerms, boolean coverage_criteria, boolean IS_A_RELATION, int type_query)
  {
    this.osPlugin = osPlugin;
    this.queryTriple = queryTriple;
    this.coverage_criteria = coverage_criteria;
    setNonCoverageOntologies(new ArrayList());
    if (RDFQueryTerms == null) {
      this.RDFQueryTerms = new ArrayList();
    } else {
      this.RDFQueryTerms = RDFQueryTerms;
    }
    if (RDFSecondTerms == null) {
      this.RDFSecondTerms = new ArrayList();
    } else {
      this.RDFSecondTerms = GroupLiterals(RDFSecondTerms);
    }
    this.IS_A_RELATION = IS_A_RELATION;
    this.RDFRelations = new ArrayList();
    this.type_query = type_query;
    FilterExactMappings();
  }
  
  private ArrayList<SearchSemanticResult> GroupLiterals(ArrayList<SearchSemanticResult> RDFTerms)
  {
    ArrayList<SearchSemanticResult> RDFResults = new ArrayList();
    ArrayList<String> RDFLiterals = new ArrayList();
    int count = 0;
    for (SearchSemanticResult RDFTerm : RDFTerms) {
      if ((RDFTerm.getEntity().isLiteral()) && (RDFTerm.getEntity().getURI() != null))
      {
        if (!RDFLiterals.contains(RDFTerm.getEntity().getLabel()))
        {
          RDFLiterals.add(RDFTerm.getEntity().getLabel());
          RDFTerm.getEntity().addGroupLiteralURIs(RDFTerm.getEntity().getURI());
          if (RDFTerm.getEntity().getRefers_to() != null) {
            RDFTerm.getEntity().addGroupLiteralPropertiesURIs(RDFTerm.getEntity().getRefers_to());
          }
          RDFResults.add(RDFTerm);
        }
        else
        {
          count += 1;
          for (SearchSemanticResult RDFResult : RDFResults) {
            if (RDFResult.getEntity().getLabel().equals(RDFTerm.getEntity().getLabel()))
            {
              RDFResult.getEntity().addGroupLiteralURIs(RDFTerm.getEntity().getURI());
              if (RDFTerm.getEntity().getRefers_to() != null) {
                RDFResult.getEntity().addGroupLiteralPropertiesURIs(RDFTerm.getEntity().getRefers_to());
              }
            }
          }
        }
      }
      else {
        RDFResults.add(RDFTerm);
      }
    }
    System.out.println("Grouping " + count + " literals");
    return RDFResults;
  }
  
  public ArrayList<SearchSemanticResult> FilterInstancesByTaxonomy(ArrayList<SearchSemanticResult> queryTerms)
  {
    ArrayList<SearchSemanticResult> results = new ArrayList();
    if (queryTerms.size() < 2) {
      return queryTerms;
    }
    for (SearchSemanticResult queryTerm : queryTerms)
    {
      ArrayList<SearchSemanticResult> auxList = (ArrayList)queryTerms.clone();
      auxList.remove(queryTerm);
      if (queryTerm.getEntity().isInstance())
      {
        ArrayList<String> parents = queryTerm.getDirectClasses().getUris();
        boolean add = true;
        for (SearchSemanticResult SSRAux : auxList) {
          if ((SSRAux.getEntity().isClass()) && (parents.contains(SSRAux.getEntity().getURI())))
          {
            System.out.println("Filtering the instance " + queryTerm.getEntity().getURI() + " as we have its class as the query term");
            
            add = false;
            break;
          }
        }
        if (add) {
          results.add(queryTerm);
        }
      }
      else
      {
        results.add(queryTerm);
      }
    }
    return results;
  }
  
  public void FilterExactMappings()
  {
    if (!this.RDFQueryTerms.isEmpty()) {
      this.RDFQueryTerms = FilterEquivalentMappings(this.RDFQueryTerms);
    }
    if (!this.RDFSecondTerms.isEmpty()) {
      this.RDFSecondTerms = FilterEquivalentMappings(this.RDFSecondTerms);
    }
    if (!this.RDFRelations.isEmpty()) {
      this.RDFRelations = FilterEquivalentMappings(this.RDFRelations);
    }
  }
  
  private ArrayList<SearchSemanticResult> FilterEquivalentMappings(ArrayList<SearchSemanticResult> mappings)
  {
    ArrayList<SearchSemanticResult> results = new ArrayList();
    
    ArrayList<SearchSemanticResult> similarProp = new ArrayList();
    ArrayList<SearchSemanticResult> similarClassInstLit = new ArrayList();
    ArrayList<SearchSemanticResult> hypClassInstLit = new ArrayList();
    
    ArrayList<SearchSemanticResult> hypProp = new ArrayList();
    for (SearchSemanticResult mapping : mappings) {
      if (mapping.getSemanticRelation().equals("equivalentMatching"))
      {
        if (mapping.getEntity().isProperty()) {
          similarProp.add(mapping);
        } else {
          similarClassInstLit.add(mapping);
        }
      }
      else if (mapping.getSemanticRelation().equals("synonym"))
      {
        if (this.coverage_criteria)
        {
          if (mapping.getEntity().isProperty()) {
            similarProp.add(mapping);
          } else {
            similarClassInstLit.add(mapping);
          }
        }
        else if ((mapping.isExact()) || (StringUtils.isSingularPluralExactMapping(mapping.getEmt_keyword(), mapping.getEntity().getLocalName())) || (StringUtils.isSingularPluralExactMapping(mapping.getEmt_keyword(), mapping.getEntity().getLabel())))
        {
          if (mapping.getEntity().isProperty()) {
            similarProp.add(mapping);
          } else {
            similarClassInstLit.add(mapping);
          }
        }
        else {
          System.out.println("DICARDED non equivalent mapping! " + mapping.getEntity().getURI());
        }
      }
      else if (mapping.isExactLexicalWord())
      {
        if ((mapping.getEntity().isProperty()) && (this.coverage_criteria)) {
          similarProp.add(mapping);
        } else {
          similarClassInstLit.add(mapping);
        }
      }
      else if (mapping.getEntity().isProperty()) {
        hypProp.add(mapping);
      } else {
        hypClassInstLit.add(mapping);
      }
    }
    if (!similarClassInstLit.isEmpty()) {
      results.addAll(similarClassInstLit);
    } else if ((similarClassInstLit.isEmpty()) && (this.coverage_criteria)) {
      results.addAll(hypClassInstLit);
    }
    if (!similarProp.isEmpty()) {
      results.addAll(similarProp);
    } else if ((similarProp.isEmpty()) && (this.coverage_criteria)) {
      results.addAll(hypProp);
    }
    return results;
  }
  
  public ArrayList<OntoTripleBean> RelationMatching()
    throws Exception
  {
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    if ((this.RDFQueryTerms.isEmpty()) && (this.RDFSecondTerms.isEmpty())) {
      return ontoTripleBeans;
    }
    if (this.RDFRelations.isEmpty())
    {
      ontoTripleBeans = RelationMatching(this.RDFQueryTerms, this.RDFSecondTerms, true, this.IS_A_RELATION);
      if ((ontoTripleBeans.isEmpty()) || (onlyPreDefineRelations(ontoTripleBeans))) {
        getNonCoverageOntologies().add(this.osPlugin.getPluginID());
      }
    }
    else
    {
      for (SearchSemanticResult RDFRelation : this.RDFRelations)
      {
        System.out.println("Checking relation " + RDFRelation.getEntity().getURI() + " with " + this.RDFQueryTerms.size() + " query terms and " + this.RDFSecondTerms.size() + " second terms");
        if (RDFRelation.getEntity().isProperty()) {
          ontoTripleBeans.addAll(property_RelationMatching(this.RDFQueryTerms, RDFRelation, this.RDFSecondTerms));
        } else {
          ontoTripleBeans.addAll(entity_RelationMatching(this.RDFQueryTerms, RDFRelation, this.RDFSecondTerms));
        }
      }
      if ((onlyPreDefineRelations(ontoTripleBeans)) || ((!OntoTripleBean.hasAnyAdHocInstanceBasedAnswer(ontoTripleBeans)) && (!this.RDFQueryTerms.isEmpty()) && (!this.RDFSecondTerms.isEmpty())))
      {
        System.out.println("Calling partial mapping ignoring the relation");
        if (ontoTripleBeans.isEmpty()) {
          ontoTripleBeans = PartialRelationMatching();
        } else {
          ontoTripleBeans.addAll(PartialRelationMatching());
        }
      }
      if ((ontoTripleBeans.isEmpty()) || (onlyPreDefineRelations(ontoTripleBeans))) {
        getNonCoverageOntologies().add(this.osPlugin.getPluginID());
      }
    }
    return ontoTripleBeans;
  }
  
  private boolean anyDirectRelation(ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    for (OntoTripleBean bean : ontoTripleBeans)
    {
      if (bean.size() == 1) {
        return true;
      }
      if ((bean.size() == 2) && (((OntoTriple)bean.getOntoTripleBean().get(0)).isIS_A_RELATION())) {
        return true;
      }
    }
    return false;
  }
  
  private boolean onlyPreDefineRelations(ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    if (ontoTripleBeans.isEmpty()) {
      return false;
    }
    ArrayList<String> NOT_RDF_PROPERTIES = new ArrayList();
    NOT_RDF_PROPERTIES.add("http://www.w3.org/2000/01/rdf-schema#seeAlso");
    NOT_RDF_PROPERTIES.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");
    NOT_RDF_PROPERTIES.add("http://www.w3.org/2000/01/rdf-schema#isDefinedBy");
    NOT_RDF_PROPERTIES.add("http://www.w3.org/2000/01/rdf-schema#member");
    for (OntoTripleBean bean : ontoTripleBeans) {
      for (OntoTriple ontoTriple : bean.getOntoTripleBean())
      {
        if (ontoTriple.getRelation() == null) {
          return false;
        }
        String rel_uri = ontoTriple.getRelation().getEntity().getURI();
        if (!NOT_RDF_PROPERTIES.contains(rel_uri)) {
          return false;
        }
      }
    }
    return true;
  }
  
  public ArrayList<OntoTripleBean> PartialRelationMatching()
    throws Exception
  {
    return RelationMatching(this.RDFQueryTerms, this.RDFSecondTerms);
  }
  
  public ArrayList<OntoTripleBean> RelationMatching(ArrayList<SearchSemanticResult> RDFQueryTerms, ArrayList<SearchSemanticResult> RDFSecondTerms)
    throws Exception
  {
    return RelationMatching(RDFQueryTerms, RDFSecondTerms, true, false);
  }
  
  public ArrayList<OntoTripleBean> RelationMatching(ArrayList<SearchSemanticResult> RDFQueryTerms, ArrayList<SearchSemanticResult> RDFSecondTerms, boolean find_indirect, boolean prefer_IS_A)
    throws Exception
  {
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    Iterator i$;
    SearchSemanticResult RDFQueryTerm;
    if ((!RDFQueryTerms.isEmpty()) && (!RDFSecondTerms.isEmpty()))
    {
      for (Iterator i$ = RDFQueryTerms.iterator(); i$.hasNext();)
      {
        RDFQueryTerm = (SearchSemanticResult)i$.next();
        if ((RDFQueryTerm.getEntity().isProperty()) && (!RDFQueryTerm.getEntity().getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")))
        {
          RDFProperty prop = this.osPlugin.getRDFProperty(RDFQueryTerm.getEntity().getURI());
          RDFQueryTerm.setEntity(prop);
        }
        for (SearchSemanticResult RDFSecondTerm : RDFSecondTerms)
        {
          if (RDFQueryTerm.equals(RDFSecondTerm))
          {
            System.out.println("Query term equals second term: " + RDFQueryTerm.getEntity().getURI());
            break;
          }
          OntoTripleBean ontoTripleBean = new OntoTripleBean();
          ArrayList<OntoTripleBean> ontoTripleBeansAux = new ArrayList();
          if ((prefer_IS_A) && (!RDFQueryTerm.getEntity().isProperty()))
          {
            ontoTripleBean = IS_A_RelationMatching(RDFQueryTerm, RDFSecondTerm);
            if (!ontoTripleBean.isEmpty()) {
              ontoTripleBeansAux.add(ontoTripleBean);
            }
          }
          if (ontoTripleBeansAux.isEmpty()) {
            if (!RDFQueryTerm.getEntity().isProperty())
            {
              ontoTripleBeansAux.addAll(Ad_hoc_RelationMatching(RDFQueryTerm, RDFSecondTerm));
            }
            else if ((!RDFQueryTerm.getEntity().getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) && (!RDFSecondTerm.getEntity().isProperty()))
            {
              ArrayList<OntoTriple> obList = getEntityToCompleteTriple(RDFSecondTerm, RDFQueryTerm, false);
              if (obList != null) {
                for (OntoTriple ob : obList)
                {
                  OntoTripleBean specialBean = new OntoTripleBean();
                  if (ob != null)
                  {
                    specialBean.addBean(ob);
                    if (find_KB_Triple(ob))
                    {
                      specialBean.setInstance_based(true);
                      ontoTripleBeansAux.add(specialBean);
                    }
                  }
                }
              }
            }
          }
          ontoTripleBeans.addAll(ontoTripleBeansAux);
        }
      }
      SearchSemanticResult RDFQueryTerm;
      Iterator i$;
      if ((!prefer_IS_A) && (ontoTripleBeans.isEmpty())) {
        for (i$ = RDFQueryTerms.iterator(); i$.hasNext();)
        {
          RDFQueryTerm = (SearchSemanticResult)i$.next();
          for (SearchSemanticResult RDFSecondTerm : RDFSecondTerms)
          {
            OntoTripleBean tripleBean = IS_A_RelationMatching(RDFQueryTerm, RDFSecondTerm);
            if (!tripleBean.isEmpty()) {
              ontoTripleBeans.add(tripleBean);
            }
          }
        }
      }
      SearchSemanticResult RDFQueryTerm;
      if (((ontoTripleBeans.isEmpty()) && (find_indirect)) || ((find_indirect) && (!OntoTripleBean.hasAnyInstanceBasedAnswer(ontoTripleBeans)))) {
        for (i$ = RDFQueryTerms.iterator(); i$.hasNext();)
        {
          RDFQueryTerm = (SearchSemanticResult)i$.next();
          for (SearchSemanticResult RDFSecondTerm : RDFSecondTerms) {
            if (!RDFQueryTerm.getEntity().isProperty()) {
              ontoTripleBeans.addAll(Indirect_RelationMatching(RDFQueryTerm, RDFSecondTerm));
            }
          }
        }
      }
    }
    else
    {
      ArrayList<SearchSemanticResult> RDFTerms = new ArrayList();
      boolean is_queryTerm = false;
      if (!RDFQueryTerms.isEmpty())
      {
        RDFTerms = RDFQueryTerms;
        is_queryTerm = true;
      }
      if (!RDFSecondTerms.isEmpty()) {
        RDFTerms = RDFSecondTerms;
      }
      ontoTripleBeans.addAll(RelationMatching(RDFTerms, is_queryTerm));
    }
    return ontoTripleBeans;
  }
  
  public ArrayList<OntoTripleBean> entity_RelationMatching(ArrayList<SearchSemanticResult> RDFQueryTerms, SearchSemanticResult RDFRelation, ArrayList<SearchSemanticResult> RDFSecondTerms)
    throws Exception
  {
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    
    boolean partialTriple = false;
    boolean is_queryTerm = false;
    if (RDFQueryTerms.isEmpty())
    {
      partialTriple = true;
    }
    else if (RDFSecondTerms.isEmpty())
    {
      partialTriple = true;
      is_queryTerm = true;
    }
    ArrayList<SearchSemanticResult> RDFRefTerms = new ArrayList();
    RDFRefTerms.add(RDFRelation);
    ArrayList<OntoTripleBean> secondParts;
    boolean uff;
    Iterator i$;
    if ((!RDFRelation.getEntity().isProperty()) && (partialTriple) && (!is_queryTerm))
    {
      ontoTripleBeans.addAll(RelationMatching(RDFRefTerms, RDFSecondTerms));
    }
    else if (((RDFRelation.getEntity().isLiteral()) && (!partialTriple)) || ((RDFRelation.getEntity().isInstance()) && (!RDFRelation.isExact())))
    {
      System.out.println("Relation ignored because is a literal or a non exact instance");
    }
    else
    {
      secondParts = RelationMatching(RDFRefTerms, RDFSecondTerms, false, false);
      if (!secondParts.isEmpty())
      {
        ArrayList<OntoTripleBean> firstParts = RelationMatching(RDFQueryTerms, RDFRefTerms, false, true);
        uff = false;
        if ((!secondParts.isEmpty()) && (!RDFQueryTerms.isEmpty())) {
          for (i$ = firstParts.iterator(); i$.hasNext();)
          {
            firstPart = (OntoTripleBean)i$.next();
            for (OntoTripleBean secondPart : secondParts)
            {
              if ((uff) && (!((OntoTriple)firstPart.getOntoTripleBean().get(0)).getFirstTerm().equals(((OntoTriple)secondPart.getOntoTripleBean().get(0)).getFirstTerm()))) {
                break;
              }
              OntoTripleBean combinePart = new OntoTripleBean();
              combinePart.addBeans(firstPart);
              combinePart.addBeans(secondPart);
              if ((firstPart.isInstance_based()) && (secondPart.isInstance_based()))
              {
                System.out.println("To DO - Does the answers Engine cover all cases?");
                RDFEntityList instances = AnswersEngine.AnswersEngineOntoTripleBean(this.osPlugin, combinePart);
                if (!instances.isEmpty())
                {
                  combinePart.setInstance_based(true);
                  combinePart.setAnswer_instances(instances);
                }
                else
                {
                  System.out.println(" !!!!! ayyyyyyy: The combination of triples does not produce an answer: ");
                  firstPart.print();
                  secondPart.print();
                  addDiscardedTripleBean(combinePart);
                }
              }
              ontoTripleBeans.add(combinePart);
            }
          }
        }
      }
    }
    OntoTripleBean firstPart;
    return ontoTripleBeans;
  }
  
  public ArrayList<OntoTripleBean> property_RelationMatching(ArrayList<SearchSemanticResult> RDFQueryTerms, SearchSemanticResult RDFRelation, ArrayList<SearchSemanticResult> RDFSecondTerms)
    throws Exception
  {
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    boolean partialTriple = false;
    ArrayList<SearchSemanticResult> RDFTerms = new ArrayList();
    boolean is_queryTerm = false;
    if (RDFQueryTerms.isEmpty())
    {
      partialTriple = true;
      RDFTerms = RDFSecondTerms;
    }
    if (RDFSecondTerms.isEmpty())
    {
      partialTriple = true;
      RDFTerms = RDFQueryTerms;
      is_queryTerm = true;
    }
    RDFProperty auxP = this.osPlugin.getRDFProperty(RDFRelation.getEntity().getURI());
    
    RDFRelation.setEntityToProperty(auxP.getDomain(), auxP.getRange());
    Iterator i$;
    SearchSemanticResult RDFQueryTerm;
    if (!partialTriple) {
      for (i$ = RDFQueryTerms.iterator(); i$.hasNext();)
      {
        RDFQueryTerm = (SearchSemanticResult)i$.next();
        if (RDFQueryTerm.getEntity().isProperty())
        {
          if (RDFQueryTerm.getEntity().getURI().equals(RDFRelation.getEntity().getURI()))
          {
            System.out.println("the query term and the relation are the same: " + RDFQueryTerm.getEntity().getURI());
            for (SearchSemanticResult RDFSecondTerm : RDFSecondTerms)
            {
              ArrayList<OntoTriple> obList = getEntityToCompleteTriple(RDFSecondTerm, RDFRelation, false);
              if (obList != null) {
                for (OntoTriple ob : obList)
                {
                  OntoTripleBean ontoTripleBean = new OntoTripleBean();
                  if (ob != null)
                  {
                    ontoTripleBean.addBean(ob);
                    ontoTripleBeans.add(ontoTripleBean);
                  }
                }
              }
            }
          }
        }
        else {
          for (SearchSemanticResult RDFSecondTerm : RDFSecondTerms) {
            if (isCompleteTriple(RDFQueryTerm, RDFRelation, RDFSecondTerm))
            {
              OntoTripleBean ontoTripleBean = new OntoTripleBean();
              
              ontoTripleBean.addBean(new OntoTriple(RDFQueryTerm, RDFRelation, RDFSecondTerm));
              ontoTripleBeans.add(ontoTripleBean);
            }
          }
        }
      }
    } else {
      for (SearchSemanticResult RDFTerm : RDFTerms)
      {
        ArrayList<OntoTriple> obList = getEntityToCompleteTriple(RDFTerm, RDFRelation, is_queryTerm);
        if (obList != null) {
          for (OntoTriple ob : obList)
          {
            OntoTripleBean ontoTripleBean = new OntoTripleBean();
            if (ob != null)
            {
              ontoTripleBean.addBean(ob);
              ontoTripleBeans.add(ontoTripleBean);
            }
          }
        }
      }
    }
    for (OntoTripleBean ontoTripleBean : ontoTripleBeans)
    {
      boolean kbBean = true;
      Iterator i$ = ontoTripleBean.getOntoTripleBean().iterator();
      if (i$.hasNext())
      {
        OntoTriple ontoTriple = (OntoTriple)i$.next();
        if (!find_KB_Triple(ontoTriple)) {
          kbBean = false;
        }
      }
      if (kbBean) {
        ontoTripleBean.setInstance_based(true);
      }
    }
    return ontoTripleBeans;
  }
  
  public boolean find_KB_Triple(OntoTripleBean ontoTripleBean)
    throws Exception
  {
    for (OntoTriple ontoTriple : ontoTripleBean.getOntoTripleBean())
    {
      boolean aux = find_KB_Triple(ontoTriple);
      if (!aux) {
        return false;
      }
    }
    return true;
  }
  
  public boolean find_KB_Triple(RDFPath path, RDFEntity source, RDFEntity target)
    throws Exception
  {
    boolean first = find_KB_Triple(source, path.getRDFProperty1(), path.getRDFEntityReference());
    if (!first) {
      return false;
    }
    boolean second = find_KB_Triple(path.getRDFEntityReference(), path.getRDFProperty1(), target);
    if ((first) && (second)) {
      return true;
    }
    return false;
  }
  
  public boolean find_KB_Triple(OntoTriple ontoTriple)
    throws Exception
  {
    RDFEntity first = ontoTriple.getFirstTerm().getEntity();
    RDFEntity second = ontoTriple.getSecondTerm().getEntity();
    RDFEntity rel = ontoTriple.getRelation().getEntity();
    return find_KB_Triple(first, rel, second);
  }
  
  public boolean find_KB_Triple(RDFEntity first, RDFEntity rel, RDFEntity second)
    throws Exception
  {
    boolean findit = false;
    if ((first.isClass()) && (second.isClass()))
    {
      findit = this.osPlugin.isKBTripleClassClass(first.getURI(), rel.getURI(), second.getURI());
    }
    else if ((first.isClass()) && (second.isInstance()))
    {
      findit = this.osPlugin.isKBTripleClassInstance(first.getURI(), rel.getURI(), second.getURI());
    }
    else if ((first.isInstance()) && (second.isClass()))
    {
      findit = this.osPlugin.isKBTripleClassInstance(second.getURI(), rel.getURI(), first.getURI());
    }
    else if ((first.isInstance()) && (second.isInstance()))
    {
      findit = this.osPlugin.isKBTripleInstanceInstance(first.getURI(), rel.getURI(), second.getURI());
    }
    else
    {
      if (((first.isLiteral()) || (first.isDataType())) && (second.isInstance())) {
        return true;
      }
      if (((second.isLiteral()) || (first.isDataType())) && (first.isInstance())) {
        return true;
      }
      if (((first.isLiteral()) || (first.isDataType())) && (second.isClass())) {
        return true;
      }
      if (((second.isLiteral()) || (first.isDataType())) && (first.isClass()))
      {
        if (this.osPlugin.getAllInstancesOfClass(first.getURI(), 1).isEmpty()) {
          return false;
        }
        return true;
      }
    }
    return findit;
  }
  
  public ArrayList<OntoTripleBean> Ad_hoc_RelationMatching(SearchSemanticResult RDFQueryTerm, SearchSemanticResult RDFSecondTerm)
    throws Exception
  {
    RDFEntityList kb_properties = new RDFEntityList();
    if ((RDFQueryTerm.getEntity().isClass()) && (RDFSecondTerm.getEntity().isClass()))
    {
      if ((RDFQueryTerm.isExact()) || (RDFSecondTerm.isExact()))
      {
        MappingSession.getLog_poweraqua().log(Level.INFO, "Ah hoc relation matching between classes " + RDFQueryTerm.getEntity().getURI() + " and " + RDFSecondTerm.getEntity().getURI());
        
        kb_properties = this.osPlugin.getKBPropertiesBetweenClasses(RDFQueryTerm.getEntity().getURI(), RDFSecondTerm.getEntity().getURI());
        
        return retrieveOntoTripleBeans(RDFQueryTerm, kb_properties, RDFSecondTerm);
      }
      return new ArrayList();
    }
    MappingSession.getLog_poweraqua().log(Level.INFO, "Ah hoc relation matching between " + RDFQueryTerm.getEntity().getURI() + " and " + RDFSecondTerm.getEntity().getURI() + "(" + RDFSecondTerm.getEntity().getLabel() + ")");
    if ((RDFQueryTerm.getEntity().isClass()) && (RDFSecondTerm.getEntity().isInstance()))
    {
      kb_properties = this.osPlugin.getKBPropertiesForGenericClass(RDFQueryTerm.getEntity().getURI(), RDFSecondTerm.getEntity().getURI());
    }
    else if ((RDFQueryTerm.getEntity().isInstance()) && (RDFSecondTerm.getEntity().isClass()))
    {
      kb_properties = this.osPlugin.getKBPropertiesForGenericClass(RDFSecondTerm.getEntity().getURI(), RDFQueryTerm.getEntity().getURI());
    }
    else if ((RDFQueryTerm.getEntity().isInstance()) && (RDFSecondTerm.getEntity().isInstance()))
    {
      kb_properties = this.osPlugin.getInstanceProperties(RDFQueryTerm.getEntity().getURI(), RDFSecondTerm.getEntity().getURI());
    }
    else if ((RDFSecondTerm.getEntity().isLiteral()) && (RDFQueryTerm.getEntity().isClass()))
    {
      if (RDFSecondTerm.getEntity().getGroupLiteralPropertiesURIs() != null) {
        kb_properties = RDFSecondTerm.getEntity().getGroupLiteralPropertiesURIs();
      } else if (RDFSecondTerm.isExact()) {
        kb_properties = this.osPlugin.getAllPropertiesBetweenClass_Literal(RDFQueryTerm.getEntity().getURI(), RDFSecondTerm.getEntity().getLabel());
      }
    }
    else
    {
      if ((RDFSecondTerm.getEntity().isLiteral()) && (RDFQueryTerm.getEntity().isInstance()))
      {
        if ((RDFQueryTerm.isExact()) || (RDFSecondTerm.isExact())) {
          return Ad_hoc_LiteralMatching(RDFSecondTerm, RDFQueryTerm);
        }
        return new ArrayList();
      }
      if ((RDFQueryTerm.getEntity().isLiteral()) && (RDFSecondTerm.getEntity().isInstance()))
      {
        if ((RDFQueryTerm.isExact()) && (RDFSecondTerm.isExact())) {
          return Ad_hoc_LiteralMatching(RDFQueryTerm, RDFSecondTerm);
        }
        return new ArrayList();
      }
      if ((RDFQueryTerm.getEntity().isLiteral()) && (RDFSecondTerm.getEntity().isLiteral()))
      {
        ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
        ArrayList<String> groupLiteralFirsts = RDFQueryTerm.getEntity().getGroupLiteralURIs();
        ArrayList<String> groupLiteralSeconds = RDFSecondTerm.getEntity().getGroupLiteralURIs();
        for (Iterator i$ = groupLiteralFirsts.iterator(); i$.hasNext();)
        {
          groupLiteralFirst = (String)i$.next();
          for (String groupLiteralSecond : groupLiteralSeconds)
          {
            OntoTripleBean ob = Ad_hoc_LiteralMatching(groupLiteralFirst, RDFQueryTerm, groupLiteralSecond, RDFSecondTerm);
            if (!ob.isEmpty()) {
              ontoTripleBeans.add(ob);
            }
          }
        }
        String groupLiteralFirst;
        return ontoTripleBeans;
      }
    }
    if (!kb_properties.isEmpty()) {
      return retrieveOntoTripleBeans(RDFQueryTerm, kb_properties, RDFSecondTerm);
    }
    return new ArrayList();
  }
  
  private OntoTripleBean Ad_hoc_LiteralMatching(String literalFirst_uri, SearchSemanticResult literalFirst, String literalSecond_uri, SearchSemanticResult literalSecond)
    throws Exception
  {
    RDFEntityList kb_properties_second;
    RDFEntity instanceEntity;
    if (literalFirst_uri.equals(literalSecond_uri))
    {
      RDFEntityList kb_properties_first = new RDFEntityList();
      kb_properties_second = new RDFEntityList();
      instanceEntity = new RDFEntity("instance", literalFirst_uri, this.osPlugin.getLabelOfEntity(literalFirst_uri), this.osPlugin.getPluginID());
      
      OcmlInstance instanceResultant = this.osPlugin.getInstanceInfo(literalFirst_uri);
      RDFEntityList aux = instanceResultant.getPropertiesWithValue(literalFirst.getEntity().getLabel());
      kb_properties_first.addNewRDFEntities(aux);
      if (!kb_properties_first.isEmpty()) {
        kb_properties_second.addNewRDFEntities(instanceResultant.getPropertiesWithValue(literalSecond.getEntity().getLabel()));
      }
      for (RDFEntity kb_property_first : kb_properties_first.getAllRDFEntities())
      {
        Iterator i$ = kb_properties_second.getAllRDFEntities().iterator();
        if (i$.hasNext())
        {
          RDFEntity kb_property_second = (RDFEntity)i$.next();
          OntoTripleBean ob = new OntoTripleBean();
          
          ob.setInstance_based(true);
          
          OntoTriple ontotrip = new OntoTriple(new SearchSemanticResult(instanceEntity, this.osPlugin), new SearchSemanticResult(kb_property_first, this.osPlugin), literalFirst);
          
          ob.addBean(ontotrip);
          ob.addBean(new OntoTriple(new SearchSemanticResult(instanceEntity, this.osPlugin), new SearchSemanticResult(kb_property_second, this.osPlugin), literalSecond));
          
          return ob;
        }
      }
    }
    return new OntoTripleBean();
  }
  
  private ArrayList<OntoTripleBean> Ad_hoc_LiteralMatching(SearchSemanticResult literal, SearchSemanticResult instance)
    throws Exception
  {
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    
    ArrayList<String> groupLiteralURIs = literal.getEntity().getGroupLiteralURIs();
    String literal_label = literal.getEntity().getLabel();
    System.out.println("*****Checking " + groupLiteralURIs.size() + " literals for " + literal_label);
    for (String groupLiteralURI : groupLiteralURIs) {
      if (groupLiteralURI.equals(instance.getEntity().getURI()))
      {
        OcmlInstance instanceResultant = this.osPlugin.getInstanceInfo(groupLiteralURI);
        RDFEntityList kb_properties_first = new RDFEntityList();
        RDFEntityList aux = instanceResultant.getPropertiesWithValue(literal_label);
        kb_properties_first.addNewRDFEntities(aux);
        for (RDFEntity kb_property_first : kb_properties_first.getAllRDFEntities())
        {
          OntoTripleBean ob = new OntoTripleBean();
          
          ob.setInstance_based(true);
          OntoTriple ontotrip = new OntoTriple(instance, new SearchSemanticResult(kb_property_first, this.osPlugin), literal);
          
          ob.addBean(ontotrip);
          ontoTripleBeans.add(ob);
        }
      }
    }
    if (!ontoTripleBeans.isEmpty()) {
      return ontoTripleBeans;
    }
    RDFEntityList directClasses = this.osPlugin.getDirectClassOfInstance((String)groupLiteralURIs.get(0));
    RDFEntityList classproperties = new RDFEntityList();
    for (RDFEntity directClass : directClasses.getAllRDFEntities()) {
      classproperties.addNewRDFEntities(this.osPlugin.getKBPropertiesForGenericClass(directClass.getURI(), instance.getEntity().getURI()));
    }
    if (classproperties.isEmpty())
    {
      System.out.println(" Not literal matching for " + instance.getEntity().getURI() + "and the literal " + literal_label + "(" + classproperties.toString() + ")");
      
      return ontoTripleBeans;
    }
    for (String literal_uri : groupLiteralURIs)
    {
      OcmlInstance instanceResultant = this.osPlugin.getInstanceInfo(literal_uri);
      
      RDFEntityList kb_properties_first = new RDFEntityList();
      kb_properties_second = new RDFEntityList();
      kb_properties_second.addNewRDFEntities(instanceResultant.getPropertiesWithValue(instance.getEntity()));
      if (!kb_properties_second.isEmpty())
      {
        RDFEntityList aux = instanceResultant.getPropertiesWithValue(literal_label);
        kb_properties_first.addNewRDFEntities(aux);
        
        instanceEntity = new RDFEntity("instance", literal_uri, this.osPlugin.getLabelOfEntity(literal_uri), this.osPlugin.getPluginID());
        for (i$ = kb_properties_first.getAllRDFEntities().iterator(); i$.hasNext();)
        {
          kb_property_first = (RDFEntity)i$.next();
          for (RDFEntity kb_property_second : kb_properties_second.getAllRDFEntities())
          {
            OntoTripleBean ob = new OntoTripleBean();
            
            ob.setInstance_based(true);
            OntoTriple ontotrip = new OntoTriple(new SearchSemanticResult(instanceEntity, this.osPlugin), new SearchSemanticResult(kb_property_first, this.osPlugin), literal);
            
            ob.addBean(ontotrip);
            ob.addBean(new OntoTriple(new SearchSemanticResult(instanceEntity, this.osPlugin), new SearchSemanticResult(kb_property_second, this.osPlugin), instance));
            
            ontoTripleBeans.add(ob);
          }
        }
      }
    }
    RDFEntityList kb_properties_second;
    RDFEntity instanceEntity;
    Iterator i$;
    RDFEntity kb_property_first;
    return ontoTripleBeans;
  }
  
  public ArrayList<OntoTripleBean> Indirect_RelationMatching(SearchSemanticResult RDFQueryTerm, SearchSemanticResult RDFSecondTerm)
    throws Exception
  {
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    
    ArrayList<RDFPath> kb_paths = new ArrayList();
    if ((!RDFQueryTerm.isExact()) && (!RDFSecondTerm.isExact())) {
      return ontoTripleBeans;
    }
    if ((RDFQueryTerm.isHyperHypoNym()) || (RDFSecondTerm.isHyperHypoNym())) {
      return ontoTripleBeans;
    }
    if ((RDFQueryTerm.getEntity().isClass()) && (RDFSecondTerm.getEntity().isClass()))
    {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Schema indirect relation matching between " + RDFQueryTerm.getEntity().getURI() + " and " + RDFSecondTerm.getEntity().getURI());
      
      kb_paths.addAll(this.osPlugin.getSchemaIndirectRelations(RDFQueryTerm.getEntity().getURI(), RDFSecondTerm.getEntity().getURI()));
      if (kb_paths.isEmpty())
      {
        RDFEntityList queryT_sbcs = RDFQueryTerm.getSubclasses();
        RDFEntityList secondT_sbcs = RDFSecondTerm.getSubclasses();
        for (RDFEntity queryT_sbc : queryT_sbcs.getAllRDFEntities()) {
          if (secondT_sbcs.isRDFEntityContained(queryT_sbc.getURI()))
          {
            OntoTripleBean ob = new OntoTripleBean();
            ob.setInstance_based(true);
            
            ob.addBean(new OntoTriple(new SearchSemanticResult(queryT_sbc, this.osPlugin), RDFSecondTerm, true));
            ob.addBean(new OntoTriple(new SearchSemanticResult(queryT_sbc, this.osPlugin), RDFQueryTerm, true));
            ontoTripleBeans.add(ob);
          }
        }
        if (!ontoTripleBeans.isEmpty()) {
          return ontoTripleBeans;
        }
      }
    }
    else if ((RDFQueryTerm.getEntity().isInstance()) && (RDFSecondTerm.getEntity().isInstance()))
    {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Instance Indirect relation matching between " + RDFQueryTerm.getEntity().getURI() + " and " + RDFSecondTerm.getEntity().getURI());
      
      kb_paths.addAll(this.osPlugin.getInstanceIndirectRelations(RDFQueryTerm.getEntity().getURI(), RDFSecondTerm.getEntity().getURI()));
    }
    else if ((RDFQueryTerm.getEntity().isClass()) && (RDFSecondTerm.getEntity().isInstance()))
    {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Indirect relation matching between " + RDFQueryTerm.getEntity().getURI() + " and " + RDFSecondTerm.getEntity().getURI());
      
      kb_paths.addAll(this.osPlugin.getKBIndirectRelations(RDFQueryTerm.getEntity().getURI(), RDFSecondTerm.getEntity().getURI()));
    }
    else if ((RDFQueryTerm.getEntity().isInstance()) && (RDFSecondTerm.getEntity().isClass()))
    {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Indirect relation matching between " + RDFQueryTerm.getEntity().getURI() + " and " + RDFSecondTerm.getEntity().getURI());
      
      kb_paths.addAll(this.osPlugin.getKBIndirectRelations(RDFSecondTerm.getEntity().getURI(), RDFQueryTerm.getEntity().getURI()));
    }
    else if ((RDFQueryTerm.getEntity().isClass()) && (RDFSecondTerm.getEntity().isLiteral()))
    {
      MappingSession.getLog_poweraqua().log(Level.INFO, "Indirect relation matching (with literal) between " + RDFQueryTerm.getEntity().getURI() + " and " + RDFSecondTerm.getEntity().getURI());
      
      kb_paths.addAll(this.osPlugin.getKBIndirectRelationsWithLiterals(RDFQueryTerm.getEntity().getURI(), RDFSecondTerm.getEntity().getLabel()));
    }
    else
    {
      return ontoTripleBeans;
    }
    for (RDFPath kb_path : kb_paths)
    {
      OntoTripleBean ob = new OntoTripleBean();
      
      ob.setInstance_based(true);
      OntoTriple ontotrip = new OntoTriple(RDFQueryTerm, new SearchSemanticResult(kb_path.getRDFProperty1(), this.osPlugin), new SearchSemanticResult(kb_path.getRDFEntityReference(), this.osPlugin));
      
      ob.addBean(ontotrip);
      
      ob.addBean(new OntoTriple(new SearchSemanticResult(kb_path.getRDFEntityReference(), this.osPlugin), new SearchSemanticResult(kb_path.getRDFProperty2(), this.osPlugin), RDFSecondTerm));
      
      ob.setAnswer_instances(kb_path.getKBAnswers());
      ontoTripleBeans.add(ob);
    }
    return ontoTripleBeans;
  }
  
  public OntoTripleBean IS_A_RelationMatching(SearchSemanticResult RDFQueryTerm, SearchSemanticResult RDFSecondTerm)
    throws Exception
  {
    OntoTripleBean ontoTripleBean = new OntoTripleBean(true);
    this.IS_A_RELATION = false;
    if (RDFQueryTerm.getEntity().isInstance())
    {
      RDFEntityList directClasses = RDFQueryTerm.getDirectClasses();
      RDFEntityList taxonomy = new RDFEntityList();
      taxonomy.addNewRDFEntities(directClasses);
      taxonomy.addNewRDFEntities(RDFQueryTerm.getSuperclasses());
      if (taxonomy.isRDFEntityContained(RDFSecondTerm.getEntity().getURI())) {
        this.IS_A_RELATION = true;
      }
    }
    if ((RDFQueryTerm.getEntity().isClass()) && (RDFSecondTerm.getEntity().isClass()))
    {
      if (RDFQueryTerm.getSuperclasses().isRDFEntityContained(RDFSecondTerm.getEntity().getURI())) {
        this.IS_A_RELATION = true;
      }
      if (RDFQueryTerm.getSubclasses().isRDFEntityContained(RDFSecondTerm.getEntity().getURI()))
      {
        this.IS_A_RELATION = true;
        
        SearchSemanticResult aux = RDFQueryTerm;
        RDFQueryTerm = RDFSecondTerm;
        RDFSecondTerm = aux;
      }
    }
    if ((RDFQueryTerm.getEntity().isClass()) && (RDFSecondTerm.getEntity().isInstance()))
    {
      RDFEntityList parents = RDFSecondTerm.getDirectClasses();
      parents.addNewRDFEntities(RDFSecondTerm.getSuperclasses());
      if (parents.isRDFEntityContained(RDFQueryTerm.getEntity().getURI()))
      {
        this.IS_A_RELATION = true;
        
        SearchSemanticResult aux = RDFQueryTerm;
        RDFQueryTerm = RDFSecondTerm;
        RDFSecondTerm = aux;
      }
    }
    if (this.IS_A_RELATION) {
      ontoTripleBean.addBean(new OntoTriple(RDFQueryTerm, RDFSecondTerm, this.IS_A_RELATION));
    }
    return ontoTripleBean;
  }
  
  public ArrayList<OntoTripleBean> RelationMatching(ArrayList<SearchSemanticResult> RDFTerms, boolean is_queryTerm)
    throws Exception
  {
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    for (SearchSemanticResult RDFTerm : RDFTerms) {
      if ((!RDFTerm.getSemanticRelation().equals("hypernym")) && (!RDFTerm.getSemanticRelation().equals("hyponym")))
      {
        MappingSession.getLog_poweraqua().log(Level.INFO, "Calling relation matching for " + RDFTerm.getEntity().getURI() + " " + RDFTerm.getEntity().getType());
        RDFEntityList proper = new RDFEntityList();
        if (RDFTerm.getEntity().isClass())
        {
          proper.addNewRDFEntities(this.osPlugin.getPropertiesForGenericClass(RDFTerm.getEntity().getURI()));
          for (RDFEntity propertyEnt : proper.getAllRDFEntities())
          {
            RDFProperty property = (RDFProperty)propertyEnt;
            
            ArrayList<OntoTriple> tripList = getEntityToCompleteTriple(RDFTerm, property, is_queryTerm);
            if (tripList != null) {
              for (OntoTriple trip : tripList)
              {
                OntoTripleBean ob = new OntoTripleBean();
                if (trip != null)
                {
                  ob.addBean(trip);
                  if (find_KB_Triple(ob))
                  {
                    ob.setInstance_based(true);
                    ontoTripleBeans.add(ob);
                  }
                }
              }
            }
          }
        }
        else if (RDFTerm.getEntity().isInstance())
        {
          proper.addNewRDFEntities(this.osPlugin.getAllPropertiesOfInstance(RDFTerm.getEntity().getURI()));
          for (RDFEntity propertyEnt : proper.getAllRDFEntities())
          {
            RDFProperty property = (RDFProperty)propertyEnt;
            
            ArrayList<OntoTriple> tripList = getEntityToCompleteTriple(RDFTerm, property, is_queryTerm);
            if (tripList != null) {
              for (OntoTriple trip : tripList)
              {
                OntoTripleBean ob = new OntoTripleBean();
                if (trip != null)
                {
                  ob.addBean(trip);
                  ob.setInstance_based(true);
                  ontoTripleBeans.add(ob);
                }
              }
            }
          }
        }
        else
        {
          ArrayList<String> groupLiteralURIs;
          OcmlInstance instanceResultant;
          if (RDFTerm.getEntity().isLiteral())
          {
            groupLiteralURIs = RDFTerm.getEntity().getGroupLiteralURIs();
            for (String groupLiteralURI : groupLiteralURIs)
            {
              String literal_label = RDFTerm.getEntity().getLabel();
              System.out.println("Partial Mapping: Checking " + groupLiteralURIs.size() + " literals for " + literal_label);
              instanceResultant = this.osPlugin.getInstanceInfo(groupLiteralURI);
              RDFEntityList auxProps = instanceResultant.getPropertiesWithValue(literal_label);
              for (RDFEntity auxProp : auxProps.getAllRDFEntities())
              {
                OntoTripleBean ob = new OntoTripleBean();
                ob.setInstance_based(true);
                OntoTriple ontotrip = new OntoTriple(new SearchSemanticResult(instanceResultant.getInstance(), this.osPlugin), new SearchSemanticResult(auxProp, this.osPlugin), RDFTerm);
                
                ob.addBean(ontotrip);
                ontoTripleBeans.add(ob);
              }
            }
          }
          else
          {
            return ontoTripleBeans;
          }
        }
      }
      else
      {
        System.out.println("Impossible to complete a triple with just one hyper-hyponym " + RDFTerm.getEntity().getURI());
      }
    }
    return ontoTripleBeans;
  }
  
  public ArrayList<SearchSemanticResult> getRDFQueryTerms()
  {
    return this.RDFQueryTerms;
  }
  
  public ArrayList<SearchSemanticResult> getRDFRelations()
  {
    return this.RDFRelations;
  }
  
  public ArrayList<SearchSemanticResult> getRDFSecondTerms()
  {
    return this.RDFSecondTerms;
  }
  
  private ArrayList<OntoTripleBean> retrieveOntoTripleBeans(SearchSemanticResult RDFQueryTerm, RDFEntityList kb_properties, SearchSemanticResult RDFSecondTerm)
    throws Exception
  {
    ArrayList<OntoTripleBean> ontoTripleBeans = new ArrayList();
    for (RDFEntity propertyEnt : kb_properties.getAllRDFEntities())
    {
      RDFProperty property = (RDFProperty)propertyEnt;
      RDFEntityList domains = this.osPlugin.getDomainOfProperty(property.getURI());
      if (domains.isEmpty()) {
        property.addDomain(RDFQueryTerm.getEntity());
      } else {
        property.setDomain(domains);
      }
      RDFEntityList ranges = this.osPlugin.getRangeOfProperty(property.getURI());
      if (ranges.isEmpty()) {
        property.setRange(RDFSecondTerm.getEntity());
      } else {
        property.setRange(ranges);
      }
      SearchSemanticResult RSS_property = new SearchSemanticResult(property, this.osPlugin);
      OntoTripleBean ontoTripleBean = new OntoTripleBean();
      ontoTripleBean.setInstance_based(true);
      ontoTripleBean.addBean(new OntoTriple(RDFQueryTerm, RSS_property, RDFSecondTerm));
      ontoTripleBeans.add(ontoTripleBean);
    }
    return ontoTripleBeans;
  }
  
  private ArrayList<OntoTriple> getEntityToCompleteTriple(SearchSemanticResult RDFTerm, RDFProperty property, boolean queryTerm)
    throws Exception
  {
    RDFEntityList AuxTermList = getEntityToCompleteTriple(RDFTerm, property);
    if (AuxTermList == null) {
      return null;
    }
    ArrayList<OntoTriple> ontoTriples = new ArrayList();
    for (RDFEntity AuxTerm : AuxTermList.getAllRDFEntities())
    {
      SearchSemanticResult SSR_property = new SearchSemanticResult(property, this.osPlugin);
      SearchSemanticResult SSR_AuxTerm = new SearchSemanticResult(AuxTerm, this.osPlugin);
      if (SSR_AuxTerm.getEntity().isClass())
      {
        SSR_AuxTerm.getMetadataBean().setSuperclasses(this.osPlugin.getAllSuperClasses(SSR_AuxTerm.getEntity().getURI()));
        SSR_AuxTerm.getMetadataBean().setDirectSuperclasses(this.osPlugin.getDirectSuperClasses(SSR_AuxTerm.getEntity().getURI()));
      }
      if (SSR_AuxTerm.getEntity().isInstance())
      {
        RDFEntityList results = this.osPlugin.getDirectClassOfInstance(SSR_AuxTerm.getEntity().getURI());
        SSR_AuxTerm.getMetadataBean().setDirectClasses(results);
        for (RDFEntity result : results.getAllRDFEntities())
        {
          SSR_AuxTerm.getMetadataBean().addDirectSuperclasses(this.osPlugin.getDirectSuperClasses(result.getURI()));
          SSR_AuxTerm.getMetadataBean().addSuperclasses(this.osPlugin.getAllSuperClasses(result.getURI()));
        }
      }
      OntoTriple ob;
      OntoTriple ob;
      if (queryTerm) {
        ob = new OntoTriple(RDFTerm, SSR_property, SSR_AuxTerm);
      } else {
        ob = new OntoTriple(SSR_AuxTerm, SSR_property, RDFTerm);
      }
      ontoTriples.add(ob);
    }
    return ontoTriples;
  }
  
  private ArrayList<OntoTriple> getEntityToCompleteTriple(SearchSemanticResult RDFTerm, SearchSemanticResult property, boolean queryTerm)
    throws Exception
  {
    System.out.println("Get entity to complete the triple " + RDFTerm.getEntity().getURI() + " and " + property.getEntity().getURI());
    
    RDFEntityList AuxTermList = getEntityToCompleteTriple(RDFTerm, (RDFProperty)property.getEntity());
    if (AuxTermList == null) {
      return null;
    }
    ArrayList<OntoTriple> ontoTriples = new ArrayList();
    for (RDFEntity AuxTerm : AuxTermList.getAllRDFEntities())
    {
      SearchSemanticResult SSR_AuxTerm = new SearchSemanticResult(AuxTerm, this.osPlugin);
      if (SSR_AuxTerm.getEntity().isClass())
      {
        SSR_AuxTerm.getMetadataBean().setSuperclasses(this.osPlugin.getAllSuperClasses(SSR_AuxTerm.getEntity().getURI()));
        SSR_AuxTerm.getMetadataBean().setDirectSuperclasses(this.osPlugin.getDirectSuperClasses(SSR_AuxTerm.getEntity().getURI()));
      }
      if (SSR_AuxTerm.getEntity().isInstance())
      {
        RDFEntityList results = this.osPlugin.getDirectClassOfInstance(SSR_AuxTerm.getEntity().getURI());
        SSR_AuxTerm.getMetadataBean().setDirectClasses(results);
        for (RDFEntity result : results.getAllRDFEntities())
        {
          SSR_AuxTerm.getMetadataBean().addDirectSuperclasses(this.osPlugin.getDirectSuperClasses(result.getURI()));
          SSR_AuxTerm.getMetadataBean().addSuperclasses(this.osPlugin.getAllSuperClasses(result.getURI()));
        }
      }
      OntoTriple ob;
      OntoTriple ob;
      if (queryTerm) {
        ob = new OntoTriple(RDFTerm, property, SSR_AuxTerm);
      } else {
        ob = new OntoTriple(SSR_AuxTerm, property, RDFTerm);
      }
      ontoTriples.add(ob);
    }
    return ontoTriples;
  }
  
  private RDFEntityList getEntityToCompleteTriple(SearchSemanticResult RDFTerm, RDFProperty property)
    throws Exception
  {
    ArrayList<String> taxonomy = new ArrayList();
    taxonomy.add(RDFTerm.getEntity().getURI());
    if (RDFTerm.getEntity().isClass())
    {
      taxonomy.addAll(RDFTerm.getSuperclasses().getUris());
      taxonomy.addAll(RDFTerm.getSubclasses().getUris());
    }
    else
    {
      RDFEntityList allClasses = this.osPlugin.getAllClassesOfInstance(RDFTerm.getEntity().getURI());
      taxonomy.addAll(allClasses.getUris());
    }
    RDFEntityList AuxTerm = null;
    if ((!property.getDomain().isEmpty()) && (!property.getRange().isEmpty()))
    {
      for (RDFEntity domainClass : property.getDomain().getAllRDFEntities()) {
        if (taxonomy.contains(domainClass.getURI()))
        {
          AuxTerm = property.getRange();
          break;
        }
      }
      for (RDFEntity rangeEntity : property.getRange().getAllRDFEntities()) {
        if (taxonomy.contains(rangeEntity.getURI()))
        {
          AuxTerm = property.getDomain();
          break;
        }
      }
    }
    else if ((!property.getRange().isEmpty()) && (RDFTerm.getEntity().isInstance()))
    {
      AuxTerm = property.getRange();
    }
    else if ((!property.getDomain().isEmpty()) && (RDFTerm.getEntity().isInstance()))
    {
      AuxTerm = property.getDomain();
    }
    else if ((this.queryTriple.getRelation() == null) || (this.queryTriple.getRelation().equals("IS_A_Relation")) || (this.queryTriple.getQueryTerm().isEmpty()) || (((String)this.queryTriple.getQueryTerm().get(0)).equals("what_is")))
    {
      if (RDFTerm.getEntity().isInstance()) {
        AuxTerm = this.osPlugin.getSlotValue(RDFTerm.getEntity().getURI(), property.getURI());
      }
    }
    else
    {
      System.out.println("No domain or range to complete the triple: noisy OTs");
      if (RDFTerm.getEntity().isInstance()) {
        AuxTerm = this.osPlugin.getSlotValue(RDFTerm.getEntity().getURI(), property.getURI());
      }
    }
    return AuxTerm;
  }
  
  private boolean isCompleteTriple(SearchSemanticResult entity1, SearchSemanticResult property, SearchSemanticResult entity2)
  {
    ArrayList<String> taxonomy_entity1 = new ArrayList();
    ArrayList<String> taxonomy_entity2 = new ArrayList();
    ArrayList<String> taxonomy_entity1_labels = new ArrayList();
    ArrayList<String> taxonomy_entity2_labels = new ArrayList();
    RDFProperty prop = (RDFProperty)property.getEntity();
    taxonomy_entity1.add(entity1.getEntity().getURI());
    taxonomy_entity2.add(entity2.getEntity().getURI());
    taxonomy_entity1_labels.add(entity1.getEntity().getLabel());
    taxonomy_entity2_labels.add(entity2.getEntity().getLabel());
    try
    {
      if (entity1.getEntity().isClass())
      {
        if (!entity1.getSuperclasses().isEmpty())
        {
          taxonomy_entity1.addAll(entity1.getSuperclasses().getUris());
          taxonomy_entity1_labels.addAll(entity1.getSuperclasses().getLabels());
        }
        if (!entity1.getSubclasses().isEmpty())
        {
          taxonomy_entity1.addAll(entity1.getSubclasses().getUris());
          taxonomy_entity1_labels.addAll(entity1.getSubclasses().getLabels());
        }
      }
      else if (entity1.getEntity().isInstance())
      {
        RDFEntityList allClasses = this.osPlugin.getAllClassesOfInstance(entity1.getEntity().getURI());
        
        taxonomy_entity1.addAll(allClasses.getUris());
        taxonomy_entity1_labels.addAll(allClasses.getLabels());
      }
      else
      {
        return false;
      }
      if (entity2.getEntity().isClass())
      {
        if (!entity2.getSuperclasses().isEmpty())
        {
          taxonomy_entity2.addAll(entity2.getSuperclasses().getUris());
          taxonomy_entity2_labels.addAll(entity2.getSuperclasses().getLabels());
        }
        if (!entity2.getSubclasses().isEmpty())
        {
          taxonomy_entity2.addAll(entity2.getSubclasses().getUris());
          taxonomy_entity2_labels.addAll(entity2.getSuperclasses().getLabels());
        }
      }
      else if (entity2.getEntity().isInstance())
      {
        RDFEntityList allClasses = this.osPlugin.getAllClassesOfInstance(entity2.getEntity().getURI());
        
        taxonomy_entity2.addAll(allClasses.getUris());
        taxonomy_entity2_labels.addAll(allClasses.getLabels());
      }
      else
      {
        return false;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    if ((prop.getDomain().isEmpty()) && (prop.getRange().isEmpty())) {
      return true;
    }
    if (prop.getDomain().isEmpty())
    {
      for (String rangeURI : prop.getRange().getUris()) {
        if ((taxonomy_entity1.contains(rangeURI)) || (taxonomy_entity2.contains(rangeURI))) {
          return true;
        }
      }
      for (String rangelabel : prop.getRange().getLabels()) {
        if ((taxonomy_entity1_labels.contains(rangelabel)) || (taxonomy_entity2_labels.contains(rangelabel))) {
          return true;
        }
      }
    }
    if (prop.getRange().isEmpty())
    {
      for (String domainURI : prop.getDomain().getUris()) {
        if ((taxonomy_entity1.contains(domainURI)) || (taxonomy_entity2.contains(domainURI))) {
          return true;
        }
      }
      for (String domainlabel : prop.getDomain().getLabels()) {
        if ((taxonomy_entity1_labels.contains(domainlabel)) || (taxonomy_entity2_labels.contains(domainlabel))) {
          return true;
        }
      }
    }
    for (String domainURI : prop.getDomain().getUris()) {
      if (taxonomy_entity1.contains(domainURI)) {
        for (String rangeURI : prop.getRange().getUris()) {
          if (taxonomy_entity2.contains(rangeURI)) {
            return true;
          }
        }
      }
    }
    for (String domainURI : prop.getDomain().getUris()) {
      if (taxonomy_entity2.contains(domainURI)) {
        for (String rangeURI : prop.getRange().getUris()) {
          if (taxonomy_entity1.contains(rangeURI)) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public void addDiscardedTripleBean(OntoTripleBean ontoTripleBean)
  {
    if (getDiscardedTripleBeans() == null) {
      this.discardedTripleBeans = new ArrayList();
    }
    getDiscardedTripleBeans().add(ontoTripleBean);
  }
  
  public ArrayList<String> getNonCoverageOntologies()
  {
    return this.nonCoverageOntologies;
  }
  
  public void setNonCoverageOntologies(ArrayList<String> nonCoverageOntologies)
  {
    this.nonCoverageOntologies = nonCoverageOntologies;
  }
  
  public ArrayList<OntoTripleBean> getDiscardedTripleBeans()
  {
    return this.discardedTripleBeans;
  }
}

