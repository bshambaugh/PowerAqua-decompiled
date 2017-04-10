package poweraqua.ranking;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.tripleModel.linguisticTriple.QueryTriple;
import poweraqua.core.tripleModel.ontologyTriple.OntoTriple;
import poweraqua.core.utils.LabelSplitter;
import poweraqua.core.utils.StringUtils;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.triplePhase.OntoTripleBean;
import poweraqua.powermap.triplePhase.TripleMappingTable;

public class MappingRanking
{
  private TripleMappingTable tripleMappingTable;
  private QueryTriple queryTriple;
  private int score;
  
  public MappingRanking(QueryTriple queryTriple, TripleMappingTable tripleMappingTable)
  {
    this.queryTriple = queryTriple;
    this.tripleMappingTable = tripleMappingTable;
    this.score = 1;
  }
  
  public void RankTMT()
  {
    ArrayList<OntoTripleBean> toRankList = get_ToRankList();
    RankTMT(toRankList, 1);
  }
  
  public static Hashtable<Integer, TripleMappingTable> create_rankMappingTables(TripleMappingTable rankTripleMappingTable)
  {
    Hashtable<Integer, TripleMappingTable> rankTripleMappings = new Hashtable();
    if (rankTripleMappingTable == null) {
      return rankTripleMappings;
    }
    for (Iterator i$ = rankTripleMappingTable.getMappingTable().keySet().iterator(); i$.hasNext();)
    {
      ontology = (String)i$.next();
      for (OntoTripleBean otb : (ArrayList)rankTripleMappingTable.getMappingTable().get(ontology))
      {
        int sc = otb.getRankingScore();
        if (rankTripleMappings.containsKey(Integer.valueOf(sc)))
        {
          TripleMappingTable tmt = (TripleMappingTable)rankTripleMappings.get(Integer.valueOf(sc));
          if (tmt.getMappingTable().containsKey(ontology))
          {
            ((ArrayList)tmt.getMappingTable().get(ontology)).add(otb);
          }
          else
          {
            ArrayList<OntoTripleBean> otbs = new ArrayList();
            otbs.add(otb);
            tmt.addOntologyTriples(ontology, otbs);
          }
        }
        else
        {
          TripleMappingTable tmt = new TripleMappingTable();
          ArrayList<OntoTripleBean> otbs = new ArrayList();
          otbs.add(otb);
          tmt.addOntologyTriples(ontology, otbs);
          rankTripleMappings.put(Integer.valueOf(sc), tmt);
        }
      }
    }
    String ontology;
    return rankTripleMappings;
  }
  
  private void RankTMT(ArrayList<OntoTripleBean> toRankList, int criterion_number)
  {
    try
    {
      if (criterion_number < 2)
      {
        System.out.println("Ranking by criterion 1: equivalent mappings");
        ArrayList<OntoTripleBean> criteria1_OTBeans = rankByEquivalentMappings(toRankList);
        if (!criteria1_OTBeans.isEmpty()) {
          if (criteria1_OTBeans.size() == 1)
          {
            ((OntoTripleBean)criteria1_OTBeans.get(0)).setRankingScore(this.score);
            System.out.println("score (criterion 1)" + this.score + " for " + ((OntoTriple)((OntoTripleBean)criteria1_OTBeans.get(0)).getOntoTripleBean().get(0)).getSecondTerm().getIdPlugin());
            
            toRankList.removeAll(criteria1_OTBeans);
            this.score += 1;
          }
          else
          {
            criterion_number = 2;
            
            toRankList.removeAll(criteria1_OTBeans);
            RankTMT(criteria1_OTBeans, criterion_number);
          }
        }
      }
      if ((criterion_number < 3) && (!toRankList.isEmpty()))
      {
        System.out.println("Ranking by criterion 2: ad hoc triples");
        ArrayList<OntoTripleBean> criteria2_OTBeans = rankByAdHocTriples(toRankList);
        if (!criteria2_OTBeans.isEmpty()) {
          if (criteria2_OTBeans.size() == 1)
          {
            ((OntoTripleBean)criteria2_OTBeans.get(0)).setRankingScore(this.score);
            System.out.println("score (criterion 2)" + this.score + " for " + ((OntoTriple)((OntoTripleBean)criteria2_OTBeans.get(0)).getOntoTripleBean().get(0)).getSecondTerm().getIdPlugin());
            
            toRankList.removeAll(criteria2_OTBeans);
            this.score += 1;
          }
          else
          {
            criterion_number = 3;
            toRankList.removeAll(criteria2_OTBeans);
            RankTMT(criteria2_OTBeans, criterion_number);
          }
        }
      }
      if ((criterion_number < 4) && (!toRankList.isEmpty()))
      {
        System.out.println("Ranking by criterion 3: relation coverage");
        ArrayList<OntoTripleBean> criteria3_OTBeans = rankByRelationCoverage(toRankList);
        if (!criteria3_OTBeans.isEmpty()) {
          if (criteria3_OTBeans.size() == 1)
          {
            ((OntoTripleBean)criteria3_OTBeans.get(0)).setRankingScore(this.score);
            System.out.println("score (criterion 3) " + this.score + " for " + ((OntoTriple)((OntoTripleBean)criteria3_OTBeans.get(0)).getOntoTripleBean().get(0)).getSecondTerm().getIdPlugin());
            
            toRankList.removeAll(criteria3_OTBeans);
            this.score += 1;
          }
          else
          {
            criterion_number = 4;
            toRankList.removeAll(criteria3_OTBeans);
            RankTMT(criteria3_OTBeans, criterion_number);
          }
        }
      }
      if ((criterion_number < 5) && (!toRankList.isEmpty()))
      {
        System.out.println("Ranking by criterion 4: exact mappings");
        ArrayList<OntoTripleBean> criteria4_OTBeans = rankByExactMappings(toRankList);
        if (!criteria4_OTBeans.isEmpty()) {
          if (criteria4_OTBeans.size() == 1)
          {
            ((OntoTripleBean)criteria4_OTBeans.get(0)).setRankingScore(this.score);
            System.out.println("score (criterion 4) " + this.score + " for " + ((OntoTriple)((OntoTripleBean)criteria4_OTBeans.get(0)).getOntoTripleBean().get(0)).getSecondTerm().getIdPlugin());
            
            toRankList.removeAll(criteria4_OTBeans);
            this.score += 1;
          }
          else
          {
            criterion_number = 5;
            toRankList.removeAll(criteria4_OTBeans);
            RankTMT(criteria4_OTBeans, criterion_number);
          }
        }
      }
      if ((criterion_number < 6) && (!toRankList.isEmpty()))
      {
        System.out.println("Ranking by criterion 5: direct relationshipsge");
        ArrayList<OntoTripleBean> criteria5_OTBeans = rankByDirectRelationShips(toRankList);
        if (!criteria5_OTBeans.isEmpty()) {
          if (criteria5_OTBeans.size() == 1)
          {
            ((OntoTripleBean)criteria5_OTBeans.get(0)).setRankingScore(this.score);
            System.out.println("score (criterion 5) " + this.score + " for " + ((OntoTriple)((OntoTripleBean)criteria5_OTBeans.get(0)).getOntoTripleBean().get(0)).getSecondTerm().getIdPlugin());
            
            toRankList.removeAll(criteria5_OTBeans);
            this.score += 1;
          }
          else
          {
            criterion_number = 6;
            toRankList.removeAll(criteria5_OTBeans);
            RankTMT(criteria5_OTBeans, criterion_number);
          }
        }
      }
      if ((criterion_number < 7) && (!toRankList.isEmpty()))
      {
        System.out.println("Ranking by criterion 6: there is either answers or direct subclasses");
        ArrayList<OntoTripleBean> criteria6_OTBeans = rankByContainsAnswers(toRankList);
        if (!criteria6_OTBeans.isEmpty()) {
          if (criteria6_OTBeans.size() == 1)
          {
            ((OntoTripleBean)criteria6_OTBeans.get(0)).setRankingScore(this.score);
            System.out.println("score (criterion 6) " + this.score + " for " + ((OntoTriple)((OntoTripleBean)criteria6_OTBeans.get(0)).getOntoTripleBean().get(0)).getSecondTerm().getIdPlugin());
            
            toRankList.removeAll(criteria6_OTBeans);
            this.score += 1;
          }
          else
          {
            criterion_number = 7;
            toRankList.removeAll(criteria6_OTBeans);
            RankTMT(criteria6_OTBeans, criterion_number);
          }
        }
      }
      if (!toRankList.isEmpty())
      {
        for (OntoTripleBean otb : toRankList)
        {
          otb.setRankingScore(this.score);
          System.out.println("score (non criteria) " + this.score + " for " + ((OntoTriple)otb.getOntoTripleBean().get(0)).getSecondTerm().getIdPlugin());
        }
        toRankList = new ArrayList();
        this.score += 1;
      }
    }
    catch (Exception e)
    {
      System.out.println("Exception while ranking the mapping tables " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  public ArrayList<OntoTripleBean> get_ToRankList()
  {
    ArrayList<OntoTripleBean> toRankList = new ArrayList();
    for (String ontology : this.tripleMappingTable.getMappingTable().keySet()) {
      toRankList.addAll((Collection)this.tripleMappingTable.getMappingTable().get(ontology));
    }
    return toRankList;
  }
  
  public ArrayList<OntoTripleBean> rankByEquivalentMappings(ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    ArrayList<OntoTripleBean> results = new ArrayList();
    for (OntoTripleBean otb : ontoTripleBeans)
    {
      boolean add = true;
      for (OntoTriple ot : otb.getOntoTripleBean())
      {
        if ((ot.getFirstTerm() != null) && 
          (ot.getFirstTerm().isHyperHypoNym()))
        {
          add = false;
          break;
        }
        if (ot.getSecondTerm().isHyperHypoNym())
        {
          add = false;
          break;
        }
      }
      if (add) {
        results.add(otb);
      }
    }
    return results;
  }
  
  public ArrayList<OntoTripleBean> rankByAdHocTriples(ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    ArrayList<OntoTripleBean> results = new ArrayList();
    if (this.queryTriple.getRelation() == null) {
      return results;
    }
    for (OntoTripleBean otb : ontoTripleBeans)
    {
      boolean add = false;
      for (OntoTriple ot : otb.getOntoTripleBean())
      {
        if (!ot.isIS_A_RELATION())
        {
          add = true;
          break;
        }
        if (!otb.getAnswer_instances().isEmpty()) {
          add = true;
        }
      }
      if (add) {
        results.add(otb);
      }
    }
    return results;
  }
  
  public ArrayList<OntoTripleBean> rankByRelationCoverage(ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    ArrayList<OntoTripleBean> results = new ArrayList();
    boolean add = false;
    if ((this.queryTriple.getRelation() == null) || (this.queryTriple.getRelation().equals("IS_A_Relation")))
    {
      for (OntoTripleBean otb : ontoTripleBeans)
      {
        add = false;
        OntoTriple ot1 = (OntoTriple)otb.getOntoTripleBean().get(0);
        if (ot1.getFirstTerm() != null)
        {
          if (!ot1.getFirstTerm().getSemanticRelation().equals("ontology_ad_hoc")) {
            add = true;
          }
          if (add) {
            results.add(otb);
          }
        }
      }
      return results;
    }
    for (OntoTripleBean otb : ontoTripleBeans)
    {
      add = false;
      if (otb.size() == 1)
      {
        OntoTriple ot1 = (OntoTriple)otb.getOntoTripleBean().get(0);
        if ((!ot1.getFirstTerm().getSemanticRelation().equals("ontology_ad_hoc")) && 
          (ot1.getRelation() != null) && (!ot1.getRelation().getSemanticRelation().equals("ontology_ad_hoc"))) {
          add = true;
        }
      }
      else if (otb.size() == 2)
      {
        OntoTriple ot1 = (OntoTriple)otb.getOntoTripleBean().get(0);
        OntoTriple ot2 = (OntoTriple)otb.getOntoTripleBean().get(1);
        if ((!ot1.getFirstTerm().getSemanticRelation().equals("ontology_ad_hoc")) && (!ot1.getSecondTerm().getSemanticRelation().equals("ontology_ad_hoc")) && (!ot2.getFirstTerm().getSemanticRelation().equals("ontology_ad_hoc")) && (!ot2.getSecondTerm().getSemanticRelation().equals("ontology_ad_hoc"))) {
          add = true;
        }
      }
      else
      {
        for (OntoTriple ot : otb.getOntoTripleBean()) {
          if ((ot.getRelation() != null) && (!ot.getRelation().getSemanticRelation().equals("ontology_ad_hoc")))
          {
            add = true;
            break;
          }
        }
      }
      if (add) {
        results.add(otb);
      }
    }
    return results;
  }
  
  private boolean exactMappings(SearchSemanticResult entity, String keyword)
  {
    if (entity.getEntity().getLabel().equalsIgnoreCase((String)this.queryTriple.getQueryTerm().get(0))) {
      return true;
    }
    if (entity.getEntity().getLocalName().equalsIgnoreCase((String)this.queryTriple.getQueryTerm().get(0))) {
      return true;
    }
    String localname = entity.getEntity().getLocalName();
    String label = entity.getEntity().getLabel();
    localname = localname.replace("-", " ");
    localname = localname.replace("_", " ");
    localname = LabelSplitter.splitOnCaps(localname);
    if (StringUtils.isSingularPluralExactMapping(localname, keyword)) {
      return true;
    }
    label = label.replace("-", " ");
    label = label.replace("_", " ");
    label = LabelSplitter.splitOnCaps(label);
    if (StringUtils.isSingularPluralExactMapping(label, keyword)) {
      return true;
    }
    return false;
  }
  
  public ArrayList<OntoTripleBean> rankByExactMappings(ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    ArrayList<OntoTripleBean> results = new ArrayList();
    for (OntoTripleBean otb : ontoTripleBeans)
    {
      boolean add = true;
      for (OntoTriple ot : otb.getOntoTripleBean())
      {
        if ((ot.getFirstTerm() != null) && (!ot.getFirstTerm().getSemanticRelation().equals("ontology_ad_hoc")) && (!ot.getFirstTerm().isExact()) && (!exactMappings(ot.getFirstTerm(), (String)this.queryTriple.getQueryTerm().get(0))))
        {
          add = false;
          break;
        }
        if ((!ot.getSecondTerm().getSemanticRelation().equals("ontology_ad_hoc")) && (!ot.getSecondTerm().isExact()) && (!exactMappings(ot.getSecondTerm(), this.queryTriple.getSecondTerm())))
        {
          add = false;
          break;
        }
      }
      if (add) {
        results.add(otb);
      }
    }
    if (results.isEmpty()) {
      for (OntoTripleBean otb : ontoTripleBeans)
      {
        boolean add = false;
        for (OntoTriple ot : otb.getOntoTripleBean())
        {
          if ((ot.getFirstTerm() != null) && (!ot.getFirstTerm().getSemanticRelation().equals("ontology_ad_hoc")) && (ot.getFirstTerm().isExact()))
          {
            add = true;
            break;
          }
          if (ot.getSecondTerm().isExact())
          {
            add = true;
            break;
          }
          if ((ot.getRelation() != null) && (ot.getRelation().isExact()))
          {
            add = true;
            break;
          }
        }
        if (add) {
          results.add(otb);
        }
      }
    }
    return results;
  }
  
  public ArrayList<OntoTripleBean> rankByDirectRelationShips(ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    ArrayList<OntoTripleBean> results = new ArrayList();
    for (OntoTripleBean otb : ontoTripleBeans) {
      if (otb.size() == 1) {
        results.add(otb);
      }
    }
    return results;
  }
  
  public ArrayList<OntoTripleBean> rankByContainsAnswers(ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    ArrayList<OntoTripleBean> results = new ArrayList();
    for (OntoTripleBean otb : ontoTripleBeans) {
      if (!otb.getAnswer_instances().isEmpty()) {
        results.add(otb);
      } else if ((otb.getOntoTripleBean().size() == 1) && 
        (((OntoTriple)otb.getOntoTripleBean().get(0)).getFirstTerm() == null)) {
        if (!((OntoTriple)otb.getOntoTripleBean().get(0)).getSecondTerm().getDirectSubclasses().isEmpty()) {
          results.add(otb);
        }
      }
    }
    return results;
  }
}

