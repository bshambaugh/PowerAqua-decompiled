package Sesame2Plugin;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;
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

public class Sesame2Plugin
  implements OntologyPlugin, Serializable
{
  private String name;
  private String serverURL = null;
  private String repositoryType = "RDF";
  private String repositoryName = null;
  private String login = null;
  private String password = null;
  private org.openrdf.repository.Repository sesameRepository = null;
  private TupleQueryResult result;
  private RepositoryConnection con;
  private static final String NOT_RDF_INSTANCES = "and not i=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>";
  private static final String NOT_RDF_CLASSES = " and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> ";
  private static final String NOT_RDF_PROPERTIES = " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
  private static final String NOT_RDF_PROPERTIES1 = " and not property=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not property=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not property=<http://www.w3.org/2000/01/rdf-schema#member> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not property=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not property=<http://www.w3.org/2000/01/rdf-schema#comment> and not property=<http://www.w3.org/2000/01/rdf-schema#label> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>";
  private static final String NOT_RDF_PROPERTIES2 = " and not property2=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not property2=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not property2=<http://www.w3.org/2000/01/rdf-schema#member> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not property2=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not property2=<http://www.w3.org/2000/01/rdf-schema#comment> and not property2=<http://www.w3.org/2000/01/rdf-schema#label> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>";
  
  public Sesame2Plugin()
  {
    setName("sesame2");
  }
  
  public void loadConfiguration(String serverURL, String repositoryType, String repositoryName, String login, String pasword)
  {
    this.serverURL = serverURL;
    this.repositoryType = repositoryType;
    this.repositoryName = repositoryName;
    this.login = login;
    this.password = pasword;
  }
  
  public void closePlugin() {}
  
  public void initializeServer()
    throws Exception
  {
    try
    {
      String repositoryid = getRepositoryName();
      int repositorynamespace = getRepositoryName().lastIndexOf("/");
      if (repositorynamespace > 0) {
        repositoryid = getRepositoryName().substring(repositorynamespace + 1);
      }
      System.out.println("Getting Services from " + getServerURL() + " " + repositoryid);
      setSesameRepository(new HTTPRepository(getServerURL(), repositoryid));
      getSesameRepository().initialize();
    }
    catch (Exception e)
    {
      System.out.println("Impossible to initialize " + getServerURL() + " repository " + getRepositoryName());
      e.printStackTrace();
      throw e;
    }
  }
  
  public void initializeServer(String proxyHost, String proxyPort)
    throws Exception
  {
    System.getProperties().setProperty("http.proxyHost", proxyHost);
    System.getProperties().setProperty("http.proxyPort", proxyPort);
    initializeServer();
  }
  
  public void logIntoServer()
    throws Exception
  {
    System.out.println("ToDo");
  }
  
  public void loadPlugin(poweraqua.serviceConfig.Repository repository)
    throws Exception
  {
    loadConfiguration(repository.getServerURL(), repository.getRepositoryType(), repository.getRepositoryName(), repository.getLogin(), repository.getPassword());
    if ((repository.getProxy() == null) || (repository.getPort() == null)) {
      initializeServer();
    } else {
      initializeServer(repository.getProxy(), repository.getPort());
    }
    if (repository.getLogin() != null) {
      logIntoServer();
    }
  }
  
  public RDFEntityList getAllClasses()
    throws Exception
  {
    try
    {
      String serql = "";
      RDFEntityList aux = new RDFEntityList();
      if (isOWLRepository())
      {
        serql = "select distinct c,l from {c} rdf:type {owl:Class}; [rdfs:label {l}]  where isURI(c) ";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        aux = retrieveRDFEntityFrom("class");
        if (aux.isEmpty()) {
          System.out.println("getClasses: Weird owl .. ");
        }
      }
      serql = "select distinct c,l from {c} rdf:type {rdfs:Class}; [rdfs:label {l}]  where isURI(c)  and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> ";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      aux.addNewRDFEntities(retrieveRDFEntityFrom("class"));
      return aux;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getAllInstancesOfClassPeriodically(String class_uri, int offset, int limit)
    throws Exception
  {
    try
    {
      String serql = "select distinct i,l from {i} rdf:type {<" + class_uri + ">}; [rdfs:label {l}] where isURI(i) " + "limit " + limit + " offset " + offset;
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      return retrieveRDFEntityFrom("instance");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getAllClassesPeriodically(int offset, int limit)
    throws Exception
  {
    try
    {
      String serql = "";
      RDFEntityList aux = new RDFEntityList();
      if (isOWLRepository())
      {
        serql = "select distinct c,l from {c} rdf:type {owl:Class}; [rdfs:label {l}]  where isURI(c) limit " + limit + " offset " + offset;
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        aux = retrieveRDFEntityFrom("class");
      }
      serql = "select distinct c,l from {c} rdf:type {rdfs:Class}; [rdfs:label {l}]  where isURI(c)  and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> limit " + limit + " offset " + offset;
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      aux.addNewRDFEntities(retrieveRDFEntityFrom("class"));
      return aux;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getAllProperties()
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      String serql = "";
      if (isOWLRepository())
      {
        serql = " select p,o,r,l,ol,rl from {p} rdf:type {X}, [{p} rdfs:domain {o}, [{o} rdfs:label {ol}]], [{p} rdfs:range {r}, [{r} rdfs:label {rl}]], [{p} rdfs:label {l}] where  X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>  ";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
      }
      else
      {
        serql = " select p,o,r,l,ol,rl from {p} rdf:type {rdf:Property}, [{p} rdfs:range {r}, [{r} rdfs:label {rl}]], [{p} rdfs:domain {o}, [{o} rdfs:label {ol}]], [{p} rdfs:label {l}]";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
      }
      aux.addNewRDFEntities(retrieveFullPropertiesFrom());
      return aux;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getAllPropertiesPeriodically(int offset, int limit)
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      String serql = "";
      if (isOWLRepository())
      {
        serql = " select p,o,r,l,ol,rl from {p} rdf:type {X}, [{p} rdfs:domain {o}, [{o} rdfs:label {ol}]], [{p} rdfs:range {r}, [{r} rdfs:label {rl}]], [{p} rdfs:label {l}] where  X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> limit " + limit + " offset " + offset;
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        aux = retrieveFullPropertiesFrom();
      }
      else
      {
        serql = " select p,o,r,l,ol,rl from {p} rdf:type {rdf:Property}, [{p} rdfs:range {r}, [{r} rdfs:label {rl}]], [{p} rdfs:domain {o}, [{o} rdfs:label {ol}]], [{p} rdfs:label {l}] limit " + limit + " offset " + offset;
        
        MappingSession.serqlCalls += 1;
        execute(serql);
        aux = retrieveFullPropertiesFrom();
      }
      return aux;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public ArrayList<RDFPath> getSchemaIndirectRelations(String sourceURI, String targetURI)
    throws Exception
  {
    try
    {
      ArrayList<RDFPath> paths = new ArrayList();
      Logger log_poweraqua = Logger.getLogger("poweraqua");
      String serql;
      if (this.repositoryName.equals("bbc_backstage"))
      {
        log_poweraqua.log(Level.INFO, "TOO EXPENSIVE: Number of indirect paths between " + sourceURI + " and " + targetURI + " :" + paths.size());
      }
      else
      {
        serql = "select distinct property, proplabel, reference, reflabel, property2, prop2label, subject, originlabel, destiny, targetlabel from {instOrigin} rdf:type {subject}, {instRef} rdf:type {reference}, {instTarget} rdf:type {destiny}, {instOrigin} property {instRef}, {instRef} property2 {instTarget}, [{property} rdfs:label {proplabel}], [{reference} rdfs:label {reflabel}], [{subject} rdfs:label {originlabel}], [{destiny} rdfs:label {targetlabel}], [{property2} rdfs:label {prop2label}] where ((subject=<" + sourceURI + "> " + "and destiny=<" + targetURI + ">) or " + "(subject=<" + targetURI + "> " + "and destiny=<" + sourceURI + ">))" + " and not (instOrigin = instTarget)  and not (instOrigin = instRef) and not (instTarget = instRef) " + "and not reference =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> " + "and not reference = <http://www.w3.org/2000/01/rdf-schema#Resource> " + "and not reference = <http://www.w3.org/1999/02/22-rdf-syntax-ns#List>" + " and not property=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not property=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not property=<http://www.w3.org/2000/01/rdf-schema#member> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not property=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not property=<http://www.w3.org/2000/01/rdf-schema#comment> and not property=<http://www.w3.org/2000/01/rdf-schema#label> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>" + " and not property2=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not property2=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not property2=<http://www.w3.org/2000/01/rdf-schema#member> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not property2=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not property2=<http://www.w3.org/2000/01/rdf-schema#comment> and not property2=<http://www.w3.org/2000/01/rdf-schema#label> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        paths.addAll(retrieveFullPathFrom(this.result));
      }
      return paths;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public boolean isKBTripleClassClass(String class1_uri, String relation, String class2_uri)
    throws Exception
  {
    try
    {
      String serq = "SELECT distinct rel from {instorigin} rdf:type {class1}, {insttarget} rdf:type {class2}, {instorigin} rel {insttarget} where (class1 =<" + class1_uri + "> " + "and class2 =<" + class2_uri + "> " + "and rel =<" + relation + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq);
      String serq2;
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq2 = "SELECT distinct rel from {instorigin} rdf:type {class1}, {insttarget} rdf:type {class2},  {insttarget} rel {instorigin} where (class1 =<" + class1_uri + "> " + "and class2 =<" + class2_uri + "> " + "and rel =<" + relation + ">)";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq2);
      }
      if ((this.result == null) || (!this.result.hasNext())) {
        return 0;
      }
      return 1;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public boolean isKBTripleClassInstance(String class_uri, String relation, String instance2_uri)
    throws Exception
  {
    try
    {
      String serq = "SELECT distinct rel from {instorigin} rdf:type {classGeneric}, {instorigin} rel {instance2} where (classGeneric =<" + class_uri + "> " + "and instance2 =<" + instance2_uri + "> " + "and rel =<" + relation + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq);
      String serq2;
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq2 = "SELECT distinct rel from {instorigin} rdf:type {classGeneric}, {instance2} rel {instorigin} where (classGeneric =<" + class_uri + "> " + "and instance2 =<" + instance2_uri + "> " + "and rel =<" + relation + ">)";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq2);
      }
      if ((this.result == null) || (!this.result.hasNext())) {
        return 0;
      }
      return 1;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public boolean isKBTripleInstanceInstance(String instance1_uri, String relation, String instance2_uri)
    throws Exception
  {
    try
    {
      String serq = "SELECT distinct rel from {instance2} rel {instance1} where (instance1 =<" + instance1_uri + "> " + "and instance2 =<" + instance2_uri + "> " + "and rel =<" + relation + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq);
      String serq2;
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq2 = "SELECT distinct rel from {instance1} rel {instance2} where (instance1 =<" + instance1_uri + "> " + "and instance2 =<" + instance2_uri + "> " + "and rel =<" + relation + ">)";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq2);
      }
      if ((this.result == null) || (!this.result.hasNext())) {
        return 0;
      }
      return 1;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public ArrayList<RDFPath> getKBIndirectRelations(String class_sourceURI, String instance_targetURI)
    throws Exception
  {
    try
    {
      ArrayList<RDFPath> paths = new ArrayList();
      
      String serql = "select instOrigin from {instOrigin} rdf:type {classGeneric} where classGeneric=<" + class_sourceURI + "> limit 1 offset 0";
      
      execute(serql);
      if ((this.result == null) || (!this.result.hasNext())) {
        return paths;
      }
      serql = "select distinct property, reference, property2 from  {instorigin} rdf:type {classGeneric},   {referencekb} rdf:type {reference},  {property}  rdfs:range {reference},  {property2} rdfs:domain {reference}, {instorigin} property {referencekb}, {referencekb} property2 {instdestiny} where classGeneric=<" + class_sourceURI + "> " + "and instdestiny=<" + instance_targetURI + ">" + " UNION " + "select distinct property, reference, property2 from " + " {instorigin} rdf:type {classGeneric}, {referencekb} property {instorigin}, {referencekb} property2 {instdestiny}, " + "{property}  rdfs:domain {reference},  {property2} rdfs:domain {reference}, " + "{referencekb} rdf:type {reference} " + "where classGeneric=<" + class_sourceURI + "> " + "and instdestiny=<" + instance_targetURI + ">";
      
      execute(serql);
      paths.addAll(retrievePathFrom());
      for (Object i$ = paths.iterator(); ((Iterator)i$).hasNext();)
      {
        RDFPath path = (RDFPath)((Iterator)i$).next();
        path.getRDFProperty1().setDomain(getDomainOfProperty(path.getRDFProperty1().getURI()));
        path.getRDFProperty1().setRange(getRangeOfProperty(path.getRDFProperty1().getURI()));
        path.getRDFProperty2().setDomain(getDomainOfProperty(path.getRDFProperty2().getURI()));
        path.getRDFProperty2().setRange(getRangeOfProperty(path.getRDFProperty2().getURI()));
      }
      return paths;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public ArrayList<RDFPath> getInstanceIndirectRelations(String instance_sourceURI, String instance_targetURI)
    throws Exception
  {
    try
    {
      ArrayList<RDFPath> paths = new ArrayList();
      String serql = "select distinct property, referenceEnt, property2 from {instorigin} property {reference}, {reference} property2 {instdestiny}, {reference} rdf:type {referenceEnt}  where instorigin=<" + instance_sourceURI + "> " + "and instdestiny=<" + instance_targetURI + ">";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      paths.addAll(retrievePathFrom());
      
      serql = "select distinct property, referenceEnt, property2 from {reference} property {instorigin}, {reference} property2 {instdestiny}, {reference} rdf:type  {referenceEnt}  where instorigin=<" + instance_sourceURI + "> " + "and instdestiny=<" + instance_targetURI + "> " + "and not property2 = property";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      paths.addAll(retrievePathFrom());
      
      serql = "select distinct property, referenceEnt, property2 from {reference} property {instorigin}, {instdestiny} property2 {reference}, {reference} rdf:type {referenceEnt}  where instorigin=<" + instance_sourceURI + "> " + "and instdestiny=<" + instance_targetURI + ">";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      paths.addAll(retrievePathFrom());
      for (RDFPath path : paths)
      {
        path.getRDFProperty1().setDomain(getDomainOfProperty(path.getRDFProperty1().getURI()));
        path.getRDFProperty1().setDomain(getRangeOfProperty(path.getRDFProperty1().getURI()));
        path.getRDFProperty2().setDomain(getDomainOfProperty(path.getRDFProperty2().getURI()));
        path.getRDFProperty2().setDomain(getRangeOfProperty(path.getRDFProperty2().getURI()));
      }
      return paths;
    }
    finally
    {
      this.result.close();
    }
  }
  
  private RDFEntityList getAllSubClassesWithInference(String class_uri)
    throws Exception
  {
    try
    {
      RDFEntityList classes = new RDFEntityList();
      String serql;
      String serql;
      if (class_uri.startsWith("node")) {
        serql = "select distinct sc,l from {c} rdfs:subClassOf {\"" + class_uri + "\"}, [{sc} rdfs:label {l}] " + "where not sc = \"" + class_uri + "\"";
      } else {
        serql = "select distinct c,l from {c} rdfs:subClassOf {<" + class_uri + ">}, [{c} rdfs:label {l}] " + "where not c =<" + class_uri + ">";
      }
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      classes = retrieveRDFEntityFrom("class");
      if (classes == null) {
        classes = new RDFEntityList();
      }
      return classes;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getAllSubClasses(String class_uri)
    throws Exception
  {
    try
    {
      RDFEntityList directclasses = new RDFEntityList();
      RDFEntityList classes = new RDFEntityList();
      
      directclasses = getAllSubClassesWithInference(class_uri);
      RDFEntityList aux;
      while (!directclasses.isEmpty())
      {
        aux = new RDFEntityList();
        for (RDFEntity directclass : directclasses.getAllRDFEntities()) {
          if (!classes.isExactRDFEntityContained(directclass))
          {
            classes.addRDFEntity(directclass);
            aux.addNewRDFEntities(getAllSubClassesWithInference(directclass.getURI()));
          }
        }
        directclasses = aux;
      }
      return classes;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getDirectSubClasses(String class_uri)
    throws Exception
  {
    try
    {
      String serql = "select distinct c, l from {c} rdfs:subClassOf {<" + class_uri + ">}, [{c} rdfs:label {l}] " + "where isURI(c) and not c=<" + class_uri + "> " + "minus " + "select distinct c, l from {c} rdfs:subClassOf {c2} rdfs:subClassOf {<" + class_uri + ">} " + ", [{c} rdfs:label {l}] where isURI(c) and isURI(c2) " + "and not c2=<" + class_uri + ">  and not c=c2";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      RDFEntityList classes = retrieveRDFEntityFrom("class");
      if (classes == null) {
        classes = new RDFEntityList();
      }
      RDFEntityList c2;
      if (isOWLRepository())
      {
        c2 = getSubClassesFromIntersectionDefinition(class_uri);
        if (classes == null) {
          return c2;
        }
        classes.addNewRDFEntities(c2);
      }
      return classes;
    }
    finally
    {
      this.result.close();
    }
  }
  
  private RDFEntityList getAllSuperClassesWithInference(String class_uri)
    throws Exception
  {
    try
    {
      String serql;
      String serql;
      if ((class_uri.startsWith("node")) || (class_uri.startsWith("_:node"))) {
        serql = "select distinct sc,l from {\"" + class_uri + "\"} rdfs:subClassOf {sc}, [{sc} rdfs:label {l}] " + "where not sc = \"" + class_uri + "\"";
      } else {
        serql = "select distinct sc,l from {<" + class_uri + ">} rdfs:subClassOf {sc}, [{sc} rdfs:label {l}] " + "where not sc =<" + class_uri + ">";
      }
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      RDFEntityList classes = retrieveRDFEntityFrom("class");
      if (classes == null) {
        classes = new RDFEntityList();
      }
      return classes;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getAllSuperClasses(String class_uri)
    throws Exception
  {
    try
    {
      RDFEntityList directclasses = new RDFEntityList();
      RDFEntityList classes = new RDFEntityList();
      
      directclasses = getAllSuperClassesWithInference(class_uri);
      RDFEntityList aux;
      while (!directclasses.isEmpty())
      {
        aux = new RDFEntityList();
        for (RDFEntity directclass : directclasses.getAllRDFEntities()) {
          if (!classes.isExactRDFEntityContained(directclass))
          {
            classes.addRDFEntity(directclass);
            aux.addNewRDFEntities(getAllSuperClassesWithInference(directclass.getURI()));
          }
        }
        directclasses = aux;
      }
      return classes;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getDirectSuperClasses(String class_uri)
    throws Exception
  {
    try
    {
      RDFEntityList classes = new RDFEntityList();
      if ((!class_uri.startsWith("node")) && (!class_uri.startsWith("_:node")))
      {
        String serql = "select distinct c, l from {<" + class_uri + ">} rdfs:subClassOf {c}, " + "[{c} rdfs:label {l}] where isURI(c) and not c=<" + class_uri + "> " + "minus select distinct c, l from {<" + class_uri + ">} rdfs:subClassOf {c2} rdfs:subClassOf {c}, " + "[{c} rdfs:label {l}] where isURI(c) and isURI(c2) " + "and not c2=<" + class_uri + ">  and not c=c2";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        classes = retrieveRDFEntityFrom("class");
      }
      if (classes == null) {
        classes = new RDFEntityList();
      }
      return classes;
    }
    finally
    {
      this.result.close();
    }
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
      String serql;
      if (isOWLRepository())
      {
        String serql = "select distinct rel, rellab from {instance} rel {value}, {rel} rdf:type {X}, [{rel} rdfs:label {rellab}] where instance=<" + instance_uri + "> " + "and not rel =<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel =<http://www.w3.org/2000/01/rdf-schema#label> " + "and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        slots = retrieveBasicPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String serql2 = "select distinct rel, rellab from {value} rel {instance}, {rel} rdf:type {X}, [{rel} rdfs:label {rellab}] where instance=<" + instance_uri + "> " + "and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) ";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql2);
        RDFEntityList slots2 = retrieveBasicPropertiesFrom();
        slots.addAllRDFEntity(slots2);
      }
      else
      {
        serql = "select distinct rel, rellab from {instance} rel {value}, {rel} rdf:type {rdf:Property}, [{rel} rdfs:label {rellab}] where instance=<" + instance_uri + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        slots = retrieveBasicPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String serql2 = "select distinct rel, rellab from {value} rel {instance}, {rel} rdf:type {rdf:Property}, [{rel} rdfs:label {rellab}] where instance=<" + instance_uri + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql2);
        RDFEntityList slots2 = retrieveBasicPropertiesFrom();
        slots.addAllRDFEntity(slots2);
      }
      return slots;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getDirectPropertiesOfClass(String class1)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      String serql;
      if (isOWLRepository())
      {
        String serql = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {rel} rdf:type {X}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where origin  =<" + class1 + "> " + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        slots = retrieveFullPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {rel} rdf:type {X}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}] where rango=<" + class1 + "> " + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql2);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
      }
      else
      {
        serql = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where origin  =<" + class1 + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        slots = retrieveFullPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}] where rango=<" + class1 + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql2);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
      }
      return slots;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getAllClassesOfInstance(String instance_uri)
    throws Exception
  {
    try
    {
      String serql = "select distinct c,l from {<" + instance_uri + ">} rdf:type {c};[rdfs:label {l}]";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      return retrieveRDFEntityFrom("class");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public ArrayList<String> getAllClassesNamesOfInstance(String instance_uri)
    throws Exception
  {
    try
    {
      String serql = "select distinct c from {<" + instance_uri + ">} rdf:type {c}";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      return retrieveEntitiesUriFrom(false);
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getDirectClassOfInstance(String instance_uri)
    throws Exception
  {
    try
    {
      String serql = "select distinct c, l from {<" + instance_uri + ">} rdf:type {c} rdfs:subClassOf {j}, [{c} rdfs:label {l}] " + "minus select distinct c, l from {<" + instance_uri + ">} rdf:type {k} rdfs:subClassOf {c}, [{c} rdfs:label {l}] where k!=c ";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      RDFEntityList classes = retrieveRDFEntityFrom("class");
      RDFEntityList localRDFEntityList1;
      if ((classes == null) || (classes.isEmpty())) {
        return new RDFEntityList();
      }
      if (classes.size() == 1) {
        return classes;
      }
      return classes;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getAllInstances()
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      if (isOWLRepository())
      {
        String serql = "select distinct p, l, title from {c} rdf:type {owl:Class},  {p} rdf:type {c},  [{p}  <http://purl.org/dc/elements/1.1/title> {title}], [{p} rdfs:label {l}]";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        aux = retrieveRDFEntityForIndex(this.result, "instance");
      }
      else
      {
        String serql = "select distinct i, l from {i} rdf:type {c}, {c} rdf:type {rdfs:Class}, [{i} rdfs:label {l}] where isURI(i)  and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo>  minus select i from {i} rdf:type {rdfs:Class} where isURI(i)   and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo>  union select i from {i} rdf:type {rdf:Property} where isURI(i)   and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> ";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        aux = retrieveRDFEntityForIndex(this.result, "instance");
      }
      return aux;
    }
    finally
    {
      this.result.close();
    }
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
          String serql = "select distinct p, l, title from {c} rdf:type {owl:Class},  {p} rdf:type {c}, [{p}  <http://purl.org/dc/elements/1.1/title> {title}] , [{p} rdfs:label {l}] limit " + limit + " offset " + offset;
          
          MappingSession.serqlCalls += 1;
          execute(serql);
          aux = retrieveRDFEntityForIndex(this.result, "instance");
        }
        String serql = " select distinct i, l, title from {i} rdf:type {c}, {c} rdf:type {rdfs:Class}, [{i}  <http://purl.org/dc/elements/1.1/title> {title}], [{i} rdfs:label {l}] where isURI(i)  and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> and not i=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>limit " + limit + " offset " + offset;
        
        MappingSession.serqlCalls += 1;
        execute(serql);
        aux.addRDFEntities(retrieveRDFEntityForIndex(this.result, "instance"));
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      return aux;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getAllInstancesOfClass(String class_uri, int limit)
    throws Exception
  {
    try
    {
      String serql;
      String serql;
      if (limit > 0) {
        serql = "select distinct i,l from {i} rdf:type {<" + class_uri + ">}; [rdfs:label {l}] where isURI(i)";
      } else {
        serql = "select distinct i,l from {i} rdf:type {<" + class_uri + ">}; [rdfs:label {l}] where isURI(i) limit " + limit;
      }
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      return retrieveRDFEntityFrom("instance");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public boolean isInstanceOf(String instance_uri, String class_uri)
    throws Exception
  {
    try
    {
      String serql = "select distinct i from {i} rdf:type {<" + class_uri + ">} " + "where i =<" + instance_uri + "> ";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      boolean bool;
      if ((this.result == null) || (!this.result.hasNext())) {
        return false;
      }
      return true;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public boolean isNameOfInstance(String instance_uri, String literal)
    throws Exception
  {
    try
    {
      String serql;
      String serql;
      if (literal.contains("\"")) {
        serql = "select distinct i from {i} property {literal},   {property} rdfs:label {name} where i =<" + instance_uri + "> and literal = " + literal + " and name = \"name\"";
      } else {
        serql = "select distinct i from {i} property {literal},   {property} rdfs:label {name} where i =<" + instance_uri + "> and literal = \"" + literal + "\" and name = \"name\"";
      }
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      boolean bool;
      if ((this.result == null) || (!this.result.hasNext())) {
        return false;
      }
      return true;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getSlotValue(String instance_uri, String property_uri)
    throws Exception
  {
    try
    {
      boolean limitsize = false;
      RDFEntityList results = new RDFEntityList();
      String serql = "select distinct v from {<" + instance_uri + ">} <" + property_uri + "> {v} where isLiteral(v)";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      results = retrieveRDFEntityLiteralsFrom(instance_uri, limitsize);
      RDFEntityList localRDFEntityList1;
      if (!results.isEmpty()) {
        return results;
      }
      serql = "select distinct v, lv from {<" + instance_uri + ">} <" + property_uri + "> {v}, [{v} rdfs:label {lv}]";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      results = retrieveRDFEntityFrom("instance");
      
      return results;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getInstancesWithSlotValue(String property_uri, String slot_value, boolean isValueLiteral)
    throws Exception
  {
    try
    {
      String serql = "";
      RDFEntityList localRDFEntityList;
      if (isValueLiteral)
      {
        serql = "select distinct i, lb from {i} <" + property_uri + "> {v} , [{i} rdfs:label {lb}] " + "where isLiteral(v) and v = \"" + slot_value + "\" ";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        return retrieveRDFEntityFrom("instance");
      }
      serql = "select distinct i, lb from {i} <" + property_uri + "> {<" + slot_value + ">} , [{i} rdfs:label {lb}]";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      return retrieveRDFEntityFrom("instance");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getLiteralValuesOfInstance(String instance_uri)
    throws Exception
  {
    try
    {
      String serql = "";
      serql = "select distinct v from {i} p {v} where isLiteral(v) and i = <" + instance_uri + "> ";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      
      execute(serql);
      boolean limitsize = true;
      return retrieveRDFEntityLiteralsFrom(instance_uri, limitsize);
    }
    finally
    {
      this.result.close();
    }
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
  
  public OcmlInstance getInstanceInfo(String instance_uri)
    throws Exception
  {
    try
    {
      RDFEntity entityInstance = new RDFEntity("instance", instance_uri, getLabelOfEntity(instance_uri), getPluginID());
      
      OcmlInstance ocmlInstance = new OcmlInstance(entityInstance);
      
      RDFEntityList directClasses = getDirectClassOfInstance(instance_uri);
      ocmlInstance.addDirectSuperClasses(directClasses);
      for (RDFEntity directClass : directClasses.getAllRDFEntities()) {
        ocmlInstance.addSuperClasses(getAllSuperClasses(directClass.getURI()));
      }
      ocmlInstance.setEquivalentInstances(getEquivalentEntitiesForInstance(instance_uri));
      
      Hashtable<RDFEntity, RDFEntityList> propertiesTable = new Hashtable();
      
      String serql = "select distinct p, v, pl, vl from {<" + instance_uri + ">} p {v} " + ", [{p} rdfs:label {pl}], [{v} rdfs:label {vl}] " + "where not p=<" + "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "type>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      if ((this.result == null) || (!this.result.hasNext())) {
        return ocmlInstance;
      }
      Object bindingNames = this.result.getBindingNames();
      BindingSet bindingSet;
      while (this.result.hasNext())
      {
        bindingSet = (BindingSet)this.result.next();
        String property = bindingSet.getValue((String)((List)bindingNames).get(0)).toString();
        String value = bindingSet.getValue((String)((List)bindingNames).get(1)).toString();
        
        Value v_propertyLabel = bindingSet.getValue((String)((List)bindingNames).get(2));
        String propertyLabel = v_propertyLabel == null ? null : v_propertyLabel.toString().trim();
        
        Value v_valueLabel = bindingSet.getValue((String)((List)bindingNames).get(3));
        String valueLabel = v_valueLabel == null ? null : v_valueLabel.toString().trim();
        
        RDFEntity entityProperty = new RDFEntity("property", property, propertyLabel, getPluginID());
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
      return ocmlInstance;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public OcmlProperty getPropertyInfo(String property_uri)
    throws Exception
  {
    RDFEntity entityClass = new RDFEntity("property", property_uri, getLabelOfEntity(property_uri), getPluginID());
    
    OcmlProperty ocmlProperty = new OcmlProperty(entityClass);
    
    ocmlProperty.setEquivalentProperties(getEquivalentEntitiesForClass(property_uri));
    
    RDFEntityList list = new RDFEntityList();
    list.addAllRDFEntity(getDomainOfProperty(property_uri));
    ocmlProperty.setDomain(list);
    list = new RDFEntityList();
    list.addAllRDFEntity(getRangeOfProperty(property_uri));
    ocmlProperty.setRange(list);
    
    return ocmlProperty;
  }
  
  public boolean isSubClassOf(String class1, String class2)
    throws Exception
  {
    try
    {
      String serql = "select c from {c} rdfs:subClassOf {<" + class2 + ">} " + "where c = <" + class1 + "> ";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      ArrayList<String> uris = retrieveEntitiesUriFrom(false);
      boolean bool;
      if (uris != null) {
        return true;
      }
      return false;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getGenericInstances(String class_uri, String slot, String value_uri)
    throws Exception
  {
    try
    {
      String serql;
      String serql;
      if ((class_uri.startsWith("node")) || (class_uri.startsWith("_:node"))) {
        serql = "select distinct instances, i_label from {instances} rdf:type {subject}, {instances} <" + slot + "> {value}, [{instances} rdfs:label {i_label}] " + "where value=<" + value_uri + "> and subject=  \"" + class_uri + "\"";
      } else {
        serql = "select distinct instances, i_label from {instances} rdf:type {subject}, {instances} <" + slot + "> {value}, [{instances} rdfs:label {i_label}] " + "where value=<" + value_uri + "> and subject=<" + class_uri + "> ";
      }
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      RDFEntityList lista1 = retrieveRDFValueFrom(this.result, "instance");
      if ((class_uri.startsWith("node")) || (class_uri.startsWith("_:node"))) {
        serql = "select distinct instances, i_label from {instances} rdf:type {subject}, {value} <" + slot + "> {instances}, [{instances} rdfs:label {i_label}] " + "where value=<" + value_uri + "> and subject= \"" + class_uri + "\"";
      } else {
        serql = "select distinct instances, i_label from {instances} rdf:type {subject}, {value} <" + slot + "> {instances}, [{instances} rdfs:label {i_label}] " + "where value=<" + value_uri + "> and subject=<" + class_uri + ">";
      }
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      RDFEntityList lista2 = retrieveRDFValueFrom(this.result, "instance");
      lista1.addAllRDFEntity(lista2);
      return lista1;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getGenericInstancesForLiteral(String class_uri, String slot, String value)
    throws Exception
  {
    try
    {
      String serql = "select distinct instances, i_label from {instances} rdf:type {subject}, {instances} <" + slot + "> {value}, [{instances} rdfs:label {i_label}] " + "where isLiteral (value) and value= \"" + value + "\" and subject=<" + class_uri + ">";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      return retrieveRDFValueFrom(this.result, "instance");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getTripleInstances(String query_class, String slot, String class2)
    throws Exception
  {
    try
    {
      String serq = "select distinct inst1, inst1label, inst2, inst2label from {inst1} rdf:type {class1},  {inst2} rdf:type {class2}, {inst1} rel {inst2}, [{inst1} rdfs:label {inst1label}], [{inst2} rdfs:label {inst2label}] where (class1 =<" + query_class + "> " + "and class2 =<" + class2 + "> " + "and rel = <" + slot + "> )";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq);
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq = "select distinct inst1, inst1label, inst2, inst2label from {inst1} rdf:type {class1},  {inst2} rdf:type {class2}, {inst2} rel {inst1}, [{inst1} rdfs:label {inst1label}], [{inst2} rdfs:label {inst2label}] where (class1 = <" + query_class + "> " + "and class2 = <" + class2 + "> " + "and rel = <" + slot + "> )";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq);
      }
      return retrieveRDFTripleValueFrom("instance");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getTripleInstances(String query_class, String prop1, String ref_class, String prop2, String instance)
    throws Exception
  {
    try
    {
      String serq = "select distinct inst, inst1label, ref_inst, inst2label from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {inst} prop1 {ref_inst}, {ref_inst} prop2 {instance}, [{inst} rdfs:label {inst1label}], [{ref_inst} rdfs:label {inst2label}] where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = <" + instance + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq);
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq = "select distinct inst, inst1label, ref_inst, inst2label from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {ref_inst} prop2 {instance}, [{inst} rdfs:label {inst1label}], [{ref_inst} rdfs:label {inst2label}] where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = <" + instance + ">)";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq);
      }
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq = "select distinct inst, inst1label, ref_inst, inst2label from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {instance} prop2 {ref_inst}, [{inst} rdfs:label {inst1label}], [{ref_inst} rdfs:label {inst2label}] where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = <" + instance + ">)";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq);
      }
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq = "select distinct inst, inst1label, ref_inst, inst2label from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {inst} prop1 {ref_inst}, {instance} prop2 {ref_inst}, [{inst} rdfs:label {inst1label}], [{ref_inst} rdfs:label {inst2label}] where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = <" + instance + ">)";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq);
      }
      return retrieveRDFTripleValueFrom("instance");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getTripleInstancesFromLiteral(String query_class, String prop1, String ref_class, String prop2, String instance)
    throws Exception
  {
    try
    {
      String serq = "select distinct inst, inst1label, ref_inst, inst2label from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {inst} prop1 {ref_inst}, {ref_inst} prop2 {instance}, [{inst} rdfs:label {inst1label}], [{ref_inst} rdfs:label {inst2label}] where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = \"" + instance + "\")";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq);
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq = "select distinct inst, inst1label, ref_inst, inst2label from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {ref_inst} prop2 {instance}, [{inst} rdfs:label {inst1label}], [{ref_inst} rdfs:label {inst2label}] where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = \"" + instance + "\")";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq);
      }
      return retrieveRDFTripleValueFrom("instance");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getTripleInstancesFromClasses(String query_class, String prop1, String ref_class, String prop2, String class2)
    throws Exception
  {
    try
    {
      String serq = "select distinct inst, inst1label, instance, inst2label from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {inst} prop1 {ref_inst}, {ref_inst} prop2 {instance}, {instance} rdf:type {class2}, [{inst} rdfs:label {inst1label}], [{instance} rdfs:label {inst2label}] where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and class2 = <" + class2 + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq);
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq = "select distinct inst, inst1label, instance, inst2label from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {ref_inst} prop2 {instance},  {instance} rdf:type {class2}, [{inst} rdfs:label {inst1label}], [{instance} rdfs:label {inst2label}] where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and class2 = <" + class2 + ">)";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq);
      }
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq = "select distinct inst, inst1label, instance, inst2label  from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {instance} prop2 {ref_inst},  {instance} rdf:type {class2} [{inst} rdfs:label {inst1label}], [{instance} rdfs:label {inst2label}] where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and class2 = <" + class2 + ">)";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq);
      }
      if ((this.result == null) || (!this.result.hasNext()))
      {
        serq = "select distinct inst, inst1label, instance, inst2label  from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {inst} prop1 {ref_inst}, {instance} prop2 {ref_inst},  {instance} rdf:type {class2} [{inst} rdfs:label {inst1label}], [{instance} rdfs:label {inst2label}] where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and class2 = <" + class2 + ">)";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq);
      }
      return retrieveRDFTripleValueFrom("instance");
    }
    finally
    {
      this.result.close();
    }
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
        String preSerq2 = "select distinct instorigin, insttarget from {instorigin} rdf:type {class1}, {insttarget} rdf:type {class2}, {instorigin} rel {insttarget}  where ((rel =<" + prop.getURI() + ">) and " + " ((class1 =<" + class1_uri + "> and class2 =<" + class2_uri + ">) or " + "(class2 =<" + class1_uri + "> and class1 =<" + class2_uri + ">))) " + "limit 1 offset 0";
        
        execute(preSerq2);
        if ((this.result != null) && (this.result.hasNext())) {
          slots.addRDFEntity(prop);
        }
      }
      return slots;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getPropertiesBetweenClasses(String class1_uri, String class2_uri)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      String serql;
      if (isOWLRepository())
      {
        String serql = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {rel} rdf:type {X}, [{term1} rdfs:subClassOf {origin}], [{term2} rdfs:subClassOf {rango}], [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where (term2 =<" + class1_uri + "> or rango  =<" + class1_uri + ">) and " + " (term1=<" + class2_uri + "> or origin  =<" + class2_uri + ">) and " + " (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        slots = retrieveFullPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {rel} rdf:type {X},[{term1} rdfs:subClassOf {origin}], [{term2} rdfs:subClassOf {rango}], [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where (term2 =<" + class2_uri + "> or rango  =<" + class2_uri + ">) and " + " (term1=<" + class1_uri + "> or origin  =<" + class1_uri + ">) and " + "  (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql2);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
      }
      else
      {
        serql = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin},  {term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where term2 =<" + class1_uri + "> and term1=<" + class2_uri + ">" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        slots = retrieveFullPropertiesFrom();
        if (slots == null) {
          slots = new RDFEntityList();
        }
        String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}] where term1=<" + class1_uri + "> and term2= <" + class2_uri + ">" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql2);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
      }
      return slots;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getPropertiesForGenericClass(String class_uri)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      String serq3;
      if (isOWLRepository())
      {
        String serq = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {X}, {origin} rdfs:subClassOf {genericterm}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where genericterm =<" + class_uri + ">" + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {X}," + "{genericterm} rdfs:subClassOf {origin}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where genericterm =<" + class_uri + "> " + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
        
        String serq3 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {X}, {rango} rdfs:subClassOf {genericterm}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where genericterm =<" + class_uri + ">" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, " + "{genericterm} rdfs:subClassOf {rango}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where genericterm =<" + class_uri + "> " + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq3);
        RDFEntityList slots3 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots3);
      }
      else
      {
        String serq = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {origin} rdfs:subClassOf {genericterm}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where genericterm =<" + class_uri + ">" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, " + "{genericterm} rdfs:subClassOf {origin}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where genericterm =<" + class_uri + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq);
        RDFEntityList slots2 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots2);
        
        serq3 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {origin}, {rel} rdfs:range {rango},  {rango} rdfs:subClassOf {genericterm}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where genericterm =<" + class_uri + ">" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, " + "{genericterm} rdfs:subClassOf {rango}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where genericterm =<" + class_uri + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
        
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serq3);
        RDFEntityList slots3 = retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots3);
      }
      String serq4 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdf:type {X}, {genericterm} rel {instance}, [{rel} rdfs:label {rellab}], [{rel} rdfs:domain {origin}, [{origin} rdfs:label {originlab}]], [{rel} rdfs:range {rango}, [{rango} rdfs:label {rangolab}]]  where genericterm =<" + class_uri + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq4);
      RDFEntityList slots4 = retrieveFullPropertiesFrom();
      slots.addAllRDFEntity(slots4);
      return slots;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getAllPropertiesBetweenClass_Literal(String classURI, String slot_value)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      String serq1;
      String serq1;
      if (slot_value.contains("\"")) {
        serq1 = "SELECT distinct p, rellab from {instance} p {v}, {p} rdfs:range {String}, {instance} rdf:type {origin}, [{p} rdfs:label {rellab}] where isLiteral(v) and v = " + slot_value + " and origin =<" + classURI + "> " + " and String=<http://www.w3.org/2001/XMLSchema#String> " + " and  ( lang(rellab) =  \"en\" or  lang(rellab) =  \"\" ) ";
      } else {
        serq1 = "SELECT distinct p, rellab from {instance} p {v}, {p} rdfs:range {String}, {instance} rdf:type {origin}, [{p} rdfs:label {rellab}] where isLiteral(v) and v = \"" + slot_value + "\" and origin =<" + classURI + "> " + " and String=<http://www.w3.org/2001/XMLSchema#String> " + " and  ( lang(rellab) =  \"en\" or  lang(rellab) =  \"\" ) ";
      }
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq1);
      
      slots = retrieveRDFEntityFrom("property");
      
      return slots;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getKBPropertiesForGenericClass(String classGeneric_uri, String instance2_uri)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      String serq;
      String serq;
      if (classGeneric_uri.startsWith("node")) {
        serq = "SELECT distinct rel, rellab from {instorigin} rdf:type {classGeneric}, {instorigin} rel {instance2}, [{rel} rdfs:label {rellab}] where (classGeneric =\"" + classGeneric_uri + "\"" + "and instance2 =<" + instance2_uri + ">)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      } else {
        serq = "SELECT distinct rel, rellab from {instorigin} rdf:type {classGeneric}, {instorigin} rel {instance2}, [{rel} rdfs:label {rellab}] where (classGeneric =<" + classGeneric_uri + "> " + "and instance2 =<" + instance2_uri + ">)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      }
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq);
      slots = retrieveBasicPropertiesFrom();
      String serq2;
      String serq2;
      if (classGeneric_uri.startsWith("node")) {
        serq2 = "SELECT distinct rel, rellab from {instorigin} rdf:type {classGeneric}, {instance2} rel {instorigin}, [{rel} rdfs:label {rellab}] where (classGeneric =\"" + classGeneric_uri + "\"" + " and instance2 =<" + instance2_uri + ">)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      } else {
        serq2 = "SELECT distinct rel, rellab from {instorigin} rdf:type {classGeneric}, {instance2} rel {instorigin}, [{rel} rdfs:label {rellab}] where (classGeneric =<" + classGeneric_uri + "> " + "and instance2 =<" + instance2_uri + ">)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      }
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq2);
      
      slots.addNewRDFEntities(retrieveBasicPropertiesFrom());
      return slots;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public ArrayList<RDFPath> getKBIndirectRelationsWithLiterals(String class_URI, String literal)
    throws Exception
  {
    try
    {
      ArrayList<RDFPath> paths = new ArrayList();
      String serql;
      if (this.repositoryName.equals("bbc_backstage"))
      {
        System.out.println("Looking for indirect relations with a literal is too expensive");
      }
      else
      {
        String serql;
        if (literal.contains("\"")) {
          serql = "select distinct property, reference, property2 from {instance} property {refInst}, {refInst} property2 {v}, {property2} rdfs:range {String}, {instance} rdf:type {origin}, {refInst} rdf:type {reference}, {property} rdfs:range {reference}, {property2} rdfs:domain {reference}, {property} rdfs:domain {origin} where isLiteral(v) and String=<http://www.w3.org/2001/XMLSchema#String> and v = " + literal + " and origin =<" + class_URI + ">";
        } else {
          serql = "select distinct property, reference, property2 from {instance} property {refInst}, {refInst} property2 {v}, {property2} rdfs:range {String}, {instance} rdf:type {origin}, {refInst} rdf:type {reference}, {property} rdfs:range {reference}, {property2} rdfs:domain {reference}, {property} rdfs:domain {origin} where isLiteral(v) and String=<http://www.w3.org/2001/XMLSchema#String> and v = \"" + literal + "\" and origin =<" + class_URI + ">";
        }
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        execute(serql);
        paths.addAll(retrievePathFrom());
        for (RDFPath path : paths)
        {
          path.getRDFProperty1().setDomain(getDomainOfProperty(path.getRDFProperty1().getURI()));
          path.getRDFProperty1().setRange(getRangeOfProperty(path.getRDFProperty1().getURI()));
          path.getRDFProperty2().setDomain(getDomainOfProperty(path.getRDFProperty2().getURI()));
          path.getRDFProperty2().setRange(getRangeOfProperty(path.getRDFProperty2().getURI()));
        }
      }
      return paths;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getInstanceProperties(String instance1_uri, String instance2_uri)
    throws Exception
  {
    try
    {
      RDFEntityList slots = new RDFEntityList();
      String serq = "SELECT distinct rel, rellab from {instance2} rel {instance1}, [{rel} rdfs:label {rellab}] where (instance1 =<" + instance1_uri + "> " + "and instance2 =<" + instance2_uri + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq);
      slots = retrieveBasicPropertiesFrom();
      
      String serq2 = "SELECT distinct rel, rellab from {instance1} rel {instance2}, [{rel} rdfs:label {rellab}] where (instance1 =<" + instance1_uri + "> " + "and instance2 =<" + instance2_uri + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serq2);
      slots.addAllRDFEntity(retrieveBasicPropertiesFrom());
      return slots;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public String getFirstEnLabel(String entity_uri)
    throws Exception
  {
    TupleQueryResult result2 = null;
    try
    {
      String serql;
      String serql;
      if (entity_uri.startsWith("node")) {
        serql = "select label(l) from {" + entity_uri + "} rdfs:label {l} " + "limit 1 ";
      } else {
        serql = "select label(l) from {<" + entity_uri + ">} rdfs:label {l} " + "limit 1 ";
      }
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      result2 = executeLocal(serql);
      if (!result2.hasNext()) {
        return null;
      }
      Object bindingNames = result2.getBindingNames();
      BindingSet bindingSet = (BindingSet)result2.next();
      Value v = bindingSet.getValue((String)((List)bindingNames).get(0));
      String str2;
      if (v == null) {
        return null;
      }
      return v.toString();
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
      String serql = "select label(l) from {<" + entity_uri + ">} rdfs:label {l}";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      if (!this.result.hasNext()) {
        return null;
      }
      Object bindingNames = this.result.getBindingNames();
      BindingSet bindingSet = (BindingSet)this.result.next();
      Value v = bindingSet.getValue((String)((List)bindingNames).get(0));
      String str2;
      if (v == null) {
        return null;
      }
      return v.toString();
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getUnionDefinitionForBlankNode(String node)
    throws Exception
  {
    try
    {
      RDFEntityList entList = new RDFEntityList();
      node = node.replaceFirst("_:", ":");
      String serql = "select c1, l1, c2, l2 from {class} <http://www.w3.org/2002/07/owl#unionOf> {x}, {x} rdf:first {c1}, [{c1} rdfs:label {l1}], {x} rdf:rest {} rdf:first {c2}, [{c2} rdfs:label {l2}] where class = \"" + node + "\"";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      RDFEntityList localRDFEntityList1;
      if (this.result == null) {
        return entList;
      }
      if (!this.result.hasNext()) {
        return entList;
      }
      Object bindingNames = this.result.getBindingNames();
      BindingSet bindingSet;
      while (this.result.hasNext())
      {
        bindingSet = (BindingSet)this.result.next();
        Value ent1 = bindingSet.getValue((String)((List)bindingNames).get(0));
        Value labelent1 = bindingSet.getValue((String)((List)bindingNames).get(1));
        Value ent2 = bindingSet.getValue((String)((List)bindingNames).get(2));
        Value labelent2 = bindingSet.getValue((String)((List)bindingNames).get(3));
        if ((ent1 != null) && (ent1.toString().trim().length() > 0)) {
          entList.addRDFEntity(new RDFEntity("class", ent1.toString().trim(), labelent1 == null ? null : labelent1.toString().trim(), getPluginID()));
        }
        if ((ent2 != null) && (ent2.toString().trim().length() > 0)) {
          entList.addRDFEntity(new RDFEntity("class", ent2.toString().trim(), labelent2 == null ? null : labelent2.toString().trim(), getPluginID()));
        }
      }
      return entList;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getDomainOfProperty(String entity_uri)
    throws Exception
  {
    try
    {
      RDFEntityList domainList = new RDFEntityList();
      String serql = "select distinct d, l from {<" + entity_uri + ">} rdfs:domain {d}, [{d} rdfs:label {l}]";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      if (this.result == null) {
        return domainList;
      }
      Object bindingNames = this.result.getBindingNames();
      BindingSet bindingSet;
      while (this.result.hasNext())
      {
        bindingSet = (BindingSet)this.result.next();
        Value domain = bindingSet.getValue((String)((List)bindingNames).get(0));
        Value labelDomain = bindingSet.getValue((String)((List)bindingNames).get(1));
        if ((domain != null) && (domain.toString().trim().length() > 0)) {
          if (domain.toString().startsWith("node"))
          {
            System.out.println("reading blank node " + domain.toString());
            domainList.addNewRDFEntities(getUnionDefinitionForBlankNode(domain.toString()));
          }
          else
          {
            domainList.addRDFEntity(new RDFEntity("class", domain.toString().trim(), labelDomain == null ? null : labelDomain.toString().trim(), getPluginID()));
          }
        }
      }
      return domainList;
    }
    finally
    {
      this.result.close();
    }
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
      
      String serql = "select distinct rt, l from {<" + property_uri + ">} rdfs:range {rt}, [{rt} rdfs:label {l}]";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      if (this.result == null) {
        return rangeList;
      }
      Object bindingNames = this.result.getBindingNames();
      BindingSet bindingSet;
      while (this.result.hasNext())
      {
        bindingSet = (BindingSet)this.result.next();
        Value range = bindingSet.getValue((String)((List)bindingNames).get(0));
        Value labelRange = bindingSet.getValue((String)((List)bindingNames).get(1));
        if ((range != null) && (MyURI.isURIValid(range.toString()))) {
          if (range.toString().startsWith("http://www.w3.org/2001/XMLSchema#"))
          {
            rangeList.addRDFEntity(new RDFEntity("datatype", range.toString().trim(), labelRange == null ? null : labelRange.toString().trim(), getPluginID()));
          }
          else if (range.toString().startsWith("_:node"))
          {
            System.out.println("reading blank node " + range.toString());
            rangeList.addNewRDFEntities(getUnionDefinitionForBlankNode(range.toString()));
          }
          else
          {
            rangeList.addRDFEntity(new RDFEntity("class", range.toString().trim(), labelRange == null ? null : labelRange.toString().trim(), getPluginID()));
          }
        }
      }
      return rangeList;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getEquivalentEntitiesForClass(String entity_uri)
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      
      String serql = " select distinct x, xl from {x} owl:equivalentClass {<" + entity_uri + ">}; [rdfs:label {xl}] ";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      aux = retrieveRDFEntityFrom("instance");
      
      serql = serql = " select distinct x, xl from {<" + entity_uri + ">} owl:equivalentClass {x}, [{x} rdfs:label {xl}] ";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      aux.addNewRDFEntities(retrieveRDFEntityFrom("instance"));
      return aux;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForClasses()
    throws Exception
  {
    try
    {
      String serql = " select distinct ent, entl, eq, eql from {ent} owl:equivalentClass {eq}, [{ent} rdfs:label {entl}], [{eq} rdfs:label {eql}] ";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      return retrieveEquivalentEntityFrom(this.result, "class");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForProperties()
    throws Exception
  {
    try
    {
      String serql = " select distinct ent, entl, eq, eql from {ent} owl:equivalentProperty {eq}, [{ent} rdfs:label {entl}], [{eq} rdfs:label {eql}] ";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      return retrieveEquivalentEntityFrom(this.result, "property");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForInstances()
    throws Exception
  {
    try
    {
      String serql = " select distinct ent, entl, eq, eql from {ent} owl:sameAs {eq}, [{ent} rdfs:label {entl}], [{eq} rdfs:label {eql}] ";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      return retrieveEquivalentEntityFrom(this.result, "instance");
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getEquivalentEntitiesForProperty(String entity_uri)
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      
      String serql = " select distinct x, xl from {x} owl:equivalentProperty {<" + entity_uri + ">}; [rdfs:label {xl}] ";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      aux = retrieveRDFEntityFrom("instance");
      
      serql = serql = " select distinct x, xl from {<" + entity_uri + ">} owl:equivalentProperty {x}, [{x} rdfs:label {xl}] ";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      aux.addNewRDFEntities(retrieveRDFEntityFrom("instance"));
      return aux;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public RDFEntityList getEquivalentEntitiesForInstance(String entity_uri)
    throws Exception
  {
    try
    {
      RDFEntityList aux = new RDFEntityList();
      String serql = " select distinct x, xl from {x} owl:sameAs {<" + entity_uri + ">}; [rdfs:label {xl}] ";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      aux = retrieveRDFEntityFrom("instance");
      
      serql = serql = " select distinct x, xl from {<" + entity_uri + ">} owl:sameAs {x}, [{x} rdfs:label {xl}] ";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      aux.addNewRDFEntities(retrieveRDFEntityFrom("instance"));
      return aux;
    }
    finally
    {
      this.result.close();
    }
  }
  
  public boolean existTripleForProperty(String entity)
  {
    try
    {
      try
      {
        this.con = getSesameRepository().getConnection();
        String serql = "select p from {s} p {o} where p =<" + entity + "> limit 1 offset 0";
        MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
        TupleQuery tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SERQL, serql);
        this.result = tupleQuery.evaluate();
        boolean bool;
        if ((this.result != null) && 
          (this.result.hasNext())) {
          return true;
        }
        return false;
      }
      finally
      {
        this.result.close();
        this.con.close();
      }
      return false;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public boolean existTripleForInstance(String entity)
  {
    return true;
  }
  
  private void execute(String serql)
  {
    try
    {
      try
      {
        this.con = getSesameRepository().getConnection();
        TupleQuery tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SERQL, serql);
        this.result = tupleQuery.evaluate();
      }
      finally
      {
        this.con.close();
      }
    }
    catch (Exception e)
    {
      System.out.println("Fail to execute: " + serql);
      
      e.printStackTrace();
    }
  }
  
  private TupleQueryResult executeLocal(String serql)
  {
    TupleQueryResult result2 = null;
    try
    {
      try
      {
        this.con = getSesameRepository().getConnection();
        TupleQuery tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SERQL, serql);
        result2 = tupleQuery.evaluate();
      }
      finally
      {
        this.con.close();
      }
    }
    catch (Exception e)
    {
      System.out.println("Fail to execute: " + serql);
      e.printStackTrace();
    }
    return result2;
  }
  
  private Hashtable<RDFEntity, RDFEntityList> retrieveEquivalentEntityFrom(TupleQueryResult resultTable, String type)
    throws Exception
  {
    Hashtable<RDFEntity, RDFEntityList> equivalentEntityTable = new Hashtable();
    if (resultTable == null) {
      return equivalentEntityTable;
    }
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      Value rdfEntity = bindingSet.getValue((String)bindingNames.get(0));
      Value rdfEntityLabel = bindingSet.getValue((String)bindingNames.get(1));
      Value rdfEquivalentEntity = bindingSet.getValue((String)bindingNames.get(2));
      Value rdfEquivalentEntityLabel = bindingSet.getValue((String)bindingNames.get(3));
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
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      Value rdfEntity = bindingSet.getValue((String)bindingNames.get(0));
      Value rdfEntityLabel = bindingSet.getValue((String)bindingNames.get(1));
      Value rdfEquivalentEntity = bindingSet.getValue((String)bindingNames.get(2));
      Value rdfEquivalentEntityLabel = bindingSet.getValue((String)bindingNames.get(3));
      
      RDFEntity entity = new RDFEntity(type, rdfEntity.toString(), rdfEntityLabel == null ? null : rdfEntityLabel.toString(), getPluginID());
      
      RDFEntity refEntity = new RDFEntity(type, rdfEquivalentEntity.toString(), rdfEquivalentEntityLabel == null ? null : rdfEquivalentEntityLabel.toString(), getPluginID());
      entity.setRefers_to(refEntity);
      
      entities.getAllRDFEntities().add(entity);
    }
    return entities;
  }
  
  private RDFEntityList retrieveRDFValueFrom(TupleQueryResult resultTable, String type)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if (resultTable == null) {
      return entities;
    }
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      Value value = bindingSet.getValue((String)bindingNames.get(0));
      if (value != null)
      {
        String valueString = value.toString().trim();
        if ((!valueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "List")))
        {
          String uri = value.toString();
          
          String label = null;
          value = bindingSet.getValue((String)bindingNames.get(1));
          if (value != null) {
            label = value.toString();
          } else {
            label = MyURI.getLocalName(uri);
          }
          RDFEntity c = new RDFEntity(type, uri, label, getPluginID());
          if (!entities.isRDFEntityContained(c.getURI())) {
            entities.addRDFEntity(c);
          }
        }
      }
    }
    return entities;
  }
  
  private RDFEntityList retrieveRDFEntityForIndex(TupleQueryResult resultTable, String type)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if (resultTable == null) {
      return entities;
    }
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      Value value = bindingSet.getValue((String)bindingNames.get(0));
      if (value != null)
      {
        String valueString = value.toString().trim();
        if ((!valueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "List")) && 
        
          (MyURI.isURIValid(value.toString())))
        {
          String uri = value.toString();
          
          String label = null;
          value = bindingSet.getValue((String)bindingNames.get(1));
          if (value != null)
          {
            label = value.toString();
          }
          else
          {
            String title = null;
            if (type == "instance")
            {
              value = bindingSet.getValue((String)bindingNames.get(2));
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
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      
      Value firstValue = bindingSet.getValue((String)bindingNames.get(0));
      if (firstValue != null)
      {
        String firstValueString = firstValue.toString().trim();
        if ((!firstValueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#" + "Class")) && (!firstValueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#" + "Class")) && (!firstValueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "List"))) {
          if (!MyURI.isURIValid(firstValueString))
          {
            if (firstValueString.startsWith("node"))
            {
              System.out.println("reading blank node " + firstValueString);
              entities.addAllRDFEntity(getUnionDefinitionForBlankNode(firstValueString));
            }
          }
          else
          {
            String uri = firstValueString;
            
            Value secondValue = bindingSet.getValue((String)bindingNames.get(1));
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
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      Value value = bindingSet.getValue((String)bindingNames.get(0));
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
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      Value value = bindingSet.getValue((String)bindingNames.get(0));
      if ((value != null) && 
      
        (MyURI.isURIValid(value.toString())) && (
        
        (keepPrimitiveURIs) || (
        (!value.toString().equals("http://www.w3.org/2002/07/owl#" + "Class")) && (!value.toString().equals("http://www.w3.org/2000/01/rdf-schema#" + "Class")) && (!value.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "List"))))) {
        entities.add(value.toString());
      }
    }
    return entities;
  }
  
  private ArrayList<RDFPath> retrieveFullPathFrom(TupleQueryResult resultTable)
    throws Exception
  {
    ArrayList<RDFPath> paths = new ArrayList();
    if (resultTable == null) {
      return paths;
    }
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      
      RDFEntity Refent = null;
      Value valueRefEnt = bindingSet.getValue((String)bindingNames.get(2));
      Value labelRefEnt = bindingSet.getValue((String)bindingNames.get(3));
      if ((valueRefEnt != null) && (MyURI.isURIValid(valueRefEnt.toString()))) {
        Refent = new RDFEntity("class", valueRefEnt.toString(), labelRefEnt == null ? null : labelRefEnt.toString().trim(), getPluginID());
      }
      RDFProperty prop1 = null;
      RDFProperty prop1_domain = null;
      
      Value valueProp1 = bindingSet.getValue((String)bindingNames.get(0));
      Value labelProp1 = bindingSet.getValue((String)bindingNames.get(1));
      Value propertydomain = bindingSet.getValue((String)bindingNames.get(6));
      Value propertydomainlabel = bindingSet.getValue((String)bindingNames.get(7));
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
      Value valueProp2 = bindingSet.getValue((String)bindingNames.get(4));
      Value labelProp2 = bindingSet.getValue((String)bindingNames.get(5));
      
      Value propertyrange2 = bindingSet.getValue((String)bindingNames.get(8));
      Value propertyrangelabel2 = bindingSet.getValue((String)bindingNames.get(9));
      if ((valueProp2 != null) && (MyURI.isURIValid(valueProp2.toString()))) {
        prop2 = new RDFProperty(valueProp2.toString(), labelProp2 == null ? null : labelProp2.toString().trim(), getPluginID());
      }
      if ((propertyrange2 != null) && (MyURI.isURIValid(propertyrange2.toString()))) {
        prop2_range = new RDFProperty(propertyrange2.toString(), propertyrangelabel2 == null ? null : propertyrangelabel2.toString().trim(), getPluginID());
      }
      prop2.setDomain(Refent);
      prop2.setRange(prop2_range);
      if (Refent != null)
      {
        RDFPath path = new RDFPath(prop1, Refent, prop2);
        paths.add(path);
      }
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
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      
      RDFEntity ent = null;
      Value valueEnt = bindingSet.getValue((String)bindingNames.get(1));
      if ((valueEnt != null) && (MyURI.isURIValid(valueEnt.toString())))
      {
        String labelEnt = getFirstEnLabel(valueEnt.toString());
        ent = new RDFEntity("class", valueEnt.toString(), labelEnt == null ? null : labelEnt, getPluginID());
      }
      RDFProperty prop1 = null;
      Value valueProp1 = bindingSet.getValue((String)bindingNames.get(0));
      if ((valueProp1 != null) && (MyURI.isURIValid(valueProp1.toString())))
      {
        String labelProp1 = getFirstEnLabel(valueProp1.toString());
        prop1 = new RDFProperty(valueProp1.toString(), labelProp1 == null ? null : labelProp1, getPluginID());
      }
      RDFProperty prop2 = null;
      Value valueProp2 = bindingSet.getValue((String)bindingNames.get(2));
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
    if (this.result == null) {
      return properties;
    }
    Hashtable<String, RDFProperty> propertiesTable = new Hashtable();
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      RDFProperty p = null;
      Value value = bindingSet.getValue((String)bindingNames.get(0));
      Value labelP = bindingSet.getValue((String)bindingNames.get(3));
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
        Value origin = bindingSet.getValue((String)bindingNames.get(1));
        Value labelOrigin = bindingSet.getValue((String)bindingNames.get(4));
        if ((origin != null) && (MyURI.isURIValid(origin.toString())))
        {
          RDFEntity classDomain = new RDFEntity("class", origin.toString().trim(), labelOrigin == null ? null : labelOrigin.toString().trim(), getPluginID());
          
          p.addDomain(classDomain);
        }
        Value range = bindingSet.getValue((String)bindingNames.get(2));
        Value labelRange = bindingSet.getValue((String)bindingNames.get(5));
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
    List<String> bindingNames = this.result.getBindingNames();
    while (this.result.hasNext())
    {
      BindingSet bindingSet = (BindingSet)this.result.next();
      RDFProperty p = null;
      Value rel = bindingSet.getValue((String)bindingNames.get(0));
      Value rel_label = bindingSet.getValue((String)bindingNames.get(1));
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
      String serql = "select c,l from {c} owl:intersectionOf {x}; [rdfs:label {l}], {x} p {<" + class_uri + ">}";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      execute(serql);
      return retrieveRDFEntityFrom("class");
    }
    finally
    {
      this.result.close();
    }
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
    return this.serverURL;
  }
  
  public void setServerURL(String serverURL)
  {
    this.serverURL = serverURL;
  }
  
  public String getRepositoryType()
  {
    return this.repositoryType;
  }
  
  public void setRepositoryType(String repositoryType)
  {
    this.repositoryType = repositoryType;
  }
  
  public String getRepositoryName()
  {
    return this.repositoryName;
  }
  
  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }
  
  public String getLogin()
  {
    return this.login;
  }
  
  public void setLogin(String login)
  {
    this.login = login;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public String getPluginID()
  {
    return this.repositoryName;
  }
  
  public int numberOfAllTriples(String onto)
  {
    try
    {
      try
      {
        this.con = getSesameRepository().getConnection();
        String serql = "select p from {s} p {o} limit 100000";
        TupleQuery tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SERQL, serql);
        this.result = tupleQuery.evaluate();
        if (this.result != null)
        {
          int rowCount = 0;
          while (this.result.hasNext()) {
            rowCount++;
          }
          return rowCount;
        }
      }
      finally
      {
        this.con.close();
        this.result.close();
      }
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
  
  public int numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(String onto, String c)
  {
    try
    {
      return getDirectSuperClasses(c).size();
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
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
    return getInstancesOf(onto, c).size();
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
        this.con = getSesameRepository().getConnection();
        String serql = "select p from {s} p {o} where s =<" + p + "> limit 1 offset 0";
        TupleQuery tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SERQL, serql);
        this.result = tupleQuery.evaluate();
        if (this.result != null)
        {
          int rowCount = 0;
          while (this.result.hasNext()) {
            rowCount++;
          }
          return rowCount;
        }
      }
      finally
      {
        this.con.close();
        this.result.close();
      }
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
        this.con = getSesameRepository().getConnection();
        String serql = "select p from {s} p {o} where p =<" + p + "> limit 1 offset 0";
        TupleQuery tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SERQL, serql);
        this.result = tupleQuery.evaluate();
        if (this.result != null)
        {
          int rowCount = 0;
          while (this.result.hasNext()) {
            rowCount++;
          }
          return rowCount;
        }
      }
      finally
      {
        this.con.close();
        this.result.close();
      }
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
  
  public String entityType(String onto, String e)
  {
    try
    {
      try
      {
        this.con = getSesameRepository().getConnection();
        String serql = "select c from {c} rdf:type {owl:Class} where c=<" + e + ">";
        TupleQuery tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SERQL, serql);
        this.result = tupleQuery.evaluate();
        String str1;
        if (this.result != null)
        {
          int rowCount = 0;
          while (this.result.hasNext()) {
            rowCount++;
          }
          if (rowCount > 0) {
            return "Class";
          }
        }
        serql = "select c from {c} rdf:type {rdfs:Class} where c=<" + e + ">";
        tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SERQL, serql);
        this.result = tupleQuery.evaluate();
        if (this.result != null)
        {
          int rowCount = 0;
          while (this.result.hasNext()) {
            rowCount++;
          }
          if (rowCount > 0) {
            return "Class";
          }
        }
        serql = "select p from {p} rdf:type {X} where  p=<" + e + "> and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) ";
        
        tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SERQL, serql);
        this.result = tupleQuery.evaluate();
        if (this.result != null)
        {
          int rowCount = 0;
          while (this.result.hasNext()) {
            rowCount++;
          }
          if (rowCount > 0) {
            return "Property";
          }
        }
        serql = " select p from {p} rdf:type {rdf:Property} where  p=<" + e + ">";
        tupleQuery = this.con.prepareTupleQuery(QueryLanguage.SERQL, serql);
        this.result = tupleQuery.evaluate();
        int rowCount;
        if (this.result != null)
        {
          rowCount = 0;
          while (this.result.hasNext()) {
            rowCount++;
          }
          if (rowCount > 0) {
            return "Property";
          }
        }
        return "Individual";
      }
      finally
      {
        this.con.close();
        this.result.close();
      }
      return null;
    }
    catch (Exception ex)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
    }
  }
  
  public org.openrdf.repository.Repository getSesameRepository()
  {
    return this.sesameRepository;
  }
  
  public void setSesameRepository(org.openrdf.repository.Repository sesameRepository)
  {
    this.sesameRepository = sesameRepository;
  }
}

