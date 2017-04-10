package poweraqua.core.plugin;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import poweraqua.core.model.myocmlmodel.OcmlClass;
import poweraqua.core.model.myocmlmodel.OcmlInstance;
import poweraqua.core.model.myocmlmodel.OcmlProperty;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.model.myrdfmodel.RDFPath;
import poweraqua.core.model.myrdfmodel.RDFProperty;
import poweraqua.serviceConfig.Repository;

public abstract interface OntologyPlugin
{
  public static final String RDF_REPOSITORY = "RDF";
  public static final String OWL_REPOSITORY = "OWL";
  public static final String DAML_REPOSITORY = "DAML";
  public static final String owl_namespace = "http://www.w3.org/2002/07/owl#";
  public static final String rdf_namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
  public static final String rdfs_namespace = "http://www.w3.org/2000/01/rdf-schema#";
  public static final String xsd_namespace = "http://www.w3.org/2001/XMLSchema#";
  public static final String PLUGIN_JENA = "jena";
  public static final String PLUGIN_SESAME = "sesame";
  public static final String PLUGIN_SESAME2 = "sesame2";
  public static final String PLUGIN_VIRTUOSO = "virtuoso";
  public static final String PLUGIN_WATSON = "watson";
  public static final String PLUGIN_REMOTESPARQL = "remoteSPARQL";
  public static final String ONTO_URL = "ONTO_URL";
  public static final String ONTO_SERVER = "ONTO_SERVER";
  public static final String ONTO_DB = "ONTO_DB";
  
  public abstract String getName();
  
  public abstract String getPluginID();
  
  public abstract void loadPlugin(Repository paramRepository)
    throws Exception;
  
  public abstract void closePlugin();
  
  public abstract String getRepositoryType()
    throws Exception;
  
  public abstract RDFEntityList getAllClasses()
    throws Exception;
  
  public abstract RDFEntityList getAllClassesPeriodically(int paramInt1, int paramInt2)
    throws Exception;
  
  public abstract RDFEntityList getAllProperties()
    throws Exception;
  
  public abstract RDFEntityList getAllPropertiesPeriodically(int paramInt1, int paramInt2)
    throws Exception;
  
  public abstract RDFEntityList getAllInstances()
    throws Exception;
  
  public abstract RDFEntityList getAllInstancesPeriodically(int paramInt1, int paramInt2)
    throws Exception;
  
  public abstract Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForClasses()
    throws Exception;
  
  public abstract Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForProperties()
    throws Exception;
  
  public abstract Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForInstances()
    throws Exception;
  
  public abstract String getLabelOfEntity(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getLiteralValuesOfInstance(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getAllSubClasses(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getDirectSubClasses(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getAllSuperClasses(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getDirectSuperClasses(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getAllClassesOfInstance(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getDirectClassOfInstance(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getAllInstancesOfClass(String paramString, int paramInt)
    throws Exception;
  
  public abstract RDFEntityList getAllPropertiesOfInstance(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getAllPropertiesBetweenClass_Literal(String paramString1, String paramString2)
    throws Exception;
  
  public abstract boolean isInstanceOf(String paramString1, String paramString2)
    throws Exception;
  
  public abstract boolean isNameOfInstance(String paramString1, String paramString2)
    throws Exception;
  
  public abstract RDFEntityList getSlotValue(String paramString1, String paramString2)
    throws Exception;
  
  public abstract RDFEntityList getInstancesWithSlotValue(String paramString1, String paramString2, boolean paramBoolean)
    throws Exception;
  
  public abstract OcmlClass getClassInfo(String paramString)
    throws Exception;
  
  public abstract OcmlInstance getInstanceInfo(String paramString)
    throws Exception;
  
  public abstract OcmlProperty getPropertyInfo(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getKBPropertiesForGenericClass(String paramString1, String paramString2)
    throws Exception;
  
  public abstract RDFEntityList getInstanceProperties(String paramString1, String paramString2)
    throws Exception;
  
  public abstract RDFEntityList getPropertiesForGenericClass(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getKBPropertiesBetweenClasses(String paramString1, String paramString2)
    throws Exception;
  
  public abstract RDFProperty getRDFProperty(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getDomainOfProperty(String paramString)
    throws Exception;
  
  public abstract RDFEntityList getRangeOfProperty(String paramString)
    throws Exception;
  
  public abstract boolean existTripleForProperty(String paramString);
  
  public abstract boolean existTripleForInstance(String paramString);
  
  public abstract boolean isKBTripleClassClass(String paramString1, String paramString2, String paramString3)
    throws Exception;
  
  public abstract boolean isKBTripleClassInstance(String paramString1, String paramString2, String paramString3)
    throws Exception;
  
  public abstract boolean isKBTripleInstanceInstance(String paramString1, String paramString2, String paramString3)
    throws Exception;
  
  public abstract RDFEntityList getGenericInstances(String paramString1, String paramString2, String paramString3)
    throws Exception;
  
  public abstract RDFEntityList getGenericInstancesForLiteral(String paramString1, String paramString2, String paramString3)
    throws Exception;
  
  public abstract RDFEntityList getTripleInstances(String paramString1, String paramString2, String paramString3)
    throws Exception;
  
  public abstract ArrayList<RDFPath> getSchemaIndirectRelations(String paramString1, String paramString2)
    throws Exception;
  
  public abstract ArrayList<RDFPath> getKBIndirectRelations(String paramString1, String paramString2)
    throws Exception;
  
  public abstract ArrayList<RDFPath> getInstanceIndirectRelations(String paramString1, String paramString2)
    throws Exception;
  
  public abstract ArrayList<RDFPath> getKBIndirectRelationsWithLiterals(String paramString1, String paramString2)
    throws Exception;
  
  public abstract RDFEntityList getTripleInstances(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws Exception;
  
  public abstract RDFEntityList getTripleInstancesFromLiteral(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws Exception;
  
  public abstract RDFEntityList getTripleInstancesFromClasses(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws Exception;
  
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
  
  public abstract String entityType(String paramString1, String paramString2);
}

