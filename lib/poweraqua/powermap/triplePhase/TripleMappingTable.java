package poweraqua.powermap.triplePhase;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;

public class TripleMappingTable
{
  private Hashtable<String, ArrayList<OntoTripleBean>> mappingTable;
  
  public TripleMappingTable()
  {
    this.mappingTable = new Hashtable();
  }
  
  public void addOntologyTriples(String ontologyID, ArrayList<OntoTripleBean> ontoTripleBeans)
  {
    getMappingTable().put(ontologyID, ontoTripleBeans);
  }
  
  public void print()
  {
    if (getMappingTable().isEmpty()) {
      System.out.println("EMPTY");
    }
    for (String ontology : getMappingTable().keySet())
    {
      ArrayList<OntoTripleBean> ontoTripleBeans = (ArrayList)getMappingTable().get(ontology);
      System.out.println("Printing ontoTripleBeans " + ontoTripleBeans.size() + " for " + ontology);
      for (OntoTripleBean ontoTripleBean : ontoTripleBeans) {
        ontoTripleBean.print();
      }
    }
  }
  
  public void printLabel()
  {
    for (String ontology : getMappingTable().keySet())
    {
      ArrayList<OntoTripleBean> ontoTripleBeans = (ArrayList)getMappingTable().get(ontology);
      for (OntoTripleBean ontoTripleBean : ontoTripleBeans) {
        ontoTripleBean.printLabel();
      }
    }
  }
  
  public Hashtable<String, ArrayList<OntoTripleBean>> getMappingTable()
  {
    return this.mappingTable;
  }
  
  public RDFEntityList getAllAnswersNoRepetitions()
  {
    RDFEntityList no_repetitions = new RDFEntityList();
    if (!getMappingTable().isEmpty()) {
      for (String ontology : getMappingTable().keySet())
      {
        ArrayList<OntoTripleBean> ontoTripleBeans = (ArrayList)getMappingTable().get(ontology);
        RDFEntityList sketch = ((OntoTripleBean)ontoTripleBeans.get(0)).getAnswer_instances();
        if (sketch.size() <= 600) {
          no_repetitions.addNewRDFEntities(sketch);
        } else {
          System.out.println("Too many Answers");
        }
      }
    }
    return no_repetitions;
  }
  
  public ArrayList<String> getAnswersSummary(String ontology, int limit)
  {
    ArrayList<String> answerSet = new ArrayList();
    ArrayList<OntoTripleBean> ontoTripleBeans = (ArrayList)getMappingTable().get(ontology);
    try
    {
      RDFEntityList sketch = ((OntoTripleBean)ontoTripleBeans.get(0)).getAnswer_instances();
      i = 0;
      for (RDFEntity ent : sketch.getAllRDFEntities())
      {
        if (i >= limit) {
          break;
        }
        answerSet.add(ent.getLabel());
        
        i++;
      }
    }
    catch (Exception e)
    {
      int i;
      System.out.println("Could not get a summary of answers");
      e.printStackTrace();
    }
    return answerSet;
  }
}

