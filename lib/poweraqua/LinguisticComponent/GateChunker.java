package poweraqua.LinguisticComponent;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CreoleRegister;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ANNIEConstants;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.OffsetComparator;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GateChunker
{
  public Document[] docs;
  public Corpus corp;
  private SerialAnalyserController aquaController;
  public String localdir = "";
  
  public GateChunker()
    throws Exception
  {
    String userDir = System.getProperty("user.dir");
    GateChunker(userDir);
  }
  
  public GateChunker(String localdir)
    throws Exception
  {
    this.localdir = localdir;
    GateChunker(localdir);
  }
  
  public void GateChunker(String localdir)
    throws Exception
  {
    try
    {
      this.localdir = localdir;
      
      File gateHome = new File(localdir);
      try
      {
        Gate.setGateHome(gateHome);
      }
      catch (Exception e)
      {
        System.out.println(e);
      }
      try
      {
        Gate.setUserConfigFile(new File(gateHome, "user-gate.xml"));
      }
      catch (Exception e)
      {
        System.out.println(e);
      }
      Gate.init();
      
      CreoleRegister creoleRegister = Gate.getCreoleRegister();
      String annie = new String("file://" + localdir + "/plugins/ANNIE");
      
      File file = new File(localdir + "/plugins/ANNIE");
      URL creoleUrl = file.toURL();
      
      creoleRegister.registerDirectories(creoleUrl);
      
      String tools = new String("file://" + localdir + "/plugins/Tools");
      
      File filetools = new File(localdir + "/plugins/Tools");
      URL creoleUrl2 = filetools.toURL();
      
      creoleRegister.registerDirectories(creoleUrl2);
      
      String[] PR_NAMES = { "gate.creole.annotdelete.AnnotationDeletePR", "gate.creole.tokeniser.DefaultTokeniser", "gate.creole.splitter.SentenceSplitter", "gate.creole.POSTagger", "gate.creole.VPChunker" };
      
      CreateSerialController(PR_NAMES);
      
      File jape = new File(gateHome, "np.jape");
      
      String URLgrammar = "file:///" + jape.getAbsolutePath();
      
      AddJapeController(URLgrammar);
      
      jape = new File(gateHome, "np_patterns.jape");
      
      String URLgrammar2 = "file:///" + jape.getAbsolutePath();
      AddJapeController(URLgrammar2);
    }
    catch (GateException gex)
    {
      System.out.println("cannot initialise GATE...");
      gex.printStackTrace();
      return;
    }
  }
  
  public GateChunker(File gateHome)
    throws Exception
  {
    try
    {
      System.out.println("Calling Gate");
      Gate.setGateHome(gateHome);
      System.out.println("Initializing with context " + gateHome.toString());
      Gate.init();
      System.out.println("GATE initializing...");
      
      String[] PR_NAMES = { "gate.creole.annotdelete.AnnotationDeletePR", "gate.creole.tokeniser.DefaultTokeniser", "gate.creole.splitter.SentenceSplitter", "gate.creole.POSTagger", "gate.creole.VPChunker" };
      
      CreateSerialController(PR_NAMES);
      File jape = new File("np.jape");
      
      String URLgrammar = "file:///" + jape.getAbsolutePath();
      System.out.println("Reading Jape file " + URLgrammar);
      AddJapeController(URLgrammar);
      jape = new File("np_patterns.jape");
      String URLgrammar2 = "file:///" + jape.getAbsolutePath();
      AddJapeController(URLgrammar2);
      System.out.println("Gate Chunker initialized");
    }
    catch (GateException gex)
    {
      System.out.println("cannot initialise GATE...");
      gex.printStackTrace();
      return;
    }
  }
  
  public void CreateLanguageResource(String[] documents, boolean isURL)
    throws Exception
  {
    this.docs = new Document[documents.length];
    if (isURL) {
      for (int x = 0; x < documents.length; x++)
      {
        System.out.println("Analizing question: " + documents[x]);
        URL u = new URL(documents[x]);
        
        this.docs[x] = Factory.newDocument(u);
      }
    } else {
      for (int x = 0; x < documents.length; x++)
      {
        System.out.println("Analizing question: " + documents[x]);
        this.docs[x] = Factory.newDocument(documents[x]);
      }
    }
    FeatureMap params = Factory.newFeatureMap();
    FeatureMap features = Factory.newFeatureMap();
    List documentsList = new ArrayList(documents.length);
    for (int i = 0; i < this.docs.length; i++) {
      documentsList.add(this.docs[i]);
    }
    params.put("documentsList", documentsList);
    
    this.corp = ((Corpus)Factory.createResource("gate.corpora.CorpusImpl", params, features, "Question"));
  }
  
  public void CreateAnnieController()
    throws Exception
  {
    this.aquaController = ((SerialAnalyserController)Factory.createResource("gate.creole.SerialAnalyserController", Factory.newFeatureMap(), Factory.newFeatureMap(), "AQUA"));
    for (int i = 0; i < ANNIEConstants.PR_NAMES.length; i++)
    {
      FeatureMap params = Factory.newFeatureMap();
      System.out.println("Adding AnnieConstants " + ANNIEConstants.PR_NAMES[i]);
      ProcessingResource pr = (ProcessingResource)Factory.createResource(ANNIEConstants.PR_NAMES[i], params);
      
      this.aquaController.add(pr);
    }
  }
  
  public void CreateSerialController(String[] PR_NAMES)
    throws Exception
  {
    System.out.println("Create serial controller");
    this.aquaController = ((SerialAnalyserController)Factory.createResource("gate.creole.SerialAnalyserController", Factory.newFeatureMap(), Factory.newFeatureMap(), "AQUA"));
    for (int i = 0; i < PR_NAMES.length; i++)
    {
      System.out.println("adding processing resource: " + PR_NAMES[i]);
      FeatureMap params = Factory.newFeatureMap();
      ProcessingResource pr = (ProcessingResource)Factory.createResource(PR_NAMES[i], params);
      
      this.aquaController.add(pr);
    }
  }
  
  public void AddJapeController(String URLgrammar)
    throws Exception
  {
    URL u = new URL(URLgrammar);
    FeatureMap params = Factory.newFeatureMap();
    params.put("grammarURL", u);
    System.out.println("Creating Jape transducer: grammarURL=" + URLgrammar);
    ProcessingResource pr = (ProcessingResource)Factory.createResource("gate.creole.Transducer", params);
    this.aquaController.add(pr);
  }
  
  public void CreateSerialController(String[] PR_NAMES, Corpus corpus)
    throws Exception
  {
    this.aquaController = ((SerialAnalyserController)Factory.createResource("gate.creole.SerialAnalyserController", Factory.newFeatureMap(), Factory.newFeatureMap(), "AQUA"));
    for (int i = 0; i < PR_NAMES.length; i++)
    {
      System.out.println("adding processing resource: " + PR_NAMES[i]);
      FeatureMap params = Factory.newFeatureMap();
      ProcessingResource pr = (ProcessingResource)Factory.createResource(PR_NAMES[i], params);
      
      this.aquaController.add(pr);
    }
  }
  
  public void ExecuteSerialController()
    throws Exception
  {
    this.aquaController.setCorpus(this.corp);
    this.aquaController.execute();
  }
  
  public AnnotationSet[] GetAnnotations()
  {
    System.out.println("Getting annotations");
    
    Vector v = new Vector();
    for (int i = 0; i < this.docs.length; i++)
    {
      AnnotationSet ann = this.docs[i].getAnnotations();
      
      List typeList = new ArrayList(ann);
      Collections.sort(typeList, new OffsetComparator());
      
      Iterator typeIter = typeList.iterator();
      
      v.addElement(ann);
    }
    AnnotationSet[] results = new AnnotationSet[v.size()];
    v.copyInto(results);
    return results;
  }
  
  public IEAnnotation[] GetAnnotationsType(String question, String type, AnnotationSet annSet)
  {
    Vector v = new Vector();
    
    AnnotationSet typeSet = annSet.get(type);
    if (typeSet == null) {
      return null;
    }
    List typeList = new ArrayList(typeSet);
    Collections.sort(typeList, new OffsetComparator());
    
    Iterator typeIter = typeList.iterator();
    while (typeIter.hasNext())
    {
      Annotation tok = (Annotation)typeIter.next();
      String tokS = tok.toString();
      System.out.println("Annotation type " + type + " " + tokS);
      
      int ind1 = tokS.indexOf("offset=");
      int ind1end = tokS.indexOf(";", ind1);
      String aux = tokS.substring(ind1 + 7, ind1end);
      Integer aInteger = new Integer(aux);
      ind1 = aInteger.intValue();
      
      int ind2 = tokS.lastIndexOf("offset=");
      
      int ind2end = tokS.indexOf("\n", ind2);
      String aux2 = tokS.substring(ind2 + 7).replace("\n", "").trim();
      aInteger = new Integer(aux2);
      ind2 = aInteger.intValue();
      
      Map features = tok.getFeatures();
      
      v.add(new IEAnnotation(ind1, ind2, question.substring(ind1, ind2), features));
    }
    IEAnnotation[] type_ann = new IEAnnotation[v.size()];
    v.copyInto(type_ann);
    return type_ann;
  }
  
  public AnnotationSet ParseQuestion(String question)
    throws Exception
  {
    System.out.println("PArsing the question " + question);
    getLock(question);
    
    String[] quests = new String[1];
    quests[0] = question;
    CreateLanguageResource(quests, false);
    ExecuteSerialController();
    AnnotationSet[] anns = GetAnnotations();
    return anns[0];
  }
  
  private void getLock(String question)
  {
    try
    {
      System.out.println("Calling the logger");
      Logger logger = Logger.getLogger("RelationService.BasicLogging");
      
      boolean append = true;
      FileHandler handler;
      FileHandler handler;
      if (this.localdir.equals(""))
      {
        System.out.println("We got the logger " + question);
        
        handler = new FileHandler("./logs/my.log", append);
      }
      else
      {
        String lockpath = this.localdir + "/logs/my.log";
        System.out.println("We got the logger " + lockpath + " for " + question);
        handler = new FileHandler(lockpath, append);
      }
      logger.addHandler(handler);
      logger.setLevel(Level.INFO);
      logger.info(question);
    }
    catch (Exception e)
    {
      System.out.println(e);
    }
  }
}

