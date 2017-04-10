package virtuosoPlugin;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import poweraqua.core.model.myocmlmodel.OcmlClass;
import poweraqua.core.model.myocmlmodel.OcmlInstance;
import poweraqua.core.model.myocmlmodel.OcmlProperty;
import poweraqua.core.model.myrdfmodel.MyURI;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.model.myrdfmodel.RDFPath;
import poweraqua.core.model.myrdfmodel.RDFProperty;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.serviceConfig.RepositoryVirtuoso;
import virtuosoPlugin.virtuosoHelpers.NotVirtuosoRepository;

public class VirtuosoPlugin
  implements OntologyPlugin, Serializable
{
  private String name;
  Connection con = null;
  private RepositoryVirtuoso repository;
  private org.openrdf.repository.Repository sesameRepository = null;
  private ResultSet result;
  private int timeoutLimit = 300000;
  private static int counter = 0;
  private static final String NOT_RDF_INSTANCES = ". FILTER( str(?i) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>)";
  private static final String NOT_MIXED_CLASSES = " . FILTER( str(?c) != <http://www.w3.org/2000/01/rdf-schema#Class> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Literal> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Datatype> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Container> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> && str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> && str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> && str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> && str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> && str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> && str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> && str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> && str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> && str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> && str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> && str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> && str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> && str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )";
  private static final String NOT_RDF_CLASSES = " . FILTER( str(?c) != <http://www.w3.org/2000/01/rdf-schema#Class> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Literal> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Datatype> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Container> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> )";
  private static final String NOT_OWL_CLASSES = " . FILTER(  str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> && str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> && str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> && str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> && str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> && str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> && str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> && str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> && str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> && str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> && str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> && str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> && str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )";
  private static final String NOT_RDF_PROPERTIES = ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)";
  private static final String NOT_RDF_PROPERTIES1 = ". FILTER( str(?property) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?property) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?property) != <http://www.w3.org/2000/01/rdf-schema#member>&& str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?property) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?property) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?property) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first>)";
  private static final String NOT_RDF_PROPERTIES2 = ". FILTER(str(?property2) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?property2) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?property2) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?property2) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?property2) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?property2) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first>)";
  static FileWriter filewriter;
  
  static
  {
    try
    {
      filewriter = new FileWriter("exception.txt");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public VirtuosoPlugin()
  {
    setName("virtuoso");
  }
  
  public void logIntoServer()
    throws Exception
  {
    System.out.println("ToDo");
  }
  
  public void loadPlugin(poweraqua.serviceConfig.Repository repository)
    throws Exception
  {
    if ((repository instanceof RepositoryVirtuoso)) {
      this.repository = ((RepositoryVirtuoso)repository);
    } else {
      throw new NotVirtuosoRepository();
    }
  }
  
  public RDFEntityList getAllClasses()
    throws Exception
  {
    try
    {
      String sparql = "";
      RDFEntityList aux = new RDFEntityList();
      if (isOWLRepository())
      {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT distinct ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {{?c rdf:type owl:Class}. " + "OPTIONAL {?c rdfs:label ?l}. " + "FILTER (isURI(?c)) " + " . FILTER(  str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> && str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> && str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> && str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> && str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> && str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> && str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> && str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> && str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> && str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> && str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> && str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> && str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )" + "}";
        
        System.out.println("1: done");
        
        execute(sparql, -1);
        aux = retrieveRDFEntityFrom("class");
        if (aux.isEmpty()) {
          System.out.println("getClasses: Weird owl .. ");
        }
      }
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT distinct ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {{?c rdf:type rdfs:Class}. OPTIONAL {?c rdfs:label ?l}. FILTER (isURI(?c))" + " . FILTER( str(?c) != <http://www.w3.org/2000/01/rdf-schema#Class> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Literal> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Datatype> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Container> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> && str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> && str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> && str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> && str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> && str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> && str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> && str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> && str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> && str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> && str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> && str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> && str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> && str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )" + "}";
      
      System.out.println("2: done");
      
      execute(sparql, -1);
      aux.addNewRDFEntities(retrieveRDFEntityFrom("class"));
      RDFEntityList localRDFEntityList1 = aux;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getAllInstancesOfClassPeriodically(String class_uri, int offset, int limit)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT distinct ?i ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {{?i rdf:type <" + class_uri + ">}. OPTIONAL {?i rdfs:label ?l} FILTER (isURI(?i))} " + "LIMIT " + limit + " OFFSET " + offset;
      
      execute(sparql, -1);
      RDFEntityList localRDFEntityList = retrieveRDFEntityFrom("instance");return localRDFEntityList;
    }
    finally {}
  }
  
  public RDFEntityList getAllClassesPeriodically(int offset, int limit)
    throws Exception
  {
    try
    {
      String sparql = "";
      RDFEntityList aux = new RDFEntityList();
      if (isOWLRepository())
      {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT distinct ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {{?c rdf:type owl:Class}. OPTIONAL {?c rdfs:label ?l} FILTER (isURI(?c)) " + " . FILTER(  str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> && str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> && str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> && str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> && str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> && str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> && str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> && str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> && str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> && str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> && str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> && str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> && str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )" + "}" + "LIMIT " + limit + " OFFSET " + offset;
        
        execute(sparql, -1);
        aux = retrieveRDFEntityFrom("class");
      }
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT distinct ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {{?c rdf:type rdfs:Class}. " + "OPTIONAL {?c rdfs:label ?l} " + "FILTER (isURI(?c))" + " . FILTER( str(?c) != <http://www.w3.org/2000/01/rdf-schema#Class> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Literal> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Datatype> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Container> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> && str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> && str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> && str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> && str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> && str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> && str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> && str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> && str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> && str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> && str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> && str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> && str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> && str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )" + "}" + "LIMIT " + limit + " OFFSET " + offset;
      
      execute(sparql, -1);
      aux.addNewRDFEntities(retrieveRDFEntityFrom("class"));
      RDFEntityList localRDFEntityList1 = aux;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getAllProperties()
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      String sparql = "";
      if (isOWLRepository())
      {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT distinct ?p ?d ?r ?l ?dl ?rl FROM <" + this.repository.getGraphIRI() + "> WHERE {{?p rdf:type ?X}. " + "OPTIONAL {?p rdfs:domain ?d . OPTIONAL { ?d rdfs:label ?dl} } " + "OPTIONAL {?p rdfs:range ?r . OPTIONAL { ?r rdfs:label ?rl} } " + "OPTIONAL {?p rdfs:label ?l} " + "FILTER (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " + "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> )}";
        
        execute(sparql, -1);
      }
      else
      {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT distinct ?p ?d ?r ?l ?dl ?rl FROM <" + this.repository.getGraphIRI() + "> WHERE {{?p rdf:type rdf:Property}. " + "OPTIONAL {?p rdfs:domain ?d . OPTIONAL { ?d rdfs:label ?dl} }. " + "OPTIONAL {?p rdfs:range ?r . OPTIONAL { ?r rdfs:label ?rl} }. " + "OPTIONAL {?p rdfs:label ?l}}";
        
        execute(sparql, -1);
      }
      aux.addNewRDFEntities(retrieveFullPropertiesFrom());
      RDFEntityList localRDFEntityList1 = aux;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getAllPropertiesPeriodically(int offset, int limit)
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      String sparql = "";
      if (isOWLRepository())
      {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT distinct ?p ?d ?r ?l ?dl ?rl FROM <" + this.repository.getGraphIRI() + "> WHERE {{?p rdf:type ?X}. " + "OPTIONAL {?p rdfs:domain ?d . OPTIONAL { ?d rdfs:label ?dl} } " + "OPTIONAL {?p rdfs:range ?r . OPTIONAL { ?r rdfs:label ?rl} } " + "OPTIONAL {?p rdfs:label ?l} " + "FILTER (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " + "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> )}" + "LIMIT " + limit + " OFFSET " + offset;
        
        execute(sparql, -1);
        aux = retrieveFullPropertiesFrom();
      }
      RDFEntityList localRDFEntityList1 = aux;return localRDFEntityList1;
    }
    finally {}
  }
  
  public ArrayList<RDFPath> getSchemaIndirectRelations(String sourceURI, String targetURI)
    throws Exception
  {
    try
    {
      ArrayList<RDFPath> paths = new ArrayList();
      Logger log_poweraqua = Logger.getLogger("poweraqua");
      if (this.repository.getRepositoryName().equals("bbc_backstage"))
      {
        log_poweraqua.log(Level.INFO, "TOO EXPENSIVE: Number of indirect paths between " + sourceURI + " and " + targetURI + " :" + paths.size());
      }
      else
      {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT distinct ?property ?proplabel ?reference ?reflabel ?property2 ?prop2label ?subject ?originlabel ?destiny ?targetlabel FROM  <" + this.repository.getGraphIRI() + "> WHERE {?instOrigin rdf:type ?subject. ?instRef rdf:type ?reference. ?instTarget rdf:type ?destiny. " + "?instOrigin ?property ?instRef. ?instRef ?property2 ?instTarget. " + "OPTIONAL {?property rdfs:label ?proplabel}. " + "OPTIONAL {?reference rdfs:label ?reflabel}. " + "OPTIONAL {?subject rdfs:label ?originlabel}. " + "OPTIONAL {?destiny rdfs:label ?targetlabel}. " + "OPTIONAL {?property2 rdfs:label ?prop2label}. " + "FILTER (" + "(str(?subject) = <" + sourceURI + "> && str(?destiny) = <" + targetURI + "> )" + "|| " + "(str(?subject) = <" + targetURI + "> && str(?destiny) = <" + sourceURI + "> )" + "&& " + "str(?reference) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> " + "&& str(?reference) != <http://www.w3.org/2000/01/rdf-schema#Resource> " + "&& str(?reference) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List>" + ")" + ". FILTER( str(?property) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?property) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?property) != <http://www.w3.org/2000/01/rdf-schema#member>&& str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?property) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?property) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?property) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first>)" + ". FILTER(str(?property2) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?property2) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?property2) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?property2) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?property2) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?property2) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first>)" + "}";
        
        execute(sparql, -1);
        paths.addAll(retrieveFullPathFrom());
      }
      String sparql = paths;return sparql;
    }
    finally {}
  }
  
  public boolean isKBTripleClassClass(String class1_uri, String relation, String class2_uri)
    throws Exception
  {
    try
    {
      String sparql1 = "ASK FROM <" + this.repository.getGraphIRI() + "> " + "WHERE { ?instorigin rdf:type <" + class1_uri + ">. ?insttarget rdf:type <" + class2_uri + ">. ?instorigin <" + relation + "> ?insttarget . " + "}";
      
      execute(sparql1, -1);
      if ((this.result == null) || (!this.result.next()))
      {
        sparql2 = "ASK FROM <" + this.repository.getGraphIRI() + "> " + "WHERE { ?instorigin rdf:type <" + class1_uri + ">. ?insttarget rdf:type <" + class2_uri + ">. ?insttarget <" + relation + "> ?instorigin}";
        
        execute(sparql2, -1);
        if ((this.result == null) || (!this.result.next()))
        {
          boolean bool = false;return bool;
        }
      }
      String sparql2 = 1;return sparql2;
    }
    finally {}
  }
  
  public boolean isKBTripleClassInstance(String class_uri, String relation, String instance_uri)
    throws Exception
  {
    try
    {
      String sparql = "ASK FROM <" + this.repository.getGraphIRI() + "> " + "WHERE { ?instorigin rdf:type <" + class_uri + ">.  ?instorigin <" + relation + "> <" + instance_uri + ">}";
      
      execute(sparql, -1);
      if ((this.result == null) || (!this.result.next()))
      {
        sparql = "ASK FROM <" + this.repository.getGraphIRI() + "> " + "WHERE  { ?instorigin rdf:type <" + class_uri + ">.  <" + instance_uri + ">  <" + relation + "> ?instorigin}";
        
        execute(sparql, -1);
        if ((this.result == null) || (!this.result.next()))
        {
          bool = false;return bool;
        }
      }
      boolean bool = true;return bool;
    }
    finally {}
  }
  
  public boolean isKBTripleInstanceInstance(String instance1_uri, String relation, String instance2_uri)
    throws Exception
  {
    try
    {
      String sparql = "ASK FROM <" + this.repository.getGraphIRI() + "> WHERE  { <" + instance2_uri + ">  <" + relation + "> <" + instance1_uri + ">}";
      
      execute(sparql, -1);
      if ((this.result == null) || (!this.result.next()))
      {
        sparql2 = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ASK FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance1_uri + ">  <" + relation + "> <" + instance2_uri + ">}";
        
        execute(sparql2, -1);
        if ((this.result == null) || (!this.result.next()))
        {
          boolean bool = false;return bool;
        }
      }
      String sparql2 = 1;return sparql2;
    }
    finally {}
  }
  
  public ArrayList<RDFPath> getKBIndirectRelations(String class_sourceURI, String instance_targetURI)
    throws Exception
  {
    try
    {
      ArrayList<RDFPath> paths = new ArrayList();
      
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> ASK FROM <" + this.repository.getGraphIRI() + "> WHERE  {?instOrigin rdf:type <" + class_sourceURI + ">}";
      
      execute(sparql, -1);
      if ((this.result == null) || (!this.result.next()))
      {
        ArrayList<RDFPath> localArrayList1 = paths;return localArrayList1;
      }
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT distinct ?property ?reference ?property2  FROM  <" + this.repository.getGraphIRI() + "> WHERE { " + " ?instorigin rdf:type <" + class_sourceURI + ">.  " + " ?referencekb rdf:type ?reference. " + " ?referencekb ?property2 <" + instance_targetURI + ">. " + "{{ " + " ?property rdfs:range ?reference. " + " ?property2 rdfs:domain ?reference. " + " ?instorigin ?property ?referencekb. } " + " UNION " + " {?property  rdfs:domain ?reference. " + " ?property2  rdfs:domain ?reference. " + " ?referencekb ?property ?instorigin}}} ";
      
      execute(sparql, 20);
      paths.addAll(retrievePathFrom());
      for (Object i$ = paths.iterator(); ((Iterator)i$).hasNext();)
      {
        RDFPath path = (RDFPath)((Iterator)i$).next();
        path.getRDFProperty1().setDomain(getDomainOfProperty(path.getRDFProperty1().getURI()));
        path.getRDFProperty1().setRange(getRangeOfProperty(path.getRDFProperty1().getURI()));
        path.getRDFProperty2().setDomain(getDomainOfProperty(path.getRDFProperty2().getURI()));
        path.getRDFProperty2().setRange(getRangeOfProperty(path.getRDFProperty2().getURI()));
      }
      i$ = paths;return (ArrayList<RDFPath>)i$;
    }
    finally {}
  }
  
  public ArrayList<RDFPath> getInstanceIndirectRelations(String instance_sourceURI, String instance_targetURI)
    throws Exception
  {
    try
    {
      ArrayList<RDFPath> paths = new ArrayList();
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT distinct ?property ?referenceEnt ?property2  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + instance_sourceURI + "> ?property ?reference. ?reference ?property2 <" + instance_targetURI + ">. " + " ?reference rdf:type ?referenceEnt }";
      
      execute(sparql, -1);
      paths.addAll(retrievePathFrom());
      
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT distinct ?property ?referenceEnt ?property2  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?reference ?property <" + instance_sourceURI + "> . ?reference ?property2 <" + instance_targetURI + ">. " + " ?reference rdf:type ?referenceEnt " + "FILTER(str(?property) != ?property2)" + "} LIMIT 1";
      
      execute(sparql, -1);
      paths.addAll(retrievePathFrom());
      
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT distinct ?property ?referenceEnt ?property2  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?reference ?property <" + instance_sourceURI + "> . <" + instance_targetURI + ">  ?property2 ?reference. " + " ?reference rdf:type ?referenceEnt " + "}";
      
      execute(sparql, -1);
      paths.addAll(retrievePathFrom());
      for (RDFPath path : paths)
      {
        path.getRDFProperty1().setDomain(getDomainOfProperty(path.getRDFProperty1().getURI()));
        path.getRDFProperty1().setRange(getRangeOfProperty(path.getRDFProperty1().getURI()));
        path.getRDFProperty2().setDomain(getDomainOfProperty(path.getRDFProperty2().getURI()));
        path.getRDFProperty2().setRange(getRangeOfProperty(path.getRDFProperty2().getURI()));
      }
      ??? = paths;return (ArrayList<RDFPath>)???;
    }
    finally {}
  }
  
  private RDFEntityList getAllSubClassesWithInference(String class_uri)
    throws Exception
  {
    try
    {
      int limit = 100;
      RDFEntityList classes = new RDFEntityList();
      String sparql;
      String sparql;
      if (class_uri.startsWith("node")) {
        sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?c ?l  FROM  <" + this.repository.getGraphIRI() + ">" + "WHERE {" + "{SELECT DISTINCT ?c ?path ?route ?jump " + "WHERE{" + "{SELECT ?c ?Class WHERE { ?c rdfs:subClassOf ?Class}" + "} " + "OPTION ( TRANSITIVE, T_DISTINCT, t_in(?c), t_out(?Class), t_step('path_id') as ?path, t_step(?c) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " + "FILTER ( ?Class = \"" + class_uri + "\" )}" + "}. OPTIONAL{ ?c rdfs:label ?l}" + "} LIMIT " + limit;
      } else {
        sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?c ?l  FROM  <" + this.repository.getGraphIRI() + ">" + "WHERE {" + "{SELECT DISTINCT ?c ?path ?route ?jump " + "WHERE{" + "{SELECT ?c ?Class WHERE { ?c rdfs:subClassOf ?Class}" + "} " + "OPTION ( TRANSITIVE, T_DISTINCT, t_in(?c), t_out(?Class), t_step('path_id') as ?path, t_step(?c) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " + "FILTER ( ?Class = <" + class_uri + "> )}" + "}. OPTIONAL{ ?c rdfs:label ?l}" + "} LIMIT " + limit;
      }
      execute(sparql, -1);
      classes = retrieveRDFEntityFrom("class");
      if (classes == null) {
        classes = new RDFEntityList();
      }
      RDFEntityList localRDFEntityList1 = classes;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getAllSubClasses(String class_uri)
    throws Exception
  {
    try
    {
      RDFEntityList classes = new RDFEntityList();
      
      classes = getAllSubClassesWithInference(class_uri);
      
      RDFEntityList localRDFEntityList1 = classes;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getDirectSubClasses(String class_uri)
    throws Exception
  {
    try
    {
      String sparql = "SELECT DISTINCT ?c ?l  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?c rdfs:subClassOf <" + class_uri + "> . " + "OPTIONAL{?c rdfs:label ?l} . " + "FILTER ( isURI(?c) && str(?c) != <" + class_uri + "> ) . }";
      
      execute(sparql, -1);
      RDFEntityList classes = retrieveRDFEntityFrom("class");
      if (classes == null) {
        classes = new RDFEntityList();
      }
      if (isOWLRepository())
      {
        c2 = getSubClassesFromIntersectionDefinition(class_uri);
        if (classes == null)
        {
          RDFEntityList localRDFEntityList1 = c2;return localRDFEntityList1;
        }
        classes.addNewRDFEntities(c2);
      }
      RDFEntityList c2 = classes;return c2;
    }
    finally {}
  }
  
  private RDFEntityList getAllSuperClassesWithInference(String class_uri)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?c ?l  FROM  <" + this.repository.getGraphIRI() + ">" + "WHERE {" + "{SELECT DISTINCT ?c ?path ?route ?jump " + "WHERE{" + "{SELECT ?c ?Class WHERE { ?Class rdfs:subClassOf ?c}" + "} " + "OPTION ( TRANSITIVE, T_DISTINCT, t_in(?c), t_out(?Class), t_step('path_id') as ?path, t_step(?c) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " + "FILTER ( ?Class = <" + class_uri + "> )}" + "}. OPTIONAL{ ?c rdfs:label ?l}" + "}";
      
      execute(sparql, -1);
      RDFEntityList classes = retrieveRDFEntityFrom("class");
      if (classes == null) {
        classes = new RDFEntityList();
      }
      RDFEntityList localRDFEntityList1 = classes;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getAllSuperClasses(String class_uri)
    throws Exception
  {
    try
    {
      RDFEntityList classes = new RDFEntityList();
      classes = getAllSuperClassesWithInference(class_uri);
      
      RDFEntityList localRDFEntityList1 = classes;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getDirectSuperClasses(String class_uri)
    throws Exception
  {
    try
    {
      RDFEntityList classes = new RDFEntityList();
      if ((!class_uri.startsWith("node")) && (!class_uri.startsWith("_:node")))
      {
        String sparql = "SELECT DISTINCT ?c ?l  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + class_uri + "> rdfs:subClassOf ?c . " + "OPTIONAL{?c rdfs:label ?l} . " + "FILTER ( isURI(?c) && str(?c) != <" + class_uri + "> )}";
        
        execute(sparql, -1);
        classes = retrieveRDFEntityFrom("class");
      }
      if (classes == null) {
        classes = new RDFEntityList();
      }
      RDFEntityList localRDFEntityList1 = classes;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getAllPropertiesOfClass(String class_uri)
    throws Exception
  {
    RDFEntityList slots = getDirectPropertiesOfClass(class_uri);
    if (slots == null) {
      slots = new RDFEntityList();
    }
    RDFEntityList super_classes = getAllSuperClasses(class_uri);
    if (super_classes != null) {
      for (RDFEntity c : super_classes.getAllRDFEntities())
      {
        RDFEntityList ss = getDirectPropertiesOfClass(c.getURI());
        if (ss != null) {
          slots.addNewRDFEntities(ss);
        }
      }
    }
    return slots;
  }
  
  public RDFEntityList getAllPropertiesOfInstance(String instance_uri)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      if (isOWLRepository())
      {
        String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?rel ?rellab FROM  <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?rel ?value. ?rel rdf:type ?X . " + "OPTIONAL{ ?rel rdfs:label ?rellab} " + ". FILTER( str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> " + "&& (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " + "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" + "}";
        
        execute(sparql, -1);
        slots = retrieveBasicPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String sparql2 = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?rel ?rellab FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?value ?rel <" + instance_uri + "> . ?rel rdf:type ?X . " + "OPTIONAL{ ?rel rdfs:label ?rellab} " + ". FILTER( (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " + "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" + "}";
        
        execute(sparql2, -1);
        slots.addAllRDFEntity(retrieveBasicPropertiesFrom());
      }
      else
      {
        String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?rel ?rellab FROM  <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?rel ?value. ?rel rdf:type ?X . " + "OPTIONAL{ ?rel rdfs:label ?rellab} " + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql, -1);
        slots = retrieveBasicPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?rel ?rellab FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?value ?rel <" + instance_uri + "> . ?rel rdf:type ?X . " + "OPTIONAL{ ?rel rdfs:label ?rellab} " + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql, -1);
        slots.addAllRDFEntity(retrieveBasicPropertiesFrom());
      }
      RDFEntityList slotsNew = new RDFEntityList();
      for (RDFEntity slot : slots.getAllRDFEntities())
      {
        String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?rel ?rellab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + slot.getURI() + "> <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> ?rel . " + "OPTIONAL{ ?rel rdfs:label ?rellab} " + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql, -1);
        slotsNew.addAllRDFEntity(retrieveBasicPropertiesFrom());
      }
      slots.addNewRDFEntities(slotsNew);
      ??? = slots;return (RDFEntityList)???;
    }
    finally {}
  }
  
  public RDFEntityList getDirectPropertiesOfClass(String class1)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      if (isOWLRepository())
      {
        String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab FROM  <" + this.repository.getGraphIRI() + "> WHERE{ " + "?rel rdfs:range ?rango. " + "?rel rdfs:domain <" + class1 + ">. " + "?rel rdf:type ?X . " + "OPTIONAL{ ?rel rdfs:label ?rellab}. " + "OPTIONAL{ <" + class1 + "> rdfs:label ?originlab}. " + "OPTIONAL{ ?rango rdfs:label ?rangolab} " + ". FILTER((str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " + "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql, -1);
        slots = retrieveFullPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String sparql2 = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?rel ?origin, ?rango, ?rellab, ?originlab, ?rangolab FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?rel rdfs:range <" + class1 + ">. ?rel rdfs:domain ?origin. ?rel rdf:type ?X . " + "OPTIONAL{ ?rel rdfs:label ?rellab}. " + "OPTIONAL{ ?origin rdfs:label ?originlab}. " + "OPTIONAL{ <" + class1 + "> rdfs:label ?rangolab} " + ". FILTER((str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " + "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql2, -1);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addNewRDFEntities(slots2);
      }
      else
      {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?rel ?origin, ?rango, ?rellab, ?originlab, ?rangolab FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?rel rdfs:range ?rango. ?rel rdfs:domain <" + class1 + ">. " + "OPTIONAL{ ?rel rdfs:label ?rellab}. " + "OPTIONAL{ <" + class1 + "> rdfs:label ?originlab}. " + "OPTIONAL{ ?rango rdfs:label ?rangolab} " + "NOT_RDF_PROPERTIES}";
        
        execute(sparql, -1);
        slots = retrieveFullPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String sparql2 = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?rel ?origin, ?rango, ?rellab, ?originlab, ?rangolab FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?rel rdfs:range <" + class1 + ">. ?rel rdfs:domain ?origin. " + "OPTIONAL{ ?rel rdfs:label ?rellab}. " + "OPTIONAL{ ?origin rdfs:label ?originlab}. " + "OPTIONAL{ <" + class1 + "> rdfs:label ?rangolab} " + "NOT_RDF_PROPERTIES}";
        
        execute(sparql2, -1);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addNewRDFEntities(slots2);
      }
      String sparql = slots;return sparql;
    }
    finally {}
  }
  
  public RDFEntityList getAllClassesOfInstance(String instance_uri)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> rdf:type ?c. OPTIONAL{ ?c rdfs:label ?l}}";
      
      execute(sparql, -1);
      RDFEntityList list = retrieveRDFEntityFrom("class");
      LinkedList<RDFEntityList> newLists = new LinkedList();
      ArrayList<RDFEntity> toCheckList = list.getAllRDFEntities();
      if (list.size() < 3) {
        for (;;)
        {
          newToCheckList = new ArrayList();
          for (RDFEntity entity : toCheckList) {
            newLists.add(getAllSuperClasses(entity.getURI()));
          }
          for (RDFEntityList newList : newLists) {
            for (RDFEntity entity : newList.getAllRDFEntities()) {
              if (!list.getAllRDFEntities().contains(entity))
              {
                list.getAllRDFEntities().add(entity);
                newToCheckList.add(entity);
              }
            }
          }
          if (newToCheckList.isEmpty()) {
            break;
          }
          toCheckList = newToCheckList;
        }
      }
      ArrayList<RDFEntity> newToCheckList = list;return newToCheckList;
    }
    finally {}
  }
  
  public ArrayList<String> getAllClassesNamesOfInstance(String instance_uri)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?c FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> rdf:type ?c}";
      
      execute(sparql, -1);
      HashSet<String> list = new HashSet();
      list.addAll(retrieveEntitiesUriFrom(false));
      
      LinkedList<RDFEntityList> newLists = new LinkedList();
      HashSet<String> toCheckList = list;
      for (;;)
      {
        HashSet<String> newToCheckList = new HashSet();
        for (String entity : toCheckList) {
          newLists.add(getAllSuperClasses(entity));
        }
        for (RDFEntityList newList : newLists) {
          for (RDFEntity entity : newList.getAllRDFEntities()) {
            if (!list.add(entity.getURI())) {
              newToCheckList.add(entity.getURI());
            }
          }
        }
        if (newToCheckList.isEmpty()) {
          break;
        }
        toCheckList = newToCheckList;
      }
      ArrayList<String> output = new ArrayList(list);
      ??? = output;return (ArrayList<String>)???;
    }
    finally {}
  }
  
  public RDFEntityList getDirectClassOfInstance(String instance_uri)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> rdf:type ?c. OPTIONAL{ ?c rdfs:label ?l}}";
      
      execute(sparql, -1);
      RDFEntityList localRDFEntityList = retrieveRDFEntityFrom("class");return localRDFEntityList;
    }
    finally {}
  }
  
  public RDFEntityList getAllInstances()
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      if (isOWLRepository())
      {
        String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?p ?l ?title FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?c rdf:type owl:Class . ?p rdf:type ?c . " + "OPTIONAL{?p  <http://purl.org/dc/elements/1.1/title> ?title} . " + "OPTIONAL{?p rdfs:label ?l} " + " . FILTER( str(?c) != <http://www.w3.org/2000/01/rdf-schema#Class> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Literal> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Datatype> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Container> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> && str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> && str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> && str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> && str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> && str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> && str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> && str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> && str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> && str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> && str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> && str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> && str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> && str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )" + "}";
        
        execute(sparql, -1);
        aux = retrieveRDFEntityForIndex("instance");
      }
      String sparql = "SELECT DISTINCT ?i ?l ?title FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?i rdf:type ?c . ?c rdf:type rdfs:Class . " + "OPTIONAL{?p  <http://purl.org/dc/elements/1.1/title> ?title} . " + "OPTIONAL{?i rdfs:label ?l} . " + "FILTER (isURI(?i) )" + " . FILTER( str(?c) != <http://www.w3.org/2000/01/rdf-schema#Class> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Literal> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Datatype> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Container> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> && str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> && str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> && str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> && str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> && str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> && str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> && str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> && str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> && str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> && str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> && str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> && str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> && str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )" + ". FILTER( str(?i) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>)" + "}";
      
      execute(sparql, -1);
      aux.addAllRDFEntity(retrieveRDFEntityForIndex("instance"));
      
      RDFEntityList localRDFEntityList1 = aux;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getAllInstancesPeriodically(int offset, int limit)
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      try
      {
        if (isOWLRepository())
        {
          String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?p ?l ?title FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?c rdf:type owl:Class . ?p rdf:type ?c . " + "OPTIONAL{?p  <http://purl.org/dc/elements/1.1/title> ?title} . " + "OPTIONAL{?p rdfs:label ?l} " + " . FILTER( str(?c) != <http://www.w3.org/2000/01/rdf-schema#Class> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Literal> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Datatype> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Container> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> && str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> && str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> && str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> && str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> && str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> && str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> && str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> && str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> && str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> && str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> && str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> && str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> && str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )" + " } LIMIT " + limit + " OFFSET " + offset;
          
          MappingSession.serqlCalls += 1;
          execute(sparql, -1);
          aux = retrieveRDFEntityForIndex("instance");
        }
        String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?i ?l ?title FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?i rdf:type ?c . ?c rdf:type rdfs:Class . " + "OPTIONAL{?p  <http://purl.org/dc/elements/1.1/title> ?title} . " + "OPTIONAL{?i rdfs:label ?l} . " + "FILTER (isURI(?i) )" + " . FILTER( str(?c) != <http://www.w3.org/2000/01/rdf-schema#Class> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Literal> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Datatype> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Container> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> && str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> && str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> && str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> && str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> && str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> && str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> && str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> && str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> && str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> && str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> && str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> && str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> && str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )" + ". FILTER( str(?i) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>)" + "} LIMIT " + limit + " OFFSET " + offset;
        
        MappingSession.serqlCalls += 1;
        execute(sparql, -1);
        aux.addAllRDFEntity(retrieveRDFEntityForIndex("instance"));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      e = aux;return e;
    }
    finally {}
  }
  
  public RDFEntityList getAllInstancesOfClass(String class_uri, int limit)
    throws Exception
  {
    try
    {
      String sparql;
      String sparql;
      if ((limit == -1) || (limit == 0)) {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?i ?l FROM <" + this.repository.getGraphIRI() + "> WHERE{ ?i rdf:type <" + class_uri + "> . OPTIONAL{?i rdfs:label ?l}. FILTER( isURI(?i))}";
      } else {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?i ?l FROM <" + this.repository.getGraphIRI() + "> WHERE{ ?i rdf:type <" + class_uri + "> . OPTIONAL{?i rdfs:label ?l}. FILTER( isURI(?i))} LIMIT " + limit;
      }
      execute(sparql, -1);
      RDFEntityList localRDFEntityList = retrieveRDFEntityFrom("instance");return localRDFEntityList;
    }
    finally {}
  }
  
  public boolean isInstanceOf(String instance_uri, String class_uri)
    throws Exception
  {
    try
    {
      String sparql = "ASK FROM <" + this.repository.getGraphIRI() + "> WHERE { <" + instance_uri + "> rdf:type <" + class_uri + ">. FILTER( isURI(?i))}";
      
      execute(sparql, -1);
      if ((this.result == null) || (!this.result.next()))
      {
        bool = false;return bool;
      }
      boolean bool = true;return bool;
    }
    finally {}
  }
  
  public boolean isNameOfInstance(String instance_uri, String literal)
    throws Exception
  {
    try
    {
      String sparql;
      String sparql;
      if (literal.contains("\"")) {
        sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?property  FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?property " + literal + " . ?property rdfs:label \"name\". }";
      } else {
        sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?property  FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?property \"" + literal + "\" . ?property rdfs:label \"name\".}";
      }
      execute(sparql, -1);
      if ((this.result == null) || (!this.result.next()))
      {
        bool = false;return bool;
      }
      boolean bool = true;return bool;
    }
    finally {}
  }
  
  public RDFEntityList getSlotValue(String instance_uri, String property_uri)
    throws Exception
  {
    try
    {
      boolean limitsize = false;
      RDFEntityList results = new RDFEntityList();
      String sparql = "SELECT DISTINCT ?v FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> <" + property_uri + "> ?v . " + "FILTER(isLiteral(?v))}";
      
      execute(sparql, -1);
      results = retrieveRDFEntityLiteralsFrom(instance_uri, limitsize);
      if (!results.isEmpty())
      {
        localRDFEntityList1 = results;return localRDFEntityList1;
      }
      sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?v ?lv FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> <" + property_uri + "> ?v .  OPTIONAL{ ?v rdfs:label ?lv}}";
      
      execute(sparql, -1);
      results = retrieveRDFEntityFrom("instance");
      
      RDFEntityList localRDFEntityList1 = results;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getInstancesWithSlotValue(String property_uri, String slot_value, boolean isValueLiteral)
    throws Exception
  {
    try
    {
      String sparql = "";
      if (isValueLiteral)
      {
        sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?i ?lb FROM <" + this.repository.getGraphIRI() + "> WHERE{ ?i <" + property_uri + "> \"" + slot_value + "\".  OPTIONAL{ ?i rdfs:label ?lb}}";
        
        execute(sparql, -1);
        localRDFEntityList = retrieveRDFEntityFrom("instance");return localRDFEntityList;
      }
      sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?i ?lb FROM <" + this.repository.getGraphIRI() + "> WHERE{ ?i <" + property_uri + "> <" + slot_value + "> .  OPTIONAL{ ?i rdfs:label ?lb}}";
      
      execute(sparql, -1);
      RDFEntityList localRDFEntityList = retrieveRDFEntityFrom("instance");return localRDFEntityList;
    }
    finally {}
  }
  
  public RDFEntityList getLiteralValuesOfInstance(String instance_uri)
    throws Exception
  {
    try
    {
      String sparql = "";
      sparql = "SELECT DISTINCT ?v FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?property ?v . " + "FILTER(isLiteral(?v))}";
      
      execute(sparql, -1);
      boolean limitsize = true;
      RDFEntityList localRDFEntityList = retrieveRDFEntityLiteralsFrom(instance_uri, limitsize);return localRDFEntityList;
    }
    finally {}
  }
  
  public OcmlClass getClassInfo(String class_uri)
    throws Exception
  {
    RDFEntity entityClass = new RDFEntity("class", class_uri, getLabelOfEntity(class_uri), getPluginID());
    
    OcmlClass c = new OcmlClass(entityClass);
    
    c.setDirectSuperClasses(getDirectSuperClasses(class_uri));
    c.setDirectSubclasses(getDirectSubClasses(class_uri));
    c.setSuperClasses(getAllSuperClasses(class_uri));
    c.setSubClasses(getAllSubClasses(class_uri));
    c.setEquivalentClasses(getEquivalentEntitiesForClass(class_uri));
    c.setProperties(getAllPropertiesOfClass(class_uri));
    c.setDirectProperties(getDirectPropertiesOfClass(class_uri));
    
    return c;
  }
  
  public boolean isURIExisted(String uri)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ASK FROM <" + this.repository.getGraphIRI() + "> WHERE  {" + "<" + uri + "> rdf:type ?o}";
      
      execute(sparql, -1);
      if ((this.result == null) || (!this.result.next()))
      {
        bool = false;return bool;
      }
      boolean bool = true;return bool;
    }
    finally {}
  }
  
  public OcmlInstance getInstanceInfo(String instance_uri)
    throws Exception
  {
    try
    {
      RDFEntity entityInstance = new RDFEntity("instance", instance_uri, getLabelOfEntity(instance_uri), getPluginID());
      
      OcmlInstance ocmlInstance = new OcmlInstance(entityInstance);
      
      RDFEntityList directClasses = getDirectClassOfInstance(instance_uri);
      
      ocmlInstance.addDirectSuperClasses(directClasses);
      if (!ocmlInstance.getDirectSuperClasses().isEmpty()) {
        for (RDFEntity directClass : directClasses.getAllRDFEntities()) {
          ocmlInstance.addSuperClasses(getAllSuperClasses(directClass.getURI()));
        }
      }
      ocmlInstance.setEquivalentInstances(getEquivalentEntitiesForInstance(instance_uri));
      
      Hashtable<RDFEntity, RDFEntityList> propertiesTable = new Hashtable();
      
      String sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?p ?v ?pl ?vl FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?p ?v .  OPTIONAL{ ?p rdfs:label ?pl}. OPTIONAL{ ?v rdfs:label ?vl}. " + "FILTER(str(?p) != <" + "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "type>)}";
      
      execute(sparql, -1);
      if ((this.result == null) || (!this.result.next()))
      {
        OcmlInstance localOcmlInstance1 = ocmlInstance;return localOcmlInstance1;
      }
      while (this.result.next())
      {
        property = this.result.getString(1);
        String value = this.result.getString(2);
        
        String v_propertyLabel = this.result.getString(3);
        String propertyLabel = v_propertyLabel == null ? null : v_propertyLabel.toString().trim();
        
        String v_valueLabel = this.result.getString(4);
        String valueLabel = v_valueLabel == null ? null : v_valueLabel.toString().trim();
        
        RDFEntity entityProperty = new RDFEntity("property", (String)property, propertyLabel, getPluginID());
        RDFEntity entityValue;
        RDFEntity entityValue;
        if ((value.indexOf("#") > -1) || (isURIString(value)))
        {
          String typeV = "instance";
          entityValue = new RDFEntity(typeV, value, valueLabel, getPluginID());
        }
        else
        {
          String typeV = "literal";
          entityValue = new RDFEntity(typeV, instance_uri, value, getPluginID());
        }
        if (propertiesTable.containsKey(entityProperty))
        {
          ((RDFEntityList)propertiesTable.get(entityProperty)).addRDFEntity(entityValue);
        }
        else
        {
          RDFEntityList list = new RDFEntityList();
          list.addRDFEntity(entityValue);
          propertiesTable.put(entityProperty, list);
        }
      }
      ocmlInstance.setProperties(propertiesTable);
      Object property = ocmlInstance;return (OcmlInstance)property;
    }
    finally {}
  }
  
  public OcmlProperty getPropertyInfo(String property_uri)
    throws Exception
  {
    RDFEntity entityClass = new RDFEntity("property", property_uri, getLabelOfEntity(property_uri), getPluginID());
    
    OcmlProperty ocmlProperty = new OcmlProperty(entityClass);
    
    ocmlProperty.setEquivalentProperties(getEquivalentEntitiesForClass(property_uri));
    
    ocmlProperty.setDirectSuperProperties(getDirectSuperProperties(property_uri));
    ocmlProperty.setSuperProperties(getAllSuperProperties(property_uri));
    ocmlProperty.setDirectSubProperties(getDirectSubProperties(property_uri));
    ocmlProperty.setSubProperties(getAllSubProperties(property_uri));
    ocmlProperty.setEquivalentProperties(getEquivalentEntitiesForProperties(property_uri));
    
    RDFEntityList list = new RDFEntityList();
    list.addAllRDFEntity(getDomainOfProperty(property_uri));
    ocmlProperty.setDomain(list);
    list = new RDFEntityList();
    list.addAllRDFEntity(getRangeOfProperty(property_uri));
    ocmlProperty.setRange(list);
    
    return ocmlProperty;
  }
  
  private RDFEntityList getEquivalentEntitiesForProperties(String propertyUri)
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      
      String sparql = "SELECT DISTINCT ?x ?xl  FROM  <" + this.repository.getGraphIRI() + "> WHERE { " + "{?x owl:equivalentProperty <" + propertyUri + ">} " + "UNION" + "{<" + propertyUri + "> owl:equivalentProperty ?x}. " + "OPTIONAL{?x rdfs:label ?xl}}";
      
      execute(sparql, -1);
      aux = retrieveRDFEntityFrom("instance");
      
      RDFEntityList localRDFEntityList1 = aux;return localRDFEntityList1;
    }
    finally {}
  }
  
  private RDFEntityList getAllSubProperties(String propertyUri)
    throws Exception
  {
    try
    {
      RDFEntityList properties = new RDFEntityList();
      
      properties = getAllSubPropertiesWithInference(propertyUri);
      
      RDFEntityList localRDFEntityList1 = properties;return localRDFEntityList1;
    }
    finally {}
  }
  
  private RDFEntityList getAllSubPropertiesWithInference(String propertyUri)
    throws Exception
  {
    try
    {
      RDFEntityList properties = new RDFEntityList();
      String sparql;
      String sparql;
      if (propertyUri.startsWith("node")) {
        sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?p ?l  FROM  <" + this.repository.getGraphIRI() + ">" + "WHERE {" + "{SELECT DISTINCT ?p ?path ?route ?jump " + "WHERE{" + "{SELECT ?p ?Property WHERE { ?p rdfs:subPropertyOf ?Property}" + "} " + "OPTION ( TRANSITIVE, T_DISTINCT, t_in(?p), t_out(?Property), t_step('path_id') as ?path, t_step(?p) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " + "FILTER ( ?Property = \"" + propertyUri + "\" )}" + "}. OPTIONAL{ ?p rdfs:label ?l}" + "}";
      } else {
        sparql = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT ?p ?l  FROM  <" + this.repository.getGraphIRI() + ">" + "WHERE {" + "{SELECT DISTINCT ?p ?path ?route ?jump " + "WHERE{" + "{SELECT ?p ?Property WHERE { ?p rdfs:subPropertyOf ?Property}" + "} " + "OPTION ( TRANSITIVE, T_DISTINCT, t_in(?p), t_out(?Property), t_step('path_id') as ?path, t_step(?p) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " + "FILTER ( ?Property = <" + propertyUri + "> )}" + "}. OPTIONAL{ ?p rdfs:label ?l}" + "}";
      }
      execute(sparql, -1);
      properties = retrieveRDFEntityFrom("property");
      if (properties == null) {
        properties = new RDFEntityList();
      }
      RDFEntityList localRDFEntityList1 = properties;return localRDFEntityList1;
    }
    finally {}
  }
  
  private RDFEntityList getDirectSubProperties(String propertyUri)
    throws Exception
  {
    try
    {
      String sparql = "SELECT DISTINCT ?p ?l  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?p rdfs:subPropertyOf <" + propertyUri + "> . " + "OPTIONAL{?p rdfs:label ?l} . " + "FILTER ( (isURI(?p) && str(?p) != <" + propertyUri + "> )) . }";
      
      execute(sparql, -1);
      RDFEntityList properties = retrieveRDFEntityFrom("property");
      if (properties == null) {
        properties = new RDFEntityList();
      }
      if (isOWLRepository())
      {
        p2 = getSubPropertyFromIntersectionDefinition(propertyUri);
        if (properties == null)
        {
          RDFEntityList localRDFEntityList1 = p2;return localRDFEntityList1;
        }
        properties.addNewRDFEntities(p2);
      }
      RDFEntityList p2 = properties;return p2;
    }
    finally {}
  }
  
  private RDFEntityList getSubPropertyFromIntersectionDefinition(String propertyUri)
    throws Exception
  {
    try
    {
      String sparql = "SELECT ?p ?l  FROM  <" + this.repository.getGraphIRI() + "> WHERE { " + "OPTIONAL{?p owl:intersectionOf ?x} . " + "OPTIONAL{?p rdfs:label ?l} . ?x ?p <" + propertyUri + ">}";
      
      execute(sparql, -1);
      RDFEntityList localRDFEntityList = retrieveRDFEntityFrom("property");return localRDFEntityList;
    }
    finally {}
  }
  
  private RDFEntityList getAllSuperProperties(String propertyUri)
    throws Exception
  {
    try
    {
      RDFEntityList properties = new RDFEntityList();
      
      properties = getAllSuperPropertiesWithInference(propertyUri);
      
      RDFEntityList localRDFEntityList1 = properties;return localRDFEntityList1;
    }
    finally {}
  }
  
  private RDFEntityList getAllSuperPropertiesWithInference(String propertyUri)
    throws Exception
  {
    try
    {
      String sparql;
      String sparql;
      if ((propertyUri.startsWith("node")) || (propertyUri.startsWith("_:node"))) {
        sparql = "SELECT DISTINCT ?p ?l  FROM  <" + this.repository.getGraphIRI() + ">" + "WHERE {" + "{SELECT DISTINCT ?p ?path ?route ?jump " + "WHERE{" + "{SELECT ?p ?Property WHERE { ?Property rdfs:subPropertyOf ?p}" + "} " + "OPTION ( TRANSITIVE, T_DISTINCT, t_in(?p), t_out(?Property), t_step('path_id') as ?path, t_step(?p) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " + "FILTER ( ?Property = \"" + propertyUri + "\" )}" + "}. OPTIONAL{ ?p rdfs:label ?l}" + "}";
      } else {
        sparql = "SELECT DISTINCT ?p ?l  FROM  <" + this.repository.getGraphIRI() + ">" + "WHERE {" + "{SELECT DISTINCT ?p ?path ?route ?jump " + "WHERE{" + "{SELECT ?p ?Property WHERE { ?Property rdfs:subPropertyOf ?p}" + "} " + "OPTION ( TRANSITIVE, T_DISTINCT, t_in(?p), t_out(?Property), t_step('path_id') as ?path, t_step(?p) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " + "FILTER ( ?Property = <" + propertyUri + "> )}" + "}. OPTIONAL{ ?p rdfs:label ?l}" + "}";
      }
      execute(sparql, -1);
      RDFEntityList properties = retrieveRDFEntityFrom("property");
      if (properties == null) {
        properties = new RDFEntityList();
      }
      RDFEntityList localRDFEntityList1 = properties;return localRDFEntityList1;
    }
    finally {}
  }
  
  private RDFEntityList getDirectSuperProperties(String propertyUri)
    throws Exception
  {
    try
    {
      RDFEntityList properties = new RDFEntityList();
      if ((!propertyUri.startsWith("node")) && (!propertyUri.startsWith("_:node")))
      {
        String sparql = "SELECT DISTINCT ?p ?l  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + propertyUri + "> rdfs:subPropertyOf ?p . " + "OPTIONAL{?p rdfs:label ?l} . " + "FILTER ( (isURI(?p) && str(?p) != <" + propertyUri + "> ))}";
        
        execute(sparql, -1);
        properties = retrieveRDFEntityFrom("property");
      }
      if (properties == null) {
        properties = new RDFEntityList();
      }
      RDFEntityList localRDFEntityList1 = properties;return localRDFEntityList1;
    }
    finally {}
  }
  
  public boolean isSubClassOf(String class1, String class2)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> ASK FROM <" + this.repository.getGraphIRI() + "> WHERE { <" + class1 + "> rdfs:subClassOf <" + class2 + ">}";
      
      execute(sparql, -1);
      if ((this.result == null) || (!this.result.next()))
      {
        bool = false;return bool;
      }
      boolean bool = true;return bool;
    }
    finally {}
  }
  
  public RDFEntityList getGenericInstances(String class_uri, String slot, String value_uri)
    throws Exception
  {
    try
    {
      String sparql;
      String sparql;
      if ((class_uri.startsWith("node")) || (class_uri.startsWith("_:node"))) {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?instances ?i_label  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?instances rdf:type \"" + class_uri + "\". ?instances <" + slot + "> <" + value_uri + ">. " + "OPTIONAL{ ?instances rdfs:label ?i_label }. " + "}";
      } else {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?instances ?i_label  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?instances rdf:type <" + class_uri + ">. ?instances <" + slot + "> <" + value_uri + ">. " + "OPTIONAL{ ?instances rdfs:label ?i_label }}";
      }
      execute(sparql, -1);
      RDFEntityList lista1 = retrieveRDFValueFrom("instance");
      if ((class_uri.startsWith("node")) || (class_uri.startsWith("_:node"))) {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?instances ?i_label  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?instances rdf:type \"" + class_uri + "\". <" + value_uri + "> <" + slot + "> ?instances. " + "OPTIONAL{ ?instances rdfs:label ?i_label }. " + "}";
      } else {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?instances ?i_label  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?instances rdf:type <" + class_uri + ">. <" + value_uri + "> <" + slot + "> ?instances. " + "OPTIONAL{ ?instances rdfs:label ?i_label }}";
      }
      execute(sparql, -1);
      RDFEntityList lista2 = retrieveRDFValueFrom("instance");
      lista1.addAllRDFEntity(lista2);
      RDFEntityList localRDFEntityList1 = lista1;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getGenericInstancesForLiteral(String class_uri, String slot, String value)
    throws Exception
  {
    String sparql = "";
    try
    {
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?instances ?i_label   FROM  <" + this.repository.getGraphIRI() + "> " + "WHERE{ ?instances a <" + class_uri + ">. ?instances <" + slot + "> ?o. " + "OPTIONAL{ ?instances rdfs:label ?i_label }. FILTER(str(?o)=\"" + value + "\")}";
      
      execute(sparql, 4);
      RDFEntityList localRDFEntityList = retrieveRDFValueFrom("instance");return localRDFEntityList;
    }
    catch (Exception e)
    {
      e = e;
      System.out.println("getGenericInstancesForLiteral " + sparql);
      e.printStackTrace();
    }
    finally {}
    return new RDFEntityList();
  }
  
  public RDFEntityList getTripleInstances(String query_class, String slot, String class2)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst1 ?inst1label ?inst2 ?inst2label  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst1 rdf:type <" + query_class + ">.  ?inst2 rdf:type <" + class2 + ">. ?inst1 <" + slot + "> ?inst2. " + "OPTIONAL{ ?inst1 rdfs:label ?inst1label }. " + "OPTIONAL{ ?inst2 rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      if (this.result != null)
      {
        aux = retrieveRDFTripleValueFrom("instance");
        if (!aux.isEmpty())
        {
          RDFEntityList localRDFEntityList1 = aux;return localRDFEntityList1;
        }
      }
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst1 ?inst1label ?inst2 ?inst2label  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst1 rdf:type <" + query_class + ">.  ?inst2 rdf:type <" + class2 + ">. ?inst2 <" + slot + "> ?inst1. " + "OPTIONAL{ ?inst1 rdfs:label ?inst1label }. " + "OPTIONAL{ ?inst2 rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      RDFEntityList aux = retrieveRDFTripleValueFrom("instance");return aux;
    }
    finally {}
  }
  
  public RDFEntityList getTripleInstances(String query_class, String prop1, String ref_class, String prop2, String instance)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " + "?inst <" + prop1 + "> ?ref_inst. ?ref_inst <" + prop2 + "> <" + instance + ">. " + "OPTIONAL{ ?inst rdfs:label ?inst1label }. " + "OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      RDFEntityList localRDFEntityList1;
      if (this.result != null)
      {
        RDFEntityList aux = retrieveRDFTripleValueFrom("instance");
        if (!aux.isEmpty())
        {
          localRDFEntityList1 = aux;return localRDFEntityList1;
        }
      }
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " + "?ref_inst <" + prop1 + "> ?inst. ?ref_inst <" + prop2 + "> <" + instance + ">. " + "OPTIONAL{ ?inst rdfs:label ?inst1label }. " + "OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      if (this.result != null)
      {
        RDFEntityList aux = retrieveRDFTripleValueFrom("instance");
        if (!aux.isEmpty())
        {
          localRDFEntityList1 = aux;return localRDFEntityList1;
        }
      }
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " + "?ref_inst <" + prop1 + "> ?inst. <" + instance + "> <" + prop2 + "> ?ref_inst." + "OPTIONAL{ ?inst rdfs:label ?inst1label }. " + "OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      if (this.result != null)
      {
        RDFEntityList aux = retrieveRDFTripleValueFrom("instance");
        if (!aux.isEmpty())
        {
          localRDFEntityList1 = aux;return localRDFEntityList1;
        }
      }
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " + "?inst <" + prop1 + "> ?ref_inst. <" + instance + "> <" + prop2 + "> ?ref_inst." + "OPTIONAL{ ?inst rdfs:label ?inst1label }. " + "OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      if (this.result != null)
      {
        aux = retrieveRDFTripleValueFrom("instance");
        if (!aux.isEmpty())
        {
          localRDFEntityList1 = aux;return localRDFEntityList1;
        }
      }
      RDFEntityList aux = retrieveRDFTripleValueFrom("instance");return aux;
    }
    finally {}
  }
  
  public RDFEntityList getTripleInstancesFromLiteral(String query_class, String prop1, String ref_class, String prop2, String instance)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " + "?inst <" + prop1 + "> ?ref_inst. ?ref_inst <" + prop2 + "> \"" + instance + "\". " + "OPTIONAL{ ?inst rdfs:label ?inst1label }. " + "OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      RDFEntityList list = retrieveRDFTripleValueFrom("instance");
      if (list.isEmpty())
      {
        sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label  FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " + "?ref_inst <" + prop1 + "> ?inst. ?ref_inst <" + prop2 + "> \"" + instance + "\". " + "OPTIONAL{ ?inst rdfs:label ?inst1label }. " + "OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";
        
        execute(sparql, -1);
      }
      else
      {
        localRDFEntityList1 = list;return localRDFEntityList1;
      }
      RDFEntityList localRDFEntityList1 = retrieveRDFTripleValueFrom("instance");return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getTripleInstancesFromClasses(String query_class, String prop1, String ref_class, String prop2, String class2)
    throws Exception
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. ?instance rdf:type <" + class2 + ">." + "?inst <" + prop1 + "> ?ref_inst. ?ref_inst <" + prop2 + "> ?instance." + "OPTIONAL{ ?inst rdfs:label ?inst1label }. " + "OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      RDFEntityList localRDFEntityList1;
      if (this.result != null)
      {
        RDFEntityList aux = retrieveRDFTripleValueFrom("instance");
        if (!aux.isEmpty())
        {
          localRDFEntityList1 = aux;return localRDFEntityList1;
        }
      }
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. ?instance rdf:type <" + class2 + ">." + "?ref_inst <" + prop1 + "> ?inst. ?ref_inst <" + prop2 + "> ?instance." + "OPTIONAL{ ?inst rdfs:label ?inst1label }. " + "OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      if (this.result != null)
      {
        RDFEntityList aux = retrieveRDFTripleValueFrom("instance");
        if (!aux.isEmpty())
        {
          localRDFEntityList1 = aux;return localRDFEntityList1;
        }
      }
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. ?instance rdf:type <" + class2 + ">." + "?ref_inst <" + prop1 + "> ?inst. ?instance <" + prop2 + "> ?ref_inst." + "OPTIONAL{ ?inst rdfs:label ?inst1label }. " + "OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      if (this.result != null)
      {
        RDFEntityList aux = retrieveRDFTripleValueFrom("instance");
        if (!aux.isEmpty())
        {
          localRDFEntityList1 = aux;return localRDFEntityList1;
        }
      }
      sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. ?instance rdf:type <" + class2 + ">." + "?inst <" + prop1 + "> ?ref_inst. ?instance <" + prop2 + "> ?ref_inst." + "OPTIONAL{ ?inst rdfs:label ?inst1label }. " + "OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";
      
      execute(sparql, -1);
      if (this.result != null)
      {
        aux = retrieveRDFTripleValueFrom("instance");
        if (!aux.isEmpty())
        {
          localRDFEntityList1 = aux;return localRDFEntityList1;
        }
      }
      RDFEntityList aux = null;return aux;
    }
    finally {}
  }
  
  public RDFEntityList getKBPropertiesBetweenClasses(String class1_uri, String class2_uri)
    throws Exception
  {
    try
    {
      RDFEntityList props = getPropertiesBetweenClasses(class1_uri, class2_uri);
      RDFEntityList slots = new RDFEntityList();
      for (RDFEntity Entprop : props.getAllRDFEntities())
      {
        RDFProperty prop = (RDFProperty)Entprop;
        System.out.println("Analyzing prop. " + prop.getURI());
        
        String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix owl: <http://www.w3.org/2002/07/owl#> ASK FROM <" + this.repository.getGraphIRI() + "> " + " WHERE { " + "{?instorigin rdf:type <" + class1_uri + ">. ?insttarget rdf:type <" + class2_uri + ">}" + " UNION " + "{?instorigin rdf:type <" + class2_uri + ">. ?insttarget rdf:type <" + class1_uri + ">}. " + "?instorigin <" + prop.getURI() + "> ?insttarget} ";
        
        execute(sparql, -1);
        if ((this.result != null) && (this.result.next())) {
          slots.addRDFEntity(prop);
        }
      }
      ??? = slots;return (RDFEntityList)???;
    }
    finally {}
  }
  
  public RDFEntityList getPropertiesBetweenClasses(String class1_uri, String class2_uri)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      if (isOWLRepository())
      {
        String sparql = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?rel rdfs:range ?rango . ?rel rdfs:domain ?origin . ?rel rdf:type ?X . " + "OPTIONAL{?term1 rdfs:subClassOf ?origin} . " + "OPTIONAL{?term2 rdfs:subClassOf ?rango} . " + "OPTIONAL{?rel rdfs:label ?rellab} . " + "OPTIONAL{?origin rdfs:label ?originlab} . " + "OPTIONAL{?rango rdfs:label ?rangolab} . " + "FILTER ((str(?term2) = <" + class1_uri + "> || str(?rango) = <" + class1_uri + ">) && " + "(str(?term1) = <" + class2_uri + "> || str(?origin) = <" + class2_uri + ">) && " + "(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql, -1);
        slots = retrieveFullPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String sparql2 = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?rel rdfs:range ?rango . ?rel rdfs:domain ?origin . ?rel rdf:type ?X . " + "OPTIONAL{?term1 rdfs:subClassOf ?origin} . " + "OPTIONAL{?term2 rdfs:subClassOf ?rango} . " + "OPTIONAL{?rel rdfs:label ?rellab} . " + "OPTIONAL{?origin rdfs:label ?originlab} . " + "OPTIONAL{?rango rdfs:label ?rangolab} . " + "FILTER ((str(?term2) = <" + class2_uri + "> || str(?rango) = <" + class2_uri + ">) && " + "(str(?term1) = <" + class1_uri + "> || str(?origin) = <" + class1_uri + ">) && " + "(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql2, -1);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
      }
      else
      {
        sparql = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?rel rdfs:range ?rango . ?rel rdfs:domain ?origin . <" + class2_uri + "> rdfs:subClassOf ?origin . <" + class1_uri + "> rdfs:subClassOf ?rango . " + "OPTIONAL{?rel rdfs:label ?rellab} . " + "OPTIONAL{?origin rdfs:label ?originlab} . " + "OPTIONAL{?rango rdfs:label ?rangolab}" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql, -1);
        slots = retrieveFullPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String sparql2 = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?rel rdfs:range ?rango . ?rel rdfs:domain ?origin . <" + class1_uri + "> rdfs:subClassOf ?origin . <" + class2_uri + "> rdfs:subClassOf ?rango . " + "OPTIONAL{?rel rdfs:label ?rellab} . " + "OPTIONAL{?origin rdfs:label ?originlab} . " + "OPTIONAL{?rango rdfs:label ?rangolab}" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql2, -1);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
      }
      String sparql = slots;return sparql;
    }
    finally {}
  }
  
  public RDFEntityList getPropertiesForGenericClass(String class_uri)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      if (isOWLRepository())
      {
        RDFEntityList slots2 = null;
        
        String sparql = "SELECT DISTINCT ?rel ?domain ?rango ?rellab ?originlab ?rangolab  FROM  <" + this.repository.getGraphIRI() + ">" + "WHERE{" + "{SELECT ?origin ?genericterm ?path ?route ?jump " + "WHERE{" + "{SELECT ?origin ?genericterm WHERE { <" + class_uri + ">  rdfs:subClassOf ?origin} }" + "OPTION ( TRANSITIVE, T_DISTINCT, t_in(?origin), t_out(?genericterm), t_step('path_id') as ?path, t_step(?origin) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ) \t" + "FILTER ( ?genericterm = <" + class_uri + "> )" + "}" + "}." + "?rel rdfs:domain ?domain." + "?rel rdfs:range ?rango." + "?rel rdf:type ?X. " + "OPTIONAL{?rel rdfs:label ?rellab} . " + "OPTIONAL{?origin rdfs:label ?originlab} . " + "OPTIONAL{?rango rdfs:label ?rangolab}. " + "FILTER(" + "(?domain = ?origin || ?rango = ?origin) && " + "(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || ?X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + ")" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql, -1);
        slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
        
        sparql = "SELECT DISTINCT ?rel ?domain ?rango ?rellab ?originlab ?rangolab  FROM  <" + this.repository.getGraphIRI() + ">" + "WHERE{" + "{SELECT ?origin ?genericterm ?path ?route ?jump " + "WHERE{" + "{SELECT ?origin ?genericterm WHERE { ?origin rdfs:subClassOf ?genericterm} }" + "OPTION ( TRANSITIVE, T_DISTINCT, t_in(?origin), t_out(?genericterm), t_step('path_id') as ?path, t_step(?origin) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ) \t" + "FILTER ( ?genericterm = <" + class_uri + "> )" + "}" + "}." + "?rel rdfs:domain ?domain." + "?rel rdfs:range ?rango." + "?rel rdf:type ?X. " + "OPTIONAL{?rel rdfs:label ?rellab} . " + "OPTIONAL{?domain rdfs:label ?originlab} . " + "OPTIONAL{?rango rdfs:label ?rangolab}." + "FILTER(" + "(?domain = ?origin || ?rango = ?origin) && " + "(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || ?X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + ")" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql, -1);
        slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
        
        sparql = "SELECT DISTINCT ?rel ?domain ?rango ?rellab ?originlab ?rangolab  FROM  <" + this.repository.getGraphIRI() + ">" + "WHERE{" + "?rel rdfs:domain ?domain." + "?rel rdfs:range ?rango." + "?rel rdf:type ?X. " + "OPTIONAL{?rel rdfs:label ?rellab} . " + "OPTIONAL{?domain rdfs:label ?originlab} . " + "OPTIONAL{?rango rdfs:label ?rangolab}." + "FILTER (( str(?domain) = <" + class_uri + "> || str(?rango) = <" + class_uri + ">) && " + "(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || ?X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + ") " + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql, -1);
        slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
      }
      else
      {
        String sparql = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab FROM  <" + this.repository.getGraphIRI() + "> " + "WHERE {" + " ?rel rdfs:domain ?origin . ?rel rdfs:range ?rango . " + "{?origin rdfs:subClassOf <" + class_uri + ">}      UNION       {<" + class_uri + "> rdfs:subClassOf ?origin}." + " OPTIONAL{?rel rdfs:label ?rellab} . " + " OPTIONAL{?origin rdfs:label ?originlab} . " + " OPTIONAL{?rango rdfs:label ?rangolab}" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql, -1);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
        
        sparql2 = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?rel rdfs:domain ?origin . ?rel rdfs:range ?rango . " + "{?rango rdfs:subClassOf <" + class_uri + "> }      UNION     { <" + class_uri + "> rdfs:subClassOf ?rango }. " + "OPTIONAL{?rel rdfs:label ?rellab} . " + "OPTIONAL{?origin rdfs:label ?originlab} . " + "OPTIONAL{?rango rdfs:label ?rangolab}" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
        
        execute(sparql2, -1);
        RDFEntityList slots3 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots3);
      }
      String sparql = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + class_uri + "> ?rel ?instance . " + "OPTIONAL{?rel rdfs:label ?rellab} . ?rel rdfs:domain ?origin . " + "OPTIONAL{?origin rdfs:label ?originlab} . ?rel rdfs:range ?rango . " + "OPTIONAL{?rango rdfs:label ?rangolab}" + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
      
      execute(sparql, -1);
      RDFEntityList slots4 = retrieveFullPropertiesFrom();
      slots.addAllRDFEntity(slots4);
      String sparql2 = slots;return sparql2;
    }
    finally {}
  }
  
  public RDFEntityList getAllPropertiesBetweenClass_Literal(String classURI, String slot_value)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      if ((!slot_value.startsWith("'")) && (slot_value.contains("'"))) {
        slot_value = slot_value.replaceAll("'", "\\\\'");
      }
      String sparql;
      String sparql;
      if (Character.isDigit(slot_value.charAt(0)))
      {
        sparql = "SELECT DISTINCT ?p ?rellab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?p ?o .?p a ?X. ?instance a <" + classURI + "> . " + "OPTIONAL{?p rdfs:label ?rellab} . " + "FILTER (isLiteral(?o)). FILTER (bif:contains(?o, '\"" + slot_value + "\"') && ( lang(?rellab) = \"en\" || lang(?rellab) = \"\" ) ) . " + "FILTER (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " + "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> )}";
      }
      else
      {
        String sparql;
        if (slot_value.contains("\""))
        {
          sparql = "SELECT DISTINCT ?p ?rellab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?p ?o . ?p a ?X. ?instance a <" + classURI + "> . " + "OPTIONAL{?p rdfs:label ?rellab} . " + "FILTER (str(?o)=" + slot_value + " && ( lang(?rellab) = \"en\" || lang(?rellab) = \"\" ) ) . }" + "FILTER (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " + "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> )}";
        }
        else
        {
          String sparql;
          if (slot_value.contains(" ")) {
            sparql = "SELECT DISTINCT ?p ?rellab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?p ?o .?p a ?X. ?instance a <" + classURI + "> . " + "OPTIONAL{?p rdfs:label ?rellab} . " + "FILTER (isLiteral(?o)). FILTER (bif:contains(?o, '\"" + slot_value + "\"') && ( lang(?rellab) = \"en\" || lang(?rellab) = \"\" ) && (str(?o)=\"" + slot_value + "\")) . " + "FILTER (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " + "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> )}";
          } else {
            sparql = "SELECT DISTINCT ?p ?rellab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?p ?o . ?p a ?X. ?instance a <" + classURI + "> . " + "OPTIONAL{?p rdfs:label ?rellab} . " + "FILTER (isLiteral(?o)). FILTER (bif:contains(?o, \"" + slot_value + "\") && ( lang(?rellab) = \"en\" || lang(?rellab) = \"\" )  && (str(?o)=\"" + slot_value + "\")) .  " + "FILTER (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " + "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> )}";
          }
        }
      }
      execute(sparql, -1);
      try
      {
        slots = retrieveRDFEntityFrom("property");
      }
      catch (Exception e)
      {
        System.out.println("GetAllPropertiesBetweenClass_Literal " + sparql);
        e.printStackTrace();
      }
      e = slots;return e;
    }
    finally {}
  }
  
  public RDFEntityList getKBPropertiesForGenericClass(String classGeneric_uri, String instance2_uri)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      String sparql;
      String sparql;
      if (classGeneric_uri.startsWith("node")) {
        sparql = "SELECT DISTINCT ?rel ?rellab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instorigin rdf:type \"" + classGeneric_uri + "\" . ?instorigin ?rel <" + instance2_uri + "> . " + "OPTIONAL{?rel rdfs:label ?rellab} . " + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
      } else {
        sparql = "SELECT DISTINCT ?rel ?rellab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instorigin rdf:type <" + classGeneric_uri + "> . ?instorigin ?rel <" + instance2_uri + "> . " + "OPTIONAL{?rel rdfs:label ?rellab}} ";
      }
      execute(sparql, -1);
      slots = retrieveBasicPropertiesFrom();
      if (classGeneric_uri.startsWith("node")) {
        sparql = "SELECT DISTINCT ?rel ?rellab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instorigin rdf:type \"" + classGeneric_uri + "\" . <" + instance2_uri + "> ?rel ?instorigin . " + "OPTIONAL{?rel rdfs:label ?rellab} . " + ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)" + "}";
      } else {
        sparql = "SELECT DISTINCT ?rel ?rellab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instorigin rdf:type <" + classGeneric_uri + "> . <" + instance2_uri + "> ?rel ?instorigin . " + "OPTIONAL{?rel rdfs:label ?rellab} } ";
      }
      execute(sparql, -1);
      slots.addNewRDFEntities(retrieveBasicPropertiesFrom());
      RDFEntityList localRDFEntityList1 = slots;return localRDFEntityList1;
    }
    finally {}
  }
  
  public ArrayList<RDFPath> getKBIndirectRelationsWithLiterals(String class_URI, String literal)
    throws Exception
  {
    try
    {
      ArrayList<RDFPath> paths = new ArrayList();
      if (this.repository.getRepositoryName().equals("bbc_backstage"))
      {
        System.out.println("Looking for indirect relations with a literal is too expensive");
      }
      else
      {
        String sparql;
        if (literal.contains("\"")) {
          sparql = "SELECT DISTINCT ?property ?reference ?property2  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?property ?refInst . ?refInst ?property2 " + literal + " . " + "?property2 rdfs:range <http://www.w3.org/2001/XMLSchema#string> . ?instance rdf:type <" + class_URI + "> . " + "?refInst rdf:type ?reference . " + "?property rdfs:range ?reference . " + "?property2 rdfs:domain ?reference . " + "?property rdfs:domain ?origin . " + "}";
        } else {
          sparql = "SELECT DISTINCT ?property ?reference ?property2  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?property ?refInst . ?refInst ?property2 \"" + literal + "\" . " + "?property2 rdfs:range <http://www.w3.org/2001/XMLSchema#string> . ?instance rdf:type <" + class_URI + "> . " + "?refInst rdf:type ?reference . " + "?property rdfs:range ?reference . " + "?property2 rdfs:domain ?reference . " + "?property rdfs:domain <" + class_URI + "> . } ";
        }
        execute(sparql, -1);
        paths.addAll(retrievePathFrom());
        for (RDFPath path : paths)
        {
          path.getRDFProperty1().setDomain(getDomainOfProperty(path.getRDFProperty1().getURI()));
          path.getRDFProperty1().setRange(getRangeOfProperty(path.getRDFProperty1().getURI()));
          path.getRDFProperty2().setDomain(getDomainOfProperty(path.getRDFProperty2().getURI()));
          path.getRDFProperty2().setRange(getRangeOfProperty(path.getRDFProperty2().getURI()));
        }
      }
      String sparql = paths;return sparql;
    }
    finally {}
  }
  
  public RDFEntityList getInstanceProperties(String instance1_uri, String instance2_uri)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      
      String sparql = "SELECT DISTINCT ?rel ?rellab FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + instance2_uri + "> ?rel <" + instance1_uri + "> . " + "OPTIONAL{?rel rdfs:label ?rellab}}";
      
      execute(sparql, -1);
      slots = retrieveBasicPropertiesFrom();
      
      String sparql2 = "SELECT DISTINCT ?rel ?rellab  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + instance1_uri + "> ?rel <" + instance2_uri + "> . " + "OPTIONAL{?rel rdfs:label ?rellab}}";
      
      execute(sparql2, -1);
      slots.addAllRDFEntity(retrieveBasicPropertiesFrom());
      RDFEntityList localRDFEntityList1 = slots;return localRDFEntityList1;
    }
    finally {}
  }
  
  public String getFirstEnLabel(String entity_uri)
    throws Exception
  {
    ResultSet result2 = null;
    try
    {
      String sparql = "SELECT ?l  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + entity_uri + "> rdfs:label ?l} LIMIT 1 ";
      
      result2 = executeLocal(sparql);
      if (!result2.next()) {
        return null;
      }
      String v = result2.getString(1);
      return v;
    }
    finally
    {
      result2.close();
    }
  }
  
  public String getLabelOfEntity(String entity_uri)
    throws Exception
  {
    try
    {
      String sparql = "SELECT ?l  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + entity_uri + "> rdfs:label ?l}";
      
      execute(sparql, -1);
      if ((this.result == null) || (!this.result.next()))
      {
        String str1 = null;return str1;
      }
      String v = this.result.getString(1);
      String str2 = v;return str2;
    }
    finally {}
  }
  
  public RDFEntityList getUnionDefinitionForBlankNode(String node)
    throws Exception
  {
    try
    {
      RDFEntityList entList = new RDFEntityList();
      node = node.replaceFirst("_:", ":");
      
      String sparql = "SELECT ?c1 ?l1 ?c2 ?l2 FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?class <http://www.w3.org/2002/07/owl#unionOf> ?x . ?x rdf:first ?c1 . " + "OPTIONAL{?c1 rdfs:label ?l1} . ?x rdf:rest ?y. ?y rdf:first ?c2 . " + "OPTIONAL{?c2 rdfs:label ?l2} . " + "FILTER (str(?class) = \"" + node + "\") . }";
      
      execute(sparql, -1);
      if ((this.result == null) || (this.con == null))
      {
        System.out.println("The results or connection is closed");
        RDFEntityList localRDFEntityList1 = entList;return localRDFEntityList1;
      }
      while (this.result.next())
      {
        ent1 = this.result.getString(1);
        String labelent1 = this.result.getString(2);
        String ent2 = this.result.getString(3);
        String labelent2 = this.result.getString(4);
        if ((ent1 != null) && (((String)ent1).toString().trim().length() > 0)) {
          entList.addRDFEntity(new RDFEntity("class", ((String)ent1).toString().trim(), labelent1 == null ? null : labelent1.toString().trim(), getPluginID()));
        }
        if ((ent2 != null) && (ent2.toString().trim().length() > 0)) {
          entList.addRDFEntity(new RDFEntity("class", ent2.toString().trim(), labelent2 == null ? null : labelent2.toString().trim(), getPluginID()));
        }
      }
      Object ent1 = entList;return (RDFEntityList)ent1;
    }
    finally {}
  }
  
  public RDFEntityList getDomainOfProperty(String entity_uri)
    throws Exception
  {
    try
    {
      RDFEntityList domainList = new RDFEntityList();
      
      String sparql = "SELECT DISTINCT ?d ?l  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + entity_uri + "> rdfs:domain ?d . " + "OPTIONAL{?d rdfs:label ?l. " + "FILTER( lang(?l) = \"en\" || lang(?l) = \"\" )}}";
      
      execute(sparql, -1);
      if (this.result == null)
      {
        RDFEntityList localRDFEntityList1 = domainList;return localRDFEntityList1;
      }
      while (this.result.next())
      {
        domain = this.result.getString(1);
        String labelDomain = this.result.getString(2);
        if ((domain != null) && (((String)domain).toString().trim().length() > 0)) {
          if (((String)domain).toString().startsWith("node"))
          {
            System.out.println("reading blank node " + ((String)domain).toString());
            domainList.addNewRDFEntities(getUnionDefinitionForBlankNode(((String)domain).toString()));
          }
          else
          {
            domainList.addRDFEntity(new RDFEntity("class", ((String)domain).toString().trim(), labelDomain == null ? null : labelDomain.toString().trim(), getPluginID()));
          }
        }
      }
      Object domain = domainList;return (RDFEntityList)domain;
    }
    finally {}
  }
  
  public boolean isOWLRepository()
  {
    if (getRepositoryType().equals("OWL")) {
      return true;
    }
    return false;
  }
  
  public RDFProperty getRDFProperty(String property_uri)
    throws Exception
  {
    if ((property_uri != null) && (MyURI.isURIValid(property_uri)))
    {
      RDFProperty p = new RDFProperty(property_uri, getLabelOfEntity(property_uri), getPluginID());
      p.setDomain(getDomainOfProperty(property_uri));
      p.setRange(getRangeOfProperty(property_uri));
      return p;
    }
    return null;
  }
  
  public RDFEntityList getRangeOfProperty(String property_uri)
    throws Exception
  {
    try
    {
      RDFEntityList rangeList = new RDFEntityList();
      
      String sparql = "SELECT DISTINCT ?rt ?l  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + property_uri + "> rdfs:range ?rt . " + "OPTIONAL{?rt rdfs:label ?l. " + "FILTER( lang(?l) = \"en\" || lang(?l) = \"\" )}}";
      
      execute(sparql, -1);
      if (this.result == null)
      {
        RDFEntityList localRDFEntityList1 = rangeList;return localRDFEntityList1;
      }
      while (this.result.next())
      {
        range = this.result.getString(1);
        String labelRange = this.result.getString(2);
        if ((range != null) && (MyURI.isURIValid(((String)range).toString()))) {
          if (((String)range).toString().startsWith("http://www.w3.org/2001/XMLSchema#"))
          {
            rangeList.addRDFEntity(new RDFEntity("datatype", ((String)range).toString().trim(), labelRange == null ? null : labelRange.toString().trim(), getPluginID()));
          }
          else if (((String)range).toString().startsWith("_:node"))
          {
            System.out.println("reading blank node " + ((String)range).toString());
            rangeList.addNewRDFEntities(getUnionDefinitionForBlankNode(((String)range).toString()));
          }
          else
          {
            rangeList.addRDFEntity(new RDFEntity("class", ((String)range).toString().trim(), labelRange == null ? null : labelRange.toString().trim(), getPluginID()));
          }
        }
      }
      Object range = rangeList;return (RDFEntityList)range;
    }
    finally {}
  }
  
  public RDFEntityList getEquivalentEntitiesForClass(String entity_uri)
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      
      String sparql = "SELECT DISTINCT ?x ?xl  FROM  <" + this.repository.getGraphIRI() + "> WHERE { " + "{?x owl:equivalentClass <" + entity_uri + ">}" + "UNION " + "{<" + entity_uri + "> owl:equivalentClass ?x}. " + "OPTIONAL{?x rdfs:label ?xl}}";
      
      execute(sparql, -1);
      aux = retrieveRDFEntityFrom("instance");
      
      RDFEntityList localRDFEntityList1 = aux;return localRDFEntityList1;
    }
    finally {}
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForClasses()
    throws Exception
  {
    try
    {
      String sparql = "SELECT DISTINCT ?ent ?entl ?eq ?eql  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?ent owl:equivalentClass ?eq . " + "OPTIONAL{?ent rdfs:label ?entl} . " + "OPTIONAL{?eq rdfs:label ?eql} . }";
      
      execute(sparql, -1);
      Hashtable localHashtable = retrieveEquivalentEntityFrom("class");return localHashtable;
    }
    finally {}
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForProperties()
    throws Exception
  {
    try
    {
      String sparql = "SELECT DISTINCT ?ent ?entl ?eq ?eql  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?ent owl:equivalentProperty ?eq . " + "OPTIONAL{?ent rdfs:label ?entl} . " + "OPTIONAL{?eq rdfs:label ?eql} . }";
      
      execute(sparql, -1);
      Hashtable localHashtable = retrieveEquivalentEntityFrom("property");return localHashtable;
    }
    finally {}
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForInstances()
    throws Exception
  {
    try
    {
      String sparql = "SELECT DISTINCT ?ent ?entl ?eq ?eql  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?ent owl:sameAs ?eq . " + "OPTIONAL{?ent rdfs:label ?entl} . " + "OPTIONAL{?eq rdfs:label ?eql} . }";
      
      execute(sparql, -1);
      Hashtable localHashtable = retrieveEquivalentEntityFrom("instance");return localHashtable;
    }
    finally {}
  }
  
  public RDFEntityList getEquivalentEntitiesForProperty(String entity_uri)
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      
      String sparql = "SELECT DISTINCT ?x ?xl  FROM  <" + this.repository.getGraphIRI() + "> WHERE { " + "{?x owl:equivalentProperty <" + entity_uri + ">} " + "UNION" + "{<" + entity_uri + "> owl:equivalentProperty ?x}." + "OPTIONAL{?x rdfs:label ?xl}}";
      
      execute(sparql, -1);
      aux = retrieveRDFEntityFrom("instance");
      
      RDFEntityList localRDFEntityList1 = aux;return localRDFEntityList1;
    }
    finally {}
  }
  
  public RDFEntityList getEquivalentEntitiesForInstance(String entity_uri)
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      
      String sparql = "SELECT DISTINCT ?x ?xl  FROM  <" + this.repository.getGraphIRI() + "> WHERE { " + "{?x owl:sameAs <" + entity_uri + ">} " + "UNION" + "{<" + entity_uri + "> owl:sameAs ?x}. " + "OPTIONAL{?x rdfs:label ?xl}}";
      
      execute(sparql, -1);
      aux = retrieveRDFEntityFrom("instance");
      
      RDFEntityList localRDFEntityList1 = aux;return localRDFEntityList1;
    }
    finally {}
  }
  
  public boolean existTripleForProperty(String entity)
  {
    return true;
  }
  
  public boolean existTripleForInstance(String entity)
  {
    return true;
  }
  
  private void execute(String sparql, int timeout_sec)
  {
    MappingSession.increaseVirtuosoCalls();
    
    long start = System.currentTimeMillis();
    long time = 0L;
    try
    {
      Class.forName("virtuoso.jdbc3.Driver");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Keine Treiber-Klasse");
      return;
    }
    try
    {
      if (this.con != null) {
        this.con.close();
      }
    }
    catch (Exception e)
    {
      System.out.println("Unable to close connection");
      e.printStackTrace();
    }
    this.con = null;
    try
    {
      try
      {
        this.con = DriverManager.getConnection("jdbc:virtuoso://" + this.repository.getServerURL() + "/UID=" + this.repository.getLogin() + "/PWD=" + this.repository.getPassword());
        
        Statement stmt = this.con.createStatement();
        
        int query_timeout = 6;
        if (timeout_sec > 0) {
          query_timeout = timeout_sec;
        }
        stmt.setQueryTimeout(query_timeout);
        this.result = stmt.executeQuery("SPARQL " + sparql);
        time = System.currentTimeMillis() - start;
        if (time > 400L) {
          throw new ToLongException();
        }
      }
      finally {}
    }
    catch (ToLongException e)
    {
      e = 
      
        e;System.out.println("Too long (plugin) : " + time + "ms\t" + sparql + "\t");boolean done = false;
      for (StackTraceElement trace : e.getStackTrace())
      {
        if (done)
        {
          System.out.println(trace + "\n");
          
          break;
        }
        done = true;
      }
    }
    catch (Exception e)
    {
      e = 
      
        e;this.result = null;System.out.println("Fail to execute: " + sparql);e.printStackTrace();
      try
      {
        this.con.close();
        this.con = null;
      }
      catch (Exception ex)
      {
        System.out.println("closing failed connection");
        ex.printStackTrace();
      }
    }
    finally {}
  }
  
  private ResultSet executeLocal(String sparql)
  {
    MappingSession.increaseVirtuosoCalls();
    ResultSet result2 = null;
    
    long start = System.currentTimeMillis();
    try
    {
      Class.forName("virtuoso.jdbc3.Driver");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Keine Treiber-Klasse");
      return null;
    }
    this.con = null;
    try
    {
      this.con = DriverManager.getConnection("jdbc:virtuoso://" + this.repository.getServerURL() + "/UID=" + this.repository.getLogin() + "/PWD=" + this.repository.getPassword());
      
      Statement stmt = this.con.createStatement();
      result2 = stmt.executeQuery("SPARQL " + sparql);
    }
    catch (Exception e)
    {
      e = 
      
        e;System.out.println("Fail to execute: " + sparql);e.printStackTrace();
    }
    finally {}
    return result2;
  }
  
  private Hashtable<RDFEntity, RDFEntityList> retrieveEquivalentEntityFrom(String type)
    throws Exception
  {
    Hashtable<RDFEntity, RDFEntityList> equivalentEntityTable = new Hashtable();
    if (this.result == null) {
      return equivalentEntityTable;
    }
    while (this.result.next())
    {
      String rdfEntity = this.result.getString(1);
      String rdfEntityLabel = this.result.getString(2);
      String rdfEquivalentEntity = this.result.getString(3);
      String rdfEquivalentEntityLabel = this.result.getString(4);
      if ((rdfEntity != null) && (rdfEquivalentEntity != null))
      {
        RDFEntity entity = new RDFEntity(type, rdfEntity.toString(), rdfEntityLabel == null ? new MyURI(rdfEntity.toString()).getLocalName() : rdfEntityLabel.toString(), getPluginID());
        RDFEntity equivalentEntity = new RDFEntity(type, rdfEquivalentEntity.toString(), rdfEquivalentEntityLabel == null ? new MyURI(rdfEquivalentEntity.toString()).getLocalName() : rdfEquivalentEntityLabel.toString(), getPluginID());
        if (equivalentEntityTable.containsKey(entity))
        {
          ((RDFEntityList)equivalentEntityTable.get(entity)).addRDFEntity(equivalentEntity);
        }
        else
        {
          RDFEntityList list = new RDFEntityList();
          list.addRDFEntity(equivalentEntity);
          equivalentEntityTable.put(entity, list);
        }
      }
    }
    return equivalentEntityTable;
  }
  
  private RDFEntityList retrieveRDFTripleValueFrom(String type)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if (this.result == null) {
      return entities;
    }
    ArrayList<RDFEntity> entitiesList = new ArrayList();
    while (this.result.next())
    {
      String rdfEntity = this.result.getString(1);
      String rdfEntityLabel = this.result.getString(2);
      String rdfEquivalentEntity = this.result.getString(3);
      String rdfEquivalentEntityLabel = this.result.getString(4);
      
      RDFEntity entity = new RDFEntity(type, rdfEntity, rdfEntityLabel, getPluginID());
      
      RDFEntity refEntity = new RDFEntity(type, rdfEquivalentEntity, rdfEquivalentEntityLabel, getPluginID());
      entity.setRefers_to(refEntity);
      
      entitiesList.add(entity);
    }
    entities.getAllRDFEntities().addAll(entitiesList);
    return entities;
  }
  
  private RDFEntityList retrieveRDFValueFrom(String type)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if ((this.result == null) || (this.con == null))
    {
      System.out.println("The result or connection is closed");
      return entities;
    }
    while (this.result.next())
    {
      String value = this.result.getString(1);
      if (value != null)
      {
        String valueString = value.toString().trim();
        if ((!valueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#List")))
        {
          String uri = value.toString();
          
          String label = null;
          value = this.result.getString(2);
          if (value != null) {
            label = value.toString();
          } else {
            label = MyURI.getLocalName(uri);
          }
          RDFEntity c = new RDFEntity(type, uri, label, getPluginID());
          
          entities.getAllRDFEntities().add(c);
        }
      }
    }
    return entities;
  }
  
  private RDFEntityList retrieveRDFEntityForIndex(String type)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if ((this.result == null) || (this.con == null))
    {
      System.out.println("The result or connection is closed");
      return entities;
    }
    while (this.result.next())
    {
      String value = this.result.getString(1);
      if (value != null)
      {
        String valueString = value.toString().trim();
        if ((!valueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#List")) && 
        
          (MyURI.isURIValid(value.toString())))
        {
          String uri = value.toString();
          
          String label = null;
          value = this.result.getString(2);
          if (value != null)
          {
            label = value.toString();
          }
          else
          {
            String title = null;
            if (type == "instance")
            {
              value = this.result.getString(3);
              if (value != null) {
                title = value.toString();
              }
            }
            if (title == null) {
              label = MyURI.getLocalName(uri);
            } else {
              label = title;
            }
          }
          RDFEntity c = new RDFEntity(type, uri, label, getPluginID());
          
          entities.addRDFEntity(c);
        }
      }
    }
    return entities;
  }
  
  private RDFEntityList retrieveRDFEntityFrom(String type)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if (this.result == null) {
      return entities;
    }
    while (this.result.next())
    {
      String firstValue = this.result.getString(1);
      if (firstValue != null)
      {
        String firstValueString = firstValue.toString().trim();
        if ((!firstValueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#Class")) && (!firstValueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#Class")) && (!firstValueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#List"))) {
          if (!MyURI.isURIValid(firstValueString))
          {
            if (firstValueString.startsWith("node"))
            {
              System.out.println("reading blank node " + firstValueString);
              entities.addNewRDFEntities(getUnionDefinitionForBlankNode(firstValueString));
            }
          }
          else
          {
            String uri = firstValueString;
            
            String secondValue = this.result.getString(2);
            String label = null;
            if (secondValue != null) {
              label = secondValue.toString();
            } else {
              label = MyURI.getLocalName(uri);
            }
            RDFEntity c;
            RDFEntity c;
            if (type.equals("property")) {
              c = new RDFProperty(uri, label, getPluginID());
            } else {
              c = new RDFEntity(type, uri, label, getPluginID());
            }
            entities.getAllRDFEntities().add(c);
          }
        }
      }
    }
    return entities;
  }
  
  private RDFEntityList retrieveRDFEntityLiteralsFrom(String instance_uri, boolean limitsize)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if (this.result == null) {
      return entities;
    }
    while (this.result.next())
    {
      String value = this.result.getString(1);
      if (value != null)
      {
        String finalValue = value.toString().trim();
        if ((!limitsize) || ((finalValue.length() <= 40) && (finalValue.length() > 0)))
        {
          RDFEntity c = new RDFEntity("literal", instance_uri, finalValue, getPluginID());
          entities.addRDFEntity(c);
        }
      }
    }
    return entities;
  }
  
  private ArrayList<String> retrieveEntitiesUriFrom(boolean keepPrimitiveURIs)
    throws Exception
  {
    ArrayList<String> entities = new ArrayList();
    if (this.result == null) {
      return entities;
    }
    while (this.result.next())
    {
      String value = this.result.getString(1);
      if ((value != null) && 
      
        (MyURI.isURIValid(value.toString())) && (
        
        (keepPrimitiveURIs) || (
        (!value.toString().equals("http://www.w3.org/2002/07/owl#Class")) && (!value.toString().equals("http://www.w3.org/2000/01/rdf-schema#Class")) && (!value.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#List"))))) {
        entities.add(value.toString());
      }
    }
    return entities;
  }
  
  private ArrayList<RDFPath> retrieveFullPathFrom()
    throws Exception
  {
    ArrayList<RDFPath> paths = new ArrayList();
    if ((this.result == null) || (!this.result.next())) {
      return paths;
    }
    while (this.result.next())
    {
      RDFEntity Refent = null;
      String valueRefEnt = this.result.getString(3);
      String labelRefEnt = this.result.getString(4);
      if ((valueRefEnt != null) && (MyURI.isURIValid(valueRefEnt.toString()))) {
        Refent = new RDFEntity("class", valueRefEnt.toString(), labelRefEnt == null ? null : labelRefEnt.toString().trim(), getPluginID());
      }
      RDFProperty prop1 = null;
      RDFProperty prop1_domain = null;
      
      String valueProp1 = this.result.getString(1);
      String labelProp1 = this.result.getString(2);
      String propertydomain = this.result.getString(7);
      String propertydomainlabel = this.result.getString(8);
      if ((valueProp1 != null) && (MyURI.isURIValid(valueProp1.toString()))) {
        prop1 = new RDFProperty(valueProp1.toString(), labelProp1 == null ? null : labelProp1.toString().trim(), getPluginID());
      }
      if ((propertydomain != null) && (MyURI.isURIValid(propertydomain.toString()))) {
        prop1_domain = new RDFProperty(propertydomain.toString(), propertydomainlabel == null ? null : propertydomainlabel.toString().trim(), getPluginID());
      }
      prop1.setDomain(prop1_domain);
      prop1.setRange(Refent);
      
      RDFProperty prop2 = null;
      
      RDFProperty prop2_range = null;
      String valueProp2 = this.result.getString(5);
      String labelProp2 = this.result.getString(6);
      
      String propertyrange2 = this.result.getString(9);
      String propertyrangelabel2 = this.result.getString(10);
      if ((valueProp2 != null) && (MyURI.isURIValid(valueProp2.toString()))) {
        prop2 = new RDFProperty(valueProp2.toString(), labelProp2 == null ? null : labelProp2.toString().trim(), getPluginID());
      }
      if ((propertyrange2 != null) && (MyURI.isURIValid(propertyrange2.toString()))) {
        prop2_range = new RDFProperty(propertyrange2.toString(), propertyrangelabel2 == null ? null : propertyrangelabel2.toString().trim(), getPluginID());
      }
      prop2.setDomain(Refent);
      prop2.setRange(prop2_range);
      
      RDFPath path = new RDFPath(prop1, Refent, prop2);
      paths.add(path);
    }
    return paths;
  }
  
  private ArrayList<RDFPath> retrievePathFrom()
    throws Exception
  {
    ArrayList<RDFPath> paths = new ArrayList();
    if (this.result == null) {
      return paths;
    }
    while (this.result.next())
    {
      RDFEntity ent = null;
      String valueEnt = this.result.getString(2);
      if ((valueEnt != null) && (MyURI.isURIValid(valueEnt.toString())))
      {
        String labelEnt = getFirstEnLabel(valueEnt.toString());
        ent = new RDFEntity("class", valueEnt.toString(), labelEnt == null ? null : labelEnt, getPluginID());
      }
      RDFProperty prop1 = null;
      String valueProp1 = this.result.getString(1);
      if ((valueProp1 != null) && (MyURI.isURIValid(valueProp1.toString())))
      {
        String labelProp1 = getFirstEnLabel(valueProp1.toString());
        prop1 = new RDFProperty(valueProp1.toString(), labelProp1 == null ? null : labelProp1, getPluginID());
      }
      RDFProperty prop2 = null;
      String valueProp2 = this.result.getString(3);
      if ((valueProp2 != null) && (MyURI.isURIValid(valueProp2.toString())))
      {
        String labelProp2 = getFirstEnLabel(valueProp2.toString());
        prop2 = new RDFProperty(valueProp2.toString(), labelProp2 == null ? null : labelProp2, getPluginID());
      }
      RDFPath path = new RDFPath(prop1, ent, prop2);
      paths.add(path);
    }
    return paths;
  }
  
  private RDFEntityList retrieveFullPropertiesFrom()
    throws Exception
  {
    RDFEntityList properties = new RDFEntityList();
    if ((this.result == null) || (this.con == null))
    {
      System.out.println("The result or connection is closed");
      return properties;
    }
    Hashtable<String, RDFProperty> propertiesTable = new Hashtable();
    while (this.result.next())
    {
      RDFProperty p = null;
      String value = this.result.getString(1);
      String labelP = this.result.getString(4);
      if ((value != null) && (MyURI.isURIValid(value.toString())))
      {
        if (propertiesTable.contains(value))
        {
          p = (RDFProperty)propertiesTable.get(value);
        }
        else
        {
          p = new RDFProperty(value.toString(), labelP == null ? null : labelP.toString().trim(), getPluginID());
          propertiesTable.put(value.toString(), p);
        }
        String origin = this.result.getString(2);
        String labelOrigin = this.result.getString(5);
        if ((origin != null) && (MyURI.isURIValid(origin.toString())))
        {
          RDFEntity classDomain = new RDFEntity("class", origin.toString().trim(), labelOrigin == null ? null : labelOrigin.toString().trim(), getPluginID());
          
          p.addDomain(classDomain);
        }
        String range = this.result.getString(3);
        String labelRange = this.result.getString(6);
        if ((range != null) && (MyURI.isURIValid(range.toString())))
        {
          RDFEntity classRange = null;
          if (range.toString().startsWith("http://www.w3.org/2001/XMLSchema#"))
          {
            classRange = new RDFEntity("datatype", range.toString().trim(), labelRange == null ? null : labelRange.toString().trim(), getPluginID());
            
            p.addRange(classRange);
          }
          else
          {
            classRange = new RDFEntity("class", range.toString().trim(), labelRange == null ? null : labelRange.toString().trim(), getPluginID());
            
            p.addRange(classRange);
          }
        }
      }
    }
    for (RDFProperty p : propertiesTable.values()) {
      properties.addRDFEntity(p);
    }
    return properties;
  }
  
  private RDFEntityList retrieveBasicPropertiesFrom()
    throws Exception
  {
    RDFEntityList properties = new RDFEntityList();
    if (this.result == null) {
      return properties;
    }
    while (this.result.next())
    {
      RDFProperty p = null;
      String rel = this.result.getString(1);
      String rel_label = this.result.getString(2);
      if ((rel != null) && (MyURI.isURIValid(rel.toString())))
      {
        p = new RDFProperty(rel.toString(), rel_label == null ? null : rel_label.toString().trim(), getPluginID());
        
        properties.getAllRDFEntities().add(p);
      }
    }
    for (RDFEntity p : properties.getAllRDFEntities())
    {
      ((RDFProperty)p).setDomain(getDomainOfProperty(p.getURI()));
      ((RDFProperty)p).setRange(getRangeOfProperty(p.getURI()));
    }
    return properties;
  }
  
  private RDFEntityList getSubClassesFromIntersectionDefinition(String class_uri)
    throws Exception
  {
    try
    {
      String sparql = "SELECT ?c ?l  FROM  <" + this.repository.getGraphIRI() + "> WHERE { " + "?c owl:intersectionOf ?x . " + "OPTIONAL{?c rdfs:label ?l} . ?x ?p ?<" + class_uri + "> . }";
      
      execute(sparql, -1);
      RDFEntityList localRDFEntityList = retrieveRDFEntityFrom("class");return localRDFEntityList;
    }
    finally {}
  }
  
  private boolean isURIString(String s)
  {
    if ((s.startsWith("http:")) || (s.startsWith("file:"))) {
      return true;
    }
    return false;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getServerURL()
  {
    return this.repository.getServerURL();
  }
  
  public String getRepositoryType()
  {
    return this.repository.getRepositoryType();
  }
  
  public String getRepositoryName()
  {
    return this.repository.getRepositoryName();
  }
  
  public String getLogin()
  {
    return this.repository.getLogin();
  }
  
  public String getPassword()
  {
    return this.repository.getPassword();
  }
  
  public String getPluginID()
  {
    return this.repository.getRepositoryName();
  }
  
  public int numberOfAllTriples(String onto)
  {
    try
    {
      try
      {
        String sparql = "SELECT count(*) FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?s ?p ?o } LIMIT 100000";
        
        execute(sparql, 1);
        if (this.result != null)
        {
          int rowCount = 0;
          while (this.result.next()) {
            rowCount = this.result.getInt(1);
          }
          int i = rowCount;return i;
        }
      }
      finally {}
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
      e.printStackTrace();
    }
    return -1;
  }
  
  public int numberOfTriplesWithInstanceAsSubject(String onto, String i)
  {
    return numberOfTriplesWithPropertyAsSubject(onto, i);
  }
  
  public int numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(String onto, String class_uri)
  {
    try
    {
      try
      {
        RDFEntityList classes = new RDFEntityList();
        if ((!class_uri.startsWith("node")) && (!class_uri.startsWith("_:node")))
        {
          String sparql = "SELECT count(*)  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + class_uri + "> rdfs:subClassOf ?c . " + "OPTIONAL{?c rdfs:label ?l} . " + "FILTER ( (isURI(?c) && not str(?c) = <" + class_uri + "> ))}";
          
          execute(sparql, -1);
          if (this.result != null)
          {
            int rowCount = 0;
            while (this.result.next()) {
              rowCount = this.result.getInt(1);
            }
            int i = rowCount;return i;
          }
        }
      }
      finally {}
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
      e.printStackTrace();
    }
    return -1;
  }
  
  public Set<String> getClassesOf(String onto, String i)
  {
    Set<String> result = new HashSet();
    try
    {
      RDFEntityList classes = getAllClassesOfInstance(i);
      for (RDFEntity classe : classes.getAllRDFEntities()) {
        result.add(classe.getURI());
      }
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
    }
    return result;
  }
  
  public Set<String> getInstancesOf(String onto, String c)
  {
    Set<String> result = new HashSet();
    try
    {
      RDFEntityList instances = getAllInstancesOfClass(c, -1);
      for (RDFEntity instance : instances.getAllRDFEntities()) {
        result.add(instance.getURI());
      }
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
    }
    return result;
  }
  
  public int numberOfTriplesWithClassAsSubject(String onto, String c)
  {
    return numberOfTriplesWithPropertyAsSubject(onto, c);
  }
  
  public int numberOfTriplesWithClassAsObjectAndTypeProperty(String onto, String c)
  {
    try
    {
      String sparql = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT DISTINCT count(*)  FROM  <" + this.repository.getGraphIRI() + "> " + "WHERE{ ?i rdf:type <" + c + "> . " + "OPTIONAL{?i rdfs:label ?l}. " + "FILTER( isURI(?i) )}";
      
      execute(sparql, -1);
      if (this.result != null)
      {
        int rowCount = 0;
        while (this.result.next()) {
          rowCount = this.result.getInt(1);
        }
        int i = rowCount;return i;
      }
    }
    catch (Exception e)
    {
      e = 
      
        e;e.printStackTrace();
    }
    finally {}
    return -1;
  }
  
  public int numberOfTripleWithClassAsObjectAndDomainProperty(String onto, String c)
  {
    try
    {
      return getDomainOfProperty(c).size();
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
    }
    return -1;
  }
  
  public int numberOfTriplesWithPropertyAsSubject(String onto, String p)
  {
    try
    {
      try
      {
        String sparql = "SELECT count(*)  FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + p + "> ?p ?o . }";
        
        execute(sparql, -1);
        if (this.result != null)
        {
          int rowCount = 0;
          while (this.result.next()) {
            rowCount = this.result.getInt(1);
          }
          int i = rowCount;return i;
        }
      }
      finally {}
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
      e.printStackTrace();
    }
    return -1;
  }
  
  public int numberOfTriplesWithPropertyAsPredicate(String onto, String p)
  {
    try
    {
      try
      {
        String sparql = "SELECT count(*)  FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?s <" + p + "> ?o}";
        
        execute(sparql, -1);
        if (this.result != null)
        {
          int rowCount = 0;
          while (this.result.next()) {
            rowCount = this.result.getInt(1);
          }
          int i = rowCount;return i;
        }
      }
      finally {}
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
      e.printStackTrace();
    }
    return -1;
  }
  
  public Set<String> getSubPropertyOf(String onto, String p)
  {
    Set<String> res = new HashSet();
    return res;
  }
  
  public org.openrdf.repository.Repository getSesameRepository()
  {
    return this.sesameRepository;
  }
  
  public void setSesameRepository(org.openrdf.repository.Repository sesameRepository)
  {
    this.sesameRepository = sesameRepository;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    System.out.println("start");
    
    VirtuosoPlugin plugin = new VirtuosoPlugin();
    plugin.loadPlugin(new RepositoryVirtuoso("kmi-web03.open.ac.uk", "wwwcache.open.ac.uk", "8890", "dba", "dba", "virtuoso", "jdbc:virtuoso://kmi-web03.open.ac.uk:8890#http://dbpedia.org", "OWL"));
    try
    {
      long startTime = System.currentTimeMillis();
      
      plugin.execute("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?c ?l FROM <http://dbpedia.org> WHERE{ <http://dbpedia.org/resource/Russia> rdf:type ?c. OPTIONAL{ ?c rdfs:label ?l}}", -1);
    }
    catch (Exception e)
    {
      System.out.println("not done");
      e.printStackTrace();
    }
  }
  
  public void loadConfiguration(String serverURL, String repositoryType, String repositoryName, String login, String pasword) {}
  
  public void closePlugin() {}
  
  public void initializeServer()
    throws Exception
  {}
  
  public void initializeServer(String proxyHost, String proxyPort)
    throws Exception
  {}
  
  /* Error */
  public String entityType(String onto, String e)
  {
    // Byte code:
    //   0: new 19	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 20	java/lang/StringBuilder:<init>	()V
    //   7: ldc_w 649
    //   10: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   13: aload_0
    //   14: getfield 12	virtuosoPlugin/VirtuosoPlugin:repository	Lpoweraqua/serviceConfig/RepositoryVirtuoso;
    //   17: invokevirtual 23	poweraqua/serviceConfig/RepositoryVirtuoso:getGraphIRI	()Ljava/lang/String;
    //   20: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: ldc_w 650
    //   26: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   29: ldc_w 651
    //   32: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   35: aload_2
    //   36: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   39: ldc_w 652
    //   42: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   45: invokevirtual 29	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   48: astore_3
    //   49: aload_0
    //   50: aload_3
    //   51: iconst_m1
    //   52: invokespecial 31	virtuosoPlugin/VirtuosoPlugin:execute	(Ljava/lang/String;I)V
    //   55: aload_0
    //   56: getfield 105	virtuosoPlugin/VirtuosoPlugin:result	Ljava/sql/ResultSet;
    //   59: ifnull +37 -> 96
    //   62: iconst_0
    //   63: istore 4
    //   65: aload_0
    //   66: getfield 105	virtuosoPlugin/VirtuosoPlugin:result	Ljava/sql/ResultSet;
    //   69: invokeinterface 106 1 0
    //   74: ifeq +9 -> 83
    //   77: iinc 4 1
    //   80: goto -15 -> 65
    //   83: iload 4
    //   85: ifle +11 -> 96
    //   88: ldc_w 653
    //   91: astore 5
    //   93: aload 5
    //   95: areturn
    //   96: new 19	java/lang/StringBuilder
    //   99: dup
    //   100: invokespecial 20	java/lang/StringBuilder:<init>	()V
    //   103: ldc_w 649
    //   106: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   109: aload_0
    //   110: getfield 12	virtuosoPlugin/VirtuosoPlugin:repository	Lpoweraqua/serviceConfig/RepositoryVirtuoso;
    //   113: invokevirtual 23	poweraqua/serviceConfig/RepositoryVirtuoso:getGraphIRI	()Ljava/lang/String;
    //   116: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   119: ldc_w 654
    //   122: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   125: ldc_w 651
    //   128: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   131: aload_2
    //   132: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: ldc_w 652
    //   138: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: invokevirtual 29	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   144: astore_3
    //   145: aload_0
    //   146: aload_3
    //   147: iconst_m1
    //   148: invokespecial 31	virtuosoPlugin/VirtuosoPlugin:execute	(Ljava/lang/String;I)V
    //   151: aload_0
    //   152: getfield 105	virtuosoPlugin/VirtuosoPlugin:result	Ljava/sql/ResultSet;
    //   155: ifnull +37 -> 192
    //   158: iconst_0
    //   159: istore 4
    //   161: aload_0
    //   162: getfield 105	virtuosoPlugin/VirtuosoPlugin:result	Ljava/sql/ResultSet;
    //   165: invokeinterface 106 1 0
    //   170: ifeq +9 -> 179
    //   173: iinc 4 1
    //   176: goto -15 -> 161
    //   179: iload 4
    //   181: ifle +11 -> 192
    //   184: ldc_w 653
    //   187: astore 5
    //   189: aload 5
    //   191: areturn
    //   192: new 19	java/lang/StringBuilder
    //   195: dup
    //   196: invokespecial 20	java/lang/StringBuilder:<init>	()V
    //   199: ldc_w 655
    //   202: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   205: aload_0
    //   206: getfield 12	virtuosoPlugin/VirtuosoPlugin:repository	Lpoweraqua/serviceConfig/RepositoryVirtuoso;
    //   209: invokevirtual 23	poweraqua/serviceConfig/RepositoryVirtuoso:getGraphIRI	()Ljava/lang/String;
    //   212: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   215: ldc_w 656
    //   218: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   221: ldc_w 657
    //   224: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   227: aload_2
    //   228: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   231: ldc_w 658
    //   234: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   237: ldc_w 659
    //   240: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   243: ldc_w 660
    //   246: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   249: invokevirtual 29	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   252: astore_3
    //   253: aload_0
    //   254: aload_3
    //   255: iconst_m1
    //   256: invokespecial 31	virtuosoPlugin/VirtuosoPlugin:execute	(Ljava/lang/String;I)V
    //   259: aload_0
    //   260: getfield 105	virtuosoPlugin/VirtuosoPlugin:result	Ljava/sql/ResultSet;
    //   263: ifnull +37 -> 300
    //   266: iconst_0
    //   267: istore 4
    //   269: aload_0
    //   270: getfield 105	virtuosoPlugin/VirtuosoPlugin:result	Ljava/sql/ResultSet;
    //   273: invokeinterface 106 1 0
    //   278: ifeq +9 -> 287
    //   281: iinc 4 1
    //   284: goto -15 -> 269
    //   287: iload 4
    //   289: ifle +11 -> 300
    //   292: ldc_w 661
    //   295: astore 5
    //   297: aload 5
    //   299: areturn
    //   300: new 19	java/lang/StringBuilder
    //   303: dup
    //   304: invokespecial 20	java/lang/StringBuilder:<init>	()V
    //   307: ldc_w 655
    //   310: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   313: aload_0
    //   314: getfield 12	virtuosoPlugin/VirtuosoPlugin:repository	Lpoweraqua/serviceConfig/RepositoryVirtuoso;
    //   317: invokevirtual 23	poweraqua/serviceConfig/RepositoryVirtuoso:getGraphIRI	()Ljava/lang/String;
    //   320: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   323: ldc_w 662
    //   326: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   329: ldc_w 657
    //   332: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   335: aload_2
    //   336: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   339: ldc_w 652
    //   342: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   345: invokevirtual 29	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   348: astore_3
    //   349: aload_0
    //   350: aload_3
    //   351: iconst_m1
    //   352: invokespecial 31	virtuosoPlugin/VirtuosoPlugin:execute	(Ljava/lang/String;I)V
    //   355: aload_0
    //   356: getfield 105	virtuosoPlugin/VirtuosoPlugin:result	Ljava/sql/ResultSet;
    //   359: ifnull +37 -> 396
    //   362: iconst_0
    //   363: istore 4
    //   365: aload_0
    //   366: getfield 105	virtuosoPlugin/VirtuosoPlugin:result	Ljava/sql/ResultSet;
    //   369: invokeinterface 106 1 0
    //   374: ifeq +9 -> 383
    //   377: iinc 4 1
    //   380: goto -15 -> 365
    //   383: iload 4
    //   385: ifle +11 -> 396
    //   388: ldc_w 661
    //   391: astore 5
    //   393: aload 5
    //   395: areturn
    //   396: ldc_w 663
    //   399: astore 4
    //   401: aload 4
    //   403: areturn
    //   404: astore 6
    //   406: aload 6
    //   408: athrow
    //   409: astore_3
    //   410: getstatic 8	java/lang/System:out	Ljava/io/PrintStream;
    //   413: new 19	java/lang/StringBuilder
    //   416: dup
    //   417: invokespecial 20	java/lang/StringBuilder:<init>	()V
    //   420: ldc_w 635
    //   423: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   426: aload_1
    //   427: invokevirtual 22	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   430: invokevirtual 29	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   433: invokevirtual 10	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   436: aconst_null
    //   437: areturn
    // Line number table:
    //   Java source line #4268	-> byte code offset #0
    //   Java source line #4271	-> byte code offset #49
    //   Java source line #4272	-> byte code offset #55
    //   Java source line #4273	-> byte code offset #62
    //   Java source line #4274	-> byte code offset #65
    //   Java source line #4275	-> byte code offset #77
    //   Java source line #4276	-> byte code offset #83
    //   Java source line #4277	-> byte code offset #88
    //   Java source line #4281	-> byte code offset #96
    //   Java source line #4284	-> byte code offset #145
    //   Java source line #4285	-> byte code offset #151
    //   Java source line #4286	-> byte code offset #158
    //   Java source line #4287	-> byte code offset #161
    //   Java source line #4288	-> byte code offset #173
    //   Java source line #4289	-> byte code offset #179
    //   Java source line #4290	-> byte code offset #184
    //   Java source line #4296	-> byte code offset #192
    //   Java source line #4301	-> byte code offset #253
    //   Java source line #4302	-> byte code offset #259
    //   Java source line #4303	-> byte code offset #266
    //   Java source line #4304	-> byte code offset #269
    //   Java source line #4305	-> byte code offset #281
    //   Java source line #4306	-> byte code offset #287
    //   Java source line #4307	-> byte code offset #292
    //   Java source line #4312	-> byte code offset #300
    //   Java source line #4315	-> byte code offset #349
    //   Java source line #4316	-> byte code offset #355
    //   Java source line #4317	-> byte code offset #362
    //   Java source line #4318	-> byte code offset #365
    //   Java source line #4319	-> byte code offset #377
    //   Java source line #4320	-> byte code offset #383
    //   Java source line #4321	-> byte code offset #388
    //   Java source line #4324	-> byte code offset #396
    //   Java source line #4325	-> byte code offset #404
    //   Java source line #4329	-> byte code offset #409
    //   Java source line #4330	-> byte code offset #410
    //   Java source line #4332	-> byte code offset #436
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	438	0	this	VirtuosoPlugin
    //   0	438	1	onto	String
    //   0	438	2	e	String
    //   48	303	3	sparql	String
    //   409	2	3	ex	Exception
    //   63	21	4	rowCount	int
    //   159	21	4	rowCount	int
    //   267	21	4	rowCount	int
    //   363	39	4	rowCount	int
    //   91	303	5	str1	String
    //   404	3	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	93	404	finally
    //   96	189	404	finally
    //   192	297	404	finally
    //   300	393	404	finally
    //   396	401	404	finally
    //   404	406	404	finally
    //   0	93	409	java/lang/Exception
    //   96	189	409	java/lang/Exception
    //   192	297	409	java/lang/Exception
    //   300	393	409	java/lang/Exception
    //   396	401	409	java/lang/Exception
    //   404	409	409	java/lang/Exception
  }
}

