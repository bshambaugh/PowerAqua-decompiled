package SesamePlugin;

import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.sesame.Sesame;
import org.openrdf.sesame.config.RepositoryInfo;
import org.openrdf.sesame.constants.QueryLanguage;
import org.openrdf.sesame.query.QueryResultsTable;
import org.openrdf.sesame.repository.RepositoryList;
import org.openrdf.sesame.repository.SesameRepository;
import org.openrdf.sesame.repository.SesameService;
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
import poweraqua.serviceConfig.Repository;

public class SesamePlugin
  implements OntologyPlugin, Serializable
{
  private String name;
  private String serverURL = null;
  private String repositoryType = "RDF";
  private String repositoryName = null;
  private String login = null;
  private String password = null;
  private SesameService sesame_service = null;
  private SesameRepository sesame_repository = null;
  private static final String NOT_RDF_INSTANCES = "and not i=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>";
  private static final String NOT_RDF_CLASSES = " and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> ";
  private static final String NOT_RDF_PROPERTIES = " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
  private static final String NOT_RDF_PROPERTIES1 = " and not property=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not property=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not property=<http://www.w3.org/2000/01/rdf-schema#member> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not property=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not property=<http://www.w3.org/2000/01/rdf-schema#comment> and not property=<http://www.w3.org/2000/01/rdf-schema#label> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>";
  private static final String NOT_RDF_PROPERTIES2 = " and not property2=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not property2=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not property2=<http://www.w3.org/2000/01/rdf-schema#member> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not property2=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not property2=<http://www.w3.org/2000/01/rdf-schema#comment> and not property2=<http://www.w3.org/2000/01/rdf-schema#label> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>";
  
  public SesamePlugin()
  {
    setName("sesame");
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
      URL sesameServerURL = new URL(getServerURL());
      setSesame_service(Sesame.getService(sesameServerURL));
      setSesame_repository(getRemoteRepository(repositoryid));
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
    try
    {
      System.out.println("Getting Services from " + getServerURL() + " " + getRepositoryName());
      System.getProperties().setProperty("http.proxyHost", proxyHost);
      System.getProperties().setProperty("http.proxyPort", proxyPort);
      initializeServer();
    }
    catch (Exception e)
    {
      System.out.println("Impossible to initialize " + getServerURL() + " repository " + getRepositoryName());
      e.printStackTrace();
      throw e;
    }
  }
  
  public void logIntoServer()
    throws Exception
  {
    System.out.println("Login " + getLogin() + " pass " + getPassword());
    getSesame_service().login(getLogin(), getPassword());
  }
  
  public void loadPlugin(Repository repository)
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
  
  public SesameRepository getRemoteRepository(String repository)
    throws Exception
  {
    return getSesame_service().getRepository(repository);
  }
  
  public RDFEntityList getAllClasses()
    throws Exception
  {
    String serql = "";
    RDFEntityList aux = new RDFEntityList();
    if (isOWLRepository())
    {
      serql = "select distinct c,l from {c} rdf:type {owl:Class}; [rdfs:label {l}]  where isURI(c) ";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      aux = retrieveRDFEntityFrom(resultTable, "class");
      if (aux.isEmpty()) {
        System.out.println("getClasses: Weird owl .. ");
      }
    }
    serql = "select distinct c,l from {c} rdf:type {rdfs:Class}; [rdfs:label {l}]  where isURI(c)  and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> ";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    
    QueryResultsTable resultTable = executeQuery(serql);
    aux.addNewRDFEntities(retrieveRDFEntityFrom(resultTable, "class"));
    return aux;
  }
  
  public RDFEntityList getAllInstancesOfClassPeriodically(String class_uri, int offset, int limit)
    throws Exception
  {
    String serql = "select distinct i,l from {i} rdf:type {<" + class_uri + ">}; [rdfs:label {l}] where isURI(i) " + "limit " + limit + " offset " + offset;
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    return retrieveRDFEntityFrom(resultTable, "instance");
  }
  
  public RDFEntityList getAllClassesPeriodically(int offset, int limit)
    throws Exception
  {
    String serql = "";
    RDFEntityList aux = new RDFEntityList();
    if (isOWLRepository())
    {
      serql = "select distinct c,l from {c} rdf:type {owl:Class}; [rdfs:label {l}]  where isURI(c) limit " + limit + " offset " + offset;
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      aux = retrieveRDFEntityFrom(resultTable, "class");
      if (aux.isEmpty()) {
        System.out.println("getClasses: Weird owl .. ");
      }
    }
    serql = "select distinct c,l from {c} rdf:type {rdfs:Class}; [rdfs:label {l}]  where isURI(c)  and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> limit " + limit + " offset " + offset;
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    
    QueryResultsTable resultTable = executeQuery(serql);
    aux.addNewRDFEntities(retrieveRDFEntityFrom(resultTable, "class"));
    return aux;
  }
  
  public RDFEntityList getAllProperties()
    throws Exception
  {
    RDFEntityList aux = new RDFEntityList();
    String serql = "";
    if (isOWLRepository())
    {
      serql = " select p,o,r,l,ol,rl from {p} rdf:type {X}, [{p} rdfs:domain {o}, [{o} rdfs:label {ol}]], [{p} rdfs:range {r}, [{r} rdfs:label {rl}]], [{p} rdfs:label {l}] where  X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>  ";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      aux = retrieveFullPropertiesFrom(resultTable);
      if (aux.isEmpty()) {
        System.out.println("Weird owl on getting properties.. ");
      }
    }
    serql = " select p,o,r,l,ol,rl from {p} rdf:type {rdf:Property}, [{p} rdfs:range {r}, [{r} rdfs:label {rl}]], [{p} rdfs:domain {o}, [{o} rdfs:label {ol}]], [{p} rdfs:label {l}]";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    aux.addNewRDFEntities(retrieveFullPropertiesFrom(resultTable));
    return aux;
  }
  
  public RDFEntityList getAllPropertiesPeriodically(int offset, int limit)
    throws Exception
  {
    RDFEntityList aux = new RDFEntityList();
    String serql = "";
    if (isOWLRepository())
    {
      serql = " select p,o,r,l,ol,rl from {p} rdf:type {X}, [{p} rdfs:domain {o}, [{o} rdfs:label {ol}]], [{p} rdfs:range {r}, [{r} rdfs:label {rl}]], [{p} rdfs:label {l}] where  X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty>limit " + limit + " offset " + offset;
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      aux = retrieveFullPropertiesFrom(resultTable);
      if (aux.isEmpty()) {
        System.out.println("Weird owl on get all properties");
      }
    }
    serql = " select p,o,r,l,ol,rl from {p} rdf:type {rdf:Property}, [{p} rdfs:range {r}, [{r} rdfs:label {rl}]], [{p} rdfs:domain {o}, [{o} rdfs:label {ol}]], [{p} rdfs:label {l}] limit " + limit + " offset " + offset;
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    aux.addNewRDFEntities(retrieveFullPropertiesFrom(resultTable));
    return aux;
  }
  
  public ArrayList<RDFPath> getSchemaIndirectRelations(String sourceURI, String targetURI)
    throws Exception
  {
    ArrayList<RDFPath> paths = new ArrayList();
    
    String serql = "select distinct property, proplabel, reference, reflabel, property2, prop2label, subject, originlabel, reference, reflabel, reference, reflabel, destiny, targetlabel from {instOrigin} rdf:type {subject}, {instRef} serql:directType {reference}, {instTarget} rdf:type {destiny}, {instOrigin} property {instRef}, {instRef} property2 {instTarget}, [{property} rdfs:label {proplabel}], [{reference} rdfs:label {reflabel}], [{subject} rdfs:label {originlabel}], [{destiny} rdfs:label {targetlabel}], [{property2} rdfs:label {prop2label}] where ((subject=<" + sourceURI + "> " + "and destiny=<" + targetURI + ">) or " + "(subject=<" + targetURI + "> " + "and destiny=<" + sourceURI + ">))" + " and not (instOrigin = instTarget)  and not (instOrigin = instRef) and not (instTarget = instRef) " + "and not reference =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> " + "and not reference = <http://www.w3.org/2000/01/rdf-schema#Resource> " + "and not reference = <http://www.w3.org/1999/02/22-rdf-syntax-ns#List>" + " and not property=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not property=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not property=<http://www.w3.org/2000/01/rdf-schema#member> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not property=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not property=<http://www.w3.org/2000/01/rdf-schema#comment> and not property=<http://www.w3.org/2000/01/rdf-schema#label> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>" + " and not property2=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not property2=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not property2=<http://www.w3.org/2000/01/rdf-schema#member> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not property2=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not property2=<http://www.w3.org/2000/01/rdf-schema#comment> and not property2=<http://www.w3.org/2000/01/rdf-schema#label> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultsTable = executeQuery(serql);
    paths.addAll(retrieveFullPathFrom(resultsTable));
    if (paths.isEmpty())
    {
      serql = "select distinct property, proplabel, reference, reflabel, property2, prop2label,  reference, reflabel, subject, originlabel, reference, reflabel, destiny, targetlabel from {instOrigin} rdf:type {subject}, {instRef} serql:directType {reference}, {instTarget} rdf:type {destiny}, {instRef} property {instOrigin}, {instRef} property2 {instTarget}, [{property} rdfs:label {proplabel}], [{reference} rdfs:label {reflabel}], [{subject} rdfs:label {originlabel}], [{destiny} rdfs:label {targetlabel}], [{property2} rdfs:label {prop2label}] where subject=<" + sourceURI + "> " + "and destiny=<" + targetURI + "> " + " and not (instOrigin = instTarget)  and not (instOrigin = instRef) and not (instTarget = instRef) " + "and not reference =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> " + "and not reference = <http://www.w3.org/2000/01/rdf-schema#Resource> " + "and not reference = <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> " + " and not property=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not property=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not property=<http://www.w3.org/2000/01/rdf-schema#member> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not property=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not property=<http://www.w3.org/2000/01/rdf-schema#comment> and not property=<http://www.w3.org/2000/01/rdf-schema#label> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not property=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>" + " and not property2=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not property2=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not property2=<http://www.w3.org/2000/01/rdf-schema#member> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not property2=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not property2=<http://www.w3.org/2000/01/rdf-schema#comment> and not property2=<http://www.w3.org/2000/01/rdf-schema#label> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not property2=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultsTable = executeQuery(serql);
      
      paths.addAll(retrieveFullPathFrom(resultsTable));
    }
    return paths;
  }
  
  public boolean isKBTripleClassClass(String class1_uri, String relation, String class2_uri)
    throws Exception
  {
    String serq = "SELECT distinct rel from {instorigin} rdf:type {class1}, {insttarget} rdf:type {class2}, {instorigin} rel {insttarget} where (class1 =<" + class1_uri + "> " + "and class2 =<" + class2_uri + "> " + "and rel =<" + relation + ">)";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serq);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      String serq2 = "SELECT distinct rel from {instorigin} rdf:type {class1}, {insttarget} rdf:type {class2},  {insttarget} rel {instorigin} where (class1 =<" + class1_uri + "> " + "and class2 =<" + class2_uri + "> " + "and rel =<" + relation + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq2);
    }
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return false;
    }
    return true;
  }
  
  public boolean isKBTripleClassInstance(String class_uri, String relation, String instance2_uri)
    throws Exception
  {
    String serq = "SELECT distinct rel from {instorigin} rdf:type {classGeneric}, {instorigin} rel {instance2} where (classGeneric =<" + class_uri + "> " + "and instance2 =<" + instance2_uri + "> " + "and rel =<" + relation + ">)";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serq);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      String serq2 = "SELECT distinct rel from {instorigin} rdf:type {classGeneric}, {instance2} rel {instorigin} where (classGeneric =<" + class_uri + "> " + "and instance2 =<" + instance2_uri + "> " + "and rel =<" + relation + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq2);
    }
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return false;
    }
    return true;
  }
  
  public boolean isKBTripleInstanceInstance(String instance1_uri, String relation, String instance2_uri)
    throws Exception
  {
    String serq = "SELECT distinct rel from {instance2} rel {instance1} where (instance1 =<" + instance1_uri + "> " + "and instance2 =<" + instance2_uri + "> " + "and rel =<" + relation + ">)";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serq);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      String serq2 = "SELECT distinct rel from {instance1} rel {instance2} where (instance1 =<" + instance1_uri + "> " + "and instance2 =<" + instance2_uri + "> " + "and rel =<" + relation + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq2);
    }
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return false;
    }
    return true;
  }
  
  public ArrayList<RDFPath> getKBIndirectRelations(String class_sourceURI, String instance_targetURI)
    throws Exception
  {
    ArrayList<RDFPath> paths = new ArrayList();
    
    String serql = "select distinct property, reference, property2 from  {instorigin} rdf:type {classGeneric}, {instdestiny} rdf:type {classRange},  {referencekb} rdf:type {reference}, {classGeneric}  rdfs:subClassOf {domainGeneric}, {property}  rdfs:range {reference}, {property} rdfs:domain {domainGeneric},  {property2} rdfs:domain {reference}, {property2} rdfs:range {classRange}, {instorigin} property {referencekb}, {referencekb} property2 {instdestiny} where classGeneric=<" + class_sourceURI + "> " + "and instdestiny=<" + instance_targetURI + ">" + " UNION " + "select distinct property, reference, property2  from " + " {instorigin} rdf:type {classGeneric}, {referencekb} property {instorigin}, {instdestiny} property2 {referencekb}, " + "{referencekb} rdf:type {reference}, {property} rdfs:domain {reference}, {property2} rdfs:range {reference} " + "where classGeneric=<" + class_sourceURI + "> " + "and instdestiny=<" + instance_targetURI + ">";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultsTable = executeQuery(serql);
    paths.addAll(retrievePathFrom(resultsTable));
    for (RDFPath path : paths)
    {
      path.getRDFProperty1().setDomain(getDomainOfProperty(path.getRDFProperty1().getURI()));
      path.getRDFProperty1().setRange(getRangeOfProperty(path.getRDFProperty1().getURI()));
      
      path.getRDFProperty2().setDomain(getDomainOfProperty(path.getRDFProperty2().getURI()));
      path.getRDFProperty2().setRange(getRangeOfProperty(path.getRDFProperty2().getURI()));
    }
    return paths;
  }
  
  public ArrayList<RDFPath> getInstanceIndirectRelations(String instance_sourceURI, String instance_targetURI)
    throws Exception
  {
    ArrayList<RDFPath> paths = new ArrayList();
    
    String serql = "select distinct property, referenceEnt, property2 from {instorigin} property {reference}, {reference} property2 {instdestiny}, {reference} serql:directType {referenceEnt}  where instorigin=<" + instance_sourceURI + "> " + "and instdestiny=<" + instance_targetURI + ">";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultsTable = executeQuery(serql);
    paths.addAll(retrievePathFrom(resultsTable));
    
    serql = "select distinct property, referenceEnt, property2 from {reference} property {instorigin}, {reference} property2 {instdestiny}, {reference} serql:directType {referenceEnt}  where instorigin=<" + instance_sourceURI + "> " + "and instdestiny=<" + instance_targetURI + "> " + "and not property2 = property";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    resultsTable = executeQuery(serql);
    paths.addAll(retrievePathFrom(resultsTable));
    
    serql = "select distinct property, referenceEnt, property2 from {reference} property {instorigin}, {instdestiny} property2 {reference}, {reference} serql:directType {referenceEnt}  where instorigin=<" + instance_sourceURI + "> " + "and instdestiny=<" + instance_targetURI + ">";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    resultsTable = executeQuery(serql);
    paths.addAll(retrievePathFrom(resultsTable));
    for (RDFPath path : paths)
    {
      path.getRDFProperty1().setDomain(getDomainOfProperty(path.getRDFProperty1().getURI()));
      path.getRDFProperty1().setDomain(getRangeOfProperty(path.getRDFProperty1().getURI()));
      
      path.getRDFProperty2().setDomain(getDomainOfProperty(path.getRDFProperty2().getURI()));
      path.getRDFProperty2().setDomain(getRangeOfProperty(path.getRDFProperty2().getURI()));
    }
    return paths;
  }
  
  public RDFEntityList getAllSubClasses(String class_uri)
    throws Exception
  {
    RDFEntityList classes = new RDFEntityList();
    String serql;
    String serql;
    if (class_uri.startsWith("node")) {
      serql = "select sc,l from {c} rdfs:subClassOf {\"" + class_uri + "\"}, [{sc} rdfs:label {l}] " + "where not sc = \"" + class_uri + "\"";
    } else {
      serql = "select c,l from {c} rdfs:subClassOf {<" + class_uri + ">}, [{c} rdfs:label {l}] " + "where not c =<" + class_uri + ">";
    }
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    classes = retrieveRDFEntityFrom(resultTable, "class");
    if (classes == null) {
      classes = new RDFEntityList();
    }
    if (isOWLRepository())
    {
      RDFEntityList c2 = getSubClassesFromIntersectionDefinition(class_uri);
      classes.addNewRDFEntities(c2);
    }
    return classes;
  }
  
  public RDFEntityList getDirectSubClasses(String class_uri)
    throws Exception
  {
    String serql = "select distinct c, l from {c} rdfs:subClassOf {<" + class_uri + ">}, [{c} rdfs:label {l}] " + "where isURI(c) and not c=<" + class_uri + "> " + "minus " + "select distinct c, l from {c} rdfs:subClassOf {c2} rdfs:subClassOf {<" + class_uri + ">} " + ", [{c} rdfs:label {l}] where isURI(c) and isURI(c2) " + "and not c2=<" + class_uri + ">  and not c=c2";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    
    QueryResultsTable resultTable = executeQuery(serql);
    RDFEntityList classes = retrieveRDFEntityFrom(resultTable, "class");
    if (classes == null) {
      classes = new RDFEntityList();
    }
    if (isOWLRepository())
    {
      RDFEntityList c2 = getSubClassesFromIntersectionDefinition(class_uri);
      if (classes == null) {
        return c2;
      }
      classes.addNewRDFEntities(c2);
    }
    return classes;
  }
  
  public RDFEntityList getAllSuperClasses(String class_uri)
    throws Exception
  {
    String serql;
    String serql;
    if (class_uri.startsWith("node")) {
      serql = "select sc,l from {\"" + class_uri + "\"} rdfs:subClassOf {sc}, [{sc} rdfs:label {l}] " + "where not sc = \"" + class_uri + "\"";
    } else {
      serql = "select sc,l from {<" + class_uri + ">} rdfs:subClassOf {sc}, [{sc} rdfs:label {l}] " + "where not sc =<" + class_uri + ">";
    }
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    RDFEntityList classes = retrieveRDFEntityFrom(resultTable, "class");
    if (classes == null) {
      classes = new RDFEntityList();
    }
    return classes;
  }
  
  public RDFEntityList getDirectSuperClasses(String class_uri)
    throws Exception
  {
    RDFEntityList classes = new RDFEntityList();
    
    String serql = "select distinct c, l from {<" + class_uri + ">} rdfs:subClassOf {c}, " + "[{c} rdfs:label {l}] where isURI(c) and not c=<" + class_uri + "> " + "minus select distinct c, l from {<" + class_uri + ">} rdfs:subClassOf {c2} rdfs:subClassOf {c}, " + "[{c} rdfs:label {l}] where isURI(c) and isURI(c2) " + "and not c2=<" + class_uri + ">  and not c=c2";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    
    QueryResultsTable resultTable = executeQuery(serql);
    classes = retrieveRDFEntityFrom(resultTable, "class");
    if (classes == null) {
      classes = new RDFEntityList();
    }
    return classes;
  }
  
  private ArrayList<String> getDirectSuperClassesNames(String class_uri)
    throws Exception
  {
    RDFEntityList super_classes = getDirectSuperClasses(class_uri);
    if (super_classes == null) {
      return new ArrayList();
    }
    return super_classes.getUris();
  }
  
  private ArrayList<String> getDirectSubClassesNames(String class_uri)
    throws Exception
  {
    RDFEntityList super_classes = getDirectSubClasses(class_uri);
    if (super_classes == null) {
      return new ArrayList();
    }
    return super_classes.getUris();
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
          slots.addAllRDFEntity(ss);
        }
      }
    }
    return slots;
  }
  
  public RDFEntityList getAllPropertiesOfInstance(String instance_uri)
    throws Exception
  {
    RDFEntityList slots = new RDFEntityList();
    if (isOWLRepository())
    {
      String serql = "select distinct rel, origin, rango, rellab, originlab, rangolab from {instance} rel {value}, {rel} rdf:type {X}, [{rel} rdfs:label {rellab}], [{rel} rdfs:range {rango}, [{value} rdf:type {rango}], [{rango} rdfs:label {rangolab}]], [{rel} rdfs:domain {origin}, {instance} rdf:type {origin}, [{origin} rdfs:label {originlab}]] where instance=<" + instance_uri + "> " + "and not rel =<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel =<http://www.w3.org/2000/01/rdf-schema#label> " + "and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      slots = retrieveFullPropertiesFrom(resultTable);
      if (slots == null) {
        slots = new RDFEntityList();
      }
      String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from {value} rel {instance}, {rel} rdf:type {X}, [{rel} rdfs:label {rellab}], [{rel} rdfs:range {rango}, {instance} rdf:type {rango}, [{rango} rdfs:label {rangolab}]],  [{rel} rdfs:domain {origin}, {value} rdf:type {origin}, [{origin} rdfs:label {originlab}]] where instance=<" + instance_uri + "> " + "and not rel =<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel =<http://www.w3.org/2000/01/rdf-schema#label> " + "and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable2 = executeQuery(serql2);
      RDFEntityList slots2 = retrieveFullPropertiesFrom(resultTable2);
      slots.addAllRDFEntity(slots2);
    }
    else
    {
      String serql = "select distinct rel, origin, rango, rellab, originlab, rangolab from {instance} rel {value}, {rel} rdf:type {rdf:Property}, [{rel} rdfs:label {rellab}], [{rel} rdfs:range {rango}, {value} rdf:type {rango}, [{rango} rdfs:label {rangolab}]], [{rel} rdfs:domain {origin}, {instance} rdf:type {origin}, [{origin} rdfs:label {originlab}]] where instance=<" + instance_uri + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      slots = retrieveFullPropertiesFrom(resultTable);
      if (slots == null) {
        slots = new RDFEntityList();
      }
      String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from {value} rel {instance}, {rel} rdf:type {rdf:Property}, [{rel} rdfs:label {rellab}], [{rel} rdfs:range {rango}, {instance} rdf:type {rango}, [{rango} rdfs:label {rangolab}]],  [{rel} rdfs:domain {origin}, {value} rdf:type {origin}, [{origin} rdfs:label {originlab}]] where instance=<" + instance_uri + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable2 = executeQuery(serql2);
      RDFEntityList slots2 = retrieveFullPropertiesFrom(resultTable2);
      slots.addAllRDFEntity(slots2);
    }
    return slots;
  }
  
  public RDFEntityList getDirectPropertiesOfClass(String class1)
    throws Exception
  {
    RDFEntityList slots = new RDFEntityList();
    if (isOWLRepository())
    {
      String serql = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {rel} rdf:type {X}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where origin  =<" + class1 + "> " + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      
      slots = retrieveFullPropertiesFrom(resultTable);
      if (slots == null) {
        slots = new RDFEntityList();
      }
      String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {rel} rdf:type {X}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}] where rango=<" + class1 + "> " + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      
      QueryResultsTable resultTable2 = executeQuery(serql2);
      RDFEntityList slots2 = retrieveFullPropertiesFrom(resultTable2);
      slots.addAllRDFEntity(slots2);
    }
    else
    {
      String serql = "select rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where origin  =<" + class1 + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      
      slots = retrieveFullPropertiesFrom(resultTable);
      if (slots == null) {
        slots = new RDFEntityList();
      }
      String serql2 = "select rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}] where rango=<" + class1 + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable2 = executeQuery(serql2);
      RDFEntityList slots2 = retrieveFullPropertiesFrom(resultTable2);
      slots.addAllRDFEntity(slots2);
    }
    return slots;
  }
  
  public RDFEntityList getAllClassesOfInstance(String instance_uri)
    throws Exception
  {
    String serql = "select c,l from {<" + instance_uri + ">} rdf:type {c};[rdfs:label {l}]";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    return retrieveRDFEntityFrom(resultTable, "class");
  }
  
  public ArrayList<String> getAllClassesNamesOfInstance(String instance_uri)
    throws Exception
  {
    String serql = "select c from {<" + instance_uri + ">} rdf:type {c}";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    return retrieveEntitiesUriFrom(resultTable, false);
  }
  
  public RDFEntityList getDirectClassOfInstance(String instance_uri)
    throws Exception
  {
    String serql = "select c,l from {i} serql:directType {c}, [{c} rdfs:label {l}] where i =<" + instance_uri + ">";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    
    QueryResultsTable resultTable = executeQuery(serql);
    
    RDFEntityList classes = retrieveRDFEntityFrom(resultTable, "class");
    if ((classes == null) || (classes.isEmpty())) {
      return new RDFEntityList();
    }
    if (classes.size() == 1) {
      return classes;
    }
    return classes;
  }
  
  public RDFEntityList getAllInstances()
    throws Exception
  {
    RDFEntityList aux = new RDFEntityList();
    String serql;
    if (isOWLRepository())
    {
      String serql = "select distinct p, l, title from {c} rdf:type {owl:Class},  {p} rdf:type {c},  [{p}  <http://purl.org/dc/elements/1.1/title> {title}], [{p} rdfs:label {l}]";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      aux = retrieveRDFEntityForIndex(resultTable, "instance");
    }
    else
    {
      serql = "select distinct i, l from {i} rdf:type {c}, {c} rdf:type {rdfs:Class}, [{i} rdfs:label {l}] where isURI(i)  and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo>  minus select i from {i} rdf:type {rdfs:Class} where isURI(i)   and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo>  union select i from {i} rdf:type {rdf:Property} where isURI(i)   and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> ";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    }
    QueryResultsTable resultTable = executeQuery(serql);
    aux.addAllRDFEntity(retrieveRDFEntityFrom(resultTable, "instance"));
    return aux;
  }
  
  public RDFEntityList getAllInstancesPeriodically(int offset, int limit)
    throws Exception
  {
    RDFEntityList aux = new RDFEntityList();
    if (isOWLRepository())
    {
      String serql = "select distinct p, l, title from {c} rdf:type {owl:Class},  {p} rdf:type {c}, [{p}  <http://purl.org/dc/elements/1.1/title> {title}] , [{p} rdfs:label {l}] limit " + limit + " offset " + offset;
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      aux = retrieveRDFEntityForIndex(resultTable, "instance");
      if (aux.isEmpty()) {
        System.out.println("getInstances: Weird owl .. ");
      }
    }
    String serql = " select distinct i, l, title from {i} rdf:type {c}, {c} rdf:type {rdfs:Class}, [{i}  <http://purl.org/dc/elements/1.1/title> {title}], [{i} rdfs:label {l}] where isURI(i)  and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> and not i=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>limit " + limit + " offset " + offset + " minus ( " + "select i, l, title from {i} rdf:type {rdfs:Class}, " + " [{i} rdfs:label {l}], [{i}  <http://purl.org/dc/elements/1.1/title> {title}] " + "where isURI(i) " + " and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> " + " union " + "select i, l, title from {i} rdf:type {owl:Class}, " + " [{i} rdfs:label {l}], [{i}  <http://purl.org/dc/elements/1.1/title> {title}] " + "where isURI(i) " + " and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> " + " union " + "select i, l, title from {i} rdf:type {rdf:Property}, " + " [{i} rdfs:label {l}], [{i}  <http://purl.org/dc/elements/1.1/title> {title}] " + "where isURI(i) " + " and not c=<http://www.w3.org/2000/01/rdf-schema#Class> and not c=<http://www.w3.org/2000/01/rdf-schema#Literal> and not c=<http://www.w3.org/2000/01/rdf-schema#Datatype> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> and not c=<http://www.w3.org/2000/01/rdf-schema#Container> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> and not c=<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> and not c=<http://www.w3.org/2000/01/rdf-schema#member> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#List> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> and not c=<http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> and not c=<http://www.w3.org/2002/07/owl#AllDifferent> and not c=<http://www.w3.org/2002/07/owl#allValuesFrom> and not c=<http://www.w3.org/2002/07/owl#AnnotationProperty> and not c=<http://www.w3.org/2002/07/owl#backwardCompatibleWith> and not c=<http://www.w3.org/2002/07/owl#cardinality> and not c=<http://www.w3.org/2002/07/owl#Class> and not c=<http://www.w3.org/2002/07/owl#complementOf> and not c=<http://www.w3.org/2002/07/owl#DataRange> and not c=<http://www.w3.org/2002/07/owl#DatatypeProperty> and not c=<http://www.w3.org/2002/07/owl#DeprecatedClass> and not c=<http://www.w3.org/2002/07/owl#DeprecatedProperty> and not c=<http://www.w3.org/2002/07/owl#differentFrom> and not c=<http://www.w3.org/2002/07/owl#disjointWith> and not c=<http://www.w3.org/2002/07/owl#distinctMembers> and not c=<http://www.w3.org/2002/07/owl#equivalentClass> and not c=<http://www.w3.org/2002/07/owl#equivalentProperty> and not c=<http://www.w3.org/2002/07/owl#FunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#hasValue> and not c=<http://www.w3.org/2002/07/owl#imports> and not c=<http://www.w3.org/2002/07/owl#incompatibleWith> and not c=<http://www.w3.org/2002/07/owl#intersectionOf> and not c=<http://www.w3.org/2002/07/owl#InverseFunctionalProperty> and not c=<http://www.w3.org/2002/07/owl#inverseOf>and not c=<http://www.w3.org/2002/07/owl#maxCardinality> and not c=<http://www.w3.org/2002/07/owl#minCardinality> and not c=<http://www.w3.org/2002/07/owl#Nothing> and not c=<http://www.w3.org/2002/07/owl#ObjectProperty> and not c=<http://www.w3.org/2002/07/owl#oneOf> and not c=<http://www.w3.org/2002/07/owl#onProperty> and not c=<http://www.w3.org/2002/07/owl#Ontology> and not c=<http://www.w3.org/2002/07/owl#OntologyProperty> and not c=<http://www.w3.org/2002/07/owl#priorVersion> and not c=<http://www.w3.org/2002/07/owl#Restriction> and not c=<http://www.w3.org/2002/07/owl#sameAs> and not c=<http://www.w3.org/2002/07/owl#someValuesFrom> and not c=<http://www.w3.org/2002/07/owl#SymmetricProperty> and not c=<http://www.w3.org/2002/07/owl#Thing> and not c=<http://www.w3.org/2002/07/owl#TransitiveProperty> and not c=<http://www.w3.org/2002/07/owl#unionOf> and not c=<http://www.w3.org/2002/07/owl#versionInfo> " + ")";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    aux.addAllRDFEntity(retrieveRDFEntityFrom(resultTable, "instance"));
    return aux;
  }
  
  public ArrayList<String> getAllInstancesNamesOfClass(String class_uri)
    throws Exception
  {
    String serql = "select distinct i from {i} rdf:type {<" + class_uri + ">} where isURI(i) ";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    
    return retrieveEntitiesUriFrom(resultTable, false);
  }
  
  public RDFEntityList getAllInstancesOfClass(String class_uri, int limit)
    throws Exception
  {
    String serql;
    String serql;
    if (limit > 0) {
      serql = "select distinct i,l from {i} rdf:type {<" + class_uri + ">}; [rdfs:label {l}] where isURI(i)";
    } else {
      serql = "select distinct i,l from {i} rdf:type {<" + class_uri + ">}; [rdfs:label {l}] where isURI(i) limit " + limit;
    }
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    return retrieveRDFEntityFrom(resultTable, "instance");
  }
  
  public boolean isInstanceOf(String instance_uri, String class_uri)
    throws Exception
  {
    String serql = "select distinct i from {i} rdf:type {<" + class_uri + ">} " + "where i =<" + instance_uri + "> ";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    if ((resultTable == null) || (resultTable.getRowCount() == 0)) {
      return false;
    }
    return true;
  }
  
  public RDFEntityList getSlotValue(String instance_uri, String property_uri)
    throws Exception
  {
    RDFEntityList results = new RDFEntityList();
    String serql = "select distinct v from {<" + instance_uri + ">} <" + property_uri + "> {v} where isLiteral(v)";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    
    results.addAllRDFEntity(retrieveRDFEntityLiteralsFrom(resultTable, instance_uri));
    if (!results.isEmpty()) {
      return results;
    }
    serql = "select distinct v, lv from {<" + instance_uri + ">} <" + property_uri + "> {v}, [{v} rdfs:label {lv}]";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    resultTable = executeQuery(serql);
    results.addAllRDFEntity(retrieveRDFEntityFrom(resultTable, "instance"));
    
    return results;
  }
  
  public RDFEntityList getInstancesWithSlotValue(String property_uri, String slot_value, boolean isValueLiteral)
    throws Exception
  {
    String serql = "";
    if (isValueLiteral)
    {
      serql = "select i, lb from {i} <" + property_uri + "> {v} , [{i} rdfs:label {lb}] " + "where isLiteral(v) and v = \"" + slot_value + "\" ";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      return retrieveRDFEntityFrom(resultTable, "instance");
    }
    serql = "select i, lb from {i} <" + property_uri + "> {<" + slot_value + ">} , [{i} rdfs:label {lb}]";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    return retrieveRDFEntityFrom(resultTable, "instance");
  }
  
  public RDFEntityList getLiteralValuesOfInstance(String instance_uri)
    throws Exception
  {
    String serql = "";
    serql = "select distinct v from {i} p {v} where isLiteral(v) and i = <" + instance_uri + "> ";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    
    QueryResultsTable resultTable = executeQuery(serql);
    return retrieveRDFEntityLiteralsFrom(resultTable, instance_uri);
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
    String serql = "select i from {i} rdf:type {rdfs:Resource} where i =<" + uri + ">  ";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return false;
    }
    return true;
  }
  
  public boolean isNameOfInstance(String instance_uri, String literal)
    throws Exception
  {
    String serql = "select distinct i from {i} property {literal},   {property} rdfs:label {name} where i =<" + instance_uri + "> and literal = \"" + literal + "\" and name = \"name\"";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return false;
    }
    return true;
  }
  
  private ArrayList<String> getInstanceURIsFromLocalName(String class_uri, String local_name)
    throws Exception
  {
    String serql = "select distinct i from {i} rdf:type {<" + class_uri + ">} " + "where localName(i) = \"" + local_name + "\" ";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return new ArrayList();
    }
    ArrayList<String> uris = new ArrayList();
    for (int i = 0; i < resultTable.getRowCount(); i++)
    {
      Value value = resultTable.getValue(i, 0);
      
      uris.add(value.toString());
    }
    return uris;
  }
  
  public OcmlInstance getInstanceInfo(String instance_uri)
    throws Exception
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
    QueryResultsTable resultTable = executeQuery(serql);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return ocmlInstance;
    }
    int rowCount = resultTable.getRowCount();
    if (rowCount == 0) {
      return ocmlInstance;
    }
    for (int i = 0; i < rowCount; i++)
    {
      String property = resultTable.getValue(i, 0).toString();
      String value = resultTable.getValue(i, 1).toString();
      
      Value v_propertyLabel = resultTable.getValue(i, 2);
      String propertyLabel = v_propertyLabel == null ? null : v_propertyLabel.toString().trim();
      
      Value v_valueLabel = resultTable.getValue(i, 3);
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
  
  public OcmlProperty getPropertyInfo(String property_uri)
    throws Exception
  {
    RDFEntity entityClass = new RDFEntity("property", property_uri, getLabelOfEntity(property_uri), getPluginID());
    
    OcmlProperty ocmlProperty = new OcmlProperty(entityClass);
    
    ocmlProperty.setDirectSuperProperties(getDirectSuperClasses(property_uri));
    ocmlProperty.setSuperProperties(getAllSuperClasses(property_uri));
    ocmlProperty.setDirectSubProperties(getDirectSubClasses(property_uri));
    ocmlProperty.setSubProperties(getAllSubClasses(property_uri));
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
    String serql = "select c from {c} rdfs:subClassOf {<" + class2 + ">} " + "where c = <" + class1 + "> ";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    
    ArrayList<String> uris = retrieveEntitiesUriFrom(resultTable, false);
    if (uris != null) {
      return true;
    }
    return false;
  }
  
  public RDFEntityList getGenericInstances(String class_uri, String slot, String value_uri)
    throws Exception
  {
    System.out.println("ToDo: Put a limit in the number of results ");
    String serql = "select distinct instances, i_label from {instances} rdf:type {subject}, {instances} <" + slot + "> {value}, [{instances} rdfs:label {i_label}] " + "where value=<" + value_uri + "> and subject=<" + class_uri + "> " + "and  (lang(i_label) = \"en\" or lang(i_label) = NULL or lang(i_label) = \"en-us\")";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    
    QueryResultsTable resultTable = executeQuery(serql);
    
    serql = "select distinct instances, i_label from {instances} rdf:type {subject}, {value} <" + slot + "> {instances}, [{instances} rdfs:label {i_label}] " + "where value=<" + value_uri + "> and subject=<" + class_uri + ">" + "and  (lang(i_label) = \"en\" or lang(i_label) = NULL or lang(i_label) = \"en-us\")";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable2 = executeQuery(serql);
    
    RDFEntityList lista1 = retrieveRDFValueFrom(resultTable, "instance");
    RDFEntityList lista2 = retrieveRDFValueFrom(resultTable2, "instance");
    lista1.addAllRDFEntity(lista2);
    return lista1;
  }
  
  public RDFEntityList getGenericInstancesForLiteral(String class_uri, String slot, String value)
    throws Exception
  {
    String serql = "select distinct instances, i_label from {instances} rdf:type {subject}, {instances} <" + slot + "> {value}, [{instances} rdfs:label {i_label}] " + "where isLiteral (value) and value= \"" + value + "\" and subject=<" + class_uri + ">";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    
    return retrieveRDFValueFrom(resultTable, "instance");
  }
  
  public RDFEntityList getTripleInstances(String query_class, String slot, String class2)
    throws Exception
  {
    String serq = "select inst1, inst2 from {inst1} rdf:type {class1},  {inst2} rdf:type {class2}, {inst1} rel {inst2} where (class1 =<" + query_class + "> " + "and class2 =<" + class2 + "> " + "and rel = <" + slot + "> )";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    
    QueryResultsTable resultTable = executeQuery(serq);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      serq = "select inst1, inst2 from {inst1} rdf:type {class1},  {inst2} rdf:type {class2}, {inst2} rel {inst1} where (class1 = <" + query_class + "> " + "and class2 = <" + class2 + "> " + "and rel = <" + slot + "> )";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq);
    }
    return retrieveRDFTripleValueFrom(resultTable, "instance");
  }
  
  public RDFEntityList getTripleInstances(String query_class, String prop1, String ref_class, String prop2, String instance)
    throws Exception
  {
    String serq = "select distinct inst, ref_inst from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {inst} prop1 {ref_inst}, {ref_inst} prop2 {instance} where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = <" + instance + ">)";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serq);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      serq = "select distinct inst, ref_inst from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {ref_inst} prop2 {instance} where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = <" + instance + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq);
    }
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      serq = "select distinct inst, ref_inst from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {instance} prop2 {ref_inst} where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = <" + instance + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq);
    }
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      serq = "select distinct inst, ref_inst from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {inst} prop1 {ref_inst}, {instance} prop2 {ref_inst} where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = <" + instance + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq);
    }
    return retrieveRDFTripleValueFrom(resultTable, "instance");
  }
  
  public RDFEntityList getTripleInstancesFromLiteral(String query_class, String prop1, String ref_class, String prop2, String instance)
    throws Exception
  {
    String serq = "select distinct inst, ref_inst from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {inst} prop1 {ref_inst}, {ref_inst} prop2 {instance} where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = \"" + instance + "\")";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serq);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      serq = "select distinct inst, ref_inst from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {ref_inst} prop2 {instance} where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and instance = \"" + instance + "\")";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq);
    }
    return retrieveRDFTripleValueFrom(resultTable, "instance");
  }
  
  public RDFEntityList getTripleInstancesFromClasses(String query_class, String prop1, String ref_class, String prop2, String class2)
    throws Exception
  {
    String serq = "select distinct inst, instance from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {inst} prop1 {ref_inst}, {ref_inst} prop2 {instance}, {instance} rdf:type {class2} where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and class2 = <" + class2 + ">)";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serq);
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      serq = "select distinct inst, instance from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {ref_inst} prop2 {instance},  {instance} rdf:type {class2} where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and class2 = <" + class2 + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq);
    }
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      serq = "select distinct inst, instance from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {instance} prop2 {ref_inst},  {instance} rdf:type {class2} where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and class2 = <" + class2 + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq);
    }
    if ((resultTable == null) || (resultTable.getRowCount() <= 0))
    {
      serq = "select distinct inst, instance from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {inst} prop1 {ref_inst}, {instance} prop2 {ref_inst},  {instance} rdf:type {class2} where (query_class =<" + query_class + "> " + "and prop1 =<" + prop1 + "> " + "and ref_class = <" + ref_class + "> " + "and prop2 = <" + prop2 + "> " + "and class2 = <" + class2 + ">)";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      resultTable = executeQuery(serq);
    }
    return retrieveRDFTripleValueFrom(resultTable, "instance");
  }
  
  public RDFEntityList getKBPropertiesBetweenClasses(String class1_uri, String class2_uri)
    throws Exception
  {
    RDFEntityList props = getPropertiesBetweenClasses(class1_uri, class2_uri);
    RDFEntityList slots = new RDFEntityList();
    for (RDFEntity Entprop : props.getAllRDFEntities())
    {
      RDFProperty prop = (RDFProperty)Entprop;
      System.out.println("Analyzing prop. " + prop.getURI());
      String preSerq2 = "select distinct instorigin, insttarget from {instorigin} rdf:type {class1}, {insttarget} rdf:type {class2}, {instorigin} rel {insttarget}  where ((rel =<" + prop.getURI() + ">) and " + " ((class1 =<" + class1_uri + "> and class2 =<" + class2_uri + ">) or " + "(class2 =<" + class1_uri + "> and class1 =<" + class2_uri + ">))) " + "limit 1 offset 0";
      
      QueryResultsTable resultTable = executeQuery(preSerq2);
      if ((resultTable != null) && (resultTable.getRowCount() > 0)) {
        slots.addRDFEntity(prop);
      }
    }
    return slots;
  }
  
  public RDFEntityList getPropertiesBetweenClasses(String class1_uri, String class2_uri)
    throws Exception
  {
    RDFEntityList slots = new RDFEntityList();
    if (isOWLRepository())
    {
      String serql = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {rel} rdf:type {X}, {term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where term2 =<" + class1_uri + "> and term1=<" + class2_uri + ">" + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      
      slots = retrieveFullPropertiesFrom(resultTable);
      if (slots == null) {
        slots = new RDFEntityList();
      }
      String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {rel} rdf:type {X},{term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}] where term1=<" + class1_uri + "> and term2= <" + class2_uri + ">" + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable2 = executeQuery(serql2);
      RDFEntityList slots2 = retrieveFullPropertiesFrom(resultTable2);
      slots.addAllRDFEntity(slots2);
    }
    else
    {
      String serql = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin},  {term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where term2 =<" + class1_uri + "> and term1=<" + class2_uri + ">" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      
      QueryResultsTable resultTable = executeQuery(serql);
      
      slots = retrieveFullPropertiesFrom(resultTable);
      if (slots == null) {
        slots = new RDFEntityList();
      }
      String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}] where term1=<" + class1_uri + "> and term2= <" + class2_uri + ">" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable2 = executeQuery(serql2);
      RDFEntityList slots2 = retrieveFullPropertiesFrom(resultTable2);
      slots.addAllRDFEntity(slots2);
    }
    return slots;
  }
  
  public RDFEntityList getPropertiesForGenericClass(String class_uri)
    throws Exception
  {
    RDFEntityList slots = new RDFEntityList();
    if (isOWLRepository())
    {
      String serq = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {X}, {origin} rdfs:subClassOf {genericterm}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where genericterm =<" + class_uri + ">" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, " + "{genericterm} rdfs:subClassOf {origin}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where genericterm =<" + class_uri + "> " + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serq);
      RDFEntityList slots2 = retrieveFullPropertiesFrom(resultTable);
      slots.addAllRDFEntity(slots2);
      
      String serq3 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {X}, {rango} rdfs:subClassOf {genericterm}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where genericterm =<" + class_uri + ">" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, " + "{genericterm} rdfs:subClassOf {rango}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where genericterm =<" + class_uri + "> " + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable2 = executeQuery(serq3);
      RDFEntityList slots3 = retrieveFullPropertiesFrom(resultTable2);
      slots.addAllRDFEntity(slots3);
    }
    else
    {
      String serq = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {origin} rdfs:subClassOf {genericterm}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where genericterm =<" + class_uri + ">" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, " + "{genericterm} rdfs:subClassOf {origin}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where genericterm =<" + class_uri + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serq);
      RDFEntityList slots2 = retrieveFullPropertiesFrom(resultTable);
      slots.addAllRDFEntity(slots2);
      
      String serq3 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {origin}, {rel} rdfs:range {rango},  {rango} rdfs:subClassOf {genericterm}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where genericterm =<" + class_uri + ">" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, " + "{genericterm} rdfs:subClassOf {rango}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where genericterm =<" + class_uri + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable2 = executeQuery(serq3);
      RDFEntityList slots3 = retrieveFullPropertiesFrom(resultTable2);
      slots.addAllRDFEntity(slots3);
    }
    String serq4 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdf:type {X}, {genericterm} rel {instance}, [{rel} rdfs:label {rellab}], [{rel} rdfs:domain {origin}, [{origin} rdfs:label {originlab}]], [{rel} rdfs:range {rango}, [{rango} rdfs:label {rangolab}]]  where genericterm =<" + class_uri + "> " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable3 = executeQuery(serq4);
    RDFEntityList slots4 = retrieveFullPropertiesFrom(resultTable3);
    slots.addAllRDFEntity(slots4);
    return slots;
  }
  
  public RDFEntityList getAllPropertiesBetweenClass_Literal(String classURI, String slot_value)
    throws Exception
  {
    RDFEntityList slots = new RDFEntityList();
    
    String serq1 = "SELECT distinct p, rellab from {instance} p {v}, {instance} rdf:type {origin}, [{p} rdfs:label {rellab}] where isLiteral(v) and v = \"" + slot_value + "\" and origin =<" + classURI + "> " + " and  (lang(rellab) = \"en\" or lang(rellab) = NULL) ";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable1 = executeQuery(serq1);
    RDFEntityList slotsaux = retrieveRDFEntityFrom(resultTable1, "property");
    for (RDFEntity slotaux : slotsaux.getAllRDFEntities()) {
      slots.addRDFEntity(getRDFProperty(slotaux.getURI()));
    }
    return slots;
  }
  
  public RDFEntityList getSchemaPropertiesForGenericClass(String classGeneric_uri, String class2_uri)
    throws Exception
  {
    RDFEntityList slots = new RDFEntityList();
    if (isOWLRepository())
    {
      String serq1 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {X}, {classGeneric} rdfs:subClassOf {origin}, {class2} rdfs:subClassOf {rango}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where (classGeneric =<" + classGeneric_uri + "> and class2 =<" + class2_uri + ">)" + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango},   {rel} rdf:type {X}, " + "{class2} rdfs:subClassOf {rango}, " + "{origin} rdfs:subClassOf {classGeneric}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where (classGeneric =<" + classGeneric_uri + "> and class2 =<" + class2_uri + "> )" + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable1 = executeQuery(serq1);
      RDFEntityList slots1 = retrieveFullPropertiesFrom(resultTable1);
      slots.addAllRDFEntity(slots1);
      
      String serq2 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {rango}, {rel} rdfs:range {origin},   {rel} rdf:type {X}, {classGeneric} rdfs:subClassOf {origin}, {class2} rdfs:subClassOf {rango}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where (classGeneric =<" + classGeneric_uri + "> and class2 =<" + class2_uri + ">)" + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {rango}, {rel} rdfs:range {origin},   {rel} rdf:type {X}, " + "{class2} rdfs:subClassOf {rango}, " + "{origin} rdfs:subClassOf {classGeneric}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where (classGeneric=<" + classGeneric_uri + "> and class2 =<" + class2_uri + ">)" + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable2 = executeQuery(serq2);
      RDFEntityList slots2 = retrieveFullPropertiesFrom(resultTable2);
      slots.addAllRDFEntity(slots2);
    }
    else
    {
      String serq1 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {rdf:Property}, {classGeneric} rdfs:subClassOf {origin}, {class2} rdfs:subClassOf {rango}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where classGeneric =<" + classGeneric_uri + "> and class2 =<" + class2_uri + ">" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {rdf:Property},  " + "{class2} rdfs:subClassOf {rango}, " + "{origin} rdfs:subClassOf {classGeneric}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where classGeneric =<" + classGeneric_uri + "> and class2 =<" + class2_uri + ">" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable1 = executeQuery(serq1);
      RDFEntityList slots1 = retrieveFullPropertiesFrom(resultTable1);
      slots.addAllRDFEntity(slots1);
      
      String serq2 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from {rel} rdfs:domain {rango}, {rel} rdfs:range {origin}, {rel} rdf:type {rdf:Property}, {classGeneric} rdfs:subClassOf {origin}, {class2} rdfs:subClassOf {rango}, [{rel} rdfs:label {rellab}], [{origin} rdfs:label {originlab}], [{rango} rdfs:label {rangolab}]  where classGeneric =<" + classGeneric_uri + "> and class2 =<" + class2_uri + ">" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>" + " UNION " + "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " + "{rel} rdfs:domain {rango}, {rel} rdfs:range {origin}, {rel} rdf:type {rdf:Property}, " + "{class2} rdfs:subClassOf {rango}, " + "{origin} rdfs:subClassOf {classGeneric}, " + "[{rel} rdfs:label {rellab}], " + "[{origin} rdfs:label {originlab}], " + "[{rango} rdfs:label {rangolab}] " + " where classGeneric=<" + classGeneric_uri + "> and class2 =<" + class2_uri + ">" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable2 = executeQuery(serq2);
      RDFEntityList slots2 = retrieveFullPropertiesFrom(resultTable2);
      slots.addAllRDFEntity(slots2);
    }
    return slots;
  }
  
  public RDFEntityList getKBPropertiesForGenericClass(String classGeneric_uri, String instance2_uri)
    throws Exception
  {
    RDFEntityList slots = new RDFEntityList();
    String serq;
    String serq;
    if (classGeneric_uri.startsWith("node")) {
      serq = "SELECT distinct rel, rellab from {instorigin} rdf:type {classGeneric}, {instorigin} rel {instance2}, [{rel} rdfs:label {rellab}] where (classGeneric =\"" + classGeneric_uri + "\"" + "and instance2 =<" + instance2_uri + ">)" + " and  (lang(rellab) =  \"en\" or lang(rellab) = NULL or lang(rellab) = \"en-us\")" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
    } else {
      serq = "SELECT distinct rel, rellab from {instorigin} rdf:type {classGeneric}, {instorigin} rel {instance2}, [{rel} rdfs:label {rellab}] where (classGeneric =<" + classGeneric_uri + "> " + "and instance2 =<" + instance2_uri + ">)" + " and  (lang(rellab) =  \"en\" or lang(rellab) = NULL or lang(rellab) = \"en-us\")" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
    }
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serq);
    slots = retrieveBasicPropertiesFrom(resultTable);
    String serq2;
    String serq2;
    if (classGeneric_uri.startsWith("node")) {
      serq2 = "SELECT distinct rel, rellab from {instorigin} rdf:type {classGeneric}, {instance2} rel {instorigin}, [{rel} rdfs:label {rellab}] where (classGeneric =\"" + classGeneric_uri + "\"" + " and instance2 =<" + instance2_uri + ">)" + " and  (lang(rellab) = \"en\" or lang(rellab) = NULL or lang(rellab) = \"en-us\")" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
    } else {
      serq2 = "SELECT distinct rel, rellab from {instorigin} rdf:type {classGeneric}, {instance2} rel {instorigin}, [{rel} rdfs:label {rellab}] where (classGeneric =<" + classGeneric_uri + "> " + "and instance2 =<" + instance2_uri + ">)" + " and  (lang(rellab) =  \"en\" or lang(rellab) = NULL or lang(rellab) = \"en-us\")" + " and not rel=<http://www.w3.org/2000/01/rdf-schema#seeAlso> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#value> and not rel=<http://www.w3.org/2000/01/rdf-schema#isDefinedBy> and not rel=<http://www.w3.org/2000/01/rdf-schema#member> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> and not rel=<http://proton.semanticweb.org/2004/12/protons#hasAlias> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel=<http://www.w3.org/2000/01/rdf-schema#comment> and not rel=<http://www.w3.org/2000/01/rdf-schema#label> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> and not rel=<http://www.w3.org/1999/02/22-rdf-syntax-ns#first> and not rel=<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
    }
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    resultTable = executeQuery(serq2);
    slots.addAllRDFEntity(retrieveBasicPropertiesFrom(resultTable));
    
    return slots;
  }
  
  public ArrayList<RDFPath> getKBIndirectRelationsWithLiterals(String class_URI, String literal)
    throws Exception
  {
    ArrayList<RDFPath> paths = new ArrayList();
    String serql = "select distinct property, reference, property2 from {instance} property {refInst}, {refInst} property2 {v}, {instance} rdf:type {origin}, {refInst} rdf:type {reference}, {property} rdfs:range {reference}, {property2} rdfs:domain {reference},  {origin} rdfs:subClassOf {dominio}, {property} rdfs:domain {dominio} where isLiteral(v) and v = \"" + literal + "\" and origin =<" + class_URI + ">";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultsTable = executeQuery(serql);
    paths.addAll(retrievePathFrom(resultsTable));
    for (RDFPath path : paths)
    {
      path.getRDFProperty1().setDomain(getDomainOfProperty(path.getRDFProperty1().getURI()));
      path.getRDFProperty1().setRange(getRangeOfProperty(path.getRDFProperty1().getURI()));
      
      path.getRDFProperty2().setDomain(getDomainOfProperty(path.getRDFProperty2().getURI()));
      path.getRDFProperty2().setRange(getRangeOfProperty(path.getRDFProperty2().getURI()));
    }
    return paths;
  }
  
  public RDFEntityList getInstanceProperties(String instance1_uri, String instance2_uri)
    throws Exception
  {
    RDFEntityList slots = new RDFEntityList();
    
    String serq = "SELECT distinct rel, rellab from {instance2} rel {instance1}, [{rel} rdfs:label {rellab}] where (instance1 =<" + instance1_uri + "> " + "and instance2 =<" + instance2_uri + ">)";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serq);
    slots = retrieveBasicPropertiesFrom(resultTable);
    
    String serq2 = "SELECT distinct rel, rellab from {instance1} rel {instance2}, [{rel} rdfs:label {rellab}] where (instance1 =<" + instance1_uri + "> " + "and instance2 =<" + instance2_uri + ">)";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    resultTable = executeQuery(serq2);
    slots.addAllRDFEntity(retrieveBasicPropertiesFrom(resultTable));
    return slots;
  }
  
  private Hashtable<RDFEntity, RDFEntityList> retrieveEquivalentEntityFrom(QueryResultsTable resultTable, String type)
    throws Exception
  {
    Hashtable<RDFEntity, RDFEntityList> equivalentEntityTable = new Hashtable();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return equivalentEntityTable;
    }
    int rowCount = resultTable.getRowCount();
    for (int row = 0; row < rowCount; row++)
    {
      Value rdfEntity = resultTable.getValue(row, 0);
      Value rdfEntityLabel = resultTable.getValue(row, 1);
      Value rdfEquivalentEntity = resultTable.getValue(row, 2);
      Value rdfEquivalentEntityLabel = resultTable.getValue(row, 3);
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
  
  private RDFEntityList retrieveRDFTripleValueFrom(QueryResultsTable resultTable, String type)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return entities;
    }
    int rowCount = resultTable.getRowCount();
    for (int row = 0; row < rowCount; row++)
    {
      Value value = resultTable.getValue(row, 0);
      if (value != null)
      {
        String valueString = value.toString().trim();
        if ((!valueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "List")))
        {
          String uri = value.toString();
          String label = getFirstEnLabel(uri);
          RDFEntity c = new RDFEntity(type, uri, label, getPluginID());
          
          value = resultTable.getValue(row, 1);
          if (value != null)
          {
            String uri_reference = value.toString();
            String label_reference = getFirstEnLabel(uri_reference);
            
            RDFEntity ref = new RDFEntity(type, uri_reference, label_reference, getPluginID());
            c.setRefers_to(ref);
          }
          entities.getAllRDFEntities().add(c);
        }
      }
    }
    return entities;
  }
  
  private RDFEntityList retrieveRDFValueFrom(QueryResultsTable resultTable, String type)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return entities;
    }
    int rowCount = resultTable.getRowCount();
    for (int row = 0; row < rowCount; row++)
    {
      Value value = resultTable.getValue(row, 0);
      if (value != null)
      {
        String valueString = value.toString().trim();
        if ((!valueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "List")))
        {
          String uri = value.toString();
          
          String label = null;
          value = resultTable.getValue(row, 1);
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
  
  private RDFEntityList retrieveRDFEntityForIndex(QueryResultsTable resultTable, String type)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return entities;
    }
    int rowCount = resultTable.getRowCount();
    for (int row = 0; row < rowCount; row++)
    {
      Value value = resultTable.getValue(row, 0);
      if (value != null)
      {
        String valueString = value.toString().trim();
        if ((!valueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "List"))) {
          if (MyURI.isURIValid(value.toString()))
          {
            String uri = value.toString();
            
            String label = null;
            value = resultTable.getValue(row, 1);
            if (value != null)
            {
              label = value.toString();
            }
            else
            {
              String title = null;
              if (type == "instance")
              {
                value = resultTable.getValue(row, 2);
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
    }
    return entities;
  }
  
  private RDFEntityList retrieveRDFEntityFrom(QueryResultsTable resultTable, String type)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return entities;
    }
    int rowCount = resultTable.getRowCount();
    for (int row = 0; row < rowCount; row++)
    {
      Value value = resultTable.getValue(row, 0);
      if (value != null)
      {
        String valueString = value.toString().trim();
        if ((!valueString.equalsIgnoreCase("http://www.w3.org/2002/07/owl#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/2000/01/rdf-schema#" + "Class")) && (!valueString.equalsIgnoreCase("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "List"))) {
          if (!MyURI.isURIValid(value.toString()))
          {
            if (value.toString().startsWith("node"))
            {
              System.out.println("reading blank node " + value.toString());
              entities.addNewRDFEntities(getUnionDefinitionForBlankNode(value.toString()));
            }
          }
          else
          {
            String uri = value.toString();
            
            String label = null;
            value = resultTable.getValue(row, 1);
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
    }
    return entities;
  }
  
  private RDFEntityList retrieveRDFEntityLiteralsFrom(QueryResultsTable resultTable, String instance_uri)
    throws Exception
  {
    RDFEntityList entities = new RDFEntityList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return entities;
    }
    int rowCount = resultTable.getRowCount();
    for (int row = 0; row < rowCount; row++)
    {
      Value value = resultTable.getValue(row, 0);
      if (value != null)
      {
        String finalValue = value.toString().trim();
        if ((finalValue.length() <= 40) && (finalValue.length() > 0))
        {
          RDFEntity c = new RDFEntity("literal", instance_uri, finalValue, getPluginID());
          entities.addRDFEntity(c);
        }
      }
    }
    return entities;
  }
  
  private ArrayList<String> retrieveEntitiesUriFrom(QueryResultsTable resultTable, boolean keepPrimitiveURIs)
    throws Exception
  {
    ArrayList<String> entities = new ArrayList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return entities;
    }
    int rowCount = resultTable.getRowCount();
    if (rowCount == 0) {
      return entities;
    }
    for (int row = 0; row < rowCount; row++)
    {
      Value value = resultTable.getValue(row, 0);
      if (value != null) {
        if (MyURI.isURIValid(value.toString())) {
          if ((keepPrimitiveURIs) || (
            (!value.toString().equals("http://www.w3.org/2002/07/owl#" + "Class")) && (!value.toString().equals("http://www.w3.org/2000/01/rdf-schema#" + "Class")) && (!value.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#" + "List")))) {
            entities.add(value.toString());
          }
        }
      }
    }
    return entities;
  }
  
  private ArrayList<RDFPath> retrieveFullPathFrom(QueryResultsTable resultTable)
    throws Exception
  {
    ArrayList<RDFPath> paths = new ArrayList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return paths;
    }
    int rowCount = resultTable.getRowCount();
    int columnCount = resultTable.getColumnCount();
    for (int row = 0; row < rowCount; row++)
    {
      RDFEntity ent = null;
      Value valueEnt = resultTable.getValue(row, 2);
      Value labelEnt = resultTable.getValue(row, 3);
      if ((valueEnt != null) && (MyURI.isURIValid(valueEnt.toString()))) {
        ent = new RDFEntity("class", valueEnt.toString(), labelEnt == null ? null : labelEnt.toString().trim(), getPluginID());
      }
      RDFProperty prop1 = null;
      RDFProperty prop1_domain = null;
      RDFProperty prop1_range = null;
      Value valueProp1 = resultTable.getValue(row, 0);
      Value labelProp1 = resultTable.getValue(row, 1);
      Value propertydomain = resultTable.getValue(row, 6);
      Value propertydomainlabel = resultTable.getValue(row, 7);
      Value propertyrange = resultTable.getValue(row, 8);
      Value propertyrangelabel = resultTable.getValue(row, 9);
      if ((valueProp1 != null) && (MyURI.isURIValid(valueProp1.toString()))) {
        prop1 = new RDFProperty(valueProp1.toString(), labelProp1 == null ? null : labelProp1.toString().trim(), getPluginID());
      }
      if ((propertydomain != null) && (MyURI.isURIValid(propertydomain.toString()))) {
        prop1_domain = new RDFProperty(propertydomain.toString(), propertydomainlabel == null ? null : propertydomainlabel.toString().trim(), getPluginID());
      }
      if ((propertyrange != null) && (MyURI.isURIValid(propertyrange.toString()))) {
        prop1_range = new RDFProperty(propertyrange.toString(), propertyrangelabel == null ? null : propertyrangelabel.toString().trim(), getPluginID());
      }
      prop1.setDomain(prop1_domain);
      prop1.setRange(prop1_range);
      
      RDFProperty prop2 = null;
      RDFProperty prop2_domain = null;
      RDFProperty prop2_range = null;
      Value valueProp2 = resultTable.getValue(row, 4);
      Value labelProp2 = resultTable.getValue(row, 5);
      Value propertydomain2 = resultTable.getValue(row, 10);
      Value propertydomainlabel2 = resultTable.getValue(row, 11);
      Value propertyrange2 = resultTable.getValue(row, 12);
      Value propertyrangelabel2 = resultTable.getValue(row, 13);
      if ((valueProp2 != null) && (MyURI.isURIValid(valueProp2.toString()))) {
        prop2 = new RDFProperty(valueProp2.toString(), labelProp2 == null ? null : labelProp2.toString().trim(), getPluginID());
      }
      if ((propertydomain2 != null) && (MyURI.isURIValid(propertydomain2.toString()))) {
        prop2_domain = new RDFProperty(propertydomain2.toString(), propertydomainlabel2 == null ? null : propertydomainlabel2.toString().trim(), getPluginID());
      }
      if ((propertyrange2 != null) && (MyURI.isURIValid(propertyrange2.toString()))) {
        prop2_range = new RDFProperty(propertyrange2.toString(), propertyrangelabel2 == null ? null : propertyrangelabel2.toString().trim(), getPluginID());
      }
      prop2.setDomain(prop2_domain);
      prop2.setRange(prop2_range);
      if (ent != null)
      {
        RDFPath path = new RDFPath(prop1, ent, prop2);
        paths.add(path);
      }
    }
    return paths;
  }
  
  private ArrayList<RDFPath> retrievePathFrom(QueryResultsTable resultTable)
    throws Exception
  {
    ArrayList<RDFPath> paths = new ArrayList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return paths;
    }
    int rowCount = resultTable.getRowCount();
    int columnCount = resultTable.getColumnCount();
    for (int row = 0; row < rowCount; row++)
    {
      RDFEntity ent = null;
      Value valueEnt = resultTable.getValue(row, 1);
      if ((valueEnt != null) && (MyURI.isURIValid(valueEnt.toString())))
      {
        String labelEnt = getFirstEnLabel(valueEnt.toString());
        ent = new RDFEntity("class", valueEnt.toString(), labelEnt == null ? null : labelEnt, getPluginID());
      }
      RDFProperty prop1 = null;
      Value valueProp1 = resultTable.getValue(row, 0);
      if ((valueProp1 != null) && (MyURI.isURIValid(valueProp1.toString())))
      {
        String labelProp1 = getFirstEnLabel(valueProp1.toString());
        prop1 = new RDFProperty(valueProp1.toString(), labelProp1 == null ? null : labelProp1, getPluginID());
      }
      RDFProperty prop2 = null;
      Value valueProp2 = resultTable.getValue(row, 2);
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
  
  private ArrayList<RDFPath> retrieveKBPathFrom(QueryResultsTable resultTable)
    throws Exception
  {
    ArrayList<RDFPath> paths = new ArrayList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return paths;
    }
    int rowCount = resultTable.getRowCount();
    int columnCount = resultTable.getColumnCount();
    for (int row = 0; row < rowCount; row++)
    {
      RDFEntity ent = null;
      Value valueEnt = resultTable.getValue(row, 2);
      Value labelEnt = resultTable.getValue(row, 3);
      if ((valueEnt != null) && (MyURI.isURIValid(valueEnt.toString()))) {
        ent = new RDFEntity("class", valueEnt.toString(), labelEnt == null ? null : labelEnt.toString().trim(), getPluginID());
      }
      RDFProperty prop1 = null;
      Value valueProp1 = resultTable.getValue(row, 0);
      Value labelProp1 = resultTable.getValue(row, 1);
      if ((valueProp1 != null) && (MyURI.isURIValid(valueProp1.toString()))) {
        prop1 = new RDFProperty(valueProp1.toString(), labelProp1 == null ? null : labelProp1.toString().trim(), getPluginID());
      }
      RDFProperty prop2 = null;
      Value valueProp2 = resultTable.getValue(row, 4);
      Value labelProp2 = resultTable.getValue(row, 5);
      if ((valueProp2 != null) && (MyURI.isURIValid(valueProp2.toString()))) {
        prop2 = new RDFProperty(valueProp2.toString(), labelProp2 == null ? null : labelProp2.toString().trim(), getPluginID());
      }
      RDFPath path = new RDFPath(prop1, ent, prop2);
      
      Value valueInstRef = resultTable.getValue(row, 6);
      Value labelInstRef = resultTable.getValue(row, 7);
      RDFEntity instRef = null;
      if ((valueInstRef != null) && (MyURI.isURIValid(valueInstRef.toString()))) {
        instRef = new RDFEntity("instance", valueInstRef.toString(), labelInstRef == null ? null : labelInstRef.toString().trim(), getPluginID());
      }
      Value valueInstOrg = resultTable.getValue(row, 8);
      Value labelInstOrg = resultTable.getValue(row, 9);
      RDFEntity instOrg = null;
      if ((valueInstOrg != null) && (MyURI.isURIValid(valueInstOrg.toString())))
      {
        instOrg = new RDFEntity("instance", valueInstOrg.toString(), labelInstOrg == null ? null : labelInstOrg.toString().trim(), getPluginID());
        
        instOrg.setRefers_to(instRef);
      }
      if (instOrg != null) {
        path.setKBAnswers(instOrg);
      }
      paths = RDFPath.mergePathLists(paths, path);
    }
    return paths;
  }
  
  private RDFEntityList retrieveFullPropertiesFrom(QueryResultsTable resultTable)
    throws Exception
  {
    RDFEntityList properties = new RDFEntityList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return properties;
    }
    int rowCount = resultTable.getRowCount();
    Hashtable<String, RDFProperty> propertiesTable = new Hashtable();
    for (int row = 0; row < rowCount; row++)
    {
      RDFProperty p = null;
      Value value = resultTable.getValue(row, 0);
      Value labelP = resultTable.getValue(row, 3);
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
        Value origin = resultTable.getValue(row, 1);
        Value labelOrigin = resultTable.getValue(row, 4);
        if ((origin != null) && (MyURI.isURIValid(origin.toString())))
        {
          RDFEntity classDomain = new RDFEntity("class", origin.toString().trim(), labelOrigin == null ? null : labelOrigin.toString().trim(), getPluginID());
          
          p.addDomain(classDomain);
        }
        Value range = resultTable.getValue(row, 2);
        Value labelRange = resultTable.getValue(row, 5);
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
  
  private RDFEntityList retrieveBasicPropertiesFrom(QueryResultsTable resultTable)
    throws Exception
  {
    RDFEntityList properties = new RDFEntityList();
    if ((resultTable == null) || (resultTable.getRowCount() <= 0)) {
      return properties;
    }
    int rowCount = resultTable.getRowCount();
    int columnCount = resultTable.getColumnCount();
    for (int row = 0; row < rowCount; row++)
    {
      RDFProperty p = null;
      Value rel = resultTable.getValue(row, 0);
      Value rel_label = resultTable.getValue(row, 1);
      if ((rel != null) && (MyURI.isURIValid(rel.toString())))
      {
        p = new RDFProperty(rel.toString(), rel_label == null ? null : rel_label.toString().trim(), getPluginID());
        if (!properties.isRDFEntityContained(p.getURI())) {
          properties.addRDFEntity(p);
        }
      }
    }
    return properties;
  }
  
  public String getFirstEnLabel(String entity_uri)
    throws Exception
  {
    String serql;
    String serql;
    if (entity_uri.startsWith("node")) {
      serql = "select label(l) from {" + entity_uri + "} rdfs:label {l} " + "where  (lang(l) = \"en\" or lang(l) = NULL) limit 1 ";
    } else {
      serql = "select label(l) from {<" + entity_uri + ">} rdfs:label {l} " + "where  (lang(l) = \"en\" or lang(l) = NULL) limit 1 ";
    }
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    if (resultTable.getRowCount() == 0) {
      return null;
    }
    Value v = resultTable.getValue(0, 0);
    if (v == null) {
      return null;
    }
    return v.toString();
  }
  
  public String getLabelOfEntity(String entity_uri)
    throws Exception
  {
    String serql = "select label(l) from {<" + entity_uri + ">} rdfs:label {l}";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    try
    {
      if (resultTable.getRowCount() == 0) {
        return null;
      }
      Value v = resultTable.getValue(0, 0);
      if (v == null) {
        return null;
      }
      return v.toString();
    }
    catch (Exception e) {}
    return null;
  }
  
  public RDFEntityList getUnionDefinitionForBlankNode(String node)
    throws Exception
  {
    RDFEntityList entList = new RDFEntityList();
    node = node.replaceFirst("_:", ":");
    String serql = "select c1, l1, c2, l2 from {class} <http://www.w3.org/2002/07/owl#unionOf> {x}, {x} rdf:first {c1}, [{c1} rdfs:label {l1}], {x} rdf:rest {} rdf:first {c2}, [{c2} rdfs:label {l2}] where class = \"" + node + "\"";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    if (resultTable == null) {
      return entList;
    }
    if (resultTable.getRowCount() == 0) {
      return entList;
    }
    for (int i = 0; i < resultTable.getRowCount(); i++)
    {
      Value ent1 = resultTable.getValue(i, 0);
      Value labelent1 = resultTable.getValue(i, 1);
      Value ent2 = resultTable.getValue(i, 2);
      Value labelent2 = resultTable.getValue(i, 3);
      if ((ent1 != null) && (ent1.toString().trim().length() > 0)) {
        entList.addRDFEntity(new RDFEntity("class", ent1.toString().trim(), labelent1 == null ? null : labelent1.toString().trim(), getPluginID()));
      }
      if ((ent2 != null) && (ent2.toString().trim().length() > 0)) {
        if (ent2.toString().startsWith("node"))
        {
          System.out.println("reading blank node recursively " + ent2.toString());
          entList.addNewRDFEntities(getUnionDefinitionForBlankNode(ent2.toString()));
        }
        else if (!ent2.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"))
        {
          entList.addRDFEntity(new RDFEntity("class", ent2.toString().trim(), labelent2 == null ? null : labelent2.toString().trim(), getPluginID()));
        }
      }
    }
    return entList;
  }
  
  public RDFEntityList getDomainOfProperty(String entity_uri)
    throws Exception
  {
    RDFEntityList domainList = new RDFEntityList();
    
    String serql = "select d, l from {<" + entity_uri + ">} rdfs:domain {d}, [{d} rdfs:label {l}]";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    if (resultTable == null) {
      return domainList;
    }
    if (resultTable.getRowCount() == 0) {
      return domainList;
    }
    for (int i = 0; i < resultTable.getRowCount(); i++)
    {
      Value domain = resultTable.getValue(i, 0);
      Value labelDomain = resultTable.getValue(i, 1);
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
  
  public QueryResultsTable executeQuery(String serql_query)
    throws Exception
  {
    QueryResultsTable resultsTableAux = null;
    try
    {
      resultsTableAux = getSesame_repository().performTableQuery(QueryLanguage.SERQL, serql_query);
    }
    catch (Exception e)
    {
      System.out.println(e + " Query can not be executed over " + this.repositoryName + " this repository " + serql_query);
    }
    return resultsTableAux;
  }
  
  public String[][] executeFreeQuery(String serql_query)
    throws Exception
  {
    QueryResultsTable resultsTable = null;
    resultsTable = executeQuery(serql_query);
    
    int rowCount = resultsTable.getRowCount();
    int columnCount = resultsTable.getColumnCount();
    if ((rowCount <= 0) || (columnCount <= 0)) {
      return (String[][])null;
    }
    String[][] results = new String[rowCount][columnCount];
    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columnCount; j++)
      {
        Value v = resultsTable.getValue(i, j);
        if ((v != null) && (!v.toString().trim().toLowerCase().equals("null"))) {
          results[i][j] = v.toString();
        }
      }
    }
    return results;
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
    RDFEntityList rangeList = new RDFEntityList();
    
    String serql = "select rt, l from {<" + property_uri + ">} rdfs:range {rt}, [{rt} rdfs:label {l}]";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultsTable = executeQuery(serql);
    if (resultsTable == null) {
      return rangeList;
    }
    for (int i = 0; i < resultsTable.getRowCount(); i++)
    {
      Value range = resultsTable.getValue(i, 0);
      Value labelRange = resultsTable.getValue(i, 1);
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
  
  private RDFEntityList getSubClassesFromIntersectionDefinition(String class_uri)
    throws Exception
  {
    String serql = "select c,l from {c} owl:intersectionOf {x}; [rdfs:label {l}], {x} p {<" + class_uri + ">}";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable r = executeQuery(serql);
    return retrieveRDFEntityFrom(r, "class");
  }
  
  private RDFEntityList getSuperClassesFromIntersectionDefinition(String class_uri)
    throws Exception
  {
    RDFEntityList classes = new RDFEntityList();
    String serql = "select c1, c2 from {<" + class_uri + ">} owl:intersectionOf {x}, " + "{x} rdf:first {c1}, " + "{x} rdf:rest {} rdf:first {c2} ";
    
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable r = executeQuery(serql);
    if ((r == null) || (r.getRowCount() <= 0)) {
      return classes;
    }
    if (r.getRowCount() <= 0) {
      return classes;
    }
    Value value = r.getValue(0, 0);
    
    classes.addRDFEntity(new RDFEntity("class", value.toString(), null, getPluginID()));
    
    value = r.getValue(0, 1);
    classes.addRDFEntity(new RDFEntity("class", value.toString(), null, getPluginID()));
    
    return classes;
  }
  
  private RDFEntityList findingMostSpecificClass(RDFEntityList classes)
    throws Exception
  {
    RDFEntityList results = classes.cloneEntityList();
    if ((classes == null) || (classes.isEmpty())) {
      return results;
    }
    for (Iterator i$ = classes.getAllRDFEntities().iterator(); i$.hasNext();)
    {
      c = (RDFEntity)i$.next();
      subs = getDirectSubClasses(c.getURI());
      if ((subs != null) && (!subs.isEmpty())) {
        for (RDFEntity c2 : classes.getAllRDFEntities()) {
          if ((!c.equals(c2)) && (subs.isRDFEntityContained(c2.getURI())))
          {
            results.removeRDFEntity(c);
            break;
          }
        }
      }
    }
    RDFEntity c;
    RDFEntityList subs;
    if (results.size() > 1) {
      for (RDFEntity e : results.getAllRDFEntities()) {
        if (e.getURI().toLowerCase().endsWith("#list"))
        {
          results.removeRDFEntity(e);
          break;
        }
      }
    }
    return results;
  }
  
  private URI createURI(ValueFactory myFactory, String entity_name, String namespace)
  {
    if (isURIString(entity_name)) {
      return myFactory.createURI(entity_name);
    }
    if ((namespace != null) && (namespace.trim().length() > 0)) {
      return myFactory.createURI(namespace, entity_name);
    }
    return null;
  }
  
  private boolean isURIString(String s)
  {
    if ((s.startsWith("http:")) || (s.startsWith("file:"))) {
      return true;
    }
    return false;
  }
  
  private void printout(RDFEntityList subs)
  {
    if (subs == null) {
      return;
    }
    System.out.println(subs.size() + " in total");
    for (int i = 0; i < subs.size(); i++) {
      System.out.println(((RDFEntity)subs.getAllRDFEntities().get(i)).getURI());
    }
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
  
  public SesameService getSesame_service()
  {
    return this.sesame_service;
  }
  
  public void setSesame_service(SesameService sesame_service)
  {
    this.sesame_service = sesame_service;
  }
  
  public SesameRepository getSesame_repository()
  {
    return this.sesame_repository;
  }
  
  public void setSesame_repository(SesameRepository sesame_repository)
  {
    this.sesame_repository = sesame_repository;
  }
  
  public ArrayList<SesameRepository> getRepositoriesList()
    throws Exception
  {
    ArrayList<SesameRepository> sesameRepositories = new ArrayList();
    RepositoryList listRepositories = getSesame_service().getRepositoryList();
    
    System.out.println("repository count = " + listRepositories.getRepositoryCount());
    List list = listRepositories.getReadableRepositories();
    System.out.println("Readable repository count = " + list.size());
    for (Iterator it = list.iterator(); it.hasNext();)
    {
      Object element = it.next();
      System.out.println("Getting repository id: " + ((RepositoryInfo)element).getRepositoryId() + " title: " + ((RepositoryInfo)element).getTitle());
      
      sesameRepositories.add(getRemoteRepository(((RepositoryInfo)element).getRepositoryId()));
    }
    return sesameRepositories;
  }
  
  public String getPluginID()
  {
    return this.repositoryName;
  }
  
  public RDFEntityList getEquivalentEntitiesForClass(String entity_uri)
    throws Exception
  {
    RDFEntityList aux = new RDFEntityList();
    
    String serql = " select distinct x, xl from {x} owl:equivalentClass {<" + entity_uri + ">}; [rdfs:label {xl}] ";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    aux = retrieveRDFEntityFrom(resultTable, "instance");
    
    serql = serql = " select distinct x, xl from {<" + entity_uri + ">} owl:equivalentClass {x}, [{x} rdfs:label {xl}] ";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    resultTable = executeQuery(serql);
    aux.addNewRDFEntities(retrieveRDFEntityFrom(resultTable, "instance"));
    return aux;
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForClasses()
    throws Exception
  {
    String serql = " select distinct ent, entl, eq, eql from {ent} owl:equivalentClass {eq}, [{ent} rdfs:label {entl}], [{eq} rdfs:label {eql}] ";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    return retrieveEquivalentEntityFrom(resultTable, "class");
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForProperties()
    throws Exception
  {
    String serql = " select distinct ent, entl, eq, eql from {ent} owl:equivalentProperty {eq}, [{ent} rdfs:label {entl}], [{eq} rdfs:label {eql}] ";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    return retrieveEquivalentEntityFrom(resultTable, "property");
  }
  
  public Hashtable<RDFEntity, RDFEntityList> getEquivalentEntitiesForInstances()
    throws Exception
  {
    String serql = " select distinct ent, entl, eq, eql from {ent} owl:sameAs {eq}, [{ent} rdfs:label {entl}], [{eq} rdfs:label {eql}] ";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    return retrieveEquivalentEntityFrom(resultTable, "instance");
  }
  
  public RDFEntityList getEquivalentEntitiesForProperty(String entity_uri)
    throws Exception
  {
    RDFEntityList aux = new RDFEntityList();
    
    String serql = " select distinct x, xl from {x} owl:equivalentProperty {<" + entity_uri + ">}; [rdfs:label {xl}] ";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    aux = retrieveRDFEntityFrom(resultTable, "instance");
    
    serql = serql = " select distinct x, xl from {<" + entity_uri + ">} owl:equivalentProperty {x}, [{x} rdfs:label {xl}] ";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    resultTable = executeQuery(serql);
    aux.addNewRDFEntities(retrieveRDFEntityFrom(resultTable, "instance"));
    return aux;
  }
  
  public RDFEntityList getEquivalentEntitiesForInstance(String entity_uri)
    throws Exception
  {
    RDFEntityList aux = new RDFEntityList();
    
    String serql = " select distinct x, xl from {x} owl:sameAs {<" + entity_uri + ">}; [rdfs:label {xl}] ";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    QueryResultsTable resultTable = executeQuery(serql);
    aux = retrieveRDFEntityFrom(resultTable, "instance");
    
    serql = serql = " select distinct x, xl from {<" + entity_uri + ">} owl:sameAs {x}, [{x} rdfs:label {xl}] ";
    MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
    resultTable = executeQuery(serql);
    aux.addNewRDFEntities(retrieveRDFEntityFrom(resultTable, "instance"));
    return aux;
  }
  
  public boolean existTripleForProperty(String entity)
  {
    try
    {
      String serql = "select p from {s} p {o} where p =<" + entity + "> limit 1 offset 0";
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      if (resultTable != null)
      {
        int rowCount = resultTable.getRowCount();
        if (rowCount > 0) {
          return true;
        }
      }
      return false;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return true;
  }
  
  public boolean existTripleForInstance(String entity)
  {
    try
    {
      String serql = "select p from {s} p {o}, {p} rdf:type {X}  where (s =<" + entity + "> or o = <" + entity + ">) " + " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + " or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " + " and not p = <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " + " and not p= <http://www.w3.org/2000/01/rdf-schema#label> " + " limit 1 offset 0";
      
      MappingSession.setSerqlCalls(MappingSession.getSerqlCalls() + 1);
      QueryResultsTable resultTable = executeQuery(serql);
      if (resultTable != null)
      {
        int rowCount = resultTable.getRowCount();
        if (rowCount > 0) {
          return true;
        }
      }
      return false;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return true;
  }
  
  public int numberOfAllTriples(String onto)
  {
    try
    {
      String serql = "select p from {s} p {o} limit 100000";
      QueryResultsTable resultTable = executeQuery(serql);
      if (resultTable != null) {
        return resultTable.getRowCount();
      }
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
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
      String serql = "select p from {s} p {o} where s =<" + p + "> limit 1 offset 0";
      QueryResultsTable resultTable = executeQuery(serql);
      if (resultTable != null) {
        return resultTable.getRowCount();
      }
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
    }
    return -1;
  }
  
  public int numberOfTriplesWithPropertyAsPredicate(String onto, String p)
  {
    try
    {
      String serql = "select p from {s} p {o} where p =<" + p + "> limit 1 offset 0";
      QueryResultsTable resultTable = executeQuery(serql);
      if (resultTable != null) {
        return resultTable.getRowCount();
      }
    }
    catch (Exception e)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
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
      String serql = "select c from {c} rdf:type {owl:Class} where c=<" + e + ">";
      QueryResultsTable resultTable = executeQuery(serql);
      if (resultTable != null)
      {
        int rowCount = resultTable.getRowCount();
        if (rowCount > 0) {
          return "Class";
        }
      }
      serql = "select c from {c} rdf:type {rdfs:Class} where c=<" + e + ">";
      resultTable = executeQuery(serql);
      if (resultTable != null)
      {
        int rowCount = resultTable.getRowCount();
        if (rowCount > 0) {
          return "Class";
        }
      }
      serql = "select p from {p} rdf:type {X} where  p=<" + e + "> and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " + "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) ";
      
      resultTable = executeQuery(serql);
      if (resultTable != null)
      {
        int rowCount = resultTable.getRowCount();
        if (rowCount > 0) {
          return "Property";
        }
      }
      serql = " select p from {p} rdf:type {rdf:Property} where  p=<" + e + ">";
      resultTable = executeQuery(serql);
      if (resultTable != null)
      {
        int rowCount = resultTable.getRowCount();
        if (rowCount > 0) {
          return "Property";
        }
      }
      return "Individual";
    }
    catch (Exception ex)
    {
      System.out.println("Exception counting number of triples to calculate trust in " + onto);
    }
    return null;
  }
  
  public static void main(String[] args)
  {
    try
    {
      SesamePlugin plugin = new SesamePlugin();
      plugin.loadConfiguration("http://kmi-web07.open.ac.uk:8080/sesame", "OWL", "russiaB", "sesame", "opensesame");
      plugin.initializeServer();
      
      System.out.println("------------------------------");
      System.out.println("Test1: get all classes");
      System.out.println("------------------------------");
      
      RDFEntityList entityList = plugin.getAllInstancesPeriodically(0, 1000);
      int offset = 0;
      while ((entityList != null) && (entityList.size() > 0))
      {
        System.out.println(entityList.size());
        System.out.print(entityList.toString());
        offset += 1000;
        entityList = plugin.getAllInstancesPeriodically(offset, 1000);
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}

