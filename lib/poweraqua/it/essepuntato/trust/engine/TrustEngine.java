package it.essepuntato.trust.engine;

import it.essepuntato.db.kvdb.HierarchicalKVDB;
import it.essepuntato.db.kvdb.IStorer;
import it.essepuntato.db.kvdb.Indexer;
import it.essepuntato.db.kvdb.KVStorer;
import it.essepuntato.db.kvdb.exception.IndexException;
import it.essepuntato.db.kvdb.exception.KVDBException;
import it.essepuntato.trust.engine.exception.TrustEngineException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TrustEngine
  implements ITrustEngine
{
  private HierarchicalKVDB db;
  private ITaxonomyProxy tp;
  private static double DEFAULT_TRUST = 0.5D;
  private static int maxEntries = 1000;
  
  public TrustEngine(File database, ITaxonomyProxy tp)
    throws TrustEngineException
  {
    if (!database.exists()) {
      database.mkdirs();
    }
    IStorer s = new KVStorer();
    Indexer i;
    try
    {
      i = new Indexer(database, maxEntries, s);
    }
    catch (IndexException ex)
    {
      throw new TrustEngineException("Problems in the initialization of the index", ex);
    }
    this.db = HierarchicalKVDB.getNewInstance("essepuntato", database, s, i);
    if (!new File(database.getAbsolutePath() + File.separator + "index.xml").exists()) {
      try
      {
        this.db.store();
      }
      catch (KVDBException ex)
      {
        throw new TrustEngineException("Problems in the storing of the index", ex);
      }
    }
    this.tp = tp;
  }
  
  public void store()
    throws TrustEngineException
  {
    try
    {
      this.db.store();
    }
    catch (KVDBException ex)
    {
      throw new TrustEngineException("Problems to store data", ex);
    }
  }
  
  public void evaluate(String ontology, String subject, String predicate, String object, String user, double value)
    throws TrustEngineException
  {
    if (checkInput(ontology, user, subject, predicate, object)) {
      try
      {
        this.db.set(user + " " + ontology + " " + subject + " " + predicate + " " + object, Double.toString(value));
      }
      catch (KVDBException ex)
      {
        throw new TrustEngineException("Problems to assign the value to the triple", ex);
      }
    } else {
      throw new TrustEngineException("The input is not well formed:\n\tonto: " + ontology + " \n" + "\tuser: " + user + " \n" + "\tsubject: " + subject + " \n" + "\tpredicate: " + predicate + " \n" + "\tobject: " + object);
    }
  }
  
  public double getEvaluation(String onto, String subject, String predicate, String object, String user)
    throws TrustEngineException
  {
    if (checkInput(onto, user, subject, predicate, object)) {
      try
      {
        List<String> result = this.db.get(user + " " + onto + " " + subject + " " + predicate + " " + object);
        if (result.isEmpty()) {
          return getDefault(onto, user);
        }
        return new Double((String)result.get(0)).doubleValue();
      }
      catch (KVDBException ex)
      {
        throw new TrustEngineException("Problems to get the value for the triple", ex);
      }
    }
    throw new TrustEngineException("The input is not well formed:\n\tonto: " + onto + " \n" + "\tuser: " + user + " \n" + "\tsubject: " + subject + " \n" + "\tpredicate: " + predicate + " \n" + "\tobject: " + object);
  }
  
  public double getGlobalEvaluation(String onto, String subject, String predicate, String object)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public double getEntityEvaluation(String onto, String entity, String user)
    throws TrustEngineException
  {
    Double result = Double.valueOf(0.0D);
    Double defaultValue = Double.valueOf(getDefault(onto, user));
    int type = this.tp.entityType(onto, entity);
    if (type == 2)
    {
      int totalTriplesConsidered = 0;
      
      int numberOfInstances = this.tp.numberOfTriplesWithInstanceAsSubject(onto, entity);
      if (numberOfInstances > -1)
      {
        totalTriplesConsidered += numberOfInstances;
        try
        {
          int currentInstancesLength = 0;
          Iterator<String> listInstances = this.db.get(user + " " + onto + " " + entity + " * *").iterator();
          while (listInstances.hasNext())
          {
            currentInstancesLength++;
            result = Double.valueOf(result.doubleValue() + new Double((String)listInstances.next()).doubleValue());
          }
          result = Double.valueOf(result.doubleValue() + (numberOfInstances - currentInstancesLength) * defaultValue.doubleValue());
        }
        catch (KVDBException ex)
        {
          throw new TrustEngineException("Problems to find the triples with the instance '" + entity + "' as subject", ex);
        }
      }
      Iterator<String> classesOfInstance = this.tp.getClassesOf(onto, entity).iterator();
      while (classesOfInstance.hasNext())
      {
        String currentClass = (String)classesOfInstance.next();
        int numberOfClasses = this.tp.numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(onto, currentClass);
        if (numberOfClasses > -1)
        {
          totalTriplesConsidered += numberOfClasses;
          try
          {
            int currentClassesLength = 0;
            Iterator<String> listClasses = this.db.get(user + " " + onto + " " + currentClass + " subClassOf *").iterator();
            while (listClasses.hasNext())
            {
              currentClassesLength++;
              result = Double.valueOf(result.doubleValue() + new Double((String)listClasses.next()).doubleValue());
            }
            result = Double.valueOf(result.doubleValue() + (numberOfClasses - currentClassesLength) * defaultValue.doubleValue());
          }
          catch (KVDBException ex)
          {
            throw new TrustEngineException("Problems to find the triples with the instances' classes as subject and the subClassOf property", ex);
          }
        }
      }
      if (totalTriplesConsidered != 0) {
        result = Double.valueOf(result.doubleValue() / new Double(totalTriplesConsidered).doubleValue());
      }
    }
    else if (type == 0)
    {
      int totalTriplesConsidered = 0;
      
      int numberOfClassesAsSubject = this.tp.numberOfTriplesWithClassAsSubject(onto, entity);
      if (numberOfClassesAsSubject > -1)
      {
        totalTriplesConsidered += numberOfClassesAsSubject;
        try
        {
          int currentClassesLength = 0;
          Iterator<String> listClasses = this.db.get(user + " " + onto + " " + entity + " * *").iterator();
          while (listClasses.hasNext())
          {
            currentClassesLength++;
            result = Double.valueOf(result.doubleValue() + new Double((String)listClasses.next()).doubleValue());
          }
          result = Double.valueOf(result.doubleValue() + (numberOfClassesAsSubject - currentClassesLength) * defaultValue.doubleValue());
        }
        catch (KVDBException ex)
        {
          throw new TrustEngineException("Problems to find the triples with the instance '" + entity + "' as subject", ex);
        }
      }
      Set<String> instances = this.tp.getInstancesOf(onto, entity);
      totalTriplesConsidered += instances.size();
      
      Iterator<String> ite = instances.iterator();
      while (ite.hasNext()) {
        result = Double.valueOf(result.doubleValue() + getEntityEvaluation(onto, (String)ite.next(), user));
      }
      Set<String> classes = this.tp.getClassesOf(onto, entity);
      
      Iterator<String> classesIterator = classes.iterator();
      while (classesIterator.hasNext())
      {
        String currentClass = (String)classesIterator.next();
        int numberOfClasses = this.tp.numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(onto, currentClass);
        if (numberOfClasses > -1)
        {
          totalTriplesConsidered += numberOfClasses;
          try
          {
            int currentClassesLength = 0;
            Iterator<String> listClassesInDb = this.db.get(user + " " + onto + " " + currentClass + " subClassOf *").iterator();
            while (listClassesInDb.hasNext())
            {
              currentClassesLength++;
              result = Double.valueOf(result.doubleValue() + new Double((String)listClassesInDb.next()).doubleValue());
            }
            result = Double.valueOf(result.doubleValue() + (numberOfClasses - currentClassesLength) * defaultValue.doubleValue());
          }
          catch (KVDBException ex)
          {
            throw new TrustEngineException("Problems to find the triples with the class '" + currentClass + "' as subject and the subClassOf predicate", ex);
          }
        }
      }
      int numberOfDomainTriple = this.tp.numberOfTripleWithClassAsObjectAndDomainProperty(onto, entity);
      if (numberOfDomainTriple > -1)
      {
        totalTriplesConsidered += numberOfDomainTriple;
        try
        {
          int currentDomainLength = 0;
          Iterator<String> listDomainInDb = this.db.get(user + " " + onto + " * domain " + entity).iterator();
          while (listDomainInDb.hasNext())
          {
            currentDomainLength++;
            result = Double.valueOf(result.doubleValue() + new Double((String)listDomainInDb.next()).doubleValue());
          }
          result = Double.valueOf(result.doubleValue() + (numberOfDomainTriple - currentDomainLength) * defaultValue.doubleValue());
        }
        catch (KVDBException ex)
        {
          throw new TrustEngineException("Problems to find the triples with the class '" + entity + "' as object and the domain predicate", ex);
        }
      }
      if (totalTriplesConsidered != 0) {
        result = Double.valueOf(result.doubleValue() / new Double(totalTriplesConsidered).doubleValue());
      }
    }
    else if (type == 1)
    {
      int totalTriplesConsidered = 0;
      
      int numberOfPropertyAsSubjectTriples = this.tp.numberOfTriplesWithPropertyAsSubject(onto, entity);
      if (numberOfPropertyAsSubjectTriples > -1)
      {
        totalTriplesConsidered += numberOfPropertyAsSubjectTriples;
        try
        {
          int currentPropertyLength = 0;
          Iterator<String> listProperties = this.db.get(user + " " + onto + " " + entity + " * *").iterator();
          while (listProperties.hasNext())
          {
            currentPropertyLength++;
            result = Double.valueOf(result.doubleValue() + new Double((String)listProperties.next()).doubleValue());
          }
          result = Double.valueOf(result.doubleValue() + (numberOfPropertyAsSubjectTriples - currentPropertyLength) * defaultValue.doubleValue());
        }
        catch (KVDBException ex)
        {
          throw new TrustEngineException("Problems to find the triples with the property '" + entity + "' as subject", ex);
        }
      }
      Set<String> classes = this.tp.getClassesOf(onto, entity);
      
      Iterator<String> classesIterator = classes.iterator();
      while (classesIterator.hasNext())
      {
        String currentClass = (String)classesIterator.next();
        int numberOfClasses = this.tp.numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(onto, currentClass);
        if (numberOfClasses > -1)
        {
          totalTriplesConsidered += numberOfClasses;
          try
          {
            int currentClassesLength = 0;
            Iterator<String> listClassesInDb = this.db.get(user + " " + onto + " " + currentClass + " subClassOf *").iterator();
            while (listClassesInDb.hasNext())
            {
              currentClassesLength++;
              result = Double.valueOf(result.doubleValue() + new Double((String)listClassesInDb.next()).doubleValue());
            }
            result = Double.valueOf(result.doubleValue() + (numberOfClasses - currentClassesLength) * defaultValue.doubleValue());
          }
          catch (KVDBException ex)
          {
            throw new TrustEngineException("Problems to find the triples with the property '" + currentClass + "' as subject and the subClassOf predicate", ex);
          }
        }
      }
      int numberOfTripleWithPropertySpecified = this.tp.numberOfTriplesWithPropertyAsPredicate(onto, entity);
      if (numberOfTripleWithPropertySpecified > -1)
      {
        totalTriplesConsidered += numberOfTripleWithPropertySpecified;
        try
        {
          int currentPropertiesLength = 0;
          Iterator<String> listPropertiesInDb = this.db.get(user + " " + onto + " * " + entity + " *").iterator();
          while (listPropertiesInDb.hasNext())
          {
            currentPropertiesLength++;
            result = Double.valueOf(result.doubleValue() + new Double((String)listPropertiesInDb.next()).doubleValue());
          }
          result = Double.valueOf(result.doubleValue() + (numberOfTripleWithPropertySpecified - currentPropertiesLength) * defaultValue.doubleValue());
        }
        catch (KVDBException ex)
        {
          throw new TrustEngineException("Problems to find the triples with the property '" + entity + "' as predicate", ex);
        }
      }
      Set<String> properties = this.tp.getSubPropertyOf(onto, entity);
      
      Iterator<String> propertiesIterator = properties.iterator();
      while (propertiesIterator.hasNext())
      {
        String currentProperty = (String)propertiesIterator.next();
        
        int numberOfProperites = this.tp.numberOfTriplesWithPropertyAsPredicate(onto, currentProperty);
        if (numberOfProperites > -1)
        {
          totalTriplesConsidered += numberOfProperites;
          try
          {
            int currentPropertiesLength = 0;
            Iterator<String> listPropertiesInDb = this.db.get(user + " " + onto + " * " + currentProperty + " *").iterator();
            while (listPropertiesInDb.hasNext())
            {
              currentPropertiesLength++;
              result = Double.valueOf(result.doubleValue() + new Double((String)listPropertiesInDb.next()).doubleValue());
            }
            result = Double.valueOf(result.doubleValue() + (numberOfProperites - currentPropertiesLength) * defaultValue.doubleValue());
          }
          catch (KVDBException ex)
          {
            throw new TrustEngineException("Problems to find the triples with the property '" + currentProperty + "' as predicate", ex);
          }
        }
      }
      if (totalTriplesConsidered != 0) {
        result = Double.valueOf(result.doubleValue() / new Double(totalTriplesConsidered).doubleValue());
      } else {
        throw new TrustEngineException("The entity specified has not any triple associated");
      }
    }
    else
    {
      throw new TrustEngineException("The type of the entity specified does not exits");
    }
    return result.doubleValue();
  }
  
  public double getGlobalEntityEvaluation(String onto, String entity)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public double getOntologyEvaluation(String onto, String user)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public double getGlobalOntologyEvaluation(String onto, String user)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public void setInitialTrust(String ontology, String user, Double trust)
    throws TrustEngineException
  {
    try
    {
      this.db.set(user + " " + ontology + " p:initial", trust.toString());
    }
    catch (KVDBException ex)
    {
      throw new TrustEngineException("Problem for setting the new initial trust value for the user '" + user + "' and the ontology '" + ontology + "'", ex);
    }
  }
  
  public double getInitialTrust(String ontology, String user)
    throws TrustEngineException
  {
    try
    {
      List<String> result = this.db.get(user + " " + ontology + " p:initial");
      if (result.isEmpty()) {
        return DEFAULT_TRUST;
      }
      return new Double((String)result.get(0)).doubleValue();
    }
    catch (KVDBException ex)
    {
      throw new TrustEngineException("Problem for getting the initial trust value for the user '" + user + "' and the ontology '" + ontology + "'", ex);
    }
  }
  
  private double getDefault(String onto, String user)
    throws TrustEngineException
  {
    try
    {
      int triplesInKB = this.tp.numberOfAllTriples(onto);
      List<String> inDb = this.db.get(user + " " + onto + " f:n");
      int triplesInDB = inDb.isEmpty() ? 0 : new Integer((String)inDb.get(0)).intValue();
      double initialTrust = getInitialTrust(onto, user);
      List<String> aT = this.db.get(user + " " + onto + " p:average");
      double averageTrust = aT.isEmpty() ? 0.0D : new Double((String)aT.get(0)).doubleValue();
      IDefaultValue defaultValue = new LogarithmicDefault(triplesInKB, triplesInDB, initialTrust, averageTrust);
      return defaultValue.getDefault();
    }
    catch (KVDBException ex)
    {
      throw new TrustEngineException("Problems to retrieve the default trust value", ex);
    }
  }
  
  private boolean checkInput(String onto, String user, String subject, String predicate, String object)
  {
    boolean result = true;
    if (onto != null) {
      try
      {
        URI uri = new URI(onto);
        if ((onto.length() == 1) && (onto.equals("*"))) {
          result = false;
        }
      }
      catch (URISyntaxException ex)
      {
        result = false;
      }
    }
    if (user != null) {
      try
      {
        URI uri = new URI(onto);
        if ((user.length() == 1) && (user.equals("*"))) {
          result = false;
        }
      }
      catch (URISyntaxException ex)
      {
        result = false;
      }
    }
    if (subject != null) {
      try
      {
        if (((subject.length() == 1) && (subject.equals("*"))) || (subject.startsWith("f:")) || (subject.startsWith("p:")) || (subject.startsWith("!"))) {
          result = false;
        }
        uri = new URI(onto);
      }
      catch (URISyntaxException ex)
      {
        URI uri;
        result = false;
      }
    }
    if (predicate != null) {
      try
      {
        URI uri = new URI(onto);
        if (((predicate.length() == 1) && (predicate.equals("*"))) || (predicate.startsWith("f:")) || (predicate.startsWith("p:")) || (predicate.startsWith("!"))) {
          result = false;
        }
      }
      catch (URISyntaxException ex)
      {
        result = false;
      }
    }
    if (object != null) {
      try
      {
        URI uri = new URI(onto);
        if (((object.length() == 1) && (object.equals("*"))) || (object.startsWith("f:")) || (object.startsWith("p:")) || (object.startsWith("!"))) {
          result = false;
        }
      }
      catch (URISyntaxException ex)
      {
        result = false;
      }
    }
    return result;
  }
}

