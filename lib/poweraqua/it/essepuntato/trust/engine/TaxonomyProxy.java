package it.essepuntato.trust.engine;

import it.essepuntato.taxonomy.Category;
import it.essepuntato.taxonomy.HTaxonomy;
import it.essepuntato.taxonomy.Instance;
import it.essepuntato.taxonomy.Property;
import it.essepuntato.taxonomy.exceptions.NoCategoryException;
import it.essepuntato.taxonomy.exceptions.NoInstanceException;
import it.essepuntato.taxonomy.exceptions.NoPropertyException;
import it.essepuntato.taxonomy.exceptions.RootException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaxonomyProxy
  implements ITaxonomyProxy
{
  private HTaxonomy t = null;
  private Set<Map<String, String>> triples = new HashSet();
  
  public TaxonomyProxy()
  {
    try
    {
      this.t = new HTaxonomy();
      Category thing = new Category("Thing");
      Category person = new Category("Person");
      Category man = new Category("Man");
      Category woman = new Category("Woman");
      Category job = new Category("Job");
      this.t.addCategory(thing);
      this.t.addCategory(person);
      this.t.addCategory(man);
      this.t.addCategory(woman);
      this.t.addCategory(job);
      this.t.setRoot(thing);
      
      this.t.subCategoryOf(person, thing);
      this.t.subCategoryOf(job, thing);
      this.t.subCategoryOf(man, person);
      this.t.subCategoryOf(woman, person);
      
      Property hasJob = new Property("hasJob");
      Property fatherOf = new Property("fatherOf");
      Property parentOf = new Property("parentOf");
      this.t.addProperty(hasJob);
      this.t.addProperty(fatherOf);
      this.t.addProperty(parentOf);
      this.t.subPropertyOf(fatherOf, parentOf);
      
      this.t.setDomain(hasJob, person);
      this.t.setRange(hasJob, job);
      
      this.t.setDomain(parentOf, person);
      this.t.setRange(parentOf, person);
      
      this.t.setDomain(fatherOf, man);
      this.t.setRange(fatherOf, person);
      
      Instance bob = new Instance("bob");
      Instance charles = new Instance("charles");
      Instance alice = new Instance("alice");
      Instance scientist = new Instance("scientist");
      Instance doctor = new Instance("doctor");
      this.t.addInstance(bob);
      this.t.addInstance(charles);
      this.t.addInstance(alice);
      this.t.addInstance(scientist);
      this.t.addInstance(doctor);
      
      this.t.instanceOf(bob, man);
      this.t.instanceOf(charles, man);
      this.t.instanceOf(alice, woman);
      this.t.instanceOf(doctor, job);
      this.t.instanceOf(scientist, job);
      
      this.triples.add(generateTriple("Person", "subClassOf", "Thing"));
      this.triples.add(generateTriple("Job", "subClassOf", "Thing"));
      this.triples.add(generateTriple("Man", "subClassOf", "Person"));
      this.triples.add(generateTriple("Woman", "subClassOf", "Person"));
      
      this.triples.add(generateTriple("hasJob", "domain", "Person"));
      this.triples.add(generateTriple("hasJob", "range", "Job"));
      this.triples.add(generateTriple("parentOf", "domain", "Person"));
      this.triples.add(generateTriple("parentOf", "range", "Person"));
      this.triples.add(generateTriple("fatherOf", "domain", "Man"));
      this.triples.add(generateTriple("fatherOf", "range", "Person"));
      
      this.triples.add(generateTriple("fatherOf", "subPropertyOf", "parentOf"));
      
      this.triples.add(generateTriple("alice", "type", "Woman"));
      this.triples.add(generateTriple("bob", "type", "Man"));
      this.triples.add(generateTriple("charles", "type", "Man"));
      this.triples.add(generateTriple("scientist", "type", "Job"));
      this.triples.add(generateTriple("doctor", "type", "Job"));
      
      this.triples.add(generateTriple("bob", "hasJob", "scientist"));
      this.triples.add(generateTriple("charles", "hasJob", "scientist"));
      this.triples.add(generateTriple("bob", "fatherOf", "alice"));
    }
    catch (NoInstanceException ex)
    {
      Logger.getLogger(TaxonomyProxy.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (NoCategoryException ex)
    {
      Logger.getLogger(TaxonomyProxy.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (NoPropertyException ex)
    {
      Logger.getLogger(TaxonomyProxy.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (RootException ex)
    {
      Logger.getLogger(TaxonomyProxy.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public int numberOfAllTriples(String onto)
  {
    return this.triples.size();
  }
  
  public int numberOfTriplesWithInstanceAsSubject(String onto, String i)
  {
    return getCurrentAsSubject(this.triples, i).size();
  }
  
  public int numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(String onto, String c)
  {
    return getCurrentAsSubject(getCurrentAsPredicate(this.triples, "subClassOf"), c).size();
  }
  
  public Set<String> getClassesOf(String onto, String i)
  {
    Set<String> result = new HashSet();
    try
    {
      Set<Category> cats = this.t.getCategoriesByInstance(this.t.getInstanceByName(i));
      Iterator<Category> ite = cats.iterator();
      while (ite.hasNext()) {
        result.add(((Category)ite.next()).getName());
      }
    }
    catch (NoInstanceException ex) {}
    return result;
  }
  
  public Set<String> getInstancesOf(String onto, String c)
  {
    Set<String> result = new HashSet();
    try
    {
      Set<Instance> cats = this.t.getInstances(this.t.getCategoryByName(c));
      Iterator<Instance> ite = cats.iterator();
      while (ite.hasNext()) {
        result.add(((Instance)ite.next()).getName());
      }
    }
    catch (NoCategoryException ex) {}
    return result;
  }
  
  public int numberOfTriplesWithClassAsSubject(String onto, String c)
  {
    return getCurrentAsSubject(this.triples, c).size();
  }
  
  public int numberOfTriplesWithClassAsObjectAndTypeProperty(String onto, String c)
  {
    return getCurrentAsObject(getCurrentAsPredicate(this.triples, "type"), c).size();
  }
  
  public int numberOfTripleWithClassAsObjectAndDomainProperty(String onto, String c)
  {
    return getCurrentAsObject(getCurrentAsPredicate(this.triples, "domain"), c).size();
  }
  
  public int numberOfTriplesWithPropertyAsSubject(String onto, String p)
  {
    return getCurrentAsSubject(this.triples, p).size();
  }
  
  public int numberOfTriplesWithPropertyAsPredicate(String onto, String p)
  {
    return getCurrentAsPredicate(this.triples, p).size();
  }
  
  public Set<String> getSubPropertyOf(String onto, String p)
  {
    Set<String> result = new HashSet();
    try
    {
      Set<Property> cats = this.t.getSubProperties(this.t.getPropertyByName(p));
      Iterator<Property> ite = cats.iterator();
      while (ite.hasNext()) {
        result.add(((Property)ite.next()).getName());
      }
    }
    catch (NoPropertyException ex) {}
    return result;
  }
  
  public int entityType(String onto, String e)
  {
    boolean cat = true;
    boolean prop = true;
    boolean inst = true;
    try
    {
      c = this.t.getCategoryByName(e);
    }
    catch (NoCategoryException ex)
    {
      Category c;
      cat = false;
    }
    try
    {
      p = this.t.getPropertyByName(e);
    }
    catch (NoPropertyException ex)
    {
      Property p;
      prop = false;
    }
    try
    {
      i = this.t.getInstanceByName(e);
    }
    catch (NoInstanceException ex)
    {
      Instance i;
      inst = false;
    }
    if (cat) {
      return 0;
    }
    if (prop) {
      return 1;
    }
    if (inst) {
      return 2;
    }
    Logger.getLogger(TaxonomyProxy.class.getName()).log(Level.SEVERE, "ERROR");
    return -1;
  }
  
  private Map<String, String> generateTriple(String subject, String predicate, String object)
  {
    Map<String, String> result = new HashMap();
    
    result.put("subject", subject);
    result.put("object", object);
    result.put("predicate", predicate);
    
    return result;
  }
  
  private Set<Map<String, String>> getCurrentAsSubject(Set<Map<String, String>> map, String current)
  {
    Set<Map<String, String>> result = new HashSet();
    
    Iterator<Map<String, String>> ite = map.iterator();
    while (ite.hasNext())
    {
      Map<String, String> currentMap = (Map)ite.next();
      if (((String)currentMap.get("subject")).equals(current)) {
        result.add(currentMap);
      }
    }
    return result;
  }
  
  private Set<Map<String, String>> getCurrentAsPredicate(Set<Map<String, String>> map, String current)
  {
    Set<Map<String, String>> result = new HashSet();
    
    Iterator<Map<String, String>> ite = map.iterator();
    while (ite.hasNext())
    {
      Map<String, String> currentMap = (Map)ite.next();
      if (((String)currentMap.get("predicate")).equals(current)) {
        result.add(currentMap);
      }
    }
    return result;
  }
  
  private Set<Map<String, String>> getCurrentAsObject(Set<Map<String, String>> map, String current)
  {
    Set<Map<String, String>> result = new HashSet();
    
    Iterator<Map<String, String>> ite = map.iterator();
    while (ite.hasNext())
    {
      Map<String, String> currentMap = (Map)ite.next();
      if (((String)currentMap.get("object")).equals(current)) {
        result.add(currentMap);
      }
    }
    return result;
  }
}

