package eu.sealsproject.domain.sst;

import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import poweraqua.LinguisticComponent.LinguisticComponent;
import poweraqua.core.model.myrdfmodel.RDFEntity;
import poweraqua.core.model.myrdfmodel.RDFEntityList;
import poweraqua.core.plugin.OntologyPlugin;
import poweraqua.core.tripleModel.linguisticTriple.QueryTriple;
import poweraqua.fusion.FusedAnswerBean;
import poweraqua.fusion.FusionService;
import poweraqua.fusion.RDFEntityCluster;
import poweraqua.fusion.RDFEntityEntry;
import poweraqua.indexingService.manager.IndexManager;
import poweraqua.indexingService.manager.MultiIndexManager;
import poweraqua.powermap.mappingModel.MappingSession;
import poweraqua.powermap.triplePhase.AnswersEngine;
import poweraqua.powermap.triplePhase.TripleMappingTable;
import poweraqua.powermap.triplePhase.TripleSimilarityService;
import poweraqua.ranking.MappingRanking;
import poweraqua.serviceConfig.MultiOntologyManager;

public class SEALSSemanticSearchToolImp
  implements SEALSSemanticSearchTool
{
  private LinguisticComponent chunk;
  private MappingSession mapSession;
  private boolean resultsReady;
  private String user_query = null;
  private String real_path;
  private long time_mapping;
  private long total_time;
  private long time_fusion;
  private Hashtable<Integer, ArrayList<RDFEntityCluster>> fuseAnswers;
  private Hashtable<Integer, RDFEntityList> individualAnswers;
  private ArrayList<String> idPluginsList;
  Hashtable<String, OntologyPlugin> osPluginsList;
  private int num_variable_name = 1;
  private String logPath = "";
  private double timeMillis = 0.0D;
  private boolean is_user_in_loop = false;
  private LogConfig logConfig = null;
  long lastaccess = 0L;
  long firstaccess = 0L;
  private ArrayList<String> answers = new ArrayList();
  
  public void initialize()
  {
    System.out.println("Initializing PowerAqua");
    clean();
    try
    {
      this.chunk = new LinguisticComponent();
      this.mapSession = new MappingSession();
      this.mapSession.setSesame_threshold(0.29D);
      this.mapSession.setUseSynonyms(false);
      MultiOntologyManager mom = this.mapSession.getMultiOntologyManager();
      this.idPluginsList = mom.listIdPlugings();
      this.osPluginsList = mom.getOsPlugins();
    }
    catch (Exception e)
    {
      System.out.println("Exception initializing PowerAqua");
      e.printStackTrace();
    }
  }
  
  public void clean()
  {
    this.resultsReady = false;
    this.fuseAnswers = new Hashtable();
    this.individualAnswers = new Hashtable();
  }
  
  public boolean loadOntology(URI ontology, String ontologyName, String ontologyNamespace)
  {
    if (ontologyName.equals("MooneyTestSuite")) {
      return true;
    }
    initialize();
    
    MultiOntologyManager mom = this.mapSession.getMultiOntologyManager();
    MultiIndexManager mim = this.mapSession.getMultiIndexManager();
    for (String id : this.idPluginsList)
    {
      System.out.println(id);
      if (id.equals(ontologyName))
      {
        System.out.println("The ontology " + ontologyName + " is accesible and loaded");
        System.out.println("PowerAqua loading the plugine " + id + " only ");
        mom.filterPlugin(id);
        IndexManager im = mim.findOntologIndex(id);
        if (im != null) {
          mim.filterIndex(im);
        } else {
          System.out.println("All Lucene indexes loaded ");
        }
        return true;
      }
    }
    System.out.println("The ontology has to be accesible online in a Virtuoso server, or a Sesame repository indexed with Lucene ");
    System.out.println("PowerAqua queries all the " + mom.getNumberPlugins() + " repositories specified in PowerAqua configuration files ");
    return false;
  }
  
  public boolean isResultSetReady()
  {
    if (((this.is_user_in_loop) && (this.logConfig == null)) || (this.user_query == null))
    {
      System.out.println("Class isUserInputComplete first");
      return false;
    }
    if (this.is_user_in_loop)
    {
      this.logConfig = new LogConfig();
      this.logConfig.readLogAnswers();
      if ((this.logConfig.millis > this.firstaccess) && (this.user_query != null))
      {
        this.firstaccess = this.logConfig.millis;
        setAnswers(this.logConfig.answers);
        
        return true;
      }
      return false;
    }
    if (this.resultsReady) {
      return true;
    }
    return false;
  }
  
  private String printHead()
  {
    String results = "<?xml version=\"1.0\"?> \n<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\"> \n";
    
    results = results.concat("<head> \n ");
    if (!this.is_user_in_loop) {
      for (int i = 0; i < this.num_variable_name; i++)
      {
        results = results.concat("<variable name=\"questionAnswer" + (i + 1) + "\"> \n ");
        results = results.concat("</variable> \n");
      }
    }
    results = results.concat("</head> \n <results> \n ");
    return results;
  }
  
  private String printTail()
  {
    String results = "</results> \n </sparql> \n";
    return results;
  }
  
  private String printResult(String uriResult, String variableName, double score)
  {
    String results = "<result> \n<binding name=\"" + variableName + "\"> \n" + "<uri>" + uriResult + "</uri> \n </binding> \n </result> \n";
    
    return results;
  }
  
  private String printResult(String uriResult)
  {
    String results = "<result> \n<binding name=\"questionAnswer\"> \n <uri>" + uriResult + "</uri> \n </binding> \n </result> \n";
    
    return results;
  }
  
  public String getResults()
  {
    String results = printHead();
    String variableName = "questionAnswer";
    RDFEntityList entities;
    if (!this.is_user_in_loop)
    {
      String aux;
      for (Integer num = Integer.valueOf(1); num.intValue() <= this.num_variable_name; aux = num = Integer.valueOf(num.intValue() + 1))
      {
        String aux;
        double score;
        if ((this.fuseAnswers.get(num) != null) && (!((ArrayList)this.fuseAnswers.get(num)).isEmpty()))
        {
          System.out.println("fused results for query " + num);
          ArrayList<RDFEntityCluster> clusters = (ArrayList)this.fuseAnswers.get(num);
          aux = variableName.concat(num.toString());
          for (RDFEntityCluster cluster : clusters)
          {
            score = cluster.getRankingScore();
            if (score == 1.0D) {
              score = 0.0D;
            } else {
              score = 0.5D;
            }
            for (RDFEntityEntry entry : cluster.getEntries())
            {
              RDFEntity entity = entry.getValue();
              String entityURI = entity.getURI();
              System.out.println(entityURI);
              results = results.concat(printResult(entityURI, aux, score));
            }
          }
        }
        else
        {
          entities = (RDFEntityList)this.individualAnswers.get(num);
          System.out.println("individual results for query " + num);
          aux = variableName.concat(num.toString());
          for (RDFEntity ent : entities.getAllRDFEntities())
          {
            System.out.println(ent.getURI());
            results = results.concat(printResult(ent.getURI(), aux, 1.0D));
          }
        }
        entities = num;
      }
    }
    else
    {
      System.out.println("user in loop");
      System.out.println("REtrieving " + this.answers.size());
      for (String answer : this.answers) {
        results = results.concat(printResult(answer));
      }
    }
    results = results.concat(printTail());
    return results;
  }
  
  public boolean isRankedList()
  {
    return true;
  }
  
  private ArrayList<String> parseQueries(String query)
  {
    this.num_variable_name = 0;
    ArrayList<String> queryList = new ArrayList();
    if (!query.contains("||"))
    {
      this.num_variable_name += 1;
      queryList.add(query);
      return queryList;
    }
    String[] queries = query.split("\\|\\|");
    for (int i = 0; i < queries.length; i++)
    {
      this.num_variable_name += 1;
      System.out.println("extracting query " + this.num_variable_name + " : " + queries[i]);
      queryList.add(queries[i].trim());
    }
    return queryList;
  }
  
  public boolean executeQuery(String queries)
  {
    this.resultsReady = false;
    this.total_time = System.currentTimeMillis();
    this.is_user_in_loop = false;
    this.user_query = queries;
    try
    {
      clean();
      ArrayList<String> queryList = parseQueries(queries);
      n = 0;
      for (String query : queryList)
      {
        n++;
        this.chunk.parseQuestion(query);
        System.out.println("The query types is " + this.chunk.typeQuestion);
        
        TripleSimilarityService TSS = new TripleSimilarityService(this.mapSession, this.chunk.getQueryTriples());
        TSS.TripleMapping();
        AnswersEngine answersEngine = new AnswersEngine(TSS.getMapSession(), TSS.getQueryTriples(), TSS.getSortedOntologies(), TSS.getOntoKBTripleMappings());
        
        System.out.println("Ranking by confidence only ");
        TSS.RanK_byConfidence();
        
        this.time_mapping = (System.currentTimeMillis() - this.total_time);
        this.time_fusion = System.currentTimeMillis();
        
        System.out.println("Fusing answers");
        FusionService fusionService = new FusionService(TSS);
        for (QueryTriple queryTriple : TSS.getQueryTriples()) {
          fusionService.formRDFEntityEntries(queryTriple);
        }
        fusionService.mergeByQueryTriples();
        this.time_fusion = (System.currentTimeMillis() - this.time_fusion);
        this.total_time = (System.currentTimeMillis() - this.total_time);
        
        System.out.println("Final fused answers: ");
        FusedAnswerBean fusedAnswerBean = fusionService.getFinalAnswerBeanSortedBy(1);
        System.out.println("Total answers: " + fusedAnswerBean.getAnswers().size());
        
        int maxSc = 2;
        for (RDFEntityCluster cluster : fusedAnswerBean.getAnswers())
        {
          int score = cluster.getRankingValue();
          if (score < maxSc) {
            if (this.fuseAnswers.containsKey(Integer.valueOf(n)))
            {
              ArrayList<RDFEntityCluster> clusters = (ArrayList)this.fuseAnswers.get(Integer.valueOf(n));
              clusters.add(cluster);
              this.fuseAnswers.put(Integer.valueOf(n), clusters);
            }
            else
            {
              ArrayList<RDFEntityCluster> clusters = new ArrayList();
              clusters.add(cluster);
              this.fuseAnswers.put(Integer.valueOf(n), clusters);
            }
          }
        }
        QueryTriple queryTriple = (QueryTriple)TSS.getQueryTriples().get(0);
        TripleMappingTable tripleMappingTable = (TripleMappingTable)TSS.getOntoKBTripleMappings().get(queryTriple);
        
        Hashtable<Integer, TripleMappingTable> rank_TripleMappingTables = MappingRanking.create_rankMappingTables(tripleMappingTable);
        RDFEntityList allanswers = new RDFEntityList();
        for (int score = 1; score < rank_TripleMappingTables.keySet().size() + 1; score++) {
          if (score < maxSc)
          {
            TripleMappingTable rank_tmt = (TripleMappingTable)rank_TripleMappingTables.get(Integer.valueOf(score));
            allanswers.addAllRDFEntity(rank_tmt.getAllAnswersNoRepetitions());
          }
        }
        this.individualAnswers.put(Integer.valueOf(n), allanswers);
      }
    }
    catch (Exception e)
    {
      int n;
      System.out.println("Exception executing the query");
      e.printStackTrace();
      return false;
    }
    this.resultsReady = true;
    return this.resultsReady;
  }
  
  public boolean isUserInputComplete()
  {
    this.is_user_in_loop = true;
    if (this.firstaccess == 0L) {
      this.firstaccess = System.currentTimeMillis();
    }
    if (this.lastaccess == 0L) {
      this.lastaccess = System.currentTimeMillis();
    }
    if (this.logConfig == null) {
      this.logConfig = new LogConfig();
    }
    this.logConfig.readLogQuestion();
    if ((this.logConfig.millis > this.lastaccess) && (this.logConfig.question != null))
    {
      this.lastaccess = this.logConfig.millis;
      this.user_query = this.logConfig.question;
      System.out.println(this.logConfig.question);
      System.out.println(this.logConfig.millis);
      return true;
    }
    return false;
  }
  
  public String getUserQuery()
  {
    return this.user_query;
  }
  
  public String getInternalQuery()
  {
    return this.user_query;
  }
  
  public void setToolInstallationPath(String path)
  {
    this.real_path = path;
  }
  
  public void showGUI(boolean show) {}
  
  public static void main(String[] args)
    throws Exception
  {
    SEALSSemanticSearchToolImp testing = new SEALSSemanticSearchToolImp();
    boolean ready = false;
    for (; !ready; Thread.sleep(9000L))
    {
      ready = testing.isUserInputComplete();
      if (ready)
      {
        System.out.println("We Found a query");
        break;
      }
      Thread.currentThread();
    }
    System.out.println("question ready!: " + testing.getUserQuery());
    
    ready = testing.isResultSetReady();
    while (!ready)
    {
      Thread.currentThread();Thread.sleep(9000L);
      ready = testing.isResultSetReady();
    }
    System.out.println("answers ready!: " + testing.getResults());
  }
  
  public ArrayList<String> getAnswers()
  {
    return this.answers;
  }
  
  public void setAnswers(ArrayList<String> answers)
  {
    this.answers = answers;
  }
}

