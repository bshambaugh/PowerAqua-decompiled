package poweraqua.powermap.triplePhase;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import poweraqua.core.model.myocmlmodel.OcmlClass;
import poweraqua.core.model.myocmlmodel.OcmlInstance;
import poweraqua.core.model.myocmlmodel.OcmlProperty;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.core.tripleModel.linguisticTriple.QueryTriple;
import poweraqua.core.tripleModel.ontologyTriple.OntoTriple;
import poweraqua.powermap.elementPhase.SearchSemanticResult;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.serviceConfig.MultiOntologyManager;

public class AnswersEngine
{
  private ArrayList<QueryTriple> queryTriples;
  private Hashtable<QueryTriple, TripleMappingTable> ontoKBTripleMappings;
  private MappingSession mapSession;
  private ArrayList<String> sortedOntologies;
  private static ArrayList<String> datatypeClasses = new ArrayList(Arrays.asList(new String[] { "http://xmlns.com/foaf/0.1/Document" }));
  
  public AnswersEngine(MappingSession mapSession, ArrayList<QueryTriple> queryTriples, ArrayList<String> sortedOntologies, Hashtable<QueryTriple, TripleMappingTable> ontoKBTripleMappings)
    throws Exception
  {
    this.mapSession = mapSession;
    this.queryTriples = queryTriples;
    this.ontoKBTripleMappings = ontoKBTripleMappings;
    this.sortedOntologies = sortedOntologies;
    for (QueryTriple queryTriple : this.queryTriples)
    {
      System.out.println("Answers engine is retrieving results for triple ");queryTriple.print();
      tripleMappingTable = (TripleMappingTable)this.ontoKBTripleMappings.get(queryTriple);
      if (tripleMappingTable == null) {
        System.out.println("TRIPLE MAPPING TABLE NULL !!!!!!!!!!!!!!");
      } else {
        for (String ontology : tripleMappingTable.getMappingTable().keySet())
        {
          System.out.println("from" + ontology);
          ArrayList<OntoTripleBean> ontoTripleBeans = (ArrayList)tripleMappingTable.getMappingTable().get(ontology);
          ArrayList<OntoTripleBean> ontoTripleBeansAux = (ArrayList)ontoTripleBeans.clone();
          for (OntoTripleBean ontoTripleBean : ontoTripleBeansAux)
          {
            RDFEntityList results = AnswersEngineOntoTripleBean(ontoTripleBean);
            
            ontoTripleBean.setAnswer_instances(results);
          }
        }
      }
    }
    TripleMappingTable tripleMappingTable;
  }
  
  public void AnswersEngineDescription(OntoTriple ontoTriple)
    throws Exception
  {
    if (ontoTriple.getTypeQuestion() == 1) {
      try
      {
        System.out.println("Description of: " + ontoTriple.getSecondTerm().getEntity().getURI() + " ( " + ontoTriple.getSecondTerm().getSemanticRelation() + " ) ");
        
        OntologyPlugin osPlugin = this.mapSession.getMultiOntologyManager().getPlugin(ontoTriple.getSecondTerm().getIdPlugin());
        if (ontoTriple.getSecondTerm().getEntity().isInstance())
        {
          OcmlInstance ocmlInstance = osPlugin.getInstanceInfo(ontoTriple.getSecondTerm().getEntity().getURI());
          System.out.println("Instance: " + ocmlInstance.toString());
        }
        else if (ontoTriple.getSecondTerm().getEntity().isClass())
        {
          OcmlClass ocmlClass = osPlugin.getClassInfo(ontoTriple.getSecondTerm().getEntity().getURI());
          System.out.println("Class: " + ocmlClass.toString());
        }
        else if (ontoTriple.getSecondTerm().getEntity().isProperty())
        {
          OcmlProperty ocmlProperty = osPlugin.getPropertyInfo(ontoTriple.getSecondTerm().getEntity().getURI());
          System.out.println("Property: " + ocmlProperty.toString());
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
  
  public RDFEntityList AnswersEngineOntoTriple(OntoTriple ontoTriple)
    throws Exception
  {
    OntologyPlugin osPlugin = this.mapSession.getMultiOntologyManager().getPlugin(ontoTriple.getFirstTerm().getIdPlugin());
    
    return AnswersEngineOntoTriple(osPlugin, ontoTriple);
  }
  
  public static RDFEntityList AnswersEngineOntoTriple(OntologyPlugin osPlugin, OntoTriple ontoTriple)
    throws Exception
  {
    RDFEntityList instances = new RDFEntityList();
    if (ontoTriple.getTypeQuestion() == 1)
    {
      System.out.println("This is a description triple, it will return instances or subclasses if a class");
      RDFEntity entity = ontoTriple.getSecondTerm().getEntity();
      if (entity.isClass())
      {
        instances = osPlugin.getAllInstancesOfClass(entity.getURI(), 200);
        if (instances.isEmpty()) {
          instances = ontoTriple.getSecondTerm().getDirectSubclasses();
        }
      }
      return instances;
    }
    if (ontoTriple.getTypeQuestion() == 5)
    {
      System.out.println("ONTO-TRIPLE REQUIRES A YES/NO AS AN ANSWER");
      instances.setAffirmativeNegative(true);
      return instances;
    }
    boolean IS_A_RELATION = ontoTriple.isIS_A_RELATION();
    RDFEntity ontoqueryterm = ontoTriple.getFirstTerm().getEntity();
    RDFEntity ontorelation = null;
    if (!IS_A_RELATION) {
      ontorelation = ontoTriple.getRelation().getEntity();
    }
    RDFEntity ontosecondterm = ontoTriple.getSecondTerm().getEntity();
    if (datatypeClasses.contains(ontoqueryterm.getURI())) {
      ontoqueryterm.setType("datatype");
    }
    if ((ontoqueryterm.isClass()) && (!IS_A_RELATION) && (ontosecondterm.isInstance()))
    {
      instances = osPlugin.getGenericInstances(ontoqueryterm.getURI(), ontorelation.getURI(), ontosecondterm.getURI());
    }
    else if ((ontoqueryterm.isInstance()) && (!IS_A_RELATION) && (ontosecondterm.isClass()))
    {
      instances = osPlugin.getGenericInstances(ontosecondterm.getURI(), ontorelation.getURI(), ontoqueryterm.getURI());
    }
    else if ((ontoqueryterm.isClass()) && (!IS_A_RELATION) && (ontosecondterm.isClass()))
    {
      instances = osPlugin.getTripleInstances(ontoqueryterm.getURI(), ontorelation.getURI(), ontosecondterm.getURI());
    }
    else if ((ontoqueryterm.isClass()) && (!IS_A_RELATION) && (ontosecondterm.isLiteral()))
    {
      ArrayList<String> groupLiteralURIs = ontosecondterm.getGroupLiteralURIs();
      for (String groupLiteralURI : groupLiteralURIs)
      {
        boolean is_answer = osPlugin.isInstanceOf(groupLiteralURI, ontoqueryterm.getURI());
        if (is_answer) {
          instances.addRDFEntity(new RDFEntity("instance", groupLiteralURI, osPlugin.getLabelOfEntity(groupLiteralURI), osPlugin.getPluginID()));
        }
      }
      instances = osPlugin.getGenericInstancesForLiteral(ontoqueryterm.getURI(), ontorelation.getURI(), ontosecondterm.getLabel());
    }
    else if ((ontoqueryterm.isDataType()) && (ontosecondterm.isClass()))
    {
      RDFEntityList instancesOfClass = osPlugin.getAllInstancesOfClass(ontosecondterm.getURI(), -1);
      for (RDFEntity inst : instancesOfClass.getAllRDFEntities())
      {
        RDFEntityList instance_values = osPlugin.getSlotValue(inst.getURI(), ontorelation.getURI());
        instance_values.addRefersToInstance(inst);
        instances.addAllRDFEntity(instance_values);
      }
    }
    else if ((ontoqueryterm.isDataType()) && (ontosecondterm.isInstance()))
    {
      instances = osPlugin.getSlotValue(ontosecondterm.getURI(), ontorelation.getURI());
    }
    else if (IS_A_RELATION)
    {
      if (ontoTriple.getFirstTerm().getEntity().isClass())
      {
        RDFEntityList answerList = osPlugin.getAllInstancesOfClass(ontoTriple.getFirstTerm().getEntity().getURI(), -1);
        if (answerList.isEmpty()) {
          answerList = ontoTriple.getFirstTerm().getDirectSubclasses();
        }
        return answerList;
      }
      if ((ontoqueryterm.isInstance()) && (IS_A_RELATION) && (ontosecondterm.isClass()))
      {
        instances.addRDFEntity(ontoqueryterm);
        instances.setAffirmativeNegative(true);
      }
      else
      {
        System.out.println("TODO IS-A RELATIONS");
      }
    }
    else if ((ontoqueryterm.isInstance()) && (ontosecondterm.isInstance()))
    {
      instances.addRDFEntity(ontoqueryterm);
    }
    else if ((ontoqueryterm.isLiteral()) && (ontosecondterm.isInstance()))
    {
      instances.addRDFEntity(ontoqueryterm);
    }
    else
    {
      System.out.println("TODO IN ANSWERS ENGINE: !!!!!!!!!!! ");
      ontoTriple.print();
    }
    return instances;
  }
  
  public RDFEntityList AnswersEngineOntoTripleBean(OntoTripleBean ontoTripleBean)
    throws Exception
  {
    RDFEntityList vacio = new RDFEntityList();
    try
    {
      OntoTriple first_ontoTriple = (OntoTriple)ontoTripleBean.getOntoTripleBean().get(0);
      
      OntologyPlugin osPlugin = this.mapSession.getMultiOntologyManager().getPlugin(first_ontoTriple.getSecondTerm().getIdPlugin());
      
      return AnswersEngineOntoTripleBean(osPlugin, ontoTripleBean);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return vacio;
  }
  
  public static RDFEntityList AnswersEngineOntoTripleBean(OntologyPlugin osPlugin, OntoTripleBean ontoTripleBean)
    throws Exception
  {
    RDFEntityList instances = new RDFEntityList();
    if (ontoTripleBean.size() == 1)
    {
      OntoTriple ontoTriple = (OntoTriple)ontoTripleBean.getOntoTripleBean().get(0);
      instances = AnswersEngineOntoTriple(osPlugin, ontoTriple);
    }
    else if (ontoTripleBean.size() == 2)
    {
      if (!ontoTripleBean.getAnswer_instances().isEmpty()) {
        return ontoTripleBean.getAnswer_instances();
      }
      System.out.println("Calculating the answer for the indirect ontoTripleBean");
      OntoTriple first_ontoTriple = (OntoTriple)ontoTripleBean.getOntoTripleBean().get(0);
      OntoTriple second_ontoTriple = (OntoTriple)ontoTripleBean.getOntoTripleBean().get(1);
      
      RDFEntity first_ontoqueryterm = first_ontoTriple.getFirstTerm().getEntity();
      RDFEntity first_ontosecondterm = first_ontoTriple.getSecondTerm().getEntity();
      RDFEntity first_ontorelation = null;
      boolean first_IS_A_RELATION = first_ontoTriple.isIS_A_RELATION();
      if (!first_IS_A_RELATION) {
        first_ontorelation = first_ontoTriple.getRelation().getEntity();
      }
      RDFEntity second_ontoqueryterm = second_ontoTriple.getFirstTerm().getEntity();
      RDFEntity second_ontosecondterm = second_ontoTriple.getSecondTerm().getEntity();
      RDFEntity second_ontorelation = null;
      boolean second_IS_A_RELATION = second_ontoTriple.isIS_A_RELATION();
      if (!second_IS_A_RELATION) {
        second_ontorelation = second_ontoTriple.getRelation().getEntity();
      }
      if ((first_ontoqueryterm.isClass()) && (!first_IS_A_RELATION) && (first_ontosecondterm.isClass()) && (first_ontosecondterm.getURI().equals(second_ontoqueryterm.getURI())) && (!second_IS_A_RELATION))
      {
        if (second_ontosecondterm.isInstance()) {
          instances = osPlugin.getTripleInstances(first_ontoqueryterm.getURI(), first_ontorelation.getURI(), first_ontosecondterm.getURI(), second_ontorelation.getURI(), second_ontosecondterm.getURI());
        } else if (second_ontosecondterm.isLiteral()) {
          instances = osPlugin.getTripleInstancesFromLiteral(first_ontoqueryterm.getURI(), first_ontorelation.getURI(), first_ontosecondterm.getURI(), second_ontorelation.getURI(), second_ontosecondterm.getLabel());
        } else {
          instances = osPlugin.getTripleInstancesFromClasses(first_ontoqueryterm.getURI(), first_ontorelation.getURI(), first_ontosecondterm.getURI(), second_ontorelation.getURI(), second_ontosecondterm.getURI());
        }
      }
      else if ((first_ontoqueryterm.isClass()) && (first_ontoqueryterm.getURI().equals(second_ontoqueryterm.getURI())))
      {
        if ((first_IS_A_RELATION) && (!second_IS_A_RELATION))
        {
          instances = AnswersEngineOntoTriple(osPlugin, second_ontoTriple);
        }
        else
        {
          instances = AnswersEngineOntoTriple(osPlugin, first_ontoTriple);
          RDFEntityList instances2 = AnswersEngineOntoTriple(osPlugin, second_ontoTriple);
          instances = InstancesCombination_AND(instances, instances2);
        }
      }
      else if ((first_ontoqueryterm.isClass()) && (second_IS_A_RELATION) && (!first_IS_A_RELATION) && (first_ontosecondterm.getURI().equals(second_ontosecondterm.getURI())))
      {
        first_ontoTriple.print();
        second_ontoTriple.print();
        if (second_ontoqueryterm.isInstance()) {
          instances = osPlugin.getGenericInstances(first_ontoqueryterm.getURI(), first_ontorelation.getURI(), second_ontoqueryterm.getURI());
        } else if (second_ontoqueryterm.isClass()) {
          instances = osPlugin.getTripleInstances(first_ontoqueryterm.getURI(), first_ontorelation.getURI(), second_ontoqueryterm.getURI());
        }
      }
      else if ((first_ontoqueryterm.isClass()) && (first_IS_A_RELATION) && (!second_IS_A_RELATION) && (first_ontosecondterm.getURI().equals(second_ontoqueryterm.getURI())))
      {
        if (second_ontosecondterm.isClass()) {
          instances = osPlugin.getTripleInstances(first_ontoqueryterm.getURI(), second_ontorelation.getURI(), second_ontosecondterm.getURI());
        } else if (second_ontosecondterm.isInstance()) {
          instances = osPlugin.getGenericInstances(first_ontoqueryterm.getURI(), second_ontorelation.getURI(), second_ontosecondterm.getURI());
        }
      }
      else
      {
        System.out.println("Answers Engine - By default!!!");
        
        instances = AnswersEngineOntoTriple(osPlugin, first_ontoTriple);
        
        RDFEntityList instances2 = AnswersEngineOntoTriple(osPlugin, second_ontoTriple);
        instances = InstancesCombination_AND(instances, instances2);
      }
    }
    return instances;
  }
  
  public static RDFEntityList InstancesCombination_AND(RDFEntityList list1, RDFEntityList list2)
  {
    RDFEntityList results = new RDFEntityList();
    for (RDFEntity ent2 : list2.getAllRDFEntities()) {
      if (list1.isExactRDFEntityContained(ent2)) {
        results.addRDFEntity(ent2);
      }
    }
    return results;
  }
}

