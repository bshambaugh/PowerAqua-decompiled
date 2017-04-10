package TrustEngine;

import it.essepuntato.trust.engine.ITaxonomyProxy;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.serviceConfig.MultiOntologyManager;

public class PowerAquaTaxonomyProxy
  implements ITaxonomyProxy
{
  MappingSession mappingSession;
  
  public PowerAquaTaxonomyProxy(MappingSession mappingSession)
  {
    try
    {
      this.mappingSession = mappingSession;
    }
    catch (Exception ex)
    {
      Logger.getLogger(PowerAquaTaxonomyProxy.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public int entityType(String onto, String e)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    String t = osPlugin.entityType(onto, e);
    if (t == null)
    {
      System.out.println("Entity " + e + " from ontology " + onto + " does not have a type");
      return -1;
    }
    if (t.equals("Class")) {
      return 0;
    }
    if (t.equals("Property")) {
      return 1;
    }
    if (t.equals("Individual")) {
      return 2;
    }
    return -1;
  }
  
  public Set<String> getClassesOf(String onto, String i)
  {
    Set<String> res = new HashSet();
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.getClassesOf(onto, i);
  }
  
  public int numberOfAllTriples(String onto)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.numberOfAllTriples(onto);
  }
  
  public int numberOfTriplesWithInstanceAsSubject(String onto, String i)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.numberOfTriplesWithInstanceAsSubject(onto, i);
  }
  
  public int numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(String onto, String c)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(onto, c);
  }
  
  public Set<String> getInstancesOf(String onto, String c)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.getInstancesOf(onto, c);
  }
  
  public int numberOfTriplesWithClassAsSubject(String onto, String c)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.numberOfTriplesWithClassAsSubject(onto, c);
  }
  
  public int numberOfTriplesWithClassAsObjectAndTypeProperty(String onto, String c)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.numberOfTriplesWithClassAsObjectAndTypeProperty(onto, c);
  }
  
  public int numberOfTripleWithClassAsObjectAndDomainProperty(String onto, String c)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.numberOfTripleWithClassAsObjectAndDomainProperty(onto, c);
  }
  
  public int numberOfTriplesWithPropertyAsSubject(String onto, String p)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.numberOfTriplesWithPropertyAsSubject(onto, p);
  }
  
  public int numberOfTriplesWithPropertyAsPredicate(String onto, String p)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.numberOfTriplesWithPropertyAsPredicate(onto, p);
  }
  
  public Set<String> getSubPropertyOf(String onto, String p)
  {
    OntologyPlugin osPlugin = this.mappingSession.getMultiOntologyManager().getPlugin(onto);
    return osPlugin.getSubPropertyOf(onto, p);
  }
}

