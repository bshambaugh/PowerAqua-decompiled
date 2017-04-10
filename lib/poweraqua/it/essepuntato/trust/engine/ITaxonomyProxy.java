package it.essepuntato.trust.engine;

import java.util.Set;

public abstract interface ITaxonomyProxy
{
  public static final int CLASS = 0;
  public static final int PROPERTY = 1;
  public static final int INSTANCE = 2;
  
  public abstract int numberOfAllTriples(String paramString);
  
  public abstract int numberOfTriplesWithInstanceAsSubject(String paramString1, String paramString2);
  
  public abstract int numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(String paramString1, String paramString2);
  
  public abstract Set<String> getClassesOf(String paramString1, String paramString2);
  
  public abstract Set<String> getInstancesOf(String paramString1, String paramString2);
  
  public abstract int numberOfTriplesWithClassAsSubject(String paramString1, String paramString2);
  
  public abstract int numberOfTriplesWithClassAsObjectAndTypeProperty(String paramString1, String paramString2);
  
  public abstract int numberOfTripleWithClassAsObjectAndDomainProperty(String paramString1, String paramString2);
  
  public abstract int numberOfTriplesWithPropertyAsSubject(String paramString1, String paramString2);
  
  public abstract int numberOfTriplesWithPropertyAsPredicate(String paramString1, String paramString2);
  
  public abstract Set<String> getSubPropertyOf(String paramString1, String paramString2);
  
  public abstract int entityType(String paramString1, String paramString2);
}

