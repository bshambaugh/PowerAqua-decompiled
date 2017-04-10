package RemoteSPARQLPlugin;
import poweraqua.core.plugin.*;


import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.engineHTTP.QueryEngineHTTP;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import poweraqua.core.model.myocmlmodel.OcmlClass;
import poweraqua.core.model.myocmlmodel.OcmlInstance;
import poweraqua.core.model.myocmlmodel.OcmlProperty;
import poweraqua.core.model.myrdfmodel.MyURI;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.model.myrdfmodel.RDFPath;
import poweraqua.core.model.myrdfmodel.RDFProperty;
import poweraqua.core.model.myrdfmodel.constants.KswConstant;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.serviceConfig.RepositoryVirtuoso;
import virtuosoPlugin.Prefixes;
import virtuosoPlugin.ToLongException;
import virtuosoPlugin.VirtuosoPlugin;
import virtuosoPlugin.virtuosoHelpers.NotVirtuosoRepository;


/*
The QueryExecutionFactory has methods for creating a QueryExecution object for remote use. QueryExecutionFactory.sparqlService

These methods build a query execution object that uses the query engine in com.hp.hpl.jena.sparql.engine.http.

The remote request is made when the execSelect, execConstruct, execDescribe or execAsk method is called.
 */

/**
 *
 * @author vl474
 */
public class RemoteSPARQLPlugin implements OntologyPlugin, java.io.Serializable {

      public  void now()
    {
// System.setProperty("socksProxyHost", "socks.corp.com");
        String sparqlQueryString = "select distinct ?Concept where {[] a ?Concept } LIMIT 50";

        Query query = QueryFactory.create(sparqlQueryString);

        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);


        try {
            ResultSet results = qexec.execSelect();
            for ( ; results.hasNext() ; )
        {
            QuerySolution soln = results.nextSolution() ;
            String x = soln.get("Concept").toString();
            System.out.print(x +"\n");
        }

        }
        finally { qexec.close() ; }

        }

        public static void main(String[] args) {
            RemoteSPARQLPlugin test = new RemoteSPARQLPlugin();
            test.now();
        }




    //private String graph = "http://dbpedia.org/sparql" ;
    // this.repository.getServerURL() = "http://dbpedia.org/sparql"

    private ResultSet results;
    private RepositoryVirtuoso repository;
    private String name;

	private int timeoutLimit = 300000; //5 min
	private static int counter = 0;
    private static final String NOT_RDF_INSTANCES = ". FILTER( str(?i) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil>)";
	private static final String NOT_MIXED_CLASSES = " . FILTER( str(?c) != <http://www.w3.org/2000/01/rdf-schema#Class> " +
	"&& str(?c) != <http://www.w3.org/2000/01/rdf-schema#Literal> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Datatype> " +
	"&& str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> " +
	"&& str(?c) != <http://www.w3.org/2000/01/rdf-schema#Container> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> " +
	"&& str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> " +
	"&& str(?c) != <http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#member> " +
	"&& str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> " +
	"&& str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> " +
	"&& str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> && str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>" +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )";
	@SuppressWarnings("unused")
	private static final String NOT_RDF_CLASSES = " . FILTER( str(?c) != <http://www.w3.org/2000/01/rdf-schema#Class> " +
	"&& str(?c) != <http://www.w3.org/2000/01/rdf-schema#Literal> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#Datatype> " +
	"&& str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> " +
	"&& str(?c) != <http://www.w3.org/2000/01/rdf-schema#Container> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> " +
	"&& str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt> " +
	"&& str(?c) != <http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty> && str(?c) != <http://www.w3.org/2000/01/rdf-schema#member> " +
	"&& str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> " +
	"&& str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> " +
	"&& str(?c) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> )";
	private static final String NOT_OWL_CLASSES = " . FILTER(  str(?c) != <http://www.w3.org/2002/07/owl#AllDifferent> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#allValuesFrom> && str(?c) != <http://www.w3.org/2002/07/owl#AnnotationProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#backwardCompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#cardinality> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#Class> && str(?c) != <http://www.w3.org/2002/07/owl#complementOf> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#DataRange> && str(?c) != <http://www.w3.org/2002/07/owl#DatatypeProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedClass> && str(?c) != <http://www.w3.org/2002/07/owl#DeprecatedProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#differentFrom> && str(?c) != <http://www.w3.org/2002/07/owl#disjointWith> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#distinctMembers> && str(?c) != <http://www.w3.org/2002/07/owl#equivalentClass> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#equivalentProperty> && str(?c) != <http://www.w3.org/2002/07/owl#FunctionalProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#hasValue> && str(?c) != <http://www.w3.org/2002/07/owl#imports> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#incompatibleWith> && str(?c) != <http://www.w3.org/2002/07/owl#intersectionOf> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#InverseFunctionalProperty> && str(?c) != <http://www.w3.org/2002/07/owl#inverseOf>" +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#maxCardinality> && str(?c) != <http://www.w3.org/2002/07/owl#minCardinality> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#Nothing> && str(?c) != <http://www.w3.org/2002/07/owl#ObjectProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#oneOf> && str(?c) != <http://www.w3.org/2002/07/owl#onProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#Ontology> && str(?c) != <http://www.w3.org/2002/07/owl#OntologyProperty> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#priorVersion> && str(?c) != <http://www.w3.org/2002/07/owl#Restriction> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#sameAs> && str(?c) != <http://www.w3.org/2002/07/owl#someValuesFrom> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#SymmetricProperty> && str(?c) != <http://www.w3.org/2002/07/owl#Thing> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#TransitiveProperty> && str(?c) != <http://www.w3.org/2002/07/owl#unionOf> " +
	"&& str(?c) != <http://www.w3.org/2002/07/owl#versionInfo> )";
	private static final String NOT_RDF_PROPERTIES = ". FILTER( str(?rel) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> " +
    "&& str(?rel) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#member> " +
    "&& str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> " +
    "&& str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> " +
    "&& str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> " +
    "&& str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?rel) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> " +
    "&& str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#comment> " +
    "&& str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> " +
    "&& str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> " +
    "&& str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#subClassOf>)";
private static final String NOT_RDF_PROPERTIES1 = ". FILTER( str(?property) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> " +
"&& str(?property) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?property) != <http://www.w3.org/2000/01/rdf-schema#member>"  +
    "&& str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> " +
    "&& str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> " +
    "&& str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> " +
    "&& str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?property) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> " +
    "&& str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?property) != <http://www.w3.org/2000/01/rdf-schema#comment> " +
    "&& str(?property) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> " +
    "&& str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> " +
    "&& str(?property) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first>)";
private static final String NOT_RDF_PROPERTIES2 = ". FILTER(str(?property2) != <http://www.w3.org/2000/01/rdf-schema#seeAlso> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#value> " +
"&& str(?property2) != <http://www.w3.org/2000/01/rdf-schema#isDefinedBy> && str(?property2) != <http://www.w3.org/2000/01/rdf-schema#member> " +
    "&& str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_1> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_2> " +
    "&& str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_3> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_4> " +
    "&& str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_5> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_6> " +
    "&& str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#_7> && str(?property2) != <http://proton.semanticweb.org/2004/12/protons#hasAlias> " +
    "&& str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> && str(?property2) != <http://www.w3.org/2000/01/rdf-schema#comment> " +
    "&& str(?property2) != <http://www.w3.org/2000/01/rdf-schema#label> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> " +
    "&& str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> && str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#object> " +
    "&& str(?property2) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#first>)";



    public RemoteSPARQLPlugin() {

        // This is the NAME OF THE PLUGIN -in the OntologyPlugin class-
        // to be used on the configuration file
        setName(OntologyPlugin.PLUGIN_REMOTESPARQL);
    }


    public void loadConfiguration(String serverURL, String repositoryType, String repositoryName, String login, String pasword){

//        this.serverURL         = serverURL;
//        this.repositoryType    = repositoryType;
//        this.repositoryName    = repositoryName;
//        this.login             = login;
//        this.password          = pasword;
    }

    public void closePlugin(){
        //put some code if it's neccesary to free some resources, connections, ...
    }

    /**
     * Initialize a remote sesame server with an url
     * @throws java.lang.Exception
     */
    public void initializeServer() throws Exception{

    }

    /**
     * WEBSERVICE ACCESS (Behind a proxy). Initializing a remover sesame service,
     * which has been already up running
     * @param url
     * @param proxyHost
     * @param proxyPort
     * (i.e. Host:"wwwcache.open.ac.uk", Port:"80")
     * ****************************************************************************
     */
    public void initializeServer(String proxyHost, String proxyPort) throws Exception {
//            System.getProperties().setProperty( "http.proxyHost", proxyHost);
//            System.getProperties().setProperty( "http.proxyPort", proxyPort);
//            this.initializeServer();
    }

    /**
     *
     * @param login
     * @param password
     * @throws Exception
     * ***************************************************************************
     */
    public void logIntoServer() throws Exception {
//        System.out.println("Login " +  this.getLogin() + " pass " + this.getPassword());
//        this.getSesame_service().login(this.getLogin(), this.getPassword());
    }


    public void loadPlugin( poweraqua.serviceConfig.Repository repository) throws Exception{
    	if(repository instanceof RepositoryVirtuoso){
    		this.repository = (RepositoryVirtuoso) repository;
    	}else{
    		throw new NotVirtuosoRepository();
    	}
    }


    /**
     * Getting the specified remote sesame repository( which have already built up)
     * The repository type is RDF by default
     * @param repository the name of the remote repository
     * @throws java.lang.Exception
     * *****************************************************************************
     */

//    public SesameRepository getRemoteRepository(String repository) throws Exception  {
//        return getSesame_service().getRepository(repository);
//
//    }

    /**
     * Getting all the classes of the specified repository
     * @return a list of RDF entities
     * @throws java.lang.Exception
     */
    public RDFEntityList getAllClasses() throws Exception {
        try{
        String sparql="";
        RDFEntityList aux = new RDFEntityList();
        if(this.isOWLRepository()){
            sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
    		"SELECT distinct ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {{?c rdf:type owl:Class}. " +
    		"OPTIONAL {?c rdfs:label ?l}. " +
    		"FILTER (isURI(?c)) " + NOT_OWL_CLASSES + "}";

	         System.out.println("1: done");

             execute (sparql,-1);
             aux = retrieveRDFEntityFrom(KswConstant.CLASS_TYPE) ;
             if (aux.isEmpty()) System.out.println ("getClasses: Weird owl .. ");
        }
         // OWL CAN ALSO BE RDF ..
        sparql = Prefixes.RDF + Prefixes.RDFS +
		"SELECT distinct ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {{?c rdf:type rdfs:Class}. OPTIONAL {?c rdfs:label ?l}. FILTER (isURI(?c))" + NOT_MIXED_CLASSES + "}";
//        sparql = Prefixes.RDF  + Prefixes.RDFS +
//		"SELECT distinct ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {?c rdf:type rdfs:Class" +
//		". OPTIONAL{?c rdfs:label ?l} " +
//		". FILTER (isURI(?c))" +
//		"}";

//        sparql = "SELECT DISTINCT ?s ?p ?o " +
//		" FROM  <http://ontologies.com> " +
//     	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?s ?p ?o } " +
//     	"LIMIT 200" +
//     	"" ;
        System.out.println("2: done");

        execute (sparql,-1);
        aux.addNewRDFEntities(this.retrieveRDFEntityFrom(KswConstant.CLASS_TYPE)) ;
        return aux;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

//The following example code adds two files, one local and one available through HTTP, to a repository:
//
//   File file = new File("/path/to/example.rdf");
//   String baseURI = "http://example.org/example/local";
//   con = myRepository.getConnection();
//   try {
//      con.add(file, baseURI, RDFFormat.RDFXML);
//
//      URL url = new URL("http://example.org/example/remote");
//      con.add(url, url.toString(), RDFFormat.RDFXML);
//   }
//   finally {
//      con.close();
//   }

   public RDFEntityList getAllInstancesOfClassPeriodically(String class_uri,
                int offset, int limit) throws Exception {
       try{
        String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
		"SELECT distinct ?i ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {{?i rdf:type <" + class_uri + ">}. OPTIONAL {?i rdfs:label ?l} FILTER (isURI(?i))} " +
				"LIMIT " + limit + " OFFSET " + offset;

          execute (sparql,-1);
        return this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE);
        } finally {
          //results.close(); // needed for repository(sesame)
        }
   }

    public RDFEntityList getAllClassesPeriodically(int offset, int limit) throws Exception {
      try{
        String sparql="";
        RDFEntityList aux = new RDFEntityList();
        if(this.isOWLRepository()){
            sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
    		"SELECT distinct ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {{?c rdf:type owl:Class}. OPTIONAL {?c rdfs:label ?l} FILTER (isURI(?c)) " + NOT_OWL_CLASSES + "}" +
				"LIMIT " + limit + " OFFSET " + offset;

              execute (sparql,-1);
            aux = retrieveRDFEntityFrom(KswConstant.CLASS_TYPE) ;
        }
        sparql = Prefixes.RDF + Prefixes.RDFS +
		"SELECT distinct ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE {{?c rdf:type rdfs:Class}. " +
		"OPTIONAL {?c rdfs:label ?l} " +
		"FILTER (isURI(?c))" +
		NOT_MIXED_CLASSES +
		"}" +
		"LIMIT " + limit + " OFFSET " + offset;

          execute (sparql,-1);
        aux.addNewRDFEntities (this.retrieveRDFEntityFrom(KswConstant.CLASS_TYPE));
        return  aux;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * This method retrieves all the properties containted in the repository
     * Each entity contained in the list is an RDFProperty object, which comprises
     * uri, label, domain, and range. Please note that domain and range may be empty
     * in owl.
     */
    public RDFEntityList getAllProperties() throws Exception {
        try{
        RDFEntityList aux = new RDFEntityList();
        String sparql="";
       if (this.isOWLRepository()){
    	   sparql = Prefixes.RDF + Prefixes.RDFS +
			"SELECT distinct ?p ?d ?r ?l ?dl ?rl FROM <" + this.repository.getGraphIRI() + "> WHERE {{?p rdf:type ?X}. " +
			"OPTIONAL {?p rdfs:domain ?d . OPTIONAL { ?d rdfs:label ?dl} } " +
			"OPTIONAL {?p rdfs:range ?r . OPTIONAL { ?r rdfs:label ?rl} } " +
			"OPTIONAL {?p rdfs:label ?l} " +
			"FILTER (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " +
			"|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> )}";  //NICO maybe Union

              execute (sparql,-1);
        }
       else {
		sparql = Prefixes.RDF + Prefixes.RDFS +
			"SELECT distinct ?p ?d ?r ?l ?dl ?rl FROM <" + this.repository.getGraphIRI() + "> WHERE {{?p rdf:type rdf:Property}. " +
			"OPTIONAL {?p rdfs:domain ?d . OPTIONAL { ?d rdfs:label ?dl} }. " +
			"OPTIONAL {?p rdfs:range ?r . OPTIONAL { ?r rdfs:label ?rl} }. " +
			"OPTIONAL {?p rdfs:label ?l}}";

         execute (sparql,-1);
       }

        aux.addNewRDFEntities(this.retrieveFullPropertiesFrom());
        return aux;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    public RDFEntityList getAllPropertiesPeriodically(int offset, int limit) throws Exception {
        try{
        RDFEntityList aux = new RDFEntityList();
        String sparql="";
        if (this.isOWLRepository()){
     	   sparql = Prefixes.RDF + Prefixes.RDFS +
			"SELECT distinct ?p ?d ?r ?l ?dl ?rl FROM <" + this.repository.getGraphIRI() + "> WHERE {{?p rdf:type ?X}. " +
			"OPTIONAL {?p rdfs:domain ?d . OPTIONAL { ?d rdfs:label ?dl} } " +
			"OPTIONAL {?p rdfs:range ?r . OPTIONAL { ?r rdfs:label ?rl} } " +
			"OPTIONAL {?p rdfs:label ?l} " +
			"FILTER (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " +
			"|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> )}" + //NICO Maybe Union
			"LIMIT " + limit + " OFFSET " + offset;

              execute (sparql,-1);
            aux =this.retrieveFullPropertiesFrom();
        }
//        else {
//            serql=" select p,o,r,l,ol,rl from " +
//            "{p} rdf:type {rdf:Property}, " +
//            "[{p} rdfs:range {r}, [{r} rdfs:label {rl}]], " +
//            "[{p} rdfs:domain {o}, [{o} rdfs:label {ol}]], " +
//            "[{p} rdfs:label {l}] " +
//            "limit " + limit + " offset " + offset;
//            MappingSession.serqlCalls++;
//            execute (serql);
//            aux = this.retrieveFullPropertiesFrom(result);
//        }

        return aux;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    public ArrayList<RDFPath> getSchemaIndirectRelations (String sourceURI, String targetURI) throws Exception {
        try {
        ArrayList<RDFPath> paths = new ArrayList<RDFPath>();
        Logger log_poweraqua = Logger.getLogger("poweraqua");
        // temporal solution for avoiding"
        // Number of indirect paths between http://dbpedia.org/ontology/Film and http://umbel.org/umbel/sc/Spain :0
        if (this.repository.getRepositoryName().equals("bbc_backstage")) {
            log_poweraqua.log (Level.INFO, "TOO EXPENSIVE: Number of indirect paths between " + sourceURI + " and " + targetURI + " :" + paths.size());
        }
        else {
        // DIRECT AND INDIRECT
        	String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
    		"SELECT distinct ?property ?proplabel ?reference ?reflabel ?property2 ?prop2label ?subject ?originlabel ?destiny ?targetlabel" +
            " FROM  <" + this.repository.getGraphIRI() + "> WHERE {?instOrigin rdf:type ?subject. ?instRef rdf:type ?reference. ?instTarget rdf:type ?destiny. " +
           "?instOrigin ?property ?instRef. ?instRef ?property2 ?instTarget. " +
//           " ?property  rdfs:range ?reference. " +
//           " ?property2 rdfs:domain ?reference. " + too expensive with umbel/River and umbel/Russia
            "OPTIONAL {?property rdfs:label ?proplabel}. " +
           "OPTIONAL {?reference rdfs:label ?reflabel}. " +
           "OPTIONAL {?subject rdfs:label ?originlabel}. " +
           "OPTIONAL {?destiny rdfs:label ?targetlabel}. " +
           "OPTIONAL {?property2 rdfs:label ?prop2label}. " +
           "FILTER (" +
           "(str(?subject) = <"+ sourceURI +"> && str(?destiny) = <"+ targetURI + "> )" +
    			"|| " +
    			"(str(?subject) = <"+ targetURI + "> && str(?destiny) = <"+ sourceURI + "> )" +   //NICO May be UNION
           "&& " +
           "str(?reference) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> " +
           "&& str(?reference) != <http://www.w3.org/2000/01/rdf-schema#Resource> " +
           "&& str(?reference) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#List>" +
           ")" +
           NOT_RDF_PROPERTIES1 +
           NOT_RDF_PROPERTIES2 +
           "}";

          execute (sparql,-1);
        paths.addAll(this.retrieveFullPathFrom());
        }
//
////       if (paths.isEmpty())  {
////           // TOO EXPENSIVE
////            serql=  "select distinct property, proplabel, reference, reflabel, property2, prop2label, " +
////                " reference, reflabel, subject, originlabel, reference, reflabel, destiny, targetlabel from " +
////                "{instOrigin} rdf:type {subject}, {instRef} rdf:type {reference}, {instTarget} rdf:type {destiny}, " +
////                "{instRef} property {instOrigin}, {instRef} property2 {instTarget}, " +
////                " {property}  rdfs:domain {reference}, " +
////                " {property2} rdfs:domain {reference}, " +
////                "[{property} rdfs:label {proplabel}], " +
////                "[{reference} rdfs:label {reflabel}], " +
////                "[{subject} rdfs:label {originlabel}], " +
////                "[{destiny} rdfs:label {targetlabel}], " +
////                "[{property2} rdfs:label {prop2label}] " +
////                " FROM  <" + this.repository.getGraphIRI() + "> WHERE subject=<"+ sourceURI +"> " +
////                "and destiny=<"+ targetURI + "> " +
////                "and not reference =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Statement> " +
////                "and not reference = <http://www.w3.org/2000/01/rdf-schema#Resource> " +
////                "and not reference = <http://www.w3.org/1999/02/22-rdf-syntax-ns#List> " +
////                this.NOT_RDF_PROPERTIES1 + this.NOT_RDF_PROPERTIES2;
////         MappingSession.serqlCalls++;
////           execute (serql);
////         paths.addAll(this.retrieveFullPathFrom (result));
////   }
    return paths;
    } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

   public boolean isKBTripleClassClass (String class1_uri, String relation, String class2_uri) throws Exception{
     try{
       String sparql1 =
		"ASK FROM <" + this.repository.getGraphIRI() + "> " +
				"WHERE { ?instorigin rdf:type <" + class1_uri + ">. ?insttarget rdf:type <" + class2_uri + ">. ?instorigin <" + relation + "> ?insttarget . " +
//		"?class1Help rdfs:subClassOf ?class1 OPTION(TRANSITIVE, T_DISTINCT). " +
//		"?class2Help rdfs:subClassOf ?class2 OPTION(TRANSITIVE, T_DISTINCT). " +
		"}";

          execute (sparql1,-1);
        if (results == null || results.hasNext() == false) {
            String sparql2 =
        		"ASK FROM <" + this.repository.getGraphIRI() + "> " +
        				"WHERE { ?instorigin rdf:type <" + class1_uri + ">. ?insttarget rdf:type <" + class2_uri + ">. ?insttarget <" + relation + "> ?instorigin}";
//    		"ASK FROM <" + this.repository.getGraphIRI() + "> WHERE { ?instorigin rdf:type ?class1Help. ?insttarget rdf:type ?class2Help. ?insttarget ?rel ?instorigin . " +
//			"?class1Help rdfs:subClassOf ?class1 OPTION(TRANSITIVE, T_DISTINCT). " +
//			"?class2Help rdfs:subClassOf ?class2 OPTION(TRANSITIVE, T_DISTINCT). " +
//    		"FILTER( str(?class1) = <" + class1_uri + ">  && str(?class2) = <" + class2_uri + "> && str(?rel) = <" + relation + ">)}";

              execute (sparql2,-1);
              if (results == null || results.hasNext() == false)
                  return false;
        }
        return true;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    public boolean isKBTripleClassInstance (String class_uri, String relation, String instance_uri) throws Exception{
        try{
        String sparql =
        	"ASK FROM <" + this.repository.getGraphIRI() + "> " +
        	"WHERE { ?instorigin rdf:type <" + class_uri + ">.  ?instorigin <" + relation + "> <" + instance_uri + ">}";
//			"ASK FROM <" + this.repository.getGraphIRI() + "> " +
//	    	"WHERE { ?instorigin rdf:type ?classGenericHelp.  ?instorigin ?rel ?instance2.  " +
//	//    	"?classGenericHelp rdfs:subClassOf ?classGeneric OPTION(TRANSITIVE, T_DISTINCT). " +
//	    	"FILTER( str(?classGeneric) = <" + class_uri + "> && str(?instance2) = <" + instance_uri + "> && str(?rel) = <" + relation + ">)" +
//			"}";

          execute (sparql,-1);
        if (results==null || results.hasNext() == false) {

            sparql =
        		"ASK FROM <" + this.repository.getGraphIRI() + "> " +
        		"WHERE  { ?instorigin rdf:type <" + class_uri + ">.  <" + instance_uri + ">  <" + relation + "> ?instorigin}";
//    		"ASK FROM <" + this.repository.getGraphIRI() + "> " +
//    		"WHERE  { ?instorigin rdf:type ?classGenericHelp.  ?instance2  ?rel ?instorigin . " +
//   		"?classGenericHelp rdfs:subClassOf ?classGeneric OPTION(TRANSITIVE, T_DISTINCT). " +
//    		"FILTER( str(?classGeneric) = <" + class_uri + "> && str(?instance2) = <" + instance_uri + "> && str(?rel) = <" + relation + ">)}";

              execute (sparql,-1);
              if (results==null || results.hasNext() == false)
                  return false;
        }
       return true;
          } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    public boolean isKBTripleInstanceInstance (String instance1_uri, String relation, String instance2_uri) throws Exception{
        try{
        String sparql = "ASK FROM <" + this.repository.getGraphIRI() + "> WHERE  { <" + instance2_uri + ">  <" + relation + "> <" + instance1_uri + ">}";

          execute (sparql,-1);
        if (results==null || results.hasNext() == false) {
            String sparql2 = Prefixes.RDF  +
    		"ASK FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance1_uri + ">  <" + relation + "> <" + instance2_uri + ">}";

              execute (sparql2,-1);
              if (results==null || results.hasNext() == false)
                  return false;
        }
        return true;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


   public ArrayList<RDFPath> getKBIndirectRelations (String class_sourceURI, String instance_targetURI) throws Exception{
        try{

        ArrayList<RDFPath> paths = new ArrayList<RDFPath>();
        // For performance reasons in particular with umbel class that most of the times had no instances
        String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
		"ASK FROM <" + this.repository.getGraphIRI() + "> WHERE  {?instOrigin rdf:type <"+ class_sourceURI +">}" ;
        execute (sparql,-1);
        if (results==null || results.hasNext() == false)
            return paths;
       // if I put {instdestiny} rdf:type {classRange}, {property2} rdfs:range {classRange} takes ages because it has many types!
        sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
				"SELECT distinct ?property ?reference ?property2 " +
				" FROM  <" + this.repository.getGraphIRI() + "> WHERE { " +
                                " ?instorigin rdf:type <"+ class_sourceURI +">.  " +
                                " ?referencekb rdf:type ?reference. " +
                                " ?referencekb ?property2 <"+ instance_targetURI + ">. " +
                                "{{ " +
                                " ?property rdfs:range ?reference. " +
                                " ?property2 rdfs:domain ?reference. " +
                                " ?instorigin ?property ?referencekb. } " +
                                " UNION " +
                                " {?property  rdfs:domain ?reference. " +
                                " ?property2  rdfs:domain ?reference. " +
                                " ?referencekb ?property ?instorigin}}} " ;


//            " UNION " + // expensive: e.g. <http://umbel.org/umbel/sc/City> and instdestiny=<http://www.bbc.co.uk/programmes/b00kctm4#programme>
//             "select distinct property, reference, property2  from " +
//            " {instorigin} rdf:type {classGeneric}, {instdestiny} property2 {referencekb}, {referencekb} property {instorigin}, " +
//            "{referencekb} rdf:type {reference},  " +
//            "{property}  rdfs:domain {reference},  {property2} rdfs:range {reference} " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE classGeneric=<"+ class_sourceURI +"> " +
//            "and instdestiny=<"+ instance_targetURI + ">" ; //{property} rdfs:range {classGeneric}
//        MappingSession.serqlCalls++;
        //System.out.println("aaaaayyyyy " + serql);
          execute (sparql,-1);
        paths.addAll(this.retrievePathFrom ());
        for (RDFPath path:paths) {
         path.getRDFProperty1().setDomain(this.getDomainOfProperty(path.getRDFProperty1().getURI()));
         path.getRDFProperty1().setRange(this.getRangeOfProperty(path.getRDFProperty1().getURI()));
         path.getRDFProperty2().setDomain(this.getDomainOfProperty(path.getRDFProperty2().getURI()));
         path.getRDFProperty2().setRange(this.getRangeOfProperty(path.getRDFProperty2().getURI()));

        }
        return paths;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    public ArrayList<RDFPath> getInstanceIndirectRelations (String instance_sourceURI, String instance_targetURI) throws Exception{
        try{
        ArrayList<RDFPath> paths = new ArrayList<RDFPath>();
        String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
						"SELECT distinct ?property ?referenceEnt ?property2 " +
						" FROM  <" + this.repository.getGraphIRI() + "> WHERE { <"+ instance_sourceURI +"> ?property ?reference. ?reference ?property2 <"+ instance_targetURI + ">. " +
//						"reference serql:directType {referenceEnt}  " + ... //That's SERQL
				        " ?reference rdf:type ?referenceEnt }" ;

          execute (sparql,-1);
        paths.addAll(this.retrievePathFrom ());

        sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
				"SELECT distinct ?property ?referenceEnt ?property2 " +
				" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?reference ?property <"+ instance_sourceURI +"> . ?reference ?property2 <"+ instance_targetURI + ">. " +
//				"reference serql:directType {referenceEnt}  " + ... //That's SERQL
		        " ?reference rdf:type ?referenceEnt " +
		        "FILTER(str(?property) != ?property2)" +
        		"}" ;

          execute (sparql,-1);
        paths.addAll(this.retrievePathFrom ());

        sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
				"SELECT distinct ?property ?referenceEnt ?property2 " +
				" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?reference ?property <"+ instance_sourceURI +"> . <"+ instance_targetURI + ">  ?property2 ?reference. " +
//              "[{property} rdfs:label {proplabel}], " +//That's SERQL
//              "[{referenceEnt} rdfs:label {reflabel}], " +//That's SERQL
//              "[{property2} rdfs:label {prop2label}] " +//That's SERQL
		        " ?reference rdf:type ?referenceEnt " +
        		"}" ;

          execute (sparql,-1);
        paths.addAll(this.retrievePathFrom ());

        for (RDFPath path:paths) {
         path.getRDFProperty1().setDomain(this.getDomainOfProperty(path.getRDFProperty1().getURI()));
         path.getRDFProperty1().setRange(this.getRangeOfProperty(path.getRDFProperty1().getURI()));
         path.getRDFProperty2().setDomain(this.getDomainOfProperty(path.getRDFProperty2().getURI()));
         path.getRDFProperty2().setRange(this.getRangeOfProperty(path.getRDFProperty2().getURI()));
        }
        return paths;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
     }

    private RDFEntityList getAllSubClassesWithInference (String class_uri) throws Exception {
        try{
        RDFEntityList classes =new RDFEntityList();
        String sparql;
        if (class_uri.startsWith("node")) {
        	sparql = Prefixes.RDFS +
        	"SELECT DISTINCT ?c ?l " +
        	" FROM  <" + this.repository.getGraphIRI() + ">" +
        	"WHERE {" +
        		"{SELECT DISTINCT ?c ?path ?route ?jump " +
        		"WHERE{" +
        			"{SELECT ?c ?Class WHERE { ?c rdfs:subClassOf ?Class}" +
    			"} " +
				"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?c), t_out(?Class), t_step('path_id') as ?path, t_step(?c) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " +
				"FILTER ( ?Class = \"" + class_uri +"\" )}" + //not replaceable
				"}. OPTIONAL{ ?c rdfs:label ?l}" +
				"}";
//            serql="select distinct sc,l from {c} rdfs:subClassOf {\"" + class_uri +"\"}, [{sc} rdfs:label {l}] "+
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE not sc = \""+class_uri+"\"";
        }
        else {
        	sparql = Prefixes.RDFS +
        	"SELECT DISTINCT ?c ?l " +
        	" FROM  <" + this.repository.getGraphIRI() + ">" +
        	"WHERE {" +
        		"{SELECT DISTINCT ?c ?path ?route ?jump " +
        		"WHERE{" +
        			"{SELECT ?c ?Class WHERE { ?c rdfs:subClassOf ?Class}" +
    			"} " +
				"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?c), t_out(?Class), t_step('path_id') as ?path, t_step(?c) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " +
				"FILTER ( ?Class = <"+ class_uri +"> )}" + //NICO is OPTION still posible, if ?Class is fix??? I think its not
			"}. OPTIONAL{ ?c rdfs:label ?l}" +
			"}";
//		    		"SELECT distinct ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE{ ?c rdfs:subClassOf ?C " +
//		    				"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?C), t_out(?c), t_step('path_id') as ?path, t_step(?C) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " +
//		    		"OPTIONAL{ ?c rdfs:label ?l} " +
//		            ". FILTER(str(?c) != <"+ class_uri +"> && str(?C) = <"+ class_uri +">)}" ;
        }

          execute (sparql,-1);
        classes=this.retrieveRDFEntityFrom( KswConstant.CLASS_TYPE) ;
        if (classes==null)
            classes=new RDFEntityList();
        //retrieving classes defined by owl:intersectionOf
//        if (this.isOWLRepository()) {
//            RDFEntityList c2 = this.getSubClassesFromIntersectionDefinition(class_uri);
//            classes.addRDFEntities(c2);
//        }
        return classes;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }
    /**
     * Getting all sub classes of the specified class. It uses RDFS inference so it
     * will return all the subclasses, no just the inmediate one
     * @param class_uri the uri string of the specified class
     * @return the list of all sub classes
     * @throws java.lang.Exception
     */
    public RDFEntityList getAllSubClasses(String class_uri) throws Exception {
        try{
//        RDFEntityList directclasses =new RDFEntityList();
        RDFEntityList classes =new RDFEntityList();
        // All Sub Classes
        classes = getAllSubClassesWithInference (class_uri);
//        //direct Classes
//        directclasses = getAllSubClassesWithInference (class_uri);
//        // START THE INFERENCE
//        while (!directclasses.isEmpty()) {
//            RDFEntityList aux = new RDFEntityList();
//            for (RDFEntity directclass: directclasses.getAllRDFEntities()) {
//                if (!classes.isExactRDFEntityContained(directclass)) {
////                    System.out.println("ayyyyy inferencing classes for the subclass " + directclass.getURI());
//                    classes.addRDFEntity(directclass);
//                    aux.addNewRDFEntities(this.getAllSubClassesWithInference (directclass.getURI()));
//                }
//            }
//            directclasses = aux;
//        }

        return classes;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    public RDFEntityList getDirectSubClasses(String class_uri) throws Exception {
        try{
        String sparql;
        RDFEntityList classes;
//        serql="select distinct c, l " +
//        		"from {c} rdfs:subClassOf {<" + class_uri + ">}, [{c} rdfs:label {l}] " +
//                " FROM  <" + this.repository.getGraphIRI() + "> WHERE isURI(c) and not c=<" + class_uri + "> " +
//                "minus " +
//                "select distinct c, l from {c} rdfs:subClassOf {c2} rdfs:subClassOf {<" + class_uri + ">} " +
//                ", [{c} rdfs:label {l}] FROM <" + this.repository.getGraphIRI() + "> WHERE isURI(c) and isURI(c2) " +
//                "and not c2=<" + class_uri + ">  and not c=c2" ;
        sparql = "SELECT DISTINCT ?c ?l " +
    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?c rdfs:subClassOf <" + class_uri + "> . " +
    	"OPTIONAL{?c rdfs:label ?l} . " +
    	"FILTER ( isURI(?c) && str(?c) != <" + class_uri + "> ) . }" ;



          execute (sparql,-1);
        classes= this.retrieveRDFEntityFrom(KswConstant.CLASS_TYPE) ;
        if (classes == null) classes = new RDFEntityList();
        //retrieving classes defined by owl:intersectionOf
        if (this.isOWLRepository()) {
            RDFEntityList c2 = this.getSubClassesFromIntersectionDefinition(class_uri);
            if (classes==null)
                return c2;
            classes.addNewRDFEntities(c2);
        }
        return classes;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Getting all super class of the specified class
     * @param class_uri the uri of the specified class
     * @return a list of RDF entities
     * @throws java.lang.Exception
     */
    private RDFEntityList getAllSuperClassesWithInference(String class_uri) throws Exception {
        try{
            // inference is not automatic in Sesame 2 when no tusing native repositories ...
            // careful with cycles ...

        String sparql;
        if (class_uri.startsWith("node") || class_uri.startsWith("_:node")) {
//             serql="select distinct sc,l from {\"" + class_uri +"\"} rdfs:subClassOf {sc}, [{sc} rdfs:label {l}] "+
//                " FROM  <" + this.repository.getGraphIRI() + "> WHERE not sc = \""+class_uri+"\"";
             sparql = "SELECT DISTINCT ?c ?l " +
		     		" FROM  <" + this.repository.getGraphIRI() + ">" +
		     		"WHERE {" +
		     			"{SELECT DISTINCT ?c ?path ?route ?jump " +
		     			"WHERE{" +
		     				"{SELECT ?c ?Class WHERE { ?Class rdfs:subClassOf ?c}" +
	     				"} " +
	     				"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?c), t_out(?Class), t_step('path_id') as ?path, t_step(?c) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " +
	     				"FILTER ( ?Class = \""+ class_uri +"\" )}" +
					"}. OPTIONAL{ ?c rdfs:label ?l}" +
					"}";
        }
        else {
//             serql="select distinct sc,l from {<"+class_uri+">} rdfs:subClassOf {sc}, [{sc} rdfs:label {l}] "+
//                " FROM  <" + this.repository.getGraphIRI() + "> WHERE not sc =<"+class_uri+">";
             sparql = Prefixes.RDFS + "SELECT DISTINCT ?c ?l FROM  <" + this.repository.getGraphIRI() + ">" +
			     		"WHERE {" +
			     			"{SELECT DISTINCT ?c ?path ?route ?jump " +
			     			"WHERE{" +
			     				"{SELECT ?c ?Class WHERE { ?Class rdfs:subClassOf ?c}" +
		     				"} " +
		     				"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?c), t_out(?Class), t_step('path_id') as ?path, t_step(?c) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " +
		     				"FILTER ( ?Class = <"+ class_uri +"> )}" + //NICO NICO is OPTION still posible, if ?Class is fix??? I think its not
						"}. OPTIONAL{ ?c rdfs:label ?l}" +
						"}";
//			     		"SELECT distinct ?sc ?l " +
//			     		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?C rdfs:subClassOf ?sc" +
//			     		" . OPTIONAL{ ?sc rdfs:label ?l} " +
//			            ". FILTER( str(?sc) != <"+ class_uri +"> && str(?C) = <"+ class_uri +">)}";
        }

          execute (sparql,-1);
        RDFEntityList classes =this.retrieveRDFEntityFrom(KswConstant.CLASS_TYPE) ;
        if (classes==null)
            classes=new RDFEntityList();
        return classes;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    public RDFEntityList getAllSuperClasses(String class_uri) throws Exception {
        try{
//        RDFEntityList directclasses =new RDFEntityList();
        RDFEntityList classes = new RDFEntityList();
        classes = getAllSuperClassesWithInference (class_uri);
//        // direct Classes for recursiv
//        directclasses = getAllSuperClassesWithInference (class_uri);
//        // START THE INFERENCE
//        while (!directclasses.isEmpty()) {
//            RDFEntityList aux = new RDFEntityList();
//            for (RDFEntity directclass: directclasses.getAllRDFEntities()) {
//                if (!classes.isExactRDFEntityContained(directclass)) {
////                    System.out.println("ayyyyy inferencing classes for the superclass " + directclass.getURI());
//                    classes.addRDFEntity(directclass);
//                    aux.addNewRDFEntities(this.getAllSuperClassesWithInference (directclass.getURI()));
//                }
//            }
//            directclasses = aux;
//        }

        return classes;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Getting all direct classes of the specified class
     * @param class_uri specifying a class by means of its URI.
     * @return a list of RDF entities
     * @throws java.lang.Exception
     */
    public RDFEntityList getDirectSuperClasses(String class_uri) throws Exception {
        try{
        String sparql;
        RDFEntityList classes = new RDFEntityList();
        if (!class_uri.startsWith("node") && !class_uri.startsWith("_:node")) {
//            serql= "select distinct c, l from {<" + class_uri + ">} rdfs:subClassOf {c}, " +
//                "[{c} rdfs:label {l}] FROM <" + this.repository.getGraphIRI() + "> WHERE isURI(c) and not c=<" + class_uri + "> " +
//                "minus select distinct c, l from {<"+ class_uri +">} rdfs:subClassOf {c2} rdfs:subClassOf {c}, " +
//                "[{c} rdfs:label {l}] FROM <" + this.repository.getGraphIRI() + "> WHERE isURI(c) and isURI(c2) " +
//                "and not c2=<"+ class_uri +">  and not c=c2";
            sparql = "SELECT DISTINCT ?c ?l " +
		            " FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + class_uri + "> rdfs:subClassOf ?c . " +
		        	"OPTIONAL{?c rdfs:label ?l} . " +
		        	"FILTER ( isURI(?c) && str(?c) != <" + class_uri + "> )}";

              execute (sparql,-1);
            classes= this.retrieveRDFEntityFrom(KswConstant.CLASS_TYPE) ;
        }
        if (classes == null)
                    classes=new RDFEntityList();
         return classes;
         } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    /**
     * Getting all properties that are associated with the specified class
     * @param class_uri specifying a class by means of its URI
     * @return  a list of RDFEntities
     * @throws java.lang.Exception
     */
    public RDFEntityList getAllPropertiesOfClass(String class_uri) throws Exception {
        RDFEntityList slots=this.getDirectPropertiesOfClass(class_uri);
        if (slots==null)
            slots= new RDFEntityList();
        RDFEntityList super_classes=this.getAllSuperClasses(class_uri);
        if (super_classes!=null) {
            for(RDFEntity c: super_classes.getAllRDFEntities()){
                RDFEntityList ss=this.getDirectPropertiesOfClass(c.getURI());
                if (ss!=null)
                    slots.addNewRDFEntities(ss);
            }
        }
        return slots;
    }


    // rdf tested!
    public RDFEntityList getAllPropertiesOfInstance (String instance_uri) throws Exception {
            try{
            //retrieve full properties is very expensive in sesame 2
            // when domain and range is not mandatory but optional
                // HOWEVER, DOMAIN AND RANGE is mandatory, otherwise we will create 1000 OTs with
                // e..g. all the instances of languages that relate to the given instance INDIA
           RDFEntityList slots = new RDFEntityList();
           if (this.isOWLRepository()){ // for datatypes:  [{value} rdf:type {rango}]
//            String serql = "select distinct rel, origin, rango, rellab, originlab, rangolab from " +
//            "{instance} rel {value}, {rel} rdf:type {X}, [{rel} rdfs:label {rellab}], " +
//            "[{rel} rdfs:range {rango}, [{rango} rdfs:label {rangolab}]], " +
//            "[{rel} rdfs:domain {origin}, [{origin} rdfs:label {originlab}]] " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE instance=<" + instance_uri + "> " +
//            "and not rel =<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> and not rel =<http://www.w3.org/2000/01/rdf-schema#label> " +
//            "and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " +
//            "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + NOT_RDF_PROPERTIES;
            String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
    		"SELECT DISTINCT ?rel ?rellab" +
    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?rel ?value. ?rel rdf:type ?X . " +
    		"OPTIONAL{ ?rel rdfs:label ?rellab} " +
    		". FILTER( str(?rel) != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  && str(?rel) != <http://www.w3.org/2000/01/rdf-schema#label> " +
            "&& (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " +
            "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" +
    		"}" ;

            execute (sparql,-1);
            slots = this.retrieveBasicPropertiesFrom();
            if (slots == null)
                slots = new RDFEntityList();

            String sparql2 = Prefixes.RDF + Prefixes.RDFS +
    		"SELECT DISTINCT ?rel ?rellab" +
    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?value ?rel <" + instance_uri + "> . ?rel rdf:type ?X . " +
    		"OPTIONAL{ ?rel rdfs:label ?rellab} " +
    		". FILTER( (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " +
            "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" +
    		"}" ;

               execute (sparql2,-1);
             slots.addAllRDFEntity(this.retrieveBasicPropertiesFrom());
             //slots.addRDFEntities(slots2); this one is looking for repetitions (addNewRDFEntities)
          }
          else{
             String sparql = Prefixes.RDF + Prefixes.RDFS +
				     		"SELECT DISTINCT ?rel ?rellab" +
				    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?rel ?value. ?rel rdf:type ?X . " +
				    		"OPTIONAL{ ?rel rdfs:label ?rellab} " +
				    		NOT_RDF_PROPERTIES + "}" ;

              execute (sparql,-1);
            slots=this.retrieveBasicPropertiesFrom();
            if (slots==null)
                slots=new RDFEntityList();

            sparql = Prefixes.RDF + Prefixes.RDFS +
			    		"SELECT DISTINCT ?rel ?rellab" +
			    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?value ?rel <" + instance_uri + "> . ?rel rdf:type ?X . " +
			    		"OPTIONAL{ ?rel rdfs:label ?rellab} " +
			             NOT_RDF_PROPERTIES + "}";

              execute (sparql,-1);
            slots.addAllRDFEntity(this.retrieveBasicPropertiesFrom());
          }
           RDFEntityList slotsNew = new RDFEntityList();
           for(RDFEntity slot : slots.getAllRDFEntities()){
        	   String sparql = Prefixes.RDF + Prefixes.RDFS +
	    		"SELECT DISTINCT ?rel ?rellab " +
	    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + slot.getURI() +"> <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> ?rel . " +
	    		"OPTIONAL{ ?rel rdfs:label ?rellab} " +
	             NOT_RDF_PROPERTIES + "}";

               execute (sparql,-1);
               slotsNew.addAllRDFEntity(this.retrieveBasicPropertiesFrom());
           }
           slots.addNewRDFEntities(slotsNew);
     return slots;
     } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Getting direct properties of the specified class.
     * @param class_uri specifying a class
     * @return a list of direct properties
     * @throws java.lang.Exception
     */

    public RDFEntityList  getDirectPropertiesOfClass(String class1) throws Exception {//NICO maybe its also here posible
        try{
         RDFEntityList slots = new RDFEntityList();
        if (this.isOWLRepository()){
            String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
				    		"SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab" +
				    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ " +
				    		"?rel rdfs:range ?rango. " +
				    		"?rel rdfs:domain <" + class1 + ">. " +
				    		"?rel rdf:type ?X . " +
//					        "{term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, " + //serql
				    		"OPTIONAL{ ?rel rdfs:label ?rellab}. " +
				    		"OPTIONAL{ <" + class1 + "> rdfs:label ?originlab}. " +
				    		"OPTIONAL{ ?rango rdfs:label ?rangolab} " +
				    		". FILTER((str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " +
				                "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" +
				    		NOT_RDF_PROPERTIES +
				    		"}" ;


              execute (sparql,-1);
            slots = this.retrieveFullPropertiesFrom();

            if (slots==null)
                slots=new RDFEntityList();

            String sparql2 = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
				    		"SELECT DISTINCT ?rel ?origin, ?rango, ?rellab, ?originlab, ?rangolab" +
				    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?rel rdfs:range <" + class1 + ">. ?rel rdfs:domain ?origin. ?rel rdf:type ?X . " +
//        					"{term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, " + //serql
				    		"OPTIONAL{ ?rel rdfs:label ?rellab}. " +
				    		"OPTIONAL{ ?origin rdfs:label ?originlab}. " +
				    		"OPTIONAL{ <" + class1 + "> rdfs:label ?rangolab} " +
				    		". FILTER((str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> " +
				                "|| str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" +
				    		NOT_RDF_PROPERTIES +
				    		"}" ;

              execute (sparql2,-1);
            RDFEntityList slots2 = this.retrieveFullPropertiesFrom();
            slots.addNewRDFEntities(slots2);
        }
        else {
            String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
			    		"SELECT DISTINCT ?rel ?origin, ?rango, ?rellab, ?originlab, ?rangolab" +
			    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?rel rdfs:range ?rango. ?rel rdfs:domain <" + class1 + ">. " +
			//        "{term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, " + //serql
			    		"OPTIONAL{ ?rel rdfs:label ?rellab}. " +
			    		"OPTIONAL{ <" + class1 + "> rdfs:label ?originlab}. " +
			    		"OPTIONAL{ ?rango rdfs:label ?rangolab} " +
			    		"NOT_RDF_PROPERTIES}" ;

              execute (sparql,-1);
            slots=this.retrieveFullPropertiesFrom();

            if (slots==null)
                slots=new RDFEntityList();

            String sparql2 = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
				    		"SELECT DISTINCT ?rel ?origin, ?rango, ?rellab, ?originlab, ?rangolab" +
				    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?rel rdfs:range <" + class1 + ">. ?rel rdfs:domain ?origin. " +
				//        "{term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, " + //serql
				    		"OPTIONAL{ ?rel rdfs:label ?rellab}. " +
				    		"OPTIONAL{ ?origin rdfs:label ?originlab}. " +
				    		"OPTIONAL{ <" + class1 + "> rdfs:label ?rangolab} " +
				    		"NOT_RDF_PROPERTIES}" ;

              execute (sparql2,-1);
            RDFEntityList slots2 = this.retrieveFullPropertiesFrom();
            slots.addNewRDFEntities(slots2);
        }
        return slots;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Getting all classes of the specified class
     * @param instance_uri specifying an instance
     * @return a list of RDF entities
     * @throws java.lang.Exception
     */
    public RDFEntityList getAllClassesOfInstance(String instance_uri) throws Exception {
        try{
        String sparql = Prefixes.RDF +
			"SELECT DISTINCT ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> rdf:type ?c. OPTIONAL{ ?c rdfs:label ?l}}" ;

          execute (sparql,-1);
          RDFEntityList list = this.retrieveRDFEntityFrom(KswConstant.CLASS_TYPE);
          LinkedList<RDFEntityList> newLists = new LinkedList<RDFEntityList>();
          ArrayList<RDFEntity> toCheckList= list.getAllRDFEntities();
          // when no inherency ..
          if (list.size()< 3) {
          while(true){
	          ArrayList<RDFEntity> newToCheckList= new ArrayList<RDFEntity>();
	          for(RDFEntity entity : toCheckList){
	        	  newLists.add(getAllSuperClasses(entity.getURI()));
	          }
	          for(RDFEntityList newList : newLists){
	              for (RDFEntity entity: newList.getAllRDFEntities())
	                  if (!list.getAllRDFEntities().contains(entity)){
	                	  list.getAllRDFEntities().add(entity);
	                	  newToCheckList.add(entity);
	                  }
	          }
	          if(newToCheckList.isEmpty())
	        	  break;
	          toCheckList = newToCheckList;
          }
          }
        return list;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Getting all classes of the specified class
     * @param instance_uri specifying an instance
     * @return a list of classes URIs
     * @throws java.lang.Exception
     */
    public ArrayList<String> getAllClassesNamesOfInstance(String instance_uri) throws Exception {
        try{
        	String sparql = Prefixes.RDF +
						"SELECT DISTINCT ?c FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> rdf:type ?c}" ;

            execute (sparql,-1);
      		HashSet<String> list = new HashSet<String>();
      		list.addAll(this.retrieveEntitiesUriFrom(false));


      		LinkedList<RDFEntityList> newLists = new LinkedList<RDFEntityList>();
      		HashSet<String> toCheckList= list;
            while(true){
            	HashSet<String> newToCheckList= new HashSet<String>();
  	          for(String entity : toCheckList){
  	        	  newLists.add(getAllSuperClasses(entity));
  	          }
  	          for(RDFEntityList newList : newLists){
  	              for (RDFEntity entity: newList.getAllRDFEntities())
  	                  if (!list.add(entity.getURI())){
  	                	  newToCheckList.add(entity.getURI());
  	                  }
  	          }
  	          if(newToCheckList.isEmpty())
  	        	  break;
  	          toCheckList = newToCheckList;
            }

      		ArrayList<String> output = new ArrayList<String>(list);
      		return output;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Getting direct classes
     * @param instance_uri specifying an instance
     * @return a list of RDF entities which are direct classes of the specified instance
     * @throws java.lang.Exception
     */
    public RDFEntityList getDirectClassOfInstance(String instance_uri) throws Exception {
    	try{
	        String sparql = Prefixes.RDF + Prefixes.RDFS +
			"SELECT DISTINCT ?c ?l FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> rdf:type ?c. OPTIONAL{ ?c rdfs:label ?l}}" ;

			execute (sparql,-1);
			return this.retrieveRDFEntityFrom(KswConstant.CLASS_TYPE);
    	} finally {
    	}
    		//results.close(); // needed for repository(sesame)
//        try{
//      RDFEntityList classes;
//        String sparql;
////        serql="select c,l from {i} serql:directType {c}, [{c} rdfs:label {l}] "+
////                " FROM  <" + this.repository.getGraphIRI() + "> WHERE i =<"+instance_uri+">
////        if (classes.isEmpty()) {
////            System.out.println("no inference allowed serql:directType " + serql);
////            serql = "select distinct c, l from {<" + instance_uri + ">} rdf:type {c} rdfs:subClassOf {j}, [{c} rdfs:label {l}] " +
////            "minus select distinct c, l from {<" + instance_uri + ">} rdf:type {k} rdfs:subClassOf {c}, [{c} rdfs:label {l}] FROM <" + this.repository.getGraphIRI() + "> WHERE k!=c ";
//
//        //            sparql = "SELECT DISTINCT ?c ?l" +
////        	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance_uri rdf:type ?c. " +
////        	"?instance_uri rdf:type ?k" +
////        	"?c rdfs:subClassOf ?j" +
////        	"?k rdfs:subClassOf ?c" +
////        	"OPTIONAL{?c rdfs:label ?l} . " +
////        	"FILTER (?instance_uri = {<" + instance_uri + ">}) . }"  +
////        	"MINUS " + "SELECT DISTINCT ?c ?l" +
////        	" FROM  <" + this.repository.getGraphIRI() + "> WHERE {  . " +
////        	"OPTIONAL{?c rdfs:label ?l} . " +
////        	"FILTER ( (k! = ?c ) && ?instance_uri = {<" + instance_uri + ">}) . }" ;
//        sparql = "SELECT DISTINCT ?c " +
//        		" FROM  {" +
//        			"CONSTRUCT ?c " +
//        			" FROM  <" + this.repository.getGraphIRI() + "> WHERE{?instance_uri rdf:type ?c}}" +
//        		" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance_uri rdf:type ?c. " +
//        		"FILTER(?instance_uri = <" + instance_uri + "> ) " +
//        		"}";
//
//              execute (sparql,-1);
//            classes= this.retrieveRDFEntityFrom(KswConstant.CLASS_TYPE) ;
////         }
//        if (classes==null || classes.isEmpty()){
//            return new RDFEntityList();
//        }
//
//        if (classes.size() ==1)
//            return classes;
//        return classes;
//        } finally {
//          //results.close(); // needed for repository(sesame)
//        }
     }

    /**
     * Get the name of all instances (for indexing purposes)
     * ***************************************************************************
     */

    public RDFEntityList getAllInstances() throws Exception {
        try{
         String sparql;
         RDFEntityList aux = new RDFEntityList();
         //TODO check if isURI(p) cause .. can have literals with this query?
         if (this.isOWLRepository()){
        	 sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
		     		"SELECT DISTINCT ?p ?l ?title" +
		     		" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?c rdf:type owl:Class . ?p rdf:type ?c . " +
		     		"OPTIONAL{?p  <http://purl.org/dc/elements/1.1/title> ?title} . " +
		     		"OPTIONAL{?p rdfs:label ?l} " +
	        		NOT_MIXED_CLASSES +
		     		"}";

               execute (sparql,-1);
             aux = this.retrieveRDFEntityForIndex(KswConstant.INSTANCE_TYPE);
          }
        	  sparql = "SELECT DISTINCT ?i ?l ?title" +
        		" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?i rdf:type ?c . ?c rdf:type rdfs:Class . " +
		     		"OPTIONAL{?p  <http://purl.org/dc/elements/1.1/title> ?title} . " +
        		"OPTIONAL{?i rdfs:label ?l} . " +
        		"FILTER (isURI(?i) )" +
        		NOT_MIXED_CLASSES +
        		NOT_RDF_INSTANCES + "}";
//        	 serql="select distinct i, l from {i} rdf:type {c}, {c} rdf:type {rdfs:Class}," +
//                      " [{i} rdfs:label {l}]" +
//                 " FROM <" + this.repository.getGraphIRI() + "> WHERE isURI(i) " + NOT_RDF_CLASSES +
//                 " minus "+
//                 "select i from {i} rdf:type {rdfs:Class} "+
//                 " FROM  <" + this.repository.getGraphIRI() + "> WHERE isURI(i)  " + NOT_RDF_CLASSES +
//                 " union "+
//                 "select i from {i} rdf:type {rdf:Property} "+
//                 " FROM  <" + this.repository.getGraphIRI() + "> WHERE isURI(i)  " + NOT_RDF_CLASSES ;

             execute (sparql,-1);
             aux.addAllRDFEntity(this.retrieveRDFEntityForIndex(KswConstant.INSTANCE_TYPE));

        return aux;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    public RDFEntityList getAllInstancesPeriodically(int offset, int limit) throws Exception {
      try{
        String sparql;
        RDFEntityList aux = new RDFEntityList();
        try{

         if (this.isOWLRepository()){
        	 sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
	     		"SELECT DISTINCT ?p ?l ?title" +
	     		" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?c rdf:type owl:Class . ?p rdf:type ?c . " +
	     		"OPTIONAL{?p  <http://purl.org/dc/elements/1.1/title> ?title} . " +
	     		"OPTIONAL{?p rdfs:label ?l} " +
        		NOT_MIXED_CLASSES + " } LIMIT " + limit + " OFFSET " + offset;
    	 	MappingSession.serqlCalls++;
    	 	execute (sparql,-1);
            aux = this.retrieveRDFEntityForIndex(KswConstant.INSTANCE_TYPE);
        }
         sparql= Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
         		"SELECT DISTINCT ?i ?l ?title" +
		 		" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?i rdf:type ?c . ?c rdf:type rdfs:Class . " +
		 		"OPTIONAL{?p  <http://purl.org/dc/elements/1.1/title> ?title} . " +
				"OPTIONAL{?i rdfs:label ?l} . " +
				"FILTER (isURI(?i) )" +
				NOT_MIXED_CLASSES +
				NOT_RDF_INSTANCES + "} LIMIT " + limit + " OFFSET " + offset;
//                 " minus ( "+
//                 "select i, l, title from {i} rdf:type {rdfs:Class}, "+
//                " [{i} rdfs:label {l}], [{i}  <http://purl.org/dc/elements/1.1/title> {title}] " +
//                 " FROM  <" + this.repository.getGraphIRI() + "> WHERE isURI(i) " + NOT_RDF_CLASSES +
//                 " union "+
//                 "select i, l, title from {i} rdf:type {owl:Class}, "+
//                " [{i} rdfs:label {l}], [{i}  <http://purl.org/dc/elements/1.1/title> {title}] " +
//                 " FROM  <" + this.repository.getGraphIRI() + "> WHERE isURI(i) " + NOT_RDF_CLASSES +
//                 " union "+
//                 "select i, l, title from {i} rdf:type {rdf:Property}, "+
//                " [{i} rdfs:label {l}], [{i}  <http://purl.org/dc/elements/1.1/title> {title}] " +
//                 " FROM  <" + this.repository.getGraphIRI() + "> WHERE isURI(i) " + NOT_RDF_CLASSES + ")";
         MappingSession.serqlCalls++;
         execute (sparql,-1);
         aux.addAllRDFEntity(this.retrieveRDFEntityForIndex(KswConstant.INSTANCE_TYPE));
        // Add the URIs that do not have a type .. (by default they should have been of type Resource)
        // e.g. http://dbpedia.org/resource/Nobel_Prize_in_Physics
//        	sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
//    		"SELECT DISTINCT ?i ?l ?title FROM <" + this.repository.getGraphIRI() + "> WHERE{ ?a ?x ?i. ?x rdf:type ?X . OPTIONAL{?i rdf:type ?type}. " +
//            " OPTIONAL{?i  <http://purl.org/dc/elements/1.1/title> ?title}. OPTIONAL{?i rdfs:label ?l} " +
//            "FILTER( isURI(?i) && NOT BOUND(?type) &&  (str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || " +
//            "str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || " +
//            "str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || " +
//            "str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))} LIMIT " + limit + "OFFSET " + offset ;
//
//         execute (sparql,-1);
//         aux = this.retrieveRDFEntityForIndex(KswConstant.INSTANCE_TYPE);
         // TEST:
//           String uri = "http://dbpedia.org/resource/Islamic_republic";
//           String label = "Islamic_republic";
//           RDFEntity c=new RDFEntity(KswConstant.INSTANCE_TYPE,uri,label, this.getPluginID());
//           aux.addRDFEntity(c);
        } catch (Exception e){
            e.printStackTrace();
        }
        return aux;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    /**
     * Getting all instances of the specified class (please this method does not work very well when the repository is in large scale.
     * @param class_uri specifying a class
     * @return a list of RDF entities
     * @throws java.lang.Exception
     */
    public RDFEntityList getAllInstancesOfClass(String class_uri, int limit) throws Exception {
        try{
        String sparql;
        if (limit > 0) {
             sparql = Prefixes.RDF + Prefixes.RDFS +
		"SELECT DISTINCT ?i ?l FROM <" + this.repository.getGraphIRI() + "> WHERE{ ?i rdf:type <"+class_uri+"> . OPTIONAL{?i rdfs:label ?l}. FILTER( isURI(?i))}";
        }
        else {
             sparql = Prefixes.RDF + Prefixes.RDFS +
		"SELECT DISTINCT ?i ?l FROM <" + this.repository.getGraphIRI() +
                "> WHERE{ ?i rdf:type <"+class_uri+"> . OPTIONAL{?i rdfs:label ?l}. FILTER( isURI(?i))} LIMIT " + limit ;
        }

          execute (sparql,-1);
        return this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE);
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Is instance of?
     * @param instance_uri
     * @param class_uri
     * @return
     * @throws Exception
     */
    public boolean isInstanceOf(String instance_uri, String class_uri) throws Exception {
        try{
        	String sparql;
//        	sparql= Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
//				"SELECT DISTINCT ?i FROM <" + this.repository.getGraphIRI() + "> WHERE{ ?i rdf:type ?C . " +
//				"FILTER( isURI(?i) && ?C = <" + class_uri + "> && ?i = <" + instance_uri + ">)}";
        	sparql = "ASK FROM <" + this.repository.getGraphIRI() + "> WHERE { <" + instance_uri + "> rdf:type <"+class_uri+">. FILTER( isURI(?i))}";

            execute (sparql,-1);
	        if (results==null || results.hasNext() == false)
	            return false;
	        return true;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Is the literal an alternative name of the instance
     * @param instance_uri
     * @param literal
     * @return
     * @throws Exception
     */
    public boolean isNameOfInstance(String instance_uri, String literal) throws Exception {//NICO maybe its possible to do it also for literals
     try{
        // There are ad-hoc properties like http://dbpedia.org/property/name not included in the index  ...
        //uriInst property literal
        //property label "name"
        String sparql;
        if  (literal.contains("\"")) {
    		sparql = Prefixes.RDFS +
    				"SELECT DISTINCT ?property " +
		    		" FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?property " + literal + " . ?property rdfs:label \"name\". }";
//          serql="select distinct i from {i} property {literal},   "+
//                "{property} rdfs:label {name} " +
//                " FROM  <" + this.repository.getGraphIRI() + "> WHERE i =<"+instance_uri+"> and literal = "+literal+" and name = \"name\"";
        }
        else {
    		sparql = Prefixes.RDFS +
		    		"SELECT DISTINCT ?property " +
		    		" FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?property \"" +literal + "\" . ?property rdfs:label \"name\".}";
//            serql="select distinct i from {i} property {literal},   "+
//                "{property} rdfs:label {name} " +
//                " FROM  <" + this.repository.getGraphIRI() + "> WHERE i =<"+instance_uri+"> and literal = \""+literal+"\" and name = \"name\"";
        }

          execute (sparql,-1);
        if (results==null || results.hasNext() == false)
            return false;
        return true;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    /**
     * Getting the value of the specified property of the specified instance.
     * @param instance_uri specifying an instance.
     * @param property_uri specifying a property.
     * @return an array of values
     * @throws java.lang.Exception
     */
    public RDFEntityList getSlotValue(String instance_uri, String property_uri) throws Exception {
        try{

         boolean limitsize = false;
        RDFEntityList results = new RDFEntityList();
        String sparql = "SELECT DISTINCT ?v FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> <" + property_uri +"> ?v . " +
						"FILTER(isLiteral(?v))}";

          execute (sparql,-1);
        results = this.retrieveRDFEntityLiteralsFrom(instance_uri, limitsize);

        if (!results.isEmpty())
            return results;


		sparql = Prefixes.RDFS +
				"SELECT DISTINCT ?v ?lv FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> <" + property_uri +"> ?v .  OPTIONAL{ ?v rdfs:label ?lv}}";

          execute (sparql,-1);
        results = this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE);

        return results;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    // tested!
    public RDFEntityList getInstancesWithSlotValue(String property_uri, String slot_value,
            boolean isValueLiteral) throws Exception {
        try{
       String sparql="";
        if (isValueLiteral){
    		sparql = Prefixes.RDFS +
    		"SELECT DISTINCT ?i ?lb FROM <" + this.repository.getGraphIRI() + "> WHERE{ ?i <" + property_uri +"> \"" + slot_value + "\".  OPTIONAL{ ?i rdfs:label ?lb}}";

              execute (sparql,-1);
            return retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE);
        }
        else{
        	sparql = Prefixes.RDFS +
    		"SELECT DISTINCT ?i ?lb FROM <" + this.repository.getGraphIRI() + "> WHERE{ ?i <" + property_uri +"> <" + slot_value + "> .  OPTIONAL{ ?i rdfs:label ?lb}}";

              execute (sparql,-1);
            return retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE);
        }
       } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Get all literal values of a given instance (used for literal indexing purposes)
     * @param instance_uri
     * @param maxLength
     * @return
     * @throws Exception
     * ***************************************************************************
     */
    public RDFEntityList getLiteralValuesOfInstance(String instance_uri) throws Exception {
        try{
        String sparql="";
		sparql = "SELECT DISTINCT ?v FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?property ?v . " +
				"FILTER(isLiteral(?v))}";


          execute (sparql,-1);
         boolean limitsize = true;
        return this.retrieveRDFEntityLiteralsFrom(instance_uri, limitsize);
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    /**
     * Getting the detailed information of the specified classs in terms of frame-based terminologies.
     * @param class_uri the uri of the specified class
     * @return an OcmlClass object, which contains super classes and direct slots
     * @throws java.lang.Exception
     */
    public OcmlClass getClassInfo(String class_uri) throws Exception {
        RDFEntity entityClass = new RDFEntity (KswConstant.CLASS_TYPE, class_uri,
                getLabelOfEntity (class_uri), getPluginID());
        OcmlClass c= new OcmlClass(entityClass);

        c.setDirectSuperClasses(this.getDirectSuperClasses(class_uri));
        c.setDirectSubclasses(this.getDirectSubClasses(class_uri));
        c.setSuperClasses(this.getAllSuperClasses(class_uri));
        c.setSubClasses(this.getAllSubClasses(class_uri));
        c.setEquivalentClasses(this.getEquivalentEntitiesForClass(class_uri));
        c.setProperties(this.getAllPropertiesOfClass(class_uri));
        c.setDirectProperties(this.getDirectPropertiesOfClass(class_uri));

        return c;
    }


    /**
     * Checking whether the specified URI existing in the repository
     * @param uri
     * @return
     * @throws java.lang.Exception
     */

    public boolean isURIExisted(String uri) throws Exception {
         try{
        String sparql = Prefixes.RDF + Prefixes.RDFS +
						"ASK FROM <" + this.repository.getGraphIRI() + "> WHERE  {" +
						"<" + uri + "> rdf:type ?o}";

          execute (sparql,-1);
        if (results==null || results.hasNext() == false)
            return false;

        return true;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    /**
     * Get detailed information of the specified instance
     * @param instance_uri the uri of the specified instance
     * @return the instance information in the format of frame-based instance
     * @throws java.lang.Exception
     */
    public OcmlInstance getInstanceInfo(String instance_uri) throws Exception {
        try {
        //System.out.println("get instance info for " + instance_uri);
        RDFEntity entityInstance = new RDFEntity (KswConstant.INSTANCE_TYPE, instance_uri,
                getLabelOfEntity(instance_uri), getPluginID());
        OcmlInstance ocmlInstance = new OcmlInstance (entityInstance);

        RDFEntityList directClasses = this.getDirectClassOfInstance(instance_uri);
        //System.out.println("1: " + directClasses);

        ocmlInstance.addDirectSuperClasses(directClasses);

        if (!ocmlInstance.getDirectSuperClasses().isEmpty()){
        for (RDFEntity directClass: directClasses.getAllRDFEntities()) {
            ocmlInstance.addSuperClasses(getAllSuperClasses(directClass.getURI()));
        }}

       ocmlInstance.setEquivalentInstances(getEquivalentEntitiesForInstance(instance_uri));

        // Generate the property table
       Hashtable <RDFEntity, RDFEntityList> propertiesTable = new Hashtable <RDFEntity, RDFEntityList>();

        String sparql = Prefixes.RDFS +
						"SELECT DISTINCT ?p ?v ?pl ?vl FROM <" + this.repository.getGraphIRI() + "> WHERE{ <" + instance_uri + "> ?p ?v .  OPTIONAL{ ?p rdfs:label ?pl}. OPTIONAL{ ?v rdfs:label ?vl}. " +
						"FILTER(str(?p) != <" + rdf_namespace + "type>)}";

          execute (sparql,-1);
        if (results==null || results.hasNext()==false){ //XXX ATTENTION: next
            return ocmlInstance;
        }
        //get direct properties
        while (results.hasNext()){
            QuerySolution soln = results.nextSolution() ;
            String property = soln.get("p").toString();

            String value = soln.get("v").toString();

            String propertyLabel = (soln.get("pl")==null?null:soln.get("pl").toString());

            String v_valueLabel = (soln.get("vl")==null?null:soln.get("vl").toString());
            String valueLabel = (v_valueLabel==null?null:v_valueLabel.toString().trim());

            RDFEntity entityProperty = new RDFEntity (KswConstant.PROPERTY_TYPE,
                    property, propertyLabel, getPluginID());
            String typeV;
            RDFEntity entityValue;
             if ((value.indexOf("#")>-1) ||  isURIString(value)) { // is instance
                typeV = KswConstant.INSTANCE_TYPE;
                entityValue = new RDFEntity (typeV, value, valueLabel, getPluginID());
             }
             else { // is literal
                typeV = KswConstant.LITERAL_TYPE;
                entityValue = new RDFEntity (typeV, instance_uri, value, getPluginID());
             }


            if (propertiesTable.containsKey(entityProperty)){
               propertiesTable.get(entityProperty).addRDFEntity(entityValue);
            }
            else{
                RDFEntityList list = new RDFEntityList();
                list.addRDFEntity(entityValue);
                propertiesTable.put(entityProperty, list);
            }
        }
        //System.out.println("num properties: " + propertiesTable.size());
        ocmlInstance.setProperties(propertiesTable);
        return ocmlInstance;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    public OcmlProperty getPropertyInfo(String property_uri) throws Exception {

        RDFEntity entityClass = new RDFEntity(KswConstant.PROPERTY_TYPE, property_uri, getLabelOfEntity(property_uri), getPluginID());

        OcmlProperty ocmlProperty= new OcmlProperty(entityClass);

        ocmlProperty.setEquivalentProperties(getEquivalentEntitiesForClass(property_uri));

        ocmlProperty.setDirectSuperProperties(getDirectSuperProperties(property_uri));
        ocmlProperty.setSuperProperties(getAllSuperProperties(property_uri));
        ocmlProperty.setDirectSubProperties(getDirectSubProperties(property_uri));
        ocmlProperty.setSubProperties(getAllSubProperties(property_uri));
        ocmlProperty.setEquivalentProperties(getEquivalentEntitiesForProperties(property_uri));

        RDFEntityList list = new RDFEntityList();
        list.addAllRDFEntity(this.getDomainOfProperty(property_uri));
        ocmlProperty.setDomain(list);
        list = new RDFEntityList();
        list.addAllRDFEntity(this.getRangeOfProperty(property_uri));
        ocmlProperty.setRange(list);

        return ocmlProperty;
    }
    /**
     * For a property return all the entities owl:equivalentProperty
     */
    private RDFEntityList getEquivalentEntitiesForProperties(String propertyUri) throws Exception {
       try{
	        String sparql;
	        RDFEntityList aux = new RDFEntityList();

//	        serql= " select distinct x, xl from {x} owl:equivalentProperty {<" + entity_uri + ">}; [rdfs:label {xl}] " ;
	        sparql = "SELECT DISTINCT ?x ?xl " +
			    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { " +
			    	"{?x owl:equivalentProperty <" + propertyUri + ">} " +
			    	"UNION" +
			    	"{<" + propertyUri + "> owl:equivalentProperty ?x}. " +
			    	"OPTIONAL{?x rdfs:label ?xl}}" ;

           execute (sparql,-1);
	        aux = this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE);

////	        serql= " select distinct x, xl from {<" + entity_uri + ">} owl:equivalentProperty {x}, [{x} rdfs:label {xl}] " ;
//	        sparql = "SELECT DISTINCT ?x ?xl " +
//			    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE {  . " +
//			    	"OPTIONAL{?x rdfs:label ?xl} . " +
//			    	"FILTER (str(?entity_uri) = <" + propertyUri + ">) . }" ;
//
//           execute (sparql,-1);
//	        aux.addNewRDFEntities(this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE));
	        return aux;
       } finally {
         //results.close(); // needed for repository(sesame)
       }
    }


	/**
     * Getting all sub properties of the specified Property. It uses RDFS inference so it
     * will return all the subproperties, no just the inmediate one
     * @param propertyUri the uri string of the specified property
     * @return the list of all sub properties
     * @throws java.lang.Exception
     */
    private RDFEntityList getAllSubProperties(String propertyUri) throws Exception {
        try{
//        RDFEntityList directproperties =new RDFEntityList();
        RDFEntityList properties =new RDFEntityList();

        properties = getAllSubPropertiesWithInference (propertyUri);
//        // direct
//        directproperties = getAllSubPropertiesWithInference (propertyUri);
//        System.out.println("juppi: \n" + directproperties);
//        // START THE INFERENCE
//        while (!directproperties.isEmpty()) {
//            RDFEntityList aux = new RDFEntityList();
//            for (RDFEntity directproperty: directproperties.getAllRDFEntities()) {
//                if (!properties.isExactRDFEntityContained(directproperty)) {
////                    System.out.println("ayyyyy inferencing properties for the subproperty " + directproperty.getURI());
//                    properties.addRDFEntity(directproperty);
//                    aux.addNewRDFEntities(this.getAllSubPropertiesWithInference (directproperty.getURI()));
//                }
//            }
//            directproperties = aux;
//        }

        return properties;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

	private RDFEntityList getAllSubPropertiesWithInference(String propertyUri) throws Exception {
        try{
        RDFEntityList properties =new RDFEntityList();
        String sparql;
        if (propertyUri.startsWith("node")) {
        	sparql = Prefixes.RDFS +
        	"SELECT DISTINCT ?p ?l " +
        	" FROM  <" + this.repository.getGraphIRI() + ">" +
        	"WHERE {" +
        		"{SELECT DISTINCT ?p ?path ?route ?jump " +
        		"WHERE{" +
        			"{SELECT ?p ?Property WHERE { ?p rdfs:subPropertyOf ?Property}" +
    			"} " +
				"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?p), t_out(?Property), t_step('path_id') as ?path, t_step(?p) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " +
				"FILTER ( ?Property = \"" + propertyUri +"\" )}" + // not replaceable
				"}. OPTIONAL{ ?p rdfs:label ?l}" +
				"}";
        }
        else {
        	sparql = Prefixes.RDFS +
        	"SELECT DISTINCT ?p ?l " +
        	" FROM  <" + this.repository.getGraphIRI() + ">" +
        	"WHERE {" +
        		"{SELECT DISTINCT ?p ?path ?route ?jump " +
        		"WHERE{" +
        			"{SELECT ?p ?Property WHERE { ?p rdfs:subPropertyOf ?Property}" +
    			"} " +
				"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?p), t_out(?Property), t_step('path_id') as ?path, t_step(?p) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " +
				"FILTER ( ?Property = <" + propertyUri +"> )}" + // not replaceable
				"}. OPTIONAL{ ?p rdfs:label ?l}" +
				"}";
        }

          execute (sparql,-1);
          properties=this.retrieveRDFEntityFrom( KswConstant.PROPERTY_TYPE) ;
        if (properties==null)
        	properties=new RDFEntityList();
        //retrieving properties defined by owl:intersectionOf
//        if (this.isOWLRepository()) {
//            RDFEntityList c2 = this.getSubPropertiesFromIntersectionDefinition(property_uri);
//            classes.addRDFEntities(c2);
//        }
        return properties;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

	private RDFEntityList getDirectSubProperties(String propertyUri) throws Exception {
        try{
        String sparql;
        RDFEntityList properties;
//        serql="select distinct c, l " +
//        		"from {c} rdfs:subClassOf {<" + class_uri + ">}, [{c} rdfs:label {l}] " +
//                " FROM  <" + this.repository.getGraphIRI() + "> WHERE isURI(c) and not c=<" + class_uri + "> " +
//                "minus " +
//                "select distinct c, l from {c} rdfs:subClassOf {c2} rdfs:subClassOf {<" + class_uri + ">} " +
//                ", [{c} rdfs:label {l}] FROM <" + this.repository.getGraphIRI() + "> WHERE isURI(c) and isURI(c2) " +
//                "and not c2=<" + class_uri + ">  and not c=c2" ;
        sparql = "SELECT DISTINCT ?p ?l " +
    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?p rdfs:subPropertyOf <" + propertyUri + "> . " +
    	"OPTIONAL{?p rdfs:label ?l} . " +
    	"FILTER ( (isURI(?p) && str(?p) != <" + propertyUri + "> )) . }" ;



          execute (sparql,-1);
          properties= this.retrieveRDFEntityFrom(KswConstant.PROPERTY_TYPE) ;
        if (properties == null) properties = new RDFEntityList();
        //retrieving classes defined by owl:intersectionOf
        if (this.isOWLRepository()) {
            RDFEntityList p2 = this.getSubPropertyFromIntersectionDefinition(propertyUri);
            if (properties==null)
                return p2;
            properties.addNewRDFEntities(p2);
        }
        return properties;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    private RDFEntityList getSubPropertyFromIntersectionDefinition(String propertyUri) throws Exception {
        try{
//	        String serql="select c,l from {c} owl:intersectionOf {x}; [rdfs:label {l}], {x} p {<"+class_uri+">}";
	        String sparql = "SELECT ?p ?l " +
					    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { " +
					    	"OPTIONAL{?p owl:intersectionOf ?x} . " +
					    	"OPTIONAL{?p rdfs:label ?l} . ?x ?p <" + propertyUri + ">}" ;

	        execute(sparql,-1);
	        return this.retrieveRDFEntityFrom(KswConstant.PROPERTY_TYPE);
        } finally {
            //results.close(); // needed for repository(sesame)
        }
    }



	private RDFEntityList getAllSuperProperties(String propertyUri) throws Exception {
        try{
//        RDFEntityList directproperties =new RDFEntityList();
        RDFEntityList properties = new RDFEntityList();

        properties = getAllSuperPropertiesWithInference (propertyUri);
//        // direct
//        directproperties = getAllSuperPropertiesWithInference (propertyUri);
//        // START THE INFERENCE
//        while (!directproperties.isEmpty()) {
//            RDFEntityList aux = new RDFEntityList();
//            for (RDFEntity directproperty: directproperties.getAllRDFEntities()) {
//                if (!properties.isExactRDFEntityContained(directproperty)) {
////                    System.out.println("ayyyyy inferencing properties for the superclass " + directclass.getURI());
//                    properties.addRDFEntity(directproperty);
//                    aux.addNewRDFEntities(this.getAllSuperPropertiesWithInference (directproperty.getURI()));
//                }
//            }
//            directproperties = aux;
//        }

        return properties;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }
    /**
     * Getting all super properties of the specified property
     * @param propertyUri the uri of the specified property
     * @return a list of RDF entities
     * @throws java.lang.Exception
     */
    private RDFEntityList getAllSuperPropertiesWithInference(String propertyUri) throws Exception {
        try{
            // inference is not automatic in Sesame 2 when no tusing native repositories ...
            // careful with cycles ...

        String sparql;
        if (propertyUri.startsWith("node") || propertyUri.startsWith("_:node")) {
             sparql = "SELECT DISTINCT ?p ?l " +
	            	" FROM  <" + this.repository.getGraphIRI() + ">" +
	            	"WHERE {" +
	            		"{SELECT DISTINCT ?p ?path ?route ?jump " +
	            		"WHERE{" +
	            			"{SELECT ?p ?Property WHERE { ?Property rdfs:subPropertyOf ?p}" +
	        			"} " +
	    				"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?p), t_out(?Property), t_step('path_id') as ?path, t_step(?p) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " +
	    				"FILTER ( ?Property = \"" + propertyUri +"\" )}" + // not replaceable
	    				"}. OPTIONAL{ ?p rdfs:label ?l}" +
	    				"}";
            }
            else {
            	sparql = "SELECT DISTINCT ?p ?l " +
	            	" FROM  <" + this.repository.getGraphIRI() + ">" +
	            	"WHERE {" +
	            		"{SELECT DISTINCT ?p ?path ?route ?jump " +
	            		"WHERE{" +
	            			"{SELECT ?p ?Property WHERE { ?Property rdfs:subPropertyOf ?p}" +
	        			"} " +
	    				"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?p), t_out(?Property), t_step('path_id') as ?path, t_step(?p) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ). " +
	    				"FILTER ( ?Property = <" + propertyUri +"> )}" + // not replaceable
	    				"}. OPTIONAL{ ?p rdfs:label ?l}" +
	    				"}";
        }

          execute (sparql,-1);
        RDFEntityList properties = this.retrieveRDFEntityFrom(KswConstant.PROPERTY_TYPE) ;
        if (properties==null)
        	properties=new RDFEntityList();
        return properties;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }



	/**
     * Getting all direct properties of the specified property
     * @param propertyUri specifying a property by means of its URI.
     * @return a list of RDF entities
     * @throws java.lang.Exception
     */
    private RDFEntityList getDirectSuperProperties(String propertyUri) throws Exception {
        try{
        String sparql;
        RDFEntityList properties = new RDFEntityList();
        if (!propertyUri.startsWith("node") && !propertyUri.startsWith("_:node")) {
            sparql = "SELECT DISTINCT ?p ?l " +
		            " FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + propertyUri + "> rdfs:subPropertyOf ?p . " +
		        	"OPTIONAL{?p rdfs:label ?l} . " +
		        	"FILTER ( (isURI(?p) && str(?p) != <" + propertyUri + "> ))}";

              execute (sparql,-1);
              properties= this.retrieveRDFEntityFrom(KswConstant.PROPERTY_TYPE) ;
              //System.out.println(properties);
        }
        if (properties == null)
        	properties = new RDFEntityList();
         return properties;
         } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


	public boolean isSubClassOf(String class1, String class2) throws Exception {
        try{
        String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
		"ASK FROM <" + this.repository.getGraphIRI() + "> WHERE { <" + class1 + "> rdfs:subClassOf <" + class2 + ">}";
//		"SELECT * FROM <" + this.repository.getGraphIRI() + "> WHERE { ?c1 rdfs:subClassOf ?c2. " +
//		"FILTER(str(?c1) = <" + class1 + "> && str(?c2) = <" + class2 + ">)} LIMIT 1";

          execute (sparql,-1);
//        ArrayList<String> uris=this.retrieveEntitiesUriFrom(false) ;
//        if (uris!=null)
//            return true;
//          return false;
        if (results == null || ! results.hasNext())
            return false;
        return true;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Return all the instances of a given generic class which have a given slot with a given value
     * @param ontology
     * @param genericClass
     * @param slot
     * @param inst
     * @return The array of objects representing instance names
     * @throws IOException
     */
    public RDFEntityList getGenericInstances(String class_uri, String slot, String value_uri) throws Exception {
        try{
        String sparql;
        if (class_uri.startsWith("node") || class_uri.startsWith("_:node")) {
        	sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
    		"SELECT DISTINCT ?instances ?i_label " +
    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?instances rdf:type \"" + class_uri + "\". ?instances <" + slot + "> <" + value_uri + ">. " +
//    		"?subjectHelp rdfs:subClassOf ?subject OPTION(TRANSITIVE, T_DISTINCT). " +
    		"OPTIONAL{ ?instances rdfs:label ?i_label }. " +
//    		"FILTER(str(?subject) = \"" + class_uri + "\")" +
    				"}";
            // "and  (lang(i_label) =  \"en\" or BOUND (lang(i_label)) or lang(i_label) = \"en-us\")"; //serql
        }
        else {
        	sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
        	"SELECT DISTINCT ?instances ?i_label " +
    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?instances rdf:type <" + class_uri + ">. ?instances <" + slot + "> <" + value_uri + ">. " +
    		"OPTIONAL{ ?instances rdfs:label ?i_label }}";
//        	"SELECT DISTINCT ?instances ?i_label " +
//    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?instances rdf:type ?subjectHelp. ?instances <" + slot + "> <" + value_uri + ">. " +
//   		"?subjectHelp rdfs:subClassOf ?subject OPTION(TRANSITIVE, T_DISTINCT). " +
//    		"OPTIONAL{ ?instances rdfs:label ?i_label }. " +
//    		"FILTER(str(?value) =  && str(?subject) = <" + class_uri + ">)}";//NICO see here also for the following
        }

          execute (sparql,-1);
        RDFEntityList lista1 = this.retrieveRDFValueFrom(KswConstant.INSTANCE_TYPE) ;
        // inversa!
        if (class_uri.startsWith("node") || class_uri.startsWith("_:node")) {
        	sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
    		"SELECT DISTINCT ?instances ?i_label " +
    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?instances rdf:type \"" + class_uri + "\". <" + value_uri + "> <" + slot + "> ?instances. " +
//    		"?subjectHelp rdfs:subClassOf ?subject OPTION(TRANSITIVE, T_DISTINCT). " +
    		"OPTIONAL{ ?instances rdfs:label ?i_label }. " +
//    		"FILTER(str(?subject) = \"" + class_uri + "\")" +
    				"}";
        }
        else{
        	sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
    		"SELECT DISTINCT ?instances ?i_label " +
    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?instances rdf:type <" + class_uri + ">. <" + value_uri + "> <" + slot + "> ?instances. " +
//    		"?subjectHelp rdfs:subClassOf ?subject OPTION(TRANSITIVE, T_DISTINCT). " +
    		"OPTIONAL{ ?instances rdfs:label ?i_label }}";

        }

          execute (sparql,-1);
        RDFEntityList lista2 = this.retrieveRDFValueFrom(KswConstant.INSTANCE_TYPE);
        lista1.addAllRDFEntity(lista2);
        return lista1;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    public RDFEntityList getGenericInstancesForLiteral(String class_uri, String slot, String value) throws Exception {
     try{
        String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
		    "SELECT DISTINCT ?instances ?i_label ?value ?subject " +
			" FROM  <" + this.repository.getGraphIRI() + "> " +
			"WHERE{ ?instances rdf:type <" + class_uri + ">. ?instances <" + slot + "> \"" + value + "\". " +
			"OPTIONAL{ ?instances rdfs:label ?i_label }}";
//        	"SELECT DISTINCT ?instances ?i_label ?value ?subject " +
//			" FROM  <" + this.repository.getGraphIRI() + "> " +
//			"WHERE{ ?instances rdf:type ?subjectHelp. ?instances <" + slot + "> ?value. " +
//			"?subjectHelp rdfs:subClassOf ?subject OPTION(TRANSITIVE, T_DISTINCT). " +
//			"OPTIONAL{ ?instances rdfs:label ?i_label }. " +
//			"FILTER(str(?value) = \"" + value + "\" && str(?subject) = <" + class_uri + "> && isLiteral(?value))}";

          execute (sparql,-1);
        return this.retrieveRDFValueFrom(KswConstant.INSTANCE_TYPE) ;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    // careful with multiple labels
    public RDFEntityList getTripleInstances (String query_class, String slot, String class2) throws Exception{
    	String sparql;
        try{
            // actors in a film returns 11439 instances
			sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
	    		"SELECT DISTINCT ?inst1 ?inst1label ?inst2 ?inst2label " +
	    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst1 rdf:type <" + query_class + ">.  ?inst2 rdf:type <" + class2 + ">. ?inst1 <" + slot + "> ?inst2. " +
	//    		"?class1Help rdfs:subClassOf ?class1 OPTION(TRANSITIVE, T_DISTINCT). " +
	//    		"?class2Help rdfs:subClassOf ?class2 OPTION(TRANSITIVE, T_DISTINCT). " +
	    		"OPTIONAL{ ?inst1 rdfs:label ?inst1label }. " +
	    		"OPTIONAL{ ?inst2 rdfs:label ?inst2label }}";
//	    		"SELECT DISTINCT ?inst1 ?inst1label ?inst2 ?inst2label " +
//	    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst1 rdf:type ?class1Help.  ?inst2 rdf:type ?class2Help. ?inst1 ?rel ?inst2. " +
//	//    		"?class1Help rdfs:subClassOf ?class1 OPTION(TRANSITIVE, T_DISTINCT). " +
//	//    		"?class2Help rdfs:subClassOf ?class2 OPTION(TRANSITIVE, T_DISTINCT). " +
//	    		"OPTIONAL{ ?inst1 rdfs:label ?inst1label }. " +
//	    		"OPTIONAL{ ?inst2 rdfs:label ?inst2label }. " +
//	    		"FILTER( str(?rel) = <" + slot + "> && str(?class1) = <" + query_class + "> && str(?class2) = <" + class2 + "> )}";
//			System.out.println(query_class + " - " + slot + " - " + class2);

            execute (sparql,-1);
	  		if(results != null){
				RDFEntityList aux = this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
	          	if(!aux.isEmpty())
	          		return aux;
	      	}
     		sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
	    		"SELECT DISTINCT ?inst1 ?inst1label ?inst2 ?inst2label " +
	    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst1 rdf:type <" + query_class + ">.  ?inst2 rdf:type <" + class2 + ">. ?inst2 <" + slot + "> ?inst1. " +
	    		"OPTIONAL{ ?inst1 rdfs:label ?inst1label }. " +
	    		"OPTIONAL{ ?inst2 rdfs:label ?inst2label }}";
//	    		"SELECT DISTINCT ?inst1 ?inst1label ?inst2 ?inst2label " +
//	    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst1 rdf:type ?class1Help.  ?inst2 rdf:type ?class2Help. ?inst2 ?rel ?inst1. " +
//	//    		"?class1Help rdfs:subClassOf ?class1 OPTION(TRANSITIVE, T_DISTINCT). " +
//	//    		"?class2Help rdfs:subClassOf ?class2 OPTION(TRANSITIVE, T_DISTINCT). " +
//	    		"OPTIONAL{ ?inst1 rdfs:label ?inst1label }. " +
//	    		"OPTIONAL{ ?inst2 rdfs:label ?inst2label }. " +
//	    		"FILTER( str(?rel) = <" + slot + "> && str(?class1) = <" + query_class + "> && str(?class2) = <" + class2 + "> )}";
//			System.out.println(query_class + " - " + slot + " - " + class2);

            execute (sparql,-1);
            return this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
         } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    // careful with multiple labesl like in dbpedia
    //select distinct inst, inst_label, ref_inst from {inst} rdf:type {query_class}, {ref_inst} rdf:type {ref_class}, {ref_inst} prop1 {inst}, {ref_inst} prop2 {instance}, [{inst} rdfs:label {inst_label}] , [{ref_inst} rdfs:label {ref_label}] FROM <" + this.repository.getGraphIRI() + "> WHERE (query_class =<http://e-culture.multimedian.nl/ns/getty/ulan#Person> and prop1 =<http://www.vraweb.org/vracore/vracore3#creator> and ref_class = <http://www.vraweb.org/vracore/vracore3#VisualResource> and prop2 = <http://www.vraweb.org/vracore/vracore3#location> and instance = <http://e-culture.multimedian.nl/ns/museums/Museo_del_Prado_Madrid>)
    public RDFEntityList getTripleInstances (String query_class, String prop1, String ref_class,
            String prop2, String instance) throws Exception{
    	String sparql;
    	try{
    		sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
	    				"SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label " +
			     		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " +
			     		"?inst <" + prop1 + "> ?ref_inst. ?ref_inst <" + prop2 + "> <" + instance + ">. " +
			     		"OPTIONAL{ ?inst rdfs:label ?inst1label }. " +
			     		"OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";

    		execute (sparql,-1);
    		if(results != null){
    			RDFEntityList aux = this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
	          	if(!aux.isEmpty())
	          		return aux;
          	}
    		sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
	    		"SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label " +
	    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " +
	    		"?ref_inst <" + prop1 + "> ?inst. ?ref_inst <" + prop2 + "> <" + instance + ">. " +
	    		"OPTIONAL{ ?inst rdfs:label ?inst1label }. " +
	    		"OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";

    		execute (sparql,-1);
    		if(results != null){
    			RDFEntityList aux = this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
	          	if(!aux.isEmpty())
	          		return aux;
          	}
    		sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
    				"SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label" +
		    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " +
		    		"?ref_inst <" + prop1 + "> ?inst. <" + instance + "> <" + prop2 + "> ?ref_inst." +
		    		"OPTIONAL{ ?inst rdfs:label ?inst1label }. " +
		    		"OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";

            execute (sparql,-1);
    		if(results != null){
    			RDFEntityList aux = this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
	          	if(!aux.isEmpty())
	          		return aux;
          	}
        	sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
		    		"SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label" +
		    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " +
		    		"?inst <" + prop1 + "> ?ref_inst. <" + instance + "> <" + prop2 + "> ?ref_inst." +
		    		"OPTIONAL{ ?inst rdfs:label ?inst1label }. " +
		    		"OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";

              execute (sparql,-1);
    		if(results != null){
    			RDFEntityList aux = this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
	          	if(!aux.isEmpty())
	          		return aux;
          	}

        return this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    public RDFEntityList getTripleInstancesFromLiteral (String query_class, String prop1, String ref_class,
            String prop2, String instance) throws Exception{
    	String sparql;
        try{
    		sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
    		"SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label " +
    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " +
     		"?inst <" + prop1 + "> ?ref_inst. ?ref_inst <" + prop2 + "> \"" + instance + "\". " +
    		"OPTIONAL{ ?inst rdfs:label ?inst1label }. " +
    		"OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";

          execute (sparql,-1);
          RDFEntityList list = this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
          if(list.isEmpty()){
    		sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
    		"SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label " +
    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. " +
    		"?ref_inst <" + prop1 + "> ?inst. ?ref_inst <" + prop2 + "> \"" + instance + "\". " +
    		"OPTIONAL{ ?inst rdfs:label ?inst1label }. " +
    		"OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";

              execute (sparql,-1);
        } else {
        	return list;
        }
        return this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

     public RDFEntityList getTripleInstancesFromClasses (String query_class, String prop1, String ref_class,
            String prop2, String class2) throws Exception{
    	 String sparql;
        try{
        	sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
		    		"SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label" +
		    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. ?instance rdf:type <" + class2 + ">." +
		     		"?inst <" + prop1 + "> ?ref_inst. ?ref_inst <" + prop2 + "> ?instance." +
		    		"OPTIONAL{ ?inst rdfs:label ?inst1label }. " +
		    		"OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";

          execute (sparql,-1);
        if(results != null){
        	RDFEntityList aux = this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
        	if(!aux.isEmpty())
        		return aux;
        }
    	sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
	    		"SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label" +
	    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. ?instance rdf:type <" + class2 + ">." +
	    		"?ref_inst <" + prop1 + "> ?inst. ?ref_inst <" + prop2 + "> ?instance." +
	    		"OPTIONAL{ ?inst rdfs:label ?inst1label }. " +
	    		"OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";

          execute (sparql,-1);
          if(results != null){
          	RDFEntityList aux = this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
          	if(!aux.isEmpty())
          		return aux;
          }
    	sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
	    		"SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label" +
	    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. ?instance rdf:type <" + class2 + ">." +
	    		"?ref_inst <" + prop1 + "> ?inst. ?instance <" + prop2 + "> ?ref_inst." +
	    		"OPTIONAL{ ?inst rdfs:label ?inst1label }. " +
	    		"OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";

          execute (sparql,-1);
          if(results != null){
          	RDFEntityList aux = this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
          	if(!aux.isEmpty())
          		return aux;
          }
    	sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
	    		"SELECT DISTINCT ?inst ?inst1label ?ref_inst ?inst2label" +
	    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE{ ?inst rdf:type <" + query_class + ">. ?ref_inst rdf:type <" + ref_class + ">. ?instance rdf:type <" + class2 + ">." +
	    		"?inst <" + prop1 + "> ?ref_inst. ?instance <" + prop2 + "> ?ref_inst." +
	    		"OPTIONAL{ ?inst rdfs:label ?inst1label }. " +
	    		"OPTIONAL{ ?ref_inst rdfs:label ?inst2label }}";

          execute (sparql,-1);
          if(results != null){
          	RDFEntityList aux = this.retrieveRDFTripleValueFrom(KswConstant.INSTANCE_TYPE) ;
          	if(!aux.isEmpty())
          		return aux;
          }

        return null ;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


     public RDFEntityList getKBPropertiesBetweenClasses (String class1_uri, String class2_uri) throws Exception {
        try{
//        String preSerq = "select distinct rel, rellab from [{rel} rdfs:label {rellab}], " +
//            "{instorigin} rdf:type {class1}, {insttarget} rdf:type {class2}, " +
//            "{instorigin} rel {insttarget} " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE ((class1 =<" + class1_uri + "> and class2 =<" + class2_uri + ">) or " +
//            "(class2 =<" + class1_uri + "> and class1 =<" + class2_uri + ">)) " + NOT_RDF_PROPERTIES;  //+

          RDFEntityList props = getPropertiesBetweenClasses (class1_uri, class2_uri);
          RDFEntityList slots = new RDFEntityList();

          for (RDFEntity Entprop: props.getAllRDFEntities()){
             RDFProperty prop = (RDFProperty) Entprop;
             System.out.println("Analyzing prop. " + prop.getURI());
//     		String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
//		    		"ASK FROM <" + this.repository.getGraphIRI() + "> WHERE { ?instorigin rdf:type ?class1. ?insttarget rdf:type ?class2. ?instorigin <"+ prop.getURI()+"> ?insttarget. " +
//		    		"FILTER(((str(?class1) = <" + class1_uri + "> && str(?class2) = <" + class2_uri + ">) || " +
//		    				"(str(?class2) = <" + class1_uri + "> && str(?class1) = <" + class2_uri + ">)) && " +
//		    				"str(?rel) = )} ";

     		String sparql = Prefixes.RDF + Prefixes.RDFS + Prefixes.OWL +
		    		"ASK FROM <" + this.repository.getGraphIRI() + "> " +
    				" WHERE { " +
    				"{?instorigin rdf:type <" + class1_uri + ">. ?insttarget rdf:type <" + class2_uri + ">}" +
    				" UNION " +
    				"{?instorigin rdf:type <" + class2_uri + ">. ?insttarget rdf:type <" + class1_uri + ">}. " +
					"?instorigin <"+ prop.getURI()+"> ?insttarget} ";//NICO UNION EXAMPLE
             //System.out.println("ayyyy " + preSerq2);
             execute (sparql,-1);
             if (results!=null && results.hasNext()){
                slots.addRDFEntity(prop);
            }
         }
        return slots;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }



    /**
     * Get all the possible slots between two classes (or its superclasses).
     * Use the taxonomy and relationships inference (herency).
     * Get relations in both ways
     * @param class1_uri
     * @param class2_uri
     * @return RDFEntityList
     * @throws Exception
     */
    public RDFEntityList getPropertiesBetweenClasses(String class1_uri, String class2_uri) throws Exception {
       try{
        // non generic classes so we just take the superclasses of the terms (because of inherancy)
        // but not the subclasses (i.e. is john working in akt --> researcher & project)
        RDFEntityList slots = new RDFEntityList();
        if (this.isOWLRepository()) {


            // CONSIDERING INHERENCY
//            String serql="select distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                    "{rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {rel} rdf:type {X}, " +
//                    "[{term1} rdfs:subClassOf {origin}], [{term2} rdfs:subClassOf {rango}], " +
//                    "[{rel} rdfs:label {rellab}], " +
//                    "[{origin} rdfs:label {originlab}], " +
//                    "[{rango} rdfs:label {rangolab}] " +
//                    " FROM <" + this.repository.getGraphIRI() + "> WHERE (term2 =<" + class1_uri + "> or rango  =<" + class1_uri + ">) and " +
//                    " (term1=<" + class2_uri + "> or origin  =<" + class2_uri + ">) and "  +
//                    " (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" +
//                    NOT_RDF_PROPERTIES ;
            String sparql = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab " +
    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?rel rdfs:range ?rango . ?rel rdfs:domain ?origin . ?rel rdf:type ?X . " +
    		"OPTIONAL{?term1 rdfs:subClassOf ?origin} . " +
    		"OPTIONAL{?term2 rdfs:subClassOf ?rango} . " +
    		"OPTIONAL{?rel rdfs:label ?rellab} . " +
    		"OPTIONAL{?origin rdfs:label ?originlab} . " +
    		"OPTIONAL{?rango rdfs:label ?rangolab} . " +
    		"FILTER ((str(?term2) = <" + class1_uri + "> || str(?rango) = <" + class1_uri + ">) && " +
    			"(str(?term1) = <" + class2_uri + "> || str(?origin) = <" + class2_uri + ">) && " +
    			"(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" +
    			NOT_RDF_PROPERTIES + "}"; //NICO 4 Unions one step inherence


       execute (sparql,-1);
    slots=this.retrieveFullPropertiesFrom();

    if (slots==null)
        slots=new RDFEntityList();

//    String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from " +
//            "{rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, {rel} rdf:type {X}," +
//            "[{term1} rdfs:subClassOf {origin}], [{term2} rdfs:subClassOf {rango}], " +
//            "[{rel} rdfs:label {rellab}], " +
//            "[{origin} rdfs:label {originlab}], " +
//            "[{rango} rdfs:label {rangolab}] " +
//            " FROM <" + this.repository.getGraphIRI() + "> WHERE (term2 =<" + class2_uri + "> or rango  =<" + class2_uri + ">) and " +
//            " (term1=<" + class1_uri + "> or origin  =<" + class1_uri + ">) and "  +
//           "  (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + NOT_RDF_PROPERTIES;
    String sparql2 = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab " +
    		" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?rel rdfs:range ?rango . ?rel rdfs:domain ?origin . ?rel rdf:type ?X . " +
    		"OPTIONAL{?term1 rdfs:subClassOf ?origin} . " +
    		"OPTIONAL{?term2 rdfs:subClassOf ?rango} . " +
    		"OPTIONAL{?rel rdfs:label ?rellab} . " +
    		"OPTIONAL{?origin rdfs:label ?originlab} . " +
    		"OPTIONAL{?rango rdfs:label ?rangolab} . " +
    		"FILTER ((str(?term2) = <" + class2_uri + "> || str(?rango) = <" + class2_uri + ">) && " +
				"(str(?term1) = <" + class1_uri + "> || str(?origin) = <" + class1_uri + ">) && " +
				"(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>))" +
				NOT_RDF_PROPERTIES + "}"; //NICO 4 Unions one step inherence


              execute (sparql2,-1);
            RDFEntityList slots2 = this.retrieveFullPropertiesFrom();
            slots.addAllRDFEntity(slots2);
        }
        else {//XXX in the following is NO INHERENCE anymore?????
//           String serql="select distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                "{rel} rdfs:range {rango}, {rel} rdfs:domain {origin},  " +
//                "{term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, " +
//                "[{rel} rdfs:label {rellab}], " +
//                "[{origin} rdfs:label {originlab}], " +
//                "[{rango} rdfs:label {rangolab}] " +
//                " FROM <" + this.repository.getGraphIRI() + "> WHERE term2 =<" + class1_uri + "> and term1=<" + class2_uri + ">" +
//                NOT_RDF_PROPERTIES;
           String sparql = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab " +
           		" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?rel rdfs:range ?rango . ?rel rdfs:domain ?origin . <" + class2_uri + "> rdfs:subClassOf ?origin . <" + class1_uri + "> rdfs:subClassOf ?rango . " +
           		"OPTIONAL{?rel rdfs:label ?rellab} . " +
           		"OPTIONAL{?origin rdfs:label ?originlab} . " +
           		"OPTIONAL{?rango rdfs:label ?rangolab}" +
           		NOT_RDF_PROPERTIES + "}";

              execute (sparql,-1);
            slots=this.retrieveFullPropertiesFrom();

            if (slots==null)
                slots=new RDFEntityList();

//            String serql2 = "select distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                    "{rel} rdfs:range {rango}, {rel} rdfs:domain {origin}, " +
//                    "{term1} rdfs:subClassOf {origin}, {term2} rdfs:subClassOf {rango}, " +
//                    "[{rel} rdfs:label {rellab}], " +
//                    "[{origin} rdfs:label {originlab}], " +
//                    "[{rango} rdfs:label {rangolab}] " +
//                    " FROM  <" + this.repository.getGraphIRI() + "> WHERE term1=<" + class1_uri + "> and term2= <" + class2_uri + ">" +
//                     NOT_RDF_PROPERTIES;
            String sparql2 = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab " +
            		" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?rel rdfs:range ?rango . ?rel rdfs:domain ?origin . <" + class1_uri + "> rdfs:subClassOf ?origin . <" + class2_uri + "> rdfs:subClassOf ?rango . " +
            		"OPTIONAL{?rel rdfs:label ?rellab} . " +
            		"OPTIONAL{?origin rdfs:label ?originlab} . " +
            		"OPTIONAL{?rango rdfs:label ?rangolab}" +
            		NOT_RDF_PROPERTIES + "}" ;

              execute (sparql2,-1);
            RDFEntityList slots2 = this.retrieveFullPropertiesFrom();
            slots.addAllRDFEntity(slots2);
        }
        return slots;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    /**
     * Get all the slots for a generic class (as a domain and as a range).
     * A generic class is the one that also includes its subclasses. I.e, when
     * we consider the class person as a generic, we want to include all the
     * subclasses of person like secretary, phd-student, profesor, research.
     * We want to obtain all the relation for its superclasses as well
     * (due to herency and taxonomy inference)
     * @param class_uri
     * @return RDFEntityList
     * @throws Exception
     */
    public RDFEntityList getPropertiesForGenericClass(String class_uri) throws Exception {
        try{
        RDFEntityList slots = new RDFEntityList();
        if (this.isOWLRepository()) {
        	RDFEntityList slots2 = null;
        	//SuperClasses
        	String sparql = "SELECT DISTINCT ?rel ?domain ?rango ?rellab ?originlab ?rangolab " +
        			" FROM  <" + this.repository.getGraphIRI() + ">" +
        			"WHERE{" +
        				"{SELECT ?origin ?genericterm ?path ?route ?jump " +
        				"WHERE{" +
        					"{SELECT ?origin ?genericterm WHERE { <" + class_uri + ">  rdfs:subClassOf ?origin} }" +
        				"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?origin), t_out(?genericterm), t_step('path_id') as ?path, t_step(?origin) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ) 	" +
//        				"FILTER ( ?genericterm = <" + class_uri + "> )" +//NICO OPTION
        				"}" +
    				"}." +
    				"?rel rdfs:domain ?domain." +
    				"?rel rdfs:range ?rango." +
    				"?rel rdf:type ?X. " +
					"OPTIONAL{?rel rdfs:label ?rellab} . " +
    				"OPTIONAL{?origin rdfs:label ?originlab} . " +
    				"OPTIONAL{?rango rdfs:label ?rangolab}. " +
    				"FILTER(" +
    				"(?domain = ?origin || ?rango = ?origin) && " +
    				"(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || ?X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" +
    				")" +
    				NOT_RDF_PROPERTIES + "}";

            execute (sparql,-1);
            slots2 = this.retrieveFullPropertiesFrom();
            slots.addAllRDFEntity(slots2);

        	//SubClasses
            sparql = "SELECT DISTINCT ?rel ?domain ?rango ?rellab ?originlab ?rangolab " +
					" FROM  <" + this.repository.getGraphIRI() + ">" +
					"WHERE{" +
						"{SELECT ?origin ?genericterm ?path ?route ?jump " +
						"WHERE{" +
							"{SELECT ?origin ?genericterm WHERE { ?origin rdfs:subClassOf ?genericterm} }" +
						"OPTION ( TRANSITIVE, T_DISTINCT, t_in(?origin), t_out(?genericterm), t_step('path_id') as ?path, t_step(?origin) as ?route, t_step('step_no') AS ?jump, T_DIRECTION 3 ) 	" +
						"FILTER ( ?genericterm = <" + class_uri + "> )" +//NICO OPTION
						"}" +
					"}." +
					"?rel rdfs:domain ?domain." +
					"?rel rdfs:range ?rango." +
					"?rel rdf:type ?X. " +
					"OPTIONAL{?rel rdfs:label ?rellab} . " +
					"OPTIONAL{?domain rdfs:label ?originlab} . " +
					"OPTIONAL{?rango rdfs:label ?rangolab}." +
    				"FILTER(" +
    				"(?domain = ?origin || ?rango = ?origin) && " +
    				"(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || ?X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" +
    				")" +
    				NOT_RDF_PROPERTIES + "}";

           execute (sparql,-1);
           slots2 = this.retrieveFullPropertiesFrom();
           slots.addAllRDFEntity(slots2);

       	//this Class
           sparql = "SELECT DISTINCT ?rel ?domain ?rango ?rellab ?originlab ?rangolab " +
					" FROM  <" + this.repository.getGraphIRI() + ">" +
					"WHERE{" +
					"?rel rdfs:domain ?domain." +
					"?rel rdfs:range ?rango." +
					"?rel rdf:type ?X. " +
					"OPTIONAL{?rel rdfs:label ?rellab} . " +
					"OPTIONAL{?domain rdfs:label ?originlab} . " +
					"OPTIONAL{?rango rdfs:label ?rangolab}." +
					"FILTER (( str(?domain) = <" + class_uri + "> || str(?rango) = <" + class_uri + ">) && " +//NICO maybe Union
    				"(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || ?X = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" +
    				") "+
    				NOT_RDF_PROPERTIES + "}";

          execute (sparql,-1);
          slots2 = this.retrieveFullPropertiesFrom();
          slots.addAllRDFEntity(slots2);
//          slots = new RDFEntityList();
        }
        else {
//            String serq ="SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                    "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, " +
//                    "{origin} rdfs:subClassOf {genericterm}, " +
//                    "[{rel} rdfs:label {rellab}], " +
//                    "[{origin} rdfs:label {originlab}], " +
//                    "[{rango} rdfs:label {rangolab}] " +
//                    " FROM <" + this.repository.getGraphIRI() + "> WHERE genericterm =<" +  class_uri + ">" +
//                    " UNION " +
//                    "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                    "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, " +
//                    "{genericterm} rdfs:subClassOf {origin}, " +
//                    "[{rel} rdfs:label {rellab}], " +
//                    "[{origin} rdfs:label {originlab}], " +
//                    "[{rango} rdfs:label {rangolab}] " +
//                    " FROM <" + this.repository.getGraphIRI() + "> WHERE genericterm =<" +  class_uri + "> " +
//                     NOT_RDF_PROPERTIES;
            String sparql = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab" +
	            " FROM  <" + this.repository.getGraphIRI() + "> " +
	            "WHERE {" +
	            " ?rel rdfs:domain ?origin . ?rel rdfs:range ?rango . " +
	            "{?origin rdfs:subClassOf <" + class_uri + ">}      UNION       {<" + class_uri + "> rdfs:subClassOf ?origin}." +
	            " OPTIONAL{?rel rdfs:label ?rellab} . " +
	            " OPTIONAL{?origin rdfs:label ?originlab} . " +
	            " OPTIONAL{?rango rdfs:label ?rangolab}" +
	            NOT_RDF_PROPERTIES + "}" ;

              execute (sparql,-1);
            RDFEntityList slots2 = this.retrieveFullPropertiesFrom();
            slots.addAllRDFEntity(slots2);

            // Indirect for the sub/super classes
//            String serq3 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                    "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango},  " +
//                    "{rango} rdfs:subClassOf {genericterm}, " +
//                    "[{rel} rdfs:label {rellab}], " +
//                    "[{origin} rdfs:label {originlab}], " +
//                    "[{rango} rdfs:label {rangolab}] " +
//                   " FROM <" + this.repository.getGraphIRI() + "> WHERE genericterm =<" +  class_uri + ">" +
//                    " UNION " +
//                    "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                    "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, " +
//                    "{genericterm} rdfs:subClassOf {rango}, " +
//                    "[{rel} rdfs:label {rellab}], " +
//                    "[{origin} rdfs:label {originlab}], " +
//                    "[{rango} rdfs:label {rangolab}] " +
//                   " FROM <" + this.repository.getGraphIRI() + "> WHERE genericterm =<" +  class_uri + "> " +
//                   NOT_RDF_PROPERTIES;
            String sparql2 = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab" +
	            " FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?rel rdfs:domain ?origin . ?rel rdfs:range ?rango . " +
	            "{?rango rdfs:subClassOf <" + class_uri + "> }      UNION     { <" + class_uri + "> rdfs:subClassOf ?rango }. " +
	            "OPTIONAL{?rel rdfs:label ?rellab} . " +
	            "OPTIONAL{?origin rdfs:label ?originlab} . " +
	            "OPTIONAL{?rango rdfs:label ?rangolab}" +
	            NOT_RDF_PROPERTIES + "}" ;

              execute (sparql2,-1);
            RDFEntityList slots3 = this.retrieveFullPropertiesFrom();
            slots.addAllRDFEntity(slots3);
        }
        // Maybe the answer is in the schema like for the atomich number of cadmiun
//        String serq4 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//            "{rel} rdf:type {X}, {genericterm} rel {instance}, " +
//            "[{rel} rdfs:label {rellab}], " +
//            "[{rel} rdfs:domain {origin}, [{origin} rdfs:label {originlab}]], " +
//            "[{rel} rdfs:range {rango}, [{rango} rdfs:label {rangolab}]]  " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE genericterm =<" +  class_uri + "> " +
//                   NOT_RDF_PROPERTIES;
        String sparql = "SELECT DISTINCT ?rel ?origin ?rango ?rellab ?originlab ?rangolab" +
	    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + class_uri + "> ?rel ?instance . " +
	    	"OPTIONAL{?rel rdfs:label ?rellab} . ?[rel rdfs:domain ?origin . " +
	    	"OPTIONAL{?origin rdfs:label ?originlab} . ?[rel rdfs:range ?rango . " +
	    	"OPTIONAL{?rango rdfs:label ?rangolab}" +
	    	NOT_RDF_PROPERTIES + "}" ;

          execute (sparql,-1);
        RDFEntityList slots4 = this.retrieveFullPropertiesFrom();
        slots.addAllRDFEntity(slots4);
        return slots;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    public RDFEntityList getAllPropertiesBetweenClass_Literal (String classURI, String slot_value) throws Exception{
        try{
        RDFEntityList slots = new RDFEntityList();
//        RDFEntityList propertyList = this.getAllPropertiesOfClass(classURI);
//        for (RDFEntity prop:propertyList.getAllRDFEntities()) {
//            System.out.println("ayyyyyy " + prop.getURI());
        	String sparql;
            // already under quotes ...
            if (slot_value.contains("\"")) {
//                 serq1 = "SELECT distinct p, rellab from " +
//                "{instance} p {v}, {p} rdfs:range {String}, {instance} rdf:type {origin}, " +
//                "[{p} rdfs:label {rellab}] " +
//                " FROM  <" + this.repository.getGraphIRI() + "> WHERE isLiteral(v) and v = "+slot_value+" and origin =<" +  classURI + "> " +
////                " and p=<" +  prop.getURI() + "> " +
//                " and String=<http://www.w3.org/2001/XMLSchema#String> " +
//                " and  ( lang(rellab) =  \"en\" or  lang(rellab) =  \"\" ) ";
                 sparql = "SELECT DISTINCT ?p ?rellab " +
                    " FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?p " + slot_value + " . ?p rdfs:range <http://www.w3.org/2001/XMLSchema#string> . ?instance rdf:type <" + classURI + "> . " +
                    "OPTIONAL{?p rdfs:label ?rellab} . " +
                    "FILTER (( lang(?rellab) = \"en\" || lang(?rellab) = \"\" ) ) . }" ;
//               sparql = "SELECT DISTINCT ?p ?rellab " +
//             	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?p ?v . ?p rdfs:range <http://www.w3.org/2001/XMLSchema#string> . ?instance rdf:type <" + classURI + "> . " +
//             	"OPTIONAL{?p rdfs:label ?rellab} . " +
//             	"FILTER (isLiteral(?v) && str(?v) = " + slot_value + " && ( lang(?rellab) = \"en\" || lang(?rellab) = \"\" ) ) . }" ;
            }
            else {
//                 serq1 = "SELECT distinct p, rellab from " +
//                "{instance} p {v}, {p} rdfs:range {String}, {instance} rdf:type {origin}, " +
//                "[{p} rdfs:label {rellab}] " +
//                " FROM  <" + this.repository.getGraphIRI() + "> WHERE isLiteral(v) and v = \""+slot_value+"\" and origin =<" +  classURI + "> " +
////                 " and p=<" +  prop.getURI() + "> " +
//                 " and String=<http://www.w3.org/2001/XMLSchema#String> " +
//                 " and  ( lang(rellab) =  \"en\" or  lang(rellab) =  \"\" ) ";
                sparql = "SELECT DISTINCT ?p ?rellab " +
                	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?p \"" + slot_value + "\" . ?p rdfs:range <http://www.w3.org/2001/XMLSchema#string> . ?instance rdf:type <" + classURI + "> . " +
                	"OPTIONAL{?p rdfs:label ?rellab} . " +
                        "FILTER (( lang(?rellab) = \"en\" || lang(?rellab) = \"\" ) ) . }" ;

//                 sparql = "SELECT DISTINCT ?p ?rellab " +
//              	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?p ?v . ?p rdfs:range <http://www.w3.org/2001/XMLSchema#string> . ?instance rdf:type <" + classURI + "> . " +
//              	"OPTIONAL{?p rdfs:label ?rellab} . " +
//              	"FILTER (isLiteral(?v) && str(?v) = \"" + slot_value + "\" && ( lang(?rellab) = \"en\" || lang(?rellab) = \"\" ) ) . }" ;


            }

            execute (sparql,-1);
            slots = this.retrieveRDFEntityFrom(KswConstant.PROPERTY_TYPE);
//            RDFEntityList slotsaux = this.retrieveRDFEntityFrom(KswConstant.PROPERTY_TYPE);
//            for (RDFEntity slotaux:slotsaux.getAllRDFEntities())
//                slots.addRDFEntity (getRDFProperty (slotaux.getURI()));
//            }


        return slots;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


    /**
     * Get all the slots for a generic class (as a domain and as a range) and a
     * second given class (or its superclasses due to herency taxonomy inference)
     * A generic class is the one that also includes its subclasses. i.e, when
     * we consider the class person as a generic, we want to include all the
     * subclasses of person like secretary, phd-student, profesor, research.
     * We want to obtain all the relation for its superclasses as well
     * (due to herency and taxonomy inference)
     * @param class_uri
     * @return RDFEntityList
     * @throws Exception
     */

    // TOO inefficient!!!: 0 ocurrences
//    public RDFEntityList getSchemaPropertiesForGenericClass(String classGeneric_uri, String class2_uri) throws Exception {
//        try{
//        RDFEntityList slots = new RDFEntityList();
//        if (this.isOWLRepository()) {
//                String serq1 ="SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                        "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {X}, " +
//                        "{classGeneric} rdfs:subClassOf {origin}, " +
//                        "{class2} rdfs:subClassOf {rango}, " +
//                        "[{rel} rdfs:label {rellab}], " +
//                        "[{origin} rdfs:label {originlab}], " +
//                        "[{rango} rdfs:label {rangolab}] " +
//                        " FROM <" + this.repository.getGraphIRI() + "> WHERE (classGeneric =<"+ classGeneric_uri + "> and class2 =<" + class2_uri + ">)"+
//                        " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " + NOT_RDF_PROPERTIES +
//                        " UNION " +
//                        "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                        "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango},   {rel} rdf:type {X}, " +
//                        "{class2} rdfs:subClassOf {rango}, " +
//                        "{origin} rdfs:subClassOf {classGeneric}, " +
//                        "[{rel} rdfs:label {rellab}], " +
//                        "[{origin} rdfs:label {originlab}], " +
//                        "[{rango} rdfs:label {rangolab}] " +
//                        " FROM <" + this.repository.getGraphIRI() + "> WHERE (classGeneric =<" + classGeneric_uri + "> and class2 =<" + class2_uri + "> )" +
//                        " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + NOT_RDF_PROPERTIES;
//                MappingSession.serqlCalls++;
//                  execute (serq1);
//                RDFEntityList slots1 = this.retrieveFullPropertiesFrom();
//                slots.addRDFEntities(slots1);
//
//                // Indirect for the sub/super classes (Inverse relations): change origin and range!
//                String serq2 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                        "{rel} rdfs:domain {rango}, {rel} rdfs:range {origin},   {rel} rdf:type {X}, " +
//                        "{classGeneric} rdfs:subClassOf {origin}, " +
//                        "{class2} rdfs:subClassOf {rango}, " +
//                        "[{rel} rdfs:label {rellab}], " +
//                        "[{origin} rdfs:label {originlab}], " +
//                        "[{rango} rdfs:label {rangolab}] " +
//                        " FROM <" + this.repository.getGraphIRI() + "> WHERE (classGeneric =<"+classGeneric_uri + "> and class2 =<" + class2_uri + ">)"+
//                        " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " +  NOT_RDF_PROPERTIES +
//                        " UNION " +
//                        "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                        "{rel} rdfs:domain {rango}, {rel} rdfs:range {origin},   {rel} rdf:type {X}, " +
//                        "{class2} rdfs:subClassOf {rango}, " +
//                        "{origin} rdfs:subClassOf {classGeneric}, " +
//                        "[{rel} rdfs:label {rellab}], " +
//                        "[{origin} rdfs:label {originlab}], " +
//                        "[{rango} rdfs:label {rangolab}] " +
//                        " FROM <" + this.repository.getGraphIRI() + "> WHERE (classGeneric=<" + classGeneric_uri + "> and class2 =<" + class2_uri + ">)"+
//                        " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X =<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>)" + NOT_RDF_PROPERTIES;
//                MappingSession.serqlCalls++;
//                  execute (serq2);
//                RDFEntityList slots2 = this.retrieveFullPropertiesFrom();
//                slots.addRDFEntities(slots2);
//        }
//        else {
//                String serq1 ="SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                        "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {rdf:Property}, " +
//                        "{classGeneric} rdfs:subClassOf {origin}, " +
//                        "{class2} rdfs:subClassOf {rango}, " +
//                        "[{rel} rdfs:label {rellab}], " +
//                        "[{origin} rdfs:label {originlab}], " +
//                        "[{rango} rdfs:label {rangolab}] " +
//                        " FROM <" + this.repository.getGraphIRI() + "> WHERE classGeneric =<"+ classGeneric_uri + "> and class2 =<" + class2_uri + ">" + NOT_RDF_PROPERTIES +
//                         " UNION " +
//                        "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                        "{rel} rdfs:domain {origin}, {rel} rdfs:range {rango}, {rel} rdf:type {rdf:Property},  " +
//                        "{class2} rdfs:subClassOf {rango}, " +
//                        "{origin} rdfs:subClassOf {classGeneric}, " +
//                        "[{rel} rdfs:label {rellab}], " +
//                        "[{origin} rdfs:label {originlab}], " +
//                        "[{rango} rdfs:label {rangolab}] " +
//                        " FROM <" + this.repository.getGraphIRI() + "> WHERE classGeneric =<" + classGeneric_uri + "> and class2 =<" + class2_uri + ">" + NOT_RDF_PROPERTIES;
//                MappingSession.serqlCalls++;
//                  execute (serq1);
//                RDFEntityList slots1 = this.retrieveFullPropertiesFrom();
//                slots.addRDFEntities(slots1);
//
//                // Indirect for the sub/super classes (Inverse relations): change origin and range!
//                String serq2 = "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                        "{rel} rdfs:domain {rango}, {rel} rdfs:range {origin}, {rel} rdf:type {rdf:Property}, " +
//                        "{classGeneric} rdfs:subClassOf {origin}, " +
//                        "{class2} rdfs:subClassOf {rango}, " +
//                        "[{rel} rdfs:label {rellab}], " +
//                        "[{origin} rdfs:label {originlab}], " +
//                        "[{rango} rdfs:label {rangolab}] " +
//                        " FROM <" + this.repository.getGraphIRI() + "> WHERE classGeneric =<"+classGeneric_uri + "> and class2 =<" + class2_uri + ">" + NOT_RDF_PROPERTIES +
//                        " UNION " +
//                        "SELECT distinct rel, origin, rango, rellab, originlab, rangolab from " +
//                        "{rel} rdfs:domain {rango}, {rel} rdfs:range {origin}, {rel} rdf:type {rdf:Property}, " +
//                        "{class2} rdfs:subClassOf {rango}, " +
//                        "{origin} rdfs:subClassOf {classGeneric}, " +
//                        "[{rel} rdfs:label {rellab}], " +
//                        "[{origin} rdfs:label {originlab}], " +
//                        "[{rango} rdfs:label {rangolab}] " +
//                        " FROM <" + this.repository.getGraphIRI() + "> WHERE classGeneric=<" + classGeneric_uri + "> and class2 =<" + class2_uri + ">" + NOT_RDF_PROPERTIES;
//                MappingSession.serqlCalls++;
//                  execute (serq2);
//                RDFEntityList slots2 = this.retrieveFullPropertiesFrom();
//                slots.addRDFEntities(slots2);
//        }
//        return slots;
//        } finally {
//          //results.close(); // needed for repository(sesame)
//        }
//    }

    public RDFEntityList getKBPropertiesForGenericClass(String classGeneric_uri, String instance2_uri) throws Exception{
        try{
      RDFEntityList slots = new RDFEntityList();
      String sparql;
      if (classGeneric_uri.startsWith("node")) {
//          serq = "SELECT distinct rel, rellab from " +
//            "{instorigin} rdf:type {classGeneric}, {instorigin} rel {instance2}, " +
//            "[{rel} rdfs:label {rellab}] " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE (classGeneric =\"" + classGeneric_uri + "\"" +
//            "and instance2 =<" + instance2_uri + ">)" +
//             //" and  (lang(rellab) =  \"en\" or BOUND(lang(rellab)) or lang(rellab) = \"en-us\")" +
//             NOT_RDF_PROPERTIES;
          sparql = "SELECT DISTINCT ?rel ?rellab " +
		      	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instorigin rdf:type \"" + classGeneric_uri + "\" . ?instorigin ?rel <" + instance2_uri + "> . " +
		      	"OPTIONAL{?rel rdfs:label ?rellab} . " +
		      	NOT_RDF_PROPERTIES + "}" ;
       }
      else {
//        serq = "SELECT distinct rel, rellab from " +
//            "{instorigin} rdf:type {classGeneric}, {instorigin} rel {instance2}, " +
//            "[{rel} rdfs:label {rellab}] " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE (classGeneric =<" + classGeneric_uri + "> " +
//            "and instance2 =<" + instance2_uri + ">)" +
//             //" and  (lang(rellab) =  \"en\" or BOUND(lang(rellab)) or lang(rellab) = \"en-us\")" +
//             NOT_RDF_PROPERTIES;
        sparql = "SELECT DISTINCT ?rel ?rellab " +
    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instorigin rdf:type <" + classGeneric_uri + "> . ?instorigin ?rel <" + instance2_uri + "> . " +
    	"OPTIONAL{?rel rdfs:label ?rellab}} "; // +
    	 //NOT_RDF_PROPERTIES + "}" ;

      }

       execute (sparql,120000);//NICO
       slots = this.retrieveBasicPropertiesFrom();

       if (classGeneric_uri.startsWith("node")) {
//           serq2 = "SELECT distinct rel, rellab from " +
//            "{instorigin} rdf:type {classGeneric}, {instance2} rel {instorigin}, " +
//            "[{rel} rdfs:label {rellab}] " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE (classGeneric =\"" + classGeneric_uri + "\"" +
//            " and instance2 =<" + instance2_uri + ">)" +
//             //" and  (lang(rellab) =  \"en\" or BOUND(lang(rellab)) or lang(rellab) = \"en-us\")" +
//             NOT_RDF_PROPERTIES;
           sparql = "SELECT DISTINCT ?rel ?rellab " +
			       	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instorigin rdf:type \"" + classGeneric_uri + "\" . <" + instance2_uri + "> ?rel ?instorigin . " +
			       	"OPTIONAL{?rel rdfs:label ?rellab} . " +
			       	NOT_RDF_PROPERTIES + "}" ;
        }
       else {
//         serq2 = "SELECT distinct rel, rellab from " +
//            "{instorigin} rdf:type {classGeneric}, {instance2} rel {instorigin}, " +
//            "[{rel} rdfs:label {rellab}] " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE (classGeneric =<" + classGeneric_uri + "> " +
//            "and instance2 =<" + instance2_uri + ">)" +
//             //" and  (lang(rellab) =  \"en\" or BOUND(lang(rellab)) or lang(rellab) = \"en-us\")" +
//             NOT_RDF_PROPERTIES;
		     sparql = "SELECT DISTINCT ?rel ?rellab " +
				 	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instorigin rdf:type <" + classGeneric_uri + "> . <" + instance2_uri + "> ?rel ?instorigin . " +
				 	"OPTIONAL{?rel rdfs:label ?rellab} } "; // +
				 	//NOT_RDF_PROPERTIES + "}" ;
       }

            execute (sparql,120000);//NICO
        slots.addNewRDFEntities(this.retrieveBasicPropertiesFrom());
        return slots;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

   public ArrayList<RDFPath> getKBIndirectRelationsWithLiterals (String class_URI, String literal) throws Exception{
        try{
         ArrayList<RDFPath> paths = new ArrayList<RDFPath>();
         if (this.repository.getRepositoryName().equals("bbc_backstage")) {
            System.out.println("Looking for indirect relations with a literal is too expensive");
         }
         else {
            String sparql;
            if (literal.contains("\"")) {
//                   serql= "select distinct property, reference, property2 from " +
//                "{instance} property {refInst}, {refInst} property2 {v}, {property2} rdfs:range {String}, " +
//                "{instance} rdf:type {origin}, {refInst} rdf:type {reference}, " +
//                "{property} rdfs:range {reference}, {property2} rdfs:domain {reference}, {property} rdfs:domain {origin} " +
//                " FROM  <" + this.repository.getGraphIRI() + "> WHERE isLiteral(v) and String=<http://www.w3.org/2001/XMLSchema#String> " +
//                "and v = " + literal + " and origin =<" + class_URI + ">";
                   sparql = "SELECT DISTINCT ?property ?reference ?property2 " +
               	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?property ?refInst . ?refInst ?property2 " + literal + " . " +
               	"?property2 rdfs:range <http://www.w3.org/2001/XMLSchema#string> . ?instance rdf:type <" + class_URI + "> . " +
            	"?refInst rdf:type ?reference . " +
            	"?property rdfs:range ?reference . " + //TODO PERFORMANCE delete if possible
            	"?property2 rdfs:domain ?reference . " + //TODO PERFORMANCE delete if possible
            	"?property rdfs:domain ?origin . " + //TODO PERFORMANCE delete if possible
            	//"FILTER (isLiteral(?v) && " +
            	 //TODO PERFORMANCE delete if possible ?STRING
               	//"str(?v) = " + literal + ") . " +
                "}" ;
            }
            else {
//                serql= "select distinct property, reference, property2 from " +
//                "{instance} property {refInst}, {refInst} property2 {v}, {property2} rdfs:range {String}, " +
//                "{instance} rdf:type {origin}, {refInst} rdf:type {reference}, " +
//                "{property} rdfs:range {reference}, {property2} rdfs:domain {reference}, {property} rdfs:domain {origin} " +
//                " FROM  <" + this.repository.getGraphIRI() + "> WHERE isLiteral(v) and String=<http://www.w3.org/2001/XMLSchema#String> " +
//                "and v = \"" + literal +
//                "\" and origin =<" + class_URI + ">";
                sparql = "SELECT DISTINCT ?property ?reference ?property2 " +
            	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?instance ?property ?refInst . ?refInst ?property2 \"" + literal + "\" . " +
            	"?property2 rdfs:range <http://www.w3.org/2001/XMLSchema#string> . ?instance rdf:type <" + class_URI + "> . " +
            	"?refInst rdf:type ?reference . " +
            	"?property rdfs:range ?reference . " + //TODO PERFORMANCE delete if possible
            	"?property2 rdfs:domain ?reference . " + //TODO PERFORMANCE delete if possible
            	"?property rdfs:domain <" + class_URI + "> . } ";// + //TODO PERFORMANCE delete if possible
            	//"FILTER (isLiteral(?v) && " +
           	 //TODO PERFORMANCE delete if possible ?STRING
            	//"str(?v) = \"" + literal + "\" && str(?origin) = <" + class_URI + ">)}" ;
            }

            ///Todo: consider infence a viceverse relationships!
    //select distinct property, reference, property2 from
    //{refInst} property {instance}, {instance} rdf:type {origin},  {instance} rdf:type {originrango}, {refInst} rdf:type {reference},
    //{refInst} property2 {v}, {property2} rdfs:range {String},
    // {property} rdfs:domain {reference}, {property2} rdfs:domain {reference},  {property2} rdfs:range {originrango}
    //FROM <" + this.repository.getGraphIRI() + "> WHERE isLiteral(v) and String=<http://www.w3.org/2001/XMLSchema#String> and v = "Pulp Fiction"@en and origin =<http://dbpedia.org/ontology/Actor>


              execute (sparql,-1);
            paths.addAll(this.retrievePathFrom ());
            for (RDFPath path:paths) {
             path.getRDFProperty1().setDomain(this.getDomainOfProperty(path.getRDFProperty1().getURI()));
             path.getRDFProperty1().setRange(this.getRangeOfProperty(path.getRDFProperty1().getURI()));
             path.getRDFProperty2().setDomain(this.getDomainOfProperty(path.getRDFProperty2().getURI()));
             path.getRDFProperty2().setRange(this.getRangeOfProperty(path.getRDFProperty2().getURI()));
            }
         }
        return paths;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
  }

    public RDFEntityList getInstanceProperties(String instance1_uri, String instance2_uri) throws Exception{
        try{
        RDFEntityList slots = new RDFEntityList();
//        String serq = "SELECT distinct rel, rellab from {instance2} rel {instance1}, " +
//            "[{rel} rdfs:label {rellab}] " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE (instance1 =<" + instance1_uri + "> " +
//            "and instance2 =<" + instance2_uri + ">)";
        String sparql = "SELECT DISTINCT ?rel ?rellab" +
	    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + instance2_uri + "> ?rel <" + instance1_uri + "> . " +
	    	"OPTIONAL{?rel rdfs:label ?rellab}}" ;

        execute (sparql,-1);
        slots = this.retrieveBasicPropertiesFrom();

//        String serq2 = "SELECT distinct rel, rellab from {instance1} rel {instance2}, " +
//            "[{rel} rdfs:label {rellab}] " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE (instance1 =<" + instance1_uri + "> " +
//            "and instance2 =<" + instance2_uri + ">)";
        String sparql2 = "SELECT DISTINCT ?rel ?rellab " +
    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + instance1_uri + "> ?rel <" + instance2_uri + "> . " +
    	"OPTIONAL{?rel rdfs:label ?rellab}}" ;

          execute (sparql2,-1);
        slots.addAllRDFEntity (this.retrieveBasicPropertiesFrom());
        return slots;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

   public String getFirstEnLabel(String entity_uri) throws Exception {

       ResultSet result2 = null;

      try{
    	  String sparql;

	  sparql = "SELECT ?l " +
			" FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + entity_uri + "> rdfs:label ?l} LIMIT 1 ";


            result2 =  executeLocal (sparql); //NICO with ASK maybe
            if (result2.hasNext() == false)
                 return null;

            QuerySolution soln = result2.nextSolution() ;
            String v =  soln.get("l").toString();


            return v;
        } finally {
        }
    }


    /**
     * Get the label of an entity
     * @param entity_uri
     * @return
     * @throws Exception
     * ****************************************************************************
     */
    public String getLabelOfEntity(String entity_uri) throws Exception {
        try{
//        String serql="select label(l) from {<"+entity_uri+">} rdfs:label {l}";
        String sparql = "SELECT ?l " +
				    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + entity_uri + "> rdfs:label ?l}" ;

          execute (sparql,-1);
        if (results.hasNext() == false)
             return null;
        QuerySolution soln = results.nextSolution() ;
        String v =  soln.get("l").toString();
        return v;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    public RDFEntityList getUnionDefinitionForBlankNode (String node) throws Exception {
        try{
        RDFEntityList entList = new RDFEntityList();
        node = node.replaceFirst("_:", ":");
//        String serql = "select c1, l1, c2, l2 " +
//        		"from {class} <http://www.w3.org/2002/07/owl#unionOf> {x}, " +
//            "{x} rdf:first {c1}, [{c1} rdfs:label {l1}], " +
//            "{x} rdf:rest {} rdf:first {c2}, [{c2} rdfs:label {l2}] " +
//            " FROM  <" + this.repository.getGraphIRI() + "> WHERE class = \"" + node + "\"";
        String sparql = "SELECT ?c1 ?l1 ?c2 ?l2" +
				    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?class <http://www.w3.org/2002/07/owl#unxionOf> ?x . ?x rdf:first ?c1 . " +
				    	"OPTIONAL{?c1 rdfs:label ?l1} . null . " + //VANESSA error is by perpose: don't know how to handle lists
				    	"OPTIONAL{?c2 rdfs:label ?l2} . " +
				    	"FILTER (str(?class) = \"" + node + "\") . }" ;

          execute (sparql,-1);
        if (results == null)
            return entList;
        while (results.hasNext()){

            QuerySolution soln = results.nextSolution() ;
            String ent1 =  soln.get("c1").toString();
             String labelent1 = soln.get("l1").toString();
             String ent2 = soln.get("c2").toString();
             String labelent2 = soln.get("l2").toString();
            if (ent1!=null && ent1.toString().trim().length()>0)
                entList.addRDFEntity(new RDFEntity(KswConstant.CLASS_TYPE, ent1.toString().trim(),
                 (labelent1==null?null:labelent1.toString().trim()), this.getPluginID()));
            if (ent2!=null && ent2.toString().trim().length()>0)
                entList.addRDFEntity(new RDFEntity(KswConstant.CLASS_TYPE, ent2.toString().trim(),
                 (labelent2==null?null:labelent2.toString().trim()), this.getPluginID()));
        }
        return entList;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    public RDFEntityList getDomainOfProperty(String entity_uri) throws Exception {
      try{
        RDFEntityList domainList = new RDFEntityList();
//        String serql="select distinct d, l from {<"+entity_uri+">} rdfs:domain {d}, [{d} rdfs:label {l}]";
        String sparql = "SELECT DISTINCT ?d ?l " +
				    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + entity_uri + "> rdfs:domain ?d . " +
				    	"OPTIONAL{?d rdfs:label ?l}}" ;

          execute (sparql,-1);

        if (results == null)
            return domainList;
        while (results.hasNext()){

            QuerySolution soln = results.nextSolution() ;
            String domain = soln.get("d").toString();
            String labelDomain = (soln.get("l")==null?null:soln.get("l").toString());
            if (domain!=null && domain.toString().trim().length()>0){
                if (domain.toString().startsWith("node")) {
                    System.out.println("reading blank node " + domain.toString());
                    domainList.addNewRDFEntities(getUnionDefinitionForBlankNode (domain.toString()));
                }
                else {
                 domainList.addRDFEntity(new RDFEntity(KswConstant.CLASS_TYPE, domain.toString().trim(),
                  (labelDomain==null?null:labelDomain.toString().trim()), this.getPluginID()));
                }
            }
        }
        return domainList;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }

    public boolean isOWLRepository() {
        if (this.getRepositoryType().equals(VirtuosoPlugin.OWL_REPOSITORY))
            return true;
        return false;
    }

    public RDFProperty getRDFProperty(String property_uri) throws Exception {

        if (property_uri!=null && MyURI.isURIValid(property_uri) ) {
            RDFProperty p = new RDFProperty(property_uri, this.getLabelOfEntity(property_uri), this.getPluginID());
            p.setDomain(this.getDomainOfProperty(property_uri));
            p.setRange(this.getRangeOfProperty(property_uri));
            return p;
        }
        return null;
    }

    public RDFEntityList getRangeOfProperty(String property_uri) throws Exception {
        try{
        	RDFEntityList rangeList = new RDFEntityList();

//        String serql="select distinct rt, l from {<"+property_uri+">} rdfs:range {rt}, [{rt} rdfs:label {l}]";
        	String sparql = "SELECT DISTINCT ?rt ?l " +
					    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + property_uri + "> rdfs:range ?rt . " +
					    	"OPTIONAL{?rt rdfs:label ?l}}" ;

        	execute (sparql,-1);

	        if (results == null)
	            return rangeList;
	        while (results.hasNext()){

                    QuerySolution soln = results.nextSolution() ;
                    String range = soln.get("rt").toString();
                    String labelRange = null;
                    if (soln.get("l") != null)
	              labelRange = soln.get("l").toString();

	            if (range != null && MyURI.isURIValid(range.toString())){
	                //if it is a literal
	                if (range.toString().startsWith("http://www.w3.org/2001/XMLSchema#")){//VANESSA maybe its fatser to check it in the query
	                    rangeList.addRDFEntity( new RDFEntity(KswConstant.DATATYPE, range.toString().trim(),
	                            (labelRange==null?null:labelRange.toString().trim()), this.getPluginID()));
	                }
	                else if (range.toString().startsWith("_:node")) {//VANESSA maybe differend in Virtuoso
	                    System.out.println("reading blank node " + range.toString());
	                    rangeList.addNewRDFEntities(getUnionDefinitionForBlankNode (range.toString()));
	                }
	                //if it is class
	                else{
	                    rangeList.addRDFEntity( new RDFEntity(KswConstant.CLASS_TYPE, range.toString().trim(),
	                            (labelRange==null?null:labelRange.toString().trim()), this.getPluginID()));
	                }
	            }
	        }
	        return rangeList;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
    }


     /**
      * For a class return all the entities owl:equivalentClass
      */
     public RDFEntityList getEquivalentEntitiesForClass (String entity_uri) throws Exception {
        try{
	        String sparql;
	        RDFEntityList aux = new RDFEntityList();
//	        serql= " select distinct x, xl from {x} owl:equivalentClass {<" + entity_uri + ">}; [rdfs:label {xl}] " ;
	        sparql = "SELECT DISTINCT ?x ?xl " +
			    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { " +
			    	"{?x owl:equivalentClass <" + entity_uri + ">}" +
			    	"UNION " +
			    	"{<" + entity_uri + "> owl:equivalentClass ?x}. " +
			    	"OPTIONAL{?x rdfs:label ?xl}}" ;

            execute (sparql,-1);
	        aux = this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE);

////	        serql= " select distinct x, xl from {<" + entity_uri + ">} owl:equivalentClass {x}, [{x} rdfs:label {xl}] " ;
//	        sparql = "SELECT DISTINCT ?x ?xl " +
//			    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { " +
//			    	"?entity_uri owl:equivalentClass ?x . " +
//			    	"OPTIONAL{?x rdfs:label ?xl} . " +
//			    	"FILTER (str(?entity_uri) = <" + entity_uri + ">) . }" ;
//
//            execute (sparql,-1);
//	        aux.addNewRDFEntities(this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE));
	        return aux;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
     }

     /**
      * Get All the Equivalent entities found in an ontology
      * owl:sameAs for instances, owl:EquivalentProperty for properties, owl:EquivalentClass for classes
      * Return HashTable<RDFEntity, RDFEntityList>
      */
     public Hashtable <RDFEntity, RDFEntityList> getEquivalentEntitiesForClasses() throws Exception {
         try{
//         String serql = " select distinct ent, entl, eq, eql from {ent} owl:equivalentClass {eq}, [{ent} rdfs:label {entl}], [{eq} rdfs:label {eql}] " ;
         String sparql = "SELECT DISTINCT ?ent ?entl ?eq ?eql " +
				     	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?ent owl:equivalentClass ?eq . " +
				     	"OPTIONAL{?ent rdfs:label ?entl} . " +
				     	"OPTIONAL{?eq rdfs:label ?eql} . }" ;

           execute (sparql,-1);
         return retrieveEquivalentEntityFrom(KswConstant.CLASS_TYPE);
         } finally {
          //results.close(); // needed for repository(sesame)
        }
     }

     /**
      * Get All the Equivalent entities found in an ontology
      * owl:sameAs for instances, owl:EquivalentProperty for properties, owl:EquivalentClass for classes
      * Return HashTable<RDFEntity, RDFEntityList>
      */
     public Hashtable <RDFEntity, RDFEntityList> getEquivalentEntitiesForProperties() throws Exception {
         try{
	//         String serql = " select distinct ent, entl, eq, eql from {ent} owl:equivalentProperty {eq}, [{ent} rdfs:label {entl}], [{eq} rdfs:label {eql}] " ;
	         String sparql = "SELECT DISTINCT ?ent ?entl ?eq ?eql " +
					     	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?ent owl:equivalentProperty ?eq . " +
					     	"OPTIONAL{?ent rdfs:label ?entl} . " +
					     	"OPTIONAL{?eq rdfs:label ?eql} . }" ;

	           execute (sparql,-1);
	         return retrieveEquivalentEntityFrom (KswConstant.PROPERTY_TYPE);
         } finally {
          //results.close(); // needed for repository(sesame)
        }
     }


     /**
      * Get All the Equivalent entities found in an ontology
      * owl:sameAs for instances, owl:EquivalentProperty for properties, owl:EquivalentClass for classes
      * Return HashTable<RDFEntity, RDFEntityList>
      */
     public Hashtable <RDFEntity, RDFEntityList> getEquivalentEntitiesForInstances() throws Exception {
         try{
//	         String serql = " select distinct ent, entl, eq, eql from {ent} owl:sameAs {eq}, [{ent} rdfs:label {entl}], [{eq} rdfs:label {eql}] " ;
	         String sparql = "SELECT DISTINCT ?ent ?entl ?eq ?eql " +
					     	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?ent owl:sameAs ?eq . " +
					     	"OPTIONAL{?ent rdfs:label ?entl} . " +
					     	"OPTIONAL{?eq rdfs:label ?eql} . }" ;

	           execute (sparql,-1);
	         return retrieveEquivalentEntityFrom (KswConstant.INSTANCE_TYPE);
         } finally {
          //results.close(); // needed for repository(sesame)
        }
     }


      /**
      * For a property return all the entities owl:equivalentProperty
      */
     public RDFEntityList getEquivalentEntitiesForProperty (String entity_uri) throws Exception {
         try{
        	 String sparql;
	        RDFEntityList aux = new RDFEntityList();

//	        serql= " select distinct x, xl from {x} owl:equivalentProperty {<" + entity_uri + ">}; [rdfs:label {xl}] " ;
	        sparql = "SELECT DISTINCT ?x ?xl " +
			    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { " +
			    	"{?x owl:equivalentProperty <" + entity_uri + ">} " +
			    	"UNION" +
			    	"{<" + entity_uri + "> owl:equivalentProperty ?x}." +
			    	"OPTIONAL{?x rdfs:label ?xl}}" ;

            execute (sparql,-1);
            aux = this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE);

////        	serql= " select distinct x, xl from {<" + entity_uri + ">} owl:equivalentProperty {x}, [{x} rdfs:label {xl}] " ;
//            sparql = "SELECT DISTINCT ?x ?xl " +
//			    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?entity_uri owl:equivalentProperty ?x . " +
//			    	"OPTIONAL{?x rdfs:label ?xl} . " +
//			    	"FILTER (str(?entity_uri) = <" + entity_uri + ">)}" ;
//
//	        execute (sparql,-1);
//	        aux.addNewRDFEntities(this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE));
	        return aux;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
     }

     /**
      * For an instance return all the entities owl:sameAs
      */
     public RDFEntityList getEquivalentEntitiesForInstance (String entity_uri) throws Exception {
        try{
	        String sparql;
	        RDFEntityList aux = new RDFEntityList();
//	        serql= " select distinct x, xl from {x} owl:sameAs {<" + entity_uri + ">}; [rdfs:label {xl}] " ;
	        sparql = "SELECT DISTINCT ?x ?xl " +
			    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { " +
			    	"{?x owl:sameAs <" + entity_uri + ">} " +
			    	"UNION" +
			    	"{<" + entity_uri + "> owl:sameAs ?x}. " +
			    	"OPTIONAL{?x rdfs:label ?xl}}" ;

	        execute (sparql,-1);
	        aux = this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE);

////	        serql= " select distinct x, xl from {<" + entity_uri + ">} owl:sameAs {x}, [{x} rdfs:label {xl}] " ;
//	        sparql = "SELECT DISTINCT ?x ?xl " +
//			    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?entity_uri owl:sameAs ?x . " +
//			    	"OPTIONAL{?x rdfs:label ?xl} . " +
//			    	"FILTER (str(?entity_uri) = <" + entity_uri + ">) . }" ;
//
//	        execute (sparql,-1);
//	        aux.addNewRDFEntities(this.retrieveRDFEntityFrom(KswConstant.INSTANCE_TYPE));
	        return aux;
        } finally {
          //results.close(); // needed for repository(sesame)
        }
     }

    // SUPER SLOW!!!: probably not needed?
    public boolean existTripleForProperty (String entity)  {
        // is the entity the subject of any triple?
//      try{
//	      try{
////	    	  con = getSesameRepository().getConnection();
////		      String serql = "select p from {s} p {o} FROM <" + this.repository.getGraphIRI() + "> WHERE p =<" + entity + "> limit 1 offset 0";
//		      String sparql = "ASK FROM <" + this.repository.getGraphIRI() + "> WHERE  { ?s ?p ?o . " +
//						  	"FILTER (str(?p) = <" + entity + "> )}";
//
//		      execute(sparql,-1);
//		      if (results !=null && results.hasNext())
//		    		  return true;
//		      return false;
//	      }finally {
//	          //results.close(); // needed for repository(sesame)
////	          con.close();
//	      }
//      }catch (Exception e){
//    	  e.printStackTrace();
//    	  return false;
//      }
        return true;
     }

     public boolean existTripleForInstance (String entity)  {
//      try{
//       try{
//        con = getSesameRepository().getConnection();
//        String serql = "select p from {s} p {o}, {p} rdf:type {X}  FROM <" + this.repository.getGraphIRI() + "> WHERE o = <" + entity + "> " +
//        " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " +
//        " or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " +
//        " limit 1 offset 0";
//        MappingSession.serqlCalls++;
//        TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SERQL, serql);
//         results = tupleQuery.evaluate();
//        if (results !=null) {
//         if (results.n5ext())
            return true;
//        }
//
////         serql = "select p from {s} p {o}, {p} rdf:type {X}  FROM <" + this.repository.getGraphIRI() + "> WHERE s =<" + entity + ">  " +
////        " and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " +
////        " or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) " +
////        " and not p = <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> " +
////        " and not p= <http://www.w3.org/2000/01/rdf-schema#label> " +
////        " limit 1 offset 0";
////        MappingSession.serqlCalls++;
////         tupleQuery = con.prepareTupleQuery(QueryLanguage.SERQL, serql);
////         results = tupleQuery.evaluate();
////        if (results !=null) {
////         if (results.n5ext())
////            return true;
////        }
//        return false;
//        } finally {
//          //results.close(); // needed for repository(sesame)
//          con.close();
//       }
//      } catch (Exception e){
//            e.printStackTrace();
//            return false;
//      }
     }


///////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

     /**
      * Is the literal an alternative name of the instance
      * @param sparql
      * @param timeout_sec if timeout > 0, then it will be set
      * @return
      */
     private void execute (String sparql, int timeout_sec){
    	 MappingSession.increaseVirtuosoCalls();

    	 long start = System.currentTimeMillis();
    	 long time = 0 ;

 	 //String sparqlQueryString = "select distinct ?Concept where {[] a ?Concept } LIMIT 50";


//         //Query query;
//         if (sparql.startsWith("prefix"))
//              query = QueryFactory.create(sparql);
//         else
//             query = QueryFactory.create(Prefixes.RDFS + sparql);
//         QueryExecution qexec = QueryExecutionFactory.sparqlService(this.repository.getServerURL(), query);

           if (!sparql.startsWith("prefix"))
               sparql = Prefixes.RDFS + sparql;

           QueryEngineHTTP qexec = new QueryEngineHTTP (this.repository.getServerURL(), sparql);


        try {
            results = qexec.execSelect();

            time = System.currentTimeMillis() - start;

            if(time > 400){
                    throw new ToLongException();
            }
        }catch(ToLongException e){
            System.out.println("Too long (plugin) : " + time + "ms\t" + sparql + "\t");
             boolean done = false;
             for(StackTraceElement trace : e.getStackTrace()){
                     if(done){
                             System.out.println(trace + "\n");
                             break;
                     }
                     done = true;
              }
        }catch(Exception e){
           System.out.println("Fail to execute: " + sparql);
           e.printStackTrace();

      }
     }


   private ResultSet executeLocal (String sparql ) {
     MappingSession.increaseVirtuosoCalls();
     ResultSet result2 = null;
	   //System.out.println("SPARQL-Query: " + sparql);
     @SuppressWarnings("unused")
     long start = System.currentTimeMillis();
     Query query = QueryFactory.create(Prefixes.RDFS  + sparql);
     QueryExecution qexec = QueryExecutionFactory.sparqlService(this.repository.getServerURL(), query);

     try {
           result2 = qexec.execSelect();
	}catch(Exception e){
           System.out.println("Fail to execute: " + sparql);
           e.printStackTrace();

      }
     return result2;
    }

    private Hashtable<RDFEntity, RDFEntityList> retrieveEquivalentEntityFrom (String type) throws Exception {

        Hashtable<RDFEntity, RDFEntityList> equivalentEntityTable= new Hashtable<RDFEntity, RDFEntityList>();
        if (results == null){
            return equivalentEntityTable;
        }

        for ( ; results.hasNext() ; ) {
            QuerySolution soln = results.nextSolution() ;
            Iterator it = soln.varNames();

            String objectName = (String) it.next();
            String rdfEntity = soln.get(objectName).toString();
            objectName = (String)it.next();
            String rdfEntityLabel = (soln.get(objectName)==null?null:soln.get(objectName).toString());
            objectName = (String)it.next();
            String rdfEquivalentEntity = soln.get(objectName).toString();
            objectName = (String)it.next();
            String rdfEquivalentEntityLabel = (soln.get(objectName)==null?null:soln.get(objectName).toString());

            objectName = (String)it.next();

            if (rdfEntity==null || rdfEquivalentEntity == null) {
                continue;
            }
            //TODO: Check if the RDFS OWL classes can have equivalent entities and erase it
            RDFEntity entity = new RDFEntity(type, rdfEntity.toString(), rdfEntityLabel==null?(new MyURI(rdfEntity.toString())).getLocalName():rdfEntityLabel.toString(), this.getPluginID());
            RDFEntity equivalentEntity = new RDFEntity(type, rdfEquivalentEntity.toString(), rdfEquivalentEntityLabel==null?(new MyURI(rdfEquivalentEntity.toString())).getLocalName():rdfEquivalentEntityLabel.toString(), this.getPluginID());
            if(equivalentEntityTable.containsKey(entity)){
                equivalentEntityTable.get(entity).addRDFEntity(equivalentEntity);
            }
            else{

                RDFEntityList list = new RDFEntityList();
                list.addRDFEntity(equivalentEntity);
                equivalentEntityTable.put(entity, list);
            }
        }
        return equivalentEntityTable;
    }


   private RDFEntityList retrieveRDFTripleValueFrom(String type) throws Exception {
        // more than 10000 elements to retrieve in "Actors in any film"
        RDFEntityList entities=new RDFEntityList();
        if (results==null){
            return entities;
        }
        ArrayList<RDFEntity> entitiesList = new ArrayList<RDFEntity>();

        for ( ; results.hasNext() ; ) {
            QuerySolution soln = results.nextSolution() ;
            Iterator it = soln.varNames();

            String objectName = (String) it.next();
            String rdfEntity = soln.get(objectName).toString();
            objectName = (String) it.next();
            String rdfEntityLabel = (soln.get(objectName)==null?null:soln.get(objectName).toString());
            objectName = (String) it.next();
            String rdfEquivalentEntity = soln.get(objectName).toString();
            objectName = (String) it.next();
            String rdfEquivalentEntityLabel = (soln.get(objectName)==null?null:soln.get(objectName).toString());


            RDFEntity entity = new RDFEntity(type, rdfEntity, rdfEntityLabel, this.getPluginID());

            RDFEntity refEntity = new RDFEntity(type, rdfEquivalentEntity, rdfEquivalentEntityLabel, this.getPluginID());
            entity.setRefers_to(refEntity);


            entitiesList.add(entity);
            //entities.getAllRDFEntities().add(entity);

        }
        entities.getAllRDFEntities().addAll(entitiesList);
        return entities;
    }


    private RDFEntityList retrieveRDFValueFrom(String type) throws Exception {

        RDFEntityList entities=new RDFEntityList();
        if (results == null){
            return entities;
        }

        for ( ; results.hasNext() ; ) {
            QuerySolution soln = results.nextSolution() ;
            Iterator it = soln.varNames();

            String objectName = (String) it.next();
            String value  = soln.get(objectName).toString();



            if (value==null) {
                continue;
            }

            String valueString = value.toString().trim();

           if ((valueString.equalsIgnoreCase(VirtuosoPlugin.owl_namespace+"Class")) ||
                   (valueString.equalsIgnoreCase(VirtuosoPlugin.rdfs_namespace+"Class")) ||
                    (valueString.equalsIgnoreCase(VirtuosoPlugin.rdf_namespace+"List" )) )
            {
                continue;
            }

            String uri=value.toString();

            String label= null;
            objectName = (String) it.next();
            value = soln.get(objectName).toString();
            if (value!=null)
                label=value.toString();
            else {
                label=MyURI.getLocalName(uri);
            }
            // System.out.println("row"+row+"--"+value.toString());
            RDFEntity c=new RDFEntity(type,uri,label, this.getPluginID());
//            if (!entities.isRDFEntityContained(c.getURI()))
            // avoid looking for repetitions
                entities.getAllRDFEntities().add(c);
            // TODO: SAME URI DIFFERENT LABELS
        }
        return entities;
    }


    private RDFEntityList retrieveRDFEntityForIndex (String type) throws Exception {

        RDFEntityList entities=new RDFEntityList();
        if (results == null){
            return entities;
        }

        for ( ; results.hasNext() ; ) {
            QuerySolution soln = results.nextSolution() ;
            Iterator it = soln.varNames();

            String objectName = (String) it.next();
            String value  = soln.get(objectName).toString();

            if (value==null) {
                continue;
            }

            String valueString = value.toString().trim();

           if ((valueString.equalsIgnoreCase(VirtuosoPlugin.owl_namespace+"Class")) ||
                   (valueString.equalsIgnoreCase(VirtuosoPlugin.rdfs_namespace+"Class")) ||
                    (valueString.equalsIgnoreCase(VirtuosoPlugin.rdf_namespace+"List" )) )
            {
                continue;
            }
            //skip the local class node generated in OWL
            if (!MyURI.isURIValid(value.toString())){
                continue;
            }

            String uri=value.toString();

            String label= null;
            objectName = (String) it.next();
            value = soln.get(objectName).toString();
            if (value!=null)
                label=value.toString();
            else{
                String title = null;
                if (type == KswConstant.INSTANCE_TYPE){
                    objectName = (String) it.next();
                    value = soln.get(objectName).toString();
                    if (value!=null)
                        title=value.toString();
                }
                if (title == null)
                 label=MyURI.getLocalName(uri);
                else
                  label = title;
            }
            // System.out.println("row"+row+"--"+value.toString());
            RDFEntity c=new RDFEntity(type,uri,label, this.getPluginID());

            entities.addRDFEntity(c);
        }
        return entities;
}

  private RDFEntityList retrieveRDFEntityFrom (String type) throws Exception {

        RDFEntityList entities=new RDFEntityList();
        if (results==null){
            return entities;
        }
        for ( ; results.hasNext() ; ) {
            QuerySolution soln = results.nextSolution() ;
            Iterator it = soln.varNames();

            String objectName = (String) it.next();
            String firstValue  = soln.get(objectName).toString();

             if (firstValue==null) {
                continue;
              }
             String firstValueString = firstValue.toString().trim();

             if ((firstValueString.equalsIgnoreCase(VirtuosoPlugin.owl_namespace+"Class")) ||
                   (firstValueString.equalsIgnoreCase(VirtuosoPlugin.rdfs_namespace+"Class")) ||
                    (firstValueString.equalsIgnoreCase(VirtuosoPlugin.rdf_namespace+"List" )) ) {
                continue;
             }
            if (!MyURI.isURIValid(firstValueString)){
                if (firstValueString.startsWith("node")) {
                    System.out.println("reading blank node " + firstValueString);
                    entities.addNewRDFEntities(getUnionDefinitionForBlankNode(firstValueString));
                }
                continue;
            }
            String uri=firstValueString;
            String secondValue = null;
            if (it.hasNext()) { // label may be null
                objectName = (String) it.next();
                secondValue  = soln.get(objectName).toString();
            }
            String label= null;
            if (secondValue!=null)
                label=secondValue.toString();
            else{
                 label=MyURI.getLocalName(uri);
            }

            RDFEntity c=new RDFEntity(type,uri,label, this.getPluginID());
            // i don' tneed to check repetitions because is expensive and I send all the queries with "distinct"
            //entities.addRDFEntity(c);
            entities.getAllRDFEntities().add(c);
        }
        return entities;
    }


 private RDFEntityList retrieveRDFEntityLiteralsFrom(String instance_uri, boolean limitsize) throws Exception {

        RDFEntityList entities=new RDFEntityList();
        if (results==null)
            return entities;


        while (results.hasNext()){
            QuerySolution soln = results.nextSolution() ;
            String value = soln.get("v").toString();
            if (value==null){
                continue;
            }
            String finalValue = value.toString().trim();

            if( (limitsize == false) || (finalValue.length()<= KswConstant.MAX_LITERAL_LENGTH && finalValue.length()>0)){
                RDFEntity c=new RDFEntity(KswConstant.LITERAL_TYPE, instance_uri, finalValue, this.getPluginID());
                entities.addRDFEntity(c);
            }
        }
        return entities;
    }

    private ArrayList<String> retrieveEntitiesUriFrom(boolean keepPrimitiveURIs) throws Exception {

        ArrayList<String> entities=new ArrayList<String>();
        if (results==null){
            return entities;
        }

       for ( ; results.hasNext() ; ) {
            QuerySolution soln = results.nextSolution() ;
            Iterator it = soln.varNames();

            String objectName = (String) it.next();
            String value  = soln.get(objectName).toString();
            if (value==null)
                continue;

            if (!MyURI.isURIValid(value.toString()))
                continue;

            if (!keepPrimitiveURIs)
                if (value.toString().equals(VirtuosoPlugin.owl_namespace+"Class") ||
                    value.toString().equals(VirtuosoPlugin.rdfs_namespace+"Class") ||
                    value.toString().equals(VirtuosoPlugin.rdf_namespace+"List"))
                    continue;

            entities.add(value.toString());
        }

        return entities;
    }


    private ArrayList<RDFPath> retrieveFullPathFrom () throws Exception {

//        select distinct property 1, proplabel 2, reference 3, reflabel 4, property2 5, prop2label 6, " +
//            "subject 7, originlabel 8, reference, reflabel, reference, reflabel, destiny 9, targetlabel 10
        ArrayList<RDFPath> paths = new ArrayList<RDFPath>();
        if (results==null){
            return paths;
        }
       for ( ; results.hasNext() ; ) {
            QuerySolution soln = results.nextSolution() ;



            RDFPath path;

            RDFEntity Refent = null;
            String valueRefEnt = soln.get("reference").toString();
            String labelRefEnt =  (soln.get("reflabel")==null?null:soln.get("reflabel").toString());

            if (valueRefEnt!=null && MyURI.isURIValid(valueRefEnt.toString()) ) {
               Refent = new RDFEntity (KswConstant.CLASS_TYPE, valueRefEnt.toString(),
                        (labelRefEnt==null?null:labelRefEnt.toString().trim()),  this.getPluginID());
            }

            RDFProperty prop1 = null;
            RDFProperty prop1_domain = null;
//            RDFProperty prop1_range = null;
            String valueProp1 = soln.get("property").toString();
            String labelProp1 = (soln.get("proplabel")==null?null:soln.get("proplabel").toString());
            String propertydomain = soln.get("subject").toString();
            String propertydomainlabel =(soln.get("originlabel")==null?null:soln.get("originlabel").toString());
//            Value propertyrange = results.getString(9);
//            Value propertyrangelabel = results.getString(10);

            if (valueProp1!=null && MyURI.isURIValid(valueProp1.toString()) ) {
                prop1 = new RDFProperty(valueProp1.toString(), (labelProp1==null?null:labelProp1.toString().trim()),
                        this.getPluginID());
            }
            if (propertydomain!=null && MyURI.isURIValid(propertydomain.toString()) ) {
                prop1_domain = new RDFProperty(propertydomain.toString(), (propertydomainlabel==null?null:propertydomainlabel.toString().trim()),
                        this.getPluginID());
            }


            prop1.setDomain(prop1_domain);

            RDFProperty prop2= null;
            RDFProperty prop2_range = null;
             String valueProp2 = soln.get("property2").toString();
             String labelProp2 = (soln.get("prop2label")==null?null:soln.get("prop2label").toString());
             String propertyrange2 = soln.get("destiny").toString();
             String propertyrangelabel2 = (soln.get("targetlabel")==null?null:soln.get("targetlabel").toString());

            if (valueProp2!=null && MyURI.isURIValid(valueProp2.toString()) ) {
                prop2 = new RDFProperty(valueProp2.toString(), (labelProp2==null?null:labelProp2.toString().trim()),
                        this.getPluginID());
            }


            if (propertyrange2!=null && MyURI.isURIValid(propertyrange2.toString()) ) {
                prop2_range = new RDFProperty(propertyrange2.toString(), (propertyrangelabel2==null?null:propertyrangelabel2.toString().trim()),
                        this.getPluginID());
            }

            prop2.setDomain (Refent);
            prop2.setRange (prop2_range);

           path = new RDFPath (prop1, Refent, prop2);
           paths.add (path);
        }
        return paths;
    }

    // In case of multiple labels I ask  for the first label in here.
    private ArrayList<RDFPath> retrievePathFrom () throws Exception {

        ArrayList<RDFPath> paths = new ArrayList<RDFPath>();
        if (results==null){
            return paths;
        }
        while (results.hasNext()){
            RDFPath path;

            RDFEntity ent = null;

            QuerySolution soln = results.nextSolution() ;
            Iterator it = soln.varNames();

            String objectName = (String) it.next();
            String valueProp1  = soln.get(objectName).toString();
            objectName = (String) it.next();
            String valueEnt  = soln.get(objectName).toString();
            objectName = (String) it.next();
            String valueProp2  = soln.get(objectName).toString();

            //String valueEnt = results.getString(2);

            if (valueEnt!=null && MyURI.isURIValid(valueEnt.toString()) ) {
                String labelEnt = this.getFirstEnLabel(valueEnt.toString());
                ent = new RDFEntity (KswConstant.CLASS_TYPE, valueEnt.toString(),
                        (labelEnt==null?null:labelEnt),  this.getPluginID());
            }

            RDFProperty prop1 = null;
            //String valueProp1 = results.getString(1);

            if (valueProp1!=null && MyURI.isURIValid(valueProp1.toString()) ) {
                String labelProp1 = this.getFirstEnLabel(valueProp1.toString());
                prop1 = new RDFProperty(valueProp1.toString(), (labelProp1==null?null:labelProp1),
                        this.getPluginID());
            }
            RDFProperty prop2= null;
            //String valueProp2 = results.getString(3);

            if (valueProp2!=null && MyURI.isURIValid(valueProp2.toString()) ) {
                 String labelProp2 = this.getFirstEnLabel(valueProp2.toString());
                prop2 = new RDFProperty(valueProp2.toString(), (labelProp2==null?null:labelProp2),
                        this.getPluginID());
            }

           path = new RDFPath (prop1, ent, prop2);
           paths.add (path);
        }
        return paths;
    }


    private RDFEntityList retrieveFullPropertiesFrom() throws Exception {

        RDFEntityList properties    = new RDFEntityList();
        if (results == null){
            return properties;
        }

        Hashtable<String, RDFProperty> propertiesTable = new Hashtable<String, RDFProperty>();
        while (results.hasNext()){
            RDFProperty p=null;

            QuerySolution soln = results.nextSolution() ;
            Iterator it = soln.varNames();

            String objectName = (String) it.next();
            String value  = soln.get(objectName).toString();
            objectName = (String) it.next();
            String origin  = soln.get(objectName).toString();
            objectName = (String) it.next();
            String range  = soln.get(objectName).toString();
            objectName = (String) it.next();
            String labelP  = (soln.get(objectName)==null?null:soln.get(objectName).toString());
            objectName = (String) it.next();
            String labelOrigin  = (soln.get(objectName)==null?null:soln.get(objectName).toString());
            objectName = (String) it.next();
            String labelRange  =(soln.get(objectName)==null?null:soln.get(objectName).toString());

           // String value = results.getString(1);
           // String labelP = results.getString(4);

            if (value != null && MyURI.isURIValid(value.toString()) ) {
                //Check if the property is already in the table
                //TODO:Allow that RDFEntities contain more than one label
                if(propertiesTable.contains(value)){
                    p = propertiesTable.get(value);

                }else{
                    //create the property
                    p = new RDFProperty(value.toString(), (labelP==null?null:labelP.toString().trim()), this.getPluginID());
                    propertiesTable.put(value.toString(), p);
                }

                //add the domain
              //  String origin = results.getString(2);
              //  String labelOrigin = results.getString(5);
                if (origin!=null && MyURI.isURIValid(origin.toString())){
                    RDFEntity classDomain = new RDFEntity(KswConstant.CLASS_TYPE,
                            origin.toString().trim(), (labelOrigin==null?null:labelOrigin.toString().trim()), this.getPluginID());
                    p.addDomain(classDomain);
                }
                //Add Range
              //  String range = results.getString(3);
              //  String labelRange = results.getString(6);
                if (range!=null && MyURI.isURIValid(range.toString())){
                    RDFEntity classRange = null;
                    if(range.toString().startsWith("http://www.w3.org/2001/XMLSchema#")){
                        classRange = new RDFEntity(KswConstant.DATATYPE, range.toString().trim(),
                                (labelRange==null?null:labelRange.toString().trim()),
                                this.getPluginID());
                        p.addRange(classRange);
                    } else{
                        classRange =  new RDFEntity(KswConstant.CLASS_TYPE,
                                range.toString().trim(),(labelRange==null?null:labelRange.toString().trim()),
                                this.getPluginID());
                        p.addRange(classRange);
                    }
                }
            }
        }
        //put the properties on the list
        for(RDFProperty p: propertiesTable.values()){
            properties.addRDFEntity(p);
        }
        return properties;
    }

    private RDFEntityList retrieveBasicPropertiesFrom() throws Exception {

        RDFEntityList properties    = new RDFEntityList();
        if (results==null){
            return properties;
        }

        while (results.hasNext()){
            RDFProperty p=null;
            QuerySolution soln = results.nextSolution() ;
            Iterator it = soln.varNames();

            String objectName = (String) it.next();
            String rel  = soln.get(objectName).toString();
            String rel_label = null;
            if (it.hasNext()) {
                objectName = (String) it.next();
                rel_label  = (soln.get(objectName)==null?null:soln.get(objectName).toString());
            }
            if (rel!=null && MyURI.isURIValid(rel.toString())) {
                p = new RDFProperty(rel.toString(), (rel_label==null?null:rel_label.toString().trim()), this.getPluginID());
                // I can not do that withouth reseting the connection!!!
                //p.setDomain(this.getDomainOfProperty(rel.toString()));
                //p.setRange(this.getRangeOfProperty(rel.toString()));
                properties.getAllRDFEntities().add(p);
            }
       }
        for (RDFEntity p: properties.getAllRDFEntities()) {
              ((RDFProperty)p).setDomain(this.getDomainOfProperty(p.getURI()));
              ((RDFProperty)p).setRange(this.getRangeOfProperty(p.getURI()));
        }

        return properties;
    }


    private RDFEntityList getSubClassesFromIntersectionDefinition(String class_uri) throws Exception {
        try{
//	        String serql="select c,l from {c} owl:intersectionOf {x}; [rdfs:label {l}], {x} p {<"+class_uri+">}";
	        String sparql = "SELECT ?c ?l " +
					    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { " +
					    	"?c owl:intersectionOf ?x . " +
					    	"OPTIONAL{?c rdfs:label ?l} . ?x ?p ?<" + class_uri + "> . }" ;

	        execute (sparql,-1);
	        return this.retrieveRDFEntityFrom(KswConstant.CLASS_TYPE);
        } finally {
            //results.close(); // needed for repository(sesame)
        }
    }

    private boolean isURIString(String s) {
        if (s.startsWith("http:") || s.startsWith("file:"))
            return true;

        return false;
    }



    public String getName() {
//        return this.repository.getRepositoryName();
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getServerURL() {
        return this.repository.getServerURL();
    }

//    public void setServerURL(String serverURL) {
//        this.serverURL = serverURL;
//    }

    public String getRepositoryType() {
        return this.repository.getRepositoryType();
    }

//    public void setRepositoryType(String repositoryType) {
//        this.repositoryType = repositoryType;
//    }


    public String getRepositoryName() {
        return this.repository.getRepositoryName();
    }

//    public void setRepositoryName(String repositoryName) {
//        this.repositoryName = repositoryName;
//    }

    public String getLogin() {
        return this.repository.getLogin();
    }

//    public void setLogin(String login) {
//        this.login = login;
//    }

    public String getPassword() {
        return this.repository.getPassword();
    }

//    public void setPassword(String password) {
//        this.password = password;
//    }



    public String getPluginID() {
        return this.repository.getRepositoryName();

                //this.repository.getServerURL() + "/" +
    }


    //========================
    //  REQUIRED FOR THE TRUST MECHANISM
    //=========================
    /* <*, *, *>  !!!!!!! */
    public int numberOfAllTriples(String onto) {
       try {
	       try{
	//       con = getSesameRepository().getConnection();
	//       String serql =  "select p from {s} p {o} limit 100000";
	       String sparql = "SELECT count(*)" +
				" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?s ?p ?o } LIMIT 100000";    //TODO TIMELIMIT
	       execute (sparql,1);
	       if (results !=null) {
	          int rowCount= 0;
	          while (results.hasNext())
	        	  rowCount = (Integer) results.next();//getInt(1);
	//              rowCount++;
	          return rowCount;
	        }
	       } finally {
	//          con.close();
	          //results.close(); // needed for repository(sesame)
	       }
       }catch (Exception e) {
            System.out.println("Exception counting number of triples to calculate trust in " + onto);
            e.printStackTrace();
       }
       return -1;
    }

    /* <i, *, *> */
    public int numberOfTriplesWithInstanceAsSubject(String onto, String i){
        return numberOfTriplesWithPropertyAsSubject (onto, i);
        //XXX count
    }

    /* <c, subClassOf, *> */
    public int numberOfTriplesWithClassAsSubjectAndSubClassOfProperty(String onto, String class_uri){
    	try{
    		try{
    			String sparql;
	            @SuppressWarnings("unused")
				RDFEntityList classes = new RDFEntityList();
	            if (!class_uri.startsWith("node") && !class_uri.startsWith("_:node")) {
	//                serql= "select distinct c, l from {<" + class_uri + ">} rdfs:subClassOf {c}, " +
	//                    "[{c} rdfs:label {l}] FROM <" + this.repository.getGraphIRI() + "> WHERE isURI(c) and not c=<" + class_uri + "> " +
	//                    "minus select distinct c, l from {<"+ class_uri +">} rdfs:subClassOf {c2} rdfs:subClassOf {c}, " +
	//                    "[{c} rdfs:label {l}] FROM <" + this.repository.getGraphIRI() + "> WHERE isURI(c) and isURI(c2) " +
	//                    "and not c2=<"+ class_uri +">  and not c=c2";
	                sparql = "SELECT count(*) " +
	    		            " FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + class_uri + "> rdfs:subClassOf ?c . " +
	    		        	"OPTIONAL{?c rdfs:label ?l} . " +
	    		        	"FILTER ( (isURI(?c) && not str(?c) = <" + class_uri + "> ))}";

	                execute (sparql,-1);
	                if (results !=null) {
	                	int rowCount= 0;
	                	while (results.hasNext())
	                		rowCount = (Integer)results.next(); //getInt(1);
	                	return rowCount;
	                }
            	}
            }finally {
//                  con.close();
              //results.close(); // needed for repository(sesame)
            }
    	}catch (Exception e) {
	        System.out.println("Exception counting number of triples to calculate trust in " + onto);
	        e.printStackTrace();
    	}
        return -1;
    }

    /* deduce all the classes of i */
    public Set<String> getClassesOf(String onto, String i){
        Set<String> results = new HashSet<String>();
        try {
            RDFEntityList classes = getAllClassesOfInstance(i);
            for (RDFEntity classe: classes.getAllRDFEntities()){
                results.add (classe.getURI());
            }
        }catch (Exception e) {
           System.out.println("Exception counting number of triples to calculate trust in " + onto);
       }
        return results;
    }

    /* deduce all the instances of c */
    public Set<String> getInstancesOf(String onto, String c){
        Set<String> results = new HashSet<String>();
        try {
            RDFEntityList instances = getAllInstancesOfClass (c, -1);
            for (RDFEntity instance: instances.getAllRDFEntities()){
                results.add (instance.getURI());
            }
         }catch (Exception e) {
           System.out.println("Exception counting number of triples to calculate trust in " + onto);
       }
        return results;
    }

    /* <c, *, *> */
    public int numberOfTriplesWithClassAsSubject(String onto, String c){
        return numberOfTriplesWithPropertyAsSubject (onto, c);
    }

    /* <*, type, c> */
    public int numberOfTriplesWithClassAsObjectAndTypeProperty(String onto, String c){
    	try{
            String sparql = Prefixes.RDF + Prefixes.RDFS +
    		"SELECT DISTINCT count(*) " +
    		" FROM  <" + this.repository.getGraphIRI() + "> " +
    				"WHERE{ ?i rdf:type <" + c + "> . " +
    				"OPTIONAL{?i rdfs:label ?l}. " +
    				"FILTER( isURI(?i) )}";

            execute (sparql,-1);
            if (results !=null) {
            	int rowCount= 0;
            	while (results.hasNext())
          		rowCount = (Integer)results.next(); //getInt(1);
            	return rowCount;
            }
        } catch (Exception e) {
			e.printStackTrace();
		} finally {
              //results.close(); // needed for repository(sesame)
    	}
        return -1;
    }

    /* <*, domain, c> */
    public int numberOfTripleWithClassAsObjectAndDomainProperty(String onto, String c){
       try {
         return this.getDomainOfProperty(c).size(); //TODO count : mayby
       }catch (Exception e) {
           System.out.println("Exception counting number of triples to calculate trust in " + onto);
       }
       return -1;
    }

    /* <p, *, *> */
    public int numberOfTriplesWithPropertyAsSubject(String onto, String p){
       try { try{
//       con = getSesameRepository().getConnection();
//       String serql =  "select p from {s} p {o} FROM <" + this.repository.getGraphIRI() + "> WHERE s =<" + p + "> limit 1 offset 0";
       String sparql = "SELECT count(*) " +
				   	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { <" + p + "> ?p ?o . }";
       execute (sparql,-1);
       if (results !=null) {
          int rowCount=0;
          while (results.hasNext())
              rowCount = (Integer) results.next(); //getInt(1);
          return rowCount;
        }
       } finally {
//          con.close();
          //results.close(); // needed for repository(sesame)
          }
       }catch (Exception e) {
           System.out.println("Exception counting number of triples to calculate trust in " + onto);
           e.printStackTrace();
       }
       return -1;
    }

    /* <*, p, *> */
    public int numberOfTriplesWithPropertyAsPredicate(String onto, String p) {
       try { try {
       String sparql = "SELECT count(*) " +
					   	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?s <" + p + "> ?o}";
       execute (sparql,-1);
       if (results !=null) {
          int rowCount=0;
          while (results.hasNext())
              rowCount = (Integer) results.next(); //.getInt(1);
          return rowCount;
        }
        } finally {
//          con.close();
          //results.close(); // needed for repository(sesame)
          }
       }catch (Exception e) {
           System.out.println("Exception counting number of triples to calculate trust in " + onto);
           e.printStackTrace();
       }
       return -1;
    }


    /* deduce all the sub properties of p */
    public Set<String> getSubPropertyOf(String onto, String p){
        Set<String> res = new HashSet<String>();
        return res;
    }

    /* Returns the type for the entity according to the three static values of this interface */
    public String entityType(String onto, String e){
        //  "Class"
        //  "Property"
        //  "Individual"
        try { try {
//        con = getSesameRepository().getConnection();
//        String serql = "select c from {c} rdf:type {owl:Class} FROM <" + this.repository.getGraphIRI() + "> WHERE c=<"+e+">";
        String sparql = "SELECT ?c" +
				    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?c rdf:type owl:Class. " +
				    	"FILTER (str(?c) = <" + e + ">)}" ;//not posible to put it in the where clause <" + e + "> instead of ?p, needed in select
        execute (sparql,-1);
        if (results != null){
          int rowCount=0;
          while (results.hasNext())
              rowCount++;
          if (rowCount > 0)
              return "Class";
        }

//        serql = "select c from {c} rdf:type {rdfs:Class} FROM <" + this.repository.getGraphIRI() + "> WHERE c=<"+e+">";
        sparql = "SELECT ?c" +
		    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?c rdf:type rdfs:Class . " +
		    	"FILTER (str(?c) = <" + e + ">)}" ;//not posible to put it in the where clause <" + e + "> instead of ?p, needed in select
        execute (sparql,-1);
        if (results != null){
          int rowCount=0;
          while (results.hasNext())
              rowCount++;
          if (rowCount > 0)
              return "Class";
        }

//        serql = "select p from {p} rdf:type {X} " +
//          " FROM  <" + this.repository.getGraphIRI() + "> WHERE  p=<"+e+"> and (X = <http://www.w3.org/2002/07/owl#ObjectProperty> or X = <http://www.w3.org/2002/07/owl#DataProperty> " +
//          "or X = <http://www.w3.org/2002/07/owl#DatatypeProperty> or X=<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) ";
        sparql = "SELECT ?p" +
		    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?p rdf:type ?X . " +
		    	"FILTER ( str(?p) = <" + e + "> && " +//not posible to put it in the where clause <" + e + "> instead of ?p, needed in select
		    			"(str(?X) = <http://www.w3.org/2002/07/owl#ObjectProperty> || str(?X) = <http://www.w3.org/2002/07/owl#DataProperty> ||" +
		    			" str(?X) = <http://www.w3.org/2002/07/owl#DatatypeProperty> || str(?X) = <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>) )}" ;
        execute (sparql,-1);
        if (results != null){
          int rowCount=0;
          while (results.hasNext())
              rowCount++;
          if (rowCount > 0)
              return "Property";
        }


//        serql=" select p from {p} rdf:type {rdf:Property} FROM <" + this.repository.getGraphIRI() + "> WHERE  p=<"+e+">";
        sparql = "SELECT ?p" +
		    	" FROM  <" + this.repository.getGraphIRI() + "> WHERE { ?p rdf:type rdf:Property . " +
		    	"FILTER ( str(?p) = <" + e + ">)}" ;//not posible to put it in the where clause <" + e + "> instead of ?p, needed in select
        execute (sparql,-1);
        if (results != null){
          int rowCount=0;
          while (results.hasNext())
              rowCount++;
          if (rowCount > 0)
              return "Property";
        }

        return "Individual";
        } finally {
//          con.close();
          //results.close(); // needed for repository(sesame)
          }
        }catch (Exception ex) {
           System.out.println("Exception counting number of triples to calculate trust in " + onto);
       }
        return null;
    }


//    public org.openrdf.repository.Repository getSesameRepository() {
//        return sesameRepository;
//    }
//
//    public void setSesameRepository(org.openrdf.repository.Repository sesameRepository) {
//        this.sesameRepository = sesameRepository;
//    }


//	public static void main(String[] args) throws Exception{
//		System.out.println("start");
//		String sparql;
//
//		VirtuosoPlugin plugin = new VirtuosoPlugin();
//		plugin.loadPlugin(new RepositoryVirtuoso("kmi-web03.open.ac.uk", "wwwcache.open.ac.uk", "8890", "dba", "dba",
//	    		"virtuoso", "jdbc:virtuoso://kmi-web03.open.ac.uk:8890#http://dbpedia.org", "OWL"));
//
//		try {
//
//			@SuppressWarnings("unused")
//			long startTime = System.currentTimeMillis();
//
////			sparql = getSPARQL(new String[]{"festival", "film"},true, ObjectType.PROPERTY, 20);
//			plugin.execute ("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT ?c ?l FROM <http://dbpedia.org> WHERE{ <http://dbpedia.org/resource/Russia> rdf:type ?c. OPTIONAL{ ?c rdfs:label ?l}}",-1);
////			System.out.println(sparql);
////			System.out.println("done");
//		} catch (Exception e) {
//			System.out.println("not done");
//			e.printStackTrace();
//		}
//	}
	}








