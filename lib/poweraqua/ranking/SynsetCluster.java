package poweraqua.ranking;

import java.util.ArrayList;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.tripleModel.ontologyTriple.OntoTriple;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.triplePhase.OntoTripleBean;

public class SynsetCluster
{
  private ArrayList<String> ontologies;
  private ArrayList<OntoTripleBean> equivalentOTBs;
  
  public SynsetCluster()
  {
    this.ontologies = new ArrayList();
    this.equivalentOTBs = new ArrayList();
  }
  
  public SynsetCluster(OntoTripleBean otb)
  {
    this.ontologies = new ArrayList();
    this.equivalentOTBs = new ArrayList();
    this.equivalentOTBs.add(otb);
    String ontology = ((OntoTriple)otb.getOntoTripleBean().get(0)).getSecondTerm().getEntity().getIdPlugin();
    this.ontologies.add(ontology);
  }
  
  public SynsetCluster(ArrayList<OntoTripleBean> otbs)
  {
    this.ontologies = new ArrayList();
    this.equivalentOTBs = new ArrayList();
    addEquivalentOTBs(otbs);
  }
  
  public ArrayList<String> getOntologies()
  {
    return this.ontologies;
  }
  
  public Integer getPopularity()
  {
    ArrayList<String> ontologiesWithValidSemanticIntepretation = new ArrayList();
    for (OntoTripleBean otb : this.equivalentOTBs) {
      if (otb.isSemantic_interpretation())
      {
        String ontology = ((OntoTriple)otb.getOntoTripleBean().get(0)).getSecondTerm().getEntity().getIdPlugin();
        if (!ontologiesWithValidSemanticIntepretation.contains(ontology)) {
          ontologiesWithValidSemanticIntepretation.add(ontology);
        }
      }
    }
    return Integer.valueOf(ontologiesWithValidSemanticIntepretation.size());
  }
  
  public ArrayList<String> getValidSemantiOntologies()
  {
    ArrayList<String> ontologiesWithValidSemanticIntepretation = new ArrayList();
    for (OntoTripleBean otb : this.equivalentOTBs) {
      if (otb.isSemantic_interpretation())
      {
        String ontology = ((OntoTriple)otb.getOntoTripleBean().get(0)).getSecondTerm().getEntity().getIdPlugin();
        if (!ontologiesWithValidSemanticIntepretation.contains(ontology)) {
          ontologiesWithValidSemanticIntepretation.add(ontology);
        }
      }
    }
    return ontologiesWithValidSemanticIntepretation;
  }
  
  public ArrayList<OntoTripleBean> getEquivalentOTBs()
  {
    return this.equivalentOTBs;
  }
  
  public void addEquivalentOTBs(ArrayList<OntoTripleBean> equivalentOTBs)
  {
    for (OntoTripleBean otb : equivalentOTBs) {
      addEquivalentOTB(otb);
    }
  }
  
  public void addEquivalentOTB(OntoTripleBean otb)
  {
    this.equivalentOTBs.add(otb);
    String ontology = ((OntoTriple)otb.getOntoTripleBean().get(0)).getSecondTerm().getEntity().getIdPlugin();
    if (!this.ontologies.contains(ontology)) {
      this.ontologies.add(ontology);
    }
  }
  
  public boolean isContained(OntoTripleBean OTB)
  {
    return this.equivalentOTBs.contains(OTB);
  }
  
  public boolean isContained(ArrayList<OntoTripleBean> OTBs)
  {
    for (OntoTripleBean otb : OTBs) {
      if (!isContained(otb)) {
        return false;
      }
    }
    return true;
  }
}

