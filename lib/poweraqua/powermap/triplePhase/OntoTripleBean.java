package poweraqua.powermap.triplePhase;

import java.io.PrintStream;
import java.util.ArrayList;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.tripleModel.ontologyTriple.OntoTriple;

public class OntoTripleBean
{
  private boolean instance_based = false;
  private ArrayList<OntoTriple> ontoTripleBean;
  private RDFEntityList answer_instances;
  private int rankingScore;
  private boolean semantic_interpretation = false;
  
  public OntoTripleBean()
  {
    this.ontoTripleBean = new ArrayList();
    setAnswer_instances(new RDFEntityList());
    setAnswer_instances(new RDFEntityList());
  }
  
  public OntoTripleBean(boolean instance_based)
  {
    this.ontoTripleBean = new ArrayList();
    
    this.instance_based = instance_based;
    setAnswer_instances(new RDFEntityList());
  }
  
  public boolean isEmpty()
  {
    if (this.ontoTripleBean.isEmpty()) {
      return true;
    }
    return false;
  }
  
  public int size()
  {
    return this.ontoTripleBean.size();
  }
  
  public void addBean(OntoTriple ontoTriple)
  {
    if (!getOntoTripleBean().contains(ontoTriple)) {
      getOntoTripleBean().add(ontoTriple);
    }
  }
  
  public void addBeans(OntoTripleBean ontoTripleBean)
  {
    for (OntoTriple ob : ontoTripleBean.getOntoTripleBean()) {
      addBean(ob);
    }
  }
  
  public ArrayList<OntoTriple> getOntoTripleBean()
  {
    return this.ontoTripleBean;
  }
  
  public static boolean hasAnyAdHocInstanceBasedAnswer(ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    for (OntoTripleBean bean : ontoTripleBeans) {
      if ((bean.isInstance_based()) && (bean.size() == 1)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean hasAnyInstanceBasedAnswer(ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    for (OntoTripleBean bean : ontoTripleBeans) {
      if (bean.isInstance_based()) {
        return true;
      }
    }
    return false;
  }
  
  public void printShort()
  {
    for (OntoTriple ontoTriple : this.ontoTripleBean) {
      try
      {
        ontoTriple.print();
      }
      catch (Exception e)
      {
        System.out.println("Exception printing OntoTriple .. " + e);
      }
    }
  }
  
  public void print()
  {
    System.out.println(">>>>>>> OntoTripleBean (ranking score (0 highest): " + getRankingScore());
    for (OntoTriple ontoTriple : this.ontoTripleBean) {
      try
      {
        ontoTriple.print();
      }
      catch (Exception e)
      {
        System.out.println("Exception printing OntoTriple .. " + e);
      }
    }
    if (!getAnswer_instances().isEmpty())
    {
      System.out.println("");
      RDFEntityList resultant_instances = getAnswer_instances();
      System.out.println("Results: " + resultant_instances.size() + " answers");
      if (resultant_instances.size() > 20) {
        for (int i = 0; i < 20; i++) {
          System.out.println(((RDFEntity)resultant_instances.getAllRDFEntities().get(i)).toString());
        }
      } else {
        System.out.println(resultant_instances.toString());
      }
    }
    System.out.println("<<<<<<<>>>>>>>");
  }
  
  public void printLabel()
  {
    for (OntoTriple ontoTriple : this.ontoTripleBean) {
      try
      {
        ontoTriple.printLabel();
      }
      catch (Exception e)
      {
        System.out.println("Exception printing OntoTriple .. " + e);
      }
    }
    if (!getAnswer_instances().isEmpty())
    {
      System.out.println(" |");
      RDFEntityList resultant_instances = getAnswer_instances();
      for (RDFEntity instance : resultant_instances.getAllRDFEntities()) {
        System.out.print(instance.getLabel() + " | ");
      }
    }
  }
  
  public boolean isInstance_based()
  {
    return this.instance_based;
  }
  
  public void setInstance_based(boolean instance_based)
  {
    this.instance_based = instance_based;
  }
  
  public RDFEntityList getAnswer_instances()
  {
    return this.answer_instances;
  }
  
  public void setAnswer_instances(RDFEntityList answer_instances)
  {
    this.answer_instances = answer_instances;
  }
  
  public int getRankingScore()
  {
    return this.rankingScore;
  }
  
  public void setRankingScore(int rankingScore)
  {
    this.rankingScore = rankingScore;
  }
  
  public boolean isSemantic_interpretation()
  {
    return this.semantic_interpretation;
  }
  
  public void setSemantic_interpretation(boolean semantic_interpretation)
  {
    this.semantic_interpretation = semantic_interpretation;
  }
}

