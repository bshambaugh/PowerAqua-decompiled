package poweraqua.fusion;

import java.util.ArrayList;
import java.util.List;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.tripleModel.ontologyTriple.OntoTriple;
import poweraqua.powermap.triplePhase.OntoTripleBean;

public class RDFEntityEntry
{
  RDFEntity value;
  RDFEntity conditionedBy = null;
  List<RDFEntity> refersToValues;
  OntoTripleBean ontoTripleBean;
  String ontologyId = null;
  String ontologyAlias = null;
  int rankingScore;
  RDFEntityEntry conditionalValue = null;
  
  public RDFEntityEntry(OntoTripleBean ontoTripleBean)
  {
    this.ontoTripleBean = ontoTripleBean;
    this.refersToValues = new ArrayList();
  }
  
  public RDFEntityEntry(OntoTripleBean ontoTripleBean, RDFEntity value)
  {
    this(ontoTripleBean);
    this.value = value;
    if (value.getRefers_to() != null) {
      this.refersToValues.add(value.getRefers_to());
    }
  }
  
  public RDFEntity getValue()
  {
    return this.value;
  }
  
  public void setValue(RDFEntity value)
  {
    this.value = value;
    if (value.getRefers_to() != null) {
      addRefersToValue(value.getRefers_to());
    }
  }
  
  public List<RDFEntity> getRefersToValues()
  {
    return this.refersToValues;
  }
  
  public void addRefersToValue(RDFEntity value)
  {
    for (RDFEntity ref : this.refersToValues) {
      if (ref.getURI().equals(value.getURI())) {
        return;
      }
    }
    this.refersToValues.add(value);
  }
  
  public OntoTripleBean getOntoTripleBean()
  {
    return this.ontoTripleBean;
  }
  
  public List<OntoTriple> getOntoTriples()
  {
    return this.ontoTripleBean.getOntoTripleBean();
  }
  
  public String getOntologyId()
  {
    return this.ontologyId;
  }
  
  public void setOntologyId(String ontologyId)
  {
    this.ontologyId = ontologyId;
    if (this.ontologyAlias == null) {
      this.ontologyAlias = getAlias(ontologyId);
    }
  }
  
  private String getAlias(String uri)
  {
    int index = uri.lastIndexOf('/');
    if (index > 0) {
      this.ontologyAlias = uri.substring(index + 1);
    }
    return this.ontologyAlias;
  }
  
  public void setOntologyAlias(String alias)
  {
    this.ontologyAlias = alias;
  }
  
  public String getOntologyAlias()
  {
    return this.ontologyAlias;
  }
  
  public int getRankingScore()
  {
    return this.ontoTripleBean.getRankingScore();
  }
  
  public RDFEntityEntry getConditionalValue()
  {
    return this.conditionalValue;
  }
  
  public void setConditionalValue(RDFEntityEntry conditionalValue)
  {
    this.conditionalValue = conditionalValue;
  }
}

